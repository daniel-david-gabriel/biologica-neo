//
// Class : Characteristic
//
// Copyright © 1997, The Concord Consortium
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

import java.awt.Color;

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

import org.apache.xerces.parsers.SAXParser;//dima

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;

/**
 * This class represents a characteristic of a trait of a species.<p>
 *
 * A trait is defined as a specific property of an organism.
 * The term "trait" is a synonym for "characteristic" and "character".<p>
 *
 * A characteristic is really a characteristic and is a single possible
 * trait value.<p>
 *
 * Examples of traits are "flower color", "horn type", "tail type", etc.
 * For each trait, there are a set of characteristics for that
 * trait - "purple" and "white" for flower color, "no horns" and "2 horns"
 * for horn type, etc.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.NAME - name of characteristic has changed
 * <li> EngineProp.PEDIGREE_SYMBOL_TYPE - solid color or two color symbol in pedigree view
 * <li> EngineProp.PEDIGREE_SYMBOL_FIRST_COLOR - first color used for this characteristic in pedigree view
 * <li> EngineProp.PEDIGREE_SYMBOL_SECOND_COLOR - second color used for this characteristic in pedigree view
 * <li> EngineProp.FATAL - characteristic is fatal
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#PEDIGREE_SYMBOL_TYPE
 * @see org.concord.biologica.engine.EngineProp#PEDIGREE_SYMBOL_FIRST_COLOR
 * @see org.concord.biologica.engine.EngineProp#PEDIGREE_SYMBOL_SECOND_COLOR
 * @see org.concord.biologica.engine.EngineProp#FATAL
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class Characteristic
extends EngineObject
implements Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * The trait which contains this characteristic.  Read-only.  May not be null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to a trait, the trait will generate a
     * property change event for a new characteristic.<p>
    **/
    private Trait				trait;

    /**
     * Name of this characteristic.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME
    **/
    private String				name;

    /**
     * Image DIB ID.  May be any integer value.<p>
    **/
    private int					imageID;

    /**
     * Boolean indicating that this is a fatal characteristic.
    **/
    private boolean				fatal;

    /**
     * Pedigree symbol type - either PEDIGREE_SYMBOL_SOLID_COLOR or PEDIGREE_SYMBOL_FORWARD_SLASH.<p>
     *
     * @see		org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_SOLID_COLOR
     * @see		org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_FORWARD_SLASH
    **/
    private int					pedigreeSymbolType;

    /**
     * First color used when drawing pedigree symbol for this characteristic.
    **/
    private Color				pedigreeSymbolFirstColor;

    /**
     * Second color used when drawing pedigree symbol for this characteristic.
    **/
    private Color				pedigreeSymbolSecondColor;

    /**
     * Pedigree symbol type values, used to determine how to draw pedigree symbols.
     * PEDIGREE_SYMBOL_SOLID_COLOR means the square or circle is one solid color
     * (pedigreeSymbolFirstColor).  PEDIGREE_SYMBOL_FORWARD_SLASH means the square
     * or circle has a diagonal line through its middle with a top color
     * (pedigreeSymbolFirstColor) and a bottom color (pedigreeSymbolSecondColor).<p>
    **/
    static final public int		PEDIGREE_SYMBOL_SOLID_COLOR 	= 0;
    static final public String  PEDIGREE_SYMBOL_SOLID_COLOR_STRING = "solidColor";

    static final public int		PEDIGREE_SYMBOL_FORWARD_SLASH 	= 1;
    static final public String  PEDIGREE_SYMBOL_FORWARD_SLASH_STRING = "forwardSlash";

    /**
     * Creates a new characteristic with the given name.<p>
     *
     * @param		aTrait Trait - the trait containing this characteristic, may not be null
     * @param		aName String - name of this characteristic, may not be null
     * @param		anImageID int - id of an image (e.g. a DIB id), may be any int value
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Characteristic(Trait aTrait, String aName, int anImageID)
    {
        // Check input arguments
        if (aTrait == null ||
            aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Copy name, so caller can use the name multiple times without
        // causing the String to be used in multiple engine objects.
        name = new String(aName);

        imageID = anImageID;
        fatal = false;
        lockedState = EngineObject.UNLOCKED;

        // Set default values for pedigree colors
        pedigreeSymbolType = PEDIGREE_SYMBOL_SOLID_COLOR;
        pedigreeSymbolFirstColor = Color.white;
        pedigreeSymbolSecondColor = Color.black;

        // Creation successful - tell trait
        trait = aTrait;
        trait.addCharacteristic(this);
    }

    /**
     * Create a new Characteristic and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aTrait Trait - the enclosing trait for this new characteristic
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
/*
    public Characteristic(Trait aTrait,
                          String anElementName,
                          int anElementID,
                          com.sun.xml.parser.Parser anXMLParser,
                          ImportContext importContext)
*/
    public Characteristic(Trait aTrait,
                          String anElementName,
                          int anElementID,
                          SAXParser anXMLParser,
                          ImportContext importContext)
    {
        if (aTrait == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        name = null;
        imageID = 0;
        fatal = false;
        lockedState = EngineObject.UNLOCKED;
        pedigreeSymbolType = PEDIGREE_SYMBOL_SOLID_COLOR;
        pedigreeSymbolFirstColor = Color.white;
        pedigreeSymbolSecondColor = Color.black;

        // Creation successful, so add this characteristic to trait
        trait = aTrait;
        trait.addCharacteristic(this);

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
            case Elements.TRAIT_ID_ELEMENT_ID:
            case Elements.NAME_ELEMENT_ID:
            case Elements.IMAGE_ID_ELEMENT_ID:
            case Elements.PEDIGREE_SYMBOL_TYPE_ELEMENT_ID:
            case Elements.PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_ID:
            case Elements.PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_ID:
            case Elements.FATAL_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("Characteristic " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        String valueString, colorString;
        int colorRed, colorGreen, colorBlue;

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

            case Elements.TRAIT_ID_ELEMENT_ID:
                // Ignore, as we already have the trait
                break;

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.IMAGE_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                imageID = Integer.valueOf(valueString).intValue();
                break;

            case Elements.PEDIGREE_SYMBOL_TYPE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setPedigreeSymbolTypeAsString(valueString);
                break;

            case Elements.PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_ID:
                {
                    valueString = xmlElementContext.getValueString();
                    StringTokenizer st = new StringTokenizer(valueString,EngineStrings.COMMA);
                    colorString = st.nextToken();
                    colorRed = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorGreen = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorBlue = Integer.valueOf(colorString).intValue();
                    pedigreeSymbolFirstColor = new Color(colorRed,colorGreen,colorBlue);
                }
                break;

            case Elements.PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_ID:
                {
                    valueString = xmlElementContext.getValueString();
                    StringTokenizer st = new StringTokenizer(valueString,EngineStrings.COMMA);
                    colorString = st.nextToken();
                    colorRed = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorGreen = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorBlue = Integer.valueOf(colorString).intValue();
                    pedigreeSymbolSecondColor = new Color(colorRed,colorGreen,colorBlue);
                }
                break;

            case Elements.FATAL_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                fatal = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.CHARACTERISTIC_ELEMENT_ID:
                // Done with this characteristic, so create colors and pop up to enclosing trait
                trait.endElement(anElementName);
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
        imageID = 0;

        // Tell trait
        trait.removeCharacteristic(this);
        trait = null;
        id = EngineObject.NULL_ID;
    }

    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        return "Characteristic: " + name;
    }

    /**
     * Return the trait of this characteristic.<p>
     *
     * @return		Trait - trait of this characteristic, never null
    **/
    public Trait getTrait()
    {
        return trait;
    }

    /**
     * Get the species containing this object.<p>
     *
     * @return	Species - the species containing this object, never null.
    **/
    public Species getSpecies()
    {
        return trait.getSpecies();
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return trait.getWorld();
    }

    /**
     * Return the name of this characteristic.<p>
     *
     * @return		String - name of this characteristic, may not be null
    **/
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the name of this characteristic.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this characteristic, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setName(String aName)
    {
        // Return immediately if aName equals the current name
        if (aName != null && name != null && aName.equals(name))
        {
            return;
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
     * Return the image ID of this characteristic.<p>
     *
     * @return		int - id of this characteristic's image
    **/
    public int getImageID()
    {
        return imageID;
    }
    
    /**
     * Set the image ID of this characteristic.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.IMAGE_ID.<p>
     *
     * @param		anImageID int - the new image ID of this characteristic, may not be null
    **/
    public void setImageID(int anImageID)
    {
        int oldImageID = imageID;
        imageID = anImageID;

        // Notify listeners
        changes.firePropertyChange(EngineProp.IMAGE_ID,
                                   new Integer(oldImageID),
                                   new Integer(imageID));
    }

    /**
     * Get the pedigree symbol type of this characteristic.
     *
     * @return		int - pedigree symbol type of this characteristic
     * @see			org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_SOLID_COLOR
     * @see			org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_FORWARD_SLASH
    **/
    public int getPedigreeSymbolType()
    {
        return pedigreeSymbolType;
    }

    /**
     * Get the pedigree symbol type of this characteristic as a string
     *
     * @return		String - pedigree symbol type of this characteristic as a string
     * @see			org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_SOLID_COLOR_STRING
     * @see			org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_FORWARD_SLASH_STRING
    **/
    public String getPedigreeSymbolTypeAsString()
    {
        if (pedigreeSymbolType == PEDIGREE_SYMBOL_SOLID_COLOR)
        {
            return PEDIGREE_SYMBOL_SOLID_COLOR_STRING;
        }
        else if (pedigreeSymbolType == PEDIGREE_SYMBOL_FORWARD_SLASH)
        {
            return PEDIGREE_SYMBOL_FORWARD_SLASH_STRING;
        }

        // default to solid color
        return PEDIGREE_SYMBOL_SOLID_COLOR_STRING;
    }

    /**
     * Set the first pedigree symbol type of this characteristic
     *
     * @param		aSymbolType int - the new pedigree symbol type of this characteristic
     * @exception	IllegalArgumentException - input type unknown
    **/
    public void setPedigreeSymbolType(int aSymbolType)
    {
        if (aSymbolType != PEDIGREE_SYMBOL_SOLID_COLOR &&
            aSymbolType != PEDIGREE_SYMBOL_FORWARD_SLASH)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if symbol types the same
        if (pedigreeSymbolType == aSymbolType)
        {
            return;
        }

        int oldSymbolType = pedigreeSymbolType;
        pedigreeSymbolType = aSymbolType;

        // Notify listeners
        changes.firePropertyChange(EngineProp.PEDIGREE_SYMBOL_TYPE,
                                   new Integer(oldSymbolType),
                                   new Integer(pedigreeSymbolType));
    }

    /**
     * Set the first pedigree symbol type of this characteristic, using a string as input.
     *
     * @param		aSymbolTypeString String - the new pedigree symbol type of this characteristic
     * @exception	IllegalArgumentException - input type unknown
    **/
    public void setPedigreeSymbolTypeAsString(String aSymbolTypeString)
    {
        int aSymbolType = 0;
        if (aSymbolTypeString == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        else if (aSymbolTypeString.equals(PEDIGREE_SYMBOL_SOLID_COLOR_STRING))
        {
            aSymbolType = PEDIGREE_SYMBOL_SOLID_COLOR;
        }
        else if (aSymbolTypeString.equals(PEDIGREE_SYMBOL_FORWARD_SLASH_STRING))
        {
            aSymbolType = PEDIGREE_SYMBOL_FORWARD_SLASH;
        }

        if (aSymbolType != PEDIGREE_SYMBOL_SOLID_COLOR &&
            aSymbolType != PEDIGREE_SYMBOL_FORWARD_SLASH)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if symbol types the same
        if (pedigreeSymbolType == aSymbolType)
        {
            return;
        }

        int oldSymbolType = pedigreeSymbolType;
        pedigreeSymbolType = aSymbolType;

        // Notify listeners
        changes.firePropertyChange(EngineProp.PEDIGREE_SYMBOL_TYPE,
                                   new Integer(oldSymbolType),
                                   new Integer(pedigreeSymbolType));
    }

    /**
     * Get the first pedigree symbol color of this characteristic.
     *
     * @return		Color - pedigree symbol first color, never null
    **/
    public Color getPedigreeSymbolFirstColor()
    {
        return pedigreeSymbolFirstColor;
    }

    /**
     * Set the first pedigree symbol color of this characteristic
     *
     * @param		aColor Color - the new first pedigree symbol color of this characteristic
     * @exception	IllegalArgumentException - input color null
    **/
    public void setPedigreeSymbolFirstColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if colors the same
        if (pedigreeSymbolFirstColor.getRGB() == aColor.getRGB())
        {
            return;
        }

        Color oldColor = pedigreeSymbolFirstColor;
        pedigreeSymbolFirstColor = aColor;

        // Notify listeners
        changes.firePropertyChange(EngineProp.PEDIGREE_SYMBOL_FIRST_COLOR,oldColor,pedigreeSymbolFirstColor);
    }

    /**
     * Get the second pedigree symbol color of this characteristic.
     *
     * @return		Color - pedigree symbol second color, never null
    **/
    public Color getPedigreeSymbolSecondColor()
    {
        return pedigreeSymbolSecondColor;
    }

    /**
     * Set the second pedigree symbol color of this characteristic
     *
     * @param		aColor Color - the new second pedigree symbol color of this characteristic
     * @exception	IllegalArgumentException - input color null
    **/
    public void setPedigreeSymbolSecondColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if colors the same
        if (pedigreeSymbolSecondColor.getRGB() == aColor.getRGB())
        {
            return;
        }

        Color oldColor = pedigreeSymbolSecondColor;
        pedigreeSymbolSecondColor = aColor;

        // Notify listeners
        changes.firePropertyChange(EngineProp.PEDIGREE_SYMBOL_SECOND_COLOR,oldColor,pedigreeSymbolSecondColor);
    }

    /**
     * Return the fatality of this characteristic.<p>
     *
     * @return		boolean - whether or not this characteristic is fatal
    **/
    public boolean isFatal()
    {
        return fatal;
    }
    
    /**
     * Set the fatality of this characteristic.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.FATAL.<p>
     *
     * @param		aFatal boolean - the new fatality of this characteristic
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setFatal(boolean aFatal)
    {
        // Return immediately if no change
        if (aFatal == fatal)
        {
            return;
        }

        boolean oldFatal = fatal;
        fatal = aFatal;

        // Notify listeners
        changes.firePropertyChange(EngineProp.FATAL,
                                   new Boolean(oldFatal),
                                   new Boolean(fatal));
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
        stream.println("<" + Elements.CHARACTERISTIC_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Trait ID
        stream.println("<" + Elements.TRAIT_ID_ELEMENT_NAME + ">" + trait.getID() +
                          "</" + Elements.TRAIT_ID_ELEMENT_NAME + ">");

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

        // Image ID
        stream.println("<" + Elements.IMAGE_ID_ELEMENT_NAME + ">" + imageID +
                          "</" + Elements.IMAGE_ID_ELEMENT_NAME + ">");

        // Pedigree symbol type
        stream.println("<" + Elements.PEDIGREE_SYMBOL_TYPE_ELEMENT_NAME + ">" +
                          getPedigreeSymbolTypeAsString() +
                          "</" + Elements.PEDIGREE_SYMBOL_TYPE_ELEMENT_NAME + ">");

        // Pedigree symbol first color red, green and blue components
        stream.println("<" + Elements.PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_NAME + ">" +
                          pedigreeSymbolFirstColor.getRed() + "," +
                          pedigreeSymbolFirstColor.getGreen() + "," +
                          pedigreeSymbolFirstColor.getBlue() +
                          "</" + Elements.PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_NAME + ">");

        // Pedigree symbol second color red, green and blue components
        stream.println("<" + Elements.PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_NAME + ">" +
                          pedigreeSymbolSecondColor.getRed() + "," +
                          pedigreeSymbolSecondColor.getGreen() + "," +
                          pedigreeSymbolSecondColor.getBlue() +
                          "</" + Elements.PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_NAME + ">");

        // Fatal
        stream.println("<" + Elements.FATAL_ELEMENT_NAME + ">" + fatal +
                          "</" + Elements.FATAL_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.CHARACTERISTIC_ELEMENT_NAME + ">");
    }
}
