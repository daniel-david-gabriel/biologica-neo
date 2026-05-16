//
// Class : PedigreeObject - the common base class for all graphical objects shown in the pedigree view
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.6 $
// $Date: 2001/09/21 18:29:22 $
// $Author: bdias $
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

import org.concord.biologica.engine.*;

/**
 * An object that manages the state for a graphical object shown in the pedigree view.
 *
**/
public abstract class PedigreeObject
{
    /**
     * The X location of the top left corner of object 
    **/
    protected int xLocation;

    /**
     * The Y location of the top left corner of the object 
    **/
    protected int yLocation;

    /**
     * The width of the pedigree object as drawn
    **/
    protected int width;

    /**
     * The height of the pedigree object as drawn
    **/
    protected int height;

    /**
     * Default constructor
    **/
    public PedigreeObject()
    {
    }
    
    /**
     * Get the X location
     *
     * @return		int - x location
    **/
    public int getXLocation()
    {
        return xLocation;
    }

    /**
     * Set the X location
     *
     * @param		anXLocation int - x location
    **/
    public void setXLocation(int anXLocation)
    {
        xLocation = anXLocation;
    }

    /**
     * Get the Y location
     *
     * @return		int - y location
    **/
    public int getYLocation()
    {
        return yLocation;
    }

    /**
     * Set the Y location
     *
     * @param		aYLocation int - y location
    **/
    public void setYLocation(int aYLocation)
    {
        yLocation = aYLocation;
    }

    /**
     * Get the width
     *
     * @return		int - width
    **/
    public int getWidth()
    {
        return width;
    }

    /**
     * Set the width
     *
     * @param		aWidth int - width
    **/
    public void setWidth(int aWidth)
    {
        width = aWidth;
    }

    /**
     * Get the height
     *
     * @return		int - height
    **/
    public int getHeight()
    {
        return height;
    }

    /**
     * Set the height
     *
     * @param		aHeight int - height
    **/
    public void setHeight(int aHeight)
    {
        height = aHeight;
    }

    /**
     * Get the top left point of the pedigree object.
     *
     * @return		Point - top left point of the object 
    **/
    public Point getTopLeftPoint()
    {
        return new Point(xLocation,yLocation);
    }

    /**
     * Get midpoint along bottom of object .
     *
     * @return		Point - midpoint along bottom of object
    **/
    public Point getBottomMidPoint()
    {
        return new Point(xLocation + (width/2),
                         yLocation + height);
    }

    /**
     * Get midpoint along top of object .
     *
     * @return		Point - midpoint along top of object
    **/
    public Point getTopMidPoint()
    {
        return new Point(xLocation + (width/2),
                         yLocation);
    }


    /**
     * Virtual methods - subclasses should override to provide specialized behavior
    **/

    /**
     * Get the minimum and maximum positions
    **/
    public void getMinimumMaximum(Point aMinPoint, Point aMaxPoint)
    {
        if (xLocation < aMinPoint.x)
        {
            aMinPoint.x = xLocation;
        }
        else if (xLocation + width > aMaxPoint.x)
        {
            aMaxPoint.x = xLocation + width;
        }

        if (yLocation < aMinPoint.y)
        {
            aMinPoint.y = yLocation;
        }
        else if (yLocation + height > aMaxPoint.y)
        {
            aMaxPoint.y = yLocation + height;
        }
    }

    /**
     * Translate the organism by the given amount
     *
     * @param		xDelta int - x delta
     * @param		yDelta int - y delta
    **/
    public void translate(int xDelta, int yDelta)
    {
        xLocation += xDelta;
        yLocation += yDelta;
    }

    /**
     * Pick this object, determining if the given x,y location is on the object.<p>
     *
     * @param		xPick int - x location of pick
     * @param		yPick int - y location of pick
     * @return		boolean - is object at the given pick location
    **/
    public boolean pick(int xPick, int yPick)
    {
        if (xLocation <= xPick && (xLocation + width) > xPick &&
            yLocation <= yPick && (yLocation + height) > yPick)
        {
            return true;
        }

        return false;
    }

    /**
     * Is this organism within the given rectangle?  We'll consider a organism
     * within the rectangle if the rectangle contains some portion of the organism.<p>
     *
     * @param		xTopLeft int - x top left coordinate
     * @param		yTopLeft int - y top left coordinate
     * @param		xBottomRight int - x bottom right coordinate
     * @param		yBottomRight int - y bottom right coordinate
     * @return		boolean - is the object within the given rectangle?
    **/
    public boolean within(int xTopLeft, int yTopLeft, int xBottomRight, int yBottomRight)
    {
        if ((xTopLeft <= (xLocation+width) && xBottomRight >= xLocation) &&
            (yTopLeft <= (yLocation+height) && yBottomRight >= yLocation))
        {
            return true;
        }

        return false;
    }

    /**
     * Can this object be squeezed? Mainly used for PedigreeFamilies.
     *
     * @return		boolean - true or false. Default implementation returns false.
    **/
    public boolean canSqueeze()
    {
        return false;
    }
    
    /**
     * Squeeze object. Mainly used for PedigreeFamilies.
     *
     * @return		Dimension - resulting dimension for placement of object.
    **/
    public Dimension squeeze()
    {
        return placeChildren();
    }
    
    /**
     * Place children. Mainly used for PedigreeFamilies.
     *
     * @return		Dimension - resulting dimension for placement of object.
    **/
    public Dimension placeChildren()
    {
        setXLocation(0);
        setYLocation(0);
        return new Dimension(width, height);
    }

    /**
     * Reset squeeze factor. Mainly used for PedigreeFamilies.
     *
    **/
    public void resetSqueezeFactor()
    {
    }

    /**
     * Get X position of leftmost parent. Mainly used for PedigreeFamilies.
     *
     * @return		int - X position. Default implementation returns -1.
    **/
    public int getLeftMostParentX()
    {
        return -1;
    }
    
    /**
     * Get X position of rightmost parent. Mainly used for PedigreeFamilies.
     *
     * @return		int - X position. Default implementation returns -1.
    **/
    public int getRightMostParentX()
    {
        return -1;
    }
    
    /**
     * Place the connections. Mainly used for PedigreeFamilies.
     *
    **/
    public void placeConnections()
    {
    }
    
    /**
     * Paint the connections. Mainly used for PedigreeFamilies.
     *
     * @param		g Graphics - graphics object to use in painting
     * @param		pedigreeOrganismView PedigreeOrganismView - the pedigree organism view
     * @param		trait Trait - the trait to show, may be null
     * @param		fontHeight int - the font height
     * @param		selectionSet selectionSet - view's selection set
    **/
    public void paintConnections(Graphics g, PedigreeOrganismView aPedigreeOrganismView, Trait trait,
                                 int fontHeight, SelectionSet selectionSet, SelectionSet highlightSet)
    {
    }

    /**
     * Abstract methods - must be implemented in subclasses
    **/

    /**
     * Paint the object with the given Graphics object on the
     * given PedigreeOrganismView.<p>
     *
     * @param		g Graphics - graphics object to use in painting
     * @param		pedigreeOrganismView PedigreeOrganismView - the pedigree organism view
     * @param		trait Trait - the trait to show, may be null
     * @param		fontHeight int - the font height
     * @param		selectionSet selectionSet - view's selection set
    **/
    public abstract void paint(Graphics g, PedigreeOrganismView pedigreeOrganismView, Trait trait,
                                int fontHeight, SelectionSet selectionSet, SelectionSet highlightSet);
}


