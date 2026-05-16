//
// Class : SpeciesChromosome
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.3 $
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
 * This class represents a species chromosome - a chromosome which has a
 * length, some genes and their alleles, etc.  This chromosome does NOT
 * have any algorithmic DNA in it and thus is not a chromosome that would
 * be found in an organism.  See OrganismChromosome for the chromosome class
 * for organisms.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.BASE_VALUES - base values of this chromosome have changed
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.GENE_ADDED - a gene has been added
 * <li> EngineProp.GENE_REMOVED - a gene has been removed
 * <li> EngineProp.IMAGE_NUMBER - the image number to be used for drawing chromosome changed
 * <li> EngineProp.LENGTH_IN_BASES - length of chromosome has changed
 * <li> EngineProp.LOCKED_STATE - the object's locked state has changed
 * <li> EngineProp.NUMBER_TYPE - number or type of this chromosome has changed
 * <li> EngineProp.VISIBLE - object visibility has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#BASE_VALUES
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#GENE_ADDED
 * @see org.concord.biologica.engine.EngineProp#GENE_REMOVED
 * @see org.concord.biologica.engine.EngineProp#IMAGE_NUMBER
 * @see org.concord.biologica.engine.EngineProp#LENGTH_IN_BASES
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NUMBER_TYPE
 * @see org.concord.biologica.engine.EngineProp#VISIBLE
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.3 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class SpeciesChromosome
extends EngineObject
implements IChromosome, Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Chromosome image numbers.<p>
    **/
    static final public int	LONGEST_CHROMOSOME_IMAGE 	= 0;
    static final public int	LONGER_CHROMOSOME_IMAGE		= 1;
    static final public int	LONG_CHROMOSOME_IMAGE	 	= 2;
    static final public int	MEDIUM_CHROMOSOME_IMAGE		= 3;
    static final public int	SHORT_CHROMOSOME_IMAGE		= 4;
    static final public int	SHORTER_CHROMOSOME_IMAGE	= 5;
    static final public int	SHORTEST_CHROMOSOME_IMAGE	= 6;
    static final public int	TINY_CHROMOSOME_IMAGE		= 7;

    /**
     * Number of segments in P and Q strands of each type of chromosome image.
    **/
    static final public int NUMBER_P_STRAND_SEGMENTS_LONGEST_CHROMOSOME		= 20;
    static final public int NUMBER_Q_STRAND_SEGMENTS_LONGEST_CHROMOSOME		= 42;

    static final public int NUMBER_P_STRAND_SEGMENTS_LONGER_CHROMOSOME		= 18;
    static final public int NUMBER_Q_STRAND_SEGMENTS_LONGER_CHROMOSOME		= 39;

    static final public int NUMBER_P_STRAND_SEGMENTS_LONG_CHROMOSOME		= 16;
    static final public int NUMBER_Q_STRAND_SEGMENTS_LONG_CHROMOSOME		= 36;

    static final public int NUMBER_P_STRAND_SEGMENTS_MEDIUM_CHROMOSOME		= 14;
    static final public int NUMBER_Q_STRAND_SEGMENTS_MEDIUM_CHROMOSOME		= 33;

    static final public int NUMBER_P_STRAND_SEGMENTS_SHORT_CHROMOSOME		= 12;
    static final public int NUMBER_Q_STRAND_SEGMENTS_SHORT_CHROMOSOME		= 30;

    static final public int NUMBER_P_STRAND_SEGMENTS_SHORTER_CHROMOSOME		= 10;
    static final public int NUMBER_Q_STRAND_SEGMENTS_SHORTER_CHROMOSOME		= 27;

    static final public int NUMBER_P_STRAND_SEGMENTS_SHORTEST_CHROMOSOME	= 8;
    static final public int NUMBER_Q_STRAND_SEGMENTS_SHORTEST_CHROMOSOME	= 24;

    static final public int NUMBER_P_STRAND_SEGMENTS_TINY_CHROMOSOME		= 6;
    static final public int NUMBER_Q_STRAND_SEGMENTS_TINY_CHROMOSOME		= 20;

    /**
     * The species containing this species chromosome.  Read-only.  May not be null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to an organism, the organism will generate a
     * property change event for a new species chromosome.<p>
    **/
    private Species				species;

    /**
     * The type or number of this chromosome.<p>
     *
     * If this is a sex chromosome, then the value of this instance variable
     * is either IChromosome.X_CHROMOSOME or IChromosome.Y_CHROMOSOME.<p>
     *
     * If this is a non-sex chromosome, then the value of this instance
     * variable is greater than 0, indicating an autosome.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NUMBER_TYPE.<p>
    **/
    private int					numberType;

    /**
     * The length of the chromosome in bases.  Must be greater than zero.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.LENGTH_IN_BASES.<p>
    **/
    private int					lengthInBases;

    /**
     * The vector of genes on this chromosome.  May not be null.<p>
     *
     * All the objects on this vector must be instances of Gene.<p>
     *
     * When a Gene object is created and added to this vector,
     * a EngineProp.GENE_ADDED property change event is fired.<p>
     *
     * When a Gene object is deleted and removed from this vector,
     * a EngineProp.GENE_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Gene
     * @see		org.concord.biologica.engine.EngineProp#GENE_ADDED
     * @see		org.concord.biologica.engine.EngineProp#GENE_REMOVED
    **/
    private Vector				genes;

    /**
     * The image number to be used when drawing the chromosome.  Must be
     * one of the above chromosome image number values.<p>
     *
     * @see		org.concord.biologica.engine.SpeciesChromosome#LONGEST_CHROMOSOME_IMAGE
     * @see		org.concord.biologica.engine.SpeciesChromosome#LONGER_CHROMOSOME_IMAGE
     * @see		org.concord.biologica.engine.SpeciesChromosome#LONG_CHROMOSOME_IMAGE
     * @see		org.concord.biologica.engine.SpeciesChromosome#MEDIUM_CHROMOSOME_IMAGE
     * @see		org.concord.biologica.engine.SpeciesChromosome#SHORT_CHROMOSOME_IMAGE
     * @see		org.concord.biologica.engine.SpeciesChromosome#SHORTER_CHROMOSOME_IMAGE
     * @see		org.concord.biologica.engine.SpeciesChromosome#SHORTEST_CHROMOSOME_IMAGE
     * @see		org.concord.biologica.engine.SpeciesChromosome#TINY_CHROMOSOME_IMAGE
    **/
    private int					imageNumber;

    /**
     * Whether this chromosome is visible or not.<p>
     *
     * When this changes, a EngineProp.VISIBLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#VISIBLE
    **/
    private boolean				visible;

    /**
     * Convert a chromosome number type to a string.
     *
     * @param 	aNumberType int - chromosome number type in int form
     * @return	String - chromosome number type in String form
    **/
    public static String convertChromosomeNumberTypeToString(int aNumberType)
    {
        if (aNumberType == IChromosome.X_CHROMOSOME)
        {
            return new String(EngineStrings.X);
        }
        else if (aNumberType == IChromosome.Y_CHROMOSOME)
        {
            return new String(EngineStrings.Y);
        }
        else
        {
            return String.valueOf(aNumberType);
        }
    }

    /**
     * Convert a chromosome number type string to an int
     *
     * @param 		aNumberTypeString String - chromosome number type in string form
     * @return		int - chromosome number type in int form
     * @exception 	NumberFormatException - if string not an acceptable number
    **/
    public static int convertStringToChromosomeNumberType(String aNumberTypeString)
    throws NumberFormatException
    {
        int stringLength = aNumberTypeString.length();
        int numberType;

        if (stringLength == 0)
        {
            // Zero length string, so throw exception
            throw new NumberFormatException();
        }
        else if (aNumberTypeString.startsWith(EngineStrings.x) ||
                 aNumberTypeString.startsWith(EngineStrings.X))
        {
            numberType = IChromosome.X_CHROMOSOME;
        }
        else if (aNumberTypeString.startsWith(EngineStrings.y) ||
                 aNumberTypeString.startsWith(EngineStrings.Y))
        {
            numberType = IChromosome.Y_CHROMOSOME;
        }
        else
        {
            // Try to convert string to an Integer, possibly throwing exception
            numberType = Integer.parseInt(aNumberTypeString);

            if (numberType < 1)
            {
                throw new NumberFormatException();
            }
        }

        return numberType;
    }

    /**
     * Create a new species chromosome.  After creating a species chromosome, genes
     * may be created on this chromosome using the Gene constructor.<p>
     *
     * @param		aSpecies Species - the species containing this chromosome, may not be null
     * @param		aNumberType int - IChromosome.X_CHROMOSOME, IChromosome.Y_CHROMOSOME,
     *								  IChromosome.NEXT_HIGHEST_AUTOSOME_NUMBER or a number greater than or equal to 1.
     * @param		aLengthInBases int - length of chromosome in bases, must be greater than zero
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public SpeciesChromosome(Species aSpecies, int aNumberType, int aLengthInBases)
    {
        // Check input arguments
        if (aSpecies == null ||
            (aNumberType != IChromosome.X_CHROMOSOME &&
             aNumberType != IChromosome.Y_CHROMOSOME &&
             aNumberType != IChromosome.NEXT_HIGHEST_AUTOSOME_NUMBER &&
             aNumberType < 1) ||
            aLengthInBases < 1)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, initialize instance variables
        species = aSpecies;
        lengthInBases = aLengthInBases;
        genes = new Vector();
        imageNumber = LONGEST_CHROMOSOME_IMAGE;
        visible = true;
        lockedState = EngineObject.UNLOCKED;

        // If aNumberType is IChromosome.NEXT_HIGHEST_AUTOSOME_NUMBER,
        // then bump it up to one greater than the current maximum
        if (aNumberType == IChromosome.NEXT_HIGHEST_AUTOSOME_NUMBER)
        {
            numberType = 1;
            SpeciesChromosome sc;
            Enumeration eSC = species.getNonSexChromosomes();
            while (eSC.hasMoreElements())
            {
                sc = (SpeciesChromosome) eSC.nextElement();
                if (sc.getNumberType() >= numberType)
                {
                    numberType = sc.getNumberType() + 1;
                }
            }
        }
        else
        {
            numberType = aNumberType;
        }

        // Creation successful, so add this species chromosome to species
        species.addChromosome(this);
    }

    /**
     * Create a new species chromosome and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aSpecies Species - the enclosing species for this new species chromosome
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public SpeciesChromosome(Species aSpecies,
                             String anElementName,
                             int anElementID,
                             /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
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
        visible = true;
        lockedState = EngineObject.UNLOCKED;

        // Create vectors
        genes = new Vector();

        // Creation successful, so add this species chromosome to species
        species = aSpecies;
        species.addChromosome(this);

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
            case Elements.NUMBER_TYPE_ELEMENT_ID:
            case Elements.LENGTH_IN_BASES_ELEMENT_ID:
            case Elements.IMAGE_NUMBER_ELEMENT_ID:
            case Elements.VISIBLE_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.GENE_ELEMENT_ID:
                new Gene(this,anElementName,anElementID,
                         xmlElementContext.getXMLParser(),
                         xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("SpeciesChromosome " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.NUMBER_TYPE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setNumberTypeAsString(valueString);
                break;

            case Elements.LENGTH_IN_BASES_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                lengthInBases = Integer.valueOf(valueString).intValue();
                break;

            case Elements.IMAGE_NUMBER_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                imageNumber = Integer.valueOf(valueString).intValue();
                break;

            case Elements.VISIBLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                visible = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.GENE_ELEMENT_ID:
                // Done with a gene, so reclaim document handler
                break;

            case Elements.SPECIES_CHROMOSOME_ELEMENT_ID:
                // Done with this species chromosome, so pop up to enclosing species
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
        Gene aGene;
        Vector genesClone = (Vector) genes.clone();
        Enumeration eGenes = genesClone.elements();
        //Enumeration eGenes = genes.elements();
        while (eGenes.hasMoreElements())
        {
            aGene = (Gene) eGenes.nextElement();
            aGene.delete(notifyChange);
        }
        genes.removeAllElements();
        genes = null;
        genesClone = null;

        species.removeChromosome(this);
        species = null;
        id = EngineObject.NULL_ID;
    }

    /**
     * Return a string representation of this object, based
     * on the chromosome's number type.<p>
     *
     * @return	String - string representation of object
    **/
    public String toString()
    {
        if (numberType == IChromosome.X_CHROMOSOME)
        {
            return EngineStrings.CHROMOSOME_COLON_X;
        }
        else if (numberType == IChromosome.Y_CHROMOSOME)
        {
            return EngineStrings.CHROMOSOME_COLON_Y;
        }

        return EngineStrings.CHROMOSOME_COLON + String.valueOf(numberType);
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
        Gene aGene;
        Enumeration eGenes = genes.elements();
        while (eGenes.hasMoreElements())
        {
            aGene = (Gene) eGenes.nextElement();
            aGene.setAutomaticLocked(automaticLocked);
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
        Gene aGene;
        Enumeration eGenes = genes.elements();
        while (eGenes.hasMoreElements())
        {
            aGene = (Gene) eGenes.nextElement();
            aGene.setManualLocked(manualLocked);
        }
    }

    /**
     * Set the lock state of the object, recursively setting the lock
     * state of children objects.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * @param	aLockedState int - new locked state of this object
     * @exception	IllegalArgumentException - new locked state invalid
    **/
    public void setLockedState(int aLockedState)
    {
        // Use EngineObject implementation for changing the locked state of this object
        super.setLockedState(aLockedState);

        // Set locked state of children
        Gene aGene;
        Enumeration eGenes = genes.elements();
        while (eGenes.hasMoreElements())
        {
            aGene = (Gene) eGenes.nextElement();
            aGene.setLockedState(lockedState);
        }
    }

    /**
     * Returns the chromosome's species.<p>
     *
     * @return		Species - chromosome's species, may not be null
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
     * Returns the length of the chromosome in bases.<p>
     *
     * @return		int - length of this chromosome in bases
    **/
    public int getLengthInBases()
    {
        return lengthInBases;
    }

    /**
     * Sets the length of the chromosome in bases.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.LENGTH_IN_BASES.<p>
     *
     * @param		aLengthInBases int - new length in bases
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setLengthInBases(int aLengthInBases)
    {
        // Validate input arguments
        if (aLengthInBases <= 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
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

        // OK - make change
        if (lengthInBases != aLengthInBases)
        {
            int oldLengthInBases = lengthInBases;
            lengthInBases = aLengthInBases;

            // Notify listeners
            changes.firePropertyChange(EngineProp.LENGTH_IN_BASES,
                                       new Integer(oldLengthInBases),
                                       new Integer(lengthInBases));
        }
    }

    /**
     * Returns the length of the chromosome in codons, calculated
     * by dividing the number of bases by 3, ignoring remainder.<p>
     *
     * @return		int - length of this chromosome in codons
    **/
    public int getLengthInCodons()
    {
        return lengthInBases / 3;
    }

    /**
     * Returns the number / type of this chromosome.<p>
     *
     * @return		int - IChromosome.X_CHROMOSOME, IChromosome.Y_CHROMOSOME or an autosome number
    **/
    public int getNumberType()
    {
        return numberType;
    }

    /**
     * Returns the number / type of this chromosome.<p>
     *
     * @return		int - IChromosome.X_CHROMOSOME, IChromosome.Y_CHROMOSOME or an autosome number
    **/
    public String getNumberTypeAsString()
    {
        int aNumberType = getNumberType();
        if (aNumberType == IChromosome.X_CHROMOSOME)
        {
            return IChromosome.X_CHROMOSOME_STRING;
        }
        else if (aNumberType == IChromosome.Y_CHROMOSOME)
        {
            return IChromosome.Y_CHROMOSOME_STRING;
        }
        else
        {
            return String.valueOf(aNumberType);
        }
    }

    /**
     * Sets the number or type of this chromosome.
     * Possible values include IChromosome.X_CHROMOSOME, IChromosome.Y_CHROMOSOME
     * and an integer greater than 0.<p>
     *
     * @param		aNumberType int - new number or type of chromosome
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setNumberType(int aNumberType)
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

        // OK - make change
        if (numberType != aNumberType)
        {
            int oldNumberType = numberType;
            numberType = aNumberType;

            // Notify listeners
            changes.firePropertyChange(EngineProp.NUMBER_TYPE,
                                       new Integer(oldNumberType),
                                       new Integer(numberType));
        }
    }

    /**
     * Sets the number or type of this chromosome, using a string as input.
     * Possible values include IChromosome.X_CHROMOSOME_STRING, IChromosome.Y_CHROMOSOME_STRING
     * and an integer greater than 0.<p>
     *
     * @param		aNumberTypeString String - new number or type of chromosome as a string
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setNumberTypeAsString(String aNumberTypeString)
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

        // Convert string to int
        int aNumberType;
        if (aNumberTypeString == null)
        {
            throw new IllegalArgumentException("aNumberTypeString null"); 
        }
        else if (aNumberTypeString.equals(IChromosome.X_CHROMOSOME_STRING))
        {
            aNumberType = IChromosome.X_CHROMOSOME;
        }
        else if (aNumberTypeString.equals(IChromosome.Y_CHROMOSOME_STRING))
        {
            aNumberType = IChromosome.Y_CHROMOSOME;
        }
        else
        {
            aNumberType = Integer.valueOf(aNumberTypeString).intValue();
        }

        // OK - make change
        if (numberType != aNumberType)
        {
            int oldNumberType = numberType;
            numberType = aNumberType;

            // Notify listeners
            changes.firePropertyChange(EngineProp.NUMBER_TYPE,
                                       new Integer(oldNumberType),
                                       new Integer(numberType));
        }
    }

    /**
     * Returns the image number of this chromosome.<p>
     *
     * @return		int - image number of chromosome
     * @see			org.concord.biologica.engine.SpeciesChromosome#LONGEST_CHROMOSOME_IMAGE
     * @see			org.concord.biologica.engine.SpeciesChromosome#LONGER_CHROMOSOME_IMAGE
     * @see			org.concord.biologica.engine.SpeciesChromosome#LONG_CHROMOSOME_IMAGE
     * @see			org.concord.biologica.engine.SpeciesChromosome#MEDIUM_CHROMOSOME_IMAGE
     * @see			org.concord.biologica.engine.SpeciesChromosome#SHORT_CHROMOSOME_IMAGE
     * @see			org.concord.biologica.engine.SpeciesChromosome#SHORTER_CHROMOSOME_IMAGE
     * @see			org.concord.biologica.engine.SpeciesChromosome#SHORTEST_CHROMOSOME_IMAGE
     * @see			org.concord.biologica.engine.SpeciesChromosome#TINY_CHROMOSOME_IMAGE
    **/
    public int getImageNumber()
    {
        return imageNumber;
    }

    /**
     * Sets the image number of this chromosome.<p>
     *
     * @param		anImageNumber int - new image number of chromosome
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setImageNumber(int anImageNumber)
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

        // OK - make change
        if (imageNumber != anImageNumber)
        {
            int oldImageNumber = imageNumber;
            imageNumber = anImageNumber;

            // Notify listeners
            changes.firePropertyChange(EngineProp.IMAGE_NUMBER,
                                       new Integer(oldImageNumber),
                                       new Integer(imageNumber));
        }
    }

    /**
     * Returns whether the chromosome is a sex chromosome.<p>
     *
     * @return		boolean - true if sex chromosome, else false
    **/
    public boolean isSexChromosome()
    {
        if (numberType == IChromosome.X_CHROMOSOME ||
            numberType == IChromosome.Y_CHROMOSOME)
        {
            return true;
        }
        
        return false;
    }

    /**
     * Get the chromosome type (autosome or sex chromosome).<p>
     *
     * @return		int - IChromosome.AUTOSOME or IChromosome.SEX_CHROMOSOME
    **/
    public int getChromosomeType()
    {
        if (numberType == IChromosome.X_CHROMOSOME ||
            numberType == IChromosome.Y_CHROMOSOME)
        {
            return IChromosome.SEX_CHROMOSOME;
        }
        
        return IChromosome.AUTOSOME;
    }

    /**
     * Returns an enumeration over the vector of genes in this chromosome.<p>
     *
     * The vector of genes is cloned and the enumeration is created for the
     * clone, so it is safe to modify the vector of genes (by creating new
     * ones, moving them around, etc.) while using this returned enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the genes in this chromosome
    **/
    public Enumeration getGenes()
    {
        // Clone genes vector to make modifications safe during stepping
        Vector genesClone = (Vector) genes.clone();
        return genesClone.elements();
    }

    /**
     * Return the number of genes in this chromosome.<p>
     *
     * @return		int - the number of genes in this chromosome
    **/
    public int getNumberOfGenes()
    {
        return genes.size();
    }

    /**
     * Adds a gene to the chromosome.<p>
     *
     * Package protected because this is only called from the
     * Gene constructor.  Creating a Gene automatically adds
     * it to the species via this method.<p>
     *
     * @param		aGene Gene - a new gene, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addGene(Gene aGene)
    {
        // Check input arguments
        if (aGene == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        genes.addElement(aGene);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.GENE_ADDED,null,aGene);
    }

    /**
     * Removes a gene from the chromosome.<p>
     *
     * Package protected because this is only called from the Gene
     * delete method.  Deleting a Gene automatically removes it from
     * the chromosome.<p>
     *
     * @param		aGene Gene - a gene, may not be null
     * @return		boolean indicating whether or not the gene was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeGene(Gene aGene)
    {
        // Validate input arguments
        if (aGene == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change
        boolean result = genes.removeElement(aGene);

        // Notify listeners if gene removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.GENE_REMOVED,null,aGene);
        }

        return result;
    }

    /**
     * Get whether this chromosome is visible.<p>
     *
     * @return	boolean - whether this chromosome is visible (true) or not visible (false)
    **/
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Set whether this chromosome is visible.<p>
     *
     * @param	TorF boolean - visible (true) or not visible (false)
    **/
    public void setVisible(boolean TorF)
    {
        if (TorF != visible)
        {
            boolean oldVisible = visible;
            visible = TorF;
    
            changes.firePropertyChange(EngineProp.VISIBLE,
                                       new Boolean(oldVisible),
                                       new Boolean(visible));
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
        stream.println("<" + Elements.SPECIES_CHROMOSOME_ELEMENT_NAME + ">");

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

        // Number type
        stream.println("<" + Elements.NUMBER_TYPE_ELEMENT_NAME + ">" +
                          getNumberTypeAsString() +
                          "</" + Elements.NUMBER_TYPE_ELEMENT_NAME + ">");

        // Length in bases
        stream.println("<" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">" + lengthInBases +
                          "</" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">");

        // Image number
        stream.println("<" + Elements.IMAGE_NUMBER_ELEMENT_NAME + ">" + imageNumber +
                          "</" + Elements.IMAGE_NUMBER_ELEMENT_NAME + ">");

        // Visible
        stream.println("<" + Elements.VISIBLE_ELEMENT_NAME + ">" + visible +
                          "</" + Elements.VISIBLE_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Recursively write children of species chromosome
        if (genes != null)
        {
            Gene aGene;
            Enumeration eGenes = genes.elements();
            while (eGenes.hasMoreElements())
            {
                aGene = (Gene) eGenes.nextElement();
                aGene.writeToStream(stream);
            }
        }

        // End object
        stream.println("</" + Elements.SPECIES_CHROMOSOME_ELEMENT_NAME + ">");
    }
}
