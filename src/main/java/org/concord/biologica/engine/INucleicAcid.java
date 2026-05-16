//
// Class : INucleicAcid
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:13 $
// $Author: ed $
//

package org.concord.biologica.engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.lang.IllegalArgumentException;
import java.lang.String;

/**
 * This interface presents the API for a nucleic acid, either
 * a full strand of DNA or RNA or just a segment of one.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public interface INucleicAcid
{
    /**
     * Delete this nucleic acid - redundant with EngineObject in most cases.<p>
    **/
    public abstract void delete();
    
    /**
     * Delete this nucleic acid - redundant with EngineObject in most cases.<p>
    **/
    public abstract void delete(boolean notifyChange);
    
    /**
     * Returns the nucleic acid's holder.<p>
     *
     * @return		INucleicAcidHolder - raw nucleic acid's holder, never null
    **/
    public abstract INucleicAcidHolder getHolder();
    
    /**
     * Returns the length of the nucleic acid in bases (vs. codons).<p>
     *
     * @return		int - length of this nucleic acid in bases (approx. codons * 3)
    **/
    public abstract int getLengthInBases();

    /**
     * Returns the length of the nucleic acid in codons (vs. bases).<p>
     *
     * @return		int - length of this nucleic acid in codons (bases % 3)
    **/
    public abstract int getLengthInCodons();

    /**
     * Returns the base value at the given index in this acid.  Note that
     * this value is along one strand.  If the value of the other strand is
     * desired, use Base.getPairBase(). <p>
     *
     * @see			org.concord.biologica.engine.Base#getPairBase
     *
     * @param		index int - index into acid, must be 0 to length of acid in bases-1
     * @return		byte - base value at given index
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public abstract byte getBase(int index);

    /**
     * Returns the start index of this nucleic acid in its holder.<p>
     *
     * @return		int - index in holder
    **/
    public abstract int getStartIndexInHolder();

    /**
     * Set the automatic locked state of this object.
    **/
    public abstract void setAutomaticLocked(boolean automaticLocked);

    /**
     * Set the manual locked state of this object.
    **/
    public abstract void setManualLocked(boolean manualLocked);

    /**
     * Set the lock state of the object.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED.<p>
     *
     * It's expected that most derived classes will override this method
     * and recursively lock their children.<p>
     *
     * @param	locked boolean - new locked state of this object
    **/
    public abstract void setLockedState(int lockedState);

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
    abstract public void writeToStream(PrintWriter stream)
    throws java.io.IOException;
}

