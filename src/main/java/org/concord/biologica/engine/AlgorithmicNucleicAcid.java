//
// Class : AlgorithmicNucleicAcid
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:11 $
// $Author: ed $
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

import java.util.Random;
import java.util.Calendar;

/**
 * This class represents a nucleic acid whose bases are algorithmically
 * and randomly generated.  The acid is guaranteed to have a length
 * which is a multiple of 3 bases.  It is NOT guaranteed that the acid
 * will not contain start and stop codons.  Nor is it guaranteed that
 * the acid will not contain a promoter region, although the odds of it
 * having one are very low.<p>
 *
 * We do this by forcing the creator of this object to specify the
 * length, in codons, of the nucleic acid.  Then we generate a random
 * seed and use that random seed to choose from the finite set of
 * possible codons (excluding start and stop codons) for the nucleic acids.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.IN_DNA_OR_RNA - whether this is in DNA or RNA changed
 * <li> EngineProp.LENGTH_IN_BASES - length in bases changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.SEED - seed changed, so all values changed
 * <li> EngineProp.START_INDEX_IN_HOLDER - start index in holder changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#IN_DNA_OR_RNA
 * @see org.concord.biologica.engine.EngineProp#LENGTH_IN_BASES
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#SEED
 * @see org.concord.biologica.engine.EngineProp#START_INDEX_IN_HOLDER
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:11 $
 * @author 		$Author: ed $
**/

public final class AlgorithmicNucleicAcid
extends EngineObject
implements INucleicAcid, Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Holder of this nucleic acid. Read-only. Never null. <p>
     *
     * No notification occurs for this property, but when this object
     * is created and added to a holder, the holder will generate a
     * property change event for a new nucleic acid.<p>
    **/
    private INucleicAcidHolder	holder;

    /**
     * Index of this nucleic acid in its holder (e.g. index of the
     * first base in the overall sequence of bases in the holder).<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.START_INDEX_IN_HOLDER.<p>
    **/
    private int					startIndexInHolder;

    /**
     * Length of this nucleic acid in bases (not in codons).<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.LENGTH_IN_BASES.<p>
    **/
    private	int					lengthInBases;

    /**
     * Random seed value, used to generate base values.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.SEED.<p>
    **/
    private long				seed;

    /**
     * Random number generator, created using seed.<p>
     *
     * Transient property, so no get and set methods and
     * no events are generated for it.<p>
    **/
    private transient Random	randomNumberGenerator;

    /**
     * Is this nucleic acid in DNA or RNA? Must have a value of
     * Base.IN_DNA or Base.IN_RNA.  Read-only.<p>
     *
     * When this property changes, a property change event is
     * generated for the property named EngineProp.IN_DNA_OR_RNA.<p>
     *
     * @see			org.concord.biologica.engine.Base#IN_DNA
     * @see			org.concord.biologica.engine.Base#IN_RNA
    **/
    private int					inDNAorRNA;

    /**
     * Create a new algorithmic nucleic acid given a length in codons.<p>
     *
     * A random seed is generated and saved with this nucleic acid for
     * use in generating base values.<p>
     *
     * @param		aHolder INucleicAcidHolder - holder of this nucleic acid (e.g. allele), may not be null
     * @param		startIndexInHolder int - index of the raw nucleic acid in its holder
     * @param		lengthInBases int - length of nucleic acid in bases, must be a multiple of 3
     * @param		inDNAorRNA int - indicates if this nucleic acid is in RNA or DNA (Base.IN_DNA or Base.IN_RNA)
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public AlgorithmicNucleicAcid(INucleicAcidHolder aHolder, int startIndexInHolder,
                                  int lengthInBases, int inDNAorRNA)
    {
        // Do coarse checks of input values
        if (aHolder == null ||
            startIndexInHolder < 0 ||
            (lengthInBases <= 0 || (lengthInBases % 3) != 0) ||
            (!(inDNAorRNA == Base.IN_DNA || inDNAorRNA == Base.IN_RNA)))
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments OK
        this.lengthInBases = lengthInBases;
        this.startIndexInHolder = startIndexInHolder;
        this.inDNAorRNA = inDNAorRNA;
        lockedState = EngineObject.UNLOCKED;

        // Use the current time as the seed for random number generator
        Calendar currentCalendar = Calendar.getInstance();
        seed = currentCalendar.get(Calendar.MILLISECOND);
        randomNumberGenerator = new Random(seed);
        
        // Creation successful, add this nucleic acid to holder
        holder = aHolder;
        holder.addNucleicAcid(this);
    }

    /**
     * Create a new algorithmic nucleic acid given another algorithmic nucleic acid to copy.<p>
     *
     * @param		aHolder INucleicAcidHolder - holder of this nucleic acid (e.g. allele), may not be null
     * @param		startIndexInHolder int - index of the raw nucleic acid in its holder
     * @param		anAlgorithmicNucleicAcid AlgorithmicNucleicAcid - nucleic acid to copy, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public AlgorithmicNucleicAcid(INucleicAcidHolder aHolder, int startIndexInHolder,
                                  AlgorithmicNucleicAcid anAlgorithmicNucleicAcid)
    {
        // Do coarse checks of input values
        if (aHolder == null ||
            startIndexInHolder < 0 ||
            anAlgorithmicNucleicAcid == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        this.lengthInBases = anAlgorithmicNucleicAcid.lengthInBases;
        this.inDNAorRNA = anAlgorithmicNucleicAcid.inDNAorRNA;
        this.startIndexInHolder = anAlgorithmicNucleicAcid.startIndexInHolder;
        lockedState = EngineObject.UNLOCKED;

        this.seed = anAlgorithmicNucleicAcid.seed;
        this.randomNumberGenerator = new Random(this.seed);
        
        // Creation successful, add this nucleic acid to holder
        holder = aHolder;
        holder.addNucleicAcid(this);
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
        lengthInBases = 0;
        seed = 0;
        randomNumberGenerator = null;

        // Remove from holder
        holder.removeNucleicAcid(this);
        holder = null;
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
        return new String(EngineStrings.ALGORITHMIC_NUCLEIC_ACID_COLON + id);
    }

    /**
     * Returns the raw nucleic acid's holder.<p>
     *
     * @return		INucleicAcidHolder - raw nucleic acid's holder, never null
    **/
    public INucleicAcidHolder getHolder()
    {
        return holder;
    }

    /**
     * Returns the world containing this nucleic acid.<p>
     *
     * @return		World - world containing this object
    **/
    public World getWorld()
    {
        return holder.getWorld();
    }

    /**
     * Returns the length of the algorithmic nucleic acid in bases.<p>
     *
     * @return		int - length of this nucleic acid in bases
    **/
    public int getLengthInBases()
    {
        return lengthInBases;
    }

    /**
     * Set the length in bases of this nucleic acid.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.LENGTH_IN_BASES.<p>
     *
     * @param	aLengthInBases int - new length in bases for object.
    **/
    public void setLengthInBases(int aLengthInBases)
    {
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
     * Returns the length of the algoritmic nucleic acid in codons.
     * If the number of bases doesn't divide evenly by 3, then the
     * remainder bases are ignored.  We do not round up in any sense.<p>
     *
     * @return		int - length of this nucleic acid in codons
    **/
    public int getLengthInCodons()
    {
        return lengthInBases * 3;
    }

    /**
     * Returns the seed value of this algorithmic nucleic acid.<p>
     *
     * @return		long - seed value of this nucleic acid
    **/
    public long getSeed()
    {
        return seed;
    }

    /**
     * Set the seed of this nucleic acid.  This should be done
     * only very carefully, as it will change the base values.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.LENGTH_IN_BASES.<p>
     *
     * @param	aSeed long - new seed
    **/
    public void setSeed(long aSeed)
    {
        if (seed != aSeed)
        {
            long oldSeed = seed;
            seed = aSeed;
            changes.firePropertyChange(EngineProp.SEED,
                                       new Integer((int) oldSeed),
                                       new Integer((int) seed));
        }
    }

    /**
     * Returns the base value at the given index in this raw nucleic acid.
     * Note that this value is along one strand.  If the value of the other
     * strand is desired, use Base.getPairBase(). <p>
     *
     * This base is calculated by creating a new seed based on the given index,
     * getting the first random number using that seed and then masking out
     * all but the 2 least significant digits to get a number between 0 and 3
     * to use to choose a base value.<p>
     *
     * This isn't a great algorithm, as it's not clear that we'll get truly
     * random sequences of base values.  But it's a fine start for now
     * and we'll improve it as necessary in the future.<p>
     *
     * @see			org.concord.biologica.engine.Base#getPairBase
     *
     * @param		index int - index into autosome, must be 0 to length of autosome-1
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public byte getBase(int index)
    {
        // Don't check for null, assuming we've been careful in this class
        if (index >= 0 && index < lengthInBases)
        {
            // Create a new seed based on index (yechhy!)
            long newSeed = seed + (seed & index);

            // Set new seed
            randomNumberGenerator.setSeed(newSeed);

            // Get value, AND'ing with 3 to get just last 2 significant bits
            int baseValue = (randomNumberGenerator.nextInt() & 3);

            switch (baseValue)
            {
                case 0:
                    return Base.ADENINE;
                case 1:
                    return Base.CYTOSINE;
                case 2:
                    return Base.GUANINE;
                case 3:
                    if (inDNAorRNA == Base.IN_DNA)
                    {
                        return Base.THYMINE;
                    }
                    else if (inDNAorRNA == Base.IN_RNA)
                    {
                        return Base.URACIL;
                    }
            }

            throw new InternalEngineException(EngineStrings.BAD_BASE_VALUE + baseValue);
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
    }

    /**
     * Returns the start index of this nucleic acid in its holder.<p>
     *
     * @return		int - index in holder, in bases
    **/
    public int getStartIndexInHolder()
    {
        return startIndexInHolder;
    }

    /**
     * Set the start index of this nucleic acid in its holder.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.START_INDEX_IN_HOLDER.<p>
     *
     * @param		int - new index in holder, must be between 0 and the length in bases
    **/
    public void setStartIndexInHolder(int anIndex)
    {
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
     * Returns whether this nucleic acid is in DNA or RNA.<p>
     *
     * @see			org.concord.biologica.engine.Base#IN_DNA
     * @see			org.concord.biologica.engine.Base#IN_RNA
     * @return		int - in DNA or RNA (Base.IN_DNA or Base.IN_RNA)
    **/
    public int getInDNAOrRNA()
    {
        return inDNAorRNA;
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
        stream.println("<" + Elements.ALGORITHMIC_NUCLEIC_ACID_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Holder ID
        stream.println("<" + Elements.HOLDER_ID_ELEMENT_NAME + ">" + holder.getID() +
                          "</" + Elements.HOLDER_ID_ELEMENT_NAME + ">");

        // Start index in holder
        stream.println("<" + Elements.START_INDEX_IN_HOLDER_ELEMENT_NAME + ">" + startIndexInHolder +
                          "</" + Elements.START_INDEX_IN_HOLDER_ELEMENT_NAME + ">");

        // Length in bases
        stream.println("<" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">" + lengthInBases +
                          "</" + Elements.LENGTH_IN_BASES_ELEMENT_NAME + ">");

        // Seed
        stream.println("<" + Elements.SEED_ELEMENT_NAME + ">" + seed +
                          "</" + Elements.SEED_ELEMENT_NAME + ">");

        // In DNA or RNA
        stream.println("<" + Elements.IN_DNA_OR_RNA_ELEMENT_NAME + ">" +
                          Base.getInDNAorRNAasString(inDNAorRNA) +
                          "</" + Elements.IN_DNA_OR_RNA_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.ALGORITHMIC_NUCLEIC_ACID_ELEMENT_NAME + ">");
    }
}

