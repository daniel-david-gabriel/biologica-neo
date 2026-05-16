//
// Class : Elements
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2002/02/20 16:44:12 $
// $Author: dima $
//

package org.concord.biologica.engine;

import java.lang.IllegalArgumentException;
import java.lang.String;

/**
 * This class contains the name strings and ids for engine XML elements.
 * They're all here to encourage reusing the same names and ids in multiple elements.
 *
 * @version		$Revision: 1.2 $ $Date: 2002/02/20 16:44:12 $
 * @author 		$Author: dima $
**/

public final class Elements
{
	/**
	 * XML element names and ids - Please keep these in alphabetical
	 * and numeric order.
	 *
	 *     ***** DO NOT CHANGE THESE STRINGS !!!! ****
	 * These strings are being stored in XML files and changing them
	 * would cause us to not recognize an element in an old XML file.
	 * So feel free to add new element names, but do NOT CHANGE any
	 * existing names.  Element names must be unique.
	 *
	 * You can change element id values, as those are used internally only for
	 * efficiency and speed optimizations and are not stored in XML files.  But
	 * a given element id may only be used by a single element below.  In other
	 * words, the same number cannot appear twice below.  So be careful.
	**/
	static final public int    ALGORITHMIC_NUCLEIC_ACID_ELEMENT_ID         = 1;
	static final public String ALGORITHMIC_NUCLEIC_ACID_ELEMENT_NAME       = "algorithmicNucleicAcid";

	static final public int    ALGORITHMIC_NUCLEIC_ACID_ID_ELEMENT_ID      = 2;
	static final public String ALGORITHMIC_NUCLEIC_ACID_ID_ELEMENT_NAME    = "algorithmicNucleicAcidID";

	static final public int    ALLELES_ALTERABLE_ELEMENT_ID                = 3;
	static final public String ALLELES_ALTERABLE_ELEMENT_NAME			   = "allelesAlterable";

	static final public int    ALLELES_VISIBLE_ELEMENT_ID                  = 4;
	static final public String ALLELES_VISIBLE_ELEMENT_NAME			       = "allelesVisible";

	static final public int    BASES_ELEMENT_ID                            = 5;
	static final public String BASES_ELEMENT_NAME			               = "bases";

	static final public int    CHARACTERISTIC_ELEMENT_ID                   = 6;
	static final public String CHARACTERISTIC_ELEMENT_NAME                 = "characteristic";

	static final public int    CHARACTERISTIC_ID_ELEMENT_ID                = 7;
	static final public String CHARACTERISTIC_ID_ELEMENT_NAME              = "characteristicID";

	static final public int    CHARACTERISTIC_IDS_ELEMENT_ID               = 8;
	static final public String CHARACTERISTIC_IDS_ELEMENT_NAME             = "characteristicIDs";

	static final public int    CHILD_IDS_ELEMENT_ID                        = 9;
	static final public String CHILD_IDS_ELEMENT_NAME                      = "childIDs";

	static final public int    COLOR_ELEMENT_ID                            = 210;
	static final public String COLOR_ELEMENT_NAME                          = "color";

	static final public int    COLUMN_INDEX_ELEMENT_ID                     = 10;
	static final public String COLUMN_INDEX_ELEMENT_NAME                   = "columnIndex";

	static final public int    DELETED_ELEMENT_ID                          = 11;
	static final public String DELETED_ELEMENT_NAME			               = "deleted";

	static final public int    DESCRIPTION_ELEMENT_ID                      = 12;
	static final public String DESCRIPTION_ELEMENT_NAME			           = "description";

	static final public int    DIPLOID_TYPE_ELEMENT_ID                     = 13;
	static final public String DIPLOID_TYPE_ELEMENT_NAME			       = "diploidType";

	static final public int    DNA_ALTERABLE_ELEMENT_ID                    = 14;
	static final public String DNA_ALTERABLE_ELEMENT_NAME			       = "dnaAlterable";

	static final public int    DNA_VISIBLE_ELEMENT_ID                      = 15;
	static final public String DNA_VISIBLE_ELEMENT_NAME			           = "dnaVisible";

	static final public int    ELSE_CHARACTERISTIC_ID_ELEMENT_ID           = 16;
	static final public String ELSE_CHARACTERISTIC_ID_ELEMENT_NAME	       = "elseCharacteristicID";

	static final public int    ENVIRONMENT_ELEMENT_ID                      = 17;
	static final public String ENVIRONMENT_ELEMENT_NAME                    = "environment";

	static final public int    ENVIRONMENT_ID_ELEMENT_ID                   = 18;
	static final public String ENVIRONMENT_ID_ELEMENT_NAME                 = "environmentID";

	static final public int    FAMILY_ELEMENT_ID                           = 19;
	static final public String FAMILY_ELEMENT_NAME                         = "family";

	static final public int    FAMILY_ID_ELEMENT_ID                        = 20;
	static final public String FAMILY_ID_ELEMENT_NAME                      = "familyID";

	static final public int    FATAL_ELEMENT_ID                            = 21;
	static final public String FATAL_ELEMENT_NAME                          = "fatal";

	static final public int    FEMALE_PARENT_ID_ELEMENT_ID                 = 22;
	static final public String FEMALE_PARENT_ID_ELEMENT_NAME               = "femaleParentID";

	static final public int    FIRST_ORGANISM_ALLELE_ID_ELEMENT_ID         = 23;
	static final public String FIRST_ORGANISM_ALLELE_ID_ELEMENT_NAME       = "firstOrganismAlleleID";

	static final public int    FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_ID     = 24;
	static final public String FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME   = "firstOrganismChromosomeID";
	
	static final public int    GENDER_ELEMENT_ID                           = 203;
	static final public String GENDER_ELEMENT_NAME	                       = "gender";

	static final public int    GENE_ELEMENT_ID                             = 25;
	static final public String GENE_ELEMENT_NAME                           = "gene";

	static final public int    GENE_ID_ELEMENT_ID                          = 26;
	static final public String GENE_ID_ELEMENT_NAME                        = "geneID";

	static final public int    GENERATION_ELEMENT_ID                       = 27;
	static final public String GENERATION_ELEMENT_NAME                     = "generation";

	static final public int    GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_ID       = 28;
	static final public String GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_NAME     = "genotypeToPhenotypeRule";

	static final public int    GENOTYPE_TO_PHENOTYPE_RULE_ID_ELEMENT_ID    = 29;
	static final public String GENOTYPE_TO_PHENOTYPE_RULE_ID_ELEMENT_NAME  = "genotypeToPhenotypeRuleID";

	static final public int    HEIGHT_ELEMENT_ID                           = 30;
	static final public String HEIGHT_ELEMENT_NAME		                   = "height";

	static final public int    HOLDER_ID_ELEMENT_ID                        = 31;
	static final public String HOLDER_ID_ELEMENT_NAME		               = "holderID";

	static final public int    HOTSPOT_COLOR_ELEMENT_ID                    = 32;
	static final public String HOTSPOT_COLOR_ELEMENT_NAME	               = "hotspotColor";

	static final public int    HOTSPOT_RADIUS_ELEMENT_ID                   = 35;
	static final public String HOTSPOT_RADIUS_ELEMENT_NAME		           = "hotspotRadius";

	static final public int    ID_ELEMENT_ID                               = 36;
	static final public String ID_ELEMENT_NAME				               = "id";

	static final public int    IF_SPECIES_ALLELE_IDS_ELEMENT_ID            = 87;
	static final public String IF_SPECIES_ALLELE_IDS_ELEMENT_NAME          = "ifSpeciesAlleleIDs";

	static final public int    IMAGE_ID_ELEMENT_ID                         = 37;
	static final public String IMAGE_ID_ELEMENT_NAME				       = "imageID";

	static final public int    IMAGE_NUMBER_ELEMENT_ID                     = 38;
	static final public String IMAGE_NUMBER_ELEMENT_NAME				   = "imageNumber";

	static final public int    IMAGE_TYPE_ELEMENT_ID                       = 39;
	static final public String IMAGE_TYPE_ELEMENT_NAME				       = "imageType";

    static final public int    IN_DNA_OR_RNA_ELEMENT_ID                    = 40;
    static final public String IN_DNA_OR_RNA_ELEMENT_NAME                  = "inDNAorRNA";
	
	static final public int    LARGE_HOTSPOT_ELEMENT_ID                    = 41;
	static final public String LARGE_HOTSPOT_ELEMENT_NAME                  = "largeHotspot";

	static final public int    LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID         = 42;
	static final public String LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME       = "largeImageColumnWidth";

	static final public int    LARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID           = 43;
	static final public String LARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME         = "largeImageRowHeight";

    static final public int    LENGTH_IN_BASES_ELEMENT_ID                  = 44;
    static final public String LENGTH_IN_BASES_ELEMENT_NAME                = "lengthInBases";

	static final public int    LOCKED_STATE_ELEMENT_ID                     = 45;
	static final public String LOCKED_STATE_ELEMENT_NAME		           = "lockedState";

	static final public int    MALE_PARENT_ID_ELEMENT_ID                   = 46;
	static final public String MALE_PARENT_ID_ELEMENT_NAME                 = "maleParentID";

	static final public int    MEDIUM_HOTSPOT_ELEMENT_ID                   = 47;
	static final public String MEDIUM_HOTSPOT_ELEMENT_NAME                 = "mediumHotspot";

	static final public int    MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_ID        = 48;
	static final public String MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_NAME      = "mediumImageColumnWidth";

	static final public int    MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_ID          = 49;
	static final public String MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_NAME        = "mediumImageRowHeight";

	static final public int    MUTATION_ALLELE_ELEMENT_ID                  = 50;
	static final public String MUTATION_ALLELE_ELEMENT_NAME                = "mutationAllele";

	static final public int    NAME_ELEMENT_ID                             = 51;
	static final public String NAME_ELEMENT_NAME                           = "name";

	static final public int    NAME_SUPER_VISIBLE_ELEMENT_ID               = 52;
	static final public String NAME_SUPER_VISIBLE_ELEMENT_NAME             = "nameSuperVisible";

	static final public int    NUCLEIC_ACID_ELEMENT_ID                     = 53;
	static final public String NUCLEIC_ACID_ELEMENT_NAME                   = "nucleicAcid";

	static final public int    NUMBER_TYPE_ELEMENT_ID                      = 54;
	static final public String NUMBER_TYPE_ELEMENT_NAME                    = "numberType";

	static final public int    ORGANISM_ELEMENT_ID                         = 55;
	static final public String ORGANISM_ELEMENT_NAME                       = "organism";

	static final public int    ORGANISM_ID_ELEMENT_ID                      = 56;
	static final public String ORGANISM_ID_ELEMENT_NAME                    = "organismID";

	static final public int    ORGANISM_ALLELE_ELEMENT_ID                  = 57;
	static final public String ORGANISM_ALLELE_ELEMENT_NAME                = "organismAllele";

	static final public int    ORGANISM_ALLELE_ID_ELEMENT_ID               = 58;
	static final public String ORGANISM_ALLELE_ID_ELEMENT_NAME             = "organismAlleleID";

	static final public int    ORGANISM_ALLELE_PAIR_ELEMENT_ID             = 59;
	static final public String ORGANISM_ALLELE_PAIR_ELEMENT_NAME           = "organismAllelePair";

	static final public int    ORGANISM_ALLELE_PAIR_ID_ELEMENT_ID          = 60;
	static final public String ORGANISM_ALLELE_PAIR_ID_ELEMENT_NAME        = "organismAllelePairID";

	static final public int    ORGANISM_CHROMOSOME_ELEMENT_ID              = 61;
	static final public String ORGANISM_CHROMOSOME_ELEMENT_NAME            = "organismChromosome";

	static final public int    ORGANISM_CHROMOSOME_ID_ELEMENT_ID           = 62;
	static final public String ORGANISM_CHROMOSOME_ID_ELEMENT_NAME         = "organismChromosomeID";

	static final public int    ORGANISM_CHROMOSOME_PAIR_ELEMENT_ID         = 63;
	static final public String ORGANISM_CHROMOSOME_PAIR_ELEMENT_NAME       = "organismChromosomePair";

	static final public int    ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_ID      = 64;
	static final public String ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_NAME    = "organismChromosomePairID";

	static final public int    ORGANISM_IMAGE_ELEMENT_ID                   = 200;
	static final public String ORGANISM_IMAGE_ELEMENT_NAME                 = "organismImage";

	static final public int    ORGANISM_IMAGE_ID_ELEMENT_ID                = 201;
	static final public String ORGANISM_IMAGE_ID_ELEMENT_NAME              = "organismImageID";

    static final public int    PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_ID      = 65;
	static final public String PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_NAME    = "pedigreeSymbolFirstColor";

    static final public int    PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_ID     = 68;
	static final public String PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_NAME   = "pedigreeSymbolSecondColor";

    static final public int    PEDIGREE_SYMBOL_TYPE_ELEMENT_ID             = 71;
	static final public String PEDIGREE_SYMBOL_TYPE_ELEMENT_NAME           = "pedigreeSymbolType";

	static final public int    PLOIDY_NUMBER_ELEMENT_ID                    = 72;
	static final public String PLOIDY_NUMBER_ELEMENT_NAME                  = "ploidyNumber";

	static final public int    RAW_NUCLEIC_ACID_ELEMENT_ID                 = 73;
	static final public String RAW_NUCLEIC_ACID_ELEMENT_NAME               = "rawNucleicAcid";

	static final public int    RAW_NUCLEIC_ACID_ID_ELEMENT_ID              = 74;
	static final public String RAW_NUCLEIC_ACID_ID_ELEMENT_NAME            = "rawNucleicAcidID";

	static final public int    ROW_INDEX_ELEMENT_ID                        = 75;
	static final public String ROW_INDEX_ELEMENT_NAME                      = "rowIndex";

	static final public int    SCHEMA_VERSION_ELEMENT_ID                   = 76;
	static final public String SCHEMA_VERSION_ELEMENT_NAME	               = "schemaVersion";
	
	static final public int    SECOND_ORGANISM_ALLELE_ID_ELEMENT_ID        = 77;
	static final public String SECOND_ORGANISM_ALLELE_ID_ELEMENT_NAME      = "secondOrganismAlleleID";

	static final public int    SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_ID    = 78;
	static final public String SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME  = "secondOrganismChromosomeID";

	static final public int    SEED_ELEMENT_ID                             = 79;
	static final public String SEED_ELEMENT_NAME	                       = "seed";

	static final public int    SEX_ELEMENT_ID                              = 80;
	static final public String SEX_ELEMENT_NAME	                           = "sex";

	static final public int    SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_ID    = 221;
	static final public String SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_NAME  = "showAsTextInOrganismView";

	static final public int    SMALL_HOTSPOT_ELEMENT_ID                    = 81;
	static final public String SMALL_HOTSPOT_ELEMENT_NAME                  = "smallHotspot";

	static final public int    SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID         = 82;
	static final public String SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME       = "smallImageColumnWidth";

	static final public int    SMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID           = 83;
	static final public String SMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME         = "smallImageRowHeight";

	static final public int    SPECIES_ELEMENT_ID                          = 84;
	static final public String SPECIES_ELEMENT_NAME                        = "species";

	static final public int    SPECIES_ID_ELEMENT_ID                       = 85;
	static final public String SPECIES_ID_ELEMENT_NAME                     = "speciesID";

	static final public int    SPECIES_ALLELE_ELEMENT_ID                   = 86;
	static final public String SPECIES_ALLELE_ELEMENT_NAME                 = "speciesAllele";

	static final public int    SPECIES_ALLELE_ID_ELEMENT_ID                = 88;
	static final public String SPECIES_ALLELE_ID_ELEMENT_NAME              = "speciesAlleleID";

	static final public int    SPECIES_CHROMOSOME_ELEMENT_ID               = 89;
	static final public String SPECIES_CHROMOSOME_ELEMENT_NAME             = "speciesChromosome";

	static final public int    SPECIES_CHROMOSOME_ID_ELEMENT_ID            = 90;
	static final public String SPECIES_CHROMOSOME_ID_ELEMENT_NAME          = "speciesChromosomeID";

	static final public int    SPECIES_IMAGE_ELEMENT_ID                    = 91;
	static final public String SPECIES_IMAGE_ELEMENT_NAME                  = "speciesImage";

	static final public int    SPECIES_IMAGE_ID_ELEMENT_ID                 = 92;
	static final public String SPECIES_IMAGE_ID_ELEMENT_NAME               = "speciesImageID";

	static final public int    SPECIES_IMAGE_COLUMN_ELEMENT_ID             = 93;
	static final public String SPECIES_IMAGE_COLUMN_ELEMENT_NAME           = "speciesImageColumn";

	static final public int    SPECIES_IMAGE_COLUMN_ID_ELEMENT_ID          = 94;
	static final public String SPECIES_IMAGE_COLUMN_ID_ELEMENT_NAME        = "speciesImageColumnID";

	static final public int    SPECIES_IMAGE_ROW_ELEMENT_ID                = 95;
	static final public String SPECIES_IMAGE_ROW_ELEMENT_NAME              = "speciesImageRow";

	static final public int    SPECIES_IMAGE_ROW_ID_ELEMENT_ID             = 96;
	static final public String SPECIES_IMAGE_ROW_ID_ELEMENT_NAME           = "speciesImageRowID";

    static final public int    START_INDEX_IN_HOLDER_ELEMENT_ID            = 97;
	static final public String START_INDEX_IN_HOLDER_ELEMENT_NAME          = "startIndexInHolder";

    static final public int    STRAND_ELEMENT_ID                           = 98;
	static final public String STRAND_ELEMENT_NAME                         = "strand";

	static final public int    TERRAIN_ELEMENT_ID                          = 99;
	static final public String TERRAIN_ELEMENT_NAME                        = "terrain";

	static final public int    TERRAIN_ID_ELEMENT_ID                       = 100;
	static final public String TERRAIN_ID_ELEMENT_NAME                     = "terrainID";

	static final public int    TERRAIN_IDS_ELEMENT_ID                      = 101;
	static final public String TERRAIN_IDS_ELEMENT_NAME                    = "terrainIDs";

	static final public int    TEXT_SYMBOL_ELEMENT_ID                      = 102;
	static final public String TEXT_SYMBOL_ELEMENT_NAME                    = "textSymbol";

	static final public int    THEN_CHARACTERISTIC_ID_ELEMENT_ID           = 103;
	static final public String THEN_CHARACTERISTIC_ID_ELEMENT_NAME         = "thenCharacteristicID";

	static final public int    TRAIT_ELEMENT_ID                            = 104;
	static final public String TRAIT_ELEMENT_NAME                          = "trait";

	static final public int    TRAIT_ID_ELEMENT_ID                         = 105;
	static final public String TRAIT_ID_ELEMENT_NAME				       = "traitID";

	static final public int    VISIBLE_ELEMENT_ID                          = 106;
	static final public String VISIBLE_ELEMENT_NAME				           = "visible";

	static final public int    WEIGHT_ELEMENT_ID                           = 107;
	static final public String WEIGHT_ELEMENT_NAME                         = "weight";

	static final public int    WIDTH_ELEMENT_ID                            = 108;
	static final public String WIDTH_ELEMENT_NAME                          = "width";

	static final public int    WORLD_ELEMENT_ID                            = 109;
	static final public String WORLD_ELEMENT_NAME                          = "world";
	
	static final public int    WORLD_ID_ELEMENT_ID                         = 110;
	static final public String WORLD_ID_ELEMENT_NAME                       = "worldID";

	static final public int    XLARGE_HOTSPOT_ELEMENT_ID                   = 111;
	static final public String XLARGE_HOTSPOT_ELEMENT_NAME                 = "xLargeHotspot";

	static final public int    XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID        = 112;
	static final public String XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME      = "xLargeImageColumnWidth";

	static final public int    XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID          = 113;
	static final public String XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME        = "xLargeImageRowHeight";

	static final public int    XSMALL_HOTSPOT_ELEMENT_ID                   = 114;
	static final public String XSMALL_HOTSPOT_ELEMENT_NAME                 = "xSmallHotspot";

	static final public int    XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID        = 115;
	static final public String XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME      = "xSmallImageColumnWidth";

	static final public int    XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID          = 116;
	static final public String XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME        = "xSmallImageRowHeight";

	static final public int    XXSMALL_HOTSPOT_ELEMENT_ID                  = 117;
	static final public String XXSMALL_HOTSPOT_ELEMENT_NAME                = "xxSmallHotspot";

	static final public int    XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID       = 118;
	static final public String XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME     = "xxSmallImageColumnWidth";

	static final public int    XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID         = 119;
	static final public String XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME       = "xxSmallImageRowHeight";
	
	static final public int    IMAGE_DATA_SOURCE_SUPPORT_ELEMENT_ID        = 120;
	static final public String IMAGE_DATA_SOURCE_SUPPORT_ELEMENT_NAME      = "imageDataSourceSupport";

	/**
	 * Map the given element name to an element id, returning that id.<p>
	 *
	 * This method will throw an IllegalArgumentException if the name is not recognized.<p>
	 *
	 * @param    anElementName String - the element name
	 * @return   int - the element id
	 * @exception IllegalArgumentException - unrecognized element name
	**/
	public static int mapElementNameToID(String anElementName)
	{
		char c = anElementName.charAt(0);

		switch (c)
		{
			case 'a':
				if (anElementName.equals(ALGORITHMIC_NUCLEIC_ACID_ELEMENT_NAME))
				{
					return ALGORITHMIC_NUCLEIC_ACID_ELEMENT_ID;
				}
				else if (anElementName.equals(ALGORITHMIC_NUCLEIC_ACID_ID_ELEMENT_NAME))
				{
					return ALGORITHMIC_NUCLEIC_ACID_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ALLELES_ALTERABLE_ELEMENT_NAME))
				{
					return ALLELES_ALTERABLE_ELEMENT_ID;
				}
				else if (anElementName.equals(ALLELES_VISIBLE_ELEMENT_NAME))
				{
					return ALLELES_VISIBLE_ELEMENT_ID;
				}
				break;

			case 'b':
				if (anElementName.equals(BASES_ELEMENT_NAME))
				{
					return BASES_ELEMENT_ID;
				}
				break;

			case 'c':
				if (anElementName.equals(CHARACTERISTIC_ELEMENT_NAME))
				{
					return CHARACTERISTIC_ELEMENT_ID;
				}
				else if (anElementName.equals(CHARACTERISTIC_ID_ELEMENT_NAME))
				{
					return CHARACTERISTIC_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(CHARACTERISTIC_IDS_ELEMENT_NAME))
				{
					return CHARACTERISTIC_IDS_ELEMENT_ID;
				}
				else if (anElementName.equals(CHILD_IDS_ELEMENT_NAME))
				{
					return CHILD_IDS_ELEMENT_ID;
				}
				else if (anElementName.equals(COLOR_ELEMENT_NAME))
				{
					return COLOR_ELEMENT_ID;
				}
				else if (anElementName.equals(COLUMN_INDEX_ELEMENT_NAME))
				{
					return COLUMN_INDEX_ELEMENT_ID;
				}
				break;

			case 'd':
				if (anElementName.equals(DELETED_ELEMENT_NAME))
				{
					return DELETED_ELEMENT_ID;
				}
				else if (anElementName.equals(DESCRIPTION_ELEMENT_NAME))
				{
					return DESCRIPTION_ELEMENT_ID;
				}
				else if (anElementName.equals(DIPLOID_TYPE_ELEMENT_NAME))
				{
					return DIPLOID_TYPE_ELEMENT_ID;
				}
				else if (anElementName.equals(DNA_ALTERABLE_ELEMENT_NAME))
				{
					return DNA_ALTERABLE_ELEMENT_ID;
				}
				else if (anElementName.equals(DNA_VISIBLE_ELEMENT_NAME))
				{
					return DNA_VISIBLE_ELEMENT_ID;
				}
				break;

			case 'e':
				if (anElementName.equals(ELSE_CHARACTERISTIC_ID_ELEMENT_NAME))
				{
					return ELSE_CHARACTERISTIC_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ENVIRONMENT_ELEMENT_NAME))
				{
					return ENVIRONMENT_ELEMENT_ID;
				}
				else if (anElementName.equals(ENVIRONMENT_ID_ELEMENT_NAME))
				{
					return ENVIRONMENT_ID_ELEMENT_ID;
				}
				break;

			case 'f':
				if (anElementName.equals(FAMILY_ELEMENT_NAME))
				{
					return FAMILY_ELEMENT_ID;
				}
				else if (anElementName.equals(FAMILY_ID_ELEMENT_NAME))
				{
					return FAMILY_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(FATAL_ELEMENT_NAME))
				{
					return FATAL_ELEMENT_ID;
				}
				else if (anElementName.equals(FEMALE_PARENT_ID_ELEMENT_NAME))
				{
					return FEMALE_PARENT_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(FIRST_ORGANISM_ALLELE_ID_ELEMENT_NAME))
				{
					return FIRST_ORGANISM_ALLELE_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME))
				{
					return FIRST_ORGANISM_CHROMOSOME_ID_ELEMENT_ID;
				}
				break;

			case 'g':
				if (anElementName.equals(GENDER_ELEMENT_NAME))
				{
					return GENDER_ELEMENT_ID;
				}
				else if (anElementName.equals(GENE_ELEMENT_NAME))
				{
					return GENE_ELEMENT_ID;
				}
				else if (anElementName.equals(GENE_ID_ELEMENT_NAME))
				{
					return GENE_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(GENERATION_ELEMENT_NAME))
				{
					return GENERATION_ELEMENT_ID;
				}
				else if (anElementName.equals(GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_NAME))
				{
					return GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_ID;
				}
				else if (anElementName.equals(GENOTYPE_TO_PHENOTYPE_RULE_ID_ELEMENT_NAME))
				{
					return GENOTYPE_TO_PHENOTYPE_RULE_ID_ELEMENT_ID;
				}
				break;

			case 'h':
				if (anElementName.equals(HEIGHT_ELEMENT_NAME))
				{
					return HEIGHT_ELEMENT_ID;
				}
				else if (anElementName.equals(HOLDER_ID_ELEMENT_NAME))
				{
					return HOLDER_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(HOTSPOT_COLOR_ELEMENT_NAME))
				{
					return HOTSPOT_COLOR_ELEMENT_ID;
				}
				else if (anElementName.equals(HOTSPOT_RADIUS_ELEMENT_NAME))
				{
					return HOTSPOT_RADIUS_ELEMENT_ID;
				}
				break;

			case 'i':
				if (anElementName.equals(ID_ELEMENT_NAME))
				{
					return ID_ELEMENT_ID;
				}
				else if (anElementName.equals(IF_SPECIES_ALLELE_IDS_ELEMENT_NAME))
				{
					return IF_SPECIES_ALLELE_IDS_ELEMENT_ID;
				}
				else if (anElementName.equals(IMAGE_ID_ELEMENT_NAME))
				{
					return IMAGE_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(IMAGE_NUMBER_ELEMENT_NAME))
				{
					return IMAGE_NUMBER_ELEMENT_ID;
				}
				else if (anElementName.equals(IMAGE_TYPE_ELEMENT_NAME))
				{
					return IMAGE_TYPE_ELEMENT_ID;
				}
				else if (anElementName.equals(IN_DNA_OR_RNA_ELEMENT_NAME))
				{
					return IN_DNA_OR_RNA_ELEMENT_ID;
				}
				else if (anElementName.equals(IMAGE_DATA_SOURCE_SUPPORT_ELEMENT_NAME))
				{
					return IMAGE_DATA_SOURCE_SUPPORT_ELEMENT_ID;//dima
				}
				break;

			case 'l':
				if (anElementName.equals(LARGE_HOTSPOT_ELEMENT_NAME))
				{
					return LARGE_HOTSPOT_ELEMENT_ID;
				}
				else if (anElementName.equals(LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME))
				{
					return LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID;
				}
				else if (anElementName.equals(LARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME))
				{
					return LARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID;
				}
				else if (anElementName.equals(LENGTH_IN_BASES_ELEMENT_NAME))
				{
					return LENGTH_IN_BASES_ELEMENT_ID;
				}
				else if (anElementName.equals(LOCKED_STATE_ELEMENT_NAME))
				{
					return LOCKED_STATE_ELEMENT_ID;
				}
				break;

			case 'm':
				if (anElementName.equals(MALE_PARENT_ID_ELEMENT_NAME))
				{
					return MALE_PARENT_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(MEDIUM_HOTSPOT_ELEMENT_NAME))
				{
					return MEDIUM_HOTSPOT_ELEMENT_ID;
				}
				else if (anElementName.equals(MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_NAME))
				{
					return MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_ID;
				}
				else if (anElementName.equals(MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_NAME))
				{
					return MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_ID;
				}
				else if (anElementName.equals(MUTATION_ALLELE_ELEMENT_NAME))
				{
					return MUTATION_ALLELE_ELEMENT_ID;
				}
				break;

			case 'n':
				if (anElementName.equals(NAME_ELEMENT_NAME))
				{
					return NAME_ELEMENT_ID;
				}
				else if (anElementName.equals(NAME_SUPER_VISIBLE_ELEMENT_NAME))
				{
					return NAME_SUPER_VISIBLE_ELEMENT_ID;
				}
				else if (anElementName.equals(NUCLEIC_ACID_ELEMENT_NAME))
				{
					return NUCLEIC_ACID_ELEMENT_ID;
				}
				else if (anElementName.equals(NUMBER_TYPE_ELEMENT_NAME))
				{
					return NUMBER_TYPE_ELEMENT_ID;
				}
				break;

			case 'o':
				if (anElementName.equals(ORGANISM_ELEMENT_NAME))
				{
					return ORGANISM_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_ID_ELEMENT_NAME))
				{
					return ORGANISM_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_ALLELE_ELEMENT_NAME))
				{
					return ORGANISM_ALLELE_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_ALLELE_ID_ELEMENT_NAME))
				{
					return ORGANISM_ALLELE_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_ALLELE_PAIR_ELEMENT_NAME))
				{
					return ORGANISM_ALLELE_PAIR_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_ALLELE_PAIR_ID_ELEMENT_NAME))
				{
					return ORGANISM_ALLELE_PAIR_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_CHROMOSOME_ELEMENT_NAME))
				{
					return ORGANISM_CHROMOSOME_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_CHROMOSOME_ID_ELEMENT_NAME))
				{
					return ORGANISM_CHROMOSOME_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_CHROMOSOME_PAIR_ELEMENT_NAME))
				{
					return ORGANISM_CHROMOSOME_PAIR_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_NAME))
				{
					return ORGANISM_CHROMOSOME_PAIR_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_IMAGE_ELEMENT_NAME))
				{
					return ORGANISM_IMAGE_ELEMENT_ID;
				}
				else if (anElementName.equals(ORGANISM_IMAGE_ID_ELEMENT_NAME))
				{
					return ORGANISM_IMAGE_ID_ELEMENT_ID;
				}
				break;

			case 'p':
				if (anElementName.equals(PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_NAME))
				{
					return PEDIGREE_SYMBOL_FIRST_COLOR_ELEMENT_ID;
				}
				else if (anElementName.equals(PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_NAME))
				{
					return PEDIGREE_SYMBOL_SECOND_COLOR_ELEMENT_ID;
				}
				else if (anElementName.equals(PEDIGREE_SYMBOL_TYPE_ELEMENT_NAME))
				{
					return PEDIGREE_SYMBOL_TYPE_ELEMENT_ID;
				}
				else if (anElementName.equals(PLOIDY_NUMBER_ELEMENT_NAME))
				{
					return PLOIDY_NUMBER_ELEMENT_ID;
				}
				break;

			case 'r':
				if (anElementName.equals(RAW_NUCLEIC_ACID_ELEMENT_NAME))
				{
					return RAW_NUCLEIC_ACID_ELEMENT_ID;
				}
				else if (anElementName.equals(RAW_NUCLEIC_ACID_ID_ELEMENT_NAME))
				{
					return RAW_NUCLEIC_ACID_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(ROW_INDEX_ELEMENT_NAME))
				{
					return ROW_INDEX_ELEMENT_ID;
				}
				break;

			case 's':
				if (anElementName.equals(SCHEMA_VERSION_ELEMENT_NAME))
				{
					return SCHEMA_VERSION_ELEMENT_ID;
				}
				else if (anElementName.equals(SECOND_ORGANISM_ALLELE_ID_ELEMENT_NAME))
				{
					return SECOND_ORGANISM_ALLELE_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_NAME))
				{
					return SECOND_ORGANISM_CHROMOSOME_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(SEED_ELEMENT_NAME))
				{
					return SEED_ELEMENT_ID;
				}
				else if (anElementName.equals(SEX_ELEMENT_NAME))
				{
					return SEX_ELEMENT_ID;
				}
				else if (anElementName.equals(SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_NAME))
				{
					return SHOW_AS_TEXT_IN_ORGANISM_VIEW_ELEMENT_ID;
				}
				else if (anElementName.equals(SMALL_HOTSPOT_ELEMENT_NAME))
				{
					return SMALL_HOTSPOT_ELEMENT_ID;
				}
				else if (anElementName.equals(SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME))
				{
					return SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID;
				}
				else if (anElementName.equals(SMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME))
				{
					return SMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_ELEMENT_NAME))
				{
					return SPECIES_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_ID_ELEMENT_NAME))
				{
					return SPECIES_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_ALLELE_ELEMENT_NAME))
				{
					return SPECIES_ALLELE_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_ALLELE_ID_ELEMENT_NAME))
				{
					return SPECIES_ALLELE_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_CHROMOSOME_ELEMENT_NAME))
				{
					return SPECIES_CHROMOSOME_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_CHROMOSOME_ID_ELEMENT_NAME))
				{
					return SPECIES_CHROMOSOME_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_IMAGE_ELEMENT_NAME))
				{
					return SPECIES_IMAGE_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_IMAGE_ID_ELEMENT_NAME))
				{
					return SPECIES_IMAGE_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_IMAGE_COLUMN_ELEMENT_NAME))
				{
					return SPECIES_IMAGE_COLUMN_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_IMAGE_COLUMN_ID_ELEMENT_NAME))
				{
					return SPECIES_IMAGE_COLUMN_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_IMAGE_ROW_ELEMENT_NAME))
				{
					return SPECIES_IMAGE_ROW_ELEMENT_ID;
				}
				else if (anElementName.equals(SPECIES_IMAGE_ROW_ID_ELEMENT_NAME))
				{
					return SPECIES_IMAGE_ROW_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(START_INDEX_IN_HOLDER_ELEMENT_NAME))
				{
					return START_INDEX_IN_HOLDER_ELEMENT_ID;
				}
				else if (anElementName.equals(STRAND_ELEMENT_NAME))
				{
					return STRAND_ELEMENT_ID;
				}
				break;

			case 't':
				if (anElementName.equals(TERRAIN_ELEMENT_NAME))
				{
					return TERRAIN_ELEMENT_ID;
				}
				else if (anElementName.equals(TERRAIN_ID_ELEMENT_NAME))
				{
					return TERRAIN_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(TERRAIN_IDS_ELEMENT_NAME))
				{
					return TERRAIN_IDS_ELEMENT_ID;
				}
				else if (anElementName.equals(TEXT_SYMBOL_ELEMENT_NAME))
				{
					return TEXT_SYMBOL_ELEMENT_ID;
				}
				else if (anElementName.equals(THEN_CHARACTERISTIC_ID_ELEMENT_NAME))
				{
					return THEN_CHARACTERISTIC_ID_ELEMENT_ID;
				}
				else if (anElementName.equals(TRAIT_ELEMENT_NAME))
				{
					return TRAIT_ELEMENT_ID;
				}
				else if (anElementName.equals(TRAIT_ID_ELEMENT_NAME))
				{
					return TRAIT_ID_ELEMENT_ID;
				}
				break;

			case 'v':
				if (anElementName.equals(VISIBLE_ELEMENT_NAME))
				{
					return VISIBLE_ELEMENT_ID;
				}
				break;

			case 'w':
				if (anElementName.equals(WEIGHT_ELEMENT_NAME))
				{
					return WEIGHT_ELEMENT_ID;
				}
				else if (anElementName.equals(WIDTH_ELEMENT_NAME))
				{
					return WIDTH_ELEMENT_ID;
				}
				else if (anElementName.equals(WORLD_ELEMENT_NAME))
				{
					return WORLD_ELEMENT_ID;
				}
				else if (anElementName.equals(WORLD_ID_ELEMENT_NAME))
				{
					return WORLD_ID_ELEMENT_ID;
				}
				break;

			case 'x':
				if (anElementName.equals(XLARGE_HOTSPOT_ELEMENT_NAME))
				{
					return XLARGE_HOTSPOT_ELEMENT_ID;
				}
				else if (anElementName.equals(XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME))
				{
					return XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID;
				}
				else if (anElementName.equals(XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME))
				{
					return XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID;
				}
				else if (anElementName.equals(XSMALL_HOTSPOT_ELEMENT_NAME))
				{
					return XSMALL_HOTSPOT_ELEMENT_ID;
				}
				else if (anElementName.equals(XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME))
				{
					return XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID;
				}
				else if (anElementName.equals(XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME))
				{
					return XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID;
				}
				else if (anElementName.equals(XXSMALL_HOTSPOT_ELEMENT_NAME))
				{
					return XXSMALL_HOTSPOT_ELEMENT_ID;
				}
				else if (anElementName.equals(XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME))
				{
					return XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID;
				}
				else if (anElementName.equals(XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME))
				{
					return XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID;
				}
				break;
		}

		// If we're here, then we failed to find a match.  Throw an exception.
		throw new IllegalArgumentException(EngineStrings.UNRECOGNIZED_XML_ELEMENT_NAME + anElementName);
	}
}
