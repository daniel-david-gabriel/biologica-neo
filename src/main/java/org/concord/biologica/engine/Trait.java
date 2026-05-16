//
// Class : Trait
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.4 $
// $Date: 2003/03/12 01:29:01 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;

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
 * This class represents a trait of a species.<p>
 *
 * A trait is defined as a specific property of an organism.
 * The term "trait" is a synonym for "characteristic" and "character".<p>
 *
 * Examples of traits are "flower color", "horn type", "tail type", etc.
 * For each trait, there are a set of characteristics for that
 * trait - "purple" and "white" for flower color, "no horns" and "2 horns"
 * for horn type, etc.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.CHARACTERISTIC_ADDED - a characteristic was added to trait
 * <li> EngineProp.CHARACTERISTIC_REMOVED - a characteristic was removed from trait
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - the locked state of the object has changed
 * <li> EngineProp.NAME - name of this trait has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_ADDED
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_REMOVED
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.4 $ $Date: 2003/03/12 01:29:01 $
 * @author 		$Author: dima $
**/

public final class Trait
extends EngineObject
implements Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * The species which contains this trait.  Read-only.  Never null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to a species, the species will generate a
     * vector property change event for a new trait.<p>
    **/
    private Species				species;

    /**
     * Name of this trait.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME
    **/
    private String				name;

    /**
     * The set of characteristics or partial phenotypes of this trait.
     * For example, if this trait was "horn type", then this set might
     * include "no horns", "1 horn" and "2 horns".<p>
     *
     * In the real world, there are more than just a few possible
     * characteristics for each trait, but we assume we'll
     * simplify reality to make it understandable to children.<p>
     *
     * We'll use "characteristic" instead of "partial phenotype" in the
     * name of this instance variable and in its related methods
     * because that's a common thing in genetics.<p>
     *
     * All the objects on this vector must be instances of Characteristic.<p>
     *
     * When a Characteristic object is created and added to this vector,
     * a EngineProp.CHARACTERISTIC_ADDED property change event is fired.<p>
     *
     * When a Characteristic object is deleted and removed from this vector,
     * a EngineProp.CHARACTERISTIC_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Characteristic
     * @see		org.concord.biologica.engine.EngineProp#CHARACTERISTIC_ADDED
     * @see		org.concord.biologica.engine.EngineProp#CHARACTERISTIC_REMOVED
    **/
    private Vector				characteristics;

    /**
     * Show this trait as text in the organism view?
     *
     * @see		org.concord.biologica.engine.EngineProp#SHOW_TRAIT_AS_TEXT_IN_ORGANISM_VIEW
    **/
    private boolean				showAsTextInOrganismView;

    /**
     * Creates a new trait with the given name.<p>
     *
     * @param		aSpecies Species - the species containing this trait, may not be null
     * @param		aName String - name of this trait, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Trait(Species aSpecies, String aName)
    {
        // Check input arguments
        if (aSpecies == null ||
            aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Copy name, so caller can use the name multiple times without
        // causing the String to be used in multiple engine objects.
        name = new String(aName);
        lockedState = EngineObject.UNLOCKED;

        characteristics = new Vector();
        showAsTextInOrganismView = false;

        // Creation successful - tell species
        species = aSpecies;
        species.addTrait(this);
    }

    /**
     * Create a new trait and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aSpecies Species - the enclosing species for this new trait
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
/*
    public Trait(Species aSpecies,
                 String anElementName,
                 int anElementID,
                 com.sun.xml.parser.Parser anXMLParser,
                 ImportContext importContext)
*/
    public Trait(Species aSpecies,
                 String anElementName,
                 int anElementID,
                 SAXParser anXMLParser,
                 ImportContext importContext)
    {
        if (aSpecies == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        name = null;
        showAsTextInOrganismView = false;
        lockedState = EngineObject.UNLOCKED;

        // Create vectors
        characteristics = new Vector();

        // Creation successful, so add this trait to species
        species = aSpecies;
        species.addTrait(this);

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
            case Elements.SPECIES_ID_ELEMENT_ID:
            case Elements.NAME_ELEMENT_ID:
            case Elements.SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.CHARACTERISTIC_ELEMENT_ID:
//            	System.out.println("CLASS "+xmlElementContext.getXMLParser().getClass().getName());
                new Characteristic(this,anElementName,anElementID,
                                   xmlElementContext.getXMLParser(),
                                   xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("Trait " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.SPECIES_ID_ELEMENT_ID:
                // Ignore, as we already have the species
                break;

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                showAsTextInOrganismView = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.CHARACTERISTIC_ELEMENT_ID:
                // Done with a characteristic, so reclaim document handler
                break;

            case Elements.TRAIT_ELEMENT_ID:
                // Done with this trait, so pop up to enclosing species
                species.endElement(anElementName);
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
     * When this method is called, a property change event is
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

        Characteristic aCharacteristic;
        Vector characteristicsClone = (Vector) characteristics.clone();
        Enumeration eCharacteristics = characteristicsClone.elements();
        //Enumeration eCharacteristics = characteristics.elements();
        while (eCharacteristics.hasMoreElements())
        {
            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
            aCharacteristic.delete(notifyChange);
        }
        characteristics.removeAllElements();
        characteristics = null;
        characteristicsClone = null;
        
        species.removeTrait(this);
        species = null;
        id = NULL_ID;
    }

    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        return EngineStrings.TRAIT_COLON + name;
    }

    /**
     * Set or unset the automatic locked state of this object, leaving
     * other components of the locked state untouched.  Recursively
     * sets the automatic locked state of children of this object.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * @param		automaticLocked boolean - object should be automatic locked (true) or not (false)
    **/
    public void setAutomaticLocked(boolean automaticLocked)
    {
        // Use EngineObject implementation for changing the state of this object
        super.setAutomaticLocked(automaticLocked);

        // Set automatic locked state of children
        Characteristic aCharacteristic;
        Enumeration eCharacteristics = characteristics.elements();
        while (eCharacteristics.hasMoreElements())
        {
            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
            aCharacteristic.setAutomaticLocked(automaticLocked);
        }
    }

    /**
     * Set or unset the manual locked state of this object, leaving
     * other components of the locked state untouched.  Recursively
     * sets the manual locked state of children of this object.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     *
     * @param		manualLocked boolean - object should be manually locked (true) or not (false)
    **/
    public void setManualLocked(boolean manualLocked)
    {
        // Use EngineObject implementation for changing the state of this object
        super.setManualLocked(manualLocked);

        // Set manual locked state of children
        Characteristic aCharacteristic;
        Enumeration eCharacteristics = characteristics.elements();
        while (eCharacteristics.hasMoreElements())
        {
            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
            aCharacteristic.setManualLocked(manualLocked);
        }
    }

    /**
     * Set the locked state of the object, recursively setting the lock
     * state of children objects.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * @param		aLockedState int - new locked state of this object
     * @exception	IllegalArgumentException - new locked state invalid
    **/
    public void setLockedState(int aLockedState)
    {
        // Use EngineObject implementation for changing the locked state of this object
        super.setLockedState(aLockedState);
            
        // Set locked state of children
        Characteristic aCharacteristic;
        Enumeration eCharacteristics = characteristics.elements();
        while (eCharacteristics.hasMoreElements())
        {
            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
            aCharacteristic.setLockedState(aLockedState);
        }
    }

    /**
     * Return the species of this trait.<p>
     *
     * @return		Species - species of this trait, never null
    **/
    public Species getSpecies()
    {
        return species;
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return species.getWorld();
    }

    /**
     * Return the name of this trait.<p>
     *
     * @return		String - name of this trait, may not be null
    **/
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the name of this trait.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this trait, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setName(String aName)
    {
        // Return immediately if aName equals the current name
        if (aName != null && name != null && aName.equals(name))
        {
            return;
        }

        // Validate input arguments
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
     * Get whether this trait should be shown as text in the organism view.<p>
     *
     * @return	boolean - whether this trait should be shown as text in organism view.
    **/
    public boolean isShowAsTextInOrganismView()
    {
        return showAsTextInOrganismView;
    }

    /**
     * Set whether this trait should be shown as text in the organism view.<p>
     *
     * @param	TorF boolean - shown as text (true) or not shown as text (false)
    **/
    public void setShowAsTextInOrganismView(boolean TorF)
    {
        if (TorF != showAsTextInOrganismView)
        {
            boolean oldShowAsTextInOrganismView = showAsTextInOrganismView;
            showAsTextInOrganismView = TorF;
    
            changes.firePropertyChange(EngineProp.SHOW_TRAIT_AS_TEXT_IN_ORGANISM_VIEW,
                                       new Boolean(oldShowAsTextInOrganismView),
                                       new Boolean(showAsTextInOrganismView));
        }
    }

    /**
     * Returns an enumeration over the vector of characteristics in this trait.<p>
     *
     * @return		Enumeration - an enumeration over the characteristics in this trait
    **/
    public Enumeration getCharacteristics()
    {
        Vector characteristicsClone = (Vector) characteristics.clone();
        return characteristicsClone.elements();
    }

    /**
     * Return the number of characteristics of this trait.<p>
     *
     * @return		int - the number of characteristics of this trait
    **/
    public int getNumberOfCharacteristics()
    {
        return characteristics.size();
    }

    /**
     * Adds a characteristic to the trait.<p>
     *
     * Package protected because this is only called from the Characteristic
     * constructor.  Creating a characteristic automatically adds it to the
     * trait.<p>
     *
     * @param		aCharacteristic Characteristic - a new characteristic, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addCharacteristic(Characteristic aCharacteristic)
    {
        characteristics.addElement(aCharacteristic);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.CHARACTERISTIC_ADDED, null, aCharacteristic);
    }

    /**
     * Removes a characteristic from the trait.<p>
     *
     * Package protected because this is only called from the Trait
     * delete method.  Deleting a characteristic automatically removes
     * it from the trait.<p>
     *
     * @param		aCharacteristic Characteristic - a characteristic, may not be null
     * @return		boolean indicating whether or not the characteristic was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeCharacteristic(Characteristic aCharacteristic)
    {
        if (aCharacteristic == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = characteristics.removeElement(aCharacteristic);

        // Notify listeners if characteristic removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.CHARACTERISTIC_REMOVED, null, aCharacteristic);
        }

        return result;
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
        stream.println("<" + Elements.TRAIT_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Species ID
        if (species == null)
        {
            stream.println("<" + Elements.SPECIES_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SPECIES_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SPECIES_ID_ELEMENT_NAME + ">" + species.getID() +
                              "</" + Elements.SPECIES_ID_ELEMENT_NAME + ">");
        }

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

        // Show as text in organism view
        stream.println("<" + Elements.SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_NAME + ">" + showAsTextInOrganismView +
                          "</" + Elements.SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Recursively write children of trait
        Characteristic aCharacteristic;
        Enumeration eCharacteristics = characteristics.elements();
        while (eCharacteristics.hasMoreElements())
        {
            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
            aCharacteristic.writeToStream(stream);
        }
        eCharacteristics = null;
        aCharacteristic = null;
        
        // End object
        stream.println("</" + Elements.TRAIT_ELEMENT_NAME + ">");
    }
}
