//
// Class : OrganismAllelePair
//
// Copyright © 1999, The Concord Consortium
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
import java.util.Random;
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
 * This class represents a pair of organism alleles or, in the case of sex chromosomes,
 * it can represent a single allele on a single chromosome. This type of object should
 * only exist for organisms of a diploid species.<p>
 *
 * In general this object is created when the organism is created and never changes
 * from that time on.  It's essentially a convenience object, especially for the
 * tree view as this object represents a node in the tree view.<p>
 *
 * An instance of this class is contained by one organism chromosome pair.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class OrganismAllelePair
extends EngineObject
implements Serializable, PropertyChangeListener
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int			schemaVersion = 1;

    /**
     * The organism chromosome pair containing this organism allele pair.
     * Read-only.  May not be null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to an organism, the organism will generate a
     * property change event for a new organism chromosome.<p>
    **/
    private OrganismChromosomePair		organismChromosomePair;
    
    /**
     * First organism allele.  Read-only.  May not be null.<p>
    **/
    private OrganismAllele				firstOrganismAllele;

    /**
     * Second organism allele.  Read-only.  May be null if in a sex chromosome pair.<p>
    **/
    private OrganismAllele				secondOrganismAllele;

    /**
     * Create a new organism allele pair from the two given organism alleles.<p>
     *
     * @param		anOrganismChromosomePair OrganismChromosomePair - containing organism chromosome pair, may not be null
     * @param		aFirstOrganismAllele OrganismAllele - a first organism allele, may not be null
     * @param		aSecondOrganismAllele OrganismAllele - a second organism allele, may be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismAllelePair(OrganismChromosomePair anOrganismChromosomePair,
                              OrganismAllele aFirstOrganismAllele,
                              OrganismAllele aSecondOrganismAllele)
    {
        // Check input arguments
        if (anOrganismChromosomePair == null || aFirstOrganismAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, initialize instance variables
        organismChromosomePair = anOrganismChromosomePair;
        firstOrganismAllele = aFirstOrganismAllele;
        secondOrganismAllele = aSecondOrganismAllele;
        lockedState = EngineObject.UNLOCKED;

        // Tell organism
        organismChromosomePair = anOrganismChromosomePair;
        organismChromosomePair.addOrganismAllelePair(this);
    }

    /**
     * Create a new organism allele pair and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      anOrganismChromosomePair OrganismChromosomePair - the enclosing organism chromosome pair for this new organism allele pair
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public OrganismAllelePair(OrganismChromosomePair anOrganismChromosomePair,
                              String anElementName,
                              int anElementID,
                              /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                              ImportContext importContext)
    {
        if (anOrganismChromosomePair == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        lockedState = EngineObject.UNLOCKED;
        organismChromosomePair = null;
        firstOrganismAllele = null;
        secondOrganismAllele = null;

        // Creation successful, so add this organism allele pair to its organism chromosome pair
        organismChromosomePair = anOrganismChromosomePair;
        organismChromosomePair.addOrganismAllelePair(this);

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
            case Elements.ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_ID:
            case Elements.FIRST_ORGANISM_ALLELE_ID_ELEMENT_ID:
            case Elements.SECOND_ORGANISM_ALLELE_ID_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            default:
                throw new IllegalArgumentException("OrganismAllelePair " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        int organismAlleleID;

        int anElementID = Elements.mapElementNameToID(anElementName);

        switch (anElementID)
        {
            case Elements.SCHEMA_VERSION_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // schemaVersion = Integer.valueOf(valueString).intValue();
                break;

            case Elements.ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // Ignore organism allele id, as we've already created it.
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

            case Elements.ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_ID:
                // Ignore, as we already have the organism chromosome pair
                break;

            case Elements.FIRST_ORGANISM_ALLELE_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    firstOrganismAllele = null;
                }
                else
                {
                    organismAlleleID = Integer.valueOf(valueString).intValue();
                    if (organismAlleleID != EngineObject.NULL_ID)
                    {
                        firstOrganismAllele = (OrganismAllele) xmlElementContext.getImportContext().getObject(organismAlleleID);
                    }
                    else
                    {
                        firstOrganismAllele = null;
                    }
                }
                break;

            case Elements.SECOND_ORGANISM_ALLELE_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    secondOrganismAllele = null;
                }
                else
                {
                    organismAlleleID = Integer.valueOf(valueString).intValue();
                    if (organismAlleleID != EngineObject.NULL_ID)
                    {
                        secondOrganismAllele = (OrganismAllele) xmlElementContext.getImportContext().getObject(organismAlleleID);
                    }
                    else
                    {
                        secondOrganismAllele = null;
                    }
                }
                break;

            case Elements.ORGANISM_ALLELE_PAIR_ELEMENT_ID:
                // Done with this organism allele pair, so pop up to enclosing organism chromosome pair
                organismChromosomePair.endElement(anElementName);
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

        // Notify organism chromosome pair
        if (organismChromosomePair != null)
        {
            organismChromosomePair.removeOrganismAllelePair(this);
            organismChromosomePair = null;
        }

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
        if (firstOrganismAllele != null)
        {
            if (secondOrganismAllele != null)
            {
                return EngineStrings.ALLELES_COLON + firstOrganismAllele.getTextSymbol() + ", " + secondOrganismAllele.getTextSymbol();
            }
            else
            {
                return EngineStrings.ALLELES_COLON + firstOrganismAllele.getTextSymbol();
            }
        }

        return "";
    }

    /**
     * Returns the organism allele pair's chromosome organism pair.<p>
     *
     * @return		OrganismChromosomePair - containing organism chromosome pair, never null
    **/
    public OrganismChromosomePair getOrganismChromosomePair()
    {
        return organismChromosomePair;
    }

    /**
     * Returns the allele pair's organism.<p>
     *
     * @return      Organism - the allele pair's organism, may not be null
    **/
    public Organism getOrganism()
    {
        return organismChromosomePair.getOrganism();
    }

    /**
     * Returns the organism chromosome pair's species.<p>
     *
     * @return		Species - organism chromosome pair's species, may not be null
    **/
    public Species getSpecies()
    {
        return organismChromosomePair.getSpecies();
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return organismChromosomePair.getWorld();
    }

    /**
     * Get the first organism allele of this pair.<p>
     *
     * @return	OrganismAllele - the first organism allele in this pair, never null.
    **/
    public OrganismAllele getFirstOrganismAllele()
    {
        return firstOrganismAllele;
    }

    /**
     * Get the second organism allele of this pair.<p>
     *
     * @return	OrganismAllele - the second organism allele in this pair, never null.
    **/
    public OrganismAllele getSecondOrganismAllele()
    {
        return secondOrganismAllele;
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();
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
        stream.println("<" + Elements.ORGANISM_ALLELE_PAIR_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Organism Chromosome Pair ID
        if (organismChromosomePair == null)
        {
            stream.println("<" + Elements.ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_NAME + ">" + organismChromosomePair.getID() +
                              "</" + Elements.ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_NAME + ">");
        }

        // First organism allele ID
        if (firstOrganismAllele == null)
        {
            stream.println("<" + Elements.FIRST_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.FIRST_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.FIRST_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">" +
                              firstOrganismAllele.getID() +
                              "</" + Elements.FIRST_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">");
        }

        // Second organism allele ID
        if (secondOrganismAllele == null)
        {
            stream.println("<" + Elements.SECOND_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SECOND_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SECOND_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">" +
                              secondOrganismAllele.getID() +
                              "</" + Elements.SECOND_ORGANISM_ALLELE_ID_ELEMENT_NAME + ">");
        }

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.ORGANISM_ALLELE_PAIR_ELEMENT_NAME + ">");
    }
}

