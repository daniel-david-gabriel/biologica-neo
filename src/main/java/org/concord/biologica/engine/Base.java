//
// Class : Base
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
 * This class represents a Base object, as in the base purines and
 * pyrimidines which form DNA and RNA.<p>
 *
 * This class contains mainly static methods.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:11 $
 * @author 		$Author: ed $
**/

public final class Base
{
	/**
	 * A pyrimidine which links with adenine only in RNA, not DNA.
	 * Don't change the value of this argument without being sure
	 * the array mapping codons to amino acids in Codon.java is okay.
	**/
	public static final byte URACIL		= 0;

	/**
	 * A purine which links with guanine in DNA and RNA.
	 * Don't change the value of this argument without being sure
	 * the array mapping codons to amino acids in Codon.java is okay.
	**/
	public static final byte CYTOSINE 	= 1;

	/**
	 * A purine which links with thymine in DNA and with uracil in RNA.
	 * Don't change the value of this argument without being sure
	 * the array mapping codons to amino acids in Codon.java is okay.
	**/
	public static final byte ADENINE	= 2;

	/**
	 * A pyrimidine which links with cytosine in DNA and RNA.
	 * Don't change the value of this argument without being sure
	 * the array mapping codons to amino acids in Codon.java is okay.
	**/
	public static final byte GUANINE 	= 3;

	/**
	 * A pyrimidine which links with adenine only in DNA, not RNA.
	**/
	public static final byte THYMINE 	= 4;

	/**
	 * Used in various methods to indicate a base or some
	 * nucleic acid is in DNA.<p>
	**/
	public static final int IN_DNA = 1;
	public static final String IN_DNA_STRING = "inDNA";

	/**
	 * Used in various methods to indicate a base or some
	 * nucleic acid is in RNA.<p>
	**/
	public static final int IN_RNA = 2;
	public static final String IN_RNA_STRING = "inRNA";

	/**
	 * Used in various methods to indicate a base or some
	 * nucleic acid is in DNA or RNA.<p>
	**/
	public static final int IN_DNA_OR_RNA = 3;
	
	/**
	 * Determines if a base value is valid.<p>
	 *
	 * @param		aBase byte - a base value
	 * @param		inDNAorRNA int - indicates if base in DNA or RNA is sought
	 * @return		boolean - true if aBase is valid or false if not valid
	**/
	public static boolean isValidBase(byte aBase, int inDNAorRNA)
	{
		if (aBase == ADENINE ||
			aBase == CYTOSINE ||
			aBase == GUANINE ||
			(aBase == THYMINE && (inDNAorRNA == IN_DNA || inDNAorRNA == IN_DNA_OR_RNA)) ||
			(aBase == URACIL && (inDNAorRNA == IN_RNA || inDNAorRNA == IN_DNA_OR_RNA)))
		{
			return true;
		}

		return false;
	}

	/**
	 * Get pair base value in either DNA or RNA.<p>
	 *
	 * @param		aBase byte - base value for which pair base is sought, must be a valid base value
	 * @param		inDNAorRNA int - indicates if pair base in DNA or RNA is sought
	 * @return		byte - pair base value
	 * @exception	IllegalArgumentException - input arguments not valid
	**/
	public static byte getPairBase(byte aBase, int inDNAorRNA)
	{
		if (inDNAorRNA == IN_DNA)
		{
			if (aBase == ADENINE) return THYMINE;
			else if (aBase == CYTOSINE) return GUANINE;
			else if (aBase == GUANINE) return CYTOSINE;
			else if (aBase == THYMINE) return ADENINE;
		}
		else if (inDNAorRNA == IN_RNA)
		{
			if (aBase == ADENINE) return URACIL;
			else if (aBase == CYTOSINE) return GUANINE;
			else if (aBase == GUANINE) return CYTOSINE;
			else if (aBase == URACIL) return ADENINE;
		}
		else if (inDNAorRNA == IN_DNA_OR_RNA)
		{
			if (aBase == CYTOSINE) return GUANINE;
			else if (aBase == GUANINE) return CYTOSINE;
			else if (aBase == THYMINE) return ADENINE;
			else if (aBase == URACIL) return ADENINE;

			// Fall through to exception if ADENINE, as we can't
			// tell if we should return THYMINE or URACIL, as the
			// caller hasn't specified DNA or RNA.
		}

		throw new IllegalArgumentException("input aBase " + aBase + " or inDNAorRNA " + inDNAorRNA + " illegal");
	}

	/**
	 * Convert an int inDNAorRNA value to a String.
	 *
	 * @return  String - in DNA or RNA as a string
	**/
	public static final String getInDNAorRNAasString(int inDNAorRNA)
	{
		if (inDNAorRNA == IN_DNA)
		{
			return IN_DNA_STRING;
		}
		else if (inDNAorRNA == IN_RNA)
		{
			return IN_RNA_STRING;
		}

		// Default to DNA
		return IN_DNA_STRING;
	}

	/**
	 * Convert an int inDNAorRNA value to a String.
	 *
	 * @return  String - in DNA or RNA as a string
	**/
	public static final int getInDNAorRNAasInt(String inDNAorRNA)
	{
		if (inDNAorRNA != null)
		{
			if (inDNAorRNA.equals(IN_DNA_STRING))
			{
				return IN_DNA;
			}
			else if (inDNAorRNA.equals(IN_RNA_STRING))
			{
				return IN_RNA;
			}
		}

		// Default to DNA
		return IN_DNA;
	}
}

