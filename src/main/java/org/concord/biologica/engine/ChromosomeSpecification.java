//
// Class : ChromosomeSpecification
//
// Copyright © 1998, The Concord Consortium
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
import java.io.Serializable;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class represents the specification of a particular chromosome for use in
 * creating an organism.  In other words, this class represents a "blueprint"
 * for a single chromosome of an organism.<p>
 *
 * During meiosis and fertilization the genotype of the child organism is created.
 * That genotype consists of an exact specification for what alleles should be
 * on what chromosomes of the resulting child organism.  This class is a portion
 * of that exact specification, the portion for a single chromosome.<p>
 *
 * Instances of this class are best considered immutable, transient and with very
 * short lifetimes.  Hence there is no way to save instances of this class to a file,
 * no notification of property changes, no methods to change instance variables, etc.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:11 $
 * @author 		$Author: ed $
**/

public final class ChromosomeSpecification
{
	/**
	 * The species for this chromosome spec.<p>
	**/
	private Species				species;

	/**
	 * The species chromosome corresponding to this chromosome.
	**/
	private SpeciesChromosome	speciesChromosome;

	/**
	 * The vector of species alleles on this chromosome.<p>
	 *
	 * Items on this vector are SpeciesAlleles.<p>
	**/
	private Vector				speciesAlleles;

	/**
	 * Create a new chromosome specification.<p>
	 *
	 * @param		aSpecies Species - the species containing this chromosome, may not be null
	 * @param		aSpeciesChromosome SpeciesChromosome - the species chromosome for this specification
	 * @param		someSpeciesAlleles Vector - a vector of species alleles
	 * @exception 	IllegalArgumentException - input argument(s) illegal
	**/
	public ChromosomeSpecification(Species aSpecies, SpeciesChromosome aSpeciesChromosome, Vector someSpeciesAlleles)
	{
		// Check input arguments
		if (aSpecies == null ||
			aSpeciesChromosome == null ||
			someSpeciesAlleles == null)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		// Input arguments OK, initialize instance variables
		species = aSpecies;
		speciesChromosome = aSpeciesChromosome;
		speciesAlleles = someSpeciesAlleles;
	}

	/**
	 * Returns the species chromosome for this specification.
	 *
	 * @return			SpeciesChromosome - this specification's species chromosome
	**/
	public SpeciesChromosome getSpeciesChromosome()
	{
		return speciesChromosome;
	}

	/**
	 * Returns an enumeration over the species alleles for this specification
	 *
	 * @return			Enumeration over the species alleles in this specification
	**/
	public Enumeration getSpeciesAlleles()
	{
		return speciesAlleles.elements();
	}
}
