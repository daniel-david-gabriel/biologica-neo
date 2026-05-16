//
// Class : WelcomeView - the welcome screen for BioLogica
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

import org.concord.biologica.ui.UIGraphics;

/**
 * The welcome view of BioLogica.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:21 $
 * @author 		$Author: ed $
**/
public final class WelcomeView extends JComponent
{
	/**
	 * Preferred width
	**/
	int preferredWidth;

	/**
	 * Preferred height
	**/
	int preferredHeight;

	/**
	 * Creates a large view with the given name and colors.
	 *
	 * @exception	IllegalArgumentException - one of input arguments null
	**/
	public WelcomeView()
	{
		preferredWidth = 200;
		preferredHeight = 300;
	}

	/**
	 * Draw the graphics in this view.
	 *
	 * @param 		g Graphics - the given graphics to use in drawing
	**/
	public void paintComponent(Graphics g)
	{
		Rectangle bounds = getBounds();

		g.setColor(Color.white);
		g.fillRect(0,0,bounds.width,bounds.height);

		// Draw welcome text
		g.setFont(UIGraphics.getFontBold12());
		g.drawString("Welcome to BioLogica!",50,40);
		g.drawString("No BioLogica World is currently open.",50,80);
		g.drawString("Please choose New World or Open World... from the",50,120);
		g.drawString("File menu to create or open a BioLogica World.",50,140);
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
}

