//
// Class : BioToolBar - A small override of JToolBar to get alignment correct
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:17 $
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

/**
 * This class represents a toolbar in BioLogica.
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:17 $
 * @author 		$Author: ed $
**/

public class BioToolBar extends JToolBar
{
	/**
	 * Add a component to the toolbar.
	**/
	public Component add(Component c)
	{
		if (c instanceof JComponent)
		{
			JComponent jc = (JComponent) c;
			jc.setAlignmentY((float)0.5);
			jc.setAlignmentX((float)0.5);
		}

		return super.add(c);
	}
}

