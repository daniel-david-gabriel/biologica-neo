//
// Class : Codon
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

import java.lang.IllegalArgumentException;
import java.lang.String;

/**
 * This class represents a Codon object, groups of 3 bases
 * which code for particular amino acids.<p>
 *
 * This class contains mainly static methods now, but will
 * have other methods in the future.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:11 $
 * @author 		$Author: ed $
**/

public final class Codon
{
	/**
	 * Amino acid table showing which amino acids are produced
	 * by which codons - 3 base combinations.  The table is
	 * a 4x4x4 array where the first index is the first base,
	 * the second index is the second base, etc.  The four
	 * bases are Base.URACIL, Base.CYTOSINE, Base.ADENINE and
	 * Base.GUANINE and we take advantage of the values of
	 * these base definitions (0,1,2,3) in Base.java here.<p>
	**/
	private static byte[][][]	codonToAminoAcidMap;

	/**
	 * Start codon.  This value MUST be different from any of
	 * the amino acid values in AminoAcid.java, as there's a
	 * reasonable chance they'll be used together and we must
	 * be able to distinguish them.<p>
	**/
	public static final byte START			= 21;

	/**
	 * Stop codon.  This value MUST be different from any of
	 * the amino acid values in AminoAcid.java, as there's a
	 * reasonable chance they'll be used together and we must
	 * be able to distinguish them.<p>
	**/
	public static final byte STOP			= 22;

	/**
	 * Static code which initializes the above array.
	**/
	static
	{
		codonToAminoAcidMap = new byte[4][4][4];

		// UUx
		codonToAminoAcidMap[Base.URACIL][Base.URACIL][Base.URACIL] = AminoAcid.PHENYLALANINE;
		codonToAminoAcidMap[Base.URACIL][Base.URACIL][Base.CYTOSINE] = AminoAcid.PHENYLALANINE;
		codonToAminoAcidMap[Base.URACIL][Base.URACIL][Base.ADENINE] = AminoAcid.LEUCINE;
		codonToAminoAcidMap[Base.URACIL][Base.URACIL][Base.GUANINE] = AminoAcid.LEUCINE;

		// UCx
		codonToAminoAcidMap[Base.URACIL][Base.CYTOSINE][Base.URACIL] = AminoAcid.SERINE;
		codonToAminoAcidMap[Base.URACIL][Base.CYTOSINE][Base.CYTOSINE] = AminoAcid.SERINE;
		codonToAminoAcidMap[Base.URACIL][Base.CYTOSINE][Base.ADENINE] = AminoAcid.SERINE;
		codonToAminoAcidMap[Base.URACIL][Base.CYTOSINE][Base.GUANINE] = AminoAcid.SERINE;

		// UAx
		codonToAminoAcidMap[Base.URACIL][Base.ADENINE][Base.URACIL] = AminoAcid.TYROSINE;
		codonToAminoAcidMap[Base.URACIL][Base.ADENINE][Base.CYTOSINE] = AminoAcid.TYROSINE;
		codonToAminoAcidMap[Base.URACIL][Base.ADENINE][Base.ADENINE] = Codon.STOP;
		codonToAminoAcidMap[Base.URACIL][Base.ADENINE][Base.GUANINE] = Codon.STOP;

		// UGx
		codonToAminoAcidMap[Base.URACIL][Base.GUANINE][Base.URACIL] = AminoAcid.CYSTEINE;
		codonToAminoAcidMap[Base.URACIL][Base.GUANINE][Base.CYTOSINE] = AminoAcid.CYSTEINE;
		codonToAminoAcidMap[Base.URACIL][Base.GUANINE][Base.ADENINE] = Codon.STOP;
		codonToAminoAcidMap[Base.URACIL][Base.GUANINE][Base.GUANINE] = AminoAcid.TRYPTOPHAN;

		// CUx
		codonToAminoAcidMap[Base.CYTOSINE][Base.URACIL][Base.URACIL] = AminoAcid.LEUCINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.URACIL][Base.CYTOSINE] = AminoAcid.LEUCINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.URACIL][Base.ADENINE] = AminoAcid.LEUCINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.URACIL][Base.GUANINE] = AminoAcid.LEUCINE;

		// CCx
		codonToAminoAcidMap[Base.CYTOSINE][Base.CYTOSINE][Base.URACIL] = AminoAcid.PROLINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.CYTOSINE][Base.CYTOSINE] = AminoAcid.PROLINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.CYTOSINE][Base.ADENINE] = AminoAcid.PROLINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.CYTOSINE][Base.GUANINE] = AminoAcid.PROLINE;

		// CAx
		codonToAminoAcidMap[Base.CYTOSINE][Base.ADENINE][Base.URACIL] = AminoAcid.HISTIDINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.ADENINE][Base.CYTOSINE] = AminoAcid.HISTIDINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.ADENINE][Base.ADENINE] = AminoAcid.GLUTAMINE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.ADENINE][Base.GUANINE] = AminoAcid.GLUTAMINE;

		// CGx
		codonToAminoAcidMap[Base.CYTOSINE][Base.GUANINE][Base.URACIL] = AminoAcid.ARGININE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.GUANINE][Base.CYTOSINE] = AminoAcid.ARGININE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.GUANINE][Base.ADENINE] = AminoAcid.ARGININE;
		codonToAminoAcidMap[Base.CYTOSINE][Base.GUANINE][Base.GUANINE] = AminoAcid.ARGININE;

		// AUx
		codonToAminoAcidMap[Base.ADENINE][Base.URACIL][Base.URACIL] = AminoAcid.ISOLEUCINE;
		codonToAminoAcidMap[Base.ADENINE][Base.URACIL][Base.CYTOSINE] = AminoAcid.ISOLEUCINE;
		codonToAminoAcidMap[Base.ADENINE][Base.URACIL][Base.ADENINE] = AminoAcid.ISOLEUCINE;
		codonToAminoAcidMap[Base.ADENINE][Base.URACIL][Base.GUANINE] = AminoAcid.METHIONINE;

		// ACx
		codonToAminoAcidMap[Base.ADENINE][Base.CYTOSINE][Base.URACIL] = AminoAcid.THREONINE;
		codonToAminoAcidMap[Base.ADENINE][Base.CYTOSINE][Base.CYTOSINE] = AminoAcid.THREONINE;
		codonToAminoAcidMap[Base.ADENINE][Base.CYTOSINE][Base.ADENINE] = AminoAcid.THREONINE;
		codonToAminoAcidMap[Base.ADENINE][Base.CYTOSINE][Base.GUANINE] = AminoAcid.THREONINE;

		// AAx
		codonToAminoAcidMap[Base.ADENINE][Base.ADENINE][Base.URACIL] = AminoAcid.ASPARAGINE;
		codonToAminoAcidMap[Base.ADENINE][Base.ADENINE][Base.CYTOSINE] = AminoAcid.ASPARAGINE;
		codonToAminoAcidMap[Base.ADENINE][Base.ADENINE][Base.ADENINE] = AminoAcid.LYSINE;
		codonToAminoAcidMap[Base.ADENINE][Base.ADENINE][Base.GUANINE] = AminoAcid.LYSINE;

		// AGx
		codonToAminoAcidMap[Base.ADENINE][Base.GUANINE][Base.URACIL] = AminoAcid.SERINE;
		codonToAminoAcidMap[Base.ADENINE][Base.GUANINE][Base.CYTOSINE] = AminoAcid.SERINE;
		codonToAminoAcidMap[Base.ADENINE][Base.GUANINE][Base.ADENINE] = AminoAcid.ARGININE;
		codonToAminoAcidMap[Base.ADENINE][Base.GUANINE][Base.GUANINE] = AminoAcid.ARGININE;

		// GUx
		codonToAminoAcidMap[Base.GUANINE][Base.URACIL][Base.URACIL] = AminoAcid.VALINE;
		codonToAminoAcidMap[Base.GUANINE][Base.URACIL][Base.CYTOSINE] = AminoAcid.VALINE;
		codonToAminoAcidMap[Base.GUANINE][Base.URACIL][Base.ADENINE] = AminoAcid.VALINE;
		codonToAminoAcidMap[Base.GUANINE][Base.URACIL][Base.GUANINE] = AminoAcid.VALINE;

		// GCx
		codonToAminoAcidMap[Base.GUANINE][Base.CYTOSINE][Base.URACIL] = AminoAcid.ALANINE;
		codonToAminoAcidMap[Base.GUANINE][Base.CYTOSINE][Base.CYTOSINE] = AminoAcid.ALANINE;
		codonToAminoAcidMap[Base.GUANINE][Base.CYTOSINE][Base.ADENINE] = AminoAcid.ALANINE;
		codonToAminoAcidMap[Base.GUANINE][Base.CYTOSINE][Base.GUANINE] = AminoAcid.ALANINE;

		// GAx
		codonToAminoAcidMap[Base.GUANINE][Base.ADENINE][Base.URACIL] = AminoAcid.ASPARTIC_ACID;
		codonToAminoAcidMap[Base.GUANINE][Base.ADENINE][Base.CYTOSINE] = AminoAcid.ASPARTIC_ACID;
		codonToAminoAcidMap[Base.GUANINE][Base.ADENINE][Base.ADENINE] = AminoAcid.GLUTAMIC_ACID;
		codonToAminoAcidMap[Base.GUANINE][Base.ADENINE][Base.GUANINE] = AminoAcid.GLUTAMIC_ACID;

		// GGx
		codonToAminoAcidMap[Base.GUANINE][Base.GUANINE][Base.URACIL] = AminoAcid.GLYCINE;
		codonToAminoAcidMap[Base.GUANINE][Base.GUANINE][Base.CYTOSINE] = AminoAcid.GLYCINE;
		codonToAminoAcidMap[Base.GUANINE][Base.GUANINE][Base.ADENINE] = AminoAcid.GLYCINE;
		codonToAminoAcidMap[Base.GUANINE][Base.GUANINE][Base.GUANINE] = AminoAcid.GLYCINE;
	}

	/**
	 * Get the amino acid, stop or start for a given codon.<p>
	 *
	 * In other words, this function will return either one of the 20 amino acids
	 * defined in AminoAcid.java or Codon.START and Codon.STOP.<p>
	 *
	 * @param		firstBase byte - first base value in codon
	 * @param		secondBase byte - second base value in codon
	 * @param		thirdBase byte - third base value in codon
	 * @return		byte - amino acid or start or stop value
	 * @exception	IllegalArgumentException - input arguments not valid
	**/
	public static byte getAminoAcidForCodon(byte firstBase, byte secondBase, byte thirdBase)
	{
		if (Base.isValidBase(firstBase,Base.IN_RNA) &&
			Base.isValidBase(secondBase,Base.IN_RNA) &&
			Base.isValidBase(thirdBase,Base.IN_RNA))
		{
			return codonToAminoAcidMap[firstBase][secondBase][thirdBase];
		}

		throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
	}
}

