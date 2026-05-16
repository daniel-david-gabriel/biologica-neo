//
// Class : Species
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.5 $
// $Date: 2004/05/10 21:08:05 $
// $Author: swang $
//

package org.concord.biologica.engine;

import java.io.File;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;

import org.xml.sax.AttributeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//import com.sun.xml.parser.Resolver;
//import com.sun.xml.tree.XmlDocument;
import org.apache.xerces.parsers.SAXParser;

/**
 * This class represents a species.<p>
 *
 * The ploidy number of a species may not be changed during the species' lifetime.<p>
 *
 * The name of a species may be changed, although be careful as no notification is
 * done for this change - at least not yet.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.DESCRIPTION - the object description has changed
 * <li> EngineProp.DIPLOID_TYPE - species diploid type
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_ADDED - a genotype to phenotype rule added
 * <li> EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_MOVED - a genotype to phenotype rule was moved (its position changed)
 * <li> EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_REMOVED - a genotype to phenotype rule removed
 * <li> EngineProp.LOCKED_STATE - object has been locked or unlocked
 * <li> EngineProp.NAME - name of this species has changed
 * <li> EngineProp.IMAGE_ROW_HEIGHT - height of rows in the images of this species has changed
 * <li> EngineProp.SPECIES_CHROMOSOME_ADDED - a chromosome was added
 * <li> EngineProp.SPECIES_CHROMOSOME_REMOVED - a chromosome was removed
 * <li> EngineProp.SPECIES_IMAGE_ADDED - a species image was added
 * <li> EngineProp.SPECIES_IMAGE_REMOVED - a species image was removed
 * <li> EngineProp.TRAIT_ADDED - a trait was added
 * <li> EngineProp.TRAIT_REMOVED - a trait was removed
 * <li> EngineProp.XXSMALL_IMAGE_ROW_HEIGHT - height of rows in the xxsmall size images of this species has changed
 * <li> EngineProp.XXSMALL_IMAGE_COLUMN_WIDTH - width of columns in the xxsmall size images of this species has changed
 * <li> EngineProp.XSMALL_IMAGE_ROW_HEIGHT - height of rows in the xsmall size images of this species has changed
 * <li> EngineProp.XSMALL_IMAGE_COLUMN_WIDTH - width of columns in the xsmall size images of this species has changed
 * <li> EngineProp.SMALL_IMAGE_ROW_HEIGHT - height of rows in the small size images of this species has changed
 * <li> EngineProp.SMALL_IMAGE_COLUMN_WIDTH - width of columns in the small size images of this species has changed
 * <li> EngineProp.MEDIUM_IMAGE_ROW_HEIGHT - height of rows in the medium size images of this species has changed
 * <li> EngineProp.MEDIUM_IMAGE_COLUMN_WIDTH - width of columns in the medium size images of this species has changed
 * <li> EngineProp.LARGE_IMAGE_ROW_HEIGHT - height of rows in the large size images of this species has changed
 * <li> EngineProp.LARGE_IMAGE_COLUMN_WIDTH - width of columns in the large size images of this species has changed
 * <li> EngineProp.XLARGE_IMAGE_ROW_HEIGHT - height of rows in the xlarge size images of this species has changed
 * <li> EngineProp.XLARGE_IMAGE_COLUMN_WIDTH - width of columns in the xlarge size images of this species has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#DESCRIPTION
 * @see org.concord.biologica.engine.EngineProp#DIPLOID_TYPE
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#GENOTYPE_TO_PHENOTYPE_RULE_ADDED
 * @see org.concord.biologica.engine.EngineProp#GENOTYPE_TO_PHENOTYPE_RULE_MOVED
 * @see org.concord.biologica.engine.EngineProp#GENOTYPE_TO_PHENOTYPE_RULE_REMOVED
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 * @see org.concord.biologica.engine.EngineProp#NAME
 * @see org.concord.biologica.engine.EngineProp#SPECIES_CHROMOSOME_ADDED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_CHROMOSOME_REMOVED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_ADDED
 * @see org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_REMOVED
 * @see org.concord.biologica.engine.EngineProp#TRAIT_ADDED
 * @see org.concord.biologica.engine.EngineProp#TRAIT_REMOVED
 * @see org.concord.biologica.engine.EngineProp#XXSMALL_IMAGE_COLUMN_WIDTH
 * @see org.concord.biologica.engine.EngineProp#XXSMALL_IMAGE_ROW_HEIGHT
 * @see org.concord.biologica.engine.EngineProp#XSMALL_IMAGE_COLUMN_WIDTH
 * @see org.concord.biologica.engine.EngineProp#XSMALL_IMAGE_ROW_HEIGHT
 * @see org.concord.biologica.engine.EngineProp#SMALL_IMAGE_COLUMN_WIDTH
 * @see org.concord.biologica.engine.EngineProp#SMALL_IMAGE_ROW_HEIGHT
 * @see org.concord.biologica.engine.EngineProp#MEDIUM_IMAGE_COLUMN_WIDTH
 * @see org.concord.biologica.engine.EngineProp#MEDIUM_IMAGE_ROW_HEIGHT
 * @see org.concord.biologica.engine.EngineProp#LARGE_IMAGE_COLUMN_WIDTH
 * @see org.concord.biologica.engine.EngineProp#LARGE_IMAGE_ROW_HEIGHT
 * @see org.concord.biologica.engine.EngineProp#XLARGE_IMAGE_COLUMN_WIDTH
 * @see org.concord.biologica.engine.EngineProp#XLARGE_IMAGE_ROW_HEIGHT
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.5 $ $Date: 2004/05/10 21:08:05 $
 * @author 		$Author: swang $
**/

public final class Species
extends EngineObject
implements Serializable, PropertyChangeListener
{
    /**
     * One type of diploid species - XX female, XY male (e.g. humans)
    **/
    public static final int		DIPLOID_TYPE_XX_FEMALE_XY_MALE = 1;
    public static final String  DIPLOID_TYPE_XX_FEMALE_XY_MALE_STRING = "XXFemaleXYMale";

    /**
     * Second type of diploid species - XY female, XX male (e.g. dragons)
    **/
    public static final int		DIPLOID_TYPE_XY_FEMALE_XX_MALE = 2;
    public static final String  DIPLOID_TYPE_XY_FEMALE_XX_MALE_STRING = "XYFemaleXXMale";

    /**
     * Third type of diploid species - no sex chromosomes (e.g. plants)
    **/
    public static final int		DIPLOID_TYPE_NO_SEX_CHROMOSOMES = 3;
    public static final String  DIPLOID_TYPE_NO_SEX_CHROMOSOMES_STRING = "NoSexChromosomes";

    /**
     * Gender values used in defining genotype to phenotype rules,
     * species image columns and species image rows.
    **/
    static final public int	    FEMALE_AND_MALE	= 1;
    static final public String  FEMALE_AND_MALE_STRING = "femaleAndMale";

    static final public int	    FEMALE_ONLY		= 2;
    static final public String  FEMALE_ONLY_STRING = "femaleOnly";

    static final public int	    MALE_ONLY		= 3;
    static final public String  MALE_ONLY_STRING = "maleOnly";

    /**
     * Schema version of this class, used in serialization.<p>
    **/
    static final private int	schemaVersion = 1;

    /**
     * Random number generator used in choosing alleles.<p>
    **/
    static private Random random = new Random();

    /**
     * Name of this species.  May not be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.NAME.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#NAME
    **/
    private String				name;

    /**
     * Description of this species.  May be null.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.DESCRIPTION.<p>
     *
     * @see		org.concord.biologica.engine.EngineProp#DESCRIPTION
    **/
    private String				description;

    /**
     * World containing this species.  May not be null.<p>
    **/
    private World				world;

    /**
     * Ploidy number.  Must be either 1 (haploid) or 2 (diploid) or 3 (both).
     * Note that a single species may have both haploid and diploid
     * organisms (e.g. yeast can be either depending on the environment).
     * A single organism cannot change ploidy number after it is created.<p>
     *
     * Read-only.<p>
    **/
    private int					ploidyNumber;

    /**
     * Diploid type. Only meaningful if the ploidyNumber is 2.
    **/
    private int					diploidType;

    /**
     * Species non-sex chromosomes that make up this species.  Never null.<p>
     *
     * All the objects on this vector must be instances of SpeciesChromosome.<p>
     *
     * When a SpeciesChromosome object is created and added to this vector,
     * a EngineProp.SPECIES_CHROMOSOME_ADDED property change event is fired.<p>
     *
     * When a SpeciesChromosome object is deleted and removed from this vector,
     * a EngineProp.SPECIES_CHROMOSOME_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.SpeciesChromosome
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_CHROMOSOME_ADDED
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_CHROMOSOME_REMOVED
    **/
    private Vector				nonSexChromosomes;

    /**
     * Species sex chromosomes for this species.  Never null.<p>
     *
     * All the objects on this vector must be instances of SpeciesChromosome.<p>
     *
     * When a SpeciesChromosome object is created and added to this vector,
     * a EngineProp.SPECIES_CHROMOSOME_ADDED property change event is fired.<p>
     *
     * When a SpeciesChromosome object is deleted and removed from this vector,
     * a EngineProp.SPECIES_CHROMOSOME_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.SpeciesChromosome
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_CHROMOSOME_ADDED
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_CHROMOSOME_REMOVED
    **/
    private Vector				sexChromosomes;

    /**
     * Traits for this species.  Never null.<p>
     *
     * All the objects on this vector must be instances of Trait.<p>
     *
     * All the objects on this vector must be instances of SpeciesChromosome.<p>
     *
     * When a Trait object is created and added to this vector,
     * a EngineProp.TRAIT_ADDED property change event is fired.<p>
     *
     * When a Trait object is deleted and removed from this vector,
     * a EngineProp.TRAIT_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.Trait
     * @see		org.concord.biologica.engine.EngineProp#TRAIT_ADDED
     * @see		org.concord.biologica.engine.EngineProp#TRAIT_REMOVED
    **/
    private Vector				traits;

    /**
     * Genotype to Phenotype rules for this species.  Never null.<p>
     *
     * All the objects on this vector must be instances of GenotypeToPhenotypeRule.<p>
     *
     * When a GenotypeToPhenotypeRule object is created and added to this vector,
     * a EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_ADDED property change event is fired.<p>
     *
     * When a GenotypeToPhenotypeRule object is deleted and removed from this vector,
     * a EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.GenotypeToPhenotypeRule
     * @see		org.concord.biologica.engine.EngineProp#GENOTYPE_TO_PHENOTYPE_RULE_ADDED
     * @see		org.concord.biologica.engine.EngineProp#GENOTYPE_TO_PHENOTYPE_RULE_REMOVED
    **/
    private Vector				genotypeToPhenotypeRules;

    /**
     * Species images for this species.  Never null.<p>
     *
     * All the objects on this vector must be instances of SpeciesImage.<p>
     *
     * When a SpeciesImage object is created and added to this vector,
     * a EngineProp.SPECIES_IMAGE_ADDED property change event is fired.<p>
     *
     * When a SpeciesImage object is deleted and removed from this vector,
     * a EngineProp.SPECIES_IMAGE_REMOVED property change event is fired.<p>
     *
     * @see		org.concord.biologica.engine.SpeciesImage
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_ADDED
     * @see		org.concord.biologica.engine.EngineProp#SPECIES_IMAGE_REMOVED
    **/
    private Vector				speciesImages;

    /**
     * Width of columns in the xxSmall images of this species.
    **/
    private int					xxSmallImageColumnWidth;

    /**
     * Height of rows in the xxSmall images of this species.
    **/
    private int					xxSmallImageRowHeight;

    /**
     * Width of columns in the xSmall images of this species.
    **/
    private int					xSmallImageColumnWidth;

    /**
     * Height of rows in the xSmall images of this species.
    **/
    private int					xSmallImageRowHeight;

    /**
     * Width of columns in the small images of this species.
    **/
    private int					smallImageColumnWidth;

    /**
     * Height of rows in the small images of this species.
    **/
    private int					smallImageRowHeight;

    /**
     * Width of columns in the medium images of this species.
    **/
    private int					mediumImageColumnWidth;

    /**
     * Height of rows in the medium images of this species.
    **/
    private int					mediumImageRowHeight;

    /**
     * Width of columns in the large images of this species.
    **/
    private int					largeImageColumnWidth;

    /**
     * Height of rows in the large images of this species.
    **/
    private int					largeImageRowHeight;

    /**
     * Width of columns in the xLarge images of this species.
    **/
    private int					xLargeImageColumnWidth;

    /**
     * Height of rows in the xLarge images of this species.
    **/
    private int					xLargeImageRowHeight;

    /**
     * Import context used when reading in world from a file, used
     * for both XML and non-XML files.  Null except when we're in
     * the midst of reading from a file.
    **/
    private ImportContext       importContext = null;

    /**
     * XML Parser for the BioLogica file being read in.
    **/
//    private com.sun.xml.parser.Parser      xmlParser = null;
    private       SAXParser      xmlParser = null;//dima

    /**
     * Importing species file from XML file
    **/
    private boolean             importingSpeciesFile = false;

    /**
     * Creates a new species given a world, ploidy number and name.<p>
     *
     * A species is initially created with no genes.  Genes can be added by creating
     * genes and specifying the species in the gene.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aPloidyNumber int - ploidy number, must be 1 or 2
     * @param		aName String - name of this organism, may not be null
     * @param		anImageColumnWidth int - width of image columns, must be >= 0
     * @param		anImageRowHeight int - height of image rows, must be >= 0
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Species(World aWorld, int aPloidyNumber, String aName)
    {
        // Test input arguments
        if (aWorld == null ||
            (!(aPloidyNumber == 1 || aPloidyNumber == 2 || aPloidyNumber == 3)) ||
            aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments ok - initialize instance variables
        ploidyNumber = aPloidyNumber;
        diploidType = DIPLOID_TYPE_XX_FEMALE_XY_MALE;
        name = new String(aName);
        description = null;
        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        traits = new Vector();
        genotypeToPhenotypeRules = new Vector();
        speciesImages = new Vector();
        lockedState = EngineObject.UNLOCKED;

        xxSmallImageColumnWidth = 10;
        xxSmallImageRowHeight = 10;
        xSmallImageColumnWidth = 10;
        xSmallImageRowHeight = 10;
        smallImageColumnWidth = 10;
        smallImageRowHeight = 10;
        mediumImageColumnWidth = 10;
        mediumImageRowHeight = 10;
        largeImageColumnWidth = 10;
        largeImageRowHeight = 10;
        xLargeImageColumnWidth = 10;
        xLargeImageRowHeight = 10;

        // Creation successful, so add this species to world
        world = aWorld;
        world.addSpecies(this);
    }

    /**
     * Creates a new species given a world and a species file.<p>
     *
     * @param		aWorld World - a world containing this species
     * @param		aFile File - a species file
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public Species(World aWorld, File aFile)
    {
        // Test input arguments
        if (aWorld == null ||
            aFile == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Input arguments ok - initialize instance variables
        world = aWorld;
        ploidyNumber = 2;
        diploidType = DIPLOID_TYPE_XX_FEMALE_XY_MALE;
        name = null;
        description = null;
        lockedState = EngineObject.UNLOCKED;
        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        traits = new Vector();
        genotypeToPhenotypeRules = new Vector();
        speciesImages = new Vector();

        xxSmallImageColumnWidth = 10;
        xxSmallImageRowHeight = 10;
        xSmallImageColumnWidth = 10;
        xSmallImageRowHeight = 10;
        smallImageColumnWidth = 10;
        smallImageRowHeight = 10;
        mediumImageColumnWidth = 10;
        mediumImageRowHeight = 10;
        largeImageColumnWidth = 10;
        largeImageRowHeight = 10;
        xLargeImageColumnWidth = 10;
        xLargeImageRowHeight = 10;

        // Open the file, crossing fingers :-)
        try
        {
            String s = aFile.getName();
            int length = s.length();
            if (length > 4 &&
                s.charAt(length-1) == 'l' &&
                s.charAt(length-2) == 'm' &&
                s.charAt(length-3) == 'x' &&
                s.charAt(length-4) == '.')
            {
                openXML(aFile);

                // Must add species to world, as not
                // done automatically for XML files
                world.addSpecies(this);
            }
            else
            {
                throw new IllegalArgumentException("File " + s + " is not an XML file.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException(EngineStrings.UNABLE_TO_OPEN_SPECIES_FILE + e);
        }
    }
    
    /**
     * Create a new species and start handling XML parsing events to set
     * the properties of this object.<p>
     *
     * @param      aWorld World - the enclosing world for this new species
     * @param      anElementName String - the element name
     * @param      anElementID int - the element id
     * @param      anXMLParser com.sun.xml.parser.Parser - the element parser
     * @param	   importContext ImportContext - import context for mapping ids from file to this world
     * @exception  IllegalArgumentException - input arguments illegal
    **/
    public Species(World aWorld,
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
        ploidyNumber = 2;
        name = null;
        description = null;
        lockedState = EngineObject.UNLOCKED;
        xxSmallImageColumnWidth = 10;
        xxSmallImageRowHeight = 10;
        xSmallImageColumnWidth = 10;
        xSmallImageRowHeight = 10;
        smallImageColumnWidth = 10;
        smallImageRowHeight = 10;
        mediumImageColumnWidth = 10;
        mediumImageRowHeight = 10;
        largeImageColumnWidth = 10;
        largeImageRowHeight = 10;
        xLargeImageColumnWidth = 10;
        xLargeImageRowHeight = 10;

        // Create vectors
        nonSexChromosomes = new Vector();
        sexChromosomes = new Vector();
        traits = new Vector();
        genotypeToPhenotypeRules = new Vector();
        speciesImages = new Vector();

        // Creation successful, so add this species to world
        world = aWorld;
        world.addSpecies(this);

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
            case Elements.DESCRIPTION_ELEMENT_ID:
            case Elements.PLOIDY_NUMBER_ELEMENT_ID:
            case Elements.DIPLOID_TYPE_ELEMENT_ID:
            case Elements.XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
            case Elements.XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
            case Elements.SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
            case Elements.MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
            case Elements.LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
            case Elements.XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
            case Elements.XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID:
            case Elements.XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID:
            case Elements.SMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID:
            case Elements.MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_ID:
            case Elements.LARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID:
            case Elements.XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID:
                xmlElementContext.newElement(anElementName,anElementID);
                break;

            case Elements.SPECIES_ELEMENT_ID:
                // If importing a species file, ok.  If not importing, error.
                if (!importingSpeciesFile)
                {
                    throw new IllegalArgumentException("Species " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
                }
                break;

            case Elements.GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_ID:
                new GenotypeToPhenotypeRule(this,anElementName,id,
                                            xmlElementContext.getXMLParser(),
                                            xmlElementContext.getImportContext());
                break;

            case Elements.SPECIES_CHROMOSOME_ELEMENT_ID:
                new SpeciesChromosome(this,anElementName,id,
                                      xmlElementContext.getXMLParser(),
                                      xmlElementContext.getImportContext());
                break;

            case Elements.SPECIES_IMAGE_ELEMENT_ID:
                new SpeciesImage(this,anElementName,id,
                                 xmlElementContext.getXMLParser(),
                                 xmlElementContext.getImportContext());
                break;

            case Elements.TRAIT_ELEMENT_ID:
                new Trait(this,anElementName,id,
                          xmlElementContext.getXMLParser(),
                          xmlElementContext.getImportContext());
                break;

            default:
                throw new IllegalArgumentException("Species " + EngineStrings.UNRECOGNIZED_XML_ELEMENT_ID + anElementID);
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

            case Elements.DESCRIPTION_ELEMENT_ID:
                description = xmlElementContext.getValueString();
                break;

            case Elements.PLOIDY_NUMBER_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                ploidyNumber = Integer.valueOf(valueString).intValue();
                break;

            case Elements.DIPLOID_TYPE_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                setDiploidTypeAsString(valueString);
                break;

            case Elements.XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                xxSmallImageColumnWidth = Integer.valueOf(valueString).intValue();
                break;

            case Elements.XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                xSmallImageColumnWidth = Integer.valueOf(valueString).intValue();
                break;

            case Elements.SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                smallImageColumnWidth = Integer.valueOf(valueString).intValue();
                break;

            case Elements.MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                mediumImageColumnWidth = Integer.valueOf(valueString).intValue();
                break;

            case Elements.LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                largeImageColumnWidth = Integer.valueOf(valueString).intValue();
                break;

            case Elements.XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                xLargeImageColumnWidth = Integer.valueOf(valueString).intValue();
                break;

            case Elements.XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                xxSmallImageRowHeight = Integer.valueOf(valueString).intValue();
                break;

            case Elements.XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                xSmallImageRowHeight = Integer.valueOf(valueString).intValue();
                break;

            case Elements.SMALL_IMAGE_ROW_HEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                smallImageRowHeight = Integer.valueOf(valueString).intValue();
                break;

            case Elements.MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                mediumImageRowHeight = Integer.valueOf(valueString).intValue();
                break;

            case Elements.LARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                largeImageRowHeight = Integer.valueOf(valueString).intValue();
                break;

            case Elements.XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_ID:
                valueString = xmlElementContext.getValueString();
                xLargeImageRowHeight = Integer.valueOf(valueString).intValue();
                break;

            case Elements.GENOTYPE_TO_PHENOTYPE_RULE_ELEMENT_ID:
                // Done with a genotype to phenotype rule, so reclaim document handler
                break;

            case Elements.SPECIES_CHROMOSOME_ELEMENT_ID:
                // Done with a species chromosome, so reclaim document handler
                break;

            case Elements.SPECIES_IMAGE_ELEMENT_ID:
                // Done with a species image, so reclaim document handler
                break;

            case Elements.TRAIT_ELEMENT_ID:
                // Done with a trait, so reclaim document handler
                break;

            case Elements.SPECIES_ELEMENT_ID:
                if (!importingSpeciesFile)
                {
                    // Done with this species, so pop up to enclosing world
                    world.endElement(anElementName);
                    reclaimDocumentHandler = false;
                }
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
     * Helper method for parseAlleleString - matches the string alleles to the
     * actual SpeciesAllele objects in the SpeciesChromosome object. It then
     * creates and new ChromosomeSpecification, adding it to the vector
     * of specifications.<p>
     *
     * @param	chromosomeSpecifications Vector - List of chromosome specifications, must not be null
     * @param	speciesChromosome SpeciesChromosome - Current chromosome specification, must not be null
     * @param	allelesHashtable Hashtable - Lookup table of specified alleles, must not be null
     * @param	prefix String - "a:" or "b:" determines which homolog, must not be null
    **/
    
    private void addSpecificAlleles(Vector chromosomeSpecifications, SpeciesChromosome speciesChromosome,
                                    Hashtable allelesHashtable, String prefix)
    {		
        // Contains the specification for this chromosome
        ChromosomeSpecification chromosomeSpecification;
        
        // List of alleles for this chromosome. Some will be specified
        // by the string and the rest will be randomly chosen.
        Vector allelesForThisChromosome = new Vector();
        
        // Get the genes from the species chromosome for matching
        Enumeration eGenes = speciesChromosome.getGenes();
        
        while (eGenes.hasMoreElements())
        {
            SpeciesAllele testSpeciesAllele = null;
            SpeciesAllele alleleToUse = null;
            Gene gene = (Gene) eGenes.nextElement();
            Enumeration eSpeciesAlleles = gene.getSpeciesAlleles();
            while (eSpeciesAlleles.hasMoreElements())
            {
                testSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                
                // The prefix will be something like "a:" or "b:" to distinguish
                // between homologs - also allows "A:" and "B:"
                String key = prefix + testSpeciesAllele.getTextSymbol();
                if (allelesHashtable.get(key) != null)
                {
                    alleleToUse = testSpeciesAllele;
                    allelesHashtable.remove(key);
                    break;
                }
                else
                {
                    key = prefix.toUpperCase() + testSpeciesAllele.getTextSymbol();
                    if (allelesHashtable.get(key) != null)
                    {
                        alleleToUse = testSpeciesAllele;
                        allelesHashtable.remove(key);
                        break;
                    }
                }
            }
            if (alleleToUse == null)
            {
                alleleToUse = randomlyChooseAllele(gene);
            }
            if (alleleToUse != null)
            {
                allelesForThisChromosome.addElement(alleleToUse);
            }
        }

        // Create chromosome specification given the alleles we've found or randomly chosen
        chromosomeSpecification = new ChromosomeSpecification(this, speciesChromosome, allelesForThisChromosome);
        chromosomeSpecifications.addElement(chromosomeSpecification);
    }

    /**
     * Parse an allele string to create a ChromosomeSpecification<p>
     *
     * The allele string contains sequences like "a:H,b:h,b:W" where the
     * "a" or "b" on the left side of the colon specifies which of the
     * chromosome pair is affected and the string on the right side of the colon
     * specifies the allele. Spaces are significant in allele names, so
     * there should be no spaces anywhere in the specification string except
     * for in the name, if necessary.<p>
     *
     * For sex chromosomes, the "a" and "b" on the left sides of the colon are
     * significant only when there are 2 X chromosomes.  Otherwise "a" must be
     * used for both X and Y chromosome alleles.  "b" will be ignored if there
     * is only one X and one Y chromosome.<p>
     *
     * Finally, when there are X and Y chromosomes, the chromosome that is created
     * first is always random and cannot be specified.<p>
     *
     * @param		sex int - specifies sex, overrides string if inconsistent
     * @param		allelesString String - string specification of the alleles
     * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    
    public Vector parseAlleleString(int sex, String allelesString)
    {
        if (((sex != Organism.MALE) && (sex != Organism.FEMALE) && (sex != Organism.NO_SEX)) ||
            allelesString == null)
        {
            throw new InternalEngineException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // This will be the vector of all ChomosomeSpecifications
        // determined by the alleles string parameter
        Vector chromosomeSpecifications = new Vector();
        
        // I guess I should break on both commas and spaces
        // when parsing the allele string
        StringTokenizer tokens = new StringTokenizer(allelesString, EngineStrings.COMMA);
        
        // This hashtable will yield easy lookup of the specified alleles.
        Hashtable allelesHashtable = new Hashtable();
        
        // Put all of the specified alleles in the hashtable
        while (tokens.hasMoreTokens())
        {
            String token = tokens.nextToken();
            allelesHashtable.put(token, token);
        }
        
        // Iterate through the non-sex chromosomes first.
        SpeciesChromosome speciesChromosome;
        Enumeration eSpeciesChromosomes = nonSexChromosomes.elements();
        while (eSpeciesChromosomes.hasMoreElements())
        {
            speciesChromosome = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
            for (int i = 0; i < 2; i++)
            {
                addSpecificAlleles(chromosomeSpecifications, speciesChromosome,
                                   allelesHashtable, i == 0 ? EngineStrings.A_COLON : EngineStrings.B_COLON);
            }
        }
        
        // Then go through the sex chromosomes
        SpeciesChromosome xSpeciesChromosome = null;
        SpeciesChromosome ySpeciesChromosome = null;
        boolean yFirst = Species.randomlyChooseBoolean();	// Is Y or X chromosome first?
        eSpeciesChromosomes = sexChromosomes.elements();
        while (eSpeciesChromosomes.hasMoreElements())
        {
            speciesChromosome = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
            if (speciesChromosome.getNumberType() == IChromosome.X_CHROMOSOME)
            {
                xSpeciesChromosome = speciesChromosome;
                ySpeciesChromosome = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
            }
            else
            {
                ySpeciesChromosome = speciesChromosome;
                xSpeciesChromosome = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
            }

            if (((sex == Organism.MALE) && (diploidType == DIPLOID_TYPE_XX_FEMALE_XY_MALE)) ||
                ((sex == Organism.FEMALE) && (diploidType == DIPLOID_TYPE_XY_FEMALE_XX_MALE)))
            {
                // Creating an XY or YX organism (notice that we use A_COLON in both X and Y cases)
                if (yFirst)
                {
                    addSpecificAlleles(chromosomeSpecifications, ySpeciesChromosome,
                                       allelesHashtable, EngineStrings.A_COLON);
                    addSpecificAlleles(chromosomeSpecifications, xSpeciesChromosome,
                                       allelesHashtable, EngineStrings.A_COLON);
                }
                else
                {
                    addSpecificAlleles(chromosomeSpecifications, xSpeciesChromosome,
                                       allelesHashtable, EngineStrings.A_COLON);
                    addSpecificAlleles(chromosomeSpecifications, ySpeciesChromosome,
                                       allelesHashtable, EngineStrings.A_COLON);
                }
            }
            else
            {
                // Creating an XX organism (use A_COLON and then B_COLON)
                addSpecificAlleles(chromosomeSpecifications, xSpeciesChromosome,
                                   allelesHashtable, EngineStrings.A_COLON);
                addSpecificAlleles(chromosomeSpecifications, xSpeciesChromosome,
                                   allelesHashtable, EngineStrings.B_COLON);
            }
        }
        
        if (allelesHashtable.size() > 0)
        {
            throw new IllegalArgumentException(EngineStrings.BAD_ALLELE_STRING_SPECIFICATION);
        }
        return chromosomeSpecifications;
    }

    /**
     * Delete this object, notifying parent objects and deleting any child objects.<p>
     *
     * When this method is called, a property change event is
     * generated for the property named EngineProp.DELETED.<p>
     *
     * @exception ObjectLockedException - object is locked and cannot be deleted
    **/
    public void delete()
    {
        delete(true);
    }
    
    public void delete(boolean notifyChange)
    {
        // Exception if this object is locked
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }

        // Avoid double deletions gracefully
        if (deleted == true)
        {
            return;
        }
        deleted = true;

        if (notifyChange)
        {
            // Notify listeners before deleting
            changes.firePropertyChange(EngineProp.DELETED,FALSE,TRUE);
        }

        // Delete instance variables, enabling garbage collection on them
        name = null;
        description = null;

        SpeciesChromosome aChromosome;
        Vector nonSexChromosomesClone = (Vector) nonSexChromosomes.clone();
        Enumeration eChromosomes = nonSexChromosomesClone.elements();
       // Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.delete(notifyChange);
        }
        nonSexChromosomes.removeAllElements();
        nonSexChromosomes = null;
        nonSexChromosomesClone = null;

        Vector sexChromosomesClone = (Vector) sexChromosomes.clone();
        eChromosomes = sexChromosomesClone.elements();
       // eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.delete(notifyChange);
        }
        sexChromosomes.removeAllElements();
        sexChromosomes = null;
        sexChromosomesClone = null;

        Trait aTrait;
        Vector traitsClone = (Vector) traits.clone();
        Enumeration eTraits = traitsClone.elements();
       // Enumeration eTraits = traits.elements();
        while (eTraits.hasMoreElements())
        {
            aTrait = (Trait) eTraits.nextElement();
            aTrait.delete(notifyChange);
        }
        traits.removeAllElements();
        traits = null;
        traitsClone = null;

        GenotypeToPhenotypeRule aRule;
        Vector genotypeToPhenotypeRulesClone = (Vector) genotypeToPhenotypeRules.clone();
        Enumeration eRules = genotypeToPhenotypeRulesClone.elements();
       // Enumeration eRules = genotypeToPhenotypeRules.elements();
        while (eRules.hasMoreElements())
        {
            aRule = (GenotypeToPhenotypeRule) eRules.nextElement();
            aRule.delete(notifyChange);
        }
        genotypeToPhenotypeRules.removeAllElements();
        genotypeToPhenotypeRules = null;
        genotypeToPhenotypeRulesClone = null;

        SpeciesImage aSpeciesImage;
        Vector speciesImagesClone = (Vector) speciesImages.clone();
        Enumeration eSpeciesImages = speciesImagesClone.elements();
        //Enumeration eSpeciesImages = speciesImages.elements();
        while (eSpeciesImages.hasMoreElements())
        {
            aSpeciesImage = (SpeciesImage) eSpeciesImages.nextElement();
            aSpeciesImage.delete(notifyChange);
        }
        speciesImages.removeAllElements();
        speciesImages = null;
        speciesImagesClone = null;

        world.removeSpecies(this);
        world = null;
        id = EngineObject.NULL_ID;
    }
    
    /**
     * Export this species to a species file.
     *
     * @param		aFile File - a species file
    **/
    public void exportTo(File aFile)
    {
        if (aFile == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Try to save to the file aFile
        FileOutputStream fileOutputStream = null;
        PrintWriter stream = null;
    
        try
        {
            // Create stream
            fileOutputStream = new FileOutputStream(aFile);
            stream = new PrintWriter(fileOutputStream);

            stream.println("<?xml version=\"1.0\" encoding=\"us-ascii\"?>");
            stream.println("");

            // Write this engine state to the stream as XML
            writeToStream(stream);
        }
        catch (IOException e1)
        {
            System.err.println(EngineStrings.CANNOT_CREATE_FILE_OUTPUT_STREAM_DUE_TO_EXCEPTION + e1);
        }
        catch (SecurityException e2)
        {
            System.err.println(EngineStrings.CANNOT_CREATE_FILE_OUTPUT_STREAM_DUE_TO_EXCEPTION + e2);
        }
        finally
        {
            try
            {
                if (fileOutputStream != null)
                {
                    fileOutputStream.close();
                }
            }
            catch (IOException e3)
            {
                System.err.println(EngineStrings.CANNOT_CLOSE_FILE_OUTPUT_STREAM_DUE_TO_EXCEPTION + e3);
            }
        }
    }

    /**
     * Private method to open the given XML file and retrieve its data into this species.<p>
     *
     * Normally this method is called only from the Species constructor that
     * takes a File argument.<p>
     *
     * @param		aFile File - the file to open
     * @exception 	IllegalArgumentException - aFile was null
    **/
    private void openXML(File aFile)
    {
        if (aFile == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        boolean succeeding = true;
        FileInputStream fileInputStream = null;
        DataInputStream stream = null;

        if (aFile.exists() == false)
        {
            System.err.println(EngineStrings.CANNOT_FIND_FILE + aFile.getAbsolutePath());
            succeeding = false;
        }
        else if (aFile.canRead() == false)
        {
            System.err.println(EngineStrings.CANNOT_READ_FILE + aFile.getAbsolutePath());
            succeeding = false;
        }

        if (succeeding == true)
        {
            try
            {
                // Create input source
//                InputSource inputSource = Resolver.createInputSource(aFile);
                  InputSource inputSource = new InputSource(new java.io.FileInputStream(aFile));//dima

                // Create parser
                if (xmlParser == null)
                {
//                    xmlParser = new com.sun.xml.parser.Parser();
                    xmlParser = new SAXParser();//dima
                }

                // Create import context to track id mappings
                importContext = new ImportContext();

                // Create xml element context to manage internal element parsing
                xmlElementContext = new ElementContext(this,xmlParser,importContext);

                // Set the first document handler to this object
                xmlParser.setDocumentHandler(this);

                // Start parsing
                importingSpeciesFile = true;
                xmlParser.parse(inputSource);
                importingSpeciesFile = false;
            }
            catch (Exception e1)
            {
                System.err.println(EngineStrings.ERROR_PARSING_XML_FILE + e1);
                if (e1 instanceof SAXParseException)
                {
                    SAXParseException spe = (SAXParseException) e1;
                    System.err.println("Line number = " + spe.getLineNumber());
                    System.err.println("Column number = " + spe.getColumnNumber());
                    System.err.println("Public ID = " + spe.getPublicId());
                    System.err.println("System ID = " + spe.getSystemId());
                }
                succeeding = false;
            }
            finally
            {
                importingSpeciesFile = false;
            }
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
        return EngineStrings.SPECIES_COLON + name;
    }

    /**
     * Randomly choose a boolean.<p>
     *
     * @return		boolean - true or false
    **/
    static boolean randomlyChooseBoolean()
    {
        int nextInt = random.nextInt();
        if (nextInt < 0)
        {
            return false;
        }

        return true;
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
        SpeciesChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.setAutomaticLocked(automaticLocked);
        }

        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.setAutomaticLocked(automaticLocked);
        }

        Trait aTrait;
        Enumeration eTraits = traits.elements();
        while (eTraits.hasMoreElements())
        {
            aTrait = (Trait) eTraits.nextElement();
            aTrait.setAutomaticLocked(automaticLocked);
        }

        GenotypeToPhenotypeRule aRule;
        Enumeration eRules = genotypeToPhenotypeRules.elements();
        while (eRules.hasMoreElements())
        {
            aRule = (GenotypeToPhenotypeRule) eRules.nextElement();
            aRule.setAutomaticLocked(automaticLocked);
        }

        SpeciesImage aSpeciesImage;
        Enumeration eSpeciesImages = speciesImages.elements();
        while (eSpeciesImages.hasMoreElements())
        {
            aSpeciesImage = (SpeciesImage) eSpeciesImages.nextElement();
            aSpeciesImage.setAutomaticLocked(automaticLocked);
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
        // Return if already locked
        if ((lockedState & EngineObject.MANUAL_LOCKED) == EngineObject.MANUAL_LOCKED)
        {
            return;
        }

        // Use EngineObject implementation for changing the state of this object
        super.setManualLocked(manualLocked);

        // Set manual locked state of children
        SpeciesChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.setManualLocked(manualLocked);
        }

        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.setManualLocked(manualLocked);
        }

        Trait aTrait;
        Enumeration eTraits = traits.elements();
        while (eTraits.hasMoreElements())
        {
            aTrait = (Trait) eTraits.nextElement();
            aTrait.setManualLocked(manualLocked);
        }

        GenotypeToPhenotypeRule aRule;
        Enumeration eRules = genotypeToPhenotypeRules.elements();
        while (eRules.hasMoreElements())
        {
            aRule = (GenotypeToPhenotypeRule) eRules.nextElement();
            aRule.setManualLocked(manualLocked);
        }

        SpeciesImage aSpeciesImage;
        Enumeration eSpeciesImages = speciesImages.elements();
        while (eSpeciesImages.hasMoreElements())
        {
            aSpeciesImage = (SpeciesImage) eSpeciesImages.nextElement();
            aSpeciesImage.setManualLocked(manualLocked);
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
        // Return if already locked
        if (lockedState == aLockedState)
        {
            return;
        }

        // Use EngineObject implementation for changing the locked state of this object
        super.setLockedState(aLockedState);

        // Set locked state of children
        SpeciesChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.setLockedState(lockedState);
        }

        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            aChromosome.setLockedState(lockedState);
        }

        Trait aTrait;
        Enumeration eTraits = traits.elements();
        while (eTraits.hasMoreElements())
        {
            aTrait = (Trait) eTraits.nextElement();
            aTrait.setLockedState(lockedState);
        }

        GenotypeToPhenotypeRule aRule;
        Enumeration eRules = genotypeToPhenotypeRules.elements();
        while (eRules.hasMoreElements())
        {
            aRule = (GenotypeToPhenotypeRule) eRules.nextElement();
            aRule.setLockedState(lockedState);
        }

        SpeciesImage aSpeciesImage;
        Enumeration eSpeciesImages = speciesImages.elements();
        while (eSpeciesImages.hasMoreElements())
        {
            aSpeciesImage = (SpeciesImage) eSpeciesImages.nextElement();
            aSpeciesImage.setLockedState(lockedState);
        }
    }

    /**
     * Returns ploidy number of this species, which may either be 1 (haploid),
     * 2 (diploid) or 3 (haploid and diploid - e.g. yeast).<p>
     *
     * @return		ploidyNumber - integer ploidy number of this organism (1, 2 or 3)
    **/
    public int getPloidyNumber()
    {
        return ploidyNumber;
    }

    /**
     * Set the ploidy number, which must be 1 or 2.<p>
     *
     * Once a species has chromosomes, its ploidy number may not be changed.<p>
     *
     * @param		newPloidyNumber int - new ploidy number (1 or 2)
     * @exception	InternalEngineException - ploidy number illegal or species ploidy cannot be changed
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setPloidyNumber(int newPloidyNumber)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Validate input arguments
        if (newPloidyNumber != 1 && newPloidyNumber != 2)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (newPloidyNumber == ploidyNumber)
        {
            // no change
            return;
        }

        if (nonSexChromosomes.size() > 0 || sexChromosomes.size() > 0)
        {
            throw new InternalEngineException(EngineStrings.CANNOT_CHANGE_PLOIDY_NUMBER_OF_SPECIES_WITH_CHROMOSOMES);
        }

        // Change ploidy number and notify listeners
        int oldPloidyNumber = ploidyNumber;
        ploidyNumber = newPloidyNumber;
        changes.firePropertyChange(EngineProp.PLOIDY_NUMBER,
                                  new Integer(oldPloidyNumber),
                                  new Integer(newPloidyNumber));
    }

    /**
    * Returns diploid type of this species, which may either be DIPLOID_TYPE_XX_FEMALE_XY_MALE,
    * DIPLOID_TYPE_XY_FEMALE_XX_MALE or DIPLOID_TYPE_NO_SEX_CHROMOSOMES.<p>
    *
    * @return		diploidType - integer indicating diploid type of this species
    **/
    public int getDiploidType()
    {
        return diploidType;
    }

    /**
     * Returns diploid type of this species, which may either be DIPLOID_TYPE_XX_FEMALE_XY_MALE,
     * DIPLOID_TYPE_XY_FEMALE_XX_MALE or DIPLOID_TYPE_NO_SEX_CHROMOSOMES.<p>
     *
     * @return		diploidType - String
    **/
    public final String getDiploidTypeAsString()
    {
        if (diploidType == DIPLOID_TYPE_XX_FEMALE_XY_MALE)
        {
            return DIPLOID_TYPE_XX_FEMALE_XY_MALE_STRING;
        }
        else if (diploidType == DIPLOID_TYPE_XY_FEMALE_XX_MALE)
        {
            return DIPLOID_TYPE_XY_FEMALE_XX_MALE_STRING;
        }
        else if (diploidType == DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
            return DIPLOID_TYPE_NO_SEX_CHROMOSOMES_STRING;
        }

        // Default to no sex chromosomes
        return DIPLOID_TYPE_NO_SEX_CHROMOSOMES_STRING;
    }

    /**
     * Set the diploid type of this species, which may either be DIPLOID_TYPE_XX_FEMALE_XY_MALE,
     * DIPLOID_TYPE_XY_FEMALE_XX_MALE or DIPLOID_TYPE_NO_SEX_CHROMOSOMES.<p>
     *
     * @param		aDiploidType int - new diploid type
     * @exception	InternalEngineException - diploid type illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setDiploidType(int aDiploidType)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Validate input arguments
        if (aDiploidType != DIPLOID_TYPE_XX_FEMALE_XY_MALE &&
            aDiploidType != DIPLOID_TYPE_XY_FEMALE_XX_MALE &&
            aDiploidType != DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Cannot change to DIPLOID_TYPE_NO_SEX_CHROMOSOMES if we have sex chromosomes
        if (aDiploidType == DIPLOID_TYPE_NO_SEX_CHROMOSOMES &&
            getNumberOfSexChromosomes() > 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if not a change
        if (aDiploidType == diploidType)
        {
            // no change
            return;
        }

        // Change diploid type and notify listeners
        int oldDiploidType = diploidType;
        diploidType = aDiploidType;
        changes.firePropertyChange(EngineProp.DIPLOID_TYPE,
                                  new Integer(oldDiploidType),
                                  new Integer(diploidType));
    }

    /**
     * Set the diploid type of this species, which may either be DIPLOID_TYPE_XX_FEMALE_XY_MALE,
     * DIPLOID_TYPE_XY_FEMALE_XX_MALE or DIPLOID_TYPE_NO_SEX_CHROMOSOMES.<p>
     *
     * @param		aDiploidTypeString String - new diploid type as a string
     * @exception	InternalEngineException - diploid type illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setDiploidTypeAsString(String aDiploidTypeString)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }
        else if (aDiploidTypeString == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Convert to integer diploid type
        int aDiploidType;
        if (aDiploidTypeString.equals(DIPLOID_TYPE_XX_FEMALE_XY_MALE_STRING))
        {
            aDiploidType = DIPLOID_TYPE_XX_FEMALE_XY_MALE;
        }
        else if (aDiploidTypeString.equals(DIPLOID_TYPE_XY_FEMALE_XX_MALE_STRING))
        {
            aDiploidType = DIPLOID_TYPE_XY_FEMALE_XX_MALE;
        }
        else if (aDiploidTypeString.equals(DIPLOID_TYPE_NO_SEX_CHROMOSOMES_STRING))
        {
            aDiploidType = DIPLOID_TYPE_NO_SEX_CHROMOSOMES;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Cannot change to DIPLOID_TYPE_NO_SEX_CHROMOSOMES if we have sex chromosomes
        if (aDiploidType == DIPLOID_TYPE_NO_SEX_CHROMOSOMES &&
            getNumberOfSexChromosomes() > 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if not a change
        if (aDiploidType == diploidType)
        {
            // no change
            return;
        }

        // Change diploid type and notify listeners
        int oldDiploidType = diploidType;
        diploidType = aDiploidType;
        changes.firePropertyChange(EngineProp.DIPLOID_TYPE,
                                  new Integer(oldDiploidType),
                                  new Integer(diploidType));
    }

    /**
     * Returns image column width of this species.
     *
     * @param		imageSize int - image size (e.g. SMALL_IMAGE_SIZE, etc.)
     * @return		integer value - the image column width of this species
    **/
    public int getImageColumnWidth(int imageSize)
    {
        int columnWidth = 0;

        switch (imageSize)
        {
            case SpeciesImage.XXSMALL_IMAGE_SIZE: columnWidth = xxSmallImageColumnWidth; break;
            case SpeciesImage.XSMALL_IMAGE_SIZE: columnWidth = xSmallImageColumnWidth; break;
            case SpeciesImage.SMALL_IMAGE_SIZE: columnWidth = smallImageColumnWidth; break;
            case SpeciesImage.MEDIUM_IMAGE_SIZE: columnWidth = mediumImageColumnWidth; break;
            case SpeciesImage.LARGE_IMAGE_SIZE: columnWidth = largeImageColumnWidth; break;
            case SpeciesImage.XLARGE_IMAGE_SIZE: columnWidth = xLargeImageColumnWidth; break;
            default:
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        return columnWidth;
    }

    /**
     * Set the image column width of a particular image size for this species.
     *
     * @param		imageSize - the image size being set (e.g. SpeciesImage.XSMALL_IMAGE_SIZE)
     * @param		anImageColumnWidth int - the new image column width of species, must be >= 0
     * @exception	InternalEngineException - diploid type illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setImageColumnWidth(int imageSize, int anImageColumnWidth)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Validate input arguments
        if (anImageColumnWidth < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Get old column width
        int oldImageColumnWidth = -1;
        switch (imageSize)
        {
            case SpeciesImage.XXSMALL_IMAGE_SIZE: oldImageColumnWidth = xxSmallImageColumnWidth; break;
            case SpeciesImage.XSMALL_IMAGE_SIZE: oldImageColumnWidth = xSmallImageColumnWidth; break;
            case SpeciesImage.SMALL_IMAGE_SIZE: oldImageColumnWidth = smallImageColumnWidth; break;
            case SpeciesImage.MEDIUM_IMAGE_SIZE: oldImageColumnWidth = mediumImageColumnWidth; break;
            case SpeciesImage.LARGE_IMAGE_SIZE: oldImageColumnWidth = largeImageColumnWidth; break;
            case SpeciesImage.XLARGE_IMAGE_SIZE: oldImageColumnWidth = xLargeImageColumnWidth; break;
            default:
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (anImageColumnWidth == oldImageColumnWidth)
        {
            // no change
            return;
        }

        // Change image column width and notify listeners
        String notification;
        switch (imageSize)
        {
            case SpeciesImage.XXSMALL_IMAGE_SIZE:
                xxSmallImageColumnWidth = anImageColumnWidth;
                notification = EngineProp.XXSMALL_IMAGE_COLUMN_WIDTH;
                break;

            case SpeciesImage.XSMALL_IMAGE_SIZE:
                xSmallImageColumnWidth = anImageColumnWidth;
                notification = EngineProp.XSMALL_IMAGE_COLUMN_WIDTH;
                break;

            case SpeciesImage.SMALL_IMAGE_SIZE:
                smallImageColumnWidth = anImageColumnWidth;
                notification = EngineProp.SMALL_IMAGE_COLUMN_WIDTH;
                break;

            case SpeciesImage.MEDIUM_IMAGE_SIZE:
                mediumImageColumnWidth = anImageColumnWidth;
                notification = EngineProp.MEDIUM_IMAGE_COLUMN_WIDTH;
                break;

            case SpeciesImage.LARGE_IMAGE_SIZE:
                largeImageColumnWidth = anImageColumnWidth;
                notification = EngineProp.LARGE_IMAGE_COLUMN_WIDTH;
                break;

            case SpeciesImage.XLARGE_IMAGE_SIZE:
                xLargeImageColumnWidth = anImageColumnWidth;
                notification = EngineProp.XLARGE_IMAGE_COLUMN_WIDTH;
                break;

            default:
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        changes.firePropertyChange(notification,
                                 new Integer(oldImageColumnWidth),
                                 new Integer(anImageColumnWidth));
    }

    /**
     * Returns image row height of this species.
     *
     * @param		imageSize int - image size (e.g. SMALL_IMAGE_SIZE, etc.)
     * @return		integer value - the image row height of this species
    **/
    public int getImageRowHeight(int imageSize)
    {
        int rowHeight = 0;

        switch (imageSize)
        {
            case SpeciesImage.XXSMALL_IMAGE_SIZE: rowHeight = xxSmallImageRowHeight; break;
            case SpeciesImage.XSMALL_IMAGE_SIZE: rowHeight = xSmallImageRowHeight; break;
            case SpeciesImage.SMALL_IMAGE_SIZE: rowHeight = smallImageRowHeight; break;
            case SpeciesImage.MEDIUM_IMAGE_SIZE: rowHeight = mediumImageRowHeight; break;
            case SpeciesImage.LARGE_IMAGE_SIZE: rowHeight = largeImageRowHeight; break;
            case SpeciesImage.XLARGE_IMAGE_SIZE: rowHeight = xLargeImageRowHeight; break;
            default:
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        return rowHeight;
    }

    /**
     * Set the image row height of a particular image size for this species.
     *
     * @param		imageSize - the image size being set (e.g. XSMALL_IMAGE_SIZE)
     * @param		anImageRowHeight int - the new image row height of species, must be >= 0
     * @exception	InternalEngineException - diploid type illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be deleted
    **/
    public void setImageRowHeight(int imageSize, int anImageRowHeight)
    {
        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Validate input arguments
        if (anImageRowHeight < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Get old row height
        int oldImageRowHeight = -1;
        switch (imageSize)
        {
            case SpeciesImage.XXSMALL_IMAGE_SIZE: oldImageRowHeight = xxSmallImageRowHeight; break;
            case SpeciesImage.XSMALL_IMAGE_SIZE: oldImageRowHeight = xSmallImageRowHeight; break;
            case SpeciesImage.SMALL_IMAGE_SIZE: oldImageRowHeight = smallImageRowHeight; break;
            case SpeciesImage.MEDIUM_IMAGE_SIZE: oldImageRowHeight = mediumImageRowHeight; break;
            case SpeciesImage.LARGE_IMAGE_SIZE: oldImageRowHeight = largeImageRowHeight; break;
            case SpeciesImage.XLARGE_IMAGE_SIZE: oldImageRowHeight = xLargeImageRowHeight; break;
            default:
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (anImageRowHeight == oldImageRowHeight)
        {
            // no change
            return;
        }

        // Change image column width and notify listeners
        String notification;
        switch (imageSize)
        {
            case SpeciesImage.XXSMALL_IMAGE_SIZE:
                xxSmallImageRowHeight = anImageRowHeight;
                notification = EngineProp.XXSMALL_IMAGE_ROW_HEIGHT;
                break;

            case SpeciesImage.XSMALL_IMAGE_SIZE:
                xSmallImageRowHeight = anImageRowHeight;
                notification = EngineProp.XSMALL_IMAGE_ROW_HEIGHT;
                break;

            case SpeciesImage.SMALL_IMAGE_SIZE:
                smallImageRowHeight = anImageRowHeight;
                notification = EngineProp.SMALL_IMAGE_ROW_HEIGHT;
                break;

            case SpeciesImage.MEDIUM_IMAGE_SIZE:
                mediumImageRowHeight = anImageRowHeight;
                notification = EngineProp.MEDIUM_IMAGE_ROW_HEIGHT;
                break;

            case SpeciesImage.LARGE_IMAGE_SIZE:
                largeImageRowHeight = anImageRowHeight;
                notification = EngineProp.LARGE_IMAGE_ROW_HEIGHT;
                break;

            case SpeciesImage.XLARGE_IMAGE_SIZE:
                xLargeImageRowHeight = anImageRowHeight;
                notification = EngineProp.XLARGE_IMAGE_ROW_HEIGHT;
                break;

            default:
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        changes.firePropertyChange(notification,
                                 new Integer(oldImageRowHeight),
                                 new Integer(anImageRowHeight));
    }

    /**
     * Return the world containing this species.  May not be null.<p>
     *
     * @return 		World - the world containing this species, may not be null
    **/
    public World getWorld()
    {
        return world;
    }

    /**
     * Return the name of this species.  May be null.<p>
     *
     * @return		String - name of this species, may not be null.
    **/
    public String getName()
    {
        return name;
    }

    /**
     * Set the name of this species.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.NAME.<p>
     *
     * @param		aName String - the new name of this species, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
    **/
    public void setName(String aName)
    {
        // Return immediately if aName equals the current name
        if (aName != null && name != null && aName.equals(name))
        {
            return;
        }

        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        if (aName == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        String oldName = name;
        name = new String(aName);

        // Notify any species images, as they use the species name in image filenames
        SpeciesImage aSpeciesImage;
        Enumeration eSpeciesImages = speciesImages.elements();
        while (eSpeciesImages.hasMoreElements())
        {
            aSpeciesImage = (SpeciesImage) eSpeciesImages.nextElement();
            aSpeciesImage.updateFilename();
        }


        // Notify listeners
        changes.firePropertyChange(EngineProp.NAME,oldName,name);
    }
    
    /**
     * Return the description of this species.  May be null.<p>
     *
     * @return		String - description of this species, may not be null.
    **/
    public String getDescription()
    {
        if (description == null)
        {
            return new String("");
        }
        return description;
    }

    /**
     * Set the description of this species.<p>
     *
     * When this method is successful, a property change event
     * is fired for the property named EngineProp.DESCRIPTION.<p>
     *
     * @param		aDescription String - the new description of this species, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
     * @exception 	ObjectLockedException - object is locked and cannot be modified
    **/
    public void setDescription(String aDescription)
    {
        // Return immediately if aDescription equals the current description
        if (aDescription != null && description != null && aDescription.equals(description))
        {
            return;
        }

        // Exception if this object is locked or deleted
        if (isLocked() == true)
        {
            throw new ObjectLockedException(EngineStrings.OBJECT_LOCKED);
        }
        else if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        if (aDescription == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        String oldDescription = description;
        description = new String(aDescription);

        // Notify listeners
        changes.firePropertyChange(EngineProp.DESCRIPTION,oldDescription,description);
    }

    /**
     * Returns an enumeration over the vector of non-sex chromosomes in this species.<p>
     *
     * The vector of non-sex chromosomes is cloned and the enumeration is created for the
     * clone, so it is safe to modify the vector of chromosomes (by creating new
     * ones, moving them around, etc.) while using this returned enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the non-sex chromosomes in this species
    **/
    public Enumeration getNonSexChromosomes()
    {
        // Clone non-sex chromosomes vector to make modifications safe during stepping
        Vector nonSexChromosomesClone = (Vector) nonSexChromosomes.clone();
        return nonSexChromosomesClone.elements();
    }

    /**
     * Return the number of non-sex chromosomes in this species.<p>
     *
     * @return		int - the number of non-sex chromosomes in this species
    **/
    public int getNumberOfNonSexChromosomes()
    {
        return nonSexChromosomes.size();
    }

    /**
     * Returns an enumeration over the vector of sex chromosomes in this species.<p>
     *
     * @return		Enumeration - an enumeration over the sex chromosomes in this species
    **/
    public Enumeration getSexChromosomes()
    {
        // Clone sex chromosomes vector to make modifications safe during stepping
        Vector sexChromosomesClone = (Vector) sexChromosomes.clone();
        return sexChromosomesClone.elements();
    }

    /**
     * Return the number of sex chromosomes in this species.<p>
     *
     * @return		int - the number of sex chromosomes in this species
    **/
    public int getNumberOfSexChromosomes()
    {
        return sexChromosomes.size();
    }

    /**
     * Return the number of sex and non-sex chromosomes in this species.<p>
     *
     * @return		int - the number of sex and non-sex chromosomes in this species
    **/
    public int getNumberOfChromosomes()
    {
        return nonSexChromosomes.size() + sexChromosomes.size();
    }

    /**
     * Adds a species chromosome (sex or non-sex) to the species.<p>
     *
     * Package protected because this is only called from the
     * SpeciesChromosome constructor.  Creating a SpeciesChromosome
     * automatically adds it to the species via this method.<p>
     *
     * @param		aChromosome SpeciesChromosome - a new species chromosome, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
    **/
    void addChromosome(SpeciesChromosome aChromosome)
    {
        // Exception if input chromosome null
        if (aChromosome == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // Add this species as a listener on the chromosome, so we'll hear
        // about any significant changes to the chromosome (e.g. numberType)
        aChromosome.addPropertyChangeListener(this);

        // Make change
        if (aChromosome.isSexChromosome() == true)
        {
            // Cannot add a sex chromosome to a species that has no sex chromosomes
            if (diploidType == DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
            {
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
            }
            sexChromosomes.addElement(aChromosome);
        }
        else
        {
            nonSexChromosomes.addElement(aChromosome);
        }

        // Notify listeners
        changes.firePropertyChange(EngineProp.SPECIES_CHROMOSOME_ADDED, null, aChromosome);
    }

    /**
     * Removes a species chromosome (sex or non-sex) from the species.<p>
     *
     * Package protected because this is only called from the SpeciesChromosome
     * delete method.  Deleting a SpeciesChromosome automatically removes it from
     * the species.<p>
     *
     * @param		aChromosome SpeciesChromosome - a species chromosome, may not be null
     * @return		boolean indicating whether or not the chromosome was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeChromosome(SpeciesChromosome aChromosome)
    {
        // Validate input arguments
        if (aChromosome == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - make change
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
            changes.firePropertyChange(EngineProp.SPECIES_CHROMOSOME_REMOVED, null, aChromosome);
        }

        return result;
    }

    /**
     * Returns the gene of this species with the given name.
     *
     * @param		aGeneName String - the name of the gene to find, if null then this method returns null
     * @return		Gene - the gene with the given name, null if gene not found or aGeneName is null
    **/
    public Gene getGene(String aGeneName)
    {
        // Return null if aGeneName is null
        if (aGeneName == null)
        {
            return null;
        }

        Gene aGene;
        Enumeration eGenes;

        // Try looking on non-sex chromosomes
        SpeciesChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            eGenes = aChromosome.getGenes();
            while (eGenes.hasMoreElements())
            {
                aGene = (Gene) eGenes.nextElement();
                if (aGene.getName().equals(aGeneName))
                {
                    return aGene;
                }
            }
        }

        // Try sex chromosomes
        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            eGenes = aChromosome.getGenes();
            while (eGenes.hasMoreElements())
            {
                aGene = (Gene) eGenes.nextElement();
                if (aGene.getName().equals(aGeneName))
                {
                    return aGene;
                }
            }
        }

        // Failed to find gene, so return null
        return null;
    }

    /**
     * Returns an enumeration over the vector of traits in this species.<p>
     *
     * The vector of traits is cloned and the enumeration is created for the
     * clone, so it is safe to modify the vector of traits (by creating new
     * ones, moving them around, etc.) while using this returned enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the traits in this species
    **/
    public Enumeration getTraits()
    {
        // Clone traits vector to make modifications safe during stepping
        Vector traitsClone = (Vector) traits.clone();
        return traitsClone.elements();
    }

    /**
     * Return the number of traits in this species.<p>
     *
     * @return		int - the number of traits in this species
    **/
    public int getNumberOfTraits()
    {
        return traits.size();
    }

    /**
     * Adds a trait to this species.<p>
     *
     * Package protected because this is only called from the Trait
     * constructor.  Creating a trait automatically adds it to the
     * species.<p>
     *
     * @param		aTrait Trait - a new trait, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
    **/
    void addTrait(Trait aTrait)
    {
        // Validate input argument
        if (aTrait == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // OK - add trait
        traits.addElement(aTrait);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.TRAIT_ADDED, null, aTrait);
    }

    /**
     * Removes a trait from this species.<p>
     *
     * Package protected because this is only called from the Trait
     * delete method.  Deleting a trait automatically removes it from
     * the trait.<p>
     *
     * @param		aTrait Trait - a trait, may not be null
     * @return		boolean indicating whether or not the trait was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeTrait(Trait aTrait)
    {
        if (aTrait == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - remove trait
        boolean result = traits.removeElement(aTrait);

        // Notify listeners if trait removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.TRAIT_REMOVED, null, aTrait);
        }

        return result;
    }

    /**
     * Move the given genotype to phenotype rule immediately after the second rule.<p>
     *
     * @param      aRuleToMove GenotypeToPhenotypeRule - the rule to move
     * @param      aRuleAfter GenotypeToPhenotypeRule - the rule to move the first rule immediately after
     * @exception  IllegalArgumentException - input rule null
    **/
    public void moveGenotypeToPhenotypeRuleAfter(GenotypeToPhenotypeRule aRuleToMove,
                                                 GenotypeToPhenotypeRule aRuleAfter)
    {
        // Exception if input rules null
        if (aRuleToMove == null || aRuleAfter == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Exception if this object is locked or deleted
        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // OK - make change
        int aRuleToMoveIndex = genotypeToPhenotypeRules.indexOf(aRuleToMove);
        int aRuleAfterIndex = genotypeToPhenotypeRules.indexOf(aRuleAfter);
        genotypeToPhenotypeRules.removeElement(aRuleToMove);
        genotypeToPhenotypeRules.insertElementAt(aRuleToMove,aRuleAfterIndex+1);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_MOVED,
                                   new Integer(aRuleToMoveIndex), aRuleToMove);
    }

    /**
     * Move the given genotype to phenotype rule up one position in the list of rules for this species.
     * Do nothing if the rule is already the first one.<p>
     *
     * @param      aRule GenotypeToPhenotypeRule - the rule to move up one
     * @exception  IllegalArgumentException - input rule null
    **/
    public void moveGenotypeToPhenotypeRuleUp(GenotypeToPhenotypeRule aRule)
    {
        // Exception if input rule null
        if (aRule == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Exception if this object is locked or deleted
        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // OK - make change
        int aRuleIndex = genotypeToPhenotypeRules.indexOf(aRule);
        if (aRuleIndex <= 0)
        {
            // rule not in this species or first in species, so do nothing
            return;
        }
        else
        {
            genotypeToPhenotypeRules.removeElement(aRule);
            genotypeToPhenotypeRules.insertElementAt(aRule,aRuleIndex-1);
        }
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_MOVED,
                                   new Integer(aRuleIndex), aRule);
    }

    /**
     * Move the given genotype to phenotype rule down one position in the list of rules for this species.
     * Do nothing if the rule is already the last one.<p>
     *
     * @param      aRule GenotypeToPhenotypeRule - the rule to move down one
     * @exception  IllegalArgumentException - input rule null
    **/
    public void moveGenotypeToPhenotypeRuleDown(GenotypeToPhenotypeRule aRule)
    {
        // Exception if input rule null
        if (aRule == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Exception if this object is locked or deleted
        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // OK - make change
        int aRuleIndex = genotypeToPhenotypeRules.indexOf(aRule);
        int lengthRules = genotypeToPhenotypeRules.size();
        if (aRuleIndex < 0 || aRuleIndex >= lengthRules-1)
        {
            // rule not in this species or last in species, so do nothing
            return;
        }
        else
        {
            genotypeToPhenotypeRules.removeElement(aRule);
            genotypeToPhenotypeRules.insertElementAt(aRule,aRuleIndex+1);
        }
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_MOVED,
                                   new Integer(aRuleIndex), aRule);
    }

    /**
     * Get index of the given genotype to phenotype rule in this species
     *
     * @param   aRule GenotypeToPhenotypeRule - the given genotype to phenotype rule
     * @return  int - index of the given rule in the rules of this species, zero-based
     * @exception  IllegalArgumentException - input rule null
    **/
    public int getIndexOfGenotypeToPhenotypeRule(GenotypeToPhenotypeRule aRule)
    {
        // Exception if input rule null
        if (aRule == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        return genotypeToPhenotypeRules.indexOf(aRule);
    }

    /**
     * Returns an enumeration over the vector of genotype to phenotype rules in this species.<p>
     *
     * The vector of rules is cloned and the enumeration is created for the
     * clone, so it is safe to modify the vector of rules (by creating new
     * ones, moving them around, etc.) while using this returned enumeration.<p>
     *
     * @return		Enumeration - an enumeration over the genotype to phenotype rules in this species
    **/
    public Enumeration getGenotypeToPhenotypeRules()
    {
        Vector genotypeToPhenotypeRulesClone = (Vector) genotypeToPhenotypeRules.clone();
        return genotypeToPhenotypeRulesClone.elements();
    }

    /**
     * Return the number of genotype to phenotype rules in this species.<p>
     *
     * @return		int - the number of genotype to phenotype rules in this species
    **/
    public int getNumberOfGenotypeToPhenotypeRules()
    {
        return genotypeToPhenotypeRules.size();
    }

    /**
     * Adds a genotype to phenotype rule to this species.<p>
     *
     * Package protected because this is only called from the GenotypeToPhenotypeRule
     * constructor.  Creating a genotype to phenotype rule automatically adds it to the
     * species.<p>
     *
     * @param		aRule GenotypeToPhenotypeRule - a new genotype to phenotype rule, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
    **/
    void addGenotypeToPhenotypeRule(GenotypeToPhenotypeRule aRule)
    {
        // Exception if input rule null
        if (aRule == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Exception if this object is locked or deleted
        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // OK - make change
        genotypeToPhenotypeRules.addElement(aRule);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_ADDED, null, aRule);
    }

    /**
     * Adds a genotype to phenotype rule to this species immediately after the given rule.<p>
     *
     * Package protected because this is only called from the GenotypeToPhenotypeRule
     * constructor.  Creating a genotype to phenotype rule automatically adds it to the
     * species.<p>
     *
     * @param		aRule GenotypeToPhenotypeRule - a new genotype to phenotype rule, may not be null
     * @param       afterRule GenotypeToPhenotypeRule - rule after which to add rule
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
    **/
    void addGenotypeToPhenotypeRuleAfter(GenotypeToPhenotypeRule aRule, GenotypeToPhenotypeRule afterRule)
    {
        // Exception if input rule null
        if (aRule == null || afterRule == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Exception if this object is locked or deleted
        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // OK - make change
        int afterRuleIndex = genotypeToPhenotypeRules.indexOf(afterRule);
        if (afterRuleIndex == -1)
        {
            genotypeToPhenotypeRules.addElement(aRule);
        }
        else
        {
            genotypeToPhenotypeRules.insertElementAt(aRule,afterRuleIndex+1);
        }
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_ADDED, null, aRule);
    }

    /**
     * Removes a genotype to phenotype rule from this species.<p>
     *
     * Package protected because this is only called from the GenotypeToPhenotypeRule
     * delete method.  Deleting a genotype to phenotype rule automatically removes it
     * from the species.<p>
     *
     * @param		aRule GenotypeToPhenotypeRule - a genotype to phenotype rule, may not be null
     * @return		boolean indicating whether or not the rule was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeGenotypeToPhenotypeRule(GenotypeToPhenotypeRule aRule)
    {
        // Validate input arguments
        if (aRule == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change
        boolean result = genotypeToPhenotypeRules.removeElement(aRule);

        // Notify listeners if rule removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_REMOVED, null, aRule);
        }

        return result;
    }
    /**
     * Randomly choose an allele of the given gene using the probability weights
     * of the alleles of that gene.
     *
     * @param		aGene Gene - the gene
     * @return		SpeciesAllele - the allele chosen, null if aGene is null
    **/
    static SpeciesAllele randomlyChooseAllele(Gene aGene)
    {
        if (aGene == null)
        {
            return null;
        }

        // Calculate the total weight of alleles of this gene
        int totalWeight = 0;
        SpeciesAllele aSpeciesAllele = null;
        Enumeration eSpeciesAlleles = aGene.getSpeciesAlleles();
        while (eSpeciesAlleles.hasMoreElements())
        {
            aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
            totalWeight += aSpeciesAllele.getWeight();
        }

        // Cannot choose an allele if there are none
        if (totalWeight > 0)
        {
            // Calculate which species allele by calculating a random number
            // between 0 and the totalWeight and then choosing the allele
            // which happens to be at that position in totalWeight.
            int alleleWeight = 0;
            int nextInt = random.nextInt();
            nextInt = (nextInt < 0) ? -1 * nextInt : nextInt;
            alleleWeight = nextInt % totalWeight;
    
            // Find the allele at that position in totalWeight
            int sumWeight = 0;
            int numberAlleles = aGene.getNumberOfSpeciesAlleles();
            SpeciesAllele [] alleles = new SpeciesAllele[numberAlleles];
            eSpeciesAlleles = aGene.getSpeciesAlleles();
            int i = 0;
            while (eSpeciesAlleles.hasMoreElements())
            {
                aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                alleles[i++] = aSpeciesAllele;
                int weight = aSpeciesAllele.getWeight();
                // If weight is 0, take this allele out of the running...
                if (weight == 0)
                {
                    continue;
                }
                sumWeight += weight;
                if (sumWeight >= alleleWeight)
                {
                    // Found allele, return it
                    return aSpeciesAllele;
                }
            }
            // If sumWeight is still zero here, it means that all the alleles were weighted
            // zero, so give each one equal weight.
            if (sumWeight == 0)
            {
                i = nextInt % numberAlleles;
                return alleles[i];
            }
        }

        // No alleles of this gene!
        return null;
    }
    
    protected void randomlySpecifyAlleles(SpeciesChromosome speciesChromosome, Vector speciesAlleles)
    {
		Enumeration eGenes = speciesChromosome.getGenes();
		while (eGenes.hasMoreElements())
		{
			Gene gene = (Gene) eGenes.nextElement();
			SpeciesAllele speciesAllele = randomlyChooseAllele(gene);
			if (speciesAllele != null)
				speciesAlleles.addElement(speciesAllele);
		}
    }
    
    protected int randomlySpecifyOrganismSex(Vector speciesAlleles)
    {
    	int sex = Organism.NO_SEX;
    	if (diploidType < DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
    	{
    		// Randomly decide the sex
    		sex = randomlyChooseBoolean() ? Organism.FEMALE : Organism.MALE;
    		// This determines whether or not the Y chromosome is ignored
    		boolean ignoreY = ((sex == Organism.FEMALE) &&
							   (diploidType == DIPLOID_TYPE_XX_FEMALE_XY_MALE)) ||
							  ((sex == Organism.MALE) &&
							   (diploidType == DIPLOID_TYPE_XY_FEMALE_XX_MALE));
    		
    		Enumeration eSpeciesChromosomes = sexChromosomes.elements();
    		while (eSpeciesChromosomes.hasMoreElements())
    		{
    			SpeciesChromosome speciesChromosome = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
    			if (ignoreY)
    			{
    				// If this is a Y chromosome then we skip this iteration
    				if (speciesChromosome.getNumberType() == IChromosome.Y_CHROMOSOME)
    					continue;
    				// If this is the X chromosome then let's get the first X allele
    				if (speciesChromosome.getNumberType() == IChromosome.X_CHROMOSOME)
    					randomlySpecifyAlleles(speciesChromosome, speciesAlleles);
    			}
    			// If we're ignoring the Y, then this should be the second X allele
    			// If we're not ignoring Y then this will generate both the X and Y alleles
    			randomlySpecifyAlleles(speciesChromosome, speciesAlleles);
    		}
    	}
    	return sex;
    }
    
    public void randomlySpecifyOrganism(Vector speciesAlleles, Vector characteristics)
    {
		Enumeration eSpeciesChromosomes = nonSexChromosomes.elements();
		while (eSpeciesChromosomes.hasMoreElements())
		{
			SpeciesChromosome speciesChromosome = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
			randomlySpecifyAlleles(speciesChromosome, speciesAlleles);
		}
       	int sex = randomlySpecifyOrganismSex(speciesAlleles);
    	runGenotypeToPhenotypeRules(sex, speciesAlleles, characteristics);
    }
    
    public SpeciesAllele findSpeciesAllele(String textSymbol)
    {
    	Vector [] chromoTypes = { nonSexChromosomes, sexChromosomes };
    	SpeciesAllele speciesAllele = null;
    	for (int i = 0; i < chromoTypes.length; i++)
    	{
			Enumeration eSpeciesChromosomes = chromoTypes[i].elements();
			while (eSpeciesChromosomes.hasMoreElements())
			{
				SpeciesChromosome speciesChromosome = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
				speciesAllele = findSpeciesAllele(textSymbol, speciesChromosome);
				if (speciesAllele != null)
					break;
			}
    	}
    	return speciesAllele;
    }
    
    public SpeciesAllele findSpeciesAllele(String textSymbol, SpeciesChromosome speciesChromosome)
    {
        Enumeration eGenes = speciesChromosome.getGenes();
        while (eGenes.hasMoreElements())
        {
            SpeciesAllele testSpeciesAllele = null;
            SpeciesAllele alleleToUse = null;
            Gene gene = (Gene) eGenes.nextElement();
            Enumeration eSpeciesAlleles = gene.getSpeciesAlleles();
            while (eSpeciesAlleles.hasMoreElements())
            {
                testSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                
                if (testSpeciesAllele.getTextSymbol().equals(textSymbol))
                {
                    alleleToUse = testSpeciesAllele;
                    return alleleToUse;
                }
            }
        }
        return null;
    }
    
    public void runGenotypeToPhenotypeRules(int sex, Vector speciesAlleles, Vector characteristics)
    {
        // Loop through species' rules gathering characteristics
        // using the species alleles gathered above.
        GenotypeToPhenotypeRule aRule;
        Enumeration eSpeciesGenotypeToPhenotypeRules = getGenotypeToPhenotypeRules();
        Vector traits = new Vector();
        while (eSpeciesGenotypeToPhenotypeRules.hasMoreElements())
        {
            aRule = (GenotypeToPhenotypeRule) eSpeciesGenotypeToPhenotypeRules.nextElement();

            // Test rule with given speciesAlleles, putting any resulting characteristics
            // on the given characteristics vector and also putting any traits satisfied
            // on the traits vector, avoiding multiple characteristics for one trait.
            aRule.test(sex,speciesAlleles,characteristics,traits);
        }
    }
    
    public int randomlySpecifyOffspring(Organism aParentOne, Organism aParentTwo, Vector characteristics)
    {
    	Vector speciesAlleles = new Vector();
    	int sex = Organism.NO_SEX;
    	
        // For each chromosome, get the 2 chromosomes of that number from the
        // father and the 2 chromosomes of that number from the mother.
        // Then randomly choose one chromosome from the mother and one from
        // the father.  Use each of those 2 chromosomes as blueprints from
        // the chromosomes of this child organism.

        // Non-sex chromosomes
        Enumeration eParentOneChromosomes = aParentOne.getNonSexChromosomes();
        Enumeration eParentTwoChromosomes = aParentTwo.getNonSexChromosomes();
        
      	int i = 0;
        while (eParentOneChromosomes.hasMoreElements() &&
               eParentTwoChromosomes.hasMoreElements())
        {
            OrganismChromosome ocOneParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            OrganismChromosome ocTwoParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
			
            OrganismChromosome ocParentOne = Organism.randomlyChooseChromosome(ocOneParentOne,ocTwoParentOne);
            Enumeration eAlleles = ocParentOne.getOrganismAlleles();
            while (eAlleles.hasMoreElements())
            {
            	OrganismAllele allele = (OrganismAllele) eAlleles.nextElement();
            	speciesAlleles.addElement(allele.getSpeciesAllele());
            }           

            OrganismChromosome ocOneParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            OrganismChromosome ocTwoParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();

            OrganismChromosome ocParentTwo = Organism.randomlyChooseChromosome(ocOneParentTwo,ocTwoParentTwo);
            eAlleles = ocParentTwo.getOrganismAlleles();
            while (eAlleles.hasMoreElements())
            {
            	OrganismAllele allele = (OrganismAllele) eAlleles.nextElement();
            	speciesAlleles.addElement(allele.getSpeciesAllele());
            }           
            i = i+1;
        }

        // Sex chromosomes
        if (getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
        	
            eParentOneChromosomes = aParentOne.getSexChromosomes();
            eParentTwoChromosomes = aParentTwo.getSexChromosomes();
           
    
            OrganismChromosome ocOneParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();
            OrganismChromosome ocTwoParentOne = (OrganismChromosome) eParentOneChromosomes.nextElement();       
	        
            OrganismChromosome ocParentOne = Organism.randomlyChooseChromosome(ocOneParentOne,ocTwoParentOne);
            Enumeration eAlleles = ocParentOne.getOrganismAlleles();
            while (eAlleles.hasMoreElements())
            {
            	OrganismAllele allele = (OrganismAllele) eAlleles.nextElement();
            	speciesAlleles.addElement(allele.getSpeciesAllele());
            }           
    
            OrganismChromosome ocOneParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
            OrganismChromosome ocTwoParentTwo = (OrganismChromosome) eParentTwoChromosomes.nextElement();
	        
	        OrganismChromosome ocParentTwo = Organism.randomlyChooseChromosome(ocOneParentTwo,ocTwoParentTwo);
            eAlleles = ocParentTwo.getOrganismAlleles();
            while (eAlleles.hasMoreElements())
            {
            	OrganismAllele allele = (OrganismAllele) eAlleles.nextElement();
            	speciesAlleles.addElement(allele.getSpeciesAllele());
            }           
    
            // Determine sex by looking at type of sex chromosomes and diploid type of species
            int numberTypeOne = ocParentOne.getNumberType();
            int numberTypeTwo = ocParentTwo.getNumberType();
    
            int diploidType = getDiploidType();
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
        
        runGenotypeToPhenotypeRules(sex, speciesAlleles, characteristics);
        return sex;
    }

    /**
     * Returns an enumeration over the vector of species images in this species.<p>
     *
     * @return		Enumeration - an enumeration over the species images in this species
    **/
    public Enumeration getSpeciesImages()
    {
        // Clone species images vector to make modifications safe during stepping
        Vector speciesImagesClone = (Vector) speciesImages.clone();
        return speciesImagesClone.elements();
    }

    /**
     * Get the number of species images for this species.<p>
     *
     * @return		int - the number of species images
    **/
    public int getNumberOfSpeciesImages()
    {
        return speciesImages.size();
    }

    /**
     * Adds a SpeciesImage to this species.<p>
     *
     * Package protected because this is only called from the SpeciesImage
     * constructor.  Creating a SpeciesImage automatically adds it to the
     * species.<p>
     *
     * @param		aSpeciesImage SpeciesImage - a new SpeciesImage, may not be null
     * @exception	IllegalArgumentException - input argument illegal
     * @exception 	ObjectDeletedException - object is deleted and cannot be modified
    **/
    void addSpeciesImage(SpeciesImage aSpeciesImage)
    {
        // Validate input argument
        if (aSpeciesImage == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Exception if this object is deleted
        if (deleted == true)
        {
            throw new ObjectDeletedException(EngineStrings.OBJECT_DELETED);
        }

        // OK - add species image
        speciesImages.addElement(aSpeciesImage);
        
        // Notify listeners
        changes.firePropertyChange(EngineProp.SPECIES_IMAGE_ADDED, null, aSpeciesImage);
    }

    /**
     * Removes a SpeciesImage from this species.<p>
     *
     * Package protected because this is only called from the SpeciesImage
     * delete method.  Deleting a SpeciesImage automatically removes it from
     * the species.<p>
     *
     * @param		aSpeciesImage SpeciesImage - a SpeciesImage, may not be null
     * @return		boolean indicating whether or not the SpeciesImage was found and removed
     * @exception	IllegalArgumentException - input argument illegal
    **/
    boolean removeSpeciesImage(SpeciesImage aSpeciesImage)
    {
        if (aSpeciesImage == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // OK - remove SpeciesImage
        boolean result = speciesImages.removeElement(aSpeciesImage);

        // Notify listeners if SpeciesImage removed
        if (result == true)
        {
            changes.firePropertyChange(EngineProp.SPECIES_IMAGE_REMOVED, null, aSpeciesImage);
        }

        return result;
    }

    /**
     * Return the number of genes in this species.<p>
     *
     * This is a convenience routine, as the genes are actually not stored
     * directly in the species, but instead off of their chromosomes.  So
     * all this method does is walk through the chromosomes adding up their
     * genes to arrive at a total count.<p>
     *
     * @return		int - the number of genes in this species
    **/
    public int getNumberOfGenes()
    {
        int numberOfGenes = 0;

        SpeciesChromosome aChromosome;
        Enumeration eChromosomes;

        // add genes on non-sex chromosomes
        eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            numberOfGenes = numberOfGenes + aChromosome.getNumberOfGenes();
        }

        // add genes on sex chromosomes
        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            numberOfGenes = numberOfGenes + aChromosome.getNumberOfGenes();
        }

        return numberOfGenes;
    }

    /**
     * Returns a vector of the Characteristics in this species.<p>
     *
     * This is a convenience routine, as anyone could also create this
     * vector themselves by getting all the traits in this species and
     * then asking each trait for its characteristics, etc.<p>
     *
     * @return		Enumeration - an enumeration over the traits in this species
    **/
    public Vector getCharacteristicsVector()
    {
        Vector characteristicsVector = new Vector();

        Characteristic aCharacteristic;
        Enumeration eCharacteristics;
        Trait aTrait;
        Enumeration eTraits = traits.elements();
        while (eTraits.hasMoreElements())
        {
            aTrait = (Trait) eTraits.nextElement();
            eCharacteristics = aTrait.getCharacteristics();
            while (eCharacteristics.hasMoreElements())
            {
                aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                characteristicsVector.addElement(aCharacteristic);
            }
        }

        return characteristicsVector;
    }

    /**
     * Returns a vector of the SpeciesAlleles in this species.<p>
     *
     * This is a convenience routine, as anyone could also create this
     * vector themselves by getting all the Chromosomes, Genes and then
     * Alleles in this species, etc.<p>
     *
     * @return		Enumeration - an enumeration over the species alleles in this species
    **/
    public Vector getSpeciesAllelesVector()
    {
        Vector speciesAllelesVector = new Vector();

        SpeciesAllele aSpeciesAllele;
        Enumeration eSpeciesAlleles;

        Gene aGene;
        Enumeration eGenes;
        
        // Non-sex chromosomes
        SpeciesChromosome aChromosome;
        Enumeration eChromosomes = nonSexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            eGenes = aChromosome.getGenes();
            while (eGenes.hasMoreElements())
            {
                aGene = (Gene) eGenes.nextElement();
                eSpeciesAlleles = aGene.getSpeciesAlleles();
                while (eSpeciesAlleles.hasMoreElements())
                {
                    aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                    speciesAllelesVector.addElement(aSpeciesAllele);
                }
            }
        }

        // Sex chromosomes
        eChromosomes = sexChromosomes.elements();
        while (eChromosomes.hasMoreElements())
        {
            aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
            eGenes = aChromosome.getGenes();
            while (eGenes.hasMoreElements())
            {
                aGene = (Gene) eGenes.nextElement();
                eSpeciesAlleles = aGene.getSpeciesAlleles();
                while (eSpeciesAlleles.hasMoreElements())
                {
                    aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                    speciesAllelesVector.addElement(aSpeciesAllele);
                }
            }
        }

        return speciesAllelesVector;
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
        stream.println("<" + Elements.SPECIES_ELEMENT_NAME + ">");

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

        // Description
        if (description == null)
        {
            stream.println("<" + Elements.DESCRIPTION_ELEMENT_NAME + ">" + "null" +
                              "</" + Elements.DESCRIPTION_ELEMENT_NAME + ">");
        }
        else
        {
            stream.println("<" + Elements.DESCRIPTION_ELEMENT_NAME + ">" + name +
                              "</" + Elements.DESCRIPTION_ELEMENT_NAME + ">");
        }

        // Ploidy Number
        stream.println("<" + Elements.PLOIDY_NUMBER_ELEMENT_NAME + ">" + ploidyNumber +
                          "</" + Elements.PLOIDY_NUMBER_ELEMENT_NAME + ">");

        // Diploid type
        stream.println("<" + Elements.DIPLOID_TYPE_ELEMENT_NAME + ">" +
                          getDiploidTypeAsString() +
                          "</" + Elements.DIPLOID_TYPE_ELEMENT_NAME + ">");

        // Image column widths
        stream.println("<" + Elements.XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">" + xxSmallImageColumnWidth +
                          "</" + Elements.XXSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">");
        stream.println("<" + Elements.XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">" + xSmallImageColumnWidth +
                          "</" + Elements.XSMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">");
        stream.println("<" + Elements.SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">" + smallImageColumnWidth +
                          "</" + Elements.SMALL_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">");
        stream.println("<" + Elements.MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">" + mediumImageColumnWidth +
                          "</" + Elements.MEDIUM_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">");
        stream.println("<" + Elements.LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">" + largeImageColumnWidth +
                          "</" + Elements.LARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">");
        stream.println("<" + Elements.XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">" + xLargeImageColumnWidth +
                          "</" + Elements.XLARGE_IMAGE_COLUMN_WIDTH_ELEMENT_NAME + ">");

        // Image row heights
        stream.println("<" + Elements.XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">" + xxSmallImageRowHeight +
                          "</" + Elements.XXSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">");
        stream.println("<" + Elements.XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">" + xSmallImageRowHeight +
                          "</" + Elements.XSMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">");
        stream.println("<" + Elements.SMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">" + smallImageRowHeight +
                          "</" + Elements.SMALL_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">");
        stream.println("<" + Elements.MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">" + mediumImageRowHeight +
                          "</" + Elements.MEDIUM_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">");
        stream.println("<" + Elements.LARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">" + largeImageRowHeight +
                          "</" + Elements.LARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">");
        stream.println("<" + Elements.XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">" + xLargeImageRowHeight +
                          "</" + Elements.XLARGE_IMAGE_ROW_HEIGHT_ELEMENT_NAME + ">");

        // Locked state
        stream.println("<" + Elements.LOCKED_STATE_ELEMENT_NAME + ">" +
                          getLockedStateAsString() +
                          "</" + Elements.LOCKED_STATE_ELEMENT_NAME + ">");

        // Recursively write some of the children of species
        {
            // Species chromosomes
            SpeciesChromosome aChromosome;
            Enumeration eChromosomes = nonSexChromosomes.elements();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
                aChromosome.writeToStream(stream);
            }

            eChromosomes = sexChromosomes.elements();
            while (eChromosomes.hasMoreElements())
            {
                aChromosome = (SpeciesChromosome) eChromosomes.nextElement();
                aChromosome.writeToStream(stream);
            }
        }

        {
            Trait aTrait;
            Enumeration eTraits = traits.elements();
            while (eTraits.hasMoreElements())
            {
                aTrait = (Trait) eTraits.nextElement();
                aTrait.writeToStream(stream);
            }
        }

        {
            GenotypeToPhenotypeRule aRule;
            Enumeration eRules = genotypeToPhenotypeRules.elements();
            while (eRules.hasMoreElements())
            {
                aRule = (GenotypeToPhenotypeRule) eRules.nextElement();
                aRule.writeToStream(stream);
            }
        }

        {
            SpeciesImage aSpeciesImage;
            Enumeration eSpeciesImages = speciesImages.elements();
            while (eSpeciesImages.hasMoreElements())
            {
                aSpeciesImage = (SpeciesImage) eSpeciesImages.nextElement();
                aSpeciesImage.writeToStream(stream);
            }
        }

        // End object
        stream.println("</" + Elements.SPECIES_ELEMENT_NAME + ">");
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.NUMBER_TYPE))
        {
            // A chromosome has changed its numberType, so we may need
            // to move it from one list to another.

            Object object = event.getSource();

            if (object instanceof SpeciesChromosome)
            {
                SpeciesChromosome sc = (SpeciesChromosome) object;

                int oldNumberType = ((Integer)event.getOldValue()).intValue();
                int newNumberType = ((Integer)event.getNewValue()).intValue();
    
                // If the chromosome has changed to/from an autosome
                // to/from a sex chromosome, move it from one list to
                // the other.
                if ((newNumberType == IChromosome.X_CHROMOSOME ||
                     newNumberType == IChromosome.Y_CHROMOSOME) &&
                    (oldNumberType != IChromosome.X_CHROMOSOME &&
                     oldNumberType != IChromosome.Y_CHROMOSOME))
                {
                    // Move chromosome from non-sex chromosome list
                    // to sex chromosome list
                    if (nonSexChromosomes.removeElement(sc))
                    {
                        if (diploidType == DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
                        {
                            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
                        }
                        sexChromosomes.addElement(sc);
                    }
                }
                else if ((oldNumberType == IChromosome.X_CHROMOSOME ||
                          oldNumberType == IChromosome.Y_CHROMOSOME) &&
                         (newNumberType != IChromosome.X_CHROMOSOME &&
                          newNumberType != IChromosome.Y_CHROMOSOME))
                {
                    // Move chromosome from sex chromosome list
                    // to non-sex chromosome list
                    if (sexChromosomes.removeElement(sc))
                    {
                        nonSexChromosomes.addElement(sc);
                    }
                }
            }
        }
    }
}
