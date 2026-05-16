//
// Class : SpeciesImageColumn
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2003/01/28 18:40:14 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;
import org.apache.xerces.parsers.SAXParser;

/**
 * This class represents a column of a SpeciesImage.  Specifically, this object
 * maintains an understanding of what species characteristics are valid for
 * this column of a SpeciesImage.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.CHARACTERISTIC_ADDED_TO_COLUMN - a characteristic has been added to this column
 * <li> EngineProp.CHARACTERISTIC_REMOVED_FROM_COLUMN - a characteristic has been removed from this column
 * <li> EngineProp.GENDER - gender of this column has changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - the object's locked state has changed
 * <li> EngineProp.NAME - name of this species image column has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_ADDED_TO_COLUMN
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_REMOVED_FROM_COLUMN
 * @see org.concord.biologica.engine.EngineProp#GENDER
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class SpeciesImageColumn
extends EngineObject
implements Serializable, PropertyChangeListener
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Name of this species image column.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME
    **/
    private String				name;

    /**
     * The SpeciesImage which contains this object.  May not be null.<p>
    **/
    private SpeciesImage		speciesImage;

    /**
     * Gender of the images in this column.  This must be one of
     * Species.FEMALE_AND_MALE, Species.FEMALE_ONLY or Species.MALE_ONLY.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.GENDER.<p>
     *
     * @see		org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see		org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see		org.concord.biologica.engine.Species#MALE_ONLY
    **/
    private int					gender;

    /**
     * Vector of Characteristics.  As of now, these characteristics are considered
     * AND'ed together, meaning that images in this column are considered drawable
     * when the organism contains all of the Characteristics in this vector.<p>
     *
     * Long term, we could make this object contain a more complex understanding
     * of how to use these characteristics (e.g. OR's, etc.).<p>
    **/
    private Vector				characteristics;

    /**
     * Creates a new species image column.<p>
     *
     * @param		aSpeciesImage SpeciesImage - the species image containing this object, may not be null
     * @param		aName String - the species image column name, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public SpeciesImageColumn(SpeciesImage aSpeciesImage, String aName)
    {
        // Check input arguments
        if (aSpeciesImage == null ||
            aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set instance variables
        name = new String(aName);
        gender = Species.FEMALE_AND_MALE;
        characteristics = new Vector();
        lockedState = EngineObject.UNLOCKED;

        // Creation successful - tell parent species image
        speciesImage = aSpeciesImage;
        speciesImage.addSpeciesImageColumn(this);
    }

    /**
     * Create a new SpeciesImageColumn and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aSpeciesImage SpeciesImage - the enclosing species image for this new species image column
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public SpeciesImageColumn(SpeciesImage aSpeciesImage,
                              String anElementName,
                              int anElementID,
                              /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                              ImportContext importContext)
    {
        if (aSpeciesImage == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        name = null;
        gender = Species.FEMALE_AND_MALE;
        lockedState = EngineObject.UNLOCKED;

        // Create vectors
        characteristics = new Vector();

        // Creation successful, so add this species image column to species image
        speciesImage = aSpeciesImage;
        speciesImage.addSpeciesImageColumn(this);

        // Create an element context
        xmlElementContext = new ElementContext(this,anXMLParser,importContext);

        // Claim document handler and return, allowing parsing to continue
        anXMLParser.setDocumentHandler(this);
    }

    /**
     * Handle notification that the parser has hit the start
     * of a new element with the given name and attributes.<p>
     *
     * For internal elements (elements that are non-EngineObject
     * instance variables of this object), call xmlElementContext.startElement()
     * and xmlElementContext will receive the remaining element notifications.
     *
     * For external elements (elements that are EngineObject instance
     * variables of this object), create an object of the appropriate
     * class and that object will receive the remaining element notifications.
     *
     * @param   anElementName String - name of element
     * @param   amap AttributeList - an attribute list
    **/
    public void startElement(String anElementName, AttributeList amap)
    throws SAXException
    {
        int anElementID = Elements.mapElementNameToID(anElementName);

        switch (anElementID)
        {
            case Elements.SCHEMA_VERSION_ELEMENT_ID:
            case Elements.ID_ELEMENT_ID:
            case Elements.DELETED_ELEMENT_ID:
            case Elements.LOCKED_STATE_ELEMENT_ID:
            case Elements.SPECIES_IMAGE_ID_ELEMENT_ID:
            case Elements.NAME_ELEMENT_ID:
            case Elements.GENDER_ELEMENT_ID:
            case Elements.CHARACTERISTIC_IDS_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("SpeciesImageColumn " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
        }
    }

    /**
     * Receive notification of character data.
     *
     * @param           ch char[] - characters
     * @param           start int - start of string
     * @param           length int - length of string
    **/
    public void characters(char[] ch, int start, int length)
    throws SAXException
    {
        // throw new IllegalArgumentException("Should never be here!!");
    }

    /**
     * Receive notification of ignorable white space character data.
     *
     * @param           ch char[] - characters
     * @param           start int - start of string
     * @param           length int - length of string
    **/
    public void ignorableWhitespace(char[] ch, int start, int length)
    throws SAXException
    {
        // throw new IllegalArgumentException("Should never be here!!");
    }

    /**
     * Handle notification that the parsing of the given element has ended.
     *
     * @param     anElementName String - the element name
    **/
    public void endElement(String anElementName)
    throws SAXException
    {
        boolean reclaimDocumentHandler = true;
        String valueString;

        int anElementID = Elements.mapElementNameToID(anElementName);

        switch (anElementID)
        {
            case Elements.SCHEMA_VERSION_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // schemaVersion = Integer.valueOf(valueString).intValue();
                break;

            case Elements.ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // Ignore world id, as we've already created it.
                // But we do need to add it to importContext.
                {
                    int oldID = Integer.valueOf(valueString).intValue();
                    xmlElementContext.getImportContext().addObject(this,oldID);
                }
                break;

            case Elements.DELETED_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                deleted = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.LOCKED_STATE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setLockedStateAsString(valueString);
                break;

            case Elements.SPECIES_IMAGE_ID_ELEMENT_ID:
                // Ignore, as we already have the species image
                break;

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.GENDER_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setGenderAsString(valueString);
                break;

            case Elements.CHARACTERISTIC_IDS_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    int characteristicId;
                    String characteristicIdString;
                    Characteristic aCharacteristic;
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    while (parser.hasMoreTokens())
                    {
                        characteristicIdString = parser.nextToken();
                        characteristicId = Integer.valueOf(characteristicIdString).intValue();
                        aCharacteristic = (Characteristic) xmlElementContext.getImportContext().getObject(characteristicId);
                        characteristics.addElement(aCharacteristic);
                    }
                }
                break;

            case Elements.SPECIES_IMAGE_COLUMN_ELEMENT_ID:
                // Done with this species image column, so pop up to enclosing trait
                speciesImage.endElement(anElementName);
                reclaimDocumentHandler = false;
                break;

            default:
                // External object. Do nothing here.
                reclaimDocumentHandler = false;
                break;
        }

        if (reclaimDocumentHandler)
        {
            // Reclaim the document handler to this object, making this
            // object ready for the next startElement() call from the parser.
            xmlElementContext.getXMLParser().setDocumentHandler(this);
        }
    }

    /**
     * Delete this object, notifying parent objects and deleting any child objects.<p>
     *
     * When this method is successful, a property change event is
     * generated for the property named EngineProp.DELETED.<p>
    **/
    public void delete()
    {
        delete(true);
    }
    
    public void delete(boolean notifyChange)
    {
        // Avoid double deletions gracefully
        if (deleted == true)
        {
            return;
        }
        deleted = true;

        if (notifyChange)
        {
            // Notify listeners before deleting
            changes.firePropertyChange(EngineProp.DELETED,FALSE,TRUE);
        }

        // Delete instance variables, enabling garbage collection on them
        name = null;

        // Remove this object as a listener on Characteristic objects
        // Do not need to clone, as we're not deleting Characteristics
        Characteristic characteristic;
        Enumeration eCharacteristics = characteristics.elements();
        while (eCharacteristics.hasMoreElements())
        {
            characteristic = (Characteristic) eCharacteristics.nextElement();
            characteristic.removePropertyChangeListener(this);
        }
        characteristics.removeAllElements();
        characteristics = null;

        // Tell species image
        speciesImage.removeSpeciesImageColumn(this);
        speciesImage = null;
        id = EngineObject.NULL_ID;
    }

    /**
     * Return a string representation of this object.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        return EngineStrings.COLUMN_COLON + name;
    }

    /**
     * Return the SpeciesImage of this object.<p>
     *
     * @return		SpeciesImage - SpeciesImage of this object, never null
    **/
    public SpeciesImage getSpeciesImage()
    {
        return speciesImage;
    }

    /**
     * Return the Species of this object.<p>
     *
     * @return		Species - the species of this object, never null
    **/
    public Species getSpecies()
    {
        return speciesImage.getSpecies();
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return speciesImage.getWorld();
    }

    /**
     * Return the name of this species image column.  May be null.<p>
     *
     * @return		String - name of this species image column, may not be null.
    **/
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this species image column.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this species image column, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
    **/
    public void setName(String aName)
    {
        // Return immediately if aName equals the current name
        if (aName != null && name != null && aName.equals(name))
        {
            return;
        }

        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        if (aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        String oldName = name;
        name = new String(aName);

        // Notify listeners
        changes.firePropertyChange(EngineProp.NAME,oldName,name);
    }

    /**
     * Return the gender of images in this column, which will be one
     * of Species.FEMALE_AND_MALE, Species.FEMALE_ONLY
     * or Species.MALE_ONLY.<p>
     *
     * @return		int - gender aspect of this species image column
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see			org.concord.biologica.engine.Species#MALE_ONLY
    **/
    public int getGender()
    {
        return gender;
    }

    /**
     * Return the gender of images in this column, which will be one
     * of Species.FEMALE_AND_MALE_STRING, Species.FEMALE_ONLY_STRING
     * or Species.MALE_ONLY_STRING.  Returns a string.<p>
     *
     * @return		int - gender aspect of this species image column
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE_STRING
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY_STRING
     * @see			org.concord.biologica.engine.Species#MALE_ONLY_STRING
    **/
    public String getGenderAsString()
    {
        if (gender == Species.FEMALE_ONLY)
        {
            return Species.FEMALE_ONLY_STRING;
        }
        else if (gender == Species.MALE_ONLY)
        {
            return Species.MALE_ONLY_STRING;
        }

        // Default to female and male
        return Species.FEMALE_AND_MALE_STRING;
    }

    /**
     * Set the gender of the images in this column.<p>
     *
     * The new gender must be one of Species.FEMALE_AND_MALE,
     * Species.FEMALE_ONLY or Species.MALE_ONLY.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.GENDER.<p>
     *
     * @param		aGender int - the new gender of this species image column
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see			org.concord.biologica.engine.Species#MALE_ONLY
    **/
    public void setGender(int aGender)
    {
        // Return immediately if aGender equals the current gender
        if (aGender == gender)
        {
            return;
        }

        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Exception if illegal new gender
        if (aGender != Species.FEMALE_AND_MALE &&
            aGender != Species.FEMALE_ONLY &&
            aGender != Species.MALE_ONLY)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input aGender ok, do it
        int oldGender = gender;
        gender = aGender;

        // Notify listeners
        changes.firePropertyChange(EngineProp.GENDER,
                                   new Integer(oldGender),
                                   new Integer(gender));
    }

    /**
     * Set the gender of the images in this column as a string.<p>
     *
     * The new gender must be one of Species.FEMALE_AND_MALE_STRING,
     * Species.FEMALE_ONLY_STRING or Species.MALE_ONLY_STRING.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.GENDER.<p>
     *
     * @param		aGenderString String - the new gender of this species image column as a string
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE_STRING
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY_STRING
     * @see			org.concord.biologica.engine.Species#MALE_ONLY_STRING
    **/
    public void setGenderAsString(String aGenderString)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }
        else if (aGenderString == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Convert gender to an integer
        int aGender;
        if (aGenderString.equals(Species.FEMALE_AND_MALE_STRING))
        {
            aGender = Species.FEMALE_AND_MALE;
        }
        else if (aGenderString.equals(Species.FEMALE_ONLY_STRING))
        {
            aGender = Species.FEMALE_ONLY;
        }
        else if (aGenderString.equals(Species.MALE_ONLY_STRING))
        {
            aGender = Species.MALE_ONLY;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if aGender equals the current gender
        if (aGender == gender)
        {
            return;
        }

        // Exception if illegal new gender
        if (aGender != Species.FEMALE_AND_MALE &&
            aGender != Species.FEMALE_ONLY &&
            aGender != Species.MALE_ONLY)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input aGender ok, do it
        int oldGender = gender;
        gender = aGender;

        // Notify listeners
        changes.firePropertyChange(EngineProp.GENDER,
                                   new Integer(oldGender),
                                   new Integer(gender));
    }

    /**
     * Returns an enumeration over the vector of characteristics in this column.<p>
     *
     * @return		Enumeration - an enumeration over the characteristics in this column
    **/
    public Enumeration getCharacteristics()
    {
        Vector characteristicsClone = (Vector) characteristics.clone();
        return characteristicsClone.elements();
    }

    /**
     * Return the number of characteristics in this column.<p>
     *
     * @return		int - the number of characteristics in this column
    **/
    public int getNumberOfCharacteristics()
    {
        return characteristics.size();
    }

    /**
     * Adds a characteristic to the column.<p>
     *
     * @param		aCharacteristic Characteristic - a new characteristic, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void addCharacteristic(Characteristic aCharacteristic)
    {
        characteristics.addElement(aCharacteristic);
        aCharacteristic.addPropertyChangeListener(this);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.CHARACTERISTIC_ADDED_TO_COLUMN, null, aCharacteristic);
    }

    /**
     * Removes a characteristic from the column.<p>
     *
     * @param		aCharacteristic Characteristic - a characteristic, may not be null
     * @return		boolean indicating whether or not the characteristic was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public boolean removeCharacteristic(Characteristic aCharacteristic)
    {
        if (aCharacteristic == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = characteristics.removeElement(aCharacteristic);

        // Notify listeners if characteristic removed
        if (result == true)
        {
            aCharacteristic.removePropertyChangeListener(this);
            changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_COLUMN, null, aCharacteristic);
        }

        return result;
    }

    /**
     * Replace the given old Characteristic with the given new Characteristic.
     * The key here is that we should maintain the position, so the old and
     * new characteristics have the same order in this object.<p>
     *
     * If the oldCharacteristic is null or not found, then we just add the
     * newCharacteristic on to the end of the list of characteristics.<p>
     *
     * If the newCharacteristic is null, then we just remove the oldCharacteristic.<p>
     *
     * If both the oldCharacteristic and the newCharacteristic are null or not found,
     * do nothing.<p>
     *
     * @param		Characteristic oldCharacteristic - characteristic to remove, may be null
     * @param		Characteristic newCharacteristic - characteristic to add, may be null
    **/
    public void replaceCharacteristic(Characteristic oldCharacteristic,
                                      Characteristic newCharacteristic)
    {
        int index = -1;

        if (oldCharacteristic != null)
        {
            index = characteristics.indexOf(oldCharacteristic);
        }

        if (oldCharacteristic != null && index != -1)
        {
            // oldCharacteristic not null and in characteristics Vector
            if (newCharacteristic != null)
            {
                // Replace old characteristic with new one and fire REMOVED and ADDED events
                characteristics.setElementAt(newCharacteristic,index);

                oldCharacteristic.removePropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_COLUMN, null, oldCharacteristic);

                newCharacteristic.addPropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_ADDED_TO_COLUMN, null, newCharacteristic);
            }
            else
            {
                // newCharacteristic null, so just remove old characteristic and fire REMOVED event
                characteristics.removeElementAt(index);

                oldCharacteristic.removePropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_COLUMN, null, oldCharacteristic);
            }
        }
        else
        {
            // oldCharacteristic null or not in characteristics Vector
            if (newCharacteristic != null)
            {
                // Add new characteristic and fire ADDED event
                characteristics.addElement(newCharacteristic);

                newCharacteristic.addPropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_ADDED_TO_COLUMN, null, newCharacteristic);
            }
        }
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.DELETED))
        {
            // A characteristic is about to be deleted, so remove it from our list.
            Object object = event.getSource();

            if (object instanceof Characteristic)
            {
                Characteristic characteristic = (Characteristic) object;

                boolean result = characteristics.removeElement(characteristic);

                if (result == true)
                {
                    characteristic.removePropertyChangeListener(this);
                    changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_COLUMN, null, characteristic);
                }
            }
        }
    }

    /**
     * Writes this object to the given stream in XML format.  Usually
     * this method is used to write the object to a file for saving it,
     * but that is potentially not the only use.<p>
     *
     * We could supply a default implementation that knows how to
     * write the id value.  But that convenience is not chosen
     * because it's never correct to use just this implementation
     * of these methods, so I'd rather have a compilation fail if a
     * derived class fails to implement this method.<p>
     *
     * The inverse of this method is a constructor that takes a
     * org.xml.sax.Parser argument and other arguments.<p>
     *
     * @param       stream PrintWriter - print stream
     * @exception	IllegalArgumentException - input arguments illegal
     * @exception	IOException - an IO error occurred in java libraries
    **/
    public void writeToStream(PrintWriter stream)
    throws java.io.IOException
    {
        // Output stream may not be null
        if (stream == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Start object
        stream.println("<" + Elements.SPECIES_IMAGE_COLUMN_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Name
        if (name == null)
        {
            stream.println("<" + Elements.NAME_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.NAME_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.NAME_ELEMENT_NAME + ">" + name +
                              "</" + Elements.NAME_ELEMENT_NAME + ">");
        }

        // Species Image ID
        if (speciesImage == null)
        {
            stream.println("<" + Elements.SPECIES_IMAGE_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SPECIES_IMAGE_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SPECIES_IMAGE_ID_ELEMENT_NAME + ">" + speciesImage.getID() +
                              "</" + Elements.SPECIES_IMAGE_ID_ELEMENT_NAME + ">");
        }

        // Gender
        stream.println("<" + Elements.GENDER_ELEMENT_NAME + ">" +
                          getGenderAsString() +
                          "</" + Elements.GENDER_ELEMENT_NAME + ">");

        // Characteristic IDs
        {
            stream.print("<" + Elements.CHARACTERISTIC_IDS_ELEMENT_NAME + ">");

            Characteristic aCharacteristic;
            Enumeration eCharacteristics = characteristics.elements();
            while (eCharacteristics.hasMoreElements())
            {
                aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                stream.print(aCharacteristic.getID() + ",");
            }

            stream.println("</" + Elements.CHARACTERISTIC_IDS_ELEMENT_NAME + ">");
        }

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.SPECIES_IMAGE_COLUMN_ELEMENT_NAME + ">");
    }
}

