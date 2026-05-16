//
// Class : SmallMeiosisView - the small meiosis view used as the bottom right and bottom
//							  left subviews in the sex view of the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.13 $
// $Date: 2003/03/12 01:29:01 $
// $Author: dima $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;
import java.net.URL;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.concord.biologica.engine.*;
import org.concord.shared.multimedia.sound.CCAudioClip;
/**
 * This class represents a view which shows the sex cell of a parent in the sex view
 * and has controls for the user to conduct meiosis within this view on that sex cell.
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * <li> UIProp.MEIOSIS_FATHER_STEP - the meiosis step of this view changed as father view
 * <li> UIProp.MEIOSIS_MODEL - the meiosis model was changed
 * <li> UIProp.MEIOSIS_MOTHER_STEP - the meiosis step of this view changed as mother view
 * <li> UIProp.SMALL_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED - the magnifying button was pushed as mother view
 * <li> UIProp.SMALL_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED - the magnifying button was pushed as father view
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_FATHER_STEP
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_MODEL
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_MOTHER_STEP
 * @see org.concord.biologica.ui.UIProp#SMALL_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED
 * @see org.concord.biologica.ui.UIProp#SMALL_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.13 $ $Date: 2003/03/12 01:29:01 $
 * @author 		$Author: dima $
**/

public final class SmallMeiosisView
extends UIView
implements MouseListener, MouseMotionListener, ActionListener, ComponentListener, ChangeListener, PropertyChangeListener
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

    // Types of meiosis views
    static public final int EMPTY_VIEW					= 0;
    static public final int FATHER_VIEW					= 1;
    static public final int MOTHER_VIEW					= 2;

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
     * Current organism
    **/
    private Organism currentOrganism;

	/**
	 * auto selection's sound
	 **/
	 private CCAudioClip audioClip;
    /**
     * Is this small meiosis view for the mother or father?
     * Note that this does NOT imply the sex of parent, as some plants
     * don't have a sex (e.g. Arabadopsis).
     *
     * @see	org.concord.biologica.ui.SmallMeiosisView#FATHER_VIEW
     * @see	org.concord.biologica.ui.SmallMeiosisView#MOTHER_VIEW
    **/
    private int motherOrFatherView;

    /**
     * Sex of current organism.  If current organism is null, then
     * the sex of the previous organism.
    **/
    private int sex = Organism.NO_SEX;

    /**
     * Magnify button - used to magnify view to entire sex view
    **/
    private JButton magnifyButton = null;

    /**
     * Stop button - used to stop meiosis
    **/
    private JToggleButton stopToggleButton = null;

    /**
     * Play forward button - used to play meiosis forward at normal speed
    **/
    private JToggleButton playForwardToggleButton = null;

    /**
     * Meiosis model which maintains the state for meiosis animation.
    **/
    private MeiosisModel meiosisModel;
    
    /**
     * 4 Gametes
     */
     private Gamete [] arrGametes = new Gamete[4];

    /**
     * Slider showing the position of the meiosis
    **/
    private JSlider animationSlider;

    /**
     * Timer for animation
    **/
    private javax.swing.Timer animationTimer;
    
     /**
     * Timer for auto gamete selection
     */
    private javax.swing.Timer autoSelectTimer;
    
    /**
    * param for auto gamete selection
    */
    private int selectNum = 0;
    private int numOfTurns = 8;
    
    /*
    * gamete select model for Meiosis, default is manual select;
    */
    private int gameteSelectMode;

    /**
     * Creates a small meiosis view.
     *
     * @param	aMotherOrFatherView int - FATHER_VIEW or MOTHER_VIEW
    **/
    public SmallMeiosisView(int aMotherOrFatherView)
    {
        super();
        try{
       		 audioClip = new CCAudioClip("ccjar:biologica/ui/sound/pinhit.au");
       		//audioClip = new CCAudioClip("ped:///pinhit.wav");
        }
        catch (Throwable e){};
        if (aMotherOrFatherView != FATHER_VIEW && aMotherOrFatherView != MOTHER_VIEW)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
       

        motherOrFatherView = aMotherOrFatherView;

        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);

        currentOrganism = null;
        meiosisModel = null;
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

        animationSlider = new JSlider(JSlider.HORIZONTAL,0,100,0);
        animationSlider.addChangeListener(this);
        animationSlider.setBounds(2,2,286,26);
        animationSlider.setBackground(Color.lightGray);
        animationSlider.setVisible(true);
        animationSlider.setEnabled(true);
        animationSlider.setToolTipText("Move slider to control meiosis");
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
        
         //create autoSelection timer
        autoSelectTimer = new javax.swing.Timer(100,this);


        // Turn on double buffering
        setDoubleBuffered(true);

        // Listen for mouse clicks, but not mouse motion initially
        addMouseListener(this);

        // Listen for resize events
        addComponentListener(this);
    }

    /**
     * Get the motherOrFatherView type of this view.
    **/
    public int getMotherOrFatherView()
    {
        return motherOrFatherView;
    }

    /**
     * Set the current meiosis model to be shown in this view.
     *
     * @param		aModel MeiosisModel - the meiosis model to be shown in this view
    **/
    void setMeiosisModel(MeiosisModel aModel)
    {
        // Ignore redundant setting
        if (meiosisModel == aModel)
        {
            return;
        }

        // Stop the animation
        stopAnimation();

        // Make the change
        if (meiosisModel != null)
        {
            meiosisModel.removePropertyChangeListener(this);
        }

        MeiosisModel oldMeiosisModel = meiosisModel;
        meiosisModel = aModel;

        if (meiosisModel != null)
        {
            meiosisModel.addPropertyChangeListener(this);
        }

        // Update state
        updateState();

        // Notify listeners that the meiosis model has changed
        changes.firePropertyChange(UIProp.MEIOSIS_MODEL,oldMeiosisModel,meiosisModel);
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
                sex = Organism.NO_SEX;
                stopToggleButton.setEnabled(false);
                playForwardToggleButton.setEnabled(false);
                animationSlider.setEnabled(false);
            }
            else
            {
                sex = currentOrganism.getSex();
                stopToggleButton.setEnabled(true);
                playForwardToggleButton.setEnabled(true);
                animationSlider.setEnabled(true);
            }
        }
        else
        {
            currentOrganism = null;
            animationSlider.setValue(0);
            sex = Organism.NO_SEX;
            stopToggleButton.setEnabled(false);
            playForwardToggleButton.setEnabled(false);
            animationSlider.setEnabled(false);
        }

        actualHeight = 0;
        actualWidth = 0;

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
        // Return immediately if meiosisModel null
        if (meiosisModel == null)
        {
            return;
        }

        // Get bounds
        Rectangle bounds = getBounds();

        // Update size if not current
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
        int[] xPoints;
        int[] yPoints;
        Color[] colors;
        OrganismAllele[] alleles;
        MeiosisChromosomeModel aChromosomeModel;
        Enumeration eChromosomeModels;

        g.setFont(getFont());

        Vector chromosomeModelEnumerations = meiosisModel.getChromosomeModelEnumerations();
        Enumeration eChromosomeModelEnumerations = chromosomeModelEnumerations.elements();
        while (eChromosomeModelEnumerations.hasMoreElements())
        {
            eChromosomeModels = (Enumeration) eChromosomeModelEnumerations.nextElement();
            while (eChromosomeModels.hasMoreElements())
            {
                aChromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
    
                if (aChromosomeModel.isVisible())
                {
                    xCentromere = (int) aChromosomeModel.getXCentromereAtStep(currentStep);
                    yCentromere = (int) aChromosomeModel.getYCentromereAtStep(currentStep);
        
                    for (iStrand=0;iStrand<4;iStrand++)
                    {
                        xPoints = null;
                        yPoints = null;
                        alleles = null;
                        colors = null;
        
                        // Get strand locations
                        switch (iStrand)
                        {
                            case 0:
                                xPoints = aChromosomeModel.getXPStrandOnePoints(currentStep);
                                yPoints = aChromosomeModel.getYPStrandOnePoints(currentStep);
                                alleles = aChromosomeModel.getPStrandOneAlleles();
                                colors = aChromosomeModel.getPStrandOneColors();
                                break;
        
                            case 1:
                                if (replicated)
                                {
                                    xPoints = aChromosomeModel.getXPStrandTwoPoints(currentStep);
                                    yPoints = aChromosomeModel.getYPStrandTwoPoints(currentStep);
                                    alleles = aChromosomeModel.getPStrandTwoAlleles();
                                    colors = aChromosomeModel.getPStrandTwoColors();
                                }
                                break;
        
                            case 2:
                                xPoints = aChromosomeModel.getXQStrandOnePoints(currentStep);
                                yPoints = aChromosomeModel.getYQStrandOnePoints(currentStep);
                                alleles = aChromosomeModel.getQStrandOneAlleles();
                                colors = aChromosomeModel.getQStrandOneColors();
                                break;
        
                            case 3:
                                if (replicated)
                                {
                                    xPoints = aChromosomeModel.getXQStrandTwoPoints(currentStep);
                                    yPoints = aChromosomeModel.getYQStrandTwoPoints(currentStep);
                                    alleles = aChromosomeModel.getQStrandTwoAlleles();
                                    colors = aChromosomeModel.getQStrandTwoColors();
                                }
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

        // Draw cell boundaries and spindles
        g.setColor(getForeground());

        Object boundary;
        CellArc cellArc;
        CellLine cellLine;
        SpindleLine spindleLine;
        Vector cellBoundaries = meiosisModel.getCellBoundaries();
        Enumeration eCellBoundaries = cellBoundaries.elements();
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
                    createGametes((CellArc) boundary);
                 
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
    }
    
    private int getTypeFromCellArc(CellArc cellArc)
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
    
    /**
    * create Gametes
    */
     protected void createGametes(CellArc boundary)
    {
        int type = getTypeFromCellArc(boundary);
        Vector models = getGameteChromosomeModels(type);
        arrGametes[type-4]= new Gamete();
        arrGametes[type-4].setTypeOfGamete(type);
        for (int i = 0; i <models.size() ; i++)
        {
            MeiosisChromosomeModel model = (MeiosisChromosomeModel) models.elementAt(i);
          //  Branch temp = null;
            
         
            for (int iStrand = 0; iStrand < 2; iStrand++)
            {
                OrganismAllele [] alleles = null;
            
                if (iStrand < 1)
                {
                    alleles = model.getPStrandOneAlleles();                 
                }
                else
                {
                    alleles = model.getQStrandOneAlleles();
                }
                for (int j = 0; j < alleles.length; j++)
                {
                    if (alleles[j] == null)
                        continue;
                    if (alleles[j].getGene().isVisible())
                    {
        
                        String text = alleles[j].getTextSymbol();
      
                       	arrGametes[type-4].setAlleleInfor(alleles[j]);
                       	arrGametes[type-4].setAllelesString(text);
 
                    }
                }
            
            }
        }
        meiosisModel.setGametes(arrGametes);
    }
    
    private Vector getGameteChromosomeModels(int modelType)
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

            // Update meiosis model and its chromosome models
            if (meiosisModel != null)
            {
                meiosisModel.setEnclosingViewRectangle(new Rectangle(0,0,actualWidth,actualHeight-28));
            }

            // Move slider and buttons appropriately
            magnifyButton.setBounds(2,actualHeight-28,26,26);
            stopToggleButton.setBounds(28,actualHeight-28,26,26);
            playForwardToggleButton.setBounds(54,actualHeight-28,26,26);
            animationSlider.setBounds(80,actualHeight-28,actualWidth-82,26);

            repaint();
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
        if (meiosisModel != null &&
            meiosisModel.getStep() > 94)
        {
        	if (gameteSelectMode == SexView.MANUAL_GAMETE_SELECT)
            	meiosisModel.selectGamete(event.getX(),event.getY());
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
     * @param		aStep int - a step (0 to 100)
    **/
    public void goToAnimationStep(int aStep)
    {
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

        repaint();
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

        int step = meiosisModel.getStep();
        int origStep = step;

        switch(currentPlayMode)
        {
            case PLAY_MODE_STOPPED:
                System.err.println("animation timer click received when stopped");
                break;

            case PLAY_MODE_FORWARD:
                if (step < 100)
                {
                    step += 1;
                }
                else
                {
                    step = 100;
                    stopAnimation();
                  
                }
                break;
        }

        // If step changed, tell meiosis model and repaint
        if (origStep != step)
        {
            meiosisModel.setStep(step);
            animationSlider.setValue(step);
            repaint();
        }
    }
    /**
    * start auto select gamete
    **/
    public void startAutoSelectGamete()
    {
    	selectNum = 0;
    	numOfTurns =8 + (int)(Math.round(10*Math.random()));
    	
    	if (gameteSelectMode == SexView.AUTO_GAMETE_SELECT)
		{
			 if (autoSelectTimer.isRunning())
			 	autoSelectTimer.stop();
			 
			 if (meiosisModel.getStep()==100)
			 {
			 	 magnifyButton.setEnabled(false);
			 	 autoSelectTimer.setDelay(100);
	 			 autoSelectTimer.start();	
     		 }
		}
	}

	public boolean isAutoSelectGameteFinished()
	{
		boolean bln = false;
		if ((!autoSelectTimer.isRunning()) && (meiosisModel.getStep()==100))
		{
			bln = true;
		}
		
		return bln;
	}
    /**
    * set select gamete model 
    */
    public void setGameteSelectMode(int i)
    {
    	gameteSelectMode = i; 
    }
    
    public int getGameteSelectMode()
    {
    	return gameteSelectMode;
    }

    /**
     * React to actions
    **/
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
         if (e.getSource() == autoSelectTimer)
        {
        	meiosisModel.selectGamete(selectNum);
        	selectNum +=1;
        	this.repaint();
        	//Toolkit.getDefaultToolkit().beep();
        	//audioClip.stop();
        	SwingUtilities.invokeLater(new Runnable(){
        		public void run(){
        			audioClip.play();
        		}
        	});
        	double a = 600.0/(Math.pow((double)numOfTurns,6.0));
        	
        	autoSelectTimer.setDelay((int)(100.0+a*(Math.pow((double)selectNum,6.0))));
			
        	if (selectNum == numOfTurns)
        	{
	    		autoSelectTimer.stop();
        		//magnifyButton.setEnabled(true);
        		selectNum = 0;
        		numOfTurns = 8;
        		autoSelectTimer.setDelay(100);
        	}
        	
        }
        else if (e.getSource() == animationTimer)
        {
            onAnimationTimerClick();
        }
        else if (cmd.equals(cmdMagnifyView))
        {
            stopAnimation();
            // Notify listeners
            if (motherOrFatherView == FATHER_VIEW)
            {
                changes.firePropertyChange(UIProp.SMALL_FATHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED,null,null);
            }
            else
            {
                changes.firePropertyChange(UIProp.SMALL_MOTHER_MEIOSIS_MAGNIFY_BUTTON_PUSHED,null,null);
            }
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
            updateState();

            if (motherOrFatherView == FATHER_VIEW)
            {
                changes.firePropertyChange(UIProp.MEIOSIS_FATHER_STEP,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
            else
            {
                changes.firePropertyChange(UIProp.MEIOSIS_MOTHER_STEP,
                                           event.getOldValue(),
                                           event.getNewValue());
            }
        }
    }
}

