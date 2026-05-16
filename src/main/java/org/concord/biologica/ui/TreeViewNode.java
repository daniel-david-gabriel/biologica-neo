//
// Class : TreeViewNode
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

import java.lang.String;
import java.util.Enumeration;
import java.util.Vector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;
import org.concord.biologica.engine.Gene;
import org.concord.biologica.engine.SpeciesAllele;
import org.concord.biologica.engine.SpeciesChromosome;

/**
 * A node of the tree in the left hand panel of the Species main window.<p>
 *
 * Every node has a corresponding EngineObject.  The visible manifestation
 * of the node will show the textual representation of the object, perhaps
 * a symbol indicating the type of object (e.g. species, chromosome, etc.)
 * and the parents and children of the object.<p>
 *
 * Child nodes are created when getChildCount() or isLeaf() is called,
 * not when the node is initially created.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:21 $
 * @author 		$Author: ed $
**/

public final class TreeViewNode
extends DefaultMutableTreeNode
implements PropertyChangeListener
{
	/**
	 * Node types, assuming the tree looks like
	 *
	 *		  world
	 *			|
	 *			+- species 1
	 *			|	|
	 *			|	+- chromosome 1 of species 1
	 *			|	|		|
	 *			|	|		+- gene 1 of species 1 on chromosome 1
	 *			|	|		|		|
	 *			|	|		|		+- allele 1 of gene 1 of species 1 on chromosome 1
	 *			|	|		|		+- allele 2 of gene 1 of species 1 on chromosome 1
	 *			|	|		|
	 *			|	|		+- gene 2 of species 1 on chromosome 1
	 *			|	|				|
	 *			|	|				+- allele 1 of gene 2 of species 1 on chromosome 1
	 *			|   |				+- allele 2 of gene 2 of species 1 on chromosome 1
	 *			|   |
	 *			|	+- chromosome 2 of species 1
	 *			|	|		|
	 *			|	|		+- gene 3 of species 1 on chromosome 2
	 *			|	|		|		|
	 *			|	|		|		+- allele 1 of gene 3 of species 1 on chromosome 2
	 *			|	|		|		+- allele 2 of gene 3 of species 1 on chromosome 2
	 *			|	|		|
	 *			|	|		+- gene 4 of species 1 on chromosome 2
	 *			|	|				|
	 *			|	|				+- allele 1 of gene 4 of species 1 on chromosome 2
	 *			|	|				+- allele 2 of gene 4 of species 1 on chromosome 2
	 *			|	|
	 *			|	+- other chromosomes ...
	 *			|	|
	 *			|	+- trait 1 of species 1
	 *			|	|		|
	 *			|	|		+- characteristic 1 of trait 1
	 *			|	|		+- characteristic 2 of trait 1
	 *			|	|		
	 *			|	+- trait 2 of species 1
	 *			|	|		|
	 *			|	|		+- characteristic 1 of trait 2
	 *			|	|		+- characteristic 2 of trait 2
	 *			|	|		
	 *			|	+- other traits...
	 *			|	|
	 *			|	+- rule 1 of species 1
	 *			|	+- rule 2 of species 1
	 *			|	+- other rules...
	 *			|	|		
	 *			|	+- species image 1 of species 1
	 *			|	|		|
	 *			|	|		+- species image column 1 of species image 1
	 *			|	|		+- species image column 2 of species image 1
	 *			|	|		+- other species image columns...
	 *			|	|		|
	 *			|	|		+- species image row 1 of species image 1
	 *			|	|		+- species image row 2 of species image 1
	 *			|	|		+- other species image rows...
	 *			|	|		
	 *			|	+- species image 2 of species 1
	 *			|	|		|
	 *			|	|		+- species image column 1 of species image 2
	 *			|	|		+- species image column 2 of species image 2
	 *			|	|		+- other species image columns...
	 *			|	|		|
	 *			|	|		+- species image row 1 of species image 2
	 *			|	|		+- species image row 2 of species image 2
	 *			|	|		+- other species image rows...
	 *			|	|		
	 *			|	+- other species images, species image columns and rows...
	 *			|
	 *			+- species 2
	 *			|		|
	 *			|		+- ...
	 *			|
	 *			+- organism 1
	 *			|		|
	 *			|		+- organism chromosome pair 1
	 *			|		|		|
	 *			|		|		+- organism allele pair 1
	 *			|		|		+- organism allele pair 2
	 *			|		|		+- other organism allele pairs...
	 *			|		|
	 *			|		+- organism chromosome pair 2
	 *			|		+- other organism chromosome pairs...
	 *			|		
	 *			+- organism 2
	 *			+- ...
	 *			|
	 *			+- terrain 1
	 *			+- terrain 2
	 *			+- ...
	 *			|
	 *      	+- environment 1
	 *			+- environment 2
	 *			+- ...
	**/
	public static final int WORLD_NODE_TYPE						= 1;
	public static final int SPECIES_NODE_TYPE					= 2;
	public static final int SPECIES_CHROMOSOME_NODE_TYPE		= 3;
	public static final int GENE_NODE_TYPE						= 4;
	public static final int SPECIES_ALLELE_NODE_TYPE			= 5;
	public static final int RULE_NODE_TYPE						= 6;
	public static final int TRAIT_NODE_TYPE						= 7;
	public static final int CHARACTERISTIC_NODE_TYPE			= 8;
	public static final int ENVIRONMENT_NODE_TYPE				= 9;
	public static final int ORGANISM_NODE_TYPE					= 10;
	public static final int TERRAIN_NODE_TYPE					= 11;
	public static final int SPECIES_IMAGE_NODE_TYPE				= 12;
	public static final int SPECIES_IMAGE_COLUMN_NODE_TYPE		= 13;
	public static final int SPECIES_IMAGE_ROW_NODE_TYPE			= 14;
	public static final int ORGANISM_CHROMOSOME_NODE_TYPE		= 15;
	public static final int ORGANISM_ALLELE_NODE_TYPE			= 16;
	public static final int ORGANISM_CHROMOSOME_PAIR_NODE_TYPE	= 17;
	public static final int ORGANISM_ALLELE_PAIR_NODE_TYPE		= 18;

	/**
	 * Type of engine object node type.  Must be one of
	 * the above node type values (e.g. SPECIES_NODE_TYPE).
	 * This is just an optimization to avoid lots of calls
	 * to instanceof all the time.<p>
	**/
	private int				nodeType;

	/**
	 * Indicates if we've created child nodes for this node yet.
	 * This is only relevant for nodes that might have children.<p>
	**/
	private boolean			haveCreatedChildNodes;

	/**
	 * This node selected?
	**/
	private boolean			selected;

	/**
	 * Does this node have focus?
	**/
	private boolean			hasFocus;

	/**
	 * Is this node deleted?
	**/
	private boolean			deleted;

	/**
	 * Is this node locked?
	**/
	private boolean			locked;

	/**
	 * Containing tree view.  Needed to notify when node changes.
	**/
	private TreeView		treeView;

    /**
     * Constructs a new TreeViewNode object.<p>
	 *
	 * @param		aTreeView TreeView - tree view containing this node
	 * @param		anEngineObject EngineObject - the object for this node, may not be null
	 * @exception 	IllegalArgumentException - input argument(s) illegal
    **/
    public TreeViewNode(TreeView aTreeView,
						EngineObject anEngineObject)
	{
		super(anEngineObject);

		// anEngineObject may not be null
		if (anEngineObject == null)
		{
			throw new IllegalArgumentException("input anEngineObject null");
		}

		// aTreeView may not be null
		if (aTreeView == null)
		{
			throw new IllegalArgumentException("input aTreeView null");
		}
		treeView = aTreeView;

		haveCreatedChildNodes = false;

		// Determine node type
		if (anEngineObject instanceof World)
		{
			nodeType = WORLD_NODE_TYPE;
		}
		else if (anEngineObject instanceof Species)
		{
			nodeType = SPECIES_NODE_TYPE;
		}
		else if (anEngineObject instanceof SpeciesChromosome)
		{
			nodeType = SPECIES_CHROMOSOME_NODE_TYPE;
		}
		else if (anEngineObject instanceof Gene)
		{
			nodeType = GENE_NODE_TYPE;
		}
		else if (anEngineObject instanceof SpeciesAllele)
		{
			nodeType = SPECIES_ALLELE_NODE_TYPE;
		}
		else if (anEngineObject instanceof GenotypeToPhenotypeRule)
		{
			nodeType = RULE_NODE_TYPE;
		}
		else if (anEngineObject instanceof Trait)
		{
			nodeType = TRAIT_NODE_TYPE;
		}
		else if (anEngineObject instanceof Characteristic)
		{
			nodeType = CHARACTERISTIC_NODE_TYPE;
		}
		else if (anEngineObject instanceof Environment)
		{
			nodeType = ENVIRONMENT_NODE_TYPE;
		}
		else if (anEngineObject instanceof Organism)
		{
			nodeType = ORGANISM_NODE_TYPE;
		}
		else if (anEngineObject instanceof Terrain)
		{
			nodeType = TERRAIN_NODE_TYPE;
		}
		else if (anEngineObject instanceof SpeciesImage)
		{
			nodeType = SPECIES_IMAGE_NODE_TYPE;
		}
		else if (anEngineObject instanceof SpeciesImageColumn)
		{
			nodeType = SPECIES_IMAGE_COLUMN_NODE_TYPE;
		}
		else if (anEngineObject instanceof SpeciesImageRow)
		{
			nodeType = SPECIES_IMAGE_ROW_NODE_TYPE;
		}
		else if (anEngineObject instanceof OrganismChromosome)
		{
			nodeType = ORGANISM_CHROMOSOME_NODE_TYPE;
		}
		else if (anEngineObject instanceof OrganismAllele)
		{
			nodeType = ORGANISM_ALLELE_NODE_TYPE;
		}
		else if (anEngineObject instanceof OrganismChromosomePair)
		{
			nodeType = ORGANISM_CHROMOSOME_PAIR_NODE_TYPE;
		}
		else if (anEngineObject instanceof OrganismAllelePair)
		{
			nodeType = ORGANISM_ALLELE_PAIR_NODE_TYPE;
		}
		else
		{
			throw new IllegalArgumentException("unexpected object type in TreeViewNode");
		}

		hasFocus = false;
		deleted = false;
		selected = false;
		locked = anEngineObject.isLocked();

		// Subscribe to object's changes
		anEngineObject.addPropertyChangeListener(this);
    }

	/**
	 * Returns if this node is a leaf node (has no children) or not.<p>
	 *
	 * @return	boolean - true if node is a leaf, else false
	**/
    public boolean isLeaf()
	{
		Species s;

		if (deleted == true)
		{
			return true;
		}

		if (haveCreatedChildNodes == false)
		{
			createChildNodes();
		}

		switch (nodeType)
		{
			case WORLD_NODE_TYPE:
			case SPECIES_NODE_TYPE:
			case SPECIES_CHROMOSOME_NODE_TYPE:
			case GENE_NODE_TYPE:
			case TRAIT_NODE_TYPE:
			case SPECIES_IMAGE_NODE_TYPE:
			case ORGANISM_NODE_TYPE:
			case ORGANISM_CHROMOSOME_NODE_TYPE:
			case ORGANISM_CHROMOSOME_PAIR_NODE_TYPE:
				return false;

			case SPECIES_ALLELE_NODE_TYPE:
			case CHARACTERISTIC_NODE_TYPE:
			case RULE_NODE_TYPE:
			case ENVIRONMENT_NODE_TYPE:
			case ORGANISM_ALLELE_NODE_TYPE:
			case ORGANISM_ALLELE_PAIR_NODE_TYPE:
			case TERRAIN_NODE_TYPE:
			case SPECIES_IMAGE_COLUMN_NODE_TYPE:
			case SPECIES_IMAGE_ROW_NODE_TYPE:
				return true;

			default:
				throw new IllegalArgumentException("illegal node type");
		}
    }

    /**
      * Return the number of children.<p>
	  *
	  * @return		int - number of children
      */
    public int getChildCount()
	{
		Gene gene;
		World world;
		Species species;
		SpeciesChromosome chromosome;
		Trait trait;
		SpeciesImage speciesImage;
		Organism organism;
		OrganismChromosome organismChromosome;
		OrganismChromosomePair organismChromosomePair;

		if (deleted == true)
		{
			return 0;
		}

		if (haveCreatedChildNodes == false)
		{
			createChildNodes();
		}

		switch (nodeType)
		{
			case WORLD_NODE_TYPE:
				world = (World) getUserObject();
				return world.getNumberOfSpecies() +
						world.getNumberOfTerrains() +
						world.getNumberOfEnvironments() +
						world.getNumberOfOrganisms();
				
			case SPECIES_NODE_TYPE:
				species = (Species) getUserObject();
				return species.getNumberOfChromosomes() +
						species.getNumberOfTraits() +
						species.getNumberOfGenotypeToPhenotypeRules() +
						species.getNumberOfSpeciesImages();

			case SPECIES_CHROMOSOME_NODE_TYPE:
				chromosome = (SpeciesChromosome) getUserObject();
				return chromosome.getNumberOfGenes();

			case GENE_NODE_TYPE:
				gene = (Gene) getUserObject();
				return gene.getNumberOfSpeciesAlleles();
			
			case TRAIT_NODE_TYPE:
				trait = (Trait) getUserObject();
				return trait.getNumberOfCharacteristics();
				
			case SPECIES_IMAGE_NODE_TYPE:
				speciesImage = (SpeciesImage) getUserObject();
				return speciesImage.getNumberOfSpeciesImageColumns() +
						speciesImage.getNumberOfSpeciesImageRows();

			case ORGANISM_NODE_TYPE:
				organism = (Organism) getUserObject();
				return organism.getNumberOfOrganismChromosomePairs();

			case ORGANISM_CHROMOSOME_NODE_TYPE:
				organismChromosome = (OrganismChromosome) getUserObject();
				return organismChromosome.getNumberOfOrganismAlleles();

			case ORGANISM_CHROMOSOME_PAIR_NODE_TYPE:
				organismChromosomePair = (OrganismChromosomePair) getUserObject();
				return organismChromosomePair.getNumberOfOrganismAllelePairs();

			case SPECIES_ALLELE_NODE_TYPE:
			case RULE_NODE_TYPE:
			case CHARACTERISTIC_NODE_TYPE:
			case ENVIRONMENT_NODE_TYPE:
			case ORGANISM_ALLELE_NODE_TYPE:
			case ORGANISM_ALLELE_PAIR_NODE_TYPE:
			case SPECIES_IMAGE_COLUMN_NODE_TYPE:
			case SPECIES_IMAGE_ROW_NODE_TYPE:
			case TERRAIN_NODE_TYPE:
			default:
				return 0;
		}
    }

    /**
      * Return the child at the given index.<p>
	  *
	  * We override this method to ensure that the child nodes are created.<p>
	  *
	  * @param		index int - child index
	  * @return		the TreeNode in this node's child array at the specified index
      */
    public TreeNode getChildAt(int index)
	{
		if (deleted == true)
		{
			return null;
		}

		if (haveCreatedChildNodes == false)
		{
			createChildNodes();
		}

		return super.getChildAt(index);
    }

    /**
	 * Create the child nodes of this node.<p>
	 *
     * Called when we know we finally need to create child nodes,
	 * presumably because some event occurred where we need to
	 * show the child nodes.<p>
    **/
    protected void createChildNodes()
	{
		TreeViewNode             newNode;
		int							counter = 0;

		if (deleted == true || haveCreatedChildNodes == true)
		{
			return;
		}
		haveCreatedChildNodes = true;

		switch (nodeType)
		{
			case WORLD_NODE_TYPE:
				{
					World world = (World) getUserObject();

					// Create species nodes
					Enumeration enumSpecies = world.getSpecies();
					Species species;
					while (enumSpecies.hasMoreElements())
					{
						species = (Species) enumSpecies.nextElement();
						newNode = new TreeViewNode(treeView,species);
						insert(newNode,counter);
						counter++;
					}

					// Create terrain nodes
					Enumeration enumTerrains = world.getTerrains();
					Terrain terrain;
					while (enumTerrains.hasMoreElements())
					{
						terrain = (Terrain) enumTerrains.nextElement();
						newNode = new TreeViewNode(treeView,terrain);
						insert(newNode,counter);
						counter++;
					}

					// Create environment nodes
					Enumeration enumEnvironments = world.getEnvironments();
					Environment environment;
					while (enumEnvironments.hasMoreElements())
					{
						environment = (Environment) enumEnvironments.nextElement();
						newNode = new TreeViewNode(treeView,environment);
						insert(newNode,counter);
						counter++;
					}

					// Create organism nodes
					Enumeration enumOrganisms = world.getOrganisms();
					Organism organism;
					while (enumOrganisms.hasMoreElements())
					{
						organism = (Organism) enumOrganisms.nextElement();
						newNode = new TreeViewNode(treeView,organism);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case SPECIES_NODE_TYPE:
				{
					// Create chromosome nodes
					Species species = (Species) getUserObject();
					SpeciesChromosome chromosome;
					Enumeration enumChromosomes;

					enumChromosomes = species.getNonSexChromosomes();
					while (enumChromosomes.hasMoreElements())
					{
						chromosome = (SpeciesChromosome) enumChromosomes.nextElement();
						newNode = new TreeViewNode(treeView,chromosome);
						insert(newNode,counter);
						counter++;
					}
					
					enumChromosomes = species.getSexChromosomes();
					while (enumChromosomes.hasMoreElements())
					{
						chromosome = (SpeciesChromosome) enumChromosomes.nextElement();
						newNode = new TreeViewNode(treeView,chromosome);
						insert(newNode,counter);
						counter++;
					}

					// Traits
					Trait trait;
					Enumeration enumTraits = species.getTraits();
					while (enumTraits.hasMoreElements())
					{
						trait = (Trait) enumTraits.nextElement();
						newNode = new TreeViewNode(treeView,trait);
						insert(newNode,counter);
						counter++;
					}

					// Rules
					GenotypeToPhenotypeRule rule;
					Enumeration enumRules = species.getGenotypeToPhenotypeRules();
					while (enumRules.hasMoreElements())
					{
						rule = (GenotypeToPhenotypeRule) enumRules.nextElement();
						newNode = new TreeViewNode(treeView,rule);
						insert(newNode,counter);
						counter++;
					}

					// SpeciesImages
					SpeciesImage speciesImage;
					Enumeration eSpeciesImages = species.getSpeciesImages();
					while (eSpeciesImages.hasMoreElements())
					{
						speciesImage = (SpeciesImage) eSpeciesImages.nextElement();
						newNode = new TreeViewNode(treeView,speciesImage);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case SPECIES_CHROMOSOME_NODE_TYPE:
				{
					// Create gene nodes
					SpeciesChromosome chromosome = (SpeciesChromosome) getUserObject();
					
					Gene gene;
					Enumeration enumGenes = chromosome.getGenes();
					while (enumGenes.hasMoreElements())
					{
						gene = (Gene) enumGenes.nextElement();
						newNode = new TreeViewNode(treeView,gene);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case GENE_NODE_TYPE:
				{
					// Create allele nodes
					Gene gene = (Gene) getUserObject();
					
					SpeciesAllele allele;
					Enumeration enumAlleles = gene.getSpeciesAlleles();
					while (enumAlleles.hasMoreElements())
					{
						allele = (SpeciesAllele) enumAlleles.nextElement();
						newNode = new TreeViewNode(treeView,allele);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case TRAIT_NODE_TYPE:
				{
					// Create characteristic nodes
					Trait trait = (Trait) getUserObject();

					Characteristic characteristic;
					Enumeration enumCharacteristics = trait.getCharacteristics();
					while (enumCharacteristics.hasMoreElements())
					{
						characteristic = (Characteristic) enumCharacteristics.nextElement();
						newNode = new TreeViewNode(treeView,characteristic);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case SPECIES_IMAGE_NODE_TYPE:
				{
					// Create species image column and row nodes
					SpeciesImage speciesImage = (SpeciesImage) getUserObject();

					SpeciesImageColumn speciesImageColumn;
					Enumeration eSpeciesImageColumns = speciesImage.getSpeciesImageColumns();
					while (eSpeciesImageColumns.hasMoreElements())
					{
						speciesImageColumn = (SpeciesImageColumn) eSpeciesImageColumns.nextElement();
						newNode = new TreeViewNode(treeView,speciesImageColumn);
						insert(newNode,counter);
						counter++;
					}

					SpeciesImageRow speciesImageRow;
					Enumeration eSpeciesImageRows = speciesImage.getSpeciesImageRows();
					while (eSpeciesImageRows.hasMoreElements())
					{
						speciesImageRow = (SpeciesImageRow) eSpeciesImageRows.nextElement();
						newNode = new TreeViewNode(treeView,speciesImageRow);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case ORGANISM_NODE_TYPE:
				{
					// Create organism chromosome nodes
					Organism organism = (Organism) getUserObject();

					OrganismChromosomePair organismChromosomePair;
					Enumeration eChromosomePairs = organism.getOrganismChromosomePairs();
					while (eChromosomePairs.hasMoreElements())
					{
						organismChromosomePair = (OrganismChromosomePair) eChromosomePairs.nextElement();
						newNode = new TreeViewNode(treeView,organismChromosomePair);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case ORGANISM_CHROMOSOME_NODE_TYPE:
				{
					// Create organism allele nodes
					OrganismChromosome organismChromosome = (OrganismChromosome) getUserObject();

					OrganismAllele organismAllele;
					Enumeration eAlleles = organismChromosome.getOrganismAlleles();
					while (eAlleles.hasMoreElements())
					{
						organismAllele = (OrganismAllele) eAlleles.nextElement();
						newNode = new TreeViewNode(treeView,organismAllele);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case ORGANISM_CHROMOSOME_PAIR_NODE_TYPE:
				{
					// Create organism allele pair nodes
					OrganismChromosomePair organismChromosomePair = (OrganismChromosomePair) getUserObject();

					OrganismAllelePair organismAllelePair;
					Enumeration eAllelePairs = organismChromosomePair.getOrganismAllelePairs();
					while (eAllelePairs.hasMoreElements())
					{
						organismAllelePair = (OrganismAllelePair) eAllelePairs.nextElement();
						newNode = new TreeViewNode(treeView,organismAllelePair);
						insert(newNode,counter);
						counter++;
					}
				}
				break;

			case SPECIES_ALLELE_NODE_TYPE:
			case RULE_NODE_TYPE:
			case CHARACTERISTIC_NODE_TYPE:
			case ENVIRONMENT_NODE_TYPE:
			case TERRAIN_NODE_TYPE:
			case ORGANISM_ALLELE_NODE_TYPE:
			case ORGANISM_ALLELE_PAIR_NODE_TYPE:
			case SPECIES_IMAGE_COLUMN_NODE_TYPE:
			case SPECIES_IMAGE_ROW_NODE_TYPE:
			default:
				break;
		}
    }

	/**
	 * Get the string corresponding to this node.
	 *
	 * @return		String - string to be displayed for this node.
	**/
	public String toString()
	{
		if (deleted == true)
		{
			return "";
		}

		return getUserObject().toString();
	}

	/**
	 * Returns node type of this node.<p>
	 *
	 * @return	int - node type - one of above values
	**/
	public int getNodeType()
	{
		return nodeType;
	}

	/**
	 * Get whether this node has focus.<p>
	 *
	 * @return	boolean - true if has focus, false if doesn't have focus
	**/
	public boolean getHasFocus()
	{
		return hasFocus;
	}

	/**
	 * Set whether this node has focus.<p>
	 *
	 * @param	hasFocus boolean - true if has focus, false if doesn't have focus
	**/
	public void setHasFocus(boolean hasFocus)
	{
		this.hasFocus = hasFocus;
	}

	/**
	 * Return the selected state of this node
	 *
	 * @return	boolean - selected state of this node (true == selected)
	**/
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * Set the selected state of this node.<p>
	 *
	 * @param	aSelected boolean - selected state of this node
	**/
	public void setSelected(boolean aSelected)
	{
		if (aSelected != selected)
		{
			selected = aSelected;
		}
	}

	/**
	 * Return the locked state of this node.
	 *
	 * @return	boolean - locked state of this node
	**/
	public boolean isLocked()
	{
		return locked;
	}

	/**
	 * Handle property change events
	 *
	 * @param	event PropertyChangeEvent - the property change event
	**/
	public void propertyChange(PropertyChangeEvent event)
	{
		String propertyName = event.getPropertyName();

		if (propertyName.equals(EngineProp.LOCKED_STATE))
		{
			boolean oldLocked = locked;
			
			int newLockedState = ((Integer)event.getNewValue()).intValue();
			if (newLockedState != EngineObject.UNLOCKED)
			{
				locked = true;
			}
			else
			{
				locked = false;
			}

			if (oldLocked != locked)
			{
				// Notify containing species left view
				treeView.nodeChanged(this);
			}
		}
		else if (propertyName.equals(EngineProp.DELETED))
		{
			// The engine object corresponding to this node has been deleted
			deleted = true;
			treeView.nodeObjectDeleted(this);
		}
		else if (propertyName.equals(EngineProp.SELECTED))
		{
			// The engine object corresponding to this node has been selected or deselected
			selected = ((Boolean) event.getNewValue()).booleanValue();
			treeView.nodeSelectedStateChanged(this,
											  ((Boolean) event.getOldValue()).booleanValue(),
											  selected);
		}
		else if (propertyName.equals(EngineProp.NAME) ||
				 propertyName.equals(EngineProp.LENGTH_IN_BASES) ||
				 propertyName.equals(EngineProp.START_INDEX_IN_HOLDER) ||
				 propertyName.equals(EngineProp.TEXT_SYMBOL) ||
				 propertyName.equals(EngineProp.FILENAME))
		{
			// Notify containing species left view
			treeView.nodeChanged(this);
		}
		else if (propertyName.equals(EngineProp.NUMBER_TYPE))
		{
			// The engine object, a chromosome, changed in number type
			int oldNumberType = ((Integer)event.getOldValue()).intValue();
			int newNumberType = ((Integer)event.getNewValue()).intValue();

			if (oldNumberType != newNumberType)
			{
				// Notify containing species left view
				treeView.nodeChanged(this);
			}
		}
		else if (propertyName.equals(EngineProp.SPECIES_ADDED))
		{
			// New species added to engine, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Species newSpecies = (Species)event.getNewValue();
				TreeViewNode newNode = new TreeViewNode(treeView, newSpecies);

				// Make the new species the last one in parent
				World world = newSpecies.getWorld();
				int indexInParent = world.getNumberOfSpecies() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.SPECIES_CHROMOSOME_ADDED))
		{
			// New species chromosome added to species, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				SpeciesChromosome newSpeciesChromosome = (SpeciesChromosome)event.getNewValue();
				
				TreeViewNode newNode = new TreeViewNode(treeView,
															  newSpeciesChromosome);
	
				// Determine index of chromosome in parent by finding the position
				// of the chromosome in the species' vector of chromosomes, where
				// we'll assume autosomes should be first in the tree, sex chromosomes last.
				int indexInParent = 0;
				int numberType = newSpeciesChromosome.getNumberType();
				Species species = newSpeciesChromosome.getSpecies();
				SpeciesChromosome sc;
				Enumeration eSpeciesChromosomes;
				if (numberType == IChromosome.X_CHROMOSOME || numberType == IChromosome.Y_CHROMOSOME)
				{
					eSpeciesChromosomes = species.getSexChromosomes();
					indexInParent += species.getNumberOfNonSexChromosomes();
				}
				else
				{
					eSpeciesChromosomes = species.getNonSexChromosomes();
				}
	
				while (eSpeciesChromosomes.hasMoreElements())
				{
					sc = (SpeciesChromosome) eSpeciesChromosomes.nextElement();
					if (sc == newSpeciesChromosome)
					{
						break;
					}
					else
					{
						indexInParent++;
					}
				}
	
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.TRAIT_ADDED))
		{
			// New trait added to a species, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Trait newTrait = (Trait)event.getNewValue();
				
				TreeViewNode newNode = new TreeViewNode(treeView,
															  newTrait);
	
				// Make the new trait the last one in parent, where nodes
				// are arranged in the order chromosomes, traits and rules
				Species species = newTrait.getSpecies();
				int indexInParent = species.getNumberOfNonSexChromosomes() +
									species.getNumberOfSexChromosomes() +
									species.getNumberOfTraits() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.CHARACTERISTIC_ADDED))
		{
			// New characteristic added to a trait, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Characteristic newCharacteristic = (Characteristic)event.getNewValue();
				
				TreeViewNode newNode = new TreeViewNode(treeView,
															  newCharacteristic);
	
				// Make the new characteristic the last node in parent trait
				Trait trait = newCharacteristic.getTrait();
				int indexInParent = trait.getNumberOfCharacteristics() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_ADDED))
		{
			// New genotype to phenotype rule added to a species, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				GenotypeToPhenotypeRule newRule = (GenotypeToPhenotypeRule)event.getNewValue();
				
				TreeViewNode newNode = new TreeViewNode(treeView,
															  newRule);
	
				// Make the new rule the last one in parent, where nodes
				// are arranged in the order chromosomes, traits and rules
				Species species = newRule.getSpecies();
				int indexInParent = species.getNumberOfNonSexChromosomes() +
									species.getNumberOfSexChromosomes() +
									species.getNumberOfTraits() +
									species.getNumberOfGenotypeToPhenotypeRules() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.GENOTYPE_TO_PHENOTYPE_RULE_MOVED))
		{
			// Genotype to phenotype rule moved within a species, so delete the old
			// node for it and create a new node for it in the proper location
			if (haveCreatedChildNodes == true)
			{
				// Get rule, species and new index of moved rule in species
				GenotypeToPhenotypeRule movedRule = (GenotypeToPhenotypeRule)event.getNewValue();
				Species species = movedRule.getSpecies();
				int oldIndexInSpecies = ((Integer)event.getOldValue()).intValue();
				int newIndexInSpecies = species.getIndexOfGenotypeToPhenotypeRule(movedRule);
				
				// Delete old node corresponding to the moved rule
				TreeViewNode oldRuleNode = (TreeViewNode) getChildAt(
					species.getNumberOfNonSexChromosomes() +
					species.getNumberOfSexChromosomes() +
					species.getNumberOfTraits() +
					oldIndexInSpecies);
				treeView.nodeObjectDeleted(oldRuleNode);

				// Create new node corresponding to the moved rule
				TreeViewNode newNode = new TreeViewNode(treeView,movedRule);
	
				// Make the new rule the last one in parent, where nodes
				// are arranged in the order chromosomes, traits and rules
				int indexInParent = species.getNumberOfNonSexChromosomes() +
									species.getNumberOfSexChromosomes() +
									species.getNumberOfTraits() +
									newIndexInSpecies;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.GENE_ADDED))
		{
			// New gene added to a species chromosome (the object this node represents).
			//
			// If this node has already had its children created, then we need to create
			// a new node, add it and cause it to be expanded.
			//
			// If this node has not had its children created, then we just need to
			// expand this node, as that will cause the proper child nodes to be created.
			if (haveCreatedChildNodes == true)
			{
				Gene newGene = (Gene)event.getNewValue();
				TreeViewNode newNode = new TreeViewNode(treeView, newGene);
	
				SpeciesChromosome sc = newGene.getSpeciesChromosome();
				int indexInParent = sc.getNumberOfGenes() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.SPECIES_ALLELE_ADDED))
		{
			// New species allele added to a gene, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				SpeciesAllele newSpeciesAllele = (SpeciesAllele)event.getNewValue();
			
				TreeViewNode newNode = new TreeViewNode(treeView,newSpeciesAllele);

				// Make the new species allele the last one in parent
				Gene gene = newSpeciesAllele.getGene();
				int indexInParent = gene.getNumberOfSpeciesAlleles() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.ENVIRONMENT_ADDED))
		{
			// New environment added to a world, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Environment newEnvironment = (Environment) event.getNewValue();
				
				TreeViewNode newNode = new TreeViewNode(treeView,
															  newEnvironment);
	
				// Make the new environment the last environment in parent world
				World world = newEnvironment.getWorld();
				int indexInParent = world.getNumberOfSpecies() +
									world.getNumberOfTerrains() +
									world.getNumberOfEnvironments() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.TERRAIN_ADDED))
		{
			// New terrain added to a world, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Terrain newTerrain = (Terrain) event.getNewValue();
				
				TreeViewNode newNode = new TreeViewNode(treeView,
															  newTerrain);
	
				// Make the new terrain the last terrain in parent world
				World world = newTerrain.getWorld();
				int indexInParent = world.getNumberOfSpecies() +
									world.getNumberOfTerrains() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.ORGANISM_ADDED))
		{
			// New organism added to a world, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Organism newOrganism = (Organism) event.getNewValue();
				
				TreeViewNode newNode = new TreeViewNode(treeView,newOrganism);
	
				// Make the new organism the last organism in parent world
				World world = newOrganism.getWorld();
				int indexInParent = world.getNumberOfSpecies() +
									world.getNumberOfTerrains() +
									world.getNumberOfEnvironments() +
									world.getNumberOfOrganisms() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.SPECIES_IMAGE_ADDED))
		{
			// New species image added to a species, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Object object = event.getNewValue();
					
				SpeciesImage newSpeciesImage = (SpeciesImage) event.getNewValue();

				TreeViewNode newNode = new TreeViewNode(treeView,
															  newSpeciesImage);
				
				// Make the new species image node the last node in parent species
				Species species = newSpeciesImage.getSpecies();
				int indexInParent = species.getNumberOfNonSexChromosomes() +
									species.getNumberOfSexChromosomes() +
									species.getNumberOfTraits() +
									species.getNumberOfGenotypeToPhenotypeRules() +
									species.getNumberOfSpeciesImages() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.SPECIES_IMAGE_COLUMN_ADDED))
		{
			// New species image column added to a species image, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Object object = event.getNewValue();

				SpeciesImageColumn newSpeciesImageColumn = (SpeciesImageColumn) event.getNewValue();

				TreeViewNode newNode = new TreeViewNode(treeView,
															  newSpeciesImageColumn);
					
				// Make the new species image node column the last column node in parent species image
				SpeciesImage speciesImage = newSpeciesImageColumn.getSpeciesImage();
				int indexInParent = speciesImage.getNumberOfSpeciesImageColumns() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
		else if (propertyName.equals(EngineProp.SPECIES_IMAGE_ROW_ADDED))
		{
			// New species image row added to a species image, so create a node
			// for it and add it to the tree.
			if (haveCreatedChildNodes == true)
			{
				Object object = event.getNewValue();

				SpeciesImageRow newSpeciesImageRow = (SpeciesImageRow) event.getNewValue();

				TreeViewNode newNode = new TreeViewNode(treeView,
															  newSpeciesImageRow);
					
				// Make the new species image node row the last node in parent species image
				SpeciesImage speciesImage = newSpeciesImageRow.getSpeciesImage();
				int indexInParent = speciesImage.getNumberOfSpeciesImageColumns() +
									speciesImage.getNumberOfSpeciesImageRows() - 1;
				treeView.addNode(this,newNode,indexInParent);
			}
			else
			{
				createChildNodes();
			}
			treeView.expandNode(this);
		}
	}
}
