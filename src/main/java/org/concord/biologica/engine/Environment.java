//
// Class : Environment
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
 * This class represents an environment.  An environment is defined as a
 * closed biosphere in which a population of organisms exists.  The organisms
 * in one environment may not move to another environment, although they
 * may move from a subenvironment to another subenvironment.<p>
 *
 * Currently this environment doesn't contain any geometric information,
 * but it probably should at sometime in the future, once we decide if
 * geometric information (e.g. locations in an environment) is 2D or 3D.<p>
 *
 * An object of this class will environmentrate the following simple,
 * non-vector property change events:<p>
 *
 * <ul>
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.HEIGHT - height of environment has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.NAME - name of this environment has changed
 * <li> EngineProp.TERRAINS - terrains in this environment have changed
 * <li> EngineProp.WIDTH - width of environment has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#HEIGHT
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#TERRAINS
 * @see org.concord.biologica.engine.EngineProp#WIDTH
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class Environment
extends EngineObject
implements Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Name of this environment.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * environmentrated for the property named EngineProp.NAME.<p>
    **/
    private String				name;

    /**
     * World containing this organism.  May not be null.<p>
    **/
    private World				world;

    /**
     * The width in rectangles of this environment.  Must be > 0.<p>
    **/
    private int					width;

    /**
     * The height in rectangles of this environment.  Must be > 0.<p>
    **/
    private int					height;

    /**
     * The array of terrains from which instances of this
     * environment are constructed.<p>
     *
     * For example, for an environment with a width of 6 and a height of 4,
     * the array of terrains would have 24 elements ordered as shown:
     *
     * +-----+-----+-----+-----+-----+-----+
     * |	 |	   |	 |     |     |     |
     * | 0,0 | 1,0 | 2,0 | 3,0 | 4,0 | 5,0 |
     * |	 |	   |	 |     |     |     |
     * +-----+-----+-----+-----+-----+-----+
     * |	 |	   |	 |     |     |     |
     * | 0,1 | 1,1 | 2,1 | 3,1 | 4,1 | 5,1 |
     * |	 |	   |	 |     |     |     |
     * +-----+-----+-----+-----+-----+-----+
     * |	 |	   |	 |     |     |     |
     * | 0,2 | 1,2 | 2,2 | 3,2 | 4,2 | 5,2 |
     * |	 |	   |	 |     |     |     |
     * +-----+-----+-----+-----+-----+-----+
     * |	 |	   |	 |     |     |     |
     * | 0,3 | 1,3 | 2,3 | 3,3 | 4,3 | 5,3 |
     * |	 |	   |	 |     |     |     |
     * +-----+-----+-----+-----+-----+-----+
    **/
    private Terrain				terrains[][];

    /**
     * Creates a new environment with the given name.<p>
     *
     * @param		aWorld World - a world containing this environment
     * @param		aName String - name of this environment, may not be null
     * @param		aWidth int - the width of the environment, must be > 0
     * @param		aHeight int - the height of the environment, must be > 0
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Environment(World aWorld, String aName, int aWidth, int aHeight)
    {
        // Check input arguments
        if (aWorld == null ||
            aName == null ||
            aWidth <= 0 ||
            aHeight <= 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Copy name, so caller can use the name multiple times without
        // causing the String to be used in multiple engine objects.
        name = new String(aName);

        // Input arguments ok - initialize instance variables
        width = aWidth;
        height = aHeight;
        terrains = new Terrain[width][height];
        lockedState = EngineObject.UNLOCKED;

        int i,j;
        for (i=0;i<width;i++)
        {
            for (j=0;j<height;j++)
            {
                terrains[i][j] = null;
            }
        }

        // Creation successful, so add this environment to world
        world = aWorld;
        aWorld.addEnvironment(this);
    }

    /**
     * Create a new environment and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aWorld World - the enclosing world for this new environment
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this environment
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public Environment(World aWorld,
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
        name = null;
        lockedState = EngineObject.UNLOCKED;
        width = 1;
        height = 1;

        // Initialize vectors and arrays
        terrains = null;

        // Creation successful, so add this environment to world
        world = aWorld;
        world.addEnvironment(this);

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
            case Elements.NAME_ELEMENT_ID:
            case Elements.WORLD_ID_ELEMENT_ID:
            case Elements.WIDTH_ELEMENT_ID:
            case Elements.HEIGHT_ELEMENT_ID:
            case Elements.TERRAIN_IDS_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("Species " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.WORLD_ID_ELEMENT_ID:
                // Ignore, as we already know the world
                break;

            case Elements.WIDTH_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                width = Integer.valueOf(valueString).intValue();
                break;

            case Elements.HEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                height = Integer.valueOf(valueString).intValue();
                break;

            case Elements.TERRAIN_IDS_ELEMENT_ID:
                // Create terrains array
                // Must have valid width and height
                if (width > 0 && height > 0)
                {
                    int i, j, terrainID;
                    String terrainIdString;

                    terrains = new Terrain[width][height];

                    valueString = xmlElementContext.getValueString();
                    StringTokenizer st = new StringTokenizer(valueString," ,\t\n\r");
                    for (j=0;j<height;j++)
                    {
                        for (i=0;i<width;i++)
                        {
                            if (st.hasMoreTokens())
                            {
                                terrainIdString = st.nextToken();
                                if (terrainIdString.equals("null"))
                                {
                                    terrains[i][j] = null;
                                }
                                else
                                {
                                    terrainID = Integer.valueOf(terrainIdString).intValue();
                                    terrains[i][j] = (Terrain) xmlElementContext.getImportContext().getObject(terrainID);
                                }
                            }
                            else
                            {
                                terrains[i][j] = null;
                            }
                        }
                    }
                }
                break;

            case Elements.ENVIRONMENT_ELEMENT_ID:
                // Done with this environment, so pop up to enclosing world
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
     * environmentrated for the property named EngineProp.DELETED.<p>
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
        terrains = null;

        // Tell world
        world.removeEnvironment(this);
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
        return "Environment: " + name;
    }

    /**
     * Return the name of this environment.<p>
     *
     * @return		String - name of this environment, may not be null
    **/
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the name of this environment.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this environment, may not be null
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
     * Return the world containing this environment.  May not be null.<p>
     *
     * @return 		World - the world containing this environment, may not be null
    **/
    public World getWorld()
    {
        return world;
    }

    /**
     * Get the width of the environment
     *
     * @return		int - width of environment in rectangles
    **/
    public int getWidth()
    {
        return width;
    }

    /**
     * Set the width of the environment.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.WIDTH.<p>
     *
     * @param		aWidth int - a new width, must be >= 0
     * @exception	IllegalArgumentException - bad input argument
    **/
    public void setWidth(int aWidth)
    {
        if (aWidth <= 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int oldWidth = width;
        width = aWidth;

        // Adjust terrains array, which in this case means
        // either truncating or adding onto the array.
        // Adjust terrains array, which in this case means
        // either truncating or adding onto the bottom of the array.
        int i,j;
        Terrain[][] oldTerrains = terrains;
        terrains = new Terrain[width][height];

        if (oldWidth < width)
        {
            // Copy elements of old terrains array into top of new array
            for (j=0;j<height;j++)
            {
                for (i=0;i<oldWidth;i++)
                {
                    terrains[i][j] = oldTerrains[i][j];
                }
                for (i=oldWidth;i<width;i++)
                {
                    terrains[i][j] = null;
                }
            }
        }
        else
        {
            // Copy as many of the old elements into the new array as possible,
            // truncating some off the bottom of the array.
            for (j=0;j<height;j++)
            {
                for (i=0;i<width;i++)
                {
                    terrains[i][j] = oldTerrains[i][j];
                }
            }
        }

        changes.firePropertyChange(EngineProp.WIDTH,new Integer(oldWidth),new Integer(width));
    }

    /**
     * Get the height of the environment
     *
     * @return		int - height of environment in rectangles
    **/
    public int getHeight()
    {
        return height;
    }

    /**
     * Set the height of the environment.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.HEIGHT.<p>
     *
     * @param		aHeight int - a new height, must be >= 0
     * @exception	IllegalArgumentException - bad input argument
    **/
    public void setHeight(int aHeight)
    {
        if (aHeight <= 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int oldHeight = height;
        height = aHeight;

        // Adjust terrains array, which in this case means
        // either truncating or adding onto the bottom of the array.
        int i,j;
        Terrain[][] oldTerrains = terrains;
        terrains = new Terrain[width][height];

        if (oldHeight < height)
        {
            // Copy elements of old terrains array into top of new array
            for (i=0;i<width;i++)
            {
                for (j=0;j<oldHeight;j++)
                {
                    terrains[i][j] = oldTerrains[i][j];
                }
                for (j=oldHeight;j<height;j++)
                {
                    terrains[i][j] = null;
                }
            }
        }
        else
        {
            // Copy as many of the old elements into the new array as possible,
            // truncating some off the bottom of the array.
            for (i=0;i<width;i++)
            {
                for (j=0;j<height;j++)
                {
                    terrains[i][j] = oldTerrains[i][j];
                }
            }
        }

        changes.firePropertyChange(EngineProp.HEIGHT,new Integer(oldHeight),new Integer(height));
    }

    /**
     * Get the terrain at the given indexes into the terrain array
     *
     * @param		indexColumn int - column index, must be valid
     * @param		indexRow int - row index, must be valid
     * @return		Terrain - terrain at given index, may be null
     * @exception	IllegalArgumentException - invalid index
    **/
    public Terrain getTerrain(int indexColumn, int indexRow)
    {
        if (indexRow < 0 || indexRow >= height || indexColumn < 0 || indexColumn >= width)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        return terrains[indexColumn][indexRow];
    }

    /**
     * Set the terrain at the given indexes into the terrain array.
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.TERRAINS.<p>
     *
     * @param		indexColumn int - column index, must be valid
     * @param		indexRow int - row index, must be valid
     * @param		aTerrain Terrain - terrain at given index, may be null
     * @exception	IllegalArgumentException - invalid index
    **/
    public void setTerrain(int indexColumn, int indexRow, Terrain aTerrain)
    {
        if (indexRow < 0 || indexRow >= height || indexColumn < 0 || indexColumn >= width)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        terrains[indexColumn][indexRow] = aTerrain;

        changes.firePropertyChange(EngineProp.TERRAINS,null,null);
    }

    /**
     * Remove all references to the given terrain, presumably because
     * the terrain has been deleted.  This is called by the parent world
     * when a terrain is removed from the world.
     *
     * @param		aTerrain Terrain - a terrain to remove
    **/
    void removeTerrain(Terrain aTerrain)
    {
        if (aTerrain != null && terrains != null && deleted == false)
        {
            int i,j;
            boolean aTerrainFound = false;
            Terrain terrain;
            for (i=0;i<width;i++)
            {
                for (j=0;j<height;j++)
                {
                    if (terrains[i][j] == aTerrain)
                    {
                        terrains[i][j] = null;
                        aTerrainFound = true;
                    }
                }
            }

            if (aTerrainFound)
            {
                changes.firePropertyChange(EngineProp.TERRAINS,null,null);
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
        stream.println("<" + Elements.ENVIRONMENT_ELEMENT_NAME + ">");

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

        // World ID
        stream.println("<" + Elements.WORLD_ID_ELEMENT_NAME + ">" + world.getID() +
                          "</" + Elements.WORLD_ID_ELEMENT_NAME + ">");

        // Width
        stream.println("<" + Elements.WIDTH_ELEMENT_NAME + ">" + width +
                          "</" + Elements.WIDTH_ELEMENT_NAME + ">");

        // Height
        stream.println("<" + Elements.HEIGHT_ELEMENT_NAME + ">" + height +
                          "</" + Elements.HEIGHT_ELEMENT_NAME + ">");

        // Terrains
        if (terrains != null)
        {
            stream.println("<" + Elements.TERRAIN_IDS_ELEMENT_NAME + ">");

            int i,j;
            Terrain aTerrain;
            for (j=0;j<height;j++)
            {
                for (i=0;i<width;i++)
                {
                    aTerrain = terrains[i][j];
                    if (aTerrain != null)
                    {
                        stream.print(terrains[i][j].getID() + ",");
                    }
                    else
                    {
                        stream.print("null,");
                    }
                }
                stream.println("");
            }

            stream.println("</" + Elements.TERRAIN_IDS_ELEMENT_NAME + ">");
        }

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.ENVIRONMENT_ELEMENT_NAME + ">");
    }
}
