//
// Class : OrganismChromosome
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.9 $
// $Date: 2004/04/27 13:37:42 $
// $Author: eburke $
//

package org.concord.biologica.engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.awt.Color;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;
import org.apache.xerces.parsers.SAXParser;

/**
 * This class represents an organism chromosome - a chromosome which has a
 * length and some organism alleles.<p>
 *
 * An instance of this class is contained by one organism.<p>
 *
 * An instance of this class may or may not be part of a diploid
 * chromosome set and therefore may or may not have a homologous chromosome.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.IMAGE_NUMBER - the image number to be used for drawing chromosome changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.NUMBER_TYPE - number or type of this chromosome has changed
 * <li> EngineProp.ORGANISM_ALLELE_ADDED - organism allele added to this chromosome
 * <li> EngineProp.ORGANISM_ALLELE_REMOVED - organism allele removed from this chromosome
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#IMAGE_NUMBER
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NUMBER_TYPE
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_ALLELE_ADDED
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_ALLELE_REMOVED
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.9 $ $Date: 2004/04/27 13:37:42 $
 * @author 		$Author: eburke $
**/

public final class OrganismChromosome
extends EngineObject
implements Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Random number generator used in choosing alleles.<p>
    **/
    static private Random random = new Random();

    /**
     * The organism containing this chromosome.  Read-only.  May not be null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to an organism, the organism will generate a
     * property change event for a new organism chromosome.<p>
    **/
    private Organism			organism;
    
    private int chromosomeViewHeight;
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
     * Vector of organism alleles.  May not be null.<p>
     *
     * The objects on this vector are instances of OrganismAllele.<p>
    **/
    private Vector				organismAlleles;

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
     * Species chromosome from which this chromosome was created.  May not be null.<p>
     *
     * There is no reciprocal reference in a species chromosome.  This means when
     * a species is modified / deleted, the right things must happen to keep this
     * reference correct.  Usually this means deleting the organism containing
     * this organism chromosome.<p>
    **/
    private SpeciesChromosome	speciesChromosome;

    /**
     * Length of this chromosome in bases.  Set when chromosome created
     * and may not be modified.<p>
    **/
    private int 				lengthInBases;

    /**
     * Create a new organism chromosome from a species chromosome, randomly
     * choosing alleles from the genes of the species chromosome.<p>
     *
     * It's expected that this method will be used to create organisms from
     * a species definition (e.g. out of thin air), as in when creating a
     * first set of organisms at the start of editing session.<p>
     *
     * @param		anOrganism Organism - containing organism, may not be null
     * @param		aSpeciesChromosome SpeciesChromosome - a species chromosome to use as a model, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismChromosome(Organism anOrganism, SpeciesChromosome aSpeciesChromosome)
    {
        // Check input arguments
        if (anOrganism == null ||
            aSpeciesChromosome == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, initialize instance variables
        speciesChromosome = aSpeciesChromosome;
        numberType = aSpeciesChromosome.getNumberType();
        imageNumber = aSpeciesChromosome.getImageNumber();
        lengthInBases = aSpeciesChromosome.getLengthInBases();
        organismAlleles = null;
        lockedState = EngineObject.UNLOCKED;

        // Randomly choose alleles of genes on the species chromosome
        SpeciesAllele aSpeciesAllele;
        OrganismAllele anOrganismAllele;

        Gene aGene;
        Enumeration eGenes = aSpeciesChromosome.getGenes();
        while (eGenes.hasMoreElements())
        {
            aGene = (Gene) eGenes.nextElement();
            aSpeciesAllele = Species.randomlyChooseAllele(aGene);
            if (aSpeciesAllele != null)
            {
                anOrganismAllele = new OrganismAllele(this,aSpeciesAllele);
            }
        }

        organism = anOrganism;
        organism.addChromosome(this);
    }

    /**
     * Create a new organism chromosome from a chromosome specification,
     * choosing alleles based on the chromosome specification.<p>
     *
     * It's expected that this method will be used to create organism
     * chromosomes produced by an explicit meiosis / fertilization process.<p>
     *
     * @param		anOrganism Organism - containing organism, may not be null
     * @param		aChromosomeSpecification ChromosomeSpecification - a chromosome specification to use, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismChromosome(Organism anOrganism, ChromosomeSpecification aChromosomeSpecification)
    {
        // Check input arguments
        if (anOrganism == null ||
            aChromosomeSpecification == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, initialize instance variables
        speciesChromosome = aChromosomeSpecification.getSpeciesChromosome();
        numberType = speciesChromosome.getNumberType();
        imageNumber = speciesChromosome.getImageNumber();
        lengthInBases = speciesChromosome.getLengthInBases();
        organismAlleles = null;
        lockedState = EngineObject.UNLOCKED;

        // Choose alleles based on ChromosomeSpecification
        SpeciesAllele aSpeciesAllele;
        OrganismAllele anOrganismAllele;

        Enumeration eSpeciesAlleles = aChromosomeSpecification.getSpeciesAlleles();
        while (eSpeciesAlleles.hasMoreElements())
        {
            aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
            anOrganismAllele = new OrganismAllele(this,aSpeciesAllele);
        }

        organism = anOrganism;
        organism.addChromosome(this);
    }

    /**
     * Create a new organism chromosome with another organism chromosome as a model.<p>
     *
     * This constructor is useful when creating an organism from another organism, as when
     * a child is formed from the genotypes of its parents.  See the Organism constructor
     * that takes 2 parent organisms for an example of how this constructor is used.<p>
     *
     * @param		anOrganism Organism - containing organism, may not be null
     * @param		anOrganismChromosome OrganismChromosome - an organism chromosome to use as a model, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismChromosome(Organism anOrganism, OrganismChromosome anOrganismChromosome)
    {
        // Check input arguments
        if (anOrganism == null ||
            anOrganismChromosome == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, initialize instance variables
        speciesChromosome = anOrganismChromosome.getSpeciesChromosome();
        numberType = anOrganismChromosome.getNumberType();
        imageNumber = anOrganismChromosome.getImageNumber();
        lengthInBases = anOrganismChromosome.getLengthInBases();
        organismAlleles = null;
        lockedState = EngineObject.UNLOCKED;

        // Copy organism alleles of input organism chromosome
        OrganismAllele oldOrganismAllele, newOrganismAllele;
        Enumeration eOrganismAlleles = anOrganismChromosome.getOrganismAlleles();
        while (eOrganismAlleles.hasMoreElements())
        {
            oldOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
            newOrganismAllele = new OrganismAllele(this,oldOrganismAllele);
        }

        organism = anOrganism;
        setChromosomeBranchs(anOrganismChromosome.getPStrand(),anOrganismChromosome.getPStrandColor(),
        					 anOrganismChromosome.getQStrand(),anOrganismChromosome.getQStrandColor());
        organism.addChromosome(this);
       
    }

    /**
     * Create a new organism chromosome and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      anOrganism Organism - the enclosing organism for this new organism chromosome
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public OrganismChromosome(Organism anOrganism,
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
        numberType = 1;
        imageNumber = 0;
        speciesChromosome = null;
        lengthInBases = 0;

        // Create vectors
        organismAlleles = new Vector();

        // Creation successful, so add this organism chromosome to organism
        organism = anOrganism;
        //organism.addChromosome(this); // Now added in Organism after parsing is done.

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
            case Elements.SPECIES_CHROMOSOME_ID_ELEMENT_ID:
            case Elements.NUMBER_TYPE_ELEMENT_ID:
            case Elements.LENGTH_IN_BASES_ELEMENT_ID:
            case Elements.IMAGE_NUMBER_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.ORGANISM_ALLELE_ELEMENT_ID:
                new OrganismAllele(this,anElementName,anElementID,
                                   xmlElementContext.getXMLParser(),
                                   xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("OrganismChromosome " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        int speciesChromosomeID;

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

            case Elements.SPECIES_CHROMOSOME_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    speciesChromosome = null;
                }
                else
                {
                    speciesChromosomeID = Integer.valueOf(valueString).intValue();
                    if (speciesChromosomeID != EngineObject.NULL_ID)
                    {
                        speciesChromosome = (SpeciesChromosome) xmlElementContext.getImportContext().getObject(speciesChromosomeID);
                    }
                    else
                    {
                        speciesChromosome = null;
                    }
                }
                break;

            case Elements.ORGANISM_ALLELE_ELEMENT_ID:
                // Done with an organism allele, so reclaim document handler
                break;

            case Elements.ORGANISM_CHROMOSOME_ELEMENT_ID:
                // Done with this organism chromosome, so pop up to enclosing organism
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

        // Delete alleles if there are any
        if (organismAlleles != null)
        {
            OrganismAllele anAllele;
           // Vector organismAllelesClone = (Vector) organismAlleles.clone();
            Enumeration eAlleles = organismAlleles.elements();
            //Enumeration eAlleles = organismAlleles.elements();
            while (eAlleles.hasMoreElements())
            {
                anAllele = (OrganismAllele) eAlleles.nextElement();
                anAllele.delete(notifyChange);
            }
            organismAlleles.removeAllElements();
            organismAlleles = null;
           // organismAllelesClone = null;
        }
        
       /*if(PStrand != null)
	    {
	    	for (int i = 0; i<PStrand.length;i++)
	    	{
	    		PStrand[i] = null;
	    	}
	    	PStrand = null;
	    }
	    
	    if(PStrandColor != null)
	    {
	    	for (int i = 0; i<PStrandColor.length;i++)
	    	{
	    		PStrandColor[i] = null;
	    	}
	    	PStrandColor = null;
	    }
	   
	    if(QStrand != null)
	    {
	    	for (int i = 0; i<QStrand.length;i++)
	    	{
	    		QStrand[i] = null;
	    	}
	    	QStrand = null;
	    }
	    if(QStrandColor!= null)
	    {
	    	for (int i = 0; i<QStrandColor.length;i++)
	    	{
	    		QStrandColor[i] = null;
	    	}
	    	QStrandColor= null;
	    }*/
        // Notify organism
        if (organism != null)
        {
            organism.removeChromosome(this);
            organism = null;
        }

        id = EngineObject.NULL_ID;
    }
    
    /**
     * Get the species chromosome for ths organism chromosome.
     *
     * @return	SpeciesChromosome - species chromosome for this organism chromosome
    **/
    public SpeciesChromosome getSpeciesChromosome()
    {
        return speciesChromosome;
    }

    /**
     * Get the visibility of this chromosome.  This is entirely determined
     * by the species chromosome of this chromosome.  In other words, you
     * cannot set the visibility of an organism chromosome individually.
     * Instead, you can set the visibility of that chromosome for an
     * entire species.  If speciesChromosome is null, return true.
    **/
    public boolean isVisible()
    {
        if (speciesChromosome != null)
        {
            return speciesChromosome.isVisible();
        }

        return true;
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
        if (organismAlleles != null)
        {
            OrganismAllele anAllele;
            Enumeration eAlleles = organismAlleles.elements();
            while (eAlleles.hasMoreElements())
            {
                anAllele = (OrganismAllele) eAlleles.nextElement();
                anAllele.setAutomaticLocked(automaticLocked);
            }
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
        if (organismAlleles != null)
        {
            OrganismAllele anAllele;
            Enumeration eAlleles = organismAlleles.elements();
            while (eAlleles.hasMoreElements())
            {
                anAllele = (OrganismAllele) eAlleles.nextElement();
                anAllele.setManualLocked(manualLocked);
            }
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
        if (organismAlleles != null)
        {
            OrganismAllele anAllele;
            Enumeration eAlleles = organismAlleles.elements();
            while (eAlleles.hasMoreElements())
            {
                anAllele = (OrganismAllele) eAlleles.nextElement();
                anAllele.setLockedState(lockedState);
            }
        }
    }

    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
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
     * Returns the organism chromosome's organism.<p>
     *
     * @return		Organism - containing organism, never null
    **/
    public Organism getOrganism()
    {
        return organism;
    }

    /**
     * Returns the organism chromosome's species.<p>
     *
     * @return		Species - organism chromosome's species, may not be null
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
     * Returns the length of the chromosome in bases.<p>
     *
     * @return		int - length of this chromosome in bases
    **/
    public int getLengthInBases()
    {
        // Don't check for null, assuming we've been careful in this class
        return lengthInBases;
    }

    /**
     * Returns the length of the chromosome in codons.<p>
     *
     * @return		int - length of this chromosome in codons
    **/
    public int getLengthInCodons()
    {
        // Don't check for null, assuming we've been careful in this class
        return lengthInBases / 3;
    }

    /**
     * Returns the base value at the given index in this chromosome.  Note that
     * this value is along one strand.  If the value of the other strand is
     * desired, use Base.getPairBase(). <p>
     *
     * @see			org.concord.biologica.engine.Base#getPairBase
     *
     * @param		index int - index into chromosome, must be 0 to length of chromosome-1
     * @return		byte - base value at given index
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public byte getBase(int index)
    {
        // Don't check for null, assuming we've been careful in this class
        int mod = index % 4;

        if (mod == 0)
        {
            return Base.CYTOSINE;
        }
        else if (mod == 1)
        {
            return Base.ADENINE;
        }
        else if (mod == 2)
        {
            return Base.GUANINE;
        }

        return Base.THYMINE;
    }

    /**
     * Returns the number or type of this chromosome.
     * Possible values include IChromosome.X_CHROMOSOME, IChromosome.Y_CHROMOSOME
     * and an integer greater than 0.<p>
     *
     * @return		int - number / type of this chromosome
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

        // Change numberType
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
     * Returns an enumeration over the chromosome's organism alleles.<p>
     *
     * If there are no alleles on this chromosome, an enumeration with no elements is returned.<p>
     *
     * If there are alleles on this chromosome, the vector of alleles is cloned and an
     * enumeration over that clone is returned.  This enables you to safely modify
     * the chromosome and its alleles while using the enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the vector of alleles of this chromosome, never null
    **/
    public Enumeration getOrganismAlleles()
    {
        if (organismAlleles == null)
        {
            Vector dummyVector = new Vector();
            return dummyVector.elements();
        }

        Vector organismAllelesClone = (Vector) organismAlleles.clone();
        return organismAllelesClone.elements();
    }

    /**
     * Return the number of organism alleles of this chromosome.<p>
     *
     * @return		int - the number of organism alleles of this chromosome
    **/
    public int getNumberOfOrganismAlleles()
    {
        if (organismAlleles == null)
        {
            return 0;
        }
        else
        {
            return organismAlleles.size();
        }
    }

    /**
     * Adds an organism allele to this gene.  Does not check if this allele
     * or an equivalent allele already has been added to this organism.<p>
     *
     * This method is called from the OrganismAllele constructors when a new
     * allele is created.  This method should not be called any other time,
     * as we wanted to limit an allele to only being in one chromosome.<p>
     *
     * @param		anOrganismAllele OrganismAllele - the organism allele to add to this chromosome, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    void addOrganismAllele(OrganismAllele anOrganismAllele)
    {
        // Do coarse check of input values
        if (anOrganismAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Create organismAlleles vector if it's null
        if (organismAlleles == null)
        {
            organismAlleles = new Vector();
        }

        organismAlleles.addElement(anOrganismAllele);

        // Notify listeners
        changes.firePropertyChange(EngineProp.ORGANISM_ALLELE_ADDED,null,anOrganismAllele);
    }

    /**
     * Removes an organism allele from this chromsome.<p>
     *
     * This method returns without doing anything if anAllele is null.<p>
     *
     * @param		anOrganismAllele OrganismAllele - the allele to remove from this chromosome, may be null
     * @return		boolean - returns true if element found, false otherwise
    **/
    boolean removeOrganismAllele(OrganismAllele anOrganismAllele)
    {
        // Return immediately if anOrganismAllele or alleles null
        if (anOrganismAllele == null || organismAlleles == null)
        {
            return false;
        }
        
        boolean result = organismAlleles.removeElement(anOrganismAllele);
        
        // Notify listeners if allele removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.ORGANISM_ALLELE_REMOVED,null,anOrganismAllele);
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
        stream.println("<" + Elements.ORGANISM_CHROMOSOME_ELEMENT_NAME + ">");

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

        // Number type
        stream.println("<" + Elements.NUMBER_TYPE_ELEMENT_NAME + ">" +
                          getNumberTypeAsString() +
                          "</" + Elements.NUMBER_TYPE_ELEMENT_NAME + ">");

        // Image number
        stream.println("<" + Elements.IMAGE_NUMBER_ELEMENT_NAME + ">" + imageNumber +
                          "</" + Elements.IMAGE_NUMBER_ELEMENT_NAME + ">");

        // Species chromosome ID
        if (speciesChromosome == null)
        {
            stream.println("<" + Elements.SPECIES_CHROMOSOME_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SPECIES_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SPECIES_CHROMOSOME_ID_ELEMENT_NAME + ">" +
                              speciesChromosome.getID() +
                              "</" + Elements.SPECIES_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }

        // Length in bases
        stream.println("<" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">" + lengthInBases +
                          "</" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Recursively write children of organism chromosome
        if (organismAlleles != null)
        {
            OrganismAllele anAllele;
            Enumeration eAlleles = organismAlleles.elements();
            while (eAlleles.hasMoreElements())
            {
                anAllele = (OrganismAllele) eAlleles.nextElement();
                anAllele.writeToStream(stream);
            }
            eAlleles = null;
            anAllele = null;
        }

        // End object
        stream.println("</" + Elements.ORGANISM_CHROMOSOME_ELEMENT_NAME + ">");
    }
    
    
    //remember the paint information of the chromosome, if used in Organism
    private OrganismAllele [] PStrand,QStrand;
    private Color [] PStrandColor, QStrandColor;
    
    
    /**
     * This method is used to remember the paint information for this chromosome,
     * Special for drawing the new chromosome for the crossing over offspring.
     * 
     * param@  P_Strand OrganismAllele [] -- the alleles on the up strand of this chromosome;
     * param@  P_Strand_Color Color [] -- the Strand's color
     * param@  Q_Strand OrganismAllele [] -- the alleles on the down strand of this chromosome;
     * param@  Q_Strand_Color Color [] -- the Strand's color
     * 
    **/
    public void setChromosomeBranchs(OrganismAllele [] P_Strand, Color [] P_Strand_Color,
    								 OrganismAllele [] Q_Strand, Color [] Q_Strand_Color)
    {
    	PStrand = P_Strand;
    	PStrandColor = P_Strand_Color;
    	QStrand = Q_Strand;
    	QStrandColor = Q_Strand_Color;
    }
    
    public OrganismAllele [] getPStrand()
    {
    	return PStrand;
    }
    
    public Color [] getPStrandColor()
    {
    	return PStrandColor;
    }
    
    public OrganismAllele [] getQStrand()
    {
    	return QStrand;
    }
    
    public Color [] getQStrandColor()
    {
    	return QStrandColor;
    }
    
    public void setChromosomeViewHeight(int h)
    {
    	chromosomeViewHeight = h;
    }
    
    public int getChromosomeViewHeight()
    {
    	return chromosomeViewHeight;
    }
}

