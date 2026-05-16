//
// Class : ObjectPropertiesView - the view showing the properties of the selected object
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.3 $
// $Date: 2001/08/16 21:16:41 $
// $Author: bdias $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

/**
 * The object properties view of BioLogica.  This view will show the properties
 * of any type of BioLogica engine object.  The properties of the object will
 * be editable if the object is not locked.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.OBJECT - the shown organism has changed from one organism to another
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#OBJECT
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.3 $ $Date: 2001/08/16 21:16:41 $
 * @author 		$Author: bdias $
**/
public final class ObjectPropertiesView
extends UIView
implements ImageObserver, MouseListener, MouseMotionListener,
    PropertyChangeListener, ItemListener, ActionListener, FocusListener
{
    // Image widths and heights, etc.  Determined by image used to draw chromosomes.
    static public final int CHROMOSOME_IMAGE_WIDTH = 23;
    static public final int CHROMOSOME_IMAGE_HEIGHT = 95;
    static public final int X_YELLOW_CHROMOSOME = 0;
    static public final int X_BLUE_CHROMOSOME = 23;

    // Commands - must be unique, 3 characters
    static private final String cmdCreateSpeciesAllele		= "CSA";
    static private final String cmdCreateSpeciesChromosome	= "CSC";
    static private final String cmdCreateGene				= "CSG";
    static private final String cmdCreateEnvironment		= "CSE";
    static private final String cmdCreateFemaleOrganism		= "CSF";
    static private final String cmdCreateCharacteristic		= "CSH";
    static private final String cmdCreateMaleOrganism		= "CSM";
    static private final String cmdCreateTerrain			= "CSN";
    static private final String cmdCreateRule				= "CSR";
    static private final String cmdCreateSpecies			= "CSS";
    static private final String cmdCreateTrait				= "CST";
    static private final String cmdCreateSpeciesImage		= "CSV";
    static private final String cmdCreateSpeciesImageColumn	= "CSW";
    static private final String cmdCreateSpeciesImageRow	= "CSX";

    static private final String cmdChangeTerrainColor				= "CCA";
    static private final String cmdChangePedigreeSymbolFirstColor	= "CCB";
    static private final String cmdChangePedigreeSymbolSecondColor	= "CCC";
    static private final String cmdChangeHotspotColor				= "CCD";

    static private final String cmdHaploid					= "SHP";
    static private final String cmdDiploidXXFemaleXYMale	= "SDA";
    static private final String cmdDiploidXYFemaleXXMale	= "SDB";
    static private final String cmdDiploidNoSexChromosomes	= "SDC";

    static private final String cmdPedigreeSymbolSolid			= "SSA";
    static private final String cmdPedigreeSymbolForwardSlash	= "STB";

    static private final String cmdBottomStrand				= "CBS";
    static private final String cmdTopStrand				= "CTS";

    static private final String cmdImageTypeNormal			= "ITN";
    static private final String cmdImageTypeHotspot			= "ITH";
    static private final String cmdImageTypeScope			= "ITS";
    static private final String cmdImageTypeInvisible		= "ITI";

    static private final String cmdApply					= "CAP";

    static private final String cmdChromosomeImageNumber[] = {"CI0","CI1","CI2","CI3","CI4","CI5","CI6","CI7"};

    static private final String cmdMoveUp                   = "CMU";
    static private final String cmdMoveDown                 = "CMD";

    /**
     * Possible painting modes for this view
    **/
    static private final int PAINTING_NOTHING						= 0;
    static private final int PAINTING_ENGINE						= 1;
    static private final int PAINTING_WORLD							= 2;
    static private final int PAINTING_SPECIES						= 3;
    static private final int PAINTING_SPECIES_CHROMOSOME			= 4;
    static private final int PAINTING_GENE							= 5;
    static private final int PAINTING_SPECIES_ALLELE				= 6;
    static private final int PAINTING_TRAIT							= 7;
    static private final int PAINTING_CHARACTERISTIC				= 8;
    static private final int PAINTING_GENOTYPE_TO_PHENOTYPE_RULE	= 9;
    static private final int PAINTING_ENVIRONMENT					= 10;
    static private final int PAINTING_ORGANISM						= 11;
    static private final int PAINTING_TERRAIN						= 12;
    static private final int PAINTING_SPECIES_IMAGE					= 13;
    static private final int PAINTING_SPECIES_IMAGE_COLUMN			= 14;
    static private final int PAINTING_SPECIES_IMAGE_ROW				= 15;
    static private final int PAINTING_ORGANISM_CHROMOSOME_PAIR		= 16;
    static private final int PAINTING_ORGANISM_ALLELE_PAIR			= 17;

    /**
     * Number of characteristics per species image row or column
    **/
    static private final int NUMBER_CHARACTERISTICS_PER_COLUMN_OR_ROW	= 3;

    /**
     * Number of species allele per genotype to phenotype rule
    **/
    static private final int NUMBER_SPECIES_ALLELES_PER_RULE			= 4;

    /**
     * Have images been loaded?
    **/
    static private boolean imagesLoaded = false;

    /**
     * Chromosome image
    **/
    static private Image chromosomeImage = null;

    /**
     * Preferred width of this view.
    **/
    private int preferredWidth = 2000;

    /**
     * Preferred height of this view.
    **/
    private int preferredHeight = 2000;

    /**
     * Text area for name
    **/
    private JTextField nameTextField = null;

    /**
     * Text area for description
    **/
    private JTextArea descriptionTextArea = null;

    /**
     * Text area for numberType of chromosome
    **/
    private JTextField numberTypeTextField = null;

    /**
     * Text area for length
    **/
    private JTextField lengthTextField = null;

    /**
     * Text field for gene length
    **/
    private JTextField geneLengthTextField = null;

    /**
     * Text area for start position
    **/
    private JTextField startTextField = null;

    /**
     * Text area for weight / probability of a particular allele
    **/
    private JTextField weightTextField = null;

    /**
     * Checkbox for chromosome visibility
    **/
    private JCheckBox visibleChromosomeCheckBox = null;

    /**
     * Checkbox for gene visibility
    **/
    private JCheckBox visibleGeneCheckBox = null;

    /**
     * Gene bottom strand radio button
    **/
    private JRadioButton bottomStrandGeneRadioButton = null;

    /**
     * Gene top strand radio button
    **/
    private JRadioButton topStrandGeneRadioButton = null;

    /**
     * Checkbox for organism visibility
    **/
    private JCheckBox visibleOrganismCheckBox = null;

    /**
     * Checkbox for organism alleles visibility
    **/
    private JCheckBox visibleOrganismAllelesCheckBox = null;

    /**
     * Checkbox for organism DNA visibility
    **/
    private JCheckBox visibleOrganismDNACheckBox = null;

    /**
     * Checkbox for organism alleles alterability
    **/
    private JCheckBox alterableOrganismAllelesCheckBox = null;

    /**
     * Checkbox for organism DNA alterability
    **/
    private JCheckBox alterableOrganismDNACheckBox = null;

    /**
     * Checkbox for species allele's mutation allele setting
    **/
    private JCheckBox mutationAlleleCheckBox = null;

    /**
     * Checkbox for species allele's visible setting
    **/
    private JCheckBox visibleAlleleCheckBox = null;

    /**
     * Text area for allele base values
    **/
    private JTextArea alleleBaseValuesTextArea = null;

    /**
     * Scroll pane for allele base values text area
    **/
    private JScrollPane alleleBaseValuesScrollPane = null;

    /**
     * Checkbox for showing a trait as text in the organism view
    **/
    private JCheckBox showTraitAsTextCheckBox = null;

    /**
     * Checkbox for showing a characteristic is fatal
    **/
    private JCheckBox fatalCharacteristicCheckBox = null;

    /**
     * Text area for width of environment
    **/
    private JTextField widthTextField = null;

    /**
     * Text area for height of environment
    **/
    private JTextField heightTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the width of columns in the xxsmall image of a species image
     *	- the x location of hotspots in xxsmall species image rows
    **/
    private JTextField xxSmallDimensionOneTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the height of rows in the xxsmall image of a species image
     *	- the y location of hotspots in xxsmall species image rows
    **/
    private JTextField xxSmallDimensionTwoTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the width of columns in the xsmall image of a species image
     *	- the x location of hotspots in xsmall species image rows
    **/
    private JTextField xSmallDimensionOneTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the height of rows in the xsmall image of a species image
     *	- the y location of hotspots in xsmall species image rows
    **/
    private JTextField xSmallDimensionTwoTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the width of columns in the small image of a species image
     *	- the x location of hotspots in small species image rows
    **/
    private JTextField smallDimensionOneTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the height of rows in the small image of a species image
     *	- the y location of hotspots in small species image rows
    **/
    private JTextField smallDimensionTwoTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the width of columns in the medium image of a species image
     *	- the x location of hotspots in medium species image rows
    **/
    private JTextField mediumDimensionOneTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the height of rows in the medium image of a species image
     *	- the y location of hotspots in medium species image rows
    **/
    private JTextField mediumDimensionTwoTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the width of columns in the large image of a species image
     *	- the x location of hotspots in large species image rows
    **/
    private JTextField largeDimensionOneTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the height of rows in the large image of a species image
     *	- the y location of hotspots in large species image rows
    **/
    private JTextField largeDimensionTwoTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the width of columns in the xlarge image of a species image
     *	- the x location of hotspots in xlarge species image rows
    **/
    private JTextField xLargeDimensionOneTextField = null;

    /**
     * Text area for 2 purposes:
     *	- the height of rows in the xlarge image of a species image
     *	- the y location of hotspots in xlarge species image rows
    **/
    private JTextField xLargeDimensionTwoTextField = null;

    /**
     * Terrains combo box - used in editing environments
    **/
    private BioComboBox terrainsComboBox = null;

    /**
     * Current Terrain shown in Terrains combo box.
    **/
    private Terrain currentTerrain = null;

    /**
     * Terrain color button - used in setting terrain color
    **/
    private JButton terrainColorButton = null;

    /**
     * Hotspot color button - used in setting species image hotspot color
    **/
    private JButton hotspotColorButton = null;

    /**
     * Hotspot radius text field
    **/
    private JTextField hotspotRadiusTextField = null;

    /**
     * Pedigree symbol type solid radio button
    **/
    private JRadioButton pedigreeSymbolSolidRadioButton = null;

    /**
     * Pedigree symbol type forward slash radio button
    **/
    private JRadioButton pedigreeSymbolForwardSlashRadioButton = null;

    /**
     * Pedigree symbol first color button - used in setting characteristic pedigree symbol first color
    **/
    private JButton pedigreeSymbolFirstColorButton = null;
    
    /**
     * Pedigree symbol second color button - used in setting characteristic pedigree symbol second color
    **/
    private JButton pedigreeSymbolSecondColorButton = null;

    /**
     * Boolean indicating if we should draw second color text
    **/
    private boolean drawSecondColorText = false;

    /**
     * Ploidy radio button - haploid
    **/
    private JRadioButton haploidRadioButton = null;

    /**
     * Ploidy radio button - diploid XX female XY male
    **/
    private JRadioButton diploidXXFemaleXYMaleRadioButton = null;

    /**
     * Ploidy radio button - diploid XY female XX male
    **/
    private JRadioButton diploidXYFemaleXXMaleRadioButton = null;

    /**
     * Ploidy radio button - diploid no sex chromosomes
    **/
    private JRadioButton diploidNoSexChromosomesRadioButton = null;

    /**
     * Image type normal radio button
    **/
    private JRadioButton normalImageTypeRadioButton = null;

    /**
     * Image type hotspot radio button
    **/
    private JRadioButton hotspotImageTypeRadioButton = null;

    /**
     * Image type scope radio button
    **/
    private JRadioButton scopeImageTypeRadioButton = null;

    /**
     * Image type invisible radio button
    **/
    private JRadioButton invisibleImageTypeRadioButton = null;

    /**
     * Create species button
    **/
    private JButton createSpeciesButton = null;

    /**
     * Create chromosome button
    **/
    private JButton createChromosomeButton = null;

    /**
     * Create gene button
    **/
    private JButton createGeneButton = null;

    /**
     * Create allele button
    **/
    private JButton createAlleleButton = null;

    /**
     * Create trait button
    **/
    private JButton createTraitButton = null;

    /**
     * Create terrain button
    **/
    private JButton createTerrainButton = null;

    /**
     * Create characteristic button
    **/
    private JButton createCharacteristicButton = null;

    /**
     * Create genotype to phenotype rule button
    **/
    private JButton createGenotypeToPhenotypeRuleButton = null;

    /**
     * Create environment button
    **/
    private JButton createEnvironmentButton = null;

    /**
     * Create species image button
    **/
    private JButton createSpeciesImageButton = null;

    /**
     * Create species image column button
    **/
    private JButton createSpeciesImageColumnButton = null;

    /**
     * Create species image row button
    **/
    private JButton createSpeciesImageRowButton = null;

    /**
     * Gender combo box - used in editing species image columns and rows
    **/
    private BioComboBox genderComboBox;

    /**
     * Characteristics combo boxes - used in editing species image columns and rows
    **/
    private BioComboBox characteristicsComboBox[];

    /**
     * Characteristics shown in Characteristic combo boxes.
     * This array has the same size as characteristicsComboBox[] and
     * represents the selected Characteristic in each combo box.
    **/
    private Characteristic characteristics[];

    /**
     * Current Characteristics shown in each Characteristic combo box.
     * This Vector has the same size as the number of Characteristics in
     * the species and represents the full set of choices in each combo box.
     * Note that the first element in the combo boxes is null ("--") and
     * is not represented in this vector.
    **/
    private Vector currentCharacteristics;

    /**
     * If species alleles combo boxes - used in editing genotype to phenotype rule
    **/
    private BioComboBox ifSpeciesAllelesComboBox[];

    /**
     * SpeciesAllele's shown in ifSpeciesAlleles combo boxes.
     * This array has the same size as ifSpeciesAllelesComboBox[] and
     * represents the selected SpeciesAlleles in each combo box.
    **/
    private SpeciesAllele ifSpeciesAlleles[];

    /**
     * Current SpeciesAlleles shown in each SpeciesAllele combo box.
     * This Vector has the same size as the number of SpeciesAlleles in
     * the species and represents the full set of choices in each combo box.
     * Note that the first element in the combo boxes is null ("--") and
     * is not represented in this vector.
    **/
    private Vector currentSpeciesAlleles;

    /**
     * GenotypeToPhenotypeRule ThenCharacteristic combo box
    **/
    private BioComboBox thenCharacteristicComboBox = null;

    /**
     * GenotypeToPhenotypeRule ElseCharacteristic combo box
    **/
    private BioComboBox elseCharacteristicComboBox = null;

    /**
     * Current Then Characteristic in GenotypeToPhenotypeRule
    **/
    private Characteristic thenCharacteristic;

    /**
     * Current Else Characteristic in GenotypeToPhenotypeRule
    **/
    private Characteristic elseCharacteristic;

    /**
     * Chromosome image number radio buttons
    **/
    private JRadioButton chromosomeImageNumberRadioButtons[] = null;

    /**
     * Apply pending changes button
    **/
    private JButton applyButton = null;

    /**
     * Move object up button
    **/
    private JButton moveUpButton = null;
    
    /**
     * Move object down button
    **/
    private JButton moveDownButton = null;

    /**
     * Updating state
    **/
    private boolean updatingState = false;

    /**
     * Object is writeable (true) or not (false).
     * Writeable == Unlocked
     * Read-Only == Locked
    **/
    private boolean objectWriteable = false;

    /**
     * Object whose properties should be shown in this view.  All of the
     * instance variables below - world, species, etc. - are just cast
     * versions of this object, so paintComponent is fast.
    **/
    private EngineObject object = null;

    /**
     * Current painting mode - one of above PAINTING_XXX value
    **/
    private int paintingMode = PAINTING_NOTHING;

    /**
     * Control with Focus
    **/
    private Object focusControl = null;

    /**
     * Environment painting variables
    **/
    private final int xEnvironmentLeft = 40;
    private final int widthEnvironment = 300;
    private int xEnvironmentRight;
    private int xDelta;
    private int xNumCells;

    private final int yEnvironmentTop = 135;
    private final int heightEnvironment = 300;
    private int yEnvironmentBottom;
    private int yDelta;
    private int yNumCells;

    /**
     * Chromosome view - used when painting organism chromosome pair
    **/
    private ChromosomeView chromosomeView;

    /**
     * Creates an object property view.
    **/
    public ObjectPropertiesView()
    {
        // Load images
        if (imagesLoaded == false)
        {
            try
            {
                //chromosomeImage = getToolkit().getImage(PathStrings.getGIFDirectory() + "chromosomes.gif");
                chromosomeImage = getToolkit().getImage(PathStrings.getGIFURL("chromosomes.gif"));
            }
            catch (Exception e)
            {
                chromosomeImage = null;
                e.printStackTrace();
            }

            imagesLoaded = true;
        }

        // Set colors
        setBackground(Color.lightGray);
        setForeground(Color.black);

        // Set layout manager to null
        setLayout(null);

        // Insets for buttons below
        Insets insets = new Insets(2,2,2,2);
        int i, x, y;
        BevelBorder borderLowered = new BevelBorder(BevelBorder.LOWERED);

        createSpeciesButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_species2.gif"));
        createSpeciesButton.setMargin(insets);
        createSpeciesButton.addActionListener(this);
        createSpeciesButton.setActionCommand(cmdCreateSpecies);
        createSpeciesButton.setFocusPainted(false);
        createSpeciesButton.setBounds(55,120,30,30);
        createSpeciesButton.setBackground(Color.lightGray);
        createSpeciesButton.setVisible(false);
        createSpeciesButton.setToolTipText("Create a species in this world");
        add(createSpeciesButton);

        createTerrainButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_terrain3.gif"));
        createTerrainButton.setMargin(insets);
        createTerrainButton.addActionListener(this);
        createTerrainButton.setActionCommand(cmdCreateTerrain);
        createTerrainButton.setFocusPainted(false);
        createTerrainButton.setBounds(85,120,30,30);
        createTerrainButton.setBackground(Color.lightGray);
        createTerrainButton.setVisible(false);
        createTerrainButton.setToolTipText("Create a terrain in this world");
        add(createTerrainButton);

        createEnvironmentButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_environment3.gif"));
        createEnvironmentButton.setMargin(insets);
        createEnvironmentButton.addActionListener(this);
        createEnvironmentButton.setActionCommand(cmdCreateEnvironment);
        createEnvironmentButton.setFocusPainted(false);
        createEnvironmentButton.setBounds(115,120,30,30);
        createEnvironmentButton.setBackground(Color.lightGray);
        createEnvironmentButton.setVisible(false);
        createEnvironmentButton.setToolTipText("Create an environment in this world");
        add(createEnvironmentButton);

        // Create species controls
        nameTextField = new JTextField(50);
        nameTextField.setBounds(50,25,200,20);
        nameTextField.setVisible(false);
        nameTextField.setFont(getFont());
        nameTextField.setBackground(Color.white);
        nameTextField.addFocusListener(this);
        add(nameTextField);

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setBounds(80,50,420,70);
        descriptionTextArea.setVisible(false);
        descriptionTextArea.setFont(getFont());
        descriptionTextArea.setBackground(Color.white);
        descriptionTextArea.setBorder(borderLowered);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.addFocusListener(this);
        add(descriptionTextArea);

        haploidRadioButton = new JRadioButton("1 (Haploid)",false);
        haploidRadioButton.setBounds(40,140,200,20);
        haploidRadioButton.setFont(getFont());
        haploidRadioButton.setVisible(false);
        haploidRadioButton.setBackground(getBackground());
        haploidRadioButton.addActionListener(this);
        haploidRadioButton.setActionCommand(cmdHaploid);
        add(haploidRadioButton);

        diploidXXFemaleXYMaleRadioButton = new JRadioButton("2 (Diploid - XX Female, XY Male)",true);
        diploidXXFemaleXYMaleRadioButton.setBounds(40,165,300,20);
        diploidXXFemaleXYMaleRadioButton.setFont(getFont());
        diploidXXFemaleXYMaleRadioButton.setVisible(false);
        diploidXXFemaleXYMaleRadioButton.setBackground(getBackground());
        diploidXXFemaleXYMaleRadioButton.addActionListener(this);
        diploidXXFemaleXYMaleRadioButton.setActionCommand(cmdDiploidXXFemaleXYMale);
        add(diploidXXFemaleXYMaleRadioButton);

        diploidXYFemaleXXMaleRadioButton = new JRadioButton("2 (Diploid - XY Female, XX Male)",true);
        diploidXYFemaleXXMaleRadioButton.setBounds(40,190,300,20);
        diploidXYFemaleXXMaleRadioButton.setFont(getFont());
        diploidXYFemaleXXMaleRadioButton.setVisible(false);
        diploidXYFemaleXXMaleRadioButton.setBackground(getBackground());
        diploidXYFemaleXXMaleRadioButton.addActionListener(this);
        diploidXYFemaleXXMaleRadioButton.setActionCommand(cmdDiploidXYFemaleXXMale);
        add(diploidXYFemaleXXMaleRadioButton);

        diploidNoSexChromosomesRadioButton = new JRadioButton("2 (Diploid - No Sex Chromosomes)",true);
        diploidNoSexChromosomesRadioButton.setBounds(40,215,300,20);
        diploidNoSexChromosomesRadioButton.setFont(getFont());
        diploidNoSexChromosomesRadioButton.setVisible(false);
        diploidNoSexChromosomesRadioButton.setBackground(getBackground());
        diploidNoSexChromosomesRadioButton.addActionListener(this);
        diploidNoSexChromosomesRadioButton.setActionCommand(cmdDiploidNoSexChromosomes);
        add(diploidNoSexChromosomesRadioButton);

        xxSmallDimensionOneTextField = new JTextField(10);
        xxSmallDimensionOneTextField.setBounds(130,262,70,20);
        xxSmallDimensionOneTextField.setVisible(false);
        xxSmallDimensionOneTextField.setFont(getFont());
        xxSmallDimensionOneTextField.setBackground(Color.white);
        xxSmallDimensionOneTextField.addFocusListener(this);
        add(xxSmallDimensionOneTextField);

        xSmallDimensionOneTextField = new JTextField(10);
        xSmallDimensionOneTextField.setBounds(130,287,70,20);
        xSmallDimensionOneTextField.setVisible(false);
        xSmallDimensionOneTextField.setFont(getFont());
        xSmallDimensionOneTextField.setBackground(Color.white);
        xSmallDimensionOneTextField.addFocusListener(this);
        add(xSmallDimensionOneTextField);

        smallDimensionOneTextField = new JTextField(10);
        smallDimensionOneTextField.setBounds(130,312,70,20);
        smallDimensionOneTextField.setVisible(false);
        smallDimensionOneTextField.setFont(getFont());
        smallDimensionOneTextField.setBackground(Color.white);
        smallDimensionOneTextField.addFocusListener(this);
        add(smallDimensionOneTextField);

        mediumDimensionOneTextField = new JTextField(10);
        mediumDimensionOneTextField.setBounds(130,337,70,20);
        mediumDimensionOneTextField.setVisible(false);
        mediumDimensionOneTextField.setFont(getFont());
        mediumDimensionOneTextField.setBackground(Color.white);
        mediumDimensionOneTextField.addFocusListener(this);
        add(mediumDimensionOneTextField);

        largeDimensionOneTextField = new JTextField(10);
        largeDimensionOneTextField.setBounds(130,362,70,20);
        largeDimensionOneTextField.setVisible(false);
        largeDimensionOneTextField.setFont(getFont());
        largeDimensionOneTextField.setBackground(Color.white);
        largeDimensionOneTextField.addFocusListener(this);
        add(largeDimensionOneTextField);
        
        xLargeDimensionOneTextField = new JTextField(10);
        xLargeDimensionOneTextField.setBounds(130,387,70,20);
        xLargeDimensionOneTextField.setVisible(false);
        xLargeDimensionOneTextField.setFont(getFont());
        xLargeDimensionOneTextField.setBackground(Color.white);
        xLargeDimensionOneTextField.addFocusListener(this);
        add(xLargeDimensionOneTextField);

        xxSmallDimensionTwoTextField = new JTextField(10);
        xxSmallDimensionTwoTextField.setBounds(230,262,70,20);
        xxSmallDimensionTwoTextField.setVisible(false);
        xxSmallDimensionTwoTextField.setFont(getFont());
        xxSmallDimensionTwoTextField.setBackground(Color.white);
        xxSmallDimensionTwoTextField.addFocusListener(this);
        add(xxSmallDimensionTwoTextField);

        xSmallDimensionTwoTextField = new JTextField(10);
        xSmallDimensionTwoTextField.setBounds(230,287,70,20);
        xSmallDimensionTwoTextField.setVisible(false);
        xSmallDimensionTwoTextField.setFont(getFont());
        xSmallDimensionTwoTextField.setBackground(Color.white);
        xSmallDimensionTwoTextField.addFocusListener(this);
        add(xSmallDimensionTwoTextField);

        smallDimensionTwoTextField = new JTextField(10);
        smallDimensionTwoTextField.setBounds(230,312,70,20);
        smallDimensionTwoTextField.setVisible(false);
        smallDimensionTwoTextField.setFont(getFont());
        smallDimensionTwoTextField.setBackground(Color.white);
        smallDimensionTwoTextField.addFocusListener(this);
        add(smallDimensionTwoTextField);

        mediumDimensionTwoTextField = new JTextField(10);
        mediumDimensionTwoTextField.setBounds(230,337,70,20);
        mediumDimensionTwoTextField.setVisible(false);
        mediumDimensionTwoTextField.setFont(getFont());
        mediumDimensionTwoTextField.setBackground(Color.white);
        mediumDimensionTwoTextField.addFocusListener(this);
        add(mediumDimensionTwoTextField);

        largeDimensionTwoTextField = new JTextField(10);
        largeDimensionTwoTextField.setBounds(230,362,70,20);
        largeDimensionTwoTextField.setVisible(false);
        largeDimensionTwoTextField.setFont(getFont());
        largeDimensionTwoTextField.setBackground(Color.white);
        largeDimensionTwoTextField.addFocusListener(this);
        add(largeDimensionTwoTextField);

        xLargeDimensionTwoTextField = new JTextField(10);
        xLargeDimensionTwoTextField.setBounds(230,387,70,20);
        xLargeDimensionTwoTextField.setVisible(false);
        xLargeDimensionTwoTextField.setFont(getFont());
        xLargeDimensionTwoTextField.setBackground(Color.white);
        xLargeDimensionTwoTextField.addFocusListener(this);
        add(xLargeDimensionTwoTextField);

        createChromosomeButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_chromosome2.gif"));
        createChromosomeButton.setMargin(insets);
        createChromosomeButton.addActionListener(this);
        createChromosomeButton.setActionCommand(cmdCreateSpeciesChromosome);
        createChromosomeButton.setFocusPainted(false);
        createChromosomeButton.setBounds(55,420,30,30);
        createChromosomeButton.setBackground(Color.lightGray);
        createChromosomeButton.setVisible(false);
        createChromosomeButton.setToolTipText("Create a chromosome for this species");
        add(createChromosomeButton);

        createTraitButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_trait3.gif"));
        createTraitButton.setMargin(insets);
        createTraitButton.addActionListener(this);
        createTraitButton.setActionCommand(cmdCreateTrait);
        createTraitButton.setFocusPainted(false);
        createTraitButton.setBounds(85,420,30,30);
        createTraitButton.setBackground(Color.lightGray);
        createTraitButton.setVisible(false);
        createTraitButton.setToolTipText("Create a trait for this species");
        add(createTraitButton);

        createGenotypeToPhenotypeRuleButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_rule2.gif"));
        createGenotypeToPhenotypeRuleButton.setMargin(insets);
        createGenotypeToPhenotypeRuleButton.addActionListener(this);
        createGenotypeToPhenotypeRuleButton.setActionCommand(cmdCreateRule);
        createGenotypeToPhenotypeRuleButton.setFocusPainted(false);
        createGenotypeToPhenotypeRuleButton.setBounds(115,420,30,30);
        createGenotypeToPhenotypeRuleButton.setBackground(Color.lightGray);
        createGenotypeToPhenotypeRuleButton.setVisible(false);
        createGenotypeToPhenotypeRuleButton.setToolTipText("Create a genotype to phenotype rule for this species");
        add(createGenotypeToPhenotypeRuleButton);

        createSpeciesImageButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_species_image.gif"));
        createSpeciesImageButton.setMargin(insets);
        createSpeciesImageButton.addActionListener(this);
        createSpeciesImageButton.setActionCommand(cmdCreateSpeciesImage);
        createSpeciesImageButton.setFocusPainted(false);
        createSpeciesImageButton.setBounds(145,420,30,30);
        createSpeciesImageButton.setBackground(Color.lightGray);
        createSpeciesImageButton.setVisible(false);
        createSpeciesImageButton.setToolTipText("Create an image for this species");
        add(createSpeciesImageButton);

        // Species chromosome controls
        numberTypeTextField = new JTextField(10);
        numberTypeTextField.setBounds(130,25,70,20);
        numberTypeTextField.setVisible(false);
        numberTypeTextField.setFont(getFont());
        numberTypeTextField.setBackground(Color.white);
        numberTypeTextField.addFocusListener(this);
        add(numberTypeTextField);

        lengthTextField = new JTextField(10);
        lengthTextField.setBounds(105,50,100,20);
        lengthTextField.setVisible(false);
        lengthTextField.setFont(getFont());
        lengthTextField.setBackground(Color.white);
        lengthTextField.addFocusListener(this);
        add(lengthTextField);

        visibleChromosomeCheckBox = new JCheckBox("Visible: ");
        visibleChromosomeCheckBox.setBounds(5,75,200,20);
        visibleChromosomeCheckBox.setVisible(false);
        visibleChromosomeCheckBox.setFont(getFont());
        visibleChromosomeCheckBox.setBackground(getBackground());
        visibleChromosomeCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        visibleChromosomeCheckBox.addFocusListener(this);
        visibleChromosomeCheckBox.addItemListener(this);
        add(visibleChromosomeCheckBox);

        createGeneButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_gene2.gif"));
        createGeneButton.setMargin(insets);
        createGeneButton.addActionListener(this);
        createGeneButton.setActionCommand(cmdCreateGene);
        createGeneButton.setFocusPainted(false);
        createGeneButton.setBounds(55,125,30,30);
        createGeneButton.setBackground(Color.lightGray);
        createGeneButton.setVisible(false);
        createGeneButton.setToolTipText("Create a gene on this chromosome");
        add(createGeneButton);

        // Gene controls
        geneLengthTextField = new JTextField(10);
        geneLengthTextField.setBounds(105,125,100,20);
        geneLengthTextField.setVisible(false);
        geneLengthTextField.setFont(getFont());
        geneLengthTextField.setBackground(Color.white);
        geneLengthTextField.addFocusListener(this);
        add(geneLengthTextField);

        startTextField = new JTextField(10);
        startTextField.setBounds(140,150,100,20);
        startTextField.setVisible(false);
        startTextField.setFont(getFont());
        startTextField.setBackground(Color.white);
        startTextField.addFocusListener(this);
        add(startTextField);

        visibleGeneCheckBox = new JCheckBox("Visible: ");
        visibleGeneCheckBox.setBounds(5,175,200,20);
        visibleGeneCheckBox.setVisible(false);
        visibleGeneCheckBox.setFont(getFont());
        visibleGeneCheckBox.setBackground(getBackground());
        visibleGeneCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        visibleGeneCheckBox.addFocusListener(this);
        visibleGeneCheckBox.addItemListener(this);
        add(visibleGeneCheckBox);

        topStrandGeneRadioButton = new JRadioButton("Top",false);
        topStrandGeneRadioButton.setBounds(60,200,200,20);
        topStrandGeneRadioButton.setFont(getFont());
        topStrandGeneRadioButton.setVisible(false);
        topStrandGeneRadioButton.setBackground(getBackground());
        topStrandGeneRadioButton.addActionListener(this);
        topStrandGeneRadioButton.setActionCommand(cmdTopStrand);
        add(topStrandGeneRadioButton);

        bottomStrandGeneRadioButton = new JRadioButton("Bottom",false);
        bottomStrandGeneRadioButton.setBounds(60,225,200,20);
        bottomStrandGeneRadioButton.setFont(getFont());
        bottomStrandGeneRadioButton.setVisible(false);
        bottomStrandGeneRadioButton.setBackground(getBackground());
        bottomStrandGeneRadioButton.addActionListener(this);
        bottomStrandGeneRadioButton.setActionCommand(cmdBottomStrand);
        add(bottomStrandGeneRadioButton);

        createAlleleButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_allele2.gif"));
        createAlleleButton.setMargin(insets);
        createAlleleButton.addActionListener(this);
        createAlleleButton.setActionCommand(cmdCreateSpeciesAllele);
        createAlleleButton.setFocusPainted(false);
        createAlleleButton.setBounds(55,250,30,30);
        createAlleleButton.setBackground(Color.lightGray);
        createAlleleButton.setVisible(false);
        createAlleleButton.setToolTipText("Create an allele for this gene");
        add(createAlleleButton);
        
        // Allele controls

        alleleBaseValuesTextArea = new JTextArea();
        alleleBaseValuesTextArea.setVisible(false);
        alleleBaseValuesTextArea.setFont(getFont());
        alleleBaseValuesTextArea.setBackground(Color.white);
        alleleBaseValuesTextArea.setLineWrap(true);
        alleleBaseValuesTextArea.addFocusListener(this);
        alleleBaseValuesScrollPane = new JScrollPane(alleleBaseValuesTextArea,
                                                     ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        alleleBaseValuesScrollPane.setBounds(5,150,495,140);
        alleleBaseValuesScrollPane.setBorder(borderLowered);
        alleleBaseValuesScrollPane.setVisible(false);
        add(alleleBaseValuesScrollPane);

        weightTextField = new JTextField(10);
        weightTextField.setBounds(75,300,100,20);
        weightTextField.setVisible(false);
        weightTextField.setFont(getFont());
        weightTextField.setBackground(Color.white);
        weightTextField.addFocusListener(this);
        add(weightTextField);

        mutationAlleleCheckBox = new JCheckBox("Mutation Allele: ");
        mutationAlleleCheckBox.setBounds(5,325,250,20);
        mutationAlleleCheckBox.setVisible(false);
        mutationAlleleCheckBox.setFont(getFont());
        mutationAlleleCheckBox.setBackground(getBackground());
        mutationAlleleCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        mutationAlleleCheckBox.addFocusListener(this);
        mutationAlleleCheckBox.addItemListener(this);
        add(mutationAlleleCheckBox);

        visibleAlleleCheckBox = new JCheckBox("Visible: ");
        visibleAlleleCheckBox.setBounds(5,350,250,20);
        visibleAlleleCheckBox.setVisible(false);
        visibleAlleleCheckBox.setFont(getFont());
        visibleAlleleCheckBox.setBackground(getBackground());
        visibleAlleleCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        visibleAlleleCheckBox.addFocusListener(this);
        visibleAlleleCheckBox.addItemListener(this);
        add(visibleAlleleCheckBox);

        // Trait controls
        showTraitAsTextCheckBox = new JCheckBox("Show As Text In Organism View: ");
        showTraitAsTextCheckBox.setBounds(5,50,300,20);
        showTraitAsTextCheckBox.setVisible(false);
        showTraitAsTextCheckBox.setFont(getFont());
        showTraitAsTextCheckBox.setBackground(getBackground());
        showTraitAsTextCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        showTraitAsTextCheckBox.addFocusListener(this);
        showTraitAsTextCheckBox.addItemListener(this);
        add(showTraitAsTextCheckBox);

        createCharacteristicButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_characteristic2.gif"));
        createCharacteristicButton.setMargin(insets);
        createCharacteristicButton.addActionListener(this);
        createCharacteristicButton.setActionCommand(cmdCreateCharacteristic);
        createCharacteristicButton.setFocusPainted(false);
        createCharacteristicButton.setBounds(55,120,30,30);
        createCharacteristicButton.setBackground(Color.lightGray);
        createCharacteristicButton.setVisible(false);
        createCharacteristicButton.setToolTipText("Create a characteristic for this trait");
        add(createCharacteristicButton);

        // Characteristic controls
        fatalCharacteristicCheckBox = new JCheckBox("Fatal: ");
        fatalCharacteristicCheckBox.setBounds(5,50,200,20);
        fatalCharacteristicCheckBox.setVisible(false);
        fatalCharacteristicCheckBox.setFont(getFont());
        fatalCharacteristicCheckBox.setBackground(getBackground());
        fatalCharacteristicCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        fatalCharacteristicCheckBox.addFocusListener(this);
        fatalCharacteristicCheckBox.addItemListener(this);
        add(fatalCharacteristicCheckBox);

        pedigreeSymbolSolidRadioButton = new JRadioButton("Solid Pedigree Symbol",false);
        pedigreeSymbolSolidRadioButton.setBounds(30,90,200,20);
        pedigreeSymbolSolidRadioButton.setFont(getFont());
        pedigreeSymbolSolidRadioButton.setVisible(false);
        pedigreeSymbolSolidRadioButton.setBackground(getBackground());
        pedigreeSymbolSolidRadioButton.addActionListener(this);
        pedigreeSymbolSolidRadioButton.setActionCommand(cmdPedigreeSymbolSolid);
        add(pedigreeSymbolSolidRadioButton);

        pedigreeSymbolForwardSlashRadioButton = new JRadioButton("Forward Slash Pedigree Symbol",false);
        pedigreeSymbolForwardSlashRadioButton.setBounds(30,110,200,20);
        pedigreeSymbolForwardSlashRadioButton.setFont(getFont());
        pedigreeSymbolForwardSlashRadioButton.setVisible(false);
        pedigreeSymbolForwardSlashRadioButton.setBackground(getBackground());
        pedigreeSymbolForwardSlashRadioButton.addActionListener(this);
        pedigreeSymbolForwardSlashRadioButton.setActionCommand(cmdPedigreeSymbolForwardSlash);
        add(pedigreeSymbolForwardSlashRadioButton);

        pedigreeSymbolFirstColorButton = new JButton("");
        pedigreeSymbolFirstColorButton.setMargin(insets);
        pedigreeSymbolFirstColorButton.addActionListener(this);
        pedigreeSymbolFirstColorButton.setActionCommand(cmdChangePedigreeSymbolFirstColor);
        pedigreeSymbolFirstColorButton.setFocusPainted(false);
        pedigreeSymbolFirstColorButton.setBounds(115,155,40,40);
        pedigreeSymbolFirstColorButton.setBackground(getBackground());
        pedigreeSymbolFirstColorButton.setVisible(false);
        pedigreeSymbolFirstColorButton.setToolTipText("Pedigree symbol first color");
        add(pedigreeSymbolFirstColorButton);

        pedigreeSymbolSecondColorButton = new JButton("");
        pedigreeSymbolSecondColorButton.setMargin(insets);
        pedigreeSymbolSecondColorButton.addActionListener(this);
        pedigreeSymbolSecondColorButton.setActionCommand(cmdChangePedigreeSymbolSecondColor);
        pedigreeSymbolSecondColorButton.setFocusPainted(false);
        pedigreeSymbolSecondColorButton.setBounds(115,200,40,40);
        pedigreeSymbolSecondColorButton.setBackground(getBackground());
        pedigreeSymbolSecondColorButton.setVisible(false);
        pedigreeSymbolSecondColorButton.setToolTipText("Pedigree symbol second color");
        add(pedigreeSymbolSecondColorButton);

        // Environment controls
        widthTextField = new JTextField(10);
        widthTextField.setBounds(50,50,100,20);
        widthTextField.setVisible(false);
        widthTextField.setFont(getFont());
        widthTextField.setBackground(Color.white);
        widthTextField.addFocusListener(this);
        add(widthTextField);

        heightTextField = new JTextField(10);
        heightTextField.setBounds(50,75,100,20);
        heightTextField.setVisible(false);
        heightTextField.setFont(getFont());
        heightTextField.setBackground(Color.white);
        heightTextField.addFocusListener(this);
        add(heightTextField);

        terrainsComboBox = new BioComboBox();
        terrainsComboBox.addItemListener(this);
        terrainsComboBox.setBounds(100,100,100,20);
        terrainsComboBox.setVisible(false);
        terrainsComboBox.setFont(getFont());
        terrainsComboBox.setBackground(getBackground());
        terrainsComboBox.addFocusListener(this);
        add(terrainsComboBox);

        // Terrain controls
        terrainColorButton = new JButton("");
        terrainColorButton.setMargin(insets);
        terrainColorButton.addActionListener(this);
        terrainColorButton.setActionCommand(cmdChangeTerrainColor);
        terrainColorButton.setFocusPainted(false);
        terrainColorButton.setBounds(50,50,100,100);
        terrainColorButton.setBackground(Color.lightGray);
        terrainColorButton.setVisible(false);
        terrainColorButton.setToolTipText("Change the color of this terrain");
        add(terrainColorButton);

        currentTerrain = null;

        // Species Image controls
        normalImageTypeRadioButton = new JRadioButton("Normal",false);
        normalImageTypeRadioButton.setBounds(40,125,100,20);
        normalImageTypeRadioButton.setFont(getFont());
        normalImageTypeRadioButton.setVisible(false);
        normalImageTypeRadioButton.setBackground(getBackground());
        normalImageTypeRadioButton.addActionListener(this);
        normalImageTypeRadioButton.setActionCommand(cmdImageTypeNormal);
        add(normalImageTypeRadioButton);

        hotspotImageTypeRadioButton = new JRadioButton("Hotspot",false);
        hotspotImageTypeRadioButton.setBounds(40,150,100,20);
        hotspotImageTypeRadioButton.setFont(getFont());
        hotspotImageTypeRadioButton.setVisible(false);
        hotspotImageTypeRadioButton.setBackground(getBackground());
        hotspotImageTypeRadioButton.addActionListener(this);
        hotspotImageTypeRadioButton.setActionCommand(cmdImageTypeHotspot);
        add(hotspotImageTypeRadioButton);

        scopeImageTypeRadioButton = new JRadioButton("Scope",false);
        scopeImageTypeRadioButton.setBounds(40,175,100,20);
        scopeImageTypeRadioButton.setFont(getFont());
        scopeImageTypeRadioButton.setVisible(false);
        scopeImageTypeRadioButton.setBackground(getBackground());
        scopeImageTypeRadioButton.addActionListener(this);
        scopeImageTypeRadioButton.setActionCommand(cmdImageTypeScope);
        add(scopeImageTypeRadioButton);
        
        invisibleImageTypeRadioButton = new JRadioButton("Invisible",false);
        invisibleImageTypeRadioButton.setBounds(40,200,100,20);
        invisibleImageTypeRadioButton.setFont(getFont());
        invisibleImageTypeRadioButton.setVisible(false);
        invisibleImageTypeRadioButton.setBackground(getBackground());
        invisibleImageTypeRadioButton.addActionListener(this);
        invisibleImageTypeRadioButton.setActionCommand(cmdImageTypeInvisible);
        add(invisibleImageTypeRadioButton);

        hotspotColorButton = new JButton("");
        hotspotColorButton.setMargin(insets);
        hotspotColorButton.addActionListener(this);
        hotspotColorButton.setActionCommand(cmdChangeHotspotColor);
        hotspotColorButton.setFocusPainted(false);
        hotspotColorButton.setBounds(250,110,50,50);
        hotspotColorButton.setBackground(Color.lightGray);
        hotspotColorButton.setVisible(false);
        hotspotColorButton.setToolTipText("Change the hotspot color of this species image");
        add(hotspotColorButton);

        hotspotRadiusTextField = new JTextField(10);
        hotspotRadiusTextField.setBounds(250,180,100,20);
        hotspotRadiusTextField.setVisible(false);
        hotspotRadiusTextField.setFont(getFont());
        hotspotRadiusTextField.setBackground(Color.white);
        hotspotRadiusTextField.addFocusListener(this);
        add(hotspotRadiusTextField);

        createSpeciesImageColumnButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_simage_column.gif"));
        createSpeciesImageColumnButton.setMargin(insets);
        createSpeciesImageColumnButton.addActionListener(this);
        createSpeciesImageColumnButton.setActionCommand(cmdCreateSpeciesImageColumn);
        createSpeciesImageColumnButton.setFocusPainted(false);
        createSpeciesImageColumnButton.setBounds(55,225,30,30);
        createSpeciesImageColumnButton.setBackground(Color.lightGray);
        createSpeciesImageColumnButton.setVisible(false);
        createSpeciesImageColumnButton.setToolTipText("Create a column in this species image");
        add(createSpeciesImageColumnButton);

        createSpeciesImageRowButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/unlocked_simage_row.gif"));
        createSpeciesImageRowButton.setMargin(insets);
        createSpeciesImageRowButton.addActionListener(this);
        createSpeciesImageRowButton.setActionCommand(cmdCreateSpeciesImageRow);
        createSpeciesImageRowButton.setFocusPainted(false);
        createSpeciesImageRowButton.setBounds(85,225,30,30);
        createSpeciesImageRowButton.setBackground(Color.lightGray);
        createSpeciesImageRowButton.setVisible(false);
        createSpeciesImageRowButton.setToolTipText("Create a row in this species image");
        add(createSpeciesImageRowButton);

        // Species image column and row controls
        genderComboBox = new BioComboBox();
        genderComboBox.addItemListener(this);
        genderComboBox.setBounds(60,52,130,20);
        genderComboBox.setVisible(false);
        genderComboBox.setFont(getFont());
        genderComboBox.setBackground(getBackground());
        genderComboBox.addFocusListener(this);
        genderComboBox.addItem("Female and Male");
        genderComboBox.addItem("Female Only");
        genderComboBox.addItem("Male Only");
        add(genderComboBox);

        characteristicsComboBox = new BioComboBox[NUMBER_CHARACTERISTICS_PER_COLUMN_OR_ROW];
        for (i=0;i<characteristicsComboBox.length;i++)
        {
            characteristicsComboBox[i] = new BioComboBox();
            characteristicsComboBox[i].addItemListener(this);
            characteristicsComboBox[i].setBounds(100,77+(i*25),150,20);
            characteristicsComboBox[i].setVisible(false);
            characteristicsComboBox[i].setFont(getFont());
            characteristicsComboBox[i].setBackground(getBackground());
            characteristicsComboBox[i].addFocusListener(this);
            add(characteristicsComboBox[i]);
        }
        characteristics = new Characteristic[NUMBER_CHARACTERISTICS_PER_COLUMN_OR_ROW];

        // Genotype to Phenotype rule controls
        ifSpeciesAllelesComboBox = new BioComboBox[NUMBER_SPECIES_ALLELES_PER_RULE];
        for (i=0;i<ifSpeciesAllelesComboBox.length;i++)
        {
            ifSpeciesAllelesComboBox[i] = new BioComboBox();
            ifSpeciesAllelesComboBox[i].addItemListener(this);
            ifSpeciesAllelesComboBox[i].setBounds(20,100+(i*45),60,20);
            ifSpeciesAllelesComboBox[i].setVisible(false);
            ifSpeciesAllelesComboBox[i].setFont(getFont());
            ifSpeciesAllelesComboBox[i].setBackground(getBackground());
            ifSpeciesAllelesComboBox[i].addFocusListener(this);
            add(ifSpeciesAllelesComboBox[i]);
        }
        ifSpeciesAlleles = new SpeciesAllele[NUMBER_SPECIES_ALLELES_PER_RULE];

        thenCharacteristicComboBox = new BioComboBox();
        thenCharacteristicComboBox.addItemListener(this);
        thenCharacteristicComboBox.setBounds(150,100,150,20);
        thenCharacteristicComboBox.setVisible(false);
        thenCharacteristicComboBox.setFont(getFont());
        thenCharacteristicComboBox.setBackground(getBackground());
        thenCharacteristicComboBox.addFocusListener(this);
        add(thenCharacteristicComboBox);

        elseCharacteristicComboBox = new BioComboBox();
        elseCharacteristicComboBox.addItemListener(this);
        elseCharacteristicComboBox.setBounds(150,145,150,20);
        elseCharacteristicComboBox.setVisible(false);
        elseCharacteristicComboBox.setFont(getFont());
        elseCharacteristicComboBox.setBackground(getBackground());
        elseCharacteristicComboBox.addFocusListener(this);
        add(elseCharacteristicComboBox);
        
        // Species Chromosome controls
        chromosomeImageNumberRadioButtons = new JRadioButton[8];
        for (i=0;i<8;i++)
        {
            chromosomeImageNumberRadioButtons[i] = new JRadioButton("",false);
            if (i<4)
            {
                x = 50 + (i * (CHROMOSOME_IMAGE_WIDTH + 50));
                y = 165;
            }
            else
            {
                x = 50 + ((i-4) * (CHROMOSOME_IMAGE_WIDTH + 50));
                y = 280;
            }
            chromosomeImageNumberRadioButtons[i].setBounds(x,y,25,25);
            chromosomeImageNumberRadioButtons[i].setFont(getFont());
            chromosomeImageNumberRadioButtons[i].setBackground(getBackground());
            chromosomeImageNumberRadioButtons[i].setVisible(false);
            chromosomeImageNumberRadioButtons[i].addActionListener(this);
            chromosomeImageNumberRadioButtons[i].setActionCommand(cmdChromosomeImageNumber[i]);
            add(chromosomeImageNumberRadioButtons[i]);
        }

        // Organism controls
        visibleOrganismCheckBox = new JCheckBox("");
        visibleOrganismCheckBox.setBounds(100,120,50,20);
        visibleOrganismCheckBox.setVisible(false);
        visibleOrganismCheckBox.setFont(getFont());
        visibleOrganismCheckBox.setBackground(getBackground());
        visibleOrganismCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        visibleOrganismCheckBox.addFocusListener(this);
        visibleOrganismCheckBox.addItemListener(this);
        visibleOrganismCheckBox.setToolTipText("Make organism visible or invisible");
        add(visibleOrganismCheckBox);

        visibleOrganismAllelesCheckBox = new JCheckBox("");
        visibleOrganismAllelesCheckBox.setBounds(100,145,50,20);
        visibleOrganismAllelesCheckBox.setVisible(false);
        visibleOrganismAllelesCheckBox.setFont(getFont());
        visibleOrganismAllelesCheckBox.setBackground(getBackground());
        visibleOrganismAllelesCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        visibleOrganismAllelesCheckBox.addFocusListener(this);
        visibleOrganismAllelesCheckBox.addItemListener(this);
        visibleOrganismAllelesCheckBox.setToolTipText("Make organism alleles visible or invisible");
        add(visibleOrganismAllelesCheckBox);

        visibleOrganismDNACheckBox = new JCheckBox("");
        visibleOrganismDNACheckBox.setBounds(100,170,50,20);
        visibleOrganismDNACheckBox.setVisible(false);
        visibleOrganismDNACheckBox.setFont(getFont());
        visibleOrganismDNACheckBox.setBackground(getBackground());
        visibleOrganismDNACheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        visibleOrganismDNACheckBox.addFocusListener(this);
        visibleOrganismDNACheckBox.addItemListener(this);
        visibleOrganismDNACheckBox.setToolTipText("Make organism DNA visible or invisible");
        add(visibleOrganismDNACheckBox);

        alterableOrganismAllelesCheckBox = new JCheckBox("");
        alterableOrganismAllelesCheckBox.setBounds(175,145,50,20);
        alterableOrganismAllelesCheckBox.setVisible(false);
        alterableOrganismAllelesCheckBox.setFont(getFont());
        alterableOrganismAllelesCheckBox.setBackground(getBackground());
        alterableOrganismAllelesCheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        alterableOrganismAllelesCheckBox.addFocusListener(this);
        alterableOrganismAllelesCheckBox.addItemListener(this);
        alterableOrganismAllelesCheckBox.setToolTipText("Make organism alleles alterable or not");
        add(alterableOrganismAllelesCheckBox);

        alterableOrganismDNACheckBox = new JCheckBox("");
        alterableOrganismDNACheckBox.setBounds(175,170,50,20);
        alterableOrganismDNACheckBox.setVisible(false);
        alterableOrganismDNACheckBox.setFont(getFont());
        alterableOrganismDNACheckBox.setBackground(getBackground());
        alterableOrganismDNACheckBox.setHorizontalTextPosition(JCheckBox.LEFT);
        alterableOrganismDNACheckBox.addFocusListener(this);
        alterableOrganismDNACheckBox.addItemListener(this);
        alterableOrganismDNACheckBox.setToolTipText("Make organism DNA alterable or not");
        add(alterableOrganismDNACheckBox);

        // Move up button - initially just for rules
        moveUpButton = new JButton("Move Up");
        moveUpButton.setMargin(insets);
        moveUpButton.addActionListener(this);
        moveUpButton.setActionCommand(cmdMoveUp);
        moveUpButton.setFocusPainted(false);
        moveUpButton.setBounds(20,270,100,21);
        moveUpButton.setBackground(Color.lightGray);
        moveUpButton.setVisible(false);
        moveUpButton.setToolTipText("Move object up in tree");
        add(moveUpButton);

        // Move down button - initially just for rules
        moveDownButton = new JButton("Move Down");
        moveDownButton.setMargin(insets);
        moveDownButton.addActionListener(this);
        moveDownButton.setActionCommand(cmdMoveDown);
        moveDownButton.setFocusPainted(false);
        moveDownButton.setBounds(20,295,100,21);
        moveDownButton.setBackground(Color.lightGray);
        moveDownButton.setVisible(false);
        moveDownButton.setToolTipText("Move object down in tree");
        add(moveDownButton);

        // Apply button for many screens
        applyButton = new JButton("Apply");
        applyButton.setMargin(insets);
        applyButton.addActionListener(this);
        applyButton.setActionCommand(cmdApply);
        applyButton.setFocusPainted(false);
        applyButton.setBounds(220,2,60,21);
        applyButton.setBackground(Color.lightGray);
        applyButton.setVisible(false);
        applyButton.setToolTipText("Apply any pending changes to this object");
        add(applyButton);

        // Chromosome view
        chromosomeView = new ChromosomeView();
        chromosomeView.setBounds(2,30,500,200);
        chromosomeView.setBackground(getBackground());
        chromosomeView.setVisible(false);
        add(chromosomeView);

        // No object shown initially
        object = null;

        // Listen for mouse clicks, but not mouse motion initially
        addMouseListener(this);
    }

    /**
     * Get the engine object whose properties are shown in this view.
     *
     * @return		EngineObject - the engine object whose properties are shown in this view, may be null
    **/
    public EngineObject getObject()
    {
        return object;
    }

    /**
     * Set the engine object whose properties should be shown in this view.
     *
     * @param		anEngineObject EngineObject - an engine object to show in this view, may be null
    **/
    public void setObject(EngineObject anObject)
    {
        // If not changing state, return immediately
        if (anObject == object)
        {
            return;
        }

        EngineObject oldObject = object;

        if (object != null)
        {
            object.removePropertyChangeListener(this);
            object = null;
        }

        if (anObject != null)
        {
            object = anObject;
            object.addPropertyChangeListener(this);
        }

        // Update state
        updateState();

        // Notify listeners
        changes.firePropertyChange(UIProp.OBJECT,oldObject,object);
    }

    /**
     * Set the current terrain.
     *
     * @param		aTerrain Terrain - the new current terrain
    **/
    private void setCurrentTerrain(Terrain aTerrain)
    {
        // Ignore redundant setting
        if (currentTerrain == aTerrain)
        {
            return;
        }

        // Remove this view as a listener on the current terrain
        if (currentTerrain != null)
        {
            currentTerrain.removePropertyChangeListener(this);
            currentTerrain = null;
        }

        // Set new current terrain
        currentTerrain = aTerrain;

        // Add this view as a listener on the new terrain
        if (currentTerrain != null)
        {
            currentTerrain.addPropertyChangeListener(this);
        }

        // Update the state of this view
        updateState();
    }

    /**
     * Draw the graphics in this view.  Note that we are NOT overriding
     * the paint method, as that would mean we're also responsible for
     * painting the border and children, which we would rather leave to
     * standard JFC code.<p>
     *
     * @param 		g Graphics - the given graphics to use in drawing
    **/
    public void paintComponent(Graphics g)
    {
        File worldFile;
        Terrain terrain;
        Color color;

        int i, j, x, y;
        int xText1 = 5;
        int xText2 = 25;
        int xText3 = 40;
        int yText1 = 15;
        int yText2 = 40;
        int yText3 = 65;
        int yText4 = 90;
        int yText5 = 115;
        int yText6 = 140;
        int yText7 = 165;
        int yText8 = 190;
        int yText9 = 215;
        int yTextWeight = 315;
        int yTextCreate = 140;
        
        // Get bounds and paint background
        Rectangle bounds = getBounds();
        paintBackground(g,bounds);

        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Draw headline text identifying the type of object properties
        g.setFont(getFont());
        g.setColor(getForeground());

        switch (paintingMode)
        {
            case PAINTING_NOTHING:
            case PAINTING_ENGINE:
                break;

            case PAINTING_WORLD:
                if (objectWriteable)
                {
                    g.drawString("BioLogica World Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("BioLogica World Properties (Read-Only)",xText1,yText1);
                }
                worldFile = ((World)object).getFile();
                if (worldFile != null)
                {
                    g.drawString("World filename: " + worldFile.getAbsolutePath(),xText1,yText2);
                }
                else
                {
                    g.drawString("World filename: Not saved",xText1,yText2);
                }
                if (objectWriteable)
                {
                    g.drawString("Create:",xText1,yTextCreate);
                }
                break;

            case PAINTING_SPECIES:
                if (objectWriteable)
                {
                    g.drawString("Species Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Species Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Description:",xText1,yText3);
                g.drawString("Ploidy Number",xText1,yText6);
                g.drawString("Column Width",xText1+120,255);
                g.drawString("Row Height",xText1+225,255);
                g.drawString("XXSmall Image:",xText1, 275);
                g.drawString("XSmall Image:",xText1, 300);
                g.drawString("Small Image:",xText1,325);
                g.drawString("Medium Image:",xText1,350);
                g.drawString("Large Image:",xText1,375);
                g.drawString("XLarge Image:",xText1,400);
                if (objectWriteable)
                {
                    g.drawString("Create:",xText1,yTextCreate+300);
                }
                break;

            case PAINTING_SPECIES_CHROMOSOME:
                if (objectWriteable)
                {
                    g.drawString("Chromosome Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Chromosome Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Number (X,Y,1,2,3,...):",xText1,yText2);
                g.drawString("Length In Bases:",xText1,yText3);
                if (objectWriteable)
                {
                    g.drawString("Create:",xText1,yTextCreate);
                }

                // Draw chromosome image choices
                g.drawString("Image:",xText1,yText7);
                {
                    Graphics g2;
                    int xInImage, yInImage;
                    int xInView, yInView;
                    for (i=0;i<2;i++)
                    {
                        for (j=0;j<4;j++)
                        {
                            xInView = 80 + (j * (CHROMOSOME_IMAGE_WIDTH + 50));
                            yInView = 160 + (i * (CHROMOSOME_IMAGE_HEIGHT + 20));
                            xInImage = 0;
                            yInImage = -(((i * 4) + j) * CHROMOSOME_IMAGE_HEIGHT);
                            g2 = g.create(xInView,yInView,
                                          CHROMOSOME_IMAGE_WIDTH,CHROMOSOME_IMAGE_HEIGHT);
                            g2.drawImage(chromosomeImage,
                                         xInImage,yInImage,this);
                            g2.dispose();
                        }
                    }
                }
                break;

            case PAINTING_GENE:
                if (objectWriteable)
                {
                    g.drawString("Gene Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Gene Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Description:",xText1,yText3);
                g.drawString("Length In Bases:",xText1,yText6);
                g.drawString("Start Position In Bases:",xText1,yText7);
                g.drawString("Strand:",xText1,yText9);
                if (objectWriteable)
                {
                    g.drawString("Create:",xText1,yTextCreate+125);
                }
                break;

            case PAINTING_SPECIES_ALLELE:
                if (objectWriteable)
                {
                    g.drawString("Allele Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Allele Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Description:",xText1,yText3);
                g.drawString("Base Values (ATCG):",xText1,yText6);
                g.drawString("Weight (int):",xText1,yTextWeight);
                break;

            case PAINTING_TRAIT:
                if (objectWriteable)
                {
                    g.drawString("Trait Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Trait Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                if (objectWriteable)
                {
                    g.drawString("Create:",xText1,yTextCreate);
                }
                break;

            case PAINTING_CHARACTERISTIC:
                if (objectWriteable)
                {
                    g.drawString("Characteristic Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Characteristic Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Pedigree Symbol:",xText1,85);
                g.drawString("Pedigree Color:",xText1,150);
                g.drawString("First Color:",xText2+5,175);
                if (drawSecondColorText == true)
                {
                    g.drawString("Second Color:",xText2+5,220);
                }
                break;

            case PAINTING_GENOTYPE_TO_PHENOTYPE_RULE:
                if (objectWriteable)
                {
                    g.drawString("Genotype To Phenotype Rule Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Genotype To Phenotype Rule Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Gender:",xText1,yText3);
                g.drawString("If",xText1,yText5);
                for (i=0;i<ifSpeciesAllelesComboBox.length-1;i++)
                {
                    g.drawString("and",40,138+(i*45));
                }
                g.drawString("Then",95,yText5);
                g.drawString("Else",95,yText5+45);
                break;

            case PAINTING_ENVIRONMENT:
                if (objectWriteable)
                {
                    g.drawString("Environment Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Environment Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Width:",xText1,yText3);
                g.drawString("Height:",xText1,yText4);
                g.drawString("Current Terrain:",xText1,yText5);

                // Draw environment blocks
                x = xEnvironmentLeft;
                for (i=0;i<xNumCells;i++)
                {
                    y = yEnvironmentTop;
                    for (j=0;j<yNumCells;j++)
                    {
                        terrain = ((Environment)object).getTerrain(i,j);
                        if (terrain != null)
                        {
                            color = terrain.getColor();
                            if (color != null)
                            {
                                g.setColor(color);
                            }
                            else
                            {
                                g.setColor(Color.white);
                            }
                            g.fillRect(x+1,y+1,xDelta,yDelta);
                        }
                        else
                        {
                            g.setColor(Color.white);
                            g.fillRect(x+1,y+1,xDelta,yDelta);
                        }

                        y+=yDelta;
                    }

                    x+=xDelta;
                }

                // Draw environment lines
                g.setColor(Color.black);
                for (x=xEnvironmentLeft;x<=xEnvironmentRight;x+=xDelta)
                {
                    g.drawLine(x,yEnvironmentTop,x,yEnvironmentBottom);
                }
                for (y=yEnvironmentTop;y<=yEnvironmentBottom;y+=yDelta)
                {
                    g.drawLine(xEnvironmentLeft,y,xEnvironmentRight,y);
                }
                break;

            case PAINTING_ORGANISM:
                if (object != null && object.isDeleted() == false)
                {
                    if (objectWriteable)
                    {
                        g.drawString("Organism Properties",xText1,yText1);
                    }
                    else
                    {
                        g.drawString("Organism Properties (Read-Only except name)",xText1,yText1);
                    }
                    g.drawString("Name:",xText1,yText2);
                    g.drawString("Species: " + ((Organism)object).getSpecies().getName(), xText1, yText3);
    
                    int gender = ((Organism)object).getSex();
                    if (gender == Organism.MALE)
                    {
                        g.drawString("Gender: Male",xText1,yText4);
                    }
                    else if (gender == Organism.FEMALE)
                    {
                        g.drawString("Gender: Female",xText1,yText4);
                    }
                    else
                    {
                        g.drawString("Gender: Unisexual",xText1,yText4);
                    }

                    g.drawString("Visible",90,115);
                    g.drawString("Alterable",160,115);
                    g.drawString("Phenotype:",5,135);
                    g.drawString("Alleles:",5,160);
                    g.drawString("DNA:",5,185);
                }
                else
                {
                    g.drawString("Organism null or deleted",xText1,yText1);
                }
                break;

            case PAINTING_TERRAIN:
                if (objectWriteable)
                {
                    g.drawString("Terrain Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Terrain Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Color:",xText1,yText3);
                break;
        
            case PAINTING_SPECIES_IMAGE:
                if (object == null || object.isDeleted() == true)
                {
                    break;
                }
                if (objectWriteable)
                {
                    g.drawString("Species Image Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Species Image Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Species Image GIF File:",xText1,yText3);
                {
                    String imageSource = ((SpeciesImage)object).getSource(SpeciesImage.LARGE_IMAGE_SIZE);
                    if (imageSource != null)
                    {
                        String imageFilePath = imageSource;
                        if (imageFilePath != null)
                        {
                            g.drawString(imageFilePath,15,yText4-5);
                        }
                    }
                }
                g.drawString("Image Type:",xText1,yText4+30);
                g.drawString("Hotspot Color:", 150, yText4+30);
                g.drawString("Hotspot Radius:", 150, yText4+105);

                if (objectWriteable)
                {
                    g.drawString("Create:",xText1,yTextCreate+105);
                }

                {
                    Image speciesImageImage = ((SpeciesImage)object).getImage(SpeciesImage.LARGE_IMAGE_SIZE,this);
                    if (speciesImageImage != null)
                    {
                        g.drawString("Image:",xText1,275);
                        int width = speciesImageImage.getWidth(this);
                        int height = speciesImageImage.getHeight(this);
                        if (width != -1 && height != -1)
                        {
                            if (width > 250)
                            {
                                // Draw at reduced scale with a width of 250
                                int newHeight = (250 * height) / width;
                                g.drawImage(speciesImageImage,50,290,250,newHeight,this);
                            }
                            else
                            {
                                // Draw at 1:1 scale
                                g.drawImage(speciesImageImage,5,290,this);
                            }
                        }
                    }
                }
                break;
        
            case PAINTING_SPECIES_IMAGE_COLUMN:
                if (objectWriteable)
                {
                    g.drawString("Species Image Column Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Species Image Column Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Gender:",xText1,yText3);
                g.drawString("Characteristics:",xText1,yText4);
                g.drawString("and",xText1+250,yText4);
                g.drawString("and",xText1+250,yText5);
                break;
        
            case PAINTING_SPECIES_IMAGE_ROW:
                if (objectWriteable)
                {
                    g.drawString("Species Image Row Properties",xText1,yText1);
                }
                else
                {
                    g.drawString("Species Image Row Properties (Read-Only)",xText1,yText1);
                }
                g.drawString("Name:",xText1,yText2);
                g.drawString("Gender:",xText1,yText3);
                g.drawString("Characteristics:",xText1,yText4);
                g.drawString("and",xText1+250,yText4);
                g.drawString("and",xText1+250,yText5);
                g.drawString("X Hotspot",xText1+130,255);
                g.drawString("Y Hotspot",xText1+230,255);
                g.drawString("XXSmall Image:",xText1, 275);
                g.drawString("XSmall Image:",xText1, 300);
                g.drawString("Small Image:",xText1,325);
                g.drawString("Medium Image:",xText1,350);
                g.drawString("Large Image:",xText1,375);
                g.drawString("XLarge Image:",xText1,400);
                break;
        
            case PAINTING_ORGANISM_CHROMOSOME_PAIR:
                if (object != null && object.isDeleted() == false)
                {
                    if (objectWriteable)
                    {
                        g.drawString("Organism Chromosome Pair Properties",xText1,yText1);
                    }
                    else
                    {
                        g.drawString("Organism Chromosome Pair Properties (Read-Only)",xText1,yText1);
                    }
                }
                else
                {
                    g.drawString("Organism Chromosome Pair null or deleted",xText1,yText1);
                }
                break;

            case PAINTING_ORGANISM_ALLELE_PAIR:
                if (object != null && object.isDeleted() == false)
                {
                    if (objectWriteable)
                    {
                        g.drawString("Organism Allele Pair Properties",xText1,yText1);
                    }
                    else
                    {
                        g.drawString("Organism Allele Pair Properties (Read-Only)",xText1,yText1);
                    }
                }
                else
                {
                    g.drawString("Organism Allele Pair null or deleted",xText1,yText1);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Return the preferred size of this canvas
     *
     * @return		Dimension - preferred size of canvas
    **/
    public Dimension getPreferredSize()
    {
        return new Dimension(preferredWidth, preferredHeight);
    }

    /**
     * ImageObserver method
    **/
    public boolean imageUpdate(Image anImage,
                               int infoFlags,
                               int x,
                               int y,
                               int width,
                               int height)
    {
        if (infoFlags == ImageObserver.ALLBITS)
        {
            // Only repaint if we're showing the properties
            // of an object that includes an image
            if (object instanceof SpeciesImage ||
                object instanceof SpeciesChromosome)
            {
                repaint();
                return false;
            }
        }
        else if (infoFlags == ImageObserver.ABORT ||
                 infoFlags == ImageObserver.ERROR)
        {
            // Notify someone?
        }

        return true;
    }

    /**
     * Handle mouse click events
    **/
    public void mouseClicked(MouseEvent event)
    {
        // Can't use mouse clicked events for creating
        // dragons, as you don't get a click event if
        // the mouse moves while the mouse button is down.
    }

    /**
     * Handle mouse entered event
    **/
    public void mouseEntered(MouseEvent event)
    {
    }

    /**
     * Handle mouse exited event
    **/
    public void mouseExited(MouseEvent event)
    {
    }

    /**
     * Handle mouse pressed event
    **/
    public void mousePressed(MouseEvent event)
    {
        // Ignore mouse events if object is not an Environment or Terrain
        // or current terrain is null
        if ((object instanceof Environment || object instanceof Terrain) == false)
        {
            return;
        }

        // If showing an environment, determine if user clicked on environment drawing
        if (object instanceof Environment)
        {
            int iRow, iColumn;
            int x = event.getX();
            int y = event.getY();
    
            if (x >= xEnvironmentLeft && x <= xEnvironmentRight &&
                y >= yEnvironmentTop && y <= yEnvironmentBottom)
            {
                // Mouse pressed on environment picture, so determine row and column
                iColumn = (x - xEnvironmentLeft - 1) / xDelta;
                iRow = (y - yEnvironmentTop - 1) / yDelta;
    
                // Set terrain in that block
                ((Environment)object).setTerrain(iColumn,iRow,currentTerrain);
    
                // Update state
                updateState();
            }
        }
    }

    /**
     * Handle mouse released event
    **/
    public void mouseReleased(MouseEvent event)
    {
    }

    /**
     * Handle mouse dragged event
    **/
    public void mouseDragged(MouseEvent event)
    {
    }

    /**
     * Handle mouse moved event
    **/
    public void mouseMoved(MouseEvent event)
    {
    }

    /**
     * Update our state
    **/
    public void updateState()
    {
        int i;

        // Avoid recursive calls to this method
        if (updatingState == true)
        {
            return;
        }
        updatingState = true;

        // Process any pending text edits
        processPendingTextEdits();

        // Hide everything initially for simplicity's sake
        paintingMode = PAINTING_NOTHING;

        nameTextField.setVisible(false);
        descriptionTextArea.setVisible(false);
        numberTypeTextField.setVisible(false);
        lengthTextField.setVisible(false);
        geneLengthTextField.setVisible(false);
        startTextField.setVisible(false);
        visibleChromosomeCheckBox.setVisible(false);
        visibleGeneCheckBox.setVisible(false);
        visibleOrganismCheckBox.setVisible(false);
        visibleOrganismAllelesCheckBox.setVisible(false);
        visibleOrganismDNACheckBox.setVisible(false);
        alterableOrganismAllelesCheckBox.setVisible(false);
        alterableOrganismDNACheckBox.setVisible(false);
        alleleBaseValuesTextArea.setVisible(false);
        alleleBaseValuesScrollPane.setVisible(false);
        weightTextField.setVisible(false);
        mutationAlleleCheckBox.setVisible(false);
        visibleAlleleCheckBox.setVisible(false);
        showTraitAsTextCheckBox.setVisible(false);
        fatalCharacteristicCheckBox.setVisible(false);
        widthTextField.setVisible(false);
        heightTextField.setVisible(false);

        bottomStrandGeneRadioButton.setVisible(false);
        topStrandGeneRadioButton.setVisible(false);

        xxSmallDimensionOneTextField.setVisible(false);
        xSmallDimensionOneTextField.setVisible(false);
        smallDimensionOneTextField.setVisible(false);
        mediumDimensionOneTextField.setVisible(false);
        largeDimensionOneTextField.setVisible(false);
        xLargeDimensionOneTextField.setVisible(false);

        xxSmallDimensionTwoTextField.setVisible(false);
        xSmallDimensionTwoTextField.setVisible(false);
        smallDimensionTwoTextField.setVisible(false);
        mediumDimensionTwoTextField.setVisible(false);
        largeDimensionTwoTextField.setVisible(false);
        xLargeDimensionTwoTextField.setVisible(false);

        terrainsComboBox.setVisible(false);
        terrainColorButton.setVisible(false);
        haploidRadioButton.setVisible(false);
        diploidXXFemaleXYMaleRadioButton.setVisible(false);
        diploidXYFemaleXXMaleRadioButton.setVisible(false);
        diploidNoSexChromosomesRadioButton.setVisible(false);
        genderComboBox.setVisible(false);

        normalImageTypeRadioButton.setVisible(false);
        hotspotImageTypeRadioButton.setVisible(false);
        scopeImageTypeRadioButton.setVisible(false);
        invisibleImageTypeRadioButton.setVisible(false);

        hotspotColorButton.setVisible(false);
        hotspotRadiusTextField.setVisible(false);

        for (i=0;i<characteristicsComboBox.length;i++)
        {
            characteristicsComboBox[i].setVisible(false);
            characteristics[i] = null;
        }

        for (i=0;i<ifSpeciesAllelesComboBox.length;i++)
        {
            ifSpeciesAllelesComboBox[i].setVisible(false);
            ifSpeciesAlleles[i] = null;
        }

        for (i=0;i<8;i++)
        {
            chromosomeImageNumberRadioButtons[i].setVisible(false);
        }
        
        thenCharacteristicComboBox.setVisible(false);
        elseCharacteristicComboBox.setVisible(false);
        pedigreeSymbolSolidRadioButton.setVisible(false);
        pedigreeSymbolForwardSlashRadioButton.setVisible(false);
        pedigreeSymbolFirstColorButton.setVisible(false);
        pedigreeSymbolSecondColorButton.setVisible(false);

        createSpeciesButton.setVisible(false);
        createChromosomeButton.setVisible(false);
        createGeneButton.setVisible(false);
        createAlleleButton.setVisible(false);
        createTraitButton.setVisible(false);
        createGenotypeToPhenotypeRuleButton.setVisible(false);
        createCharacteristicButton.setVisible(false);
        createEnvironmentButton.setVisible(false);
        createTerrainButton.setVisible(false);
        createSpeciesImageButton.setVisible(false);
        createSpeciesImageColumnButton.setVisible(false);
        createSpeciesImageRowButton.setVisible(false);
        applyButton.setVisible(false);
        moveUpButton.setVisible(false);
        moveDownButton.setVisible(false);

        chromosomeView.setVisible(false);

        if (object != null)
        {
            if (object.isDeleted() == true)
            {
                // Object deleted, bail with state set to null
                object.removePropertyChangeListener(this);
                object = null;
                updatingState = false;
                repaint();
                return;
            }
    
            if (object.isLocked())
            {
                objectWriteable = false;
            }
            else
            {
                objectWriteable = true;
            }

            if (object instanceof World)
            {
                paintingMode = PAINTING_WORLD;
                createSpeciesButton.setVisible(true);
                createSpeciesButton.setEnabled(objectWriteable);
                createTerrainButton.setVisible(true);
                createTerrainButton.setEnabled(objectWriteable);
                createEnvironmentButton.setVisible(true);
                createEnvironmentButton.setEnabled(objectWriteable);
            }
            else if (object instanceof Species)
            {
                paintingMode = PAINTING_SPECIES;
                Species species = (Species) object;
                nameTextField.setVisible(true);
                nameTextField.setText(species.getName());
                nameTextField.setEnabled(objectWriteable);
                descriptionTextArea.setVisible(true);
                descriptionTextArea.setText(species.getDescription());
                descriptionTextArea.setEnabled(objectWriteable);
                haploidRadioButton.setVisible(true);
                diploidXXFemaleXYMaleRadioButton.setVisible(true);
                diploidXYFemaleXXMaleRadioButton.setVisible(true);
                diploidNoSexChromosomesRadioButton.setVisible(true);
                if (species.getPloidyNumber() == 1)
                {
                    haploidRadioButton.setSelected(true);
                    diploidXYFemaleXXMaleRadioButton.setSelected(false);
                    diploidXXFemaleXYMaleRadioButton.setSelected(false);
                    diploidNoSexChromosomesRadioButton.setSelected(false);

                    // Cannot change to diploid if sex chromosomes exist
                    if (species.getNumberOfSexChromosomes() > 0 ||
                        objectWriteable == false)
                    {
                        haploidRadioButton.setEnabled(false);
                        diploidXXFemaleXYMaleRadioButton.setEnabled(false);
                        diploidXYFemaleXXMaleRadioButton.setEnabled(false);
                        diploidNoSexChromosomesRadioButton.setEnabled(false);
                    }
                    else
                    {
                        haploidRadioButton.setEnabled(true);
                        diploidXXFemaleXYMaleRadioButton.setEnabled(true);
                        diploidXYFemaleXXMaleRadioButton.setEnabled(true);
                        diploidNoSexChromosomesRadioButton.setEnabled(true);
                    }
                }
                else  // diploid
                {
                    haploidRadioButton.setSelected(false);
                    if (species.getDiploidType() == Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE)
                    {
                        diploidXYFemaleXXMaleRadioButton.setSelected(false);
                        diploidXXFemaleXYMaleRadioButton.setSelected(true);
                        diploidNoSexChromosomesRadioButton.setSelected(false);
                    }
                    else if (species.getDiploidType() == Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE)
                    {
                        diploidXYFemaleXXMaleRadioButton.setSelected(true);
                        diploidXXFemaleXYMaleRadioButton.setSelected(false);
                        diploidNoSexChromosomesRadioButton.setSelected(false);
                    }
                    else
                    {
                        diploidXYFemaleXXMaleRadioButton.setSelected(false);
                        diploidXXFemaleXYMaleRadioButton.setSelected(false);
                        diploidNoSexChromosomesRadioButton.setSelected(true);
                    }
                    
                    if (objectWriteable == false)
                    {
                        // Locked, so can't change
                        haploidRadioButton.setEnabled(false);
                        diploidXXFemaleXYMaleRadioButton.setEnabled(false);
                        diploidXYFemaleXXMaleRadioButton.setEnabled(false);
                        diploidNoSexChromosomesRadioButton.setEnabled(false);
                    }
                    else if (species.getNumberOfSexChromosomes() > 0)
                    {
                        // Cannot change to haploid if sex chromosomes exist
                        haploidRadioButton.setEnabled(false);
                        diploidXXFemaleXYMaleRadioButton.setEnabled(true);
                        diploidXYFemaleXXMaleRadioButton.setEnabled(true);
                        diploidNoSexChromosomesRadioButton.setEnabled(false);
                    }
                    else
                    {
                        haploidRadioButton.setEnabled(true);
                        diploidXXFemaleXYMaleRadioButton.setEnabled(true);
                        diploidXYFemaleXXMaleRadioButton.setEnabled(true);
                        diploidNoSexChromosomesRadioButton.setEnabled(true);
                    }
                }
                xxSmallDimensionOneTextField.setVisible(true);
                xxSmallDimensionOneTextField.setText(Integer.toString(species.getImageColumnWidth(SpeciesImage.XXSMALL_IMAGE_SIZE)));
                xxSmallDimensionOneTextField.setEnabled(objectWriteable);
                xSmallDimensionOneTextField.setVisible(true);
                xSmallDimensionOneTextField.setText(Integer.toString(species.getImageColumnWidth(SpeciesImage.XSMALL_IMAGE_SIZE)));
                xSmallDimensionOneTextField.setEnabled(objectWriteable);
                smallDimensionOneTextField.setVisible(true);
                smallDimensionOneTextField.setText(Integer.toString(species.getImageColumnWidth(SpeciesImage.SMALL_IMAGE_SIZE)));
                smallDimensionOneTextField.setEnabled(objectWriteable);
                mediumDimensionOneTextField.setVisible(true);
                mediumDimensionOneTextField.setText(Integer.toString(species.getImageColumnWidth(SpeciesImage.MEDIUM_IMAGE_SIZE)));
                mediumDimensionOneTextField.setEnabled(objectWriteable);
                largeDimensionOneTextField.setVisible(true);
                largeDimensionOneTextField.setText(Integer.toString(species.getImageColumnWidth(SpeciesImage.LARGE_IMAGE_SIZE)));
                largeDimensionOneTextField.setEnabled(objectWriteable);
                xLargeDimensionOneTextField.setVisible(true);
                xLargeDimensionOneTextField.setText(Integer.toString(species.getImageColumnWidth(SpeciesImage.XLARGE_IMAGE_SIZE)));
                xLargeDimensionOneTextField.setEnabled(objectWriteable);

                xxSmallDimensionTwoTextField.setVisible(true);
                xxSmallDimensionTwoTextField.setText(Integer.toString(species.getImageRowHeight(SpeciesImage.XXSMALL_IMAGE_SIZE)));
                xxSmallDimensionTwoTextField.setEnabled(objectWriteable);
                xSmallDimensionTwoTextField.setVisible(true);
                xSmallDimensionTwoTextField.setText(Integer.toString(species.getImageRowHeight(SpeciesImage.XSMALL_IMAGE_SIZE)));
                xSmallDimensionTwoTextField.setEnabled(objectWriteable);
                smallDimensionTwoTextField.setVisible(true);
                smallDimensionTwoTextField.setText(Integer.toString(species.getImageRowHeight(SpeciesImage.SMALL_IMAGE_SIZE)));
                smallDimensionTwoTextField.setEnabled(objectWriteable);
                mediumDimensionTwoTextField.setVisible(true);
                mediumDimensionTwoTextField.setText(Integer.toString(species.getImageRowHeight(SpeciesImage.MEDIUM_IMAGE_SIZE)));
                mediumDimensionTwoTextField.setEnabled(objectWriteable);
                largeDimensionTwoTextField.setVisible(true);
                largeDimensionTwoTextField.setText(Integer.toString(species.getImageRowHeight(SpeciesImage.LARGE_IMAGE_SIZE)));
                largeDimensionTwoTextField.setEnabled(objectWriteable);
                xLargeDimensionTwoTextField.setVisible(true);
                xLargeDimensionTwoTextField.setText(Integer.toString(species.getImageRowHeight(SpeciesImage.XLARGE_IMAGE_SIZE)));
                xLargeDimensionTwoTextField.setEnabled(objectWriteable);

                createChromosomeButton.setVisible(objectWriteable);
                createTraitButton.setVisible(objectWriteable);
                createGenotypeToPhenotypeRuleButton.setVisible(objectWriteable);
                createSpeciesImageButton.setVisible(objectWriteable);
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof SpeciesChromosome)
            {
                paintingMode = PAINTING_SPECIES_CHROMOSOME;
                SpeciesChromosome speciesChromosome = (SpeciesChromosome) object;
                numberTypeTextField.setVisible(true);
                numberTypeTextField.setText(SpeciesChromosome.convertChromosomeNumberTypeToString(speciesChromosome.getNumberType()));
                numberTypeTextField.setEnabled(objectWriteable);
                lengthTextField.setVisible(true);
                lengthTextField.setText(Integer.toString(speciesChromosome.getLengthInBases()));
                lengthTextField.setEnabled(objectWriteable);
                visibleChromosomeCheckBox.setVisible(true);
                visibleChromosomeCheckBox.setSelected(speciesChromosome.isVisible());
                visibleChromosomeCheckBox.setEnabled(objectWriteable);
                createGeneButton.setVisible(objectWriteable);
                applyButton.setVisible(objectWriteable);

                int imageNumber = speciesChromosome.getImageNumber();
                for (i=0;i<8;i++)
                {
                    chromosomeImageNumberRadioButtons[i].setVisible(true);
                    if (imageNumber == i)
                    {
                        chromosomeImageNumberRadioButtons[i].setSelected(true);
                    }
                    else
                    {
                        chromosomeImageNumberRadioButtons[i].setSelected(false);
                    }
                    chromosomeImageNumberRadioButtons[i].setEnabled(objectWriteable);
                }
            }
            else if (object instanceof Gene)
            {
                paintingMode = PAINTING_GENE;
                Gene gene = (Gene) object;
                nameTextField.setVisible(true);
                nameTextField.setText(gene.getName());
                nameTextField.setEnabled(objectWriteable);
                descriptionTextArea.setVisible(true);
                descriptionTextArea.setText(gene.getDescription());
                descriptionTextArea.setEnabled(objectWriteable);
                geneLengthTextField.setVisible(true);
                geneLengthTextField.setText(Integer.toString(gene.getLengthInBases()));
                geneLengthTextField.setEnabled(objectWriteable);
                startTextField.setVisible(true);
                startTextField.setText(Integer.toString(gene.getStartIndexInHolder()));
                startTextField.setEnabled(objectWriteable);
                visibleGeneCheckBox.setVisible(true);
                visibleGeneCheckBox.setSelected(gene.isVisible());
                visibleGeneCheckBox.setEnabled(objectWriteable);
                bottomStrandGeneRadioButton.setVisible(true);
                bottomStrandGeneRadioButton.setEnabled(objectWriteable);
                topStrandGeneRadioButton.setVisible(true);
                topStrandGeneRadioButton.setEnabled(objectWriteable);
                if (gene.getStrand() == Gene.BOTTOM_STRAND)
                {
                    bottomStrandGeneRadioButton.setSelected(true);
                    topStrandGeneRadioButton.setSelected(false);
                }
                else
                {
                    bottomStrandGeneRadioButton.setSelected(false);
                    topStrandGeneRadioButton.setSelected(true);
                }
                createAlleleButton.setVisible(objectWriteable);
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof SpeciesAllele)
            {
                paintingMode = PAINTING_SPECIES_ALLELE;
                SpeciesAllele speciesAllele = (SpeciesAllele) object;
                nameTextField.setText(speciesAllele.getTextSymbol());
                nameTextField.setVisible(true);
                nameTextField.setEnabled(objectWriteable);
                descriptionTextArea.setVisible(true);
                descriptionTextArea.setText(speciesAllele.getDescription());
                descriptionTextArea.setEnabled(objectWriteable);
                alleleBaseValuesTextArea.setText(speciesAllele.getBasesAsString());
                alleleBaseValuesTextArea.setEnabled(objectWriteable);
                alleleBaseValuesTextArea.setVisible(true);
                alleleBaseValuesScrollPane.setVisible(true);
                weightTextField.setText(Integer.toString(speciesAllele.getWeight()));
                weightTextField.setVisible(true);
                weightTextField.setEnabled(objectWriteable);
                mutationAlleleCheckBox.setVisible(true);
                mutationAlleleCheckBox.setSelected(speciesAllele.isMutationAllele());
                mutationAlleleCheckBox.setEnabled(objectWriteable);
                visibleAlleleCheckBox.setVisible(true);
                visibleAlleleCheckBox.setSelected(speciesAllele.isVisible());
                visibleAlleleCheckBox.setEnabled(objectWriteable);
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof Trait)
            {
                paintingMode = PAINTING_TRAIT;
                Trait trait = (Trait) object;
                nameTextField.setText(trait.getName());
                nameTextField.setVisible(true);
                nameTextField.setEnabled(objectWriteable);
                showTraitAsTextCheckBox.setVisible(true);
                showTraitAsTextCheckBox.setSelected(trait.isShowAsTextInOrganismView());
                showTraitAsTextCheckBox.setEnabled(objectWriteable);
                createCharacteristicButton.setVisible(objectWriteable);
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof Characteristic)
            {
                paintingMode = PAINTING_CHARACTERISTIC;
                Characteristic characteristic = (Characteristic) object;
                nameTextField.setText(characteristic.getName());
                nameTextField.setVisible(true);
                nameTextField.setEnabled(objectWriteable);
                fatalCharacteristicCheckBox.setVisible(true);
                fatalCharacteristicCheckBox.setSelected(characteristic.isFatal());
                fatalCharacteristicCheckBox.setEnabled(objectWriteable);
                if (characteristic.getPedigreeSymbolType() == Characteristic.PEDIGREE_SYMBOL_SOLID_COLOR)
                {
                    pedigreeSymbolSolidRadioButton.setSelected(true);
                    pedigreeSymbolForwardSlashRadioButton.setSelected(false);
                }
                else
                {
                    pedigreeSymbolSolidRadioButton.setSelected(false);
                    pedigreeSymbolForwardSlashRadioButton.setSelected(true);
                }
                pedigreeSymbolSolidRadioButton.setVisible(true);
                pedigreeSymbolSolidRadioButton.setEnabled(objectWriteable);
                pedigreeSymbolForwardSlashRadioButton.setVisible(true);
                pedigreeSymbolForwardSlashRadioButton.setEnabled(objectWriteable);
                pedigreeSymbolFirstColorButton.setBackground(characteristic.getPedigreeSymbolFirstColor());
                pedigreeSymbolFirstColorButton.setVisible(true);
                pedigreeSymbolFirstColorButton.setEnabled(objectWriteable);
                if (objectWriteable)
                {
                    // If pedigree color type is solid color, then disable and hide second color button, else enable it
                    if (characteristic.getPedigreeSymbolType() == Characteristic.PEDIGREE_SYMBOL_SOLID_COLOR)
                    {
                        pedigreeSymbolSecondColorButton.setEnabled(false);
                    }
                    else
                    {
                        drawSecondColorText = true;
                        pedigreeSymbolSecondColorButton.setEnabled(true);
                        pedigreeSymbolSecondColorButton.setBackground(characteristic.getPedigreeSymbolSecondColor());
                        pedigreeSymbolSecondColorButton.setVisible(true);
                    }
                }
                else
                {
                    pedigreeSymbolSecondColorButton.setEnabled(false);
                }
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof GenotypeToPhenotypeRule)
            {
                paintingMode = PAINTING_GENOTYPE_TO_PHENOTYPE_RULE;
                GenotypeToPhenotypeRule genotypeToPhenotypeRule = (GenotypeToPhenotypeRule) object;
                Species species = genotypeToPhenotypeRule.getSpecies();

                // Set name field
                nameTextField.setText(genotypeToPhenotypeRule.getName());
                nameTextField.setVisible(true);
                nameTextField.setEnabled(objectWriteable);

                // Set gender combo box
                genderComboBox.setVisible(true);
                genderComboBox.setEnabled(objectWriteable);
                int gender = genotypeToPhenotypeRule.getGender();
                if (gender == Species.FEMALE_ONLY)
                {
                    genderComboBox.setSelectedItem("Female Only");
                }
                else if (gender == Species.MALE_ONLY)
                {
                    genderComboBox.setSelectedItem("Male Only");
                }
                else
                {
                    genderComboBox.setSelectedItem("Female and Male");
                }

                // Clear the if species alleles combo boxes,
                // make them visible and add one item "--"
                for (i=0;i<ifSpeciesAllelesComboBox.length;i++)
                {
                    if (ifSpeciesAllelesComboBox[i].getItemCount() > 0)
                    {
                        ifSpeciesAllelesComboBox[i].removeAllItems();
                    }

                    ifSpeciesAllelesComboBox[i].addItem("--");
                    ifSpeciesAllelesComboBox[i].setVisible(true);
                    ifSpeciesAllelesComboBox[i].setEnabled(objectWriteable);
                }

                // Create currentSpeciesAlleles vector and
                // add all SpeciesAlleles to all combo boxes
                SpeciesAllele aSpeciesAllele;
                currentSpeciesAlleles = species.getSpeciesAllelesVector();
                Enumeration eSpeciesAlleles= currentSpeciesAlleles.elements();
                while (eSpeciesAlleles.hasMoreElements())
                {
                    aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                    for (i=0;i<ifSpeciesAllelesComboBox.length;i++)
                    {
                        ifSpeciesAllelesComboBox[i].addItem(aSpeciesAllele.getTextSymbol());
                    }
                }

                // Select the current species allele in each combo box if not null
                eSpeciesAlleles = genotypeToPhenotypeRule.getIfSpeciesAlleles();
                for (i=0;i<ifSpeciesAllelesComboBox.length;i++)
                {
                    if (eSpeciesAlleles.hasMoreElements())
                    {
                        aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                        ifSpeciesAllelesComboBox[i].setSelectedItem(aSpeciesAllele.getTextSymbol());
                        ifSpeciesAlleles[i] = aSpeciesAllele;
                    }
                    else
                    {
                        ifSpeciesAllelesComboBox[i].setSelectedItem("--");
                        ifSpeciesAlleles[i] = null;
                    }
                }

                // Clear thenCharacteristic and elseCharacteristic combo boxes,
                // make them visible and add one item "--"
                if (thenCharacteristicComboBox.getItemCount() > 0)
                {
                    thenCharacteristicComboBox.removeAllItems();
                }
                thenCharacteristicComboBox.addItem("--");
                thenCharacteristicComboBox.setVisible(true);
                thenCharacteristicComboBox.setEnabled(objectWriteable);

                if (elseCharacteristicComboBox.getItemCount() > 0)
                {
                    elseCharacteristicComboBox.removeAllItems();
                }
                elseCharacteristicComboBox.addItem("--");
                elseCharacteristicComboBox.setVisible(true);
                elseCharacteristicComboBox.setEnabled(objectWriteable);

                // Determine the selected then and else characteristics
                thenCharacteristic = genotypeToPhenotypeRule.getThenCharacteristic();
                elseCharacteristic = genotypeToPhenotypeRule.getElseCharacteristic();

                // Create currentCharacteristics vector and
                // add all Characteristics to thenCharacteristicComboBox
                // and elseCharacteristicComboBox
                Characteristic aCharacteristic;
                currentCharacteristics = species.getCharacteristicsVector();
                Enumeration eCharacteristics = currentCharacteristics.elements();
                while (eCharacteristics.hasMoreElements())
                {
                    aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                    thenCharacteristicComboBox.addItem(aCharacteristic.getName());
                    elseCharacteristicComboBox.addItem(aCharacteristic.getName());
                }
                if (thenCharacteristic != null)
                {
                    thenCharacteristicComboBox.setSelectedItem(thenCharacteristic.getName());
                }
                if (elseCharacteristic != null)
                {
                    elseCharacteristicComboBox.setSelectedItem(elseCharacteristic.getName());
                }

                applyButton.setVisible(objectWriteable);

                moveUpButton.setVisible(true);
                moveUpButton.setEnabled(objectWriteable);
                moveDownButton.setVisible(true);
                moveDownButton.setEnabled(objectWriteable);
            }
            else if (object instanceof Terrain)
            {
                paintingMode = PAINTING_TERRAIN;
                Terrain terrain = (Terrain) object;
                nameTextField.setText(terrain.getName());
                nameTextField.setVisible(true);
                nameTextField.setEnabled(objectWriteable);
                terrainColorButton.setBackground(terrain.getColor());
                terrainColorButton.setVisible(true);
                terrainColorButton.setEnabled(objectWriteable);
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof Environment)
            {
                paintingMode = PAINTING_ENVIRONMENT;
                Environment environment = (Environment) object;
                nameTextField.setText(environment.getName());
                nameTextField.setVisible(true);
                nameTextField.setEnabled(objectWriteable);
                widthTextField.setText(Integer.toString(environment.getWidth()));
                widthTextField.setVisible(true);
                widthTextField.setEnabled(objectWriteable);
                heightTextField.setText(Integer.toString(environment.getHeight()));
                heightTextField.setVisible(true);
                heightTextField.setEnabled(objectWriteable);

                // Clear terrains combo box
                if (terrainsComboBox.getItemCount() > 0)
                {
                    terrainsComboBox.removeAllItems();
                }

                // Repopulate terrains combo box
                World world = environment.getWorld();
                if (world.getNumberOfTerrains() > 0)
                {
                    Terrain aTerrain;
                    Enumeration eTerrains = world.getTerrains();
                    while (eTerrains.hasMoreElements())
                    {
                        aTerrain = (Terrain) eTerrains.nextElement();
                        terrainsComboBox.addItem(aTerrain.getName());
                    }

                    // Select the current terrain, if not null
                    if (currentTerrain != null)
                    {
                        terrainsComboBox.setSelectedItem(currentTerrain.getName());
                    }
                }

                terrainsComboBox.setVisible(true);
                terrainsComboBox.setEnabled(objectWriteable);

                // Calculate environment drawing parameters
                xNumCells = environment.getWidth();
                yNumCells = environment.getHeight();
                xDelta = widthEnvironment / xNumCells;
                yDelta = heightEnvironment / yNumCells;
                xEnvironmentRight = xEnvironmentLeft + (xDelta * xNumCells);
                yEnvironmentBottom = yEnvironmentTop + (yDelta * yNumCells);

                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof Organism)
            {
                paintingMode = PAINTING_ORGANISM;
                Organism organism = (Organism) object;
                nameTextField.setText(organism.getName());
                nameTextField.setVisible(true);
                nameTextField.setEnabled(true);
                visibleOrganismCheckBox.setVisible(true);
                visibleOrganismCheckBox.setSelected(organism.isVisible());
                visibleOrganismCheckBox.setEnabled(objectWriteable);
                visibleOrganismAllelesCheckBox.setVisible(true);
                visibleOrganismAllelesCheckBox.setSelected(organism.isAllelesVisible());
                visibleOrganismAllelesCheckBox.setEnabled(objectWriteable);
                visibleOrganismDNACheckBox.setVisible(true);
                visibleOrganismDNACheckBox.setSelected(organism.isDNAVisible());
                visibleOrganismDNACheckBox.setEnabled(objectWriteable);
                alterableOrganismAllelesCheckBox.setVisible(true);
                alterableOrganismAllelesCheckBox.setSelected(organism.isAllelesAlterable());
                alterableOrganismAllelesCheckBox.setEnabled(objectWriteable &&
                                                            organism.isAllelesVisible());
                alterableOrganismDNACheckBox.setVisible(true);
                alterableOrganismDNACheckBox.setSelected(organism.isDNAAlterable());
                alterableOrganismDNACheckBox.setEnabled(objectWriteable &&
                                                        organism.isDNAVisible());
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof SpeciesImage)
            {
                paintingMode = PAINTING_SPECIES_IMAGE;
                SpeciesImage speciesImage = (SpeciesImage) object;
                nameTextField.setVisible(true);
                nameTextField.setText(speciesImage.getName());
                nameTextField.setEnabled(objectWriteable);
                normalImageTypeRadioButton.setVisible(true);
                normalImageTypeRadioButton.setEnabled(true);
                hotspotImageTypeRadioButton.setVisible(true);
                hotspotImageTypeRadioButton.setEnabled(true);
                scopeImageTypeRadioButton.setVisible(true);
                scopeImageTypeRadioButton.setEnabled(true);
                invisibleImageTypeRadioButton.setVisible(true);
                invisibleImageTypeRadioButton.setEnabled(true);
                hotspotColorButton.setBackground(speciesImage.getHotspotColor());
                hotspotColorButton.setVisible(true);
                hotspotRadiusTextField.setText(Integer.toString(speciesImage.getHotspotRadius()));
                hotspotRadiusTextField.setVisible(true);
                switch(speciesImage.getImageType())
                {
                    case SpeciesImage.NORMAL_IMAGE_TYPE:
                        normalImageTypeRadioButton.setSelected(true);
                        hotspotImageTypeRadioButton.setSelected(false);
                        scopeImageTypeRadioButton.setSelected(false);
                        invisibleImageTypeRadioButton.setSelected(false);
                        hotspotColorButton.setEnabled(false);
                        hotspotRadiusTextField.setEnabled(false);
                        break;

                    case SpeciesImage.HOTSPOT_IMAGE_TYPE:
                        normalImageTypeRadioButton.setSelected(false);
                        hotspotImageTypeRadioButton.setSelected(true);
                        scopeImageTypeRadioButton.setSelected(false);
                        invisibleImageTypeRadioButton.setSelected(false);
                        hotspotColorButton.setEnabled(objectWriteable);
                        hotspotRadiusTextField.setEnabled(objectWriteable);
                        break;

                    case SpeciesImage.SCOPE_IMAGE_TYPE:
                        normalImageTypeRadioButton.setSelected(false);
                        hotspotImageTypeRadioButton.setSelected(false);
                        scopeImageTypeRadioButton.setSelected(true);
                        invisibleImageTypeRadioButton.setSelected(false);
                        hotspotColorButton.setEnabled(false);
                        hotspotRadiusTextField.setEnabled(false);
                        break;

                    case SpeciesImage.INVISIBLE_IMAGE_TYPE:
                        normalImageTypeRadioButton.setSelected(false);
                        hotspotImageTypeRadioButton.setSelected(false);
                        scopeImageTypeRadioButton.setSelected(false);
                        invisibleImageTypeRadioButton.setSelected(true);
                        hotspotColorButton.setEnabled(false);
                        hotspotRadiusTextField.setEnabled(false);
                        break;
                }
                createSpeciesImageColumnButton.setVisible(objectWriteable);
                createSpeciesImageRowButton.setVisible(objectWriteable);
                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof SpeciesImageColumn)
            {
                paintingMode = PAINTING_SPECIES_IMAGE_COLUMN;
                SpeciesImageColumn speciesImageColumn = (SpeciesImageColumn) object;
                Species species = speciesImageColumn.getSpecies();
                
                // Set name field
                nameTextField.setVisible(true);
                nameTextField.setText(speciesImageColumn.getName());
                nameTextField.setEnabled(objectWriteable);

                // Set gender combo box
                if (species.getPloidyNumber() == 2)
                {
                    genderComboBox.setVisible(true);
                    genderComboBox.setEnabled(objectWriteable);
                    int gender = speciesImageColumn.getGender();
                    if (gender == Species.FEMALE_ONLY)
                    {
                        genderComboBox.setSelectedItem("Female Only");
                    }
                    else if (gender == Species.MALE_ONLY)
                    {
                        genderComboBox.setSelectedItem("Male Only");
                    }
                    else
                    {
                        genderComboBox.setSelectedItem("Female and Male");
                    }
                }
                else
                {
                    genderComboBox.setVisible(true);
                    genderComboBox.setEnabled(false);
                    genderComboBox.setSelectedItem("Female and Male");
                }

                // Clear characteristics combo boxes,
                // make them visible and add one item "--"
                for (i=0;i<characteristicsComboBox.length;i++)
                {
                    if (characteristicsComboBox[i].getItemCount() > 0)
                    {
                        characteristicsComboBox[i].removeAllItems();
                    }

                    characteristicsComboBox[i].addItem("--");
                    characteristicsComboBox[i].setVisible(true);
                    characteristicsComboBox[i].setEnabled(objectWriteable);
                }

                // Create currentCharacteristics vector and
                // add all Characteristics to all combo boxes
                Characteristic aCharacteristic;
                currentCharacteristics = species.getCharacteristicsVector();
                Enumeration eCharacteristics= currentCharacteristics.elements();
                while (eCharacteristics.hasMoreElements())
                {
                    aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                    for (i=0;i<characteristicsComboBox.length;i++)
                    {
                        characteristicsComboBox[i].addItem(aCharacteristic.getName());
                    }
                }

                // Select the current characteristic in each combo box if not null
                eCharacteristics = speciesImageColumn.getCharacteristics();
                for (i=0;i<characteristicsComboBox.length;i++)
                {
                    if (eCharacteristics.hasMoreElements())
                    {
                        aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                        characteristicsComboBox[i].setSelectedItem(aCharacteristic.getName());
                        characteristics[i] = aCharacteristic;
                    }
                    else
                    {
                        characteristicsComboBox[i].setSelectedItem("--");
                        characteristics[i] = null;
                    }
                }

                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof SpeciesImageRow)
            {
                paintingMode = PAINTING_SPECIES_IMAGE_ROW;
                SpeciesImageRow speciesImageRow = (SpeciesImageRow) object;
                Species species = speciesImageRow.getSpecies();

                // Set name field
                nameTextField.setVisible(true);
                nameTextField.setText(speciesImageRow.getName());
                nameTextField.setEnabled(objectWriteable);

                // Set gender combo box
                if (species.getPloidyNumber() == 2)
                {
                    genderComboBox.setVisible(true);
                    genderComboBox.setEnabled(objectWriteable);
                    int gender = speciesImageRow.getGender();
                    if (gender == Species.FEMALE_ONLY)
                    {
                        genderComboBox.setSelectedItem("Female Only");
                    }
                    else if (gender == Species.MALE_ONLY)
                    {
                        genderComboBox.setSelectedItem("Male Only");
                    }
                    else
                    {
                        genderComboBox.setSelectedItem("Female and Male");
                    }
                }
                else
                {
                    genderComboBox.setVisible(true);
                    genderComboBox.setEnabled(false);
                    genderComboBox.setSelectedItem("Female and Male");
                }

                // Clear characteristics combo boxes and make them visible with one item "--"
                if (currentCharacteristics != null)
                {
                    currentCharacteristics.removeAllElements();
                }
                else
                {
                    currentCharacteristics = new Vector();
                }

                for (i=0;i<characteristicsComboBox.length;i++)
                {
                    if (characteristicsComboBox[i].getItemCount() > 0)
                    {
                        characteristicsComboBox[i].removeAllItems();
                    }

                    characteristicsComboBox[i].addItem("--");
                    characteristicsComboBox[i].setVisible(true);
                    characteristicsComboBox[i].setEnabled(objectWriteable);
                }

                // Add all Characteristics to all combo boxes
                if (species.getNumberOfTraits() > 0)
                {
                    Characteristic aCharacteristic;
                    Enumeration eCharacteristics;
                    Trait aTrait;
                    Enumeration eTraits = species.getTraits();
                    while (eTraits.hasMoreElements())
                    {
                        aTrait = (Trait) eTraits.nextElement();
                        eCharacteristics = aTrait.getCharacteristics();
                        while (eCharacteristics.hasMoreElements())
                        {
                            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                            for (i=0;i<characteristicsComboBox.length;i++)
                            {
                                characteristicsComboBox[i].addItem(aCharacteristic.getName());
                                currentCharacteristics.addElement(aCharacteristic);
                            }
                        }
                    }

                    // Select the current characteristic if not null
                    eCharacteristics = speciesImageRow.getCharacteristics();
                    for (i=0;i<characteristicsComboBox.length;i++)
                    {
                        if (eCharacteristics.hasMoreElements())
                        {
                            aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                            characteristicsComboBox[i].setSelectedItem(aCharacteristic.getName());
                            characteristics[i] = aCharacteristic;
                        }
                        else
                        {
                            characteristicsComboBox[i].setSelectedItem("--");
                            characteristics[i] = null;
                        }
                    }
                }

                // Set hotspot location text field values
                {
                    Point hotspot = speciesImageRow.getXXSmallHotspot();
                    xxSmallDimensionOneTextField.setVisible(true);
                    xxSmallDimensionOneTextField.setText(Integer.toString(hotspot.x));
                    xxSmallDimensionOneTextField.setEnabled(objectWriteable);
                    xxSmallDimensionTwoTextField.setVisible(true);
                    xxSmallDimensionTwoTextField.setText(Integer.toString(hotspot.y));
                    xxSmallDimensionTwoTextField.setEnabled(objectWriteable);

                    hotspot = speciesImageRow.getXSmallHotspot();
                    xSmallDimensionOneTextField.setVisible(true);
                    xSmallDimensionOneTextField.setText(Integer.toString(hotspot.x));
                    xSmallDimensionOneTextField.setEnabled(objectWriteable);
                    xSmallDimensionTwoTextField.setVisible(true);
                    xSmallDimensionTwoTextField.setText(Integer.toString(hotspot.y));
                    xSmallDimensionTwoTextField.setEnabled(objectWriteable);

                    hotspot = speciesImageRow.getSmallHotspot();
                    smallDimensionOneTextField.setVisible(true);
                    smallDimensionOneTextField.setText(Integer.toString(hotspot.x));
                    smallDimensionOneTextField.setEnabled(objectWriteable);
                    smallDimensionTwoTextField.setVisible(true);
                    smallDimensionTwoTextField.setText(Integer.toString(hotspot.y));
                    smallDimensionTwoTextField.setEnabled(objectWriteable);

                    hotspot = speciesImageRow.getMediumHotspot();
                    mediumDimensionOneTextField.setVisible(true);
                    mediumDimensionOneTextField.setText(Integer.toString(hotspot.x));
                    mediumDimensionOneTextField.setEnabled(objectWriteable);
                    mediumDimensionTwoTextField.setVisible(true);
                    mediumDimensionTwoTextField.setText(Integer.toString(hotspot.y));
                    mediumDimensionTwoTextField.setEnabled(objectWriteable);

                    hotspot = speciesImageRow.getLargeHotspot();
                    largeDimensionOneTextField.setVisible(true);
                    largeDimensionOneTextField.setText(Integer.toString(hotspot.x));
                    largeDimensionOneTextField.setEnabled(objectWriteable);
                    largeDimensionTwoTextField.setVisible(true);
                    largeDimensionTwoTextField.setText(Integer.toString(hotspot.y));
                    largeDimensionTwoTextField.setEnabled(objectWriteable);

                    hotspot = speciesImageRow.getXLargeHotspot();
                    xLargeDimensionOneTextField.setVisible(true);
                    xLargeDimensionOneTextField.setText(Integer.toString(hotspot.x));
                    xLargeDimensionOneTextField.setEnabled(objectWriteable);
                    xLargeDimensionTwoTextField.setVisible(true);
                    xLargeDimensionTwoTextField.setText(Integer.toString(hotspot.y));
                    xLargeDimensionTwoTextField.setEnabled(objectWriteable);
                }

                applyButton.setVisible(objectWriteable);
            }
            else if (object instanceof OrganismChromosomePair)
            {
                paintingMode = PAINTING_ORGANISM_CHROMOSOME_PAIR;
                OrganismChromosomePair organismChromosomePair = (OrganismChromosomePair) object;

                chromosomeView.setChromosomesToShow(organismChromosomePair.getChromosomePairNumberType());
                chromosomeView.setOrganism(organismChromosomePair.getOrganism());
                chromosomeView.setVisible(true);
                applyButton.setVisible(false);
            }
            else if (object instanceof OrganismAllelePair)
            {
                paintingMode = PAINTING_ORGANISM_ALLELE_PAIR;
                OrganismAllelePair organismAllelePair = (OrganismAllelePair) object;
                OrganismChromosomePair organismChromosomePair = organismAllelePair.getOrganismChromosomePair();

                chromosomeView.setChromosomesToShow(organismChromosomePair.getChromosomePairNumberType());
                chromosomeView.setOrganism(organismChromosomePair.getOrganism());
                chromosomeView.setVisible(true);
                
                applyButton.setVisible(false);
            }
        }

        // Switch telling other code that we're updating state
        updatingState = false;

        // Finally, queue a repaint
        revalidate();
        repaint();
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.DELETED))
        {
            Object sourceObject = event.getSource();
            if (sourceObject == object)
            {
                // Current object is being deleted, so forget it
                setObject(null);
            }
            else if (sourceObject instanceof Terrain &&
                     sourceObject == currentTerrain)
            {
                // Current terrain is being deleted, so forget it
                setCurrentTerrain(null);
            }
        }
        else
        {
            updateState();
        }
    }

    /**
     * Handle combo box item changed events.
    **/
    public void itemStateChanged(ItemEvent event)
    {
        // Ignore event if we're updating state
        if (updatingState == true)
        {
            return;
        }

        Object sourceObject = event.getSource();
        if (sourceObject instanceof BioComboBox)
        {
            BioComboBox comboBox = (BioComboBox) sourceObject;
            Object selectedObject = comboBox.getSelectedItem();
            if (selectedObject instanceof String)
            {
                if (comboBox == terrainsComboBox &&
                    object instanceof Environment)
                {
                    String newTerrainName = (String) selectedObject;
    
                    // Determine which terrain was selected
                    Terrain aTerrain;
                    Environment environment = (Environment) object;
                    World world = environment.getWorld();
                    Enumeration eTerrains = world.getTerrains();
                    while (eTerrains.hasMoreElements())
                    {
                        aTerrain = (Terrain) eTerrains.nextElement();
                        if (aTerrain.getName().equals(newTerrainName))
                        {
                            // Set current terrain in this view
                            setCurrentTerrain(aTerrain);
                            break;
                        }
                    }
                }
                else if (comboBox == genderComboBox)
                {
                    int gender;
                    String genderName = (String) selectedObject;
                    if (genderName.equals("Female Only"))
                    {
                        gender = Species.FEMALE_ONLY;
                    }
                    else if (genderName.equals("Male Only"))
                    {
                        gender = Species.MALE_ONLY;
                    }
                    else
                    {
                        gender = Species.FEMALE_AND_MALE;
                    }
                    if (object instanceof GenotypeToPhenotypeRule)
                    {
                        ((GenotypeToPhenotypeRule)object).setGender(gender);
                    }
                    else if (object instanceof SpeciesImageColumn)
                    {
                        ((SpeciesImageColumn)object).setGender(gender);
                    }
                    else if (object instanceof SpeciesImageRow)
                    {
                        ((SpeciesImageRow)object).setGender(gender);
                    }
                }
                else if (comboBox == thenCharacteristicComboBox ||
                         comboBox == elseCharacteristicComboBox)
                {
                    String characteristicName = (String) selectedObject;
                    Characteristic aCharacteristic;
                    Characteristic newlySelectedCharacteristic = null;    // null is okay
                    Enumeration eCharacteristics = currentCharacteristics.elements();
                    while (eCharacteristics.hasMoreElements())
                    {
                        aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                        if (aCharacteristic.getName().equals(characteristicName))
                        {
                            // Found it - break out of loop
                            newlySelectedCharacteristic = aCharacteristic;
                            break;
                        }
                    }
                    
                    // Replace then or else characteristic in GenotypeToPhenotypeRule
                    if (object instanceof GenotypeToPhenotypeRule)
                    {
                        GenotypeToPhenotypeRule genotypeToPhenotypeRule = (GenotypeToPhenotypeRule) object;
                        if (comboBox == thenCharacteristicComboBox)
                        {
                            thenCharacteristic = newlySelectedCharacteristic;
                            genotypeToPhenotypeRule.setThenCharacteristic(newlySelectedCharacteristic);
                        }
                        else
                        {
                            elseCharacteristic = newlySelectedCharacteristic;
                            genotypeToPhenotypeRule.setElseCharacteristic(newlySelectedCharacteristic);
                        }
                    }
                }
                else
                {
                    // May be a characteristics combo box - find out
                    SpeciesImageRow speciesImageRow = null;
                    SpeciesImageColumn speciesImageColumn = null;
                    GenotypeToPhenotypeRule genotypeToPhenotypeRule = null;
                    if (object instanceof SpeciesImageRow)
                    {
                        speciesImageRow = (SpeciesImageRow) object;
                    }
                    else if (object instanceof SpeciesImageColumn)
                    {
                        speciesImageColumn = (SpeciesImageColumn) object;
                    }
                    else if (object instanceof GenotypeToPhenotypeRule)
                    {
                        genotypeToPhenotypeRule = (GenotypeToPhenotypeRule) object;
                    }

                    if (speciesImageRow != null || speciesImageColumn != null)
                    {
                        int i;
                        String characteristicName;
                        Characteristic aCharacteristic;
                        Characteristic oldSelectedCharacteristic;
                        Characteristic newlySelectedCharacteristic;
                        Enumeration eCharacteristics;

                        for (i=0;i<characteristicsComboBox.length;i++)
                        {
                            if (comboBox == characteristicsComboBox[i])
                            {
                                // Find Characteristic with selected name
                                characteristicName = (String) selectedObject;
                                oldSelectedCharacteristic = characteristics[i];
                                newlySelectedCharacteristic = null;    // null is okay
                                eCharacteristics = currentCharacteristics.elements();
                                while (eCharacteristics.hasMoreElements())
                                {
                                    aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                                    if (aCharacteristic.getName().equals(characteristicName))
                                    {
                                        // Found it - break out of loop
                                        newlySelectedCharacteristic = aCharacteristic;
                                        break;
                                    }
                                }
                                
                                // Replace characteristic in SpeciesImageRow
                                if (speciesImageRow != null)
                                {
                                    characteristics[i] = newlySelectedCharacteristic;
                                    speciesImageRow.replaceCharacteristic(oldSelectedCharacteristic,
                                                                          newlySelectedCharacteristic);
                                }
                                else if (speciesImageColumn != null)
                                {
                                    characteristics[i] = newlySelectedCharacteristic;
                                    speciesImageColumn.replaceCharacteristic(oldSelectedCharacteristic,
                                                                             newlySelectedCharacteristic);
                                                                                    
                                }
                                break;
                            }
                        }
                    }
                    else if (genotypeToPhenotypeRule != null)
                    {
                        int i;
                        String ifSpeciesAlleleName;
                        SpeciesAllele aSpeciesAllele;
                        SpeciesAllele oldSelectedIfSpeciesAllele;
                        SpeciesAllele newlySelectedIfSpeciesAllele;
                        Enumeration eSpeciesAlleles;

                        for (i=0;i<ifSpeciesAllelesComboBox.length;i++)
                        {
                            if (comboBox == ifSpeciesAllelesComboBox[i])
                            {
                                // Find SpeciesAllele with selected name
                                ifSpeciesAlleleName = (String) selectedObject;
                                oldSelectedIfSpeciesAllele = ifSpeciesAlleles[i];
                                newlySelectedIfSpeciesAllele = null;    // null is okay
                                eSpeciesAlleles = currentSpeciesAlleles.elements();
                                while (eSpeciesAlleles.hasMoreElements())
                                {
                                    aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
                                    if (aSpeciesAllele.getTextSymbol().equals(ifSpeciesAlleleName))
                                    {
                                        // Found it - break out of loop
                                        newlySelectedIfSpeciesAllele = aSpeciesAllele;
                                        break;
                                    }
                                }
                                
                                // Replace ifSpeciesAllele in selectedGenotypeToPhenotypeRule
                                if (genotypeToPhenotypeRule != null)
                                {
                                    ifSpeciesAlleles[i] = newlySelectedIfSpeciesAllele;
                                    genotypeToPhenotypeRule.replaceIfSpeciesAllele(oldSelectedIfSpeciesAllele,
                                                                                   newlySelectedIfSpeciesAllele);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        else if (sourceObject instanceof JCheckBox)
        {
            JCheckBox checkBox = (JCheckBox) sourceObject;

            if (checkBox == visibleChromosomeCheckBox &&
                object instanceof SpeciesChromosome)
            {
                if (event.getStateChange() == ItemEvent.SELECTED)
                {
                    ((SpeciesChromosome)object).setVisible(true);
                }
                else
                {
                    ((SpeciesChromosome)object).setVisible(false);
                }
            }
            else if (checkBox == visibleGeneCheckBox &&
                     object instanceof Gene)
            {
                if (event.getStateChange() == ItemEvent.SELECTED)
                {
                    ((Gene)object).setVisible(true);
                }
                else
                {
                    ((Gene)object).setVisible(false);
                }
            }
            else if (checkBox == mutationAlleleCheckBox &&
                     object instanceof SpeciesAllele)
            {
                if (event.getStateChange() == ItemEvent.SELECTED)
                {
                    ((SpeciesAllele)object).setMutationAllele(true);
                }
                else
                {
                    ((SpeciesAllele)object).setMutationAllele(false);
                }
            }
            else if (checkBox == visibleAlleleCheckBox &&
                     object instanceof SpeciesAllele)
            {
                if (event.getStateChange() == ItemEvent.SELECTED)
                {
                    ((SpeciesAllele)object).setVisible(true);
                }
                else
                {
                    ((SpeciesAllele)object).setVisible(false);
                }
            }
            else if (object instanceof Organism)
            {
                Organism organism = (Organism) object;
                if (checkBox == visibleOrganismCheckBox)
                {
                    if (event.getStateChange() == ItemEvent.SELECTED)
                    {
                        organism.setVisible(true);
                    }
                    else
                    {
                        organism.setVisible(false);
                    }
                }
                else if (checkBox == visibleOrganismAllelesCheckBox)
                {
                    if (event.getStateChange() == ItemEvent.SELECTED)
                    {
                        organism.setAllelesVisible(true);
                    }
                    else
                    {
                        organism.setAllelesVisible(false);
                    }
                }
                else if (checkBox == visibleOrganismDNACheckBox)
                {
                    if (event.getStateChange() == ItemEvent.SELECTED)
                    {
                        organism.setDNAVisible(true);
                    }
                    else
                    {
                        organism.setDNAVisible(false);
                    }
                }
                else if (checkBox == alterableOrganismAllelesCheckBox)
                {
                    if (event.getStateChange() == ItemEvent.SELECTED)
                    {
                        organism.setAllelesAlterable(true);
                    }
                    else
                    {
                        organism.setAllelesAlterable(false);
                    }
                }
                else if (checkBox == alterableOrganismDNACheckBox)
                {
                    if (event.getStateChange() == ItemEvent.SELECTED)
                    {
                        organism.setDNAAlterable(true);
                    }
                    else
                    {
                        organism.setDNAAlterable(false);
                    }
                }
            }
            else if (checkBox == showTraitAsTextCheckBox &&
                     object instanceof Trait)
            {
                if (event.getStateChange() == ItemEvent.SELECTED)
                {
                    ((Trait)object).setShowAsTextInOrganismView(true);
                }
                else
                {
                    ((Trait)object).setShowAsTextInOrganismView(false);
                }
            }
            else if (checkBox == fatalCharacteristicCheckBox &&
                     object instanceof Characteristic)
            {
                if (event.getStateChange() == ItemEvent.SELECTED)
                {
                    ((Characteristic)object).setFatal(true);
                }
                else
                {
                    ((Characteristic)object).setFatal(false);
                }
            }
        }

        // No need to call updateState(), as setting state in engine objects
        // should automatically cause that to occur
    }

    /**
     * React to actions
    **/
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if (cmd.equals(cmdApply))
        {
            // Do nothing, as hitting this button caused focus to change (pretty slick, eh?)
        }
        else if (cmd.equals(cmdMoveUp))
        {
            GenotypeToPhenotypeRule aRule = (GenotypeToPhenotypeRule)object;
            Species aSpecies = aRule.getSpecies();
            aSpecies.moveGenotypeToPhenotypeRuleUp(aRule);
        }
        else if (cmd.equals(cmdMoveDown))
        {
            GenotypeToPhenotypeRule aRule = (GenotypeToPhenotypeRule)object;
            Species aSpecies = aRule.getSpecies();
            aSpecies.moveGenotypeToPhenotypeRuleDown(aRule);
        }
        else if (cmd.equals(cmdCreateSpecies))
        {
            Species species = new Species((World)object,2,"?");
        }
        else if (cmd.equals(cmdCreateSpeciesChromosome))
        {
            SpeciesChromosome sc = new SpeciesChromosome((Species)object,
                                                         IChromosome.NEXT_HIGHEST_AUTOSOME_NUMBER,
                                                         300);
        }
        else if (cmd.equals(cmdCreateTrait))
        {
            Trait trait = new Trait((Species)object,"?");
        }
        else if (cmd.equals(cmdCreateRule))
        {
            GenotypeToPhenotypeRule rule =
                    new GenotypeToPhenotypeRule((Species)object,"?");
        }
        else if (cmd.equals(cmdCreateGene))
        {
            Gene gene = new Gene((SpeciesChromosome)object, "?", 0, 1);
        }
        else if (cmd.equals(cmdCreateSpeciesAllele))
        {
            int length = ((Gene)object).getLengthInBases();
            byte baseValues[] = new byte[length];
            int i;
            for (i=0;i<length;i++)
            {
                baseValues[i] = Base.ADENINE;
            }

            SpeciesAllele sa = new SpeciesAllele((Gene)object, "?", baseValues);
        }
        else if (cmd.equals(cmdCreateCharacteristic))
        {
            Characteristic c = new Characteristic((Trait)object,"?",0);
        }
        else if (cmd.equals(cmdCreateTerrain))
        {
            Terrain terrain = new Terrain((World)object, "?", Color.black);
        }
        else if (cmd.equals(cmdCreateEnvironment))
        {
            Environment environment = new Environment((World)object, "?", 10, 10);
        }
        else if (cmd.equals(cmdHaploid))
        {
            ((Species)object).setPloidyNumber(1);
            diploidXXFemaleXYMaleRadioButton.setSelected(false);
            diploidXYFemaleXXMaleRadioButton.setSelected(false);
            diploidNoSexChromosomesRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdDiploidXXFemaleXYMale))
        {
            ((Species)object).setPloidyNumber(2);
            ((Species)object).setDiploidType(Species.DIPLOID_TYPE_XX_FEMALE_XY_MALE);
            haploidRadioButton.setSelected(false);
            diploidXYFemaleXXMaleRadioButton.setSelected(false);
            diploidNoSexChromosomesRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdDiploidXYFemaleXXMale))
        {
            ((Species)object).setPloidyNumber(2);
            ((Species)object).setDiploidType(Species.DIPLOID_TYPE_XY_FEMALE_XX_MALE);
            haploidRadioButton.setSelected(false);
            diploidXXFemaleXYMaleRadioButton.setSelected(false);
            diploidNoSexChromosomesRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdDiploidNoSexChromosomes))
        {
            ((Species)object).setPloidyNumber(2);
            ((Species)object).setDiploidType(Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES);
            haploidRadioButton.setSelected(false);
            diploidXXFemaleXYMaleRadioButton.setSelected(false);
            diploidXYFemaleXXMaleRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdImageTypeNormal))
        {
            ((SpeciesImage)object).setImageType(SpeciesImage.NORMAL_IMAGE_TYPE);
            hotspotImageTypeRadioButton.setSelected(false);
            scopeImageTypeRadioButton.setSelected(false);
            invisibleImageTypeRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdImageTypeHotspot))
        {
            ((SpeciesImage)object).setImageType(SpeciesImage.HOTSPOT_IMAGE_TYPE);
            normalImageTypeRadioButton.setSelected(false);
            scopeImageTypeRadioButton.setSelected(false);
            invisibleImageTypeRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdImageTypeScope))
        {
            ((SpeciesImage)object).setImageType(SpeciesImage.SCOPE_IMAGE_TYPE);
            normalImageTypeRadioButton.setSelected(false);
            hotspotImageTypeRadioButton.setSelected(false);
            invisibleImageTypeRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdImageTypeInvisible))
        {
            ((SpeciesImage)object).setImageType(SpeciesImage.INVISIBLE_IMAGE_TYPE);
            normalImageTypeRadioButton.setSelected(false);
            hotspotImageTypeRadioButton.setSelected(false);
            scopeImageTypeRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdChangeTerrainColor))
        {
            Color newColor = JColorChooser.showDialog(this,
                                                      "Change Terrain Color",
                                                      ((Terrain)object).getColor());
            if (newColor != null)
            {
                ((Terrain)object).setColor(newColor);
                updateState();
            }
        }
        else if (cmd.equals(cmdChangeHotspotColor))
        {
            Color newColor = JColorChooser.showDialog(this,
                                                      "Change Hotspot Color",
                                                      ((SpeciesImage)object).getHotspotColor());
            if (newColor != null)
            {
                ((SpeciesImage)object).setHotspotColor(newColor);
                updateState();
            }
        }
        else if (cmd.equals(cmdChangePedigreeSymbolFirstColor))
        {
            Color newColor = JColorChooser.showDialog(this,
                                                      "Change Pedigree Symbol First Color",
                                                      ((Characteristic)object).getPedigreeSymbolFirstColor());
            if (newColor != null)
            {
                ((Characteristic)object).setPedigreeSymbolFirstColor(newColor);
                updateState();
            }
        }
        else if (cmd.equals(cmdChangePedigreeSymbolSecondColor))
        {
            Color newColor = JColorChooser.showDialog(this,
                                                      "Change Pedigree Symbol Second Color",
                                                      ((Characteristic)object).getPedigreeSymbolSecondColor());
            if (newColor != null)
            {
                ((Characteristic)object).setPedigreeSymbolSecondColor(newColor);
                updateState();
            }
        }
        else if (cmd.equals(cmdPedigreeSymbolSolid))
        {
            ((Characteristic)object).setPedigreeSymbolType(Characteristic.PEDIGREE_SYMBOL_SOLID_COLOR);
            pedigreeSymbolForwardSlashRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdPedigreeSymbolForwardSlash))
        {
            ((Characteristic)object).setPedigreeSymbolType(Characteristic.PEDIGREE_SYMBOL_FORWARD_SLASH);
            pedigreeSymbolSolidRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdBottomStrand))
        {
            ((Gene)object).setStrand(Gene.BOTTOM_STRAND);
            bottomStrandGeneRadioButton.setSelected(true);
            topStrandGeneRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdTopStrand))
        {
            ((Gene)object).setStrand(Gene.TOP_STRAND);
            bottomStrandGeneRadioButton.setSelected(false);
            topStrandGeneRadioButton.setSelected(true);
        }
        else if (cmd.equals(cmdCreateSpeciesImage))
        {
            SpeciesImage si = new SpeciesImage(((Species)object), "?");
        }
        else if (cmd.equals(cmdCreateSpeciesImageColumn))
        {
            SpeciesImageColumn sic = new SpeciesImageColumn(((SpeciesImage)object),"?");
        }
        else if (cmd.equals(cmdCreateSpeciesImageRow))
        {
            SpeciesImageRow sir = new SpeciesImageRow(((SpeciesImage)object),"?");
        }
        else if (cmd.equals(cmdChromosomeImageNumber[0]))
        {
            chromosomeImageNumberRadioButtons[1].setSelected(false);
            chromosomeImageNumberRadioButtons[2].setSelected(false);
            chromosomeImageNumberRadioButtons[3].setSelected(false);
            chromosomeImageNumberRadioButtons[4].setSelected(false);
            chromosomeImageNumberRadioButtons[5].setSelected(false);
            chromosomeImageNumberRadioButtons[6].setSelected(false);
            chromosomeImageNumberRadioButtons[7].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(0);
        }
        else if (cmd.equals(cmdChromosomeImageNumber[1]))
        {
            chromosomeImageNumberRadioButtons[0].setSelected(false);
            chromosomeImageNumberRadioButtons[2].setSelected(false);
            chromosomeImageNumberRadioButtons[3].setSelected(false);
            chromosomeImageNumberRadioButtons[4].setSelected(false);
            chromosomeImageNumberRadioButtons[5].setSelected(false);
            chromosomeImageNumberRadioButtons[6].setSelected(false);
            chromosomeImageNumberRadioButtons[7].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(1);
        }
        else if (cmd.equals(cmdChromosomeImageNumber[2]))
        {
            chromosomeImageNumberRadioButtons[0].setSelected(false);
            chromosomeImageNumberRadioButtons[1].setSelected(false);
            chromosomeImageNumberRadioButtons[3].setSelected(false);
            chromosomeImageNumberRadioButtons[4].setSelected(false);
            chromosomeImageNumberRadioButtons[5].setSelected(false);
            chromosomeImageNumberRadioButtons[6].setSelected(false);
            chromosomeImageNumberRadioButtons[7].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(2);
        }
        else if (cmd.equals(cmdChromosomeImageNumber[3]))
        {
            chromosomeImageNumberRadioButtons[0].setSelected(false);
            chromosomeImageNumberRadioButtons[1].setSelected(false);
            chromosomeImageNumberRadioButtons[2].setSelected(false);
            chromosomeImageNumberRadioButtons[4].setSelected(false);
            chromosomeImageNumberRadioButtons[5].setSelected(false);
            chromosomeImageNumberRadioButtons[6].setSelected(false);
            chromosomeImageNumberRadioButtons[7].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(3);
        }
        else if (cmd.equals(cmdChromosomeImageNumber[4]))
        {
            chromosomeImageNumberRadioButtons[0].setSelected(false);
            chromosomeImageNumberRadioButtons[1].setSelected(false);
            chromosomeImageNumberRadioButtons[2].setSelected(false);
            chromosomeImageNumberRadioButtons[3].setSelected(false);
            chromosomeImageNumberRadioButtons[5].setSelected(false);
            chromosomeImageNumberRadioButtons[6].setSelected(false);
            chromosomeImageNumberRadioButtons[7].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(4);
        }
        else if (cmd.equals(cmdChromosomeImageNumber[5]))
        {
            chromosomeImageNumberRadioButtons[0].setSelected(false);
            chromosomeImageNumberRadioButtons[1].setSelected(false);
            chromosomeImageNumberRadioButtons[2].setSelected(false);
            chromosomeImageNumberRadioButtons[3].setSelected(false);
            chromosomeImageNumberRadioButtons[4].setSelected(false);
            chromosomeImageNumberRadioButtons[6].setSelected(false);
            chromosomeImageNumberRadioButtons[7].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(5);
        }
        else if (cmd.equals(cmdChromosomeImageNumber[6]))
        {
            chromosomeImageNumberRadioButtons[0].setSelected(false);
            chromosomeImageNumberRadioButtons[1].setSelected(false);
            chromosomeImageNumberRadioButtons[2].setSelected(false);
            chromosomeImageNumberRadioButtons[3].setSelected(false);
            chromosomeImageNumberRadioButtons[4].setSelected(false);
            chromosomeImageNumberRadioButtons[5].setSelected(false);
            chromosomeImageNumberRadioButtons[7].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(6);
        }
        else if (cmd.equals(cmdChromosomeImageNumber[7]))
        {
            chromosomeImageNumberRadioButtons[0].setSelected(false);
            chromosomeImageNumberRadioButtons[1].setSelected(false);
            chromosomeImageNumberRadioButtons[2].setSelected(false);
            chromosomeImageNumberRadioButtons[3].setSelected(false);
            chromosomeImageNumberRadioButtons[4].setSelected(false);
            chromosomeImageNumberRadioButtons[5].setSelected(false);
            chromosomeImageNumberRadioButtons[6].setSelected(false);
            ((SpeciesChromosome)object).setImageNumber(7);
        }
    }

    /**
     * Handle focus gained event - do nothing in most cases.
     *
     * @param		event FocusEvent - focus gained event
    **/
    public void focusGained(FocusEvent event)
    {
        focusControl = event.getSource();
    }

    /**
     * Handle focus lost event - check text in most cases.
     *
     * @param		event FocusEvent - focus lost event
    **/
    public void focusLost(FocusEvent event)
    {
        Object focusObject = event.getSource();

        if (focusObject instanceof JTextField)
        {
            JTextField textField = (JTextField) event.getSource();

            processPendingTextEdit(textField);
        }
        else if (focusObject instanceof JTextArea)
        {
            JTextArea textArea = (JTextArea) event.getSource();

            processPendingTextEdit(textArea);
        }

        // No control has focus temporarily
        focusControl = null;
    }

    /**
     * Process pending text edit control modifications before allowing
     * something else to occur in the user interface.
    **/
    public void processPendingTextEdits()
    {
        if (focusControl != null)
        {
            if (focusControl instanceof JTextField)
            {
                JTextField textField = (JTextField) focusControl;
                processPendingTextEdit(textField);
            }
            else if (focusControl instanceof JTextArea)
            {
                JTextArea textArea = (JTextArea) focusControl;
                processPendingTextEdit(textArea);
            }
            // Null out focus control to avoid reprocessing text in it
            focusControl = null;
        }

    }

    /**
     * Process pending text edit control modifications.  This is called
     * by focusLost() for normal focus lost processing and
     * by processingPendingTextEdit() when there is a pending text edit.<p>
     *
     * @param		textField JTextField
    **/
    private void processPendingTextEdit(JTextField textField)
    {
        if (textField != null && object != null)
        {
            String newText = textField.getText();
    
            if (textField == nameTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    ((Species)object).setName(newText);
                }
                else if (paintingMode == PAINTING_GENE)
                {
                    ((Gene)object).setName(newText);
                }
                else if (paintingMode == PAINTING_SPECIES_ALLELE)
                {
                    ((SpeciesAllele)object).setTextSymbol(newText);
                }
                else if (paintingMode == PAINTING_TRAIT)
                {
                    ((Trait)object).setName(newText);
                }
                else if (paintingMode == PAINTING_CHARACTERISTIC)
                {
                    ((Characteristic)object).setName(newText);
                }
                else if (paintingMode == PAINTING_GENOTYPE_TO_PHENOTYPE_RULE)
                {
                    ((GenotypeToPhenotypeRule)object).setName(newText);
                }
                else if (paintingMode == PAINTING_ENVIRONMENT)
                {
                    ((Environment)object).setName(newText);
                }
                else if (paintingMode == PAINTING_ORGANISM)
                {
                    ((Organism)object).setName(newText);
                }
                else if (paintingMode == PAINTING_TERRAIN)
                {
                    ((Terrain)object).setName(newText);
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE)
                {
                    ((SpeciesImage)object).setName(newText);
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_COLUMN)
                {
                    ((SpeciesImageColumn)object).setName(newText);
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    ((SpeciesImageRow)object).setName(newText);
                }
            }
            else if (textField == numberTypeTextField)
            {
                if (paintingMode == PAINTING_SPECIES_CHROMOSOME)
                {
                    try
                    {
                        int numberType = SpeciesChromosome.convertStringToChromosomeNumberType(newText);
                        ((SpeciesChromosome)object).setNumberType(numberType);
                    }
                    catch (NumberFormatException exception)
                    {
                        String numberTypeString = SpeciesChromosome.convertChromosomeNumberTypeToString(((SpeciesChromosome)object).getNumberType());
                        numberTypeTextField.setText(numberTypeString);
                    }
                }
            }
            else if (textField == lengthTextField)
            {
                if (paintingMode == PAINTING_SPECIES_CHROMOSOME)
                {
                    try
                    {
                        int newLength = Integer.parseInt(newText);
                        ((SpeciesChromosome)object).setLengthInBases(newLength);
                    }
                    catch (NumberFormatException exception2)
                    {
                        lengthTextField.setText(Integer.toString(((SpeciesChromosome)object).getLengthInBases()));
                    }
                }
            }
            else if (textField == geneLengthTextField)
            {
                if (paintingMode == PAINTING_GENE)
                {
                    try
                    {
                        int newLength = Integer.parseInt(newText);
                        ((Gene)object).setLengthInBases(newLength);
                    }
                    catch (NumberFormatException exception2)
                    {
                        geneLengthTextField.setText(Integer.toString(((Gene)object).getLengthInBases()));
                    }
                }
            }
            else if (textField == startTextField)
            {
                if (paintingMode == PAINTING_GENE)
                {
                    try
                    {
                        int newStart = Integer.parseInt(newText);
                        ((Gene)object).setStartIndexInHolder(newStart);
                    }
                    catch (NumberFormatException exception2)
                    {
                        startTextField.setText(Integer.toString(((Gene)object).getStartIndexInHolder()));
                    }
                }
            }
            else if (textField == weightTextField)
            {
                if (paintingMode == PAINTING_SPECIES_ALLELE)
                {
                    try
                    {
                        int newWeight = Integer.parseInt(newText);
                        ((SpeciesAllele)object).setWeight(newWeight);
                    }
                    catch (IllegalArgumentException exception3)
                    {
                        weightTextField.setText(Integer.toString(((SpeciesAllele)object).getWeight()));
                    }
                }
            }
            else if (textField == widthTextField)
            {
                if (paintingMode == PAINTING_ENVIRONMENT)
                {
                    try
                    {
                        int newWidth = Integer.parseInt(newText);
                        ((Environment)object).setWidth(newWidth);
                    }
                    catch (NumberFormatException exception3)
                    {
                        widthTextField.setText(Integer.toString(((Environment)object).getWidth()));
                    }
                }
            }
            else if (textField == heightTextField)
            {
                if (paintingMode == PAINTING_ENVIRONMENT)
                {
                    try
                    {
                        int newHeight = Integer.parseInt(newText);
                        ((Environment)object).setHeight(newHeight);
                    }
                    catch (NumberFormatException exception3)
                    {
                        heightTextField.setText(Integer.toString(((Environment)object).getHeight()));
                    }
                }
            }
            else if (textField == hotspotRadiusTextField)
            {
                if (paintingMode == PAINTING_SPECIES_IMAGE)
                {
                    try
                    {
                        int newRadius = Integer.parseInt(newText);
                        ((SpeciesImage)object).setHotspotRadius(newRadius);
                    }
                    catch (NumberFormatException exception3)
                    {
                        hotspotRadiusTextField.setText(Integer.toString(((SpeciesImage)object).getHotspotRadius()));
                    }
                }
            }
            else if (textField == xxSmallDimensionOneTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newColumnWidth = Integer.parseInt(newText);
                        ((Species)object).setImageColumnWidth(SpeciesImage.XXSMALL_IMAGE_SIZE,newColumnWidth);
                    }
                    catch (NumberFormatException exception3)
                    {
                        xxSmallDimensionOneTextField.setText(Integer.toString(((Species)object).getImageColumnWidth(SpeciesImage.XXSMALL_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(newText);
                        int yValue = Integer.parseInt(xxSmallDimensionTwoTextField.getText());
                        ((SpeciesImageRow)object).setXXSmallHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getXXSmallHotspot();
                        xxSmallDimensionOneTextField.setText(Integer.toString(point.x));
                    }
                }
            }
            else if (textField == xSmallDimensionOneTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newColumnWidth = Integer.parseInt(newText);
                        ((Species)object).setImageColumnWidth(SpeciesImage.XSMALL_IMAGE_SIZE,newColumnWidth);
                    }
                    catch (NumberFormatException exception3)
                    {
                        xSmallDimensionOneTextField.setText(Integer.toString(((Species)object).getImageColumnWidth(SpeciesImage.XSMALL_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(newText);
                        int yValue = Integer.parseInt(xSmallDimensionTwoTextField.getText());
                        ((SpeciesImageRow)object).setXSmallHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getXSmallHotspot();
                        xSmallDimensionOneTextField.setText(Integer.toString(point.x));
                    }
                }
            }
            else if (textField == smallDimensionOneTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newColumnWidth = Integer.parseInt(newText);
                        ((Species)object).setImageColumnWidth(SpeciesImage.SMALL_IMAGE_SIZE,newColumnWidth);
                    }
                    catch (NumberFormatException exception3)
                    {
                        smallDimensionOneTextField.setText(Integer.toString(((Species)object).getImageColumnWidth(SpeciesImage.SMALL_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(newText);
                        int yValue = Integer.parseInt(smallDimensionTwoTextField.getText());
                        ((SpeciesImageRow)object).setSmallHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getSmallHotspot();
                        smallDimensionOneTextField.setText(Integer.toString(point.x));
                    }
                }
            }
            else if (textField == mediumDimensionOneTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newColumnWidth = Integer.parseInt(newText);
                        ((Species)object).setImageColumnWidth(SpeciesImage.MEDIUM_IMAGE_SIZE,newColumnWidth);
                    }
                    catch (NumberFormatException exception3)
                    {
                        mediumDimensionOneTextField.setText(Integer.toString(((Species)object).getImageColumnWidth(SpeciesImage.MEDIUM_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(newText);
                        int yValue = Integer.parseInt(mediumDimensionTwoTextField.getText());
                        ((SpeciesImageRow)object).setMediumHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getMediumHotspot();
                        mediumDimensionOneTextField.setText(Integer.toString(point.x));
                    }
                }
            }
            else if (textField == largeDimensionOneTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newColumnWidth = Integer.parseInt(newText);
                        ((Species)object).setImageColumnWidth(SpeciesImage.LARGE_IMAGE_SIZE,newColumnWidth);
                    }
                    catch (NumberFormatException exception3)
                    {
                        largeDimensionOneTextField.setText(Integer.toString(((Species)object).getImageColumnWidth(SpeciesImage.LARGE_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(newText);
                        int yValue = Integer.parseInt(largeDimensionTwoTextField.getText());
                        ((SpeciesImageRow)object).setLargeHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getLargeHotspot();
                        largeDimensionOneTextField.setText(Integer.toString(point.x));
                    }
                }
            }
            else if (textField == xLargeDimensionOneTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newColumnWidth = Integer.parseInt(newText);
                        ((Species)object).setImageColumnWidth(SpeciesImage.XLARGE_IMAGE_SIZE,newColumnWidth);
                    }
                    catch (NumberFormatException exception3)
                    {
                        xLargeDimensionOneTextField.setText(Integer.toString(((Species)object).getImageColumnWidth(SpeciesImage.XLARGE_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(newText);
                        int yValue = Integer.parseInt(xLargeDimensionTwoTextField.getText());
                        ((SpeciesImageRow)object).setXLargeHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getXLargeHotspot();
                        xLargeDimensionOneTextField.setText(Integer.toString(point.x));
                    }
                }
            }
            else if (textField == xxSmallDimensionTwoTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newRowHeight = Integer.parseInt(newText);
                        ((Species)object).setImageRowHeight(SpeciesImage.XXSMALL_IMAGE_SIZE,newRowHeight);
                    }
                    catch (NumberFormatException exception3)
                    {
                        xxSmallDimensionTwoTextField.setText(Integer.toString(((Species)object).getImageRowHeight(SpeciesImage.XXSMALL_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(xxSmallDimensionOneTextField.getText());
                        int yValue = Integer.parseInt(newText);
                        ((SpeciesImageRow)object).setXXSmallHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getXXSmallHotspot();
                        xxSmallDimensionTwoTextField.setText(Integer.toString(point.y));
                    }
                }
            }
            else if (textField == xSmallDimensionTwoTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newRowHeight = Integer.parseInt(newText);
                        ((Species)object).setImageRowHeight(SpeciesImage.XSMALL_IMAGE_SIZE,newRowHeight);
                    }
                    catch (NumberFormatException exception3)
                    {
                        xSmallDimensionTwoTextField.setText(Integer.toString(((Species)object).getImageRowHeight(SpeciesImage.XSMALL_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(xSmallDimensionOneTextField.getText());
                        int yValue = Integer.parseInt(newText);
                        ((SpeciesImageRow)object).setXSmallHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getXSmallHotspot();
                        xSmallDimensionTwoTextField.setText(Integer.toString(point.y));
                    }
                }
            }
            else if (textField == smallDimensionTwoTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newRowHeight = Integer.parseInt(newText);
                        ((Species)object).setImageRowHeight(SpeciesImage.SMALL_IMAGE_SIZE,newRowHeight);
                    }
                    catch (NumberFormatException exception3)
                    {
                        smallDimensionTwoTextField.setText(Integer.toString(((Species)object).getImageRowHeight(SpeciesImage.SMALL_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(smallDimensionOneTextField.getText());
                        int yValue = Integer.parseInt(newText);
                        ((SpeciesImageRow)object).setSmallHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getSmallHotspot();
                        smallDimensionTwoTextField.setText(Integer.toString(point.y));
                    }
                }
            }
            else if (textField == mediumDimensionTwoTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newRowHeight = Integer.parseInt(newText);
                        ((Species)object).setImageRowHeight(SpeciesImage.MEDIUM_IMAGE_SIZE,newRowHeight);
                    }
                    catch (NumberFormatException exception3)
                    {
                        mediumDimensionTwoTextField.setText(Integer.toString(((Species)object).getImageRowHeight(SpeciesImage.MEDIUM_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(mediumDimensionOneTextField.getText());
                        int yValue = Integer.parseInt(newText);
                        ((SpeciesImageRow)object).setMediumHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getMediumHotspot();
                        mediumDimensionTwoTextField.setText(Integer.toString(point.y));
                    }
                }
            }
            else if (textField == largeDimensionTwoTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newRowHeight = Integer.parseInt(newText);
                        ((Species)object).setImageRowHeight(SpeciesImage.LARGE_IMAGE_SIZE,newRowHeight);
                    }
                    catch (NumberFormatException exception3)
                    {
                        largeDimensionTwoTextField.setText(Integer.toString(((Species)object).getImageRowHeight(SpeciesImage.LARGE_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(largeDimensionOneTextField.getText());
                        int yValue = Integer.parseInt(newText);
                        ((SpeciesImageRow)object).setLargeHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getLargeHotspot();
                        largeDimensionTwoTextField.setText(Integer.toString(point.y));
                    }
                }
            }
            else if (textField == xLargeDimensionTwoTextField)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    try
                    {
                        int newRowHeight = Integer.parseInt(newText);
                        ((Species)object).setImageRowHeight(SpeciesImage.XLARGE_IMAGE_SIZE,newRowHeight);
                    }
                    catch (NumberFormatException exception3)
                    {
                        xLargeDimensionTwoTextField.setText(Integer.toString(((Species)object).getImageRowHeight(SpeciesImage.XLARGE_IMAGE_SIZE)));
                    }
                }
                else if (paintingMode == PAINTING_SPECIES_IMAGE_ROW)
                {
                    try
                    {
                        int xValue = Integer.parseInt(xLargeDimensionOneTextField.getText());
                        int yValue = Integer.parseInt(newText);
                        ((SpeciesImageRow)object).setXLargeHotspot(new Point(xValue,yValue));
                    }
                    catch (NumberFormatException exception3)
                    {
                        Point point = ((SpeciesImageRow)object).getXLargeHotspot();
                        xLargeDimensionTwoTextField.setText(Integer.toString(point.y));
                    }
                }
            }
        }
    }

    /**
     * Process pending text area control modifications.  This is called
     * by focusLost() for normal focus lost processing and
     * by processingPendingTextEdits() when there is a pending text edit.<p>
     *
     * @param		textArea JTextArea
    **/
    private void processPendingTextEdit(JTextArea textArea)
    {
        if (textArea != null && object != null)
        {
            String newText = textArea.getText();
    
            if (textArea == descriptionTextArea)
            {
                if (paintingMode == PAINTING_SPECIES)
                {
                    ((Species)object).setDescription(newText);
                }
                else if (paintingMode == PAINTING_GENE)
                {
                    ((Gene)object).setDescription(newText);
                }
                else if (paintingMode == PAINTING_SPECIES_ALLELE)
                {
                    ((SpeciesAllele)object).setDescription(newText);
                }
            }
            else if (textArea == alleleBaseValuesTextArea)
            {
                if (paintingMode == PAINTING_SPECIES_ALLELE)
                {
                    try
                    {
                        ((SpeciesAllele)object).setBasesAsString(newText);
                    }
                    catch (IllegalArgumentException exception3)
                    {
                        alleleBaseValuesTextArea.setText(((SpeciesAllele)object).getBasesAsString());
                    }
                }
            }
        }
    }
}

