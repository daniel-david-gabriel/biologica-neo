//
// Class : PedigreeOrganism - a small object that manages the state for an organism shown in the pedigree view
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.5 $
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
 * An object that manages the state for an organism in the pedigree view.
 *
**/
public final class PedigreeOrganism
extends PedigreeObject
{
    /**
     * The organism
    **/
    private Organism organism;

    /**
     * Creates a new pedigree organism.
     * <p>Note: X and Y locations are now ignored. Placement is done via new
     * PedigreeLevel class. This interface should be changed.
     *
     * @param		anOrganism Organism - the organism, may be null
     * @param		anXLocation int - x location of organism in pedigree view
     * @param		aYLocation int - y location of organism in pedigree view
     * @param		aWidth int - width of organism cell
     * @param		aHeight int - height of organism cell
    **/
    public PedigreeOrganism(Organism anOrganism, int anXLocation, int aYLocation, int aWidth, int aHeight)
    {
        organism = anOrganism;
        xLocation = anXLocation;
        yLocation = aYLocation;
        width = aWidth;
        height = aHeight;
    }
    
    /**
     * Get the organism for this PedigreeOrganism.
     *
     * @return		Organism - the organism
    **/
    public Organism getOrganism()
    {
        return organism;
    }

    /**
     * Paint the object with the given Graphics object on the
     * given PedigreeOrganismView.<p>
     *
     * The vertical positions of the 2 bars (yChildBar and yParentBar) and the horizontal
     * position of the vertical bar (xVerticalBar) are given values and may only be modified
     * by the user by clicking and dragging those bars with the mouse.<p>
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
    public void paint(Graphics g, PedigreeOrganismView aPedigreeOrganismView, Trait trait,
                int fontHeight, SelectionSet selectionSet, SelectionSet highlightSet)
    {
        if ((g != null) && (organism != null) && (aPedigreeOrganismView != null))
        {
            boolean selected = selectionSet.contains(organism);
            boolean highlighted = highlightSet.contains(organism);
            if (organism.isDeleted())
                return;
            if (trait == null)
            {
                aPedigreeOrganismView.paintOrganism(g, organism,
                                                    xLocation, yLocation,
                                                    fontHeight, selected);
            }
            else
            {
                // Draw a single trait of the organism
                Trait aTrait = null;
                Characteristic aCharacteristic = null;
                Enumeration eCharacteristics = organism.getCharacteristics();
                while (eCharacteristics.hasMoreElements())
                {
                    aCharacteristic = (Characteristic) eCharacteristics.nextElement();
                    aTrait = aCharacteristic.getTrait();
                    if (aTrait == trait)
                    {
                        aPedigreeOrganismView.paintOrganismCharacteristic(g, organism, aCharacteristic,
                                                                          xLocation, yLocation,
                                                                          fontHeight, selected,
                                                                          highlighted);
                        break;
                    }
                }
            }
        }
    }

}

