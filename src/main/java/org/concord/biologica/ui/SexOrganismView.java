//
// Class : SexOrganismView - the sex organism view used as the top 3 subviews in the
// 							 sex view of the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2003/01/16 14:06:38 $
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
 * This class represents a view which shows a parent or offspring organism in the sex view.
 * Instances of this class are the top 3 subviews of the sex view, containing the mother,
 * father and offspring of the meiosis and fertilization operations in the sex view.<p>
 *
 * This class differs from SingleOrganismView.java in that this view does not have scrollbars,
 * does not react to engine or mouse events and does have a magnifying glass push button in
 * its top left corner.<p>
 *
 * This view has 2 modes - a mode for meiosis where setOrganism() is called with the
 * organism to display and a second mode for fertilization where setFertilizationModel()
 * is called with the fertilization model that this view should monitor to know when
 * a child organism has been born.  The view automatically switches back and forth between
 * the 2 modes when setOrganism() and setFertilizationModel() are called.<p>
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
 * @see org.concord.biologica.ui.UIProp#SEX_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SPECIES_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#TEXT_INDENT
 * @see org.concord.biologica.ui.UIProp#TEXT_LINE_SPACING
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2003/01/16 14:06:38 $
 * @author 		$Author: qliao $
**/

public final class SexOrganismView
extends OrganismView
implements MouseListener, ImageObserver, PropertyChangeListener
{
    /**
     * Current organism.  When this changes, a UIProp.ORGANISM property change event is fired.
    **/
    private Organism organism;

    /**
     * Current fertilization model, if that's how this view works.
     * When this changes, a UIProp.ORGANISM property change event is fired, as the organism
     * always changes when the fertilizationModel changes and it seemed redundant and
     * unnecessary to fire both an ORGANISM and FERTILIZATION_MODEL event for one action.
    **/
    private FertilizationModel fertilizationModel;

    /**
     * Active tool
    **/
    private int activeTool = Tool.SELECTION;

    /**
     * Creates a parent organism view.
     *
     * @exception	IllegalArgumentException - one of input arguments null
    **/
    public SexOrganismView()
    {
        super();

        organism = null;
        fertilizationModel = null;

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
    Organism getOrganism()
    {
        return organism;
    }

    /**
     * Set the current organism to be shown in this view.
     *
     * @param		anOrganism Organism - the new organism to be shown in this view, may be null
    **/
    void setOrganism(Organism anOrganism)
    {
        // Ignore redundant setting
        if (organism == anOrganism)
        {
            return;
        }

        Organism oldOrganism = organism;

        // Make the change
        if (fertilizationModel != null)
        {
            fertilizationModel.removePropertyChangeListener(this);
        }
        fertilizationModel = null;

        if (organism != null)
        {
            if (! organism.isDeleted())
                organism.addPropertyChangeListener(this);
        }
        organism = anOrganism;
        if (organism != null)
        {
            if (organism.isDeleted())
                organism = null;
            else
                organism.addPropertyChangeListener(this);
        }

        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM,oldOrganism,organism);
    }

    /**
     * The fertilization model just notified us that it has a new
     * organism.  So get that new organism and update our state.
    **/
    void refreshOrganism()
    {
        // Return immediately if fertilizationModel null
        if (fertilizationModel == null)
        {
            return;
        }

        // Save old organism
        Organism oldOrganism = organism;
        if (organism != null)
        {
            if (! organism.isDeleted())
                organism.addPropertyChangeListener(this);
        }

        // Make the change
        organism = fertilizationModel.getOffspringOrganism();
        if (organism != null)
        {
            if (organism.isDeleted())
                organism = null;
            else
                organism.addPropertyChangeListener(this);
        }

        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM,oldOrganism,organism);
    }

    /**
     * Get the fertilization model shown in this view.
     *
     * @return		FertilizationModel - the fertilization model shown in the view, may be null
    **/
    FertilizationModel getFertilizationModel()
    {
        return fertilizationModel;
    }

    /**
     * Set the current fertilization model to be shown in this view.
     *
     * @param		aFertilizationModel FertilizationModel - new fertilization model
    **/
    void setFertilizationModel(FertilizationModel aFertilizationModel)
    {
        // Ignore redundant setting
        if (fertilizationModel == aFertilizationModel)
        {
            return;
        }

        Organism oldOrganism = organism;

        // Make the change
        if (organism != null)
        {
            if (! organism.isDeleted())
                organism.removePropertyChangeListener(this);
        }
        organism = null;

        if (fertilizationModel != null)
        {
            fertilizationModel.removePropertyChangeListener(this);
        }
        fertilizationModel = aFertilizationModel;

        // Wire things up if fertilizationModel is not null
        if (fertilizationModel != null)
        {
            fertilizationModel.addPropertyChangeListener(this);
            organism = fertilizationModel.getOffspringOrganism();
            if (organism != null)
            {
                if (organism.isDeleted())
                    organism = null;
                else
                    organism.addPropertyChangeListener(this);
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
        // Get bounds and paint background
        Rectangle bounds = getBounds();
        paintBackground(g,bounds);

        g.setFont(getFont());
        g.setColor(getForeground());

        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Draw organism in middle of available view
        if (organism != null)
        {
            if (organism.isDeleted())
            {
                organism = null;
            }
            else
            {
                Species species = organism.getSpecies();
                int imageWidth = species.getImageColumnWidth(organismImageSize);
                paintOrganism(g,organism,(bounds.width-imageWidth)/2,10,fontHeight,false);
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
            // Don't allow actual selection in this view - not sure why
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

        if (propertyName.equals(UIProp.FERTILIZATION_OFFSPRING_ORGANISM))
        {
            // Fertilization model has new organism
         //   System.out.println("sexOrganimsView: propertyChange");
            refreshOrganism();
        }
        else if (propertyName.equals(EngineProp.DELETED))
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

