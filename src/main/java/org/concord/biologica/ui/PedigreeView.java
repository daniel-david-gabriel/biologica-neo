//
// Class : PedigreeView - the pedigree view in the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.23 $
// $Date: 2004/12/09 06:04:12 $
// $Author: dima $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

import org.concord.domainsupport.*;
import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.concord.pedagogica.engine.Logging;
import org.concord.pedagogica.engine.Activity;



/**
 * The pedigree view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.CHARACTERISTICS_TEXT_VISIBLE - characteristics text visibility changed
 * <li> UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM - the user clicked on an organism with the chromosome tool
 * <li> UIProp.CROSS_SUCCEEDED - the cross succeeded
 * <li> UIProp.CROSS_TOOL_ENABLED - the cross tool enabled changed
 * <li> UIProp.CROSS_TOOL_VISIBLE - the cross tool visibility changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * <li> UIProp.NAME_TEXT_VISIBLE - the name text visibility boolean changed
 * <li> UIProp.NUMBER_FEMALE_CHILDREN - the number of female children in a family when in male / female mode
 * <li> UIProp.NUMBER_MALE_CHILDREN - the number of male children in a family when in male / female mode
 * <li> UIProp.OFFSPRING_MODE - the offspring mode has changed
 * <li> UIProp.OFFSPRING_MODE_PULLDOWN_ENABLED - the family mode pulldown enabled state changed
 * <li> UIProp.OFFSPRING_MODE_PULLDOWN_VISIBLE - the family mode pulldown visibility changed
 * <li> UIProp.ORGANISM_ADDED_TO_VIEW - an organism has been added to this view
 * <li> UIProp.ORGANISM_IMAGE_SIZE - the image size to use for drawing organisms in this view
 * <li> UIProp.ORGANISM_IMAGES_VISIBLE - the organism images (vs. trait squares and circles) are visible
 * <li> UIProp.ORGANISM_REMOVED_FROM_VIEW - an organism has been removed from this view
 * <li> UIProp.SELECTION_TOOL_ENABLED - the selection tool enabled state changed
 * <li> UIProp.SELECTION_TOOL_VISIBLE - the selection tool visibility changed
 * <li> UIProp.SEX_TEXT_VISIBLE - the sex text visibility boolean changed
 * <li> UIProp.SNIP_TOOL_ENABLED - the snip tool enabled state changed
 * <li> UIProp.SNIP_TOOL_VISIBLE - the snip tool visibility changed
 * <li> UIProp.SPECIES_TEXT_VISIBLE - the species text visibility boolean changed
 * <li> UIProp.TEXT_INDENT - the indentation of text from left edge of image
 * <li> UIProp.TEXT_LINE_SPACING - the number of pixels between lines of text
 * <li> UIProp.TRAIT_PULLDOWN_ENABLED - the trait pulldown enabled state changed
 * <li> UIProp.TRAIT_PULLDOWN_VISIBLE - the trait pulldown visibility changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#CHARACTERISTICS_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#CHROMOSOME_TOOL_PICK_ON_ORGANISM
 * @see org.concord.biologica.ui.UIProp#CROSS_SUCCEEDED
 * @see org.concord.biologica.ui.UIProp#CROSS_TOOL_ENABLED
 * @see org.concord.biologica.ui.UIProp#CROSS_TOOL_VISIBLE
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#NAME_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#NUMBER_FEMALE_CHILDREN
 * @see org.concord.biologica.ui.UIProp#NUMBER_MALE_CHILDREN
 * @see org.concord.biologica.ui.UIProp#OFFSPRING_MODE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_ADDED_TO_VIEW
 * @see org.concord.biologica.ui.UIProp#ORGANISM_IMAGE_SIZE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_IMAGES_VISIBLE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_REMOVED_FROM_VIEW
 * @see org.concord.biologica.ui.UIProp#SELECTION_TOOL_ENABLED
 * @see org.concord.biologica.ui.UIProp#SELECTION_TOOL_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SEX_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SNIP_TOOL_ENABLED
 * @see org.concord.biologica.ui.UIProp#SNIP_TOOL_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SPECIES_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#TEXT_INDENT
 * @see org.concord.biologica.ui.UIProp#TEXT_LINE_SPACING
 * @see org.concord.biologica.ui.UIProp#TRAIT_PULLDOWN_ENABLED
 * @see org.concord.biologica.ui.UIProp#TRAIT_PULLDOWN_VISIBLE
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.23 $ $Date: 2004/12/09 06:04:12 $
 * @author 		$Author: dima $
**/
public final class PedigreeView
extends UIView
implements ActionListener, ComponentListener, ItemListener, PropertyChangeListener, FocusListener, DomainView
{
    // Pedigree view offspring modes - must be unique
    static public final int    OFFSPRING_MODE_FIXED			        = 1;
    static public final String OFFSPRING_MODE_FIXED_STRING          = "Fixed";

    static public final int    OFFSPRING_MODE_MIN_MAX               = 2;
    static public final String OFFSPRING_MODE_MIN_MAX_STRING        = "Min / Max";

    static public final int    OFFSPRING_MODE_MALE_FEMALE	        = 3;
    static public final String OFFSPRING_MODE_MALE_FEMALE_STRING    = "Male / Female";

    // Apply command
    static private final String cmdApply					= "CAP";

    /**
     * Active tool - selection, cross, snip, etc.
    **/
    private int activeTool;

    /**
     * Control with Focus
    **/
    private Object focusControl = null;

    /**
     * Apply pending changes button
    **/
    private JButton applyButton = null;

    /**
     * Selection tool toggle button
    **/
    private JToggleButton selectionToolToggleButton;

    /**
     * Cross tool toggle button
    **/
    private JToggleButton crossToolToggleButton;

    /**
     * Snip tool toggle button
    **/
    private JToggleButton snipToolToggleButton;

    /**
     * Chromosome tool toggle button
    **/
    private JToggleButton chromosomeToolToggleButton;

	
	/**
     * Pedigree tool toggle button
    **/
	private JToggleButton pedigreeToolToggleButton;
	
    /**
     * Implement correct radio button behavior
    **/
    private ButtonGroup buttonGroup;

    /**
     * Trait pulldown
    **/
    private BioComboBox traitPulldown;

    /**
     * Family mode pulldown
    **/
    private BioComboBox offspringModePulldown;

    /**
     * Fixed number of offspring text field
    **/
    private JTextField numberFixedOffspringTextField = null;

    /**
     * Number of male offspring text field
    **/
    private JTextField numberMaleOffspringTextField = null;
    
    /**
     * Number of female offspring text field
    **/
    private JTextField numberFemaleOffspringTextField = null;

    /**
     * Minimum number of offspring text field
    **/
    private JTextField minimumOffspringTextField = null;
    
    /**
     * Maximum number of offspring text field
    **/
    private JTextField maximumOffspringTextField = null;

	/**
	 * cross over tool
	 */
	private JCheckBox crossOverCheckBox;
	 
    /**
     * JScrollPane which holds the pedigree organism view
    **/
    private JScrollPane pedigreeOrganismViewScrollPane;

    /**
     * Pedigree organism view
    **/
    private PedigreeOrganismView pedigreeOrganismView;

    /**
     * Selection tool enabled?
    **/
    private boolean selectionToolEnabled;

    /**
     * Selection tool visible
    **/
    private boolean selectionToolVisible;

    /**
     * Cross tool enabled?
    **/
    private boolean crossToolEnabled;

    /**
     * Cross tool visible
    **/
    private boolean crossToolVisible;

    /**
     * Snip tool enabled?
    **/
    private boolean snipToolEnabled;
    
    /**
     * Snip tool visible
    **/
    private boolean snipToolVisible;
    
    /**
     * Pedigree tool enabled?
    **/
    private boolean pedigreeToolEnabled;
    
    /**
     * Pedigree tool visible
    **/
    private boolean pedigreeToolVisible;

    /**
     * Chromosome tool enabled?
    **/
    private boolean chromosomeToolEnabled;
    
    /**
     * Chromosome tool visible
    **/
    private boolean chromosomeToolVisible;

    /**
     * Trait pulldown enabled?
    **/
    private boolean traitPulldownEnabled;

    /**
     * Trait pulldown visible
    **/
    private boolean traitPulldownVisible;
    
    /**
     * Cross Over Check Box enabled?
    **/
    private boolean blnCrossOverEnabled;
    
    /**
     * Cross Over Check Box visible?
    **/
    private boolean blnCrossOverVisible;
    
    /**
     * Cross Over Turn on?
    **/
    private boolean blnCrossOverTurnOn;
    

    /**
     * Family mode pulldown enabled?
    **/
    private boolean offspringModePulldownEnabled;

    /**
     * Family mode pulldown visible
    **/
    private boolean offspringModePulldownVisible;
    
    /**
    * Legend is visible?
    **/
    private boolean blnShowLegend;
    /**
    * Mutation Trait Legend visible?
    */
	private boolean blnMutationLegendVisible;
    /**
     * Actual width of this view.
    **/
    private int actualWidth = -10;

    /**
     * Actual height of this view.
    **/
    private int actualHeight = -10;
    
    /**
     *Cross over Possibility
     */
     
     private double P;
		
	// Prevent recursion in itemStateChanged	
	private int callLevel = 0;
	
	//Mutation Characteristics
	private Vector mutationCharacteristic;

    /**
     * Creates a pedigree view.
    **/
    public PedigreeView()
    {
        super();

        // Set colors
        setBackground(Color.lightGray);
        setForeground(Color.black);

        activeTool = Tool.SELECTION;
        selectionToolEnabled = true;
        selectionToolVisible = true;
        crossToolEnabled = true;
        crossToolVisible = true;
        snipToolEnabled = true;
        snipToolVisible = true;
        pedigreeToolVisible = false;
        pedigreeToolEnabled = true;
        chromosomeToolEnabled = true;
        chromosomeToolVisible = true;
        traitPulldownEnabled = true;
        traitPulldownVisible = true;
        offspringModePulldownEnabled = true;
        offspringModePulldownVisible = true;
        blnCrossOverEnabled = false;
        blnCrossOverVisible = false;
        blnShowLegend = true;
        blnMutationLegendVisible = false;
        
        mutationCharacteristic = new Vector();
        mutationCharacteristic.addElement("Characteristic: Unicorn");
        mutationCharacteristic.addElement("Characteristic: Albino");
        mutationCharacteristic.addElement("Characteristic: Double Wings");
        mutationCharacteristic.addElement("Characteristic: Arrow Tail");
        mutationCharacteristic.addElement("Characteristic: Blue Fire");

        // Create child components
        Insets insets = new Insets(2,2,2,2);
        
        buttonGroup = new ButtonGroup();

        selectionToolToggleButton = new JToggleButton(Tool.selectionToolImageIcon);
        selectionToolToggleButton.setMargin(insets);
        selectionToolToggleButton.addActionListener(this);
        selectionToolToggleButton.setActionCommand(Tool.COMMAND_SELECTION);
        selectionToolToggleButton.setFocusPainted(false);
        selectionToolToggleButton.setToolTipText(Tool.TIP_SELECTION);
        selectionToolToggleButton.setSelected(true);
        buttonGroup.add(selectionToolToggleButton);
        add(selectionToolToggleButton);

        crossToolToggleButton = new JToggleButton(Tool.crossToolImageIcon);
        crossToolToggleButton.setMargin(insets);
        crossToolToggleButton.addActionListener(this);
        crossToolToggleButton.setActionCommand(Tool.COMMAND_CROSS);
        crossToolToggleButton.setFocusPainted(false);
        crossToolToggleButton.setToolTipText(Tool.TIP_CROSS);
        crossToolToggleButton.setSelected(false);
        buttonGroup.add(crossToolToggleButton);
        add(crossToolToggleButton);

        snipToolToggleButton = new JToggleButton(Tool.snipToolImageIcon);
        snipToolToggleButton.setMargin(insets);
        snipToolToggleButton.addActionListener(this);
        snipToolToggleButton.setActionCommand(Tool.COMMAND_SNIP);
        snipToolToggleButton.setFocusPainted(false);
        snipToolToggleButton.setToolTipText(Tool.TIP_SNIP);
        snipToolToggleButton.setSelected(false);
        buttonGroup.add(snipToolToggleButton);
        add(snipToolToggleButton);

        chromosomeToolToggleButton = new JToggleButton(Tool.chromosomeToolImageIcon);
        chromosomeToolToggleButton.setMargin(insets);
        chromosomeToolToggleButton.addActionListener(this);
        chromosomeToolToggleButton.setActionCommand(Tool.COMMAND_CHROMOSOME);
        chromosomeToolToggleButton.setFocusPainted(false);
        chromosomeToolToggleButton.setToolTipText(Tool.TIP_CHROMOSOME);
        chromosomeToolToggleButton.setSelected(false);
        buttonGroup.add(chromosomeToolToggleButton);
        add(chromosomeToolToggleButton);
        
        pedigreeToolToggleButton  = new JToggleButton(Tool.pedigreeToolImageIcon);
        
        pedigreeToolToggleButton.setMargin(insets);
        pedigreeToolToggleButton.addActionListener(this);
        pedigreeToolToggleButton.setActionCommand(Tool.COMMAND_PEDIGREE);
        pedigreeToolToggleButton.setFocusPainted(false);
        pedigreeToolToggleButton.setToolTipText(Tool.TIP_PEDIGREE);
        pedigreeToolToggleButton.setSelected(false);
        buttonGroup.add(pedigreeToolToggleButton);
        add(pedigreeToolToggleButton);
        

        traitPulldown = new BioComboBox();
        traitPulldown.addItemListener(this);
        traitPulldown.setVisible(false);
        traitPulldown.setEnabled(false);
        add(traitPulldown);
        
        crossOverCheckBox = new JCheckBox("Crossing Over");
        crossOverCheckBox.addActionListener(this);
        crossOverCheckBox.setBounds(200,2,160,20);
        crossOverCheckBox.setBackground(Color.lightGray);
        crossOverCheckBox.setVisible(false);
        crossOverCheckBox.setEnabled(false);
        add(crossOverCheckBox);

        offspringModePulldown = new BioComboBox();
        offspringModePulldown.addItem(OFFSPRING_MODE_FIXED_STRING);
        offspringModePulldown.addItem(OFFSPRING_MODE_MIN_MAX_STRING);
        offspringModePulldown.setVisible(false);
        offspringModePulldown.setEnabled(false);
        offspringModePulldown.addItemListener(this);
        add(offspringModePulldown);

        numberFixedOffspringTextField = new JTextField(5);
        numberFixedOffspringTextField.setBounds(410,2,50,24);
        numberFixedOffspringTextField.setVisible(false);
        numberFixedOffspringTextField.setFont(getFont());
        numberFixedOffspringTextField.setBackground(Color.white);
        numberFixedOffspringTextField.addFocusListener(this);
        add(numberFixedOffspringTextField);

        numberMaleOffspringTextField = new JTextField(5);
        numberMaleOffspringTextField.setBounds(410,2,50,24);
        numberMaleOffspringTextField.setVisible(false);
        numberMaleOffspringTextField.setFont(getFont());
        numberMaleOffspringTextField.setBackground(Color.white);
        numberMaleOffspringTextField.addFocusListener(this);
        add(numberMaleOffspringTextField);

        numberFemaleOffspringTextField = new JTextField(5);
        numberFemaleOffspringTextField.setBounds(510,2,50,24);
        numberFemaleOffspringTextField.setVisible(false);
        numberFemaleOffspringTextField.setFont(getFont());
        numberFemaleOffspringTextField.setBackground(Color.white);
        numberFemaleOffspringTextField.addFocusListener(this);
        add(numberFemaleOffspringTextField);

        minimumOffspringTextField = new JTextField(5);
        minimumOffspringTextField.setBounds(410,2,50,24);
        minimumOffspringTextField.setVisible(false);
        minimumOffspringTextField.setFont(getFont());
        minimumOffspringTextField.setBackground(Color.white);
        minimumOffspringTextField.addFocusListener(this);
        add(minimumOffspringTextField);

        maximumOffspringTextField = new JTextField(5);
        maximumOffspringTextField.setBounds(510,2,50,24);
        maximumOffspringTextField.setVisible(false);
        maximumOffspringTextField.setFont(getFont());
        maximumOffspringTextField.setBackground(Color.white);
        maximumOffspringTextField.addFocusListener(this);
        add(maximumOffspringTextField);

        applyButton = new JButton("Apply");
        applyButton.setMargin(insets);
        applyButton.addActionListener(this);
        applyButton.setActionCommand(cmdApply);
        applyButton.setFocusPainted(false);
        applyButton.setBounds(600,2,60,24);
        applyButton.setForeground(Color.black);
        applyButton.setBackground(Color.lightGray);
        applyButton.setVisible(false);
        applyButton.setToolTipText("Apply pending text changes");
        add(applyButton);

        pedigreeOrganismView = new PedigreeOrganismView();
        pedigreeOrganismViewScrollPane = new JScrollPane(pedigreeOrganismView,
                                                         ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pedigreeOrganismView.setScrollPane(pedigreeOrganismViewScrollPane);
        pedigreeOrganismView.setTraitPulldown(traitPulldown);
        pedigreeOrganismView.setCrossOverCheckBox(crossOverCheckBox);
        pedigreeOrganismView.setOffspringModePulldown(offspringModePulldown);
        pedigreeOrganismView.addPropertyChangeListener(this);
       // pedigreeOrganismView.setPedigreeView(this);
        add(pedigreeOrganismViewScrollPane);

        // Turn on double buffering
        setDoubleBuffered(true);

        // Update state of controls
        updateControls();

        // Listen for resize events
        addComponentListener(this);
    }

    /**
     * Get the pedigree organism view in this view.
     *
     * @return		PedigreeOrganismView - the pedigree organism view of this larger view
    **/
    public PedigreeOrganismView getPedigreeOrganismView()
    {
        return pedigreeOrganismView;
    }

	/**
    *	Turn on one invisible gene on trait pulldown
    *   @param    aTraitName String--a Trait Name of currentSpecies;
    */
    public void setTraitVisible(String aTraitName,boolean bln)
    {
    	if (pedigreeOrganismView == null) return;
    	
    	pedigreeOrganismView.setTraitVisible(aTraitName,bln);

    }
    /**
     * Returns whether or not the given organism is in this view.
     *
     * @param       anOrganism Organism - the organism to find
     * @return      boolean true (in view) or false (not in view)
    **/
    public boolean containsOrganism(Organism anOrganism)
    {
        return pedigreeOrganismView.containsOrganism(anOrganism);
    }

    /**
     * Add an organism to this view.<p>
     *
     * @param		anOrganism Organism - an organism to add, may not be null
     * @param		xLocation int - x location of organism
     * @param		yLocation int - y location of organism
     * @exception	IllegalArgumentException - input organism null
    **/
    public void addOrganism(Organism anOrganism, int xLocation, int yLocation)
    {
        if (getSpecies() == null)
        {
            setSpecies(anOrganism.getSpecies());
            if (getSpecies().getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
            {
                offspringModePulldown.addItem(OFFSPRING_MODE_MALE_FEMALE_STRING);
            }
        }
        pedigreeOrganismView.addOrganism(anOrganism,xLocation,yLocation);
    }

    /**
     * Add an organism to this view and specify the trait to show, which is
     * used only if this is the first organism added to the view.  If there
     * are organisms in the view already, aTraitName is ignored and the caller
     * must set the trait by calling setTrait().<p>
     *
     * @param		anOrganism Organism - an organism to add, may not be null
     * @param		xLocation int - x location of organism
     * @param		yLocation int - y location of organism
     * @param		aTraitName String - the name of a trait to show, null causes full organisms to be shown
     * @exception	IllegalArgumentException - input organism null
    **/
    public void addOrganism(Organism anOrganism, int xLocation, int yLocation, String aTraitName)
    {
        pedigreeOrganismView.addOrganism(anOrganism,xLocation,yLocation,aTraitName);
    }

    /**
     * Removes an organism from this view. This method returns false if anOrganism is null.<p>
     *
     * @param		anOrganism Organism - an organism, may be null
     * @return		boolean indicating whether or not the organism was found and removed
    **/
    public boolean removeOrganism(Organism anOrganism)
    {
        return pedigreeOrganismView.removeOrganism(anOrganism);
    }

    /**
     * Remove all pedigreeOrganisms
    **/
    public void removeAllOrganisms()
    {
        //pedigreeOrganismView.removeAllOrganisms();
        pedigreeOrganismView.removeAll();
    }

    /**
     * Get the number of pedigreeOrganisms in this view.
     *
     * @return		int - number of pedigreeOrganisms in this view (0 or greater)
    **/
    public int getNumberOfOrganisms()
    {
        return pedigreeOrganismView.getNumberOfOrganisms();
    }

    /**
     * Get the set of pedigreeOrganisms in this view.
     *
     * @return		Enumeration - enumeration over the set of pedigreeOrganisms in this view
    **/
    public Enumeration getOrganisms()
    {
        return pedigreeOrganismView.getOrganisms();
    }

    /**
     * Add a family to this view.  If the parents or children are not already in the
     * view, this method will create and arbitrarily place them in the view.<p>
     *
     * If neither parent is in the view, the given x, y coordinates are used to place
     * the mother organism and then the father and children positions are calculated
     * relative to the mother's position.<p>
     *
     * @param		aFamily Family - a family to add, may not be null nor already in the view
     * @param		xLocation int - x location of first parent organism, ignored if a parent already in view
     * @param		yLocation int - y location of first parent organism, ignored if a parent already in view
     * @exception	IllegalArgumentException - input organism null
    **/
    public void addFamily(Family aFamily, int xLocation, int yLocation)
    {
        pedigreeOrganismView.addFamily(aFamily,xLocation,yLocation);
    }

    /**
     * Removes a family from this view. This method returns false if aFamily is null.<p>
     *
     * @param		aFamily Family - a family, may be null
     * @return		boolean indicating whether or not the family was found and removed
    **/
    public boolean removeFamily(Family aFamily)
    {
        return pedigreeOrganismView.removeFamily(aFamily);
    }

    /**
     * Remove all pedigreeFamilies, with no notificaition
    **/
    public void removeAllFamilies()
    {
        //pedigreeOrganismView.removeAllFamilies();
        pedigreeOrganismView.removeAll();
    }

    /**
     * Get the number of pedigreeFamilies in this view.
     *
     * @return		int - number of pedigreeFamilies in this view (0 or greater)
    **/
    public int getNumberOfFamilies()
    {
        return pedigreeOrganismView.getNumberOfFamilies();
    }

    /**
     * Get the set of pedigreeFamilies in this view.
     *
     * @return		Enumeration - enumeration over the set of pedigreeFamilies in this view
    **/
    public Enumeration getFamilies()
    {
        return pedigreeOrganismView.getFamilies();
    }

    /**
     * Get the Family for the given parent Organisms
     *
     * @param		parentOne Organism - the first parent Organism
     * @param		parentTwo Organism - the second parent Organism
     * @return		Family - the resulting Family or null.
    **/
    public Family getFamilyForParents(Organism parentOne, Organism parentTwo)
    {
        return pedigreeOrganismView.getFamilyForParents(parentOne, parentTwo);
    }
    
    
     /**
     * Add a child to a family in pedigreeView.
     *
     * @param		aFamily Family - Family to add children to.
     * @param		newChild Organism - child to add.
    **/
    public void addChildToFamily(Organism parentOne, Organism parentTwo, Organism newChild)
    {
    	pedigreeOrganismView.addChildToFamily(parentOne,parentTwo,newChild);
    }

    
    /**
     * Remove all objects in this view, both organisms and families.<p>
    **/
    public void removeAll()
    {
        pedigreeOrganismView.removeAll();
    }

    /**
     * Get the offspring mode
     *
     * @return		int - the offspring mode
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_FIXED
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_MIN_MAX
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_MALE_FEMALE 
    **/
    public int getOffspringMode()
    {
        return pedigreeOrganismView.getOffspringMode();
    }

    /**
     * Set the offspring mode
     *
     * @param		anOffspringMode int - the new offspring mode
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_FIXED
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_MIN_MAX
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_MALE_FEMALE 
    **/
    public void setOffspringMode(int anOffspringMode)
    {
        pedigreeOrganismView.setOffspringMode(anOffspringMode);

        // Update controls
        updateControls();
    }
    
    public JButton getUtilityButton()
    {
        return applyButton;
    }

    /**
     * Is the Only Live Children flag true?
     *
     * @return		boolean - onlyLiveChildren
    **/
    public boolean isOnlyLiveChildren()
    {
        return pedigreeOrganismView.isOnlyLiveChildren();
    }

    /**
     * Set the Only Live Children flag
     *
     * @param		onlyLiveChildren boolean - only live children?
    **/
    public void setOnlyLiveChildren(boolean onlyLiveChildren)
    {
        pedigreeOrganismView.setOnlyLiveChildren(onlyLiveChildren);
    }

    /**
     * Is the organism images visible?
     *
     * @return		boolean - organism images visible?
    **/
    public boolean isOrganismImagesVisible()
    {
        return pedigreeOrganismView.isOrganismImagesVisible();
    }

    /**
     * Set the organism images visible
     *
     * @param		visible boolean - visible?
    **/
    public void setOrganismImagesVisible(boolean visible)
    {
        pedigreeOrganismView.setOrganismImagesVisible(visible);
    }

    /**
     * Get the active tool.
     *
     * @return		int - the active tool
    **/
    public int getActiveTool()
    {
        return activeTool;
    }

    /**
     * Method called by ToolView when the current tool changes.<p>
     *
     * @param   aTool int - the active tool
    **/
    public void toolChanged(int aTool)
    {
        setActiveTool(aTool);
    }

    /**
     * Set the active tool
     *
     * @param		anActiveTool int - the new active tool
    **/
    public void setActiveTool(int anActiveTool)
    {
        // Return immediately if not a change
        if (anActiveTool == activeTool)
        {
            return;
        }

        // Save old tool and make change
        int oldActiveTool = activeTool;
        activeTool = anActiveTool;

        // Update tool buttons and cursor
        if (activeTool == Tool.SELECTION)
        {
            selectionToolToggleButton.setSelected(true);
            crossToolToggleButton.setSelected(false);
            snipToolToggleButton.setSelected(false);
            chromosomeToolToggleButton.setSelected(false);
            pedigreeToolToggleButton.setSelected(false);
            setCursor(Tool.getCursor(activeTool));
            pedigreeOrganismView.setCursor(Tool.getCursor(activeTool));
        }
        else if (activeTool == Tool.CROSS)
        {
            selectionToolToggleButton.setSelected(false);
            crossToolToggleButton.setSelected(true);
            snipToolToggleButton.setSelected(false);
            chromosomeToolToggleButton.setSelected(false);
            pedigreeToolToggleButton.setSelected(false);
            setCursor(Tool.getCursor(activeTool));
            pedigreeOrganismView.setCursor(Tool.getCursor(activeTool));
        }
        else if (activeTool == Tool.SNIP)
        {
            selectionToolToggleButton.setSelected(false);
            crossToolToggleButton.setSelected(false);
            snipToolToggleButton.setSelected(true);
            chromosomeToolToggleButton.setSelected(false);
            pedigreeToolToggleButton.setSelected(false);
            setCursor(Tool.getCursor(activeTool));
            pedigreeOrganismView.setCursor(Tool.getCursor(activeTool));
        }
        else if (activeTool == Tool.CHROMOSOME)
        {
            selectionToolToggleButton.setSelected(false);
            crossToolToggleButton.setSelected(false);
            snipToolToggleButton.setSelected(false);
            pedigreeToolToggleButton.setSelected(false);
            chromosomeToolToggleButton.setSelected(true);
            setCursor(Tool.getCursor(activeTool));
            pedigreeOrganismView.setPedigreeView(this);
            pedigreeOrganismView.setCursor(Tool.getCursor(activeTool));
           
            
        }
       
        else if (activeTool == Tool.NO_TOOL)
        {
            selectionToolToggleButton.setSelected(false);
            crossToolToggleButton.setSelected(false);
            snipToolToggleButton.setSelected(false);
            chromosomeToolToggleButton.setSelected(false);
            pedigreeToolToggleButton.setSelected(false);
            setCursor(Tool.getCursor(activeTool));
            pedigreeOrganismView.setCursor(Tool.getCursor(activeTool));
        }
        
        
        // Tell pedigree organism view, which will cause it to change cursor, etc.
        pedigreeOrganismView.setActiveTool(activeTool);

        // Notify listeners
        changes.firePropertyChange(UIProp.ACTIVE_TOOL,
                                   new Integer(oldActiveTool),
                                   new Integer(activeTool));
    }

    /**
     * Is the selection tool enabled?
     *
     * @return		boolean - is the selection tool enabled?
    **/
    public boolean isSelectionToolEnabled()
    {
        return selectionToolEnabled;
    }

    /**
     * Set the selection tool enabled state.
     *
     * @param		aSelectionToolEnabled boolean - enabled?
    **/
    public void setSelectionToolEnabled(boolean aSelectionToolEnabled)
    {
        // Return immediately if no change of state
        if (aSelectionToolEnabled == selectionToolEnabled)
        {
            return;
        }

        // Change
        boolean oldSelectionToolEnabled = selectionToolEnabled;
        selectionToolEnabled = aSelectionToolEnabled;

        // Enable/Disable selection tool
        selectionToolToggleButton.setEnabled(selectionToolEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.SELECTION_TOOL_ENABLED,
                                   new Boolean(oldSelectionToolEnabled),
                                   new Boolean(selectionToolEnabled));
    }

    /**
     * Is the cross tool enabled?
     *
     * @return		boolean - is the cross tool enabled?
    **/
    public boolean isCrossToolEnabled()
    {
        return crossToolEnabled;
    }

    /**
     * Set the cross tool enabled state.
     *
     * @param		aCrossToolEnabled boolean - enabled?
    **/
    public void setCrossToolEnabled(boolean aCrossToolEnabled)
    {
        // Return immediately if no change of state
        if (aCrossToolEnabled == crossToolEnabled)
        {
            return;
        }

        // Change
        boolean oldCrossToolEnabled = crossToolEnabled;
        crossToolEnabled = aCrossToolEnabled;

        // Enable/disable cross tool
        crossToolToggleButton.setEnabled(crossToolEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.CROSS_TOOL_ENABLED,
                                   new Boolean(oldCrossToolEnabled),
                                   new Boolean(crossToolEnabled));
    }

    /**
     * Is the snip tool enabled?
     *
     * @return		boolean - is the snip tool enabled?
    **/
    public boolean isSnipToolEnabled()
    {
        return snipToolEnabled;
    }

    /**
     * Set the snip tool enabled state.
     *
     * @param		aSnipToolEnabled boolean - enabled?
    **/
    public void setSnipToolEnabled(boolean aSnipToolEnabled)
    {
        // Return immediately if no change of state
        if (aSnipToolEnabled == snipToolEnabled)
        {
            return;
        }

        // Change
        boolean oldSnipToolEnabled = snipToolEnabled;
        snipToolEnabled = aSnipToolEnabled;

        // Enable/Disable snip tool
        snipToolToggleButton.setEnabled(snipToolEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.SNIP_TOOL_ENABLED,
                                   new Boolean(oldSnipToolEnabled),
                                   new Boolean(snipToolEnabled));
    }
    
     /**
     * Is the pedigree tool enabled?
     *
     * @return		boolean - is the pedigree tool enabled?
    **/
    public boolean isPedigreeToolEnabled()
    {
        return pedigreeToolEnabled;
    }

    /**
     * Set the pedigree tool enabled state.
     *
     * @param		aPedigreeToolEnabled boolean - enabled?
    **/
    public void setPedigreeToolEnabled(boolean aPedigreeToolEnabled)
    {
        // Return immediately if no change of state
        if (aPedigreeToolEnabled == pedigreeToolEnabled)
        {
            return;
        }

        // Change
        boolean oldPedigreeToolEnabled = pedigreeToolEnabled;
        pedigreeToolEnabled = aPedigreeToolEnabled;

        // Enable/Disable snip tool
        pedigreeToolToggleButton.setEnabled(pedigreeToolEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.PEDIGREE_TOOL_ENABLED,
                                   new Boolean(oldPedigreeToolEnabled),
                                   new Boolean(pedigreeToolEnabled));
    }
    /**
    * Is the Legend visible?
    * @return		boolean - is the legend visible?
    */
    public boolean isLegendVisible()
    {
    	return blnShowLegend;
    }
    
     /**
     * Set the legend visible state.
     *
     * @param		aBlnShowLegend boolean - visible?
    **/
    public void setLegendVisible(boolean aBlnShowLegend)
    {
        // Return immediately if no change of state
        if (aBlnShowLegend == blnShowLegend)
        {
            return;
        }

       	blnShowLegend = aBlnShowLegend;
       	repaint();
    }


    /**
     * Is the chromosome tool enabled?
     *
     * @return		boolean - is the chromosome tool enabled?
    **/
    public boolean isChromosomeToolEnabled()
    {
        return chromosomeToolEnabled;
    }

    /**
     * Set the chromosome tool enabled state.
     *
     * @param		aChromosomeToolEnabled boolean - enabled?
    **/
    public void setChromosomeToolEnabled(boolean aChromosomeToolEnabled)
    {
        // Return immediately if no change of state
        if (aChromosomeToolEnabled == chromosomeToolEnabled)
        {
            return;
        }

        // Change
        boolean oldChromosomeToolEnabled = chromosomeToolEnabled;
        chromosomeToolEnabled = aChromosomeToolEnabled;

        // Enable/disable chromosome tool
        chromosomeToolToggleButton.setEnabled(chromosomeToolEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.CHROMOSOME_TOOL_ENABLED,
                                   new Boolean(oldChromosomeToolEnabled),
                                   new Boolean(chromosomeToolEnabled));
    }
    
    /**
     * set Cross over possibility
     */
     public void setCrossOverPossibility(int p)
     {
     	pedigreeOrganismView.setCrossOverPossibility(((double)p)/100);
     } 
    
    /**
     * Is cross over enabled?
     *
     * @return		boolean - Is cross over enabled?
    **/
    public boolean isCrossOverEnabled()
    {
    	return blnCrossOverEnabled;
    }
    
    /**
     * set the Cross over check box enabled state
     *
     * @param		aCrossOverEnabled boolean - enabled?
    **/
    public void setCrossOverEnabled(boolean aCrossOverEnabled)
    {
       
       
        // Return immediately if no change of state
        if (aCrossOverEnabled == blnCrossOverEnabled)
        {
            return;
        }

        // Change
        boolean oldBlnCrossOverEnabled = blnCrossOverEnabled;
        blnCrossOverEnabled = aCrossOverEnabled;

        // Enable/Disable trait pulldown
        crossOverCheckBox.setEnabled(true);
      

        // Notify listeners
        /*changes.firePropertyChange(UIProp.TRAIT_PULLDOWN_ENABLED,
                                   new Boolean(oldBlnCrossOverEnabled),
                                   new Boolean(blnCrossOverEnabled));*/
    }
    /**
     * Is the cross over check box visibled?
     *
     * @return	boolean - Is the cross over check box visibled?
    **/
    
    public boolean isCrossOverVisible()
    {
    	return blnCrossOverVisible;
    }
    
    /**
     * set the Cross over check box visible state
     *
     * @param		aCrossOverEnabled boolean - visible?
    **/
    
    public void setCrossOverVisible(boolean aCrossOverVisible)
    {
    	if (blnCrossOverVisible == aCrossOverVisible)
        {
            return;
        }

        // Change
        boolean oldBlnCrossOverVisible = blnCrossOverVisible;
        blnCrossOverVisible = aCrossOverVisible;

        // Enable/Disable trait pulldown
        crossOverCheckBox.setVisible(blnCrossOverVisible);
        
    }
    
   
    /**
     * Is the Cross over turn on?
     *
     * @return 		boolean - visible?
    **/
    
    public boolean isCrossOverTurnOn()
    {
    	return blnCrossOverTurnOn;
    }
    
    public void setCrossOverTurnOn(boolean aBlnCrossOverTurnOn)
    {
    	blnCrossOverTurnOn = aBlnCrossOverTurnOn;
    	PedigreeOrganismView temp = this.getPedigreeOrganismView();
    	temp.setCrossOverTurnOn(blnCrossOverTurnOn);
    }

    /**
     * Set the trait pulldown enabled state.
     *
     * @param		aTraitPulldownEnabled boolean - enabled?
    **/
    public void setTraitPulldownEnabled(boolean aTraitPulldownEnabled)
    {
        // Return immediately if no change of state
        if (aTraitPulldownEnabled == traitPulldownEnabled)
        {
            return;
        }

        // Change
        boolean oldTraitPulldownEnabled = traitPulldownEnabled;
        traitPulldownEnabled = aTraitPulldownEnabled;

        // Enable/Disable trait pulldown
        traitPulldown.setEnabled(traitPulldownEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.TRAIT_PULLDOWN_ENABLED,
                                   new Boolean(oldTraitPulldownEnabled),
                                   new Boolean(traitPulldownEnabled));
    }

    /**
     * Is the offspring mode pulldown enabled?
     *
     * @return		boolean - is the offspring mode pulldown enabled?
    **/
    public boolean isOffspringModePulldownEnabled()
    {
        return offspringModePulldownEnabled;
    }

    /**
     * Set the offspring mode pulldown enabled state.
     *
     * @param		anOffspringModePulldownEnabled boolean - enabled?
    **/
    public void setOffspringModePulldownEnabled(boolean anOffspringModePulldownEnabled)
    {
        // Return immediately if no change of state
        if (anOffspringModePulldownEnabled == offspringModePulldownEnabled)
        {
            return;
        }

        // Change
        boolean oldOffspringModePulldownEnabled = offspringModePulldownEnabled;
        offspringModePulldownEnabled = anOffspringModePulldownEnabled;

        // Enable/Disable offspring mode pulldown
        offspringModePulldown.setEnabled(offspringModePulldownEnabled);
        numberFixedOffspringTextField.setEnabled(offspringModePulldownEnabled);
        numberMaleOffspringTextField.setEnabled(offspringModePulldownEnabled);
        numberFemaleOffspringTextField.setEnabled(offspringModePulldownEnabled);
        minimumOffspringTextField.setEnabled(offspringModePulldownEnabled);
        maximumOffspringTextField.setEnabled(offspringModePulldownEnabled);
        applyButton.setEnabled(offspringModePulldownEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.OFFSPRING_MODE_PULLDOWN_ENABLED,
                                   new Boolean(oldOffspringModePulldownEnabled),
                                   new Boolean(offspringModePulldownEnabled));
    }

    /**
     * Is the selection tool visible?
     *
     * @return		boolean - is the selection tool visible
    **/
    public boolean isSelectionToolVisible()
    {
        return selectionToolVisible;
    }

    /**
     * Set the selection tool visibility.
     *
     * @param		aSelectionToolVisible boolean - visible?
    **/
    public void setSelectionToolVisible(boolean aSelectionToolVisible)
    {
        // Return immediately if no change of state
        if (aSelectionToolVisible == selectionToolVisible)
        {
            return;
        }

        // Change
        boolean oldSelectionToolVisible = selectionToolVisible;
        selectionToolVisible = aSelectionToolVisible;

        // Show/Hide selection tool
        selectionToolToggleButton.setVisible(selectionToolVisible);
        updateSize();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.SELECTION_TOOL_VISIBLE,
                                   new Boolean(oldSelectionToolVisible),
                                   new Boolean(selectionToolVisible));
    }

    /**
     * Is the cross tool visible?
     *
     * @return		boolean - is the cross tool visible
    **/
    public boolean isCrossToolVisible()
    {
        return crossToolVisible;
    }

    /**
     * Set the cross tool visibility.
     *
     * @param		aCrossToolVisible boolean - visible?
    **/
    public void setCrossToolVisible(boolean aCrossToolVisible)
    {
        // Return immediately if no change of state
        if (aCrossToolVisible == crossToolVisible)
        {
            return;
        }

        // Change
        boolean oldCrossToolVisible = crossToolVisible;
        crossToolVisible = aCrossToolVisible;

        // Show/Hide cross tool
        crossToolToggleButton.setVisible(crossToolVisible);
        updateSize();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.CROSS_TOOL_VISIBLE,
                                   new Boolean(oldCrossToolVisible),
                                   new Boolean(crossToolVisible));
    }

    /**
     * Is the snip tool visible?
     *
     * @return		boolean - is the snip tool visible
    **/
    public boolean isSnipToolVisible()
    {
        return snipToolVisible;
    }
    
   
    /**
     * Set the snip tool visibility.
     *
     * @param		aSnipToolVisible boolean - visible?
    **/
    public void setSnipToolVisible(boolean aSnipToolVisible)
    {
        // Return immediately if no change of state
        if (aSnipToolVisible == snipToolVisible)
        {
            return;
        }

        // Change
        boolean oldSnipToolVisible = snipToolVisible;
        snipToolVisible = aSnipToolVisible;

        // Show/Hide selection tool
        snipToolToggleButton.setVisible(snipToolVisible);
        updateSize();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.SNIP_TOOL_VISIBLE,
                                   new Boolean(oldSnipToolVisible),
                                   new Boolean(snipToolVisible));
    }
    
     /**
     * Is the pedigree tool visible?
     *
     * @return		boolean - is the pedigree tool visible
    **/
    public boolean isPedigreeToolVisible()
    {
        return pedigreeToolVisible;
    }
    
     /**
     * Set the pedigree tool visibility.
     *
     * @param		aPedigreeToolVisible boolean - visible?
    **/
    public void setPedigreeToolVisible(boolean aPedigreeToolVisible)
    {
        // Return immediately if no change of state
        if (aPedigreeToolVisible == pedigreeToolVisible)
        {
            return;
        }

        // Change
        boolean oldPedigreeToolVisible = pedigreeToolVisible;
        pedigreeToolVisible = aPedigreeToolVisible;

        // Show/Hide selection tool
        pedigreeToolToggleButton.setVisible(pedigreeToolVisible);
        updateSize();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.PEDIGREE_TOOL_VISIBLE,
                                   new Boolean(oldPedigreeToolVisible),
                                   new Boolean(pedigreeToolVisible));
    }



    /**
     * Is the chromosome tool visible?
     *
     * @return		boolean - is the chromosome tool visible
    **/
    public boolean isChromosomeToolVisible()
    {
        return chromosomeToolVisible;
    }

	 /**
     * set the text on the chromosomeView
     */
     public void setTextOnChromosomeView(String str)
     {
     	 if (pedigreeOrganismView == null) return;
     	 pedigreeOrganismView.setTextOnChromosomeView(str);
     }
    /**
     * Set the chromosome tool visibility.
     *
     * @param		aChromosomeToolVisible boolean - visible?
    **/
    public void setChromosomeToolVisible(boolean aChromosomeToolVisible)
    {
        // Return immediately if no change of state
        if (aChromosomeToolVisible == chromosomeToolVisible)
        {
            return;
        }

        // Change
        boolean oldChromosomeToolVisible = chromosomeToolVisible;
        chromosomeToolVisible = aChromosomeToolVisible;

        // Show/Hide chromosome tool
        chromosomeToolToggleButton.setVisible(chromosomeToolVisible);
        updateSize();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.CHROMOSOME_TOOL_VISIBLE,
                                   new Boolean(oldChromosomeToolVisible),
                                   new Boolean(chromosomeToolVisible));
    }
      
     /**
     * set pop up chromosome view visible
     *
     * @param		bln boolean
     */
     public void setChromosomeBoxVisible(boolean bln)
     {
     	 if (pedigreeOrganismView == null) return;
     	 pedigreeOrganismView.setChromosomeBoxVisible(bln);
     }
     
     /**
     * Is pop up chromosome view visible
     *
     * return boolean -- Is pop up chromosome view visible
     */
     public boolean isChromosomeBoxVisible(boolean bln)
     {
     	 if (pedigreeOrganismView == null) return false;
     	 
     	 return pedigreeOrganismView.isChromosomeBoxVisible();
     }
     

    /**
     * Is the trait pulldown visible?
     *
     * @return		boolean - is the trait pulldown visible?
    **/
    public boolean isTraitPulldownVisible()
    {
        return traitPulldownVisible;
    }

    /**
     * Set the trait pulldown visibility.
     *
     * @param		aTraitPulldownVisible boolean - visible?
    **/
    public void setTraitPulldownVisible(boolean aTraitPulldownVisible)
    {
        // Return immediately if no change of state
        if (aTraitPulldownVisible == traitPulldownVisible)
        {
            return;
        }

        // Change
        boolean oldTraitPulldownVisible = traitPulldownVisible;
        traitPulldownVisible = aTraitPulldownVisible;

        // Show/hide trait pulldown
        traitPulldown.setVisible(traitPulldownVisible);
        updateSize();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.TRAIT_PULLDOWN_VISIBLE,
                                   new Boolean(oldTraitPulldownVisible),
                                   new Boolean(traitPulldownVisible));
    }

    /**
     * Is the offspring mode pulldown visible?
     *
     * @return		boolean - is the offspring mode pulldown visible?
    **/
    public boolean isOffspringModePulldownVisible()
    {
        return offspringModePulldownVisible;
    }

    /**
     * Set the offspring mode pulldown visibility.
     *
     * @param		anOffspringModePulldownVisible boolean - visible?
    **/
    public void setOffspringModePulldownVisible(boolean anOffspringModePulldownVisible)
    {
        // Return immediately if no change of state
        if (anOffspringModePulldownVisible == offspringModePulldownVisible)
        {
            return;
        }

        // Change
        boolean oldOffspringModePulldownVisible = offspringModePulldownVisible;
        offspringModePulldownVisible = anOffspringModePulldownVisible;

        // Show/hide offspring mode pulldown and other related controls
        offspringModePulldown.setVisible(offspringModePulldownVisible);
        numberFixedOffspringTextField.setVisible(offspringModePulldownVisible);
        numberMaleOffspringTextField.setVisible(offspringModePulldownVisible);
        numberFemaleOffspringTextField.setVisible(offspringModePulldownVisible);
        minimumOffspringTextField.setVisible(offspringModePulldownVisible);
        maximumOffspringTextField.setVisible(offspringModePulldownVisible);
        applyButton.setVisible(offspringModePulldownVisible);

        updateSize();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.OFFSPRING_MODE_PULLDOWN_VISIBLE,
                                   new Boolean(oldOffspringModePulldownVisible),
                                   new Boolean(offspringModePulldownVisible));
    }

    /**
     * Set the trait for this view.  If aTrait is null, the whole organism's phenotype will be drawn.<p>
     *
     * @param		aTraitName String - the name of the trait to draw, may be null to cause whole organisms to be drawn
    **/
    public void setTrait(String aTraitName)
    {
        pedigreeOrganismView.setTrait(aTraitName);
        //revalidate();
        repaint();
    }

    /**
     * Get the trait for this view.<p>
     *
     * @return		String - the name of the trait to draw
    **/
    public String getTrait()
    {
        return pedigreeOrganismView.getTrait().getName();
    }

    /**
     * Get the current species.<p>
     *
     * @return		Species - the current species, may be null
    **/
    public Species getSpecies()
    {
        return pedigreeOrganismView.getSpecies();
    }

    /**
     * Set the current species.  Private because it should be set
     * via the addition of an organism to this view. <p>
     *
     * @param		aSpecies Species - new current species, may be null
    **/
    private void setSpecies(Species aSpecies)
    {
        pedigreeOrganismView.setSpecies(aSpecies);
    }

    /**
     * Get the fixed number of children from a breeding.<p>
     *
     * @return		int - fixed number of children from a breeding
    **/
    public int getFixedNumberChildren()
    {
        return pedigreeOrganismView.getFixedNumberChildren();
    }

    /**
     * Set the fixed number of children from a breeding.<p>
     *
     * @param		aFixedNumberChildren int - fixed number of children from a breeding
     * @exception	IllegalArgumentException - new maximum illegal, may be less than minimum
    **/
    public void setFixedNumberChildren(int aFixedNumberChildren)
    {
        pedigreeOrganismView.setFixedNumberChildren(aFixedNumberChildren);
    }

    /**
     * Get the maximum number of children from a breeding.<p>
     *
     * @return		int - maximum number of children from a breeding
    **/
    public int getMaximumNumberChildren()
    {
        return pedigreeOrganismView.getMaximumNumberChildren();
    }

    /**
     * Set the maximum number of children from a breeding.  This must
     * be higher than the minimum or else an exception is thrown.<p>
     *
     * @param		aMaximumNumberChildren int - maximum number of children from a breeding
     * @exception	IllegalArgumentException - new maximum illegal, may be less than minimum
    **/
    public void setMaximumNumberChildren(int aMaximumNumberChildren)
    {
        pedigreeOrganismView.setMaximumNumberChildren(aMaximumNumberChildren);
    }

    /**
     * Get the minimum number of children from a breeding.<p>
     *
     * @return		int - minimum number of children from a breeding
    **/
    public int getMinimumNumberChildren()
    {
        return pedigreeOrganismView.getMinimumNumberChildren();
    }

    /**
     * Set the minimum number of children from a breeding.  This must
     * be less than the maximum or else an exception is thrown.<p>
     *
     * @param		aMinimumNumberChildren int - minimum number of children from a breeding
     * @exception	IllegalArgumentException - new minimum illegal, may be more than maximum
    **/
    public void setMinimumNumberChildren(int aMinimumNumberChildren)
    {
        pedigreeOrganismView.setMinimumNumberChildren(aMinimumNumberChildren);
    }

    /**
     * Set the minimum and maximum number of children from a breeding.  The minimum
     * must be less than or equal to the maximum or else an exception is thrown.<p>
     *
     * @param		aMinimumNumberChildren int - minimum number of children from a breeding
     * @param		aMaximumNumberChildren int - maximum number of children from a breeding
     * @exception	IllegalArgumentException - new minimum illegal, may be more than maximum
    **/
    public void setMinMaxNumberChildren(int aMinimumNumberChildren, int aMaximumNumberChildren)
    {
        pedigreeOrganismView.setMinMaxNumberChildren(aMinimumNumberChildren, aMaximumNumberChildren);
    }

    /**
     * Get the number of female children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @return		int - number of female children
    **/
    public int getNumberFemaleChildren()
    {
        return pedigreeOrganismView.getNumberFemaleChildren();
    }

    /**
     * Set the number of female children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @param		aNumberFemaleChildren int - new number of female children, must be >= 0
     * @exception	IllegalArgumentException - new value illegal
    **/
    public void setNumberFemaleChildren(int aNumberFemaleChildren)
    {
        pedigreeOrganismView.setNumberFemaleChildren(aNumberFemaleChildren);
    }

    /**
     * Get the number of male children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @return		int - number of male children
    **/
    public int getNumberMaleChildren()
    {
        return pedigreeOrganismView.getNumberMaleChildren();
    }

    /**
     * Set the number of male children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @param		aNumberMaleChildren int - new number of male children, must be >= 0
     * @exception	IllegalArgumentException - new value illegal
    **/
    public void setNumberMaleChildren(int aNumberMaleChildren)
    {
        pedigreeOrganismView.setNumberMaleChildren(aNumberMaleChildren);
    }

    /**
     * Set the number of female and male children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @param		aNumberFemaleChildren int - new number of female children, must be >= 0
     * @param		aNumberMaleChildren int - new number of male children, must be >= 0
     * @exception	IllegalArgumentException - new value illegal
    **/
    public void setNumberMaleFemaleChildren(int aNumberFemaleChildren,
                                            int aNumberMaleChildren)
    {
        pedigreeOrganismView.setNumberMaleFemaleChildren(aNumberFemaleChildren,
                                                        aNumberMaleChildren);
    }

    /**
     * Set the bounds of this view.<p>
     *
     * @param		x int - x of top left corner
     * @param		y int - y of top left corner
     * @param		width int - width
     * @param		height int - height
    **/
    public void setBounds(int x, int y, int width, int height)
    {
        super.setBounds(x,y,width,height);
        updateSize();
    }


    /**
     * Get the current selection set
     *
     * @return		SelectionSet - the current selection set
    **/
    public SelectionSet getSelectionSet()
    {
        return pedigreeOrganismView.getSelectionSet();
    }

    /**
     * Set the current selection set.
     *
     * @param		aSelectionSet SelectionSet - a new selection set
    **/
    public void setSelectionSet(SelectionSet aSelectionSet)
    {
        pedigreeOrganismView.setSelectionSet(aSelectionSet);
    }
    
    public void setVisible(boolean isVisible)
    {
        super.setVisible(isVisible);
        if (isVisible)
            updateSize();
    }

    /**
     * Update the size of this view.  To do this, get the current size of the view
     * and update the models for anything shown in the view.  Do not repaint or generate
     * a repaint event, as that event is already coming automatically from AWT.
    **/
    public void updateSize()
    {
        Rectangle bounds = getBounds();

        // Update sizes of cell, graphics, models, etc.
        actualWidth = bounds.width;
        actualHeight = bounds.height;

        // Move pedigree organism view, tool buttons and pulldowns appropriately
        int yPosition = 56;
        if (selectionToolVisible)
        {
            selectionToolToggleButton.setBounds(2,yPosition,32,32);
            yPosition += 32;
        }
        if (crossToolVisible)
        {
            crossToolToggleButton.setBounds(2,yPosition,32,32);
            yPosition += 32;
        }
        if (snipToolVisible)
        {
            snipToolToggleButton.setBounds(2,yPosition,32,32);
            yPosition += 32;
        }
        if (chromosomeToolVisible)
        {
            chromosomeToolToggleButton.setBounds(2,yPosition,32,32);
            yPosition += 32;
        }
        if (pedigreeToolVisible)
        {
        	pedigreeToolToggleButton.setBounds(2,yPosition,32,32);
            yPosition += 32;
        }
        if (traitPulldownVisible)
        {
            traitPulldown.setBounds(45,2,115,24);
        }
        if (offspringModePulldownVisible)
        {
            offspringModePulldown.setBounds(235,2,115,24);
        }

        // Resize inner pedigree organism view appropriately
        if (selectionToolVisible || crossToolVisible || snipToolVisible || chromosomeToolVisible||pedigreeToolVisible)
        {
            if (traitPulldownVisible || offspringModePulldownVisible)
            {
                pedigreeOrganismViewScrollPane.setBounds(34,52,actualWidth-38,actualHeight-56);
            }
            else
            {
                pedigreeOrganismViewScrollPane.setBounds(34,26,actualWidth-38,actualHeight-30);
            }
        }
        else
        {
            if (traitPulldownVisible || offspringModePulldownVisible)
            {
                pedigreeOrganismViewScrollPane.setBounds(2,52,actualWidth-4,actualHeight-56);
            }
            else
            {
                pedigreeOrganismViewScrollPane.setBounds(2,26,actualWidth-4,actualHeight-30);
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Update the state (visibility, enabled, values) of the controls.<p>
    **/
    public void updateControls()
    {
        // Process any pending text edits
        processPendingTextEdits();

        // Set visibility and enabledness of tools
        selectionToolToggleButton.setVisible(selectionToolVisible);
        selectionToolToggleButton.setEnabled(selectionToolEnabled);
        if (activeTool == Tool.SELECTION)
        {
            selectionToolToggleButton.setSelected(true);
        }
        else
        {
            selectionToolToggleButton.setSelected(false);
        }

        crossToolToggleButton.setVisible(crossToolVisible);
        crossToolToggleButton.setEnabled(crossToolEnabled);
        if (activeTool == Tool.CROSS)
        {
            crossToolToggleButton.setSelected(true);
        }
        else
        {
            crossToolToggleButton.setSelected(false);
        }

        snipToolToggleButton.setVisible(snipToolVisible);
        snipToolToggleButton.setEnabled(snipToolEnabled);
        if (activeTool == Tool.SNIP)
        {
            snipToolToggleButton.setSelected(true);
        }
        else
        {
            snipToolToggleButton.setSelected(false);
        }

        
        chromosomeToolToggleButton.setVisible(snipToolVisible);
        chromosomeToolToggleButton.setEnabled(snipToolEnabled);
        if (activeTool == Tool.CHROMOSOME)
        {
            chromosomeToolToggleButton.setSelected(true);
        }
        else
        {
            chromosomeToolToggleButton.setSelected(false);
        }
        
        pedigreeToolToggleButton.setVisible(pedigreeToolVisible);
        pedigreeToolToggleButton.setEnabled(pedigreeToolEnabled);
        if (activeTool == Tool.PEDIGREE)
        {
            pedigreeToolToggleButton.setSelected(true);
        }
        else
        {
            pedigreeToolToggleButton.setSelected(false);
        }

        // Set state of trait pulldown
        traitPulldown.setVisible(traitPulldownVisible);
        traitPulldown.setEnabled(traitPulldownEnabled);
        crossOverCheckBox.setVisible(blnCrossOverVisible);
        crossOverCheckBox.setEnabled(blnCrossOverEnabled);
        // Trait pulldown selected item set by pedigree organism view

        // Set state of offspring controls
        if (pedigreeOrganismView != null)
        {
            int offspringMode = pedigreeOrganismView.getOffspringMode();

            if (offspringMode == OFFSPRING_MODE_FIXED)
            {
                offspringModePulldown.setSelectedItem(OFFSPRING_MODE_FIXED_STRING);

                offspringModePulldown.setVisible(offspringModePulldownVisible);
                numberFixedOffspringTextField.setVisible(offspringModePulldownVisible);
                applyButton.setVisible(offspringModePulldownVisible);

                numberMaleOffspringTextField.setVisible(false);
                numberFemaleOffspringTextField.setVisible(false);
                minimumOffspringTextField.setVisible(false);
                maximumOffspringTextField.setVisible(false);

                offspringModePulldown.setEnabled(offspringModePulldownEnabled);
                numberFixedOffspringTextField.setEnabled(offspringModePulldownEnabled);
                applyButton.setEnabled(offspringModePulldownEnabled);

                numberMaleOffspringTextField.setEnabled(false);
                numberFemaleOffspringTextField.setEnabled(false);
                minimumOffspringTextField.setEnabled(false);
                maximumOffspringTextField.setEnabled(false);
            }
            else if (offspringMode == OFFSPRING_MODE_MIN_MAX)
            {
                offspringModePulldown.setSelectedItem(OFFSPRING_MODE_MIN_MAX_STRING);

                offspringModePulldown.setVisible(offspringModePulldownVisible);
                minimumOffspringTextField.setVisible(offspringModePulldownVisible);
                maximumOffspringTextField.setVisible(offspringModePulldownVisible);
                applyButton.setVisible(offspringModePulldownVisible);

                numberFixedOffspringTextField.setVisible(false);
                numberMaleOffspringTextField.setVisible(false);
                numberFemaleOffspringTextField.setVisible(false);

                offspringModePulldown.setEnabled(offspringModePulldownEnabled);
                minimumOffspringTextField.setEnabled(offspringModePulldownEnabled);
                maximumOffspringTextField.setEnabled(offspringModePulldownEnabled);
                applyButton.setEnabled(offspringModePulldownEnabled);

                numberFixedOffspringTextField.setEnabled(false);
                numberMaleOffspringTextField.setEnabled(false);
                numberFemaleOffspringTextField.setEnabled(false);
            }
            else if (offspringMode == OFFSPRING_MODE_MALE_FEMALE)
            {
                offspringModePulldown.setSelectedItem(OFFSPRING_MODE_MALE_FEMALE_STRING);

                offspringModePulldown.setVisible(offspringModePulldownVisible);
                numberMaleOffspringTextField.setVisible(offspringModePulldownVisible);
                numberFemaleOffspringTextField.setVisible(offspringModePulldownVisible);
                applyButton.setVisible(offspringModePulldownVisible);

                numberFixedOffspringTextField.setVisible(false);
                minimumOffspringTextField.setVisible(false);
                maximumOffspringTextField.setVisible(false);

                offspringModePulldown.setEnabled(offspringModePulldownEnabled);
                numberMaleOffspringTextField.setEnabled(offspringModePulldownEnabled);
                numberFemaleOffspringTextField.setEnabled(offspringModePulldownEnabled);
                applyButton.setEnabled(offspringModePulldownEnabled);

                numberFixedOffspringTextField.setEnabled(false);
                minimumOffspringTextField.setEnabled(false);
                maximumOffspringTextField.setEnabled(false);
            }
        }

        // Set values of various text fields
        if (numberFixedOffspringTextField != null)
        {
            numberFixedOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getFixedNumberChildren()));
            numberMaleOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getNumberMaleChildren()));
            numberFemaleOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getNumberFemaleChildren()));
            minimumOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getMinimumNumberChildren()));
            maximumOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getMaximumNumberChildren()));
        }
        repaint();
    }
    
 	 /**
     * Is Mutation Trait Legend visible?
     *
     * @return		boolean - blnMutationLegendVisible
    **/
   public boolean isMutationTraitLegendVisible()
    {
    	return blnMutationLegendVisible;
    }

    /**
     * Set Mutation Trait Legend visible flag
     *
     * @param		onlyLiveChildren boolean - only live children?
    **/
    public void setMutationTraitLegendVisible(boolean blnVisible)
    {
    	if (blnVisible == blnMutationLegendVisible)
    	{
    		return;
    	}
    	blnMutationLegendVisible = blnVisible;
    }
   

    /**
     * Draw the graphics in this view.
     *
     * @param 		g Graphics - the given graphics to use in drawing
    **/
    public void paintComponent(Graphics g)
    {
        // Let superclass do real drawing
        super.paintComponent(g);

        // Get bounds and paint background
        Rectangle bounds = getBounds();
        paintBackground(g,bounds);

        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Set font and color
        g.setFont(getFont());
        g.setColor(getForeground());

        // Draw text 
        if (traitPulldownVisible)
        {
            g.drawString("",5,18);
        }
        if (offspringModePulldownVisible)
        {
            g.drawString("Offspring:",175,18);
        }
        int offspringMode = pedigreeOrganismView.getOffspringMode();
        if (offspringMode == OFFSPRING_MODE_FIXED)
        {
        	Enumeration allFamilies = getFamilies();
        	int num = 0;
        	while (allFamilies.hasMoreElements())
        	{
        		PedigreeFamily pf = (PedigreeFamily)(allFamilies.nextElement());
        		num = num + (pf.getFamily()).getNumberOfChildren();
        	}
        	if (num<0)
        		num = 0;
        	String str = "Number of Offspring: " + String.valueOf(num);
            g.drawString(str,360,18);
           
        }
        else if (offspringMode == OFFSPRING_MODE_MIN_MAX)
        {
            g.drawString("Min:",370,18);
            g.drawString(String.valueOf(pedigreeOrganismView.getMinimumNumberChildren()),400,18);
            g.drawString("Max:",470,18);
            g.drawString(String.valueOf(pedigreeOrganismView.getMaximumNumberChildren()),500,18);
        }
        else if (offspringMode == OFFSPRING_MODE_MALE_FEMALE)
        {
            g.drawString("Male:",365,18);
            g.drawString("Female:",463,18);
        }

        // Draw legend if there is a current trait
        Trait currentTrait = pedigreeOrganismView.getTrait();
        if (currentTrait != null)
        {
            boolean showMaleAndFemale = true;
            if (currentTrait.getSpecies().getDiploidType() == Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
            {
                showMaleAndFemale = false;
            }

            int x = 35;
            int spacing = 90;
            Color color1, color2;
            Characteristic aCharacteristic;
            Enumeration eCharacteristics = currentTrait.getCharacteristics();
          
         
            if (blnShowLegend)
            {
	            while (eCharacteristics.hasMoreElements())
	            {
	            	
	                aCharacteristic = (Characteristic) eCharacteristics.nextElement();
	               // System.out.println("aCharacteristic.toString()"+aCharacteristic.toString());
	                if (mutationCharacteristic.contains(aCharacteristic.toString()))
	                {
	                	if (!blnMutationLegendVisible)
	                		continue;
	                }
	                color1 = aCharacteristic.getPedigreeSymbolFirstColor();
	                color2 = aCharacteristic.getPedigreeSymbolSecondColor();
	                switch (aCharacteristic.getPedigreeSymbolType())
	                {
	                    case Characteristic.PEDIGREE_SYMBOL_SOLID_COLOR:
	                    {
	                        if (showMaleAndFemale)
	                        {
	                            g.setColor(color1);
	                            g.fillOval(x, 35, 10, 10);
	                            g.fillRect(x+12, 35, 10, 10);
	    
	                            g.setColor(Color.black);
	                            g.drawOval(x, 35, 10, 10);
	                            g.drawRect(x+12, 35, 10, 10);
	                        }
	                        else
	                        {
	                            g.setColor(color1);
	                            g.fillOval(x, 35, 10, 10);

	                            g.setColor(Color.black);
	                            g.drawOval(x, 35, 10, 10);
	                        }
	    
	                        x += 14;
	                    }
	                    break;
	            
	                    case Characteristic.PEDIGREE_SYMBOL_FORWARD_SLASH:
	                    {
	                        if (showMaleAndFemale)
	                        {
	                            g.setColor(color1);
	                            g.fillOval(x, 35, 10, 10);
	                            g.setColor(color2);
	                            g.fillArc(x, 35, 10, 10, 45, -180);
	    
	                            g.setColor(color1);
	                            g.fillRect(x+12, 35, 10, 10);
	                            Polygon p = new Polygon();
	                            p.addPoint(x+12,45);
	                            p.addPoint(x+22,35);
	                            p.addPoint(x+22,45);
	                            p.addPoint(x+12,45);
	                            g.setColor(color2);
	                            g.fillPolygon(p);
	    
	                            g.setColor(Color.black);
	                            g.drawOval(x, 35, 10, 10);
	                            g.drawRect(x+12, 35, 10, 10);
	                        }
	                        else
	                        {
	                            g.setColor(color1);
	                            g.fillOval(x, 35, 10, 10);
	                            g.setColor(color2);
	                            g.fillArc(x, 35, 10, 10, 45, -180);

	                            g.setColor(Color.black);
	                            g.drawOval(x, 35, 10, 10);
	                        }

	                        x += 14;
	                    }
	                    break;
	                }

	                // Draw characteristic name
	                g.setColor(Color.black);
	                x += 15;
	                g.drawString(aCharacteristic.getName(),x,44);
	                
	                if (currentTrait.toString().equals("Trait: Color")){
	                	//spacing = 8*(aCharacteristic.getName()).length()+10;
	                	String str = aCharacteristic.getName();
	                	spacing = g.getFontMetrics(g.getFont()).stringWidth(str) + 5;
	                }
	               
	                x += spacing;
	            }
           }
        }
    }

    /**
     * React to actions
    **/
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if (cmd.equals(Tool.COMMAND_SELECTION))
        {
            setActiveTool(Tool.SELECTION);
        }
        else if (cmd.equals(Tool.COMMAND_CROSS))
        {
            setActiveTool(Tool.CROSS);
        }
        else if (cmd.equals(Tool.COMMAND_SNIP))
        {
            setActiveTool(Tool.SNIP);
        }
        else if (cmd.equals(Tool.COMMAND_PEDIGREE))
        {
        	setActiveTool(Tool.PEDIGREE);
        }
        else if (cmd.equals(Tool.COMMAND_CHROMOSOME))
        {
            setActiveTool(Tool.CHROMOSOME);
        }
        else if (cmd.equals(cmdApply))
        {
            // Do nothing, as hitting this button causes focus to change (pretty slick, eh?)
        }
        else
        {
            // Pedigree and other tools default to selection
            setActiveTool(Tool.SELECTION);
        }
        
        if (e.getSource()==crossOverCheckBox)
        {
        	boolean bln = crossOverCheckBox.isSelected();
        	setCrossOverTurnOn(bln);
        }
    }

    /**
     * Handle combo box item changed events.
     *
     * @param	event ItemEvent - change event to handle
    **/
    public void itemStateChanged(ItemEvent event)
    {
        JComboBox comboBox = (JComboBox) event.getSource();

        // Return immediately if combo box null
        if (comboBox == null)
        {
            return;
        }
		if (callLevel > 0)
			return;
		callLevel++;
		try
		{
	        // Only combo boxes can generate this event now
	        if (comboBox == traitPulldown)
	        {
	            // Set trait in pedigree organism view
	            SwingUtilities.invokeLater(new Runnable(){
	                public void run(){
	                    pedigreeOrganismView.setTrait((String)traitPulldown.getSelectedItem());
	                    repaint();
	                }
	            });
	        
	       }
	        else if (comboBox == offspringModePulldown)
	        {
	            // Set new offspring mode in pedigree organism view
	            String s = (String) offspringModePulldown.getSelectedItem();
	            if (s.equals(OFFSPRING_MODE_FIXED_STRING))
	            {
	                pedigreeOrganismView.setOffspringMode(OFFSPRING_MODE_FIXED);
	            }
	            else if (s.equals(OFFSPRING_MODE_MIN_MAX_STRING))
	            {
	                pedigreeOrganismView.setOffspringMode(OFFSPRING_MODE_MIN_MAX);
	            }
	            else if (s.equals(OFFSPRING_MODE_MALE_FEMALE_STRING))
	            {
	                pedigreeOrganismView.setOffspringMode(OFFSPRING_MODE_MALE_FEMALE);
	            }
	
	            // Update controls
	            updateControls();
	        }
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		callLevel--;
    }

    /**
     * Component events
    **/
    public void componentHidden(ComponentEvent event)
    {
    }

    public void componentMoved(ComponentEvent event)
    {
    }

    public void componentResized(ComponentEvent event)
    {
        if (event != null && event.getSource() == this)
        {
            updateSize();
        }
    }

    public void componentShown(ComponentEvent event)
    {
    }


	
    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        // Refire property change events from pedigree organism view, as
        // listeners don't listen to both this view and the pedigree
        // organism view.  So we make them look identical.
        if (event.getSource() == pedigreeOrganismView)
        {
            String propertyName = event.getPropertyName();
            Object oldValue = event.getOldValue();
            Object newValue = event.getNewValue();

            if (propertyName != null &&
                (propertyName.equals(UIProp.OFFSPRING_MODE) ||
                 propertyName.equals(UIProp.FIXED_NUMBER_CHILDREN) ||
                 propertyName.equals(UIProp.MAXIMUM_NUMBER_CHILDREN) ||
                 propertyName.equals(UIProp.MINIMUM_NUMBER_CHILDREN) ||
                 propertyName.equals(UIProp.NUMBER_FEMALE_CHILDREN) ||
                 propertyName.equals(UIProp.NUMBER_MALE_CHILDREN)))
            {
                updateControls();
            }
    
            changes.firePropertyChange(propertyName,oldValue,newValue);
        }
    }

    /**
     * Is the lock symbol visible?
     *
     * @return		boolean - is the lock symbol visible
    **/
    public boolean isLockSymbolVisible()
    {
        return pedigreeOrganismView.isLockSymbolVisible();
    }

    /**
     * Set the lock symbol visible boolean.
     *
     * @param		aLockSymbolVisible boolean - visible?
    **/
    public void setLockSymbolVisible(boolean aLockSymbolVisible)
    {
        pedigreeOrganismView.setLockSymbolVisible(aLockSymbolVisible);
    }

    /**
     * Is the characteristics text visible?
     *
     * @return		boolean - is the characteristics text visible
    **/
    public boolean isCharacteristicsTextVisible()
    {
        return pedigreeOrganismView.isCharacteristicsTextVisible();
    }

    /**
     * Set the characteristics text visible boolean.
     *
     * @param		aCharacteristicsTextVisible boolean - visible?
    **/
    public void setCharacteristicsTextVisible(boolean aCharacteristicsTextVisible)
    {
        pedigreeOrganismView.setCharacteristicsTextVisible(aCharacteristicsTextVisible);
    }

    /**
     * Is the name text visible?
     *
     * @return		boolean - is the name text visible
    **/
    public boolean isNameTextVisible()
    {
        return pedigreeOrganismView.isNameTextVisible();
    }

    /**
     * Set the name text visible boolean.
     *
     * @param		aNameTextVisible boolean - visible?
    **/
    public void setNameTextVisible(boolean aNameTextVisible)
    {
        pedigreeOrganismView.setNameTextVisible(aNameTextVisible);
    }

    /**
     * Is the sex text visible?
     *
     * @return		boolean - is the sex text visible
    **/
    public boolean isSexTextVisible()
    {
        return pedigreeOrganismView.isSexTextVisible();
    }

    /**
     * Set the sex text visible boolean.
     *
     * @param		aSexTextVisible boolean - visible?
    **/
    public void setSexTextVisible(boolean aSexTextVisible)
    {
        pedigreeOrganismView.setSexTextVisible(aSexTextVisible);
    }

    /**
     * Is the species text visible?
     *
     * @return		boolean - is the species text visible
    **/
    public boolean isSpeciesTextVisible()
    {
        return pedigreeOrganismView.isSpeciesTextVisible();
    }

    /**
     * Set the species text visible boolean.
     *
     * @param		aSpeciesTextVisible boolean - visible?
    **/
    public void setSpeciesTextVisible(boolean aSpeciesTextVisible)
    {
        pedigreeOrganismView.setSpeciesTextVisible(aSpeciesTextVisible);
    }

    /**
     * Get the organism image size for this view.<p>
     *
     * @return		int - organism image size
    **/
    public int getOrganismImageSize()
    {
        return pedigreeOrganismView.getOrganismImageSize();
    }

    /**
     * Set the organism image size for this view.  The size must
     * be one of the SpeciesImage.XXX_IMAGE_SIZE static values.<p>
     *
     * @param		anOrganismImageSize int - a new organism image size
     * @exception	IllegalArgumentException - illegal input value
    **/
    public void setOrganismImageSize(int anOrganismImageSize)
    {
        pedigreeOrganismView.setOrganismImageSize(anOrganismImageSize);
    }

    /**
     * Get the text indent for this view.<p>
     *
     * @return		int - text indent
    **/
    public int getTextIndent()
    {
        return pedigreeOrganismView.getTextIndent();
    }

    /**
     * Set the text indent for this view.<p>
     *
     * @param		aTextIndent int - a new text indent
    **/
    public void setTextIndent(int aTextIndent)
    {
        pedigreeOrganismView.setTextIndent(aTextIndent);
    }

    /**
     * Get the text line spacing for this view, the number of
     * pixels between lines of text below an organism.<p>
     *
     * @return		int - text line spacing
    **/
    public int getTextLineSpacing()
    {
        return pedigreeOrganismView.getTextLineSpacing();
    }

    /**
     * Set the text line spacing for this view, the number of pixels
     * between lines of text below an organism.<p>
     *
     * @param		aTextLineSpacing int - a new text line spacing
    **/
    public void setTextLineSpacing(int aTextLineSpacing)
    {
        pedigreeOrganismView.setTextLineSpacing(aTextLineSpacing);
    }

    /**
     * Handle focus gained event - do nothing in most cases.
     *
     * @param		event FocusEvent - focus gained event
    **/
    public void focusGained(FocusEvent event)
    {
        focusControl = event.getSource();

        // Enable apply button
        applyButton.setEnabled(true);
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

        // No control has focus temporarily
        focusControl = null;

        // Disable apply button
        // applyButton.setEnabled(false);
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

            // Null out focus control to avoid reprocessing text in it
            focusControl = null;

            // Disable apply button
            // applyButton.setEnabled(false);
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
        if (textField != null)
        {
            String newText = textField.getText();

            if (textField == numberFixedOffspringTextField)
            {
                try
                {
                    int newNumber = Integer.parseInt(newText);
                    pedigreeOrganismView.setFixedNumberChildren(newNumber);
                }
                catch (NumberFormatException exception2)
                {
                    numberFixedOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getFixedNumberChildren()));
                }
            }
            else if (textField == maximumOffspringTextField)
            {
                try
                {
                    int newNumber = Integer.parseInt(newText);
                    pedigreeOrganismView.setMaximumNumberChildren(newNumber);
                }
                catch (NumberFormatException exception2)
                {
                    // Do nothing here
                }

                // Set both because setting one may modify the other
                minimumOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getMinimumNumberChildren()));
                maximumOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getMaximumNumberChildren()));
            }
            else if (textField == minimumOffspringTextField)
            {
                try
                {
                    int newNumber = Integer.parseInt(newText);
                    pedigreeOrganismView.setMinimumNumberChildren(newNumber);
                }
                catch (NumberFormatException exception2)
                {
                    // Do nothing
                }

                // Set both because setting one may modify the other
                minimumOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getMinimumNumberChildren()));
                maximumOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getMaximumNumberChildren()));
            }
            else if (textField == numberFemaleOffspringTextField)
            {
                try
                {
                    int newNumber = Integer.parseInt(newText);
                    pedigreeOrganismView.setNumberFemaleChildren(newNumber);
                }
                catch (NumberFormatException exception2)
                {
                    numberFemaleOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getNumberFemaleChildren()));
                }
            }
            else if (textField == numberMaleOffspringTextField)
            {
                try
                {
                    int newNumber = Integer.parseInt(newText);
                    pedigreeOrganismView.setNumberMaleChildren(newNumber);
                }
                catch (NumberFormatException exception2)
                {
                    numberMaleOffspringTextField.setText(Integer.toString(pedigreeOrganismView.getNumberMaleChildren()));
                }
            }
        }
    }



	public Element serializeAsElement(Document document){
		return  null;
	}

	public Element serializeAsElementLog(Document document){
		return  serializeAsElement(document);
	}

	public void save(Writer writer){}
	public void restore(Reader reader){}
	
	public void save(OutputStream output){}
	public void restore(InputStream input){}


	public DomainView create(String vewSpec){return this;}
	public void closeEverything(){}
	public void release(DomainView view){}
	public Vector getViewMethods(){return null;}
	public Vector getViewEvents(){return null;}
	public Vector getViewActions(){return null;}
	
	public DomainEngine getEngine(){return null;}
	
	public void setActivity(Object activity){
		if(pedigreeOrganismView != null) pedigreeOrganismView.setActivity(activity);
	}
	
	public int getSerializingLevel(){return SERIALIZING_LEVEL_HIGH;}
	public void setSerializingLevel(int val){}
	public int getLogSerializingLevel(){return SERIALIZING_LEVEL_BASIC;}
	public void setLogSerializingLevel(int val){}
	
}

