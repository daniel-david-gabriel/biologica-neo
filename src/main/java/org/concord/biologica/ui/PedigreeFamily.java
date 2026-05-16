//
// Class : PedigreeFamily - a small object that manages the state for a family shown in the pedigree view
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.9 $
// $Date: 2003/02/06 19:51:02 $
// $Author: qliao $
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
 * An object that manages the state for a family in the pedigree view.
 *
**/
public final class PedigreeFamily
extends PedigreeObject
{
    /**
     * The family
    **/
    private Family family;

    /**
     * Mother pedigree organism
    **/
    private PedigreeOrganism motherPedigreeOrganism;

    /**
     * Father pedigree organism
    **/
    private PedigreeOrganism fatherPedigreeOrganism;

    /**
     * Child pedigree organisms
    **/
    private Vector childPedigreeOrganisms;

    /**
     * The y location of the horizontal bar drawn for the parent of the family.
     * This is a value that can be modified directly when the user clicks and
     * drags the parent bar with the mouse.  It is not a calculated value.<p>
    **/
    private int yParentBar;

    /**
     * The x location of the vertical line between the parent and child bars
     * This is a value that can be modified directly when the user clicks and
     * drags the vertical bar with the mouse.  It is not a calculated value.<p>
    **/
    private int xVerticalBar;

    /**
     * The y location of the horizontal bar drawn for the children of the family.
     * This is a value that can be modified directly when the user clicks and
     * drags the child bar with the mouse.  It is not a calculated value.<p>
    **/
    private int yChildBar;

    /**
     * The x location of the left end of the parent bar.
     * This value is derived from the positions of the parents and is updated
     * everytime paint() is called.  It is stored here only to facilite picking.<p>
    **/
    private int xParentBarLeft = 0;

    /**
     * The x location of the right end of the parent bar.
     * This value is derived from the positions of the parents and is updated
     * everytime paint() is called.  It is stored here only to facilite picking.<p>
    **/
    private int xParentBarRight = 0;

    /**
     * The x location of the left end of the child bar.
     * This value is derived from the positions of the children and is updated
     * everytime paint() is called.  It is stored here only to facilite picking.<p>
    **/
    private int xChildBarLeft = 0;

    /**
     * The x location of the right end of the child bar.
     * This value is derived from the positions of the children and is updated
     * everytime paint() is called.  It is stored here only to facilite picking.<p>
    **/
    private int xChildBarRight = 0;
    
    /**
     * Constant used in placement algorithm
     **/
    private Dimension cellSize = null;

    /**
     * "F1", "F2" string drawn above family
     **/
    private String generationString = null;

    /**
     * Length of generation string
     **/
    private int generationStringLength = 0;
    
    /**
     * Vector of points used when painting connections
     **/
    private Vector highlightedChildrenPoints = null;
    
    /**
     * Creates a new pedigree family
     *
     * @param		pedigreeOrganismView PedigreeOrganismView - the view
     * @param		aFamily Family - the family, may not be null
     * @param		aMotherPedigreeOrganism PedigreeOrganism - mother pedigree organism, may not be null
     * @param		aFatherPedigreeOrganism PedigreeOrganism - father pedigree organism, may not be null
     * @param		aChildPedigreeOrganisms Vector - vector of child pedigree organisms, may not be null
    **/
    public PedigreeFamily(PedigreeOrganismView pedigreeOrganismView,
                   Family aFamily,
                   PedigreeOrganism aMotherPedigreeOrganism,
                   PedigreeOrganism aFatherPedigreeOrganism,
                   Vector aChildPedigreeOrganisms)
    {
        if (aFamily == null || aMotherPedigreeOrganism == null ||
            aFatherPedigreeOrganism == null || aChildPedigreeOrganisms == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        family = aFamily;
        motherPedigreeOrganism = aMotherPedigreeOrganism;
        fatherPedigreeOrganism = aFatherPedigreeOrganism;
        childPedigreeOrganisms = aChildPedigreeOrganisms;

        // Constants for placement algorithm
        cellSize = pedigreeOrganismView.getOrganismCellDimensions(aFatherPedigreeOrganism.getOrganism(), 0, 0, 0, true);

        generationString = new String("F" + family.getGeneration());
        generationStringLength = 20;
        Graphics g = pedigreeOrganismView.getGraphics();
        if (g != null)
        {
            generationStringLength = g.getFontMetrics().stringWidth(generationString);
        }

        highlightedChildrenPoints = new Vector();
    }
    
    /**
     * Get the family for this PedigreeFamily.
     *
     * @return		Family - the family
    **/
    public Family getFamily()
    {
        return family;
    }

    /**
     * Get the mother PedigreeOrganism
     *
     * @return		PedigreeOrganism - the mother pedigree organism
    **/
    public PedigreeOrganism getMotherPedigreeOrganism()
    {
        return motherPedigreeOrganism;
    }

    /**
     * Get the father PedigreeOrganism
     *
     * @return		PedigreeOrganism - the father pedigree organism
    **/
    public PedigreeOrganism getFatherPedigreeOrganism()
    {
        return fatherPedigreeOrganism;
    }

    /**
     * Get the X location of the leftmost parent in this family.<p>
     *
     * @return		int - X location
    **/
    public int getLeftMostParentX()
    {
        int motherX = motherPedigreeOrganism.getTopLeftPoint().x;
        int fatherX = fatherPedigreeOrganism.getTopLeftPoint().x;
        int leftmost = (motherX < fatherX) ? motherX : fatherX;
        return leftmost;
    }
    
    /**
     * Get the X location of the rightmost parent in this family.<p>
     *
     * @return		int - X location
    **/
    public int getRightMostParentX()
    {
        int motherX = motherPedigreeOrganism.getTopLeftPoint().x;
        int fatherX = fatherPedigreeOrganism.getTopLeftPoint().x;
        int rightmost = (motherX > fatherX) ? motherX : fatherX;
        return rightmost;
    }
    
    /**
     * Get the leftmost child organism.<p>
     *
     * @return		PedigreeOrganism - leftmost child organism.
    **/
    public PedigreeOrganism getLeftMostChild()
    {
        PedigreeOrganism leftMost = (PedigreeOrganism) childPedigreeOrganisms.firstElement();
        PedigreeOrganism kid = null;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            kid = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            leftMost = (kid.getXLocation() < leftMost.getXLocation()) ? kid : leftMost;
        }
        return leftMost;
    }
    
    /**
     * Get the rightmost child organism.<p>
     *
     * @return		PedigreeOrganism - rightmost child organism.
    **/
    public PedigreeOrganism getRightMostChild()
    {
        PedigreeOrganism rightMost = (PedigreeOrganism) childPedigreeOrganisms.firstElement();
        PedigreeOrganism kid = null;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            kid = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            rightMost = (kid.getXLocation() > rightMost.getXLocation()) ? kid : rightMost;
        }
        return rightMost;
    }
    
    /**
     * Get the topmost child organism.<p>
     *
     * @return		PedigreeOrganism - topmost child organism.
    **/
    public PedigreeOrganism getTopMostChild()
    {
        PedigreeOrganism topMost = (PedigreeOrganism) childPedigreeOrganisms.firstElement();
        PedigreeOrganism kid = null;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            kid = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            topMost = (kid.getYLocation() < topMost.getYLocation()) ? kid : topMost;
        }
        return topMost;
    }
    
    /**
     * Get the bottommost child organism.<p>
     *
     * @return		PedigreeOrganism - bottommost child organism.
    **/
    public PedigreeOrganism getBottomMostChild()
    {
        PedigreeOrganism bottomMost = (PedigreeOrganism) childPedigreeOrganisms.firstElement();
        PedigreeOrganism kid = null;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            kid = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            bottomMost = (kid.getYLocation() > bottomMost.getYLocation()) ? kid : bottomMost;
        }
        return bottomMost;
    }
    
    /**
     * Get the minimum and maximum positions for children of this family.
     *
     * @param	Point - minimum point. Point is set by this method.
     * @param	Point - maximum point. Point is set by this method.
    **/
    public void getMinimumMaximum(Point aMinPoint, Point aMaxPoint)
    {
        PedigreeOrganism aChildPedigreeOrganism;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            aChildPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            aChildPedigreeOrganism.getMinimumMaximum(aMinPoint, aMaxPoint);
        }
    }

    /**
     * Get an enumeration over the set of child pedigree organisms in this family
     *
     * @return		Enumeration - enumeration over the set of child pedigree organisms in this family
    **/
    public Enumeration getChildPedigreeOrganisms()
    {
        if (childPedigreeOrganisms == null)
        {
            Vector v = new Vector();
            return v.elements();
        }

        return childPedigreeOrganisms.elements();
    }

    /**
     * Get the number of children for this family.<p>
     *
     * @return		int - the number of children.
    **/
    public int getNumberOfChildren()
    {
        return childPedigreeOrganisms.size();
    }
    
    /**
     * Is this family empty? True if there are no children.<p>
     *
     * @return		boolean - true or false.
    **/
    public boolean isEmpty()
    {
        return childPedigreeOrganisms.isEmpty();
    }


    /**
     * Does this family contain this pedigreeOrganism
     *
     * @param		aPedigreeOrganism PedigreeOrganism - the pedigree organism to find
     * @return		boolean - true or false
    **/
    public boolean contains(PedigreeOrganism aPedigreeOrganism)
    {
        return childPedigreeOrganisms.contains(aPedigreeOrganism);
    }

    
    /**
     * Does this family contain this Organism?<p>
     *
     * @param		anOrganism Organism - the organism to find
     * @return		boolean - true or false
    **/
    public boolean containsOrganism(Organism anOrganism)
    {
        PedigreeOrganism aChildPedigreeOrganism;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            aChildPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            if (aChildPedigreeOrganism.getOrganism() == anOrganism)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Find the PedigreeOrganism given the Organism.<p>
     *
     * @param		anOrganism Organism - the organism to find
     * @return		PedigreeOrganism - the found PedigreeOrganism or null.
    **/
    public PedigreeOrganism findPedigreeOrganism(Organism anOrganism)
    {
        PedigreeOrganism aChildPedigreeOrganism;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            aChildPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            if (aChildPedigreeOrganism.getOrganism() == anOrganism)
            {
                return aChildPedigreeOrganism;
            }
        }

        return null;
    }

    // Didn't see the value in creating all the child pedigree organisms in the 
    // View, but I guess it's so to be able to add the listener and get the size info.
    // Should move this logic back to the view, I guess.
    
    /**
     * Add the given children to the family.<p>
     * Current inconsistency:
     * Didn't see the value in creating all the child pedigree organisms in the 
     * View, but I guess it's so to be able to add the listener and get the size info.
     * Should move this logic back to the view, I guess.
     *
     * @param		newChildren Vector - the children to add to the family.
     * @param		aPedigreeOrganismView aPedigreeOrganismView - the view
    **/
    public void addChildren(Vector newChildren, PedigreeOrganismView aPedigreeOrganismView)
    {
        Organism childOrganism = null;
        Enumeration eNewChildren = newChildren.elements();
        while (eNewChildren.hasMoreElements())
        {
            childOrganism = (Organism) eNewChildren.nextElement();

            // Add view as a listener on this organism
            childOrganism.addPropertyChangeListener(aPedigreeOrganismView);
    
            PedigreeOrganism childPedigreeOrganism =
                    new PedigreeOrganism(childOrganism,0,0,cellSize.width,cellSize.height);

            childPedigreeOrganisms.addElement(childPedigreeOrganism);
        }
    }
    
    /**
     * Remove the children of this family.<p>
     * Current inconsistency: same as for add. This could be in the view.
     *
     * @param		aPedigreeOrganismView PedigreeOrganismView - the view
    **/
    public void removeChildren(PedigreeOrganismView aPedigreeOrganismView)
    {
        PedigreeOrganism childPedigreeOrganism = null;
        Organism childOrganism = null;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            childPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            childOrganism = childPedigreeOrganism.getOrganism();
            childOrganism.removePropertyChangeListener(aPedigreeOrganismView);
        }
        childPedigreeOrganisms.removeAllElements();
    }
    
    /**
     * Remove a child pedigree organism, presumably because the child was deleted
     *
     * @param		aChildPedigreeOrganism PedigreeOrganism - the pedigree organism to remove
     * @return		boolean - was child successfully found and removed?
    **/
    public boolean removeChildPedigreeOrganism(PedigreeOrganism aChildPedigreeOrganism)
    {
        return childPedigreeOrganisms.removeElement(aChildPedigreeOrganism);
    }

    /**
     * Translate the family by the given amount.<p>
     *
     * @param		xDelta int - x delta
     * @param		yDelta int - y delta
    **/
    public void translate(int xDelta, int yDelta)
    {
        // Move our own point
        xLocation += xDelta;
        yLocation += yDelta;
        
        // Move the child bar and the vertical bar
        xChildBarLeft += xDelta;
        xChildBarRight += xDelta;
        yChildBar += yDelta;

        // Move the children
        PedigreeOrganism aChildPedigreeOrganism;
        Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
        while (eChildPedigreeOrganisms.hasMoreElements())
        {
            aChildPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
            aChildPedigreeOrganism.translate(xDelta, yDelta);
        }
    }

    /**
     * The squeeze factor is an abstraction for how much to squeeze a family
     * horizontally when placing it's children. It's currently used very concretely
     * as the number of rows to draw (hence its 1-based default).
    **/
    private int squeezeFactor = 1;
    
    /**
     * Accessor for squeeze factor.<p>
     *
     * @return		int - the squeeze factor. 
    **/
    public int getSqueezeFactor()
    {
        return squeezeFactor;
    }
    
    /**
     * Accessor for setting squeeze factor.<p>
     *
     * @param		int - new value to set.
    **/
    private void setSqueezeFactor(int factor)
    {
        squeezeFactor = factor;
    }
    
    /**
     * Accessor for incrementing the squeeze factor. Should only be called after canSqueeze().<p>
    **/
    private void bumpSqueezeFactor()
    {
        squeezeFactor++;
    }
    
    /**
     * Accessor for resetting the squeeze factor.<p>
    **/
    public void resetSqueezeFactor()
    {
        squeezeFactor = 1;
    }
    
    /**
     * Can the family be squeezed any more? Enforces the maximum squeeze factor.
     * Also covers shortcomings in the squeezing algorithm (placeChildren).
     * Special cases are mostly empirical.
     *
     * @return		boolean - true or false
    **/
    public boolean canSqueeze()
    {
        int numKids = childPedigreeOrganisms.size();
        int numRows = getSqueezeFactor() + 1;
        int kidsPerRow = numKids / numRows;
        int remainingKids = numKids % numRows;
        
        // Max is 4 rows.
        if (getSqueezeFactor() == 4)
            return false;
        
        // Algorithm will do the reduction but end up with an even number of kids
        // in 3 rows and go from 4 to 3 rows. May exist for other numerical combos?
        if ((getSqueezeFactor() == 3) && (numKids == 9))
            return false;
        
        // The only way we want one kid on a row is when it's the last row.
        if ((kidsPerRow == 1))
            return false;
        
        return true;
    }
    
    /**
     * Public interface for squeezing family and redoing the placement.<p>
     *
     * @return		Dimension - resulting dimension after placing family.
    **/
    public Dimension squeeze()
    {
        if (canSqueeze())
            bumpSqueezeFactor();

        return placeChildren();
    }
    
    /**
     * Placement constants.
     * Should one day be made relative to organism sizes as specified in world files.
    **/
    private int hSpace = 24;
    private int vSpace = 20;

    /**
     * Loop through the children and set their relative placement<p>
     *
     * @return		Dimension - resulting dimension family will be drawn in
    **/
    public Dimension placeChildren()
    {
        int xNextKid = 0;
        int yNextKid = 0;
        
        int numKids = childPedigreeOrganisms.size();
        int numRows = getSqueezeFactor();
        
        int kidsPerRow = numKids / numRows;
        int remainingKids = numKids % numRows;

        // If they don't divide evenly per row, adjust the row sizes
        if (remainingKids != 0)
        {
            int oldKidsPerRow = kidsPerRow;
            int oldRemainingKids = remainingKids;
            kidsPerRow = numKids / (numRows - 1);
            remainingKids = numKids % (numRows - 1);
            if (remainingKids == 0)
            {
                // Bad, one less row divides evenly. Force unevenness,
                // but only if last row will not be bigger than others.
                // This is the major breakdown in this algorithm.
                // Use canSqueeze() to eliminate special cases.
                if ((kidsPerRow - 1) > (remainingKids + (numRows - 1)))
                {
                    kidsPerRow--;
                    remainingKids += (numRows - 1);
                }
                else
                {
                    // Revert!
                    kidsPerRow = oldKidsPerRow;
                    remainingKids = oldRemainingKids;
                }
            }
            // Take from top rows and add to last row until last would be bigger than rest
            while (true)
            {
                int oneLess = kidsPerRow - 1;
                int lastRow = remainingKids + (numRows - 1);
                if (oneLess <= lastRow)
                    break;
                kidsPerRow = oneLess;
                remainingKids = lastRow;
            }
        }
        
        int bumpX = hSpace / numRows;		// Nice divisible numbers: 24, 12, 8, 6

        int iKid = 0;
        PedigreeOrganism kid;
        for (int i=0; i<numRows; i++)
        {
            kidsPerRow = ((i == numRows - 1) && (remainingKids != 0))
                ? remainingKids : kidsPerRow;
            
            for (int j=0; j<kidsPerRow; j++)
            {
                kid = (PedigreeOrganism) childPedigreeOrganisms.elementAt(iKid);
                kid.setXLocation(xNextKid);
                kid.setYLocation(yNextKid);
                xNextKid += hSpace;
                iKid++;
            }
            xNextKid = (bumpX * (i + 1));
            yNextKid += vSpace;
        }

        placeConnections();
        
        PedigreeOrganism rightMost = getRightMostChild();
        PedigreeOrganism bottomMost = getBottomMostChild();

        setWidth(rightMost.getXLocation() + cellSize.width);
        setHeight(bottomMost.getYLocation() + cellSize.height);
        return new Dimension(getWidth(), getHeight());
    }
    
    /**
     * Place the connections.<p>
     *
    **/
    public void placeConnections()
    {
        // Place the child bar
        PedigreeOrganism leftMost = getLeftMostChild();
        PedigreeOrganism rightMost = getRightMostChild();
        PedigreeOrganism topMost = getTopMostChild();

        xChildBarLeft = leftMost.getTopMidPoint().x;
        xChildBarRight = rightMost.getTopMidPoint().x;
        yChildBar = topMost.getTopMidPoint().y - 10;

        // Place the parent bar
        Point motherPoint = motherPedigreeOrganism.getBottomMidPoint();
        Point fatherPoint = fatherPedigreeOrganism.getBottomMidPoint();
        if (motherPoint.x < fatherPoint.x)
        {
            xParentBarLeft = motherPoint.x;
            xParentBarRight = fatherPoint.x;
        }
        else
        {
            xParentBarLeft = fatherPoint.x;
            xParentBarRight = motherPoint.x;
        }
        yParentBar = yChildBar - 14;

        // Place the vertical bar
        xVerticalBar = xChildBarLeft + ((xChildBarRight - xChildBarLeft) / 2);
        
        // Adjust connection to parent bar
        if (xParentBarRight < xVerticalBar)
        {
            xParentBarRight = xVerticalBar;
        }
        else if (xParentBarLeft > xVerticalBar)
        {
            xParentBarLeft = xVerticalBar;
        }
    }

    /**
     * Paint the Families and Organisms with the given Graphics object on the
     * given PedigreeOrganismView.<p>
     *
     * The caller of this method must set the foreground and background colors of
     * the Graphics object before calling this method.<p>
     *
     * @param		g Graphics - graphics object to use in painting
     * @param		pedigreeOrganismView PedigreeOrganismView - the pedigree organism view
     * @param		trait Trait - the trait to show, may be null
     * @param		fontHeight int - the font height
     * @param		selectionSet SelectionSet - the view's selection set
     * @param		highlightSet SelectionSet - the view's highlight set
    **/
    public void paint(Graphics g, PedigreeOrganismView aPedigreeOrganismView,
                        Trait trait, int fontHeight, SelectionSet selectionSet,
                        SelectionSet highlightSet)
    {
        if ((g != null) && (aPedigreeOrganismView != null))
        {
            PedigreeOrganism aChildPedigreeOrganism;
            Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
            while (eChildPedigreeOrganisms.hasMoreElements())
            {
                aChildPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
                aChildPedigreeOrganism.paint(g,aPedigreeOrganismView,trait,
                                            fontHeight,selectionSet,highlightSet);
            }
        }
    }

    /**
     * Paint the connections with the given Graphics object on the
     * given PedigreeOrganismView.<p>
     *
     * Other positions - the ends of the bars and the locations of the lines drawn from
     * the child and parents bars to the children and parents respectively are derived
     * from the positions of the parents and children.<p>
     *
     * The caller of this method must set the foreground and background colors of
     * the Graphics object before calling this method.<p>
     *
     * @param		g Graphics - graphics object to use in painting
     * @param		pedigreeOrganismView PedigreeOrganismView - the pedigree organism view
     * @param		trait Trait - the trait to show, may be null
     * @param		fontHeight int - the font height
     * @param		selectionSet SelectionSet - the view's selection set
     * @param		highlightSet SelectionSet - the view's highlight set
    **/
    public void paintConnections(Graphics g, PedigreeOrganismView aPedigreeOrganismView,
                                 Trait trait, int fontHeight, SelectionSet selectionSet,
                                 SelectionSet highlightSet)
    {
        if (g != null && aPedigreeOrganismView != null)
        {
            // Initialize
            PedigreeOrganism aChildPedigreeOrganism = null;
            Point motherPoint = motherPedigreeOrganism.getBottomMidPoint();
            Point fatherPoint = fatherPedigreeOrganism.getBottomMidPoint();
            Point childPoint = null;
            Point highlightedChildPoint = null;
            highlightedChildrenPoints.removeAllElements();
            boolean highlightFamily = highlightSet.contains(this.getFamily());

            // Loop through children highlighting vertical lines to each child from child bar
            Enumeration eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
            while (eChildPedigreeOrganisms.hasMoreElements())
            {
                aChildPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
                childPoint = aChildPedigreeOrganism.getTopMidPoint();
                // If this child is part of highlight set, highlight line and save points
               if (highlightSet.contains(aChildPedigreeOrganism.getOrganism()) && 
               	   highlightSet.contains(motherPedigreeOrganism.getOrganism()) &&
               	   highlightSet.contains(fatherPedigreeOrganism.getOrganism()))
                {
                    highlightedChildrenPoints.addElement(childPoint);
                    g.setColor(aPedigreeOrganismView.getHighlightColor());
                    g.fillRect(childPoint.x - 2, yChildBar,
                               5, childPoint.y - yChildBar);
                }
            }

            // Highlight vertical lines from parents to parent bar
            if (!highlightedChildrenPoints.isEmpty() || highlightFamily)
            {
  
                if (highlightSet.contains(motherPedigreeOrganism.getOrganism()))
                {
                    g.setColor(aPedigreeOrganismView.getHighlightColor());
                    g.fillRect(motherPoint.x - 2, motherPoint.y,
                               5, yParentBar + 3 - motherPoint.y);
                }
                if (highlightSet.contains(fatherPedigreeOrganism.getOrganism()))
                {
                    g.setColor(aPedigreeOrganismView.getHighlightColor());
                    g.fillRect(fatherPoint.x - 2, fatherPoint.y,
                               5, yParentBar + 3 - fatherPoint.y);
                }
            }

            // Highlight horizontal child bar between highlighted child and vertical bar
            Enumeration eHighlightedChildrenPoints = highlightedChildrenPoints.elements();
            while (eHighlightedChildrenPoints.hasMoreElements())
            {
                highlightedChildPoint = (Point) eHighlightedChildrenPoints.nextElement();
                int highlightBarWidth = xVerticalBar - highlightedChildPoint.x;
                if (highlightBarWidth > 0)
                {
                    g.setColor(aPedigreeOrganismView.getHighlightColor());
                    g.fillRect(highlightedChildPoint.x - 2, yChildBar - 2,
                                highlightBarWidth + 5, 5);
                }
                else
                {
                    g.setColor(aPedigreeOrganismView.getHighlightColor());
                    highlightBarWidth = -highlightBarWidth;
                    g.fillRect(xVerticalBar - 2, yChildBar - 2,
                                highlightBarWidth + 5, 5);
                }

            }

            // Highlight vertical bar
            if (!highlightedChildrenPoints.isEmpty() || highlightFamily)
            {
                g.setColor(aPedigreeOrganismView.getHighlightColor());
                g.fillRect(xVerticalBar-2, yParentBar - 2,
                           5, yChildBar + 3 - yParentBar);
            }

            // Highlight horizontal parent bar
            if (!highlightedChildrenPoints.isEmpty() || highlightFamily)
            {
                g.setColor(aPedigreeOrganismView.getHighlightColor());
                g.fillRect(xParentBarLeft - 2, yParentBar-2,
                           xParentBarRight + 3 - xParentBarLeft, 5);
            }

            // Loop through children drawing vertical lines to each child from child bar
            eChildPedigreeOrganisms = childPedigreeOrganisms.elements();
            while (eChildPedigreeOrganisms.hasMoreElements())
            {
                aChildPedigreeOrganism = (PedigreeOrganism) eChildPedigreeOrganisms.nextElement();
                childPoint = aChildPedigreeOrganism.getTopMidPoint();
                g.setColor(aPedigreeOrganismView.getForeground());
                g.drawLine(childPoint.x,childPoint.y + 2,
                           childPoint.x,yChildBar);
            }

            // Draw vertical lines from parents to parent bar
            g.setColor(aPedigreeOrganismView.getForeground());
            g.drawLine(motherPoint.x,motherPoint.y - 7,
                       motherPoint.x,yParentBar);
            g.drawLine(fatherPoint.x,fatherPoint.y - 7,
                       fatherPoint.x,yParentBar);
            
            // Draw horizontal child bar
            g.setColor(aPedigreeOrganismView.getForeground());
            g.drawLine(xChildBarLeft,yChildBar,
                       xChildBarRight,yChildBar);

            // Draw vertical bar
            g.setColor(aPedigreeOrganismView.getForeground());
            g.drawLine(xVerticalBar,yChildBar,
                       xVerticalBar,yParentBar);
            g.drawString(generationString,
                         xVerticalBar + 5, yChildBar-1);

            // Draw horizontal parent bar
            g.setColor(aPedigreeOrganismView.getForeground());
            g.drawLine(xParentBarLeft,yParentBar,
                       xParentBarRight,yParentBar);
        }
    }

    /**
     * Pick this object, determining if the given x,y location is on the object.<p>
     *
     * @param		xPick int - x location of pick
     * @param		yPick int - y location of pick
     * @return		boolean - is object at the given pick location
    **/
    public boolean pickForSnip(int xPick, int yPick)
    {
        boolean result = false;

        // Try vertical line first, as it could be short sometimes
        if ((xVerticalBar-5 <= xPick && xVerticalBar+5 + generationStringLength >= xPick) &&
            (yParentBar-5 <= yPick && yChildBar+5 >= yPick))
        {
            result = true;
        }
        // Try parent bar
        else if ((yParentBar-5 <= yPick && yParentBar+5 >= yPick) &&
                 (xParentBarLeft-5 <= xPick && xParentBarRight+5 >= xPick))
        {
            result = true;
        }
        // Try child bar
        else if ((yChildBar-5 <= yPick && yChildBar+5 >= yPick) &&
                 (xChildBarLeft-5 <= xPick && xChildBarRight+5 >= xPick))
        {
            result = true;
        }

        return result;
    }

    /**
     * Is this family within the given rectangle?  We'll consider a family
     * within the rectangle if the rectangle contains some portion of the
     * child bar, parent bar or vertical bar.  Otherwise it won't be within.
     * This means a rectangle crossing the lines from the parent bar to
     * the parent organism or crossing the lines from the child bar to the
     * child organisms will not qualify as within.<p>
     *
     * @param		xTopLeft int - x top left coordinate
     * @param		yTopLeft int - y top left coordinate
     * @param		xBottomRight int - x bottom right coordinate
     * @param		yBottomRight int - y bottom right coordinate
     * @return		boolean - is the object within the given rectangle?
    **/
    public boolean within(int xTopLeft, int yTopLeft, int xBottomRight, int yBottomRight)
    {
        boolean result = false;

        // Try parent bar
        if ((yTopLeft <= yParentBar && yBottomRight >= yParentBar) &&
            (xTopLeft <= xParentBarRight && xBottomRight >= xParentBarLeft))
        {
            result = true;
        }
        // Try child bar
        else if ((yTopLeft <= yChildBar && yBottomRight >= yChildBar) &&
                 (xTopLeft <= xChildBarRight && xBottomRight >= xChildBarLeft))
        {
            result = true;
        }
        // Try vertical bar
        else if ((xTopLeft <= xVerticalBar && xBottomRight >= xVerticalBar) &&
                 ((yParentBar < yChildBar &&
                   (yTopLeft <= yChildBar && yBottomRight >= yParentBar)) ||
                  (yTopLeft <= yParentBar && yBottomRight >= yChildBar)))
        {
            result = true;
        }

        return result;
    }
}

