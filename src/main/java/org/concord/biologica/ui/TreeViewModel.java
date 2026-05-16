//
// Class : TreeViewModel
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

import java.lang.String;
import java.util.Enumeration;
import java.util.Vector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.concord.biologica.engine.*;

/**
 * TreeViewModel extends JTreeModel to extends valueForPathChanged.
 * This method is called as a result of the user editing a value in
 * the tree.  If you allow editing in your tree, are using TreeNodes
 * and the user object of the TreeNodes is not a String, then you're going
 * to have to subclass JTreeModel as this example does.
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:21 $
 * @author 		$Author: ed $
**/
public final class TreeViewModel extends DefaultTreeModel
{
    /**
     * Creates a new instance of TreeViewModel with newRoot set
     * to the root of this model.
    **/
    public TreeViewModel(TreeNode newRoot)
	{
		super(newRoot);
    }

    /**
     * Subclassed to message setString() to the changed path item.
    **/
    public void valueForPathChanged(TreePath path, Object newValue)
	{
		// Update the user object.
		DefaultMutableTreeNode aNode = (DefaultMutableTreeNode)path.getLastPathComponent();

		// Since we've changed how the data is to be displayed, message nodeChanged.
		nodeChanged(aNode);
    }
}
