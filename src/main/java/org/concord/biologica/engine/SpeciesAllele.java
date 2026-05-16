//
// Class : SpeciesAllele
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
 * This class represents a species allele - a known, fixed variation of a species gene.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.BASE_VALUES - base values for allele's raw nucleic acid changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.DESCRIPTION - the object description has changed
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - the object's locked state has changed
 * <li> EngineProp.MUTATION_ALLELE - this is the allele used when there is a weird mutation
 * <li> EngineProp.TEXT_SYMBOL	- text symbol for allele changed
 * <li> EngineProp.VISIBLE - visible, typically used for mutation allele becoming visible when user edits DNA
 * <li> EngineProp.WEIGHT - probabalistic weight of this allele relative to other alleles of this gene
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#BASE_VALUES
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#DESCRIPTION
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#MUTATION_ALLELE
 * @see org.concord.biologica.engine.EngineProp#TEXT_SYMBOL
 * @see org.concord.biologica.engine.EngineProp#VISIBLE
 * @see org.concord.biologica.engine.EngineProp#WEIGHT
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/28 18:40:14 $
 * @author 		$Author: dima $
**/

public final class SpeciesAllele
extends EngineObject
implements INucleicAcidHolder, Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * The gene for which this is an allele. Read-only. Never null.<p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to a gene, the gene will generate a
     * vector property change event for a new allele.<p>
    **/
    private Gene				gene;

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
     * SpeciesAllele's raw nucleic acid.  May not be null.<p>
     *
     * When this property or the values in this nucleic acid are
     * changed, a property change event is generated for the
     * property named EngineProp.BASE_VALUES.<p>
    **/
    private RawNucleicAcid		rawNucleicAcid;

    /**
     * Weight of this allele in a probabilistic sense relative to
     * the other alleles of this allele's gene.  In other words,
     * when an organism is created the weights of all the alleles
     * of a gene are totalled and used to probabalistically give
     * alleles with a greater weight a higher probability of
     * occurring in the organism.  Must be zero or greater.
    **/
    private int					weight;

    /**
     * Description of this species.  May be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.DESCRIPTION.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#DESCRIPTION
    **/
    private String				description;

    /**
     * Mutation allele?  Typically a mutation allele should have
     * a weight of zero, meaning it never occurs in the wild and
     * only occurs when the user hand edits some DNA.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.MUTATION_ALLELE.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#MUTATION_ALLELE
    **/
    private boolean				mutationAllele;

    /**
     * Visible?<p>
     *
     * This is typically used when this allele is a mutation allele
     * that starts out as invisible and then becomes visible when
     * the user edits another organism allele directly to create a
     * mutation.  When the mutation is created, this allele would
     * become visible.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.VISIBLE.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#VISIBLE
    **/
    private boolean				visible;

    /**
     * Create a new allele given a gene and the base values to use.<p>
     *
     * @param		aGene Gene - the gene of this allele, may not be null
     * @param		aTextSymbol String - textual symbol of this allele (e.g. "h", "H"), may not be null
     * @param		baseValues byte[] - base values array, may not be null or zero length
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public SpeciesAllele(Gene aGene, String aTextSymbol, byte[] baseValues)
    {
        // Do coarse check of input values
        if (baseValues == null ||
            baseValues.length == 0 ||
            aGene == null ||
            aTextSymbol == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Input arguments and base values array OK, so initialize other instance variables

        // Create raw nucleic acid
        rawNucleicAcid = new RawNucleicAcid(this, 0, baseValues, Base.IN_DNA);

        textSymbol = new String(aTextSymbol);

        lockedState = EngineObject.UNLOCKED;

        // Default weight is 10
        weight = 10;

        // Default description is null
        description = null;

        // Default mutation allele is false
        mutationAllele = false;

        // Default visibility is true
        visible = true;

        // Tell the parent gene
        gene = aGene;
        gene.addSpeciesAllele(this);
    }
    
    /**
     * Create a new species allele and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aGene Gene - the enclosing gene for this new species allele
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public SpeciesAllele(Gene aGene,
                         String anElementName,
                         int anElementID,
                         /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                         ImportContext importContext)
    {
        if (aGene == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        weight = 10;
        description = null;
        lockedState = EngineObject.UNLOCKED;
        mutationAllele = false;
        visible = true;
        textSymbol = null;

        // Creation successful, so add this species allele to its gene
        gene = aGene;
        gene.addSpeciesAllele(this);

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
            case Elements.GENE_ID_ELEMENT_ID:
            case Elements.TEXT_SYMBOL_ELEMENT_ID:
            case Elements.WEIGHT_ELEMENT_ID:
            case Elements.DESCRIPTION_ELEMENT_ID:
            case Elements.MUTATION_ALLELE_ELEMENT_ID:
            case Elements.VISIBLE_ELEMENT_ID:
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

            case Elements.GENE_ID_ELEMENT_ID:
                // Ignore, as we already have the gene
                break;

            case Elements.TEXT_SYMBOL_ELEMENT_ID:
                textSymbol = xmlElementContext.getValueString();
                break;

            case Elements.WEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                weight = Integer.valueOf(valueString).intValue();
                break;

            case Elements.DESCRIPTION_ELEMENT_ID:
                description = xmlElementContext.getValueString();
                break;

            case Elements.MUTATION_ALLELE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                mutationAllele = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.VISIBLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                visible = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.RAW_NUCLEIC_ACID_ELEMENT_ID:
                // Done with a raw nucleic acid, so reclaim document handler
                break;

            case Elements.SPECIES_ALLELE_ELEMENT_ID:
                // Done with this species allele, so pop up to enclosing gene
                gene.endElement(anElementName);
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
        if (rawNucleicAcid != null)
        {
            rawNucleicAcid.delete();
            rawNucleicAcid = null;
        }

        // Remove from gene
        gene.removeSpeciesAllele(this);
        gene = null;

        // Forget text symbol and description
        textSymbol = null;
        description = null;

        id = EngineObject.NULL_ID;
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
        if (rawNucleicAcid != null)
        {
            rawNucleicAcid.setAutomaticLocked(automaticLocked);
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
        if (rawNucleicAcid != null)
        {
            rawNucleicAcid.setManualLocked(manualLocked);
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
        if (rawNucleicAcid != null)
        {
            rawNucleicAcid.setLockedState(lockedState);
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
        return EngineStrings.ALLELE_COLON + textSymbol;
    }

    /**
     * Is this allele the mutation allele for its gene?<p>
     *
     * @return	boolean - mutation allele for its gene?
    **/
    public boolean isMutationAllele()
    {
        return mutationAllele;
    }

    /**
     * Set whether or not this allele is the mutation allele for its gene.<p>
     *
     * @param	aMutationAllele boolean - is this allele the mutation allele for its gene?
    **/
    public void setMutationAllele(boolean aMutationAllele)
    {
        // Return immediately if no change
        if (mutationAllele == aMutationAllele)
        {
            return;
        }

        // Make change
        boolean oldMutationAllele = mutationAllele;
        mutationAllele = aMutationAllele;

        // Notify listeners
        changes.firePropertyChange(EngineProp.MUTATION_ALLELE,
                                   new Boolean(oldMutationAllele),
                                   new Boolean(mutationAllele));
    }

    /**
     * Is this allele visible?<p>
     *
     * @return	boolean - visible?
    **/
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Set whether or not this allele is visible.<p>
     *
     * @param	aVisible boolean - is this allele visible?
    **/
    public void setVisible(boolean aVisible)
    {
        // Return immediately if no change
        if (visible == aVisible)
        {
            return;
        }

        // Make change
        boolean oldVisible = visible;
        visible = aVisible;

        // Notify listeners
        changes.firePropertyChange(EngineProp.VISIBLE,
                                   new Boolean(oldVisible),
                                   new Boolean(visible));
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
     * @param		aTextSymbol String - new text symbol for object, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setTextSymbol(String aTextSymbol)
    {
        // Return immediately if aTextSymbol equals the current textSymbol
        if (aTextSymbol != null && textSymbol != null && aTextSymbol.equals(textSymbol))
        {
            return;
        }

        // Validate input arguments
        if (aTextSymbol == null)
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
        if (textSymbol != aTextSymbol)
        {
            String oldTextSymbol = textSymbol;
            textSymbol = aTextSymbol;
            changes.firePropertyChange(EngineProp.TEXT_SYMBOL,oldTextSymbol,textSymbol);
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
     * Get the species chromosome containing this object.<p>
     *
     * @return	SpeciesChromosome - the species chromosome containing this object, never null.
    **/
    public SpeciesChromosome getSpeciesChromosome()
    {
        return gene.getSpeciesChromosome();
    }

    /**
     * Get the species containing this object.<p>
     *
     * @return	Species - the species containing this object, never null.
    **/
    public Species getSpecies()
    {
        return gene.getSpecies();
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    public World getWorld()
    {
        return gene.getWorld();
    }

    /**
     * Returns the length of the allele in bases.<p>
     *
     * @return		int - length of this autosome in bases
    **/
    public int getLengthInBases()
    {
        // Don't check for null, assuming we've been careful in this class
        if (rawNucleicAcid != null)
        {
            return rawNucleicAcid.getLengthInBases();
        }

        return 0;
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
        if (rawNucleicAcid != null)
        {
            return rawNucleicAcid.getLengthInCodons();
        }

        return 0;
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
     * Get the bases as a String of characters A,T,C and G.
     *
     * @return		String - bases in a string using A,T,C and G characters
    **/
    public String getBasesAsString()
    {
        int length = rawNucleicAcid.getLengthInBases();

        if (length > 0)
        {
            char bases[] = new char[length];
    
            int i;
            for (i=0;i<length;i++)
            {
                switch (rawNucleicAcid.getBase(i))
                {
                    case Base.URACIL:
                        bases[i] = 'U';
                        break;
                    case Base.CYTOSINE:
                        bases[i] = 'C';
                        break;
                    case Base.ADENINE:
                        bases[i] = 'A';
                        break;
                    case Base.GUANINE:
                        bases[i] = 'G';
                        break;
                    case Base.THYMINE:
                        bases[i] = 'T';
                        break;
                    default:
                        bases[i] = '?';
                }
            }

            // Convert array of characters to a string to a String
            return new String(bases);
        }

        // Length was <= 0, so return an empty string
        return new String("");
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
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setBase(int index, byte newBase)
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

        rawNucleicAcid.setBase(index, newBase);

        changes.firePropertyChange(EngineProp.BASE_VALUES,null,null);
    }

    /**
     * Get the base values array for this allele.<p>
     *
     * @return    byte[] - base values as a byte array
    **/
    public byte[] getBases()
    {
        // Exception if this object is locked or deleted
        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        return rawNucleicAcid.getBases();
    }

    /**
     * Sets the base values array for this allele.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.BASE_VALUES.<p>
     *
     * @param		newBases byte[] - new base values array
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setBases(byte[] newBases)
    {
        // Exception if this object is locked or deleted
        // Do not throw an exception if this is a mutation allele with no bases specified
        // yet, as we allow the bases to be changed in that case.
        if (isLocked() == true && (isMutationAllele() == false || getLengthInBases() != 0))
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        rawNucleicAcid.setBases(newBases);

        changes.firePropertyChange(EngineProp.BASE_VALUES,null,null);
    }

    /**
     * Sets the bases for this allele using a String as input.  The String
     * must have character values 'A','a','C','c','G','g','T','t' or an
     * IllegalArgumentException will be thrown.<p>
     *
     * @param		String - bases in a string using A,T,C and G characters, may be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setBasesAsString(String bases)
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

        // If bases null, set rawNucleicAcid bases to null and return
        if (bases == null)
        {
            rawNucleicAcid.setBases(null);
            return;
        }

        // bases not null, so loop through bases forming a byte bases array
        int length = bases.length();
        if (length > 0)
        {
            byte baseBytes[] = new byte[length];
    
            int i;
            for (i=0;i<length;i++)
            {
                switch (bases.charAt(i))
                {
                    case 'C':
                    case 'c':
                        baseBytes[i] = Base.CYTOSINE;
                        break;
                    case 'A':
                    case 'a':
                        baseBytes[i] = Base.ADENINE;
                        break;
                    case 'G':
                    case 'g':
                        baseBytes[i] = Base.GUANINE;
                        break;
                    case 'T':
                    case 't':
                        baseBytes[i] = Base.THYMINE;
                        break;
                    default:
                        throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
                }
            }

            // Set bases in rawNucleicAcid
            rawNucleicAcid.setBases(baseBytes);
        }
        else
        {
            rawNucleicAcid.setBases(null);
        }
    }

    /**
     * Do the input bases match the bases of this allele?
     *
     * @param    inputBases byte[] - input bases
     * @return   boolean - true (they match) or false (they don't match)
    **/
    public boolean matchBases(byte[] inputBases)
    {
        if (inputBases == null || rawNucleicAcid == null)
        {
            return false;
        }

        byte[] myBases = rawNucleicAcid.getBases();

        if (myBases == null || myBases.length != inputBases.length)
        {
            return false;
        }

        int i;
        for (i=0;i<myBases.length;i++)
        {
            if (myBases[i] != inputBases[i])
            {
                return false;
            }
        }

        return true;
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
        return gene.getStartIndexInHolder();
    }

    /**
     * Get the raw nucleic acid in this allele.  This is not public
     * because it's only used by OrganismAllele's when creating an
     * instance of the nucleic acid in the organism allele.<p>
     *
     * @return		RawNucleicAcid - raw nucleic acid
    **/
    RawNucleicAcid getRawNucleicAcid()
    {
        return rawNucleicAcid;
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
        // Validate input arguments
        if ((!(aNucleicAcid instanceof RawNucleicAcid)) ||
            rawNucleicAcid != null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - make change
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
        // Validate input arguments
        if (aNucleicAcid != rawNucleicAcid)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change
        rawNucleicAcid = null;

        return true;
    }

    /**
     * Get the weight of this allele.
     *
     * @return		int - weight of this allele
    **/
    public int getWeight()
    {
        return weight;
    }

    /**
     * Set the weight of this allele.
     *
     * @param		aWeight int - new weight of this allele, must be zero or greater
     * @exception	IllegalArgumentException - weight less than zero
    **/
    public void setWeight(int aWeight)
    {
        if (aWeight < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (weight != aWeight)
        {
            int oldWeight = weight;
            weight = aWeight;
            changes.firePropertyChange(EngineProp.WEIGHT,
                                       new Integer(oldWeight),
                                       new Integer(weight));
        }
    }

    /**
     * Return the description of this allele.  May be null.<p>
     *
     * @return		String - description of this allele, may not be null.
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
     * Set the description of this allele.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.DESCRIPTION.<p>
     *
     * @param		aDescription String - the new description of this allele, may not be null
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
        stream.println("<" + Elements.SPECIES_ALLELE_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

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

        // Text symbol
        stream.println("<" + Elements.TEXT_SYMBOL_ELEMENT_NAME + ">" + textSymbol +
                          "</" + Elements.TEXT_SYMBOL_ELEMENT_NAME + ">");

        // Weight
        stream.println("<" + Elements.WEIGHT_ELEMENT_NAME + ">" + weight +
                          "</" + Elements.WEIGHT_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

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

        // Mutation allele
        stream.println("<" + Elements.MUTATION_ALLELE_ELEMENT_NAME + ">" + mutationAllele +
                          "</" + Elements.MUTATION_ALLELE_ELEMENT_NAME + ">");

        // Visible
        stream.println("<" + Elements.VISIBLE_ELEMENT_NAME + ">" + visible +
                          "</" + Elements.VISIBLE_ELEMENT_NAME + ">");

        // Recursively write children of organism allele
        if (rawNucleicAcid != null)
        {
            rawNucleicAcid.writeToStream(stream);
        }

        // End object
        stream.println("</" + Elements.SPECIES_ALLELE_ELEMENT_NAME + ">");
    }
}

