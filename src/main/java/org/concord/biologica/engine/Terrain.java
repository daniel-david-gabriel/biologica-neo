//
// Class : Terrain
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

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;
import org.apache.xerces.parsers.SAXParser;

/**
 * This class represents a type of terrain in a world.  Terrains are "meta-descriptions"
 * which can be used to create environments.  An environment is an array of regions,
 * each using an existing terrain as its blueprint.<p>
 *
 * An object of this class will generate the following simple,
 * non-vector property change events:<p>
 *
 * <ul>
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.NAME - name of this terrain has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.COLOR - the color of the terrain
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class Terrain
extends EngineObject
implements Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Name of this terrain.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
    **/
    private String				name;

    /**
     * World containing this terrain.  May not be null.<p>
    **/
    private World				world;

    /**
     * Color used when drawing the terrain.  May not be null.<p>
    **/
    private Color				color;

    /**
     * Creates a new terrain in the given world with the given name and color.<p>
     *
     * @param		aWorld World - a world containing this terrain
     * @param		aName String - name of this terrain, may not be null
     * @param		aColor Color - a color for the terrain, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Terrain(World aWorld, String aName, Color aColor)
    {
        // Check input arguments
        if (aWorld == null ||
            aName == null ||
            aColor == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Copy name, so caller can use the name multiple times without
        // causing the String to be used in multiple engine objects.
        name = new String(aName);

        // Input arguments ok - initialize instance variables
        color = aColor;

        // Creation successful, so add this terrain to the world
        world = aWorld;
        world.addTerrain(this);
    }

    /**
     * Create a new terrain and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aWorld World - the enclosing world for this new terrain
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public Terrain(World aWorld,
                   String anElementName,
                   int anElementID,
                   /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                   ImportContext importContext)
    {
        if (aWorld == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        lockedState = EngineObject.UNLOCKED;
        color = Color.red;
        name = null;

        // Creation successful, so add this terrain to the world
        world = aWorld;
        world.addTerrain(this);

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
            case Elements.WORLD_ID_ELEMENT_ID:
            case Elements.NAME_ELEMENT_ID:
            case Elements.COLOR_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("Terrain " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.WORLD_ID_ELEMENT_ID:
                // Ignore, as we already have the world
                break;

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.COLOR_ELEMENT_ID:
                {
                    valueString = xmlElementContext.getValueString();
                    StringTokenizer st = new StringTokenizer(valueString,EngineStrings.COMMA);
                    colorString = st.nextToken();
                    colorRed = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorGreen = Integer.valueOf(colorString).intValue();
                    colorString = st.nextToken();
                    colorBlue = Integer.valueOf(colorString).intValue();
                    color = new Color(colorRed,colorGreen,colorBlue);
                }
                break;

            case Elements.TERRAIN_ELEMENT_ID:
                // Done with this terrain, so pop up to enclosing world
                world.endElement(anElementName);
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

        // Tell world
        world.removeTerrain(this);
        world = null;
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
        return EngineStrings.TERRAIN_COLON + name;
    }

    /**
     * Return the name of this terrain.<p>
     *
     * @return		String - name of this terrain, may not be null
    **/
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the name of this terrain.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this terrain, may not be null
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
     * Return the world containing this terrain.  May not be null.<p>
     *
     * @return 		World - the world containing this terrain, may not be null
    **/
    public World getWorld()
    {
        return world;
    }

    /**
     * Get the color of this terrain
     *
     * @return		Color - color of terrain, never null
    **/
    public Color getColor()
    {
        return color;
    }

    /**
     * Set the color of this terrain
     *
     * @param		aColor Color - the color of the terrain, may not be null
     * @exception	IllegalArgumentException - input color null
    **/
    public void setColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if colors the same
        if (color.getRGB() == aColor.getRGB())
        {
            return;
        }

        Color oldColor = color;
        color = aColor;

        // Notify listeners
        changes.firePropertyChange(EngineProp.COLOR,oldColor,color);
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
        stream.println("<" + Elements.TERRAIN_ELEMENT_NAME + ">");

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

        // Terrain color red, green and blue components
        stream.println("<" + Elements.COLOR_ELEMENT_NAME + ">" +
                          color.getRed() + "," +
                          color.getGreen() + "," +
                          color.getBlue() +
                          "</" + Elements.COLOR_ELEMENT_NAME + ">");

        // World ID
        stream.println("<" + Elements.WORLD_ID_ELEMENT_NAME + ">" + world.getID() +
                          "</" + Elements.WORLD_ID_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.TERRAIN_ELEMENT_NAME + ">");
    }
}
