//
// Class : BigFertilizationView - the small fertilization view used as the bottom center
//							        subview in the sex view of the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.4 $
// $Date: 2002/12/19 22:00:30 $
// $Author: qliao $
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
 * This class represents a view which shows the 2 sex cells of 2 parents in the sex view
 * and has controls for the user to conduct fertilization within this view of those 2 cells.
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.BIG_FERTILIZATION_MAGNIFY_BUTTON_PUSHED - the magnifying button was pushed in this view was pushed
 * <li> UIProp.FERTILIZATION_MANUALLY_DISABLED - the manually disabled state of fertilization changed
 * <li> UIProp.FERTILIZATION_MODEL - the fertilization model was changed
 * <li> UIProp.FERTILIZATION_STEP - the fertilization step has changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#BIG_FERTILIZATION_MAGNIFY_BUTTON_PUSHED
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_MANUALLY_DISABLED
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_MODEL
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_STEP
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.4 $ $Date: 2002/12/19 22:00:30 $
 * @author 		$Author: qliao $
**/

public final class BigFertilizationView
extends UIView
implements MouseListener, MouseMotionListener, ActionListener, ComponentListener, ChangeListener, PropertyChangeListener
{
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

    /**
     * Play modes for this view.  One of them is always true and the rest false.
    **/
    static private final int PLAY_MODE_BACKWARD_FAST	= 1;
    static private final int PLAY_MODE_BACKWARD			= 2;
    static private final int PLAY_MODE_STOPPED			= 3;
    static private final int PLAY_MODE_FORWARD			= 4;
    static private final int PLAY_MODE_FORWARD_FAST		= 5;

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

    /**
     * Length of leader line to allele symbol text
    **/
    private int lengthLeaderLine = 0;

    /**
     * Magnify button - used to magnify view back down to 6 views version of sex view
    **/
    private JButton magnifyButton = null;

    /**
     * Go to start button - used to rewind fertilization to start
    **/
    private JButton goToStartButton = null;

    /**
     * Play backward fast button - used to rewind fertilization quickly
    **/
    private JToggleButton playBackwardFastToggleButton = null;

    /**
     * Play backward button - used to play fertilization backward at normal speed
    **/
    private JToggleButton playBackwardToggleButton = null;

    /**
     * Step backward button - used to step fertilization one step backward
    **/
    private JButton stepBackwardButton = null;

    /**
     * Stop button - used to stop fertilization
    **/
    private JToggleButton stopToggleButton = null;

    /**
     * Step forward button - used to step fertilization one step forward
    **/
    private JButton stepForwardButton = null;

    /**
     * Play forward button - used to play fertilization forward at normal speed
    **/
    private JToggleButton playForwardToggleButton = null;

    /**
     * Play forward fast button - used to play fertilization forward quickly
    **/
    private JToggleButton playForwardFastToggleButton = null;

    /**
     * Go to end button - used to fast forward fertilization to end
    **/
    private JButton goToEndButton = null;

    /**
     * Current fertilization model
    **/
    private FertilizationModel fertilizationModel;

    /**
     * Slider showing the position of the fertilization
    **/
    private JSlider animationSlider;

    /**
     * Timer for animation
    **/
    private javax.swing.Timer animationTimer;

    /**
     * Selected chromosome model
    **/
    private FertilizationChromosomeModel selectedFertilizationChromosomeModel = null;

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
     * Creates a big fertilization view.
    **/
    public BigFertilizationView()
    {
        super();

        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);

        fertilizationModel = null;
        currentPlayMode = PLAY_MODE_STOPPED;
        selectedFertilizationChromosomeModel = null;
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

        animationSlider = new JSlider(JSlider.HORIZONTAL,0,35,0);
        animationSlider.addChangeListener(this);
        animationSlider.setBounds(2,2,286,26);
        animationSlider.setBackground(Color.lightGray);
        animationSlider.setVisible(true);
        animationSlider.setEnabled(true);
        animationSlider.setToolTipText("Move slider to control fertilization");
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

        // Create animation timer
        animationTimer = new javax.swing.Timer(100,this);

        // Turn on double buffering
        setDoubleBuffered(true);

        // Listen for mouse clicks, but not mouse motion initially
        addMouseListener(this);
        addMouseMotionListener(this);

        // Listen for resize events
        addComponentListener(this);
    }

    /**
     * Set the current fertilization model to be shown in this view.
     *
     * @param		aModel FertilizationModel - the fertilization model to be shown in this view
    **/
    void setFertilizationModel(FertilizationModel aModel)
    {
        // Ignore redundant setting
        if (fertilizationModel == aModel)
        {
            return;
        }

        // Stop the animation
        stopAnimation();

        // Make the change
        if (fertilizationModel != null)
        {
            fertilizationModel.removePropertyChangeListener(this);
        }

        fertilizationModel = aModel;

        if (fertilizationModel != null)
        {
            animationSlider.setValue(fertilizationModel.getStep());
            fertilizationModel.addPropertyChangeListener(this);

            if (fertilizationModel.isValidToDoFertilization() &&
                fertilizationManuallyDisabled == false)
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
            }
            else
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
            }
        }
        else
        {
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
        }

        // Repaint
        repaint();
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

        // Make change
        boolean oldFertilizationManuallyDisabled = fertilizationManuallyDisabled;
        fertilizationManuallyDisabled = aManuallyDisabled;

        // Enabled or disable controls as necessary
        if (fertilizationModel != null &&
            fertilizationModel.isValidToDoFertilization() &&
            fertilizationManuallyDisabled == false)
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
        }
        else
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
        }

        // Notify listeners
        changes.firePropertyChange(UIProp.FERTILIZATION_MANUALLY_DISABLED,
                                   new Boolean(oldFertilizationManuallyDisabled),
                                   new Boolean(fertilizationManuallyDisabled));
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
     * Update the state of this view, usually called because the state
     * of the underlying fertilization model has changed somehow and this
     * view should reflect that changed state.
    **/
    public void updateState()
    {
        // Stop the animation
        stopAnimation();

        if (fertilizationModel != null)
        {
            animationSlider.setValue(fertilizationModel.getStep());

            if (fertilizationModel.isValidToDoFertilization())
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
            }
        }
        else
        {
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
        }

        // Repaint
        repaint();
    }

    /**
     * Draw the graphics in this view.
     *
     * @param 		g Graphics - the given graphics to use in drawing
    **/
    public void paintComponent(Graphics g)
    {
        // Return immediately if fertilizationModel null
        if (fertilizationModel == null)
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

        // Draw cell boundaries
        g.setFont(getFont());
        g.setColor(getForeground());

        int currentStep = fertilizationModel.getStep();

        Object boundary;
        CellArc cellArc;
        CellLine cellLine;
        FilledCircle filledCircle;
        
        Vector cellBoundaries = fertilizationModel.getCellBoundaries();
        Enumeration eCellBoundaries = cellBoundaries.elements();
        while (eCellBoundaries.hasMoreElements())
        {
            boundary = eCellBoundaries.nextElement();

            if (boundary instanceof CellArc)
            {
                cellArc = (CellArc) boundary;
                g.drawArc(cellArc.getXLeft(), cellArc.getYTop(), cellArc.getWidth(), cellArc.getHeight(),
                          cellArc.getStartAngle(), cellArc.getSpanAngle());
                if (cellArc.getModel() == CellArc.FERTILIZATION_FATHER_GAMETE_MODEL)
                {
                	int [] tailx= new int[5];
                	int [] taily= new int[5];
                	tailx[0] = cellArc.getXLeft()+cellArc.getWidth();
                	taily[0] = cellArc.getYTop()+cellArc.getHeight()/2;
                	for (int i=1;i<5;i++)
                	{
                		tailx[i] = tailx[i-1]+20;
                	}
                	for (int i = 1;i<5;i++)
                	{
                		if (taily[i-1] == taily[0])
                			taily[i] = taily[i-1]+10;
                		else if (taily[i-1] > taily[0])
                			taily[i] = taily[i-1]-20;
                		else 
                			taily[i] = taily[i-1]+20;
                	}
                	
                	for(int i= 1;i<5;i++)
                	{
                		g.drawLine(tailx[i-1], taily[i-1],tailx[i],taily[i]);
                	}
                }
                          
                 if(fertilizationModel.getStep() == 0)
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
            else if (boundary instanceof FilledCircle && fertilizationModel.getStep()!=0)
            {
                filledCircle = (FilledCircle) boundary;
                g.setColor(getBackground());
                g.fillOval(filledCircle.getXLeft(), filledCircle.getYTop(),
                           filledCircle.getWidth(), filledCircle.getHeight());
                g.setColor(getForeground());
            }
        }
      

        // Draw chromosomes.
        // Done with a double while loop where the first loop is through a Vector
        // of Enumerations over the chromosomes in a particular portion of the
        // fertilization and the inner loop is over the chromosome models in that
        // particular Enumeration.
        int i;
        int iStrand;
        int length;
        int xCentromere, yCentromere;
        int[] xPoints;
        int[] yPoints;
        Color[] colors;
        OrganismAllele[] alleles;
        String alleleTextSymbol;
        int xTemporaryOffset = 0;
        int yTemporaryOffset = 0;

        FertilizationChromosomeModel aChromosomeModel;
        Enumeration eChromosomeModels;

        // Draw chromosomes and alleles
        Vector chromosomeModelEnumerations = fertilizationModel.getChromosomeModelEnumerations();
        Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
        while (eChromosomeModelEnumerations.hasMoreElements())
        {
            eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            while (eChromosomeModels.hasMoreElements())
            {
            	
                aChromosomeModel = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
    			
                xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();

                xCentromere = (int) aChromosomeModel.getXCentromereAtStep(currentStep);
                yCentromere = (int) aChromosomeModel.getYCentromereAtStep(currentStep);
    
                for (iStrand=0;iStrand<2;iStrand++)
                {
                    xPoints = null;
                    yPoints = null;
                    alleles = null;
                    colors = null;
    
                    // Get strand locations
                    switch (iStrand)
                    {
                        case 0:
                            xPoints = aChromosomeModel.getXPStrandPoints(currentStep);
                            yPoints = aChromosomeModel.getYPStrandPoints(currentStep);
                            alleles = aChromosomeModel.getPStrandAlleles();
                            colors = aChromosomeModel.getPStrandColors();
                            break;
        
                        case 1:
                            xPoints = aChromosomeModel.getXQStrandPoints(currentStep);
                            yPoints = aChromosomeModel.getYQStrandPoints(currentStep);
                            alleles = aChromosomeModel.getQStrandAlleles();
                            colors = aChromosomeModel.getQStrandColors();
                            break;
                    }
                    
                    // Draw filled polygon for this strand, if the strand exists
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
       

        // Draw allele text symbols
        chromosomeModelEnumerations = fertilizationModel.getChromosomeModelEnumerations();
        eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
        while (eChromosomeModelEnumerations.hasMoreElements())
        {
            eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            while (eChromosomeModels.hasMoreElements())
            {
                aChromosomeModel = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
    
                xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();

                xCentromere = (int) aChromosomeModel.getXCentromereAtStep(currentStep);
                yCentromere = (int) aChromosomeModel.getYCentromereAtStep(currentStep);
    
                for (iStrand=0;iStrand<2;iStrand++)
                {
                    xPoints = null;
                    yPoints = null;
                    alleles = null;
    
                    // Get strand locations
                    switch (iStrand)
                    {
                        case 0:
                            xPoints = aChromosomeModel.getXPStrandPoints(currentStep);
                            yPoints = aChromosomeModel.getYPStrandPoints(currentStep);
                            alleles = aChromosomeModel.getPStrandAlleles();
                            break;
        
                        case 1:
                            xPoints = aChromosomeModel.getXQStrandPoints(currentStep);
                            yPoints = aChromosomeModel.getYQStrandPoints(currentStep);
                            alleles = aChromosomeModel.getQStrandAlleles();
                            break;
                    }
                    
                    if (xPoints != null && yPoints != null && alleles != null)
                    {
                        length = xPoints.length;
                        for (i=0;i<length;i++)
                        {
                            if (alleles[i] != null)
                            {
                                // If this gene is not visible skip painting the allele text symbol
                                if (! alleles[i].getGene().isVisible())
                                    continue;
                                alleleTextSymbol = alleles[i].getTextSymbol();

                                // Put symbol on far side of centromere
                                if (xPoints[i] < xCentromere && currentStep>0)
                                {
                                    // Make strand one's symbol be on left
                                    paintAlleleTextSymbol(g,
                                                          xPoints[i]+xTemporaryOffset,
                                                          yPoints[i]+yTemporaryOffset,
                                                          alleleTextSymbol,
                                                          fontMetrics.stringWidth(alleleTextSymbol),
                                                          fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_LEFT);
                                }
                                else if (currentStep>0)
                                {
                                    // Make strand one's symbol be on right
                                    paintAlleleTextSymbol(g,
                                                          xPoints[i]+xTemporaryOffset,
                                                          yPoints[i]+yTemporaryOffset,
                                                          alleleTextSymbol,
                                                          fontMetrics.stringWidth(alleleTextSymbol),
                                                          fontAscent, fontHeight, DRAW_ALLELE_SYMBOL_RIGHT);
                                }
                            }
                        }
                    }
                }
            }
        }
        
       
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
                                       int xPoint, int yPoint,
                                       String text, int textWidth,
                                       int fontAscent, int fontHeight,
                                       int side)
    {
        g.setFont(UIGraphics.getFontBold12());

        if (side == DRAW_ALLELE_SYMBOL_RIGHT)
        {
            // Draw symbol on right side
            g.setColor(Color.lightGray);
            g.drawLine(xPoint+radiusChromosome,
                       yPoint-radiusChromosome,
                       xPoint+radiusChromosome+lengthLeaderLine,
                       yPoint-radiusChromosome);
            g.setColor(getBackground());
            g.fillRect(xPoint+radiusChromosome+lengthLeaderLine,
                       yPoint-radiusChromosome-(fontHeight/2),
                       textWidth+4,fontHeight);
            g.setColor(getForeground());
            g.drawRect(xPoint+radiusChromosome+lengthLeaderLine,
                       yPoint-radiusChromosome-(fontHeight/2),
                       textWidth+4,fontHeight);
            g.drawString(text,
                         xPoint+radiusChromosome+lengthLeaderLine+3,
                         yPoint-radiusChromosome-(fontHeight/2)+fontAscent);
        }
        else if (side == DRAW_ALLELE_SYMBOL_LEFT)
        {
            // Draw symbol on left side
            g.setColor(Color.lightGray);
            g.drawLine(xPoint-radiusChromosome,
                       yPoint-radiusChromosome,
                       xPoint-radiusChromosome-lengthLeaderLine,
                       yPoint-radiusChromosome);
            g.setColor(getBackground());
            g.fillRect(xPoint-radiusChromosome-lengthLeaderLine-textWidth-4,
                       yPoint-radiusChromosome-(fontHeight/2),
                       textWidth+4,fontHeight);
            g.setColor(getForeground());
            g.drawRect(xPoint-radiusChromosome-lengthLeaderLine-textWidth-4,
                       yPoint-radiusChromosome-(fontHeight/2),
                       textWidth+4,fontHeight);
            g.drawString(text,
                         xPoint-radiusChromosome-lengthLeaderLine-textWidth-1,
                         yPoint-radiusChromosome-(fontHeight/2)+fontAscent);
        }
    }
    
    protected int getTypeFromCellArc(CellArc cellArc)
    {
        int xLeft = cellArc.getXLeft();
        int yTop = cellArc.getYTop();
        int width = cellArc.getWidth();
        int height = cellArc.getHeight();
        if (xLeft < width)
        {
       		return FertilizationChromosomeModel.FEMALE_GAMETE_CHROMOSOME;
        }
        else  
        {
           return FertilizationChromosomeModel.MALE_GAMETE_CHROMOSOME;
        }
    }


	protected void paintAlleleList (Graphics g, CellArc boundary)
    {
        int type = getTypeFromCellArc(boundary);
        Vector models = getGameteChromosomeModels(type);
      
        int xText = boundary.getXLeft();
        int yText = boundary.getYTop()-15;
      	
        for (int i = 0; i <models.size() ; i++)
        {
            FertilizationChromosomeModel model = (FertilizationChromosomeModel) models.elementAt(i);
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
                    alleles = model.getPStrandAlleles();
                   
                    xAlleles = model.getXPStrandPoints(0);
                    yAlleles = model.getYPStrandPoints(0);
                    
                }
                else
                {
                    alleles = model.getQStrandAlleles();
                   
                    xAlleles = model.getXQStrandPoints(0);
                    yAlleles = model.getYQStrandPoints(0);
                   
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
    }
     protected Vector getGameteChromosomeModels(int modelType)
    {
        Vector result = new Vector();
        if (fertilizationModel instanceof FertilizationModel)
        {
            Vector chromosomeModelEnumerations = fertilizationModel.getChromosomeModelEnumerations();
            Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
            while (eChromosomeModelEnumerations.hasMoreElements())
            {
                Enumeration eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
                while (eChromosomeModels.hasMoreElements())
                {
                    FertilizationChromosomeModel model = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
                   
                    if (model.getChromosomeModelType() == modelType)
                        result.addElement(model);
                }
            }
        }
        return result;
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

            // Update fertilization model and its chromosome models
            if (fertilizationModel != null)
            {
                fertilizationModel.setEnclosingViewRectangle(new Rectangle(0,0,actualWidth,actualHeight-52));
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
        }
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
        if (fertilizationModel != null)
        {
            // Clear state if we think we still have chromosome selected for some reason
            if (selectedFertilizationChromosomeModel != null)
            {
                selectedFertilizationChromosomeModel = null;
                xMouseDown = 0;
                yMouseDown = 0;
            }

            // Mouse coordinates
            int xMouse = event.getX();
            int yMouse = event.getY();

            int xDelta, yDelta;
            int xyDeltaMin = 10000;
            FertilizationChromosomeModel closestFertilizationChromosomeModel = null;

            // Find the closest chromosome and, if we find one sufficiently close,
            // record that chromosome, the current step and put us into move mode.
            int currentStep = fertilizationModel.getStep();
        
            int i;
            int iStrand;
            int length;
            int[] xPoints;
            int[] yPoints;
            int xTemporaryOffset = 0;
            int yTemporaryOffset = 0;
    
            FertilizationChromosomeModel aChromosomeModel;
            Enumeration eChromosomeModels;
    
            // Walk through chromosomes finding closest chromosome
            Vector chromosomeModelEnumerations = fertilizationModel.getChromosomeModelEnumerations();
            Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
            while (eChromosomeModelEnumerations.hasMoreElements())
            {
                eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
                while (eChromosomeModels.hasMoreElements())
                {
                    aChromosomeModel = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
    
                    xTemporaryOffset = aChromosomeModel.getXTemporaryOffset();
                    yTemporaryOffset = aChromosomeModel.getYTemporaryOffset();
        
                    for (iStrand=0;iStrand<2;iStrand++)
                    {
                        xPoints = null;
                        yPoints = null;
        
                        // Get strand locations
                        switch (iStrand)
                        {
                            case 0:
                                xPoints = aChromosomeModel.getXPStrandPoints(currentStep);
                                yPoints = aChromosomeModel.getYPStrandPoints(currentStep);
                                break;
        
                            case 1:
                                xPoints = aChromosomeModel.getXQStrandPoints(currentStep);
                                yPoints = aChromosomeModel.getYQStrandPoints(currentStep);
                                break;
                        }
                        
                        // Loop to find closest chromosome
                        if (xPoints != null && yPoints != null)
                        {
                            // Just strand one shown
                            length = xPoints.length;
                            for (i=0;i<length;i++)
                            {
                                // Check strand one
                                if (xMouse > (xPoints[i]+xTemporaryOffset))
                                {
                                    xDelta = xMouse - (xPoints[i]+xTemporaryOffset);
                                }
                                else
                                {
                                    xDelta = (xPoints[i]+xTemporaryOffset) - xMouse;
                                }

                                if (yMouse > (yPoints[i]+yTemporaryOffset))
                                {
                                    yDelta = yMouse - (yPoints[i]+yTemporaryOffset);
                                }
                                else
                                {
                                    yDelta = (yPoints[i]+yTemporaryOffset) - yMouse;
                                }

                                if (xDelta + yDelta < xyDeltaMin)
                                {
                                    xyDeltaMin = xDelta + yDelta;
                                    closestFertilizationChromosomeModel = aChromosomeModel;
                                }
                            }
                        }
                    }
                }
            }

            // Select closest chromosome model
            if (selectedFertilizationChromosomeModel != closestFertilizationChromosomeModel)
            {
                selectedFertilizationChromosomeModel = closestFertilizationChromosomeModel;

                if (selectedFertilizationChromosomeModel != null)
                {
                    xTemporaryOffset = selectedFertilizationChromosomeModel.getXTemporaryOffset();
                    yTemporaryOffset = selectedFertilizationChromosomeModel.getYTemporaryOffset();

                    xMouseDown = xMouse - xTemporaryOffset;
                    yMouseDown = yMouse - yTemporaryOffset;

                    if (movedChromosomeModels == null)
                    {
                        movedChromosomeModels = new Vector();
                    }
                    movedChromosomeModels.addElement(selectedFertilizationChromosomeModel);
                }

                repaint();
            }
        }
    }

    /**
     * Handle mouse released event
    **/
    public void mouseReleased(MouseEvent event)
    {
        if (fertilizationModel != null &&
            selectedFertilizationChromosomeModel != null)
        {
            int xMouse = event.getX();
            int yMouse = event.getY();

            selectedFertilizationChromosomeModel.setTemporaryOffsets(xMouse-xMouseDown,
                                                                     yMouse-yMouseDown);

            // End drag
            selectedFertilizationChromosomeModel = null;
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
        if (fertilizationModel != null &&
            selectedFertilizationChromosomeModel != null)
        {
            int xMouse = event.getX();
            int yMouse = event.getY();

            selectedFertilizationChromosomeModel.setTemporaryOffsets(xMouse-xMouseDown,
                                                                     yMouse-yMouseDown);
            // Force repaint
            repaint();
        }
    }

    /**
     * Handle mouse moved event
    **/
    public void mouseMoved(MouseEvent event)
    {
    }

    /**
     * Play animation backward fast
    **/
    public void playAnimationBackwardFast()
    {
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

            repaint();
        }
    }

    /**
     * Play animation backward
    **/
    public void playAnimationBackward()
    {
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

            repaint();
        }
    }

    /**
     * Step animation backward one step
    **/
    public void stepAnimationBackward()
    {
        stopAnimation();

        // Can't change step if fertilizationModel null
        if (fertilizationModel == null)
        {
            return;
        }

        int currentStep = fertilizationModel.getStep();

        if (currentStep > 0)
        {
            currentStep -= 1;
            fertilizationModel.setStep(currentStep);
            animationSlider.setValue(currentStep);

            repaint();
        }
    }

    /**
     * Stop playing animation
    **/
    public void stopAnimation()
    {
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
        stopAnimation();

        // Can't change step if fertilizationModel null
        if (fertilizationModel == null)
        {
            return;
        }

        int currentStep = fertilizationModel.getStep();

        if (currentStep < 35)
        {
            currentStep += 1;
            fertilizationModel.setStep(currentStep);
            animationSlider.setValue(currentStep);

            repaint();
        }
    }

    /**
     * Play animation forward
    **/
    public void playAnimationForward()
    {
        // Can't change step if fertilizationModel null
        if (fertilizationModel == null)
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
        // Can't change step if fertilizationModel null
        if (fertilizationModel == null)
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
     * @param		aStep int - a step (0 to 35)
    **/
    public void goToAnimationStep(int aStep)
    {
        stopAnimation();

        // Can't change step if fertilizationModel null
        if (fertilizationModel == null)
        {
            return;
        }

        int step = aStep;
        if (step < 0)
        {
            step = 0;
        }
        else if (step > 35)
        {
            step = 35;
        }

        fertilizationModel.setStep(step);
        animationSlider.setValue(step);

        repaint();
    }

    /**
     * Handle an animation time click
    **/
    private void onAnimationTimerClick()
    {
        // Can't change step if fertilizationModel null
        if (fertilizationModel == null)
        {
            stopAnimation();
            return;
        }

        int step = fertilizationModel.getStep();
        int origStep = step;

        switch(currentPlayMode)
        {
            case PLAY_MODE_BACKWARD_FAST:
                if (step > 1)
                {
                    step -= 2;
                }
                else
                {
                    step = 0;
                    stopAnimation();
                }
                break;

            case PLAY_MODE_BACKWARD:
                if (step > 0)
                {
                    step--;
                }
                else
                {
                    step = 0;
                    stopAnimation();
                }
                break;

            case PLAY_MODE_STOPPED:
                System.err.println("animation timer click received when stopped");
                break;

            case PLAY_MODE_FORWARD:
                if (step < 35)
                {
                    step += 1;
                }
                else
                {
                    step = 35;
                    stopAnimation();
                }
                break;
            
            case PLAY_MODE_FORWARD_FAST:
                if (step < 34)
                {
                    step += 2;
                }
                else
                {
                    step = 35;
                    stopAnimation();
                }
                break;
        }

        // If step changed, tell meiosis model and repaint
        if (origStep != step)
        {
            fertilizationModel.setStep(step);
            animationSlider.setValue(step);
            repaint();
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
        else if (cmd.equals(cmdMagnifyView))
        {
            stopAnimation();
            // Notify listeners
            changes.firePropertyChange(UIProp.BIG_FERTILIZATION_MAGNIFY_BUTTON_PUSHED,null,null);
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
            goToAnimationStep(35);
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
        if (fertilizationModel == null)
        {
            return;
        }

        String propertyName = event.getPropertyName();

        if (propertyName.equals(UIProp.FERTILIZATION_GAMETES))
        {
            updateState();
        }
        else if (propertyName.equals(UIProp.FERTILIZATION_STEP))
        {
            // fertilization step changed, so clear any drag state
            clearMovedChromosomeModelsState();
        }
    }

    /**
     * Clear the moved chromosome models state both here and
     * in the moved chromosomes models.
    **/
    private void clearMovedChromosomeModelsState()
    {
        selectedFertilizationChromosomeModel = null;
        xMouseDown = 0;
        yMouseDown = 0;

        if (movedChromosomeModels != null)
        {
            FertilizationChromosomeModel aChromosomeModel;
            Enumeration eChromosomeModels = movedChromosomeModels.elements();
            while (eChromosomeModels.hasMoreElements())
            {
                aChromosomeModel = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
                aChromosomeModel.clearTemporaryOffsets();
            }

            movedChromosomeModels = null;
        }

        // Don't do repaint here, as it happens automatically via other means
    }
}

