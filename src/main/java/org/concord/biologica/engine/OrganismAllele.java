//
// Class : OrganismAllele
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
 * This class represents an organism allele - an allele within
 * one organism.  The key difference between this and a species
 * allele is that a species allele cannot change within a single
 * organism, whereas an organism allele may change (mutate).<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.BASE_VALUES - base values for allele's raw nucleic acid changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.TEXT_SYMBOL	- text symbol for allele changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#BASE_VALUES
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#TEXT_SYMBOL
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class OrganismAllele
extends EngineObject
implements INucleicAcidHolder, Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * The organism chromosome containing this allele.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to an organism, the organism will generate a
     * vector property change event for a new allele.<p>
    **/
    private OrganismChromosome	organismChromosome;

    /**
     * The gene to which this allele corresponds.<p>
    **/
    private Gene gene;

    /**
     * The species allele from which this allele originated.
     * However, this allele may be mutated.  So there is no
     * guarantee that this allele will contain the same bases
     * specified in the species allele.<p>
    **/
    private SpeciesAllele speciesAllele;

    /**
     * The textual symbol of this allele, if it has one.
     * For example "H" or "h" would be the possible textual
     * symbols of alleles for a horn gene.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.TEXT_SYMBOL.<p>
    **/
    private String				textSymbol;

    /**
     * OrganismAllele's raw nucleic acid.  May not be null.<p>
     *
     * When this property or the values in this nucleic acid are
     * changed, a property change event is generated for the
     * property named EngineProp.BASE_VALUES.<p>
    **/
    private RawNucleicAcid		rawNucleicAcid;

    /**
     * Create a new organism allele given an organism chromosome parent and the species allele to use.
     *
     * @param		anOrganismChromosome OrganismChromosome - the organism chromosome of this allele, may not be null
     * @param		aSpeciesAllele - the species allele of this allele, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismAllele(OrganismChromosome anOrganismChromosome,
                          SpeciesAllele aSpeciesAllele)
    {
        // Do check of input values
        if (anOrganismChromosome == null ||
            aSpeciesAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Input arguments OK, so initialize other instance variables
        lockedState = EngineObject.UNLOCKED;

        // Create raw nucleic acid
        RawNucleicAcid speciesRawNucleicAcid = aSpeciesAllele.getRawNucleicAcid();

        rawNucleicAcid = new RawNucleicAcid(this,
                                            speciesRawNucleicAcid.getStartIndexInHolder(),
                                            speciesRawNucleicAcid,
                                            Base.IN_DNA);

        speciesAllele = aSpeciesAllele;

        gene = speciesAllele.getGene();

        textSymbol = speciesAllele.getTextSymbol();

        // Tell the parent chromosome
        organismChromosome = anOrganismChromosome;
        organismChromosome.addOrganismAllele(this);
    }
    
    /**
     * Create a new organism allele given an organism chromosome and an organism allele to copy.<p>
     *
     * This version of the constructor is used when a child is created from 2 parents and the child's
     * organism chromosomes and organism alleles are inherited from its parents.<p>
     *
     * @param		anOrganismChromosome OrganismChromosome - the organism chromosome parent of this allele, may not be null
     * @param		anOrganismAllele - the organism allele to be copied, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public OrganismAllele(OrganismChromosome anOrganismChromosome,
                          OrganismAllele anOrganismAllele)
    {
        // Do check of input values
        if (anOrganismChromosome == null ||
            anOrganismAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Input arguments OK, so initialize other instance variables
        lockedState = EngineObject.UNLOCKED;

        // Create raw nucleic acid
        speciesAllele = anOrganismAllele.getSpeciesAllele();
        RawNucleicAcid speciesRawNucleicAcid = speciesAllele.getRawNucleicAcid();
        
        rawNucleicAcid = new RawNucleicAcid(this,
                                            speciesRawNucleicAcid.getStartIndexInHolder(),
                                            speciesRawNucleicAcid,
                                            Base.IN_DNA);

        gene = speciesAllele.getGene();

        textSymbol = speciesAllele.getTextSymbol();

        // Tell the parent chromosome
        organismChromosome = anOrganismChromosome;
        organismChromosome.addOrganismAllele(this);
    }

    /**
     * Create a new organism allele and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      anOrganismChromosome OrganismChromosome - the enclosing organism chromosome for this new organism allele
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public OrganismAllele(OrganismChromosome anOrganismChromosome,
                          String anElementName,
                          int anElementID,
                          /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                          ImportContext importContext)
    {
        if (anOrganismChromosome == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        lockedState = EngineObject.UNLOCKED;
        textSymbol = null;

        // Creation successful, so add this species allele to its gene
        organismChromosome = anOrganismChromosome;
        organismChromosome.addOrganismAllele(this);

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
            case Elements.ORGANISM_CHROMOSOME_ID_ELEMENT_ID:
            case Elements.GENE_ID_ELEMENT_ID:
            case Elements.SPECIES_ALLELE_ID_ELEMENT_ID:
            case Elements.TEXT_SYMBOL_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.RAW_NUCLEIC_ACID_ELEMENT_ID:
                rawNucleicAcid = new RawNucleicAcid(this,anElementName,anElementID,
                                                    xmlElementContext.getXMLParser(),
                                                    xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("SpeciesAllele " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        int geneID;
        int speciesAlleleID;

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

            case Elements.ORGANISM_CHROMOSOME_ID_ELEMENT_ID:
                // Ignore, as we already have the organism chromosome
                break;

            case Elements.GENE_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    gene = null;
                }
                else
                {
                    geneID = Integer.valueOf(valueString).intValue();
                    if (geneID != EngineObject.NULL_ID)
                    {
                        gene = (Gene) xmlElementContext.getImportContext().getObject(geneID);
                    }
                    else
                    {
                        gene = null;
                    }
                }
                break;

            case Elements.SPECIES_ALLELE_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    speciesAllele = null;
                }
                else
                {
                    speciesAlleleID = Integer.valueOf(valueString).intValue();
                    if (speciesAlleleID != EngineObject.NULL_ID)
                    {
                        speciesAllele = (SpeciesAllele) xmlElementContext.getImportContext().getObject(speciesAlleleID);
                    }
                    else
                    {
                        speciesAllele = null;
                    }
                }
                break;

            case Elements.TEXT_SYMBOL_ELEMENT_ID:
                textSymbol = xmlElementContext.getValueString();
                break;

            case Elements.RAW_NUCLEIC_ACID_ELEMENT_ID:
                // Done with a raw nucleic acid, so reclaim document handler
                break;

            case Elements.ORGANISM_ALLELE_ELEMENT_ID:
                // Done with this organism allele, so pop up to enclosing organism chromosome
                organismChromosome.endElement(anElementName);
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
        rawNucleicAcid.delete();
        rawNucleicAcid = null;

        // Forget text symbol, gene and species allele
        textSymbol = null;
        gene = null;
        speciesAllele = null;

        // Remove from chromosome
        organismChromosome.removeOrganismAllele(this);
        organismChromosome = null;
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
        return EngineStrings.ORGANISM_ALLELE_COLON + textSymbol;
    }

    /**
     * Returns the allele's text symbol.<p>
     *
     * @return		String - allele's text symbol
    **/
    public String getTextSymbol()
    {
        return textSymbol;
    }

    /**
     * Set the text symbol of the allele.<p>
     *
     * @param	aTextSymbol String - new text symbol for object, may be null
    **/
    public void setTextSymbol(String aTextSymbol)
    {
        if (textSymbol != aTextSymbol)
        {
            // Handle case where aTextSymbol is null
            if (aTextSymbol == null)
            {
                speciesAllele = null;
                String oldTextSymbol = textSymbol;
                textSymbol = null;
                changes.firePropertyChange(EngineProp.TEXT_SYMBOL,oldTextSymbol,textSymbol);
                return;
            }
            else
            {
                // Find the corresponding species allele
                RawNucleicAcid newRawNucleicAcid;
                SpeciesAllele aSpeciesAllele;
                Gene gene = speciesAllele.getGene();
                Enumeration eSpeciesAlleles = gene.getSpeciesAlleles();
                while (eSpeciesAlleles.hasMoreElements())
                {
                    aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                    if (aSpeciesAllele.getTextSymbol().equals(aTextSymbol))
                    {
                        String oldTextSymbol = textSymbol;
                        speciesAllele = aSpeciesAllele;
                        textSymbol = aTextSymbol;
                        RawNucleicAcid speciesRawNucleicAcid = speciesAllele.getRawNucleicAcid();
                        rawNucleicAcid = null;
                        newRawNucleicAcid = new RawNucleicAcid(this,
                                                               speciesRawNucleicAcid.getStartIndexInHolder(),
                                                               speciesRawNucleicAcid,
                                                               Base.IN_DNA);
                        changes.firePropertyChange(EngineProp.TEXT_SYMBOL,oldTextSymbol,textSymbol);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Returns the allele's gene.<p>
     *
     * @return		Gene - allele's gene, may not be null
    **/
    public Gene getGene()
    {
        return gene;
    }

    /**
     * Returns the allele's organism chromosome.<p>
     *
     * @return		OrganismChromosome - allele's organism chromosome, may not be null
    **/
    public OrganismChromosome getOrganismChromosome()
    {
        return organismChromosome;
    }

    /**
     * Returns the allele's organism.<p>
     *
     * @return      Organism - the allele's organism, may not be null
    **/
    public Organism getOrganism()
    {
        return organismChromosome.getOrganism();
    }

    /**
     * Returns the organism allele's species.<p>
     *
     * @return		Species - organism allele's species, may not be null
    **/
    public Species getSpecies()
    {
        return organismChromosome.getSpecies();
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return organismChromosome.getWorld();
    }

    /**
     * Is this allele an instance of the mutation allele for its gene?<p>
     *
     * @return	boolean - mutation allele for its gene?
    **/
    public boolean isMutationAllele()
    {
        return speciesAllele.isMutationAllele();
    }

    /**
     * Returns the organism allele's species allele.<p>
     *
     * @return		SpeciesAllele - organism allele's species allele, may not be null
    **/
    public SpeciesAllele getSpeciesAllele()
    {
        return speciesAllele;
    }

    /**
     * Set the organism's species allele.<p>
     *
     * @param		aSpeciesAllele - the organism allele's new species allele, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setSpeciesAllele(SpeciesAllele aSpeciesAllele)
    {
        if (aSpeciesAllele == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (speciesAllele != aSpeciesAllele)
        {
            speciesAllele = aSpeciesAllele;
            String oldTextSymbol = textSymbol;
            textSymbol = speciesAllele.getTextSymbol();
            changes.firePropertyChange(EngineProp.TEXT_SYMBOL,oldTextSymbol,textSymbol);
        }
    }

    /**
     * Returns the length of the allele in bases.<p>
     *
     * @return		int - length of this autosome in bases
    **/
    public int getLengthInBases()
    {
        // Don't check for null, assuming we've been careful in this class
        return rawNucleicAcid.getLengthInBases();
    }

    /**
     * Returns the length of the allele in codons.  Note that this
     * is calculated by dividing the number of bases by 3, ignoring
     * bases which may not fit evenly into multiples of 3 (codons).<p>
     *
     * @return		int - length of this autosome in codons
    **/
    public int getLengthInCodons()
    {
        // Don't check for null, assuming we've been careful in this class
        return rawNucleicAcid.getLengthInCodons();
    }

    /**
     * Returns the base value at the given index in this allele.  Note that
     * this value is along one strand.  If the value of the other strand is
     * desired, use Base.getPairBase(). <p>
     *
     * @see			org.concord.biologica.engine.Base#getPairBase
     *
     * @param		index int - index into allele, must be 0 to length of allele-1
     * @return		byte - base value at given index
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public byte getBase(int index)
    {
        return rawNucleicAcid.getBase(index);
    }

    /**
     * Sets the base value at the given index in this allele.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.BASE_VALUES.<p>
     *
     * @param		index int - index into allele, must be 0 to length of allele-1
     * @param		newBase byte - new base value at given index
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setBase(int index, byte newBase)
    {
        rawNucleicAcid.setBase(index, newBase);

        changes.firePropertyChange(EngineProp.BASE_VALUES,null,null);
    }

    /**
     * Sets the base values array for this allele.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.BASE_VALUES.<p>
     *
     * @param		newBases byte[] - new base values array
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public void setBases(byte[] newBases)
    {
        rawNucleicAcid.setBases(newBases);

        changes.firePropertyChange(EngineProp.BASE_VALUES,null,null);
    }

    /**
     * Reconcile base changes already made to this organism allele, usually
     * due to a user editing the DNA directly in the DNA View.
    **/
    public void reconcileBaseChanges()
    {
        // Try to match new bases to existing species alleles
        SpeciesAllele aSpeciesAllele;
        SpeciesAllele mutationAllele = null;
        boolean foundSpeciesAllele = false;
        String oldTextSymbol = textSymbol;
        byte[] myBases = rawNucleicAcid.getBases();
        int iMutationAllele = 0, indexMutationAllele = 0;

        Gene myGene = speciesAllele.getGene();
        Enumeration eMySpeciesAlleles = myGene.getSpeciesAlleles();
        while (eMySpeciesAlleles.hasMoreElements())
        {
            iMutationAllele++;
            aSpeciesAllele = (SpeciesAllele) eMySpeciesAlleles.nextElement();

            if (aSpeciesAllele.isMutationAllele() && mutationAllele == null)
            {
                mutationAllele = aSpeciesAllele;
                indexMutationAllele = iMutationAllele;
            }

            if (aSpeciesAllele.matchBases(myBases))
            {
                foundSpeciesAllele = true;
                speciesAllele = aSpeciesAllele;
                textSymbol = aSpeciesAllele.getTextSymbol();
                changes.firePropertyChange(EngineProp.TEXT_SYMBOL,oldTextSymbol,textSymbol);
                break;
            }
            else if (aSpeciesAllele.getLengthInBases() == 0 &&
                     aSpeciesAllele.isMutationAllele())
            {
                // Found a blank mutation species allele.  Set its bases to be the
                // new bases of this organism allele.  In essence, we're claiming
                // the blank mutation species allele as the allele for this mutated
                // organism allele.  The next mutation of this gene will need to
                // create a new species allele, as shown below.
                mutationAllele = aSpeciesAllele;
                indexMutationAllele = iMutationAllele;

                mutationAllele.setBases(myBases);
                mutationAllele.setVisible(true);

                foundSpeciesAllele = true;
                speciesAllele = aSpeciesAllele;
                textSymbol = aSpeciesAllele.getTextSymbol();
                changes.firePropertyChange(EngineProp.TEXT_SYMBOL,oldTextSymbol,textSymbol);
                break;
            }
        }

        // Create new allele if we didn't find one that matches our base values
        if (!foundSpeciesAllele)
        {
            // Create new species allele
            int numberOfNewSpeciesAllele = myGene.getNumberOfSpeciesAlleles() - indexMutationAllele + 1;
            String mutationTextSymbol = speciesAllele.getTextSymbol() + numberOfNewSpeciesAllele;
            if (mutationAllele != null)
            {
                mutationTextSymbol = mutationAllele.getTextSymbol() + numberOfNewSpeciesAllele;
            }
            speciesAllele = new SpeciesAllele(myGene,mutationTextSymbol,myBases);
            textSymbol = speciesAllele.getTextSymbol();
            speciesAllele.setVisible(true);

            // Create new genotype to phenotype rules for this new mutation allele,
            // copying any existing rules that have mutation allele
            if (mutationAllele != null)
            {
                String newRuleName;
                Species mySpecies = myGene.getSpecies();
                GenotypeToPhenotypeRule aRule, newRule;
                Enumeration eGenotypeToPhenotypeRules = mySpecies.getGenotypeToPhenotypeRules();
                while (eGenotypeToPhenotypeRules.hasMoreElements())
                {
                    aRule = (GenotypeToPhenotypeRule) eGenotypeToPhenotypeRules.nextElement();
                    if (aRule.containsIfSpeciesAllele(mutationAllele))
                    {
                        newRuleName = aRule.getName() + numberOfNewSpeciesAllele;
                        newRule = new GenotypeToPhenotypeRule(mySpecies,newRuleName,
                                                              aRule,
                                                              mutationAllele,
                                                              speciesAllele);
                        // mySpecies.moveGenotypeToPhenotypeRuleAfter(newRule,aRule);
                        newRule.setLockedState(EngineObject.AUTOMATIC_LOCKED);
                    }
                }
            }

            speciesAllele.setLockedState(EngineObject.AUTOMATIC_LOCKED);
            changes.firePropertyChange(EngineProp.TEXT_SYMBOL,oldTextSymbol,textSymbol);
        }

        // Upate genotype and phenotype of organism, notifying listeners
        organismChromosome.getOrganism().updateGenotypeAndPhenotype(true);
    }

    /**
     * Get the text symbol that this allele would have if the changes
     * pending on it were to be applied.<p>
     *
     * @return  String - pending allele text symbol
    **/
    public String getPendingTextSymbol()
    {
        // Try to match new bases to existing species alleles
        SpeciesAllele aSpeciesAllele;
        SpeciesAllele mutationAllele = null;
        byte[] myBases = rawNucleicAcid.getBases();
        int iMutationAllele = 0, indexMutationAllele = 0;
        String pendingTextSymbol = null;

        Gene myGene = speciesAllele.getGene();
        Enumeration eMySpeciesAlleles = myGene.getSpeciesAlleles();
        while (eMySpeciesAlleles.hasMoreElements())
        {
            iMutationAllele++;
            aSpeciesAllele = (SpeciesAllele) eMySpeciesAlleles.nextElement();

            if (aSpeciesAllele.isMutationAllele() && mutationAllele == null)
            {
                mutationAllele = aSpeciesAllele;
                indexMutationAllele = iMutationAllele;
            }

            if (aSpeciesAllele.matchBases(myBases))
            {
                pendingTextSymbol = aSpeciesAllele.getTextSymbol();
                return pendingTextSymbol;
            }
            else if (aSpeciesAllele.getLengthInBases() == 0 &&
                     aSpeciesAllele.isMutationAllele())
            {
                pendingTextSymbol = aSpeciesAllele.getTextSymbol();
                return pendingTextSymbol;
            }
        }

        // Did not find a species allele, so we'd create a new one
        int numberOfNewSpeciesAllele = myGene.getNumberOfSpeciesAlleles() - indexMutationAllele + 1;
        String mutationTextSymbol = speciesAllele.getTextSymbol() + numberOfNewSpeciesAllele;
        if (mutationAllele != null)
        {
            mutationTextSymbol = mutationAllele.getTextSymbol() + numberOfNewSpeciesAllele;
        }
        return mutationTextSymbol;
    }

    /**
     * Returns the start index of this nucleic acid in its holder.<p>
     *
     * For an allele, this is always zero as the allele's position in the
     * chromosome isn't known to the allele, but rather its gene.<p>
     *
     * @return		int - index in holder
    **/
    public int getStartIndexInHolder()
    {
        return speciesAllele.getStartIndexInHolder();
    }

    /**
     * Adds a nucleic acid to the holder.<p>
     *
     * This SHOULD be package protected because this is only to be called
     * from the nucleic acid's constructor.  Creating a nucleic acid
     * automatically adds it to the holder via this method.<p>
     *
     * BUT, Java has some wierd idea that interface methods are always
     * public, so I can't make this package protected.  So please don't
     * use them method from outside of the engine package!!<p>
     *
     * @param		aNucleicAcid INucleicAcid - a new nucleic acid, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void addNucleicAcid(INucleicAcid aNucleicAcid)
    {
        if ((!(aNucleicAcid instanceof RawNucleicAcid)) ||
            rawNucleicAcid != null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        rawNucleicAcid = (RawNucleicAcid) aNucleicAcid;
    }

    /**
     * Removes a nucleic acid from the holder.<p>
     *
     * This SHOULD be package protected because this is only to be called
     * from the nucleic acid's delete method.  Deleting a nucleic acid
     * automatically removes it from the holder via this method.<p>
     *
     * BUT, Java has some wierd idea that interface methods are always
     * public, so I can't make this package protected.  So please don't
     * use them method from outside of the engine package!!<p>
     *
     * @param		aNucleicAcid INucleicAcid - a nucleic acid, may not be null
     * @return		boolean indicating whether or not the nucleic acid was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public boolean removeNucleicAcid(INucleicAcid aNucleicAcid)
    {
        if (aNucleicAcid != rawNucleicAcid)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        rawNucleicAcid = null;

        return true;
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
        stream.println("<" + Elements.ORGANISM_ALLELE_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Organism chromosome ID
        if (organismChromosome == null)
        {
            stream.println("<" + Elements.ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">" + organismChromosome.getID() +
                              "</" + Elements.ORGANISM_CHROMOSOME_ID_ELEMENT_NAME + ">");
        }

        // Gene ID
        if (gene == null)
        {
            stream.println("<" + Elements.GENE_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.GENE_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.GENE_ID_ELEMENT_NAME + ">" + gene.getID() +
                              "</" + Elements.GENE_ID_ELEMENT_NAME + ">");
        }

        // Species allele ID
        if (speciesAllele == null)
        {
            stream.println("<" + Elements.SPECIES_ALLELE_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SPECIES_ALLELE_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SPECIES_ALLELE_ID_ELEMENT_NAME + ">" + speciesAllele.getID() +
                              "</" + Elements.SPECIES_ALLELE_ID_ELEMENT_NAME + ">");
        }

        // Text symbol
        stream.println("<" + Elements.TEXT_SYMBOL_ELEMENT_NAME + ">" + textSymbol +
                          "</" + Elements.TEXT_SYMBOL_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Recursively write children of organism allele
        if (rawNucleicAcid != null)
        {
            rawNucleicAcid.writeToStream(stream);
        }

        // End object
        stream.println("</" + Elements.ORGANISM_ALLELE_ELEMENT_NAME + ">");
    }
}

