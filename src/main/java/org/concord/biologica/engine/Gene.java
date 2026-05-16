//
// Class : Gene
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
 * This class represents a gene.<p>
 *
 * This class does not implement INucleicAcid because a gene is considered
 * to be an abstract relationship between different alleles where the alleles
 * are the actual nucleic acids.  <p>
 *
 * A gene MAY change after it has been created, especially new alleles may be
 * created and added to the gene.  This is possible due to mutations.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.DESCRIPTION - the object description has changed
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LENGTH_IN_BASES - length of this gene in bases has changed
 * <li> EngineProp.LOCKED_STATE - object has been locked or unlocked
 * <li> EngineProp.NAME - name of this gene has changed
 * <li> EngineProp.SPECIES_ALLELE_ADDED - a species allele was added
 * <li> EngineProp.SPECIES_ALLELE_REMOVED - a species allele was removed
 * <li> EngineProp.START_INDEX_IN_HOLDER - index of this gene in its chromosome has changed
 * <li> EngineProp.STRAND - top or bottom strand?
 * <li> EngineProp.VISIBLE - gene visibility has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#DESCRIPTION
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LENGTH_IN_BASES
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#SPECIES_ALLELE_ADDED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_ALLELE_REMOVED
 * @see org.concord.biologica.engine.EngineProp#START_INDEX_IN_HOLDER
 * @see org.concord.biologica.engine.EngineProp#STRAND
 * @see org.concord.biologica.engine.EngineProp#VISIBLE
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.3 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class Gene
extends EngineObject
implements Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Top strand
    **/
    static final public int		BOTTOM_STRAND 	= 1;

    /**
     * Bottom strand
    **/
    static final public int		TOP_STRAND		= 2;

    /**
     * Name of this species.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
    **/
    private String				name;

    /**
     * Description of this gene.  May be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.DESCRIPTION.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#DESCRIPTION
    **/
    private String				description;

    /**
     * The chromosome on which this gene exists.  May not be null.<p>
     *
     * Read-only and may not be modified after it is set.<p>
    **/
    private SpeciesChromosome	speciesChromosome;

    /**
     * The start index of this gene on its chromosome in bases.  Must be >= 0.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.START_INDEX_IN_HOLDER.<p>
    **/
    private int					startIndexInHolder;

    /**
     * The "normal" length of this gene, realizing that different alleles
     * of this gene may have different lengths.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.LENGTH_IN_BASES.<p>
    **/
    private int					lengthInBases;

    /**
     * Species alleles.  Rules determine how the species alleles are mapped
     * into characteristics.<p>
     *
     * All the objects on this vector must be instances of SpeciesAllele.<p>
     *
     * When a SpeciesAllele object is created and added to this vector,
     * a EngineProp.SPECIES_ALLELE_ADDED property change event is fired.<p>
     *
     * When a SpeciesAllele object is deleted and removed from this vector,
     * a EngineProp.SPECIES_ALLELE_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.SpeciesAllele
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_ALLELE_ADDED
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_ALLELE_REMOVED
    **/
    private Vector				speciesAlleles;

    /**
     * Whether this gene is visible or not.<p>
     *
     * When this changes, a EngineProp.VISIBLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#VISIBLE
    **/
    private boolean				visible;

    /**
     * Top or bottom strand?<p>
     *
     * If the top strand, then the gene bases are in the order of
     * the DNA sequence.  If the bottom strand, then the bases are in
     * the backwards order of the DNA sequence.  In other words, for
     * the bottom strand, the start base index is actually the last
     * base from the sequence's point of view.<p>
     *
     * @see		org.concord.biologica.engine.Gene#BOTTOM_STRAND
     * @see		org.concord.biologica.engine.Gene#TOP_STRAND
     * @see		org.concord.biologica.engine.EngineProp#STRAND
    **/
    private int					strand;

    /**
     * Create a new gene given a species chromosome.<p>
     *
     * @param		aSpeciesChromosome SpeciesChromosome - the species chromosome of this gene, may not be null
     * @param		aName String - the name for this gene, may not be null
     * @param		aStartIndexInHolder int - the start index of the gene in bases on its chromosome, must be greater than zero
     * @param		aLengthInBases int - the "normal" length of the gene, must be zero or greater
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Gene(SpeciesChromosome aSpeciesChromosome, String aName,
                int aStartIndexInHolder, int aLengthInBases)
    {
        // Do coarse check of input values
        if (aSpeciesChromosome == null ||
            aStartIndexInHolder < 0 ||
            aLengthInBases < 1 ||
            aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK, so initialize instance variables
        name = new String(aName);
        description = null;
        visible = true;
        lockedState = EngineObject.UNLOCKED;

        startIndexInHolder = aStartIndexInHolder;
        lengthInBases = aLengthInBases;
        speciesAlleles = new Vector();
        strand = TOP_STRAND;

        // Creation successful, so add this gene to species chromosome
        speciesChromosome = aSpeciesChromosome;
        speciesChromosome.addGene(this);
    }

    /**
     * Create a new gene and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aSpeciesChromosome SpeciesChromosome - the enclosing species chromosome for this new gene
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public Gene(SpeciesChromosome aSpeciesChromosome,
                String anElementName,
                int anElementID,
                /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                ImportContext importContext)
    {
        if (aSpeciesChromosome == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        visible = true;
        lockedState = EngineObject.UNLOCKED;
        name = null;
        description = null;
        strand = TOP_STRAND;
        startIndexInHolder = 0;
        lengthInBases = 0;

        // Create vectors
        speciesAlleles = new Vector();

        // Creation successful, so add this species chromosome to species
        speciesChromosome = aSpeciesChromosome;
        speciesChromosome.addGene(this);

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
            case Elements.DESCRIPTION_ELEMENT_ID:
            case Elements.SPECIES_CHROMOSOME_ID_ELEMENT_ID:
            case Elements.START_INDEX_IN_HOLDER_ELEMENT_ID:
            case Elements.LENGTH_IN_BASES_ELEMENT_ID:
            case Elements.VISIBLE_ELEMENT_ID:
            case Elements.STRAND_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.SPECIES_ALLELE_ELEMENT_ID:
                new SpeciesAllele(this,anElementName,anElementID,
                                  xmlElementContext.getXMLParser(),
                                  xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("Gene " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.SPECIES_CHROMOSOME_ID_ELEMENT_ID:
                // Ignore, as we already have the species chromosome
                break;

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.DESCRIPTION_ELEMENT_ID:
                description = xmlElementContext.getValueString();
                break;

            case Elements.START_INDEX_IN_HOLDER_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                startIndexInHolder = Integer.valueOf(valueString).intValue();
                break;

            case Elements.LENGTH_IN_BASES_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                lengthInBases = Integer.valueOf(valueString).intValue();
                break;

            case Elements.VISIBLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                visible = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.STRAND_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                strand = Integer.valueOf(valueString).intValue();
                break;

            case Elements.SPECIES_ALLELE_ELEMENT_ID:
                // Done with a species allele, so reclaim document handler
                break;

            case Elements.GENE_ELEMENT_ID:
                // Done with this gene, so pop up to enclosing species chromosome
                speciesChromosome.endElement(anElementName);
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
        description = null;
        startIndexInHolder = 0;
        lengthInBases = 0;

        SpeciesAllele aSpeciesAllele;
        Vector speciesAllelesClone = (Vector) speciesAlleles.clone();
        Enumeration eSpeciesAlleles = speciesAllelesClone.elements();
        //Enumeration eSpeciesAlleles = speciesAlleles.elements();
        while (eSpeciesAlleles.hasMoreElements())
        {
            aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
            aSpeciesAllele.delete(notifyChange);
        }
        speciesAlleles.removeAllElements();
        speciesAlleles = null;
        speciesAllelesClone = null;

        speciesChromosome.removeGene(this);
        speciesChromosome = null;

        id = EngineObject.NULL_ID;
    }
    
    /**
     * Get the species chromosome containing this gene.
     *
     * @return		SpeciesChromosome - the chromosome containing this gene
    **/
    public SpeciesChromosome getSpeciesChromosome()
    {
        return speciesChromosome;
    }

    /**
     * Get the species containing this object.<p>
     *
     * @return	Species - the species containing this object, never null.
    **/
    public Species getSpecies()
    {
        return speciesChromosome.getSpecies();
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return speciesChromosome.getWorld();
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
        SpeciesAllele anAllele;
        Enumeration eAlleles = speciesAlleles.elements();
        while (eAlleles.hasMoreElements())
        {
            anAllele = (SpeciesAllele) eAlleles.nextElement();
            anAllele.setAutomaticLocked(automaticLocked);
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
        SpeciesAllele anAllele;
        Enumeration eAlleles = speciesAlleles.elements();
        while (eAlleles.hasMoreElements())
        {
            anAllele = (SpeciesAllele) eAlleles.nextElement();
            anAllele.setManualLocked(manualLocked);
        }
    }

    /**
     * Set the locked state of the object, recursively setting the locked
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
        SpeciesAllele anAllele;
        Enumeration eAlleles = speciesAlleles.elements();
        while (eAlleles.hasMoreElements())
        {
            anAllele = (SpeciesAllele) eAlleles.nextElement();
            anAllele.setLockedState(lockedState);
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
        return EngineStrings.GENE_COLON + name;
    }

    /**
     * Return the name of this gene.  May be null.<p>
     *
     * @return		String - name of this gene, may not be null.
    **/
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this gene.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this gene, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
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
            throw new IllegalArgumentException("input aName null");
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
        String oldName = name;
        name = new String(aName);

        // Notify listeners
        changes.firePropertyChange(EngineProp.NAME,oldName,name);
    }

    /**
     * Return the description of this gene.  May be null.<p>
     *
     * @return		String - description of this gene, may not be null.
    **/
    public String getDescription()
    {
        if (description == null)
        {
            return new String("");
        }
        return description;
    }

    /**
     * Set the description of this gene.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.DESCRIPTION.<p>
     *
     * @param		aDescription String - the new description of this gene, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
    **/
    public void setDescription(String aDescription)
    {
        // Return immediately if aDescription equals the current description
        if (aDescription != null && description != null && aDescription.equals(description))
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

        if (aDescription == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        String oldDescription = description;
        description = new String(aDescription);

        // Notify listeners
        changes.firePropertyChange(EngineProp.DESCRIPTION,oldDescription,description);
    }

    /**
     * Returns the start index of this gene acid in its chromosome.<p>
     *
     * @return		int - index in holder, in bases
    **/
    public int getStartIndexInHolder()
    {
        return startIndexInHolder;
    }

    /**
     * Set the start index of this gene in its chromosome.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.START_INDEX_IN_HOLDER.<p>
     *
     * @param		int - new index in holder, must be between 0 and the length in bases
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setStartIndexInHolder(int anIndex)
    {
        // Check input arguments
        if (anIndex < 0)
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
        if (anIndex != startIndexInHolder)
        {
            int oldIndex = startIndexInHolder;
            startIndexInHolder = anIndex;
            changes.firePropertyChange(EngineProp.START_INDEX_IN_HOLDER,
                                       new Integer(oldIndex),
                                       new Integer(startIndexInHolder));
        }
    }

    /**
     * Returns the length of this gene in bases.<p>
     *
     * @return		int - length of this gene in bases
    **/
    public int getLengthInBases()
    {
        return lengthInBases;
    }

    /**
     * Set the length in bases of this gene.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.LENGTH_IN_BASES.<p>
     *
     * @param		aLengthInBases int - new length in bases for gene
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setLengthInBases(int aLengthInBases)
    {
        // Check input arguments
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

        // Ok - make change
        if (lengthInBases != aLengthInBases)
        {
            int oldLengthInBases = lengthInBases;
            lengthInBases = aLengthInBases;
            changes.firePropertyChange(EngineProp.LENGTH_IN_BASES,
                                       new Integer(oldLengthInBases),
                                       new Integer(lengthInBases));
        }
    }

    /**
     * Returns the mutation species allele for this gene, if there is one.<p>
     *
     * The mutation species allele is the allele that will be assumed when
     * the user manually mutates an existing allele of this gene into something
     * that isn't recognizable as a known allele.  In this case, the mutation
     * allele will be used for purposes of genotype to phenotype calculations, etc.<p>
     *
     * @return		SpeciesAllele - the mutation species allele, may be null
    **/
    public SpeciesAllele getMutationSpeciesAllele()
    {
        if (speciesAlleles != null)
        {
            SpeciesAllele aSpeciesAllele;
            Enumeration eSpeciesAlleles = speciesAlleles.elements();
            while (eSpeciesAlleles.hasMoreElements())
            {
                aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                if (aSpeciesAllele.isMutationAllele())
                {
                    return aSpeciesAllele;
                }
            }
            
        }

        return null;
    }

    /**
     * Returns an enumeration over the gene's species alleles.<p>
     *
     * If there are no alleles of this gene, an enumeration with no elements is returned.<p>
     *
     * If there are alleles of this gene, the vector of alleles is cloned and an
     * enumeration over that clone is returned.  This enables you to safely modify
     * the gene and its alleles while using the enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the vector of alleles of this gene, never null
    **/
    public Enumeration getSpeciesAlleles()
    {
        if (speciesAlleles == null)
        {
            Vector dummyVector = new Vector();
            return dummyVector.elements();
        }

        Vector speciesAllelesClone = (Vector) speciesAlleles.clone();
        return speciesAllelesClone.elements();
    }

    /**
     * Return the number of species alleles of this gene.<p>
     *
     * @return		int - the number of species alleles of this gene
    **/
    public int getNumberOfSpeciesAlleles()
    {
        return speciesAlleles.size();
    }

    /**
     * Adds a species allele to this gene.  Does not check if this allele
     * or an equivalent allele already has been added to this gene.<p>
     *
     * This method is called from the Allele constructors when a new
     * allele is created.  This method should not be called any other time,
     * as we wanted to limit an allele to only being in one gene.<p>
     *
     * @param		aSpeciesAllele SpeciesAllele - the allele to add to this gene, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    void addSpeciesAllele(SpeciesAllele aSpeciesAllele)
    {
        // Do coarse check of input values
        if (aSpeciesAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Create speciesAlleles vector if it's null
        if (speciesAlleles == null)
        {
            speciesAlleles = new Vector();
        }

        speciesAlleles.addElement(aSpeciesAllele);

        // Notify listeners
        changes.firePropertyChange(EngineProp.SPECIES_ALLELE_ADDED,null,aSpeciesAllele);
    }

    /**
     * Removes a species allele from this gene.<p>
     *
     * This method should not be called yet, as we haven't determined
     * how objects are destroyed yet in this engine.  There's a good
     * chance that we'll add destroy() methods to all engine classes
     * eventually, which would cause this method to be called. <p>
     *
     * This method returns without doing anything if anAllele is null.<p>
     *
     * @param		aSpeciesAllele SpeciesAllele - the allele to remove from this gene, may be null
     * @return		boolean - returns true if element found, false otherwise
    **/
    boolean removeSpeciesAllele(SpeciesAllele aSpeciesAllele)
    {
        // Return immediately if anAllele or alleles null
        if (aSpeciesAllele == null || speciesAlleles == null)
        {
            return false;
        }
        
        boolean result = speciesAlleles.removeElement(aSpeciesAllele);
        
        // Notify listeners if allele removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.SPECIES_ALLELE_REMOVED,null,aSpeciesAllele);
        }

        return result;
    }

    /**
     * Get whether this gene is visible.<p>
     *
     * @return	boolean - whether this gene is visible (true) or not visible (false)
    **/
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Set whether this gene is visible.<p>
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
     * Get the strand of this gene.<p>
     *
     * @return	int - the strand of this gene, TOP_STRAND or BOTTOM_STRAND
     * @see		org.concord.biologica.engine.Gene#BOTTOM_STRAND
     * @see		org.concord.biologica.engine.Gene#TOP_STRAND
    **/
    public int getStrand()
    {
        return strand;
    }

    /**
     * Set the strand of this gene.<p>
     *
     * @param	aStrand int - strand of gene
    **/
    public void setStrand(int aStrand)
    {
        if (aStrand != strand)
        {
            int oldStrand = strand;
            strand = aStrand;
    
            changes.firePropertyChange(EngineProp.STRAND,
                                       new Integer(oldStrand),
                                       new Integer(strand));
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
        stream.println("<" + Elements.GENE_ELEMENT_NAME + ">");

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

        // Description
        if (description == null)
        {
            stream.println("<" + Elements.DESCRIPTION_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.DESCRIPTION_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.DESCRIPTION_ELEMENT_NAME + ">" + description +
                              "</" + Elements.DESCRIPTION_ELEMENT_NAME + ">");
        }

        // Species chromosome ID
        stream.println("<" + Elements.SPECIES_CHROMOSOME_ID_ELEMENT_NAME + ">" + speciesChromosome.getID() +
                          "</" + Elements.SPECIES_CHROMOSOME_ID_ELEMENT_NAME + ">");

        // Start index in holder
        stream.println("<" + Elements.START_INDEX_IN_HOLDER_ELEMENT_NAME + ">" + startIndexInHolder +
                          "</" + Elements.START_INDEX_IN_HOLDER_ELEMENT_NAME + ">");

        // Length in bases
        stream.println("<" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">" + lengthInBases +
                          "</" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">");

        // Visible
        stream.println("<" + Elements.VISIBLE_ELEMENT_NAME + ">" + visible +
                          "</" + Elements.VISIBLE_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Strand
        stream.println("<" + Elements.STRAND_ELEMENT_NAME + ">" + strand +
                          "</" + Elements.STRAND_ELEMENT_NAME + ">");

        // Recursively write children of gene
        {
            SpeciesAllele anAllele;
            Enumeration eAlleles = speciesAlleles.elements();
            while (eAlleles.hasMoreElements())
            {
                anAllele = (SpeciesAllele) eAlleles.nextElement();
                anAllele.writeToStream(stream);
            }
        }

        // End object
        stream.println("</" + Elements.GENE_ELEMENT_NAME + ">");
    }
}

