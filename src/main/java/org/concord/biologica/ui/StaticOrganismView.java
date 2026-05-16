//
// Class : StaticOrganismView - A view of a static organism.  No scrollbars, no mouse behavior, etc.
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:21 $
// $Author: ed $
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
 * A simple, static view that shows an organism and does nothing else. <p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.CHARACTERISTICS_TEXT_VISIBLE - the characteristics text visibility boolean changed
 * <li> UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM - the user clicked on an organism with the chromosome tool
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * <li> UIProp.LOCK_SYMBOL_VISIBLE - the lock symbol should or should not be displayed if appropriate
 * <li> UIProp.NAME_TEXT_VISIBLE - the name text visibility boolean changed
 * <li> UIProp.ORGANISM - the shown organism has changed from one organism to another, can be caused
 *						  by calling either setOrganism() or setFertilizationModel()
 * <li> UIProp.ORGANISM_IMAGE_SIZE - the image size to use for drawing organisms in this view
 * <li> UIProp.SEX_TEXT_VISIBLE - the sex text visibility boolean changed
 * <li> UIProp.SPECIES_TEXT_VISIBLE - the species text visibility boolean changed
 * <li> UIProp.SELECTION_TOOL_PICK_ON_STATIC_ORGANISM - user clicked on static organism with selection tool
 * <li> UIProp.TEXT_INDENT - the indentation of text from left edge of image
 * <li> UIProp.TEXT_LINE_SPACING - the number of pixels between lines of text
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#CHARACTERISTICS_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#CHROMOSOME_TOOL_PICK_ON_ORGANISM
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#LOCK_SYMBOL_VISIBLE
 * @see org.concord.biologica.ui.UIProp#NAME_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#ORGANISM
 * @see org.concord.biologica.ui.UIProp#ORGANISM_IMAGE_SIZE
 * @see org.concord.biologica.ui.UIProp#SELECTION_TOOL_PICK_ON_STATIC_ORGANISM
 * @see org.concord.biologica.ui.UIProp#SEX_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SPECIES_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#TEXT_INDENT
 * @see org.concord.biologica.ui.UIProp#TEXT_LINE_SPACING
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:21 $
 * @author 		$Author: ed $
**/

public final class StaticOrganismView
extends OrganismView
implements MouseListener, ImageObserver, PropertyChangeListener
{
    /**
     * Current organism.  When this changes, a UIProp.ORGANISM property change event is fired.
    **/
    private Organism organism;

    /**
     * Active tool, which we limit to snip, selection or chromosome
     *
     * @see org.concord.biologica.ui.Tool#CHROMOSOME
     * @see org.concord.biologica.ui.Tool#SELECTION
     * @see org.concord.biologica.ui.Tool#SNIP
    **/
    private int activeTool = Tool.SELECTION;
    
    private Dimension preferredSize;

    /**
     * Creates a parent organism view.
     *
     * @exception	IllegalArgumentException - one of input arguments null
    **/
    public StaticOrganismView()
    {
        super();

        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);

        organism = null;

        // Turn on double buffering
        setDoubleBuffered(true);
        
        // Listen for mouse clicks, but not mouse motion initially
        addMouseListener(this);
    }

    /**
     * Get the organism shown in this view.
     *
     * @return		Organism - the organism shown in the view, may be null
    **/
    public Organism getOrganism()
    {
        return organism;
    }

    /**
     * Set the current organism to be shown in this view.
     *
     * @param		anOrganism Organism - the new organism to be shown in this view, may be null
    **/
    public void setOrganism(Organism anOrganism)
    {
        // Ignore redundant setting
        if (organism == anOrganism)
        {
            return;
        }

        Organism oldOrganism = organism;
        organism = anOrganism;

        if (oldOrganism != null)
        {
            oldOrganism.removePropertyChangeListener(this);
        }
        if (organism != null)
        {
            organism.addPropertyChangeListener(this);
            Dimension cellSize = getOrganismCellDimensions(organism, 0, 0, 20, false);
            if (preferredSize == null)
            {
                preferredSize = new Dimension(cellSize.width, cellSize.height);
            }
            else
            {
                preferredSize.width = cellSize.width;
                preferredSize.height = cellSize.height;
            }
        }

        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM,oldOrganism,organism);
    }
    
    /**
     * Draw the graphics in this view.  Uses the OrganismView.paintOrganism() method
     * to do the really drawing.<p>
     *
     * @param 		g Graphics - the given graphics to use in drawing
    **/
    public void paintComponent(Graphics g)
    {
        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Draw organism in middle of available view
        if (organism != null)
        {
            if (organism.isDeleted())
                return;
            // Draw background
            Rectangle bounds = getBounds();
            paintBackground(g,bounds);
    
            // Set font and color
            g.setFont(getFont());
            g.setColor(getForeground());
    
            // Draw organism
            Species species = organism.getSpecies();
            int imageWidth = species.getImageColumnWidth(organismImageSize);
            Dimension size = paintOrganism(g,organism,(bounds.width-imageWidth)/2,10,fontHeight,false);
            if (preferredSize == null)
            {
                preferredSize = new Dimension(size.width, size.height);
            }
            else
            {
                preferredSize.width = size.width;
                preferredSize.height = size.height;
            }
        }
    }

    /**
     * Return the preferred size of this canvas
     *
     * @return		Dimension - preferred size of canvas
    **/
    public Dimension getPreferredSize()
    {
        Dimension cellSize = getOrganismCellDimensions(organism, 0, 0, 20, false);
        if (preferredSize == null)
        {
            preferredSize = new Dimension(cellSize.width, cellSize.height);
        }
        else
        {
            preferredSize.width = cellSize.width;
            preferredSize.height = cellSize.height;
        }
        return preferredSize;
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
            repaint();
        }

        return true;
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
        // Ignore mouse events if no organism in view
        if (organism == null)
        {
            return;
        }

        // Get mouse x and y
        int xMouse = event.getX();
        int yMouse = event.getY();

        // Adjust mouse click coordinates
        Rectangle bounds = getBounds();
        Species species = organism.getSpecies();
        int imageWidth = species.getImageColumnWidth(organismImageSize);
        xMouse = xMouse-((bounds.width-imageWidth)/2);
        yMouse = yMouse-10;

        // Determine if the click is on a hotspot and turn on the hotspot image if it is.
        if (activeTool == Tool.SELECTION)
        {
            updateHotspotOnMousePress(organism,xMouse,yMouse);
            changes.firePropertyChange(UIProp.SELECTION_TOOL_PICK_ON_STATIC_ORGANISM,
                                       null, organism);
        }
        else if (activeTool == Tool.SNIP)
        {
            organism.delete();
        }
        else if (activeTool == Tool.CHROMOSOME)
        {
            // Notify listeners that a chromosome tool event happened on organism
            changes.firePropertyChange(UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM,
                                       null, organism);
        }
        repaint();
    }

    /**
     * Handle mouse released event
    **/
    public void mouseReleased(MouseEvent event)
    {
        updateHotspotOnMouseReleased();
        repaint();
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
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.DELETED))
        {
            // Our organism has been deleted
            Object object = event.getSource();
            if (object instanceof Organism)
            {
                Organism anOrganism = (Organism) object;
                if (organism == anOrganism)
                {
                    setOrganism(null);
                }
            }
        }
        else if (propertyName.equals(EngineProp.ORGANISM_GENOTYPE_AND_PHENOTYPE))
        {
            // Our organism's genotype and/or phenotype has changed
            repaint();
        }
    }
}

