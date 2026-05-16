//
// Class : INucleicAcidHolder
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

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.io.PrintWriter;

import org.xml.sax.SAXException;

/**
 * This interface presents the API for a holder of nucleic acid.<p>
 *
 * Examples of such holders include alleles, other nucleic acids, chromosomes, etc.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public interface INucleicAcidHolder
{
    /**
     * Returns the ID of this object.  Duplicate of the method in Engine.<p>
     *
     * @return		int - id of this object
    **/
    abstract public int getID();

    /**
     * Adds a nucleic acid to the holder.<p>
     *
     * Package protected because this is only called from the nucleic acid's
     * constructor.  Creating a nucleic acid automatically adds it to the
     * holder via this method.<p>
     *
     * @param		aNucleicAcid INucleicAcid - a new nucleic acid, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    abstract void addNucleicAcid(INucleicAcid aNucleicAcid);

    /**
     * Removes a nucleic acid from the holder.<p>
     *
     * Package protected because this is only called from the nucleic acid's
     * delete method.  Deleting a nucleic acid automatically removes it from
     * the holder via this method.<p>
     *
     * @param		aNucleicAcid INucleicAcid - a nucleic acid, may not be null
     * @return		boolean indicating whether or not the nucleic acid was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    abstract boolean removeNucleicAcid(INucleicAcid aNucleicAcid);

    /**
     * Returns the world containing this holder
     *
     * @return		World - world containing this object
    **/
    abstract public World getWorld();

    /**
     * Handle notification that the parsing of the given element has ended.
     *
     * @param     anElementName String - the element name
    **/
    abstract public void endElement(String anElementName)
    throws SAXException;
}

