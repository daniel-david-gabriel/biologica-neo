//
// Class : SpindleLine
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:21 $
// $Author: ed $
//

package org.concord.biologica.ui;

import org.concord.biologica.engine.*;

/**
 * SpindleLine has the information necessary to draw a line to represent
 * a portion of a spindle.  This is used by the MeiosisModel
 * code to help views paint the spindles during meiosis.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:21 $
 * @author 		$Author: ed $
**/
public final class SpindleLine
{
	private int x1;
	private int y1;
	private int x2;
	private int y2;

	/**
	 * Create a new SpindleLine
	**/
	public SpindleLine(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * Return x1
	**/
	public int getX1()
	{
		return x1;
	}

	/**
	 * Return y1
	**/
	public int getY1()
	{
		return y1;
	}

	/**
	 * Return x2
	**/
	public int getX2()
	{
		return x2;
	}

	/**
	 * Return y2
	**/
	public int getY2()
	{
		return y2;
	}
}

