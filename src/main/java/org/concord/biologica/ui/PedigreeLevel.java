//
// Class : PedigreeLevel - a layout managing abstraction for a level (generation) of pedigreeObjects
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Dias
//
// $Revision: 1.9 $
// $Date: 2003/01/14 21:36:03 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.concord.biologica.engine.*;

/**
 * An object that manages the contents and layout of a generation in the pedigree view.
 *
**/
public final class PedigreeLevel
{
    /**
     * Our real estate
    **/
    private Rectangle rect;

    /**
     * A vector of PedigreeFamilies and/or PedigreeOrganisms to show at this level
    **/
    private Vector pedigreeObjects = null;

    /**
     * Creates a new pedigree level.
    **/
    public PedigreeLevel()
    {
        rect = new Rectangle(0,0,0,0);
        pedigreeObjects = new Vector();
    }
    
    /**
     * Get our location.<p>
     *
     * @return		location Point - our location
    **/
    public Point getLocation()
    {
        return rect.getLocation();
    }
    
    /**
     * Get our width.<p>
     *
     * @return		width int - our width
    **/
    public int getWidth()
    {
        return rect.width;
    }
    
    /**
     * Get our height.<p>
     *
     * @return		height int - our height
    **/
    public int getHeight()
    {
        return rect.height;
    }
    
    /**
     * Get an enumeration of all the PedigreeObjects in this level
     *
     * @return		Enumeration - Enumeration of the objects or an empty enumeration.
    **/
    public Enumeration getPedigreeObjects()
    {
        if (pedigreeObjects == null)
        {
            Vector v = new Vector();
            return v.elements();
        }

        return pedigreeObjects.elements();
    }
    
    /**
     * Is this level empty? True of there are no PedigreeObjects 
     *
     * @return		boolean - true or false
    **/
    public boolean isEmpty()
    {
        return pedigreeObjects.isEmpty();
    }

    /**
     * Add an organism to this level.<p>
     *
     * @param		aPedigreeOrganism PedigreeOrganism - an organism to add, may not be null
     * @exception	IllegalArgumentException - input organism null
    **/
    public void addOrganism(PedigreeOrganism aPedigreeOrganism)
    {
        if (aPedigreeOrganism == null)
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        
        pedigreeObjects.addElement(aPedigreeOrganism);
    }
    
    /**
     * Add a family to this level.<p>
     *
     * @param		aPedigreeFamily PedigreeFamily - a family to add, may not be null
     * @exception	IllegalArgumentException - input family null
    **/
    public void addFamily(PedigreeFamily aPedigreeFamily)
    {
        if (aPedigreeFamily == null)
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        
        if (pedigreeObjects.size() == 0)
        {
            pedigreeObjects.addElement(aPedigreeFamily);
            return;
        }
        // Get new family's parents positions
        int newLeft = aPedigreeFamily.getLeftMostParentX();
        int newRight = aPedigreeFamily.getRightMostParentX();
        
        // Keep vector sorted, add family in approriate spot
        for (int i=0; i<pedigreeObjects.size(); i++)
        {
            int currentLeft = ((PedigreeObject)pedigreeObjects.elementAt(i)).getLeftMostParentX();
            int currentRight = ((PedigreeObject)pedigreeObjects.elementAt(i)).getRightMostParentX();
            
            // Stand alone organism has no parents, tough call. skip over it.
            if (currentLeft == -1)
                break;
            
            // New is more left than current, insert here
            if (newLeft < currentLeft)
            {
                pedigreeObjects.insertElementAt(aPedigreeFamily, i);
                return;
            }
            // New and current have same leftmost parent, consider other parent
            if (newLeft == currentLeft)
            {
                if (newRight < currentRight)
                {
                    pedigreeObjects.insertElementAt(aPedigreeFamily, i);
                    return;
                }
            }			
        }
        // Went through the whole vector without inserting, so append
        pedigreeObjects.addElement(aPedigreeFamily);
    }

    /**
     * Remove a family from this level.<p>
     *
     * @param		aPedigreeOrganism PedigreeOrganism - a family to remove, may not be null
     * @return		result boolean - success or not
     * @exception	IllegalArgumentException - input organism null
    **/
    public boolean removeFamily(PedigreeFamily aPedigreeFamily)
    {
        if (aPedigreeFamily == null)
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);

		//pedigreeObjects.removeElement(aPedigreeFamily);
        boolean result = pedigreeObjects.removeElement(aPedigreeFamily);
        return result;
    }
    
    /**
     * Remove an organism from this level.<p>
     *
     * @param		aPedigreeOrganism PedigreeOrganism - an organism to remove , may not be null
     * @return		result boolean - success or not
     * @exception	IllegalArgumentException - input organism null
    **/
    public boolean removeOrganism(PedigreeOrganism aPedigreeOrganism)
    {
        if (aPedigreeOrganism == null)
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);

        boolean result = false;
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
        	 pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
	         if (pedigreeObject instanceof PedigreeOrganism)
	         {
	                result = pedigreeObjects.removeElement(aPedigreeOrganism);
	                
	         }
	         else if (pedigreeObject instanceof PedigreeFamily)
	         {
	         	PedigreeFamily pedigreeFamily = (PedigreeFamily)pedigreeObject;
	         	if (pedigreeFamily.contains(aPedigreeOrganism))
	         	{
	         		if(pedigreeFamily.getLeftMostChild().equals(pedigreeFamily.getRightMostChild()))
	         		{
	         			result = pedigreeObjects.removeElement(pedigreeFamily);
	         		}
	         		else
	         		{
	         			result = pedigreeFamily.removeChildPedigreeOrganism(aPedigreeOrganism);
	         		}
	         		
	         	}
	         }
	    }
       
        return result;
    }
    
    /**
     * Remove an object from this level.
     *
     * @param		aPedigreeObject PedigreeObject - either an pedigree organism or a pedigree family
     * @return		boolean - true or false
    **/
    public boolean removeObject(PedigreeObject aPedigreeObject)
    {
        if (aPedigreeObject == null)
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		
        boolean result =pedigreeObjects.removeElement(aPedigreeObject);
        return result;
    }

    /**
     * Remove everything from this level
     *
    **/
    public void removeAll()
    {
        pedigreeObjects.removeAllElements();
    }

    /**
     * Get the total number of organisms at this level. Includes children within
     * families and stand-alone organisms.
     *
     * @return		int - total number of organisms.
    **/
    public int getNumberOfOrganisms()
    {
        int count = 0;
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            if (pedigreeObject instanceof PedigreeOrganism)
            {
                count++;
            }
            else if (pedigreeObject instanceof PedigreeFamily)
            {
                count += ((PedigreeFamily)pedigreeObject).getNumberOfChildren();
            }
        }
        return count;
    }

    /**
     * Get the number of families at this level.
     *
     * @return		int - the number of families.
    **/
    public int getNumberOfFamilies()
    {
        int count = 0;
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            if (pedigreeObject instanceof PedigreeFamily)
            {
                count ++;
            }
        }
        return count;
    }

    /**
     * Does this level contain this pedigreeFamily
     *
     * @param		aPedigreeFamily PedigreeFamily - the pedigree family to find
     * @return		boolean - true or false
    **/
    public boolean contains(PedigreeFamily aPedigreeFamily)
    {
        return pedigreeObjects.contains(aPedigreeFamily);
    }

    /**
     * Does this level contain this pedigreeOrganism? Looks within families and at
     * stand-alone organisms.
     *
     * @param		aPedigreeOrganism PedigreeOrganism - the pedigree organism to find
     * @return		boolean - true or false
    **/
    public boolean contains(PedigreeOrganism aPedigreeOrganism)
    {
        if (pedigreeObjects.contains(aPedigreeOrganism))
            return true;

        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            if (pedigreeObject instanceof PedigreeFamily)
            {
                if (((PedigreeFamily)pedigreeObject).contains(aPedigreeOrganism))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Does this level contain this organism? Looks within families and at
     * stand-alone organisms.
     *
     * @param		anOrganism Organism - an organism
     * @return		boolean - true or false
    **/
    public boolean containsOrganism(Organism anOrganism)
    {
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            if (pedigreeObject instanceof PedigreeFamily)
            {
                if (((PedigreeFamily)pedigreeObject).containsOrganism(anOrganism))
                {
                    return true;
                }
            }
            else if (pedigreeObject instanceof PedigreeOrganism)
            {
                if (((PedigreeOrganism)pedigreeObject).getOrganism() == anOrganism)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find the PedigreeFamily at this level given the Family
     *
     * @param		aFamily Family - the Family
     * @return		PedigreeFamily - the PedigreeFamily or null if not found
    **/
    public PedigreeFamily findPedigreeFamily(Family aFamily)
    {
        PedigreeFamily foundPedigreeFamily = null;
        
        PedigreeObject aPedigreeObject = null;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            aPedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            if (aPedigreeObject instanceof PedigreeFamily)
            {
            	Family tempFamily =((PedigreeFamily)aPedigreeObject).getFamily();
            	
                if (((PedigreeFamily)aPedigreeObject).getFamily().equals(aFamily))
                {
                    foundPedigreeFamily = (PedigreeFamily)aPedigreeObject;
               
                    break;
                }
            }
        }
        return foundPedigreeFamily;
    }		
    
    /**
     * Find the Pedigree Organism at this level given the Organism
     *
     * @param		anOrganism Organism - the Organism
     * @return		PedigreeOrganism - the PedigreeOrganism or null if not found
    **/
    public PedigreeOrganism findPedigreeOrganism(Organism anOrganism)
    {
        PedigreeOrganism foundPedigreeOrganism = null;
        
        PedigreeObject aPedigreeObject = null;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements() && foundPedigreeOrganism == null)
        {
            aPedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            if (aPedigreeObject instanceof PedigreeFamily)
            {
                foundPedigreeOrganism = (((PedigreeFamily)aPedigreeObject).findPedigreeOrganism(anOrganism));
            }
            else if (((PedigreeOrganism)aPedigreeObject).getOrganism() == anOrganism)
            {
                foundPedigreeOrganism = (PedigreeOrganism)aPedigreeObject;
            }
        }
        return foundPedigreeOrganism;
    }
    
    /**
     * Horizontal space added between families placed in this level.
    **/
    private int hSpace = 20;

    /**
     * Translate the level by the given amount.<p>
     *
     * @param		x int - x position to set
     * @param		y int - y position to set
    **/
    public void translate(int x, int y)
    {
        rect.setLocation(x, y);
        
        int nextX = x;
        int nextY = y;
        
        // Move all the families
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            pedigreeObject.translate(nextX, nextY);
            nextX += pedigreeObject.getWidth() + hSpace;
        }
    }

    /**
     * Place all the families at this level. Position them horizontally given 
     * the bounds dimension supplied. Loops over objects at this level placing
     * them and repeatedly squeezing them until they fit horizontally. 
     *
     * @param		bounds Dimension - dimension (especially width) the level is to be placed within. 
     * @return		Dimension - resulting dimension this level will use when drawn.
    **/
    public Dimension placeFamilies(Dimension bounds)
    {
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            pedigreeObject.resetSqueezeFactor();
            pedigreeObject.setXLocation(0);
            pedigreeObject.setYLocation(0);
        }
            
        boolean squeeze = false;
        Dimension dim = _placeFamilies(squeeze);
        boolean canSqueeze = true;
        while ((dim.width >= bounds.width) && canSqueeze)
        {
            squeeze = true;
            dim = _placeFamilies(squeeze);
            canSqueeze = canSqueezeFamilies();
        }
        return dim;
    }
            
    /**
     * Internal method for placing levels. Called from public method.
     *
     * @param		squeeze boolean - flag indicating whether or not to squeeze the families
     * @return		Dimension - resulting dimension this level will used when drawn.
    **/
    private Dimension _placeFamilies(boolean squeeze)
    {
        Dimension totalDim = new Dimension(0,0);
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            Dimension dim = (squeeze) ? pedigreeObject.squeeze() :
                pedigreeObject.placeChildren();
            
            totalDim.width += dim.width + hSpace;
            totalDim.height = (dim.height > totalDim.height) ? dim.height : totalDim.height;
        }
        totalDim.width -= hSpace;
        return totalDim;
    }
    
    /**
     * Can we get any more squeezing into these families? Returns true if any of the 
     * families can be squeezed more.
     *
     * @return		boolean - true or false.
    **/
    private boolean canSqueezeFamilies()
    {
        boolean canSqueeze = false;

        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            if (pedigreeObject.canSqueeze())
                canSqueeze = true;
        }
        return canSqueeze;
    }
    
    /**
     * Place the connections at this level. 
     *
    **/
    public void placeConnections()
    {
        PedigreeObject pedigreeObject;
        Enumeration ePedigreeObjects = pedigreeObjects.elements();
        while (ePedigreeObjects.hasMoreElements())
        {
            pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
            pedigreeObject.placeConnections();
        }
    }

    /**
     * Paint the level's connections given PedigreeOrganismView.<p>
     *
     * The caller of this method must set the foreground and background colors of
     * the Graphics object before calling this method.<p>
     *
     * @param		g Graphics - graphics object to use in painting
     * @param		pedigreeOrganismView PedigreeOrganismView - the pedigree organism view
     * @param		trait Trait - the trait to show, may be null
     * @param		fontHeight int - the font height
     * @param		selectionSet - the selection set
    **/
    public void paintConnections(Graphics g, PedigreeOrganismView aPedigreeOrganismView,
                                 Trait trait, int fontHeight, SelectionSet selectionSet,
                                 SelectionSet highlightSet)
    {
        if ((g != null) && (aPedigreeOrganismView != null))
        {
            if (pedigreeObjects != null && pedigreeObjects.size() > 0)
            {
                PedigreeObject pedigreeObject;
                Enumeration ePedigreeObjects = pedigreeObjects.elements();
                while (ePedigreeObjects.hasMoreElements())
                {
                    pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
                    pedigreeObject.paintConnections(g,aPedigreeOrganismView,
                                            trait,fontHeight,selectionSet,highlightSet);
                }
            }
        }
    }

    /**
     * Paint the level's PedigreeOrganisms given PedigreeOrganismView.<p>
     *
     * The caller of this method must set the foreground and background colors of
     * the Graphics object before calling this method.<p>
     *
     * @param		g Graphics - graphics object to use in painting
     * @param		pedigreeOrganismView PedigreeOrganismView - the pedigree organism view
     * @param		trait Trait - the trait to show, may be null
     * @param		fontHeight int - the font height
     * @param		selectionSet - the selection set
     * @param		highlightSet - the highlight set
    **/
    public void paintOrganisms(Graphics g, PedigreeOrganismView aPedigreeOrganismView,
                                Trait trait, int fontHeight, SelectionSet selectionSet,
                                SelectionSet highlightSet)
    {
        if ((g != null) && (aPedigreeOrganismView != null))
        {
            // Draw pedigreeObjects one at a time
            if (pedigreeObjects != null && pedigreeObjects.size() > 0)
            {
                PedigreeObject pedigreeObject;
                Enumeration ePedigreeObjects = pedigreeObjects.elements();
                while (ePedigreeObjects.hasMoreElements())
                {
                    pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
                    pedigreeObject.paint(g,aPedigreeOrganismView,
                                        trait,fontHeight,selectionSet,highlightSet);
                }
            }
        }
    }
}

