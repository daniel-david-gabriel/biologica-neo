//
// Class : OrganismImage
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.3 $
// $Date: 2003/01/28 18:40:14 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.io.File;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.PrintWriter;

import java.beans.PropertyChangeEvent;
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
 * This class represents a portion of the image used to draw an
 * organism.  A single OrganismImage maintains a reference to
 * a single SpeciesImage and a reference to a single cell in that
 * SpeciesImage.  That single cell is one, probably of many, cells
 * used to draw an organism.<p>
 *
 * For example, if we have an organism of species dragon and we
 * have a SpeciesImage named "bodies" which contains all the images
 * of bodies used for the dragon species, then an OrganismImage
 * for that organism would contain a reference to the "bodies"
 * SpeciesImage and the row and column integer numbers of the
 * cell that represents the body for that organism.  The organism
 * would have multiple OrganismImage objects - the bodies one and
 * others for other parts of the dragaon (head, wings, scales, etc.).
 *
 * An OrganismImage also knows how to draw itself if given a Graphics
 * object and a location.  In other words, an organism draws itself
 * by walking through its OrganismImage list asking each OrganismImage
 * to draw itself at the appropriate location.
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.COLUMN_INDEX - the column index for this object has changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - object has been locked or unlocked
 * <li> EngineProp.ROW_INDEX - the row index for this object has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#COLUMN_INDEX
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#ROW_INDEX
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.3 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class OrganismImage
extends EngineObject
implements Serializable, PropertyChangeListener
{
    /**
     * Indicates image should not draw anything, as there was no cell
     * in the speciesImage which should be drawn for this organism.
    **/
    static final public int		NO_CELL = -1;

    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * The organism which contains this organism image.  May not be null.<p>
    **/
    private Organism			organism;

    /**
     * The species image corresponding to this object.  May not be null.<p>
    **/
    private SpeciesImage		speciesImage;

    /**
     * Column in associated species image to be drawn.  May be a number 0 or greater or NO_CELL.<p>
    **/
    private int					columnIndex;

    /**
     * Row in associated species image to be drawn.  May be a number 0 or greater or NO_CELL.<p>
    **/
    private int					rowIndex;

    /**
     * Creates a new organism image given an organism and a species image.<p>
     *
     * @param		anOrganism Organism - the organism containing this image, may not be null
     * @param		aSpeciesImage SpeciesImage - the species image corresponding to this object
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismImage(Organism anOrganism, SpeciesImage aSpeciesImage)
    {
        // Check input arguments
        if (anOrganism == null ||
            aSpeciesImage == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set instance variables
        rowIndex = NO_CELL;
        columnIndex = NO_CELL;
        organism = anOrganism;
        speciesImage = aSpeciesImage;
        lockedState = EngineObject.UNLOCKED;

        // Creation successful - tell organism
        organism.addOrganismImage(this);

        // Add this object as a listener on the speciesImage, so we'll
        // be notified if the speciesImage changes substantially or is deleted.
        speciesImage.addPropertyChangeListener(this);

        // Calcuate actual row and column indices of this organism image
        speciesImage.updateOrganismImage(this);
    }

    /**
     * Create a new organism image and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      anOrganism Organism - the enclosing organism for this new organism image
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public OrganismImage(Organism anOrganism,
                         String anElementName,
                         int anElementID,
                         /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                         ImportContext importContext)
    {
        if (anOrganism == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        lockedState = EngineObject.UNLOCKED;
        rowIndex = NO_CELL - 1;
        columnIndex = NO_CELL - 1;

        // Creation successful, so add this organism image to organism
        organism = anOrganism;
        //organism.addOrganismImage(this); // Now added in Organism after parsing is done.

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
            case Elements.ORGANISM_ID_ELEMENT_ID:
            case Elements.SPECIES_IMAGE_ID_ELEMENT_ID:
            case Elements.COLUMN_INDEX_ELEMENT_ID:
            case Elements.ROW_INDEX_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("OrganismImage " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        int speciesImageID;

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

            case Elements.ORGANISM_ID_ELEMENT_ID:
                // Ignore, as we already have the organism
                break;

            case Elements.SPECIES_IMAGE_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    speciesImage = null;
                }
                else
                {
                    speciesImageID = Integer.valueOf(valueString).intValue();
                    if (speciesImageID != EngineObject.NULL_ID)
                    {
                        speciesImage = (SpeciesImage) xmlElementContext.getImportContext().getObject(speciesImageID);
                    }
                    else
                    {
                        speciesImage = null;
                    }
                }
                break;

            case Elements.COLUMN_INDEX_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                columnIndex = Integer.valueOf(valueString).intValue();
                break;

            case Elements.ROW_INDEX_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                rowIndex = Integer.valueOf(valueString).intValue();
                break;

            case Elements.ORGANISM_IMAGE_ELEMENT_ID:
                // Done with this organism image , so pop up to enclosing organism
                organism.endElement(anElementName);
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
        columnIndex = NO_CELL;
        rowIndex = NO_CELL;
        organismImageDirty = true;

        // Tell species image and organism
        organism.removeOrganismImage(this);
        organism = null;

        // Remove this object as a listener on the speciesImage
        speciesImage.removePropertyChangeListener(this);
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
        return EngineStrings.ORGANISM_IMAGE_COLON + organism.getName() + EngineStrings.DASH + speciesImage.getName();
    }

    /**
     * Return the organism of this object.<p>
     *
     * @return		Organism - organism of this object, never null
    **/
    public Organism getOrganism()
    {
        return organism;
    }

    /**
     * Return the species image of this object.<p>
     *
     * @return		SpeciesImage - species image of this object, never null
    **/
    public SpeciesImage getSpeciesImage()
    {
        return speciesImage;
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return organism.getWorld();
    }

    /**
     * Return the row index of this organism image in its corresponding species image.<p>
     *
     * @return		int - row index of this organism image
    **/
    public int getRowIndex()
    {
        return rowIndex;
    }
    
    /**
     * Sets the row index of this organism image.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.ROW.<p>
     *
     * @param		aRowIndex int - new row index, may not be < NO_CELL
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setRowIndex(int aRowIndex)
    {
        // Return immediately if this is not a change
        if (aRowIndex == rowIndex)
        {
            return;
        }

        // Validate input arguments
        if (aRowIndex < NO_CELL)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int oldValue = rowIndex;
        rowIndex = aRowIndex;

        // Notify listeners
        changes.firePropertyChange(EngineProp.ROW_INDEX,
                                   new Integer(oldValue),
                                   new Integer(rowIndex));
        organismImageDirty = true;
    }

    /**
     * Return the column index of this organism image in its corresponding species image.<p>
     *
     * @return		int - column index of this organism image
    **/
    public int getColumnIndex()
    {
        return columnIndex;
    }
    
    /**
     * Sets the column index of this organism image.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.ROW.<p>
     *
     * @param		aColumnIndex int - new column index, may not be < NO_CELL
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setColumnIndex(int aColumnIndex)
    {
        // Return immediately if this is not a change
        if (aColumnIndex == columnIndex)
        {
            return;
        }

        // Validate input arguments
        if (aColumnIndex < NO_CELL)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int oldValue = columnIndex;
        columnIndex = aColumnIndex;

        // Notify listeners
        changes.firePropertyChange(EngineProp.COLUMN_INDEX,
                                   new Integer(oldValue),
                                   new Integer(columnIndex));
        organismImageDirty = true;
    }

    /**
     * Sets the column and row indices of this organism image.<p>
     *
     * When this method is successful, two property change events
     * are fired - one for the property named EngineProp.COLUMN and
     * another named EngineProp.ROW.<p>
     *
     * @param		aColumnIndex int - new column index, may not be < NO_CELL
     * @param		aRowIndex int - new row index, may not be < NO_CELL
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setColumnAndRowIndices(int aColumnIndex, int aRowIndex)
    {
        // Return immediately if this is not a change
        if (aColumnIndex == columnIndex && aRowIndex == rowIndex)
        {
            return;
        }

        // Validate input arguments
        if (aColumnIndex < NO_CELL ||
            aRowIndex < NO_CELL)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int oldColumnIndex, oldRowIndex;
        
        oldColumnIndex = columnIndex;
        oldRowIndex = rowIndex;
        columnIndex = aColumnIndex;
        rowIndex = aRowIndex;

        // Notify listeners
        changes.firePropertyChange(EngineProp.COLUMN_INDEX,
                                   new Integer(oldColumnIndex),
                                   new Integer(columnIndex));
        changes.firePropertyChange(EngineProp.ROW_INDEX,
                                   new Integer(oldRowIndex),
                                   new Integer(rowIndex));
        organismImageDirty = true;
    }

    /**
     * Get the image type for this organism image.  We don't store that
     * information here in this object, so we have to go to the parent
     * species image to get it.<p>
     *
     * @return		int - image type of this species image
    **/
    public int getImageType()
    {
        return speciesImage.getImageType();
    }

    /**
     * Handle property change events.  In particular, react to an event that
     * says the SpeciesImage has changed or been deleted.<p>
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.DELETED))
        {
            Object object = event.getSource();

            if (object instanceof SpeciesImage)
            {
                // The parent SpeciesImage for this object has been deleted,
                // so we must delete this OrganismImage.
                this.delete();
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
        stream.println("<" + Elements.ORGANISM_IMAGE_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // SpeciesImage ID
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

        // Column index
        stream.println("<" + Elements.COLUMN_INDEX_ELEMENT_NAME + ">" + columnIndex +
                          "</" + Elements.COLUMN_INDEX_ELEMENT_NAME + ">");

        // Row index
        stream.println("<" + Elements.ROW_INDEX_ELEMENT_NAME + ">" + rowIndex +
                          "</" + Elements.ROW_INDEX_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.ORGANISM_IMAGE_ELEMENT_NAME + ">");
    }

	public boolean	organismImageDirty;
	public void clearDirtyBit(){
		organismImageDirty = false;
	}

}
