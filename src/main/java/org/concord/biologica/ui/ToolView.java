//
// Class : ToolView - the tool view in the BioLogica user interface
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2002/02/20 17:48:46 $
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
 * The tool view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2002/02/20 17:48:46 $
 * @author 		$Author: dima $
**/
public final class ToolView
extends UIView
implements ActionListener, ComponentListener
{
	/**
	 * Horizontal orientation
	**/
	public static final int TOOL_VIEW_HORIZONTAL = 1;

	/**
	 * Vertical orientation
	**/
	public static final int TOOL_VIEW_VERTICAL = 2;

	/**
	 * Active tool id
	**/
	private int activeTool;

    /**
	 * Array of tool toggle buttons
	**/
	private JToggleButton[] toolToggleButton = null;

	/**
	 * Array of tool visibilities
	**/
	private boolean[] toolVisible = null;

	/**
	 * Array of tool enabledness
	**/
	private boolean[] toolEnabled = null;

    /**
	 * Orientation of view - horizontal or vertical
	**/
	private int orientation;

	/**
	 * Views for which we'll change the cursor.<p>
     *
	 * All of these views must be instances of
	 * java.awt.Container.  This means any Swing
	 * JComponent is a valid view.<p>
	**/
	private Vector cursorViews;

	/**
	 * Views which we'll notify when the tool changes.<p>
	 *
	 * This list is a proper subset of the cursorViews
	 * list, in that every view to notify when the
	 * tool changes must also be on the cursorViews list.<p>
	 *
	 * All of these views must be instances of
	 * org.concord.biologica.ui.UIView.<p>
	**/
	private Vector toolViews;

	/**
	 * Creates a tool view.
	 *
	 * @param    anOrientation int - horizontal or vertical
	 * @see      org.concord.biologica.ui.ToolView#TOOL_VIEW_HORIZONTAL
	 * @see      org.concord.biologica.ui.ToolView#TOOL_VIEW_VERTICAL
	 * @exception 	IllegalArgumentException - input argument(s) illegal
	**/
	public ToolView()
	{
		this(TOOL_VIEW_HORIZONTAL);
	}
	
	public ToolView(int anOrientation)
	{
		super();

        if (anOrientation != TOOL_VIEW_HORIZONTAL &&
			anOrientation != TOOL_VIEW_VERTICAL)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		// Set colors
		setBackground(Color.lightGray);
		setForeground(Color.black);

		activeTool = Tool.NO_TOOL;
		orientation = anOrientation;
		cursorViews = new Vector();
		toolViews = new Vector();

        toolToggleButton = new JToggleButton[Tool.NUMBER_OF_TOOLS];
        toolEnabled = new boolean[Tool.NUMBER_OF_TOOLS];
        toolVisible = new boolean[Tool.NUMBER_OF_TOOLS];

		// Turn on double buffering
		setDoubleBuffered(true);

		// Listen for resize events
		addComponentListener(this);
	}

    /**
     * Return the preferred size of this application
     *
     * @return		Dimension - preferred size of application
    **/
    public Dimension getPreferredSize()
    {
		Dimension size = null;
		
		if (orientation == TOOL_VIEW_VERTICAL)
		{
			size = new Dimension(36,130);
		}
		else
		{
			size = new Dimension(130,36);
		}

		return size;
    }

    /**
	 * Get the orientation of the view - horizontal or vertical.
	 *
	 * @return   int - horizontal or vertical
	 * @see      org.concord.biologica.ui.ToolView#TOOL_VIEW_HORIZONTAL
	 * @see      org.concord.biologica.ui.ToolView#TOOL_VIEW_VERTICAL
	**/
	public int getOrientation()
	{
		return orientation;
	}
	
    /**
	 * Set the orientation of the view - horizontal or vertical.
	 *
	 * @param    anOrientation int - horizontal or vertical
	 * @see      org.concord.biologica.ui.ToolView#TOOL_VIEW_HORIZONTAL
	 * @see      org.concord.biologica.ui.ToolView#TOOL_VIEW_VERTICAL
	 * @exception 	IllegalArgumentException - input argument(s) illegal
	**/
	public void setOrientation(int anOrientation)
	{
	   // Return immediately if no change 
	   if (anOrientation == orientation)
	   {
		   return;
	   }

	   // Check for illegal value
	   if (anOrientation != TOOL_VIEW_HORIZONTAL &&
		   anOrientation != TOOL_VIEW_VERTICAL)
	   {
		   throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
	   }

	   // Make change
	   orientation = anOrientation;

       // Force an update of the size
	   updateSize();
	   revalidate();
	}

    /**
	 * Add the given tool to the view
	 *
	 * @param      aTool int - a tool to add
	**/
	public void addTool(int aTool)
	{
		// Return immediately if bad tool
		if (!Tool.validTool(aTool))
		{
			return;
		}

		// Create tool toggle button if not already created
		if (toolToggleButton[aTool] == null)
		{
			Insets insets = new Insets(2,2,2,2);
			toolToggleButton[aTool] = new JToggleButton(Tool.getImageIcon(aTool));
			toolToggleButton[aTool].setMargin(insets);
			toolToggleButton[aTool].addActionListener(this);
			toolToggleButton[aTool].setActionCommand(Tool.getCommandString(aTool));
			toolToggleButton[aTool].setFocusPainted(false);
			toolToggleButton[aTool].setToolTipText(Tool.getToolTipString(aTool));
			if (activeTool == Tool.NO_TOOL)
			{
				activeTool = aTool;
				toolToggleButton[aTool].setSelected(true);
			}
			else
			{
				toolToggleButton[aTool].setSelected(false);
			}
			toolToggleButton[aTool].setVisible(true);
			toolToggleButton[aTool].setEnabled(true);
			toolVisible[aTool] = true;
			toolEnabled[aTool] = true;
			add(toolToggleButton[aTool]);
		}
	}

    /**
	 * Remove the given tool from the view
	 *
	 * @param      aTool int - a tool to add
	**/
	public void removeTool(int aTool)
	{
		// Return immediately if bad tool
		if (!Tool.validTool(aTool))
		{
			return;
		}

		// Create tool toggle button if not already created
		if (toolToggleButton[aTool] != null)
		{
			remove(toolToggleButton[aTool]);
			toolToggleButton[aTool] = null;

			// If we just removed the active tool, make the
			// first other tool active
			if (activeTool == aTool)
			{
				int i;
				for (i=0;i<Tool.NUMBER_OF_TOOLS;i++)
				{
					if (toolToggleButton[aTool] != null)
					{
						activeTool = i;
						toolToggleButton[i].setSelected(true);
						return;
					}
				}
			}
		}
	}

	/**
	 * Get the active tool id.
	 *
	 * @return		int - the active tool
	**/
	public int getActiveTool()
	{
		return activeTool;
	}

	/**
	 * Set the active tool
	 *
	 * @param		anActiveTool int - the new active tool
	**/
	public void setActiveTool(int anActiveTool)
	{
		// Return immediately if not a change
		if (anActiveTool == activeTool)
		{
			return;
		}

		// Save old tool and make change
		int oldActiveTool = activeTool;
		activeTool = anActiveTool;

        // Update tool buttons and cursor
		int i;
		for (i=0;i<Tool.NUMBER_OF_TOOLS;i++)
		{
			if (toolToggleButton[i] != null)
			{
				if (i == activeTool)
				{
					toolToggleButton[i].setSelected(true);
				}
				else
				{
					toolToggleButton[i].setSelected(false);
				}
			}
		}

		setCursor(Tool.getCursor(activeTool));

		// Set cursor of cursor views
		Container aContainer;
		Enumeration eCursorViews = cursorViews.elements();
		while (eCursorViews.hasMoreElements())
		{
			aContainer = (Container) eCursorViews.nextElement();
			aContainer.setCursor(Tool.getCursor(activeTool));
		}

		// Notify tool views that tool changed
		UIView aToolView;
		Enumeration eToolViews = toolViews.elements();
		while (eToolViews.hasMoreElements())
		{
			aToolView = (UIView) eToolViews.nextElement();
			aToolView.toolChanged(activeTool);
		}

		// Notify listeners
		changes.firePropertyChange(UIProp.ACTIVE_TOOL,
								   new Integer(oldActiveTool),
								   new Integer(activeTool));
	}

	/**
	 * Is the tool enabled?
	 *
	 * @param       aTool int - tool id
	 * @return		boolean - is the tool enabled?
	**/
	public boolean isToolEnabled(int aTool)
	{
		if (Tool.validTool(aTool))
		{
			return toolEnabled[aTool];
		}
		return false;
	}

	/**
	 * Set the tool enabled state.
	 *
	 * @param       aTool int - tool id
	 * @param		aToolEnabled boolean - enabled?
	**/
	public void setToolEnabled(int aTool, boolean aToolEnabled)
	{
		// Return immediately if bad tool
		if (!Tool.validTool(aTool))
		{
			return;
		}

		// Return immediately if no change of state
		if (toolEnabled[aTool] == aToolEnabled)
		{
			return;
		}

		// Change
		boolean oldToolEnabled = toolEnabled[aTool];
		toolEnabled[aTool] = aToolEnabled;

		// Enable/Disable tool
		toolToggleButton[aTool].setEnabled(aToolEnabled);

		// Notify listeners
		changes.firePropertyChange(Tool.getEnabledPropertyChangeEvent(aTool),
								   new Boolean(oldToolEnabled),
								   new Boolean(toolEnabled[aTool]));
	}

	/**
	 * Is the tool visible?
	 *
	 * @param       aTool int - tool id
	 * @return		boolean - is the tool visible?
	**/
	public boolean isToolVisible(int aTool)
	{
		if (Tool.validTool(aTool))
		{
			return toolVisible[aTool];
		}
		return false;
	}

	/**
	 * Set the tool visibility.
	 *
	 * @param		aToolVisible boolean - visible?
	**/
	public void setToolVisible(int aTool, boolean aToolVisible)
	{
		// Return immediately if bad tool
		if (!Tool.validTool(aTool))
		{
			return;
		}

		// Return immediately if no change of state
		if (toolVisible[aTool] == aToolVisible)
		{
			return;
		}

		// Change
		boolean oldToolVisible = toolVisible[aTool];
		toolVisible[aTool] = aToolVisible;

		// Show/Hide selection tool
		toolToggleButton[aTool].setVisible(aToolVisible);
		updateSize();

		// Force repaint
		repaint();

		// Notify listeners
		changes.firePropertyChange(Tool.getVisiblePropertyChangeEvent(aTool),
								   new Boolean(oldToolVisible),
								   new Boolean(toolVisible[aTool]));
	}

    /**
	 * Add a view to the list of views that care about which
	 * tool in this tool view is active.<p>
	 *
	 * If the given view is a UIView, it will be notified when
	 * the tool changes.<p>
	 *
	 * All views are told to set their cursor, even views that
	 * are not UIView's.<p>
	 *
	 * @param    aView Container - a new view to add, should not be null
	**/
	public void addView(Container aView)
	{
		if (aView == null)
		{
			return;
		}

		if (!cursorViews.contains(aView))
		{
			cursorViews.addElement(aView);
			if (activeTool != Tool.NO_TOOL)
			{
				aView.setCursor(Tool.getCursor(activeTool));
			}

			if (aView instanceof UIView &&
				(!toolViews.contains(aView)))
			{
				toolViews.addElement(aView);

				UIView toolView = (UIView) aView;
				toolView.toolChanged(activeTool);
			}
		}
	}

    /**
	 * Remove a view to the list of views that care about which
	 * tool in this tool view is active.<p>
	 *
	 * @param    aView Container - a new view to add, should not be null
	**/
	public void removeView(Container aView)
	{
		if (aView == null)
		{
			return;
		}

		cursorViews.removeElement(aView);
        toolViews.removeElement(aView);
	}

	/**
	 * Update the size of this view.  To do this, get the current size of the view
	 * and update the models for anything shown in the view.  Do not repaint or generate
	 * a repaint event, as that event is already coming automatically from AWT.
	**/
	public void updateSize()
	{
		Rectangle bounds = getBounds();
		int i;

		// Move pedigree organism view, tool buttons and pulldowns appropriately
		if (orientation == TOOL_VIEW_VERTICAL)
		{
			// Vertically layout tool buttons
			int yPosition = 2;
			for (i=0;i<Tool.NUMBER_OF_TOOLS;i++)
			{
				if (toolToggleButton[i] != null &&
					toolVisible[i])
				{
					toolToggleButton[i].setBounds(2,yPosition,32,32);
					yPosition += 32;
				}
			}
		}
		else
		{
			// Horizontally layout tool buttons
			int xPosition = 2;
			for (i=0;i<Tool.NUMBER_OF_TOOLS;i++)
			{
				if (toolToggleButton[i] != null &&
					toolVisible[i])
				{
					toolToggleButton[i].setBounds(xPosition,2,32,32);
					xPosition += 32;
				}
			}
		}

		revalidate();
	}

	/**
	 * React to actions
	**/
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();

		if (cmd.equals(Tool.COMMAND_SELECTION))
		{
			setActiveTool(Tool.SELECTION);
		}
		else if (cmd.equals(Tool.COMMAND_CROSS))
		{
			setActiveTool(Tool.CROSS);
		}
		else if (cmd.equals(Tool.COMMAND_SNIP))
		{
			setActiveTool(Tool.SNIP);
		}
		else if (cmd.equals(Tool.COMMAND_CHROMOSOME))
		{
			setActiveTool(Tool.CHROMOSOME);
		}
		else if (cmd.equals(Tool.COMMAND_PEDIGREE))
		{
			setActiveTool(Tool.PEDIGREE);
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
}

