//
// Class : FilledCircle
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:18 $
// $Author: ed $
//

package org.concord.biologica.ui;

import org.concord.biologica.engine.*;

/**
 * FilledCircle has the information necessary to draw a filled circle
 * to represent a portion of a cell boundary.  This is used by the
 * MeiosisModel and FertilizationModel code to help views paint the
 * cell during meiosis and fertilization.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:18 $
 * @author 		$Author: ed $
**/
public final class FilledCircle
{
	private int xLeft;
	private int yTop;
	private int width;
	private int height;

	/**
	 * Create a new FilledCircle
	**/
	public FilledCircle(int xLeft, int yTop, int width, int height)
	{
		this.xLeft = xLeft;
		this.yTop = yTop;
		this.width = width;
		this.height = height;
	}

	/**
	 * Return xLeft
	**/
	public int getXLeft()
	{
		return xLeft;
	}

	/**
	 * Return yTop
	**/
	public int getYTop()
	{
		return yTop;
	}

	/**
	 * Return width
	**/
	public int getWidth()
	{
		return width;
	}

	/**
	 * Return height
	**/
	public int getHeight()
	{
		return height;
	}
}

