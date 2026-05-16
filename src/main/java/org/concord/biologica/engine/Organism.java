//
// Class : Organism
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.24 $
// $Date: 2004/08/08 18:15:22 $
// $Author: qliao $
//

package org.concord.biologica.engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Date;

import java.awt.*;
import org.concord.biologica.ui.*;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;
import org.apache.xerces.parsers.SAXParser;

/**
 * This class represents an organism.  At the moment, all organisms
 * should be modeled using this class - haploid, diploid, plants, mammals, etc.
 * Perhaps in the future we'll create subclasses of this class as necessary.<p>
 *
 * For now, the name, color and ploidy number of an organism may not be modified
 * during the organism's lifetime.  We can relax this if necessary later.<p>
 *
 * An organism actually has two possible 'states'.  In the default state, the
 * organism knows what genes it contains but the actual chromosome and DNA
 * objects embodying those genes are not created.  In this default state, the
 * genes are stored directly and chromosomes are not created.  This default
 * state is faster, uses less storage and is simpler for most cases, especially
 * the population level.  In the non-default, or full, state the organism's
 * chromosome and DNA objects are created and correctly include the genes.
 * This level of detail is only needed when the organism's DNA level is opened.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.ALLELES_ALTERABLE - organism allele alterability has change
 * <li> EngineProp.ALLELES_VISIBLE - organism allele visibility has change
 * <li> EngineProp.CHARACTERISTIC_ADDED - a characteristic was added
 * <li> EngineProp.CHARACTERISTIC_REMOVED - a characteristic was removed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.DNA_ALTERABLE - organism DNA alterability has change
 * <li> EngineProp.DNA_VISIBLE - organism DNA visibility has change
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.LOCKED_STATE - locked state of object has changed
 * <li> EngineProp.NAME - name of this organism has changed
 * <li> EngineProp.NAME_SUPER_VISIBLE - organism name super visibility has changed
 * <li> EngineProp.ORGANISM_CHROMOSOME_ADDED - an organism chromosome was added
 * <li> EngineProp.ORGANISM_CHROMOSOME_REMOVED - an organism chromosome was removed
 * <li> EngineProp.ORGANISM_GENOTYPE_AND_NOT_PHENOTYPE - genotype of organism has changed but the phenotype did not change
 * <li> EngineProp.ORGANISM_GENOTYPE_AND_PHENOTYPE - genotype and phenotype of organism has changed
 * <li> EngineProp.ORGANISM_IMAGE_ADDED - an organism image was added
 * <li> EngineProp.ORGANISM_IMAGE_REMOVED - an organism image was removed
 * <li> EngineProp.ORGANISM_PHENOTYPE - phenotype of organism has changed
 * <li> EngineProp.SEX - sex of this organism
 * <li> EngineProp.SPECIES - species of this organism has changed
 * <li> EngineProp.VISIBLE - organism visibility has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#ALLELES_ALTERABLE
 * @see org.concord.biologica.engine.EngineProp#ALLELES_VISIBLE
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_ADDED
 * @see org.concord.biologica.engine.EngineProp#CHARACTERISTIC_REMOVED
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#DNA_ALTERABLE
 * @see org.concord.biologica.engine.EngineProp#DNA_VISIBLE
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#NAME_SUPER_VISIBLE
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_CHROMOSOME_ADDED
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_CHROMOSOME_REMOVED
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_GENOTYPE_AND_NOT_PHENOTYPE
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_GENOTYPE_AND_PHENOTYPE
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_IMAGE_ADDED
 * @see org.concord.biologica.engine.EngineProp#ORGANISM_IMAGE_REMOVED
 * @see org.concord.biologica.engine.EngineProp#SEX
 * @see org.concord.biologica.engine.EngineProp#SPECIES
 * @see org.concord.biologica.engine.EngineProp#VISIBLE
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.24 $ $Date: 2004/08/08 18:15:22 $
 * @author 		$Author: qliao $
**/

public final class Organism
extends EngineObject
implements Serializable,CCDraggable
{

	/**
	 * age limit
	**/
	 static final public int AGE_LIMIT = 100;
	 //static final public int AGE_LIMIT = 10;
	 static final public int aZero = 10000;
	/**
	 * hungry limit
	**/
	static final public int HUNGRY_LIMIT = 10;
	
    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Random number generator used in choosing X or Y first in females.<p>
    **/
    static private Random random = new Random();

    /**
     * Sex values
    **/
    static final public int		MALE 	      = 0;
    static final public String  MALE_STRING   = "male";

    static final public int		FEMALE 	      = 1;
    static final public String  FEMALE_STRING = "female";

    static final public int		NO_SEX 	      = 2;
    static final public String  NO_SEX_STRING = "none";

    /**
     * Value for the speciesPhenotypeIndex when the species is null,
     * indicating the index is not known.<p>
    **/
    static final private int	NULL_PHENOTYPE_INDEX = -1;

    /**
     * Name of this organism.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME
    **/
    private String				name;

    /**
     * World containing this organism.  May not be null.<p>
    **/
    private World				world;

    /**
     * Sex of this organism - one of above values.<p>
    **/
    private int					sex;

    /**
     * Ploidy number.  Must be either 1 (haploid) or 2 (diploid).
     * Note that a single species may have both haploid and diploid
     * organisms (e.g. yeast can be either depending on the environment).
     * A single organism cannot change ploidy number after it is created.<p>
     *
     * Read-only.<p>
    **/
    private int					ploidyNumber;

    /**
     * Species reference.  At this time, cannot be null.<p>
     *
     * But in the future, we may allow it to be null during
     * evolution when an organism can evolve to a new species.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.SPECIES.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#SPECIES
    **/
    private Species				species;
    
    // These just hold the newly created objects as the are parsed from
    // the XML world file, so they can be added after parsing is finished.
    transient private OrganismChromosome organismChromosome = null;
    transient private OrganismChromosomePair organismChromosomePair = null;
    transient private OrganismImage organismImage = null;

    /**
     * Vector of organism non-sex chromosomes in this organism.  Never null.<p>
     *
     * All the objects on this vector must be instances of OrganismChromosome.<p>
     *
     * When an OrganismChromosome object is created and added to this vector,
     * a EngineProp.ORGANISM_CHROMOSOME_ADDED property change event is fired.<p>
     *
     * When an OrganismChromosome object is deleted and removed from this vector,
     * a EngineProp.ORGANISM_CHROMOSOME_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.OrganismChromosome
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_CHROMOSOME_ADDED
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_CHROMOSOME_REMOVED
    **/
    private Vector				nonSexChromosomes;

    /**
     * Vector of organism sex chromosomes in this organism.  Never null.<p>
     *
     * All the objects on this vector must be instances of OrganismChromosome.<p>
     *
     * When an OrganismChromosome object is created and added to this vector,
     * a EngineProp.ORGANISM_CHROMOSOME_ADDED property change event is fired.<p>
     *
     * When an OrganismChromosome object is deleted and removed from this vector,
     * a EngineProp.ORGANISM_CHROMOSOME_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.OrganismChromosome
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_CHROMOSOME_ADDED
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_CHROMOSOME_REMOVED
    **/
    private Vector				sexChromosomes;

    /**
     * Vector of organism chromosome pairs.  Null if not a diploid species.<p>
     *
     * All the objects on this vector must be instances of OrganismChromosomePair.<p>
     *
     * No notifications as it's assumed the pairs won't change.<p>
     *
     * @see		org.concord.biologica.engine.OrganismChromosomePair
    **/
    private Vector				chromosomePairs;

    /**
     * Whether this organism is visible or not.<p>
     *
     * When this changes, a EngineProp.VISIBLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#VISIBLE
    **/
    private boolean				visible;

    /**
     * Whether this organism is name super visible or not.  A name super visible organism
     * is one that some views may show the name of even when the view isn't showing
     * organism names in general.  This is especially useful for showing the names of
     * root organisms in the pedigree view (and avoiding showing the child names).<p>
     *
     * When this changes, a EngineProp.NAME_SUPER_VISIBLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME_SUPER_VISIBLE
    **/
    private boolean				nameSuperVisible;

    /**
     * Whether this organism's alleles are visible or not.<p>
     *
     * When this changes, a EngineProp.ALLELES_VISIBLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#ALLELES_VISIBLE
    **/
    private boolean				allelesVisible;

    /**
     * Whether this organism's DNA is visible or not.<p>
     *
     * When this changes, a EngineProp.DNA_VISIBLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#DNA_VISIBLE
    **/
    private boolean				dnaVisible;

    /**
     * Whether this organism's alleles are alterable or not.<p>
     *
     * When this changes, a EngineProp.ALLELES_ALTERABLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#ALLELES_ALTERABLE
    **/
    private boolean				allelesAlterable;
    
     /**
     * Whether this organism's chromosomes are visible or not.<p>
     *
     *
    **/
    private boolean				chromosomesVisible;
    
    /**
     * Whether this organism's DNA is alterable or not.<p>
     *
     * When this changes, a EngineProp.DNA_ALTERABLE property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#DNA_ALTERABLE
    **/
    private boolean				dnaAlterable;

    /**
     * The vector of OrganismImage objects which should be used to draw this organism.
     *
     * When an OrganismImage object is created and added to this vector,
     * a EngineProp.ORGANISM_IMAGE_ADDED property change event is fired.<p>
     *
     * When an OrganismImage object is deleted and removed from this vector,
     * a EngineProp.ORGANISM_IMAGE_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.OrganismImage
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_IMAGE_ADDED
     * @see		org.concord.biologica.engine.EngineProp#ORGANISM_IMAGE_REMOVED
    **/
    private Vector				organismImages;

    /**
     * The precomputed phenotype of this organism, kept in the form of a vector of
     * Characteristics.  It's kept in this form to make it easy to compute and to
     * draw this organism.<p>
     *
     * Note that there is no provision currently for handling the deletion of a
     * Characteristic gracefully, as it's assumed Characteristic objects become
     * locked down when Organism objects are created from the Species containing
     * those Characteristics.  This is done this way because it's generally
     * impossible to recompute the phenotype of an organism if the species is
     * changed even moderately significantly.<p>
     *
     * This vector is not saved to a file when the organism is saved.  Instead,
     * it must be recomputed when the organism is read back in from the file,
     * at least for now.<p>
    **/
    private Vector				characteristics;

    /**
     * The parent family of this organism, meaning the family which this organism
     * is a child in.  For organisms with no parents (created out of thin air) or
     * for organisms of a non-diploid species, this instance variable is null.<p>
    **/
    private Family				parentFamily;

    /**
     * The child families of this organism, meaning the families for which this organism
     * is a parent.  For organisms with no children or organisms of a non-diploid
     * species, this instance variable is null.<p>
    **/
    private Vector				childFamilies;

    /**
     * Creates a new organism in the given world from a species definition,
     * randomly choosing the sex and genetic makeup of the organism.<p>
     *
     * The new organism will randomly choose a genotype using the species
     * definition.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aPloidyNumber int - ploidy number, must be 1 or 2
     * @param		aSpecies Species - species of this organism, may be null
     * @param		aName String - name of this organism, may be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Organism(World aWorld, String aName, int aPloidyNumber, Species aSpecies)
    {
        // Test input arguments
        if (aWorld == null ||
            (!(aPloidyNumber == 1 || aPloidyNumber == 2)) ||
            aSpecies == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // If input name is null, try to use the organism's species and id,
        // else just use "Organism" and id
        if (aName == null)
        {
            String speciesName = aSpecies.getName();
            if (speciesName != null)
            {
                name = speciesName + EngineStrings.SPACE + id;
            }
            else
            {
                name = EngineStrings.ORGANISM + EngineStrings.SPACE + id;
            }
        }
        else
        {
            name = new String(aName);
        }
        
        // Input arguments ok - initialize instance variables
        lockedState = EngineObject.UNLOCKED;
        world = aWorld;
        ploidyNumber = aPloidyNumber;
        species = aSpecies;
        
        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        chromosomePairs = new Vector();
        characteristics = new Vector();
        organismImages = new Vector();
        visible = true;
        chromosomesVisible = true;
        nameSuperVisible = false;
        allelesVisible = true;
        dnaVisible = true;
        allelesAlterable = true;
        dnaAlterable = true;
		
        // This organism has no parents and initially no children
        parentFamily = null;
        childFamilies = new Vector();
        
        // Create chromosomes and alleles of organism randomly
        SpeciesChromosome sc;
        Enumeration eNonSexChromosomes;
        OrganismChromosome oc, oc1, oc2;
        OrganismChromosomePair ocp;

        if (species.getPloidyNumber() == 1)
        {
            // Non-sex chromosomes only - one organism chromosome per species chromosome
            eNonSexChromosomes = species.getNonSexChromosomes();
            while (eNonSexChromosomes.hasMoreElements())
            {
                sc = (SpeciesChromosome) eNonSexChromosomes.nextElement();
                oc = new OrganismChromosome(this,sc);
            }
        }
        else if (species.getPloidyNumber() == 2)
        {
            // Non-sex chromosomes first - two organism chromosomes per species chromosome and pairs
            eNonSexChromosomes = species.getNonSexChromosomes();
            while (eNonSexChromosomes.hasMoreElements())
            {
                sc = (SpeciesChromosome) eNonSexChromosomes.nextElement();
                oc1 = new OrganismChromosome(this,sc);
                oc2 = new OrganismChromosome(this,sc);
                ocp = new OrganismChromosomePair(this,oc1,oc2);
            }
        }

        // If diploid, create sex chromosomes
        if (species.getPloidyNumber() == 2 &&
            species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
            // Randomly choose male or female
            sex = randomlyChooseSex();

            // Find X and Y species sex chromosomes
            int numberType;
            SpeciesChromosome scX = null;
            SpeciesChromosome scY = null;
            Enumeration eSexChromosomes = species.getSexChromosomes();
            while (eSexChromosomes.hasMoreElements())
            {
                sc = (SpeciesChromosome) eSexChromosomes.nextElement();
                numberType = sc.getNumberType();
                if (numberType == IChromosome.X_CHROMOSOME)
                {
                    scX = sc;
                }
                else if (numberType == IChromosome.Y_CHROMOSOME)
                {
                    scY = sc;
                }
            }
    
            if (scX != null && scY != null)
            {
                int diploidType = species.getDiploidType();
                if (sex == Organism.MALE)
                {
                    if (diploidType == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
                    {
                        // XY male - randomly choose to make X or Y first
                        if (randomlyChooseXorYChromosome() == IChromosome.X_CHROMOSOME)
                        {
                            // Make X first, Y second
                            oc1 = new OrganismChromosome(this,scX);
                            oc2 = new OrganismChromosome(this,scY);
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                        else
                        {
                            // Make Y first, X second
                            oc1 = new OrganismChromosome(this,scY);
                            oc2 = new OrganismChromosome(this,scX);
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                    }
                    else if (diploidType == Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE)
                    {
                        // XX male
                        oc1 = new OrganismChromosome(this,scX);
                        oc2 = new OrganismChromosome(this,scX);
                        ocp = new OrganismChromosomePair(this,oc1,oc2);
                    }
                }
                else	// female
                {
                    if (diploidType == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
                    {
                        // XX female
                        oc1 = new OrganismChromosome(this,scX);
                        oc2 = new OrganismChromosome(this,scX);					
                        ocp = new OrganismChromosomePair(this,oc1,oc2);
                    }
                    else if (diploidType == Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE)
                    {
                        // XY female - randomly choose to make X or Y first
                        if (randomlyChooseXorYChromosome() == IChromosome.X_CHROMOSOME)
                        {
                            // Make X first
                            oc1 = new OrganismChromosome(this,scX);
                            oc2 = new OrganismChromosome(this,scY);
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                        else
                        {
                            // Make Y first
                            oc1 = new OrganismChromosome(this,scY);					
                            oc2 = new OrganismChromosome(this,scX);
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                    }
                }
            }
        }
        else
        {
            sex = NO_SEX;
        }

        // Update the genotype and phenotype
        updateGenotypeAndPhenotype(false);

        // Creation successful, so add this organism to world
        aWorld.addOrganism(this);
  
    }
    
    /**
     * Creates a new organism from 2 parents, randomly choosing the parent
     * chromosome from which to copy alleles for each child chromosome.<p>
     *
     * The 2 parents must both be non-null, of opposite sexes of the same
     * species and in the same world.  Ploidy number must be two.<p>
     *
     * @param		aParentOne Organism - one of the parents, may not be null
     * @param		aParentTwo Organism - the other of the parents, may not be null
     * @param		aName String - name of this organism, may be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
     public Organism(Organism aParentOne, Organism aParentTwo, String aName)
    {
    	this(aParentOne, aParentTwo,aName,false);
    }
    
    public Organism(Organism aParentOne, Organism aParentTwo, String aName,boolean crossingOver)
    {
        // Test input arguments
        if (aParentOne == null || aParentTwo == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Make sure species and worlds of 2 parents are non-null and identical
        world = aParentOne.getWorld();
        if (world == null ||
            world != aParentTwo.getWorld())
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Make sure parent species are identical and have ploidy number of 2
        species = aParentOne.getSpecies();
        if (species == null ||
            species != aParentTwo.getSpecies() ||
            species.getPloidyNumber() != 2)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        ploidyNumber = 2;
        
        // If input name is null, try to use the organism's species and id,
        // else just use "Organism" and id
        if (aName == null)
        {
            String speciesName = species.getName();
            if (speciesName != null)
            {
                name = speciesName + EngineStrings.SPACE + id;
            }
            else
            {
                name = EngineStrings.ORGANISM + EngineStrings.SPACE + id;
            }
        }
        else
        {
            name = new String(aName);
        }
        
        // Input arguments ok - initialize instance variables
        lockedState = EngineObject.UNLOCKED;
        Organism tempOrg = null;
        
        if (aParentOne.getSex()==Organism.MALE)
        {
        	tempOrg = aParentOne;
        	aParentOne = aParentTwo;
        	aParentTwo = tempOrg;
        }

        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        chromosomePairs = new Vector();
        characteristics = new Vector();
        organismImages = new Vector();
        visible = true;
        chromosomesVisible = true;
        nameSuperVisible = false;
        allelesVisible = true;
        dnaVisible = true;
        allelesAlterable = true;
        dnaAlterable = true;

	
        // This organism has no parents and initially no children
        parentFamily = null;
        childFamilies = new Vector();
        
        // Create chromosomes and alleles of organism randomly
        OrganismChromosome ocParentOne, ocOneParentOne, ocTwoParentOne,tempParentOne;
        OrganismChromosome ocParentTwo, ocOneParentTwo, ocTwoParentTwo;
        OrganismChromosome ocChildOne, ocChildTwo;
        OrganismChromosomePair ocp;
        Vector chrBranchs = new Vector();

        // For each chromosome, get the 2 chromosomes of that number from the
        // father and the 2 chromosomes of that number from the mother.
        // Then randomly choose one chromosome from the mother and one from
        // the father.  Use each of those 2 chromosomes as blueprints from
        // the chromosomes of this child organism.

        // Non-sex chromosomes
        Enumeration eParentOneChromosomes = aParentOne.getNonSexChromosomes();
        Enumeration eParentTwoChromosomes = aParentTwo.getNonSexChromosomes();
        Branch [] eParentOneBranchs = aParentOne.getChromosomePaintInfo();
        Branch [] eParentTwoBranchs = aParentTwo.getChromosomePaintInfo();
        
      	int i = 0;
        while (eParentOneChromosomes.hasMoreElements() &&
               eParentTwoChromosomes.hasMoreElements())
        {
            ocOneParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            ocTwoParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            setPaintInfo(ocOneParentOne,ocTwoParentOne,eParentOneBranchs[i]);
			
            ocParentOne = randomlyChooseChromosome(ocOneParentOne,ocTwoParentOne);
            ocChildOne = new OrganismChromosome(this,ocParentOne);
           
          
   
            ocOneParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            ocTwoParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            setPaintInfo(ocOneParentTwo,ocTwoParentTwo,eParentTwoBranchs[i]);

            ocParentTwo = randomlyChooseChromosome(ocOneParentTwo,ocTwoParentTwo);
            ocChildTwo = new OrganismChromosome(this,ocParentTwo);
            ocp = new OrganismChromosomePair(this,ocChildOne,ocChildTwo);
            
            Branch temp = new Branch(ocChildOne.getPStrand(),ocChildOne.getPStrandColor(),
            						 ocChildTwo.getPStrand(),ocChildTwo.getPStrandColor(),
            						 ocChildOne.getQStrand(),ocChildOne.getQStrandColor(),
            						 ocChildTwo.getQStrand(),ocChildTwo.getQStrandColor());
            chrBranchs.addElement(temp);
            i = i+1;
        }

        // Sex chromosomes
        if (species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
        	
            eParentOneChromosomes = aParentOne.getSexChromosomes();
            eParentTwoChromosomes = aParentTwo.getSexChromosomes();
           
    
            ocOneParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            ocTwoParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();       
            setPaintInfo(ocOneParentOne,ocTwoParentOne,eParentOneBranchs[i]);
	        
            ocParentOne = randomlyChooseChromosome(ocOneParentOne,ocTwoParentOne);
            ocChildOne = new OrganismChromosome(this,ocParentOne);
    
            ocOneParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            ocTwoParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
	        setPaintInfo(ocOneParentTwo,ocTwoParentTwo,eParentTwoBranchs[i]);
	        
	        ocParentTwo = randomlyChooseChromosome(ocOneParentTwo,ocTwoParentTwo);
            ocChildTwo = new OrganismChromosome(this,ocParentTwo);
  
            ocp = new OrganismChromosomePair(this,ocChildOne,ocChildTwo);
            Branch temp = new Branch(ocChildOne.getPStrand(),ocChildOne.getPStrandColor(),
            						 ocChildTwo.getPStrand(),ocChildTwo.getPStrandColor(),
            						 ocChildOne.getQStrand(),ocChildOne.getQStrandColor(),
            						 ocChildTwo.getQStrand(),ocChildTwo.getQStrandColor());
            chrBranchs.addElement(temp);
    
            // Determine sex by looking at type of sex chromosomes and diploid type of species
            int numberTypeOne = ocChildOne.getNumberType();
            int numberTypeTwo = ocChildTwo.getNumberType();
    
            int diploidType = species.getDiploidType();
            if (diploidType == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
            {
                if (numberTypeOne == IChromosome.X_CHROMOSOME)
                {
                    if (numberTypeTwo == IChromosome.X_CHROMOSOME)
                    {
                        sex = Organism.FEMALE;
                    }
                    else
                    {
                        sex = Organism.MALE;
                    }
                }
                else
                {
                    sex = Organism.MALE;
                }
            }
            else if (diploidType == Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE)
            {
                if (numberTypeOne == IChromosome.X_CHROMOSOME)
                {
                    if (numberTypeTwo == IChromosome.X_CHROMOSOME)
                    {
                        sex = Organism.MALE;
                    }
                    else
                    {
                        sex = Organism.FEMALE;
                    }
                }
                else
                {
                    sex = Organism.FEMALE;
                }
            }
        }
        else
        {
            sex = NO_SEX;
        }

        // Update the genotype and phenotype
        updateGenotypeAndPhenotype(false);

        // Creation successful, so add this organism to world
        world.addOrganism(this);
     
       
        if (!crossingOver)
        	originalChromosomePaintInfo();
        else
        {
        	newChromosomes = new Branch[chrBranchs.size()];
        	for (int j = 0;j<chrBranchs.size();j++)
        	{
        		newChromosomes[j] = (Branch)chrBranchs.elementAt(j);
        	}
        }
    		
    }

    /**
     * Creates a new organism from 2 parents, randomly choosing the parent
     * chromosome from which to copy alleles for each child chromosome.<p>
     *
     * The 2 parents must both be non-null, of opposite sexes of the same
     * species and in the same world.  Ploidy number must be two.<p>
     *
     * This version is different from the above version in that you specify
     * the sex of the child in this version.<p>
     *
     * @param		aParentOne Organism - one of the parents, may not be null
     * @param		aParentTwo Organism - the other of the parents, may not be null
     * @param		aName String - name of this organism, may be null
     * @param		aSex int - sex of organism (MALE or FEMALE)
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Organism(Organism aParentOne, Organism aParentTwo, String aName, int aSex)
    {
    		this(aParentOne,aParentTwo,aName,aSex,false);
    }
    public Organism(Organism aParentOne, Organism aParentTwo, String aName, int aSex, boolean crossingOver)
    {
        // Test input arguments
      
        if (aParentOne == null ||
            aParentTwo == null ||
            (aSex != MALE && aSex != FEMALE && aSex != NO_SEX))
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Make sure species and worlds of 2 parents are non-null and identical
        world = aParentOne.getWorld();
        if (world == null ||
            world != aParentTwo.getWorld())
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Make sure parent species are identical and have ploidy number of 2
        species = aParentOne.getSpecies();
        if (species == null ||
            species != aParentTwo.getSpecies() ||
            species.getPloidyNumber() != 2)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        ploidyNumber = 2;
        
        // If input name is null, try to use the organism's species and id,
        // else just use "Organism" and id
        if (aName == null)
        {
            String speciesName = species.getName();
            if (speciesName != null)
            {
                name = speciesName + EngineStrings.SPACE + id;
            }
            else
            {
                name = EngineStrings.ORGANISM + EngineStrings.SPACE + id;
            }
        }
        else
        {
            name = new String(aName);
        }
        
        // Input arguments ok - initialize instance variables
        lockedState = EngineObject.UNLOCKED;

        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        chromosomePairs = new Vector();
        characteristics = new Vector();
        organismImages = new Vector();
        visible = true;
        nameSuperVisible = false;
        allelesVisible = true;
        dnaVisible = true;
        allelesAlterable = true;
        dnaAlterable = true;
        chromosomesVisible = true;

        // This organism has no parents and initially no children
        parentFamily = null;
        childFamilies = new Vector();
        Vector chrBranchs = new Vector();
        // Create chromosomes and alleles of organism randomly
        OrganismChromosome ocParentOne, ocOneParentOne, ocTwoParentOne;
        OrganismChromosome ocParentTwo, ocOneParentTwo, ocTwoParentTwo;
        OrganismChromosome ocChildOne, ocChildTwo;
        OrganismChromosomePair ocp;

        // For each chromosome, get the 2 chromosomes of that number from the
        // father and the 2 chromosomes of that number from the mother.
        // Then randomly choose one chromosome from the mother and one from
        // the father.  Use each of those 2 chromosomes as blueprints from
        // the chromosomes of this child organism.

        // Non-sex chromosomes
        Enumeration eParentOneChromosomes = aParentOne.getNonSexChromosomes();
        Enumeration eParentTwoChromosomes = aParentTwo.getNonSexChromosomes();
        Branch [] eParentOneBranchs = aParentOne.getChromosomePaintInfo();
        Branch [] eParentTwoBranchs = aParentTwo.getChromosomePaintInfo();
      	int i = 0;
        while (eParentOneChromosomes.hasMoreElements() &&
               eParentTwoChromosomes.hasMoreElements())
        {
        	
            ocOneParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            ocTwoParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            setPaintInfo(ocOneParentOne,ocTwoParentOne,eParentOneBranchs[i]);
			
            ocParentOne = randomlyChooseChromosome(ocOneParentOne,ocTwoParentOne);
            ocChildOne = new OrganismChromosome(this,ocParentOne);
           

            ocOneParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            ocTwoParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            setPaintInfo(ocOneParentTwo,ocTwoParentTwo,eParentTwoBranchs[i]);

            ocParentTwo = randomlyChooseChromosome(ocOneParentTwo,ocTwoParentTwo);
            ocChildTwo = new OrganismChromosome(this,ocParentTwo);
            ocp = new OrganismChromosomePair(this,ocChildOne,ocChildTwo);
            
            Branch temp = new Branch(ocChildOne.getPStrand(),ocChildOne.getPStrandColor(),
            						 ocChildTwo.getPStrand(),ocChildTwo.getPStrandColor(),
            						 ocChildOne.getQStrand(),ocChildOne.getQStrandColor(),
            						 ocChildTwo.getQStrand(),ocChildTwo.getQStrandColor());
            chrBranchs.addElement(temp);
            i = i+1;
        }


        // Sex chromosomes
        if (species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
        	i = 0;
            eParentOneChromosomes = aParentOne.getSexChromosomes();
            eParentTwoChromosomes = aParentTwo.getSexChromosomes();
    
            ocOneParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            ocTwoParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            setPaintInfo(ocOneParentOne,ocTwoParentOne,eParentOneBranchs[i]);
    
            ocOneParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            ocTwoParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            setPaintInfo(ocOneParentTwo,ocTwoParentTwo,eParentTwoBranchs[i]);

            OrganismChromosome xOneParentOne = null, xTwoParentOne = null, yParentOne = null;
            OrganismChromosome xOneParentTwo = null, xTwoParentTwo = null, yParentTwo = null;
    
            if (ocOneParentOne.getNumberType() == IChromosome.X_CHROMOSOME)
            {
                xOneParentOne = ocOneParentOne;
                if (ocTwoParentOne.getNumberType() == IChromosome.X_CHROMOSOME)
                {
                    xTwoParentOne = ocTwoParentOne;
                    yParentOne = null;
                }
                else
                {
                    xTwoParentOne = null;
                    yParentOne = ocTwoParentOne;
                }
            }
            else
            {
                yParentOne = ocOneParentOne;
                xOneParentOne = ocTwoParentOne;
                xTwoParentOne = null;
            }
    
            if (ocOneParentTwo.getNumberType() == IChromosome.X_CHROMOSOME)
            {
                xOneParentTwo = ocOneParentTwo;
                if (ocTwoParentTwo.getNumberType() == IChromosome.X_CHROMOSOME)
                {
                    xTwoParentTwo = ocTwoParentTwo;
                    yParentTwo = null;
                }
                else
                {
                    xTwoParentTwo = null;
                    yParentTwo = ocTwoParentTwo;
                }
            }
            else
            {
                yParentTwo = ocOneParentTwo;
                xOneParentTwo = ocTwoParentTwo;
                xTwoParentTwo = null;
            }
    
            // Randomly choose proper sex chromosomes to get the desired sex
            int diploidType = species.getDiploidType();
            if (diploidType == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
            {
                if (aSex == FEMALE)
                {
                    // Female - choose an X chromosome from both parents
                    if (xTwoParentOne == null)
                    {
                        ocChildOne = xOneParentOne;
                    }
                    else
                    {
                        ocChildOne = randomlyChooseChromosome(xOneParentOne, xTwoParentOne);
                    }
                    if (xTwoParentTwo == null)
                    {
                        ocChildTwo = xOneParentTwo;
                    }
                    else
                    {
                        ocChildTwo = randomlyChooseChromosome(xOneParentTwo, xTwoParentTwo);
                    }
                }
                else
                {
                    // Male - choose an X from one parent and a Y from the other
                    // Choose an X from the parent that does not have the Y
                    if (yParentOne == null)
                    {
                        ocChildOne = yParentTwo;
                        ocChildTwo = randomlyChooseChromosome(xOneParentOne, xTwoParentOne);
                    }
                    else
                    {
                        ocChildOne = randomlyChooseChromosome(xOneParentTwo, xTwoParentTwo);
                        ocChildTwo = yParentOne;
                    }
                }
            }
            else
            {
                // XX Male, XY Female
                if (aSex == MALE)
                {
                    // Male - choose an X chromosome from both parents
                    if (xTwoParentOne == null)
                    {
                        ocChildOne = xOneParentOne;
                    }
                    else
                    {
                        ocChildOne = randomlyChooseChromosome(xOneParentOne, xTwoParentOne);
                    }
                    if (xTwoParentTwo == null)
                    {
                        ocChildTwo = xOneParentTwo;
                    }
                    else
                    {
                        ocChildTwo = randomlyChooseChromosome(xOneParentTwo, xTwoParentTwo);
                    }
                }
                else
                {
                    // Female - choose an X from one parent and a Y from the other
                    // Choose an X from the parent that does not have the Y
                    if (yParentOne == null)
                    {
                        ocChildOne = yParentTwo;
                        ocChildTwo = randomlyChooseChromosome(xOneParentOne, xTwoParentOne);
                    }
                    else
                    {
                        ocChildOne = randomlyChooseChromosome(xOneParentTwo, xTwoParentTwo);
                        ocChildTwo = yParentOne;
                    }
                }
            }
    
            // Create sex chromosomes
            sex = aSex;
            OrganismChromosome newChildOne = new OrganismChromosome(this,ocChildOne);
            OrganismChromosome newChildTwo = new OrganismChromosome(this,ocChildTwo);
            ocp = new OrganismChromosomePair(this,newChildOne,newChildTwo);
            Branch temp = new Branch(ocChildOne.getPStrand(),ocChildOne.getPStrandColor(),
            						 ocChildTwo.getPStrand(),ocChildTwo.getPStrandColor(),
            						 ocChildOne.getQStrand(),ocChildOne.getQStrandColor(),
            						 ocChildTwo.getQStrand(),ocChildTwo.getQStrandColor());
            chrBranchs.addElement(temp);
        }
        else
        {
            // Must be a plant species - no sex chromosomes
            sex = NO_SEX;
        }

        // Update the genotype and phenotype
        updateGenotypeAndPhenotype(false);

        // Creation successful, so add this organism to world
        world.addOrganism(this);
       
        if (!crossingOver)
        	originalChromosomePaintInfo();
        else
        {
        	newChromosomes = new Branch[chrBranchs.size()];
        	for (int j = 0;j<chrBranchs.size();j++)
        	{
        		newChromosomes[j] = (Branch)chrBranchs.elementAt(j);
        	}
        }
      
    }


    /**
     * Creates a new organism of the specified sex, randomly choosing
     * the alleles of the chromosomes.  It's assumed since you're
     * specifying the sex that the ploidy number is 2.<p>
     *
     * The new organism will randomly choose a genotype using the species
     * definition.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aSex int - either Organism.MALE, Organism.FEMALE or Organism.NO_SEX
     * @param		aName String - name of this organism, may be null which will cause one to be assigned
     * @param		aSpecies Species - species of this organism, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Organism(World aWorld, int aSex, String aName, Species aSpecies)
    {
        // Test input arguments
        if (aWorld == null ||
            (aSex != MALE && aSex != FEMALE && aSex != NO_SEX) ||
            aSpecies == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments ok - initialize instance variables
        lockedState = EngineObject.UNLOCKED;

        // If input name is null, use organism's id to assign a name, else use input name
        // If input name is null, try to use the organism's species and id,
        // else just use "Organism" and id
        if (aName == null)
        {
            String speciesName = aSpecies.getName();
            if (speciesName != null)
            {
                name = speciesName + EngineStrings.SPACE + id;
            }
            else
            {
                name = EngineStrings.ORGANISM + EngineStrings.SPACE + id;
            }
        }
        else
        {
            name = new String(aName);
        }

        world = aWorld;
        ploidyNumber = 2;
        species = aSpecies;
        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        chromosomePairs = new Vector();
        organismImages = new Vector();
        characteristics = new Vector();
        childFamilies = new Vector();
        visible = true;
        chromosomesVisible = true;
        nameSuperVisible = false;
        allelesVisible = true;
        dnaVisible = true;
        allelesAlterable = true;
        dnaAlterable = true;

        if (species.getDiploidType() == Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
            sex = Organism.NO_SEX;
        }
        else
        {
            sex = aSex;
        }
        
        // Create chromosomes and alleles of organism randomly
        OrganismChromosome oc1, oc2;
        OrganismChromosomePair ocp;
        SpeciesChromosome sc;

        // Non-sex chromosomes first - two organism chromosomes per species chromosome
        Enumeration eNonSexChromosomes = species.getNonSexChromosomes();
        while (eNonSexChromosomes.hasMoreElements())
        {
            sc = (SpeciesChromosome) eNonSexChromosomes.nextElement();
            oc1 = new OrganismChromosome(this,sc);
            oc2 = new OrganismChromosome(this,sc);
            ocp = new OrganismChromosomePair(this,oc1,oc2);
        }

        // If diploid, create sex chromosomes
        if (species.getPloidyNumber() == 2 &&
            species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
            // Find X and Y species sex chromosomes
            int numberType;
            SpeciesChromosome scX = null;
            SpeciesChromosome scY = null;
            Enumeration eSexChromosomes = species.getSexChromosomes();
            while (eSexChromosomes.hasMoreElements())
            {
                sc = (SpeciesChromosome) eSexChromosomes.nextElement();
                numberType = sc.getNumberType();
                if (numberType == IChromosome.X_CHROMOSOME)
                {
                    scX = sc;
                }
                else if (numberType == IChromosome.Y_CHROMOSOME)
                {
                    scY = sc;
                }
            }
    
            // Create organism sex chromosomes if we found species X and Y chromosomes
            if (scX != null && scY != null)
            {
                int diploidType = species.getDiploidType();
                if (sex == Organism.MALE)
                {
                    if (diploidType == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
                    {
                        // XY male - randomly choose to make X or Y first
                        if (randomlyChooseXorYChromosome() == IChromosome.X_CHROMOSOME)
                        {
                            // Make X first, Y second
                            oc1 = new OrganismChromosome(this,scX);
                            oc2 = new OrganismChromosome(this,scY);
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                        else
                        {
                            // Make Y first, X second
                            oc1 = new OrganismChromosome(this,scY);
                            oc2 = new OrganismChromosome(this,scX);
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                    }
                    else if (diploidType == Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE)
                    {
                        // XX male
                        oc1 = new OrganismChromosome(this,scX);
                        oc2 = new OrganismChromosome(this,scX);
                        ocp = new OrganismChromosomePair(this,oc1,oc2);
                    }
                }
                else	// female
                {
                    if (diploidType == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
                    {
                        // XX female
                        oc1 = new OrganismChromosome(this,scX);
                        oc2 = new OrganismChromosome(this,scX);					
                        ocp = new OrganismChromosomePair(this,oc1,oc2);
                    }
                    else if (diploidType == Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE)
                    {
                        // XY female - randomly choose to make X or Y first
                        if (randomlyChooseXorYChromosome() == IChromosome.X_CHROMOSOME)
                        {
                            // Make X first
                            oc1 = new OrganismChromosome(this,scX);
                            oc2 = new OrganismChromosome(this,scY);					
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                        else
                        {
                            // Make Y first
                            oc1 = new OrganismChromosome(this,scY);					
                            oc2 = new OrganismChromosome(this,scX);
                            ocp = new OrganismChromosomePair(this,oc1,oc2);
                        }
                    }
                }
            }
        }

        // Update the genotype and phenotype
        updateGenotypeAndPhenotype(false);

		
        // Creation successful, so add this organism to world
        aWorld.addOrganism(this);
        
    }

    /**
     * Creates a new organism of the specified species using the given
     * ChromosomeSpecification objects to create the correct alleles.<p>
     *
     * No family is created.  Use the next constructor if you also want
     * a family to be created.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aName String - name of this organism, may be null which will cause one to be assigned
     * @param		aSpecies Species - species of this organism, may not be null
     * @param		chromosomeSpecifications Vector - vector of chromosome specifications, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Organism(World aWorld, String aName, Species aSpecies, Vector chromosomeSpecifications)
    {
    	chromosomesVisible = true;
        createSpecificOrganism(aWorld, aName, aSpecies, chromosomeSpecifications);
    }

    /**
     * Creates a new organism of the specified species using the given
     * ChromosomeSpecification objects to create the correct alleles.<p>
     *
     * Also creates a new family, making this organism a child of the
     * input mother and father.  If you don't want a family created, use
     * the constructor above.<p>
     *
     * If either mother or father is null, no family is created.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aName String - name of this organism, may be null which will cause one to be assigned
     * @param		aSpecies Species - species of this organism, may not be null
     * @param		aMother Organism - a mother organism for this child organism
     * @param		aFather Organism - a father organism for this child organism
     * @param		chromosomeSpecifications Vector - vector of chromosome specifications, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Organism(World aWorld, String aName, Species aSpecies,
                    Organism mother, Organism father, Vector chromosomeSpecifications)
    {
    	chromosomesVisible = true;
        createSpecificOrganism(aWorld, aName, aSpecies, chromosomeSpecifications);
                            
        if (mother != null && father != null)
        {
            Family aFamily = new Family(mother,father,this);
        }
    }

    
    /**
     * Creates a new organism of the specified species using the given
     * allele string to create the correct alleles.<p>
     *
     * No family is created.  Use the next constructor if you also want
     * a family to be created.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aName String - name of this organism, may be null which will cause one to be assigned
     * @param		aSpecies Species - species of this organism, may not be null
     * @param		aSex int - the sex of the organism
     * @param		alleles String - string of allele symbol specifications, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Organism(World aWorld, String aName, Species aSpecies, int aSex, String alleles)
    {
    	
        Vector chromosomeSpecifications = aSpecies.parseAlleleString(aSex, alleles);
        for (int i=0;i<chromosomeSpecifications.size();i++)
        {
        	ChromosomeSpecification aChromosomeSpecification = (ChromosomeSpecification)(chromosomeSpecifications.elementAt(i));
        	Enumeration eSpeciesAlleles = aChromosomeSpecification.getSpeciesAlleles();
        	while (eSpeciesAlleles.hasMoreElements())
        	{
        		SpeciesAllele aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
        		aSpeciesAllele.setVisible(true);
        	}
        }
        createSpecificOrganism(aWorld, aName, aSpecies, chromosomeSpecifications);
	random.setSeed(System.currentTimeMillis());
    }

    /**
     * Create a specific organism given chromosome specifications.<p>
     *
     * This is a private little helper function used by the above 4 constructors and is not meant to be
     * called by any code outside of this class.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aName String - name of this organism, may be null which will cause one to be assigned
     * @param		aSpecies Species - species of this organism, may not be null
     * @param		chromosomeSpecifications Vector - vector of chromosome specifications, may not be null
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    private void createSpecificOrganism(World aWorld, String aName, Species aSpecies,
                                        Vector chromosomeSpecifications)
    {
        // Test input arguments
        if (aWorld == null ||
            aSpecies == null ||
            chromosomeSpecifications == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments ok - initialize instance variables
        sex = NO_SEX;
        lockedState = EngineObject.UNLOCKED;

        // If input name is null, use organism's id to assign a name, else use input name
        // If input name is null, try to use the organism's species and id,
        // else just use "Organism" and id
        if (aName == null)
        {
            String speciesName = aSpecies.getName();
            if (speciesName != null)
            {
                name = speciesName + EngineStrings.SPACE + id;
            }
            else
            {
                name = EngineStrings.ORGANISM + EngineStrings.SPACE + id;
            }
        }
        else
        {
            name = new String(aName);
        }

        world = aWorld;
        ploidyNumber = 2;
        species = aSpecies;
        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        chromosomePairs = new Vector();
        organismImages = new Vector();
        characteristics = new Vector();
        childFamilies = new Vector();
        visible = true;
        chromosomesVisible = true;
        nameSuperVisible = false;
        allelesVisible = true;
        dnaVisible = true;
        allelesAlterable = true;
        dnaAlterable = true;
        
        // Create chromosomes and alleles of organism using chromosome specifications
        OrganismChromosome oc, oc1 = null, oc2 = null;
        SpeciesChromosome sc;
        ChromosomeSpecification cs;
        OrganismChromosome ocX1 = null, ocX2 = null;
        OrganismChromosome ocY1 = null, ocY2 = null;
        OrganismChromosomePair ocp;

        Enumeration eChromosomeSpecifications = chromosomeSpecifications.elements();
        while (eChromosomeSpecifications.hasMoreElements())
        {
            cs = (ChromosomeSpecification) eChromosomeSpecifications.nextElement();
            oc = new OrganismChromosome(this,cs);

            // Track what X and Y chromosomes we've created for later sex determination
            if (oc.getNumberType() == IChromosome.X_CHROMOSOME)
            {
                if (ocX1 == null)
                {
                    ocX1 = oc;
                }
                else if (ocX2 == null)
                {
                    ocX2 = oc;
                }
            }
            else if (oc.getNumberType() == IChromosome.Y_CHROMOSOME)
            {
                if (ocY1 == null)
                {
                    ocY1 = oc;
                }
                else if (ocY2 == null)
                {
                    ocY2 = oc;
                }
            }

            // Make chromosome pairs, assuming specification specifies them in pairs
            if (oc1 == null)
            {
                oc1 = oc;
            }
            else if (oc2 == null)
            {
                oc2 = oc;
                ocp = new OrganismChromosomePair(this,oc1,oc2);
                oc1 = null;
                oc2 = null;
            }
        }
        // Determine sex
        if (species.getPloidyNumber() == 2)
        {
            int diploidType = species.getDiploidType();
            if (diploidType == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
            {
                if (ocX1 != null && ocX2 != null)
                {
                    sex = Organism.FEMALE;
                }
                else if (ocX1 != null && ocY1 != null)
                {
                    sex = Organism.MALE;
                }
                else
                {
                    throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
                }
            }
            else if (diploidType == Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE)
            {
                if (ocX1 != null && ocX2 != null)
                {
                    sex = Organism.MALE;
                }
                else if (ocX1 != null && ocY1 != null)
                {
                    sex = Organism.FEMALE;
                }
                else
                {
                    throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
                }
            }
            else if (diploidType == Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
            {
                sex = Organism.NO_SEX;
            }
        }

        // Update the genotype and phenotype
        updateGenotypeAndPhenotype(false);

        // Creation successful, so add this organism to world
        aWorld.addOrganism(this);
       
    }

    /**
     * Create a new organism and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aWorld World - the enclosing world for this new species
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public Organism(World aWorld,
                    String anElementName,
                    int anElementID,
                    /*com.sun.xml.parser.Parser*/SAXParser anXMLParser,
                    ImportContext importContext)
    {
        if (aWorld == null ||
            anElementName == null ||
            anXMLParser == null ||
            importContext == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Set default values for some fields
        world = aWorld;
        visible = true;
        chromosomesVisible = true;
        nameSuperVisible = false;
        allelesVisible = true;
        dnaVisible = true;
        allelesAlterable = true;
        dnaAlterable = true;
        lockedState = EngineObject.UNLOCKED;
        ploidyNumber = 2;
        name = null;
        species = null;
        sex = NO_SEX;

        // Create vectors
        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        chromosomePairs = new Vector();
        organismImages = new Vector();
        characteristics = new Vector();
        childFamilies = new Vector();

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
            case Elements.NAME_ELEMENT_ID:
            case Elements.PLOIDY_NUMBER_ELEMENT_ID:
            case Elements.SPECIES_ID_ELEMENT_ID:
            case Elements.WORLD_ID_ELEMENT_ID:
            case Elements.VISIBLE_ELEMENT_ID:
            case Elements.NAME_SUPER_VISIBLE_ELEMENT_ID:
            case Elements.ALLELES_VISIBLE_ELEMENT_ID:
            case Elements.DNA_VISIBLE_ELEMENT_ID:
            case Elements.ALLELES_ALTERABLE_ELEMENT_ID:
            case Elements.DNA_ALTERABLE_ELEMENT_ID:
            case Elements.SEX_ELEMENT_ID:
            case Elements.CHARACTERISTIC_IDS_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.ORGANISM_CHROMOSOME_ELEMENT_ID:
                organismChromosome = new OrganismChromosome(this,anElementName,id,
                                       xmlElementContext.getXMLParser(),
                                       xmlElementContext.getImportContext());
                break;

            case Elements.ORGANISM_CHROMOSOME_PAIR_ELEMENT_ID:
                organismChromosomePair = new OrganismChromosomePair(this,anElementName,id,
                                           xmlElementContext.getXMLParser(),
                                           xmlElementContext.getImportContext());
                break;

            case Elements.ORGANISM_IMAGE_ELEMENT_ID:
                organismImage = new OrganismImage(this,anElementName,id,
                                  xmlElementContext.getXMLParser(),
                                  xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("Organism " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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
        int speciesID;

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

            case Elements.NAME_ELEMENT_ID:
                name = xmlElementContext.getValueString();
                break;

            case Elements.PLOIDY_NUMBER_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                ploidyNumber = Integer.valueOf(valueString).intValue();
                break;

            case Elements.SPECIES_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString.equals("null"))
                {
                    species = null;
                }
                else
                {
                    speciesID = Integer.valueOf(valueString).intValue();
                    if (speciesID != EngineObject.NULL_ID)
                    {
                        species = (Species) xmlElementContext.getImportContext().getObject(speciesID);
                    }
                    else
                    {
                        species = null;
                    }
                }
                break;

            case Elements.WORLD_ID_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                // ignore as we already know the world
                break;

            case Elements.VISIBLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                visible = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.NAME_SUPER_VISIBLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                nameSuperVisible = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.ALLELES_VISIBLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                allelesVisible = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.DNA_VISIBLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                dnaVisible = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.ALLELES_ALTERABLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                allelesAlterable = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.DNA_ALTERABLE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                dnaAlterable = Boolean.valueOf(valueString).booleanValue();
                break;

            case Elements.SEX_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setSexAsString(valueString);
                break;

            case Elements.CHARACTERISTIC_IDS_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                if (valueString != null && valueString.length() > 0)
                {
                    int characteristicId;
                    String characteristicIdString;
                    Characteristic aCharacteristic;
                    StringTokenizer parser = new StringTokenizer(valueString,EngineStrings.COMMA);
                    while (parser.hasMoreTokens())
                    {
                        characteristicIdString = parser.nextToken();
                        characteristicId = Integer.valueOf(characteristicIdString).intValue();
                        aCharacteristic = (Characteristic) xmlElementContext.getImportContext().getObject(characteristicId);
                        characteristics.addElement(aCharacteristic);
                    }
                }
                break;

            case Elements.ORGANISM_CHROMOSOME_ELEMENT_ID:
                if (organismChromosome instanceof OrganismChromosome)
                {
                    addChromosome(organismChromosome);
                }
                organismChromosome = null;
                break;

            case Elements.ORGANISM_CHROMOSOME_PAIR_ELEMENT_ID:
                if (organismChromosomePair instanceof OrganismChromosomePair)
                {
                    addOrganismChromosomePair(organismChromosomePair);
                }
                organismChromosomePair = null;
                break;

            case Elements.ORGANISM_IMAGE_ELEMENT_ID:
                if (organismImage instanceof OrganismImage)
                {
                    addOrganismImage(organismImage);
                }
                organismImage = null;
                break;

            case Elements.ORGANISM_ELEMENT_ID:
                // Done with this organism, so add to world,
                // update genotype and phenotype and pop up to enclosing world
                world.addOrganism(this);
                updateGenotypeAndPhenotype(false);
                world.endElement(anElementName);
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
        
    
        // Unlock this organism, as it's about to be deleted
        if (isLocked() == true)
        {
            setLockedState(EngineObject.UNLOCKED);
        }

        if (notifyChange)
        {
            // Notify listeners before deleting
            changes.firePropertyChange(EngineProp.DELETED,FALSE,TRUE);
        }

        // Mark this object as deleted
        deleted = true;

        // Delete instance variables, enabling garbage collection on them
        name = null;

        OrganismChromosomePair aChromosomePair;
        Enumeration eChromosomePairs = chromosomePairs.elements();
        while (eChromosomePairs.hasMoreElements())
        {
            aChromosomePair = (OrganismChromosomePair) eChromosomePairs.nextElement();
            aChromosomePair.delete(notifyChange);
            aChromosomePair = null;
        }
        chromosomePairs.removeAllElements();
        chromosomePairs = null;

        OrganismChromosome aChromosome;
        Vector nonSexChromosomesClone = (Vector) nonSexChromosomes.clone();
        Enumeration eChromosomes = nonSexChromosomesClone.elements();
        //Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            aChromosome.delete(notifyChange);
            aChromosome = null;
        }
        //nonSexChromosomes.removeAllElements();
        nonSexChromosomes = null;
        eChromosomes = null;
        nonSexChromosomesClone = null;
 
        Vector sexChromosomesClone = (Vector) sexChromosomes.clone();
        eChromosomes = sexChromosomesClone.elements();
        //eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
           
            aChromosome.delete(notifyChange);
            aChromosome = null;
        }
        //sexChromosomes.removeAllElements();
        sexChromosomes = null;
        eChromosomes = null;
        sexChromosomesClone = null;
 
        OrganismImage anOrganismImage;
        Vector organismImagesClone = (Vector) organismImages.clone();
        Enumeration eImages = organismImagesClone.elements();
        //Enumeration eImages = organismImages.elements();
        while (eImages.hasMoreElements())
        {
            anOrganismImage = (OrganismImage) eImages.nextElement();
            anOrganismImage.delete(notifyChange);
            anOrganismImage = null;
        }
        //organismImages.removeAllElements();
        organismImages = null;
        eImages = null;
        organismImagesClone = null;

        // Don't delete characteristics, just lose our references to them
        characteristics.removeAllElements();
        characteristics = null;

        // Delete the families that this organism is the parent of
        Family aChildFamily;
        Vector childFamiliesClone = (Vector) childFamilies.clone();
    	Enumeration eChildFamilies = childFamiliesClone.elements();
        //Enumeration eChildFamilies = childFamilies.elements();
        while (eChildFamilies.hasMoreElements())
        {
            aChildFamily = (Family) eChildFamilies.nextElement();
            aChildFamily.delete(notifyChange);
            aChildFamily = null;  
        }
        //childFamilies.removeAllElements();
        childFamilies = null;
        eChildFamilies = null;
        childFamiliesClone = null;

        // Remove this organism from its parent family
        if (parentFamily != null)
        {
            parentFamily.removeChild(this);
            parentFamily = null;
        }
        
        if (newChromosomes != null){
	        for(int i = 0;i<newChromosomes.length;i++)
	        {
	        	newChromosomes[i] = null;
	        }
			newChromosomes = null;
		}

		// Tell unit
		if(enUnit != null)
		{
			enUnit.removeOrganism(this);
		}

        // Tell world
        world.removeOrganism(this);
        world = null;
        species = null;
        id = EngineObject.NULL_ID;
      
    }

    /**
     * Randomly choose either X or Y as the first chromosome
     * of a female.
     *
     * @return 	int - IChromosome.X_CHROMOSOME or IChromosome.Y_CHROMOSOME
    **/
    private int randomlyChooseXorYChromosome()
    {
        int nextInt = random.nextInt();
        
        if (nextInt < 0)
        {
            return IChromosome.X_CHROMOSOME;
        }

        return IChromosome.Y_CHROMOSOME;
    }

    /**
     * Randomly choose sex
     *
     * @return 	int - MALE or FEMALE
    **/
    private int randomlyChooseSex()
    {
        int nextInt = random.nextInt();
        
        if (nextInt < 0)
        {
            return MALE;
        }

        return FEMALE;
    }

    /**
     * Randomly choose one of the two given chromosomes.  This is used when
     * creating a child organism from two parents, as the child receives
     * one of each chromosome number from each parent.<p>
     *
     * @param		anOrganismChromosomeOne OrganismChromosome - first organism chromosome
     * @param		anOrganismChromosomeTwo OrganismChromosome - second organism chromosome
     * @return 		OrganismChromosome - one of the 2 organism chromosomes
    **/
    public static OrganismChromosome randomlyChooseChromosome(OrganismChromosome anOrganismChromosomeOne,
                                                        OrganismChromosome anOrganismChromosomeTwo)
    {
        int nextInt = random.nextInt();
        
        if (nextInt < 0)
        {
            return anOrganismChromosomeOne;
        }

        return anOrganismChromosomeTwo;
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
        OrganismChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            aChromosome.setAutomaticLocked(automaticLocked);
        }

        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            aChromosome.setAutomaticLocked(automaticLocked);
        }

        OrganismImage anOrganismImage;
        Enumeration eImages = organismImages.elements();
        while (eImages.hasMoreElements())
        {
            anOrganismImage = (OrganismImage) eImages.nextElement();
            anOrganismImage.setAutomaticLocked(automaticLocked);
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
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Return if already locked
        if (((lockedState & EngineObject.MANUAL_LOCKED) == EngineObject.MANUAL_LOCKED) && manualLocked)
        {
            return;
        }

        // Use EngineObject implementation for changing the state of this object
        super.setManualLocked(manualLocked);

        // Set manual locked state of children
        OrganismChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            aChromosome.setManualLocked(manualLocked);
        }

        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            aChromosome.setManualLocked(manualLocked);
        }

        OrganismImage anOrganismImage;
        Enumeration eImages = organismImages.elements();
        while (eImages.hasMoreElements())
        {
            anOrganismImage = (OrganismImage) eImages.nextElement();
            anOrganismImage.setManualLocked(manualLocked);
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
        // Return if already deleted
        if (deleted)
        {
            return;
        }

        // Return if already locked
        if (lockedState == aLockedState)
        {
            return;
        }

        // Use EngineObject implementation for changing the locked state of this object
        super.setLockedState(aLockedState);

        // Set locked state of children
        OrganismChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            aChromosome.setLockedState(lockedState);
        }

        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            aChromosome.setLockedState(lockedState);
        }

        OrganismImage anOrganismImage;
        Enumeration eImages = organismImages.elements();
        while (eImages.hasMoreElements())
        {
            anOrganismImage = (OrganismImage) eImages.nextElement();
            anOrganismImage.setLockedState(lockedState);
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
        if (deleted)
        {
			return EngineStrings.ORGANISM_COLON + name + "(deleted)";
        }

        return EngineStrings.ORGANISM_COLON + name;
    }

    /**
     * Update the genotype and phenotype of this organism.<p>
     *
     * In concrete terms, this means:
     *
     *	- Gather all the alleles of this organism in a Vector.
     *
     *	- Loop through the species' genotype to phenotype rules
     *	  determining which rules should fire for this organism.
     *	  The rules use the alleles gathered in the previous
     * 	  step and produce characteristics for this organism.
     *
     *  - Loop through the organismImages of this organism,
     *	  asking each organismImage to update itself to reflect
     *    the current characteristics of this organism.
     *
     * At the end of this process, the organismImage objects for
     * this organism will be updated and all ready to be called
     * when the organism needs to be drawn.<p>
     *
     * @param		notify boolean - notify listeners that genotype and/or phenotype changed?
    **/
    public void updateGenotypeAndPhenotype(boolean notify)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // If we need to notify, save characteristics away and create new vector, else
        // just clear the existing characteristics vector.
        Vector oldCharacteristics = null;
        if (notify == true)
        {
            oldCharacteristics = characteristics;
            characteristics = new Vector();
        }
        else
        {
            characteristics.removeAllElements();
        }

        // Remove and delete all existing organism images
        OrganismImage anOrganismImage;
        if (organismImages != null)
        {
            // Clear out the existing organismsVector, deleting its elements
            Enumeration eOrganismImages = getOrganismImages();
            while (eOrganismImages.hasMoreElements())
            {
                anOrganismImage = (OrganismImage) eOrganismImages.nextElement();
                anOrganismImage.delete();
            }
            organismImages.removeAllElements();
        }
        else
        {
            organismImages = new Vector();
        }

        // Gather the speciesAlleles of this organism by
        // getting each organismAllele and then getting
        // its speciesAllele.
        Vector speciesAlleles = new Vector();
        {
            SpeciesAllele aSpeciesAllele;
            OrganismAllele anOrganismAllele;
            Enumeration eOrganismAlleles;
            OrganismChromosome aChromosome;

            // Non-sex chromosomes
            Enumeration eChromosomes = getNonSexChromosomes();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eChromosomes.nextElement();
                eOrganismAlleles = aChromosome.getOrganismAlleles();
                while (eOrganismAlleles.hasMoreElements())
                {
                    anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                    aSpeciesAllele = anOrganismAllele.getSpeciesAllele();
                    speciesAlleles.addElement(aSpeciesAllele);
                }
            }
    
            // Sex chromosomes
            eChromosomes = getSexChromosomes();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eChromosomes.nextElement();
                eOrganismAlleles = aChromosome.getOrganismAlleles();
                while (eOrganismAlleles.hasMoreElements())
                {
                    anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                    aSpeciesAllele = anOrganismAllele.getSpeciesAllele();
                    speciesAlleles.addElement(aSpeciesAllele);
                }
            }
        }
        
        // Run the rules
        species.runGenotypeToPhenotypeRules(sex, speciesAlleles, characteristics);

        // Update our understanding of how to draw this organism
        // by walking through the species images for this organism's
        // species, generating a new set of organism images.
        SpeciesImage speciesImage;
        Enumeration eSpeciesImages = species.getSpeciesImages();
        while (eSpeciesImages.hasMoreElements())
        {
            speciesImage = (SpeciesImage) eSpeciesImages.nextElement();
            anOrganismImage = new OrganismImage(this,speciesImage);
            // Don't add to organismImages explicitly, as the OrganismImage
            // constructor does that automatically.
        }

        // Determine if phenotype changed
        if (notify && oldCharacteristics != null)
        {
            // Determine if the new characteristics vector is different from the old
            // characteristics vector
            boolean phenotypeChange = false;
            Characteristic aNewCharacteristic, anOldCharacteristic;
            Enumeration eNewCharacteristics = characteristics.elements();
            Enumeration eOldCharacteristics = oldCharacteristics.elements();
            while (phenotypeChange == false &&
                   eNewCharacteristics.hasMoreElements() &&
                   eOldCharacteristics.hasMoreElements())
            {
                aNewCharacteristic = (Characteristic) eNewCharacteristics.nextElement();
                anOldCharacteristic = (Characteristic) eOldCharacteristics.nextElement();
                if (aNewCharacteristic != anOldCharacteristic)
                {
                    phenotypeChange = true;
                }
            }

            // Make sure we didn't run out of characteristics on one but not both of the lists
            if (phenotypeChange == false &&
                (eNewCharacteristics.hasMoreElements() ||
                 eOldCharacteristics.hasMoreElements()))
            {
                phenotypeChange = true;
            }

            // Fire 1 of 2 possible property change events
            if (phenotypeChange == true)
            {
                changes.firePropertyChange(EngineProp.ORGANISM_GENOTYPE_AND_PHENOTYPE,
                                           oldCharacteristics,
                                           characteristics);
            }
            else
            {
                changes.firePropertyChange(EngineProp.ORGANISM_GENOTYPE_AND_NOT_PHENOTYPE,null,null);
            }
        }
    }

    /**
     * Get whether this organism is visible.<p>
     *
     * @return	boolean - whether this organism is visible (true) or not visible (false)
    **/
    public boolean isVisible()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return visible;
    }

    /**
     * Set whether this organism is visible.<p>
     *
     * @param	TorF boolean - visible (true) or not visible (false)
    **/
    public void setVisible(boolean TorF)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (TorF != visible)
        {
            boolean oldVisible = visible;
            visible = TorF;
    		if (!visible)
    			chromosomesVisible = false;
            changes.firePropertyChange(EngineProp.VISIBLE,
                                       new Boolean(oldVisible),
                                       new Boolean(visible));
        }
    }

    /**
     * Get whether this organism's name is super visible, meaning that the
     * name should be shown even when a view isn't showing names in general.<p>
     *
     * @return	boolean - whether this organism's name is super visible (true) or not super visible (false)
    **/
    public boolean isNameSuperVisible()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return nameSuperVisible;
    }

    /**
     * Set whether this organism's name is super visible, meaning that the name
     * should be shown even when a view isn't showing names in general.
     * The default is false, meaning the name isn't shown if a view isn't
     * showing names by default.<p>
     *
     * @param	TorF boolean - name visible (true) or not visible (false)
    **/
    public void setNameSuperVisible(boolean TorF)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (TorF != nameSuperVisible)
        {
            boolean oldNameSuperVisible = nameSuperVisible;
            nameSuperVisible = TorF;
    
            changes.firePropertyChange(EngineProp.NAME_SUPER_VISIBLE,
                                       new Boolean(oldNameSuperVisible),
                                       new Boolean(nameSuperVisible));
        }
    }

    /**
     * Get whether this organism's alleles are visible.<p>
     *
     * @return	boolean - whether this organism's alleles are visible (true) or not visible (false)
    **/
    public boolean isAllelesVisible()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return allelesVisible;
    }

    /**
     * Set whether this organism's alleles are visible.<p>
     *
     * @param	TorF boolean - visible (true) or not visible (false)
    **/
    public void setAllelesVisible(boolean TorF)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (TorF != allelesVisible)
        {
            boolean oldAllelesVisible = allelesVisible;
            allelesVisible = TorF;
    
            changes.firePropertyChange(EngineProp.ALLELES_VISIBLE,
                                       new Boolean(oldAllelesVisible),
                                       new Boolean(allelesVisible));
        }
    }

    /**
     * Get whether this organism's DNA is visible.<p>
     *
     * @return	boolean - whether this organism's DNA is visible (true) or not visible (false)
    **/
    public boolean isDNAVisible()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return dnaVisible;
    }

    /**
     * Set whether this organism's DNA is visible.<p>
     *
     * @param	TorF boolean - visible (true) or not visible (false)
    **/
    public void setDNAVisible(boolean TorF)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (TorF != dnaVisible)
        {
            boolean oldDNAVisible = dnaVisible;
            dnaVisible = TorF;
    
            changes.firePropertyChange(EngineProp.DNA_VISIBLE,
                                       new Boolean(oldDNAVisible),
                                       new Boolean(dnaVisible));
        }
    }

    /**
     * Get whether this organism's alleles are visible.<p>
     *
     * @return	boolean - whether this organism's alleles are visible (true) or not visible (false)
    **/
    public boolean isAllelesAlterable()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return allelesAlterable;
    }

    /**
     * Set whether this organism's alleles are alterable.<p>
     *
     * @param	TorF boolean - alterable (true) or not alterable (false)
    **/
    public void setAllelesAlterable(boolean TorF)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (TorF != allelesAlterable)
        {
            boolean oldAllelesAlterable = allelesAlterable;
            allelesAlterable = TorF;
    
            changes.firePropertyChange(EngineProp.ALLELES_ALTERABLE,
                                       new Boolean(oldAllelesAlterable),
                                       new Boolean(allelesAlterable));
        }
    }

    /**
     * Get whether this organism's DNA is alterable.<p>
     *
     * @return	boolean - whether this organism's DNA is alterable (true) or not alterable (false)
    **/
    public boolean isDNAAlterable()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return dnaAlterable;
    }

    /**
     * Set whether this organism's DNA is visible.<p>
     *
     * @param	TorF boolean - visible (true) or not visible (false)
    **/
    public void setDNAAlterable(boolean TorF)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (TorF != dnaAlterable)
        {
            boolean oldDNAAlterable = dnaAlterable;
            dnaAlterable = TorF;
    
            changes.firePropertyChange(EngineProp.DNA_ALTERABLE,
                                       new Boolean(oldDNAAlterable),
                                       new Boolean(dnaAlterable));
        }
    }

    /**
     * Returns ploidy number of this organism.<p>
     *
     * @return		int - ploidy number of this organism (1, 2 or 3)
    **/
    public int getPloidyNumber()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return ploidyNumber;
    }

    /**
     * Return the name of this organism.<p>
     *
     * @return		String - name of this organism, may not be null
    **/
    public String getName()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return name;
    }

    /**
     * Set the name of this organism.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - new name, may not be null
     * @exception	IllegalArgumentException - input arguments illegal
    **/
    public void setName(String aName)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Return immediately if aName equals the current name
        if (aName != null && name != null && aName.equals(name))
        {
            return;
        }

        // Validate input arguments
        if (aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        String oldName = name;
        name = new String(aName);

        // Notify listeners
        changes.firePropertyChange(EngineProp.NAME,oldName,name);
    }

    /**
     * Return the species of this organism, if it is known.  If
     * it isn't known, then null is returned.<p>
     *
     * @return		Species - species of this organism, may be null
    **/
    public Species getSpecies()
    {
        return species;
    }

    /**
     * Set the species of this organism.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.SPECIES.<p>
     *
     * @param		aSpecies Species - the new species of this organism, may be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setSpecies(Species aSpecies)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (aSpecies != species)
        {
            Species oldSpecies = species;
            species = aSpecies;

            // Notify listeners
            changes.firePropertyChange(EngineProp.SPECIES,oldSpecies,species);
        }
    }

    /**
     * Return the world containing this organism.  May not be null.<p>
     *
     * @return 		World - the world containing this organism, may not be null
    **/
    public World getWorld()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return world;
    }

    /**
     * Return the number of non-sex chromosomes in this organism.<p>
     *
     * @return		int - the number of non-sex chromosomes in this organism
    **/
    public int getNumberOfNonSexChromosomes()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return nonSexChromosomes.size();
    }

    /**
     * Returns an enumeration over the vector of non-sex chromosomes in this organism.<p>
     *
     * The vector of non-sex chromosomes is cloned and the enumeration is created for the
     * clone, so it is safe to modify the vector of chromosomes (by creating new
     * ones, moving them around, etc.) while using this returned enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the non-sex chromosomes in this organism, never null
    **/
    public Enumeration getNonSexChromosomes()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Clone non-sex chromosomes vector to make modifications safe during stepping
        Vector nonSexChromosomesClone = (Vector) nonSexChromosomes.clone();
        return nonSexChromosomesClone.elements();
    }

    /**
     * Return the number of sex chromosomes in this organism.<p>
     *
     * @return		int - the number of sex chromosomes in this organism
    **/
    public int getNumberOfSexChromosomes()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return sexChromosomes.size();
    }

    /**
     * Returns an enumeration over the vector of sex chromosomes in this organism.<p>
     *
     * The vector of sex chromosomes is cloned and the enumeration is created for the
     * clone, so it is safe to modify the vector of chromosomes (by creating new
     * ones, moving them around, etc.) while using this returned enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the sex chromosomes in this organism
    **/
    public Enumeration getSexChromosomes()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Clone sex chromosomes vector to make modifications safe during stepping
        Vector sexChromosomesClone = (Vector) sexChromosomes.clone();
       
        return sexChromosomesClone.elements();
    }

    /**
     * Adds an organism chromosome (sex or non-sex) to the organism.<p>
     *
     * Package protected because this is only called from the
     * OrganismChromosome constructor.  Creating a OrganismChromosome
     * automatically adds it to the organism via this method.<p>
     *
     * @param		aChromosome OrganismChromosome - a new organism chromosome, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addChromosome(OrganismChromosome aChromosome)
    {
        if (aChromosome == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        else if (aChromosome.isSexChromosome() == true)
        {
            sexChromosomes.addElement(aChromosome);
        }
        else
        {
            nonSexChromosomes.addElement(aChromosome);
        }

        // Notify listeners
        changes.firePropertyChange(EngineProp.ORGANISM_CHROMOSOME_ADDED, null, aChromosome);
    }

    /**
     * Removes a species chromosome (sex or non-sex) from the organism.<p>
     *
     * Package protected because this is only called from the OrganismChromosome
     * delete method.  Deleting a OrganismChromosome automatically removes it from
     * the organism via this method.<p>
     *
     * @param		aChromosome OrganismChromosome - an organism chromosome, may not be null
     * @return		boolean indicating whether or not the chromosome was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeChromosome(OrganismChromosome aChromosome)
    {
        if (aChromosome == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = false;

        if (aChromosome.isSexChromosome() == true)
        {
            result = sexChromosomes.removeElement(aChromosome);
        }
        else
        {
            result = nonSexChromosomes.removeElement(aChromosome);
        }

        // Notify listeners if chromosome removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.ORGANISM_CHROMOSOME_REMOVED, null, aChromosome);
        }

        return result;
    }

    /**
     * Get the number of chromosomes, both sex and non-sex.
     *
     * @return		int - the number of sex chromosomes in this organism
    **/
    public int getNumberOfChromosomes()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return nonSexChromosomes.size() + sexChromosomes.size();
    }

    /**
     * Returns an enumeration over the vector of all chromosomes, sex and non-sex,
     * in this organism.<p>
     *
     * @return		Enumeration - an enumeration over all chromosomes in this organism
    **/
    public Enumeration getChromosomes()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Clone non-sex chromosomes vector
        Vector chromosomes = (Vector) nonSexChromosomes.clone();

        // Add sex chromosomes to vector
        OrganismChromosome anOrganismChromosome;
        Enumeration eSexChromosomes = sexChromosomes.elements();
        while (eSexChromosomes.hasMoreElements())
        {
            anOrganismChromosome = (OrganismChromosome) eSexChromosomes.nextElement();
            chromosomes.addElement(anOrganismChromosome);
        }

        return chromosomes.elements();
    }

    /**
     * Adds an organism chromosome pair to an organism.<p>
     *
     * Package protected because this is only called from the
     * OrganismChromosomePair constructor.  Creating a OrganismChromosomePair
     * automatically adds it to the organism via this method.<p>
     *
     * @param		aChromosomePair OrganismChromosomePair - a new organism chromosome pair, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addOrganismChromosomePair(OrganismChromosomePair aChromosomePair)
    {
        if (aChromosomePair == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        chromosomePairs.addElement(aChromosomePair);

        // Don't notify listeners
    }

    /**
     * Removes an organism chromosome pair from the organism.<p>
     *
     * Package protected because this is only called from the OrganismChromosomePair
     * delete method.  Deleting a OrganismChromosome automatically removes it from
     * the organism via this method.<p>
     *
     * @param		aChromosome OrganismChromosome - an organism chromosome, may not be null
     * @return		boolean indicating whether or not the chromosome was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeOrganismChromosomePair(OrganismChromosomePair aChromosomePair)
    {
        if (aChromosomePair == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = chromosomePairs.removeElement(aChromosomePair);

        // Don't bother notifying

        return result;
    }

    /**
     * Get the number of organism chromosome pairs, both sex and non-sex.
     *
     * @return		int - the number of organism chromosome pairs in this organism
    **/
    public int getNumberOfOrganismChromosomePairs()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return chromosomePairs.size();
    }

    /**
     * Returns an enumeration over the vector of all organism chromosome pairs
     * in this organism.<p>
     *
     * @return		Enumeration - an enumeration over all chromosomes in this organism
    **/
    public Enumeration getOrganismChromosomePairs()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return chromosomePairs.elements();
    }

    /**
     * Get a vector of organism alleles in this organism for the given gene.<p>
     *
     * @param		aGene Gene - a gene, if null then an empty vector is returned
     * @return		Vector - a vector of alleles, empty if no alleles found or aGene null
    **/
    public Vector getGeneOrganismAlleles(Gene aGene)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        Vector alleles = new Vector();

        if (aGene != null)
        {
            OrganismAllele anOrganismAllele;
            Enumeration eOrganismAlleles;
            OrganismChromosome aChromosome;

            // Try non-sex chromosomes
            Enumeration eChromosomes = nonSexChromosomes.elements();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eChromosomes.nextElement();
                eOrganismAlleles = aChromosome.getOrganismAlleles();
                while (eOrganismAlleles.hasMoreElements())
                {
                    anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                    if (anOrganismAllele.getGene() == aGene)
                    {
                        alleles.addElement(anOrganismAllele);
                    }
                }
            }
            
            // Try sex chromosomes
            eChromosomes = sexChromosomes.elements();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eChromosomes.nextElement();
                eOrganismAlleles = aChromosome.getOrganismAlleles();
                while (eOrganismAlleles.hasMoreElements())
                {
                    anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                    if (anOrganismAllele.getGene() == aGene)
                    {
                        alleles.addElement(anOrganismAllele);
                    }
                }
            }
        }

        return alleles;
    }

    /**
     * Get a vector of organism alleles in this organism for the gene with the given name.<p>
     *
     * @param		aGeneName String - a name of a gene, if null then an empty vector is returned
     * @return		Vector - a vector of alleles, empty if no alleles found or aGeneName null
    **/
    public Vector getGeneOrganismAlleles(String aGeneName)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        Vector alleles = new Vector();

        if (aGeneName != null)
        {
            OrganismAllele anOrganismAllele;
            Enumeration eOrganismAlleles;
            OrganismChromosome aChromosome;

            // Try non-sex chromosomes
            Enumeration eChromosomes = nonSexChromosomes.elements();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eChromosomes.nextElement();
                eOrganismAlleles = aChromosome.getOrganismAlleles();
                while (eOrganismAlleles.hasMoreElements())
                {
                    anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                    if (anOrganismAllele.getGene().getName().equals(aGeneName))
                    {
                        alleles.addElement(anOrganismAllele);
                    }
                }
            }
            
            // Try sex chromosomes
            eChromosomes = sexChromosomes.elements();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eChromosomes.nextElement();
                eOrganismAlleles = aChromosome.getOrganismAlleles();
                while (eOrganismAlleles.hasMoreElements())
                {
                    anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                    if (anOrganismAllele.getGene().getName().equals(aGeneName))
                    {
                        alleles.addElement(anOrganismAllele);
                    }
                }
            }
        }

        return alleles;
    }
    
    public OrganismAllele findOrganismAllele(Vector alleles, String alleleText)
    {
        int length = alleles.size();
        for (int i = 0; i < length; i++)
        {
            OrganismAllele allele = (OrganismAllele) alleles.elementAt(i);
            if (allele.getTextSymbol().equals(alleleText))
                return allele;
        }
        return null;
    }
    
    /**
     * Get an organism allele in this organism that has the gene with the given name and
     *  with the given allele symbol<p>
     *
     * @param		geneName String - a name of a gene, if null then null is returned
     * @param		alleleText String - an allele symbol, if null then null returned
     * @return		OrganismAllele - an organism allele or null if not found
    **/
    public OrganismAllele getGeneOrganismAllele(String geneName, String alleleText)
    {
        return findOrganismAllele(getGeneOrganismAlleles(geneName), alleleText);
    }

    /**
     * Get an organism allele in this organism that has the given gene and
     *  with the given allele symbol<p>
     *
     * @param		gene Gene - a gene, if null then null is returned
     * @param		alleleText String - an allele symbol, if null then null returned
     * @return		OrganismAllele - an organism allele or null if not found
    **/
    public OrganismAllele getGeneOrganismAllele(Gene gene, String alleleText)
    {
        return findOrganismAllele(getGeneOrganismAlleles(gene), alleleText);
    }

    /**
     * Find an organism allele pair given an organism allele
     *
     * @param		allele OrganismAllele - an allele , if null then null returned
     * @return		OrganismAllelePair - an organism allele pair or null if not found
    **/
    public OrganismAllelePair findAllelePair(OrganismAllele allele)
    {
        OrganismChromosome chromo = allele.getOrganismChromosome();
        Enumeration chromoPairs = getOrganismChromosomePairs();
        while(chromoPairs.hasMoreElements())
        {
            OrganismChromosomePair chromoPair = (OrganismChromosomePair) chromoPairs.nextElement();
            OrganismChromosome firstChromo = chromoPair.getFirstOrganismChromosome();
            OrganismChromosome secondChromo = chromoPair.getSecondOrganismChromosome();
            if ((chromo == firstChromo) || (chromo == secondChromo))
            {
                Enumeration allelePairs = chromoPair.getOrganismAllelePairs();
                while(allelePairs.hasMoreElements())
                {
                    OrganismAllelePair allelePair = (OrganismAllelePair) allelePairs.nextElement();
                    OrganismAllele firstAllele = allelePair.getFirstOrganismAllele();
                    OrganismAllele secondAllele = allelePair.getSecondOrganismAllele();
                    if ((allele == firstAllele) || (allele == secondAllele))
                    {
                        return allelePair;
                    }
                }
            }
        }
        return null;
    }

    public boolean hasCharacteristics(String [] characteristics, String [] traits)
    {
        int n = traits.length;
        for (int i = 0; i < n; i++)
        {
            Characteristic characteristic = getCharacteristicOfTrait(traits[n]);
            if (characteristic.getName().equals(characteristics[n]))
                continue;
            else
                return false;
        }
        return true;
    }
    
    /**
     * Set an allele text symbol of an allele of the given gene on the given chromosome.
     *
     * @param		aChromosomeAorB int - chromosome (IChromosome.A_CHROMOSOME or IChromosome.B_CHROMOSOME)
     * @param		aGeneName String - the gene name (e.g. "Horns"), may not be null
     * @param		aNewAlleleTextSymbol String - new allele text symbol (e.g. "H"), may not be null
    **/
    public void setAllele(int aChromosomeAorB, String aGeneName, String aNewAlleleTextSymbol)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if ((aChromosomeAorB != IChromosome.A_CHROMOSOME &&
             aChromosomeAorB != IChromosome.B_CHROMOSOME) ||
            aGeneName == null ||
            aNewAlleleTextSymbol == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean changeNextAllele = false;
        if (aChromosomeAorB == IChromosome.A_CHROMOSOME)
        {
            changeNextAllele = true;
        }

        OrganismAllele anOrganismAllele;
        Enumeration eOrganismAlleles;
        OrganismChromosome aChromosome;

        // Try non-sex chromosomes
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            eOrganismAlleles = aChromosome.getOrganismAlleles();
            while (eOrganismAlleles.hasMoreElements())
            {
                anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                if (anOrganismAllele.getGene().getName().equals(aGeneName))
                {
                    if (changeNextAllele)
                    {
                        anOrganismAllele.setTextSymbol(aNewAlleleTextSymbol);
                        updateGenotypeAndPhenotype(true);
                        return;
                    }
                    else
                    {
                        changeNextAllele = true;
                    }
                }
            }
        }
        
        // Try sex chromosomes
        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (OrganismChromosome) eChromosomes.nextElement();
            eOrganismAlleles = aChromosome.getOrganismAlleles();
            while (eOrganismAlleles.hasMoreElements())
            {
                anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                if (anOrganismAllele.getGene().getName().equals(aGeneName))
                {
                    if (changeNextAllele)
                    {
                        anOrganismAllele.setTextSymbol(aNewAlleleTextSymbol);
                        updateGenotypeAndPhenotype(true);
                        return;
                    }
                    else
                    {
                        changeNextAllele = true;
                    }
                }
            }
        }
    }

    /**
     * Get the sex of this organism.<p>
     *
     * @return		int - Organism.MALE, Organism.FEMALE or Organism.NO_SEX
    **/
    public int getSex()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return sex;
    }

    /**
     * Get the sex of this organism as a string.<p>
     *
     * @return		String - Organism.MALE_STRING, Organism.FEMALE_STRING or Organism.NO_SEX_STRING
    **/
    public String getSexAsString()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (sex == Organism.MALE)
        {
            return Organism.MALE_STRING;
        }
        else if (sex == Organism.FEMALE)
        {
            return Organism.FEMALE_STRING;
        }

        // Default to no sex
        return Organism.NO_SEX_STRING;
    }

    /**
     * Set the sex of this organism as a string.  Private because
     * this can only be called when reading in an organism from
     * a file.<p>
     *
     * @param		aSexString String - the sex as a string
    **/
    private void setSexAsString(String aSexString)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (aSexString == null)
        {
            return;
        }
        else if (aSexString.equals(Organism.MALE_STRING))
        {
            sex = Organism.MALE;
        }
        else if (aSexString.equals(Organism.FEMALE_STRING))
        {
            sex = Organism.FEMALE;
        }
        else if (aSexString.equals(Organism.NO_SEX_STRING))
        {
            sex = Organism.NO_SEX;
        }
    }

    /**
     * Return the number of organism images in this organism.<p>
     *
     * @return		int - the number of organism images in this organism
    **/
    public int getNumberOfOrganismImages()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return organismImages.size();
    }

    /**
     * Returns an enumeration over the vector of organism images in this organism.<p>
     *
     * @return		Enumeration - an enumeration over the organism images in this organism
    **/
    public Enumeration getOrganismImages()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Clone organism images vector to make modifications safe during stepping
        Vector organismImagesClone = (Vector) organismImages.clone();
        return organismImagesClone.elements();
    }

    /**
     * Adds an organism image to this organism.<p>
     *
     * Package protected because this is only called from the
     * OrganismImage constructor.  Creating an OrganismImage
     * automatically adds it to the organism via this method.<p>
     *
     * @param		anOrganismImage OrganismImage - a new organism image, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addOrganismImage(OrganismImage anOrganismImage)
    {
        if (anOrganismImage == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        organismImages.addElement(anOrganismImage);

        // Notify listeners
        changes.firePropertyChange(EngineProp.ORGANISM_IMAGE_ADDED, null, anOrganismImage);
    }

    /**
     * Removes an organism image from the organism.<p>
     *
     * Package protected because this is only called from the OrganismImage
     * delete method.  Deleting an OrganismImage automatically removes it from
     * the organism via this method.<p>
     *
     * @param		anOrganismImage OrganismImage - an organism image, may not be null
     * @return		boolean indicating whether or not the organism image was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeOrganismImage(OrganismImage anOrganismImage)
    {
        if (anOrganismImage == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = organismImages.removeElement(anOrganismImage);

        // Notify listeners if organism image removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.ORGANISM_IMAGE_REMOVED, null, anOrganismImage);
        }

        return result;
    }

    /**
     * Returns true or false on whether this organism contains a fatal characteristic.
     * I believe this is synonymous with whether the organism is alive or dead due to
     * genetic reasons, but I'm not sure.
     *
     * @return		boolean - contains fatal characteristic (true) or does not (false)
    **/
    public boolean containsFatalCharacteristic()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        boolean containsFatalCharacteristic = false;

        if (characteristics != null)
        {
            Characteristic aCharacteristic;
            Enumeration eCharacteristics = characteristics.elements();
            while (eCharacteristics.hasMoreElements() &&
                   containsFatalCharacteristic == false)
            {
                aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                containsFatalCharacteristic = aCharacteristic.isFatal();
            }
        }

        return containsFatalCharacteristic;
    }

    /**
     * Returns true or false on whether this organism contains the given characteristic.
     *
     * @param		aCharacteristic Characteristic - a characteristic to look for, if null then false is returned
     * @return		boolean - contains characteristic (true) or does not (false)
    **/
    public boolean containsCharacteristic(Characteristic aCharacteristic)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (aCharacteristic == null)
        {
            return false;
        }

        boolean containsCharacteristic = false;

        if (characteristics != null)
        {
            Characteristic anOrganismCharacteristic;
            Enumeration eCharacteristics = characteristics.elements();
            while (eCharacteristics.hasMoreElements() &&
                   containsCharacteristic == false)
            {
                anOrganismCharacteristic = (Characteristic) eCharacteristics.nextElement();
                if (anOrganismCharacteristic == aCharacteristic)
                {
                    containsCharacteristic = true;
                }
            }
        }

        return containsCharacteristic;
    }

    /**
     * Returns true or false on whether this organism contains the a characteristic with the given name.
     * Note that this method may return a false positive answer if there are 2 or more characteristics
     * with the same name.<p>
     *
     * @param		aCharacteristicName String - a characteristic name to look for, if null then false is returned
     * @return		boolean - contains characteristic (true) or does not (false)
    **/
    public boolean containsCharacteristic(String aCharacteristicName)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (aCharacteristicName == null)
        {
            return false;
        }

        boolean containsCharacteristic = false;

        if (characteristics != null)
        {
            Characteristic anOrganismCharacteristic;
            Enumeration eCharacteristics = characteristics.elements();
            while (eCharacteristics.hasMoreElements() &&
                   containsCharacteristic == false)
            {
                anOrganismCharacteristic = (Characteristic) eCharacteristics.nextElement();
                if (aCharacteristicName.equals(anOrganismCharacteristic.getName()))
                {
                    containsCharacteristic = true;
                }
            }
        }

        return containsCharacteristic;
    }

    /**
     * Returns the characteristic of a given trait for this organism.  Returns null
     * if the input trait is null or if no characteristic is found for the input trait.
     *
     * @param		aTrait Trait - a trait, if null then null is returned
     * @return		Characteristic - the characteristic of the given trait, null if none found
    **/
    public Characteristic getCharacteristicOfTrait(Trait aTrait)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (aTrait == null)
        {
            return null;
        }

        Characteristic foundCharacteristic = null;

        if (characteristics != null)
        {
            Characteristic anOrganismCharacteristic;
            Enumeration eCharacteristics = characteristics.elements();
            while (eCharacteristics.hasMoreElements() &&
                   foundCharacteristic == null)
            {
                anOrganismCharacteristic = (Characteristic) eCharacteristics.nextElement();
                if (anOrganismCharacteristic.getTrait() == aTrait)
                {
                    foundCharacteristic = anOrganismCharacteristic;
                }
            }
        }

        return foundCharacteristic;
    }

    /**
     * Returns the characteristic of a trait of the given name for this organism.  Returns null
     * if the input trait name is null or if no characteristic is found for the input trait name.
     *
     * @param		aTraitName String - a trait name, if null then null is returned
     * @return		Characteristic - the characteristic of a trait with the given name, null if none found
    **/
    public Characteristic getCharacteristicOfTrait(String aTraitName)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (aTraitName == null)
        {
            return null;
        }

        Characteristic foundCharacteristic = null;

        if (characteristics != null)
        {
            Characteristic aCharacteristic;
            Trait aTrait;
            Enumeration eCharacteristics = characteristics.elements();
            while (eCharacteristics.hasMoreElements() &&
                   foundCharacteristic == null)
            {
                aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                aTrait = aCharacteristic.getTrait();
                if (aTrait.getName().equals(aTraitName))
                {
                    foundCharacteristic = aCharacteristic;
                }
            }
        }

        return foundCharacteristic;
    }

    /**
     * Returns an enumeration over the vector of characteristics of this organism.<p>
     *
     * @return		Enumeration - an enumeration over the characteristics of this organism
    **/
    public Enumeration getCharacteristics()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        Vector characteristicsClone = (Vector) characteristics.clone();
        return characteristicsClone.elements();
    }
    
    public String [] compareCharacteristics(Organism organism)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        Vector diffs = new Vector();
        Enumeration eFirst = characteristics.elements();
        Enumeration eSecond = organism.getCharacteristics();
        while (eFirst.hasMoreElements() && eSecond.hasMoreElements())
        {
            Characteristic first = (Characteristic) eFirst.nextElement();
            Characteristic second = (Characteristic) eSecond.nextElement();
            if (first.getName().equals(second.getName()))
                continue;
            diffs.addElement(second.getName());
        }
        if (diffs.size() == 0)
            return null;
        else
        {
            String [] diffNames = new String[diffs.size()];
            diffs.copyInto(diffNames);
            return diffNames;
        }
    }

    /**
     * Returns the actual Vector of characteristics for this organism.  This is used
     * in SpeciesImage to determine which images to use when drawing the organism.  It
     * is not public synchronized because code outside of the engine package should not be calling it.
     *
     * @return		Vector - the vector of Characteristics in this organism
    **/
    Vector getCharacteristicsVector()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return characteristics;
    }

    /**
     * Return the number of characteristics of this organism.<p>
     *
     * @return		int - the number of characteristics of this organism
    **/
    public int getNumberOfCharacteristics()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return characteristics.size();
    }

    /**
     * Adds a characteristic to the organism.<p>
     *
     * @param		aCharacteristic Characteristic - a new characteristic, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addCharacteristic(Characteristic aCharacteristic)
    {
        characteristics.addElement(aCharacteristic);
    }

    /**
     * Removes a characteristic from the organism.<p>
     *
     * @param		aCharacteristic Characteristic - a characteristic, may not be null
     * @return		boolean indicating whether or not the characteristic was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeCharacteristic(Characteristic aCharacteristic)
    {
        if (aCharacteristic == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = characteristics.removeElement(aCharacteristic);

        return result;
    }

    /**
     * Get the parent family for this organism.  May be null.<p>
     *
     * @return		Family - the parent family of this organism, may be null
    **/
    public Family getParentFamily()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return parentFamily;
    }

    /**
     * Set the parent family for this organism.  May be set to null.<p>
     *
     * @param		aFamily Family - the new parent family of this organism, may be null
    **/
    public void setParentFamily(Family aFamily)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Return immediately if no change occurring
        if (aFamily == parentFamily)
        {
            return;
        }

        Family oldParentFamily = parentFamily;
        parentFamily = aFamily;

        // Update lock state
        if (isAutomaticLocked() == false &&
            oldParentFamily == null &&
            parentFamily != null)
        {
            setAutomaticLocked(true);
        }
        else if (isAutomaticLocked() == true &&
                 oldParentFamily != null &&
                 parentFamily == null &&
                 childFamilies.size() == 0)
        {
            setAutomaticLocked(false);
        }

        // Notify listeners
        changes.firePropertyChange(EngineProp.PARENT_FAMILY, oldParentFamily, aFamily);
    }

    /**
     * Return the number of child families in this organism.<p>
     *
     * @return		int - the number of child families in this organism
    **/
    public int getNumberOfChildFamilies()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return childFamilies.size();
    }

    /**
     * Returns an enumeration over the vector of child families in this organism.<p>
     *
     * @return		Enumeration - an enumeration over the child families in this organism
    **/
    public Enumeration getChildFamilies()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        // Clone child families vector to make modifications safe during stepping
        Vector childFamiliesClone = (Vector) childFamilies.clone();
        return childFamiliesClone.elements();
    }

    /**
     * Adds a child family to this organism.<p>
     *
     * Package protected because this is only called from the
     * Family constructor.  Creating a Family
     * automatically adds it to the organism via this method.<p>
     *
     * @param		aFamily Family - a new family, may not be null
     * @exception	IllegalArgumentException - input argument illegal
    **/
    void addChildFamily(Family aFamily)
    {
        if (aFamily == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        childFamilies.addElement(aFamily);

        // Update lock state
        if (isAutomaticLocked() == false)
        {
            setAutomaticLocked(true);
        }

        // Notify listeners
        changes.firePropertyChange(EngineProp.CHILD_FAMILY_ADDED, null, aFamily);
    }

    /**
     * Removes a child family from the organism.<p>
     *
     * Package protected because this is only called from the Family
     * delete method.  Deleting a Family automatically removes it from
     * the organism via this method.<p>
     *
     * @param		aFamily Family - a family, may not be null
     * @return		boolean indicating whether or not the family was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeChildFamily(Family aFamily)
    {
        if (aFamily == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean result = childFamilies.removeElement(aFamily);

        // Notify listeners if organism image removed
        if (result == true)
        {
            // Update lock state
            if (isAutomaticLocked() == true &&
                childFamilies.size() == 0)
            {
                setAutomaticLocked(false);
            }

            changes.firePropertyChange(EngineProp.CHILD_FAMILY_REMOVED, null, aFamily);
        }

        return result;
    }

    /**
     * Get the generation of this organism.  If this organism is not in a family, zero
     * is returned.  Otherwise we get the generation of this organism's family.<p>
     *
     * @return		int - generation of this organism
    **/
    public int getGeneration()
    {
        if (parentFamily == null)
        {
            return 0;
        }

        return parentFamily.getGeneration();
    }
    
    public String getGenotypeAsString()
    {
    	if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }
        Vector differentTextSymbol = new Vector();
        Vector textSymbolVector = new Vector();
        Enumeration theChromosomes = getChromosomes();
        while (theChromosomes.hasMoreElements())
        {
        	Enumeration theOrganismAlleles =((OrganismChromosome)(theChromosomes.nextElement())).getOrganismAlleles();
        	while (theOrganismAlleles.hasMoreElements())
        	{
        		String tempStr = ((OrganismAllele)(theOrganismAlleles.nextElement())).getTextSymbol();
        		if (!differentTextSymbol.contains(tempStr.toLowerCase()))
        		{
        			differentTextSymbol.addElement(tempStr.toLowerCase());
        			textSymbolVector.addElement(tempStr);
        		}
        		else 
        		{
        			for(int i = 0; i<textSymbolVector.size();i++)
        			{
        				if ((tempStr.toLowerCase()).equals(((String)(textSymbolVector.elementAt(i))).toLowerCase()))
        				{
        					String tempStr2 =  (String)(textSymbolVector.elementAt(i))+tempStr;
        					textSymbolVector.setElementAt(tempStr2,i);
        				}
        			}
        		}		
        		
        	}
        }
        String totalStr =(String)(textSymbolVector.elementAt(0));
        for (int i = 1; i<textSymbolVector.size();i++)
        {
        	totalStr = totalStr+","+ textSymbolVector.elementAt(i);
        }
        return totalStr;
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
        stream.println("<" + Elements.ORGANISM_ELEMENT_NAME + ">");

        // Version
        stream.println("<" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">" + schemaVersion +
                          "</" + Elements.SCHEMA_VERSION_ELEMENT_NAME + ">");

        // ID
        stream.println("<" + Elements.ID_ELEMENT_NAME + ">" + id +
                          "</" + Elements.ID_ELEMENT_NAME + ">");

        // Deleted
        stream.println("<" + Elements.DELETED_ELEMENT_NAME + ">" + deleted +
                          "</" + Elements.DELETED_ELEMENT_NAME + ">");

        // Name
        if (name == null)
        {
            stream.println("<" + Elements.NAME_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.NAME_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.NAME_ELEMENT_NAME + ">" + name +
                              "</" + Elements.NAME_ELEMENT_NAME + ">");
        }

        // Ploidy Number
        stream.println("<" + Elements.PLOIDY_NUMBER_ELEMENT_NAME + ">" + ploidyNumber +
                          "</" + Elements.PLOIDY_NUMBER_ELEMENT_NAME + ">");


        // Species ID
        if (species == null)
        {
            stream.println("<" + Elements.SPECIES_ID_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.SPECIES_ID_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.SPECIES_ID_ELEMENT_NAME + ">" + species.getID() +
                              "</" + Elements.SPECIES_ID_ELEMENT_NAME + ">");
        }

        // Visible
        stream.println("<" + Elements.VISIBLE_ELEMENT_NAME + ">" + visible +
                          "</" + Elements.VISIBLE_ELEMENT_NAME + ">");

        // Name super visible
        stream.println("<" + Elements.NAME_SUPER_VISIBLE_ELEMENT_NAME + ">" + nameSuperVisible +
                          "</" + Elements.NAME_SUPER_VISIBLE_ELEMENT_NAME + ">");

        // Alleles visible
        stream.println("<" + Elements.ALLELES_VISIBLE_ELEMENT_NAME + ">" + allelesVisible +
                          "</" + Elements.ALLELES_VISIBLE_ELEMENT_NAME + ">");

        // DNA visible
        stream.println("<" + Elements.DNA_VISIBLE_ELEMENT_NAME + ">" + dnaVisible +
                          "</" + Elements.DNA_VISIBLE_ELEMENT_NAME + ">");

        // Alleles alterable
        stream.println("<" + Elements.ALLELES_ALTERABLE_ELEMENT_NAME + ">" + allelesAlterable +
                          "</" + Elements.ALLELES_ALTERABLE_ELEMENT_NAME + ">");

        // DNA alterable
        stream.println("<" + Elements.DNA_ALTERABLE_ELEMENT_NAME + ">" + dnaAlterable +
                          "</" + Elements.DNA_ALTERABLE_ELEMENT_NAME + ">");

        // World ID
        stream.println("<" + Elements.WORLD_ID_ELEMENT_NAME + ">" + world.getID() +
                          "</" + Elements.WORLD_ID_ELEMENT_NAME + ">");

        // Sex
        stream.println("<" + Elements.SEX_ELEMENT_NAME + ">" +
                          getSexAsString() +
                          "</" + Elements.SEX_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Characteristic IDs
        stream.print("<" + Elements.CHARACTERISTIC_IDS_ELEMENT_NAME + ">");
        {
            Characteristic aCharacteristic;
            Enumeration eCharacteristics = characteristics.elements();
            while (eCharacteristics.hasMoreElements())
            {
                aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                stream.print(aCharacteristic.getID() + ",");
            }
        }
        stream.println("</" + Elements.CHARACTERISTIC_IDS_ELEMENT_NAME + ">");

        // Recursively write some of the children of organism
        {
            OrganismChromosome aChromosome;
            Enumeration eNonSexChromosomes = nonSexChromosomes.elements();
            while (eNonSexChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eNonSexChromosomes.nextElement();
                aChromosome.writeToStream(stream);
            }
        }

        {
            OrganismChromosome aChromosome;
            Enumeration eSexChromosomes = sexChromosomes.elements();
            while (eSexChromosomes.hasMoreElements())
            {
                aChromosome = (OrganismChromosome) eSexChromosomes.nextElement();
                aChromosome.writeToStream(stream);
            }
        }

        // Organism chromosome pairs
        {
            OrganismChromosomePair anOrganismChromosomePair;
            Enumeration eOrganismChromosomePairs = chromosomePairs.elements();
            while (eOrganismChromosomePairs.hasMoreElements())
            {
                anOrganismChromosomePair = (OrganismChromosomePair) eOrganismChromosomePairs.nextElement();
                anOrganismChromosomePair.writeToStream(stream);
            }
        }

        // Organism images
        {
            OrganismImage anOrganismImage;
            Enumeration eOrganismImages = organismImages.elements();
            while (eOrganismImages.hasMoreElements())
            {
                anOrganismImage = (OrganismImage) eOrganismImages.nextElement();
                anOrganismImage.writeToStream(stream);
            }
        }

        // End object
        stream.println("</" + Elements.ORGANISM_ELEMENT_NAME + ">");
    }

 /**
     * cross over happened on this organism
     * @param   boolean bln
     */
     private boolean parentCrossOver = false;
     public void setParentCrossOver(boolean bln)
     {
     	parentCrossOver = bln;
     }
     
     public boolean isParentCrossOver()
     {
     	return parentCrossOver;
     }
    
    /**
     * set Chromosome drawing infor
     */
     
     private Branch [] newChromosomes = null;
     public void setChromosomePaintInfo(Branch [] v)
     {
     	 newChromosomes = v;
     	
     }
     
     public Branch [] getChromosomePaintInfo()
     {
     	if (newChromosomes == null)
     	{
     		originalChromosomePaintInfo();
     	}
     	
     	
     	return newChromosomes;
     }
     
     
     public void setAsOriginalPaintInfo()
     {
     	originalChromosomePaintInfo();
     }
     private void originalChromosomePaintInfo()
     {
     	Enumeration chromosomes = this.getChromosomes();
		OrganismChromosome chr1 = null;
		OrganismChromosome chr2 = null;
		
		Vector orgBranchs1 = new Vector();
		Vector orgBranchs2 = new Vector();
		Branch temp1 = null;
		Branch temp2 = null;
		
		while (chromosomes.hasMoreElements())
		{
			try
			{
				chr1 = (OrganismChromosome)(chromosomes.nextElement());
				chr2 = (OrganismChromosome)(chromosomes.nextElement());
				
			}
			catch (Exception e)
            {
               
               System.out.println(e);
            }
            
  			Branch branchForChrom1 = new Branch(chr1,Color.blue);
			Branch branchForChrom2 = new Branch(chr2,Color.orange);
			
			//Branch branchForChrom1 = new Branch(chr1,Color.orange);
			//Branch branchForChrom2 = new Branch(chr2,Color.blue);
			
			
			orgBranchs1.addElement(branchForChrom1);
			orgBranchs2.addElement(branchForChrom2);
			
			newChromosomes = new Branch[orgBranchs1.size()];
			
		
			for (int i = 0; i<orgBranchs1.size();i++)
			{
				temp1 = (Branch)orgBranchs1.elementAt(i);
				temp2 = (Branch)orgBranchs2.elementAt(i);
				newChromosomes[i] = new Branch(temp1.getPLStrand(),temp1.getPLStrandColor(),
											temp2.getPLStrand(),temp2.getPLStrandColor(),
											temp1.getQLStrand(),temp1.getQLStrandColor(),
											temp2.getQLStrand(),temp2.getQLStrandColor());
			}
			
		}	
     }
     
     private void setPaintInfo(OrganismChromosome chr1, OrganismChromosome chr2, Branch b)
     {
     	if (chr1.isSexChromosome() && chr2.isSexChromosome())
     	{
	     	 if (chr1.getNumberTypeAsString().equals("X")&&chr2.getNumberTypeAsString().equals("X"))
	         {
		            chr1.setChromosomeBranchs(b.getPLStrand(),b.getPLStrandColor(),
		            						  b.getQLStrand(),b.getQLStrandColor());
		            chr2.setChromosomeBranchs(b.getPRStrand(),b.getPRStrandColor(),
		        						      b.getQRStrand(),b.getQRStrandColor());
		     }
	         else 
	         {
		        	OrganismAllele [] l1=  b.getPLStrand();
		        	OrganismAllele [] l2=  b.getPRStrand();
		        	if (chr1.getNumberTypeAsString().equals("X"))
		        	{
		        		if (l1.length>l2.length)
		        		{
		        			 chr1.setChromosomeBranchs(b.getPLStrand(),b.getPLStrandColor(),
			            						       b.getQLStrand(),b.getQLStrandColor());
			            	 chr2.setChromosomeBranchs(b.getPRStrand(),b.getPRStrandColor(),
		            								   b.getQRStrand(),b.getQRStrandColor());					  
		        		}
		        		else
		        		{
		        			 chr2.setChromosomeBranchs(b.getPLStrand(),b.getPLStrandColor(),
			            						       b.getQLStrand(),b.getQLStrandColor());
			            	 chr1.setChromosomeBranchs(b.getPRStrand(),b.getPRStrandColor(),
		            								   b.getQRStrand(),b.getQRStrandColor());	
		        		}
		        	}
		        	else
		        	{
		        		if (l1.length>l2.length)
		        		{
		        			 chr2.setChromosomeBranchs(b.getPLStrand(),b.getPLStrandColor(),
			            						       b.getQLStrand(),b.getQLStrandColor());
			            	 chr1.setChromosomeBranchs(b.getPRStrand(),b.getPRStrandColor(),
		            								   b.getQRStrand(),b.getQRStrandColor());					  
		        		}
		        		else
		        		{
		        			 chr1.setChromosomeBranchs(b.getPLStrand(),b.getPLStrandColor(),
			            						       b.getQLStrand(),b.getQLStrandColor());
			            	 chr2.setChromosomeBranchs(b.getPRStrand(),b.getPRStrandColor(),
		            								   b.getQRStrand(),b.getQRStrandColor());	
		        		}
		        	}
		        	
	         }
       }
       else
       {
       		  chr1.setChromosomeBranchs(b.getPLStrand(),b.getPLStrandColor(),
	            						b.getQLStrand(),b.getQLStrandColor());
	          chr2.setChromosomeBranchs(b.getPRStrand(),b.getPRStrandColor(),
            						    b.getQRStrand(),b.getQRStrandColor());
       }
     }
     
    public boolean isChromosomesVisible()
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        return chromosomesVisible;
    }

    /**
     * Set whether this organism's chromosomes is super visible.
     * The default is true.<p>
     *
     * @param	TorF boolean - name visible (true) or not visible (false)
    **/
    public void setChromosomesVisible(boolean TorF)
    {
        if (deleted)
        {
            throw new ObjectDeletedException("organism deleted");
        }

        if (TorF != chromosomesVisible)
        {
            boolean oldChromosomesVisible = chromosomesVisible;
            chromosomesVisible = TorF;
    
            changes.firePropertyChange(EngineProp.NAME_SUPER_VISIBLE,
                                       new Boolean(oldChromosomesVisible),
                                       new Boolean(chromosomesVisible));
        }
    }
	/**
	* add code for population
	**/
	// organism's age
	private int age;
	
	//organism's moving speed;
	private double speed;
	
	// orgnaism's moving direction
	private double direction; 
	
	//organism's current Location;
	private TerrainEng enUnit;
	//organism's current X;
	private int x_loc;
	private int y_loc;
	private boolean wantToMate = false;
	
	
	//organism's location
	private Vector neighbours;
	/**
	 * Handle the age of this organism
	 **/
	 public void setAge(int newAge)
	 {
	 	
	 	if (newAge>getAgeOfMate())
	 		wantToMate = true;
	 	else
	 		wantToMate = false;
	 	age = newAge;
	 	
	 }
	 
	 public int getAge()
	 {
	 	return age;
	 }
	 public void setWantToMate(boolean bln)
	 {
	 	if (getAge()>getAgeOfMate() && hungry<5)
	 		wantToMate = bln;
	 	else
	 		wantToMate = false;
	 }
	 public boolean isWantToMate()
	 {
	 	return wantToMate;
	 }
	 /**
	  * Handle the speed of the organism
	  **/
	  public void setSpeed(double newSpeed)
	  {
	  	speed = newSpeed;
	  }
	  
	  public double getSpeed()
	  {
	  	return speed;
	  }
	  
	  public void setDirection(double dir)
	  {
	  	 direction = dir;
	  }
	  public double getDirection()
	  {
	  	return direction;
	  }
	  public double getVx()
	  {
	  	return getSpeed()*Math.sin(direction);
	  }
	  
	  public double getVy()
	  {
	  	return getSpeed()*Math.cos(direction);
	  }
	  
	  public void setXloc(int x1)
	  {
	  	x_loc = x1;
	  }
	  public void  setYloc(int y1)
	  {
	  	y_loc = y1;
	  }
	  
	  public int getXloc()
	  {
	  	return x_loc;
	  }
	  
	  public int getYloc()
	  {
	  	return y_loc;
	  }
	  
	  public void setCurrentUnit(TerrainEng eu)
	  {
	  	if (wantToMate == false && age>getAgeOfMate() && hungry <5)
	  		wantToMate = true;
	  	
		if(enUnit != null)
		{
			enUnit.removeOrganism(this);
		}

		// The organism should be gone right now
		/*
		TerrainEng remaining = null;
		if(eu != null) eu.getMotherEnvi().findOrganism(this);
		else if(enUnit != null) enUnit.getMotherEnvi().findOrganism(this);

		if(remaining != null)
		{
			System.out.println("The organism exists in two units at the same time. \n" +
							   " The old unit was: " + enUnit + "\n" +
							   " The new unit should be: " + eu + "\n" +
							   " The bad unit is: " + remaining);										 
		}
		*/

	  	enUnit = eu;
	  	if (enUnit !=null)
		{
	  		enUnit.addOrganism(this);
	  	}
	  }
	 
	  public TerrainEng getCurrentUnit()
	  {
	  	return enUnit;
	  }
	  public void nextStep()
	  {
		double r;
	  	if (world == null) return;
	  	if (deleted) return;
	  	if (enUnit==null) return;
	  
	  	/*if (age == Organism.AGE_LIMIT|| hungry>Organism.HUNGRY_LIMIT)
	  	{
	  		enUnit.removeOrganism(this);
	  		EnvironmentView e = enUnit.getMotherEnvi();
	  		e.removeOrganism(this);
	  		setCurrentUnit(null);

			//if(getParentFamily() !=null)
			//	setParentFamily(null);
			
	  		delete();
	  		
	  		return;
	  	}*/
		/*else
		{
			//System.out.println("age: " + age);
			age++;
		}*/
		
		//r = Math.random();
		
		Date dat = new Date();
			
		/*if(age >  rule.getAgeLimit() 
			//|| age >= rule.getAgeLimitForSickel()// && containsCharacteristic("Four Legs")) 
			|| enUnit.getBarrier())
			//|| (age >= rule.getAgeLimitForSNormalInSickelEnv() && enUnit.getEnvironment() == TerrainEng.ENVIRONMENT_WATER))//&& containsCharacteristic("No Legs")))
		/*if(r <= calculateN() || age > aZero || (age >= 10 && containsCharacteristic("Four Legs"))
			|| enUnit.getBarrier())*/
		
		foodConsume();
		moveStep(); 
		age++;
	  }
	
	  private void moveStep()
	  {
	  	if (deleted) return;
	  	if (enUnit == null) return;
	  	Vector neighbours = enUnit.getNeighbours();
	  	EnvironmentView e = enUnit.getMotherEnvi();
	
		int newX = (int)(this.getXloc()+Math.round(this.getVx()));
		int newY = (int)(this.getYloc()+Math.round(this.getVy()));
		  	
		Rules rule = enUnit.getRules();
		
		rule.changeDirection(enUnit,this,newX,newY);
	  }
	  
	  private void foodConsume()
	  {
		getRepast();  
	  	if (enUnit.getFood()<TerrainEng.FOOD_LOW || enUnit.getWater()<TerrainEng.WATER_LOW)
		{
			if (enUnit.getFood()<repast)
		  	{
		  		enUnit.setFood(0);
				//age += 1000;
				age += 2;
		  		hungry +=1;
		  		if (hungry>4)
		  			wantToMate = false;
		  	}
		  	else
		  	{
		  		hungry = 0;
				enUnit.setFood(enUnit.getFood() - repast);
		 		enUnit.setWater(enUnit.getWater()-5);
		 	}
		  
		  	
		 }
		 else
		 {
		 	enUnit.setFood(enUnit.getFood() - repast);
		 	if (hungry>0)
		 	{
		 		hungry = 0;
		 		if (getAge()>getAgeOfMate() && wantToMate == false)
		 			wantToMate = true;
		 	}
		 }
		 
	  }
	  private double calculateN()
	  {
		  double n;
		  
		  n = (double)age/aZero;
		 // System.out.println("age: " + age +" n: "+n);
		  return n;
	  }
	  public int isHungry()
	  {
	  	return hungry;
	  }
	 
	  private int repast = 2;
	  private int hungry = 0;
	  private int R;
	  private int ageOfMate;
	  private double health;
	  
	  public void setRepast(int r)
	  {
	  	repast = r;
	  }
	  public int getRepast()
	  {
		/*switch (enUnit.getEnvironment())
		{
			case TerrainEng.ENVIRONMENT_LAND:
				repast = enUnit.getLandConsuming();
				break;
			case TerrainEng.ENVIRONMENT_SAND:
				repast = enUnit.getSandConsuming();
				break;
			case TerrainEng.ENVIRONMENT_WATER:
				repast = enUnit.getWaterConsuming();
				break;
			case TerrainEng.ENVIRONMENT_MOUNTAIN:
				repast = enUnit.getMountainConsuming();
				break;
			default:
				break;
		}*/
		
	  	return repast;
	  }
	 
	  public void setHealth(double a)
	  {
	  	 health = a;
	  }
	  public double getHealth()
	  {
	  	return health;
	  }
	  public void setAgeOfMate(int a)
	  {
	  	ageOfMate = a;
	  	if (age >ageOfMate)
	  		wantToMate = true;
	  	else 
	  		wantToMate = false;
	  		
	  }
	  public int getAgeOfMate()
	  {
	  	return ageOfMate;
	  }
	 
	 	
	  public void setR(int r)
	  {
	  	R = r;
	  }
	  public int getR()
	  {
	  	return R;
	  }
	  
	  public void doDrag(int x,int y){
	  	setXloc(x-R/2);
	  	setYloc(y-R/2);
	  }
	  public void endDrag(int x,int y){
	  	Rules rule = enUnit.getRules();
	  	int newXloc = x-R/2;
	  	int newYloc = y-R/2;
	  
	  	if (newXloc<0)
	  	{
	  		setXloc(0);
	  	}
  		if (newYloc<0)
  		{
  			setYloc(0);
  		}
  		if ((x+R)>(enUnit.getMotherEnvi()).getWidth())
  		{
  			setXloc((enUnit.getMotherEnvi()).getWidth()-R);
  		}
  		if ((y+R)>(enUnit.getMotherEnvi()).getHeight())
  		{
  			setYloc((enUnit.getMotherEnvi()).getHeight()-R);
  		}
  		if (newXloc>=0 && (x+R)<=(enUnit.getMotherEnvi()).getWidth()
  		   && newYloc>=0 && (y+R)<=(enUnit.getMotherEnvi()).getHeight())
  		{
  			setXloc(newXloc);
  			setYloc(newYloc);
	  	}
	  }
	  public void startDrag(int x,int y){}
	  public void setDraggable(boolean draggable){}
	  public boolean isDraggable(){
	  	return false;
	  }
	  public void paintOrganism(Graphics g)
	  {
	  		
	  	Trait currentTrait = (enUnit.getMotherEnvi()).getTrait();
        if (currentTrait != null)
        {
            boolean showMaleAndFemale = true;
            if (currentTrait.getSpecies().getDiploidType() == Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
            {
                showMaleAndFemale = false;
            }

     
            Color color1, color2;
            Characteristic aCharacteristic;
            Enumeration eCharacteristics = currentTrait.getCharacteristics();
            while (eCharacteristics.hasMoreElements())
            {
                aCharacteristic = (Characteristic) eCharacteristics.nextElement();

                color1 = aCharacteristic.getPedigreeSymbolFirstColor();
                color2 = aCharacteristic.getPedigreeSymbolSecondColor();
                switch (aCharacteristic.getPedigreeSymbolType())
                {
                    case Characteristic.PEDIGREE_SYMBOL_SOLID_COLOR:
                    {
                        if (showMaleAndFemale && this.containsCharacteristic(aCharacteristic.getName()))
                        {
                        	if (this.getSex()==Organism.FEMALE)
                        	{
                          	  g.setColor(color1);
                          	  g.fillOval(x_loc,y_loc,R,R);
                          	  
                          	  g.setColor(Color.black);
                          	  g.drawOval(x_loc,y_loc,R,R);
                          	}
                          	else if (this.getSex() == Organism.MALE)
                          	{
                          		g.setColor(color1);
                           	 	g.fillRect(x_loc,y_loc,R,R);
    							
    							g.setColor(Color.black);
                            	g.drawRect(x_loc,y_loc,R,R);
                            }
                        }
                        else if (this.containsCharacteristic(aCharacteristic.getName()))
                        {
                            g.setColor(color1);
                            g.fillOval(x_loc,y_loc,R,R);

                            g.setColor(Color.black);
                            g.drawOval(x_loc,y_loc,R,R);
                        }
   
                    }
                    break;
            
                    case Characteristic.PEDIGREE_SYMBOL_FORWARD_SLASH:
                    {
                        if (showMaleAndFemale && this.containsCharacteristic(aCharacteristic.getName()))
                        {
                        	if(this.getSex() == Organism.FEMALE)
                        	{
                            	g.setColor(color1);
                            	g.fillOval(x_loc,y_loc,R,R);
                           		g.setColor(color2);
                           		g.fillArc(x_loc,y_loc,R,R,45,-180);
                           		g.setColor(Color.black);
                            	g.drawOval(x_loc,y_loc,R,R);
    						}
    						else  if (this.getSex() == Organism.MALE)
    						{
	                            g.setColor(color1);
	                            g.fillRect(x_loc,y_loc,R,R);
	                            Polygon p = new Polygon();
	                            p.addPoint(x_loc+R,y_loc);
	                            p.addPoint(x_loc+R,y_loc+R);
	                            p.addPoint(x_loc,y_loc+R);
	                            p.addPoint(x_loc+R,y_loc);
	                            g.setColor(color2);
	                            g.fillPolygon(p);
	                            g.setColor(Color.black);
                            	g.drawRect(x_loc,y_loc,R,R);
    						}
                           
                        }
                        else if (this.containsCharacteristic(aCharacteristic.getName()))
                        {
                            g.setColor(color1);
                            g.fillOval(x_loc,y_loc,R,R);
                           	g.setColor(color2);
                           	g.fillArc(x_loc,y_loc,R,R,45,-180);
                           	g.setColor(Color.black);
                            g.drawOval(x_loc,y_loc,R,R);
                        }

                    }
                    break;
                }

            }
	 	 }
	  }
	 
}
