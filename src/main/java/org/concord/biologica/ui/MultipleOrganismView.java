//
// Class : MultiOrganismView - the multiple organism level view in the BioLogica user interface
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.4 $
// $Date: 2002/02/21 05:45:03 $
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

/**
 * The multiple organism view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.BACKGROUND - the background color of the view changed
 * <li> org.concord.biologica.ui.UIProp.CHARACTERISTICS_TEXT_VISIBLE - the characteristics text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM - the user clicked on an organism with the chromosome tool
 * <li> org.concord.biologica.ui.UIProp.FONT - the font of the view changed
 * <li> org.concord.biologica.ui.UIProp.FOREGROUND - the foreground color of the view changed
 * <li> org.concord.biologica.ui.UIProp.LOCK_SYMBOL_VISIBLE - the lock symbol should or should not be displayed if appropriate
 * <li> org.concord.biologica.ui.UIProp.NAME_TEXT_VISIBLE - the name text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_ADDED_TO_VIEW - an organism has been added to this view
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_IMAGE_SIZE - the image size to use for drawing organisms in this view
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_LAYOUT_STYLE - the organism layout style for this view
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_REMOVED_FROM_VIEW - an organism has been removed from this view
 * <li> org.concord.biologica.ui.UIProp.SELECTION_MODE - the selection mode of this view has changed
 * <li> org.concord.biologica.ui.UIProp.SEX_TEXT_VISIBLE - the sex text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.SPECIES_TEXT_VISIBLE - the species text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.TEXT_INDENT - the indentation of text from left edge of image
 * <li> org.concord.biologica.ui.UIProp.TEXT_LINE_SPACING - the number of pixels between lines of text
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#CHARACTERISTICS_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#CHROMOSOME_TOOL_PICK_ON_ORGANISM
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#LOCK_SYMBOL_VISIBLE
 * @see org.concord.biologica.ui.UIProp#NAME_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_ADDED_TO_VIEW
 * @see org.concord.biologica.ui.UIProp#ORGANISM_IMAGE_SIZE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_LAYOUT_STYLE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_REMOVED_FROM_VIEW
 * @see org.concord.biologica.ui.UIProp#SELECTION_MODE
 * @see org.concord.biologica.ui.UIProp#SEX_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SPECIES_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#TEXT_INDENT
 * @see org.concord.biologica.ui.UIProp#TEXT_LINE_SPACING
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.4 $ $Date: 2002/02/21 05:45:03 $
 * @author 		$Author: dima $
**/
public final class MultipleOrganismView
extends OrganismView
implements MouseListener, MouseMotionListener, PropertyChangeListener, SelectionPresenter
{
	/**
	 * Default organism layout style for this view - left to right and wrap (e.g. multiple rows).
	**/
	static public final int ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP = 1;

	/**
	 * An alternative organism layout style for this view - left to right with no wrap (e.g. single row).
	**/
	static public final int ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_NO_WRAP = 2;

	/**
	 * An alternative organism layout style for this view - top to bottom and wrap (e.g. multiple columns).
	**/
	static public final int ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_WRAP = 3;

	/**
	 * An alternative organism layout style for this view - top to bottom with no wrap (e.g. single column).
	**/
	static public final int ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_NO_WRAP = 4;

	/**
	 * Maximum x of drawn organisms (rightmost edge of drawn organisms)
	**/
	private int xMaximumDrawnOrganisms;

	/**
	 * Maximum y of drawn organisms (bottommost edge of drawn organisms and their text)
	**/
	private int yMaximumDrawnOrganisms;

	/**
	 * Vector of organisms to show in this view
	**/
	private Vector organisms = null;

	/**
	 * Selection set for this view.
	**/
	private SelectionSet selectionSet = null;

	/**
	 * Active tool, which we limit to snip, selection or chromosome
	 *
	 * @see org.concord.biologica.ui.Tool#CHROMOSOME
	 * @see org.concord.biologica.ui.Tool#PEDIGREE
	 * @see org.concord.biologica.ui.Tool#SELECTION
	 * @see org.concord.biologica.ui.Tool#SNIP
	**/
	private int activeTool = Tool.SELECTION;

	/**
	 * Current organism layout style.  See above static final int ORGANISM_LAYOUT_STYLE_XXX
	 * values for the list of possible values.
	**/
	private int organismLayoutStyle = ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP;

	/**
	 * Creates a multiple organism view.
	**/
	public MultipleOrganismView()
	{
		super();

		// Use default selection set initially
        selectionSet = SelectionSet.getDefaultSelectionSet();

 		// Set colors
		setBackground(Color.white);
		setForeground(Color.black);

		// Maximums are 0 initially because there are
		// no organisms in the view initially.
		xMaximumDrawnOrganisms = 0;
		yMaximumDrawnOrganisms = 0;

		organisms = new Vector();
		organismLayoutStyle = ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP;

		// Turn on double buffering
		setDoubleBuffered(true);

		// Listen for mouse clicks, but not mouse motion initially
		addMouseListener(this);

		// Tell the selection set about this view
		selectionSet.addSelectionPresenter(this);
		

	    try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    } catch (Exception e){
			e.printStackTrace();
	    }
	    SwingUtilities.updateComponentTreeUI(this);
		
		
		
		
	}

	/**
	 * Tell this view its scroll pane.
	 *
	 * @param		aScrollPane JScrollPane - the scroll pane containing this view
    **/
	public void setScrollPane(JScrollPane aScrollPane)
	{
		scrollPane = aScrollPane;
		updateScrollBars();
	}

	/**
	 * Add an organism to this view.<p>
	 *
	 * @param		anOrganism Organism - an organism to add, may not be null
	 * @exception	IllegalArgumentException - input organism null
	**/
	public void addOrganism(Organism anOrganism)
	{
		if (anOrganism == null)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		// Ok - add element if not already
		if (organisms.contains(anOrganism) == false)
		{
			organisms.addElement(anOrganism);

			anOrganism.addPropertyChangeListener(this);

			// Force a repaint
			repaint();
	
			// Notify listeners
			changes.firePropertyChange(UIProp.ORGANISM_ADDED_TO_VIEW,null,anOrganism);
			
			// Force scroll bars to maximums to see new organism
			if (scrollPane != null)
			{
				// Update scroll bars
				updateScrollBars();
	
				JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
				JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
	
				if (verticalScrollBar != null)
				{
					verticalScrollBar.setValue(verticalScrollBar.getMaximum());
				}
				if (horizontalScrollBar != null)
				{
					horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
				}
			}
		}
	}

	/**
	 * Removes an organism from this view. This method returns false if anOrganism is null.<p>
	 *
	 * @param		anOrganism Organism - an organism, may be null
	 * @return		boolean indicating whether or not the organism was found and removed
	**/
	public boolean removeOrganism(Organism anOrganism)
	{
		// Return immediately if anOrganism or organisms null
		if (anOrganism == null)
		{
			return false;
		}

		// Ok - remove element
		boolean result = organisms.removeElement(anOrganism);
		anOrganism.removePropertyChangeListener(this);
		
		// Force a repaint
		repaint();

		// Update scroll bars
		updateScrollBars();

		// Notify listeners if the organism was truly removed
		if (result == true)
		{
			changes.firePropertyChange(UIProp.ORGANISM_REMOVED_FROM_VIEW,null,anOrganism);
		}

		return result;
	}

	/**
	 * Remove all organisms
	**/
	public void removeAllOrganisms()
	{
		// Switch vectors to effectively clear out organisms vector
		Vector organismsToRemove = organisms;
		organisms = new Vector();

		// Notify listeners of organisms being removed
		Organism anOrganism;
		Enumeration eOrganisms = organismsToRemove.elements();
		while (eOrganisms.hasMoreElements())
		{
			anOrganism = (Organism) eOrganisms.nextElement();
			changes.firePropertyChange(UIProp.ORGANISM_REMOVED_FROM_VIEW,null,anOrganism);
		}
		organismsToRemove.removeAllElements();
		organismsToRemove = null;

		// Update scroll bars
		updateScrollBars();
	}

	/**
	 * Get the number of organisms in this view.
	 *
	 * @return		int - number of organisms in this view (0 or greater)
	**/
	public int getNumberOfOrganisms()
	{
		if (organisms == null)
		{
			return 0;
		}

		return organisms.size();
	}

	/**
	 * Get the set of organisms in this view.  A clone is returned, enabling
	 * you to delete the organisms without causing bugs in this code.<p>
	 *
	 * @return		Enumeration - enumeration over the set of organisms in this view
	**/
	public Enumeration getOrganisms()
	{
		// If organisms is null, return empty enumeration
		if (organisms == null)
		{
			Vector v = new Vector();
			return v.elements();
		}

		Vector organismsClone = (Vector) organisms.clone();
		return organismsClone.elements();
	}

	/**
	 * Get the current selection set
	 *
	 * @return		SelectionSet - the current selection set
	**/
	public SelectionSet getSelectionSet()
	{
		return selectionSet;
	}

	/**
	 * Set the current selection set.
	 *
	 * @param		aSelectionSet SelectionSet - a new selection set
	**/
	public void setSelectionSet(SelectionSet aSelectionSet)
	{
		// If selection set hasn't changed, return immediately
		if (aSelectionSet == selectionSet)
		{
			return;
		}

		// Validate input arguments
		if (aSelectionSet == null)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		// Make change
		selectionSet = aSelectionSet;

		// Repaint view, which will cause scrollbars to update if necessary
		repaint();

		// Don't notify listeners, as this isn't an event that anyone cares about
	}

	/**
	 * Get the current organism layout style.
	 *
	 * @return		int - current organism layout style
	**/
	public int getOrganismLayoutStyle()
	{
		return organismLayoutStyle;
	}

	/**
	 * Set the current organism layout style.
	 *
	 * @param		anOrganismLayoutStyle int - a new organism layout style
	**/
	public void setOrganismLayoutStyle(int anOrganismLayoutStyle)
	{
		// If organism layout style hasn't changed, return immediately
		if (anOrganismLayoutStyle == organismLayoutStyle)
		{
			return;
		}

		// Validate input arguments
		if (anOrganismLayoutStyle < ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP ||
			anOrganismLayoutStyle > ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_NO_WRAP)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		// Ok to change, make change
		int oldOrganismLayoutStyle = organismLayoutStyle;
		organismLayoutStyle = anOrganismLayoutStyle;

		// Force repaint, which should cause scrollbars to update if necessary
		repaint();

		// Notify listeners
		changes.firePropertyChange(UIProp.ORGANISM_LAYOUT_STYLE,
								   new Integer(oldOrganismLayoutStyle),
								   new Integer(organismLayoutStyle));
	}

	/**
	 * Update the scroll bar extents based on the number of organisms
	**/
	public void updateScrollBars()
	{
		// If this view isn't in a scroll pane or the preferred height and width
		// are already large enough, return immediately.
		if (scrollPane == null ||
			(preferredHeight == yMaximumDrawnOrganisms &&
			 preferredWidth == xMaximumDrawnOrganisms))
		{
			return;
		}

		// Update scrollbars
		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		boolean needToRevalidate = false;

		if (yMaximumDrawnOrganisms != preferredHeight)
		{
			preferredHeight = yMaximumDrawnOrganisms;
			needToRevalidate = true;

			if (verticalScrollBar != null)
			{
				verticalScrollBar.setMaximum(preferredHeight);
			}
		}
		if (xMaximumDrawnOrganisms != preferredWidth)
		{
			preferredWidth = xMaximumDrawnOrganisms;
			needToRevalidate = true;

			if (horizontalScrollBar != null)
			{
				horizontalScrollBar.setMaximum(preferredWidth);
			}
		}

		if (verticalScrollBar != null)
		{
			verticalScrollBar.setUnitIncrement(30);
		}
		if (horizontalScrollBar != null)
		{
			horizontalScrollBar.setUnitIncrement(30);
		}

		// Revalidate if we need to because a preferred width or height changed
		if (needToRevalidate)
		{
			revalidate();
		}
	}

	/**
	 * Draw the graphics in this view.
	 *
	 * @param 		g Graphics - the given graphics to use in drawing
	**/
	public void paintComponent(Graphics g)
	{
		// Get bounds and paint background
		Rectangle bounds = getBounds();
		paintBackground(g,bounds);

		// Set font and color
		g.setFont(getFont());
		g.setColor(getForeground());

		// Make sure we have fontMetrics defined
		if (fontMetrics == null)
		{
			updateFont(g);
		}

		xMaximumDrawnOrganisms = 0;
		yMaximumDrawnOrganisms = 0;
		Dimension cellSize;

		// Draw organisms one at a time, putting 3 in each row
		if (organisms != null && organisms.size() > 0)
		{
			int iOrganism = 0;
			int maxCellHeight = 0;
			int maxCellWidth = 0;
			int xLeftCell = 0;
			int yTopCell = 0;
	
			Organism organism;
			Enumeration eOrganisms = organisms.elements();
			
			while (eOrganisms.hasMoreElements())
			{
				// Paint organism
				organism = (Organism) eOrganisms.nextElement();
				cellSize = paintOrganism(g,organism,xLeftCell,yTopCell,fontHeight,
										 selectionSet.contains(organism));

				// Update max cell dimensions
				if (cellSize.width > maxCellWidth)
				{
					maxCellWidth = cellSize.width;
				}
				if (cellSize.height > maxCellHeight)
				{
					maxCellHeight = cellSize.height;
				}

				// Calculate new maximums, used for scrollbars
				if (xMaximumDrawnOrganisms < (xLeftCell + maxCellWidth))
				{
					xMaximumDrawnOrganisms = xLeftCell + maxCellWidth;
				}
				if (yMaximumDrawnOrganisms < (yTopCell + maxCellHeight))
				{
					yMaximumDrawnOrganisms = yTopCell + maxCellHeight;
				}

				// Calculate position of next organism, based on organism layout style
				iOrganism++;

				if (organismLayoutStyle == ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP ||
					organismLayoutStyle == ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_NO_WRAP)
				{
					xLeftCell += cellSize.width;	// Normally put next organism to the right of current one
					
					// Check if we might go off right edge of view for the next organism, wrap if we might
					if ((xLeftCell > (bounds.width - maxCellWidth)) &&
						organismLayoutStyle == ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP)
					{
						// Reset to draw the next organism in the next row.
						xLeftCell = 0;
						yTopCell += maxCellHeight;
						yMaximumDrawnOrganisms = yTopCell + maxCellHeight;
						maxCellWidth = 0;
						maxCellHeight = 0;
					}
				}
				else if (organismLayoutStyle == ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_WRAP ||
						 organismLayoutStyle == ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_NO_WRAP)
				{
					yTopCell += cellSize.height;

					// If style is a wrapping one and we're about to go off the bottom edge of the view, wrap
					if ((yTopCell > (bounds.height - cellSize.height)) &&
						organismLayoutStyle == ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_WRAP)
					{
						xLeftCell += maxCellWidth;
						xMaximumDrawnOrganisms = xLeftCell + maxCellWidth;
						yTopCell = 0;
						maxCellWidth = 0;
						maxCellHeight = 0;
					}
				}
			}
		}

		// Check if we need to update scrollbars
		if (scrollPane != null &&
			(preferredHeight != yMaximumDrawnOrganisms ||
			 preferredWidth != xMaximumDrawnOrganisms))
		{
			updateScrollBars();
		}
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
		else if (aTool == Tool.PEDIGREE)
		{
			// Pedigree tool
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
		// Ignore mouse events if no organisms in view or selection mode is no organisms
		if (organisms == null || organisms.size() == 0 ||
			selectionSet.getSelectionMode() == SelectionSet.SELECTION_MODE_ZERO_OBJECTS)
		{
			return;
		}

		// Get view bounds
		Rectangle bounds = getBounds();

		// Is this shift key down?
		boolean shiftDown = event.isShiftDown();

		// Determine which dragon was selected
		int xMouse = event.getX();
		int yMouse = event.getY();

		// Determine which organism was selected, using same algorithm as paintComponent
		int maxCellHeight = 0;
		int maxCellWidth = 0;
		int xLeftCell = 0;
		int yTopCell = 0;

		Dimension cellSize;

		Organism organism;
		Enumeration eOrganisms = organisms.elements();
		while (eOrganisms.hasMoreElements())
		{
			organism = (Organism) eOrganisms.nextElement();

			cellSize = getOrganismCellDimensions(organism, xLeftCell, yTopCell, fontHeight, false);

			// Select organism if mouse within cell
			if (xLeftCell <= xMouse &&
				(xLeftCell + cellSize.width) > xMouse &&
				yTopCell <= yMouse &&
				(yTopCell + cellSize.height) > yMouse)
			{
				if (activeTool == Tool.SELECTION)
				{
					// Determine if the click is on a hotspot and turn on the hotspot
					// image if it is.  Finally select the organism and return.
					updateHotspotOnMousePress(organism,xMouse-xLeftCell,yMouse-yTopCell);
					selectionSet.selectObject(organism,shiftDown,shiftDown);
				}
				else if (activeTool == Tool.SNIP)
				{
					if (organism.isManualLocked() == false)
					{
						organism.delete();
						organism = null;
					}
				}
				else if (activeTool == Tool.CHROMOSOME)
				{
					// Notify listeners that a chromosome tool event happened on organism
					changes.firePropertyChange(UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM,
											   null, organism);
				}
				else if (activeTool == Tool.PEDIGREE)
				{
					// Notify listeners that a pedigree tool event happened on organism
					changes.firePropertyChange(UIProp.PEDIGREE_TOOL_PICK_ON_ORGANISM,
											   null, organism);
				}
				return;
			}

			// Update max cell dimensions
			if (cellSize.width > maxCellWidth)
			{
				maxCellWidth = cellSize.width;
			}
			if (cellSize.height > maxCellHeight)
			{
				maxCellHeight = cellSize.height;
			}

			// Calculate position of next organism
			if (organismLayoutStyle == ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP ||
				organismLayoutStyle == ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_NO_WRAP)
			{
				xLeftCell += cellSize.width;	// Normally put next organism to the right of current one
				
				// Check if we're going off right edge of view
				if (xLeftCell > (bounds.width - cellSize.width))
				{
					// If style is a wrapping one, wrap
					if (organismLayoutStyle == ORGANISM_LAYOUT_STYLE_LEFT_TO_RIGHT_WRAP)
					{
						// Reset to draw the next organism in the next row.
						xLeftCell = 0;
						yTopCell += maxCellHeight;
						maxCellWidth = 0;
						maxCellHeight = 0;
					}
				}
			}
			else if (organismLayoutStyle == ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_WRAP ||
					 organismLayoutStyle == ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_NO_WRAP)
			{
				yTopCell += cellSize.height;		// Normally put next organism under previous one
				
				// Check if we're about to go off the bottom of the view
				if (yTopCell > (bounds.height - cellSize.height))
				{
					// If style is a wrapping one, wrap
					if (organismLayoutStyle == ORGANISM_LAYOUT_STYLE_TOP_TO_BOTTOM_WRAP)
					{
						xLeftCell += maxCellWidth;
						yTopCell = 0;
						maxCellWidth = 0;
						maxCellHeight = 0;
					}
				}
			}
		}

		if (activeTool == Tool.SELECTION)
		{
			// Did not click on any organism, so deselect all objects
			selectionSet.deselectAllObjects();
		}
	}

	/**
	 * Handle mouse released event
	**/
	public void mouseReleased(MouseEvent event)
	{
		updateHotspotOnMouseReleased();
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
	 * Selection changed notification
	**/
	public void selectionChanged()
	{
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

		if (propertyName.equals(EngineProp.ORGANISM_GENOTYPE_AND_PHENOTYPE) ||
			propertyName.equals(EngineProp.LOCKED_STATE) ||
			propertyName.equals(EngineProp.VISIBLE))
		{
			// Force repaint
			repaint();
		}
		else if (propertyName.equals(EngineProp.DELETED))
		{
			// Remove deleted organism from this view
			Object object = event.getSource();
			if (object instanceof Organism)
			{
				Organism eventOrganism = (Organism) object;
				removeOrganism(eventOrganism);
			}
		}
	}
}

