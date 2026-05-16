//
// Class : OrganismChromosomePair
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
 * This class represents a pair of organism chromosomes, either a pair of autosomes or
 * a pair of sex chromosomes.  This type of object should only exist for organisms of
 * a diploid species.<p>
 *
 * In general this object is created when the organism is created and never changes
 * from that time on.  It's essentially a convenience object.<p>
 *
 * An instance of this class is contained by one organism.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.CHROMOSOME_PAIR_TYPE - autosome or sex chromosome pair?
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

public final class OrganismChromosomePair
extends EngineObject
implements Serializable, PropertyChangeListener
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * The organism containing this chromosome.  Read-only.  May not be null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to an organism, the organism will generate a
     * property change event for a new organism chromosome.<p>
    **/
    private Organism				organism;
    
    /**
     * First chromosome.  Read-only.  May not be null.<p>
    **/
    private OrganismChromosome		firstOrganismChromosome;

    /**
     * Second chromosome.  Read-only.  May not be null.<p>
    **/
    private OrganismChromosome		secondOrganismChromosome;

    /**
     * Organism allele pairs
    **/
    private Vector					organismAllelePairs;

    /**
     * Create a new organism chromosome pair from the two given organism chromosomes.<p>
     *
     * @param		anOrganism Organism - containing organism, may not be null
     * @param		aFirstOrganismChromosome OrganismChromosome - a first organism chromosome
     * @param		aSecondOrganismChromosome OrganismChromosome - a second organism chromosome
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismChromosomePair(Organism anOrganism,
                                  OrganismChromosome aFirstOrganismChromosome,
                                  OrganismChromosome aSecondOrganismChromosome)
    {
        // Check input arguments
        if ((anOrganism == null || aFirstOrganismChromosome == null || aSecondOrganismChromosome == null) ||
            (aFirstOrganismChromosome.getOrganism() != aSecondOrganismChromosome.getOrganism()) ||
            (aFirstOrganismChromosome.getChromosomeType() != aSecondOrganismChromosome.getChromosomeType()) ||
            (aFirstOrganismChromosome.getChromosomeType() == IChromosome.AUTOSOME &&
             aFirstOrganismChromosome.getNumberType() != aSecondOrganismChromosome.getNumberType()))
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, initialize instance variables
        organism = anOrganism;
        firstOrganismChromosome = aFirstOrganismChromosome;
        secondOrganismChromosome = aSecondOrganismChromosome;
        lockedState = EngineObject.UNLOCKED;

        // Create organism allele pairs
        organismAllelePairs = new Vector();

        if (firstOrganismChromosome.getNumberType() == secondOrganismChromosome.getNumberType())
        {
            // Create organism allele pairs using both chromosomes
            OrganismAllele alleleOne, alleleTwo;
            Enumeration eAllelesOne = firstOrganismChromosome.getOrganismAlleles();
            Enumeration eAllelesTwo = secondOrganismChromosome.getOrganismAlleles();
            while (eAllelesOne.hasMoreElements() &&
                   eAllelesTwo.hasMoreElements())
            {
                alleleOne = (OrganismAllele) eAllelesOne.nextElement();
                alleleTwo = (OrganismAllele) eAllelesTwo.nextElement();
                new OrganismAllelePair(this,alleleOne,alleleTwo);
            }
        }
        else
        {
            // Create organism allele pairs separately for each chromosome
            OrganismAllele allele;
            Enumeration eAlleles;

            // First chromosome
            eAlleles = firstOrganismChromosome.getOrganismAlleles();
            while (eAlleles.hasMoreElements())
            {
                allele = (OrganismAllele) eAlleles.nextElement();
                new OrganismAllelePair(this,allele,null);
            }

            // First chromosome
            eAlleles = secondOrganismChromosome.getOrganismAlleles();
            while (eAlleles.hasMoreElements())
            {
                allele = (OrganismAllele) eAlleles.nextElement();
                new OrganismAllelePair(this,allele,null);
            }
        }

        // Tell organism
        organism = anOrganism;
        organism.addOrganismChromosomePair(this);
    }

    /**
     * Create a new organism chromosome pair and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      anOrganism Organism - the enclosing organism for this new organism chromosome pair
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public OrganismChromosomePair(Organism anOrganism,
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
        firstOrganismChromosome = null;
        secondOrganismChromosome = null;

        // Create vectors
        organismAllelePairs = new Vector();

        // Creation successful, so add this organism chromosome pair to organism
        organism = anOrganism;
        //organism.addOrganismChromosomePair(this); // Now added in Organism after parsing is done.

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
            case Elements.FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_ID:
            case Elements.SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.ORGANISM_ALLELE_PAIR_ELEMENT_ID:
                new OrganismAllelePair(this,anElementName,anElementID,
                                       xmlElementContext.getXMLParser(),
                                       xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("OrganismChromosomePair " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        int organismChromosomeID;

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

            case Elements.FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    firstOrganismChromosome = null;
                }
                else
                {
                    organismChromosomeID = Integer.valueOf(valueString).intValue();
                    if (organismChromosomeID != EngineObject.NULL_ID)
                    {
                        firstOrganismChromosome = (OrganismChromosome) xmlElementContext.getImportContext().getObject(organismChromosomeID);
                    }
                    else
                    {
                        firstOrganismChromosome = null;
                    }
                }
                break;

            case Elements.SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    secondOrganismChromosome = null;
                }
                else
                {
                    organismChromosomeID = Integer.valueOf(valueString).intValue();
                    if (organismChromosomeID != EngineObject.NULL_ID)
                    {
                        secondOrganismChromosome = (OrganismChromosome) xmlElementContext.getImportContext().getObject(organismChromosomeID);
                    }
                    else
                    {
                        secondOrganismChromosome = null;
                    }
                }
                break;

            case Elements.ORGANISM_ALLELE_PAIR_ELEMENT_ID:
                // Done with an organism allele pair, so reclaim document handler
                break;

            case Elements.ORGANISM_CHROMOSOME_PAIR_ELEMENT_ID:
                // Done with this organism chromosome pair, so pop up to enclosing organism
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

        Enumeration eAllelePairs = organismAllelePairs.elements();
        while (eAllelePairs.hasMoreElements())
        {
            OrganismAllelePair aAllelePair = (OrganismAllelePair) eAllelePairs.nextElement();
            aAllelePair.delete(notifyChange);
        }
        organismAllelePairs.removeAllElements();
        organismAllelePairs = null;

        // Notify organism
        if (organism != null)
        {
            organism.removeOrganismChromosomePair(this);
            organism = null;
        }

        firstOrganismChromosome = null;
        secondOrganismChromosome = null;

        id = EngineObject.NULL_ID;
    }
    
    /**
     * Get the chromosome pair number number type.  If this is a pair of autosomes, return
     * the number of the chromosomes (e.g. 1, 2, 3, ...).  If this is a pair of
     * sex chromosomes, return IChromosome.SEX_CHROMOSOME.
     *
     * @return		int - IChromosome.SEX_CHROMOSOME or a number > 0
    **/
    public int getChromosomePairNumberType()
    {
        if (firstOrganismChromosome.isSexChromosome())
        {
            return IChromosome.SEX_CHROMOSOME;
        }

        return firstOrganismChromosome.getNumberType();
    }

    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        int firstNumberType = firstOrganismChromosome.getNumberType();
        int secondNumberType = secondOrganismChromosome.getNumberType();

        if (firstNumberType == IChromosome.X_CHROMOSOME &&
            secondNumberType == IChromosome.X_CHROMOSOME)
        {
            return EngineStrings.CHROMOSOME_PAIR_XX;
        }
        else if (firstNumberType == IChromosome.X_CHROMOSOME &&
                 secondNumberType == IChromosome.Y_CHROMOSOME)
        {
            return EngineStrings.CHROMOSOME_PAIR_XY;
        }
        else if (firstNumberType == IChromosome.Y_CHROMOSOME &&
                 secondNumberType == IChromosome.X_CHROMOSOME)
        {
            return EngineStrings.CHROMOSOME_PAIR_YX;
        }

        return EngineStrings.CHROMOSOME_PAIR_COLON + String.valueOf(firstNumberType) + "a and " +
                String.valueOf(firstNumberType) + "b";
    }

    /**
     * Returns the organism chromosome pair's organism.<p>
     *
     * @return		Organism - containing organism, never null
    **/
    public Organism getOrganism()
    {
        return organism;
    }

    /**
     * Returns the organism chromosome pair's species.<p>
     *
     * @return		Species - organism chromosome pair's species, may not be null
    **/
    public Species getSpecies()
    {
        return organism.getSpecies();
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
     * Get the first organism chromosome of this pair.<p>
     *
     * @return	OrganismChromosome - the first organism chromosome in this pair, never null.
    **/
    public OrganismChromosome getFirstOrganismChromosome()
    {
        return firstOrganismChromosome;
    }

    /**
     * Get the second organism chromosome of this pair.<p>
     *
     * @return	OrganismChromosome - the second organism chromosome in this pair, never null.
    **/
    public OrganismChromosome getSecondOrganismChromosome()
    {
        return secondOrganismChromosome;
    }

    /**
     * Adds an organism allele pair to this organism chromosome pair.<p>
     *
     * Package protected because this is only called from the
     * OrganismAllelePair constructor.  Creating an OrganismAllelePair
     * automatically adds it to the organism via this method.<p>
     *
     * @param		anAllelePair OrganismAllelePair - a new organism allele pair, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addOrganismAllelePair(OrganismAllelePair anAllelePair)
    {
        if (anAllelePair == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        organismAllelePairs.addElement(anAllelePair);

        // Don't notify listeners
    }

    /**
     * Removes an organism allele pair from the organism chromosome pair.<p>
     *
     * Package protected because this is only called from the OrganismAllelePair
     * delete method.  Deleting an OrganismAllelePair automatically removes it from
     * the organism chromosome pair via this method.<p>
     *
     * @param		anAllelePair OrganismAllelePair - an organism allele pair, may not be null
     * @return		boolean indicating whether or not the organism allele pair was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeOrganismAllelePair(OrganismAllelePair anAllelePair)
    {
        if (anAllelePair == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = organismAllelePairs.removeElement(anAllelePair);

        // Don't bother notifying

        return result;
    }

    /**
     * Get the number of organism allele pairs
     *
     * @return		int - the number of organism allele pairs in this organism chromosome pair
    **/
    public int getNumberOfOrganismAllelePairs()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism chromosome pair deleted");
        }

        return organismAllelePairs.size();
    }

    /**
     * Returns an enumeration over the vector of all organism allele pairs
     * in this organism chromosome pair.<p>
     *
     * @return		Enumeration - an enumeration over all organism allele pairs in this organism chromosome pair
    **/
    public Enumeration getOrganismAllelePairs()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism chromosome pair deleted");
        }

        return organismAllelePairs.elements();
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
        stream.println("<" + Elements.ORGANISM_CHROMOSOME_PAIR_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Organism ID
        if (organism == null)
        {
            stream.println("<" + Elements.ORGANISM_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.ORGANISM_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.ORGANISM_ID_ELEMENT_NAME + ">" + organism.getID() +
                              "</" + Elements.ORGANISM_ID_ELEMENT_NAME + ">");
        }

        // First organism chromosome ID
        if (firstOrganismChromosome == null)
        {
            stream.println("<" + Elements.FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">" +
                              firstOrganismChromosome.getID() +
                              "</" + Elements.FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }

        // Second organism chromosome ID
        if (secondOrganismChromosome == null)
        {
            stream.println("<" + Elements.SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">" +
                              secondOrganismChromosome.getID() +
                              "</" + Elements.SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Organism allele pairs
        OrganismAllelePair anOrganismAllelePair;
        Enumeration eOrganismAllelePairs = organismAllelePairs.elements();
        while (eOrganismAllelePairs.hasMoreElements())
        {
            anOrganismAllelePair = (OrganismAllelePair) eOrganismAllelePairs.nextElement();
            anOrganismAllelePair.writeToStream(stream);
        }

        // End object
        stream.println("</" + Elements.ORGANISM_CHROMOSOME_PAIR_ELEMENT_NAME + ">");
    }
}

