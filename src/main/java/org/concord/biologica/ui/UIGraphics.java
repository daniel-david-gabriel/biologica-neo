//
// Class : UIGraphics - Various graphics constants and methods to access them
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

/**
 * BioLogica graphics colors, fonts and other static stuff and methods.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:21 $
 * @author 		$Author: ed $
**/
public final class UIGraphics
{
	/**
	 * Font - bold 12
	**/
	private static Font fontBold12 = null;

	/**
	 * Returns bold size 12 font
	 *
	 * @return		Font - bold size 12 font
	**/
	public static Font getFontBold12()
	{
		if (fontBold12 == null)
		{
			fontBold12 = new Font("SansSerif", Font.BOLD, 12);
		}
 		
		return fontBold12;
	}
}

