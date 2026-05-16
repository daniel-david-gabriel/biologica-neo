//
// Class : NucleicAcid
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2002/10/16 18:27:32 $
// $Author: qliao $
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

/**
 * This class represents a container nucleic acid, which is little more than
 * a collection of other nucleic acids, some of which are alleles, some
 * algorithmic sequences, some containers, etc.<p>
 *
 * This class may be used for both DNA and RNA.<p>
 *
 * This class hides the different types of nucleic acids from casual users
 * of the class, creating children of the various types as needed and storing
 * them in a vector.  For example, an autosome just needs to store a reference
 * to an instance of this class and doesn't need to worry about what form of
 * nucleic acid object is stored within this object (raw, etc.).<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.IN_DNA_OR_RNA - whether this is in DNA or RNA changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.NUCLEIC_ACID_ADDED - a nucleic acid was added
 * <li> EngineProp.NUCLEIC_ACID_REMOVED - a nucleic acid was removed
 * <li> EngineProp.START_INDEX_IN_HOLDER - start index in holder changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#IN_DNA_OR_RNA
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#NUCLEIC_ACID_ADDED
 * @see org.concord.biologica.engine.EngineProp#NUCLEIC_ACID_REMOVED
 * @see org.concord.biologica.engine.EngineProp#START_INDEX_IN_HOLDER
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2002/10/16 18:27:32 $
 * @author 		$Author: qliao $
**/

public class NucleicAcid
extends EngineObject
implements INucleicAcid, INucleicAcidHolder, Serializable
{
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Holder of this nucleic acid.  Never null.<p>
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
     * Vector of child INucleicAcid objects.  Never null.<p>
     *
     * All the objects on this vector must be instances of INucleicAcid.<p>
     *
     * When an INucleicAcid object is created and added to this vector,
     * a EngineProp.NUCLEIC_ACID_ADDED property change event is fired.<p>
     *
     * When an INucleicAcid object is deleted and removed from this vector,
     * a EngineProp.NUCLEIC_ACID_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.INucleicAcid
     * @see		org.concord.biologica.engine.EngineProp#NUCLEIC_ACID_ADDED
     * @see		org.concord.biologica.engine.EngineProp#NUCLEIC_ACID_REMOVED
    **/
    private Vector				nucleicAcids;

    /**
     * Creates a new nucleic acid from the given base values.<p>
     *
     * @param		aHolder INucleicAcidHolder - holder of this nucleic acid, may not be null
     * @param		startIndexInHolder int - start index of this nucleic acid in holder
     * @param		baseValues byte[] - base values array, may not be null or zero length
     * @param		inDNAorRNA int - in DNA or RNA?, must be Base.IN_DNA or Base.IN_RNA
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public NucleicAcid(INucleicAcidHolder aHolder, int startIndexInHolder,
                       byte[] baseValues, int inDNAorRNA)
    {
        // Do coarse checks of input values
        if (aHolder == null ||
            startIndexInHolder < 0 ||
            (baseValues == null || baseValues.length == 0) ||
            (inDNAorRNA != Base.IN_DNA && inDNAorRNA != Base.IN_RNA))
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input seems ok, initialize instance variables
        this.startIndexInHolder = startIndexInHolder;
        this.inDNAorRNA = inDNAorRNA;

        // Create a raw nucleic acid to hold base values
        // and create the nucleicAcids vector to hold it.
        RawNucleicAcid rawNA = new RawNucleicAcid(this, 0, baseValues, inDNAorRNA);
        nucleicAcids = new Vector();
        nucleicAcids.addElement(rawNA);

        // Creation successful, add this nucleic acid to holder
        holder = aHolder;
        holder.addNucleicAcid(this);
    }

    /**
     * Creates a new nucleic acid from the given vector of nucleic acids,
     * which is handy when the vector is created during meiosis.<p>
     *
     * @param		aHolder INucleicAcidHolder - holder of this nucleic acid, may not be null
     * @param		startIndexInHolder int - start index of this nucleic acid in holder
     * @param		nucleicAcids Vector - vector of existing nucleic acids, may be null or empty
     * @param		inDNAorRNA int - in DNA or RNA?, must be Base.IN_DNA or Base.IN_RNA
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public NucleicAcid(INucleicAcidHolder aHolder, int startIndexInHolder,
                       Vector nucleicAcids, int inDNAorRNA)
    {
        // Do coarse checks of input values
        if (aHolder == null ||
            startIndexInHolder < 0 ||
            (inDNAorRNA != Base.IN_DNA && inDNAorRNA != Base.IN_RNA))
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input seems ok, so initialize instance variables
        this.inDNAorRNA = inDNAorRNA;
        this.startIndexInHolder = startIndexInHolder;

        // Check nucleic acids
        if (nucleicAcids != null)
        {
            // TBD - may need to tweak this to get it right,
            // as clone'ing may not set children correctly.
            this.nucleicAcids = (Vector) nucleicAcids.clone();
        }
        else
        {
            this.nucleicAcids = null;
        }
        
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
        INucleicAcid aNucleicAcid;
        Vector nucleicAcidsClone = (Vector) nucleicAcids.clone();
        Enumeration eNucleicAcids = nucleicAcidsClone.elements();
        //Enumeration eNucleicAcids = nucleicAcids.elements();
        while (eNucleicAcids.hasMoreElements())
        {
            aNucleicAcid = (INucleicAcid) eNucleicAcids.nextElement();
            aNucleicAcid.delete(notifyChange);
        }
        nucleicAcids.removeAllElements();
        nucleicAcids = null;
        nucleicAcidsClone = null;

        // Remove from holder
        holder.removeNucleicAcid(this);
        holder = null;

        id = EngineObject.NULL_ID;
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
        INucleicAcid aNucleicAcid;
        Enumeration eNucleicAcids = nucleicAcids.elements();
        while (eNucleicAcids.hasMoreElements())
        {
            aNucleicAcid = (INucleicAcid) eNucleicAcids.nextElement();
            aNucleicAcid.setAutomaticLocked(automaticLocked);
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
        INucleicAcid aNucleicAcid;
        Enumeration eNucleicAcids = nucleicAcids.elements();
        while (eNucleicAcids.hasMoreElements())
        {
            aNucleicAcid = (INucleicAcid) eNucleicAcids.nextElement();
            aNucleicAcid.setManualLocked(manualLocked);
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
        INucleicAcid aNucleicAcid;
        Enumeration eNucleicAcids = nucleicAcids.elements();
        while (eNucleicAcids.hasMoreElements())
        {
            aNucleicAcid = (INucleicAcid) eNucleicAcids.nextElement();
            aNucleicAcid.setLockedState(lockedState);
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
        return new String(EngineStrings.NUCLEIC_ACID_COLON + id);
    }

    /**
     * Returns the length of the nucleic acid by summing
     * up the lengths of all of the child nucleic acids.<p>
     *
     * @return		int - length of this acid in bases, may be zero
    **/
    public int getLengthInBases()
    {
        // Length is zero if no nucleic acid children.
        if (nucleicAcids == null)
        {
            return 0;
        }

        // Recurse through children of this nucleic acid,
        // summing their lengths.
        int lengthInBases = 0;
        INucleicAcid na;
        Enumeration e = nucleicAcids.elements();

        while (e.hasMoreElements())
        {
            na = (INucleicAcid) e.nextElement();
            lengthInBases = lengthInBases + na.getLengthInBases();
        }

        return lengthInBases;
    }

    /**
     * Returns the length of the nucleic acid in codons by summing
     * up the lengths in codons of all of the child nucleic acids.<p>
     *
     * @return		int - length of this acid in codons, may be zero
    **/
    public int getLengthInCodons()
    {
        // Length is zero if no nucleic acid children.
        if (nucleicAcids == null)
        {
            return 0;
        }

        // Recurse through children of this nucleic acid,
        // summing their lengths.
        int lengthInCodons = 0;
        INucleicAcid na;
        Enumeration e = nucleicAcids.elements();

        while (e.hasMoreElements())
        {
            na = (INucleicAcid) e.nextElement();
            lengthInCodons = lengthInCodons + na.getLengthInCodons();
        }

        return lengthInCodons;
    }

    /**
     * Returns the base value at the given index in this sequence.  Note that
     * this value is along one strand.  If the value of the other strand is
     * desired, use Base.getPairBase(). <p>
     *
     * @see			org.concord.biologica.engine.Base#getPairBase
     *
     * @param		index int - index into acid (in bases), must be 0 to length of acid in bases-1
     * @return		byte - base value at given index
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public byte getBase(int index)
    {
        // Cannot find base if nucleicAcids is null
        if (nucleicAcids == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
    
        // Recurse through children of this nucleic acid
        // until we find a child which contains the index.
        int lengthChild = 0;
        int indexIntoChild = index;
        INucleicAcid na;
        Enumeration e = nucleicAcids.elements();
    
        while (e.hasMoreElements())
        {
            na = (INucleicAcid) e.nextElement();
            
            lengthChild = na.getLengthInBases();

            if (lengthChild > index)
            {
                return na.getBase(indexIntoChild);
            }
            else
            {
                indexIntoChild = indexIntoChild - lengthChild;
            }
        }

        // If we've gotten to here, then index was greater than length of acid
        throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
    }

    /**
     * Returns the start index of this nucleic acid in its holder.<p>
     *
     * @return		int - index in holder
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
     * Adds a nucleic acid to the holder.<p>
     *
     * This SHOULD be package protected because this is only to be called
     * from the nucleic acid's constructor.  Creating a nucleic acid
     * automatically adds it to the holder via this method.<p>
     *
     * BUT, Java has some wierd idea that interface methods are always
     * public, so I can't make this package protected.  So PLEASE don't
     * use them method from outside of the engine package!!<p>
     *
     * @param		aNucleicAcid INucleicAcid - a new nucleic acid, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void addNucleicAcid(INucleicAcid aNucleicAcid)
    {
        if (aNucleicAcid == null ||
            nucleicAcids == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        nucleicAcids.addElement(aNucleicAcid);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.NUCLEIC_ACID_ADDED,null,aNucleicAcid);
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
        // Return immediately if aNucleicAcid or nucleicAcids null
        if (aNucleicAcid == null || nucleicAcids == null)
        {
            return false;
        }

        boolean result = nucleicAcids.removeElement(aNucleicAcid);

        // Notify listeners if nucleic acid removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.NUCLEIC_ACID_REMOVED,null,aNucleicAcid);
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
        stream.println("<" + Elements.NUCLEIC_ACID_ELEMENT_NAME + ">");

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

        // In DNA or RNA
        stream.println("<" + Elements.IN_DNA_OR_RNA_ELEMENT_NAME + ">" +
                          Base.getInDNAorRNAasString(inDNAorRNA) +
                          "</" + Elements.IN_DNA_OR_RNA_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // End object
        stream.println("</" + Elements.NUCLEIC_ACID_ELEMENT_NAME + ">");

        // Recursively write children of nucleic acid
        {
            INucleicAcid aNucleicAcid;
            Enumeration eNucleicAcids = nucleicAcids.elements();
            while (eNucleicAcids.hasMoreElements())
            {
                aNucleicAcid = (INucleicAcid) eNucleicAcids.nextElement();
                aNucleicAcid.writeToStream(stream);
            }
        }
    }
}
