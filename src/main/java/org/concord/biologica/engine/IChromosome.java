//
// Interface : IChromosome
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:13 $
// $Author: ed $
//

package org.concord.biologica.engine;

/**
 * This interface represents a chromosome which all chromosomes implement.<p>
 *
 * Examples are SpeciesChromosome and OrganismChromosome.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public interface IChromosome
{
	/**
	 * X chromosome - used as a "number type".<p>
	**/
	static public final int				X_CHROMOSOME = -1;
	static public final String          X_CHROMOSOME_STRING = "X";

	/**
	 * Y chromosome - used as a "number type".<p>
	**/
	static public final int 			Y_CHROMOSOME = -2;
	static public final String          Y_CHROMOSOME_STRING = "Y";

	/**
	 * The "A" chromosome of a pair, sometimes of three.<p>
	**/
	static public final int				A_CHROMOSOME = -90;

	/**
	 * The "B" chromosome of a pair, sometimes of three.<p>
	**/
	static public final int				B_CHROMOSOME = -91;

	/**
	 * The "C" chromosome of a pair, sometimes of three.<p>
	**/
	static public final int				C_CHROMOSOME = -92;

	/**
	 * Chromosome type - autosome (non-sex chromosome)
	**/
	static public final int				AUTOSOME = -101;

	/**
	 * Chromosome type - sex chromosome
	**/
	static public final int				SEX_CHROMOSOME = -102;

	/**
	 * Next highest autosome number.<p>
	 *
	 * Users should use this value when they want the new species chromosome
	 * to have the next highest chromosome value.<p>
	 *
	 * This is NOT a valid number type for most uses.  It may only be used
	 * when constructing the species chromosome initially.<p>
	**/
	static public final int				NEXT_HIGHEST_AUTOSOME_NUMBER = 0;

	/**
	 * Returns the length of chromosome in bases.<p>
	 *
	 * @return		int - length of this chromosome in bases
	**/
	abstract public int getLengthInBases();

	/**
	 * Returns the length of the chromosome in codons, calculated
	 * by dividing the number of bases by 3, ignoring remainder.<p>
	 *
	 * @return		int - length of this chromosome in codons
	**/
	abstract public int getLengthInCodons();

	/**
	 * Returns the number / type of this chromosome.<p>
	 *
	 * @return		int - IChromosome.X_CHROMOSOME, IChromosome.Y_CHROMOSOME or an autosome number
	**/
	abstract public int getNumberType();

	/**
	 * Returns whether the chromosome is a sex chromosome.<p>
	 *
	 * @return		boolean - true if sex chromosome, else false
	**/
	abstract public boolean isSexChromosome();

	/**
	 * Get the chromosome type (autosome or sex chromosome).<p>
	 *
	 * @return		int - IChromosome.AUTOSOME or IChromosome.SEX_CHROMOSOME
	**/
	abstract public int getChromosomeType();
}
