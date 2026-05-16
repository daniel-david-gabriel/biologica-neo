//
// Class : TreeViewCellRenderer
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2001/06/05 18:48:20 $
// $Author: ed $
//

package org.concord.biologica.ui;

import java.lang.String;
import java.util.Enumeration;
import java.util.Vector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.concord.biologica.engine.*;

/**
 * This class knows how to render (draw) the cells in
 * the species' tree view.  It does this by knowing
 * about the different types of objects in the tree
 * (e.g. species, chromosomes, etc.) and knowing what
 * sorts of icons, text, editing, etc. is appropriate
 * for each type of cell.<p>
 *
 * @version		$Revision: 1.2 $ $Date: 2001/06/05 18:48:20 $
 * @author 		$Author: ed $
**/
public final class TreeViewCellRenderer extends DefaultTreeCellRenderer
{
    /**
     * Icon to use when the item is an unlocked world.
    **/
    static protected ImageIcon        	unlockedWorldIcon;

    /**
     * Icon to use when the item is a locked world.
    **/
    static protected ImageIcon        	lockedWorldIcon;

    /**
     * Icon to use when the item is an unlocked species.
    **/
    static protected ImageIcon        	unlockedSpeciesIcon;

    /**
     * Icon to use when the item is a locked species.
    **/
    static protected ImageIcon			lockedSpeciesIcon;

    /**
     * Icon to use when the item is an unlocked species chromosome
    **/
    static protected ImageIcon        	unlockedSpeciesChromosomeIcon;

    /**
     * Icon to use when the item is a locked species chromosome
    **/
    static protected ImageIcon        	lockedSpeciesChromosomeIcon;

    /**
     * Icon to use when the item is an unlocked gene
    **/
    static protected ImageIcon        	unlockedGeneIcon;

    /**
     * Icon to use when the item is a locked gene
    **/
    static protected ImageIcon        	lockedGeneIcon;

    /**
     * Icon to use when the item is an unlocked allele
    **/
    static protected ImageIcon		  	unlockedSpeciesAlleleIcon1;
    static protected ImageIcon		  	unlockedSpeciesAlleleIcon2;
    static protected ImageIcon		  	unlockedSpeciesAlleleIcon3;
    static protected ImageIcon		  	unlockedSpeciesAlleleIcon4;
    static protected ImageIcon		  	unlockedSpeciesAlleleIcon5;

    /**
     * Icon to use when the item is a locked allele
    **/
    static protected ImageIcon		  	lockedSpeciesAlleleIcon1;
    static protected ImageIcon		  	lockedSpeciesAlleleIcon2;
    static protected ImageIcon		  	lockedSpeciesAlleleIcon3;
    static protected ImageIcon		  	lockedSpeciesAlleleIcon4;
    static protected ImageIcon		  	lockedSpeciesAlleleIcon5;

    /**
     * Icon to use when the item is an unlocked rule
    **/
    static protected ImageIcon        	unlockedRuleIcon;

    /**
     * Icon to use when the item is a locked rule
    **/
    static protected ImageIcon        	lockedRuleIcon;

    /**
     * Icon to use when the item is an unlocked trait
    **/
    static protected ImageIcon        	unlockedTraitIcon;

    /**
     * Icon to use when the item is a locked trait
    **/
    static protected ImageIcon        	lockedTraitIcon;
    
    /**
     * Icon to use when the item is an unlocked characteristic
    **/
    static protected ImageIcon        	unlockedCharacteristicIcon;

    /**
     * Icon to use when the item is a locked characteristic
    **/
    static protected ImageIcon        	lockedCharacteristicIcon;

    /**
     * Icon to use when the item is an unlocked environment
    **/
    static protected ImageIcon			unlockedEnvironmentIcon;

    /**
     * Icon to use when the item is a locked environment
    **/
    static protected ImageIcon			lockedEnvironmentIcon;

    /**
     * Icon to use when the item is an unlocked terrain
    **/
    static protected ImageIcon			unlockedTerrainIcon;

    /**
     * Icon to use when the item is a locked terrain
    **/
    static protected ImageIcon			lockedTerrainIcon;

    /**
     * Icon to use when the item is an unlocked organism
    **/
    static protected ImageIcon			unlockedOrganismIcon;

    /**
     * Icon to use when the item is a locked organism
    **/
    static protected ImageIcon			lockedOrganismIcon;

    /**
     * Icon to use when the item is an unlocked species image
    **/
    static protected ImageIcon			unlockedSpeciesImageIcon;

    /**
     * Icon to use when the item is a locked species image
    **/
    static protected ImageIcon			lockedSpeciesImageIcon;

    /**
     * Icon to use when the item is an unlocked species image column
    **/
    static protected ImageIcon			unlockedSpeciesImageColumnIcon;

    /**
     * Icon to use when the item is a locked species image column
    **/
    static protected ImageIcon			lockedSpeciesImageColumnIcon;

    /**
     * Icon to use when the item is an unlocked species image row
    **/
    static protected ImageIcon			unlockedSpeciesImageRowIcon;

    /**
     * Icon to use when the item is a locked species image row
    **/
    static protected ImageIcon			lockedSpeciesImageRowIcon;

    /**
     * Selected?
    **/
    static protected boolean	 	  	selected;

    /**
     * Locked state
    **/
    static protected boolean		  	locked;

    static
    {
        try
        {
            unlockedWorldIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_world2.gif");
            lockedWorldIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_world2.gif");
            unlockedSpeciesIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_species2.gif");
            lockedSpeciesIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_species2.gif");
            unlockedEnvironmentIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_environment2.gif");
            lockedEnvironmentIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_environment2.gif");
            unlockedSpeciesChromosomeIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_chromosome2.gif");
            lockedSpeciesChromosomeIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_chromosome2.gif");
            unlockedGeneIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_gene2.gif");
            lockedGeneIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_gene2.gif");
            unlockedSpeciesAlleleIcon1 = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_allele1.gif");
            lockedSpeciesAlleleIcon1 = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_allele1.gif");
            unlockedSpeciesAlleleIcon2 = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_allele2.gif");
            lockedSpeciesAlleleIcon2 = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_allele2.gif");
            unlockedSpeciesAlleleIcon3 = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_allele3.gif");
            lockedSpeciesAlleleIcon3 = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_allele3.gif");
            unlockedSpeciesAlleleIcon4 = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_allele4.gif");
            lockedSpeciesAlleleIcon4 = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_allele4.gif");
            unlockedSpeciesAlleleIcon5 = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_allele5.gif");
            lockedSpeciesAlleleIcon5 = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_allele5.gif");
            unlockedRuleIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_rule2.gif");
            lockedRuleIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_rule2.gif");
            unlockedTraitIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_trait3.gif");
            lockedTraitIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_trait3.gif");
            unlockedCharacteristicIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_characteristic2.gif");
            lockedCharacteristicIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_characteristic2.gif");
            unlockedEnvironmentIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_environment3.gif");
            lockedEnvironmentIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_environment3.gif");
            unlockedOrganismIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_organism2.gif");
            lockedOrganismIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_organism2.gif");
            unlockedTerrainIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_terrain3.gif");
            lockedTerrainIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_terrain3.gif");
            unlockedSpeciesImageIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_species_image.gif");
            lockedSpeciesImageIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_species_image.gif");
            unlockedSpeciesImageColumnIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_simage_column.gif");
            lockedSpeciesImageColumnIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_simage_column.gif");
            unlockedSpeciesImageRowIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/unlocked_simage_row.gif");
            lockedSpeciesImageRowIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/locked_simage_row.gif");
        }
        catch (Exception e)
        {
            System.err.println("Couldn't load images: " + e);
        }

        selected = false;
        locked = false;
    }

    public TreeViewCellRenderer(TreeView treeView)
    {
        setFont(UIGraphics.getFontBold12());
        setBackgroundNonSelectionColor(treeView.getBackground());
        setBackgroundSelectionColor(treeView.getSelectionColor());
        setTextNonSelectionColor(Color.black);
        setTextSelectionColor(Color.black);
    }

    /**
      * This is messaged from JTree whenever it needs to get the size
      * of the component or it wants to draw it.
      * This attempts to set the font based on value, which will be
      * a TreeNode.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus)
    {
        /* Set the text and tooltips based on the engine object */
        if (value instanceof TreeViewNode)
        {
            TreeViewNode node = (TreeViewNode)value;
            
            int nodeType = node.getNodeType();
            int iRow = row % 5;
    
            this.selected = node.isSelected();

            locked = node.isLocked();
    
            /* Set the text and font. */
            String s = node.toString();
            setText(s);
            setToolTipText(s);
    
            /* Set the image. */
            switch (nodeType)
            {
                case TreeViewNode.WORLD_NODE_TYPE:
                    if (locked)
                    {
                        setOpenIcon(lockedWorldIcon);
                        setClosedIcon(lockedWorldIcon);
                    }
                    else
                    {
                        setOpenIcon(unlockedWorldIcon);
                        setClosedIcon(unlockedWorldIcon);
                    }
                    break;
            
                case TreeViewNode.SPECIES_NODE_TYPE:
                    if (locked)
                    {
                        setOpenIcon(lockedSpeciesIcon);
                        setClosedIcon(lockedSpeciesIcon);
                    }
                    else
                    {
                        setOpenIcon(unlockedSpeciesIcon);
                        setClosedIcon(unlockedSpeciesIcon);
                    }
                    break;
                
                case TreeViewNode.SPECIES_CHROMOSOME_NODE_TYPE:
                case TreeViewNode.ORGANISM_CHROMOSOME_NODE_TYPE:
                case TreeViewNode.ORGANISM_CHROMOSOME_PAIR_NODE_TYPE:
                    if (locked)
                    {
                        setOpenIcon(lockedSpeciesChromosomeIcon);
                        setClosedIcon(lockedSpeciesChromosomeIcon);
                    }
                    else
                    {
                        setOpenIcon(unlockedSpeciesChromosomeIcon);
                        setClosedIcon(unlockedSpeciesChromosomeIcon);
                    }
                    break;
                
                case TreeViewNode.ENVIRONMENT_NODE_TYPE:
                    if (locked)
                    {
                        setLeafIcon(lockedEnvironmentIcon);
                    }
                    else
                    {
                        setLeafIcon(unlockedEnvironmentIcon);
                    }
                    break;
                
                case TreeViewNode.GENE_NODE_TYPE:
                    if (locked)
                    {
                        setOpenIcon(lockedGeneIcon);
                        setClosedIcon(lockedGeneIcon);
                    }
                    else
                    {
                        setOpenIcon(unlockedGeneIcon);
                        setClosedIcon(unlockedGeneIcon);
                    }
                    break;
                
                case TreeViewNode.SPECIES_ALLELE_NODE_TYPE:
                case TreeViewNode.ORGANISM_ALLELE_NODE_TYPE:
                case TreeViewNode.ORGANISM_ALLELE_PAIR_NODE_TYPE:
                    if (locked)
                    {
                        switch (iRow)
                        {
                            case 0:
                                setLeafIcon(lockedSpeciesAlleleIcon1);
                                break;
                            case 1:
                                setLeafIcon(lockedSpeciesAlleleIcon2);
                                break;
                            case 2:
                                setLeafIcon(lockedSpeciesAlleleIcon3);
                                break;
                            case 3:
                                setLeafIcon(lockedSpeciesAlleleIcon4);
                                break;
                            default:
                                setLeafIcon(lockedSpeciesAlleleIcon5);
                                break;
                        }
                    }
                    else
                    {
                        switch (iRow)
                        {
                            case 0:
                                setLeafIcon(unlockedSpeciesAlleleIcon1);
                                break;
                            case 1:
                                setLeafIcon(unlockedSpeciesAlleleIcon2);
                                break;
                            case 2:
                                setLeafIcon(unlockedSpeciesAlleleIcon3);
                                break;
                            case 3:
                                setLeafIcon(unlockedSpeciesAlleleIcon4);
                                break;
                            default:
                                setLeafIcon(unlockedSpeciesAlleleIcon5);
                                break;
                        }
                    }
                    break;
                
                case TreeViewNode.RULE_NODE_TYPE:
                    if (locked)
                    {
                        setLeafIcon(lockedRuleIcon);
                    }
                    else
                    {
                        setLeafIcon(unlockedRuleIcon);
                    }
                    break;
                
                case TreeViewNode.TRAIT_NODE_TYPE:
                    if (locked)
                    {
                        setOpenIcon(lockedTraitIcon);
                        setClosedIcon(lockedTraitIcon);
                    }
                    else
                    {
                        setOpenIcon(unlockedTraitIcon);
                        setClosedIcon(unlockedTraitIcon);
                    }
                    break;
    
                case TreeViewNode.CHARACTERISTIC_NODE_TYPE:
                    if (locked)
                    {
                        setLeafIcon(lockedCharacteristicIcon);
                    }
                    else
                    {
                        setLeafIcon(unlockedCharacteristicIcon);
                    }
                    break;

                case TreeViewNode.TERRAIN_NODE_TYPE:
                    if (locked)
                    {
                        setLeafIcon(lockedTerrainIcon);
                    }
                    else
                    {
                        setLeafIcon(unlockedTerrainIcon);
                    }
                    break;

                case TreeViewNode.SPECIES_IMAGE_NODE_TYPE:
                    if (locked)
                    {
                        setOpenIcon(lockedSpeciesImageIcon);
                        setClosedIcon(lockedSpeciesImageIcon);
                    }
                    else
                    {
                        setOpenIcon(unlockedSpeciesImageIcon);
                        setClosedIcon(unlockedSpeciesImageIcon);
                    }
                    break;

                case TreeViewNode.SPECIES_IMAGE_COLUMN_NODE_TYPE:
                    if (locked)
                    {
                        setLeafIcon(lockedSpeciesImageColumnIcon);
                    }
                    else
                    {
                        setLeafIcon(unlockedSpeciesImageColumnIcon);
                    }
                    break;

                case TreeViewNode.SPECIES_IMAGE_ROW_NODE_TYPE:
                    if (locked)
                    {
                        setLeafIcon(lockedSpeciesImageRowIcon);
                    }
                    else
                    {
                        setLeafIcon(unlockedSpeciesImageRowIcon);
                    }
                    break;

                case TreeViewNode.ORGANISM_NODE_TYPE:
                    if (locked)
                    {
                        setOpenIcon(lockedOrganismIcon);
                        setClosedIcon(lockedOrganismIcon);
                    }
                    else
                    {
                        setOpenIcon(unlockedOrganismIcon);
                        setClosedIcon(unlockedOrganismIcon);
                    }
                    break;

                default:
                    break;
            }
        }

        return super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
    }
}
