//
// Class : PedigreeOrganismView - The pedigree organism subview of the larger pedigree view.
//								  This is the portion of the pedigree view that has organisms displayed
//								  in it and is typically surrounded by the tool buttons and trait
//								  pulldown in the larger pedigree view.
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.38 $
// $Date: 2004/12/09 06:06:23 $
// $Author: dima $
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;
import org.concord.domainsupport.*;
import org.concord.util.logging.*;
import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.concord.pedagogica.engine.Logging;
import org.concord.pedagogica.engine.Activity;
/**
 * The pedigree organism view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.BACKGROUND - the background color of the view changed
 * <li> org.concord.biologica.ui.UIProp.CHARACTERISTICS_TEXT_VISIBLE - the characteristics text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM - the user clicked on an organism with the chromosome tool
 * <li> org.concord.biologica.ui.UIProp.CROSS_SUCCEEDED - the cross succeeded
 * <li> org.concord.biologica.ui.UIProp.FIXED_NUMBER_CHILDREN - the fixed number of children from a breeding
 * <li> org.concord.biologica.ui.UIProp.FONT - the font of the view changed
 * <li> org.concord.biologica.ui.UIProp.FOREGROUND - the foreground color of the view changed
 * <li> org.concord.biologica.ui.UIProp.LOCK_SYMBOL_VISIBLE - the lock symbol should or should not be displayed if appropriate
 * <li> org.concord.biologica.ui.UIProp.MAXIMUM_NUMBER_CHILDREN - the maximum number of children from a breeding
 * <li> org.concord.biologica.ui.UIProp.MINIMUM_NUMBER_CHILDREN - the minimum number of children from a breeding
 * <li> org.concord.biologica.ui.UIProp.NAME_TEXT_VISIBLE - the name text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.NUMBER_FEMALE_CHILDREN - the number of female children in a family when in male / female mode
 * <li> org.concord.biologica.ui.UIProp.NUMBER_MALE_CHILDREN - the number of male children in a family when in male / female mode
 * <li> org.concord.biologica.ui.UIProp.OFFSPRING_MODE - the pedigree view offspring mode changed
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_ADDED_TO_VIEW - an organism has been added to this view
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_IMAGE_SIZE - the image size to use for drawing organisms in this view
 * <li> org.concord.biologica.ui.UIProp.ORGANISM_REMOVED_FROM_VIEW - an organism has been removed from this view
 * <li> org.concord.biologica.ui.UIProp.PEDIGREE_TOOL_PICK_ON_ORGANISM - the user clicked on an organism with the pedigree tool
 * <li> org.concord.biologica.ui.UIProp.SEX_TEXT_VISIBLE - the sex text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.SNIP_TOOL_PICK_ON_FAMILY - the user clicked on an organism with the snip tool with family locked
 * <li> org.concord.biologica.ui.UIProp.SNIP_TOOL_PICK_ON_ORGANISM - the user clicked on an organism with the snip tool with organism locked
 * <li> org.concord.biologica.ui.UIProp.SPECIES_TEXT_VISIBLE - the species text visibility boolean changed
 * <li> org.concord.biologica.ui.UIProp.TEXT_INDENT - the indentation of text from left edge of image
 * <li> org.concord.biologica.ui.UIProp.TEXT_LINE_SPACING - the number of pixels between lines of text
 * <li> org.concord.biologica.ui.UIProp.TRAIT - the trait to draw, null if should draw whole organism
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#CHARACTERISTICS_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#CHROMOSOME_TOOL_PICK_ON_ORGANISM
 * @see org.concord.biologica.ui.UIProp#CROSS_SUCCEEDED
 * @see org.concord.biologica.ui.UIProp#FIXED_NUMBER_CHILDREN
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see org.concord.biologica.ui.UIProp#LOCK_SYMBOL_VISIBLE
 * @see org.concord.biologica.ui.UIProp#MAXIMUM_NUMBER_CHILDREN
 * @see org.concord.biologica.ui.UIProp#MINIMUM_NUMBER_CHILDREN
 * @see org.concord.biologica.ui.UIProp#NAME_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#NUMBER_FEMALE_CHILDREN
 * @see org.concord.biologica.ui.UIProp#NUMBER_MALE_CHILDREN
 * @see org.concord.biologica.ui.UIProp#OFFSPRING_MODE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_ADDED_TO_VIEW
 * @see org.concord.biologica.ui.UIProp#ORGANISM_IMAGE_SIZE
 * @see org.concord.biologica.ui.UIProp#ORGANISM_REMOVED_FROM_VIEW	
 * @see org.concord.biologica.ui.UIProp#PEDIGREE_TOOL_PICK_ON_ORGANISM
 * @see org.concord.biologica.ui.UIProp#SEX_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#SNIP_TOOL_PICK_ON_FAMILY
 * @see org.concord.biologica.ui.UIProp#SNIP_TOOL_PICK_ON_ORGANISM
 * @see org.concord.biologica.ui.UIProp#SPECIES_TEXT_VISIBLE
 * @see org.concord.biologica.ui.UIProp#TEXT_INDENT
 * @see org.concord.biologica.ui.UIProp#TEXT_LINE_SPACING
 * @see org.concord.biologica.ui.UIProp#TRAIT
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.38 $ $Date: 2004/12/09 06:06:23 $
 * @author 		$Author: dima $
**/
public final class PedigreeOrganismView
extends OrganismView
implements MouseListener, MouseMotionListener, PropertyChangeListener, SelectionPresenter, DomainView, Loggable
{
    /**
     * Random number generator used in choosing X or Y first in females.<p>
    **/
    static private Random random = new Random();

    /**
     * Offspring mode.<p>
     *
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_FIXED
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_MIN_MAX
     * @see org.concord.biologica.ui.PedigreeView#OFFSPRING_MODE_MALE_FEMALE 
    **/
    private int offspringMode;


	private PedigreeView thePedigreeView;
    /**
     * Active tool in parent pedigree view. Impacts how we handle mouse events
    **/
    private int activeTool;

    /**
     * Trait pulldown, to be kept current with species of organisms in this view
    **/
    private BioComboBox traitPulldown;
    
    private JCheckBox checkBox;

    /**
     * Offspring mode pulldown, to be kept current with offspringMode in this view
    **/
    private BioComboBox offspringModePulldown;

    /**
     * Maximum x of drawn organisms
    **/
    private int xMaximum;

    /**
     * Maximum y of drawn organisms
    **/
    private int yMaximum;

    /**
     * Vector of pedigree levels (generations) to show in this view
    **/
    private Vector pedigreeLevels = null;
    
    /**
     * Parent one pedigree organism, used during cross
    **/
    private PedigreeOrganism parentOnePedigreeOrganism = null;

    /**
     * Dynamic rectangle mouse down point
    **/
    private Point dynamicRectangleMouseDownPoint = null;

    /**
     * X location of mouse at previous event during rubber banding
    **/
    private int xMousePrevious;

    /**
     * Y location of mouse at previous event during rubber banding
    **/
    private int yMousePrevious;

    /**
     * Currently highlighted organism
    **/
    private PedigreeFamily highlightedFamily = null;

    /**
     * Currently highlighted organism
    **/
    private PedigreeOrganism highlightedOrganism = null;

    /**
     * Current species
    **/
    private Species currentSpecies;

    /**
     * The trait to draw, null if we should draw the whole organism
    **/
    private Trait trait;

    /**
     * The fixed number of children in a breeding.  This value is
     * only used when the view is in PedigreeView.OFFSPRING_MODE_FIXED mode.
    **/
    private int fixedNumberChildren;

    /**
     * The minimum number of children in a breeding.  This value is
     * only used when the view is in PedigreeView.OFFSPRING_MODE_MIN_MAX mode.
    **/
    private int minimumNumberChildren;

    /**
     * The maximum number of children in a breeding.  This value is
     * only used when the view is in PedigreeView.OFFSPRING_MODE_MIN_MAX mode.
    **/
    private int maximumNumberChildren;

    /**
     * The number of female children to create in a family when
     * the view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.
    **/
    private int numberFemaleChildren;

    /**
     * The number of male children to create in a family when
     * the view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.
    **/
    private int numberMaleChildren;

    /**
     * Are the organism images visible?
    **/
    private boolean organismImagesVisible;

    /**
     * Highlight set
    **/
    private SelectionSet highlightSet = null;

    /**
     * Selection set
    **/
    private SelectionSet selectionSet = null;

    /**
     * Only live children flag
    **/
    private boolean onlyLiveChildren = false;

    /**
     * Flag to assure we have a dimension before placing everything
    **/
    private boolean needPlaceLevels = false;
    
    
    /**
     *Flag to assure cross over turn on
     */
    private boolean blnCrossOverTurnOn = false; 
    
  
    
   /**
   	* Selection tool visibled before?
   **/
    private boolean blnSelectionToolVisible;
    
   /**
   	* Cross tool visibled before?
   **/
    private boolean blnCrossToolVisible;
    
   /**
   	* Snip tool visibled before?
   **/
    private boolean blnSnipToolVisible;
    
   /**
   	* Chromosome tool visibled before?
   **/
    private boolean blnChromosomeToolVisible;
    
    /**
     * Text on the Chromosome View
    **/
    private String chromosomeStr;
    
    /**
     *pop up chromosome view visible?
    **/
    private boolean blnChromosomeViewVisible;
 
  
    /**
     * Show Chromosomes
     */
     BioDialogBox chromosomeBox;
    
    /**
     * Creates a pedigree organism view.
    **/
    public PedigreeOrganismView()
    {
        super();
		
        // Set selection set to default initially
        selectionSet = SelectionSet.getDefaultSelectionSet();
        
        // Create selection set for highlighting path from children to ancestors
        highlightSet = new SelectionSet();
        
       
        // Set colors
        setBackground(Color.white);
        setForeground(Color.black);

        xMaximum = 0;
        yMaximum = 0;
        xMousePrevious = 0;
        yMousePrevious = 0;
        

        pedigreeLevels = new Vector();
        parentOnePedigreeOrganism = null;
        dynamicRectangleMouseDownPoint = null;
        trait = null;
        offspringMode = PedigreeView.OFFSPRING_MODE_MIN_MAX;
        activeTool = Tool.SELECTION;
        fixedNumberChildren = 4;
        minimumNumberChildren = 3;
        maximumNumberChildren = 5;
        numberFemaleChildren = 2;
        numberMaleChildren = 2;
        organismImagesVisible = false;
        chromosomeStr = "";

        // Override defaults
        organismImageSize = SpeciesImage.XXSMALL_IMAGE_SIZE;
        textIndent = 5;
        textLineSpacing = 5;
        lockSymbolVisible = false;
        characteristicsTextVisible = false;
        nameTextVisible = false;
        sexTextVisible = false;
        speciesTextVisible = false;
        preferredHeight = 50;
        preferredWidth = 50;
        blnChromosomeViewVisible = true;
     

        // Turn on double buffering
        setDoubleBuffered(true);

        // Listen for mouse clicks, and mouse motion
        
        addMouseListener(this);
        addMouseMotionListener(this);

        // Tell the selection set about this view
        selectionSet.addSelectionPresenter(this);
    }

    /**
     * Tell this view its scroll pane.
     *
     * @param		aScrollPane JScrollPane - the scroll pane containing this view
    **/
    public void setScrollPane(JScrollPane aScrollPane)
    {
        scrollPane = aScrollPane;
    }

    /**
     * Get the offspring mode
     *
     * @return		int - the offspring mode
    **/
    public int getOffspringMode()
    {
        return offspringMode;
    }

    /**
     * Set the offspring mode
     *
     * @param		anOffspringMode int - the new offspring mode
     * @exception	IllegalArgumentException - input offspring mode illegal
    **/
    public void setOffspringMode(int anOffspringMode)
    {
        // Return immediately if no change
        if (offspringMode == anOffspringMode)
        {
            return;
        }

        // Throw exception if not a legal value
        if (anOffspringMode != PedigreeView.OFFSPRING_MODE_FIXED &&
            anOffspringMode != PedigreeView.OFFSPRING_MODE_MIN_MAX &&
            anOffspringMode != PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Make change
        int oldOffspringMode = offspringMode;
        offspringMode = anOffspringMode;

        // Update pulldown
        if (offspringModePulldown != null)
        {
            if (offspringMode == PedigreeView.OFFSPRING_MODE_FIXED)
            {
                offspringModePulldown.setSelectedItem(PedigreeView.OFFSPRING_MODE_FIXED_STRING);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MIN_MAX)
            {
                offspringModePulldown.setSelectedItem(PedigreeView.OFFSPRING_MODE_MIN_MAX_STRING);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
            {
                offspringModePulldown.setSelectedItem(PedigreeView.OFFSPRING_MODE_MALE_FEMALE_STRING);
            }
        }

        // Notify listeners
        changes.firePropertyChange(UIProp.OFFSPRING_MODE,
                                   new Integer(oldOffspringMode),
                                   new Integer(offspringMode));
    }

    /**
     * Get the current selection set
     *
     * @return		SelectionSet - the current selection set
    **/
    public SelectionSet getSelectionSet()
    {
        return selectionSet;
    }

    /**
     * Set the current selection set.
     *
     * @param		aSelectionSet SelectionSet - a new selection set
    **/
    public void setSelectionSet(SelectionSet aSelectionSet)
    {
        // If selection set hasn't changed, return immediately
        if (aSelectionSet == selectionSet)
        {
            return;
        }

        // Validate input arguments
        if (aSelectionSet == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Make change
        selectionSet = aSelectionSet;

        // Update view
        repaint();

        // Don't notify listeners, as this isn't an event that anyone cares about
    }

    /**
     * Is the organism images visible?
     *
     * @return		boolean - organism images visible?
    **/
    public boolean isOrganismImagesVisible()
    {
        return organismImagesVisible;
    }

    /**
     * Set the organism images visible
     *
     * @param		visible boolean - visible?
    **/
    public void setOrganismImagesVisible(boolean visible)
    {
        // Return immediately if no change
        if (organismImagesVisible == visible)
        {
            return;
        }

        // Make change
        boolean oldOrganismImagesVisible = organismImagesVisible;
        organismImagesVisible = visible;

        // Update species trait list in pulldown
        Species tempCurrentSpecies = currentSpecies;
        Trait tempCurrentTrait = trait;
        setSpecies(null);
        if (tempCurrentSpecies != null)
        {
            setSpecies(tempCurrentSpecies);
            setTrait(tempCurrentTrait);
        }

        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM_IMAGES_VISIBLE,
                                   new Boolean(oldOrganismImagesVisible),
                                   new Boolean(organismImagesVisible));
    }

    /**
     * Is the Only Live Children flag true?
     *
     * @return		boolean - onlyLiveChildren
    **/
    public boolean isOnlyLiveChildren()
    {
        return onlyLiveChildren;
    }

    /**
     * Set the Only Live Children flag
     *
     * @param		anOnlyLiveChildren boolean - only live children?
    **/
    public void setOnlyLiveChildren(boolean anOnlyLiveChildren)
    {
        onlyLiveChildren = anOnlyLiveChildren;
    }

    /**
     * Get the active tool.
     *
     * @return		int - the active tool
    **/
    public int getActiveTool()
    {
        return activeTool;
    }
    
    public void setPedigreeView(PedigreeView pv)
    {
    	if (chromosomeBox == null){
    		Frame temp = JOptionPane.getFrameForComponent(pv);
    		chromosomeBox = new BioDialogBox(temp,"ChromosomeView");
    	}
    	thePedigreeView = pv;
    }
    
    /**
     * set the text on the chromosomeView
     * @ @param		str String -- then new String showing on chromosome view
     */
     public void setTextOnChromosomeView(String str)
     {
     	if (str == null) return;
     	chromosomeStr = str;
     }
     
     /**
     * set pop up chromosome view visible
     *
     * @param		bln boolean
     */
     public void setChromosomeBoxVisible(boolean bln)
     {
     	blnChromosomeViewVisible = bln;
     }
     
    /**
     * Is pop up chromosome view visible
     *
     * @return		boolean - Is pop up chromosome view visible?
    **/
     public boolean isChromosomeBoxVisible()
     {
		return blnChromosomeViewVisible;
     }

    /**
     * Set the active tool
     *
     * @param		anActiveTool int - the new active tool
    **/
    public void setActiveTool(int anActiveTool)
    {
        // Return immediately if not a change
        if (anActiveTool == activeTool)
        {
            return;
        }

        // Save old tool and make change
        int oldActiveTool = activeTool;
        activeTool = anActiveTool;

        // Hack - cursor is updated by parent pedigree view (yuch!!)

        // Notify listeners
        changes.firePropertyChange(UIProp.ACTIVE_TOOL,
                                   new Integer(oldActiveTool),
                                   new Integer(activeTool));
    }
    
    
    public void addChildToFamily(Organism one, Organism two, Organism child)
    {
    	Family parentsFamily = getFamilyForParents(one,two);
  
  		PedigreeFamily aPedigreeFamily = findPedigreeFamily(parentsFamily);
    
    			
    	if (parentsFamily == null ){
    	
    		parentsFamily = new Family(one,two,child);
    		addFamily(parentsFamily,0,0);
       		 repaint();
    	}
    	else if (parentsFamily.isChild(child) && aPedigreeFamily == null)
    	{
    		addFamily(parentsFamily,0,0);
    	}
    	else
    	{
    		Vector tempChildren = new Vector();
    		tempChildren.addElement(child);
    		addChildrenToFamily(parentsFamily,tempChildren);
    	}
    	
    	
    }

	
    /**
     * Add children to a family.
     *
     * @param		aFamily Family - Family to add children to.
     * @param		newChildren Vector - Children to add.
    **/
    private void addChildrenToFamily(Family aFamily, Vector newChildren)
    {
        if (aFamily == null || newChildren == null)
        {
       
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
		
        PedigreeFamily aPedigreeFamily = findPedigreeFamily(aFamily);
        if (aPedigreeFamily == null)
        {
        	
           throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        aPedigreeFamily.addChildren(newChildren, this);

        // Notify listeners
        changes.firePropertyChange(UIProp.CHILDREN_ADDED_TO_VIEW,null,aFamily);

        placeLevels();
        repaint();
    }
    
    /**
     * Add a family to this view.  If the parents or children are not already in the
     * view, this method will create and arbitrarily place them in the view.<p>
     *
     * If neither parent is in the view, the given x, y coordinates are used to place
     * the mother organism and then the father and children positions are calculated
     * relative to the mother's position.<p>
     *
     * @param		aFamily Family - a family to add, may not be null nor already in the view
     * @param		xLocation int - x location of first parent organism, ignored if a parent already in view
     * @param		yLocation int - y location of first parent organism, ignored if a parent already in view
     * @exception	IllegalArgumentException - input organism null
    **/
    public void addFamily(Family aFamily, int xLocation, int yLocation)
    {
        // Cannot add a null family
        if (aFamily == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Determine if this family is already in view, throwing an exception if it is
        PedigreeFamily aPedigreeFamily = findPedigreeFamily(aFamily);
        if (aPedigreeFamily != null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Make sure both parents are in the view and calculate child positions
        Organism motherOrganism = aFamily.getFemaleParent();
        Organism fatherOrganism = aFamily.getMaleParent();
        PedigreeOrganism motherPedigreeOrganism = findPedigreeOrganism(motherOrganism);
        PedigreeOrganism fatherPedigreeOrganism = findPedigreeOrganism(fatherOrganism);

        Dimension cellSize = getOrganismCellDimensions(motherOrganism,xLocation,yLocation,0,true);
        if (motherPedigreeOrganism == null && fatherPedigreeOrganism == null)
        {
        	
            addOrganism(motherOrganism,0,0);
            addOrganism(fatherOrganism,0,0);
            motherPedigreeOrganism = findPedigreeOrganism(motherOrganism);
            fatherPedigreeOrganism = findPedigreeOrganism(fatherOrganism);
        }
        else if (motherPedigreeOrganism != null && fatherPedigreeOrganism == null)
        {
            addOrganism(fatherOrganism,0,0);
            fatherPedigreeOrganism = findPedigreeOrganism(fatherOrganism);
        }
        else if (fatherPedigreeOrganism != null && motherPedigreeOrganism == null)
        {
            addOrganism(motherOrganism,0,0);
            motherPedigreeOrganism = findPedigreeOrganism(motherOrganism);
        }
        else
        {
            // have what we need
        }

        // Make sure all the children are in the view
        Organism childOrganism;
        PedigreeOrganism childPedigreeOrganism;
        Vector childPedigreeOrganisms = new Vector();
        Enumeration eChildOrganisms = aFamily.getChildren();
        while (eChildOrganisms.hasMoreElements())
        {
            childOrganism = (Organism) eChildOrganisms.nextElement();

            // Add this view as a listener on this organism
            childOrganism.addPropertyChangeListener(this);
    
            childPedigreeOrganism = new PedigreeOrganism(childOrganism,0,0,cellSize.width,cellSize.height);
            childPedigreeOrganisms.addElement(childPedigreeOrganism);
        }

        // Create a new pedigree family
        aPedigreeFamily = new PedigreeFamily(this,aFamily,motherPedigreeOrganism,fatherPedigreeOrganism,childPedigreeOrganisms);
  
        // Determine level at which to add pedigree family 
        int motherLevel = findLevelForOrganism(motherPedigreeOrganism);
        int fatherLevel = findLevelForOrganism(fatherPedigreeOrganism);
      
        if (motherLevel == -1 || fatherLevel == -1)
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
            
        int nextLevel = (motherLevel >= fatherLevel) ? motherLevel+1 : fatherLevel+1;
       
        // Create this level if we don't have it
        if (pedigreeLevels.size() <= nextLevel){
            pedigreeLevels.addElement(new PedigreeLevel());
           
          }
            
        // Add pedigree family to level
        PedigreeLevel level = (PedigreeLevel) pedigreeLevels.elementAt(nextLevel);
       
        level.addFamily(aPedigreeFamily);
                
        // Add this view as a listener on this family
        aFamily.addPropertyChangeListener(this);

        // Force a repaint
        placeLevels();
        repaint();
    
        // Notify listeners
        changes.firePropertyChange(UIProp.FAMILY_ADDED_TO_VIEW,null,aFamily);
     
        
    
    }

    /**
     * Removes a family from this view. This method returns false if aFamily is null.<p>
     *
     * @param		aFamily Family - a family, may be null
     * @return		boolean indicating whether or not the family was found and removed
    **/
    public boolean removeFamily(Family aFamily)
    {
        // Return immediately if aFamily is null
        if (aFamily == null)
        {
            return false;
        }

        boolean result = false;

        // Determine if this family is already in view, removing it if it is
        int levelArr[] = new int[1];
        PedigreeFamily aPedigreeFamily = findPedigreeFamily(aFamily, levelArr);
        if (aPedigreeFamily != null)
        {
            // Remove this view as a listener on this family
            aFamily.removePropertyChangeListener(this);
    
            // Remove family from level
            int level = levelArr[0];
            PedigreeLevel pedigreeLevel =
                (PedigreeLevel) pedigreeLevels.elementAt(level);
            result = pedigreeLevel.removeFamily(aPedigreeFamily);
            
            aPedigreeFamily = null;

        }
        // If result is true, force a repaint and notify listeners
        if (result == true)
        {
            placeLevels();
            repaint();
            changes.firePropertyChange(UIProp.FAMILY_REMOVED_FROM_VIEW,null,aFamily);
        }

        return result;
    }

    /**
     * Get the number of pedigreeFamilies in this view.
     *
     * @return		int - number of pedigreeFamilies in this view (0 or greater)
    **/
    public int getNumberOfFamilies()
    {
        int count = 0;
        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            count += pedigreeLevel.getNumberOfFamilies();
        }
        return count;
    }

    /**
     * Add an organism to this view without any required ties to a family.  The trait name
     * specified will be used to set the trait ONLY IF THIS IS THE FIRST ORGANISM ADDED TO
     * THE VIEW.  Otherwise, aTraitName is ignored.<p>
     * Note: X and Y locations are now ignored. Placement is done via new PedigreeLevel class.
     * This interface should be changed to take a level number instead of X and Y coordinates.
     *
     * @param		anOrganism Organism - an organism to add, may not be null
     * @param		xLocation int - x location of organism
     * @param		yLocation int - y location of organism
     * @param		aTraitName String - a trait name, if null then full organism images will be shown
     * @exception	IllegalArgumentException - input organism null
    **/
    public void addOrganism(Organism anOrganism, int xLocation, int yLocation, String aTraitName)
    {
        // Cannot add a null organism
        if (anOrganism == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Determine if this organism is already in view, throwing an exception if it is
        PedigreeOrganism aPedigreeOrganism = findPedigreeOrganism(anOrganism);
        if (aPedigreeOrganism != null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Determine if the organism is the same species as the current species.
        // Throw an exception if it is not.  Set species if current species null.
        if (currentSpecies == null)
        {
            // Set species and trait
            setSpecies(anOrganism.getSpecies());
            setTrait(aTraitName);
        }
        else
        {
            if (currentSpecies != anOrganism.getSpecies())
            {
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
            }
        }

        // Add this view as a listener on this organism
        anOrganism.addPropertyChangeListener(this);

        // Ok - create a new pedigree organism
        Dimension cellSize = getOrganismCellDimensions(anOrganism, xLocation, yLocation, 0, true);
        aPedigreeOrganism = new PedigreeOrganism(anOrganism,0,0,cellSize.width,cellSize.height);
        
        // Make sure we have first level
        if (pedigreeLevels.size() == 0)
            pedigreeLevels.addElement(new PedigreeLevel());
            
        // Add organism to first level
        PedigreeLevel level = (PedigreeLevel) pedigreeLevels.firstElement();
        level.addOrganism(aPedigreeOrganism);

        // Do the layout
        placeLevels();
        repaint();
        
        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM_ADDED_TO_VIEW,null,anOrganism);
    }

    /**
     * Add an organism to this view without any required ties to a family.<p>
     * Note: X and Y locations are now ignored. Placement is done via new PedigreeLevel class.
     * This interface should be changed to take a level number instead of X and Y coordinates.
     *
     * @param		anOrganism Organism - an organism to add, may not be null
     * @param		xLocation int - x location of organism
     * @param		yLocation int - y location of organism
     * @exception	IllegalArgumentException - input organism null
    **/
    public void addOrganism(Organism anOrganism, int xLocation, int yLocation)
    {
        // Cannot add a null organism
        if (anOrganism == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Determine if this organism is already in view, returning immediately if it is
        PedigreeOrganism aPedigreeOrganism = findPedigreeOrganism(anOrganism);
        if (aPedigreeOrganism != null)
        {
            return;
        }

        // Determine if the organism is the same species as the current species.
        // Throw an exception if it is not.  Set species if current species null.
        if (currentSpecies == null)
        {
            // Set species
            setSpecies(anOrganism.getSpecies());
        }
        else
        {
            if (currentSpecies != anOrganism.getSpecies())
            {
                throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
            }
        }

        // Add this view as a listener on this organism
        anOrganism.addPropertyChangeListener(this);

        // Ok - create a new pedigree organism
        Dimension cellSize = getOrganismCellDimensions(anOrganism, xLocation, yLocation, 0, true);
        aPedigreeOrganism = new PedigreeOrganism(anOrganism,0,0,cellSize.width,cellSize.height);
    
        // Make sure we have first level
        if (pedigreeLevels.size() == 0)
            pedigreeLevels.addElement(new PedigreeLevel());
            
        // Add organism to first level
        PedigreeLevel level = (PedigreeLevel) pedigreeLevels.firstElement();
        level.addOrganism(aPedigreeOrganism);

        // Do the layout
        placeLevels();
        repaint();
        
        // Notify listeners
        changes.firePropertyChange(UIProp.ORGANISM_ADDED_TO_VIEW,null,anOrganism);
    }

    /**
     * Removes an organism from this view. This method returns false if anOrganism is null.<p>
     *
     * @param		anOrganism Organism - an organism, may be null
     * @return		boolean indicating whether or not the organism was found and removed
    **/
    public boolean removeOrganism(Organism anOrganism)
    {
        boolean result = false;
        boolean standalone = false;

        if (anOrganism == null)
            return false;

        // Determine if this organism is already in view, removing it if it is
        PedigreeOrganism aPedigreeOrganism = findPedigreeOrganism(anOrganism);
        if (aPedigreeOrganism != null)
        {
            // Remove this view as a listener on this organism
            anOrganism.removePropertyChangeListener(this);
    
            // Remove from enclosing PedigreeFamily, if it has one
            Family family = anOrganism.getParentFamily();
            if (family != null)
            {
                PedigreeFamily aPedigreeFamily = findPedigreeFamily(family);
                if (aPedigreeFamily != null)
                {
                    result = aPedigreeFamily.removeChildPedigreeOrganism(aPedigreeOrganism);
                    if (result && aPedigreeFamily.isEmpty())
                    {
                        removeFamily(family);
                        return result;				// removing the pedigreeFamily will repaint everything
                    }
                    aPedigreeFamily.placeConnections();
                }
            }
            else	// Stand-alone organism
            {
                int level = findLevelForOrganism(aPedigreeOrganism);
                if (level != -1)
                {
                    PedigreeLevel pedigreeLevel = (PedigreeLevel)pedigreeLevels.elementAt(level);
                    result = pedigreeLevel.removeOrganism(aPedigreeOrganism);
                    standalone = true;
                }
            }
            aPedigreeOrganism = null;
        }

        // Unset the current species if there are no organisms in this view anymore
        PedigreeLevel firstLevel = (PedigreeLevel) pedigreeLevels.firstElement();
        if (!firstLevel.getPedigreeObjects().hasMoreElements())
        {
            setSpecies(null);
        }

        // Don't redo the layout unless we removed a stand-alone organism
        if (standalone)
            placeLevels();

        repaint();

        // Notify listeners if the organism was truly removed
        if (result == true)
        {
            changes.firePropertyChange(UIProp.ORGANISM_REMOVED_FROM_VIEW,null,anOrganism);
        }

        return result;
    }

    /**
     * Get the number of pedigreeOrganisms in this view.
     *
     * @return		int - number of pedigreeOrganisms in this view (0 or greater)
    **/
    public int getNumberOfOrganisms()
    {
        int count = 0;
        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            count += pedigreeLevel.getNumberOfOrganisms();
        }
        return count;
    }

    /**
     * Get the set of pedigreeOrganisms in this view.
     *
     * @return		Enumeration - enumeration over the set of pedigreeOrganisms in this view
    **/
    public Enumeration getOrganisms()
    {
        Vector all = new Vector();
        
        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            PedigreeObject pedigreeObject;
            Enumeration ePedigreeObjects = pedigreeLevel.getPedigreeObjects();
            while (ePedigreeObjects.hasMoreElements())
            {
                pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
                if (pedigreeObject instanceof PedigreeFamily)
                {
                    PedigreeOrganism pedigreeChild = null;
                    Enumeration ePedigreeChildren = ((PedigreeFamily) pedigreeObject).getChildPedigreeOrganisms();
                    while (ePedigreeChildren.hasMoreElements())
                    {
                        pedigreeChild = (PedigreeOrganism) ePedigreeChildren.nextElement();
                        all.addElement(pedigreeChild);
                    }
                }
                else if (pedigreeObject instanceof PedigreeOrganism)
                {
                    all.addElement(pedigreeObject);
                }
            }
        }
        return all.elements();
    }

    public Enumeration getFamilies()
    {
        Vector all = new Vector();
        
        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            PedigreeObject pedigreeObject;
            Enumeration ePedigreeObjects = pedigreeLevel.getPedigreeObjects();
            while (ePedigreeObjects.hasMoreElements())
            {
                pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
                if (pedigreeObject instanceof PedigreeFamily)
                {
                    all.addElement(pedigreeObject);
                }
            }
        }
        
        return all.elements();
    }

    /**
     * Remove all the objects in this view, both organisms and families, without
     * deleting anything.<p> Remove all the listeners and then the objects.
    **/
    public void removeAll()
    {
        Family aFamily = null;
        Organism anOrganism = null;
        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            PedigreeObject pedigreeObject = null;
            Enumeration ePedigreeObjects = pedigreeLevel.getPedigreeObjects();
            while (ePedigreeObjects.hasMoreElements())
            {
                pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
                if (pedigreeObject instanceof PedigreeFamily)
                {
                    aFamily = ((PedigreeFamily)pedigreeObject).getFamily();
                    aFamily.removePropertyChangeListener(this);
                    ((PedigreeFamily)pedigreeObject).removeChildren(this);
                }
                else
                {
                    anOrganism = ((PedigreeOrganism)pedigreeObject).getOrganism();
                    anOrganism.removePropertyChangeListener(this);
                }
            }
            // Remove all the objects
            pedigreeLevel.removeAll();
        }
        // redo layout
        placeLevels();
        repaint();
    }

    /**
     * Get the current species.<p>
     *
     * @return		Species - the current species, may be null
    **/
    public Species getSpecies()
    {
        return currentSpecies;
    }

    /**
     * Set the current species.<p>
     *
     * @param		aSpecies Species - new current species, may be null
    **/
    public void setSpecies(Species aSpecies)
    {
        Species oldSpecies = currentSpecies;

        // Unsubscribe from old species if different from new species
        if (currentSpecies != null && currentSpecies != aSpecies)
        {
            currentSpecies.removePropertyChangeListener(this);
        }

        // Unset the current trait
        setTrait((Trait)null);

        // Make change
        currentSpecies = aSpecies;

        // Subscribe to new species
        if (currentSpecies != null && currentSpecies != oldSpecies)
        {
            currentSpecies.addPropertyChangeListener(this);
        }

        String selectedItemName = null;

        // Set the items in the trait pulldown
        if (traitPulldown != null)
        {
            // Clear pulldown of all items
            traitPulldown.removeAllItems();

            // Add new items
            if (currentSpecies != null)
            {
                // Add an "Organism" item if that option is turned on
                if (organismImagesVisible)
                {
                    traitPulldown.addItem("Organism");
                    selectedItemName = "Organism";
                }

                // Add traits
                Trait aTrait;
                String traitName;
                Enumeration eTraits = currentSpecies.getTraits();
                while (eTraits.hasMoreElements())
                {
                    aTrait = (Trait) eTraits.nextElement();
                    traitName = aTrait.getName();

                    // Don't put the Liveliness trait in the pulldown - Paul H. wants to hide it (10/7/99)
                    if (!traitName.equals("Liveliness"))
                    {
                        if (selectedItemName == null)
                        {
                            selectedItemName = traitName;
                        }
                        
                        if (!traitName.equals("Scales") && !traitName.equals("Plates"))
                        {
                            traitPulldown.addItem(aTrait.getName());
                        }
                        else 
                        {
                        	Gene currentGene = currentSpecies.getGene(traitName);
                        	if (currentGene.isVisible())
                        	{
                        		traitPulldown.addItem(aTrait.getName());
                        	}
                        }
                    }
                }

                if (selectedItemName != null)
                {
                    traitPulldown.setSelectedItem(selectedItemName);
                }
            }
        }
    }
    /**
    *	Turn on one invisible gene on trait pulldown
    *   @param    aTraitName String--a Trait Name of currentSpecies;
    */
    public void setTraitVisible(String aTraitName,boolean bln)
    {
    	 String traitName ="";
    	 boolean alreadyHas = false;
    	 Trait aTrait;
    	 
    	 if(currentSpecies ==null) {
    	 	System.out.println("Don't know which species on this pedigree yet!");
    	 	return;
    	 }
    	
    	 Enumeration eTraits = currentSpecies.getTraits();
    	
         while (eTraits.hasMoreElements())
         {
             aTrait = (Trait) eTraits.nextElement();
             traitName = aTrait.getName();
             
             if (traitName.equals(aTraitName))
             {
             	int counter = 0;
             	//don't add the same trait again
             	while (counter<traitPulldown.getItemCount())
             	{
             		if ((traitPulldown.getItemAt(counter)).toString().equals(traitName))
             		{
             			alreadyHas = true;
             			break;
             		}
             		counter +=1;
             	}
             	if (!alreadyHas && bln)
             		traitPulldown.addItem(aTrait.getName());
             	else if (alreadyHas && !bln)
             		traitPulldown.removeItem(new String(aTraitName));
             		
             }
          }
         
    }
    /**
     * Set the trait pulldown.  This method exists so the containing pedigree view
     * can tell this view the pulldown and this view can keep the items in thee
     * pulldown current as the species of this view changes.
     *
     * @param		aTraitPulldown BioComboBox - a trait pulldown, may be null
    **/
    public void setTraitPulldown(BioComboBox aTraitPulldown)
    {
        traitPulldown = aTraitPulldown;

        // Call code which will update items in pulldown
        setSpecies(currentSpecies);
    }
    
    public void setCrossOverCheckBox(JCheckBox box)
    {
    	checkBox = box;
    }

    /**
     * Set the offspring mode pulldown.  This method exists so the containing pedigree view
     * can tell this view the pulldown and this view can keep the selected item in the
     * pulldown current as the offspring mode of this view changes.<p>
     *
     * @param		anOffspringPulldown BioComboBox - an offspring pulldown, may be null
    **/
    public void setOffspringModePulldown(BioComboBox anOffspringModePulldown)
    {
        offspringModePulldown = anOffspringModePulldown;

        // Update selected item
        if (offspringModePulldown != null)
        {
            if (offspringMode == PedigreeView.OFFSPRING_MODE_FIXED)
            {
                offspringModePulldown.setSelectedItem(PedigreeView.OFFSPRING_MODE_FIXED_STRING);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MIN_MAX)
            {
                offspringModePulldown.setSelectedItem(PedigreeView.OFFSPRING_MODE_MIN_MAX_STRING);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
            {
                offspringModePulldown.setSelectedItem(PedigreeView.OFFSPRING_MODE_MALE_FEMALE_STRING);
            }
        }
    }

    /**
     * Get the trait for this view.<p>
     *
     * @return		Trait - the trait to draw, null if whole organism
    **/
    public Trait getTrait()
    {
        return trait;
    }

    /**
     * Set the trait for this view.  If aTrait is null, the whole organism's phenotype will be drawn.<p>
     *
     * This method is NOT public because we don't update the pulldown in this method.  So callers must
     * call the String version below.<p>
     *
     * @param		aTrait Trait - the trait to draw, may be null to cause whole organisms to be drawn
    **/
    private void setTrait(Trait aTrait)
    {
        // Return immediately if no change
        if (aTrait == trait)
        {
            return;
        }

        // Okay to make change
        Trait oldTrait = trait;
        trait = aTrait;
        
        // Update pulldown
        if (aTrait != null)
        {
            traitPulldown.setSelectedItem(aTrait.getName());
        }
        else if (organismImagesVisible)
        {
            traitPulldown.setSelectedItem("Organism");
        }
        else
        {
            // Set it to null, which should cause first item to be selected
            traitPulldown.setSelectedItem(null);
        }

        // Force repaint
        repaint();

		//log trait changes
		logChangeOfCharateristic(oldTrait,trait);

        // Notify listeners
        changes.firePropertyChange(UIProp.TRAIT, oldTrait, trait);
    }

    /**
     * Set the trait for this view.  If aTrait is null, the whole organism's phenotype will be drawn.<p>
     *
     * @param		aTraitName String - the name of the trait to draw, may be null to cause whole organisms to be drawn
    **/
    public void setTrait(String aTraitName)
    {
        // Return immediately if no change
        if (trait != null &&
            aTraitName != null &&
            trait.getName().equals(aTraitName))
        {
            return;
        }

        // Return immediately if no current species
        if (currentSpecies == null)
        {
            return;
        }

        // Find trait and set it.  If aTraitName null or "Organism", set trait to null
        if (aTraitName == null || aTraitName.equals("Organism"))
        {
            setTrait((Trait)null);
        }
        else
        {
            // Find trait and set it.  Do nothing if cannot find trait.
            Trait aTrait = null;
            Enumeration eTraits = currentSpecies.getTraits();
            while (eTraits.hasMoreElements())
            {
                aTrait = (Trait) eTraits.nextElement();
                if (aTrait.getName().equals(aTraitName))
                {
                    setTrait(aTrait);
                    return;
                }
            }
        }
    }
    
    
    /**
     * Get the fixed number of children from a breeding.<p>
     *
     * @return		int - fixed number of children from a breeding
    **/
    public int getFixedNumberChildren()
    {
        return fixedNumberChildren;
    }

    /**
     * Set the fixed number of children from a breeding.<p>
     *
     * @param		aFixedNumberChildren int - fixed number of children from a breeding
     * @exception	IllegalArgumentException - new fixed number illegal
    **/
    public void setFixedNumberChildren(int aFixedNumberChildren)
    {
        // Return immediately if no change
        if (fixedNumberChildren == aFixedNumberChildren)
        {
            return;
        }

        // Throw exception if illegal value
        if (aFixedNumberChildren < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change
        int oldFixedNumberChildren = fixedNumberChildren;
        fixedNumberChildren = aFixedNumberChildren;

        // Notify listeners
        changes.firePropertyChange(UIProp.FIXED_NUMBER_CHILDREN,
                                   new Integer(oldFixedNumberChildren),
                                   new Integer(fixedNumberChildren));
    }

    /**
     * Get the maximum number of children from a breeding.<p>
     *
     * @return		int - maximum number of children from a breeding
    **/
    public int getMaximumNumberChildren()
    {
        return maximumNumberChildren;
    }

    /**
     * Set the maximum number of children from a breeding.  This must
     * be higher than the minimum or else an exception is thrown.<p>
     *
     * @param		aMaximumNumberChildren int - maximum number of children from a breeding
     * @exception	IllegalArgumentException - new maximum illegal, may be less than minimum
    **/
    public void setMaximumNumberChildren(int aMaximumNumberChildren)
    {
        // Return immediately if no change
        if (maximumNumberChildren == aMaximumNumberChildren)
        {
            return;
        }

        // Throw exception if illegal value
        if (aMaximumNumberChildren < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Drop minimum down if maximum would be less than minimum
        if (aMaximumNumberChildren < minimumNumberChildren)
        {
            // Change minimum and maximum
            minimumNumberChildren = aMaximumNumberChildren;
            maximumNumberChildren = aMaximumNumberChildren;

            // Notify listeners
            changes.firePropertyChange(UIProp.MINMAX_NUMBER_CHILDREN,null,null);
        }
        else
        {
            // Just change maximum
            int oldMaximumNumberChildren = maximumNumberChildren;
            maximumNumberChildren = aMaximumNumberChildren;

            // Notify listeners
            changes.firePropertyChange(UIProp.MAXIMUM_NUMBER_CHILDREN,
                                       new Integer(oldMaximumNumberChildren),
                                       new Integer(maximumNumberChildren));
        }
    }

    /**
     * Get the minimum number of children from a breeding.<p>
     *
     * @return		int - minimum number of children from a breeding
    **/
    public int getMinimumNumberChildren()
    {
        return minimumNumberChildren;
    }

    /**
     * Set the minimum number of children from a breeding.  This must
     * be less than the maximum or else an exception is thrown.<p>
     *
     * @param		aMinimumNumberChildren int - minimum number of children from a breeding
     * @exception	IllegalArgumentException - new minimum illegal, may be more than maximum
    **/
    public void setMinimumNumberChildren(int aMinimumNumberChildren)
    {
        // Return immediately if no change
        if (minimumNumberChildren == aMinimumNumberChildren)
        {
            return;
        }

        // Throw exception if illegal value
        if (aMinimumNumberChildren < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Push maximum up if minimum would be greater than maximum now
        if (aMinimumNumberChildren > maximumNumberChildren)
        {
            // Change minimum and maximum
            minimumNumberChildren = aMinimumNumberChildren;
            maximumNumberChildren = aMinimumNumberChildren;

            // Notify listeners
            changes.firePropertyChange(UIProp.MINMAX_NUMBER_CHILDREN,null,null);
        }
        else
        {
            // Just change minimum
            int oldMinimumNumberChildren = minimumNumberChildren;
            minimumNumberChildren = aMinimumNumberChildren;

            // Notify listeners
            changes.firePropertyChange(UIProp.MINIMUM_NUMBER_CHILDREN,
                                       new Integer(oldMinimumNumberChildren),
                                       new Integer(minimumNumberChildren));
        }
    }

    /**
     * Set the minimum and maximum number of children from a breeding.  The minimum
     * must be less than or equal to the maximum or else an exception is thrown.<p>
     *
     * @param		aMinimumNumberChildren int - minimum number of children from a breeding
     * @param		aMaximumNumberChildren int - maximum number of children from a breeding
     * @exception	IllegalArgumentException - new minimum illegal, may be more than maximum
    **/
    public void setMinMaxNumberChildren(int aMinimumNumberChildren, int aMaximumNumberChildren)
    {
        // Return immediately if no change
        if (minimumNumberChildren == aMinimumNumberChildren &&
            maximumNumberChildren == aMaximumNumberChildren)
        {
            return;
        }

        // Throw exception if illegal values
        if (aMinimumNumberChildren < 0 ||
            aMaximumNumberChildren < 0 ||
            aMinimumNumberChildren > aMaximumNumberChildren)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change
        minimumNumberChildren = aMinimumNumberChildren;
        maximumNumberChildren = aMaximumNumberChildren;

        // Notify listeners
        changes.firePropertyChange(UIProp.MINMAX_NUMBER_CHILDREN,null,null);
    }

    /**
     * Get the number of female children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @return		int - number of female children
    **/
    public int getNumberFemaleChildren()
    {
        return numberFemaleChildren;
    }

    /**
     * Set the number of female children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @param		aNumberFemaleChildren int - new number of female children, must be >= 0
     * @exception	IllegalArgumentException - new value illegal
    **/
    public void setNumberFemaleChildren(int aNumberFemaleChildren)
    {
        // Return immediately if no change
        if (numberFemaleChildren == aNumberFemaleChildren)
        {
            return;
        }

        // Throw exception if illegal value
        if (aNumberFemaleChildren < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change
        int oldNumberFemaleChildren = numberFemaleChildren;
        numberFemaleChildren = aNumberFemaleChildren;

        // Notify listeners
        changes.firePropertyChange(UIProp.NUMBER_FEMALE_CHILDREN,
                                   new Integer(oldNumberFemaleChildren),
                                   new Integer(numberFemaleChildren));
    }

    /**
     * Get the number of male children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @return		int - number of male children
    **/
    public int getNumberMaleChildren()
    {
        return numberMaleChildren;
    }

    /**
     * Set the number of male children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @param		aNumberMaleChildren int - new number of male children, must be >= 0
     * @exception	IllegalArgumentException - new value illegal
    **/
    public void setNumberMaleChildren(int aNumberMaleChildren)
    {
        // Return immediately if no change
        if (numberMaleChildren == aNumberMaleChildren)
        {
            return;
        }

        // Throw exception if illegal value
        if (aNumberMaleChildren < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change
        int oldNumberMaleChildren = numberMaleChildren;
        numberMaleChildren = aNumberMaleChildren;

        // Notify listeners
        changes.firePropertyChange(UIProp.NUMBER_MALE_CHILDREN,
                                   new Integer(oldNumberMaleChildren),
                                   new Integer(numberMaleChildren));
    }

    /**
     * Set the number of female and male children to create in a family when the
     * view is in PedigreeView.OFFSPRING_MODE_MALE_FEMALE mode.<p>
     *
     * @param		aNumberFemaleChildren int - new number of female children, must be >= 0
     * @param		aNumberMaleChildren int - new number of male children, must be >= 0
     * @exception	IllegalArgumentException - new value illegal
    **/
    public void setNumberMaleFemaleChildren(int aNumberFemaleChildren,
                                            int aNumberMaleChildren)
    {
        // Return immediately if no change
        if (numberFemaleChildren == aNumberFemaleChildren &&
            numberMaleChildren == aNumberMaleChildren)
        {
            return;
        }

        // Throw exception if illegal value
        if (aNumberFemaleChildren < 0 ||
            aNumberMaleChildren < 0)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok - make change for females
        if (numberFemaleChildren != aNumberFemaleChildren)
        {
            int oldNumberFemaleChildren = numberFemaleChildren;
            numberFemaleChildren = aNumberFemaleChildren;
    
            // Notify listeners
            changes.firePropertyChange(UIProp.NUMBER_FEMALE_CHILDREN,
                                       new Integer(oldNumberFemaleChildren),
                                       new Integer(numberFemaleChildren));
            
        }

        // Make change for males
        if (numberMaleChildren != aNumberMaleChildren)
        {
            int oldNumberMaleChildren = numberMaleChildren;
            numberMaleChildren = aNumberMaleChildren;
    
            // Notify listeners
            changes.firePropertyChange(UIProp.NUMBER_MALE_CHILDREN,
                                       new Integer(oldNumberMaleChildren),
                                       new Integer(numberMaleChildren));
        }
    }

    /**
     * A helper function which produces the number of children to create
     * for a family using a random number.  The number will be inclusively
     * between the minimum and maximum number of children.
     *
     * @return		int - number of children
    **/
    private int getRandomNumberChildren()
    {
        // Return value quickly if minimum and maximum are equal
        if (minimumNumberChildren == maximumNumberChildren)
        {
            return minimumNumberChildren;
        }

        // Range exists, so use random number to determine value
        int range = maximumNumberChildren - minimumNumberChildren + 1;
        int nextInt = random.nextInt();

        if (nextInt < 0)
        {
            nextInt = -nextInt;
        }

        int numberChildren = minimumNumberChildren + (nextInt % range);

        return numberChildren;
    }

    /**
     * Returns whether or not the given organism is in this view.
     *
     * @param       anOrganism Organism - the organism to find
     * @return      boolean true (in view) or false (not in view)
    **/
    public boolean containsOrganism(Organism anOrganism)
    {
        if (anOrganism == null)
            return false;

        PedigreeLevel level = null;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            level = (PedigreeLevel) ePedigreeLevels.nextElement();
            if (level.containsOrganism(anOrganism))
                return true;
        }
        return false;
    }

    /**
     * Find the pedigreeOrganism corresponding to a given organism.
     *
     * @param		anOrganism Organism - the organism to find
     * @return		PedigreeOrganism - the pedigree organism for the given organism
    **/
    public PedigreeOrganism findPedigreeOrganism(Organism anOrganism)
    {
        PedigreeOrganism aPedigreeOrganism = null;
        PedigreeLevel aPedigreeLevel = null;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            aPedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            aPedigreeOrganism = aPedigreeLevel.findPedigreeOrganism(anOrganism);
            if (aPedigreeOrganism != null)
            {
                break;
            }
        }
        return aPedigreeOrganism;
    }

    /**
     * Find the pedigreeFamily corresponding to a given family
     *
     * @param		aFamily Family - the family to find
     * @return		PedigreeFamily - the pedigree family for the given family
    **/
    public PedigreeFamily findPedigreeFamily(Family aFamily)
    {
        PedigreeFamily aPedigreeFamily = null;
        PedigreeLevel level = null;
        
        for (int i=0; i<pedigreeLevels.size(); i++)
        {
        	level = (PedigreeLevel) pedigreeLevels.elementAt(i);
            aPedigreeFamily = level.findPedigreeFamily(aFamily);
            if (aPedigreeFamily != null)
            {
                break;
            }
        }
      
        
        return aPedigreeFamily;
    }
    
    /**
     * Find the PedigreeFamily for the given Family
     *
     * @param		aFamily Family - the Family to find
     * @param		returnLevel int[] - the resulting level (in the first array element)
     * @return		PedgireeLevel - the resulting PedigreeFamily or null.
    **/
    public PedigreeFamily findPedigreeFamily(Family aFamily, int returnLevel[])
    {
        PedigreeFamily aPedigreeFamily = null;
        returnLevel[0] = -1;
        PedigreeLevel level = null;
        for (int i=0; i<pedigreeLevels.size(); i++)
        {
            level = (PedigreeLevel) pedigreeLevels.elementAt(i);
            aPedigreeFamily = level.findPedigreeFamily(aFamily);
            if (aPedigreeFamily != null)
            {
                returnLevel[0] = i;
                break;
            }
        }
        return aPedigreeFamily;
    }

    /**
     * Find the level containing the pedigreeOrganism
     *
     * @param		aPedigreeOrganism PedigreeOrganism - the pedigree organism to find
     * @return		int - the level containing it or -1 if not found.
    **/
    public int findLevelForOrganism(PedigreeOrganism aPedigreeOrganism)
    {
        PedigreeLevel level = null;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        for (int i=0; ePedigreeLevels.hasMoreElements(); i++)
        {
            level = (PedigreeLevel) ePedigreeLevels.nextElement();
            if (level.contains(aPedigreeOrganism))
                return i;
        }
        return -1;
    }
        
    /**
     * Find the level containing the pedigreeFamily
     *
     * @param		aPedigreeFamily PedigreeFamily - the pedigree organism to find
     * @return		int - the level containing it or -1 if not found.
    **/
    public int findLevelForFamily(PedigreeFamily aPedigreeFamily)
    {
        PedigreeLevel level = null;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        for (int i=0; ePedigreeLevels.hasMoreElements(); i++)
        {
            level = (PedigreeLevel) ePedigreeLevels.nextElement();
            if (level.contains(aPedigreeFamily))
                return i;
        }
        return -1;
    }
        
    /**
     * Get the Family for the given parent Organisms
     *
     * @param		parentOne Organism - the first parent Organism
     * @param		parentTwo Organism - the second parent Organism
     * @return		Family - the resulting Family or null.
    **/
    public Family getFamilyForParents(Organism parentOne, Organism parentTwo)
    {
        Family parentOneFamily = null;
        Enumeration eParentOneFamilies = parentOne.getChildFamilies();
        while (eParentOneFamilies.hasMoreElements())
        {
            parentOneFamily = (Family)eParentOneFamilies.nextElement();
            Family parentTwoFamily = null;
            Enumeration eParentTwoFamilies = parentTwo.getChildFamilies();
            while (eParentTwoFamilies.hasMoreElements())
            {
                parentTwoFamily = (Family)eParentTwoFamilies.nextElement();
                if (parentOneFamily == parentTwoFamily)
                {
                    return parentTwoFamily;
                }
            }
        }
        return null;
    }

    /**
     * Get the PedigreeFamily for the given pedigreeOrganism
     *
     * @param		pedigreeOrganism PedigreeOrganism - the pedigreeOrganism to find
     * @return		PedigreeFamily - the resulting pedigreeFamily or null.
    **/
    private PedigreeFamily getFamilyForOrganism(PedigreeOrganism pedigreeOrganism)
    {
        PedigreeLevel level = null;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            level = (PedigreeLevel) ePedigreeLevels.nextElement();
            PedigreeObject pedigreeObject = null;
            Enumeration ePedigreeObjects = level.getPedigreeObjects();
            while (ePedigreeObjects.hasMoreElements())
            {
                pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
                if (pedigreeObject instanceof PedigreeFamily)
                {
                    if (((PedigreeFamily)pedigreeObject).contains(pedigreeOrganism))
                    {
                        return (PedigreeFamily) pedigreeObject;
                    }
                }
            }
        }
        return null;
    }
        
    /**
     * Add a new family or add to an existing family. addFamily and addChildrenToFamily
     * redo the layout of the view. 
     *
     * @param		parentOne Organism - the first parent of the family
     * @param		parentTwo Organism - the second parent of the family
     * @return		Family - the new or existing family
    **/
    /*private Family addOrAddToFamily(Organism parentOne, Organism parentTwo)
    {
        Family retFamily = null;
        Family existingFamily = getFamilyForParents(parentOne, parentTwo);
        if (existingFamily == null)
        {
            // Create and add family, respecting the family mode in effect
            Family family;
            if (offspringMode == PedigreeView.OFFSPRING_MODE_FIXED)
            {
                family = new Family(parentOne,parentTwo,fixedNumberChildren,onlyLiveChildren);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MIN_MAX)
            {
                family = new Family(parentOne,parentTwo,getRandomNumberChildren(),onlyLiveChildren);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
            {
                family = new Family(parentOne,parentTwo,numberFemaleChildren,numberMaleChildren,onlyLiveChildren);
            }
            else
            {
                throw new InternalEngineException("No family created.  Illegal family mode: " + offspringMode);
            }
            addFamily(family,0,0);
            retFamily = family;
        }
        else
        {
            // Add children to existing family
            Vector newChildren;
            if (offspringMode == PedigreeView.OFFSPRING_MODE_FIXED)
            {
                newChildren = existingFamily.addChildren(fixedNumberChildren,onlyLiveChildren);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MIN_MAX)
            {
                newChildren = existingFamily.addChildren(getRandomNumberChildren(),onlyLiveChildren);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
            {
                newChildren = existingFamily.addChildren(numberFemaleChildren,numberMaleChildren,onlyLiveChildren);
            }
            else
            {
                throw new InternalEngineException("No family created.  Illegal family mode: " + offspringMode);
            }
            addChildrenToFamily(existingFamily, newChildren);
            retFamily = existingFamily;
        }			

        return retFamily;
    }*/
    
    
    /**
     * Set cross over turn on?
     *
     * @return on boolean --Turn on?
     */
    public void setCrossOverTurnOn(boolean on)
    {
    	blnCrossOverTurnOn = on;
    }
    
    /**
     * Is cross over turn on?
     * @return boolean --Turn on?
     */
    public boolean getCrossOverTurnOn()
    {
    	return blnCrossOverTurnOn;
    }
    
   /**
     * Add a new family or add to an existing family. addFamily and addChildrenToFamily
     * redo the layout of the view with crossing over. 
     *
     * @param		parentOne Organism - the first parent of the family
     * @param		parentTwo Organism - the second parent of the family
     * @return		Family - the new or existing family
    **/
    private Family addOrAddToFamily(Organism one, Organism two)
    {
    
        Family retFamily = null;
        boolean blnCrossOver = getCrossOverTurnOn();
        double p = getCrossOverPossibility();
    
        Organism parentOne = one;
        Organism parentTwo = two;
       
        Family existingFamily = getFamilyForParents(parentOne, parentTwo);
     
        PedigreeFamily aPedigreeFamily=null;
        
        if (existingFamily !=null)
        {
        	aPedigreeFamily = findPedigreeFamily(existingFamily);
        	
        	if (aPedigreeFamily == null)
        		addFamily(existingFamily,0,0);
        }
       
        if (existingFamily == null)
        {
            // Create and add family, respecting the family mode in effect
       
            Family family=null;
            //If crossOver is turn on
            if (blnCrossOver)
            {
        		CrossOver co = new CrossOver();
        		co.setPossibility(p);
                if (offspringMode == PedigreeView.OFFSPRING_MODE_FIXED)
            	{
               	 family = co.getCrossOverFamily(parentOne,parentTwo,fixedNumberChildren,onlyLiveChildren);
            
            	}
            	else if (offspringMode == PedigreeView.OFFSPRING_MODE_MIN_MAX)
            	{
           	     family = co.getCrossOverFamily(parentOne,parentTwo,getRandomNumberChildren(),onlyLiveChildren);
           		}
            	else if (offspringMode == PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
            	{
              	  family = co.getCrossOverFamily(parentOne,parentTwo,numberFemaleChildren,numberMaleChildren,onlyLiveChildren);
           	 	}
           		else
           		{
                	throw new InternalEngineException("No family created.  Illegal family mode: " + offspringMode);
           		}
            	
            }
            else
            {
	            if (offspringMode == PedigreeView.OFFSPRING_MODE_FIXED)
	            {
	                family = new Family(parentOne,parentTwo,fixedNumberChildren,onlyLiveChildren);
	            }
	            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MIN_MAX)
	            {
	                family = new Family(parentOne,parentTwo,getRandomNumberChildren(),onlyLiveChildren);
	            }
	            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
	            {
	                family = new Family(parentOne,parentTwo,numberFemaleChildren,numberMaleChildren,onlyLiveChildren);
	            }
	            else
	            {
	                throw new InternalEngineException("No family created.  Illegal family mode: " + offspringMode);
	            }
	        }
            addFamily(family,0,0);
            retFamily = family;
        }
        else
        {
            // Add children to existing family
            Vector newChildren;
            if (offspringMode == PedigreeView.OFFSPRING_MODE_FIXED)
            {
                newChildren = existingFamily.addChildren(fixedNumberChildren,onlyLiveChildren);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MIN_MAX)
            {
                newChildren = existingFamily.addChildren(getRandomNumberChildren(),onlyLiveChildren);
            }
            else if (offspringMode == PedigreeView.OFFSPRING_MODE_MALE_FEMALE)
            {
                newChildren = existingFamily.addChildren(numberFemaleChildren,numberMaleChildren,onlyLiveChildren);
            }
            else
            {
                throw new InternalEngineException("No family created.  Illegal family mode: " + offspringMode);
            }
            addChildrenToFamily(existingFamily, newChildren);
            retFamily = existingFamily;
        }

        return retFamily;
    }
    
    /**
     * Place the levels top down. Given a bounding dimension, each level places 
     * its objects and reports back the resulting dimension it will use. Using
     * this resulting dimension, levels are placed centered within our scrollable
     * region.
     *
     * The families/objects are placed first and return their relative placement.
     * Then based on the placement in our viewport, they are moved (via translate())
     * to an absolute placement. Then the connections are placed so that they can
     * utilize the placement of the corresponding parents.
     *
    **/
    private void placeLevels()
    {
        int x = 0;
        int y = 20;		// initial padding from top edge

        // Size of the scrollable area
        Dimension boundsDim = scrollPane.getViewport().getSize();

        // We are not guaranteed to have a size yet, so this will defer
        // placing everything until paintComponent.
        if (boundsDim.width == 0)
        {
            needPlaceLevels = true;
            return;
        }

        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            Dimension dim = pedigreeLevel.placeFamilies(boundsDim);
            x = (boundsDim.width / 2) - (dim.width / 2);
            if (x < 0) {
                int neg = 0 - x;
                x += neg;
            }
            //x += 10;	// padding from left edge
            pedigreeLevel.translate(x, y);
            pedigreeLevel.placeConnections();
            y += dim.height;
            y += 32;	// static height of connections
        }
        updatePreferredSize();
        
    }
    
    /**
     * Manage the scrollbars. Our base class overrides getPreferredSize, utilizing
     * the members preferredWidth and preferredHeight. By manipulating them here we
     * affect the results of that call which is used by the objects doing the 
     * scrollbar management.
     *
    **/
    private void updatePreferredSize()
    {
        // Find most negative positions
        Point minimumPoint = new Point(10000,10000);
        Point maximumPoint = new Point(0,0);

        // Loop over all organisms and families and get min and max points
        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            PedigreeObject pedigreeObject;
            Enumeration ePedigreeObjects = pedigreeLevel.getPedigreeObjects();
            while (ePedigreeObjects.hasMoreElements())
            {
                pedigreeObject = (PedigreeObject) ePedigreeObjects.nextElement();
                pedigreeObject.getMinimumMaximum(minimumPoint,maximumPoint);
            }
        }
        // Update xMaximum and yMaximum given positions of objects
        xMaximum = maximumPoint.x;
        yMaximum = maximumPoint.y;

        Dimension viewDim = scrollPane.getViewport().getSize();
        int viewWidth = viewDim.width;
        int viewHeight = viewDim.height;

        if (xMaximum > viewWidth)
            preferredWidth = xMaximum + 20;
        else
            preferredWidth = 0;
        
        if (yMaximum > viewHeight)
            preferredHeight = yMaximum + 20;
        else
            preferredHeight = 0;


        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        if (scrollBar != null)
            scrollBar.setUnitIncrement(20);

        scrollBar = scrollPane.getHorizontalScrollBar();
        if (scrollBar != null)
            scrollBar.setUnitIncrement(20);

        revalidate();
    }

    /**
     * Draw the graphics in this view. First paint the connections and then the
     * families and organisms so to ensure that the organisms are drawn on top of
     * the connections.
     *
     * Draw the rubberbanding for crossing and for the selection rectangle.
     *
     * @param 		g Graphics - the given graphics to use in drawing
    **/
    public void paintComponent(Graphics g)
    {
        // Place the levels again in case when done the first time
        // the component didn't have a size yet. This method will be 
        // running in the UI thread whereas placeLevels was previously
        // called from an EASL thread.
        if (needPlaceLevels == true)
        {
            placeLevels();
            needPlaceLevels = false;
        }
        
        // Get bounds and paint background
        Rectangle boundsRectangle = getBounds();
        paintBackground(g,boundsRectangle);

        // Make sure we have fontMetrics defined
        if (fontMetrics == null)
        {
            updateFont(g);
        }

        // Set color and font
        g.setFont(getFont());
        g.setColor(getForeground());
        
        // Loop through levels and paint connections
        PedigreeLevel pedigreeLevel;
        Enumeration ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            pedigreeLevel.paintConnections(g,this,trait,fontHeight,selectionSet,highlightSet);
        }
            
        // Loop through levels and paint the families and organisms
        ePedigreeLevels = pedigreeLevels.elements();
        while (ePedigreeLevels.hasMoreElements())
        {
            pedigreeLevel = (PedigreeLevel) ePedigreeLevels.nextElement();
            pedigreeLevel.paintOrganisms(g,this,trait,fontHeight,selectionSet,highlightSet);
        }
            
        // If in the process of doing a cross, draw rubber banding graphics
        if (parentOnePedigreeOrganism != null)
        {
            PedigreeFamily parentOnePedigreeFamily =
                            getFamilyForOrganism(parentOnePedigreeOrganism);
            Point parentOnePoint = parentOnePedigreeOrganism.getBottomMidPoint();
            Point familyOnePoint = (parentOnePedigreeFamily != null)
                        ? parentOnePedigreeFamily.getBottomMidPoint()
                        : parentOnePoint;
            int yCrossBar;
            if (yMousePrevious > familyOnePoint.y)
            {
                yCrossBar = yMousePrevious + 10;
            }
            else
            {
                yCrossBar = familyOnePoint.y + 10;
            }

            g.drawLine(parentOnePoint.x,parentOnePoint.y,parentOnePoint.x,yCrossBar);
            g.drawLine(parentOnePoint.x,yCrossBar,xMousePrevious,yCrossBar);
            g.drawLine(xMousePrevious,yCrossBar,xMousePrevious,yMousePrevious);
        }

        // If in the process of doing a dynamic rectangle selection, show graphics
        if (dynamicRectangleMouseDownPoint != null)
        {
            int xTopLeft, yTopLeft, xBottomRight, yBottomRight;
            if (dynamicRectangleMouseDownPoint.x < xMousePrevious)
            {
                xTopLeft = dynamicRectangleMouseDownPoint.x;
                xBottomRight = xMousePrevious;
            }
            else
            {
                xTopLeft = xMousePrevious;
                xBottomRight = dynamicRectangleMouseDownPoint.x;
            }
            if (dynamicRectangleMouseDownPoint.y < yMousePrevious)
            {
                yTopLeft = dynamicRectangleMouseDownPoint.y;
                yBottomRight = yMousePrevious;
            }
            else
            {
                yTopLeft = yMousePrevious;
                yBottomRight = dynamicRectangleMouseDownPoint.y;
            }
            g.drawRect(xTopLeft,yTopLeft,xBottomRight-xTopLeft,yBottomRight-yTopLeft);
        }
    }

    /**
     * Handle mouse click events
     *
     * @param 		event MouseEvent - the event object
    **/
    public void mouseClicked(MouseEvent event)
    {
        // Can't use mouse clicked events for creating
        // dragons, as you don't get a click event if
        // the mouse moves while the mouse button is down.
    }

    /**
     * Handle mouse entered event
     *
     * @param 		event MouseEvent - the event object
    **/
    public void mouseEntered(MouseEvent event)
    {
    }

    /**
     * Handle mouse exited event
     *
     * @param 		event MouseEvent - the event object
    **/
    public void mouseExited(MouseEvent event)
    {
    }

    /**
     * Handle mouse pressed event. Handles Snipping, Chromosome and Pedigree tools.
     * Assists in handling Selection.
     *
     * @param 		event MouseEvent - the event object
    **/
    public void mousePressed(MouseEvent event)
    {
        // Ignore mouse events if nothing in view
        if (pedigreeLevels == null || pedigreeLevels.size() == 0)
        {
            return;
        }

        // Is this shift or control key down?
        // (current behavior is really that of the control key. Ideally we'd implement
        // standard shift key selection behavior too)
        boolean shiftDown = event.isShiftDown() || event.isControlDown();

        // Get mouse coordinates
        int xMouse = event.getX();
        int yMouse = event.getY();

        // Are organisms drawn as traits?
        boolean drawAsTrait = false;
        if (trait != null)
        {
            drawAsTrait = true;
        }

        if (activeTool == Tool.SELECTION)
        {
            // We're not going to select family connections like we used to.
            // Instead we're just going to loop over all the organisms in the view.
            PedigreeOrganism pedigreeOrganism = null;
            Enumeration ePedigreeOrganisms = getOrganisms();
            while (ePedigreeOrganisms.hasMoreElements())
            {
                pedigreeOrganism = (PedigreeOrganism)ePedigreeOrganisms.nextElement();
                if (pedigreeOrganism.pick(xMouse,yMouse))
                {
                    if (selectionSet != null)
                    {
                        selectionSet.selectObject(pedigreeOrganism.getOrganism(),shiftDown,shiftDown);
                        xMousePrevious = xMouse;
                        yMousePrevious = yMouse;
                    }
                    return;
                }
            }

            // Start dynamic rectangle selection
            dynamicRectangleMouseDownPoint = new Point(xMouse,yMouse);
            xMousePrevious = xMouse;
            yMousePrevious = yMouse;
            return;
        }
        else if (activeTool == Tool.CROSS)
        {
            // Find the other parent will happen in mouse release event handling
        }
        else if (activeTool == Tool.SNIP)
        {
            // Deletion event - first check organisms
            PedigreeOrganism pedigreeOrganism;
            Enumeration ePedigreeOrganisms = getOrganisms();
            Frame tempFrame = JOptionPane.getFrameForComponent(this);
            while (ePedigreeOrganisms.hasMoreElements())
            {
                pedigreeOrganism = (PedigreeOrganism) ePedigreeOrganisms.nextElement();
                if (pedigreeOrganism.pick(xMouse,yMouse))
                {
                    // Delete organism
                    
                    Organism organism = pedigreeOrganism.getOrganism();
                    
                     // Notify listeners that a snip tool event happened on organism
                    changes.firePropertyChange(UIProp.SNIP_TOOL_PICK_ON_ORGANISM, null, organism);
                    
                    if (organism.isManualLocked() == false)
                    {
                    	int level = findLevelForOrganism(pedigreeOrganism);
                    	PedigreeLevel pedigreeLevel = (PedigreeLevel)(pedigreeLevels.elementAt(level));
   
                   		pedigreeLevel.removeOrganism(pedigreeOrganism);
                		pedigreeLevel.placeConnections();
                		
                		logUseOfSnipTool(organism);
                        organism.delete();
                        organism = null;
                		
                		tempFrame.repaint();
                    }
                   
                    return;
                }
            }
            // Next check families (connections)
            PedigreeFamily pedigreeFamily;
            Enumeration ePedigreeFamilies = getFamilies();
            int temp = 0;
            while (ePedigreeFamilies.hasMoreElements())
            {
                pedigreeFamily = (PedigreeFamily) ePedigreeFamilies.nextElement();
                if (pedigreeFamily.pickForSnip(xMouse,yMouse))
                {
                    // Delete family
                    Family family = pedigreeFamily.getFamily();
                   // Notify listeners that a snip tool event happened on organism
                    changes.firePropertyChange(UIProp.SNIP_TOOL_PICK_ON_FAMILY, null, family);
                    if (family.isManualLocked() == false)
                    {
                    	logUseOfSnipTool(family);
                        family.delete();
                        family = null;
                   		tempFrame.repaint();
                    }
                
                    
                    
                    return;
                }
               temp+=1;
            }
        }
        else if (activeTool == Tool.CHROMOSOME)
        {
            // Check for a pick on an organism
            PedigreeOrganism pedigreeOrganism;
            Enumeration ePedigreeOrganisms = getOrganisms();
            while (ePedigreeOrganisms.hasMoreElements())
            {
                // Pick on organisms
                pedigreeOrganism = (PedigreeOrganism) ePedigreeOrganisms.nextElement();
                if (pedigreeOrganism.pick(xMouse,yMouse))
                {
                    // Select organism
                    if (selectionSet != null)
                    {
                        selectionSet.selectObject(pedigreeOrganism.getOrganism(),shiftDown,shiftDown);
                    }
					//log use of Chromosome Tool
					logUseOfChromosomeTool(pedigreeOrganism.getOrganism());

                    // Notify listeners that a chromosome tool event happened on organism
                    changes.firePropertyChange(UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM,
                                               null, pedigreeOrganism.getOrganism());
                    //create new DialogBox 
                  	chromosomeBox.setShowingText(chromosomeStr);
                  	if (blnChromosomeViewVisible)  
                  		chromosomeBox.setChromosomeView(pedigreeOrganism.getOrganism());
              
                    
                    if (thePedigreeView != null){
                    
                    	blnSelectionToolVisible = thePedigreeView.isSelectionToolVisible();
    					blnCrossToolVisible = thePedigreeView.isCrossToolVisible();;
   						blnSnipToolVisible = thePedigreeView.isSnipToolVisible();;
   						blnChromosomeToolVisible = thePedigreeView.isChromosomeToolVisible();;
   					  }
                    
                    return;
                }
            }
        }
        else if (activeTool == Tool.PEDIGREE)
        {
            // Check for a pick on an organism
            PedigreeOrganism pedigreeOrganism;
            Enumeration ePedigreeOrganisms = getOrganisms();
            while (ePedigreeOrganisms.hasMoreElements())
            {
                // Pick on organisms
                pedigreeOrganism = (PedigreeOrganism) ePedigreeOrganisms.nextElement();
                if (pedigreeOrganism.pick(xMouse,yMouse))
                {
                    // Select organism
                    if (selectionSet != null)
                    {
                        selectionSet.selectObject(pedigreeOrganism.getOrganism(),shiftDown,shiftDown);
                    }

                    // Notify listeners that a pedigree tool event happened on organism
                    changes.firePropertyChange(UIProp.PEDIGREE_TOOL_PICK_ON_ORGANISM,
                                               null, pedigreeOrganism.getOrganism());
                    return;
                }
            }
        }
    }
    
   
    /**
     * Is the selection tool visibled before?
     *
     * @return		boolean - is the selection tool visibled?
    **/
    public boolean isSelectionToolVisibledBefore(){
    	return blnSelectionToolVisible;
    }
    
    /**
     * Is the cross tool visibled before?
     *
     * @return		boolean - is the cross tool visibled?
    **/
    public boolean isCrossToolVisibledBefore(){
    	return blnCrossToolVisible;
    }
    /**
     * Is the snip tool visibled before?
     *
     * @return		boolean - is the snip tool visibled?
    **/
    public boolean isSnipToolVisibledBefore(){
    	return blnSnipToolVisible;
    }
    /**
     * Is the chromosome tool visibled before?
     *
     * @return		boolean - is the chromosome tool visibled?
    **/
    public boolean isChromosomeToolVisibledBefore(){
    	return blnChromosomeToolVisible;
    }

    /**
     * Handle mouse released event. Handles Crossing and assists in handling Selection.
     *
     * @param 		event MouseEvent - the event object
    **/
    public void mouseReleased(MouseEvent event)
    {
        boolean needRepaint = false;
        Organism parentOne = null;

        // Clear state related to rubber banding
        if (parentOnePedigreeOrganism != null)
        {
            parentOne = parentOnePedigreeOrganism.getOrganism();
            parentOnePedigreeOrganism = null;
            needRepaint = true;
        }

        // Handle event
        int xMouse = event.getX();
        int yMouse = event.getY();

        // Is this shift or control key down?
        // (current behavior is really that of the control key. Ideally we'd implement
        // standard shift key selection behavior too)
        boolean shiftDown = event.isShiftDown() || event.isControlDown();

        if (activeTool == Tool.SELECTION)
        {
            if (dynamicRectangleMouseDownPoint == null)
            {
                needRepaint = true;
            }
            else
            {
                needRepaint = true;
    
                int xTopLeft, yTopLeft, xBottomRight, yBottomRight;
                if (dynamicRectangleMouseDownPoint.x < xMouse)
                {
                    xTopLeft = dynamicRectangleMouseDownPoint.x;
                    xBottomRight = xMouse;
                }
                else
                {
                    xTopLeft = xMouse;
                    xBottomRight = dynamicRectangleMouseDownPoint.x;
                }
                if (dynamicRectangleMouseDownPoint.y < yMouse)
                {
                    yTopLeft = dynamicRectangleMouseDownPoint.y;
                    yBottomRight = yMouse;
                }
                else
                {
                    yTopLeft = yMouse;
                    yBottomRight = dynamicRectangleMouseDownPoint.y;
                }
    
                // Force extend selection for objects after first one
                boolean firstObjectSelected = false;
    
                // Select organisms within the rectangle
                PedigreeOrganism pedigreeOrganism = null;
                Enumeration ePedigreeOrganisms = getOrganisms();
                while (ePedigreeOrganisms.hasMoreElements())
                {
                    pedigreeOrganism = (PedigreeOrganism)ePedigreeOrganisms.nextElement();
                    if (pedigreeOrganism.within(xTopLeft, yTopLeft,
                                                xBottomRight, yBottomRight))
                    {
                        if (firstObjectSelected == false)
                        {
                            selectionSet.selectObject(pedigreeOrganism.getOrganism(),shiftDown,shiftDown);
                            firstObjectSelected = true;
                        }
                        else
                        {
                            selectionSet.selectObject(pedigreeOrganism.getOrganism(),true,true);
                        }
                        repaint();
                    }
                }

                // If no objects were selected, force a deselection of everything
                if (firstObjectSelected == false)
                {
                    selectionSet.deselectAllObjects();
                }
    
                dynamicRectangleMouseDownPoint = null;
            }
        }
        else if (activeTool == Tool.CROSS)
        {
            if (parentOne == null)
            {
                PedigreeOrganism pedigreeOrganism = null;
                Enumeration ePedigreeOrganisms = getOrganisms();
                while (ePedigreeOrganisms.hasMoreElements())
                {
                    pedigreeOrganism = (PedigreeOrganism)ePedigreeOrganisms.nextElement();
                    if (pedigreeOrganism.pick(xMouse,yMouse))
                    {
                        parentOnePedigreeOrganism = pedigreeOrganism;
                        break;
                    }
                }
                if (parentOnePedigreeOrganism != null)
                {
                    // If not a dead organism, set first parent organism and return immediately
                    if (parentOnePedigreeOrganism.getOrganism().containsFatalCharacteristic())
                    {
                        parentOnePedigreeOrganism = null;
                    }
                    else 
                    {
                        xMousePrevious = xMouse;
                        yMousePrevious = yMouse;
                        return;
                    }
                }
            }
            else
            {
                // Look for a second parent
                Organism parentTwo = null;
                PedigreeOrganism pedigreeOrganism = null;
                Enumeration ePedigreeOrganisms = getOrganisms();
                while (ePedigreeOrganisms.hasMoreElements())
                {
                    pedigreeOrganism = (PedigreeOrganism)ePedigreeOrganisms.nextElement();
                    if (pedigreeOrganism.pick(xMouse,yMouse))
                    {
                        parentTwo = pedigreeOrganism.getOrganism();
                        break;
                    }
                }
                if (parentTwo != null)
                {
                    // Found second parent, so do cross
                    if (!parentTwo.containsFatalCharacteristic())
                    {
                        // Make sure they're not the same gender unless we have a species
                        // which doesn't have sexes
                        if (parentOne.getSex() != parentTwo.getSex() ||
                            currentSpecies.getDiploidType() == Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
                        {
                            // Add family or add to an existing family
                          
                            Family family = addOrAddToFamily(parentOne, parentTwo);
                            
                            
                            //keepObjectsInPositiveSpace();
                            changes.firePropertyChange(UIProp.CROSS_SUCCEEDED,null,family);
                           	logIt(family);
                            repaint();
                            Frame temp = JOptionPane.getFrameForComponent(this);
                            temp.repaint();
                            return;
                        }
                        else if (!parentTwo.equals(parentOne))// Same sex - keep trying to find an opposite sex organism in picking range
                        {
                        	JOptionPane.showMessageDialog(this,"You have tried to cross a "+parentOne.getSexAsString()+
                        								" with a "+parentTwo.getSexAsString()+"! \nYou can cross any two "+
                        								"organisms you like as \nlong as they are a male and a female.","Alert",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                     else
                    {
                    	JOptionPane.showMessageDialog(this,"One or both of the parent organisms is dead","Alert",JOptionPane.ERROR_MESSAGE);                     
                    }// May be a dead organism, which isn't allowed to breed
                }
            }

            // Didn't find an organism at the mouse location
            changes.firePropertyChange(UIProp.CROSS_FAILED,parentOne,null);
            needRepaint = true;
        }
        if (needRepaint)
        {
            repaint();
        }
    }

    /**
     * Handle mouse dragged event
     *
     * @param 		event MouseEvent - the event object
    **/
    public void mouseDragged(MouseEvent event)
    {
        int xMouse = event.getX();
        int yMouse = event.getY();

        if (activeTool == Tool.SELECTION)
        {
            // Used to do moving here, but now just track mouse	
            xMousePrevious = xMouse;
            yMousePrevious = yMouse;
        }
        
        repaint();
    }
    
    /**
     * Handle mouse moved event
     *
     * @param 		event MouseEvent - the event object
    **/
    public void mouseMoved(MouseEvent event)
    {
        boolean needRepaint = true;

        int xMouse = event.getX();
        int yMouse = event.getY();

        if (activeTool == Tool.CROSS)
        {
            // Just track mouse
            xMousePrevious = xMouse;
            yMousePrevious = yMouse;
        }
        
        // If we aren't in the middle of a cross, highlight connections from organism
        // to ancestors. If we are in the middle of a cross, turn highlighting off.
        if (parentOnePedigreeOrganism == null)
        {
            needRepaint = highlightConnections(xMouse, yMouse);
        }
        else
        {
            needRepaint = clearHighlighting();
        }

        if (needRepaint)
            repaint();
    }

    /**
     * Highlight the path from the given organism back to its ancestors.
     *
     * @param 		pedigreeOrganism PedigreeOrganism - organism who's path to highlight.
    **/
    private boolean highlightConnections(int xMouse, int yMouse)
    {
        boolean needRepaint = false;
        boolean highlightingFamily = false;
        boolean highlightingOrganism = false;
        
        // Loop over families and if we get a hit, clean out set of stuff to highlight 
        // and add family and all of its ancestors
        PedigreeFamily pedigreeFamily = null;
        Enumeration ePedigreeFamilies = getFamilies();
        while (ePedigreeFamilies.hasMoreElements())
        {
            pedigreeFamily = (PedigreeFamily)ePedigreeFamilies.nextElement();
            if (pedigreeFamily.pick(xMouse,yMouse))
            {
                highlightingFamily = true;
                if (pedigreeFamily == highlightedFamily)
                {
                    break;	// Avoid flashing
                }
                else
                {
                    highlightSet.deselectAllObjects();
                    addToHighlightSet(pedigreeFamily, true);	// remove recursively
                    highlightedFamily = pedigreeFamily;
                    needRepaint = true;
                    break;
                }
            }
        }
        // Loop over organisms. If we don't find anything under the pointer, the family
        // added above will still trigger the highlighting. If we do find an organism,
        // remove the selected one and add the new one. Do nothing regarding ancestors.
        PedigreeOrganism pedigreeOrganism = null;
        Enumeration ePedigreeOrganisms = getOrganisms();
        while (ePedigreeOrganisms.hasMoreElements())
        {
            pedigreeOrganism = (PedigreeOrganism)ePedigreeOrganisms.nextElement();
           
            if (pedigreeOrganism.pick(xMouse,yMouse))
            {
                highlightingOrganism = true;
                if (pedigreeOrganism == highlightedOrganism)
                {
                    break;	// Avoid flashing
                }
                else
                {
                    if (highlightedOrganism != null)
                        highlightSet.deselectObject(highlightedOrganism.getOrganism());

                    addToHighlightSet(pedigreeOrganism, false);	// Don't recurse
                   
                    highlightedOrganism = pedigreeOrganism;
                    needRepaint = true;
                    break;
                }
            }
        }
        // If we didn't get any mouseOver hits, clear out highlight set and repaint.
        if (!highlightingFamily && !highlightingOrganism)
        {
            needRepaint = clearHighlighting();
        }
        
        return needRepaint;
    }

    /**
     * Clear the set of objects and the other state variables.
    **/
    private boolean clearHighlighting()
    {
        highlightSet.deselectAllObjects();
        highlightedFamily = null;
        highlightedOrganism = null;
        return true;
    }		

    /**
     * Add organism and optionally its parents to highlight set
    **/
    private void addToHighlightSet(PedigreeOrganism pedigreeOrganism, boolean recurse)
    {
        Organism organism = pedigreeOrganism.getOrganism();
 
        if (!highlightSet.contains(organism))
        {
            highlightSet.selectObject(organism,true,true);
            
          /* PedigreeFamily pedigreeFamily = getFamilyForOrganism(pedigreeOrganism);
            if (pedigreeFamily != null)
            {
                PedigreeOrganism motherOrganism = pedigreeFamily.getMotherPedigreeOrganism();
                PedigreeOrganism fatherOrganism = pedigreeFamily.getFatherPedigreeOrganism();
                highlightSet.selectObject(motherOrganism.getOrganism(),true,true);
                highlightSet.selectObject(fatherOrganism.getOrganism(),true,true);
                System.out.println("add both mother and father");
            }*/
         }

        if (recurse)
        {
            PedigreeFamily pedigreeFamily = getFamilyForOrganism(pedigreeOrganism);
            if (pedigreeFamily != null)
            {
                PedigreeOrganism motherOrganism = pedigreeFamily.getMotherPedigreeOrganism();
                PedigreeOrganism fatherOrganism = pedigreeFamily.getFatherPedigreeOrganism();
                addToHighlightSet(motherOrganism, true);
                addToHighlightSet(fatherOrganism, true);
            }
        }
    }

    /**
     * Add Family and optionally its parents to highlight set
    **/
    private void addToHighlightSet(PedigreeFamily pedigreeFamily, boolean recurse)
    {
        Family family = pedigreeFamily.getFamily();
        if (!highlightSet.contains(family))
        {
            highlightSet.selectObject(family,true,true);
        }
        

        if (recurse)
        {
            PedigreeOrganism motherOrganism = pedigreeFamily.getMotherPedigreeOrganism();
            PedigreeOrganism fatherOrganism = pedigreeFamily.getFatherPedigreeOrganism();
            addToHighlightSet(motherOrganism, false);
            addToHighlightSet(fatherOrganism, false);
        }
    }

    /**
     * Selection changed
    **/
    public void selectionChanged()
    {
        repaint();
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.ORGANISM_GENOTYPE_AND_PHENOTYPE) ||
            propertyName.equals(EngineProp.LOCKED_STATE) ||
            propertyName.equals(EngineProp.VISIBLE))
        {
            // Force repaint
            repaint();
        }
        else if (propertyName.equals(EngineProp.DELETED))
        {
            Object object = event.getSource();
            if (object instanceof Organism)
            {
                // Remove deleted organism from this view
                Organism eventOrganism = (Organism) object;
                removeOrganism(eventOrganism);
            }
            else if (object instanceof Family)
            {
                // Remove deleted family from this view
                Family eventFamily = (Family) object;
                removeFamily(eventFamily);
            }
            else if (object instanceof Species)
            {
                // Remove deleted species and trait from this view
                setSpecies(null);
            }
        }
    }
    
    /**
    *set Cross Over possibility
    **/
    private double P = 0;
    public void setCrossOverPossibility(double p)
    {
    	P = p;
    }
    
    private double getCrossOverPossibility()
    {
    	return P;
    }




	public Element serializeAsElement(Document document){
		return  null;
	}

	public Element serializeAsElementLog(Document document){
		return  serializeAsElement(document);
	}

	public void save(Writer writer){}
	public void restore(Reader reader){}
	
	public void save(OutputStream output){}
	public void restore(InputStream input){}


	public DomainView create(String vewSpec){return this;}
	public void closeEverything(){}
	public void release(DomainView view){}
	public Vector getViewMethods(){return null;}
	public Vector getViewEvents(){return null;}
	public Vector getViewActions(){return null;}
	
	public DomainEngine getEngine(){return null;}
	
	public void setActivity(Object activity){
		if(activity instanceof Activity){
			this.activity = (Activity)activity;
		}
	} 
	
	Activity activity;

	public Loggable getLoggable(Object selector){
		return this;
	}


	int logmode = LOG_ACTION;
	public void setLogMode(int logmode){
		this.logmode = logmode;
	}
	public int  getLogMode(){
		return logmode;
	}

	public void log(Writer writer,LogHintMessage hint){ 
		if(hint == null) return;
		try{
			Object obj = hint.getOwner();
			if(obj instanceof Family){
				Family family = (Family)obj;
				Logging logging = activity.getLogging();
				if(logging != null)
				{
					Organism femaleOrg = family.getFemaleParent();
					Organism maleOrg = family.getMaleParent();
					logging.logAction(3,"Characteristic is being observed: "+(this.getTrait()).toString());
					logging.logAction(3,"Genotype of mother: "+femaleOrg.getGenotypeAsString());
					logging.logAction(3,"Genotype of father: "+maleOrg.getGenotypeAsString());
					logging.logAction(3,"Generation of mother: "+String.valueOf(femaleOrg.getGeneration()));
					logging.logAction(3,"Generation of father: "+String.valueOf(maleOrg.getGeneration()));
					logging.logAction(3,"number of offspring: "+String.valueOf(family.getNumberOfChildren()));
				} 
			}
			
		}catch(Exception e){}
	
	}
	public void log(OutputStream out,LogHintMessage hint){
		log(new OutputStreamWriter(out),hint);
	}

	LogHintMessage  hint = new LogHintMessage(null,org.concord.util.logging.LogTransaction.MIDDLE_PRIORITY,Loggable.LOG_FROM_ACTION);

	public void logIt(Family family){
		
		if(activity == null) return;
		Logging logging = activity.getLogging();
		hint.setOwner(family);
		if(logging != null) logging.logLoggable(this,hint);
	}
	/**
	 *	log change of characteristic viewed 
	 *
     *  @param	oldTrait Trait - the trait that used to show, may be null;
     *  @param	newTrait Trait - the trait to show right now, may be null;
    **/
	private void logChangeOfCharateristic(Trait oldTrait, Trait newTrait)
	{
		if(activity == null) return;
		Logging logging = activity.getLogging();
		if(logging != null)
		{
			String oldTraitString = "";
			String newTraitString = "";
			if (oldTrait != null) oldTraitString = oldTrait.getName();
			if (newTrait != null) newTraitString = newTrait.getName();
			logging.logAction(3,"Characteristic is being changed from "+oldTraitString +" to "+newTraitString);
		}
	}
	/**
	 *	log use of chromosome Tool; 
	 *
     *  @param	aOrganism Organism - the organism which has been examined;
     * 
    **/
	private void logUseOfChromosomeTool(Organism aOrganism)
	{
		if(activity == null) return;
		if (aOrganism == null) return;
		Logging logging = activity.getLogging();
		if(logging != null)
		{
			String nameOfSpecies = ((Species)(aOrganism.getSpecies())).getName();
			logging.logAction(3,"Use chromosome tool to examine a "+nameOfSpecies+". Genotype "+
							  "of this "+nameOfSpecies+" is: "+aOrganism.getGenotypeAsString()+
							  "Generation: "+aOrganism.getGeneration()+ ". Gender: "+aOrganism.getSexAsString());
		}
	}
	/**
	 *	log use of snip Tool; 
	 *
     *  @param	aOrganism Organism - the organism which has been examined;
     * 
    **/
	private void logUseOfSnipTool(Organism aOrganism)
	{
		if(activity == null) return;
		if (aOrganism == null) return;
		Logging logging = activity.getLogging();
		if(logging != null)
		{
			String nameOfSpecies = ((Species)(aOrganism.getSpecies())).getName();
			logging.logAction(3,"Snip out one "+nameOfSpecies+". Genotype "+
							  "of this "+nameOfSpecies+" is: "+aOrganism.getGenotypeAsString());
		}
	}
	private void logUseOfSnipTool(Family family)
	{
		if(activity == null) return;
		if (family == null) return;
		Logging logging = activity.getLogging();
		if(logging != null)
		{
			String nameOfSpecies = ((Species)(this.getSpecies())).getName();
			logging.logAction(3,"Snip out one family. Total "+family.getNumberOfChildren()+" "+nameOfSpecies+"s has been sniped out.");
		}
	}

    public int getSerializingLevel(){return SERIALIZING_LEVEL_HIGH;}
    public void setSerializingLevel(int val){}
    public int getLogSerializingLevel(){return SERIALIZING_LEVEL_BASIC;}
    public void setLogSerializingLevel(int val){}
    public int getDefaultLogMode(){
        return LOG_FROM_ACTION;
    }
    
    public void setLogName(String str){}
    public String getLogName(){return getName();}

    public Interaction getFirstInteraction(){return null;}
    public Interaction getActionInteraction(){return null;}
    public Interaction getLastInteraction(){return null;}
    public void initLogState(){}

}
/*
		if((getLogMode() & LOG_ACTION) == 0) return;
		if(activity == null) return;
		Logging logging = activity.getLogging();
		hint.setOwner(activity);
		if(logging != null) logging.logLoggable(this,hint);
*/

