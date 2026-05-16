//
// Class : TreeView - the BioLogica tree view
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
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

/**
 * The tree view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.ROOT_ENGINE_OBJECT - the root engine object of this view changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#ROOT_ENGINE_OBJECT
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:21 $
 * @author 		$Author: ed $
**/
public final class TreeView
extends JScrollPane
implements PropertyChangeListener, TreeSelectionListener, SelectionPresenter
{
	/**
	 * Default selection color
	**/
	static private Color defaultSelectionColor = Color.yellow;

	/**
	 * The engine object to show as the root of the tree in this view
	**/
	private EngineObject rootEngineObject = null;
	
	/**
	 * The tree itself, a child of the scroll pane.
	**/
	private JTree tree = null;

	/**
	 * The tree model
	**/
	private TreeViewModel treeModel = null;

	/**
	 * Row height
	**/
	private int rowHeight = 20;

	/**
	 * Utility object which manages property change events and listeners.
	 * Needed here since we don't inherit from UIView.
	**/
	protected transient PropertyChangeSupport changes = null;

	/**
	 * Selection set
	**/
	private SelectionSet selectionSet = null;

	/**
	 * In selectionChanged method?
	**/
	private boolean inSelectionChanged = false;

	/**
	 * In valueChanged method?
	**/
	private boolean inValueChanged = false;

	/**
	 * Selection color for a particular view
	**/
	protected Color selectionColor;

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
	 * Creates a species tree view.
	 *
	 * @exception	IllegalArgumentException - one of input arguments null
	**/
	public TreeView()
	{
		super(VERTICAL_SCROLLBAR_ALWAYS,HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Set selection set to default initially
		selectionSet = SelectionSet.getDefaultSelectionSet();

		changes = new PropertyChangeSupport(this);

		// Set colors
		setBackground(Color.white);
		setForeground(Color.black);
		selectionColor = defaultSelectionColor;

		// All children are null until we have a root engine object
		rootEngineObject = null;
		tree = null;

		// Tell the selection set about this view
		selectionSet.addSelectionPresenter(this);
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
	 * Get the root engine object of this view.
	 *
	 * @return		EngineObject - the root engine object of this view, may be null
	**/
	public EngineObject getRootEngineObject()
	{
		return rootEngineObject;
	}

	/**
	 * Set the root engine object of this view.
	 *
	 * @param		aRootEngineObject EngineObject - a new root engine object, may be null
	**/
	public void setRootEngineObject(EngineObject aRootEngineObject)
	{
		// Return immediately if root engine object not changing
		if (aRootEngineObject == rootEngineObject)
		{
			return;
		}

		// Save old root engine object and tree
		EngineObject oldRootEngineObject = rootEngineObject;

		// Unset old state, removing old views
		rootEngineObject = null;
		treeModel = null;
		if (tree != null)
		{
			tree.setModel(null);
			tree = null;
		}

		// Set new root engine object
		rootEngineObject = aRootEngineObject;

		// If rootEngineObject not null, create new root tree node and tree model
		if (rootEngineObject != null)
		{
			TreeViewNode rootTreeViewNode = new TreeViewNode(this,rootEngineObject);
			treeModel = new TreeViewModel(rootTreeViewNode);
			
			// Create tree and set tooltips, row height, renderer, selection model, etc.
			tree = new JTree(treeModel);
			ToolTipManager.sharedInstance().registerComponent(tree);
			tree.setRowHeight(rowHeight);
			tree.setCellRenderer(new TreeViewCellRenderer(this));
			TreeSelectionModel selectionModel = tree.getSelectionModel();
			selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			tree.addTreeSelectionListener(this);
	
			// Put tree in this scroll pane view
			setViewportView(tree);
		}
		else
		{
			// Make viewport view null
			setViewportView(null);
		}

		// Clear selection
		selectionSet.deselectAllObjects();

		// Force a revalidation
		revalidate();
	}

	/**
	 * Selection changed.  This is called by the SelectionSet object when the selection
	 * changes due to some event.<p>
	 *
	 * @param		anEngineObject EngineObject - a new engine object to select, may be null
	**/
	public void selectionChanged()
	{
		if (!inSelectionChanged && !inValueChanged && tree != null && selectionSet != null)
		{
			inSelectionChanged = true;

			// Clear selection
			tree.clearSelection();
  				
			// Select selected objects
			EngineObject aSelectedObject;
			Enumeration eSelectedObjects = selectionSet.getSelectedObjects();
			while (eSelectedObjects.hasMoreElements())
			{
				aSelectedObject = (EngineObject) eSelectedObjects.nextElement();
				aSelectedObject.notifySelected(false,true);
			}

			inSelectionChanged = false;
		}
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

		// Update view
		repaint();

		// Don't notify listeners, as this isn't an event that anyone cares about
	}

	/**
	 * Handle property change events
	 *
	 * @param	event PropertyChangeEvent - the property change event
	**/
	public void propertyChange(PropertyChangeEvent event)
	{
		String propertyName = event.getPropertyName();

		// TBD??
	}

	/**
	 * Handle tree selection change events
	 *
	 * @param	event TreeSelectionEvent - tree selection event
	**/
	public void valueChanged(TreeSelectionEvent event)
	{
		if (!inSelectionChanged && !inValueChanged)
		{
			inValueChanged = true;

			int i;
			TreePath treePath;
			TreeViewNode treeNode;
			TreePath[] treePaths = event.getPaths();
			for (i=0;i<treePaths.length;i++)
			{
				treeNode = (TreeViewNode)treePaths[i].getLastPathComponent();
				if (event.isAddedPath(treePaths[i]))
				{
					selectionSet.selectObject((EngineObject)treeNode.getUserObject(),
											  true,true);
				}
				else
				{
					selectionSet.deselectObject((EngineObject)treeNode.getUserObject());
				}
			}

			inValueChanged = false;
		}
	}

	/**
	 * Notification that a node's selected state in this tree changed.
	 *
	 * @param		aTreeViewNode TreeViewNode - the species tree node that changed
	 * @param		oldSelectedState boolean - old selected state of node
	 * @param		newSelectedState boolean - new selected state of node
	**/
	public void nodeSelectedStateChanged(TreeViewNode aTreeViewNode,
										 boolean oldSelected, boolean newSelected)
	{
		if (tree != null && aTreeViewNode != null)
		{
			TreeNode [] treeNodePath = aTreeViewNode.getPath();
			TreePath treePath = new TreePath(treeNodePath);

			if (newSelected)
			{
				tree.addSelectionPath(treePath);
			}
			else
			{
				tree.removeSelectionPath(treePath);
			}
		}
	}

	/**
	 * Notification that a node in this tree has changed.  This is usually called
	 * by the node when something significant occurs to the node (e.g. its lock
	 * state changes, its text changes, etc.).<p>
	 *
	 * @param		aTreeViewNode TreeViewNode - the species tree node that changed
	**/
	public void nodeChanged(TreeViewNode aTreeViewNode)
	{	
		if (tree != null)
		{
			((DefaultTreeModel)treeModel).nodeChanged(aTreeViewNode);
		}
    }

	/**
	 * Notification that the engine object corresponding to a
	 * node in this tree has been deleted.<p>
	 *
	 * @param		aTreeViewNode TreeViewNode - the species tree node whose object was deleted
	**/
	public void nodeObjectDeleted(TreeViewNode aTreeViewNode)
	{
		if (tree != null && aTreeViewNode != null && aTreeViewNode.getParent() != null)
		{
			try
			{
				((DefaultTreeModel)treeModel).removeNodeFromParent(aTreeViewNode);
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				// continue on??
			}
		}
	}

	/**
	 * Add a new node to the tree with the given parent node.
	 *
	 * @param		parentNode TreeViewNode - parent node
	 * @param		newNode TreeViewNode - new node to add
	 * @param		indexInParentNode - index in parent node
	**/
	public void addNode(TreeViewNode parentNode, TreeViewNode newNode, int indexInParentNode)
	{
		// Add new node to parent node
		if (tree != null)
		{
			((DefaultTreeModel)treeModel).insertNodeInto(newNode,parentNode,indexInParentNode);
		}
	}

	/**
	 * Expand the given node
	 *
	 * @param		node TreeViewNode - node to expand
	**/
	public void expandNode(TreeViewNode node)
	{
		if (tree != null)
		{
			TreeNode nodeArray[] = node.getPath();
			TreePath path = new TreePath(nodeArray);
			tree.expandPath(path);
		}
	}

	/**
	 * Add a property change listener for properties.
	 *
	 * @param	aListener PropertyChangeListener - a new listener
    **/
	public void addPropertyChangeListener(PropertyChangeListener aListener)
	{
		if (changes == null)
		{
			changes = new PropertyChangeSupport(this);
		}
		changes.addPropertyChangeListener(aListener);
	}

	/**
	 * Remove a property change listener for properties.
	 *
	 * @param	aListener PropertyChangeListener - a listener to remove
	**/
	public void removePropertyChangeListener(PropertyChangeListener aListener)
	{
		if (changes == null)
		{
			changes = new PropertyChangeSupport(this);
		}
		changes.removePropertyChangeListener(aListener);
	}
}

