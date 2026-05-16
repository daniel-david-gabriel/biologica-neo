//
// Class : SpeciesImageRow
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

import java.awt.Point;

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
 * This class represents a row of a SpeciesImage.  Specifically, this object
 * maintains an understanding of what species characteristics are valid for
 * this row of a SpeciesImage.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.CHARACTERISTIC_ADDED_TO_ROW - a characteristic has been added to this row
 * <li> EngineProp.CHARACTERISTIC_REMOVED_FROM_ROW - a characteristic has been removed from this row
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - the locked state of the object has changed
 * <li> EngineProp.NAME - name of this species image row has changed
 * <li> EngineProp.ROW_GENDER - gender of this row has changed
 * <li> EngineProp.XXSMALL_HOTSPOT - location of hotspot on xxsmall image changed
 * <li> EngineProp.XSMALL_HOTSPOT - location of hotspot on xsmall image changed
 * <li> EngineProp.SMALL_HOTSPOT - location of hotspot on small image changed
 * <li> EngineProp.MEDIUM_HOTSPOT - location of hotspot on medium image changed
 * <li> EngineProp.LARGE_HOTSPOT - location of hotspot on large image changed
 * <li> EngineProp.XLARGE_HOTSPOT - location of hotspot on xlarge image changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_ADDED_TO_ROW
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_REMOVED_FROM_ROW
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#ROW_GENDER
 * @see org.concord.biologica.engine.EngineProp#XXSMALL_HOTSPOT
 * @see org.concord.biologica.engine.EngineProp#XSMALL_HOTSPOT
 * @see org.concord.biologica.engine.EngineProp#SMALL_HOTSPOT
 * @see org.concord.biologica.engine.EngineProp#MEDIUM_HOTSPOT
 * @see org.concord.biologica.engine.EngineProp#LARGE_HOTSPOT
 * @see org.concord.biologica.engine.EngineProp#XLARGE_HOTSPOT
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class SpeciesImageRow
extends EngineObject
implements Serializable, PropertyChangeListener
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Name of this species image row.  May not be null.<p>
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
     * Gender of the images in this row.  This must be one of
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
     * AND'ed together, meaning that images in this row are considered drawable
     * when the organism contains all of the Characteristics in this vector.<p>
     *
     * Long term, we could make this object contain a more complex understanding
     * of how to use these characteristics (e.g. OR's, etc.).<p>
    **/
    private Vector				characteristics;

    /**
     * xxSmall hotspot location
    **/
    private Point				xxSmallHotspot;

    /**
     * xSmall hotspot location
    **/
    private Point				xSmallHotspot;

    /**
     * Small hotspot location
    **/
    private Point				smallHotspot;

    /**
     * Medium hotspot location
    **/
    private Point				mediumHotspot;

    /**
     * Large hotspot location
    **/
    private Point				largeHotspot;

    /**
     * xLarge hotspot location
    **/
    private Point				xLargeHotspot;

    /**
     * Creates a new species image row.<p>
     *
     * @param		aSpeciesImage SpeciesImage - the species image containing this object, may not be null
     * @param		aName String - the species image row name, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public SpeciesImageRow(SpeciesImage aSpeciesImage, String aName)
    {
        // Check input arguments
        if (aSpeciesImage == null ||
            aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set instance variables
        lockedState = EngineObject.UNLOCKED;
        name = new String(aName);
        gender = Species.FEMALE_AND_MALE;
        characteristics = new Vector();
        xxSmallHotspot = new Point(0,0);
        xSmallHotspot = new Point(0,0);
        smallHotspot = new Point(0,0);
        mediumHotspot = new Point(0,0);
        largeHotspot = new Point(0,0);
        xLargeHotspot = new Point(0,0);

        // Creation successful - tell parent species image
        speciesImage = aSpeciesImage;
        speciesImage.addSpeciesImageRow(this);
    }

    /**
     * Create a new SpeciesImageRow and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aSpeciesImage SpeciesImage - the enclosing species image for this new species image row
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public SpeciesImageRow(SpeciesImage aSpeciesImage,
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
        xxSmallHotspot = new Point(0,0);
        xSmallHotspot = new Point(0,0);
        smallHotspot = new Point(0,0);
        mediumHotspot = new Point(0,0);
        largeHotspot = new Point(0,0);
        xLargeHotspot = new Point(0,0);

        // Create vectors
        characteristics = new Vector();

        // Creation successful, so add this species image row to species image
        speciesImage = aSpeciesImage;
        speciesImage.addSpeciesImageRow(this);

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
            case Elements.XXSMALL_HOTSPOT_ELEMENT_ID:
            case Elements.XSMALL_HOTSPOT_ELEMENT_ID:
            case Elements.SMALL_HOTSPOT_ELEMENT_ID:
            case Elements.MEDIUM_HOTSPOT_ELEMENT_ID:
            case Elements.LARGE_HOTSPOT_ELEMENT_ID:
            case Elements.XLARGE_HOTSPOT_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("SpeciesImageRow " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.XXSMALL_HOTSPOT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    String xString = parser.nextToken();
                    String yString = parser.nextToken();
                    xxSmallHotspot.x = Integer.valueOf(xString).intValue();
                    xxSmallHotspot.y = Integer.valueOf(yString).intValue();
                }
                break;

            case Elements.XSMALL_HOTSPOT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    String xString = parser.nextToken();
                    String yString = parser.nextToken();
                    xSmallHotspot.x = Integer.valueOf(xString).intValue();
                    xSmallHotspot.y = Integer.valueOf(yString).intValue();
                }
                break;

            case Elements.SMALL_HOTSPOT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    String xString = parser.nextToken();
                    String yString = parser.nextToken();
                    smallHotspot.x = Integer.valueOf(xString).intValue();
                    smallHotspot.y = Integer.valueOf(yString).intValue();
                }
                break;

            case Elements.MEDIUM_HOTSPOT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    String xString = parser.nextToken();
                    String yString = parser.nextToken();
                    mediumHotspot.x = Integer.valueOf(xString).intValue();
                    mediumHotspot.y = Integer.valueOf(yString).intValue();
                }
                break;

            case Elements.LARGE_HOTSPOT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    String xString = parser.nextToken();
                    String yString = parser.nextToken();
                    largeHotspot.x = Integer.valueOf(xString).intValue();
                    largeHotspot.y = Integer.valueOf(yString).intValue();
                }
                break;

            case Elements.XLARGE_HOTSPOT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    String xString = parser.nextToken();
                    String yString = parser.nextToken();
                    xLargeHotspot.x = Integer.valueOf(xString).intValue();
                    xLargeHotspot.y = Integer.valueOf(yString).intValue();
                }
                break;

            case Elements.SPECIES_IMAGE_ROW_ELEMENT_ID:
                // Done with this species image row, so pop up to enclosing trait
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

        xxSmallHotspot = null;
        xSmallHotspot = null;
        smallHotspot = null;
        mediumHotspot = null;
        largeHotspot = null;
        xLargeHotspot = null;

        // Tell species image
        speciesImage.removeSpeciesImageRow(this);
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
        return EngineStrings.ROW_COLON + name;
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
     * Return the name of this species image row.  May be null.<p>
     *
     * @return		String - name of this species image row, may not be null.
    **/
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this species image row.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this species image row, may not be null
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
     * Return the gender of images in this row, which will be one
     * of Species.FEMALE_AND_MALE, Species.FEMALE_ONLY
     * or Species.MALE_ONLY.<p>
     *
     * @return		int - gender aspect of this species image row
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see			org.concord.biologica.engine.Species#MALE_ONLY
    **/
    public int getGender()
    {
        return gender;
    }


    /**
     * Return the gender of images in this row, which will be one
     * of Species.FEMALE_AND_MALE_STRING, Species.FEMALE_ONLY_STRING
     * or Species.MALE_ONLY_STRING.  Returns a string.<p>
     *
     * @return		int - gender aspect of this species image row
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
     * Set the gender of the images in this row.<p>
     *
     * The new gender must be one of Species.FEMALE_AND_MALE,
     * Species.FEMALE_ONLY or Species.MALE_ONLY.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.ROW_GENDER.<p>
     *
     * @param		aGender int - the new gender of this species image row
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
     * Set the gender of the images in this row as a string.<p>
     *
     * The new gender must be one of Species.FEMALE_AND_MALE_STRING,
     * Species.FEMALE_ONLY_STRING or Species.MALE_ONLY_STRING.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.ROW_GENDER.<p>
     *
     * @param		aGenderString String - the new gender of this species image row as a string
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
     * @see			org.concord.biologica.engine.Species#FEMALE_AND_MALE
     * @see			org.concord.biologica.engine.Species#FEMALE_ONLY
     * @see			org.concord.biologica.engine.Species#MALE_ONLY
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
     * Returns an enumeration over the vector of characteristics in this row.<p>
     *
     * @return		Enumeration - an enumeration over the characteristics in this row
    **/
    public Enumeration getCharacteristics()
    {
        Vector characteristicsClone = (Vector) characteristics.clone();
        return characteristicsClone.elements();
    }

    /**
     * Return the number of characteristics in this row.<p>
     *
     * @return		int - the number of characteristics in this row
    **/
    public int getNumberOfCharacteristics()
    {
        return characteristics.size();
    }

    /**
     * Adds a characteristic to the row.<p>
     *
     * @param		aCharacteristic Characteristic - a new characteristic, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void addCharacteristic(Characteristic aCharacteristic)
    {
        characteristics.addElement(aCharacteristic);
        aCharacteristic.addPropertyChangeListener(this);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.CHARACTERISTIC_ADDED_TO_ROW, null, aCharacteristic);
    }

    /**
     * Removes a characteristic from the row.<p>
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
            changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_ROW, null, aCharacteristic);
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
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_ROW, null, oldCharacteristic);

                newCharacteristic.addPropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_ADDED_TO_ROW, null, newCharacteristic);
            }
            else
            {
                // newCharacteristic null, so just remove old characteristic and fire REMOVED event
                characteristics.removeElementAt(index);

                oldCharacteristic.removePropertyChangeListener(this);
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_ROW, null, oldCharacteristic);
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
                changes.firePropertyChange(EngineProp.CHARACTERISTIC_ADDED_TO_ROW, null, newCharacteristic);
            }
        }
    }

    /**
     * Get the given hotspot location.<p>
     *
     * @param		imageSize int - image size
     * @return		Point - hotspot location, null if imageSize wasn't a legal one
    **/
    public Point getHotspot(int imageSize)
    {
        switch (imageSize)
        {
            case SpeciesImage.XXSMALL_IMAGE_SIZE:
                return xxSmallHotspot;
            
            case SpeciesImage.XSMALL_IMAGE_SIZE:
                return xSmallHotspot;
            
            case SpeciesImage.SMALL_IMAGE_SIZE:
                return smallHotspot;
            
            case SpeciesImage.MEDIUM_IMAGE_SIZE:
                return mediumHotspot;
            
            case SpeciesImage.LARGE_IMAGE_SIZE:
                return largeHotspot;
            
            case SpeciesImage.XLARGE_IMAGE_SIZE:
                return xLargeHotspot;
        }

        return null;
    }

    /**
     * Get the xxSmall hotspot location.<p>
     *
     * @return		Point - xxSmall hotspot location, never null
    **/
    public Point getXXSmallHotspot()
    {
        return xxSmallHotspot;
    }

    /**
     * Set the xxSmall hotspot location.<p>
     *
     * @param		aHotspot Point - a new hotspot location, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setXXSmallHotspot(Point aHotspot)
    {
        if (aHotspot == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if no change
        if (aHotspot.x == xxSmallHotspot.x &&
            aHotspot.y == xxSmallHotspot.y)
        {
            return;
        }

        // Make change
        Point oldXXSmallHotspot = new Point(xxSmallHotspot.x,xxSmallHotspot.y);
        xxSmallHotspot.x = aHotspot.x;
        xxSmallHotspot.y = aHotspot.y;

        // Notify listeners
        changes.firePropertyChange(EngineProp.XXSMALL_HOTSPOT,oldXXSmallHotspot,xxSmallHotspot);
    }

    /**
     * Get the xSmall hotspot location.<p>
     *
     * @return		Point - xSmall hotspot location, never null
    **/
    public Point getXSmallHotspot()
    {
        return xSmallHotspot;
    }

    /**
     * Set the xSmall hotspot location.<p>
     *
     * @param		aHotspot Point - a new hotspot location, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setXSmallHotspot(Point aHotspot)
    {
        if (aHotspot == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if no change
        if (aHotspot.x == xSmallHotspot.x &&
            aHotspot.y == xSmallHotspot.y)
        {
            return;
        }

        // Make change
        Point oldXSmallHotspot = new Point(xSmallHotspot.x,xSmallHotspot.y);
        xSmallHotspot.x = aHotspot.x;
        xSmallHotspot.y = aHotspot.y;

        // Notify listeners
        changes.firePropertyChange(EngineProp.XSMALL_HOTSPOT,oldXSmallHotspot,xSmallHotspot);
    }

    /**
     * Get the small hotspot location.<p>
     *
     * @return		Point - small hotspot location, never null
    **/
    public Point getSmallHotspot()
    {
        return smallHotspot;
    }

    /**
     * Set the small hotspot location.<p>
     *
     * @param		aHotspot Point - a new hotspot location, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setSmallHotspot(Point aHotspot)
    {
        if (aHotspot == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if no change
        if (aHotspot.x == smallHotspot.x &&
            aHotspot.y == smallHotspot.y)
        {
            return;
        }

        // Make change
        Point oldSmallHotspot = new Point(smallHotspot.x,smallHotspot.y);
        smallHotspot.x = aHotspot.x;
        smallHotspot.y = aHotspot.y;

        // Notify listeners
        changes.firePropertyChange(EngineProp.SMALL_HOTSPOT,oldSmallHotspot,smallHotspot);
    }

    /**
     * Get the medium hotspot location.<p>
     *
     * @return		Point - medium hotspot location, never null
    **/
    public Point getMediumHotspot()
    {
        return mediumHotspot;
    }

    /**
     * Set the medium hotspot location.<p>
     *
     * @param		aHotspot Point - a new hotspot location, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setMediumHotspot(Point aHotspot)
    {
        if (aHotspot == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if no change
        if (aHotspot.x == mediumHotspot.x &&
            aHotspot.y == mediumHotspot.y)
        {
            return;
        }

        // Make change
        Point oldMediumHotspot = new Point(mediumHotspot.x,mediumHotspot.y);
        mediumHotspot.x = aHotspot.x;
        mediumHotspot.y = aHotspot.y;

        // Notify listeners
        changes.firePropertyChange(EngineProp.MEDIUM_HOTSPOT,oldMediumHotspot,mediumHotspot);
    }

    /**
     * Get the large hotspot location.<p>
     *
     * @return		Point - large hotspot location, never null
    **/
    public Point getLargeHotspot()
    {
        return largeHotspot;
    }

    /**
     * Set the large hotspot location.<p>
     *
     * @param		aHotspot Point - a new hotspot location, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setLargeHotspot(Point aHotspot)
    {
        if (aHotspot == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if no change
        if (aHotspot.x == largeHotspot.x &&
            aHotspot.y == largeHotspot.y)
        {
            return;
        }

        // Make change
        Point oldLargeHotspot = new Point(largeHotspot.x,largeHotspot.y);
        largeHotspot.x = aHotspot.x;
        largeHotspot.y = aHotspot.y;

        // Notify listeners
        changes.firePropertyChange(EngineProp.LARGE_HOTSPOT,oldLargeHotspot,largeHotspot);
    }

    /**
     * Get the xLarge hotspot location.<p>
     *
     * @return		Point - xLarge hotspot location, never null
    **/
    public Point getXLargeHotspot()
    {
        return xLargeHotspot;
    }

    /**
     * Set the xLarge hotspot location.<p>
     *
     * @param		aHotspot Point - a new hotspot location, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setXLargeHotspot(Point aHotspot)
    {
        if (aHotspot == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if no change
        if (aHotspot.x == xLargeHotspot.x &&
            aHotspot.y == xLargeHotspot.y)
        {
            return;
        }

        // Make change
        Point oldXLargeHotspot = new Point(xLargeHotspot.x,xLargeHotspot.y);
        xLargeHotspot.x = aHotspot.x;
        xLargeHotspot.y = aHotspot.y;

        // Notify listeners
        changes.firePropertyChange(EngineProp.XLARGE_HOTSPOT,oldXLargeHotspot,xLargeHotspot);
    }

    /**
     * Return whether the given coordinates are on the hotspot for this image row
     *
     * @param		organismImageSize int - organism image size
     * @param		x int - x coordinate
     * @param		y int - y coordinate
     * @return		boolean - coordinates are on (true) or not on (false) hotspot
    **/
    public boolean isOnHotspot(int organismImageSize, int x, int y)
    {
        if (speciesImage.getImageType() == SpeciesImage.HOTSPOT_IMAGE_TYPE)
        {
            int xHotspot = -1;
            int yHotspot = -1;
            int epsilon = 5;

            switch (organismImageSize)
            {
                case SpeciesImage.XXSMALL_IMAGE_SIZE:
                    xHotspot = xxSmallHotspot.x;
                    yHotspot = xxSmallHotspot.y;
                    break;
                
                case SpeciesImage.XSMALL_IMAGE_SIZE:
                    xHotspot = xSmallHotspot.x;
                    yHotspot = xSmallHotspot.y;
                    break;

                case SpeciesImage.SMALL_IMAGE_SIZE:
                    xHotspot = smallHotspot.x;
                    yHotspot = smallHotspot.y;
                    break;
                
                case SpeciesImage.MEDIUM_IMAGE_SIZE:
                    xHotspot = mediumHotspot.x;
                    yHotspot = mediumHotspot.y;
                    break;
                
                case SpeciesImage.LARGE_IMAGE_SIZE:
                    xHotspot = largeHotspot.x;
                    yHotspot = largeHotspot.y;
                    break;
                
                case SpeciesImage.XLARGE_IMAGE_SIZE:
                    xHotspot = xLargeHotspot.x;
                    yHotspot = xLargeHotspot.y;
                    break;
            }
    
            if (xHotspot > 0)
            {
                if ((xHotspot - epsilon < x &&
                     xHotspot + epsilon > x) &&
                    (yHotspot - epsilon < y &&
                     yHotspot + epsilon > y))
                {
                    return true;
                }
            }
        }

        return false;
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
                    changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED_FROM_ROW, null, characteristic);
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
        stream.println("<" + Elements.SPECIES_IMAGE_ROW_ELEMENT_NAME + ">");

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

        // Hotspot locations
        stream.println("<" + Elements.XXSMALL_HOTSPOT_ELEMENT_NAME + ">" +
                          xxSmallHotspot.x + "," + xxSmallHotspot.y +
                          "</" + Elements.XXSMALL_HOTSPOT_ELEMENT_NAME + ">");

        stream.println("<" + Elements.XSMALL_HOTSPOT_ELEMENT_NAME + ">" +
                          xSmallHotspot.x + "," + xSmallHotspot.y +
                          "</" + Elements.XSMALL_HOTSPOT_ELEMENT_NAME + ">");

        stream.println("<" + Elements.SMALL_HOTSPOT_ELEMENT_NAME + ">" +
                          smallHotspot.x + "," + smallHotspot.y +
                          "</" + Elements.SMALL_HOTSPOT_ELEMENT_NAME + ">");

        stream.println("<" + Elements.MEDIUM_HOTSPOT_ELEMENT_NAME + ">" +
                          mediumHotspot.x + "," + mediumHotspot.y +
                          "</" + Elements.MEDIUM_HOTSPOT_ELEMENT_NAME + ">");

        stream.println("<" + Elements.LARGE_HOTSPOT_ELEMENT_NAME + ">" +
                          largeHotspot.x + "," + largeHotspot.y +
                          "</" + Elements.LARGE_HOTSPOT_ELEMENT_NAME + ">");

        stream.println("<" + Elements.XLARGE_HOTSPOT_ELEMENT_NAME + ">" +
                          xLargeHotspot.x + "," + xLargeHotspot.y +
                          "</" + Elements.XLARGE_HOTSPOT_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.SPECIES_IMAGE_ROW_ELEMENT_NAME + ">");
    }
}
