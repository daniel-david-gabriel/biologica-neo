//
// Class : SmallFertilizationView - the small fertilization view used as the bottom center
//							        subview in the sex view of the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.4 $
// $Date: 2002/12/19 21:59:42 $
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
 * <li> UIProp.FERTILIZATION_MANUALLY_DISABLED - the manually disabled state of fertilization changed
 * <li> UIProp.FERTILIZATION_MODEL - the fertilization model was changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * <li> UIProp.SMALL_FERTILIZATION_MAGNIFY_BUTTON_PUSHED - the magnifying button was pushed in this view was pushed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_MANUALLY_DISABLED
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_MODEL
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#SMALL_FERTILIZATION_MAGNIFY_BUTTON_PUSHED
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.4 $ $Date: 2002/12/19 21:59:42 $
 * @author 		$Author: qliao $
**/

public final class SmallFertilizationView
extends UIView
implements ActionListener, ComponentListener, ChangeListener, PropertyChangeListener
{
    // Commands - must be unique
    static private final String cmdMagnifyView		= "cmdMagnifyView";
    static private final String cmdStop				= "cmdStop";
    static private final String cmdPlayForward		= "cmdPlayForward";

    /**
     * Play modes for this view.  One of them is always true and the rest false.
    **/
    static private final int PLAY_MODE_STOPPED			= 1;
    static private final int PLAY_MODE_FORWARD			= 2;

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
     * Magnify button - used to magnify view to entire sex view
    **/
    private JButton magnifyButton = null;

    /**
     * Stop button - used to stop fertilization
    **/
    private JToggleButton stopToggleButton = null;

    /**
     * Play forward button - used to play fertilization forward at normal speed
    **/
    private JToggleButton playForwardToggleButton = null;

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
     * Creates a small fertilization view.
    **/
    public SmallFertilizationView()
    {
        super();

        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);

        fertilizationModel = null;
        currentPlayMode = PLAY_MODE_STOPPED;

        Insets insets = new Insets(2,2,2,2);

        magnifyButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/magupglass.gif"));
        magnifyButton.setMargin(insets);
        magnifyButton.addActionListener(this);
        magnifyButton.setActionCommand(cmdMagnifyView);
        magnifyButton.setFocusPainted(false);
        magnifyButton.setBounds(2,2,26,26);
        magnifyButton.setBackground(Color.lightGray);
        magnifyButton.setVisible(true);
        magnifyButton.setEnabled(true);
        magnifyButton.setToolTipText("Magnify this view");
        add(magnifyButton);

        animationSlider = new JSlider(JSlider.HORIZONTAL,0,35,0);
        animationSlider.addChangeListener(this);
        animationSlider.setBounds(2,2,286,26);
        animationSlider.setBackground(Color.lightGray);
        animationSlider.setVisible(true);
        animationSlider.setEnabled(true);
        animationSlider.setToolTipText("Move slider to control fertilization");
        add(animationSlider);

        stopToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/stop.gif"));
        stopToggleButton.setMargin(insets);
        stopToggleButton.addActionListener(this);
        stopToggleButton.setActionCommand(cmdStop);
        stopToggleButton.setFocusPainted(false);
        stopToggleButton.setToolTipText("Stop");
        stopToggleButton.setSelected(true);
        add(stopToggleButton);

        playForwardToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/play.gif"));
        playForwardToggleButton.setMargin(insets);
        playForwardToggleButton.addActionListener(this);
        playForwardToggleButton.setActionCommand(cmdPlayForward);
        playForwardToggleButton.setFocusPainted(false);
        playForwardToggleButton.setToolTipText("Play");
        playForwardToggleButton.setSelected(false);
        add(playForwardToggleButton);

        // Create animation timer
        animationTimer = new javax.swing.Timer(100,this);

        // Turn on double buffering
        setDoubleBuffered(true);

        // Listen for resize events
        addComponentListener(this);
    }
    
    /**
     * Set the current fertilization model to be shown in this view.
     *
     * @param		aFertilizationModel FertilizationModel - the fertilization model to be shown in this view
    **/
    void setFertilizationModel(FertilizationModel aFertilizationModel)
    {
        // Ignore redundant setting
        if (fertilizationModel == aFertilizationModel)
        {
            return;
        }

        // Stop the animation
        stopAnimation();

        // Save old fertilization model
        FertilizationModel oldFertilizationModel = fertilizationModel;

        // Make the change
        if (fertilizationModel != null)
        {
            fertilizationModel.removePropertyChangeListener(this);
        }

        fertilizationModel = aFertilizationModel;

        if (fertilizationModel != null)
        {
            animationSlider.setValue(fertilizationModel.getStep());
            fertilizationModel.addPropertyChangeListener(this);

            if (fertilizationModel.isValidToDoFertilization() &&
                fertilizationManuallyDisabled == false)
            {
                stopToggleButton.setEnabled(true);
                playForwardToggleButton.setEnabled(true);
                animationSlider.setEnabled(true);
                magnifyButton.setEnabled(true);
            }
            else
            {
                stopToggleButton.setEnabled(false);
                playForwardToggleButton.setEnabled(false);
                animationSlider.setEnabled(false);
                magnifyButton.setEnabled(false);
            }
            updateSize(true);
        }
        else
        {
            animationSlider.setValue(0);
            stopToggleButton.setEnabled(false);
            playForwardToggleButton.setEnabled(false);
            animationSlider.setEnabled(false);
            magnifyButton.setEnabled(false);
            actualHeight = 0;
            actualWidth = 0;
        }

        // Repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.FERTILIZATION_MODEL,oldFertilizationModel,fertilizationModel);
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
            stopToggleButton.setEnabled(true);
            playForwardToggleButton.setEnabled(true);
            animationSlider.setEnabled(true);
            magnifyButton.setEnabled(true);
        }
        else
        {
            stopToggleButton.setEnabled(false);
            playForwardToggleButton.setEnabled(false);
            animationSlider.setEnabled(false);
            magnifyButton.setEnabled(false);
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

            if (fertilizationModel.isValidToDoFertilization() &&
                fertilizationManuallyDisabled == false)
            {
                stopToggleButton.setEnabled(true);
                playForwardToggleButton.setEnabled(true);
                animationSlider.setEnabled(true);
                magnifyButton.setEnabled(true);
            }
            else
            {
                stopToggleButton.setEnabled(false);
                playForwardToggleButton.setEnabled(false);
                animationSlider.setEnabled(false);
                magnifyButton.setEnabled(false);
            }
        }
        else
        {
            animationSlider.setValue(0);
            stopToggleButton.setEnabled(false);
            playForwardToggleButton.setEnabled(false);
            animationSlider.setEnabled(false);
            magnifyButton.setEnabled(false);
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
        
        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Draw background
        paintBackground(g,bounds);

        // Draw cell and chromosomes
        int currentStep = fertilizationModel.getStep();
        
        // Draw cell boundaries.  Set font and color.
        g.setFont(getFont());
        g.setColor(getForeground());

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
                		tailx[i] = tailx[i-1]+10;
                	}
                	for (int i = 1;i<5;i++)
                	{
                		if (taily[i-1] == taily[0])
                			taily[i] = taily[i-1]+5;
                		else if (taily[i-1] > taily[0])
                			taily[i] = taily[i-1]-10;
                		else 
                			taily[i] = taily[i-1]+10;
                	}
                	
                	for(int i= 1;i<5;i++)
                	{
                		g.drawLine(tailx[i-1], taily[i-1],tailx[i],taily[i]);
                	}
                }
            }
            else if (boundary instanceof CellLine)
            {
                cellLine = (CellLine) boundary;
                g.drawLine(cellLine.getX1(), cellLine.getY1(),
                           cellLine.getX2(), cellLine.getY2());
            }
            else if (boundary instanceof FilledCircle)
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
        FertilizationChromosomeModel aFertilizationChromosomeModel;
        Enumeration eChromosomeModels;

        Vector chromosomeModelEnumerations = fertilizationModel.getChromosomeModelEnumerations();
        Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
        while (eChromosomeModelEnumerations.hasMoreElements())
        {
            eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            while (eChromosomeModels.hasMoreElements())
            {
                aFertilizationChromosomeModel = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
    
                xCentromere = (int) aFertilizationChromosomeModel.getXCentromereAtStep(currentStep);
                yCentromere = (int) aFertilizationChromosomeModel.getYCentromereAtStep(currentStep);
    
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
                            xPoints = aFertilizationChromosomeModel.getXPStrandPoints(currentStep);
                            yPoints = aFertilizationChromosomeModel.getYPStrandPoints(currentStep);
                            alleles = aFertilizationChromosomeModel.getPStrandAlleles();
                            colors = aFertilizationChromosomeModel.getPStrandColors();
                            break;
        
                        case 1:
                            xPoints = aFertilizationChromosomeModel.getXQStrandPoints(currentStep);
                            yPoints = aFertilizationChromosomeModel.getYQStrandPoints(currentStep);
                            alleles = aFertilizationChromosomeModel.getQStrandAlleles();
                            colors = aFertilizationChromosomeModel.getQStrandColors();
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
                                g.fillOval(xPoints[i]-radiusChromosome,yPoints[i]-radiusChromosome,
                                           diameterChromosome,diameterChromosome);
                            }
                            else
                            {
                                g.setColor(Color.red);
                                g.fillOval(xPoints[i]-radiusChromosome,yPoints[i]-radiusChromosome,
                                           diameterChromosome,diameterChromosome);
                            }
                        }
                    }
                }
    
                // Draw centromere after all strands drawn
                g.setColor(getForeground());
                g.fillOval(xCentromere-radiusCentromere,yCentromere-radiusCentromere,
                           diameterCentromere,diameterCentromere);
            }
        }
    }

    /**
     * Update the size of this view.  To do this, get the current size of the view
     * and update the models for anything shown in the view.  Do not repaint or generate
     * a repaint event, as that event is already coming automatically from AWT.
    **/
    public void updateSize(boolean forceUpdate)
    {
        Rectangle bounds = getBounds();

        if (forceUpdate || (bounds.width != actualWidth) || (bounds.height != actualHeight))
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
            }
            else if (actualWidth < 400 || actualHeight < 500)
            {
                radiusCentromere = 2;
                diameterCentromere = 4;
                radiusChromosome = 2;
                diameterChromosome = 4;
            }
            else if (actualWidth < 500 || actualHeight < 700)
            {
                radiusCentromere = 3;
                diameterCentromere = 6;
                radiusChromosome = 3;
                diameterChromosome = 6;
            }
            else
            {
                radiusCentromere = 4;
                diameterCentromere = 8;
                radiusChromosome = 4;
                diameterChromosome = 8;
            }
            // Update fertilization model and its chromosome models
            if (fertilizationModel != null)
            {
                fertilizationModel.setEnclosingViewRectangle(new Rectangle(0,0,actualWidth,actualHeight-28));
            }

            // Move slider and buttons appropriately
            magnifyButton.setBounds(2,actualHeight-28,26,26);
            stopToggleButton.setBounds(28,actualHeight-28,26,26);
            playForwardToggleButton.setBounds(54,actualHeight-28,26,26);
            animationSlider.setBounds(80,actualHeight-28,actualWidth-82,26);

            repaint();
        }
    }

    public void updateSize()
    {
        updateSize(false);
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
            stopToggleButton.setSelected(true);
            playForwardToggleButton.setSelected(false);

            repaint();
        }
    }

    /**
     * Play animation forward
    **/
    public void playAnimationForward()
    {
        if (currentPlayMode != PLAY_MODE_FORWARD)
        {
            currentPlayMode = PLAY_MODE_FORWARD;

            if (animationTimer.isRunning() == false)
            {
                animationTimer.start();
            }
    
            // Set toggle button state
            stopToggleButton.setSelected(false);
            playForwardToggleButton.setSelected(true);

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

    /*
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
        }

        // If step changed, tell fertilization model and repaint
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
            changes.firePropertyChange(UIProp.SMALL_FERTILIZATION_MAGNIFY_BUTTON_PUSHED,null,null);
        }
        else if (cmd.equals(cmdStop))
        {
            stopAnimation();
        }
        else if (cmd.equals(cmdPlayForward))
        {
            playAnimationForward();
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
    }
}

