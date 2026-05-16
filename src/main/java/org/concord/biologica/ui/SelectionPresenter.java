//
// Class : SelectionPresenter - Selection presenter interface, implemented by a class of objects
//								that present a selection set somehow.  Normally this interface
//								is implemented by a view class that presents objects graphically.<p>
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:20 $
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
 * The selection presenter interface, implemented by an object which presents a set of selected engine objects.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:20 $
 * @author 		$Author: ed $
**/
public interface SelectionPresenter
{
	/**
	 * Selection changed.  Called by a SelectionSet to cause the presenter
	 * to re-present the selected objects.<p>
	**/
	public void selectionChanged();

	/**
	 * Get the current selection set.
	 *
	 * @return		SelectionSet - the current selection set
	**/
	public SelectionSet getSelectionSet();

	/**
	 * Set the current selection set.
	 *
	 * @param		aSelectionSet SelectionSet - a new selection set
	**/
	public void setSelectionSet(SelectionSet aSelectionSet);
}

