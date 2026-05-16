//
// Class : AminoAcid
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

import java.io.Serializable;

import java.lang.IllegalArgumentException;
import java.lang.String;

/**
 * This class represents an amino acid object.<p>
 *
 * This class contains mainly static methods.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:11 $
 * @author 		$Author: ed $
**/

public final class AminoAcid
{
	/**
	 * Alanine
	**/
	public static final byte ALANINE		= 1;
	
	/**
	 * An abbreviation for Alanine
	**/
	public static final byte ALA			= ALANINE;

	/**
	 * Arginine
	**/
	public static final byte ARGININE		= 2;
	
	/**
	 * An abbreviation for Arginine
	**/
	public static final byte ARG			= ARGININE;

	/**
	 * Asparagine
	**/
	public static final byte ASPARAGINE		= 3;
	
	/**
	 * An abbreviation for Asparagine
	**/
	public static final byte ASN			= ASPARAGINE;

	/**
	 * Aspartic acid
	**/
	public static final byte ASPARTIC_ACID	= 4;
	
	/**
	 * An abbreviation for Aspartic acid
	**/
	public static final byte ASP			= ASPARTIC_ACID;

	/**
	 * Cysteine
	**/
	public static final byte CYSTEINE		= 5;
	
	/**
	 * An abbreviation for Cysteine
	**/
	public static final byte CYS			= CYSTEINE;

	/**
	 * Glutamine
	**/
	public static final byte GLUTAMINE		= 6;

	/**
	 * An abbreviation for Glutamine
	**/
	public static final byte GLN			= GLUTAMINE;

	/**
	 * Glutamic acid
	**/
	public static final byte GLUTAMIC_ACID	= 7;

	/**
	 * An abbreviation for Glutamic acid
	**/
	public static final byte GLU			= GLUTAMIC_ACID;

	/**
	 * Glycine
	**/
	public static final byte GLYCINE		= 8;

	/**
	 * An abbreviation for Glycine
	**/
	public static final byte GLY			= GLYCINE;

	/**
	 * Histidine
	**/
	public static final byte HISTIDINE		= 9;

	/**
	 * An abbreviation for Histidine
	**/
	public static final byte HIS			= HISTIDINE;

	/**
	 * Isoleucine
	**/
	public static final byte ISOLEUCINE		= 10;

	/**
	 * An abbreviation for Isoleucine
	**/
	public static final byte ILE			= ISOLEUCINE;

	/**
	 * Leucine
	**/
	public static final byte LEUCINE		= 11;

	/**
	 * An abbreviation for Leucine
	**/
	public static final byte LEU			= LEUCINE;

	/**
	 * Lysine
	**/
	public static final byte LYSINE			= 12;

	/**
	 * An abbreviation for Lysine
	**/
	public static final byte LYS			= LYSINE;

	/**
	 * Methionine
	**/
	public static final byte METHIONINE		= 13;

	/**
	 * An abbreviation for Methionine
	**/
	public static final byte MET			= METHIONINE;

	/**
	 * Phenylalanine
	**/
	public static final byte PHENYLALANINE	= 14;

	/**
	 * An abbreviation for Phenylalanine
	**/
	public static final byte PHE			= PHENYLALANINE;

	/**
	 * Proline
	**/
	public static final byte PROLINE		= 15;

	/**
	 * An abbreviation for Proline
	**/
	public static final byte PRO			= PROLINE;

	/**
	 * Serine
	**/
	public static final byte SERINE			= 16;

	/**
	 * An abbreviation for Serine
	**/
	public static final byte SER			= SERINE;

	/**
	 * Threonine
	**/
	public static final byte THREONINE		= 17;

	/**
	 * An abbreviation for Threonine
	**/
	public static final byte THR			= THREONINE;

	/**
	 * Tryptophan
	**/
	public static final byte TRYPTOPHAN		= 18;

	/**
	 * An abbreviation for Tryptophan
	**/
	public static final byte TRP			= TRYPTOPHAN;

	/**
	 * Tyrosine
	**/
	public static final byte TYROSINE		= 19;

	/**
	 * An abbreviation for Tyrosine
	**/
	public static final byte TYR			= TYROSINE;

	/**
	 * Valine
	**/
	public static final byte VALINE			= 20;

	/**
	 * An abbreviation for Valine
	**/
	public static final byte VAL			= VALINE;

	/**
	 * Determines if an amino acid value is valid.<p>
	 *
	 * @param		anAminoAcid byte - an amino acid value
	 * @return		boolean - true if anAminoAcid is valid or false if not valid
	**/
	public static boolean isValidAminoAcid(byte anAminoAcid)
	{
		// Take advantage of the known range of values for performance reasons
		if (anAminoAcid > 0 && anAminoAcid < 21)
		{
			return true;
		}

		return false;
	}
}

