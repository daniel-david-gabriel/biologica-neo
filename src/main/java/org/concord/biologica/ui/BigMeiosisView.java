//
// Class : BigMeiosisView - the big meiosis view used as a subview
//							in the sex view of the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.10 $
// $Date: 2003/02/04 16:09:00 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.concord.biologica.engine.*;

/**
 * This class represents a view which shows the sex cell of a parent in the sex view
 * and has controls for the user to conduct meiosis within this view on that sex cell.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.ALIGNMENT_CONTROLS_VISIBLE - alignment controls visibility changed
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.BIG_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED - the magnifying button was pushed as mother view
 * <li> UIProp.BIG_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED - the magnifying button was pushed as father view
 * <li> UIProp.CROSSOVER_CONTROLS_VISIBLE - crossover controls visibility changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * <li> UIProp.MEIOSIS_MODEL - the meiosis model was changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#ALIGNMENT_CONTROLS_VISIBLE
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#BIG_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED
 * @see org.concord.biologica.ui.UIProp#BIG_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED
 * @see org.concord.biologica.ui.UIProp#CROSSOVER_CONTROLS_VISIBLE
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_MODEL
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.10 $ $Date: 2003/02/04 16:09:00 $
 * @author 		$Author: qliao $
**/

public final class BigMeiosisView
extends UIView
implements MouseListener, MouseMotionListener, ActionListener, ComponentListener, ChangeListener, PropertyChangeListener
{


    
    /**
     * Step at which animation stops when horizontal alignment controls are turned on
    **/
    static final int CONTROL_HORIZONTAL_ALIGNMENT_STEP = 33;
    
    /**
     * Step at which animation stops when crossover may be done
    **/
    static final int CONTROL_CROSSOVER_STEP = CONTROL_HORIZONTAL_ALIGNMENT_STEP;

    /**
     * Step at which animation stops when vertical alignment controls are turned on
    **/
    static final int CONTROL_VERTICAL_ALIGNMENT_STEP = 73;

    /**
     * Number of switch horizontal alignment buttons we pre-create
    **/
    static final int NUMBER_OF_SWITCH_HORIZONTAL_ALIGNMENT_BUTTONS = 8;

    /**
     * Number of switch vertical alignment buttons we pre-create
    **/
    static final int NUMBER_OF_SWITCH_VERTICAL_ALIGNMENT_BUTTONS = 16;

    /**
     * Number of steps in the crossing over animation
    **/
    static final int NUMBER_OF_CROSSING_OVER_STEPS = 40;

    /**
     * Step on which we should do the actual crossover during the animation
    **/
    static final int CROSSING_OVER_STEP = 20;
    
    static final int DOUBLECLICK_INTERVAL = 300;
    /**
     * 4 Gametes
     */
     Gamete [] arrGametes = new Gamete[4];

    // Commands - must be unique
    static private final String cmdMagnifyView		= "cmdMagnifyView";
    static private final String cmdGoToStart		= "cmdGoToStart";
    static private final String cmdPlayBackwardFast	= "cmdPlayBackwardFast";
    static private final String cmdPlayBackward		= "cmdPlayBackward";
    static private final String cmdStepBackward		= "cmdStepBackward";
    static private final String cmdStop				= "cmdStop";
    static private final String cmdStepForward		= "cmdStepForward";
    static private final String cmdPlayForward		= "cmdPlayForward";
    static private final String cmdPlayForwardFast	= "cmdPlayForwardFast";
    static private final String cmdGoToEnd			= "cmdGoToEnd";
    static private final String cmdSwitchHorizontalAlignment = "cmdSwitchHorizontalAlignment";
    static private final String cmdSwitchVerticalAlignment = "cmdSwitchVerticalAlignment";
    static private final String cmdAutomaticAlignment = "cmdAutomaticAlignment";
    static private final String cmdControlledAlignment = "cmdControlledAlignment";
    static private final String cmdAutomaticCrossover = "cmdAutomaticCrossover";
    static private final String cmdControlledCrossover = "cmdControlledCrossover";
    
    /**
     * Play modes for this view.  Animations can either involve stepping through
     * meiosis or flashing the cut locations for crossing over.
    **/
    static private final int PLAY_MODE_BACKWARD_FAST	   = 1;
    static private final int PLAY_MODE_BACKWARD			   = 2;
    static private final int PLAY_MODE_STOPPED			   = 3;
    static private final int PLAY_MODE_FORWARD			   = 4;
    static private final int PLAY_MODE_FORWARD_FAST		   = 5;
    static private final int PLAY_MODE_CROSSING_OVER       = 6;

    /**
     * Indication of where to draw allele symbol relative to allele location
    **/
    static private final int DRAW_ALLELE_SYMBOL_RIGHT	= 1;
    static private final int DRAW_ALLELE_SYMBOL_LEFT	= 2;

    /**
     * Current play mode for this view.
    **/
    private int currentPlayMode = PLAY_MODE_STOPPED;

    /**
     * Actual width of this view, initially set to nonsense number
    **/
    private int actualWidth = -10;

    /**
     * Actual height of this view, initially set to nonsense number
    **/
    private int actualHeight = -10;

    /**
     * Radius of chromosome circles
    **/
    private int radiusChromosome = 0;

    /**
     * Diameter of chromosome circles
    **/
    private int diameterChromosome = 0;

    /**
     * Radius of centromere circles
    **/
    private int radiusCentromere = 0;

    /**
     * Diameter of centromere circles
    **/
    private int diameterCentromere = 0;
    
    private long firstClick = 0;

    /**
     * Length of leader line to allele symbol text
    **/
    private int lengthLeaderLine = 0;

    /**
     * Current organism
    **/
    private Organism currentOrganism;

    /**
     * Get the corresponding SmallMeiosisView which contains
     * the same organism as this view.  May be null.<p>
    **/
    private SmallMeiosisView smallMeiosisView = null;

    /**
     * Magnify button - used to magnify view back down to 6 views version of sex view
    **/
    private JButton magnifyButton = null;

    /**
     * Go to start button - used to rewind meiosis to start
    **/
    private JButton goToStartButton = null;

    /**
     * Play backward fast button - used to rewind meiosis quickly
    **/
    private JToggleButton playBackwardFastToggleButton = null;

    /**
     * Play backward button - used to play meiosis backward at normal speed
    **/
    private JToggleButton playBackwardToggleButton = null;

    /**
     * Step backward button - used to step meiosis one step backward
    **/
    private JButton stepBackwardButton = null;

    /**
     * Stop button - used to stop meiosis
    **/
    private JToggleButton stopToggleButton = null;

    /**
     * Step forward button - used to step meiosis one step forward
    **/
    private JButton stepForwardButton = null;

    /**
     * Play forward button - used to play meiosis forward at normal speed
    **/
    private JToggleButton playForwardToggleButton = null;

    /**
     * Play forward fast button - used to play meiosis forward quickly
    **/
    private JToggleButton playForwardFastToggleButton = null;

    /**
     * Go to end button - used to fast forward meiosis to end
    **/
    private JButton goToEndButton = null;

    /**
     * Meiosis model which maintains the state for meiosis animation.
    **/
    private MeiosisModel meiosisModel;

    /**
     * Slider showing the position of the meiosis
    **/
    private JSlider animationSlider;

    /**
     * Timer for animation
    **/
    private javax.swing.Timer animationTimer;
    
   

    /**
     * Timer for blinking
    **/
    private javax.swing.Timer blinkingTimer;

    /**
     * Current blink cut locations visibility.  This value
     * alternates true and false when the user has clicked
     * on the first cut location, blinking the possible
     * second cut locations for crossing over.
    **/
    private boolean blinkCutLocationsVisible = true;

    /**
     * Array of buttons for switching horizontal alignment of chromosomes
    **/
    private JButton switchHorizontalAlignmentButton[] = null;

    /**
     * Array of buttons for switching vertical alignment of chromosomes
    **/
    private JButton switchVerticalAlignmentButton[] = null;

    /**
     * Automatic alignment radio button
    **/
    private JRadioButton automaticAlignmentRadioButton = null;

    /**
     * Controlled alignment radio button
    **/
    private JRadioButton controlledAlignmentRadioButton = null;

    /**
     * Automatic crossover radio button
    **/
    private JRadioButton automaticCrossoverRadioButton = null;

    /**
     * Controlled crossover radio button
    **/
    private JRadioButton controlledCrossoverRadioButton = null;

    /**
     * Is alignment controlled?
    **/
    private boolean controlAlignment = false;

    /**
     * Is crossing over controlled?
    **/
    private boolean controlCrossover = false;
    
    /**
     *Is crossing over animationed?
     **/
     private boolean animationCrossover = false;

    /**
     * Switch alignment buttons visible?
    **/
    private boolean switchAlignmentButtonsVisible = false;

    /**
     * Selected chromosome model
    **/
    private MeiosisChromosomeModel selectedMeiosisChromosomeModel = null;

    /**
     * Vector of chromosome models that have been moved
    **/
    private Vector movedChromosomeModels = null;

    /**
     * X location of original mouse down
    **/
    private int xMouseDown = 0;

    /**
     * Y location of original mouse down
    **/
    private int yMouseDown = 0;

    /**
     * Alignment controls visible
    **/
    private boolean alignmentControlsVisible = true;

    /**
     * Crossover controls visible
    **/
    private boolean crossoverControlsVisible = false;

    /**
     * Active tool
    **/
    private int activeTool = Tool.SELECTION;

    /**
     * Chromosome model for the first cut.
    **/
    private MeiosisChromosomeModel meiosisChromosomeModelFirstCut = null;

    /**
     * Chromosome model for the second cut.
    **/
    private MeiosisChromosomeModel meiosisChromosomeModelSecondCut = null;

    /**
     * Type of strand for first cut.<p>
     *
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#P_STRAND_ONE
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#Q_STRAND_ONE
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#P_STRAND_TWO
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#Q_STRAND_TWO
    **/
    private int strandTypeFirstCut = 0;

    /**
     * Type of strand for second cut.<p>
     *
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#P_STRAND_ONE
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#Q_STRAND_ONE
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#P_STRAND_TWO
     * @see org.concord.biologica.ui.MeiosisChromosomeModel#Q_STRAND_TWO
    **/
    private int strandTypeSecondCut = 0;

    /**
     * Index of the strand point for the cut on the chromosome strands.
    **/
    private int strandPointIndexCut = 0;

    /**
     * Crossing over step, ranging from 0 to NUMBER_OF_CROSSING_OVER_STEPS
    **/
    private int crossingOverStep = 0;

    /**
     * Original crossing over first cut x temporary offset
    **/
    private int xCrossingOverOriginalFirstCutTemporaryOffset;

    /**
     * Original crossing over first cut y temporary offset
    **/
    private int yCrossingOverOriginalFirstCutTemporaryOffset;

    /**
     * Original crossing over second cut x temporary offset
    **/
    private int xCrossingOverOriginalSecondCutTemporaryOffset;

    /**
     * Original crossing over second cut y temporary offset
    **/
    private int yCrossingOverOriginalSecondCutTemporaryOffset;
    
    private CellArc currentCellArc;
    
    private Rectangle boundingRectangle;
    

    /**
     * Creates a big meiosis view.
    **/
    public BigMeiosisView()
    {
        super();
        
        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);

        currentOrganism = null;
        smallMeiosisView = null;
        meiosisModel = null;
        currentPlayMode = PLAY_MODE_STOPPED;
        selectedMeiosisChromosomeModel = null;
        movedChromosomeModels = null;
        xMouseDown = 0;
        yMouseDown = 0;
        
        Insets insets = new Insets(2,2,2,2);

        magnifyButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/magdnglass.gif"));
        magnifyButton.setMargin(insets);
        magnifyButton.addActionListener(this);
        magnifyButton.setActionCommand(cmdMagnifyView);
        magnifyButton.setFocusPainted(false);
        magnifyButton.setBounds(2,2,26,26);
        magnifyButton.setBackground(Color.lightGray);
        magnifyButton.setVisible(true);
        magnifyButton.setEnabled(true);
        magnifyButton.setToolTipText("Magnify down to small view");
        add(magnifyButton);

        animationSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
        animationSlider.addChangeListener(this);
        animationSlider.setBounds(2,2,286,26);
        animationSlider.setBackground(Color.lightGray);
        animationSlider.setVisible(true);
        animationSlider.setEnabled(true);
        animationSlider.setToolTipText("Move slider to control meiosis");
        add(animationSlider);

        goToStartButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/backend.gif"));
        goToStartButton.setMargin(insets);
        goToStartButton.addActionListener(this);
        goToStartButton.setActionCommand(cmdGoToStart);
        goToStartButton.setFocusPainted(false);
        goToStartButton.setBounds(30,30,26,26);
        goToStartButton.setBackground(Color.lightGray);
        goToStartButton.setVisible(true);
        goToStartButton.setEnabled(true);
        goToStartButton.setToolTipText("Rewind to start");
        add(goToStartButton);

        playBackwardFastToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/backfast.gif"));
        playBackwardFastToggleButton.setMargin(insets);
        playBackwardFastToggleButton.addActionListener(this);
        playBackwardFastToggleButton.setActionCommand(cmdPlayBackwardFast);
        playBackwardFastToggleButton.setFocusPainted(false);
        playBackwardFastToggleButton.setToolTipText("Rewind");
        playBackwardFastToggleButton.setSelected(false);
        add(playBackwardFastToggleButton);

        playBackwardToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/backslow.gif"));
        playBackwardToggleButton.setMargin(insets);
        playBackwardToggleButton.addActionListener(this);
        playBackwardToggleButton.setActionCommand(cmdPlayBackward);
        playBackwardToggleButton.setFocusPainted(false);
        playBackwardToggleButton.setToolTipText("Play backwards");
        playBackwardToggleButton.setSelected(false);
        add(playBackwardToggleButton);

        stepBackwardButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/backone.gif"));
        stepBackwardButton.setMargin(insets);
        stepBackwardButton.addActionListener(this);
        stepBackwardButton.setActionCommand(cmdStepBackward);
        stepBackwardButton.setFocusPainted(false);
        stepBackwardButton.setBounds(30,30,26,26);
        stepBackwardButton.setBackground(Color.lightGray);
        stepBackwardButton.setVisible(true);
        stepBackwardButton.setEnabled(true);
        stepBackwardButton.setToolTipText("Rewind one step");
        add(stepBackwardButton);

        stopToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/stop.gif"));
        stopToggleButton.setMargin(insets);
        stopToggleButton.addActionListener(this);
        stopToggleButton.setActionCommand(cmdStop);
        stopToggleButton.setFocusPainted(false);
        stopToggleButton.setToolTipText("Stop");
        stopToggleButton.setSelected(true);
        add(stopToggleButton);

        stepForwardButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/playone.gif"));
        stepForwardButton.setMargin(insets);
        stepForwardButton.addActionListener(this);
        stepForwardButton.setActionCommand(cmdStepForward);
        stepForwardButton.setFocusPainted(false);
        stepForwardButton.setBounds(30,30,26,26);
        stepForwardButton.setBackground(Color.lightGray);
        stepForwardButton.setVisible(true);
        stepForwardButton.setEnabled(true);
        stepForwardButton.setToolTipText("Play one step");
        add(stepForwardButton);

        playForwardToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/play.gif"));
        playForwardToggleButton.setMargin(insets);
        playForwardToggleButton.addActionListener(this);
        playForwardToggleButton.setActionCommand(cmdPlayForward);
        playForwardToggleButton.setFocusPainted(false);
        playForwardToggleButton.setToolTipText("Play");
        playForwardToggleButton.setSelected(false);
        add(playForwardToggleButton);

        playForwardFastToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/playfast.gif"));
        playForwardFastToggleButton.setMargin(insets);
        playForwardFastToggleButton.addActionListener(this);
        playForwardFastToggleButton.setActionCommand(cmdPlayForwardFast);
        playForwardFastToggleButton.setFocusPainted(false);
        playForwardFastToggleButton.setToolTipText("Fast forward");
        playForwardFastToggleButton.setSelected(false);
        add(playForwardFastToggleButton);

        goToEndButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/playend.gif"));
        goToEndButton.setMargin(insets);
        goToEndButton.addActionListener(this);
        goToEndButton.setActionCommand(cmdGoToEnd);
        goToEndButton.setFocusPainted(false);
        goToEndButton.setBounds(30,30,26,26);
        goToEndButton.setBackground(Color.lightGray);
        goToEndButton.setVisible(true);
        goToEndButton.setEnabled(true);
        goToEndButton.setToolTipText("Fast forward to end");
        add(goToEndButton);

        automaticAlignmentRadioButton = new JRadioButton("Automatic",true);
        automaticAlignmentRadioButton.setMargin(insets);
        automaticAlignmentRadioButton.setFont(getFont());
        automaticAlignmentRadioButton.addActionListener(this);
        automaticAlignmentRadioButton.setActionCommand(cmdAutomaticAlignment);
        automaticAlignmentRadioButton.setToolTipText("Automatic alignment");
        automaticAlignmentRadioButton.setBackground(Color.lightGray);
        add(automaticAlignmentRadioButton);

        controlledAlignmentRadioButton = new JRadioButton("Controlled",false);
        controlledAlignmentRadioButton.setMargin(insets);
        controlledAlignmentRadioButton.setFont(getFont());
        controlledAlignmentRadioButton.addActionListener(this);
        controlledAlignmentRadioButton.setActionCommand(cmdControlledAlignment);
        controlledAlignmentRadioButton.setToolTipText("Controlled alignment");
        controlledAlignmentRadioButton.setBackground(Color.lightGray);
        add(controlledAlignmentRadioButton);

        automaticCrossoverRadioButton = new JRadioButton("Automatic",true);
        automaticCrossoverRadioButton.setMargin(insets);
        automaticCrossoverRadioButton.setFont(getFont());
        automaticCrossoverRadioButton.addActionListener(this);
        automaticCrossoverRadioButton.setActionCommand(cmdAutomaticCrossover);
        automaticCrossoverRadioButton.setToolTipText("Automatic crossing over");
        automaticCrossoverRadioButton.setEnabled(true);
        automaticCrossoverRadioButton.setVisible(false);
        automaticCrossoverRadioButton.setBackground(Color.lightGray);
        add(automaticCrossoverRadioButton);

        controlledCrossoverRadioButton = new JRadioButton("Controlled",false);
        controlledCrossoverRadioButton.setMargin(insets);
        controlledCrossoverRadioButton.setFont(getFont());
        controlledCrossoverRadioButton.addActionListener(this);
        controlledCrossoverRadioButton.setActionCommand(cmdControlledCrossover);
        controlledCrossoverRadioButton.setToolTipText("Controlled crossing over");
        controlledCrossoverRadioButton.setEnabled(false);
        controlledCrossoverRadioButton.setVisible(false);
        controlledCrossoverRadioButton.setBackground(Color.lightGray);
        add(controlledCrossoverRadioButton);

        // Create alignment buttons, only NUMBER_OF_SWITCH_ALIGNMENT_BUTTONS for now
        int i;
        switchHorizontalAlignmentButton = new JButton[NUMBER_OF_SWITCH_HORIZONTAL_ALIGNMENT_BUTTONS];
        for (i=0;i<switchHorizontalAlignmentButton.length;i++)
        {
            switchHorizontalAlignmentButton[i] = new JButton(getLocalImage("org/concord/biologica/locked/gifs/alignment.gif"));
            switchHorizontalAlignmentButton[i].setMargin(insets);
            switchHorizontalAlignmentButton[i].addActionListener(this);
            switchHorizontalAlignmentButton[i].setActionCommand(cmdSwitchHorizontalAlignment);
            switchHorizontalAlignmentButton[i].setFocusPainted(false);
            switchHorizontalAlignmentButton[i].setBounds(30,30,26,26);
            switchHorizontalAlignmentButton[i].setBackground(Color.lightGray);
            switchHorizontalAlignmentButton[i].setVisible(false);
            switchHorizontalAlignmentButton[i].setEnabled(false);
            switchHorizontalAlignmentButton[i].setToolTipText("Change alignment of chromosome pair");
            add(switchHorizontalAlignmentButton[i]);
        }

        switchVerticalAlignmentButton = new JButton[NUMBER_OF_SWITCH_VERTICAL_ALIGNMENT_BUTTONS];
        for (i=0;i<switchVerticalAlignmentButton.length;i++)
        {
            switchVerticalAlignmentButton[i] = new JButton(getLocalImage("org/concord/biologica/locked/gifs/valignment.gif"));
            switchVerticalAlignmentButton[i].setMargin(insets);
            switchVerticalAlignmentButton[i].addActionListener(this);
            switchVerticalAlignmentButton[i].setActionCommand(cmdSwitchVerticalAlignment);
            switchVerticalAlignmentButton[i].setFocusPainted(false);
            switchVerticalAlignmentButton[i].setBounds(30,30,26,26);
            switchVerticalAlignmentButton[i].setBackground(Color.lightGray);
            switchVerticalAlignmentButton[i].setVisible(false);
            switchVerticalAlignmentButton[i].setEnabled(false);
            switchVerticalAlignmentButton[i].setToolTipText("Change alignment of chromosome pair");
            add(switchVerticalAlignmentButton[i]);
        }
        
        boundingRectangle = new Rectangle(0, 0, 0, 0);
        
        // Create animation timer
        animationTimer = new javax.swing.Timer(100,this);
        
       
        // Create blinking timer
        blinkingTimer = new javax.swing.Timer(500,this);

        // Turn on double buffering
        setDoubleBuffered(true);

        // Listen for mouse clicks, but not mouse motion initially
        addMouseListener(this);
        addMouseMotionListener(this);
        
      

        // Listen for resize events
        addComponentListener(this);
    }

    /**
     * Set the current meiosis model to be shown in this view.
     *
     * @param		aModel MeiosisModel - the meiosis model to be shown in this view
     * @param		aSmallMeiosisView SmallMeiosisView - the small meiosis view for which this is a big view, may be null
     * @see			org.concord.biologica.ui.SmallMeiosisView#FATHER_VIEW
     * @see			org.concord.biologica.ui.SmallMeiosisView#MOTHER_VIEW
    **/
    void setMeiosisModel(MeiosisModel aModel, SmallMeiosisView aSmallMeiosisView)
    {
        // Ignore redundant setting
        if (meiosisModel == aModel)
        {
            return;
        }

        // Stop the animation
        stopAnimation();

        // Clear any movement state we had at this step
        clearMovedChromosomeModelsState();

        // Need to clear crossing over state
        clearCrossingOverState();

        int i;
        int length = switchHorizontalAlignmentButton.length;
        for (i=0;i<length;i++)
        {
            switchHorizontalAlignmentButton[i].setVisible(false);
        }
        length = switchVerticalAlignmentButton.length;
        for (i=0;i<length;i++)
        {
            switchVerticalAlignmentButton[i].setVisible(false);
        }

        // Make the change, saving old one
        MeiosisModel oldMeiosisModel = meiosisModel;
        if (meiosisModel != null)
        {
            meiosisModel.removePropertyChangeListener(this);
        }

        meiosisModel = aModel;
        smallMeiosisView = aSmallMeiosisView;

        if (meiosisModel != null)
        {
            currentOrganism = meiosisModel.getOrganism();
            animationSlider.setValue(meiosisModel.getStep());
            meiosisModel.addPropertyChangeListener(this);

            if (currentOrganism == null)
            {
                goToStartButton.setEnabled(false);
                playBackwardFastToggleButton.setEnabled(false);
                playBackwardToggleButton.setEnabled(false);
                stepBackwardButton.setEnabled(false);
                stopToggleButton.setEnabled(false);
                stepForwardButton.setEnabled(false);
                playForwardToggleButton.setEnabled(false);
                playForwardFastToggleButton.setEnabled(false);
                goToEndButton.setEnabled(false);
                animationSlider.setEnabled(false);

                automaticAlignmentRadioButton.setEnabled(false);
                controlledAlignmentRadioButton.setEnabled(false);
                automaticCrossoverRadioButton.setEnabled(false);
                controlledCrossoverRadioButton.setEnabled(false);
            }
            else
            {
                goToStartButton.setEnabled(true);
                playBackwardFastToggleButton.setEnabled(true);
                playBackwardToggleButton.setEnabled(true);
                stepBackwardButton.setEnabled(true);
                stopToggleButton.setEnabled(true);
                stepForwardButton.setEnabled(true);
                playForwardToggleButton.setEnabled(true);
                playForwardFastToggleButton.setEnabled(true);
                goToEndButton.setEnabled(true);
                animationSlider.setEnabled(true);

                automaticAlignmentRadioButton.setEnabled(true);
                controlledAlignmentRadioButton.setEnabled(true);
                automaticCrossoverRadioButton.setEnabled(true);
                controlledCrossoverRadioButton.setEnabled(true);
            }
        }
        else
        {
            currentOrganism = null;
            animationSlider.setValue(0);
            goToStartButton.setEnabled(false);
            playBackwardFastToggleButton.setEnabled(false);
            playBackwardToggleButton.setEnabled(false);
            stepBackwardButton.setEnabled(false);
            stopToggleButton.setEnabled(false);
            stepForwardButton.setEnabled(false);
            playForwardToggleButton.setEnabled(false);
            playForwardFastToggleButton.setEnabled(false);
            goToEndButton.setEnabled(false);
            animationSlider.setEnabled(false);

            automaticAlignmentRadioButton.setEnabled(false);
            controlledAlignmentRadioButton.setEnabled(false);
            automaticCrossoverRadioButton.setEnabled(false);
            controlledCrossoverRadioButton.setEnabled(false);
        }

        // Force the meiosis model to update size next time we paint
        actualHeight = 0;
        actualWidth = 0;

        // Repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.MEIOSIS_MODEL,oldMeiosisModel,meiosisModel);
    }
    
    /**
     * Get the small meiosis view.  May be null.
     *
     * @return		SmallMeiosisView - small meiosis view for which this is a big meiosis view, may be null
    **/
    public SmallMeiosisView getSmallMeiosisView()
    {
        return smallMeiosisView;
    }

    /**
     * Do the actual crossing over now given the current crossing over state.
    **/
    public void doCrossingOver()
    {
        // Return immediately if crossing over state not as expected
        if (meiosisChromosomeModelFirstCut == null ||
            meiosisChromosomeModelSecondCut == null)
        {
            return;
        }

        	// Get original, current state of two strands
	        OrganismAllele firstStrandAlleles[] = meiosisChromosomeModelFirstCut.getStrandAlleles(strandTypeFirstCut);
	        OrganismAllele secondStrandAlleles[] = meiosisChromosomeModelSecondCut.getStrandAlleles(strandTypeSecondCut);
	        Color firstStrandColors[] = meiosisChromosomeModelFirstCut.getStrandColors(strandTypeFirstCut);
	        Color secondStrandColors[] = meiosisChromosomeModelSecondCut.getStrandColors(strandTypeSecondCut);
	        if (firstStrandAlleles.length != secondStrandAlleles.length)
	        {
	            System.err.println("Unexpected chromosome lengths encountered during crossing over.");
	            return;
	        }

	        // Switch the colors and alleles for each strand (leaving X, Y coordinates unchanged)
	        int strandLength = firstStrandAlleles.length;
	        OrganismAllele newFirstStrandAlleles[] = new OrganismAllele[strandLength];
	        OrganismAllele newSecondStrandAlleles[] = new OrganismAllele[strandLength];
	        Color newFirstStrandColors[] = new Color[strandLength];
	        Color newSecondStrandColors[] = new Color[strandLength];

	        int i;
	        for (i=0;i<strandLength;i++)
	        {
	            if (i<strandPointIndexCut)
	            {
	                newFirstStrandAlleles[i] = firstStrandAlleles[i];
	                newSecondStrandAlleles[i] = secondStrandAlleles[i];
	                newFirstStrandColors[i] = firstStrandColors[i];
	                newSecondStrandColors[i] = secondStrandColors[i];
	            }
	            else
	            {
	                newFirstStrandAlleles[i] = secondStrandAlleles[i];
	                newSecondStrandAlleles[i] = firstStrandAlleles[i];
	                newFirstStrandColors[i] = secondStrandColors[i];
	                newSecondStrandColors[i] = firstStrandColors[i];
	            }
	        }

	        // Modify the strands
	        meiosisChromosomeModelFirstCut.setStrandAlleles(strandTypeFirstCut,newFirstStrandAlleles);
	        meiosisChromosomeModelSecondCut.setStrandAlleles(strandTypeSecondCut,newSecondStrandAlleles);
	        meiosisChromosomeModelFirstCut.setStrandColors(strandTypeFirstCut,newFirstStrandColors);
	        meiosisChromosomeModelSecondCut.setStrandColors(strandTypeSecondCut,newSecondStrandColors);

	        // Also modify the appropriate gamete cell chromosome models
	        // by telling them to regenerate their alleles and colors
	        MeiosisChromosomeModel aGameteChromosomeModel;
	        MeiosisChromosomeModel previousChromosomeModel;
	        Enumeration eGameteChromosomeModels;
	        for (i=0;i<4;i++)
	        {
	            switch(i)
	            {
	                case 0:
	                    eGameteChromosomeModels = meiosisModel.getGameteChromosomeModels(MeiosisModel.TOP_LEFT_GAMETE);                
	                    break;
	                case 1:
	                    eGameteChromosomeModels = meiosisModel.getGameteChromosomeModels(MeiosisModel.BOTTOM_LEFT_GAMETE);
	                    break;
	                case 2:
	                    eGameteChromosomeModels = meiosisModel.getGameteChromosomeModels(MeiosisModel.TOP_RIGHT_GAMETE);
	                    break;
	                default:
	                    eGameteChromosomeModels = meiosisModel.getGameteChromosomeModels(MeiosisModel.BOTTOM_RIGHT_GAMETE);
	                    break;
	            }

	            while (eGameteChromosomeModels.hasMoreElements())
	            {
	                aGameteChromosomeModel = (MeiosisChromosomeModel) eGameteChromosomeModels.nextElement();
	                previousChromosomeModel = aGameteChromosomeModel.getPreviousMeiosisChromosomeModel();
	                //if (previousChromosomeModel == meiosisChromosomeModelFirstCut ||
	                //	previousChromosomeModel == meiosisChromosomeModelSecondCut)
	                //{
	                    aGameteChromosomeModel.setAlleleAndColorVectors(null);
	                //}
	            }
	        }
	    
	  
    }
    
    /**
     * Update the state of this view, usually called because the state
     * of the underlying meiosis model has changed somehow and this
     * view should reflect that changed state.
    **/
    public void updateState()
    {
        if (meiosisModel != null)
        {
            currentOrganism = meiosisModel.getOrganism();
            animationSlider.setValue(meiosisModel.getStep());

            if (currentOrganism == null)
            {
                goToStartButton.setEnabled(false);
                playBackwardFastToggleButton.setEnabled(false);
                playBackwardToggleButton.setEnabled(false);
                stepBackwardButton.setEnabled(false);
                stopToggleButton.setEnabled(false);
                stepForwardButton.setEnabled(false);
                playForwardToggleButton.setEnabled(false);
                playForwardFastToggleButton.setEnabled(false);
                goToEndButton.setEnabled(false);
                animationSlider.setEnabled(false);

                automaticAlignmentRadioButton.setEnabled(false);
                controlledAlignmentRadioButton.setEnabled(false);
                automaticCrossoverRadioButton.setEnabled(false);
                controlledCrossoverRadioButton.setEnabled(false);
            }
            else
            {
                goToStartButton.setEnabled(true);
                playBackwardFastToggleButton.setEnabled(true);
                playBackwardToggleButton.setEnabled(true);
                stepBackwardButton.setEnabled(true);
                stopToggleButton.setEnabled(true);
                stepForwardButton.setEnabled(true);
                playForwardToggleButton.setEnabled(true);
                playForwardFastToggleButton.setEnabled(true);
                goToEndButton.setEnabled(true);
                animationSlider.setEnabled(true);
                
                automaticAlignmentRadioButton.setEnabled(true);
                controlledAlignmentRadioButton.setEnabled(true);
                automaticCrossoverRadioButton.setEnabled(true);
                controlledCrossoverRadioButton.setEnabled(true);
            }
        }
        else
        {
            currentOrganism = null;
            animationSlider.setValue(0);
            goToStartButton.setEnabled(false);
            playBackwardFastToggleButton.setEnabled(false);
            playBackwardToggleButton.setEnabled(false);
            stepBackwardButton.setEnabled(false);
            stopToggleButton.setEnabled(false);
            stepForwardButton.setEnabled(false);
            playForwardToggleButton.setEnabled(false);
            playForwardFastToggleButton.setEnabled(false);
            goToEndButton.setEnabled(false);
            animationSlider.setEnabled(false);
            actualHeight = 0;
            actualWidth = 0;

            automaticAlignmentRadioButton.setEnabled(false);
            controlledAlignmentRadioButton.setEnabled(false);
            automaticCrossoverRadioButton.setEnabled(false);
            controlledCrossoverRadioButton.setEnabled(false);
        }

        // Repaint
        repaint();
    }

    /**
     * Draw the graphics in this view.
     *
     * @param 		g Graphics - the given graphics to use in drawing
    **/
   	int oldStep1 =200;
    int oldStep2 = 200;
    int oldStep = 200;
    boolean resetValue = false;
    public void paintComponent(Graphics g)
    {
    
    	    	
        // Return immediately if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        // Update size if not current
        Rectangle bounds = getBounds();
        if (actualWidth != bounds.width || actualHeight != bounds.height)
        {
            updateSize();
        }

        // Draw background
        paintBackground(g,bounds);

        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Draw cell and chromosomes
        int currentStep = meiosisModel.getStep();
        
        boolean replicated = meiosisModel.isReplicated();
      
        // Draw chromosomes.
        // Done with a double while loop where the first loop is through a Vector
        // of Enumerations over the chromosomes in a particular portion of the
        // meiosis and the inner loop is over the chromosome models in that
        // particular Enumeration.  For example, when in meiosis steps 31 to 70
        // there are 2 daughter cells and hence 2 Enumerations, each enumeration
        // with a species-specific number of chromosome models for their particular
        // daughter cell.
        int i;
        int iStrand;
        int length;
        int xCentromere, yCentromere;
        int[] xPoints, xPointsStrandOne, xPointsStrandTwo;
        int[] yPoints, yPointsStrandOne, yPointsStrandTwo;
        Color[] colors;
        OrganismAllele[] alleles, allelesStrandOne, allelesStrandTwo;
        OrganismAllele[] nextAlleles = null;
        Color [] nextColors = null;
        String alleleStrandOneTextSymbol, alleleStrandTwoTextSymbol;
        int xTemporaryOffset = 0;
        int yTemporaryOffset = 0;

		Vector tempBranchs;
        MeiosisChromosomeModel aChromosomeModel;
        Enumeration eChromosomeModels;
		int index = 0;
        // Draw chromosomes
        g.setFont(getFont());
        
        Vector [] branchs = meiosisModel.getBranchs();
        Vector chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
       
        Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
       
        //while (eChromosomeModelEnumerations.hasMoreElements
       // System.out.println("chromosomeModelEnumerations.size()"+chromosomeModelEnumerations.size());
        for (int k =0;k<chromosomeModelEnumerations.size();k++)
        {
           // eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
          	eChromosomeModels =(Enumeration)chromosomeModelEnumerations.elementAt(k);
          	index = 0;
          	Branch temp = null;
            while (eChromosomeModels.hasMoreElements())
            {
            	aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
            	
            
            	if ( animationCrossover && currentStep >=CONTROL_CROSSOVER_STEP)
            	{
            		
            		
            	   if (currentStep<71)
            	   {
	            		tempBranchs = (Vector)branchs[k%2];
	               }
	               else
	               {
	               		if (k<branchs.length)
	               		{
	               			tempBranchs = (Vector)branchs[0];
	                    }
	                    else
	                    {
	                    	tempBranchs = (Vector)branchs[1];
	                    }
	               }
	            		
		            	if (index<tempBranchs.size())
		            	{
		            		temp = (Branch)tempBranchs.elementAt(index);
		            	    index = index +1;
		             	}
	              
	              
             	}
    			if (aChromosomeModel.isVisible() == true)
                {
                    xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                    yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();

                    xCentromere = (int) aChromosomeModel.getXCentromereAtStep(currentStep);
                    yCentromere = (int) aChromosomeModel.getYCentromereAtStep(currentStep);
    
                    for (iStrand=0;iStrand<4;iStrand++)
                    {
                        xPoints = null;
                        yPoints = null;
                        alleles = null;
                        colors = null;
                       	Enumeration eGameteChromosomeModels = null;
                        
                        // Get strand locations
                        switch (iStrand)
                        {
                        	
                            case 0:
                            	
                            		xPoints = aChromosomeModel.getXPStrandOnePoints(currentStep);
	                           		yPoints = aChromosomeModel.getYPStrandOnePoints(currentStep);
	                           		
	                            	alleles = aChromosomeModel.getPStrandOneAlleles();
	                           		colors = aChromosomeModel.getPStrandOneColors();
                            		if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP&&chromosomeModelEnumerations.size()!=3)
	                               	{
	                               		
	                           			alleles = temp.getPLStrand();
	                               		colors = temp.getPLStrandColor();
	                               		
	                               		if (currentStep>70)
	                               		{
	                               			switch(k%2)
	                               			{
	                               				case 0:
	                               				    alleles = temp.getPLStrand();
	                               					colors = temp.getPLStrandColor();
	                               					break;
	                               				case 1:
	                               					alleles = temp.getPRStrand();
	                               					colors = temp.getPRStrandColor();
	                               				    break;
	                               				
	                               				default:
	                               					break;
	                               			}
	                               		}
	                               		
	                               		if (xPoints.length !=alleles.length)//now x and y chromosomes
	                               		{
	                               			
	                               			alleles = aChromosomeModel.getPStrandOneAlleles();
	                           				colors = aChromosomeModel.getPStrandOneColors();
	                               		}
	                      			}
	                      			
	                      			if (chromosomeModelEnumerations.size()!=3)
	                      			{
	                      				aChromosomeModel.setPStrandOneAlleles(alleles);
	                      				aChromosomeModel.setPStrandOneColors(colors);
	                      			}
	                           		break;
    
                            case 1:
                                if (replicated)
                                {
                                	xPoints = aChromosomeModel.getXPStrandTwoPoints(currentStep);
                                    yPoints = aChromosomeModel.getYPStrandTwoPoints(currentStep);
                                    alleles = aChromosomeModel.getPStrandTwoAlleles();
                                    colors = aChromosomeModel.getPStrandTwoColors();
                                  
                                	 if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP)
                               		{	
                               		 	 alleles = temp.getPRStrand();
                                    	 colors = temp.getPRStrandColor();
                                    	 
                                    	 if (xPoints.length !=alleles.length)//now x and y chromosomes
	                               		{
	                               			alleles = aChromosomeModel.getPStrandTwoAlleles();
	                           				colors = aChromosomeModel.getPStrandTwoColors();
	                               		}
                              	 	}
                             
                                }
                               
                                break;
    
                            case 2:
                            		
                              		xPoints = aChromosomeModel.getXQStrandOnePoints(currentStep);
                                   	yPoints = aChromosomeModel.getYQStrandOnePoints(currentStep);
                                    alleles = aChromosomeModel.getQStrandOneAlleles();
                                    colors = aChromosomeModel.getQStrandOneColors();
                              		if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP &&chromosomeModelEnumerations.size()!=3)
	                               	{
	                               		alleles = temp.getQLStrand();
	                               		colors = temp.getQLStrandColor();
	                               		
	                               		if (currentStep>70)
	                               		{
	                               			switch(k%2)
	                               			{
	                               				case 0 :
	                               					alleles = temp.getQLStrand();
	                               					colors = temp.getQLStrandColor();
	                               					break;
	                               				case 1:
	                               					alleles = temp.getQRStrand();
	                               					colors = temp.getQRStrandColor();
	                               				    break;
	                               				
	                               				default:
	                               					break;
	                               			}
	                               		}
	                               		if (xPoints.length !=alleles.length)//now x and y chromosomes
	                               		{
	                               			alleles = aChromosomeModel.getQStrandOneAlleles();
	                           				colors = aChromosomeModel.getQStrandOneColors();
	                               		}
	                               		
	                      			}
	                      			if (chromosomeModelEnumerations.size()!=3)
	                      			{	
	                      				aChromosomeModel.setQStrandOneAlleles(alleles);
	                      				aChromosomeModel.setQStrandOneColors(colors);
	                      			}
	                      			
                                break;
    
                            case 3:
                                if (replicated)
                                {
                                	xPoints = aChromosomeModel.getXQStrandTwoPoints(currentStep);
                                    yPoints = aChromosomeModel.getYQStrandTwoPoints(currentStep);
                                    alleles = aChromosomeModel.getQStrandTwoAlleles();
                                    colors = aChromosomeModel.getQStrandTwoColors();
                                	if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP )
	                               	{
	                               		alleles = temp.getQRStrand();
	                               		colors = temp.getQRStrandColor();
									}
									if (xPoints.length !=alleles.length)//now x and y chromosomes
	                               	{
	                               		alleles = aChromosomeModel.getQStrandOneAlleles();
	                           			colors = aChromosomeModel.getQStrandOneColors();
	                               	}
	                              }
                               
                                break;
                        }
                        
                         
                        // Draw filled circles for this strand, if the strand exists
                        if (xPoints != null)
                        {
                            length = xPoints.length;
                            for (i=0;i<length;i++)
                            {
                                if (alleles[i] == null)
                                {
                                    g.setColor(colors[i]);
                                    g.fillOval(xPoints[i]-radiusChromosome+xTemporaryOffset,
                                               yPoints[i]-radiusChromosome+yTemporaryOffset,
                                               diameterChromosome,diameterChromosome);
                                }
                                else
                                {
                                    g.setColor(Color.red);
                                    g.fillOval(xPoints[i]-radiusChromosome+xTemporaryOffset,
                                               yPoints[i]-radiusChromosome+yTemporaryOffset,
                                               diameterChromosome,diameterChromosome);
                                }
                            }
                           
                        }
                    }

                    // Draw centromere after all strands drawn
                    g.setColor(Color.black);
                    g.fillOval(xCentromere-radiusCentromere+xTemporaryOffset,
                             yCentromere-radiusCentromere+yTemporaryOffset,
                               diameterCentromere,diameterCentromere);
                }
            }
        }

        // Draw allele symbols and leader lines, etc.
        chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
        eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
         
        for (int k =0;k<chromosomeModelEnumerations.size();k++)
        {
        	index = 0;
            eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            while (eChromosomeModels.hasMoreElements())
            {
                aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
                Branch temp = null;
            	  
            	if ( animationCrossover && currentStep >=CONTROL_CROSSOVER_STEP)
            	{
            	   if (currentStep<71)
            	   {
	            		tempBranchs = (Vector)branchs[k%2];
	               }
	               else
	               {
	               		if (k<branchs.length)
	               		{
	               			tempBranchs = (Vector)branchs[0];
	                    }
	                    else
	                    {
	                    	tempBranchs = (Vector)branchs[1];
	                    }
	               }
	            		
		            if (index<tempBranchs.size())
		            {
		            	temp = (Branch)tempBranchs.elementAt(index);
		                index = index +1;
		            }
	              
	              
             	}

                if (aChromosomeModel.isVisible() == true)
                {
                    xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                    yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();

                    xCentromere = (int) aChromosomeModel.getXCentromereAtStep(currentStep);
                    yCentromere = (int) aChromosomeModel.getYCentromereAtStep(currentStep);
        
                    for (iStrand=0;iStrand<2;iStrand++)
                    {
                        xPointsStrandOne = null;
                        xPointsStrandTwo = null;
                        yPointsStrandOne = null;
                        yPointsStrandTwo = null;
                        allelesStrandOne = null;
                        allelesStrandTwo = null;
        
                        // Get strand locations
                        switch (iStrand)
                        {
                            case 0:
                            	
                                xPointsStrandOne = aChromosomeModel.getXPStrandOnePoints(currentStep);
                                yPointsStrandOne = aChromosomeModel.getYPStrandOnePoints(currentStep);
                                allelesStrandOne = aChromosomeModel.getPStrandOneAlleles();
                                
                               if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP )
	                               	{
	                               		
	                           			allelesStrandOne = temp.getPLStrand();
	                               		
	                               		if (currentStep>70)
	                               		{
	                               			switch(k%2)
	                               			{
	                               				case 0:
	                               				    allelesStrandOne = temp.getPLStrand();
	                               					
	                               					break;
	                               				case 1:
	                               					allelesStrandOne = temp.getPRStrand();
	                               					
	                               				    break;
	                               				
	                               				default:
	                               					break;
	                               			}
	                               		}
	                               		
	                               		if (xPointsStrandOne.length !=allelesStrandOne.length)//now x and y chromosomes
	                               		{
	                               			allelesStrandOne = aChromosomeModel.getPStrandOneAlleles();
	                           			}
	                      			}
	
	                            if (replicated)
                                {
                                    xPointsStrandTwo = aChromosomeModel.getXPStrandTwoPoints(currentStep);
                                    yPointsStrandTwo = aChromosomeModel.getYPStrandTwoPoints(currentStep);
                                    allelesStrandTwo = aChromosomeModel.getPStrandTwoAlleles();
                                     if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP)
                               		{	
                               		 	 allelesStrandTwo = temp.getPRStrand();
                               		 	 if (xPointsStrandTwo.length !=allelesStrandTwo.length)//now x and y chromosomes
	                               		{
	                               			allelesStrandTwo = aChromosomeModel.getPStrandTwoAlleles();
	                           			}
                                    	
                              	 	}
                                    
                                }
                                break;
        
                            case 1:
                            	
                                xPointsStrandOne = aChromosomeModel.getXQStrandOnePoints(currentStep);
                                yPointsStrandOne = aChromosomeModel.getYQStrandOnePoints(currentStep);
                                allelesStrandOne = aChromosomeModel.getQStrandOneAlleles();
                                if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP )
	                               	{
	                               		
	                           			allelesStrandOne = temp.getQLStrand();
	                              		if (currentStep>70)
	                               		{
	                               			switch(k%2)
	                               			{
	                               				case 0 :
	                               					allelesStrandOne = temp.getQLStrand();
	                      						break;
	                               				case 1:
	                               					allelesStrandOne = temp.getQRStrand();
	                              				    break;
	                               				
	                               				default:
	                               					break;
	                               			}
	                               		}
	                               		
	                               		if (xPointsStrandOne.length !=allelesStrandOne.length)//now x and y chromosomes
	                               		{
	                               			allelesStrandOne = aChromosomeModel.getQStrandOneAlleles();
	                           			}
	                               		
	                      			}
                               
                                if (replicated)
                                {
                                  	
                                    xPointsStrandTwo = aChromosomeModel.getXQStrandTwoPoints(currentStep);
                                    yPointsStrandTwo = aChromosomeModel.getYQStrandTwoPoints(currentStep);
                                    allelesStrandTwo = aChromosomeModel.getQStrandTwoAlleles();
                                     if (animationCrossover && currentStep>=CONTROL_CROSSOVER_STEP)
                               		{	
                               		 	 allelesStrandTwo = temp.getQRStrand();
                               		 	 if (xPointsStrandTwo.length !=allelesStrandTwo.length)//now x and y chromosomes
	                               		{
	                               			allelesStrandTwo = aChromosomeModel.getQStrandTwoAlleles();
	                           			}
                                    	
                              	 	}
                                   
                                }
                                break;
                        }
                        
                        // Branch on whether we're drawing one or two strands
                        if (xPointsStrandOne != null && xPointsStrandTwo != null &&
                            yPointsStrandOne != null && yPointsStrandTwo != null &&
                            allelesStrandOne != null && allelesStrandTwo != null)
                        {
                            // Both strands shown
                            length = xPointsStrandOne.length;
                        
                            for (i=0;i<length;i++)
                            {
                                if (allelesStrandOne[i] != null && allelesStrandTwo[i] != null)
                                {
                                    // If this gene is not visible skip painting the allele text symbol
                                    if (! allelesStrandOne[i].getGene().isVisible())
                                        continue;
                                    alleleStrandOneTextSymbol = allelesStrandOne[i].getTextSymbol();
                                    alleleStrandTwoTextSymbol = allelesStrandTwo[i].getTextSymbol();
                                    
                                    // Determine which symbol goes on which side
                                    if (xPointsStrandOne[i] < xPointsStrandTwo[i])
                                    {
                                    		
                                    	if (currentStep !=CONTROL_CROSSOVER_STEP)
                                    	{	
                                    		paintAlleleTextSymbol(g,
	                                                              xPointsStrandOne[i], xTemporaryOffset,
	                                                              yPointsStrandOne[i], yTemporaryOffset,
	                                                              alleleStrandOneTextSymbol,
	                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
	                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT,0);
	    
	                                        paintAlleleTextSymbol(g,
	                                                              xPointsStrandTwo[i], xTemporaryOffset,
	                                                              yPointsStrandTwo[i], yTemporaryOffset,
	                                                              alleleStrandTwoTextSymbol,
	                                                              fontMetrics.stringWidth(alleleStrandTwoTextSymbol),
	                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT,0);
	                                    }
	                                    else
	                                    {
	                                      	if (k<(chromosomeModelEnumerations.size()/2))
		                                    {
		                                    	paintAlleleTextSymbol(g,
		                                                              xPointsStrandOne[i], xTemporaryOffset,
		                                                              yPointsStrandOne[i], yTemporaryOffset,
		                                                              alleleStrandOneTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT,60);
		    
		                                        paintAlleleTextSymbol(g,
		                                                              xPointsStrandTwo[i], xTemporaryOffset,
		                                                              yPointsStrandTwo[i], yTemporaryOffset,
		                                                              alleleStrandTwoTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandTwoTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT,30);
		                                     }
		                                     else
		                                     {
		                                     	paintAlleleTextSymbol(g,
		                                                              xPointsStrandOne[i], xTemporaryOffset,
		                                                              yPointsStrandOne[i], yTemporaryOffset,
		                                                              alleleStrandOneTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT,30);
		    
		                                        paintAlleleTextSymbol(g,
		                                                              xPointsStrandTwo[i], xTemporaryOffset,
		                                                              yPointsStrandTwo[i], yTemporaryOffset,
		                                                              alleleStrandTwoTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandTwoTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT,60);
		                                     }
		                               }
	                                  
                                    }
                                    else
                                    {
                                    	if (currentStep !=CONTROL_CROSSOVER_STEP)
                                    	{
	                                        // Make strand one's symbol be on right, two's on left
	                                      	paintAlleleTextSymbol(g,
	                                                              xPointsStrandOne[i], xTemporaryOffset,
	                                                              yPointsStrandOne[i], yTemporaryOffset,
	                                                              alleleStrandOneTextSymbol,
	                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
	                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT,0);
	    
	                                        paintAlleleTextSymbol(g,
	                                                              xPointsStrandTwo[i], xTemporaryOffset,
	                                                              yPointsStrandTwo[i], yTemporaryOffset,
	                                                              alleleStrandTwoTextSymbol,
	                                                              fontMetrics.stringWidth(alleleStrandTwoTextSymbol),
	                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT,0);
	                                    }
	                                    else
	                                    {
	                                    	if (k<(chromosomeModelEnumerations.size()/2))
		                                    {
		                                    	paintAlleleTextSymbol(g,
		                                                              xPointsStrandOne[i], xTemporaryOffset,
		                                                              yPointsStrandOne[i], yTemporaryOffset,
		                                                              alleleStrandOneTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT,60);
		    
		                                        paintAlleleTextSymbol(g,
		                                                              xPointsStrandTwo[i], xTemporaryOffset,
		                                                              yPointsStrandTwo[i], yTemporaryOffset,
		                                                              alleleStrandTwoTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandTwoTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT,30);
		                                     }
		                                     else
		                                     {
		                                     	paintAlleleTextSymbol(g,
		                                                              xPointsStrandOne[i], xTemporaryOffset,
		                                                              yPointsStrandOne[i], yTemporaryOffset,
		                                                              alleleStrandOneTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT,30);
		    
		                                        paintAlleleTextSymbol(g,
		                                                              xPointsStrandTwo[i], xTemporaryOffset,
		                                                              yPointsStrandTwo[i], yTemporaryOffset,
		                                                              alleleStrandTwoTextSymbol,
		                                                              fontMetrics.stringWidth(alleleStrandTwoTextSymbol),
		                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT,60);
		                                     }
	                                    }
	                                   
	                                }
	                                    
                                }
                            }
                            
                        }
                        else if (xPointsStrandOne != null && yPointsStrandOne != null && allelesStrandOne != null)
                        {
                            // Just strand one shown
                            length = xPointsStrandOne.length;
                            for (i=0;i<length;i++)
                            {
                                if (allelesStrandOne[i] != null)
                                {
                                    // If this gene is not visible skip painting the allele text symbol
                                    if (! allelesStrandOne[i].getGene().isVisible())
                                        continue;
                                    alleleStrandOneTextSymbol = allelesStrandOne[i].getTextSymbol();
                                    
                                    // Put symbol on far side of centromere
                                    if (xPointsStrandOne[i] < xCentromere)
                                    {
                                        // Make strand one's symbol be on left
                                        paintAlleleTextSymbol(g,
                                                              xPointsStrandOne[i], xTemporaryOffset,
                                                              yPointsStrandOne[i], yTemporaryOffset,
                                                              alleleStrandOneTextSymbol,
                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT,0);
                                    }
                                    else
                                    {
                                        // Make strand one's symbol be on right
                                        paintAlleleTextSymbol(g,
                                                              xPointsStrandOne[i], xTemporaryOffset,
                                                              yPointsStrandOne[i], yTemporaryOffset,
                                                              alleleStrandOneTextSymbol,
                                                              fontMetrics.stringWidth(alleleStrandOneTextSymbol),
                                                              fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT,0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Draw cell boundaries and spindles
        g.setColor(getForeground());

        Object boundary;
        CellArc cellArc;
        CellLine cellLine;
        SpindleLine spindleLine;
        Vector cellBoundaries = meiosisModel.getCellBoundaries();
        Enumeration eCellBoundaries = cellBoundaries.elements();
        
       // int gameteIndex = 0;
        while (eCellBoundaries.hasMoreElements())
        {
            boundary = eCellBoundaries.nextElement();

            if (boundary instanceof CellArc)
            {
                cellArc = (CellArc) boundary;
                g.drawArc(cellArc.getXLeft(), cellArc.getYTop(), cellArc.getWidth(), cellArc.getHeight(),
                          cellArc.getStartAngle(), cellArc.getSpanAngle());
                 
                if (meiosisModel.getStep() == 100)
                {
                    paintAlleleList(g, (CellArc) boundary);
                 
                }
               
            }
            else if (boundary instanceof CellLine)
            {
                cellLine = (CellLine) boundary;
                g.drawLine(cellLine.getX1(), cellLine.getY1(),
                           cellLine.getX2(), cellLine.getY2());
            }
            else if (boundary instanceof SpindleLine)
            {
                spindleLine = (SpindleLine) boundary;
                g.setColor(Color.lightGray);
                g.drawLine(spindleLine.getX1(), spindleLine.getY1(),
                           spindleLine.getX2(), spindleLine.getY2());
                g.setColor(getForeground());
            }
   
         
           
        }
     
         
        // If we're doing crossover, then we may be doing special graphics
        if (currentStep == CONTROL_CROSSOVER_STEP)
        {
                     		
          if (currentPlayMode != PLAY_MODE_CROSSING_OVER &&
                meiosisChromosomeModelFirstCut != null &&
                meiosisChromosomeModelSecondCut == null)
            {
                // Just drawing blinking arrows
                // Get the location of the first cut
                int xCut, yCut;
                int xFlashingCut, yFlashingCut;

                // Draw first cut as non-blinking arrow
                xTemporaryOffset = meiosisChromosomeModelFirstCut.getXTemporaryOffset();
                yTemporaryOffset = meiosisChromosomeModelFirstCut.getYTemporaryOffset();
                if (strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                    strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_TWO)
                {
                		//System.out.println("Cut P");
                    // Cut on P strand
                    xPointsStrandOne = meiosisChromosomeModelFirstCut.getXPStrandOnePoints(currentStep);
                    yPointsStrandOne = meiosisChromosomeModelFirstCut.getYPStrandOnePoints(currentStep);
                    xPointsStrandTwo = meiosisChromosomeModelFirstCut.getXPStrandTwoPoints(currentStep);
                    yPointsStrandTwo = meiosisChromosomeModelFirstCut.getYPStrandTwoPoints(currentStep);
                }
                else
                {
                	//System.out.println("Cut Q");
                    // Cut on Q strand
                    xPointsStrandOne = meiosisChromosomeModelFirstCut.getXQStrandOnePoints(currentStep);
                    yPointsStrandOne = meiosisChromosomeModelFirstCut.getYQStrandOnePoints(currentStep);
                    xPointsStrandTwo = meiosisChromosomeModelFirstCut.getXQStrandTwoPoints(currentStep);
                    yPointsStrandTwo = meiosisChromosomeModelFirstCut.getYQStrandTwoPoints(currentStep);
                }

                if (strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                    strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_ONE)
                {
                    // Cut on strand one, flashing cut on strand two
                   // System.out.println("strandPointIndexCut " +strandPointIndexCut);
                    
                    xCut = xPointsStrandOne[strandPointIndexCut] + xTemporaryOffset;
                    yCut = yPointsStrandOne[strandPointIndexCut] + yTemporaryOffset;
                    xFlashingCut = xPointsStrandTwo[strandPointIndexCut] + xTemporaryOffset;
                    yFlashingCut = yPointsStrandTwo[strandPointIndexCut] + yTemporaryOffset;
                   // System.out.println("strandPointIndexCut " +strandPointIndexCut);
                   // System.out.println("xcut " + xCut);
                    
                }
                else
                {
                    // Cut on strand two, flashing cut on strand one
                    xCut = xPointsStrandTwo[strandPointIndexCut] + xTemporaryOffset;
                    yCut = yPointsStrandTwo[strandPointIndexCut] + yTemporaryOffset;
                    xFlashingCut = xPointsStrandOne[strandPointIndexCut] + xTemporaryOffset;
                    yFlashingCut = yPointsStrandOne[strandPointIndexCut] + yTemporaryOffset;
                }

                // Draw the cut arrow
               
	                g.drawLine(xCut-12, yCut, xCut-4, yCut);
	                g.drawLine(xCut-4, yCut, xCut-8, yCut-2);
	                g.drawLine(xCut-4, yCut, xCut-8, yCut+2);
	            
	           
	            

                // Draw other 3 cuts as blinking arrows
                if (blinkCutLocationsVisible)
                {
                    // Draw the "flashing cut" on the other strand of the cut chromosome
                    
	                    g.drawLine(xFlashingCut-12, yFlashingCut, xFlashingCut-4, yFlashingCut);
	                    g.drawLine(xFlashingCut-4, yFlashingCut, xFlashingCut-8, yFlashingCut-2);
	                    g.drawLine(xFlashingCut-4, yFlashingCut, xFlashingCut-8, yFlashingCut+2);
	                
                    
                   

                    // Draw the other two flashing cuts, both on the "opposite" chromosome
                    // model from the one initially cut.  Sometimes there is no opposite
                    // chromosome (e.g. X and Y in an XY organism).
                    MeiosisChromosomeModel otherChromosomeModel = meiosisModel.getCrossoverChromosomeModel(meiosisChromosomeModelFirstCut);
                    if (otherChromosomeModel != null)
                    {
                        xTemporaryOffset = otherChromosomeModel.getXTemporaryOffset();
                        yTemporaryOffset = otherChromosomeModel.getYTemporaryOffset();
                        if (strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                            strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_TWO)
                        {
                            // Cut on P strand
                            xPointsStrandOne = otherChromosomeModel.getXPStrandOnePoints(currentStep);
                            yPointsStrandOne = otherChromosomeModel.getYPStrandOnePoints(currentStep);
                            xPointsStrandTwo = otherChromosomeModel.getXPStrandTwoPoints(currentStep);
                            yPointsStrandTwo = otherChromosomeModel.getYPStrandTwoPoints(currentStep);
                        }
                        else
                        {
                            // Cut on Q strand
                            xPointsStrandOne = otherChromosomeModel.getXQStrandOnePoints(currentStep);
                            yPointsStrandOne = otherChromosomeModel.getYQStrandOnePoints(currentStep);
                            xPointsStrandTwo = otherChromosomeModel.getXQStrandTwoPoints(currentStep);
                            yPointsStrandTwo = otherChromosomeModel.getYQStrandTwoPoints(currentStep);
                        }

                        // Draw cut on strand one
                        xCut = xPointsStrandOne[strandPointIndexCut] + xTemporaryOffset;
                        yCut = yPointsStrandOne[strandPointIndexCut] + yTemporaryOffset;

	                    
	                        g.drawLine(xCut-12, yCut, xCut-4, yCut);
	                        g.drawLine(xCut-4, yCut, xCut-8, yCut-2);
	                        g.drawLine(xCut-4, yCut, xCut-8, yCut+2);
	                   

                        // Draw cut on strand two
                        xCut = xPointsStrandTwo[strandPointIndexCut] + xTemporaryOffset;
                        yCut = yPointsStrandTwo[strandPointIndexCut] + yTemporaryOffset;

                        
                        g.drawLine(xCut-12, yCut, xCut-4, yCut);
                       	g.drawLine(xCut-4, yCut, xCut-8, yCut-2);
                       	g.drawLine(xCut-4, yCut, xCut-8, yCut+2);
                       
                    }
                }
            }
        }

        // Draw gray band along the bottom underneath all the other controls
        g.setColor(Color.lightGray);
        g.fillRect(2,actualHeight-52,actualWidth-4,50);
        g.setColor(Color.black);
        if (alignmentControlsVisible)
        {
            g.drawString("Alignment:",287,actualHeight-38);
        }
        if (crossoverControlsVisible)
        {
            g.drawString("Crossing Over:",385,actualHeight-38);
        }
    }
    
    protected Vector getGameteChromosomeModels(int modelType)
    {
        Vector result = new Vector();
        if (meiosisModel instanceof MeiosisModel)
        {
            Vector chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
            Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
            while (eChromosomeModelEnumerations.hasMoreElements())
            {
                Enumeration eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
                while (eChromosomeModels.hasMoreElements())
                {
                    MeiosisChromosomeModel model = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
                   
                    if (model.getChromosomeModelType() == modelType)
                        result.addElement(model);
                }
            }
        }
        return result;
    }
    
    protected int getTypeFromCellArc(CellArc cellArc)
    {
        int xLeft = cellArc.getXLeft();
        int yTop = cellArc.getYTop();
        int width = cellArc.getWidth();
        int height = cellArc.getHeight();
        if (xLeft < width)
        {
            if (yTop < height)
                return MeiosisChromosomeModel.TOP_LEFT_GAMETE_CHROMOSOME;
            else
                return MeiosisChromosomeModel.BOTTOM_LEFT_GAMETE_CHROMOSOME;
        }
        else
        {
            if (yTop < height)
                return MeiosisChromosomeModel.TOP_RIGHT_GAMETE_CHROMOSOME;
            else
                return MeiosisChromosomeModel.BOTTOM_RIGHT_GAMETE_CHROMOSOME;
        }
    }

    protected void paintAlleleList(Graphics g, CellArc boundary)
    {
        int type = getTypeFromCellArc(boundary);
        Vector models = getGameteChromosomeModels(type);
        arrGametes[type-4]= new Gamete();
        arrGametes[type-4].setTypeOfGamete(type);
        int xText = boundary.getXLeft();
        int yText = boundary.getYTop();
      	
        for (int i = 0; i <models.size() ; i++)
        {
            MeiosisChromosomeModel model = (MeiosisChromosomeModel) models.elementAt(i);
          //  Branch temp = null;
            
         
            for (int iStrand = 0; iStrand < 2; iStrand++)
            {
                OrganismAllele [] alleles = null;
                int [] xAlleles = null;
                int [] yAlleles = null;
                int xOffset = model.getXTemporaryOffset();
                int yOffset = model.getYTemporaryOffset();
                if (iStrand < 1)
                {
                    alleles = model.getPStrandOneAlleles();
                   
                    xAlleles = model.getXPStrandOnePoints(100);
                    yAlleles = model.getYPStrandOnePoints(100);
                    
                }
                else
                {
                    alleles = model.getQStrandOneAlleles();
                   
                    xAlleles = model.getXQStrandOnePoints(100);
                    yAlleles = model.getYQStrandOnePoints(100);
                   
                }
                for (int j = 0; j < alleles.length; j++)
                {
                    if (alleles[j] == null)
                        continue;
                    if (alleles[j].getGene().isVisible())
                    {
                        int xAllele = xAlleles[j] + xOffset;
                        int yAllele = yAlleles[j] + yOffset;
                        String text = alleles[j].getTextSymbol();
                       
                       	arrGametes[type-4].setAlleleInfor(alleles[j]);
                       	arrGametes[type-4].setAllelesString(text);
                        int textWidth = fontMetrics.stringWidth(text);
                        g.setColor(Color.black);
                        g.drawLine(xText + (textWidth / 2), yText + fontHeight, xAllele, yAllele);
                        g.setColor(Color.white);
                        g.fillRect(xText, yText, textWidth + 6, fontHeight);
                        g.setColor(getForeground());
                        g.drawRect(xText, yText, textWidth + 6, fontHeight);
                        g.drawString(text, xText + 3, yText + fontHeight - 2);
                        xText = xText + textWidth + 12;
                    }
                }
            
            }
        }
        meiosisModel.setGametes(arrGametes);
    }
    
    /**
     * Paint the allele text symbol on the given side of the allele's location.<p>
     *
     * @param		g Graphics - the graphics object to use in drawing
     * @param		xPoint int - x location of allele
     * @param		yPoint int - y location of allele
     * @param		text String - allele text
     * @param		textWidth int - allele text width in the current font
     * @param		fontAscent int - ascent of current font
     * @param		fontHeight int - height of current font
     * @param		side int - side of text symbol (DRAW_ALLELE_SYMBOL_RIGHT or DRAW_ALLELE_SYMBOL_LEFT)
    **/
    private void paintAlleleTextSymbol(Graphics g,
                                       int xPoint, int xOffset, int yPoint, int yOffset,
                                       String text, int textWidth,
                                       int fontAscent, int fontHeight,
                                       int side, int lengthExt)
    {
        if (meiosisModel.getStep() < 100)
        {
            xPoint += xOffset;
            yPoint += yOffset;
            if (side == DRAW_ALLELE_SYMBOL_RIGHT)
            {
                // Draw symbol on right side
                g.setColor(Color.black); //lightGray);
                g.drawLine(xPoint+radiusChromosome,
                           yPoint-radiusChromosome,
                           xPoint+radiusChromosome+lengthLeaderLine+lengthExt,
                           yPoint-radiusChromosome);
                g.setColor(getBackground());
                g.fillRect(xPoint+radiusChromosome+lengthLeaderLine+lengthExt+3,
                           yPoint-radiusChromosome-(fontHeight/2),
                           textWidth+4,fontHeight);
                g.setColor(getForeground());
                g.drawRect(xPoint+radiusChromosome+lengthLeaderLine+lengthExt+3,
                           yPoint-radiusChromosome-(fontHeight/2),
                           textWidth+4,fontHeight);
                g.drawString(text,
                             xPoint+radiusChromosome+lengthLeaderLine+lengthExt+5,
                             yPoint-radiusChromosome-(fontHeight/2)+fontAscent);
            }
            else if (side == DRAW_ALLELE_SYMBOL_LEFT)
            {
                // Draw symbol on left side
                g.setColor(Color.green);
                g.drawLine(xPoint-radiusChromosome,
                           yPoint-radiusChromosome,
                           xPoint-radiusChromosome-lengthLeaderLine-lengthExt,
                           yPoint-radiusChromosome);
                g.setColor(getBackground());
                g.fillRect(xPoint-radiusChromosome-lengthLeaderLine-textWidth-lengthExt-4,
                           yPoint-radiusChromosome-(fontHeight/2),
                           textWidth+4,fontHeight);
                g.setColor(getForeground());
                g.drawRect(xPoint-radiusChromosome-lengthLeaderLine-textWidth-lengthExt-4,
                           yPoint-radiusChromosome-(fontHeight/2),
                           textWidth+4,fontHeight);
                g.drawString(text,
                             xPoint-radiusChromosome-lengthLeaderLine-textWidth-lengthExt-1,
                             yPoint-radiusChromosome-(fontHeight/2)+fontAscent);
            }
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

        if (bounds.width != actualWidth || bounds.height != actualHeight)
        {
            // Update sizes of cell, graphics, models, etc.
            actualWidth = bounds.width;
            actualHeight = bounds.height;

            // Update size of centromere and chromosome circles used in drawing
            if (actualWidth < 300 || actualHeight < 300)
            {
                radiusCentromere = 2;
                diameterCentromere = 3;
                radiusChromosome = 2;
                diameterChromosome = 3;
                lengthLeaderLine = 12;
            }
            else if (actualWidth < 400 || actualHeight < 500)
            {
                radiusCentromere = 2;
                diameterCentromere = 4;
                radiusChromosome = 2;
                diameterChromosome = 4;
                lengthLeaderLine = 16;
            }
            else if (actualWidth < 500 || actualHeight < 700)
            {
                radiusCentromere = 3;
                diameterCentromere = 6;
                radiusChromosome = 3;
                diameterChromosome = 6;
                lengthLeaderLine = 24;
            }
            else
            {
                radiusCentromere = 4;
                diameterCentromere = 8;
                radiusChromosome = 4;
                diameterChromosome = 8;
                lengthLeaderLine = 32;
            }

            // Update meiosis model and its chromosome models
            if (meiosisModel != null)
            {
                meiosisModel.setEnclosingViewRectangle(new Rectangle(0,0,actualWidth,actualHeight-52));
            }

            // Move slider and buttons appropriately
            magnifyButton.setBounds(2,actualHeight-52,25,25);
            animationSlider.setBounds(27,actualHeight-52,250,25);
            goToStartButton.setBounds(2,actualHeight-27,25,25);
            playBackwardFastToggleButton.setBounds(27,actualHeight-27,25,25);
            playBackwardToggleButton.setBounds(52,actualHeight-27,25,25);
            stepBackwardButton.setBounds(77,actualHeight-27,25,25);
            stopToggleButton.setBounds(102,actualHeight-27,50,25);
            stepForwardButton.setBounds(152,actualHeight-27,25,25);
            playForwardToggleButton.setBounds(177,actualHeight-27,50,25);
            playForwardFastToggleButton.setBounds(227,actualHeight-27,25,25);
            goToEndButton.setBounds(252,actualHeight-27,25,25);
            automaticAlignmentRadioButton.setBounds(302,actualHeight-35,80,15);
            controlledAlignmentRadioButton.setBounds(302,actualHeight-20,80,15);
            automaticCrossoverRadioButton.setBounds(400,actualHeight-35,80,15);
            controlledCrossoverRadioButton.setBounds(400,actualHeight-20,80,15);

            // Reposition alignment buttons appropriately
            if (switchAlignmentButtonsVisible)
            {
                showSwitchAlignmentButtons();
            }

            repaint();
        }
    }

    /**
     * Show switch alignment buttons
    **/
    public void showSwitchAlignmentButtons()
    {
        // Can't show buttons if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        // Don't return if switchAlignmentButtonsVisible already
        // true, as this method is called sometimes to reposition
        // the alignment buttons when the view is resized.

        int currentStep = meiosisModel.getStep();

        int iButton = 0;

        if (currentStep == CONTROL_HORIZONTAL_ALIGNMENT_STEP)
        {
            switchAlignmentButtonsVisible = true;

            int xLeftCentromere, yLeftCentromere;
            int xRightCentromere, yRightCentromere;
            MeiosisChromosomeModel aLeftChromosomeModel, aRightChromosomeModel;
            Enumeration eLeftDaughterCellChromosomeModels;
            Enumeration eRightDaughterCellChromosomeModels;
            Vector chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();

            Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
            eLeftDaughterCellChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            eRightDaughterCellChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();

            while (eLeftDaughterCellChromosomeModels.hasMoreElements() &&
                   eRightDaughterCellChromosomeModels.hasMoreElements())
            {
                aLeftChromosomeModel = (MeiosisChromosomeModel) eLeftDaughterCellChromosomeModels.nextElement();
                aRightChromosomeModel = (MeiosisChromosomeModel) eRightDaughterCellChromosomeModels.nextElement();

                if (aLeftChromosomeModel.isVisible() == true &&
                    aRightChromosomeModel.isVisible() == true)
                {
                    xLeftCentromere = (int) aLeftChromosomeModel.getXCentromereAtStep(currentStep);
                    yLeftCentromere = (int) aLeftChromosomeModel.getYCentromereAtStep(currentStep);

                    xRightCentromere = (int) aRightChromosomeModel.getXCentromereAtStep(currentStep);
                    yRightCentromere = (int) aRightChromosomeModel.getYCentromereAtStep(currentStep);

                    if (iButton < NUMBER_OF_SWITCH_HORIZONTAL_ALIGNMENT_BUTTONS)
                    {
                        switchHorizontalAlignmentButton[iButton].setBounds(((xLeftCentromere + xRightCentromere)/2)-10,
                                                                 yLeftCentromere-12, 20, 24);
                        switchHorizontalAlignmentButton[iButton].setVisible(true);
                        switchHorizontalAlignmentButton[iButton].setEnabled(true);
                    }

                    iButton++;
                }
            }

            // Also want to start blinking if a cut has been made
            if (meiosisChromosomeModelFirstCut != null)
            {
                startBlinking();
            }
        }
        else if (currentStep == CONTROL_VERTICAL_ALIGNMENT_STEP)
        {
            switchAlignmentButtonsVisible = true;

            int xTopLeftCentromere, yTopLeftCentromere;
            int xBottomLeftCentromere, yBottomLeftCentromere;
            int xTopRightCentromere, yTopRightCentromere;
            int xBottomRightCentromere, yBottomRightCentromere;

            MeiosisChromosomeModel aTopLeftChromosomeModel, aTopRightChromosomeModel;
            MeiosisChromosomeModel aBottomLeftChromosomeModel, aBottomRightChromosomeModel;

            Enumeration eTopLeftGameteChromosomeModels, eBottomLeftGameteChromosomeModels;
            Enumeration eTopRightGameteChromosomeModels, eBottomRightGameteChromosomeModels;

            Vector chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();

            Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
            eTopLeftGameteChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            eBottomLeftGameteChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            eTopRightGameteChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            eBottomRightGameteChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();

            // Left gametes
            while (eTopLeftGameteChromosomeModels.hasMoreElements() &&
                   eBottomLeftGameteChromosomeModels.hasMoreElements())
            {
                aTopLeftChromosomeModel = (MeiosisChromosomeModel) eTopLeftGameteChromosomeModels.nextElement();
                aBottomLeftChromosomeModel = (MeiosisChromosomeModel) eBottomLeftGameteChromosomeModels.nextElement();

                if (aTopLeftChromosomeModel.isVisible() == true &&
                    aBottomLeftChromosomeModel.isVisible() == true)
                {
                    xTopLeftCentromere = (int) aTopLeftChromosomeModel.getXCentromereAtStep(currentStep);
                    yTopLeftCentromere = (int) aTopLeftChromosomeModel.getYCentromereAtStep(currentStep);

                    xBottomLeftCentromere = (int) aBottomLeftChromosomeModel.getXCentromereAtStep(currentStep);
                    yBottomLeftCentromere = (int) aBottomLeftChromosomeModel.getYCentromereAtStep(currentStep);

                    if (iButton < NUMBER_OF_SWITCH_VERTICAL_ALIGNMENT_BUTTONS)
                    {
                        switchVerticalAlignmentButton[iButton].setBounds(xTopLeftCentromere-12,
                                                                        ((yTopLeftCentromere+yBottomLeftCentromere)/2)-10, 24, 20);
                        switchVerticalAlignmentButton[iButton].setVisible(true);
                        switchVerticalAlignmentButton[iButton].setEnabled(true);
                    }

                    iButton++;
                }
            }

            // Right gametes
            while (eTopRightGameteChromosomeModels.hasMoreElements() &&
                   eBottomRightGameteChromosomeModels.hasMoreElements())
            {
                aTopRightChromosomeModel = (MeiosisChromosomeModel) eTopRightGameteChromosomeModels.nextElement();
                aBottomRightChromosomeModel = (MeiosisChromosomeModel) eBottomRightGameteChromosomeModels.nextElement();

                if (aTopRightChromosomeModel.isVisible() == true &&
                    aBottomRightChromosomeModel.isVisible() == true)
                {
                    xTopRightCentromere = (int) aTopRightChromosomeModel.getXCentromereAtStep(currentStep);
                    yTopRightCentromere = (int) aTopRightChromosomeModel.getYCentromereAtStep(currentStep);

                    xBottomRightCentromere = (int) aBottomRightChromosomeModel.getXCentromereAtStep(currentStep);
                    yBottomRightCentromere = (int) aBottomRightChromosomeModel.getYCentromereAtStep(currentStep);

                    if (iButton < NUMBER_OF_SWITCH_VERTICAL_ALIGNMENT_BUTTONS)
                    {
                        switchVerticalAlignmentButton[iButton].setBounds(xTopRightCentromere-12,
                                                                        ((yTopRightCentromere+yBottomRightCentromere)/2)-10, 24, 20);
                        switchVerticalAlignmentButton[iButton].setVisible(true);
                        switchVerticalAlignmentButton[iButton].setEnabled(true);
                    }

                    iButton++;
                }
            }
        }
        else
        {
            // Not at one of the steps where the buttons should be shown, so hide them
            if (switchAlignmentButtonsVisible)
            {
                hideSwitchAlignmentButtons();
            }
        }

        repaint();
    }

    /**
     * Hide switch alignment buttons
    **/
    public void hideSwitchAlignmentButtons()
    {
        switchAlignmentButtonsVisible = false;

        int i;
        int length = switchHorizontalAlignmentButton.length;
        for (i=0;i<length;i++)
        {
            switchHorizontalAlignmentButton[i].setVisible(false);
            switchHorizontalAlignmentButton[i].setEnabled(false);
        }

        length = switchVerticalAlignmentButton.length;
        for (i=0;i<length;i++)
        {
            switchVerticalAlignmentButton[i].setVisible(false);
            switchVerticalAlignmentButton[i].setEnabled(false);
        }

        // Also want to stop blinking and clear crossing over state
        clearCrossingOverState();
    }

    /**
     * Get alignment controls visibility
     *
     * @return	boolean - visibility
    **/
    public boolean isAlignmentControlsVisible()
    {
        return alignmentControlsVisible;
    }

    /**
     * Set alignment controls visibility
     *
     * @param	aVisible boolean - visibility
    **/
    public void setAlignmentControlsVisible(boolean aVisible)
    {
        // Return immediately if no change
        if (aVisible == alignmentControlsVisible)
        {
            return;
        }

        // Make change
        boolean oldAlignmentControlsVisible = alignmentControlsVisible;
        alignmentControlsVisible = aVisible;

        automaticAlignmentRadioButton.setVisible(alignmentControlsVisible);
        controlledAlignmentRadioButton.setVisible(alignmentControlsVisible);

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.ALIGNMENT_CONTROLS_VISIBLE,
                                   new Boolean(oldAlignmentControlsVisible),
                                   new Boolean(alignmentControlsVisible));
    }

    /**
     * Get crossover controls visibility
     *
     * @return	boolean - visibility
    **/
    public boolean isCrossoverControlsVisible()
    {
        return crossoverControlsVisible;
    }

    /**
     * Set crossover controls visibility
     *
     * @param	aVisible boolean - visibility
    **/
    public void setCrossoverControlsVisible(boolean aVisible)
    {
        // Return immediately if no change
        if (aVisible == crossoverControlsVisible)
        {
            return;
        }
        
         
      
        // Make change
        boolean oldCrossoverControlsVisible = crossoverControlsVisible;
        crossoverControlsVisible = aVisible;

        automaticCrossoverRadioButton.setVisible(crossoverControlsVisible);
        controlledCrossoverRadioButton.setVisible(crossoverControlsVisible);

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.CROSSOVER_CONTROLS_VISIBLE,
                                   new Boolean(oldCrossoverControlsVisible),
                                   new Boolean(crossoverControlsVisible));
    }

    /**
     * Set magnify button visibility.
     *
     * @param		visible boolean - visibility (0 to 100)
    **/
    public void setMagnifyButtonVisible(boolean visible)
    {
        magnifyButton.setVisible(visible);
    }

    /**
     * Set magnify button enabled.
     *
     * @param		enabled boolean 
    **/
    public void setMagnifyButtonEnabled(boolean enabled)
    {
        magnifyButton.setEnabled(enabled);
    }
    
    /**
     * Set animation slider visibility.
     *
     * @param		visible boolean - visibility (0 to 100)
    **/
    public void setAnimationSliderVisible(boolean visible)
    {
        animationSlider.setVisible(visible);
    }

    /**
     * Set animation slider enabled.
     *
     * @param		enabled boolean 
    **/
    public void setAnimationSliderEnabled(boolean enabled)
    {
        animationSlider.setEnabled(enabled);
    }
    
    /**
     * Set stop toggle visibility.
     *
     * @param		visible boolean - visibility (0 to 100)
    **/
    public void setStopToggleButtonVisible(boolean visible)
    {
        stopToggleButton.setVisible(visible);
    }

    /**
     * Set stop toggle enabled.
     *
     * @param		enabled boolean 
    **/
    public void setStopToggleButtonEnabled(boolean enabled)
    {
        stopToggleButton.setEnabled(enabled);
    }
    
    /**
     * Set play forward toggle button visibility.
     *
     * @param		visible boolean - visibility (0 to 100)
    **/
    public void setPlayForwardToggleButtonVisible(boolean visible)
    {
        playForwardToggleButton.setVisible(visible);
    }

    /**
     * Set play forward toggle button enabled.
     *
     * @param		enabled boolean 
    **/
    public void setPlayForwardToggleButtonEnabled(boolean enabled)
    {
        playForwardToggleButton.setEnabled(enabled);
    }
    
    /**
     * Check magnify button visibility.
     *
    **/
    public boolean isMagnifyButtonVisible()
    {
        return magnifyButton.isVisible();
    }

    /**
     * Check animation slider visibility.
     *
    **/
    public boolean isAnimationSliderVisible()
    {
        return animationSlider.isVisible();
    }

    /**
     * Check stop toggle visibility.
     *
    **/
    public boolean isStopToggleButtonVisible()
    {
        return stopToggleButton.isVisible();
    }

    /**
     * Check play forward toggle button visibility.
     *
    **/
    public boolean isPlayForwardToggleButtonVisible()
    {
        return playForwardToggleButton.isVisible();
    }

    /**
     * Set go to start button visibility.
     *
     * @param		visible boolean - visibility
    **/
    public void setGoToStartButtonVisible(boolean visible)
    {
        goToStartButton.setVisible(visible);
    }
    
    /**
     * Set go to start button enabled.
     *
     * @param		enabled boolean 
    **/
    public void setGoToStartButtonEnabled(boolean enabled)
    {
        goToStartButton.setEnabled(enabled);
    }

    /**
     * Set play backward fast toggle button visibility.
     *
     * @param		visible boolean - visibility
    **/
    public void setPlayBackwardFastToggleButtonVisible(boolean visible)
    {
        playBackwardFastToggleButton.setVisible(visible);
    }

    /**
     * Set play backward fast toggle button enabled.
     *
     * @param		enabled boolean 
    **/
    public void setPlayBackwardFastToggleButtonEnabled(boolean enabled)
    {
        playBackwardFastToggleButton.setEnabled(enabled);
    }
    
    /**
     * Set play backward toggle button visibility.
     *
     * @param		visible boolean - visibility
    **/
    public void setPlayBackwardToggleButtonVisible(boolean visible)
    {
        playBackwardToggleButton.setVisible(visible);
    }
    
    /**
     * Set play backward toggle button enabled.
     *
     * @param		enabled boolean 
    **/
    public void setPlayBackwardToggleButtonEnabled(boolean enabled)
    {
        playBackwardToggleButton.setEnabled(enabled);
    }

    /**
     * Set step backward button visibility.
     *
     * @param		visible boolean - visibility
    **/
    public void setStepBackwardButtonVisible(boolean visible)
    {
        stepBackwardButton.setVisible(visible);
    }
    
    /**
     * Set step backward button enabled.
     *
     * @param		enabled boolean
    **/
    public void setStepBackwardButtonEnabled(boolean enabled)
    {
        stepBackwardButton.setEnabled(enabled);
    }

    /**
     * Set step forward button visibility.
     *
     * @param		visible boolean - visibility
    **/
    public void setStepForwardButtonVisible(boolean visible)
    {
        stepForwardButton.setVisible(visible);
    }
    
    /**
     * Set step forward button enabled.
     *
     * @param		enabled boolean 
    **/
    public void setStepForwardButtonEnabled(boolean enabled)
    {
        stepForwardButton.setEnabled(enabled);
    }

    /**
     * Set play forward fast toggle button visibility.
     *
     * @param		visible boolean - visibility
    **/
    public void setplayForwardFastToggleButtonVisible(boolean visible)
    {
        playForwardFastToggleButton.setVisible(visible);
    }
    
    /**
     * Set play forward fast toggle button enabled.
     *
     * @param		enabled boolean
    **/
    public void setplayForwardFastToggleButtonEnabled(boolean enabled)
    {
        playForwardFastToggleButton.setEnabled(enabled);
    }
    
    /**
     * Set go to end button visibility.
     *
     * @param		visible boolean - visibility
    **/
    public void setGoToEndButtonVisible(boolean visible)
    {
        goToEndButton.setVisible(visible);
    }
    
    /**
     * Set go to end button enabled.
     *
     * @param		enabled boolean
    **/
    public void setGoToEndButtonEnabled(boolean enabled)
    {
        goToEndButton.setEnabled(enabled);
    }
    
    /**
     * Check go to start button visibility.
     *
    **/
    public boolean isGoToStartButtonVisible()
    {
        return goToStartButton.isVisible();
    }

    /**
     * Check play backward fast toggle button visibility.
     *
    **/
    public boolean isPlayBackwardFastToggleButtonVisible()
    {
        return playBackwardFastToggleButton.isVisible();
    }

    /**
     * Check play backward toggle button visibility.
     *
    **/
    public boolean isPlayBackwardToggleButtonVisible()
    {
        return playBackwardToggleButton.isVisible();
    }

    /**
     * Check step backward button visibility.
     *
    **/
    public boolean isStepBackwardButtonVisible()
    {
        return stepBackwardButton.isVisible();
    }

    /**
     * Check step forward button visibility.
     *
    **/
    public boolean isStepForwardButtonVisible()
    {
        return stepForwardButton.isVisible();
    }

    /**
     * Check play forward fast toggle button visibility.
     *
    **/
    public boolean isplayForwardFastToggleButtonVisible()
    {
        return playForwardFastToggleButton.isVisible();
    }
    
    /**
     * Check go to end button visibility.
     *
    **/
    public boolean isGoToEndButtonVisible()
    {
        return goToEndButton.isVisible();
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
        if (meiosisModel != null)
        {
            // Clear state if we think we still have chromosome selected for some reason
            if (selectedMeiosisChromosomeModel != null)
            {
                selectedMeiosisChromosomeModel = null;
                xMouseDown = 0;
                yMouseDown = 0;
            }
            // Mouse coordinates
            int xMouse = event.getX();
            int yMouse = event.getY();
       
         
            int currentStep = meiosisModel.getStep();
            boolean replicated = meiosisModel.isReplicated();
            MeiosisChromosomeModel closestMeiosisChromosomeModel = null;

            int xDelta, yDelta;
            int xyDeltaMin = 10000;

            int i;
            int iStrand;
            int length;
            int[] xPointsStrandOne, xPointsStrandTwo;
            int[] yPointsStrandOne, yPointsStrandTwo;
            int xTemporaryOffset = 0;
            int yTemporaryOffset = 0;

            MeiosisChromosomeModel aChromosomeModel;
            Enumeration eChromosomeModels;
            Vector chromosomeModelEnumerations;
            Enumeration eChromosomeModelEnumerations;
            
                  
            if (activeTool == Tool.SELECTION)
            {
                // Find the closest chromosome and, if we find one sufficiently close,
                // record that chromosome, the current step and put us into move mode.

                // Walk through chromosomes finding closest chromosome
                chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
                eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
                while (eChromosomeModelEnumerations.hasMoreElements())
                {
                    eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
                    while (eChromosomeModels.hasMoreElements())
                    {
                        aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();

                        // Only pick on visible chromosomes
                        if (aChromosomeModel.isVisible() == true)
                        {
                            xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                            yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();

                            for (iStrand=0;iStrand<2;iStrand++)
                            {
                                xPointsStrandOne = null;
                                xPointsStrandTwo = null;
                                yPointsStrandOne = null;
                                yPointsStrandTwo = null;

                                // Get strand locations
                                switch (iStrand)
                                {
                                    case 0:
                                        xPointsStrandOne = aChromosomeModel.getXPStrandOnePoints(currentStep);
                                        yPointsStrandOne = aChromosomeModel.getYPStrandOnePoints(currentStep);
                                        if (replicated)
                                        {
                                            xPointsStrandTwo = aChromosomeModel.getXPStrandTwoPoints(currentStep);
                                            yPointsStrandTwo = aChromosomeModel.getYPStrandTwoPoints(currentStep);
                                        }
                                        break;

                                    case 1:
                                        xPointsStrandOne = aChromosomeModel.getXQStrandOnePoints(currentStep);
                                        yPointsStrandOne = aChromosomeModel.getYQStrandOnePoints(currentStep);
                                        if (replicated)
                                        {
                                            xPointsStrandTwo = aChromosomeModel.getXQStrandTwoPoints(currentStep);
                                            yPointsStrandTwo = aChromosomeModel.getYQStrandTwoPoints(currentStep);
                                        }
                                        break;
                                }

                                // Branch on whether we're drawing one or two strands
                                if (xPointsStrandOne != null && xPointsStrandTwo != null &&
                                    yPointsStrandOne != null && yPointsStrandTwo != null)
                                {
                                    // Both strands shown
                                    length = xPointsStrandOne.length;
                                    for (i=0;i<length;i++)
                                    {
                                        int xOffset = xPointsStrandOne[i]+xTemporaryOffset;
                                        int yOffset = yPointsStrandOne[i]+yTemporaryOffset;
                                        // Check strand one
                                        if (xMouse > xOffset)
                                        {
                                            xDelta = xMouse - xOffset;
                                        }
                                        else
                                        {
                                            xDelta = xOffset - xMouse;
                                        }

                                        if (yMouse > yOffset)
                                        {
                                            yDelta = yMouse - yOffset;
                                        }
                                        else
                                        {
                                            yDelta = yOffset - yMouse;
                                        }

                                        if (xDelta + yDelta < xyDeltaMin)
                                        {
                                            xyDeltaMin = xDelta + yDelta;
                                            closestMeiosisChromosomeModel = aChromosomeModel;
                                        }
                                    }
                                    
                                    for (i=0;i<length;i++)
                                    {
                                        int xOffset = xPointsStrandTwo[i]+xTemporaryOffset;
                                        int yOffset = yPointsStrandTwo[i]+yTemporaryOffset;
                                        // Check strand two
                                        if (xMouse > xOffset)
                                        {
                                            xDelta = xMouse - xOffset;
                                        }
                                        else
                                        {
                                            xDelta = xOffset - xMouse;
                                        }

                                        if (yMouse > yOffset)
                                        {
                                            yDelta = yMouse - yOffset;
                                        }
                                        else
                                        {
                                            yDelta = yOffset - yMouse;
                                        }

                                        if (xDelta + yDelta < xyDeltaMin)
                                        {
                                            xyDeltaMin = xDelta + yDelta;
                                            closestMeiosisChromosomeModel = aChromosomeModel;
                                        }
                                    }
                                }
                                else if (xPointsStrandOne != null && yPointsStrandOne != null)
                                {
                                    // Just strand one shown
                                    length = xPointsStrandOne.length;
                                    for (i=0;i<length;i++)
                                    {
                                        int xOffset = xPointsStrandOne[i]+xTemporaryOffset;
                                        int yOffset = yPointsStrandOne[i]+yTemporaryOffset;
                                        // Check strand one
                                        if (xMouse > xOffset)
                                        {
                                            xDelta = xMouse - xOffset;
                                        }
                                        else
                                        {
                                            xDelta = xOffset - xMouse;
                                        }

                                        if (yMouse > yOffset)
                                        {
                                            yDelta = yMouse - yOffset;
                                        }
                                        else
                                        {
                                            yDelta = yOffset - yMouse;
                                        }

                                        if (xDelta + yDelta < xyDeltaMin)
                                        {
                                            xyDeltaMin = xDelta + yDelta;
                                            closestMeiosisChromosomeModel = aChromosomeModel;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Select closest chromosome model
                if (selectedMeiosisChromosomeModel != closestMeiosisChromosomeModel && xyDeltaMin < 20)
                {
                    // Assume user is trying to move a chromosome
                    selectedMeiosisChromosomeModel = closestMeiosisChromosomeModel;

                    if (selectedMeiosisChromosomeModel != null)
                    {
                        long interval = System.currentTimeMillis() - firstClick;
                        if (interval < DOUBLECLICK_INTERVAL)
                        {
                            
                            OrganismChromosome chromosome = selectedMeiosisChromosomeModel.getChromosome();
                            changes.firePropertyChange(UIProp.MEIOSIS_CHROMOSOME_SELECTED,null,chromosome);
                        }
                        firstClick = System.currentTimeMillis();
                        xTemporaryOffset = selectedMeiosisChromosomeModel.getXTemporaryOffset();
                        yTemporaryOffset = selectedMeiosisChromosomeModel.getYTemporaryOffset();

                        xMouseDown = xMouse - xTemporaryOffset;
                        yMouseDown = yMouse - yTemporaryOffset;

                        if (movedChromosomeModels == null)
                        {
                            movedChromosomeModels = new Vector();
                        }
                        movedChromosomeModels.addElement(selectedMeiosisChromosomeModel);
                    }

                    Vector cellBoundaries = meiosisModel.getCellBoundaries();
                    Enumeration eCellBoundaries = cellBoundaries.elements();
                    while (eCellBoundaries.hasMoreElements())
                    {
                        Object boundary = eCellBoundaries.nextElement();
            
                        if (boundary instanceof CellArc)
                        {
                            CellArc cellArc = (CellArc) boundary;
                            if (cellArc.contains(xMouseDown, yMouseDown))
                            {
                                currentCellArc = cellArc;
                                break;
                            }
                            else
                            {
                                currentCellArc = null;
                            }
                        }
                    }

                    repaint();
                }
                else if (xyDeltaMin >= 20)
                {
                    // Assume user is trying to select a gamete
                    if (meiosisModel != null &&
                        meiosisModel.getStep() > 94)
                    {
                    	if (getSmallMeiosisView().getGameteSelectMode() == SexView.MANUAL_GAMETE_SELECT)
                    		meiosisModel.selectGamete(event.getX(),event.getY());
                    
                    }
                }
            }
            else if (activeTool == Tool.SNIP && currentStep == CONTROL_CROSSOVER_STEP)
            {
                        	
                if (meiosisChromosomeModelSecondCut == null)
                {
                    // Find the closest point on the closest chromosome strand.
                    int closestStrandType = 0;
                    int closestStrandPointIndex = 0;
                   

                    // Walk through chromosomes finding closest chromosome
                    chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
                    eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
                    while (eChromosomeModelEnumerations.hasMoreElements())
                    {
                    	
                        eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
                        while (eChromosomeModels.hasMoreElements())
                        {
                            aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();

                            // Only pick on visible chromosomes
                            if (aChromosomeModel.isVisible() == true)
                            {
                                xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                                yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();

                                for (iStrand=0;iStrand<2;iStrand++)
                                {
                                    xPointsStrandOne = null;
                                    xPointsStrandTwo = null;
                                    yPointsStrandOne = null;
                                    yPointsStrandTwo = null;

                                    // Get strand locations
                                    switch (iStrand)
                                    {
                                        case 0:
                                            xPointsStrandOne = aChromosomeModel.getXPStrandOnePoints(currentStep);
                                            yPointsStrandOne = aChromosomeModel.getYPStrandOnePoints(currentStep);
                                            if (replicated)
                                            {
                                                xPointsStrandTwo = aChromosomeModel.getXPStrandTwoPoints(currentStep);
                                                yPointsStrandTwo = aChromosomeModel.getYPStrandTwoPoints(currentStep);
                                            }
                                            break;

                                        case 1:
                                            xPointsStrandOne = aChromosomeModel.getXQStrandOnePoints(currentStep);
                                            yPointsStrandOne = aChromosomeModel.getYQStrandOnePoints(currentStep);
                                            if (replicated)
                                            {
                                                xPointsStrandTwo = aChromosomeModel.getXQStrandTwoPoints(currentStep);
                                                yPointsStrandTwo = aChromosomeModel.getYQStrandTwoPoints(currentStep);
                                            }
                                            break;
                                    }

                                    // Branch on whether we're drawing one or two strands
                                    if (xPointsStrandOne != null && xPointsStrandTwo != null &&
                                        yPointsStrandOne != null && yPointsStrandTwo != null)
                                    {
                                        // Both strands shown
                                        length = xPointsStrandOne.length;
                                      
                                        for (i=0;i<length;i++)
                                        {
                                        	
                                            // Check strand one
                                            if (xMouse > (xPointsStrandOne[i]+xTemporaryOffset))
                                            {
                                                xDelta = xMouse - (xPointsStrandOne[i]+xTemporaryOffset);
                                            }
                                            else
                                            {
                                                xDelta = (xPointsStrandOne[i]+xTemporaryOffset) - xMouse;
                                            }

                                            if (yMouse > (yPointsStrandOne[i]+yTemporaryOffset))
                                            {
                                                yDelta = yMouse - (yPointsStrandOne[i]+yTemporaryOffset);
                                            }
                                            else
                                            {
                                                yDelta = (yPointsStrandOne[i]+yTemporaryOffset) - yMouse;
                                            }

                                            if (xDelta + yDelta < xyDeltaMin)
                                            {
                                            	xyDeltaMin = xDelta + yDelta;
                                                closestMeiosisChromosomeModel = aChromosomeModel;
                                                //System.out.println("first two strand");
                                                if (iStrand == 0)
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.P_STRAND_ONE;
                                                }
                                                else
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.Q_STRAND_ONE;
                                                }
                                                closestStrandPointIndex = i;
                                            }
                                        }
                                        
                                        for (i=0;i<length;i++)
                                        {
                                            // Check strand two
                                            if (xMouse > (xPointsStrandTwo[i]+xTemporaryOffset))
                                            {
                                                xDelta = xMouse - (xPointsStrandTwo[i]+xTemporaryOffset);
                                            }
                                            else
                                            {
                                                xDelta = (xPointsStrandTwo[i]+xTemporaryOffset) - xMouse;
                                            }

                                            if (yMouse > (yPointsStrandTwo[i]+yTemporaryOffset))
                                            {
                                                yDelta = yMouse - (yPointsStrandTwo[i]+yTemporaryOffset);
                                            }
                                            else
                                            {
                                                yDelta = (yPointsStrandTwo[i]+yTemporaryOffset) - yMouse;
                                            }

                                            if (xDelta + yDelta < xyDeltaMin)
                                            {
                                                xyDeltaMin = xDelta + yDelta;
                                                closestMeiosisChromosomeModel = aChromosomeModel;
                                                //System.out.println("second two strand");
                                                if (iStrand == 0)
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.P_STRAND_TWO;
                                                }
                                                else
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.Q_STRAND_TWO;
                                                }
                                                closestStrandPointIndex = i;
                                            }
                                        }
                                    }
                                    else if (xPointsStrandOne != null && yPointsStrandOne != null)
                                    {
                                        // Just strand one shown
                                        length = xPointsStrandOne.length;
                                        //System.out.print("xPointsStrandOne " +length);
                                        for (i=0;i<length;i++)
                                        {
                                            // Check strand one
                                            if (xMouse > (xPointsStrandOne[i]+xTemporaryOffset))
                                            {
                                                xDelta = xMouse - (xPointsStrandOne[i]+xTemporaryOffset);
                                            }
                                            else
                                            {
                                                xDelta = (xPointsStrandOne[i]+xTemporaryOffset) - xMouse;
                                            }

                                            if (yMouse > (yPointsStrandOne[i]+yTemporaryOffset))
                                            {
                                                yDelta = yMouse - (yPointsStrandOne[i]+yTemporaryOffset);
                                            }
                                            else
                                            {
                                                yDelta = (yPointsStrandOne[i]+yTemporaryOffset) - yMouse;
                                            }

                                            if (xDelta + yDelta < xyDeltaMin)
                                            {
                                                xyDeltaMin = xDelta + yDelta;
                                                closestMeiosisChromosomeModel = aChromosomeModel;
                                                //System.out.println("one strand");
                                                if (iStrand == 0)
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.P_STRAND_ONE;
                                                }
                                                else
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.Q_STRAND_ONE;
                                                }
                                                closestStrandPointIndex = i;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                   
                    if (closestMeiosisChromosomeModel != null)
                    {
                    	
                        // Branch on whether this is the first or second snip click
                        if (meiosisChromosomeModelFirstCut == null)
                        {
                            meiosisChromosomeModelFirstCut = closestMeiosisChromosomeModel;
                            strandTypeFirstCut = closestStrandType;
                            strandPointIndexCut = closestStrandPointIndex;
                            
                            // Start blinking, which will force a repaint
                            startBlinking();
                        }
                        if (meiosisChromosomeModelSecondCut == null)
                        {
                        
                        	
                            if ((meiosisChromosomeModelFirstCut.getVisibleChromosomePairNumber() !=
                                 closestMeiosisChromosomeModel.getVisibleChromosomePairNumber()) ||
                                (meiosisChromosomeModelFirstCut.getNumberType() !=
                                 closestMeiosisChromosomeModel.getNumberType()) ||
                                (meiosisChromosomeModelFirstCut == closestMeiosisChromosomeModel &&
                                 (((strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                                    strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_ONE) &&
                                   (closestStrandType == MeiosisChromosomeModel.P_STRAND_ONE ||
                                    closestStrandType == MeiosisChromosomeModel.Q_STRAND_ONE)) ||
                                  ((strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_TWO ||
                                    strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_TWO) &&
                                   (closestStrandType == MeiosisChromosomeModel.P_STRAND_TWO ||
                                    closestStrandType == MeiosisChromosomeModel.Q_STRAND_TWO)))))
                            {
                                // Incompatible second cut (clicked on same strand of same chromosome
                                // or clicked on another chromosome entirely).  So reset first cut.
                                meiosisChromosomeModelFirstCut = closestMeiosisChromosomeModel;
                                strandTypeFirstCut = closestStrandType;
                                strandPointIndexCut = closestStrandPointIndex;
						
                                // Force repaint to get the cut arrows drawn
                                repaint();
                            }
                            else
                            {
                            
                                // Compatible second cut.
                                meiosisChromosomeModelSecondCut = closestMeiosisChromosomeModel;
                                strandTypeSecondCut = closestStrandType;

                                // Make sure the strand types and chromosome models are compatible
                                if (strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                                    strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_TWO)
                                {
                                    if (strandTypeSecondCut == MeiosisChromosomeModel.Q_STRAND_ONE)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.P_STRAND_ONE;
                                    }
                                    else if (strandTypeSecondCut == MeiosisChromosomeModel.Q_STRAND_TWO)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.P_STRAND_TWO;
                                    }
                                }
                                else if (strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_ONE ||
                                         strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_TWO)
                                {
                                    if (strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_ONE)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.Q_STRAND_ONE;
                                    }
                                    else if (strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_TWO)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.Q_STRAND_TWO;
                                    }
                                }

                                // At this point we want to start the crossover animation
                                stopBlinking();
                                //System.out.println("playCrossoverAnimation");
                                playCrossoverAnimation();
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    /**
    * Handle autoCrossing Over
    */
    
    
    public void autoCrossOver(int x1, int y1)
    {
    	if (meiosisModel != null)
        {
    		// Clear state if we think we still have chromosome selected for some reason
            if (selectedMeiosisChromosomeModel != null)
            {
                selectedMeiosisChromosomeModel = null;
                xMouseDown = 0;
                yMouseDown = 0;
            }
            // Mouse coordinates
            int xMouse = x1;
            int yMouse = y1;

    		int currentStep = meiosisModel.getStep();
            boolean replicated = meiosisModel.isReplicated();
            MeiosisChromosomeModel closestMeiosisChromosomeModel = null;
            
           
    	    int i;
            int iStrand;
            int length;
            int[] xPointsStrandOne, xPointsStrandTwo;
            int[] yPointsStrandOne, yPointsStrandTwo;
            int xTemporaryOffset = 0;
            int yTemporaryOffset = 0;
            
            int xDelta, yDelta;
            int xyDeltaMin = 10000;

            MeiosisChromosomeModel aChromosomeModel;
            Enumeration eChromosomeModels;
            Vector chromosomeModelEnumerations;
            Enumeration eChromosomeModelEnumerations;
            
    	          	
                      	
                if (meiosisChromosomeModelSecondCut == null)
                {
                    // Find the closest point on the closest chromosome strand.
                    int closestStrandType = 0;
                    int closestStrandPointIndex = 0;
                   

                    // Walk through chromosomes finding closest chromosome
                    chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
                    eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
                    while (eChromosomeModelEnumerations.hasMoreElements())
                    {
                    	
                        eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
                        while (eChromosomeModels.hasMoreElements())
                        {
                            aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();

                            // Only pick on visible chromosomes
                            if (aChromosomeModel.isVisible() == true)
                            {
                                xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                                yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();

                                for (iStrand=0;iStrand<2;iStrand++)
                                {
                                    xPointsStrandOne = null;
                                    xPointsStrandTwo = null;
                                    yPointsStrandOne = null;
                                    yPointsStrandTwo = null;

                                    // Get strand locations
                                    switch (iStrand)
                                    {
                                        case 0:
                                            xPointsStrandOne = aChromosomeModel.getXPStrandOnePoints(currentStep);
                                            yPointsStrandOne = aChromosomeModel.getYPStrandOnePoints(currentStep);
                                            if (replicated)
                                            {
                                                xPointsStrandTwo = aChromosomeModel.getXPStrandTwoPoints(currentStep);
                                                yPointsStrandTwo = aChromosomeModel.getYPStrandTwoPoints(currentStep);
                                            }
                                            break;

                                        case 1:
                                            xPointsStrandOne = aChromosomeModel.getXQStrandOnePoints(currentStep);
                                            yPointsStrandOne = aChromosomeModel.getYQStrandOnePoints(currentStep);
                                            if (replicated)
                                            {
                                                xPointsStrandTwo = aChromosomeModel.getXQStrandTwoPoints(currentStep);
                                                yPointsStrandTwo = aChromosomeModel.getYQStrandTwoPoints(currentStep);
                                            }
                                            break;
                                    }

                                    // Branch on whether we're drawing one or two strands
                                    if (xPointsStrandOne != null && xPointsStrandTwo != null &&
                                        yPointsStrandOne != null && yPointsStrandTwo != null)
                                    {
                                        // Both strands shown
                                        length = xPointsStrandOne.length;
                                      
                                        for (i=0;i<length;i++)
                                        {
                                        	
                                            // Check strand one
                                            if (xMouse > (xPointsStrandOne[i]+xTemporaryOffset))
                                            {
                                                xDelta = xMouse - (xPointsStrandOne[i]+xTemporaryOffset);
                                            }
                                            else
                                            {
                                                xDelta = (xPointsStrandOne[i]+xTemporaryOffset) - xMouse;
                                            }

                                            if (yMouse > (yPointsStrandOne[i]+yTemporaryOffset))
                                            {
                                                yDelta = yMouse - (yPointsStrandOne[i]+yTemporaryOffset);
                                            }
                                            else
                                            {
                                                yDelta = (yPointsStrandOne[i]+yTemporaryOffset) - yMouse;
                                            }

                                            if (xDelta + yDelta < xyDeltaMin)
                                            {
                                            	xyDeltaMin = xDelta + yDelta;
                                                closestMeiosisChromosomeModel = aChromosomeModel;
                                                //System.out.println("first two strand");
                                                if (iStrand == 0)
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.P_STRAND_ONE;
                                                }
                                                else
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.Q_STRAND_ONE;
                                                }
                                                closestStrandPointIndex = i;
                                            }
                                        }
                                        
                                        for (i=0;i<length;i++)
                                        {
                                            // Check strand two
                                            if (xMouse > (xPointsStrandTwo[i]+xTemporaryOffset))
                                            {
                                                xDelta = xMouse - (xPointsStrandTwo[i]+xTemporaryOffset);
                                            }
                                            else
                                            {
                                                xDelta = (xPointsStrandTwo[i]+xTemporaryOffset) - xMouse;
                                            }

                                            if (yMouse > (yPointsStrandTwo[i]+yTemporaryOffset))
                                            {
                                                yDelta = yMouse - (yPointsStrandTwo[i]+yTemporaryOffset);
                                            }
                                            else
                                            {
                                                yDelta = (yPointsStrandTwo[i]+yTemporaryOffset) - yMouse;
                                            }

                                            if (xDelta + yDelta < xyDeltaMin)
                                            {
                                                xyDeltaMin = xDelta + yDelta;
                                                closestMeiosisChromosomeModel = aChromosomeModel;
                                                //System.out.println("second two strand");
                                                if (iStrand == 0)
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.P_STRAND_TWO;
                                                }
                                                else
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.Q_STRAND_TWO;
                                                }
                                                closestStrandPointIndex = i;
                                            }
                                        }
                                    }
                                    else if (xPointsStrandOne != null && yPointsStrandOne != null)
                                    {
                                        // Just strand one shown
                                        length = xPointsStrandOne.length;
                                       // System.out.print("xPointsStrandOne " +length);
                                        for (i=0;i<length;i++)
                                        {
                                            // Check strand one
                                            if (xMouse > (xPointsStrandOne[i]+xTemporaryOffset))
                                            {
                                                xDelta = xMouse - (xPointsStrandOne[i]+xTemporaryOffset);
                                            }
                                            else
                                            {
                                                xDelta = (xPointsStrandOne[i]+xTemporaryOffset) - xMouse;
                                            }

                                            if (yMouse > (yPointsStrandOne[i]+yTemporaryOffset))
                                            {
                                                yDelta = yMouse - (yPointsStrandOne[i]+yTemporaryOffset);
                                            }
                                            else
                                            {
                                                yDelta = (yPointsStrandOne[i]+yTemporaryOffset) - yMouse;
                                            }

                                            if (xDelta + yDelta < xyDeltaMin)
                                            {
                                                xyDeltaMin = xDelta + yDelta;
                                                closestMeiosisChromosomeModel = aChromosomeModel;
                                                //System.out.println("one strand");
                                                if (iStrand == 0)
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.P_STRAND_ONE;
                                                }
                                                else
                                                {
                                                    closestStrandType = MeiosisChromosomeModel.Q_STRAND_ONE;
                                                }
                                                closestStrandPointIndex = i;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    
                   
                    if (closestMeiosisChromosomeModel != null)
                    {
                    	
                        // Branch on whether this is the first or second snip click
                        if (meiosisChromosomeModelFirstCut == null)
                        {
                            meiosisChromosomeModelFirstCut = closestMeiosisChromosomeModel;
                            strandTypeFirstCut = closestStrandType;
                            strandPointIndexCut = closestStrandPointIndex;
                            
                            // Start blinking, which will force a repaint
                           // startBlinking();
                        }
                        if (meiosisChromosomeModelSecondCut == null)
                        {
                        
                        	
                            if ((meiosisChromosomeModelFirstCut.getVisibleChromosomePairNumber() !=
                                 closestMeiosisChromosomeModel.getVisibleChromosomePairNumber()) ||
                                (meiosisChromosomeModelFirstCut.getNumberType() !=
                                 closestMeiosisChromosomeModel.getNumberType()) ||
                                (meiosisChromosomeModelFirstCut == closestMeiosisChromosomeModel &&
                                 (((strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                                    strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_ONE) &&
                                   (closestStrandType == MeiosisChromosomeModel.P_STRAND_ONE ||
                                    closestStrandType == MeiosisChromosomeModel.Q_STRAND_ONE)) ||
                                  ((strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_TWO ||
                                    strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_TWO) &&
                                   (closestStrandType == MeiosisChromosomeModel.P_STRAND_TWO ||
                                    closestStrandType == MeiosisChromosomeModel.Q_STRAND_TWO)))))
                            {
                                // Incompatible second cut (clicked on same strand of same chromosome
                                // or clicked on another chromosome entirely).  So reset first cut.
                                meiosisChromosomeModelFirstCut = closestMeiosisChromosomeModel;
                                strandTypeFirstCut = closestStrandType;
                                strandPointIndexCut = closestStrandPointIndex;
						
                                // Force repaint to get the cut arrows drawn
                                repaint();
                            }
                            else
                            {
                            
                                // Compatible second cut.
                                meiosisChromosomeModelSecondCut = closestMeiosisChromosomeModel;
                                strandTypeSecondCut = closestStrandType;

                                // Make sure the strand types and chromosome models are compatible
                                if (strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                                    strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_TWO)
                                {
                                    if (strandTypeSecondCut == MeiosisChromosomeModel.Q_STRAND_ONE)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.P_STRAND_ONE;
                                    }
                                    else if (strandTypeSecondCut == MeiosisChromosomeModel.Q_STRAND_TWO)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.P_STRAND_TWO;
                                    }
                                }
                                else if (strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_ONE ||
                                         strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_TWO)
                                {
                                    if (strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_ONE)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.Q_STRAND_ONE;
                                    }
                                    else if (strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_TWO)
                                    {
                                        strandTypeSecondCut = MeiosisChromosomeModel.Q_STRAND_TWO;
                                    }
                                }

                                // At this point we want to start the crossover animation
                                stopBlinking();
                               // System.out.println("playCrossoverAnimation");
                                playCrossoverAnimation();
                            }
                        }
              		 }
               }
               
            }
        
    }//end of autoCrossing
   

    /**
     * Handle mouse released event
    **/
    public void mouseReleased(MouseEvent event)
    {
        if (meiosisModel != null &&
            selectedMeiosisChromosomeModel != null &&
            activeTool == Tool.SELECTION)
        {
            int xMouse = event.getX();
            int yMouse = event.getY();

            if ((currentCellArc == null) || currentCellArc.contains(xMouse, yMouse))
            {
                selectedMeiosisChromosomeModel.setTemporaryOffsets(xMouse-xMouseDown, yMouse-yMouseDown);
            }

            // End drag
            selectedMeiosisChromosomeModel = null;
            xMouseDown = 0;
            yMouseDown = 0;

            // DON'T destroy movedChromosomeModels vector, as we need that later

            // Force repaint
            repaint();
        }
    }

    /**
     * Handle mouse dragged event
    **/
    public void mouseDragged(MouseEvent event)
    {
        if (meiosisModel != null &&
            selectedMeiosisChromosomeModel != null &&
            activeTool == Tool.SELECTION)
        {
            int xMouse = event.getX();
            int yMouse = event.getY();
            
            if ((currentCellArc == null) || currentCellArc.contains(xMouse, yMouse))
            {
                selectedMeiosisChromosomeModel.setTemporaryOffsets(xMouse-xMouseDown, yMouse-yMouseDown);
                // Force repaint
                repaint();
            }
        }
    }

    /**
     * Handle mouse moved event
    **/
    public void mouseMoved(MouseEvent event)
    {
    }
    
    private void initBoundingRectangle(int x, int y)
    {
        boundingRectangle.x = x;
        boundingRectangle.y = y;
        boundingRectangle.width = 0;
        boundingRectangle.x = 0;
    }
    
    private void addNextPoint(int x, int y)
    {
        
    }

    /**
     * Play the crossover animation
    **/
    public void playCrossoverAnimation()
    {
    	
        if (currentPlayMode != PLAY_MODE_CROSSING_OVER)
        {
            currentPlayMode = PLAY_MODE_CROSSING_OVER;
            

            if (animationTimer.isRunning() == false)
            {
                animationTimer.start();
            }
        }
    }

    /**
     * Play animation backward fast
    **/
    public void playAnimationBackwardFast()
    {
        clearCrossingOverState();

        if (currentPlayMode != PLAY_MODE_BACKWARD_FAST)
        {
            currentPlayMode = PLAY_MODE_BACKWARD_FAST;
    
            if (animationTimer.isRunning() == false)
            {
                animationTimer.start();
            }

            // Set toggle button state
            playBackwardFastToggleButton.setSelected(true);
            playBackwardToggleButton.setSelected(false);
            stopToggleButton.setSelected(false);
            playForwardToggleButton.setSelected(false);
            playForwardFastToggleButton.setSelected(false);

            if (switchAlignmentButtonsVisible)
            {
                hideSwitchAlignmentButtons();
            }

            repaint();
        }
    }

    /**
     * Play animation backward
    **/
    public void playAnimationBackward()
    {
        clearCrossingOverState();

        if (currentPlayMode != PLAY_MODE_BACKWARD)
        {
            currentPlayMode = PLAY_MODE_BACKWARD;
            
            if (animationTimer.isRunning() == false)
            {
                animationTimer.start();
            }
    
            // Set toggle button state
            playBackwardFastToggleButton.setSelected(false);
            playBackwardToggleButton.setSelected(true);
            stopToggleButton.setSelected(false);
            playForwardToggleButton.setSelected(false);
            playForwardFastToggleButton.setSelected(false);

            if (switchAlignmentButtonsVisible)
            {
                hideSwitchAlignmentButtons();
            }

            repaint();
        }
    }

    /**
     * Step animation backward one step
    **/
    public void stepAnimationBackward()
    {
        clearCrossingOverState();
        stopAnimation();

        // Can't change step if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        int currentStep = meiosisModel.getStep();

        if (currentStep > 0)
        {
            currentStep -= 1;
            meiosisModel.setStep(currentStep);
            animationSlider.setValue(currentStep);

            if (controlAlignment == true &&
                (currentStep == CONTROL_HORIZONTAL_ALIGNMENT_STEP ||
                 currentStep == CONTROL_VERTICAL_ALIGNMENT_STEP) &&
                switchAlignmentButtonsVisible == false)
            {
                showSwitchAlignmentButtons();
            }
            else if (switchAlignmentButtonsVisible == true &&
                     (currentStep != CONTROL_HORIZONTAL_ALIGNMENT_STEP &&
                      currentStep != CONTROL_VERTICAL_ALIGNMENT_STEP))
            {
                hideSwitchAlignmentButtons();
            }

            repaint();
        }
    }

    /**
     * Stop playing animation
    **/
    public void stopAnimation()
    {
        clearCrossingOverState();

        if (currentPlayMode != PLAY_MODE_STOPPED)
        {
            if (animationTimer.isRunning() == true)
            {
                animationTimer.stop();
            }

            currentPlayMode = PLAY_MODE_STOPPED;
    
            // Set toggle button state
            playBackwardFastToggleButton.setSelected(false);
            playBackwardToggleButton.setSelected(false);
            stopToggleButton.setSelected(true);
            playForwardToggleButton.setSelected(false);
            playForwardFastToggleButton.setSelected(false);

            repaint();
        }
    }

    /**
     * Step animation forward one step
    **/
    public void stepAnimationForward()
    {
        clearCrossingOverState();
        stopAnimation();

        // Can't change step if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        int currentStep = meiosisModel.getStep();

        if (currentStep < 100)
        {
            currentStep += 1;
            meiosisModel.setStep(currentStep);
            animationSlider.setValue(currentStep);

            if (controlAlignment == true &&
                (currentStep == CONTROL_HORIZONTAL_ALIGNMENT_STEP ||
                 currentStep == CONTROL_VERTICAL_ALIGNMENT_STEP) &&
                switchAlignmentButtonsVisible == false)
            {
                showSwitchAlignmentButtons();
            }
            else if (switchAlignmentButtonsVisible == true &&
                     (currentStep != CONTROL_HORIZONTAL_ALIGNMENT_STEP &&
                      currentStep != CONTROL_VERTICAL_ALIGNMENT_STEP))
            {
                hideSwitchAlignmentButtons();
            }

            repaint();
        }
    }

    /**
     * Play animation forward
    **/
    public void playAnimationForward()
    {
        clearCrossingOverState();

        // Can't change step if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        if (currentPlayMode != PLAY_MODE_FORWARD)
        {
            currentPlayMode = PLAY_MODE_FORWARD;

            if (animationTimer.isRunning() == false)
            {
                animationTimer.start();
            }
    
            // Set toggle button state
            playBackwardFastToggleButton.setSelected(false);
            playBackwardToggleButton.setSelected(false);
            stopToggleButton.setSelected(false);
            playForwardToggleButton.setSelected(true);
            playForwardFastToggleButton.setSelected(false);

            repaint();
        }
    }

    /**
     * Play animation forward fast
    **/
    public void playAnimationForwardFast()
    {
        clearCrossingOverState();

        // Can't change step if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        if (currentPlayMode != PLAY_MODE_FORWARD_FAST)
        {
            currentPlayMode = PLAY_MODE_FORWARD_FAST;

            if (animationTimer.isRunning() == false)
            {
                animationTimer.start();
            }
    
            // Set toggle button state
            playBackwardFastToggleButton.setSelected(false);
            playBackwardToggleButton.setSelected(false);
            stopToggleButton.setSelected(false);
            playForwardToggleButton.setSelected(false);
            playForwardFastToggleButton.setSelected(true);

            repaint();
        }
    }

    /**
     * Go to a particular step in the animation and stop there.
     *
     * @param		aStep int - a step (0 to 100)
    **/
    public void goToAnimationStep(int aStep)
    {
        clearCrossingOverState();
        stopAnimation();

        // Can't change step if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        int step = aStep;
        if (step < 0)
        {
            step = 0;
        }
        else if (step > 100)
        {
            step = 100;
        }

        meiosisModel.setStep(step);
        animationSlider.setValue(step);

        if (controlAlignment == true &&
            (step == CONTROL_HORIZONTAL_ALIGNMENT_STEP ||
             step == CONTROL_VERTICAL_ALIGNMENT_STEP) &&
            switchAlignmentButtonsVisible == false)
        {
            showSwitchAlignmentButtons();
        }
        else if (switchAlignmentButtonsVisible == true &&
                 (step != CONTROL_HORIZONTAL_ALIGNMENT_STEP &&
                  step != CONTROL_VERTICAL_ALIGNMENT_STEP))
        {
            hideSwitchAlignmentButtons();
        }
        
       if (animationCrossover && step >= CONTROL_CROSSOVER_STEP-1)
       {
       		if (currentPlayMode != PLAY_MODE_CROSSING_OVER)
       		{
       			if (!meiosisModel.isAutoCrossOverHappened())
       				autoCrossing();
       		}
       	
       }

        repaint();
    }

    /*
     * Handle an animation time click
    **/
    private void onAnimationTimerClick()
    {
        // Can't change step if meiosisModel null
        if (meiosisModel == null)
        {
            stopAnimation();
            return;
        }
        int checkStep = meiosisModel.getStep();
        
        if (controlCrossover && checkStep == CONTROL_CROSSOVER_STEP-1)
       {
       		toolChanged(Tool.SNIP);
       		if (currentPlayMode != PLAY_MODE_CROSSING_OVER)
       		{
       			//toolChanged(Tool.SNIP);
       			meiosisModel.setControlledCrossOverHappened(true);
       			animationTimer.stop();
       		}
       		else if (animationTimer.isRunning() == false)
       		{
       			animationTimer.start();
      		}
       }
       else if(controlCrossover && checkStep> 95)
       {
       		toolChanged(Tool.SELECTION);
       }
      
       if (animationCrossover && checkStep >= CONTROL_CROSSOVER_STEP-1)
       {
       		if (currentPlayMode != PLAY_MODE_CROSSING_OVER)
       		{
       			if (!meiosisModel.isAutoCrossOverHappened())
       				autoCrossing();
       		}
       		else if (animationTimer.isRunning() == false)
       		{
       		
       			animationTimer.start();
      		}
       }
       
        if (currentPlayMode == PLAY_MODE_CROSSING_OVER &&
            meiosisChromosomeModelFirstCut != null &&
            meiosisChromosomeModelSecondCut != null)
        {
        
        	  
        
            // Doing crossing over animation
            if (crossingOverStep == NUMBER_OF_CROSSING_OVER_STEPS)
            {
                // End animation
               // animationTimer.stop();
                
                
             
                currentPlayMode = PLAY_MODE_STOPPED;

                playBackwardFastToggleButton.setSelected(false);
                playBackwardToggleButton.setSelected(false);
                stopToggleButton.setSelected(true);
                playForwardToggleButton.setSelected(false);
                playForwardFastToggleButton.setSelected(false);

                // Clear animation state
                clearCrossingOverState();
                
              
                //toolChanged(Tool.SELECTION);
                
            }
            else
            {
                if (crossingOverStep == 0)
                {
                    // Starting crossing over animation, set original offsets
                    if (meiosisChromosomeModelFirstCut != null)
                    {
                        xCrossingOverOriginalFirstCutTemporaryOffset = meiosisChromosomeModelFirstCut.getXTemporaryOffset();
                        yCrossingOverOriginalFirstCutTemporaryOffset = meiosisChromosomeModelFirstCut.getYTemporaryOffset();
                    }
                    if (meiosisChromosomeModelSecondCut != null)
                    {
                        xCrossingOverOriginalSecondCutTemporaryOffset = meiosisChromosomeModelSecondCut.getXTemporaryOffset();
                        yCrossingOverOriginalSecondCutTemporaryOffset = meiosisChromosomeModelSecondCut.getYTemporaryOffset();
                    }
                }

                // Somewhere in the middle of the crossing over animation
                // Calculate graphics for crossing over animation to be displayed next repaint
                int xCut1, yCut1, xCut2, yCut2;

                int[] xPointsStrandOne, xPointsStrandTwo;
                int[] yPointsStrandOne, yPointsStrandTwo;

                int currentStep = meiosisModel.getStep();

                if (meiosisChromosomeModelFirstCut != null &&
                    meiosisChromosomeModelSecondCut != null)
                {
                    // Determine cut 1 and maybe cut 2
                    if (strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                        strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_TWO)
                    {
                        // Cut on P strand
                        xPointsStrandOne = meiosisChromosomeModelFirstCut.getXPStrandOnePoints(currentStep);
                        yPointsStrandOne = meiosisChromosomeModelFirstCut.getYPStrandOnePoints(currentStep);
                        xPointsStrandTwo = meiosisChromosomeModelFirstCut.getXPStrandTwoPoints(currentStep);
                        yPointsStrandTwo = meiosisChromosomeModelFirstCut.getYPStrandTwoPoints(currentStep);
                    }
                    else
                    {
                        // Cut on Q strand
                        xPointsStrandOne = meiosisChromosomeModelFirstCut.getXQStrandOnePoints(currentStep);
                        yPointsStrandOne = meiosisChromosomeModelFirstCut.getYQStrandOnePoints(currentStep);
                        xPointsStrandTwo = meiosisChromosomeModelFirstCut.getXQStrandTwoPoints(currentStep);
                        yPointsStrandTwo = meiosisChromosomeModelFirstCut.getYQStrandTwoPoints(currentStep);
                    }

                    if (strandTypeFirstCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                        strandTypeFirstCut == MeiosisChromosomeModel.Q_STRAND_ONE)
                    {
                        // Cut 1 on strand one.  Cut 2 MAY be on strand two.
                        xCut1 = xPointsStrandOne[strandPointIndexCut] + xCrossingOverOriginalFirstCutTemporaryOffset;
                        yCut1 = yPointsStrandOne[strandPointIndexCut] + yCrossingOverOriginalFirstCutTemporaryOffset;
                        xCut2 = xPointsStrandTwo[strandPointIndexCut] + xCrossingOverOriginalFirstCutTemporaryOffset;
                        yCut2 = yPointsStrandTwo[strandPointIndexCut] + yCrossingOverOriginalFirstCutTemporaryOffset;
                    }
                    else
                    {
                        // Cut 1 on strand two.  Cut 2 MAY be on strand one.
                        xCut1 = xPointsStrandTwo[strandPointIndexCut] + xCrossingOverOriginalFirstCutTemporaryOffset;
                        yCut1 = yPointsStrandTwo[strandPointIndexCut] + yCrossingOverOriginalFirstCutTemporaryOffset;
                        xCut2 = xPointsStrandOne[strandPointIndexCut] + xCrossingOverOriginalFirstCutTemporaryOffset;
                        yCut2 = yPointsStrandOne[strandPointIndexCut] + yCrossingOverOriginalFirstCutTemporaryOffset;
                    }

                    // If both cuts aren't on the same chromosome model, then we still
                    // need to find the position of cut 2.
                    if (meiosisChromosomeModelFirstCut != meiosisChromosomeModelSecondCut)
                    {
                        if (strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                            strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_TWO)
                        {
                            // Cut 2 on P strand
                            if (strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                                strandTypeSecondCut == MeiosisChromosomeModel.Q_STRAND_ONE)
                            {
                                // Cut 2 on P strand one.
                                xPointsStrandOne = meiosisChromosomeModelSecondCut.getXPStrandOnePoints(currentStep);
                                yPointsStrandOne = meiosisChromosomeModelSecondCut.getYPStrandOnePoints(currentStep);
                                xCut2 = xPointsStrandOne[strandPointIndexCut] + xCrossingOverOriginalSecondCutTemporaryOffset;
                                yCut2 = yPointsStrandOne[strandPointIndexCut] + yCrossingOverOriginalSecondCutTemporaryOffset;
                            }
                            else
                            {
                                // Cut 2 on P strand two.
                                xPointsStrandTwo = meiosisChromosomeModelSecondCut.getXPStrandTwoPoints(currentStep);
                                yPointsStrandTwo = meiosisChromosomeModelSecondCut.getYPStrandTwoPoints(currentStep);
                                xCut2 = xPointsStrandTwo[strandPointIndexCut] + xCrossingOverOriginalSecondCutTemporaryOffset;
                                yCut2 = yPointsStrandTwo[strandPointIndexCut] + yCrossingOverOriginalSecondCutTemporaryOffset;
                            }
                        }
                        else
                        {
                            // Cut 2 on Q strand
                            if (strandTypeSecondCut == MeiosisChromosomeModel.P_STRAND_ONE ||
                                strandTypeSecondCut == MeiosisChromosomeModel.Q_STRAND_ONE)
                            {
                                // Cut 2 on Q strand one.
                                xPointsStrandOne = meiosisChromosomeModelSecondCut.getXQStrandOnePoints(currentStep);
                                yPointsStrandOne = meiosisChromosomeModelSecondCut.getYQStrandOnePoints(currentStep);
                                xCut2 = xPointsStrandOne[strandPointIndexCut] + xCrossingOverOriginalSecondCutTemporaryOffset;
                                yCut2 = yPointsStrandOne[strandPointIndexCut] + yCrossingOverOriginalSecondCutTemporaryOffset;
                            }
                            else
                            {
                                // Cut 2 on Q strand two.
                                xPointsStrandTwo = meiosisChromosomeModelSecondCut.getXQStrandTwoPoints(currentStep);
                                yPointsStrandTwo = meiosisChromosomeModelSecondCut.getYQStrandTwoPoints(currentStep);
                                xCut2 = xPointsStrandTwo[strandPointIndexCut] + xCrossingOverOriginalSecondCutTemporaryOffset;
                                yCut2 = yPointsStrandTwo[strandPointIndexCut] + yCrossingOverOriginalSecondCutTemporaryOffset;
                            }
                        }
                    }

                    if (crossingOverStep < CROSSING_OVER_STEP)
                    {
                        meiosisChromosomeModelFirstCut.setTemporaryOffsets(
                            (xCrossingOverOriginalFirstCutTemporaryOffset+((xCut2-xCut1)*crossingOverStep)/(2*CROSSING_OVER_STEP)),
                            (yCrossingOverOriginalFirstCutTemporaryOffset+((yCut2-yCut1)*crossingOverStep)/(2*CROSSING_OVER_STEP)));

                        meiosisChromosomeModelSecondCut.setTemporaryOffsets(
                            (xCrossingOverOriginalSecondCutTemporaryOffset+((xCut1-xCut2)*crossingOverStep)/(2*CROSSING_OVER_STEP)),
                            (yCrossingOverOriginalSecondCutTemporaryOffset+((yCut1-yCut2)*crossingOverStep)/(2*CROSSING_OVER_STEP)));
                    }
                    else if (crossingOverStep == CROSSING_OVER_STEP)
                    {
                        meiosisChromosomeModelFirstCut.setTemporaryOffsets(
                            (xCrossingOverOriginalFirstCutTemporaryOffset+((xCut2-xCut1)*crossingOverStep)/(2*CROSSING_OVER_STEP)),
                            (yCrossingOverOriginalFirstCutTemporaryOffset+((yCut2-yCut1)*crossingOverStep)/(2*CROSSING_OVER_STEP)));

                        meiosisChromosomeModelSecondCut.setTemporaryOffsets(
                            (xCrossingOverOriginalSecondCutTemporaryOffset+((xCut1-xCut2)*crossingOverStep)/(2*CROSSING_OVER_STEP)),
                            (yCrossingOverOriginalSecondCutTemporaryOffset+((yCut1-yCut2)*crossingOverStep)/(2*CROSSING_OVER_STEP)));

                        doCrossingOver();
                      
                       
                    }
                    else if (crossingOverStep < NUMBER_OF_CROSSING_OVER_STEPS)
                    {
                        int step = NUMBER_OF_CROSSING_OVER_STEPS-crossingOverStep;
                        meiosisChromosomeModelFirstCut.setTemporaryOffsets(
                            (xCrossingOverOriginalFirstCutTemporaryOffset+((xCut2-xCut1)*step)/(2*CROSSING_OVER_STEP)),
                            (yCrossingOverOriginalFirstCutTemporaryOffset+((yCut2-yCut1)*step)/(2*CROSSING_OVER_STEP)));

                        meiosisChromosomeModelSecondCut.setTemporaryOffsets(
                            (xCrossingOverOriginalSecondCutTemporaryOffset+((xCut1-xCut2)*step)/(2*CROSSING_OVER_STEP)),
                            (yCrossingOverOriginalSecondCutTemporaryOffset+((yCut1-yCut2)*step)/(2*CROSSING_OVER_STEP)));
                    }
                }
                
                crossingOverStep++;
                //System.out.println("crossingOverStep: "+crossingOverStep);
            }

            repaint();
            
        }
        else
        {
            // Doing meiosis animation
            boolean steppingForwardOneStep = false;
            boolean steppingBackwardOneStep = false;
            int step = meiosisModel.getStep();
            int origStep = step;

            switch(currentPlayMode)
            {
                case PLAY_MODE_BACKWARD_FAST:
                    step -= 2;
                    break;

                case PLAY_MODE_BACKWARD:
                    step--;
                    steppingBackwardOneStep = true;
                    break;

                case PLAY_MODE_STOPPED:
                    //System.err.println("animation timer click received when stopped");
                    break;

                case PLAY_MODE_FORWARD:
                    step++;
                    steppingForwardOneStep = true;
                    break;

                case PLAY_MODE_FORWARD_FAST:
                    step += 2;
                    break;
            }

            // If step changed, tell meiosis model and repaint
            if (origStep != step)
            {
                // Stop animation if control alignment on
               if ((step == CONTROL_HORIZONTAL_ALIGNMENT_STEP ||
                     (steppingForwardOneStep == false && step == CONTROL_HORIZONTAL_ALIGNMENT_STEP+1) ||
                     (steppingBackwardOneStep == false && step == CONTROL_HORIZONTAL_ALIGNMENT_STEP-1)) &&
                    controlAlignment == true)
                {
                    step = CONTROL_HORIZONTAL_ALIGNMENT_STEP;
                    stopAnimation();
                    meiosisModel.setStep(step);
                    animationSlider.setValue(step);
                    
                    //System.out.println("CONTROL_HORIZONTAL_ALIGNMENT_STEP");
                    showSwitchAlignmentButtons();
                }
                else if ((step == CONTROL_VERTICAL_ALIGNMENT_STEP ||
                          (steppingForwardOneStep == false && step == CONTROL_VERTICAL_ALIGNMENT_STEP+1) ||
                          (steppingBackwardOneStep == false && step == CONTROL_VERTICAL_ALIGNMENT_STEP-1)) &&
                    controlAlignment == true)
                {
                    step = CONTROL_VERTICAL_ALIGNMENT_STEP;
                    stopAnimation();
                    meiosisModel.setStep(step);
                    animationSlider.setValue(step);
                     // System.out.println("CONTROL_VERTICAL_ALIGNMENT_STEP");
                    showSwitchAlignmentButtons();
                }
                else if (step >= 100)
                {
                    step = 100;
                    stopAnimation();
                    meiosisModel.setStep(step);
                    animationSlider.setValue(step);
                }
                else if (step <= 0)
                {
                    step = 0;
                    stopAnimation();
                    meiosisModel.setStep(step);
                    animationSlider.setValue(step);
                }
                else
                {
                    meiosisModel.setStep(step);
                    animationSlider.setValue(step);
                }

                if (switchAlignmentButtonsVisible &&
                    (step != CONTROL_HORIZONTAL_ALIGNMENT_STEP &&
                     step != CONTROL_VERTICAL_ALIGNMENT_STEP))
                {
                    // Hide switch alignment buttons if the buttons visible
                    // and we're not at a step where they should be visible
                    hideSwitchAlignmentButtons();
                }

                repaint();
            }
        }
    }

    /**
     * Start blinking if there is a first cut and
     * the blinking timer isn't already running.
    **/
    public void startBlinking()
    {
        if (meiosisChromosomeModelFirstCut != null &&
            blinkingTimer.isRunning() == false)
        {
            blinkingTimer.start();
            repaint();
        }
    }

    /**
     * Stop blinking
    **/
    public void stopBlinking()
    {
        if (blinkingTimer.isRunning() == true)
        {
            blinkingTimer.stop();
        }

        repaint();
    }

    /*
     * Handle a blinking time click
    **/
    private void onBlinkingTimerClick()
    {
        // Can't do anything if there is no current meiosis model
        if (meiosisModel == null)
        {
            stopAnimation();
            stopBlinking();
            return;
        }

        // If step changed, turn blinking off
        if (meiosisModel.getStep() != CONTROL_CROSSOVER_STEP)
        {
            stopBlinking();
            return;
        }

        // Reverse visibility of cut locations
        if (blinkCutLocationsVisible)
        {
            blinkCutLocationsVisible = false;
        }
        else
        {
            blinkCutLocationsVisible = true;
        }

        repaint();
    }
    
    /**
     * do auto crossing over
     **/
     
     Vector newChromosomeBranchs = new Vector();
     Vector [] branchs = new Vector[2];
     public void autoCrossing()
     {
     	Vector v1 = new Vector();
     	Vector v2 = new Vector();
     	Vector newChromosomeBranchs = new Vector();
    	Vector [] branchs = new Vector[2];
     	Branch [] arrBranch  = new Branch[2];
     	if (meiosisModel == null)
     	{
     		return;
     	}
     	Organism org = meiosisModel.getOrganism();
     	Enumeration eChromosomes = org.getChromosomes();
     	Vector chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
       
        Enumeration eChromosomeModels = null;
       
       
        Color color1 = Color.orange;
        Color color2 = Color.blue;
        
     	while (eChromosomes.hasMoreElements())
     	{
     		OrganismChromosome chr1 = (OrganismChromosome)eChromosomes.nextElement();
     		OrganismChromosome chr2 = (OrganismChromosome)eChromosomes.nextElement();
     		
     		 Color [] colors = null;
     		for (int i=0; i<chromosomeModelEnumerations.size();i++)
     		{
     			eChromosomeModels =(Enumeration)chromosomeModelEnumerations.elementAt(i);
     			while(eChromosomeModels.hasMoreElements())
     			{
     				MeiosisChromosomeModel aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
     				OrganismChromosome tempChrom =  aChromosomeModel.getChromosome();
     				if (tempChrom.equals(chr1))
     				{
     					colors = aChromosomeModel.getPStrandOneColors();
     					if (colors[0].equals(Color.orange))
     					{
     						color1 = Color.orange;
     						color2 = Color.blue;
     							
     					}
     					else
     					{
     						color1 = Color.blue;
     						color2 = Color.orange;
     						
     					}
     				}
     			}
     		}
     		
     		Branch b1 = new Branch(chr1,color1);
     		Branch b2 = new Branch(chr2,color2);
     		CrossOver co = new CrossOver();
     		if ((chr1.getNumberTypeAsString().equals("X")) && (chr2.getNumberTypeAsString().equals("Y"))||
		    	(chr2.getNumberTypeAsString().equals("X")) && (chr1.getNumberTypeAsString().equals("Y")))
		    	{
		    		arrBranch[0] = b1;
		    		arrBranch[0].setChromosome(b1.getChromosome());
		    		arrBranch[1] = b2;
		    		arrBranch[1].setChromosome(b2.getChromosome());
		    	}
		    else
		    	{
     				 arrBranch = co.switchAlleles(b1,b2);
     				 arrBranch[0].setChromosome(chr1);
     				 arrBranch[1].setChromosome(chr2);
     			}
     		
     		
     		
     		v1.addElement(arrBranch[0]);
     		v2.addElement(arrBranch[1]);
     		
     	}
     	branchs[0] = v1;
     	
     	branchs[1] = v2;
     	
     	meiosisModel.setBranchs(branchs);
     	meiosisModel.setAutoCrossOverHappened(true);
     	for (int i = 0;i<v1.size();i++)
     	{
     		newChromosomeBranchs .addElement(v1.elementAt(i));
     	}
     	for (int i = 0;i<v2.size();i++)
     	{
     		newChromosomeBranchs .addElement(v2.elementAt(i));
     	}
     	
     }
    
    /**
     * React to actions
    **/
  
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
 
        if (e.getSource() == animationTimer)
        {
            onAnimationTimerClick();
        }
        else if (e.getSource() == blinkingTimer)
        {
            onBlinkingTimerClick();
        }
        else if (cmd.equals(cmdMagnifyView))
        {
            stopAnimation();
            // Try to use correct sex property change, defaulting to mother if organism null
            if (currentOrganism != null &&
                currentOrganism.getSex() == Organism.MALE)
            {
                changes.firePropertyChange(UIProp.BIG_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED,null,null);
            }
            else
            {
                changes.firePropertyChange(UIProp.BIG_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED,null,null);
            }
        }
        else if (cmd.equals(cmdGoToStart))
        {
            goToAnimationStep(0);
        }
        else if (cmd.equals(cmdPlayBackwardFast))
        {
            playAnimationBackwardFast();
        }
        else if (cmd.equals(cmdPlayBackward))
        {
            playAnimationBackward();
        }
        else if (cmd.equals(cmdStepBackward))
        {
            stepAnimationBackward();
        }
        else if (cmd.equals(cmdStop))
        {
            stopAnimation();
        }
        else if (cmd.equals(cmdStepForward))
        {
            stepAnimationForward();
        }
        else if (cmd.equals(cmdPlayForward))
        {
        	
            playAnimationForward();
        }
        else if (cmd.equals(cmdPlayForwardFast))
        {
            playAnimationForwardFast();
        }
        else if (cmd.equals(cmdGoToEnd))
        {
            goToAnimationStep(100);
        }
        else if (cmd.equals(cmdAutomaticAlignment))
        {
            controlAlignment = false;
            automaticAlignmentRadioButton.setSelected(true);
            controlledAlignmentRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdControlledAlignment))
        {
            controlAlignment = true;
            // toolChanged(Tool.SELECTION);
            automaticAlignmentRadioButton.setSelected(false);
            controlledAlignmentRadioButton.setSelected(true);
        }
        else if (cmd.equals(cmdAutomaticCrossover))
        {
        	
            controlCrossover = false;
           // toolChanged(Tool.SELECTION);
            if (meiosisModel.getStep()<100)
        	{
            	animationCrossover = true;
            }
            automaticCrossoverRadioButton.setSelected(true);
            controlledCrossoverRadioButton.setSelected(false);
        }
        else if (cmd.equals(cmdControlledCrossover))
        {
        	controlCrossover = true;
            animationCrossover = false;
            automaticCrossoverRadioButton.setSelected(false);
            controlledCrossoverRadioButton.setSelected(true);
        }
        else if (cmd.equals(cmdSwitchHorizontalAlignment))
        {
        	 //toolChanged(Tool.SELECTION);
            // Can't do anything if meiosisModel null
            if (meiosisModel == null)
            {
                return;
            }

            int i;
            int length = switchHorizontalAlignmentButton.length;
            for(i=0;i<length;i++)
            {
                if (e.getSource() == switchHorizontalAlignmentButton[i])
                {
                    meiosisModel.switchHorizontalAlignment(i+1);
                    repaint();
                    return;
                }
            }
        }
        else if (cmd.equals(cmdSwitchVerticalAlignment))
        {
        	 //toolChanged(Tool.SELECTION);
            // Can't do anything if meiosisModel null
            if (meiosisModel == null)
            {
                return;
            }

            int i;
            int length = switchVerticalAlignmentButton.length;
            for(i=0;i<length;i++)
            {
                if (e.getSource() == switchVerticalAlignmentButton[i])
                {
                    meiosisModel.switchVerticalAlignment(i+1);
                    repaint();
                    return;
                }
            }
        }
    }

    /**
     * Change listener events
    **/
    public void stateChanged(ChangeEvent e)
    {
        // Ignore state change events on the slider if the play mode is not stopped.
        // In other words, we already know about slider change events when we're
        // playing as we're the ones who told the slider to change its value.
        if (currentPlayMode == PLAY_MODE_STOPPED)
        {
            int sliderValue = animationSlider.getValue();
            goToAnimationStep(sliderValue);
        }
    }

    /**
     * Method called by ToolView when the current tool changes.
     * If we get a tool other than snip, we default to the
     * selection tool.  In other words, we ignore tools
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
        else
        {
            // Default to selection
            setCursor(Tool.getCursor(Tool.SELECTION));
            activeTool = Tool.SELECTION;

            // Clear crossover state
            clearCrossingOverState();
            repaint();
        }
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
        // Return immediately if no cells shown
        if (meiosisModel == null)
        {
            return;
        }

        String propertyName = event.getPropertyName();

        if (propertyName.equals(UIProp.SELECTED_GAMETE) ||
            propertyName.equals(UIProp.MOVED_GAMETE) ||
            propertyName.equals(UIProp.ORGANISM))
        {
            stopAnimation();
            updateState();
        }
        else if (propertyName.equals(UIProp.MEIOSIS_STEP))
        {
            // meiosis step changed, so clear any drag state
            clearMovedChromosomeModelsState();
            updateState();

            if (smallMeiosisView != null)
            {
                int typeOfView = smallMeiosisView.getMotherOrFatherView();
                if (typeOfView == SmallMeiosisView.FATHER_VIEW)
                {
                    changes.firePropertyChange(UIProp.MEIOSIS_FATHER_STEP,
                                               event.getOldValue(),
                                               event.getNewValue());
                }
                else if (typeOfView == SmallMeiosisView.MOTHER_VIEW)
                {
                    changes.firePropertyChange(UIProp.MEIOSIS_MOTHER_STEP,
                                               event.getOldValue(),
                                               event.getNewValue());
                }
            }
            else
            {
                // Just fire generic meiosis step event
                changes.firePropertyChange(UIProp.MEIOSIS_STEP,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
        }
    }

    private void clearCrossingOverState()
    {
        stopBlinking();

        if (crossingOverStep > 0)
        {
            if (meiosisChromosomeModelFirstCut != null)
            {
                meiosisChromosomeModelFirstCut.setTemporaryOffsets(xCrossingOverOriginalFirstCutTemporaryOffset,
                                                                   yCrossingOverOriginalFirstCutTemporaryOffset);
            }
            if (meiosisChromosomeModelSecondCut != null)
            {
                meiosisChromosomeModelSecondCut.setTemporaryOffsets(xCrossingOverOriginalSecondCutTemporaryOffset,
                                                                    yCrossingOverOriginalSecondCutTemporaryOffset);
            }
        }

        meiosisChromosomeModelFirstCut = null;
        meiosisChromosomeModelSecondCut = null;
        strandTypeFirstCut = 0;
        strandTypeSecondCut = 0;
        strandPointIndexCut = 0;
        crossingOverStep = 0;
    }

    private void clearMovedChromosomeModelsState()
    {
        selectedMeiosisChromosomeModel = null;
        xMouseDown = 0;
        yMouseDown = 0;

        if (movedChromosomeModels != null)
        {
            MeiosisChromosomeModel aChromosomeModel;
            Enumeration eChromosomeModels = movedChromosomeModels.elements();
            while (eChromosomeModels.hasMoreElements())
            {
                aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
                aChromosomeModel.clearTemporaryOffsets();
            }

            movedChromosomeModels = null;
        }

        // Don't do repaint here, as it happens automatically via other means
    }
}

