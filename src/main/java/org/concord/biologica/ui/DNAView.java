//
// Class : DNAView - the DNA level view in the BioLogica user interface
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2001/06/12 15:30:04 $
// $Author: ed $
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

/**
 * The chromosome view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.APPLY_BUTTON_PUSHED - apply button was pushed
 * <li> org.concord.biologica.ui.UIProp.DEFER_APPLY - the defer apply flag changed
 * <li> org.concord.biologica.ui.UIProp.DEFER_REVERT - the defer revert flag changed
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_ALLELE - the shown organism allele has changed from one organism allele to another
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_ALLELE_PAIR - the shown organism allele pair has changed from one organism allele pair to another
 * <li> org.concord.biologica.ui.UIProp.REVERT_BUTTON_PUSHED - revert button was pushed
 * <li> org.concord.biologica.ui.UIProp.SELECTED_BASES - the selected bases have changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#APPLY_BUTTON_PUSHED
 * @see org.concord.biologica.ui.UIProp#DEFER_APPLY
 * @see org.concord.biologica.ui.UIProp#DEFER_REVERT
 * @see org.concord.biologica.ui.UIProp#ORGANISM_ALLELE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_ALLELE_PAIR
 * @see org.concord.biologica.ui.UIProp#REVERT_BUTTON_PUSHED
 * @see org.concord.biologica.ui.UIProp#SELECTED_BASES
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2001/06/12 15:30:04 $
 * @author 		$Author: ed $
**/
public final class DNAView
extends UIView
implements ImageObserver, MouseListener, MouseMotionListener, PropertyChangeListener,
AdjustmentListener, ChangeListener, ActionListener, KeyListener
{
    /**
     * Commands - must be unique
    **/
    static private final String cmdApply		= "cmdApply";
    static private final String cmdRevert		= "cmdRevert";

    static public final String DNA_CMD_APPLY		= cmdApply;
    static public final String DNA_CMD_REVERT		= cmdRevert;

    /**
     * Button locations
    **/
    static private final int REVERT_BUTTON_LEFT     = 120;
    static private final int APPLY_BUTTON_LEFT      = 305;
    static private final int BUTTON_SINGLE_TOP      = 120;
    static private final int BUTTON_DOUBLE_TOP      = 195;
    static private final int BUTTON_WIDTH           = 105;
    static private final int BUTTON_HEIGHT          = 26;

    /**
     * DNA area static values, used in drawing and picking on the DNA area
    **/
    static public final int DNA_AREA_LEFT           = 50;
    static public final int DNA_AREA_TOP            = 40;
    static public final int DNA_AREA_WIDTH          = 420;
    static public final int DNA_AREA_SINGLE_HEIGHT  = 42;
    static public final int DNA_AREA_DOUBLE_HEIGHT  = 114;
    static public final int DNA_AREA_GAP            = 30;	// DNA_AREA_DOUBLE_HEIGHT - (2*DNA_AREA_SINGLE_HEIGHT)

    /**
     * DNA image values, used to draw base pairs
    **/
    static public final int DNA_IMAGE_HEIGHT                    = 42;
    static public final int DNA_IMAGE_WIDTH                     = 14;
    static public final int DNA_IMAGE_MAX_BASES                 = 30;

    static public final int DNA_IMAGE_ARROWS_INDEX              = 0;
    static public final int DNA_IMAGE_GC_DARK_INDEX             = 1;
    static public final int DNA_IMAGE_CG_DARK_INDEX             = 2;
    static public final int DNA_IMAGE_TA_DARK_INDEX             = 3;
    static public final int DNA_IMAGE_AT_DARK_INDEX             = 4;
    static public final int DNA_IMAGE_GC_LIGHT_INDEX            = 5;
    static public final int DNA_IMAGE_CG_LIGHT_INDEX            = 6;
    static public final int DNA_IMAGE_TA_LIGHT_INDEX            = 7;
    static public final int DNA_IMAGE_AT_LIGHT_INDEX            = 8;
    static public final int DNA_IMAGE_GC_DARK_SELECTED_INDEX    = 9;
    static public final int DNA_IMAGE_CG_DARK_SELECTED_INDEX    = 10;
    static public final int DNA_IMAGE_TA_DARK_SELECTED_INDEX    = 11;
    static public final int DNA_IMAGE_AT_DARK_SELECTED_INDEX    = 12;

    static public final int DNA_TOP_STRAND_Y                    = 5;
    static public final int DNA_BOTTOM_STRAND_Y                 = 22;
    static public final int DNA_CURSOR_HEIGHT                   = 15;

    /**
     * Scrollbar values
    **/
    static public final int DNA_SCROLLBAR_HEIGHT                = 18;
    static public final int DNA_SCROLLBAR_AREA_OFFSET           = 10;

    static public final int DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES = 20;

    /**
     * Strand values
    **/
    static public final int DNA_TOP_STRAND                      = 1;
    static public final int DNA_BOTTOM_STRAND                   = 2;

    /**
     * Top or bottom allele?
    **/
    static public final int TOP_ALLELE                          = 1;
    static public final int BOTTOM_ALLELE                       = 2;

    /**
     * Have images been preloaded?
    **/
    static private boolean imagesPreloaded = false;

    /**
     * DNA image
    **/
    static Image dnaImage = null;

    /**
     * DNA image loaded?
    **/
    static boolean dnaImageLoaded = false;

    /**
     * Default cursor color
    **/
    static private Color defaultCursorColor = Color.white;

    /**
     * Default equal base color
    **/
    static private Color defaultEqualBaseColor = new Color(51,153,255);

    /**
     * Default different base color
    **/
    static private Color defaultDifferentBaseColor = new Color(250,0,0);

    /**
     * Preferred width of pane
    **/
    private int preferredWidth = 500;

    /**
     * Preferred height of pane
    **/
    private int preferredHeight = 400;

    /**
     * Organism DNA visible
    **/
    private boolean organismDNAVisible = false;

    /**
     * Organism DNA alterable
    **/
    private boolean organismDNAAlterable = true;

    /**
     * Organism allele strand - top or bottom?
    **/
    private int organismAlleleStrand;

    /**
     * Shown organism allele pair.
     * This, organismAllele or both must be null.
    **/
    private OrganismAllelePair organismAllelePair;

    /**
     * Shown organism allele.
     * This, organismAllelePair or both must be null.
    **/
    private OrganismAllele organismAllele;

    /**
     * Organism allele start index in larger chromosome
    **/
    private int organismAlleleStartIndex;
    
    /**
     * Organism of allele or allele pair
    **/
    private Organism alleleOrganism;

    /**
     * Index of leftmost base pair.  This changes as user scrolls.  -1 if no organismAllele in view.
    **/
    private int leftmostBasePairIndex;

    /**
     * Selection start index. -1 if no selection.  It is not
     * guaranteed that the end index will be greater than
     * the start index.
    **/
    private int selectionStartIndex;

    /**
     * Selection end index. -1 if no selection.  It is not
     * guaranteed that the end index will be greater than
     * the start index.
    **/
    private int selectionEndIndex;

    /**
     * Listening for mouse motion
    **/
    private boolean listeningForMouseMotion;

    /**
     * Cursor index.  The index is by base pair on the ** allele **!!,
     * not the chromosome.  It's done this way because the user can
     * only select within the allele, not in the garbage base pairs.
     * But it is possible for the cursor to be scrolled off the side
     * if the allele is scrolled off the side.  -1 if no cursor.
    **/
    private int cursorIndex;

    /**
     * Timer for cursor animation
    **/
    private javax.swing.Timer cursorTimer;

    /**
     * Cursor visible?  This switches back and forth
     * with timer events, making the cursor flash.
    **/
    private boolean cursorVisible;

    /**
     * Cursor allele
    **/
    private OrganismAllele cursorAllele;

    /**
     * Cursor strand - top or bottom?
     *
     * @see		org.concord.biologica.ui.DNAView#DNA_TOP_STRAND
     * @see		org.concord.biologica.ui.DNAView#DNA_BOTTOM_STRAND
    **/
    private int cursorStrand;

    /**
     * Is the shift key down?
    **/
    private boolean shiftDown;

    /**
     * Scroll bar
    **/
    private JScrollBar scrollBar;

    /**
     * Cursor color
    **/
    protected Color cursorColor;

    /**
     * Equal base color
    **/
    protected Color equalBaseColor;

    /**
     * Different base color
    **/
    protected Color differentBaseColor;

    /**
     * Revert button
    **/
    private JButton revertButton = null;

    /**
     * Apply button
    **/
    private JButton applyButton = null;

    /**
     * Original DNA of top allele
    **/
    private byte[] originalTopBases = null;

    /**
     * Original DNA of bottom allele
    **/
    private byte[] originalBottomBases = null;

    /**
     * Vector for listeners on revert and apply buttons
    **/
    private Vector actionListeners = new Vector();
    
    /**
     * Defer the apply when the user pushes the apply button?
    **/
    private boolean deferApply = false;

    /**
     * Defer the revert when the user pushes the revert button?
    **/
    private boolean deferRevert = false;

    /**
     * Get the default cursor color
     *
     * @return		Color - default cursor color, never null
    **/
    public static Color getDefaultCursorColor()
    {
        return defaultCursorColor;
    }

    /**
     * Set the default cursor color.  This will be used as the default
     * cursor color for views created after calling this method.<p>
     *
     * @param		aColor Color - a default cursor color, may not be null
     * @exception	IllegalArgumentException - input argument illegal (null)
    **/
    public static void setDefaultCursorColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException("aColor null");
        }

        defaultCursorColor = aColor;
    }

    /**
     * Get the default equal base color
     *
     * @return		Color - default equal base color, never null
    **/
    public static Color getDefaultEqualBaseColor()
    {
        return defaultEqualBaseColor;
    }

    /**
     * Set the default equal base color.  This will be used as the default
     * equal base color for views created after calling this method.<p>
     *
     * @param		aColor Color - a default equal base color, may not be null
     * @exception	IllegalArgumentException - input argument illegal (null)
    **/
    public static void setDefaultEqualBaseColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException("aColor null");
        }

        defaultEqualBaseColor = aColor;
    }

    /**
     * Get the default different base color
     *
     * @return		Color - default different base color, never null
    **/
    public static Color getDefaultDifferentBaseColor()
    {
        return defaultDifferentBaseColor;
    }

    /**
     * Set the default different base color.  This will be used as the default
     * different base color for views created after calling this method.<p>
     *
     * @param		aColor Color - a default different base color, may not be null
     * @exception	IllegalArgumentException - input argument illegal (null)
    **/
    public static void setDefaultDifferentBaseColor(Color aColor)
    {
        if (aColor == null)
        {
            throw new IllegalArgumentException("aColor null");
        }

        defaultDifferentBaseColor = aColor;
    }

    /**
     * Creates a chromosome view.
    **/
    public DNAView()
    {
        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);
        cursorColor = defaultCursorColor;
        equalBaseColor = defaultEqualBaseColor;
        differentBaseColor = defaultDifferentBaseColor;

        organismAllelePair = null;
        organismAllele = null;
        organismAlleleStrand = Gene.TOP_STRAND;
        organismAlleleStartIndex = -1;
        leftmostBasePairIndex = -1;
        selectionStartIndex = -1;
        selectionEndIndex = -1;
        listeningForMouseMotion = false;
        cursorIndex = -1;
        cursorTimer = new javax.swing.Timer(500,this);
        cursorVisible = false;
        cursorAllele = null;
        cursorStrand = DNA_TOP_STRAND;
        shiftDown = false;

        // Turn on double buffering
        setDoubleBuffered(true);

        // Set layout manager to null
        setLayout(null);

        // Enable focus
        setRequestFocusEnabled(true);

        // Set up handler for key events
        shiftDown = false;
        addKeyListener(this);

        // Create scrollbar
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL,0,20,0,100);
        scrollBar.addAdjustmentListener(this);
        BoundedRangeModel model = scrollBar.getModel();
        model.addChangeListener(this);
        add(scrollBar);
        scrollBar.setVisible(false);

        // Create apply and revert buttons
        Insets insets = new Insets(2,2,2,2);

        applyButton = new JButton("Apply");
        applyButton.addActionListener(this);
        applyButton.setActionCommand(cmdApply);
        applyButton.setFocusPainted(false);
        applyButton.setBounds(250,8,90,24);
        applyButton.setBackground(Color.lightGray);
        applyButton.setVisible(false);
        applyButton.setEnabled(false);
        applyButton.setToolTipText("Apply DNA Changes");
        add(applyButton);

        revertButton = new JButton("Revert");
        revertButton.addActionListener(this);
        revertButton.setActionCommand(cmdRevert);
        revertButton.setFocusPainted(false);
        revertButton.setBounds(360,8,90,24);
        revertButton.setBackground(Color.lightGray);
        revertButton.setVisible(false);
        revertButton.setEnabled(false);
        revertButton.setToolTipText("Revert DNA Changes");
        add(revertButton);

        // Load images
        if (imagesPreloaded == false)
        {
            try
            {
                dnaImage = getLocalImage("org/concord/biologica/locked/gifs/dna.gif").getImage();
            }
            catch (Exception e)
            {
                dnaImage = null;
                e.printStackTrace();
            }

            imagesPreloaded = true;
        }

        // Listen for mouse clicks, but not mouse motion initially
        addMouseListener(this);
    }

    /**
     * Get the cursor color
     *
     * @return		Color - cursor color for this
    **/
    public Color getCursorColor()
    {
        return cursorColor;
    }

    /**
     * Set the cursor color.  If null is input, the color reverts
     * back to the default cursor color.<p>
     *
     * @param		aColor Color - a new cursor color, may be null
    **/
    public void setCursorColor(Color aColor)
    {
        if (aColor == null)
        {
            cursorColor = defaultCursorColor;
        }
        else
        {
            cursorColor = aColor;
        }

        repaint();
    }

    /**
     * Get the equal base color
     *
     * @return		Color - equal base color for this view
    **/
    public Color getEqualBaseColor()
    {
        return equalBaseColor;
    }

    /**
     * Set the equal base color.  If null is input, the color reverts
     * back to the default equal base color.<p>
     *
     * @param		aColor Color - a new equal base color, may be null
    **/
    public void setEqualBaseColor(Color aColor)
    {
        if (aColor == null)
        {
            equalBaseColor = defaultEqualBaseColor;
        }
        else
        {
            equalBaseColor = aColor;
        }

        repaint();
    }

    /**
     * Get the different base color
     *
     * @return		Color - different base color for this view
    **/
    public Color getDifferentBaseColor()
    {
        return differentBaseColor;
    }

    /**
     * Set the different base color.  If null is input, the color reverts
     * back to the default different base color.<p>
     *
     * @param		aColor Color - a new different base color, may be null
    **/
    public void setDifferentBaseColor(Color aColor)
    {
        if (aColor == null)
        {
            differentBaseColor = defaultDifferentBaseColor;
        }
        else
        {
            differentBaseColor = aColor;
        }

        repaint();
    }

    /**
     * Get the defer apply setting.
     *
     * @return		boolean - defer apply setting
    **/
    public boolean getDeferApply()
    {
        return deferApply;
    }

    /**
     * Set the defer apply setting.  If true, then the apply will not
     * happen when the user pushes on the apply button.  Instead, only
     * notification that the user pushed the button will be done and
     * it is up to a listener to apply the pending changes (or revert
     * them or do nothing with them).
     *
     * @param		trueOrFalse boolean - true or false
    **/
    public void setDeferApply(boolean trueOrFalse)
    {
        if (trueOrFalse == deferApply)
        {
            return;
        }

        boolean oldDeferApply = deferApply;
        deferApply = trueOrFalse;

        changes.firePropertyChange(UIProp.DEFER_APPLY,
                                   new Boolean(oldDeferApply),
                                   new Boolean(deferApply));
    }

    /**
     * Get the defer revert setting.
     *
     * @return		boolean - defer revert setting
    **/
    public boolean getDeferRevert()
    {
        return deferRevert;
    }

    /**
     * Set the defer revert setting.  If true, then the revert will not
     * happen when the user pushes on the revert button.  Instead, only
     * notification that the user pushed the revert button will be done and
     * it is up to a listener to revert the pending changes (or apply
     * them or do nothing with them).
     *
     * @param		trueOrFalse boolean - true or false
    **/
    public void setDeferRevert(boolean trueOrFalse)
    {
        if (trueOrFalse == deferRevert)
        {
            return;
        }

        boolean oldDeferRevert = deferRevert;
        deferRevert = trueOrFalse;

        changes.firePropertyChange(UIProp.DEFER_REVERT,
                                   new Boolean(oldDeferRevert),
                                   new Boolean(deferRevert));
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
        int xInView, yInView;

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

        // Draw allele(s)
        if (organismAllele != null &&
            organismAllele.isDeleted() == false)
        {
            g.drawString(organismAllele.toString(),10,20);

            xInView = DNA_AREA_LEFT - DNA_IMAGE_WIDTH;
            yInView = DNA_AREA_TOP;

            if (organismDNAVisible)
            {
                paintAllele(g,organismAllele,null,xInView,yInView);
            }
            else
            {
                g.drawString("DNA invisible",20,40);
            }
        }
        else if (organismAllelePair != null &&
                 organismAllelePair.isDeleted() == false)
        {
            OrganismAllele pairAllele1 = organismAllelePair.getFirstOrganismAllele();
            OrganismAllele pairAllele2 = organismAllelePair.getSecondOrganismAllele();

            if (pairAllele1 != null && pairAllele2 != null)
            {
                g.drawString("Organism Allele Pair: " + pairAllele1.getTextSymbol() + " and " + pairAllele2.getTextSymbol(),10,20);
            }
            else if (pairAllele1 != null)
            {
                g.drawString("Organism Allele Pair: " + pairAllele1.getTextSymbol(),10,20);
            }

            if (organismDNAVisible)
            {
                // Paint alleles DNA
                if (pairAllele1 != null)
                {
                    xInView = DNA_AREA_LEFT - DNA_IMAGE_WIDTH;

                    yInView = DNA_AREA_TOP;
                    paintAllele(g,pairAllele1,pairAllele2,xInView,yInView);

                    if (pairAllele2 != null)
                    {
                        yInView = DNA_AREA_TOP + DNA_IMAGE_HEIGHT + DNA_AREA_GAP;
                        paintAllele(g,pairAllele2,pairAllele1,xInView,yInView);
                    }
                }
            }
            else
            {
                g.drawString("DNA invisible",20,40);
            }
        }
    }

    /**
     * Paint a single allele
    **/
    public void paintAllele(Graphics g, OrganismAllele allele, OrganismAllele comparisonAllele, int xTopLeft, int yTopLeft)
    {
        int i;
        byte base;
        boolean textDrawnOverAllele = false;
        int lengthInBases = allele.getLengthInBases();
        int comparisonLengthInBases = 0;
        OrganismChromosome organismChromosome = allele.getOrganismChromosome();
        OrganismChromosome comparisonChromosome = null;
        if (comparisonAllele != null)
        {
            comparisonChromosome = comparisonAllele.getOrganismChromosome();
            comparisonLengthInBases = comparisonAllele.getLengthInBases();
        }

        int index = 0;
        int xInView = xTopLeft;
        int yInView = yTopLeft;

        // Draw left edge
        int xInImage = -(DNA_IMAGE_ARROWS_INDEX * DNA_IMAGE_WIDTH);;
        int yInImage = 0;
        Graphics g2 = g.create(xInView,yInView,
                               DNA_IMAGE_WIDTH,DNA_IMAGE_HEIGHT);
        g2.drawImage(dnaImage,xInImage,yInImage,this);
        g2.dispose();

        // Draw text identifying allele
        g.drawString(allele.getTextSymbol(),xInView-30,yInView+26);

        // Draw numbers for min and max index of shown base pairs
        String min = " " + leftmostBasePairIndex;
        int maxIndex = leftmostBasePairIndex + DNA_IMAGE_MAX_BASES;
        String max = " " + maxIndex;

        g.drawString(min,xInView-5,yInView-4);
        g.drawString(max,xInView+((DNA_IMAGE_MAX_BASES+1)*DNA_IMAGE_WIDTH),yInView-4);

        // Draw base pairs to left of allele, if any visible
        if (organismAlleleStartIndex >= leftmostBasePairIndex)
        {
            for (i=leftmostBasePairIndex;i<organismAlleleStartIndex && index<DNA_IMAGE_MAX_BASES;i++)
            {
                base = organismChromosome.getBase(i);

                xInView += DNA_IMAGE_WIDTH;
                index++;

                paintBase(g,base,xInView,yInView,false,false);
            }
        }

        int barTop = yInView+1;
        int barHeight = 4;

        // Loop drawing organism allele base pairs
        if ((organismAlleleStartIndex + lengthInBases) >= leftmostBasePairIndex)
        {
            for (i=0;i<lengthInBases && index<DNA_IMAGE_MAX_BASES;i++)
            {
                if (organismAlleleStartIndex + i >= leftmostBasePairIndex)
                {
                    base = allele.getBase(i);

                    xInView += DNA_IMAGE_WIDTH;
                    index++;

                    if (cursorAllele == allele &&
                        selectionStartIndex != -1 &&
                        ((selectionStartIndex < selectionEndIndex &&
                          selectionStartIndex <= i && i < selectionEndIndex) ||
                         (selectionStartIndex > selectionEndIndex &&
                          selectionEndIndex <= i && i < selectionStartIndex)))
                    {
                        paintBase(g,base,xInView,yInView,true,true);
                    }
                    else
                    {
                        paintBase(g,base,xInView,yInView,true,false);
                    }

                    // Draw allele bar, but only if not selected
                    if (comparisonAllele != null &&
                        (i >= comparisonLengthInBases ||
                         comparisonAllele.getBase(i) != base))
                    {
                        if (organismAlleleStrand == Gene.TOP_STRAND)
                        {
                            barTop = yInView;
                        }
                        else
                        {
                            barTop = yInView + 37;
                        }
                        barHeight = 5;
                        g.setColor(differentBaseColor);
                    }
                    else
                    {
                        if (organismAlleleStrand == Gene.TOP_STRAND)
                        {
                            barTop = yInView + 1;
                        }
                        else
                        {
                            barTop = yInView + 37;
                        }
                        barHeight = 4;
                        g.setColor(equalBaseColor);
                    }

                    g.fillRect(xInView,barTop,DNA_IMAGE_WIDTH,barHeight);
                    g.setColor(getForeground());

                    if (!textDrawnOverAllele)
                    {
                        textDrawnOverAllele = true;
                        g.drawString(allele.getTextSymbol(),xInView+4,yInView-5);
                    }
                }
            }
        }

        // Draw base pairs to the right of allele, if any visible
        if (index < DNA_IMAGE_MAX_BASES)
        {
            for (i=leftmostBasePairIndex+index;index<DNA_IMAGE_MAX_BASES;i++)
            {
                base = organismChromosome.getBase(i);

                xInView += DNA_IMAGE_WIDTH;
                index++;

                paintBase(g,base,xInView,yInView,false,false);
            }
        }

        // Draw right edge
        xInView += DNA_IMAGE_WIDTH;
        xInImage = -(DNA_IMAGE_ARROWS_INDEX * DNA_IMAGE_WIDTH);
        yInImage = 0;
        g2 = g.create(xInView,yInView,
                      DNA_IMAGE_WIDTH,DNA_IMAGE_HEIGHT);
        g2.drawImage(dnaImage,xInImage,yInImage,this);
        g2.dispose();

        // Draw cursor if visible
        if (cursorVisible &&
            cursorAllele == allele &&
            (cursorIndex >= 0 &&
             cursorIndex <= lengthInBases))
        {
            g.setColor(cursorColor);
            xInView = xTopLeft + DNA_IMAGE_WIDTH + (((cursorIndex+organismAlleleStartIndex)-leftmostBasePairIndex) * DNA_IMAGE_WIDTH);
            if (cursorStrand == DNA_TOP_STRAND)
            {
                yInView = yTopLeft + DNA_TOP_STRAND_Y;
            }
            else
            {
                yInView = yTopLeft + DNA_BOTTOM_STRAND_Y;
            }
            g.fillRect(xInView-1,yInView,2,DNA_CURSOR_HEIGHT);
            g.setColor(getForeground());
        }
    }

    /**
     * Paint a single base pair
    **/
    public void paintBase(Graphics g, byte base, int xInView, int yInView, boolean inAllele, boolean selected)
    {
        int imageNumber;

        if (base == Base.CYTOSINE)
        {
            if (inAllele)
            {
                if (selected)
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_CG_DARK_SELECTED_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_GC_DARK_SELECTED_INDEX;
                    }
                }
                else
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_CG_DARK_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_GC_DARK_INDEX;
                    }
                }
            }
            else
            {
                if (organismAlleleStrand == Gene.TOP_STRAND)
                {
                    imageNumber = DNA_IMAGE_CG_LIGHT_INDEX;
                }
                else
                {
                    imageNumber = DNA_IMAGE_GC_LIGHT_INDEX;
                }
            }
        }
        else if (base == Base.ADENINE)
        {
            if (inAllele)
            {
                if (selected)
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_AT_DARK_SELECTED_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_TA_DARK_SELECTED_INDEX;
                    }
                }
                else
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_AT_DARK_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_TA_DARK_INDEX;
                    }
                }
            }
            else
            {
                if (organismAlleleStrand == Gene.TOP_STRAND)
                {
                    imageNumber = DNA_IMAGE_AT_LIGHT_INDEX;
                }
                else
                {
                    imageNumber = DNA_IMAGE_TA_LIGHT_INDEX;
                }
            }
        }
        else if (base == Base.GUANINE)
        {
            if (inAllele)
            {
                if (selected)
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_GC_DARK_SELECTED_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_CG_DARK_SELECTED_INDEX;
                    }
                }
                else
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_GC_DARK_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_CG_DARK_INDEX;
                    }
                }
            }
            else
            {
                if (organismAlleleStrand == Gene.TOP_STRAND)
                {
                    imageNumber = DNA_IMAGE_GC_LIGHT_INDEX;
                }
                else
                {
                    imageNumber = DNA_IMAGE_CG_LIGHT_INDEX;
                }
            }
        }
        else
        {
            if (inAllele)
            {
                if (selected)
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_TA_DARK_SELECTED_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_AT_DARK_SELECTED_INDEX;
                    }
                }
                else
                {
                    if (organismAlleleStrand == Gene.TOP_STRAND)
                    {
                        imageNumber = DNA_IMAGE_TA_DARK_INDEX;
                    }
                    else
                    {
                        imageNumber = DNA_IMAGE_AT_DARK_INDEX;
                    }
                }
            }
            else
            {
                if (organismAlleleStrand == Gene.TOP_STRAND)
                {
                    imageNumber = DNA_IMAGE_TA_LIGHT_INDEX;
                }
                else
                {
                    imageNumber = DNA_IMAGE_AT_LIGHT_INDEX;
                }
            }
        }

        int xInImage = -(imageNumber * DNA_IMAGE_WIDTH);
        int yInImage = 0;

        Graphics g2 = g.create(xInView,yInView,
                               DNA_IMAGE_WIDTH,DNA_IMAGE_HEIGHT);
        g2.drawImage(dnaImage,xInImage,yInImage,this);
        g2.dispose();
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
            if (dnaImageLoaded == false && anImage == dnaImage)
            {
                dnaImageLoaded = true;
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
        // Ignore mouse clicks if cannot see or cannot alter DNA
        if (!organismDNAVisible || !organismDNAAlterable)
        {
            return;
        }

        // Draw chromosomes given the current organism
        if ((organismAllele != null &&
             organismAllele.isDeleted() == false) ||
            (organismAllelePair != null &&
             organismAllelePair.isDeleted() == false))
        {
            // Determine which base was selected
            int xMouse = event.getX();
            int yMouse = event.getY();

            // Only handle mouse clicks if within horizontal and vertical
            // region of DNA graphics, specifically within the allele.
            OrganismAllele pickedAllele = null;
            int lengthInBases = 0;
            if (organismAllele != null)
            {
                pickedAllele = organismAllele;
            }
            else if (organismAllelePair != null)
            {
                OrganismAllele firstAlleleOfPair = organismAllelePair.getFirstOrganismAllele();
                OrganismAllele secondAlleleOfPair = organismAllelePair.getSecondOrganismAllele();

                if (yMouse <= DNA_AREA_TOP + DNA_AREA_SINGLE_HEIGHT)
                {
                    pickedAllele = firstAlleleOfPair;
                }
                else
                {
                    pickedAllele = secondAlleleOfPair;
                }
            }

            if (pickedAllele != null &&
                yMouse >= DNA_AREA_TOP &&
                xMouse >= DNA_AREA_LEFT && xMouse <= DNA_AREA_LEFT + DNA_AREA_WIDTH)
            {
                int oldCursorIndex = cursorIndex;
                OrganismAllele oldCursorAllele = cursorAllele;
                lengthInBases =	pickedAllele.getLengthInBases();
                int index = leftmostBasePairIndex + ((xMouse - DNA_AREA_LEFT) / DNA_IMAGE_WIDTH);

                // Adjust index to be relative to start of allele
                if (index >= organismAlleleStartIndex &&
                    index < (organismAlleleStartIndex + lengthInBases + 1))
                {
                    index -= organismAlleleStartIndex;
                }
                else
                {
                    // Click not not on allele
                    stopCursor();
                    return;
                }

                if (yMouse <= DNA_AREA_TOP + (DNA_AREA_SINGLE_HEIGHT/2))
                {
                    // Top strand of top allele
                    setCursor(index,DNA_TOP_STRAND,pickedAllele);
                    if (oldCursorIndex != -1 && shiftDown && oldCursorAllele == pickedAllele)
                    {
                        startMouseMotionListener(oldCursorIndex,index);
                    }
                    else
                    {
                        startMouseMotionListener(index,index);
                    }
                    return;
                }
                else if (yMouse <= DNA_AREA_TOP + DNA_AREA_SINGLE_HEIGHT)
                {
                    // Bottom strand of top allele
                    setCursor(index,DNA_BOTTOM_STRAND,pickedAllele);
                    if (oldCursorIndex != -1 && shiftDown && oldCursorAllele == pickedAllele)
                    {
                        startMouseMotionListener(oldCursorIndex,index);
                    }
                    else
                    {
                        startMouseMotionListener(index,index);
                    }
                    return;
                }
                else if (yMouse <= DNA_AREA_TOP + ((3 * DNA_AREA_SINGLE_HEIGHT)/2) + DNA_AREA_GAP)
                {
                    // Top strand of bottom allele
                    setCursor(index,DNA_TOP_STRAND,pickedAllele);
                    if (oldCursorIndex != -1 && shiftDown && oldCursorAllele == pickedAllele)
                    {
                        startMouseMotionListener(oldCursorIndex,index);
                    }
                    else
                    {
                        startMouseMotionListener(index,index);
                    }
                    return;
                }
                else if (yMouse <= DNA_AREA_TOP + DNA_AREA_DOUBLE_HEIGHT)
                {
                    // Bottom strand of bottom allele
                    setCursor(index,DNA_BOTTOM_STRAND,pickedAllele);
                    if (oldCursorIndex != -1 && shiftDown && oldCursorAllele == pickedAllele)
                    {
                        startMouseMotionListener(oldCursorIndex,index);
                    }
                    else
                    {
                        startMouseMotionListener(index,index);
                    }
                    return;
                }
            }
        }
        stopCursor();
    }

    /**
     * Handle mouse released event
    **/
    public void mouseReleased(MouseEvent event)
    {
        if (listeningForMouseMotion &&
            selectionStartIndex > -1)
        {
            int xMouse = event.getX();
            int yMouse = event.getY();

            removeMouseMotionListener(this);
            listeningForMouseMotion = false;

            // Only handle mouse clicks if within horizontal
            // region of DNA graphics, specifically within the allele.
            OrganismAllele pickedAllele = null;
            if (organismAllele != null)
            {
                pickedAllele = organismAllele;
            }
            else
            {
                pickedAllele = organismAllelePair.getFirstOrganismAllele();
            }
            int lengthInBases = pickedAllele.getLengthInBases();

            int index = leftmostBasePairIndex + ((xMouse - DNA_AREA_LEFT) / DNA_IMAGE_WIDTH);

            // Adjust index to be relative to start of allele
            if (index < organismAlleleStartIndex)
            {
                selectionEndIndex = 0;
            }
            else if (index > (organismAlleleStartIndex + lengthInBases + 1))
            {
                selectionEndIndex = lengthInBases;
            }
            else
            {
                selectionEndIndex = index - organismAlleleStartIndex;
            }

            // Null out the selection if it's zero length
            if (selectionStartIndex == selectionEndIndex)
            {
                selectionStartIndex = -1;
                selectionEndIndex = -1;
            }

            // Force repaint
            repaint();
        }
    }

    /**
     * Handle mouse dragged event
    **/
    public void mouseDragged(MouseEvent event)
    {
        if (listeningForMouseMotion &&
            selectionStartIndex > -1)
        {
            int xMouse = event.getX();
            int yMouse = event.getY();

            // Only handle mouse clicks if within horizontal
            // region of DNA graphics, specifically within the allele.
            OrganismAllele pickedAllele = null;
            if (organismAllele != null)
            {
                pickedAllele = organismAllele;
            }
            else
            {
                pickedAllele = organismAllelePair.getFirstOrganismAllele();
            }
            int lengthInBases = pickedAllele.getLengthInBases();

            int index = leftmostBasePairIndex + ((xMouse - DNA_AREA_LEFT) / DNA_IMAGE_WIDTH);

            // Adjust index to be relative to start of allele
            if (index < organismAlleleStartIndex)
            {
                selectionEndIndex = 0;
            }
            else if (index > (organismAlleleStartIndex + lengthInBases + 1))
            {
                selectionEndIndex = lengthInBases;
            }
            else
            {
                selectionEndIndex = index - organismAlleleStartIndex;
            }

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
     * Start mouse motion listener given the start and end index of
     * any current selection.  If there is no current selection, then
     * startIndex and endIndex should be the same.
    **/
    public void startMouseMotionListener(int startIndex, int endIndex)
    {
        if (listeningForMouseMotion == false)
        {
            selectionStartIndex = startIndex;
            selectionEndIndex = endIndex;
            listeningForMouseMotion = true;
            addMouseMotionListener(this);
        }
    }

    /**
     * Get the shown organism allele for this view.
     *
     * @return		OrganismAllele - the shown organism allele in this view, may be null
    **/
    public OrganismAllele getOrganismAllele()
    {
        return organismAllele;
    }

    /**
     * Set the shown organism allele for this view.  This will result in the given
     * organism allele's base pairs being shown in the view.  No particular base pair
     * is selected initially.<p>
     *
     * @param		anOrganismAllele OrganismAllele - the organism allele whose base pairs to show, may be null
    **/
    public void setOrganismAllele(OrganismAllele anOrganismAllele)
    {
        // Return immediately if no change
        if (organismAllelePair == null &&
            (organismAllele == anOrganismAllele))
        {
            return;
        }

        // Stop the cursor if there is one
        stopCursor();

        // Revert old base values if changes are pending
        revertPendingChanges();

        // Save old values
        OrganismAllele oldOrganismAllele = organismAllele;
        OrganismAllelePair oldOrganismAllelePair = organismAllelePair;

        if (alleleOrganism != null)
        {
            alleleOrganism.removePropertyChangeListener(this);
            alleleOrganism = null;
        }
        
        // Unsubscribe references to old organism allele and old organism allele pair
        if (organismAllele != null)
        {
            organismAllele.removePropertyChangeListener(this);
            organismAllele = null;
        }
        if (organismAllelePair != null)
        {
            organismAllelePair.removePropertyChangeListener(this);
            OrganismAllele pairOrganismAllele = organismAllelePair.getFirstOrganismAllele();
            if (pairOrganismAllele != null)
            {
                pairOrganismAllele.removePropertyChangeListener(this);
            }
            pairOrganismAllele = organismAllelePair.getSecondOrganismAllele();
            if (pairOrganismAllele != null)
            {
                pairOrganismAllele.removePropertyChangeListener(this);
            }
            organismAllelePair = null;
        }

        // Null out state
        organismAlleleStrand = Gene.TOP_STRAND;
        organismAlleleStartIndex = -1;
        leftmostBasePairIndex = -1;
        selectionStartIndex = -1;
        selectionEndIndex = -1;
        scrollBar.setVisible(false);
        organismDNAVisible = false;
        organismDNAAlterable = true;

        // Set new organism allele
        if (anOrganismAllele != null)
        {
            alleleOrganism = anOrganismAllele.getOrganism();
            alleleOrganism.addPropertyChangeListener(this);
            organismAllele = anOrganismAllele;
            organismAllele.addPropertyChangeListener(this);
            organismAlleleStrand = organismAllele.getGene().getStrand();
            organismAlleleStartIndex = organismAllele.getStartIndexInHolder();
            leftmostBasePairIndex = organismAlleleStartIndex - 5;
            if (leftmostBasePairIndex < 0)
            {
                leftmostBasePairIndex = 0;
            }
            int lengthAllele = anOrganismAllele.getLengthInBases();

            OrganismChromosome organismChromosome = organismAllele.getOrganismChromosome();
            int lengthChromosome = organismChromosome.getLengthInBases();

            Organism organism = organismChromosome.getOrganism();
            organismDNAVisible = organism.isDNAVisible();
            organismDNAAlterable = organism.isDNAAlterable();

            int scrollMinimum, scrollMaximum, scrollValue;
            if (organismAlleleStartIndex < DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES)
            {
                scrollMinimum = 0;
            }
            else
            {
                scrollMinimum = organismAlleleStartIndex - DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES;
            }

            if (organismAlleleStartIndex + lengthAllele + DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES > lengthChromosome)
            {
                scrollMaximum = lengthChromosome;
            }
            else
            {
                scrollMaximum = organismAlleleStartIndex + lengthAllele + DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES;
            }

            scrollBar.setBounds(DNA_AREA_LEFT,
                                DNA_AREA_TOP + DNA_AREA_SINGLE_HEIGHT + DNA_SCROLLBAR_AREA_OFFSET,
                                DNA_AREA_WIDTH,
                                DNA_SCROLLBAR_HEIGHT);
            scrollBar.setValues(leftmostBasePairIndex, 30, scrollMinimum, scrollMaximum);

            // revertButton.setBounds(REVERT_BUTTON_LEFT, BUTTON_SINGLE_TOP,
            // 					   BUTTON_WIDTH, BUTTON_HEIGHT);

            // applyButton.setBounds(APPLY_BUTTON_LEFT, BUTTON_SINGLE_TOP,
            // 					  BUTTON_WIDTH, BUTTON_HEIGHT);
        }

        if (organismDNAVisible)
        {
            scrollBar.setVisible(true);
            revertButton.setVisible(true);
            applyButton.setVisible(true);
        }
        else
        {
            scrollBar.setVisible(false);
            revertButton.setVisible(false);
            applyButton.setVisible(false);
        }

        // Force repaint
        repaint();

        // Notify listeners
        if (oldOrganismAllelePair != null)
        {
            changes.firePropertyChange(UIProp.ORGANISM_ALLELE_PAIR,oldOrganismAllelePair,organismAllelePair);
        }
        changes.firePropertyChange(UIProp.ORGANISM_ALLELE,oldOrganismAllele,organismAllele);
    }


    /**
     * Get the shown organism allele pair for this view.
     *
     * @return		OrganismAllelePair - the shown organism allele pair in this view, may be null
    **/
    public OrganismAllelePair getOrganismAllelePair()
    {
        return organismAllelePair;
    }

    /**
     * Set the shown organism allele pair for this view.  This will result in the given
     * organism allele pair's base pairs being shown in the view.  No particular base pair
     * is selected initially.<p>
     *
     * @param		anOrganismAllelePair OrganismAllelePair - the organism allele pair whose base pairs to show, may be null
    **/
    public void setOrganismAllelePair(OrganismAllelePair anOrganismAllelePair)
    {
        // Return immediately if no change
        if (organismAllele == null &&
            (organismAllelePair == anOrganismAllelePair))
        {
            return;
        }

        stopCursor();

        // Revert old base values if changes are pending
        revertPendingChanges();

        // Save old values
        OrganismAllele oldOrganismAllele = organismAllele;
        OrganismAllelePair oldOrganismAllelePair = organismAllelePair;

        if (alleleOrganism != null)
        {
            alleleOrganism.removePropertyChangeListener(this);
            alleleOrganism = null;
        }

        // Unsubscribe references to old organism allele and old organism allele pair
        if (organismAllele != null)
        {
            organismAllele.removePropertyChangeListener(this);
            organismAllele = null;
        }
        if (organismAllelePair != null)
        {
            organismAllelePair.removePropertyChangeListener(this);
            OrganismAllele pairOrganismAllele = organismAllelePair.getFirstOrganismAllele();
            if (pairOrganismAllele != null)
            {
                pairOrganismAllele.removePropertyChangeListener(this);
            }
            pairOrganismAllele = organismAllelePair.getSecondOrganismAllele();
            if (pairOrganismAllele != null)
            {
                pairOrganismAllele.removePropertyChangeListener(this);
            }
            organismAllelePair = null;
        }

        // Null out state
        organismAlleleStartIndex = -1;
        leftmostBasePairIndex = -1;
        selectionStartIndex = -1;
        selectionEndIndex = -1;
        scrollBar.setVisible(false);
        organismDNAVisible = false;
        organismDNAAlterable = true;

        // Set new organism allele pair
        if (anOrganismAllelePair != null)
        {
            alleleOrganism = anOrganismAllelePair.getOrganism();
            alleleOrganism.addPropertyChangeListener(this);
            organismAllelePair = anOrganismAllelePair;
            organismAllelePair.addPropertyChangeListener(this);

            OrganismAllele firstOrganismAllele = organismAllelePair.getFirstOrganismAllele();
            if (firstOrganismAllele != null)
            {
                firstOrganismAllele.addPropertyChangeListener(this);
            }
            OrganismAllele secondOrganismAllele = organismAllelePair.getSecondOrganismAllele();
            if (secondOrganismAllele != null)
            {
                secondOrganismAllele.addPropertyChangeListener(this);
            }

            organismAlleleStrand = firstOrganismAllele.getGene().getStrand();
            organismAlleleStartIndex = firstOrganismAllele.getStartIndexInHolder();
            leftmostBasePairIndex = organismAlleleStartIndex - 5;

            if (leftmostBasePairIndex < 0)
            {
                leftmostBasePairIndex = 0;
            }
            int lengthAllele = firstOrganismAllele.getLengthInBases();

            OrganismChromosome organismChromosome = firstOrganismAllele.getOrganismChromosome();
            int lengthChromosome = organismChromosome.getLengthInBases();

            Organism organism = organismChromosome.getOrganism();
            organismDNAVisible = organism.isDNAVisible();
            organismDNAAlterable = organism.isDNAAlterable();

            int scrollMinimum, scrollMaximum, scrollValue;
            if (organismAlleleStartIndex < DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES)
            {
                scrollMinimum = 0;
            }
            else
            {
                scrollMinimum = organismAlleleStartIndex - DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES;
            }

            if (organismAlleleStartIndex + lengthAllele + DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES > lengthChromosome)
            {
                scrollMaximum = lengthChromosome;
            }
            else
            {
                scrollMaximum = organismAlleleStartIndex + lengthAllele + DNA_SCROLLBAR_GARBAGE_WIDTH_IN_BASES;
            }

            if (secondOrganismAllele != null)
            {
                scrollBar.setBounds(DNA_AREA_LEFT,
                                    DNA_AREA_TOP + DNA_AREA_DOUBLE_HEIGHT + DNA_SCROLLBAR_AREA_OFFSET,
                                    DNA_AREA_WIDTH,
                                    DNA_SCROLLBAR_HEIGHT);
                scrollBar.setValues(leftmostBasePairIndex, 30, scrollMinimum, scrollMaximum);

                // revertButton.setBounds(REVERT_BUTTON_LEFT, BUTTON_DOUBLE_TOP,
                // 					   BUTTON_WIDTH, BUTTON_HEIGHT);

                // applyButton.setBounds(APPLY_BUTTON_LEFT, BUTTON_DOUBLE_TOP,
                // 					  BUTTON_WIDTH, BUTTON_HEIGHT);
            }
            else
            {
                scrollBar.setBounds(DNA_AREA_LEFT,
                                    DNA_AREA_TOP + DNA_AREA_SINGLE_HEIGHT + DNA_SCROLLBAR_AREA_OFFSET,
                                    DNA_AREA_WIDTH,
                                    DNA_SCROLLBAR_HEIGHT);
                scrollBar.setValues(leftmostBasePairIndex, 30, scrollMinimum, scrollMaximum);

                // revertButton.setBounds(REVERT_BUTTON_LEFT, BUTTON_SINGLE_TOP,
                // 					   BUTTON_WIDTH, BUTTON_HEIGHT);

                // applyButton.setBounds(APPLY_BUTTON_LEFT, BUTTON_SINGLE_TOP,
                // 					  BUTTON_WIDTH, BUTTON_HEIGHT);
            }
        }

        if (organismDNAVisible)
        {
            scrollBar.setVisible(true);
            revertButton.setVisible(true);
            applyButton.setVisible(true);
        }
        else
        {
            scrollBar.setVisible(false);
            revertButton.setVisible(false);
            applyButton.setVisible(false);
        }

        // Force repaint
        repaint();

        // Notify listeners
        if (oldOrganismAllele != null)
        {
            changes.firePropertyChange(UIProp.ORGANISM_ALLELE,oldOrganismAllele,organismAllele);
        }
        changes.firePropertyChange(UIProp.ORGANISM_ALLELE_PAIR,oldOrganismAllelePair,organismAllelePair);
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals(EngineProp.DNA_ALTERABLE))
        {
            organismDNAAlterable = ((Boolean) event.getNewValue()).booleanValue();
        }
        else if (event.getPropertyName().equals(EngineProp.DELETED))
        {
            if (event.getSource() == organismAllele)
            {
                organismAllele = null;
            }
            else if (event.getSource() == organismAllelePair)
            {
                organismAllelePair = null;
            }
        }
        repaint();
    }

    /**
     * Handle adjustment event
    **/
    public void adjustmentValueChanged(AdjustmentEvent event)
    {
        leftmostBasePairIndex = event.getValue();
        repaint();
    }

    /**
     * Handle change event
    **/
    public void stateChanged(ChangeEvent event)
    {

    }

    public void setRevertVisible(boolean visible)
    {
        revertButton.setVisible(visible);
    }
    
    public void setApplyVisible(boolean visible)
    {
        applyButton.setVisible(visible);
    }
    
    /**
     * Set cursor to the given index, starting the
     * cursor timer if necessary.
    **/
    private void setCursor(int newIndex, int strand, OrganismAllele allele)
    {
        if (newIndex < 0)
        {
            return;
        }
        cursorIndex = newIndex;

        cursorAllele = allele;
        cursorStrand = strand;

        if (!cursorTimer.isRunning())
        {
            cursorTimer.start();
            cursorVisible = true;
        }

        if (!hasFocus())
        {
            requestFocus();
        }

        repaint();
    }

    /**
     * Stop cursor
    **/
    private void stopCursor()
    {
        if (cursorTimer.isRunning())
        {
            cursorTimer.stop();
        }

        cursorIndex = -1;
        cursorVisible = false;
        cursorAllele = null;
        cursorStrand = DNA_TOP_STRAND;

        repaint();
    }

    /**
     * Handle a cursor time click
    **/
    private void onCursorTimerClick()
    {
        // Must stop timer if no organismAllele and no organismAllelePair
        // or if the cursor position is -1.
        if ((organismAllele == null && organismAllelePair == null) ||
            cursorVisible == false && cursorIndex == -1)
        {
            stopCursor();
            return;
        }

        if (cursorVisible)
        {
            cursorVisible = false;
        }
        else
        {
            cursorVisible = true;
        }

        // Force a repaint
        repaint();
    }

    /**
     * React to actions
    **/
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();

        if (e.getSource() == cursorTimer)
        {
            onCursorTimerClick();
        }
        else if (cmd.equals(cmdApply))
        {
            // If defer apply is false, apply pending changes
            if (deferApply == false)
            {
                applyPendingChanges();
            }

            // Let other action listeners hear this action
            callActionListeners(e);

            // stop cursor
            stopCursor();

            // Notify listeners
            changes.firePropertyChange(UIProp.APPLY_BUTTON_PUSHED,null,null);
        }
        else if (cmd.equals(cmdRevert))
        {
            // If defer revert is false, revert pending changes
            if (deferRevert == false)
            {
                revertPendingChanges();
            }

            // Let other action listeners hear this action
            callActionListeners(e);

            // stop cursor
            stopCursor();

            // Notify listeners
            changes.firePropertyChange(UIProp.REVERT_BUTTON_PUSHED,null,null);
        }
    }

    /**
     * Handle key typed event
    **/
    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * Handle key pressed event
    **/
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SHIFT)
        {
            shiftDown = true;
            return;
        }

        // Ignore key stroke if cursor not running, the view isn't visible
        // or doesn't currently have a parent
        if (cursorAllele != null && cursorTimer.isRunning() &&
            isVisible() && (getParent() != null))
        {
            // Scroll to make the cursor visible
            int cursorIndexInScrollBar = cursorIndex + organismAlleleStartIndex;
            if (leftmostBasePairIndex > cursorIndexInScrollBar ||
                cursorIndexInScrollBar > leftmostBasePairIndex + DNA_IMAGE_MAX_BASES)
            {
                if (cursorIndexInScrollBar > DNA_IMAGE_MAX_BASES)
                {
                    scrollBar.setValue(cursorIndexInScrollBar - (DNA_IMAGE_MAX_BASES/2));
                }
                else
                {
                    scrollBar.setValue(cursorIndexInScrollBar);
                }
            }

            byte base = Base.URACIL;	// an invalid base for DNA
            char keyChar = e.getKeyChar();

            if (keyChar == 'a' || keyChar == 'A')
            {
                base = Base.ADENINE;
            }
            else if (keyChar == 'c' || keyChar == 'C')
            {
                base = Base.CYTOSINE;
            }
            else if (keyChar == 'g' || keyChar == 'G')
            {
                base = Base.GUANINE;
            }
            else if (keyChar == 't' || keyChar == 'T')
            {
                base = Base.THYMINE;
            }

            int iNewBase = 0;
            int iOldBase = 0;
            int lengthInBases = cursorAllele.getLengthInBases();
            byte newBases[];

            if (base != Base.URACIL)
            {
                // Add the base in the appropriate place
                newBases = new byte[lengthInBases+1];
                for (iOldBase=0;iOldBase<lengthInBases;iOldBase++)
                {
                    if (iOldBase == cursorIndex)
                    {
                        if (cursorStrand == DNA_TOP_STRAND)
                        {
                            newBases[iNewBase] = base;
                        }
                        else
                        {
                            newBases[iNewBase] = Base.getPairBase(base,Base.IN_DNA);
                        }
                        iNewBase++;
                    }
                    newBases[iNewBase] = cursorAllele.getBase(iOldBase);
                    iNewBase++;
                }

                // If new base is at end, insert it now
                if (cursorIndex == lengthInBases)
                {
                    if (cursorStrand == DNA_TOP_STRAND)
                    {
                        newBases[iNewBase] = base;
                    }
                    else
                    {
                        newBases[iNewBase] = Base.getPairBase(base,Base.IN_DNA);
                    }
                }

                cursorAllele.setBases(newBases);
                cursorIndex++;

                allelesEdited();

                repaint();
            }
            else
            {
                if (keyCode == KeyEvent.VK_LEFT)
                {
                    if (cursorIndex > 0)
                    {
                        // Either extend or clear selection
                        if (shiftDown)
                        {
                            // Add to selection, if necessary
                            if (selectionStartIndex != -1)
                            {
                                if (cursorIndex == selectionStartIndex)
                                {
                                    selectionStartIndex--;
                                }
                                else if (cursorIndex == selectionEndIndex)
                                {
                                    selectionEndIndex--;
                                }

                                // Clear selection if the start and end index are equal
                                if (selectionStartIndex == selectionEndIndex)
                                {
                                    selectionStartIndex = -1;
                                    selectionEndIndex = -1;
                                }
                            }
                            else
                            {
                                // New selection
                                selectionStartIndex = cursorIndex - 1;
                                selectionEndIndex = cursorIndex;
                            }
                        }
                        else
                        {
                            // Clear selection
                            selectionStartIndex = -1;
                            selectionEndIndex = -1;
                        }
                        cursorIndex--;
                        repaint();
                    }
                }
                else if (keyCode == KeyEvent.VK_RIGHT)
                {
                    if (cursorIndex < lengthInBases)
                    {
                        // Either extend or clear selection
                        if (shiftDown)
                        {
                            // Add to selection, if necessary
                            if (selectionStartIndex != -1)
                            {
                                if (cursorIndex == selectionStartIndex)
                                {
                                    selectionStartIndex++;
                                }
                                else if (cursorIndex == selectionEndIndex)
                                {
                                    selectionEndIndex++;
                                }

                                // Clear selection if the start and end index are equal
                                if (selectionStartIndex == selectionEndIndex)
                                {
                                    selectionStartIndex = -1;
                                    selectionEndIndex = -1;
                                }
                            }
                            else
                            {
                                // New selection
                                selectionStartIndex = cursorIndex + 1;
                                selectionEndIndex = cursorIndex;
                            }
                        }
                        else
                        {
                            // Clear selection
                            selectionStartIndex = -1;
                            selectionEndIndex = -1;
                        }
                        cursorIndex++;
                        repaint();
                    }
                }
                else if (keyCode == KeyEvent.VK_BACK_SPACE)
                {
                    if (selectionStartIndex != -1)
                    {
                        // Delete selected bases
                        if (selectionStartIndex < selectionEndIndex)
                        {
                            newBases = new byte[lengthInBases-(selectionEndIndex-selectionStartIndex)];
                        }
                        else
                        {
                            newBases = new byte[lengthInBases-(selectionStartIndex-selectionEndIndex)];
                        }
                        for (iOldBase=0;iOldBase<lengthInBases;iOldBase++)
                        {
                            if ((selectionStartIndex < selectionEndIndex &&
                                 selectionStartIndex <= iOldBase && iOldBase < selectionEndIndex) ||
                                (selectionStartIndex > selectionEndIndex &&
                                 selectionEndIndex <= iOldBase && iOldBase < selectionStartIndex))
                            {
                                // Base selected - skip it, thereby effectively deleting it
                            }
                            else
                            {
                                // Base not selected - copy it
                                newBases[iNewBase] = cursorAllele.getBase(iOldBase);
                                iNewBase++;
                            }
                        }

                        // Clear selection
                        selectionStartIndex = -1;
                        selectionEndIndex = -1;

                        // Set new bases
                        cursorAllele.setBases(newBases);
                        allelesEdited();
                        repaint();
                    }
                    else
                    {
                        // Delete base to immediate left of cursor unless we're at cursor position 0
                        if (cursorIndex != 0)
                        {
                            iNewBase = 0;
                            newBases = new byte[lengthInBases-1];
                            for (iOldBase=0;iOldBase<lengthInBases;iOldBase++)
                            {
                                if (iOldBase != (cursorIndex-1))
                                {
                                    newBases[iNewBase] = cursorAllele.getBase(iOldBase);
                                    iNewBase++;
                                }
                            }

                            cursorAllele.setBases(newBases);
                            allelesEdited();
                            cursorIndex--;
                            repaint();
                        }
                    }
                }
                else if (keyCode == KeyEvent.VK_DELETE)
                {
                    if (selectionStartIndex != -1)
                    {
                        // Delete selected bases
                        if (selectionStartIndex < selectionEndIndex)
                        {
                            newBases = new byte[lengthInBases-(selectionEndIndex-selectionStartIndex)];
                        }
                        else
                        {
                            newBases = new byte[lengthInBases-(selectionStartIndex-selectionEndIndex)];
                        }
                        for (iOldBase=0;iOldBase<lengthInBases;iOldBase++)
                        {
                            if ((selectionStartIndex < selectionEndIndex &&
                                 selectionStartIndex <= iOldBase && iOldBase < selectionEndIndex) ||
                                (selectionStartIndex > selectionEndIndex &&
                                 selectionEndIndex <= iOldBase && iOldBase < selectionStartIndex))
                            {
                                // Base selected - skip it, thereby effectively deleting it
                            }
                            else
                            {
                                // Base not selected - copy it
                                newBases[iNewBase] = cursorAllele.getBase(iOldBase);
                                iNewBase++;
                            }
                        }

                        // Clear selection
                        selectionStartIndex = -1;
                        selectionEndIndex = -1;

                        // Set new bases
                        cursorAllele.setBases(newBases);
                        allelesEdited();
                        repaint();
                    }
                    else
                    {
                        // Delete base to immediate right of cursor unless we're at the last cursor position
                        if (cursorIndex != lengthInBases)
                        {
                            iNewBase = 0;
                            newBases = new byte[lengthInBases-1];
                            for (iOldBase=0;iOldBase<lengthInBases;iOldBase++)
                            {
                                if (iOldBase != cursorIndex)
                                {
                                    newBases[iNewBase] = cursorAllele.getBase(iOldBase);
                                    iNewBase++;
                                }
                            }

                            cursorAllele.setBases(newBases);
                            allelesEdited();
                            cursorIndex--;
                            repaint();
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle key released event
    **/
    public void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SHIFT)
        {
            shiftDown = false;
            return;
        }
    }

    /**
     * Alleles edited
    **/
    public void allelesEdited()
    {
        applyButton.setEnabled(true);
        revertButton.setEnabled(true);
    }

    /**
     * Get the text symbol of the top or bottom allele if the pending changes are
     * made to that allele.<p>
     *
     * @param      topOrBottomAllele int - DNAView.TOP_ALLELE or DNAView.BOTTOM_ALLELE
     * @return     textSymbol String - text symbol of allele, null if no strands are shown
     * @exception	IllegalArgumentException - input topOrBottomAllele argument illegal
    **/
    public String getAllelePendingTextSymbol(int topOrBottomAllele)
    {
        if (topOrBottomAllele != TOP_ALLELE &&
            topOrBottomAllele != BOTTOM_ALLELE)
        {
            throw new IllegalArgumentException("bad allele");
        }

        if (organismAllele != null)
        {
            return organismAllele.getPendingTextSymbol();
        }
        else if (organismAllelePair != null)
        {
            OrganismAllele allele;
            if (topOrBottomAllele == TOP_ALLELE)
            {
                allele = organismAllelePair.getFirstOrganismAllele();
            }
            else
            {
                allele = organismAllelePair.getSecondOrganismAllele();
            }
            if (allele != null)
            {
                return allele.getPendingTextSymbol();
            }
        }

        return null;
    }

    /**
     * Apply pending changes made to DNA
    **/
    public void applyPendingChanges()
    {
        applyButton.setEnabled(false);
        revertButton.setEnabled(false);

        if (organismAllele != null ||
            organismAllelePair != null)
        {
            if (organismAllele != null)
            {
                organismAllele.reconcileBaseChanges();
            }
            else if (organismAllelePair != null)
            {
                OrganismAllele allele;
                allele = organismAllelePair.getFirstOrganismAllele();
                if (allele != null)
                {
                    allele.reconcileBaseChanges();
                }
                allele = organismAllelePair.getSecondOrganismAllele();
                if (allele != null)
                {
                    allele.reconcileBaseChanges();
                }
            }
        }

        repaint();
    }

    /**
     * Revert pending changes to DNA
    **/
    public void revertPendingChanges()
    {
        applyButton.setEnabled(false);
        revertButton.setEnabled(false);

        OrganismAllele allele;
        SpeciesAllele speciesAllele;
        if (organismAllele != null)
        {
            allele = organismAllele;
            speciesAllele = allele.getSpeciesAllele();
            allele.setBases(speciesAllele.getBases());
        }
        else if (organismAllelePair != null)
        {
            allele = organismAllelePair.getFirstOrganismAllele();
            if (allele != null)
            {
                speciesAllele = allele.getSpeciesAllele();
                // WHY do I have to do this check????
                if (speciesAllele != null)
                {
                    allele.setBases(speciesAllele.getBases());
                }
            }
            allele = organismAllelePair.getSecondOrganismAllele();
            if (allele != null)
            {
                speciesAllele = allele.getSpeciesAllele();
                if (speciesAllele != null)
                {
                    allele.setBases(speciesAllele.getBases());
                }
            }
        }

        repaint();
    }
    
    public void addActionListener(ActionListener listener)
    {
        actionListeners.addElement(listener);
    }
    
    public void removeActionListener(ActionListener listener)
    {
        actionListeners.removeElement(listener);
    }
    
    protected void callActionListeners(ActionEvent e)
    {
        Enumeration listeners = actionListeners.elements();
        while (listeners.hasMoreElements())
        {
            ActionListener listener = (ActionListener) listeners.nextElement();
            listener.actionPerformed(e);
        }
    }
}

