//
// Class : EngineProp
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:12 $
// $Author: ed $
//

package org.concord.biologica.engine;

import java.lang.String;

/**
 * This class contains definitions for BioLogica engine simple and
 * vector property names.  These property names are used to notify
 * listeners of property changes in the engine classes.<p>
 *
 * The property names are here because we wanted to standardize on
 * a given set of names across the engine, rather than having each
 * engine class define its own names.<p>
 *
 * @see	java.beans.PropertyChangeEvent
 * @see	java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:12 $
 * @author 		$Author: ed $
**/
public final class EngineProp
{
	// Please keep these properties in alphabetical order

	/**
	 * Alleles alterable flag.
	 * New and old values are of type Boolean.<p>
	**/
	public static final String ALLELES_ALTERABLE = "allelesAlterable";

	/**
	 * Alleles visible flag.
	 * New and old values are of type Boolean.<p>
	**/
	public static final String ALLELES_VISIBLE = "allelesVisible";

	/**
	 * Base values in an object containing nucleic acid changed, either
	 * the value of a particular base or adding or removing a base.
	 * New and old values are null, so the listener must query the
	 * nucleic acid to find the new values.<p>
	**/
	public static final String BASE_VALUES = "baseValues";

	/**
	 * A Characteristic object was added to a Trait.
	 * New value is the added Characteristic object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHARACTERISTIC_ADDED = "characteristicAdded";

	/**
	 * A Characteristic object was removed from a Trait.
	 * New value is the removed Characteristic object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHARACTERISTIC_REMOVED = "characteristicRemoved";

	/**
	 * A Characteristic object was added to a SpeciesImageColumn.
	 * New value is the added Characteristic object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHARACTERISTIC_ADDED_TO_COLUMN = "characteristicAddedToColumn";

	/**
	 * A Characteristic object was removed from a SpeciesImageColumn.
	 * New value is the removed Characteristic object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHARACTERISTIC_REMOVED_FROM_COLUMN = "characteristicRemovedFromColumn";
	/**
	 * A Characteristic object was added to a SpeciesImageRow.
	 * New value is the added Characteristic object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHARACTERISTIC_ADDED_TO_ROW = "characteristicAddedToRow";

	/**
	 * A Characteristic object was removed from a SpeciesImageRow.
	 * New value is the removed Characteristic object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHARACTERISTIC_REMOVED_FROM_ROW = "characteristicRemovedFromRow";

	/**
	 * A child organism was added to a family.
	 * New value is the added child Organism object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHILD_ADDED = "childAdded";

	/**
	 * A child organism was removed from a family.
	 * New value is the removed child Organism object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHILD_REMOVED = "childRemoved";

	/**
	 * A child Family object was added to an organism.
	 * New value is the added Family object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHILD_FAMILY_ADDED = "childFamilyAdded";

	/**
	 * A child Family object was removed from an organism.
	 * New value is the removed Family object.
	 * Old value is null and should be ignored.
	**/
	public static final String CHILD_FAMILY_REMOVED = "childFamilyRemoved";

	/**
	 * The color of an engine object.
	 * New value is the new color of the object.
	 * Old value is the old color of the object.
	**/
	public static final String COLOR = "color";

	/**
	 * The column index of an OrganismImage in its SpeciesImage object.
	 * New and old values are integers.
	**/
	public static final String COLUMN_INDEX = "columnIndex";

	/**
	 * The current species in a world changed.
	 * New value is the new current species in the world.
	 * Old value is the old current species in the world.
	**/
	public static final String CURRENT_SPECIES = "currentSpecies";

	/**
	 * The current terrain in a world changed.
	 * New value is the new current terrain in the world.
	 * Old value is the old current terrain in the world.
	**/
	public static final String CURRENT_TERRAIN = "currentTerrain";

	/**
	 * DELETED simple property name.
	 * New and old values are of type Boolean.<p>
	 *
	 * When this property event is fired and the value is true,
	 * it means the object has been deleted and all other properties
	 * of the object are about to be deleted.  No other simple
	 * property events will be fired from this object during its
	 * deletion.<p>
	 *
	 * But vector property element removed events will come from
	 * this object as its children, if it has any, are deleted.<p>
	 *
	 * And vector property element removed event(s) will come from
	 * this object's parent(s), one per parent, as the final event(s)
	 * when this object is done being deleted.<p>
	**/
	public static final String DELETED = "deleted";

	/**
	 * Description, indicating that the description string for an
	 * object has changed.  New and old values are of type String.<p>
	**/
	public static final String DESCRIPTION = "description";

	/**
	 * Dirty property, indicating that the current Engine state has
	 * been modified since the state was last saved to a file.<p>
	 * New and old values are of type Boolean.<p>
	**/
	public static final String DIRTY = "dirty";

	/**
	 * Diploid type of a species changed, indicating that the rule
	 * for which is female or male has changed.<p>
	 * New and old values are of type Integer.<p>
	**/
	public static final String DIPLOID_TYPE = "diploidType";

	/**
	 * DNA alterable flag.
	 * New and old values are of type Boolean.<p>
	**/
	public static final String DNA_ALTERABLE = "dnaAlterable";

	/**
	 * DNA visible flag.
	 * New and old values are of type Boolean.<p>
	**/
	public static final String DNA_VISIBLE = "dnaVisible";

	/**
	 * The "else characteristic" of a genotype to phenotype rule changed.
	 * New and old values are of type Characteristic.
	**/
	public static final String ELSE_CHARACTERISTIC = "elseCharacteristic";

	/**
	 * An Environment object was added.
	 * New value is the added Environment object.
	 * Old value is null and should be ignored.
	**/
	public static final String ENVIRONMENT_ADDED = "environmentAdded";

	/**
	 * An Environment object was removed.
	 * New value is the removed Environment object.
	 * Old value is null and should be ignored.
	**/
	public static final String ENVIRONMENT_REMOVED = "environmentRemoved";

	/**
	 * A Family object was added to a world.
	 * New value is the added Family object.
	 * Old value is null and should be ignored.
	**/
	public static final String FAMILY_ADDED = "familyAdded";

	/**
	 * A Family object was removed.
	 * New value is the removed Family object.
	 * Old value is null and should be ignored.
	**/
	public static final String FAMILY_REMOVED = "familyRemoved";

	/**
	 * Fatal characteristic simple property name.  boolean.
	 * New value is the new fatal property of the characteristic.
	 * Old value is the old fatal property of the characteristic.
	**/
	public static final String FATAL = "fatal";

	/**
	 * File simple property name.
	 * New and old values are of type File.<p>
	**/
	public static final String FILE = "file";

	/**
	 * Filename simple property name.
	 * New and old values are of type String.<p>
	**/
	public static final String FILENAME = "filename";

	/**
	 * A Gamete object was added.
	 * New value is the added Gamete object.
	 * Old value is null and should be ignored.
	**/
	public static final String GAMETE_ADDED = "gameteAdded";

	/**
	 * A Gamete object was removed.
	 * New value is the removed Gamete object.
	 * Old value is null and should be ignored.
	**/
	public static final String GAMETE_REMOVED = "gameteRemoved";

	/**
	 * An object's gender changed. New and old values
	 * are Integers (Species.FEMALE_AND_MALE, FEMALE_ONLY or MALE_ONLY).<p>
	 * @see		org.concord.biologica.engine.Species#FEMALE_AND_MALE
	 * @see		org.concord.biologica.engine.Species#FEMALE_ONLY
	 * @see		org.concord.biologica.engine.Species#MALE_ONLY
	**/
	public static final String GENDER = "gender";

	/**
	 * A Gene object was added to a species chromosome.
	 * New value is the added Gene object.
	 * Old value is null and should be ignored.
	**/
	public static final String GENE_ADDED = "geneAdded";

	/**
	 * A Gene object was removed from a species chromosome.
	 * New value is the removed Gene object.
	 * Old value is null and should be ignored.
	**/
	public static final String GENE_REMOVED = "geneRemoved";

	/**
	 * A GenotypeToPhenotypeRule object was added.
	 * New value is the added GenotypeToPhenotypeRule object.
	 * Old value is null and should be ignored.<p>
	**/
	public static final String GENOTYPE_TO_PHENOTYPE_RULE_ADDED = "genotypeToPhenotypeRuleAdded";

	/**
	 * A GenotypeToPhenotypeRule object was moved.
	 * New value is the moved GenotypeToPhenotypeRule object.
	 * Old value is null and should be ignored.
	**/
	public static final String GENOTYPE_TO_PHENOTYPE_RULE_MOVED = "genotypeToPhenotypeRuleMoved";

	/**
	 * A GenotypeToPhenotypeRule object was removed.
	 * New value is the removed GenotypeToPhenotypeRule object.
	 * Old value is null and should be ignored.
	**/
	public static final String GENOTYPE_TO_PHENOTYPE_RULE_REMOVED = "genotypeToPhenotypeRuleRemoved";

	/**
	 * Height changed.
	 * New and old values are of type Integer and represent
	 * the new and old height values.<p>
	**/
	public static final String HEIGHT = "height";

	/**
	 * Hotspot color changed.
	 * New and old values are of type Color and represent
	 * the new and old hotspot color values.<p>
	**/
	public static final String HOTSPOT_COLOR = "hotspotColor";

	/**
	 * Hotspot radius changed.
	 * New and old values are of type Integer and represent
	 * the new and old hotspot radius values.<p>
	**/
	public static final String HOTSPOT_RADIUS = "hotspotRadius";

	/**
	 * ID simple property name.
	 * New and old values are of type Integer.<p>
	**/
	public static final String ID = "id";

	/**
	 * An "if species allele" was added to a genotype to phenotype rule.
	 * New value is the added SpeciesAllele object.
	 * Old value is null and should be ignored.
	**/
	public static final String IF_SPECIES_ALLELE_ADDED = "ifSpeciesAlleleAdded";

	/**
	 * An "if species allele" was removed from a genotype to phenotype rule.
	 * New value is the removed SpeciesAllele object.
	 * Old value is null and should be ignored.
	**/
	public static final String IF_SPECIES_ALLELE_REMOVED = "ifSpeciesAlleleRemoved";

	/**
	 * Phenotype image ID changed.
	 * New and old values are of type Integer and may
	 * have any integer values.<p>
	**/
	public static final String IMAGE_ID = "imageID";

	/**
	 * Image number of a chromosome, used in chromosome view.
	 * New and old values are of type Integer.<p>
	**/
	public static final String IMAGE_NUMBER = "imageNumber";

	/**
	 * Image type property changed.
	 * New and old values are of type Integer and represent
	 * the type of the image (normal, hotspot, scope, etc.).<p>
	**/
	public static final String IMAGE_TYPE = "imageType";

	/**
	 * In DNA or RNA simple property name.  New and old values are
	 * of type Integer with values of Base.IN_DNA, Base.IN_RNA or
	 * Base.IN_DNA_OR_RNA.<p>
	 *
	 * @see org.concord.biologica.engine.Base#IN_DNA
	 * @see org.concord.biologica.engine.Base#IN_RNA
	 * @see org.concord.biologica.engine.Base#IN_DNA_OR_RNA
	**/
	public static final String IN_DNA_OR_RNA = "inDNAorRNA";

	/**
	 * Large hotspot location.
	 * New and old values are of type Point.<p>
	**/
	public static final String LARGE_HOTSPOT = "largeHotspot";

	/**
	 * Large image column width of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String LARGE_IMAGE_COLUMN_WIDTH = "largeImageColumnWidth";

	/**
	 * Large image row height of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String LARGE_IMAGE_ROW_HEIGHT = "largeImageRowHeight";

	/**
	 * Length in bases simple property name.
	 * New and old values are of type Integer.<p>
	**/
	public static final String LENGTH_IN_BASES = "lengthInBases";

	/**
	 * Locked boolean state of the object.
	 * New and old values are of type Boolean.<p>
	 *
	 * **** NOTE THIS IS OLD AND NOT USED ANYMORE ****
	 * ****       USE LOCKED_STATE BELOW          ****
    **/
	// public static final String LOCKED = "locked";

	/**
	 * Locked state of the object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String LOCKED_STATE = "lockedState";

	/**
	 * Medium hotspot location.
	 * New and old values are of type Point.<p>
	**/
	public static final String MEDIUM_HOTSPOT = "mediumHotspot";

	/**
	 * Medium image column width of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String MEDIUM_IMAGE_COLUMN_WIDTH = "mediumImageColumnWidth";

	/**
	 * Medium image row height of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String MEDIUM_IMAGE_ROW_HEIGHT = "mediumImageRowHeight";

	/**
	 * Mutation allele of its gene?
	 * New and old values are Boolean.<p>
	**/
	public static final String MUTATION_ALLELE = "mutationAllele";

	/**
	 * Name simple property name.
	 * New and old values are of type String.<p>
	**/
	public static final String NAME = "name";

	/**
	 * Name super visible flag.  Used in Organism.<p>
	 * New and old values are of type Boolean.<p>
	**/
	public static final String NAME_SUPER_VISIBLE = "nameSuperVisible";

	/**
	 * An INucleicAcid object was added.
	 * New value is the added INucleicAcid object.
	 * Old value is null and should be ignored.
	**/
	public static final String NUCLEIC_ACID_ADDED = "nucleicAcidAdded";

	/**
	 * An INucleicAcid object was removed.
	 * New value is the removed INucleicAcid object.
	 * Old value is null and should be ignored.
	**/
	public static final String NUCLEIC_ACID_REMOVED = "nucleicAcidRemoved";

	/**
	 * Chromosome number type.
	 * New and old values are of type Integer and have
	 * values of IChromosome.X_CHROMOSOME or IChromosome.Y_CHROMOSOME
	 * if the chromosome is a sex chromosome and a number
	 * greater than 0 if the chromosome is an autosome.<p>
	 *
	 * @see org.concord.biologica.engine.IChromosome#X_CHROMOSOME
	 * @see org.concord.biologica.engine.IChromosome#Y_CHROMOSOME
	**/
	public static final String NUMBER_TYPE = "numberType";

	/**
	 * The number of columns in a SpeciesImage.
	 * New and old values are integers.
	**/
	public static final String NUMBER_OF_COLUMNS = "numberOfColumns";

	/**
	 * The number of rows in a SpeciesImage.
	 * New and old values are integers.
	**/
	public static final String NUMBER_OF_ROWS = "numberOfRows";

	/**
	 * An Organism object was added.
	 * New value is the added Organism object.
	 * Old value is null and should be ignored.
	**/
	public static final String ORGANISM_ADDED = "organismAdded";

	/**
	 * An OrganismAllele object was added to an OrganismChromosome.
	 * New value is the added OrganismAllele object.
	 * Old value is null and should be ignored.
	**/
	public static final String ORGANISM_ALLELE_ADDED = "organismAlleleAdded";

	/**
	 * An OrganismAllele object was removed from an OrganismChromosome.
	 * New value is the removed OrganismAllele object.
	 * Old value is null and should be ignored.
	**/
	public static final String ORGANISM_ALLELE_REMOVED = "organismAlleleRemoved";

	/**
	 * Organism chromosome added.
	 * New value is the added OrganismChromosome object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Organism
	 * @see org.concord.biologica.engine.OrganismChromosome
	**/
	public static final String ORGANISM_CHROMOSOME_ADDED = "organismChromosomeAdded";

	/**
	 * Organism chromosome removed.
	 * New value is the removed OrganismChromosome object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Organism
	 * @see org.concord.biologica.engine.OrganismChromosome
	**/
	public static final String ORGANISM_CHROMOSOME_REMOVED = "organismChromosomeRemoved";

	/**
	 * The genotype of an organism has changed but the phenotype did not change.
	 * Containing object of type Organism.
	 * The old and new value objects are null.<p>
	**/
	public static final String ORGANISM_GENOTYPE_AND_NOT_PHENOTYPE = "organismGenotypeAndNotPhenotype";

	/**
	 * The genotype and phenotype of an organism have changed.
	 * Containing object of type Organism.
	 * The old and new value objects are Vector's containing the old and new Characteristics.
	 * A receiver of this event may look at these 2 Vectors to analyze what phenotype
	 * change occurred.<p>
	**/
	public static final String ORGANISM_GENOTYPE_AND_PHENOTYPE = "organismGenotypeAndPhenotype";

	/**
	 * Organism image added.
	 * New value is the added OrganismImage object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Organism
	 * @see org.concord.biologica.engine.OrganismImage
	**/
	public static final String ORGANISM_IMAGE_ADDED = "organismImageAdded";

	/**
	 * Organism image removed.
	 * New value is the removed OrganismImage object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Organism
	 * @see org.concord.biologica.engine.OrganismImage
	**/
	public static final String ORGANISM_IMAGE_REMOVED = "organismImageRemoved";

	/**
	 * An Organism object was removed.
	 * New value is the removed Organism object.
	 * Old value is null and should be ignored.
	**/
	public static final String ORGANISM_REMOVED = "organismRemoved";

	/**
	 * The parent family of this organism has changed.
	 * New value is the new Family object, may be null.
     * Old value is the old Family object, may be null.
	**/
	public static final String PARENT_FAMILY = "parentFamily";

	/**
	 * The first color used by a characteristic in drawing its symbol in a pedigree view.
	 * New value is the new pedigree symbol first color.
	 * Old value is the old pedigree symbol first color.
	**/
	public static final String PEDIGREE_SYMBOL_FIRST_COLOR = "pedigreeSymbolFirstColor";

	/**
	 * The second color used by a characteristic in drawing its symbol in a pedigree view.
	 * New value is the new pedigree symbol second color.
	 * Old value is the old pedigree symbol second color.
	**/
	public static final String PEDIGREE_SYMBOL_SECOND_COLOR = "pedigreeSymbolSecondColor";

	/**
	 * The symbol type used by a characteristic in drawing its symbol in a pedigree view.
	 * New and old values are of Integer type and either PEDIGREE_SYMBOL_SOLID_COLOR
	 * or PEDIGREE_SYMBOL_FORWARD_SLASH.
	 *
	 * @see		org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_SOLID_COLOR
	 * @see		org.concord.biologica.engine.Characteristic#PEDIGREE_SYMBOL_FORWARD_SLASH
	**/
	public static final String PEDIGREE_SYMBOL_TYPE = "pedigreeSymbolType";

	/**
	 * PLOIDY_NUMBER simple property name.
	 * New and old values are of type Integer.<p>
	**/
	public static final String PLOIDY_NUMBER = "ploidyNumber";

	/**
	 * The row index of an OrganismImage in its SpeciesImage object.
	 * New and old values are integers.
	**/
	public static final String ROW_INDEX = "rowIndex";

	/**
	 * Seed, as used for random number generation, simple property name.
	 * New and old values are of type Long.<p>
	**/
	public static final String SEED	= "seed";

	/**
	 * Selected state of object changed.
	 * New and old values are of type Boolean.<p>
	**/
	public static final String SELECTED = "selected";

	/**
	 * Sex of an organism.
	 * New and old values are of type Integer.<p>
	**/
	public static final String SEX = "sex";

	/**
	 * Show trait as text in the organism view.<p>
	 * New and old values are of type Boolean.<p>
	**/
	public static final String SHOW_TRAIT_AS_TEXT_IN_ORGANISM_VIEW = "showTraitAsTextInOrganismView";

	/**
	 * Small hotspot location.
	 * New and old values are of type Point.<p>
	**/
	public static final String SMALL_HOTSPOT = "smallHotspot";

	/**
	 * Small image column width of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String SMALL_IMAGE_COLUMN_WIDTH = "smallImageColumnWidth";

	/**
	 * Small image row height of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String SMALL_IMAGE_ROW_HEIGHT = "smallImageRowHeight";

	/**
	 * Species of an organism.
	 * New and old values are of type Species.<p>
	**/
	public static final String SPECIES = "species";

	/**
	 * A Species object was added.
	 * New value is the added Species object.
	 * Old value is null and should be ignored.
	**/
	public static final String SPECIES_ADDED = "speciesAdded";

	/**
	 * A SpeciesAllele object was added to a SpeciesChromosome.
	 * New value is the added SpeciesAllele object.
	 * Old value is null and should be ignored.
	**/
	public static final String SPECIES_ALLELE_ADDED = "speciesAlleleAdded";

	/**
	 * A SpeciesAllele object was removed from a SpeciesChromosome.
	 * New value is the removed SpeciesAllele object.
	 * Old value is null and should be ignored.
	**/
	public static final String SPECIES_ALLELE_REMOVED = "speciesAlleleRemoved";

	/**
	 * Species chromosome added.
	 * New value is the added SpeciesChromosome object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Species
	 * @see org.concord.biologica.engine.SpeciesChromosome
	**/
	public static final String SPECIES_CHROMOSOME_ADDED = "speciesChromosomeAdded";

	/**
	 * Species chromosome removed.
	 * New value is the removed SpeciesChromosome object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Species
	 * @see org.concord.biologica.engine.SpeciesChromosome
	**/
	public static final String SPECIES_CHROMOSOME_REMOVED = "speciesChromosomeRemoved";

	/**
	 * Species image added to a Species object.
	 * New value is the added SpeciesImage object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Species
	 * @see org.concord.biologica.engine.SpeciesImage
	**/
	public static final String SPECIES_IMAGE_ADDED = "speciesImageAdded";

	/**
	 * Species image removed from a Species object.
	 * New value is the removed SpeciesImage object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.Species
	 * @see org.concord.biologica.engine.SpeciesImage
	**/
	public static final String SPECIES_IMAGE_REMOVED = "speciesImageRemoved";

	/**
	 * Species image column added to a species image.
	 * New value is the added SpeciesImageColumn object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.SpeciesImage
	 * @see org.concord.biologica.engine.SpeciesImageColumn
	**/
	public static final String SPECIES_IMAGE_COLUMN_ADDED = "speciesImageColumnAdded";

	/**
	 * Species image column removed from a species image.
	 * New value is the removed SpeciesImageColumn object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.SpeciesImage
	 * @see org.concord.biologica.engine.SpeciesImageColumn
	**/
	public static final String SPECIES_IMAGE_COLUMN_REMOVED = "speciesImageColumnRemoved";

	/**
	 * Species image row added to a species image.
	 * New value is the added SpeciesImageRow object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.SpeciesImage
	 * @see org.concord.biologica.engine.SpeciesImageRow
	**/
	public static final String SPECIES_IMAGE_ROW_ADDED = "speciesImageRowAdded";

	/**
	 * Species image row removed from a species image
	 * New value is the removed SpeciesImageRow object.
	 * Old value is null and should be ignored.
	 *
	 * @see org.concord.biologica.engine.SpeciesImage
	 * @see org.concord.biologica.engine.SpeciesImageRow
	**/
	public static final String SPECIES_IMAGE_ROW_REMOVED = "speciesImageRowRemoved";

	/**
	 * A Species object was removed.
	 * New value is the removed Species object.
	 * Old value is null and should be ignored.
	**/
	public static final String SPECIES_REMOVED = "speciesRemoved";

	/**
	 * Start index in holder simple property name.
	 * New and old values are of type Integer.<p>
	**/
	public static final String START_INDEX_IN_HOLDER = "startIndexInHolder";

	/**
	 * Strand of a gene on its chromosome.
	 * New and old values are of type Integer - TOP_STRAND or BOTTOM_STRAND
	**/
	public static final String STRAND = "strand";

	/**
	 * A Terrain object was added to a world.
	 * New value is the added Terrain object.
	 * Old value is null and should be ignored.
	**/
	public static final String TERRAIN_ADDED = "terrainAdded";

	/**
	 * A Terrain object was removed from a world.
	 * New value is the removed Terrain object.
	 * Old value is null and should be ignored.
	**/
	public static final String TERRAIN_REMOVED = "terrainRemoved";

	/**
	 * A terrain or multiple terrains was changed in an environment.
	 * New and old values are both null.
	**/
	public static final String TERRAINS = "terrains";

	/**
	 * Text symbol simple property name.
	 * New and old values are of type String.<p>
	**/
	public static final String TEXT_SYMBOL = "textSymbol";

	/**
	 * The "then characteristic" of a genotype to phenotype rule changed.
	 * New and old values are of type Characteristic.
	**/
	public static final String THEN_CHARACTERISTIC = "thenCharacteristic";

	/**
	 * A Trait object was added.
	 * New value is the added Trait object.
	 * Old value is null and should be ignored.
	**/
	public static final String TRAIT_ADDED = "traitAdded";

	/**
	 * A Trait object was removed.
	 * New value is the removed Trait object.
	 * Old value is null and should be ignored.
	**/
	public static final String TRAIT_REMOVED = "traitRemoved";

	/**
	 * Visible flag.
	 * New and old values are of type Boolean.<p>
	**/
	public static final String VISIBLE = "visible";

	/**
	 * Weight changed.
	 * New and old values are of type Integer and represent
	 * the new and old weight values.<p>
	**/
	public static final String WEIGHT = "weight";

	/**
	 * Width changed.
	 * New and old values are of type Integer and represent
	 * the new and old width values.<p>
	**/
	public static final String WIDTH = "width";

	/**
	 * World added.
	 * New value is the opened World object.
	 * Old value is null and should be ignored.
	**/
	public static final String WORLD_ADDED = "worldAdded";

	/**
	 * World removed.
	 * New value is the removed World object.
	 * Old value is null and should be ignored.
	**/
	public static final String WORLD_REMOVED = "worldRemoved";

	/**
	 * XLarge hotspot location.
	 * New and old values are of type Point.<p>
	**/
	public static final String XLARGE_HOTSPOT = "xLargeHotspot";

	/**
	 * XLarge image column width of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String XLARGE_IMAGE_COLUMN_WIDTH = "xLargeImageColumnWidth";

	/**
	 * XLarge image row height of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String XLARGE_IMAGE_ROW_HEIGHT = "xLargeImageRowHeight";

	/**
	 * XML file simple property name.
	 * New and old values are of type File.<p>
	**/
	public static final String XML_FILE = "xmlFile";

	/**
	 * XML filename simple property name.
	 * New and old values are of type String.<p>
	**/
	public static final String XML_FILENAME = "xmlFilename";

	/**
	 * XSmall hotspot location.
	 * New and old values are of type Point.<p>
	**/
	public static final String XSMALL_HOTSPOT = "xSmallHotspot";

	/**
	 * XSmall image column width of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String XSMALL_IMAGE_COLUMN_WIDTH = "xSmallImageColumnWidth";

	/**
	 * XSmall image row height of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String XSMALL_IMAGE_ROW_HEIGHT = "xSmallImageRowHeight";

	/**
	 * XXSmall hotspot location.
	 * New and old values are of type Point.<p>
	**/
	public static final String XXSMALL_HOTSPOT = "xxSmallHotspot";

	/**
	 * XXSmall image column width of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String XXSMALL_IMAGE_COLUMN_WIDTH = "xxSmallImageColumnWidth";

	/**
	 * XXSmall image row height of a Species object.
	 * New and old values are of type Integer.<p>
	**/
	public static final String XXSMALL_IMAGE_ROW_HEIGHT = "xxSmallImageRowHeight";
}

