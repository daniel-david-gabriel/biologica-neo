//
// Class : CellArc
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2002/12/19 21:57:31 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.awt.Rectangle;

import org.concord.biologica.engine.*;

/**
 * CellArc has the information necessary to draw an arc to represent
 * a portion of a cell boundary.  This is used by the MeiosisModel
 * code to help views paint the cell boundary during meiosis.<p>
 *
 * @version		$Revision: 1.2 $ $Date: 2002/12/19 21:57:31 $
 * @author 		$Author: qliao $
**/
public final class CellArc
{
    private int xLeft;
    private int yTop;
    private int width;
    private int height;
    private int startAngle;
    private int spanAngle;
    private Rectangle rectangle;
    private int model;
    public static final int MEIOSIS_GAMETE_MODEL = 0;
    public static final int FERTILIZATION_MOTHER_GAMETE_MODEL = 1;
    public static final int FERTILIZATION_FATHER_GAMETE_MODEL = 2;

    /**
     * Create a new CellArc
    **/
    public CellArc(int xLeft, int yTop, int width, int height, int startAngle, int spanAngle)
    {
        this.xLeft = xLeft;
        this.yTop = yTop;
        this.width = width;
        this.height = height;
        this.startAngle = startAngle;
        this.spanAngle = spanAngle;
        rectangle = new Rectangle(xLeft, yTop, width, height);
        model = MEIOSIS_GAMETE_MODEL;
    }
    
    public boolean contains(int x, int y)
    {
        boolean test = rectangle.contains(x, y);
        return test;
    }

    public boolean contains(Rectangle r)
    {
        if (r.isEmpty())
        {
            return false;
        }
        return rectangle.intersection(r).equals(r);
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

    /**
     * Return start angle
    **/
    public int getStartAngle()
    {
        return startAngle;
    }
    /**
    * set cellArc model
    */
    public void setModel(int mod)
    {
    	model = mod;
    }
    
    /**
    *	Return cellArc model
    **/
    public int getModel()
    {
    	return model;
    }

    /**
     * Return span angle, using the same definition as
     * is used in java.awt.Graphics.drawArc().
    **/
    public int getSpanAngle()
    {
        return spanAngle;
    }
}

