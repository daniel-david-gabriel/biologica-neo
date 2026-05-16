//
// Class : MeiosisView - the main sex view that holds the subviews for creating offspring, doing
//					 meiosis and fertilization, etc.
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1 $
// $Date: 2004/09/10 12:35:18 $
// $Author: eburke $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.concord.biologica.engine.*;

/**
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.ALIGNMENT_CONTROLS_VISIBLE - alignment controls visibility changed
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM - the user clicked on an organism with the chromosome tool
 * <li> UIProp.CROSSOVER_CONTROLS_VISIBLE - crossover controls visibility changed
 * <li> UIProp.FATHER_ORGANISM - the father organism shown in this sex view changed
 * <li> UIProp.FERTILIZATION_STARTED_AND_STOPPED - the fertilization started and stopped immediately
 * <li> UIProp.FERTILIZATION_STEP - the fertilization step changed
 * <li> UIProp.FERTILIZATION_MANUALLY_DISABLED - the manually disabled state of fertilization changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * <li> UIProp.MEIOSIS_FATHER_STARTED_AND_STOPPED - the father meiosis started and stopped immediately
 * <li> UIProp.MEIOSIS_FATHER_STEP - the father meiosis step has changed
 * <li> UIProp.MEIOSIS_MOTHER_STARTED_AND_STOPPED - the mother meiosis started and stopped immediately
 * <li> UIProp.MEIOSIS_MOTHER_STEP - the mother meiosis step has changed
 * <li> UIProp.MOTHER_ORGANISM - the mother organism shown in this sex view changed
 * <li> UIProp.MOVED_FATHER_GAMETE - the moved father gamete of this view changed
 * <li> UIProp.MOVED_MOTHER_GAMETE - the moved mother gamete of this view changed
 * <li> UIProp.NAME_TEXT_VISIBLE - the name text visibility boolean changed
 * <li> UIProp.OFFSPRING_ORGANISM - the offspring organism shown in this sex view changed
 * <li> UIProp.ORGANISM_IMAGE_SIZE - the image size to use for drawing organisms in this view
 * <li> UIProp.ORGANISM_VIEW_HEIGHT_PERCENTAGE - the percentage of the view height devoted to the organisms changed
 * <li> UIProp.REPLAY_BUTTON_ENABLED - the replay button enabled changed
 * <li> UIProp.REPLAY_BUTTON_VISIBLE - the replay button visibility changed
 * <li> UIProp.RESET_AUTOMATIC - reset is automatic when an offspring is made
 * <li> UIProp.RESET_BUTTON_ENABLED - the reset button enabled changed
 * <li> UIProp.RESET_BUTTON_VISIBLE - the reset button visibility changed
 * <li> UIProp.RESET_DELETE_OFFSPRING_ORGANISM - the reset causing the deletion of the offspring organism changed
 * <li> UIProp.SELECTED_FATHER_GAMETE - the selected father gamete of this view changed
 * <li> UIProp.SELECTED_MOTHER_GAMETE - the selected mother gamete of this view changed
 * <li> UIProp.SEX_TEXT_VISIBLE - the sex text visibility boolean changed
 * <li> UIProp.SEX_VIEW_MODE - the sex view mode changed (eg. 6 pane mode, etc.)
 * <li> UIProp.SPECIES_TEXT_VISIBLE - the species text visibility boolean changed
 * <li> UIProp.STOP_WHEN_FERTILIZATION_STARTS - the stop when fertilization starts flag changed
 * <li> UIProp.STOP_WHEN_MEIOSIS_STARTS - the stop when meiosis starts flag changed
 * <li> UIProp.TEXT_INDENT - the indentation of text from left edge of image
 * <li> UIProp.TEXT_LINE_SPACING - the number of pixels between lines of text
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#ALIGNMENT_CONTROLS_VISIBLE
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#CHARACTERISTICS_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#CHROMOSOME_TOOL_PICK_ON_ORGANISM
 * @see org.concord.biologica.ui.UIProp#CROSSOVER_CONTROLS_VISIBLE
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_STARTED_AND_STOPPED
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_STEP
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_MANUALLY_DISABLED
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FATHER_ORGANISM
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_FATHER_STARTED_AND_STOPPED
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_FATHER_STEP
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_MOTHER_STARTED_AND_STOPPED
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_MOTHER_STEP
 * @see org.concord.biologica.ui.UIProp#MOTHER_ORGANISM
 * @see org.concord.biologica.ui.UIProp#MOVED_FATHER_GAMETE
 * @see org.concord.biologica.ui.UIProp#MOVED_MOTHER_GAMETE
 * @see org.concord.biologica.ui.UIProp#NAME_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#OFFSPRING_ORGANISM
 * @see org.concord.biologica.ui.UIProp#ORGANISM_IMAGE_SIZE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_LAYOUT_STYLE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_VIEW_HEIGHT_PERCENTAGE
 * @see org.concord.biologica.ui.UIProp#REPLAY_BUTTON_ENABLED
 * @see org.concord.biologica.ui.UIProp#REPLAY_BUTTON_VISIBLE
 * @see org.concord.biologica.ui.UIProp#RESET_AUTOMATIC
 * @see org.concord.biologica.ui.UIProp#RESET_BUTTON_ENABLED
 * @see org.concord.biologica.ui.UIProp#RESET_BUTTON_VISIBLE
 * @see org.concord.biologica.ui.UIProp#RESET_DELETE_OFFSPRING_ORGANISM
 * @see org.concord.biologica.ui.UIProp#SELECTED_FATHER_GAMETE
 * @see org.concord.biologica.ui.UIProp#SELECTED_MOTHER_GAMETE
 * @see org.concord.biologica.ui.UIProp#SEX_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SEX_VIEW_MODE
 * @see org.concord.biologica.ui.UIProp#SPECIES_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#STOP_WHEN_FERTILIZATION_STARTS
 * @see org.concord.biologica.ui.UIProp#STOP_WHEN_MEIOSIS_STARTS
 * @see org.concord.biologica.ui.UIProp#TEXT_INDENT
 * @see org.concord.biologica.ui.UIProp#TEXT_LINE_SPACING
 * @see java.beans.PropertyChangeListener
**/
public final class MeiosisView
extends OrganismView
implements PropertyChangeListener, ActionListener, ComponentListener
{
    // Commands - must be unique
    static private final String cmdReplay				= "cmdReplay";
    static private final String cmdReset				= "cmdReset";
    static private final String cmdMoveMotherGameteIntoFertilization	= "cmdMoveMotherGameteIntoFertilization";
    static private final String cmdMoveMotherGameteOutOfFertilization	= "cmdMoveMotherGameteOutOfFertilization";
    static private final String cmdMoveFatherGameteIntoFertilization	= "cmdMoveFatherGameteIntoFertilization";
    static private final String cmdMoveFatherGameteOutOfFertilization	= "cmdMoveFatherGameteOutOfFertilization";
    static private final String cmdAutoSelectMotherGamete = "cmdAutoSelectMotherGamete";
    static private final String cmdAutoSelectFatherGamete = "cmdAutoSelectFatherGamete";

    // Possible modes / subview organizations for this view
    static public final int SEX_VIEW_MODE_NO_VIEWS				= 0;
    static public final int SEX_VIEW_MODE_SIX_VIEWS 			= 1;
    static public final int SEX_VIEW_MODE_MEIOSIS_VIEW_MOTHER 	= 2;
    static public final int SEX_VIEW_MODE_MEIOSIS_VIEW_FATHER 	= 3;
    static public final int SEX_VIEW_MODE_FERTILIZATION_VIEW	= 4;
    static public final int SEX_VIEW_MODE_ALL_VIEWS				= 5;

    // Height reserved for text at top of sex view
    static public final int SEX_VIEW_TOP_TEXT_HEIGHT = 25;

    // Height reserved for replay and reset buttons at bottom of sex view
    static public final int SEX_VIEW_BOTTOM_BUTTON_HEIGHT = 25;

    /**
     * Organism view height percentage.  Must be between 0 and 100;
    **/
    private int organismViewHeightPercentage = 0;

    /**
     * Sex view mode (e.g. number and type of subviews)
    **/
    private int sexViewMode = SEX_VIEW_MODE_NO_VIEWS;

    /**
     * Mother organism view
    **/
    private JComponent motherOrganismView;

    /**
     * Offspring organism view
    **/
    private JComponent offspringOrganismView;

    /**
     * Father organism view
    **/
    private JComponent fatherOrganismView;

    /**
     * Mother small meiosis view
    **/
    private SmallMeiosisView motherSmallMeiosisView;

    /**
     * Offspring small fertilization view
    **/
    private SmallFertilizationView smallFertilizationView;

    /**
     * Father small meiosis view
    **/
    private SmallMeiosisView fatherSmallMeiosisView;

    /**
     * Big meiosis view
    **/
    private BigMeiosisView bigMeiosisView;

    /**
     * Big fertilization view
    **/
    private BigFertilizationView bigFertilizationView;

    /**
     * Current mother
    **/
    private Organism motherOrganism;

    /**
     * Current father
    **/
    private Organism fatherOrganism;

    /**
     * Current offspring
    **/
    private Organism offspringOrganism;

    /**
     * Updating state
    **/
    private boolean updatingState = false;

    /**
     * Fertilization manually disabled.  This is a manual override
     * of the normal enabling of fertilization.  If this value is
     * true, fertilization will be disabled regardless of whether
     * the mother and father organisms have gametes, etc.
     * If this value is false, fertilization will enabled normally
     * when the mother and father have gone through meiosis and
     * gametes have been selected.<p>
    **/
    private boolean fertilizationManuallyDisabled = false;

    /**
     * Mother's meiosis model, shared among multiple views
    **/
    private MeiosisModel motherMeiosisModel = null;

    /**
     * Father's meiosis model, shared among multiple views
    **/
    private MeiosisModel fatherMeiosisModel = null;

    /**
     * Offspring's fertilization model, shared among multiple views
    **/
    private FertilizationModel offspringFertilizationModel = null;

    /**
     * Is a reset automatic when an offspring is created?  Defaults to false.<p>
     *
     * When this flag changes, a UIProp.RESET_AUTOMATIC property
     * change event is fired.
    **/
    private boolean resetAutomatic = false;

    /**
     * Reset button enabled?  Defaults to true.<p>
     *
     * When this flag changes, a UIProp.RESET_BUTTON_ENABLED property
     * change event is fired.
    **/
    private boolean resetButtonEnabled = true;

    /**
     * Reset button visible?  Defaults to true.<p>
     *
     * When this flag changes, a UIProp.RESET_BUTTON_VISIBLE property
     * change event is fired.
    **/
    private boolean resetButtonVisible = true;

    /**
     * Reset deletes the offspring organism, if one exists?  Defaults to false.<p>
     *
     * When this flag changes, a UIProp.RESET_DELETE_OFFSPRING_ORGANISM property
     * change event is fired.
    **/
    private boolean resetDeleteOffspringOrganism = false;

    /**
     * Reset button
    **/
    private JButton resetButton = null;

    /**
     * Gamete move buttons enabled?  Defaults to true.<p>
     *
     * When this flag changes, a UIProp.REPLAY_BUTTON_ENABLED property
     * change event is fired.
    **/
    private boolean gameteMoveButtonEnabled = true;
    
    /**
     * Replay button enabled?  Defaults to true.<p>
     *
     * When this flag changes, a UIProp.REPLAY_BUTTON_ENABLED property
     * change event is fired.
    **/
    private boolean replayButtonEnabled = true;

    /**
     * Replay button visible?  Defaults to true.<p>
     *
     * When this flag changes, a UIProp.REPLAY_BUTTON_VISIBLE property
     * change event is fired.
    **/
    private boolean replayButtonVisible = true;

    /**
     * Replay button
    **/
    private JButton replayButton = null;

    /**
     * Stop fertilization immediately when fertilization starts?  Defaults to false.<p>
     *
     * When this flag changes, a UIProp.STOP_WHEN_FERTILIZATION_STARTS property
     * change event is fired.
    **/
    private boolean stopWhenFertilizationStarts = false;

    /**
     * Stop meiosis immediately when meiosis starts?  Defaults to false.<p>
     *
     * When this flag changes, a UIProp.STOP_WHEN_MEIOSIS_STARTS property
     * change event is fired.
    **/
    private boolean stopWhenMeiosisStarts = false;

    /**
     * Mother move gamete into fertilization button
    **/
    private JButton motherMoveGameteIntoFertilizationButton = null;

    /**
     * Mother move gamete into fertilization button
    **/
    private JButton motherMoveGameteOutOfFertilizationButton = null;

    /**
     * Father move gamete into fertilization button
    **/
    private JButton fatherMoveGameteIntoFertilizationButton = null;

    /**
     * Father move gamete out of fertilization button
    **/
    private JButton fatherMoveGameteOutOfFertilizationButton = null;
    
    /**
    *	Mother auto select gamete button
    */
    private JButton selectGameteButton1;
    
    /**
    * Father auto select gamete button
    */
    private JButton selectGameteButton2;
    
    /**
    * Auto select gamete buttons enabled?
    */
    private boolean selectGameteButtonEnabled = true;
    
    /**
    *	Auto select gamete buttosn visible?
    */
    private boolean selectGameteButtonVisible = true;
    

    /**
     * Active tool
    **/
    private int activeTool = Tool.SELECTION;
    
    /**
     *	gamete select model
     */
     public static final int MANUAL_GAMETE_SELECT = 2;
     public static final int AUTO_GAMETE_SELECT = 1;
     
     private int gameteSelectMode;
     
     /**
     * Auto Move Gamete Timer
     */
     private javax.swing.Timer autoSelectTimerMother;
     private javax.swing.Timer autoSelectTimerFather;


    /**
     * Creates a sex view.
     *
     * @exception	IllegalArgumentException - one of input arguments null
    **/
    public MeiosisView()
    {
        super();
        
        // Set colors
        setBackground(Color.lightGray);
        setForeground(Color.black);

        sexViewMode = SEX_VIEW_MODE_NO_VIEWS;

        // Turn on double buffering
        setDoubleBuffered(true);
		autoSelectTimerMother = new javax.swing.Timer(100,this);
		autoSelectTimerFather = new javax.swing.Timer(100,this);
        // No layout manager (stupid layout managers!!)
        setLayout(null);

        // Create my border - an empty strip of 10 pixels
        EmptyBorder myBorder = new EmptyBorder(20,10,10,10);
        setBorder(myBorder);

		
        // Create empty meiosis and fertilization models for now
        motherOrganism = null;
        fatherOrganism = null;
        offspringOrganism = null;
        motherMeiosisModel = new MeiosisModel();
        motherMeiosisModel.addPropertyChangeListener(this);
        fatherMeiosisModel = new MeiosisModel();
        fatherMeiosisModel.addPropertyChangeListener(this);
        offspringFertilizationModel = new FertilizationModel();
        offspringFertilizationModel.setMeiosisModels(motherMeiosisModel,fatherMeiosisModel);
        offspringFertilizationModel.addPropertyChangeListener(this);

        // Create 6 small subviews, all dependent on this parent
        // to tell them what organisms to draw, etc.
        BevelBorder subViewBorder = new BevelBorder(BevelBorder.LOWERED);

        motherOrganismView = new SexOrganismView();
        motherOrganismView.setBorder(subViewBorder);
        motherOrganismView.setBackground(getBackground());
        motherOrganismView.setForeground(getForeground());

        offspringOrganismView = new SexOrganismView();
        offspringOrganismView.setBorder(subViewBorder);
        offspringOrganismView.setBackground(getBackground());
        offspringOrganismView.setForeground(getForeground());

        fatherOrganismView = new SexOrganismView();
        fatherOrganismView.setBorder(subViewBorder);
        fatherOrganismView.setBackground(getBackground());
        fatherOrganismView.setForeground(getForeground());

        motherSmallMeiosisView = new SmallMeiosisView(SmallMeiosisView.MOTHER_VIEW);
        motherSmallMeiosisView.setBorder(subViewBorder);
        motherSmallMeiosisView.addPropertyChangeListener(this);
        motherSmallMeiosisView.setBackground(getBackground());
        motherSmallMeiosisView.setForeground(getForeground());

        smallFertilizationView = new SmallFertilizationView();
        smallFertilizationView.setBorder(subViewBorder);
        smallFertilizationView.addPropertyChangeListener(this);
        smallFertilizationView.setBackground(getBackground());
        smallFertilizationView.setForeground(getForeground());
        
        fatherSmallMeiosisView = new SmallMeiosisView(SmallMeiosisView.FATHER_VIEW);
        fatherSmallMeiosisView.setBorder(subViewBorder);
        fatherSmallMeiosisView.addPropertyChangeListener(this);
        fatherSmallMeiosisView.setBackground(getBackground());
        fatherSmallMeiosisView.setForeground(getForeground());

        // Create 2 big views, again also dependent on this parent
        // view to tell them what organisms to draw, etc.
        bigMeiosisView = new BigMeiosisView();
        bigMeiosisView.setBorder(subViewBorder);
        bigMeiosisView.addPropertyChangeListener(this);
        bigMeiosisView.setBackground(getBackground());
        bigMeiosisView.setForeground(getForeground());

        bigFertilizationView = new BigFertilizationView();
        bigFertilizationView.setBorder(subViewBorder);
        bigFertilizationView.addPropertyChangeListener(this);
        bigFertilizationView.setBackground(getBackground());
        bigFertilizationView.setForeground(getForeground());

        // Create reset and replay buttons
        Insets insets = new Insets(2,2,2,2);
        
        selectGameteButton1 = new JButton("Select Gamete");
        selectGameteButton1.setMargin(insets);
        selectGameteButton1.addActionListener(this);
        selectGameteButton1.setActionCommand(cmdAutoSelectMotherGamete);
        selectGameteButton1.setFocusPainted(false);
        selectGameteButton1.setBounds(2,2,100,26);
        selectGameteButton1.setBackground(Color.lightGray);
        selectGameteButton1.setVisible(selectGameteButtonVisible);
        selectGameteButton1.setEnabled(selectGameteButtonEnabled);
        selectGameteButton1.setToolTipText("Run auto select gamete in mother view ");
        
        selectGameteButton2 = new JButton("Select Gamete");
        selectGameteButton2.setMargin(insets);
        selectGameteButton2.addActionListener(this);
        selectGameteButton2.setActionCommand(cmdAutoSelectFatherGamete);
        selectGameteButton2.setFocusPainted(false);
        selectGameteButton2.setBounds(2,2,100,26);
        selectGameteButton2.setBackground(Color.lightGray);
        selectGameteButton2.setVisible(selectGameteButtonVisible);
        selectGameteButton2.setEnabled(selectGameteButtonEnabled);
        selectGameteButton2.setToolTipText("Run auto select gamete in father view ");


        replayButton = new JButton("Replay");
        replayButton.setMargin(insets);
        replayButton.addActionListener(this);
        replayButton.setActionCommand(cmdReplay);
        replayButton.setFocusPainted(false);
        replayButton.setBounds(2,2,100,26);
        replayButton.setBackground(Color.lightGray);
        replayButton.setVisible(replayButtonVisible);
        replayButton.setEnabled(replayButtonEnabled);
        replayButton.setToolTipText("Replay meiosis and fertilization in this view ");

        resetButton = new JButton("Reset");
        resetButton.setMargin(insets);
        resetButton.addActionListener(this);
        resetButton.setActionCommand(cmdReset);
        resetButton.setFocusPainted(false);
        resetButton.setBounds(2,2,100,26);
        resetButton.setBackground(Color.lightGray);
        resetButton.setVisible(resetButtonVisible);
        resetButton.setEnabled(resetButtonEnabled);
        resetButton.setToolTipText("Reset meiosis and fertilization in this view ");

        motherMoveGameteIntoFertilizationButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/arrowright.gif"));
        motherMoveGameteIntoFertilizationButton.setMargin(insets);
        motherMoveGameteIntoFertilizationButton.addActionListener(this);
        motherMoveGameteIntoFertilizationButton.setActionCommand(cmdMoveMotherGameteIntoFertilization);
        motherMoveGameteIntoFertilizationButton.setFocusPainted(false);
        motherMoveGameteIntoFertilizationButton.setBounds(2,2,26,26);
        motherMoveGameteIntoFertilizationButton.setBackground(Color.lightGray);
        motherMoveGameteIntoFertilizationButton.setVisible(true);
        motherMoveGameteIntoFertilizationButton.setEnabled(false);
        motherMoveGameteIntoFertilizationButton.setToolTipText("Move selected mother's gamete into fertilization");

        motherMoveGameteOutOfFertilizationButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/arrowleft.gif"));
        motherMoveGameteOutOfFertilizationButton.setMargin(insets);
        motherMoveGameteOutOfFertilizationButton.addActionListener(this);
        motherMoveGameteOutOfFertilizationButton.setActionCommand(cmdMoveMotherGameteOutOfFertilization);
        motherMoveGameteOutOfFertilizationButton.setFocusPainted(false);
        motherMoveGameteOutOfFertilizationButton.setBounds(2,2,26,26);
        motherMoveGameteOutOfFertilizationButton.setBackground(Color.lightGray);
        motherMoveGameteOutOfFertilizationButton.setVisible(true);
        motherMoveGameteOutOfFertilizationButton.setEnabled(false);
        motherMoveGameteOutOfFertilizationButton.setToolTipText("Move selected mother's gamete out of fertilization");

        fatherMoveGameteIntoFertilizationButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/arrowleft.gif"));
        fatherMoveGameteIntoFertilizationButton.setMargin(insets);
        fatherMoveGameteIntoFertilizationButton.addActionListener(this);
        fatherMoveGameteIntoFertilizationButton.setActionCommand(cmdMoveFatherGameteIntoFertilization);
        fatherMoveGameteIntoFertilizationButton.setFocusPainted(false);
        fatherMoveGameteIntoFertilizationButton.setBounds(2,2,26,26);
        fatherMoveGameteIntoFertilizationButton.setBackground(Color.lightGray);
        fatherMoveGameteIntoFertilizationButton.setVisible(true);
        fatherMoveGameteIntoFertilizationButton.setEnabled(false);
        fatherMoveGameteIntoFertilizationButton.setToolTipText("Move selected father's gamete into fertilization");

        fatherMoveGameteOutOfFertilizationButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/arrowright.gif"));
        fatherMoveGameteOutOfFertilizationButton.setMargin(insets);
        fatherMoveGameteOutOfFertilizationButton.addActionListener(this);
        fatherMoveGameteOutOfFertilizationButton.setActionCommand(cmdMoveFatherGameteOutOfFertilization);
        fatherMoveGameteOutOfFertilizationButton.setFocusPainted(false);
        fatherMoveGameteOutOfFertilizationButton.setBounds(2,2,26,26);
        fatherMoveGameteOutOfFertilizationButton.setBackground(Color.lightGray);
        fatherMoveGameteOutOfFertilizationButton.setVisible(true);
        fatherMoveGameteOutOfFertilizationButton.setEnabled(false);
        fatherMoveGameteOutOfFertilizationButton.setToolTipText("Move selected father's gamete out of fertilization");

        // Set the view to all subviews mode, as that's necessary
        // to get the Look and Feel correct on all the sub views.
        setMeiosisViewMode(SEX_VIEW_MODE_ALL_VIEWS);
        setGameteSelectMode(MeiosisView.MANUAL_GAMETE_SELECT);

        // Update size
        updateSize();

        // Listen for resize events
        addComponentListener(this);
    }
    
    public void setOrganismSubviews(JComponent mother, JComponent offspring, JComponent father)
    {
    	if ((mother != null) && (offspring != null) && (father != null))
    	{
    		motherOrganismView = mother;
    		offspringOrganismView = offspring;
    		fatherOrganismView = father;
    		organismViewHeightPercentage = 50;
    	}
    }
    
    public BigMeiosisView getBigMeiosisView()
    {
        return bigMeiosisView;
    }

    public BigFertilizationView getBigFertilizationView()
    {
        return bigFertilizationView;
    }

    public SmallFertilizationView getSmallFertilizationView()
    {
        return smallFertilizationView;
    }

    public SmallMeiosisView getFatherSmallMeiosisView()
    {
        return fatherSmallMeiosisView;
    }

    public SmallMeiosisView getMotherSmallMeiosisView()
    {
        return motherSmallMeiosisView;
    }

    /**
     * Get alignment controls visibility in big meiosis view
     *
     * @return	boolean - visibility
    **/
    public boolean isAlignmentControlsVisible()
    {
        return bigMeiosisView.isAlignmentControlsVisible();
    }

    /**
     * Set alignment controls visibility in big meiosis view
     *
     * @param	aVisible boolean - visibility
    **/
    public void setAlignmentControlsVisible(boolean aVisible)
    {
        bigMeiosisView.setAlignmentControlsVisible(aVisible);
    }

    /**
     * Get crossover controls visibility in big meiosis view
     *
     * @return	boolean - visibility
    **/
    public boolean isCrossoverControlsVisible()
    {
        return bigMeiosisView.isCrossoverControlsVisible();
    }

    /**
     * Set crossover controls visibility
     *
     * @param	aVisible boolean - visibility
    **/
    public void setCrossoverControlsVisible(boolean aVisible)
    {
        bigMeiosisView.setCrossoverControlsVisible(aVisible);
    }

    /**
     * Get the mother organism for this view
     *
     * @return		Organism - mother organism, may be null
    **/
    public Organism getMotherOrganism()
    {
        return motherOrganism;
    }

    /**
     * Set the mother organism for this view
     *
     * @param		aMotherOrganism Organism - a new mother organism, may be null
     * @exception	IllegalArgumentException - mother organism illegal (not same species as father)
    **/
    public void setMotherOrganism(Organism aMotherOrganism)
    {
    	if (fatherOrganism != null&&fatherOrganism.isDeleted())
    	{
    		fatherOrganism = null;
    		
    	}
        // Return if not the same species as father
        if (fatherOrganism != null && aMotherOrganism != null &&
            fatherOrganism.getSpecies() != aMotherOrganism.getSpecies())
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if mother organism not changing
        if (aMotherOrganism == motherOrganism)
        {
            return;
        }

        // Changing, so save old mother organism
        Organism oldMotherOrganism = motherOrganism;

        // Switch mother organisms
        if (motherOrganism != null)
        {
            motherOrganism.removePropertyChangeListener(this);
            if (motherMeiosisModel != null)
            {
                motherMeiosisModel.setOrganism(null);
            }
        }

        motherOrganism = aMotherOrganism;

        if (motherOrganism != null)
        {
            motherOrganism.addPropertyChangeListener(this);
            if (motherMeiosisModel != null)
            {
                motherMeiosisModel.setOrganism(motherOrganism);
            }
        }

        // Update UI
        updateState();

        // Notify listeners
        changes.firePropertyChange(UIProp.MOTHER_ORGANISM,oldMotherOrganism,motherOrganism);
    }

    /**
     * Get the father organism for this view
     *
     * @return		Organism - father organism, may be null
    **/
    public Organism getFatherOrganism()
    {
        return fatherOrganism;
    }

    /**
     * Set the father organism for this view
     *
     * @param		aFatherOrganism Organism - a new father organism, may be null
     * @exception	IllegalArgumentException - father organism illegal (not same species as mother)
    **/
    public void setFatherOrganism(Organism aFatherOrganism)
    {
    	if (motherOrganism != null && motherOrganism.isDeleted())
    			motherOrganism = null;
        // Throw exception if not the same species as mother
        if (motherOrganism != null && aFatherOrganism != null &&
            motherOrganism.getSpecies() != aFatherOrganism.getSpecies())
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Return immediately if father organism not changing
        if (aFatherOrganism == fatherOrganism)
        {
            return;
        }

        // Changing, so save old father organism
        Organism oldFatherOrganism = fatherOrganism;

        // Switch father organisms
        if (fatherOrganism != null)
        {
            fatherOrganism.removePropertyChangeListener(this);
            if (fatherMeiosisModel != null)
            {
                fatherMeiosisModel.setOrganism(null);
            }
        }

        fatherOrganism = aFatherOrganism;

        if (fatherOrganism != null)
        {
            fatherOrganism.addPropertyChangeListener(this);
            if (fatherMeiosisModel != null)
            {
                fatherMeiosisModel.setOrganism(fatherOrganism);
            }
        }

        // Update UI
        updateState();

        // Notify listeners
        changes.firePropertyChange(UIProp.FATHER_ORGANISM,oldFatherOrganism,fatherOrganism);
    }

    /**
     * Replay the state of this view back to the very start.  This means we lose all
     * fertilization state but we retain the meiosis state, the selected gamete, etc.
     * So the user can replay meiosis, see the selected gamete and then manually move
     * that selected gamete into the fertilization chamber if they want.  Or they may
     * choose to select another gamete.
    **/
    public void replay()
    {
    	 autoSelectTimerMother.stop();
    	 autoSelectTimerFather.stop();
        // Unset father and mother moved gametes and reset steps to 0
        if (fatherMeiosisModel != null)
        {
        	fatherMeiosisModel.getMovedGamete();
            fatherMeiosisModel.unmoveGamete();
            blnMoveFatherGamete = false;
            fatherMeiosisModel.setStep(0);
      
        }

        if (motherMeiosisModel != null)
        {
        	motherMeiosisModel.getMovedGamete();
            motherMeiosisModel.unmoveGamete();
            blnMoveMotherGamete = false;
            motherMeiosisModel.setStep(0);
           
            
        }
    }

    /**
     * Reset the state of this view, erasing and recalculating all meiosis state, removing
     * any state for fertilization (e.g. selected gametes), etc.
    **/
    public void reset()
    {
        // If there's an offspring organism, delete it if the flag is set appropriately
        autoSelectTimerMother.stop();
        autoSelectTimerFather.stop();
        if (offspringOrganism != null &&
            resetDeleteOffspringOrganism)
        {
            offspringOrganism.delete();
            offspringOrganism = null;
        }

        // Unset father and mother
        if (fatherMeiosisModel != null)
        {
            fatherMeiosisModel.setOrganism(null);
        }

        if (motherMeiosisModel != null)
        {
            motherMeiosisModel.setOrganism(null);
        }
        
        // Reset father and mother, forcing a rerandomization
        if (fatherMeiosisModel != null)
        {
            fatherMeiosisModel.setOrganism(fatherOrganism);
        }

        if (motherMeiosisModel != null)
        {
            motherMeiosisModel.setOrganism(motherOrganism);
        }

        repaint();
    }

    /**
     * Get the offspring organism for this view
     *
     * @return		Organism - offspring organism, may be null
    **/
    public Organism getOffspringOrganism()
    {
        return offspringOrganism;
    }

    /**
     * Get the mother's meiosis model
    **/
    public MeiosisModel getMotherMeiosisModel()
    {
        return motherMeiosisModel;
    }

    /**
     * Get the father's meiosis model
    **/
    public MeiosisModel getFatherMeiosisModel()
    {
        return fatherMeiosisModel;
    }

    /**
     * Get the fertilization model
    **/
    public FertilizationModel getOffspringFertilizationModel()
    {
        return offspringFertilizationModel;
    }

    /**
     * Get the sex view mode.
     *
     * @return		sexViewMode int - the current sex view mode
    **/
    public int getMeiosisViewMode()
    {
        return sexViewMode;
    }

    /**
     * Set sex view mode, updating view and subviews appropriately.
     *
     * @param	aMeiosisViewMode int - new sex view mode
    **/
    public void setMeiosisViewMode(int aMeiosisViewMode)
    {
        // Return without doing anything if new mode is the same as old mode
        if (aMeiosisViewMode == sexViewMode)
        {
            return;
        }

        // Save old sex view mode
        int oldMeiosisViewMode = sexViewMode;

        // Remove all subviews from this view
        removeAll();

        // Get size
        Rectangle bounds = getBounds();

        // Add appropriate views back into main panel in correct order
        switch (aMeiosisViewMode)
        {
            case SEX_VIEW_MODE_NO_VIEWS:
                sexViewMode = aMeiosisViewMode;
                // Don't add any views
                validate();
                break;

            case SEX_VIEW_MODE_SIX_VIEWS:
                sexViewMode = aMeiosisViewMode;
                {
                    // Calculate heights and widths, reserving top SEX_VIEW_TOP_TEXT_HEIGHT pixels
                    // for Mother, Father, Offspring and reserving height for buttons if necessary
                    int topHeight;
                    int bottomHeight;
                    int topViewWidth = (bounds.width - 12) / 3;
                    int lastTopViewWidth = bounds.width - topViewWidth - topViewWidth - 12;
                    int bottomViewWidth = (bounds.width - 64) / 3;
                    int lastBottomViewWidth = bounds.width - bottomViewWidth - bottomViewWidth - 64;

                    if (replayButtonVisible || resetButtonVisible ||selectGameteButtonVisible)
                    {
                        topHeight = (((bounds.height-SEX_VIEW_TOP_TEXT_HEIGHT-SEX_VIEW_BOTTOM_BUTTON_HEIGHT) * organismViewHeightPercentage) / 100) - 4;
                        bottomHeight = bounds.height - SEX_VIEW_TOP_TEXT_HEIGHT - SEX_VIEW_BOTTOM_BUTTON_HEIGHT - topHeight - 12;
                    }
                    else
                    {
                        topHeight = (((bounds.height-SEX_VIEW_TOP_TEXT_HEIGHT) * organismViewHeightPercentage) / 100) - 4;
                        bottomHeight = bounds.height - SEX_VIEW_TOP_TEXT_HEIGHT - topHeight - 8;
                    }

                    // Resize the 6 small views and buttons
                    motherOrganismView.setBounds(2,2+SEX_VIEW_TOP_TEXT_HEIGHT,topViewWidth,topHeight);
                    offspringOrganismView.setBounds(topViewWidth+6,2+SEX_VIEW_TOP_TEXT_HEIGHT,topViewWidth,topHeight);
                    fatherOrganismView.setBounds(topViewWidth+topViewWidth+10,2+SEX_VIEW_TOP_TEXT_HEIGHT,lastTopViewWidth,topHeight);
                    motherSmallMeiosisView.setBounds(2,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT,bottomViewWidth,bottomHeight);
                    motherMoveGameteIntoFertilizationButton.setBounds(4+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT+(bottomHeight/5),27,(bottomHeight/5));
                    motherMoveGameteOutOfFertilizationButton.setBounds(4+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT+((3*bottomHeight)/5),27,(bottomHeight/5));
                    smallFertilizationView.setBounds(32+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT,bottomViewWidth,bottomHeight);
                    fatherMoveGameteIntoFertilizationButton.setBounds(34+bottomViewWidth+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT+(bottomHeight/5),27,(bottomHeight/5));
                    fatherMoveGameteOutOfFertilizationButton.setBounds(34+bottomViewWidth+bottomViewWidth,topHeight+12+SEX_VIEW_TOP_TEXT_HEIGHT+((3*bottomHeight)/5),27,(bottomHeight/5));
                    fatherSmallMeiosisView.setBounds(62+bottomViewWidth+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT,lastBottomViewWidth,bottomHeight);

					remove(selectGameteButton1);
			    	remove(selectGameteButton2);
					  
                    if (replayButtonVisible)
                    {
                        replayButton.setBounds(2+(topViewWidth),topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
                    }
                    if (resetButtonVisible)
                    {
                        resetButton.setBounds(2+((3*topViewWidth)/2),topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
                    }
              
                    if (gameteSelectMode == MeiosisView.AUTO_GAMETE_SELECT && selectGameteButtonVisible)
			    	{
			    		selectGameteButton1.setBounds(2,topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
			       		selectGameteButton2.setBounds(5+((5*topViewWidth)/2),topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
			          
			            add(selectGameteButton1);
			            add(selectGameteButton2);
			        }
			        
                    // Add the 6 small views and buttons
                    add(motherOrganismView);
                    add(offspringOrganismView);
                    add(fatherOrganismView);
                    add(motherSmallMeiosisView);
                    add(smallFertilizationView);
                   
                   	if (gameteSelectMode == MeiosisView.AUTO_GAMETE_SELECT)
                   	{
                   		remove(motherMoveGameteOutOfFertilizationButton);
			    		remove(motherMoveGameteIntoFertilizationButton);
			    		remove(fatherMoveGameteOutOfFertilizationButton);
			    		remove(fatherMoveGameteIntoFertilizationButton);
			        }
			        else
			        {
			        	add(motherMoveGameteIntoFertilizationButton);
                    	add(motherMoveGameteOutOfFertilizationButton);
                    	add(fatherMoveGameteIntoFertilizationButton);
                   		add(fatherMoveGameteOutOfFertilizationButton);
			        }
                    add(fatherSmallMeiosisView);
                    add(replayButton);
                    add(resetButton);
                }
                validate();
                break;

            case SEX_VIEW_MODE_MEIOSIS_VIEW_MOTHER:
            case SEX_VIEW_MODE_MEIOSIS_VIEW_FATHER:
                sexViewMode = aMeiosisViewMode;
    
                // Add just the big meiosis view
                bigMeiosisView.setBounds(2,2+SEX_VIEW_TOP_TEXT_HEIGHT,bounds.width-4,bounds.height-4-SEX_VIEW_TOP_TEXT_HEIGHT);
                add(bigMeiosisView);
                validate();
                break;

            case SEX_VIEW_MODE_FERTILIZATION_VIEW:
                sexViewMode = aMeiosisViewMode;
    
                // Add just the big fertilization view
                bigFertilizationView.setBounds(2,2+SEX_VIEW_TOP_TEXT_HEIGHT,bounds.width-4,bounds.height-4-SEX_VIEW_TOP_TEXT_HEIGHT);
                add(bigFertilizationView);
                validate();
                break;

            case SEX_VIEW_MODE_ALL_VIEWS:
                sexViewMode = aMeiosisViewMode;

                // Add all 8 views anywhere they want to exist
                add(motherOrganismView);
                add(offspringOrganismView);
                add(fatherOrganismView);
                add(motherSmallMeiosisView);
                add(motherMoveGameteIntoFertilizationButton);
                add(motherMoveGameteOutOfFertilizationButton);
                add(smallFertilizationView);
                add(fatherMoveGameteIntoFertilizationButton);
                add(fatherMoveGameteOutOfFertilizationButton);
                add(fatherSmallMeiosisView);
                add(bigMeiosisView);
                add(bigFertilizationView);
                add(replayButton);
                add(resetButton);
                validate();
                break;
        }

        // Update state
        updateState();

        // Notify listeners
        if (oldMeiosisViewMode != sexViewMode)
        {
            changes.firePropertyChange(UIProp.SEX_VIEW_MODE,
                                       new Integer(oldMeiosisViewMode),
                                       new Integer(sexViewMode));
        }
    }

    /**
     * Get whether or not fertilization is manually disabled.<p>
     *
     * @return 		boolean - is fertilization manually disabled?
    **/
    public final boolean isFertilizationManuallyDisabled()
    {
        return fertilizationManuallyDisabled;
    }

    /**
     * Set whether fertilization is manually disabled or not.<p>
     *
     * @param		aManuallyDisabled boolean - new manually disabled value
    **/
    public void setFertilizationManuallyDisabled(boolean aManuallyDisabled)
    {
        // Return immediately if value not changing
        if (aManuallyDisabled == fertilizationManuallyDisabled)
        {
            return;
        }

        boolean oldFertilizationManuallyDisabled = fertilizationManuallyDisabled;
        fertilizationManuallyDisabled = aManuallyDisabled;

        smallFertilizationView.setFertilizationManuallyDisabled(fertilizationManuallyDisabled);
        bigFertilizationView.setFertilizationManuallyDisabled(fertilizationManuallyDisabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.FERTILIZATION_MANUALLY_DISABLED,
                                   new Boolean(oldFertilizationManuallyDisabled),
                                   new Boolean(fertilizationManuallyDisabled));
    }

    /**
     * Get the organism view height percentage.  This is the percentage
     * of the total view height devoted to the organism views.  The
     * acceptable range is 0 to 100.<p>
     *
     * @return		int - organism view height percentage
    **/
    public int getOrganismViewHeightPercentage()
    {
        return organismViewHeightPercentage;
    }

    /**
     * Set the organism view height percentage.  The input value must
     * be between 0 and 100.
     *
     * @param		anOrganismViewHeightPercentage - between 0 and 100
     * @exception	IllegalArgumentException - input argument illegal
    **/
    public void setOrganismViewHeightPercentage(int anOrganismViewHeightPercentage)
    {
        if (anOrganismViewHeightPercentage < 0 ||
            anOrganismViewHeightPercentage > 100)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        // Make change
        int oldOrganismViewHeightPercentage = organismViewHeightPercentage;
        organismViewHeightPercentage = anOrganismViewHeightPercentage;

        // Update sizes
        updateSize();

        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM_VIEW_HEIGHT_PERCENTAGE,
                                   new Integer(oldOrganismViewHeightPercentage),
                                   new Integer(organismViewHeightPercentage));
    }

    /**
     * Get whether or not reset is automatic when an offspring is created.<p>
     *
     * @return 		boolean - is reset automatic when an offspring is created
    **/
    public final boolean isResetAutomatic()
    {
        return resetAutomatic;
    }

    /**
     * Set whether a reset is automatic when an offspring is created.<p>
     *
     * @param		aResetAutomatic boolean - new reset automatic value
    **/
    public void setResetAutomatic(boolean aResetAutomatic)
    {
        // Return immediately if value not changing
        if (aResetAutomatic == resetAutomatic)
        {
            return;
        }

        boolean oldResetAutomatic = resetAutomatic;
        resetAutomatic = aResetAutomatic;

        // Notify listeners
        changes.firePropertyChange(UIProp.RESET_AUTOMATIC,
                                   new Boolean(oldResetAutomatic),
                                   new Boolean(resetAutomatic));
    }
    
    /**
     * Get whether or not the reset button is enabled.<p>
     *
     * @return 		boolean - is reset button enabled
    **/
    public final boolean isResetButtonEnabled()
    {
        return resetButtonEnabled;
    }

    /**
     * Set whether the reset button is enabled.<p>
     *
     * @param		aResetButtonEnabled boolean - new reset button enabled
    **/
    public void setResetButtonEnabled(boolean aResetButtonEnabled)
    {
        // Return immediately if value not changing
        if (aResetButtonEnabled == resetButtonEnabled)
        {
            return;
        }

        boolean oldResetButtonEnabled = resetButtonEnabled;
        resetButtonEnabled = aResetButtonEnabled;

        resetButton.setEnabled(resetButtonEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.RESET_BUTTON_ENABLED,
                                   new Boolean(oldResetButtonEnabled),
                                   new Boolean(resetButtonEnabled));
    }

    /**
     * Get whether or not the reset button is visible.<p>
     *
     * @return 		boolean - is reset button visible
    **/
    public final boolean isResetButtonVisible()
    {
        return resetButtonVisible;
    }

    /**
     * Set whether the reset button is visible.<p>
     *
     * @param		aResetButtonVisible boolean - new reset button visibility
    **/
    public void setResetButtonVisible(boolean aResetButtonVisible)
    {
        // Return immediately if value not changing
        if (aResetButtonVisible == resetButtonVisible)
        {
            return;
        }

        boolean oldResetButtonVisible = resetButtonVisible;
        resetButtonVisible = aResetButtonVisible;

        resetButton.setVisible(resetButtonVisible);
        updateSize();

        // Notify listeners
        changes.firePropertyChange(UIProp.RESET_BUTTON_VISIBLE,
                                   new Boolean(oldResetButtonVisible),
                                   new Boolean(resetButtonVisible));
    }
    
    /**
     * Get whether or not the replay button is enabled.<p>
     *
     * @return 		boolean - is replay button enabled
    **/
    public final boolean isReplayButtonEnabled()
    {
        return replayButtonEnabled;
    }

    /**
     * Set whether the replay button is enabled.<p>
     *
     * @param		aReplayButtonEnabled boolean - new replay button enabled
    **/
    public void setReplayButtonEnabled(boolean aReplayButtonEnabled)
    {
        // Return immediately if value not changing
        if (aReplayButtonEnabled == replayButtonEnabled)
        {
            return;
        }

        boolean oldReplayButtonEnabled = replayButtonEnabled;
        replayButtonEnabled = aReplayButtonEnabled;

        replayButton.setEnabled(replayButtonEnabled);

        // Notify listeners
        changes.firePropertyChange(UIProp.REPLAY_BUTTON_ENABLED,
                                   new Boolean(oldReplayButtonEnabled),
                                   new Boolean(replayButtonEnabled));
    }

	 /**
     * Get whether or not the Select Gamete buttons are enabled.<p>
     *
     * @return 		boolean - are Select Gamete button enabled
    **/
	public final boolean isSelectGameteButtonEnabled()
    {
        return selectGameteButtonEnabled;
    }

    /**
     * Set whether the Select Gamete buttons are enabled.<p>
     *
     * @param		aReplayButtonEnabled boolean - new replay button enabled
    **/
    public void setSelectGameteButtonEnabled(boolean aSelectGameteButtonEnabled)
    {
        // Return immediately if value not changing
        if (aSelectGameteButtonEnabled == selectGameteButtonEnabled)
        {
            return;
        }

        boolean oldSelectGameteButtonEnabled = selectGameteButtonEnabled;
        selectGameteButtonEnabled = aSelectGameteButtonEnabled;

        selectGameteButton1.setEnabled(selectGameteButtonEnabled);
        selectGameteButton2.setEnabled(selectGameteButtonEnabled);

    }
    /**
     * Get whether or not the Select Gamete buttons are visible.<p>
     *
     * @return 		boolean - Are Select Gamete buttos visible
    **/
    public final boolean isSelectGameteButtonVisible()
    {
        return selectGameteButtonVisible;
    }

    /**
     * Set whether the replay button is visible.<p>
     *
     * @param		aReplayButtonVisible boolean - new replay button visibility
    **/
    public void setSelectGameteButtonVisible(boolean aSelectGameteButtonVisible)
    {
        // Return immediately if value not changing
        if (aSelectGameteButtonVisible == selectGameteButtonVisible)
        {
            return;
        }

		if (gameteSelectMode != MeiosisView.AUTO_GAMETE_SELECT)
		{
			return;
		}
        boolean oleSelectGameteButtonVisible = selectGameteButtonVisible;
        selectGameteButtonVisible = aSelectGameteButtonVisible;

        selectGameteButton1.setEnabled(selectGameteButtonVisible);
        selectGameteButton2.setEnabled(selectGameteButtonVisible);

        updateSize();
    }
    
    /**
     * Get whether or not the GameteMoveButton is enabled.<p>
     *
     * @return 		boolean - is GameteMoveButton enabled
    **/
    public final boolean isGameteMoveButtonEnabled()
    {
        return gameteMoveButtonEnabled;
    }

    /**
     * Set whether the replay button is enabled.<p>
     *
     * @param		aReplayButtonEnabled boolean - new replay button enabled
    **/
    public void setGameteMoveButtonEnabled(boolean aGameteMoveButtonEnabled)
    {
        gameteMoveButtonEnabled = aGameteMoveButtonEnabled;
        motherMoveGameteIntoFertilizationButton.setEnabled(gameteMoveButtonEnabled);
        motherMoveGameteOutOfFertilizationButton.setEnabled(gameteMoveButtonEnabled);
        fatherMoveGameteIntoFertilizationButton.setEnabled(gameteMoveButtonEnabled);
        fatherMoveGameteOutOfFertilizationButton.setEnabled(gameteMoveButtonEnabled);
        if (gameteMoveButtonEnabled)
            updateInOutButtons();
    }
    

    /**
     * Get whether or not the replay button is visible.<p>
     *
     * @return 		boolean - is replay button visible
    **/
    public final boolean isReplayButtonVisible()
    {
        return replayButtonVisible;
    }

    /**
     * Set whether the replay button is visible.<p>
     *
     * @param		aReplayButtonVisible boolean - new replay button visibility
    **/
    public void setReplayButtonVisible(boolean aReplayButtonVisible)
    {
        // Return immediately if value not changing
        if (aReplayButtonVisible == replayButtonVisible)
        {
            return;
        }

        boolean oldReplayButtonVisible = replayButtonVisible;
        replayButtonVisible = aReplayButtonVisible;

        replayButton.setVisible(replayButtonVisible);
        updateSize();

        // Notify listeners
        changes.firePropertyChange(UIProp.REPLAY_BUTTON_VISIBLE,
                                   new Boolean(oldReplayButtonVisible),
                                   new Boolean(replayButtonVisible));
    }

    /**
     * Get whether or not reset should automatically delete an offspring organism.<p>
     *
     * @return 		boolean - should reset delete an offspring?
    **/
    public final boolean isResetDeleteOffspringOrganism()
    {
        return resetDeleteOffspringOrganism;
    }

    /**
     * Set whether reset should automaticallly delete an offspring organism.<p>
     *
     * @param		aResetDeleteOffspringOrganism boolean - new reset delete offspring organism value
    **/
    public void setResetDeleteOffspringOrganism(boolean aResetDeleteOffspringOrganism)
    {
        // Return immediately if value not changing
        if (aResetDeleteOffspringOrganism == resetDeleteOffspringOrganism)
        {
            return;
        }

        boolean oldResetDeleteOffspringOrganism = resetDeleteOffspringOrganism;
        resetDeleteOffspringOrganism = aResetDeleteOffspringOrganism;

        // Notify listeners
        changes.firePropertyChange(UIProp.RESET_DELETE_OFFSPRING_ORGANISM,
                                   new Boolean(oldResetDeleteOffspringOrganism),
                                   new Boolean(resetDeleteOffspringOrganism));
    }

    /**
     * Get whether or not fertilization should stop immediately when it starts.
     * This is used by scripts to stop fertilization and put up some sort of
     * a message or do something keyed off fertilization starting.  The script
     * will continue fertilization after the user has done whatever.<p>
     *
     * @return 		boolean - stop when fertilization starts?
    **/
    public final boolean isStopWhenFertilizationStarts()
    {
        return stopWhenFertilizationStarts;
    }

    /**
     * Set whether fertilization should immediately stop when it starts.<p>
     *
     * @param		aStopWhenFertilizationStarts - stop when fertilization starts?
    **/
    public void setStopWhenFertilizationStarts(boolean aStopWhenFertilizationStarts)
    {
        // Return immediately if value not changing
        if (aStopWhenFertilizationStarts == stopWhenFertilizationStarts)
        {
            return;
        }

        boolean oldStopWhenFertilizationStarts = stopWhenFertilizationStarts;
        stopWhenFertilizationStarts = aStopWhenFertilizationStarts;

        // Notify listeners
        changes.firePropertyChange(UIProp.STOP_WHEN_FERTILIZATION_STARTS,
                                   new Boolean(oldStopWhenFertilizationStarts),
                                   new Boolean(stopWhenFertilizationStarts));
    }

    /**
     * Get whether or not meiosiis should stop immediately when it starts.
     * This is used by scripts to stop meiosis and put up some sort of
     * a message or do something keyed off meiosis starting.  The script
     * may continue meiosis after the user has done whatever.<p>
     *
     * @return 		boolean - stop when meiosis starts?
    **/
    public final boolean isStopWhenMeiosisStarts()
    {
        return stopWhenMeiosisStarts;
    }

    /**
     * Set whether meiosis should immediately stop when it starts.<p>
     *
     * @param		aStopWhenMeiosisStarts - stop when meiosis starts?
    **/
    public void setStopWhenMeiosisStarts(boolean aStopWhenMeiosisStarts)
    {
        // Return immediately if value not changing
        if (aStopWhenMeiosisStarts == stopWhenMeiosisStarts)
        {
            return;
        }

        boolean oldStopWhenMeiosisStarts = stopWhenMeiosisStarts;
        stopWhenMeiosisStarts = aStopWhenMeiosisStarts;

        // Notify listeners
        changes.firePropertyChange(UIProp.STOP_WHEN_MEIOSIS_STARTS,
                                   new Boolean(oldStopWhenMeiosisStarts),
                                   new Boolean(stopWhenMeiosisStarts));
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

        int yText = 18;

        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Set font and color
        g.setFont(getFont());
        g.setColor(getForeground());

        // Calculate locations for text
        Rectangle bounds = getBounds();
        int sixth = bounds.width/6;

        if (sexViewMode == SEX_VIEW_MODE_SIX_VIEWS)
        {
            // Draw Mother, Offspring and Father text strings above each column
            g.drawString("Mother",sixth-20,yText);
            g.drawString("Offspring",(3*sixth)-20,yText);
            g.drawString("Father",(5*sixth)-20,yText);
        }
        else if (sexViewMode == SEX_VIEW_MODE_MEIOSIS_VIEW_MOTHER)
        {
            g.drawString("Mother",(3*sixth)-20,yText);
        }
        else if (sexViewMode == SEX_VIEW_MODE_MEIOSIS_VIEW_FATHER)
        {
            g.drawString("Father",(3*sixth)-20,yText);
        }
        else if (sexViewMode == SEX_VIEW_MODE_FERTILIZATION_VIEW)
        {
            g.drawString("Offspring",(3*sixth)-20,yText);
        }
    }

    /**
     * Set the font for this view.  If a null font is specified,
     * the view will revert back to its default font.<p>
     *
     * @param		aFont Font - a new font, if null, then will revert to default font
    **/
    public void setFont(Font aFont)
    {
        // Set this view's value
        super.setFont(aFont);

        // Set child view values
        if (motherOrganismView != null)
        {
            motherOrganismView.setFont(aFont);
            offspringOrganismView.setFont(aFont);
            fatherOrganismView.setFont(aFont);
    
            motherSmallMeiosisView.setFont(aFont);
            fatherSmallMeiosisView.setFont(aFont);
            bigMeiosisView.setFont(aFont);
                
            smallFertilizationView.setFont(aFont);
            bigFertilizationView.setFont(aFont);
        }
    }

    /**
     * Set the background color for this view.  If a null background color
     * is specified, the view will revert back to its default background color.<p>
     *
     * @param		aBackgroundColor Color - a new color, if null, then will revert to default background color
    **/
    public void setBackground(Color aBackgroundColor)
    {
        // Set this view's value
        super.setBackground(aBackgroundColor);

        // Set child view values
        if (motherOrganismView != null)
        {
            motherOrganismView.setBackground(aBackgroundColor);
            offspringOrganismView.setBackground(aBackgroundColor);
            fatherOrganismView.setBackground(aBackgroundColor);
    
            motherSmallMeiosisView.setBackground(aBackgroundColor);
            fatherSmallMeiosisView.setBackground(aBackgroundColor);
            bigMeiosisView.setBackground(aBackgroundColor);
            
            smallFertilizationView.setBackground(aBackgroundColor);
            bigFertilizationView.setBackground(aBackgroundColor);
        }
    }

    /**
     * Set the background color for the cell sub views.  If a null foreground color
     * is specified, the view will revert back to its default background color.<p>
     *
     * @param		aBackgroundColor Color - a new color, if null, then will revert to default background color
    **/
    public void setBackgroundCellSubViews(Color aBackgroundColor)
    {
        motherSmallMeiosisView.setBackground(aBackgroundColor);
        fatherSmallMeiosisView.setBackground(aBackgroundColor);
        bigMeiosisView.setBackground(aBackgroundColor);
        
        smallFertilizationView.setBackground(aBackgroundColor);
        bigFertilizationView.setBackground(aBackgroundColor);
    }

    /**
     * Set the background color for the organism sub views.  If a null foreground color
     * is specified, the view will revert back to its default background color.<p>
     *
     * @param		aBackgroundColor Color - a new color, if null, then will revert to default background color
    **/
    public void setBackgroundOrganismSubViews(Color aBackgroundColor)
    {
        motherOrganismView.setBackground(aBackgroundColor);
        offspringOrganismView.setBackground(aBackgroundColor);
        fatherOrganismView.setBackground(aBackgroundColor);
    }

    /**
     * Set the foreground color for this view.  If a null foreground color
     * is specified, the view will revert back to its default foreground color.<p>
     *
     * @param		aForegroundColor Color - a new color, if null, then will revert to default foreground color
    **/
    public void setForeground(Color aForegroundColor)
    {
        // Set this view's value
        super.setForeground(aForegroundColor);

        // Set child view values
        if (motherOrganismView != null)
        {
            motherOrganismView.setForeground(aForegroundColor);
            offspringOrganismView.setForeground(aForegroundColor);
            fatherOrganismView.setForeground(aForegroundColor);
    
            motherSmallMeiosisView.setForeground(aForegroundColor);
            fatherSmallMeiosisView.setForeground(aForegroundColor);
            bigMeiosisView.setForeground(aForegroundColor);
            
            smallFertilizationView.setForeground(aForegroundColor);
            bigFertilizationView.setForeground(aForegroundColor);
        }
    }

    /**
     * Set the foreground color for the cell sub views.  If a null foreground color
     * is specified, the view will revert back to its default foreground color.<p>
     *
     * @param		aForegroundColor Color - a new color, if null, then will revert to default foreground color
    **/
    public void setForegroundCellSubViews(Color aForegroundColor)
    {
        motherSmallMeiosisView.setForeground(aForegroundColor);
        fatherSmallMeiosisView.setForeground(aForegroundColor);
        bigMeiosisView.setForeground(aForegroundColor);
        
        smallFertilizationView.setForeground(aForegroundColor);
        bigFertilizationView.setForeground(aForegroundColor);
    }

    /**
     * Set the foreground color for the organism sub views.  If a null foreground color
     * is specified, the view will revert back to its default foreground color.<p>
     *
     * @param		aForegroundColor Color - a new color, if null, then will revert to default foreground color
    **/
    public void setForegroundOrganismSubViews(Color aForegroundColor)
    {
        motherOrganismView.setForeground(aForegroundColor);
        offspringOrganismView.setForeground(aForegroundColor);
        fatherOrganismView.setForeground(aForegroundColor);
    }

    /**
     * Update state of interface (buttons, etc.)
    **/
    private void updateState()
    {
        // Avoid recursive calls to this method
        if (updatingState == true)
        {
            return;
        }
        updatingState = true;
            
        // Tell the views to wake up, repaint, reset their state, etc.
        switch (sexViewMode)
        {
            case SEX_VIEW_MODE_NO_VIEWS:
                motherSmallMeiosisView.setMeiosisModel(null);
                smallFertilizationView.setFertilizationModel(null);
                fatherSmallMeiosisView.setMeiosisModel(null);
                bigMeiosisView.setMeiosisModel(null,null);
                bigFertilizationView.setFertilizationModel(null);
                break;

            case SEX_VIEW_MODE_SIX_VIEWS:
                motherSmallMeiosisView.setMeiosisModel(motherMeiosisModel);
                smallFertilizationView.setFertilizationModel(offspringFertilizationModel);
                fatherSmallMeiosisView.setMeiosisModel(fatherMeiosisModel);
                bigMeiosisView.setMeiosisModel(null,null);
                bigFertilizationView.setFertilizationModel(null);
                break;

            case SEX_VIEW_MODE_MEIOSIS_VIEW_MOTHER:
                motherSmallMeiosisView.setMeiosisModel(null);
                smallFertilizationView.setFertilizationModel(null);
                fatherSmallMeiosisView.setMeiosisModel(null);
                bigMeiosisView.setMeiosisModel(motherMeiosisModel,motherSmallMeiosisView);
                bigFertilizationView.setFertilizationModel(null);
                break;

            case SEX_VIEW_MODE_MEIOSIS_VIEW_FATHER:
                motherSmallMeiosisView.setMeiosisModel(null);
                smallFertilizationView.setFertilizationModel(null);
                fatherSmallMeiosisView.setMeiosisModel(null);
                bigMeiosisView.setMeiosisModel(fatherMeiosisModel,fatherSmallMeiosisView);
                bigFertilizationView.setFertilizationModel(null);
                break;

            case SEX_VIEW_MODE_FERTILIZATION_VIEW:
                motherSmallMeiosisView.setMeiosisModel(null);
                smallFertilizationView.setFertilizationModel(null);
                fatherSmallMeiosisView.setMeiosisModel(null);
                bigMeiosisView.setMeiosisModel(null,null);
                bigFertilizationView.setFertilizationModel(offspringFertilizationModel);
                break;

            case SEX_VIEW_MODE_ALL_VIEWS:
                 motherSmallMeiosisView.setMeiosisModel(null);
                smallFertilizationView.setFertilizationModel(null);
                fatherSmallMeiosisView.setMeiosisModel(null);
                bigMeiosisView.setMeiosisModel(null,null);
                bigFertilizationView.setFertilizationModel(null);
                break;
        }

        updateInOutButtons();

        updatingState = false;

        // Force a repaint
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

        if (propertyName.equals(UIProp.BIG_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED) ||
            propertyName.equals(UIProp.BIG_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED) ||
            propertyName.equals(UIProp.BIG_FERTILIZATION_MAGNIFY_BUTTON_PUSHED))
        {
            setMeiosisViewMode(SEX_VIEW_MODE_SIX_VIEWS);
        }
        else if (propertyName.equals(UIProp.SMALL_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED))
        {
            setMeiosisViewMode(SEX_VIEW_MODE_MEIOSIS_VIEW_MOTHER);
        }
        else if (propertyName.equals(UIProp.SMALL_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED))
        {
            setMeiosisViewMode(SEX_VIEW_MODE_MEIOSIS_VIEW_FATHER);
        }
        else if (propertyName.equals(UIProp.SMALL_FERTILIZATION_MAGNIFY_BUTTON_PUSHED))
        {
            setMeiosisViewMode(SEX_VIEW_MODE_FERTILIZATION_VIEW);
        }
        else if (propertyName.equals(UIProp.FERTILIZATION_OFFSPRING_ORGANISM))
        {
        	
            if (offspringFertilizationModel != null)
            {
                Organism newOffspringOrganism = offspringFertilizationModel.getOffspringOrganism();
                if (newOffspringOrganism != offspringOrganism)
                {
                    Organism oldOffspringOrganism = offspringOrganism;
                    offspringOrganism = newOffspringOrganism;
                    if (!motherMeiosisModel.isAutoCrossOverHappened() &&!fatherMeiosisModel.isAutoCrossOverHappened()
                    	&&!motherMeiosisModel.isControlledCrossOverHappened() && !fatherMeiosisModel.isControlledCrossOverHappened())
                    {
                    	if (offspringOrganism != null)
                    		offspringOrganism.setAsOriginalPaintInfo();
                    }
                    updateState();
                    changes.firePropertyChange(UIProp.OFFSPRING_ORGANISM,oldOffspringOrganism,offspringOrganism);
                }
            }
        }
        else if (propertyName.equals(EngineProp.ORGANISM_GENOTYPE_AND_PHENOTYPE) ||
                 propertyName.equals(EngineProp.ORGANISM_GENOTYPE_AND_NOT_PHENOTYPE))
        {
            Organism changedOrganism = (Organism) event.getSource();
            if (changedOrganism != null)
            {
                if (changedOrganism == motherOrganism)
                {
                    motherOrganism = null;
                    motherMeiosisModel.setOrganism(motherOrganism);
                    updateState();
                }
                else if (changedOrganism == fatherOrganism)
                {
                    fatherOrganism = null;
                    fatherMeiosisModel.setOrganism(fatherOrganism);
                    updateState();
                }
                else if (changedOrganism == offspringOrganism)
                {
                    offspringOrganism = null;
                    updateState();
                }
            }
        }
        else if (propertyName.equals(EngineProp.DELETED))
        {
        	
            Object object = (Object) event.getSource();
            if (object instanceof Organism)
            {
                Organism deletedOrganism = (Organism) object;
                if (deletedOrganism == motherOrganism)
                {
                    setMotherOrganism(null);
                }
                else if (deletedOrganism == fatherOrganism)
                {
                    setFatherOrganism(null);
                }
                else if (deletedOrganism == offspringOrganism)
                {
                    offspringOrganism = null;
                    // do more???
                }
            }
        }
        else if (propertyName.equals(UIProp.ORGANISM))
        {
            updateInOutButtons();
        }
        else if (propertyName.equals(UIProp.SELECTED_GAMETE))
        {
        	
            updateInOutButtons();

            MeiosisModel aMeiosisModel = (MeiosisModel) event.getSource();
           
            if (aMeiosisModel == fatherMeiosisModel)
            {
            	
                changes.firePropertyChange(UIProp.SELECTED_FATHER_GAMETE,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
            else
            {
            	
                changes.firePropertyChange(UIProp.SELECTED_MOTHER_GAMETE,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
        }
        else if (propertyName.equals(UIProp.MOVED_GAMETE))
        {
            updateInOutButtons();
           

            MeiosisModel aMeiosisModel = (MeiosisModel) event.getSource();
            if (aMeiosisModel == fatherMeiosisModel)
            {
                changes.firePropertyChange(UIProp.MOVED_FATHER_GAMETE,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
            else
            {
            	
                changes.firePropertyChange(UIProp.MOVED_MOTHER_GAMETE,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
        }
        else if (propertyName.equals(UIProp.MEIOSIS_STARTED))
        {
            MeiosisModel aMeiosisModel = (MeiosisModel) event.getSource();
            if (aMeiosisModel == motherMeiosisModel)
            {
                if (stopWhenMeiosisStarts)
                {
                    // Stop animation and set step to 1
                    motherSmallMeiosisView.goToAnimationStep(1);
                    bigMeiosisView.goToAnimationStep(1);

                    changes.firePropertyChange(UIProp.MEIOSIS_MOTHER_STARTED_AND_STOPPED,
                                               event.getOldValue(),
                                               new Integer(1));
                }
                else
                {
                    changes.firePropertyChange(UIProp.MEIOSIS_MOTHER_STEP,
                                               event.getOldValue(),
                                               event.getNewValue());
                }
            }
            else if (aMeiosisModel == fatherMeiosisModel)
            {
                if (stopWhenMeiosisStarts)
                {
                    // Stop animation and set step to 1
                    fatherSmallMeiosisView.goToAnimationStep(1);
                    bigMeiosisView.goToAnimationStep(1);

                    changes.firePropertyChange(UIProp.MEIOSIS_FATHER_STARTED_AND_STOPPED,
                                               event.getOldValue(),
                                               new Integer(1));
                }
                else
                {
                    changes.firePropertyChange(UIProp.MEIOSIS_FATHER_STEP,
                                               event.getOldValue(),
                                               event.getNewValue());
                }
            }
        }
        else if (propertyName.equals(UIProp.MEIOSIS_STEP))
        {
            MeiosisModel aMeiosisModel = (MeiosisModel) event.getSource();
            if (aMeiosisModel == motherMeiosisModel)
            {
                changes.firePropertyChange(UIProp.MEIOSIS_MOTHER_STEP,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
            else if (aMeiosisModel == fatherMeiosisModel)
            {
                changes.firePropertyChange(UIProp.MEIOSIS_FATHER_STEP,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
        }
        else if (propertyName.equals(UIProp.FERTILIZATION_STARTED))
        {
            if (stopWhenFertilizationStarts)
            {
                // Stop animation and set step to 1
                smallFertilizationView.goToAnimationStep(1);
                bigFertilizationView.goToAnimationStep(1);

                changes.firePropertyChange(UIProp.FERTILIZATION_STARTED_AND_STOPPED,
                                           event.getOldValue(),
                                           new Integer(1));
            }
            else
            {
                changes.firePropertyChange(UIProp.FERTILIZATION_STEP,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
        }
        else if (propertyName.equals(UIProp.FERTILIZATION_STEP))
        {
            changes.firePropertyChange(UIProp.FERTILIZATION_STEP,
                                       event.getOldValue(),
                                       event.getNewValue());
        }
        else if (propertyName.equals(EngineProp.VISIBLE))
        {
            repaint();
        }
    }

    /**
     * Update the size of this view.  To do this, get the current size of the view
     * and update the models for anything shown in the view.  Do not repaint or generate
     * a repaint event, as that event is already coming automatically from AWT.
    **/
    public void updateSize()
    {
        Rectangle bounds = getBounds();

        // Resize sub views as appropriate
        switch (sexViewMode)
        {
            case SEX_VIEW_MODE_SIX_VIEWS:
                {
                    // Calculate heights and widths, reserving top SEX_VIEW_TOP_TEXT_HEIGHT pixels for
                    // Mother, Father, Offspring and reserving SEX_VIEW_BOTTOM_BUTTON_HEIGHT for buttons if necessary
                    int topHeight;
                    int bottomHeight;
                    int topViewWidth = (bounds.width - 12) / 3;
                    int lastTopViewWidth = bounds.width - topViewWidth - topViewWidth - 12;
                    int bottomViewWidth = (bounds.width - 64) / 3;
                    int lastBottomViewWidth = bounds.width - bottomViewWidth - bottomViewWidth - 64;

                    if (replayButtonVisible || resetButtonVisible || selectGameteButtonVisible)
                    {
                        topHeight = (((bounds.height-SEX_VIEW_TOP_TEXT_HEIGHT-SEX_VIEW_BOTTOM_BUTTON_HEIGHT) * organismViewHeightPercentage) / 100) - 4;
                        bottomHeight = (bounds.height-SEX_VIEW_TOP_TEXT_HEIGHT-SEX_VIEW_BOTTOM_BUTTON_HEIGHT) - topHeight - 12;
                    }
                    else
                    {
                        topHeight = (((bounds.height-SEX_VIEW_TOP_TEXT_HEIGHT) * organismViewHeightPercentage) / 100) - 4;
                        bottomHeight = (bounds.height-SEX_VIEW_TOP_TEXT_HEIGHT) - topHeight - 8;
                    }

                    // Resize the 6 small views and buttons
                    motherOrganismView.setBounds(2,2+SEX_VIEW_TOP_TEXT_HEIGHT,topViewWidth,topHeight);
                    offspringOrganismView.setBounds(topViewWidth+6,2+SEX_VIEW_TOP_TEXT_HEIGHT,topViewWidth,topHeight);
                    fatherOrganismView.setBounds(topViewWidth+topViewWidth+10,2+SEX_VIEW_TOP_TEXT_HEIGHT,lastTopViewWidth,topHeight);
                    motherSmallMeiosisView.setBounds(2,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT,bottomViewWidth,bottomHeight);
                    motherMoveGameteIntoFertilizationButton.setBounds(4+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT+(bottomHeight/5),27,(bottomHeight/5));
                    motherMoveGameteOutOfFertilizationButton.setBounds(4+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT+((3*bottomHeight)/5),27,(bottomHeight/5));
                    smallFertilizationView.setBounds(32+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT,bottomViewWidth,bottomHeight);
                    fatherMoveGameteIntoFertilizationButton.setBounds(34+bottomViewWidth+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT+(bottomHeight/5),27,(bottomHeight/5));
                    fatherMoveGameteOutOfFertilizationButton.setBounds(34+bottomViewWidth+bottomViewWidth,topHeight+12+SEX_VIEW_TOP_TEXT_HEIGHT+((3*bottomHeight)/5),27,(bottomHeight/5));
                    fatherSmallMeiosisView.setBounds(62+bottomViewWidth+bottomViewWidth,topHeight+6+SEX_VIEW_TOP_TEXT_HEIGHT,lastBottomViewWidth,bottomHeight);

					remove(selectGameteButton1);
					remove(selectGameteButton2);
                    if (replayButtonVisible)
                    {
                        replayButton.setBounds(2+(topViewWidth/2),topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
                    }
                    if (resetButtonVisible)
                    {
                        resetButton.setBounds(2+((3*topViewWidth)/2),topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
                    }
                    
                    if (gameteSelectMode == MeiosisView.AUTO_GAMETE_SELECT && selectGameteButtonVisible)
			    	{
			    		selectGameteButton1.setBounds(2,topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
			       		selectGameteButton2.setBounds(5+((5*topViewWidth)/2),topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
			          
			            add(selectGameteButton1);
			            add(selectGameteButton2);
			        }
                }
                validate();
                break;

            case SEX_VIEW_MODE_MEIOSIS_VIEW_MOTHER:
            case SEX_VIEW_MODE_MEIOSIS_VIEW_FATHER:
                bigMeiosisView.setBounds(2,2+SEX_VIEW_TOP_TEXT_HEIGHT,bounds.width-4,bounds.height-4-SEX_VIEW_TOP_TEXT_HEIGHT);
                validate();
                break;

            case SEX_VIEW_MODE_FERTILIZATION_VIEW:
                bigFertilizationView.setBounds(2,2+SEX_VIEW_TOP_TEXT_HEIGHT,bounds.width-4,bounds.height-4-SEX_VIEW_TOP_TEXT_HEIGHT);
                validate();
                break;

            case SEX_VIEW_MODE_NO_VIEWS:
            case SEX_VIEW_MODE_ALL_VIEWS:
                break;
        }
    }

    /**
     * Update buttons into and out of fertilization
    **/
    void updateInOutButtons()
    {
        if (gameteMoveButtonEnabled)
        {
            int motherSelectedGamete = motherMeiosisModel.getSelectedGamete();
            int motherMovedGamete = motherMeiosisModel.getMovedGamete();
            int fatherSelectedGamete = fatherMeiosisModel.getSelectedGamete();
            int fatherMovedGamete = fatherMeiosisModel.getMovedGamete();
    
            if (motherSelectedGamete != MeiosisModel.NO_GAMETE &&
                motherMovedGamete == MeiosisModel.NO_GAMETE)
            {
                motherMoveGameteIntoFertilizationButton.setEnabled(true);
            }
            else
            {
                motherMoveGameteIntoFertilizationButton.setEnabled(false);
            }
    
            if (motherMovedGamete != MeiosisModel.NO_GAMETE)
            {
                motherMoveGameteOutOfFertilizationButton.setEnabled(true);
            }
            else
            {
                motherMoveGameteOutOfFertilizationButton.setEnabled(false);
            }
    
            if (fatherSelectedGamete != MeiosisModel.NO_GAMETE &&
                fatherMovedGamete == MeiosisModel.NO_GAMETE)
            {
                fatherMoveGameteIntoFertilizationButton.setEnabled(true);
            }
            else
            {
                fatherMoveGameteIntoFertilizationButton.setEnabled(false);
            }
    
            if (fatherMovedGamete != MeiosisModel.NO_GAMETE)
            {
                fatherMoveGameteOutOfFertilizationButton.setEnabled(true);
            }
            else
            {
                fatherMoveGameteOutOfFertilizationButton.setEnabled(false);
            }
        }
    }
    
    /**
    * set gamete select model of this sexview
    */
    public void setGameteSelectMode(int i)
    {
    	
    	gameteSelectMode = i;
    	if (fatherSmallMeiosisView == null ||
    		motherSmallMeiosisView == null)
    		return;
    		
    	Rectangle bounds = getBounds();
    	if (gameteSelectMode == MeiosisView.AUTO_GAMETE_SELECT)
    	{
    		remove(selectGameteButton1);
    		remove(selectGameteButton2);
    		remove(motherMoveGameteOutOfFertilizationButton);
    		remove(motherMoveGameteIntoFertilizationButton);
    		remove(fatherMoveGameteOutOfFertilizationButton);
    		remove(fatherMoveGameteIntoFertilizationButton);
	        int topViewWidth = (bounds.width - 12) / 3;
	        int lastTopViewWidth = bounds.width - topViewWidth - topViewWidth - 12;
	        int bottomViewWidth = (bounds.width - 64) / 3;
	        int lastBottomViewWidth = bounds.width - bottomViewWidth - bottomViewWidth - 64;

	        int topHeight = (((bounds.height-SEX_VIEW_TOP_TEXT_HEIGHT-SEX_VIEW_BOTTOM_BUTTON_HEIGHT) * organismViewHeightPercentage) / 100) - 4;
	        int bottomHeight = bounds.height - SEX_VIEW_TOP_TEXT_HEIGHT - SEX_VIEW_BOTTOM_BUTTON_HEIGHT - topHeight - 12;
	        if (selectGameteButtonVisible)
			{
	        	selectGameteButton1.setBounds(2,topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
            	selectGameteButton2.setBounds(5+((5*topViewWidth)/2),topHeight+8+SEX_VIEW_TOP_TEXT_HEIGHT+bottomHeight,topViewWidth/2,SEX_VIEW_BOTTOM_BUTTON_HEIGHT);
            
            	add(selectGameteButton1);
           		add(selectGameteButton2);
           	}
            repaint();
	      
	    }
    	
    	if (fatherSmallMeiosisView != null)
    	{
    		fatherSmallMeiosisView.setGameteSelectMode(i);
    	}
    	if(motherSmallMeiosisView !=null)
    	{
    		motherSmallMeiosisView.setGameteSelectMode(i);
    	}
    }
    
    /**
    *	get gamete select model of this sex view
    **/

	public int getGameteSelectMode()
	{
		return gameteSelectMode;
	}
    /**
     * Method called by ToolView when the current tool changes.
     * If we get a tool other than snip or chromosome, we default
     * to the selection tool.  In other words, we ignore tools
     * that we don't understand (e.g. DNA tool, Cross tool).<p>
     *
     * @param   aTool int - the active tool
    **/
    public void toolChanged(int aTool)
    {
        if (aTool == Tool.SNIP)
        {
            // Snip tool
            setCursor(Tool.getCursor(aTool));
            activeTool = aTool;
        }
        else if (aTool == Tool.CHROMOSOME)
        {
            // Chromosome tool
            setCursor(Tool.getCursor(aTool));
            activeTool = aTool;
        }
        else
        {
            // Default to selection
            setCursor(Tool.getCursor(Tool.SELECTION));
            activeTool = Tool.SELECTION;
        }

        bigMeiosisView.toolChanged(activeTool);
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
     * React to actions
    **/
    private boolean blnMoveMotherGamete = false;
    private boolean blnMoveFatherGamete = false;
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
		if (e.getSource() ==  autoSelectTimerMother)
		{
			
			if (motherSmallMeiosisView.isAutoSelectGameteFinished() && motherMeiosisModel != null)
			{
				motherMeiosisModel.moveGamete();
				blnMoveMotherGamete = true;
				autoSelectTimerMother.stop();
				
			}
			if (blnMoveMotherGamete && blnMoveFatherGamete)
			{
				motherSmallMeiosisView.setMagnifyButtonEnabled(true);
				fatherSmallMeiosisView.setMagnifyButtonEnabled(true);
			}
			
		}
		else if (e.getSource() == autoSelectTimerFather)
		{
			
	        if (fatherSmallMeiosisView.isAutoSelectGameteFinished() && fatherMeiosisModel != null)
	        {
	        	fatherMeiosisModel.moveGamete();
	        	 autoSelectTimerFather.stop();
	        	 blnMoveFatherGamete = true;
	        }
	        if (blnMoveMotherGamete && blnMoveFatherGamete)
			{
				motherSmallMeiosisView.setMagnifyButtonEnabled(true);
				fatherSmallMeiosisView.setMagnifyButtonEnabled(true);
			}
	         
		}
        else if (cmd.equals(cmdReset))
        {
            reset();
        }
        else if (cmd.equals(cmdReplay))
        {
  
            replay();
        }
        else if (cmd.equals(cmdAutoSelectMotherGamete))
        {
        	if (gameteSelectMode == MeiosisView.AUTO_GAMETE_SELECT)
            {
	        	if (motherMeiosisModel != null)
	            {
	                motherMeiosisModel.unmoveGamete();
	               
	            }
	            if (motherSmallMeiosisView != null && motherMeiosisModel.getStep()==100)
	            {
	            	blnMoveMotherGamete = false;
	            	
	            	motherSmallMeiosisView.startAutoSelectGamete();
	            	motherSmallMeiosisView.setMagnifyButtonEnabled(false);
					fatherSmallMeiosisView.setMagnifyButtonEnabled(false);
	            	if (!autoSelectTimerMother.isRunning())
	            		autoSelectTimerMother.start();
	            }
           
            }
        }
        else if (cmd.equals(cmdAutoSelectFatherGamete))
        {
        	 if (gameteSelectMode == MeiosisView.AUTO_GAMETE_SELECT)
            {
            	blnMoveFatherGamete = false;
	        	if (fatherMeiosisModel != null)
	            {
	                fatherMeiosisModel.unmoveGamete();
	              
	            }
	            if (fatherSmallMeiosisView != null && fatherMeiosisModel.getStep()==100)
	            {
	            	blnMoveFatherGamete = false;
	            	fatherSmallMeiosisView.startAutoSelectGamete();
	            	motherSmallMeiosisView.setMagnifyButtonEnabled(false);
				    fatherSmallMeiosisView.setMagnifyButtonEnabled(false);
	            	if (!autoSelectTimerFather.isRunning())
	            		autoSelectTimerFather.start();
	            }
           
            }
        }
        else if (cmd.equals(cmdMoveMotherGameteIntoFertilization))
        {
            if (motherMeiosisModel != null)
            {
                motherMeiosisModel.moveGamete();
               
            }
        }
        else if (cmd.equals(cmdMoveMotherGameteOutOfFertilization))
        {
            if (motherMeiosisModel != null)
            {
                motherMeiosisModel.unmoveGamete();
                blnMoveMotherGamete = false;
            }
        }
        else if (cmd.equals(cmdMoveFatherGameteIntoFertilization))
        {
            if (fatherMeiosisModel != null)
            {
                fatherMeiosisModel.moveGamete();
                
            }
        }
        else if (cmd.equals(cmdMoveFatherGameteOutOfFertilization))
        {
            if (fatherMeiosisModel != null)
            {
                fatherMeiosisModel.unmoveGamete();
                blnMoveFatherGamete = false;
            }
        }
    }
}

