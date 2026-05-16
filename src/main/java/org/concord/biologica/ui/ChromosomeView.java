//
// Class : ChromosomeView - the chromosome level view in the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.17 $
// $Date: 2003/08/15 17:53:58 $
// $Author: dima $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

/**
 * The chromosome view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.ORGANISM - the shown organism has changed from one organism to another
 * <li> org.concord.biologica.ui.UIProp.SELECTED_ALLELE - the selected allele has changed
 * <li> org.concord.biologica.ui.UIProp.SELECTED_CHROMOSOME - the selected chromosome has changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#ORGANISM
 * @see org.concord.biologica.ui.UIProp#SELECTED_ALLELE
 * @see org.concord.biologica.ui.UIProp#SELECTED_CHROMOSOME
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.17 $ $Date: 2003/08/15 17:53:58 $
 * @author 		$Author: dima $
**/
public final class ChromosomeView
extends UIView
implements ImageObserver, MouseListener, MouseMotionListener, PropertyChangeListener
{
    // Image widths and heights, etc.  Determined by image used to draw chromosomes.
    static public final int CHROMOSOME_IMAGE_WIDTH = 23;
    static public final int CHROMOSOME_IMAGE_HEIGHT = 95;
    static public final int X_YELLOW_CHROMOSOME = 0;
    static public final int X_BLUE_CHROMOSOME = 23;

    static public final int MIN_PREFERRED_HEIGHT = 400;

    // Used in drawing chromosome images
    static public final int Y_TOP_IMAGE = 20;
    static public final int Y_IMAGE_OFFSET = 30;
    static public final int X_LEFT_IMAGE = 20;
    static public final int X_RIGHT_IMAGE = 250;

    // Used to position chromosome allele combo boxes
    static public final int X_COMBO_BOX_OFFSET = 20;

    /**
     * Have images been preloaded?
    **/
    static private boolean imagesPreloaded = false;

    /**
     * Chromosome image
    **/
    static Image chromosomeImage = null;

    /**
     * Chromosome image loaded?
    **/
    static boolean chromosomeImageLoaded = false;

    /**
     * Lock image
    **/
    static Image lockImage = null;

    /**
     * Lock image loaded?
    **/
    static boolean lockImageLoaded = false;

    /**
     * Chromosome image values, used to draw genes and alleles on them
    **/
    static public final int CHROMOSOME_TOP_SECTION_TOP[] =       { 1, 1, 1, 1, 1, 1, 1, 1};
    static public final int CHROMOSOME_TOP_SECTION_BOTTOM[] =    {30,26,24,23,22,20,17,12};
    static public final int CHROMOSOME_BOTTOM_SECTION_TOP[] =    {42,38,36,35,34,32,29,24};
    static public final int CHROMOSOME_BOTTOM_SECTION_BOTTOM[] = {93,88,83,80,77,70,60,43};
    static public final int CHROMOSOME_TOP_LENGTH[] = 		  	 {29,25,23,22,21,19,16,11};
    static public final int CHROMOSOME_BOTTOM_LENGTH[] = 	   	 {51,50,47,45,43,38,31,19};
    static public final int CHROMOSOME_TOTAL_LENGTH[] = 		 {80,75,70,67,64,57,47,30};
    static public final float CHROMOSOME_MID_POINT[] =			 {(float)0.3,(float)0.3,(float)0.4,(float)0.4,
                                                                  (float)0.4,(float)0.4,(float)0.4,(float)0.5};
    /**
     * Default selection color
    **/
    static private Color defaultSelectionColor = Color.yellow;

    /**
     * Preferred width of pane
    **/
    private int preferredWidth = 500;

    /**
     * Preferred height of pane
    **/
    private int preferredHeight = 400;

    /**
     * Number of organism alleles
    **/
    private int numberOfOrganismAlleles = 0;

    /**
     * Number of organism chromosomes (not pairs!)
    **/
    private int numberOfOrganismChromosomes = 0;

    /**
     * Array of organism allele controls
    **/
    private OrganismAlleleControl organismAlleleControls[];

    /**
     * Indicates we're in the midst of updating state
    **/
    private boolean updatingState = false;

    /**
     * Shown organism, may be null.  When this changes, a property change
     * event of type UIProp.ORGANISM is fired.
    **/
    private Organism organism;

    /**
     * Selected organism chromosome, may be null.  When this changes, a
     * property change event of type UIProp.SELECTED_CHROMOSOME is fired.
    **/
    private OrganismChromosome selectedChromosome;
    private boolean chromosomesSelectable = false;

    /**
     * Selected organism allele, may be null.  When this changes, a
     * property change event of type UIProp.SELECTED_ALLELE is fired.
    **/
    private OrganismAllele selectedAllele;

    /**
     * Which chromosome pair(s) to show, must be one of the following:
     *
     *	  	IChromosome.SEX_CHROMOSOME 	- when the sex chromosome pair should be shown
     *		0 							- when all the chromosome pairs should be shown (default)
     *		number > 0					- when the chromosome pair with the given number should be shown
    **/
    private int chromosomesToShow;

    /**
     * Selection set for this view.
    **/
    private SelectionSet selectionSet = null;

    /**
     * Selection color for a particular view
    **/
    protected Color selectionColor;
    private  Branch []b;

    /**
     * Get the default selection color
     *
     * @return		Color - default selection color, never null
    **/
    public static Color getDefaultSelectionColor()
    {
        return defaultSelectionColor;
    }

    /**
     * Set the default selection color.  This will be used as the default
     * selection color for views created after calling this method.<p>
     *
     * @param		aColor Color - a default selection color, may not be null
     * @exception	IllegalArgumentException - input argument illegal (null)
    **/
    public static void setDefaultSelectionColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException("aColor null");
        }

        defaultSelectionColor = aColor;
    }

    /**
     * Creates a chromosome view.
    **/
    public ChromosomeView()
    {
        // Use default selection set initially
        selectionSet = SelectionSet.getDefaultSelectionSet();

        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);
        selectionColor = defaultSelectionColor;

        chromosomesToShow = 0;

        numberOfOrganismAlleles = 0;
        numberOfOrganismChromosomes = 0;
        organismAlleleControls = new OrganismAlleleControl[30];

        int i;
        for (i=0;i<organismAlleleControls.length;i++)
        {
            organismAlleleControls[i] = new OrganismAlleleControl(this);
            add(organismAlleleControls[i]);
        }

        organism = null;
        selectedChromosome = null;
        selectedAllele = null;

        // Turn on double buffering
        setDoubleBuffered(true);

        // Set layout manager to null
        setLayout(null);

        // Load images
        if (imagesPreloaded == false)
        {
            try
            {
                LocalImageIcon chromosomeImageIcon = new LocalImageIcon("org/concord/biologica/locked/gifs/chromosomes.gif");
                chromosomeImageIcon.waitForLoadImage();
                chromosomeImage = chromosomeImageIcon.getImage();
                LocalImageIcon lockImageIcon = new LocalImageIcon("org/concord/biologica/locked/gifs/lock.gif");
                lockImageIcon.waitForLoadImage();
                lockImage = lockImageIcon.getImage();
            }
            catch (Exception e)
            {
                chromosomeImage = null;
                lockImage = null;
                e.printStackTrace();
            }

            imagesPreloaded = true;
        }

        // Create combo boxes later, when we know how many to create, etc.

        // Listen for mouse clicks, but not mouse motion initially
        addMouseListener(this);
       
    }

    /**
     * Tell this view its scroll pane.
     *
     * @param		aScrollPane JScrollPane - the scroll pane containing this view
    **/
    public void setScrollPane(JScrollPane aScrollPane)
    {
        super.setScrollPane(aScrollPane);
        updateScrollBars();
    }

    /**
     * Update the scroll bar extents based on the number of organisms
    **/
    public void updateScrollBars()
    {
        // If this view isn't in a scroll pane or the preferred height and width
        // are already large enough, return immediately.
        if (scrollPane == null)
        {
            return;
        }

        // Update scrollbars
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        if (scrollBar != null)
        {
            scrollBar.setMaximum(preferredHeight);
            scrollBar.setUnitIncrement(30);
        }

        scrollBar = scrollPane.getHorizontalScrollBar();
        if (scrollBar != null)
        {
            scrollBar.setMaximum(preferredWidth);
            scrollBar.setUnitIncrement(30);
        }
    }
    
    public void setChromosomesSelectable(boolean selectable)
    {
        chromosomesSelectable = selectable;
    }
    
    public boolean isChromosomesSelectable(boolean selectable)
    {
        return chromosomesSelectable;
    }
    
    public void setSelectionSet(SelectionSet selectSet)
    {
        selectionSet = selectSet;
    }

    public SelectionSet getSelectionSet()
    {
        return selectionSet;
    }

    /**
     * Get the selection color
     *
     * @return		Color - selection color for this
    **/
    public Color getSelectionColor()
    {
        return selectionColor;
    }

    /**
     * Set the selection color.  If null is input, the color reverts
     * back to the default selection color.<p>
     *
     * @param		aColor Color - a new selection color, may be null
    **/
    public void setSelectionColor(Color aColor)
    {
        if (aColor == null)
        {
            selectionColor = defaultSelectionColor;
        }
        else
        {
            selectionColor = aColor;
        }

        repaint();
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
        // Draw chromosomes given the current organism
        preferredHeight = 0;
        if (organism != null &&
            organism.isDeleted() == false)
        {
            int xInView, xInImage;
            int yInView, yInImage;
            int imageNumber;
            String chromosomeName;
            Graphics g2;
            boolean locked = organism.isLocked();

            // Draw background
            Rectangle bounds = getBounds();
            paintBackground(g,bounds);

            // Make sure we have fontMetrics defined
            if (fontMetrics == null)
            {
                updateFont(g);
            }
    
            g.setFont(getFont());
            g.setColor(getForeground());

            g.drawString(organism.getName(),10,20);

            xInView = X_LEFT_IMAGE +50;
            yInView = Y_TOP_IMAGE + Y_IMAGE_OFFSET;

            // Loop drawing organism's chromosomes
            // Note that we assume the chromosomes are in the appropriate order in pairs
            OrganismChromosome anOrganismChromosome;
            Enumeration eOrganismChromosomes = organism.getChromosomes();
          
            int i = 0;
           
            while(eOrganismChromosomes.hasMoreElements() && i<b.length)
            {
         
            	OrganismAllele [] PLStrand = b[i].getPLStrand();
            	Color [] PLStrandColor = b[i].getPLStrandColor();
            	OrganismAllele [] PRStrand = b[i].getPRStrand();
            	Color [] PRStrandColor = b[i].getPRStrandColor();
            	OrganismAllele [] QLStrand = b[i].getQLStrand();
            	Color [] QLStrandColor = b[i].getQLStrandColor();
            	OrganismAllele [] QRStrand = b[i].getQRStrand();
            	Color [] QRStrandColor = b[i].getQRStrandColor();
            	
        
                anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
                if ((anOrganismChromosome == selectedChromosome) && (selectedAllele == null))
                {
                    Color color = g.getColor();
                    g.setColor(selectionColor);
                    //g.fillRect(0, yInView-20, X_RIGHT_IMAGE - CHROMOSOME_IMAGE_WIDTH, CHROMOSOME_IMAGE_HEIGHT+20);
                    g.fillRect(0, yInView-20, X_RIGHT_IMAGE - CHROMOSOME_IMAGE_WIDTH,anOrganismChromosome.getChromosomeViewHeight()+20);
                    g.setColor(color);
                }
                if (anOrganismChromosome.isVisible() &&
                    (chromosomesToShow == 0 ||
                     chromosomesToShow == anOrganismChromosome.getNumberType() ||
                     (chromosomesToShow == IChromosome.SEX_CHROMOSOME &&
                      anOrganismChromosome.isSexChromosome())))
                {
                	g.setColor(Color.black);
                    imageNumber = anOrganismChromosome.getImageNumber();
                    xInImage = 0;
                    yInImage = -(imageNumber * CHROMOSOME_IMAGE_HEIGHT);
                    /*g2 = g.create(xInView,yInView,
                                  CHROMOSOME_IMAGE_WIDTH,CHROMOSOME_IMAGE_HEIGHT);
                    g2.drawImage(chromosomeImage,xInImage,yInImage,this);
                    g2.dispose();*/
                    if (locked)
                    {
                        g.drawImage(lockImage,xInView-10,yInView-27,this);
                        g.drawString(anOrganismChromosome.toString(),xInView+10,yInView-10);
                    }
                    else
                    {
                        g.drawString(anOrganismChromosome.toString(),xInView-10,yInView-10);
                    }
                  
                    int xPoint = xInView;
                    int yPoint = yInView; 
                    
                    for(int j = PLStrand.length-1;j>=0;j--)
                    {
                  
                    	if (PLStrandColor[j].equals(Color.blue))
                    	{
                    		 g.setColor(new Color(200,250,255));
                    		
                    	}
                    	else
                    	{
                    		g.setColor(new Color(0,200,200));
                    	}
                    	//g.setColor(PLStrandColor[j]);
                    	
                    	
                    	 g.fillRect(xPoint,yPoint,20,2);
                    	
                         yPoint = yPoint +2;
                    }
                 
                    g.setColor(Color.black);
                    g.drawRoundRect(xInView-1,yInView-1,21,yPoint-yInView+1,4,4);
                    g.fillOval(xInView + 5,yPoint,10,6);
                    
                    yPoint = yPoint+6;
                    int yQLStart = yPoint;
                    for(int j = 0;j<QLStrand.length;j++)
                    {
                    	
                		if (QLStrandColor[j].equals(Color.blue))
                    	{
                    		g.setColor(new Color(200,255,255));
                    	}
                    	else
                    	{
                    		g.setColor(new Color(0,200,200));
                    	}
                    	//g.setColor(QLStrandColor[j]);
                    	
                	 	g.fillRect(xPoint,yPoint,20,2);
                	 	
                        yPoint = yPoint +2;
                    }
                 
                 	g.setColor(Color.black);
                    g.drawRoundRect(xInView-1,yQLStart-1,21,yPoint-yQLStart+1,4,4);
                    anOrganismChromosome.setChromosomeViewHeight(yPoint - yInView);
                }
                
               
                
                anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
                if ((anOrganismChromosome == selectedChromosome) && (selectedAllele == null))
                {
                    Color color = g.getColor();
                    g.setColor(selectionColor);
                   // g.fillRect(X_RIGHT_IMAGE - CHROMOSOME_IMAGE_WIDTH, yInView-20, X_RIGHT_IMAGE + CHROMOSOME_IMAGE_WIDTH, CHROMOSOME_IMAGE_HEIGHT+20);
                     g.fillRect(X_RIGHT_IMAGE - CHROMOSOME_IMAGE_WIDTH, yInView-20, X_RIGHT_IMAGE + CHROMOSOME_IMAGE_WIDTH,anOrganismChromosome.getChromosomeViewHeight()+20);
                    g.setColor(color);
                }
                if (anOrganismChromosome.isVisible() &&
                    (chromosomesToShow == 0 ||
                     chromosomesToShow == anOrganismChromosome.getNumberType() ||
                     (chromosomesToShow == IChromosome.SEX_CHROMOSOME &&
                      anOrganismChromosome.isSexChromosome())))
                {
                	
                	g.setColor(Color.black);
                    imageNumber = anOrganismChromosome.getImageNumber();
                    xInView = X_RIGHT_IMAGE+40;
                    xInImage = -CHROMOSOME_IMAGE_WIDTH;
                    yInImage = -(imageNumber * CHROMOSOME_IMAGE_HEIGHT);
                    /*g2 = g.create(xInView,yInView,
                                  CHROMOSOME_IMAGE_WIDTH,CHROMOSOME_IMAGE_HEIGHT);
                    g2.drawImage(chromosomeImage,xInImage,yInImage,this);
                    g2.dispose();*/
                    if (locked)
                    {
                        g.drawImage(lockImage,xInView-10,yInView-27,this);
                        g.drawString(anOrganismChromosome.toString(),xInView+10,yInView-10);
                    }
                    else
                    {
                        g.drawString(anOrganismChromosome.toString(),xInView-10,yInView-10);
                    }
                    
                    xInView = X_RIGHT_IMAGE+50;
                    int xPoint = xInView;
                    int yPoint = yInView; 
                    
                    for(int j = PRStrand.length-1;j>=0;j--)
                    {
                    	
                    	if (PRStrandColor[j].equals(Color.blue))
                    	{
                    		g.setColor(new Color(230,230,0));
                    	}
                    	else
                    	{
                    		g.setColor(new Color(255,255,150)); 
                    	}
                    	//g.setColor(PRStrandColor[j]);
                    	g.fillRect(xPoint,yPoint,20,2);
                    	yPoint = yPoint +2;
                    }
                 	
                    g.setColor(Color.black);
                    g.drawRoundRect(xInView-1,yInView-1,21,yPoint-yInView+1,4,4);
                    g.fillOval(xInView+5,yPoint,10,6);
                    
                    yPoint = yPoint+6;
                    int yQLStart = yPoint;
                    
                    for(int j =0 ;j<QRStrand.length;j++)
                    {
                    	if (QRStrandColor[j].equals(Color.blue))
                    	{
                    		g.setColor(new Color(230,230,0));
                    	}
                    	else
                    	{
                    		g.setColor(new Color(255,255,150));
                    	}
                    	//g.setColor(QRStrandColor[j]);
	                    g.fillRect(xPoint,yPoint,20,2);
	                    
	                    yPoint = yPoint +2;
                    	
                    }
                    g.setColor(Color.black);
                    g.drawRoundRect(xInView-1,yQLStart-1,21,yPoint-yQLStart+1,4,4);
                   
                    anOrganismChromosome.setChromosomeViewHeight(yPoint-yInView);

                    xInView = X_LEFT_IMAGE+50;
                    yInView = (yPoint + Y_IMAGE_OFFSET);
                    preferredHeight = yPoint;
                   
                }
                i = i+1;
        		
            }
            
            // Draw organism alleles on top of chromosome images
           
            int linesHeight = 0; 
            for (int j=0;j<numberOfOrganismAlleles;j++)
            {
            	
                linesHeight = Math.max(linesHeight,organismAlleleControls[j].paintLines(g,selectionColor));
            }
            preferredHeight = Math.max(linesHeight,preferredHeight);
            preferredHeight = preferredHeight +10;
            
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
     * Get the chromosomes to show.  The returned value will be one of the following:
     *
     * <ul>
     * <li>	IChromosome.SEX_CHROMOSOME 	- when the sex chromosome pair should be shown
     * <li>	0 							- when all the chromosome pairs should be shown (default)
     * <li>	number > 0					- when the chromosome pair with the given number should be shown
     * </ul>
     *
     * @return	chromosomesToShow int - the chromosomes to be shown (IChromosome.SEX_CHROMOSOME, 0 or positive integer)
    **/
    public int getChromosomesToShow()
    {
        return chromosomesToShow;
    }

    /**
     * Set the chromosomes to show.  The input value must be one of the following:
     *
     * <ul>
     * <li>	IChromosome.SEX_CHROMOSOME 	- when the sex chromosome pair should be shown
     * <li>	IChromosome.X_CHROMOSOME 	- when the sex chromosome pair should be shown
     * <li>	IChromosome.Y_CHROMOSOME 	- when the sex chromosome pair should be shown
     * <li>	0 							- when all the chromosome pairs should be shown (default)
     * <li>	number > 0					- when the chromosome pair with the given number should be shown
     * </ul>
     *
     * Note that this means you can pass exactly the result you get from calling the method
     * getNumberType() on a SpeciesChromosome or OrganismChromosome object into this function
     * and it will work properly.
     *
     * @param	aChromosomesToShow int - the chromosome pair to show
    **/
    public void setChromosomesToShow(int aChromosomesToShow)
    {
        // Return immediately if not a change
        if (aChromosomesToShow == chromosomesToShow)
        {
            return;
        }
        else if (chromosomesToShow == IChromosome.SEX_CHROMOSOME &&
                 (aChromosomesToShow == IChromosome.X_CHROMOSOME ||
                  aChromosomesToShow == IChromosome.Y_CHROMOSOME))
        {
            // Also no change
            return;
        }

        // Make change
        int oldChromosomesToShow = chromosomesToShow;
        if (aChromosomesToShow == IChromosome.SEX_CHROMOSOME ||
            aChromosomesToShow == IChromosome.X_CHROMOSOME ||
            aChromosomesToShow == IChromosome.Y_CHROMOSOME)
        {
            chromosomesToShow = IChromosome.SEX_CHROMOSOME;
        }
        else
        {
            chromosomesToShow = aChromosomesToShow;
        }

        // Update buttons, etc.
        updateState();

        // Notify listeners
        changes.firePropertyChange(UIProp.CHROMOSOMES_TO_SHOW,
                                   new Integer(oldChromosomesToShow),
                                   new Integer(chromosomesToShow));
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
            if (chromosomeImageLoaded == false && anImage == chromosomeImage)
            {
                chromosomeImageLoaded = true;
                repaint();
                return false;
            }
            else if (lockImageLoaded == false && anImage == lockImage)
            {
                lockImageLoaded = true;
                repaint();
                return false;
            }
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
        if (organism != null &&
            organism.isDeleted() == false)
        {
            // Get mouse coordinates
            int xMouse = event.getX();
            int yMouse = event.getY();
            
            // Deselect any existing selected allele
            OrganismAllele oldSelectedAllele = selectedAllele;
            if (selectedAllele != null)
            {
                selectionSet.deselectObject(selectedAllele);
                selectedAllele.removePropertyChangeListener(this);
                selectedAllele = null;
            }
    
            // Determine if mouse click was on an existing allele and select allele if it is
            int i;
            for (i=0;i<organismAlleleControls.length;i++)
            {
                if (organismAlleleControls[i] != null &&
                    organismAlleleControls[i].pick(xMouse,yMouse))
                {
                    selectedAllele = organismAlleleControls[i].getOrganismAllele();
                    if (selectedAllele.getGene().isVisible())
                    {
                        selectedAllele.addPropertyChangeListener(this);
                        selectionSet.selectObject(selectedAllele,true,false);
                        break;
                    }
                    else
                        selectedAllele = null;
                }
            }
            selectedChromosome = null;
            
            if (chromosomesSelectable)
            {
                int xInView = X_LEFT_IMAGE+50;
                int yInView = Y_TOP_IMAGE + Y_IMAGE_OFFSET;
                int high = CHROMOSOME_IMAGE_HEIGHT;
               
                // Note that we assume the chromosomes are in the appropriate order in pairs
                OrganismChromosome anOrganismChromosome;
                Enumeration eOrganismChromosomes = organism.getChromosomes();
                while(eOrganismChromosomes.hasMoreElements())
                {
                    anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
                    high = anOrganismChromosome.getChromosomeViewHeight();
                    Rectangle chromoRect = new Rectangle(xInView, yInView, CHROMOSOME_IMAGE_WIDTH, high);
                   
                    if (anOrganismChromosome.isVisible() &&
                        (chromosomesToShow == 0 ||
                         chromosomesToShow == anOrganismChromosome.getNumberType() ||
                         (chromosomesToShow == IChromosome.SEX_CHROMOSOME &&
                          anOrganismChromosome.isSexChromosome())))
                    {
                    	if (chromoRect.contains(xMouse, yMouse))
                        {
                        	
                            selectedChromosome = anOrganismChromosome;
                            changes.firePropertyChange(UIProp.SELECTED_CHROMOSOME,null,selectedChromosome);
                            break;
                        }
                    }
                    
                    anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
                    if (anOrganismChromosome.isVisible() &&
                        (chromosomesToShow == 0 ||
                         chromosomesToShow == anOrganismChromosome.getNumberType() ||
                         (chromosomesToShow == IChromosome.SEX_CHROMOSOME &&
                          anOrganismChromosome.isSexChromosome())))
                    {
                        xInView = X_RIGHT_IMAGE+50;
                        high = anOrganismChromosome.getChromosomeViewHeight();
                        chromoRect.x = xInView;
                        chromoRect.height = high;
                        if (chromoRect.contains(xMouse, yMouse))
                        {
                        	selectedChromosome = anOrganismChromosome;
                         
                            changes.firePropertyChange(UIProp.SELECTED_CHROMOSOME,null,selectedChromosome);
                            break;
                        }
                        xInView = X_LEFT_IMAGE+50;
                        //yInView += (CHROMOSOME_IMAGE_HEIGHT + Y_IMAGE_OFFSET);
                         yInView = yInView +high +Y_IMAGE_OFFSET;
                        chromoRect.x = xInView;
                        chromoRect.y = yInView;
                    }
                }
            }
            updateState();

            // Notify listeners if selected allele changed
            if (oldSelectedAllele != selectedAllele)
            {
                changes.firePropertyChange(UIProp.SELECTED_ALLELE,oldSelectedAllele,selectedAllele);
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
     * Get the shown organism for this view.
     *
     * @return		Organism - the shown organism in this view, may be null
    **/
    public Organism getOrganism()
    {
        return organism;
    }

    /**
     * Set the shown organism for this view.  This will result in the given
     * organism's chromosomes being shown in the view.  No particular chromosome
     * nor allele is selected initially.<p>
     *
     * @param		anOrganism Organism - the organism whose chromosomes to show, may be null
    **/
    public void setOrganism(Organism anOrganism)
    {
        // Return immediately if no change
        if (organism == anOrganism)
        {
            return;
        }

        Organism oldOrganism = organism;

        // Unsubscribe and null out references to old organism, chromosome and allele
        if (organism != null)
        {
            organism.removePropertyChangeListener(this);
            organism = null;
        }
        if (selectedChromosome != null)
        {
            selectedChromosome.removePropertyChangeListener(this);
            selectedChromosome = null;
        }
        if (selectedAllele != null)
        {
            selectionSet.deselectObject(selectedAllele);
            if (selectedAllele != null)
            {
                selectedAllele.removePropertyChangeListener(this);
                selectedAllele = null;
            }
        }

        // Set new organism
        if (anOrganism != null)
        {
            organism = anOrganism;
       
            organism.addPropertyChangeListener(this);
            b = organism.getChromosomePaintInfo();
      
          
        }
        

        // Update combo boxes, etc.
        updateState();
         

        // Update scroll bars
        updateScrollBars();

        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM,oldOrganism,organism);
  
    }

    /**
     * Get the selected chromosome.
     *
     * @return		OrganismChromosome - the currently selected chromosome, may be null
    **/
    public OrganismChromosome getSelectedChromosome()
    {
        return selectedChromosome;
    }

    /**
     * Set the selected chromosome.  This chromosome must be a chromosome of
     * the current organism.  If not, the call is ignored.<p>
     *
     * @param		aChromosome - an organism chromosome to select, may be null
    **/
    public void setSelectedChromosome(OrganismChromosome aChromosome)
    {
        // Return immediately if no change
        if (selectedChromosome == aChromosome)
        {
            return;
        }

        // If we have no organism or if this chromosome isn't a chromosome
        // on the current organism, ignore this call entirely
        if (organism == null ||
            (aChromosome != null && aChromosome.getOrganism() != organism))
        {
            return;
        }

        // Ok to make the change, save old selected chromosome
        OrganismChromosome oldSelectedChromosome = selectedChromosome;

        // Zero out existing state, including the allele as that's a substate of chromosome state
        if (selectedChromosome != null)
        {
            selectedChromosome.removePropertyChangeListener(this);
            selectedChromosome = null;
        }
        if (selectedAllele != null)
        {
            selectedAllele.removePropertyChangeListener(this);
            selectedAllele = null;
        }

        // Set new chromosome
        if (aChromosome != null)
        {
            selectedChromosome = aChromosome;
            selectedChromosome.addPropertyChangeListener(this);
        }

        // Update buttons, etc.
        updateState();

        // Notify listeners
        changes.firePropertyChange(UIProp.SELECTED_CHROMOSOME,oldSelectedChromosome,selectedChromosome);
    }

    /**
     * Get the selected allele.
     *
     * @return		OrganismAllele - the currently selected allele, may be null
    **/
    public OrganismAllele getSelectedAllele()
    {
        return selectedAllele;
    }

    /**
     * Set the selected allele.  The allele must be an allele of
     * the current selected chromosome  If not, the call is ignored.<p>
     *
     * @param		anAllele - an organism allele to select, may be null
    **/
    public void setSelectedAllele(OrganismAllele anAllele)
    {
        // Return immediately if no change
        if (selectedAllele == anAllele)
        {
            return;
        }

        // If we have no organism or no selected chromosome or if this allele
        // isn't an allele on the selected chromosome, ignore this call
        if (organism == null ||
            selectedChromosome == null ||
            (anAllele != null && anAllele.getOrganismChromosome() != selectedChromosome))
        {
            return;
        }

        // Ok to make the change, save old selected allele
        OrganismAllele oldSelectedAllele = selectedAllele;

        // Zero out existing state
        if (selectedAllele != null)
        {
            selectedAllele.removePropertyChangeListener(this);
            selectedAllele = null;
        }

        // Set new allele
        if (anAllele != null)
        {
            selectedAllele = anAllele;
            selectedAllele.addPropertyChangeListener(this);
        }

        // Update buttons, etc.
        updateState();

        // Notify listeners
        changes.firePropertyChange(UIProp.SELECTED_ALLELE,oldSelectedAllele,selectedAllele);
    }

    /**
     * Update our view state.  In this case, that means showing and hiding
     * allele value combo boxes, etc.
    **/
    public void updateState()
    {
        // Avoid recursive calls to this method
        if (updatingState == true)
        {
            return;
        }
        updatingState = true;

        int i;
        String currentValue = null;

        if ((organism != null) && (organism.isDeleted() == false))
        {
            // Determine the number of chromosomes, alleles, etc.
            numberOfOrganismAlleles = 0;
            numberOfOrganismChromosomes = 0;
            OrganismChromosome anOrganismChromosome;
            Enumeration eOrganismChromosomes = organism.getNonSexChromosomes();
            while (eOrganismChromosomes.hasMoreElements())
            {
                anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
                if (anOrganismChromosome.isVisible() &&
                    (chromosomesToShow == 0 ||
                     (chromosomesToShow == anOrganismChromosome.getNumberType())))
                {
                    numberOfOrganismChromosomes++;
                    numberOfOrganismAlleles += anOrganismChromosome.getNumberOfOrganismAlleles();
                }
            }

            eOrganismChromosomes = organism.getSexChromosomes();
            while (eOrganismChromosomes.hasMoreElements())
            {
                anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
                if (anOrganismChromosome.isVisible() &&
                    (chromosomesToShow == 0 ||
                     (chromosomesToShow == IChromosome.SEX_CHROMOSOME)))
                {
                    numberOfOrganismChromosomes++;
                    numberOfOrganismAlleles += anOrganismChromosome.getNumberOfOrganismAlleles();
                }
            }

            // Allocate a new array if current one not large enough
            int oldLength = organismAlleleControls.length;
            if (oldLength < numberOfOrganismAlleles)
            {
                OrganismAlleleControl newOrganismAlleleControls[] =
                        new OrganismAlleleControl[numberOfOrganismAlleles+6];

                // Copy non-null elements to new array, creating combo boxes if needed
                for (i=0;i<oldLength;i++)
                {
                    if (organismAlleleControls[i] != null)
                    {
                        newOrganismAlleleControls[i] = organismAlleleControls[i];
                    }
                    else
                    {
                        newOrganismAlleleControls[i] = new OrganismAlleleControl(this);
                    }
                }

                // Create new combo boxes in new section of array
                for (i=oldLength;i<(numberOfOrganismAlleles+6);i++)
                {
                    newOrganismAlleleControls[i] = new OrganismAlleleControl(this);
                    add(newOrganismAlleleControls[i]);
                }
                
                // Make new array the current array, letting old array be garbage collected
                organismAlleleControls = newOrganismAlleleControls;
            }

            // For each chromosome
            //	 For each organism allele on chromosome
            //		Determine where to draw red line on chromosome for gene
            //  	Create a combo box and position it appropriately
            //		Determine where to draw a line from gene to combo box
            boolean selected;
            int imageNumber;
            int xTopLeftChromosomeImage = X_LEFT_IMAGE;
            int yTopLeftChromosomeImage = Y_IMAGE_OFFSET;
            int yTopLeftComboBox = yTopLeftChromosomeImage;
            int iChromosome = 0;
            int iAlleleControl = 0;
            boolean didSexChromosomes = false;
            OrganismAllele anOrganismAllele;
            
            
            Enumeration eOrganismAlleles;
            eOrganismChromosomes = organism.getNonSexChromosomes();
           
            int deltaY = 0;
            
           // int newPreferredHeight = 0;
            while (eOrganismChromosomes.hasMoreElements())// &&iChromosome<4)
            {
            	
                anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
                OrganismAllele [] PLStrand = null;
            	OrganismAllele [] QLStrand = null;

                if (anOrganismChromosome.isVisible() &&
                   (chromosomesToShow == 0 ||
                	chromosomesToShow == anOrganismChromosome.getNumberType()||
                    (chromosomesToShow == IChromosome.SEX_CHROMOSOME &&
                    anOrganismChromosome.isSexChromosome())))
                {
                    imageNumber = anOrganismChromosome.getImageNumber();
    				
                    // Calculate positions
                    if ((iChromosome % 2) == 0)
                    {
                        // Left chromosome
                        xTopLeftChromosomeImage = X_LEFT_IMAGE+50-1;
                        yTopLeftChromosomeImage = Y_TOP_IMAGE + Y_IMAGE_OFFSET+deltaY;
                        PLStrand = b[iChromosome/2].getPLStrand();
                        QLStrand = b[iChromosome/2].getQLStrand();
                    }
                    else
                    {
                        // Right chromosome
                        xTopLeftChromosomeImage = X_RIGHT_IMAGE+50-1;
                        yTopLeftChromosomeImage = Y_TOP_IMAGE + Y_IMAGE_OFFSET+deltaY;
                        PLStrand = b[(iChromosome-1)/2].getPRStrand();
                        QLStrand = b[(iChromosome-1)/2].getQRStrand();
                    }
                    
                    //qing add something here
                    //yTopLeftChromosomeImage = Y_TOP_IMAGE + Y_IMAGE_OFFSET + ((iChromosome / 2) * (CHROMOSOME_IMAGE_HEIGHT + Y_IMAGE_OFFSET));
                    yTopLeftComboBox = yTopLeftChromosomeImage - 20;
                   
                    for(int j = PLStrand.length-1;j>=0;j--)
                    {
                    	
                    	 if (PLStrand[j] != null)
  						 {
  						 	//yTopLeftChromosomeImage = yTopLeftChromosomeImage;
  						 	anOrganismAllele = PLStrand[j];
  						 	
  						 	
  						 	selected = (anOrganismAllele == selectedAllele); 
  						 	yTopLeftComboBox += 25;
  						 	
                       		organismAlleleControls[iAlleleControl].setState(anOrganismAllele,
                                                                        imageNumber,
                                                                        xTopLeftChromosomeImage,
                                                                        yTopLeftChromosomeImage,
                                                                        yTopLeftComboBox,
                                                                        selected);   
                            iAlleleControl ++;   	 
  						 }
                    	  yTopLeftChromosomeImage = yTopLeftChromosomeImage +2;
                    	  
                    }
      
                    yTopLeftChromosomeImage = yTopLeftChromosomeImage +6;
                    for(int j = 0;j<QLStrand.length;j++)
                    {
                    	
                    	 if (QLStrand[j] != null)
  						 {
  						 	//yTopLeftChromosomeImage = yTopLeftChromosomeImage;
  						 	anOrganismAllele = QLStrand[j];
  						 	selected = (anOrganismAllele == selectedAllele); 
  						 	yTopLeftComboBox += 25;
                       		organismAlleleControls[iAlleleControl].setState(anOrganismAllele,
                                                                        imageNumber,
                                                                        xTopLeftChromosomeImage,
                                                                        yTopLeftChromosomeImage,
                                                                        yTopLeftComboBox,
                                                                        selected); 
                            iAlleleControl ++;     	 
  						 }
                    	  yTopLeftChromosomeImage = yTopLeftChromosomeImage +2;
                    	  
                    }
                   
                    if (iChromosome%2 != 0)
                    {
                    	deltaY = yTopLeftChromosomeImage - Y_TOP_IMAGE;
                    	
                    }
                    //yTopLeftComboBox = yTopLeftChromosomeImage - 20;
    
                    // Create allele combo boxes
                   /* eOrganismAlleles = anOrganismChromosome.getOrganismAlleles();
                    while (eOrganismAlleles.hasMoreElements())
                    {
                        anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
                        selected = (anOrganismAllele == selectedAllele);
                        yTopLeftComboBox += 25;
                        organismAlleleControls[iAlleleControl].setState(anOrganismAllele,
                                                                        imageNumber,
                                                                        xTopLeftChromosomeImage,
                                                                        yTopLeftChromosomeImage,
                                                                        yTopLeftComboBox,
                                                                        selected);
                        iAlleleControl++;
                    }*/
					
                    //iChromosome++;
                }
				 iChromosome++;
                // If we've run out of non-sex chromosomes, switch to sex chromosomes
                if ((eOrganismChromosomes.hasMoreElements() == false) && didSexChromosomes == false)
                {
                    eOrganismChromosomes = organism.getSexChromosomes();
                    didSexChromosomes = true;
                }
            }

            for (i=iAlleleControl;i<organismAlleleControls.length;i++)
            {
                organismAlleleControls[i].setState(null,0,0,0,0,false);
            }
        }
        else
        {
            // Turn off all allele controls
            for (i=0;i<organismAlleleControls.length;i++)
            {
                organismAlleleControls[i].setState(null,0,0,0,0,false);
            }
        }

        // Update the preferred height to reflect the number of chromosomes
        int newPreferredHeight = 0;
        if (numberOfOrganismChromosomes>6)
        {
         	newPreferredHeight = ((numberOfOrganismChromosomes+1) * (CHROMOSOME_IMAGE_HEIGHT + Y_IMAGE_OFFSET+30))/2;
        }
        else 
        {
        	newPreferredHeight = ((numberOfOrganismChromosomes+1) * (CHROMOSOME_IMAGE_HEIGHT + Y_IMAGE_OFFSET))/2;
        }
     	/*int newPreferredHeight = Y_IMAGE_OFFSET;
       	Enumeration allOrganismChromosomes = organism.getChromosomes();
       	while (allOrganismChromosomes.hasMoreElements())
       	{
       		 OrganismChromosome oneOrganismChromosome = (OrganismChromosome) allOrganismChromosomes.nextElement();
       		 if (oneOrganismChromosome.isVisible())
       		 {
       		 	newPreferredHeight = newPreferredHeight + oneOrganismChromosome.getChromosomeViewHeight()+Y_IMAGE_OFFSET;
       		 }
       	}
       	newPreferredHeight = newPreferredHeight/2;*/
        if (newPreferredHeight > preferredHeight)
        {
            preferredHeight = newPreferredHeight;
        }
        else if (newPreferredHeight < MIN_PREFERRED_HEIGHT)
        {
            preferredHeight = MIN_PREFERRED_HEIGHT;
        }

        // Switch telling other code that we're updating state
        updatingState = false;

        // Finally, queue a revalidate and repaint
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
        // Only care if we're currently showing an organism's chromosomes
        if (organism != null)
        {
            String propertyName = event.getPropertyName();

            if (propertyName.equals(EngineProp.ORGANISM_GENOTYPE_AND_PHENOTYPE) ||
                propertyName.equals(EngineProp.ORGANISM_GENOTYPE_AND_NOT_PHENOTYPE))
            {
                Object object = event.getSource();
                if (object instanceof Organism)
                {
                    Organism eventOrganism = (Organism) object;
                    if (eventOrganism == organism)
                    {
                        // Shown organism's genotype and phenotype changed
                        updateState();
                    }
                    else
                    {
                        // Something funny going on - remove this view as a property change listener on the organism
                        eventOrganism.removePropertyChangeListener(this);
                    }
                }
                else if (object instanceof EngineObject)
                {
                    // Something funny going on - remove this view as a property change listener on the engine object
                    EngineObject engineObject = (EngineObject) object;
                    engineObject.removePropertyChangeListener(this);
                }
            }
            else if (propertyName.equals(EngineProp.DELETED))
            {
                // Only handling deleted events on organisms.
                Object object = event.getSource();
                if (object instanceof Organism)
                {
                    Organism eventOrganism = (Organism) object;
                    if (eventOrganism == organism)
                    {
                        // Shown organism is being deleted, so set organism to null
                        setOrganism(null);
                    }
                    else
                    {
                        // Something funny going on - remove this view as a property change listener on the organism
                        eventOrganism.removePropertyChangeListener(this);
                    }
                }
                else if (object instanceof EngineObject)
                {
                    // Something funny going on - remove this view as a property change listener on the engine object
                    EngineObject engineObject = (EngineObject) object;
                    engineObject.removePropertyChangeListener(this);
                }
            }
        }
    }
	/**
	 * return the information of the allele which has been changed
	 **/
	 private String oldAllele, newAllele;
	 private OrganismChromosome currentChromosome;
	 
	 public void setOldAllele(String oldOne)
	 {
	 	oldAllele = oldOne;
	 }

	 public String getOldAllele()
	 {
	 	return oldAllele;
	 }
	 
	 public void setNewAllele(String newOne)
	 {
	 	newAllele = newOne;
	 }
	 
	 public String getNewAllele()
	 {
	 	return newAllele;
	 }

	 public void setCurrentChromosome(OrganismChromosome chr)
	 {
	 	currentChromosome = chr;
	 }

	 public OrganismChromosome getCurrentChromosome()
	 {
	 	return currentChromosome;
	 }
   
 	 /**
    * Handle combo box item changed events.
    **/
    public void organismAlleleControlChanged(OrganismAlleleControl control,
                                             OrganismAllele allele,
                                             String newValue)
    {
        // Return immediately if no organism is selected
        if (organism == null)
        {
            return;
        }

        // Ignore event if we're updating state
        if (updatingState == true)
        {
            return;
        }

        // Update allele and update genotype and phenotype of organism
        String oldValue = allele.getTextSymbol();
        if (oldValue.equals(newValue) == false)
        {
        	setOldAllele(oldValue);
        	setNewAllele(newValue);
        	setCurrentChromosome(allele.getOrganismChromosome());
            allele.setTextSymbol(newValue);
            organism.updateGenotypeAndPhenotype(true);

            // Finally, update the state of this view
            updateState();
        }
    }
}

class LocalImageIcon
extends ImageIcon
{
    public LocalImageIcon(String source)
    {
        super(PathStrings.getResource(source));
    }
    
    public void waitForLoadImage()
    {
        loadImage(getImage());
    }
}


