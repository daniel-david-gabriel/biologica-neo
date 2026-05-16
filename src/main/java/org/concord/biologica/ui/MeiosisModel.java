//
// Class : MeiosisModel
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.5 $
// $Date: 2003/02/04 16:08:40 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.lang.String;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import java.awt.Color;
import java.awt.Rectangle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

/**
 * MeiosisModel contains the data describing the meiosis process state
 * for a given organism.  It does not contain the code for drawing the
 * animation as that is in the view objects that show meiosis.<p>
 *
 * This model and its associated chromosome models assume a display
 * space of 0 to 1000 by 0 to 1000 and all locations are within that range.
 * This means a user of these model classes must scale locations
 * appropriately from this coordinate system to their own in order
 * to draw the chromosomes.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.MEIOSIS_STARTED - meiosis has started
 * <li> org.concord.biologica.ui.UIProp.MEIOSIS_STEP - meiosis step has changed
 * <li> org.concord.biologica.ui.UIProp.MOVED_GAMETE - the moved gamete has changed
 * <li> org.concord.biologica.ui.UIProp.ORGANISM - the organism has changed from one organism to another
 * <li> org.concord.biologica.ui.UIProp.SELECTED_GAMETE - the selected gamete has changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_STARTED
 * @see org.concord.biologica.ui.UIProp#MEIOSIS_STEP
 * @see org.concord.biologica.ui.UIProp#MOVED_GAMETE
 * @see org.concord.biologica.ui.UIProp#ORGANISM
 * @see org.concord.biologica.ui.UIProp#SELECTED_GAMETE
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.5 $ $Date: 2003/02/04 16:08:40 $
 * @author 		$Author: qliao $
**/
public final class MeiosisModel implements PropertyChangeListener
{
	/**
	 * Meiosis phase values
	**/
	private static final int PRE_MEIOSIS	= 0;
	private static final int INTERPHASE		= 1;
	private static final int PROPHASE_1		= 2;
	private static final int METAPHASE_1	= 3;
	private static final int ANAPHASE_1		= 4;
	private static final int TELOPHASE_1	= 5;
	private static final int PROPHASE_2		= 6;
	private static final int METAPHASE_2	= 7;
	private static final int ANAPHASE_2		= 8;
	private static final int TELOPHASE_2	= 9;
	private static final int CYTOKINESIS	= 10;
	private static final int POST_MEIOSIS	= 11;

	/**bbb
	 * Values indicating which daughter cell will get chromosome
	**/
	private static final int GO_EITHER		= 0;	// randomly choose direction
	private static final int GO_LEFT		= 1;	// go to left daughter cell
	private static final int GO_RIGHT		= 2;	// go to right daughter cell

	/**
	 * Gamete identifiers
	**/
	public static final int NO_GAMETE			= 0;
	public static final int TOP_LEFT_GAMETE		= 1;
	public static final int BOTTOM_LEFT_GAMETE	= 2;
	public static final int TOP_RIGHT_GAMETE	= 3;
	public static final int BOTTOM_RIGHT_GAMETE	= 4;

	/**
	 * Random number generator used in choosing locations, angles, etc.<p>
	**/
	private static Random random = new Random();

	/**
	 * Meiosis step values range from 0 to 100 where the phase
	 * for each step is:
	 *
	 *          step          phase
	 *          --------      -----------
	 *			0 			- PRE_MEIOSIS
	 *			1 to 10 	- INTERPHASE
	 *			11 to 20 	- PROPHASE_1
	 *			21 to 30	- METAPHASE_1
	 *			31 to 40	- ANAPHASE_1
	 *			41 to 50	- TELOPHASE_1
	 *			51 to 60	- PROPHASE_2
	 *			61 to 70	- METAPHASE_2
	 *			71 to 80	- ANAPHASE_2
	 *			81 to 90	- TELOPHASE_2
	 *			91 to 99	- CYTOKINESIS
	 *			100			- POST_MEIOSIS
	**/
	private int currentStep = 0;

	/**
	 * The organism, may be null
	**/
	private Organism organism = null;

	/**
	 * Selected gamete.  This is the gamete that has
	 * most recently been selected and has not yet
	 * been moved into fertilization.<p>
	 *
	 * One or both of selectedGamete and movedGamete
	 * must be NO_GAMETE at all times.  In other words,
	 * you can't have a different selectedGamete than
	 * the movedGamete.<p>
	**/
	private int selectedGamete = NO_GAMETE;
	private Gamete realSelectedGamete = new Gamete();

	/**
	 * Moved gamete.  This is the gamete that has been
	 * moved into fertilization.<p>
	 *
	 * One or both of selectedGamete and movedGamete
	 * must be NO_GAMETE at all times.  In other words,
	 * you can't have a different selectedGamete than
	 * the movedGamete.<p>
	**/
	private int movedGamete = NO_GAMETE;
	private Gamete realMovedGamete = new Gamete();;

	/**
	 * Vector of chromosome models for original cell of this meiosis.
	 * These chromosome models should be used for steps 0 to 30.  Step 31,
	 * anaphase 1, is when the left and right daughter cell chromosome
	 * models should start being used.
	**/
	private	Vector origCellChromosomeModels;

	/**
	 * Vector of chromosome models for left daughter cell of this meiosis,
	 * created during the first split of the main cell.  These chromosome
	 * models should be used for steps 31 to 70.  Step 71, anaphase 2, is
	 * when the top and bottom gamete chromosome models should start being used.
	**/
	private Vector leftDaughterCellChromosomeModels;

	/**
	 * Vector of chromosome models for second daughter call of this meiosis,
	 * created during first split of the main cell.  These chromosome
	 * models should be used for steps 31 to 70.  Step 71, anaphase 2, is
	 * when the top and bottom gamete chromosome models should start being used.
	**/
	private Vector rightDaughterCellChromosomeModels;

	/**
	 * Vector of chromosome models for top left gamete of this meiosis,
	 * created when the left daughter cell splits in two.  These chromosome
	 * models should be used for steps 71 to 100.
	**/
	private Vector topLeftGameteChromosomeModels;

	/**
	 * Vector of chromosome models for bottom left gamete of this meiosis,
	 * created when the left daughter cell splits in two.  These chromosome
	 * models should be used for steps 71 to 100.
	**/
	private Vector bottomLeftGameteChromosomeModels;

	/**
	 * Vector of chromosome models for top right gamete of this meiosis,
	 * created when the right daughter cell splits in two.  These chromosome
	 * models should be used for steps 71 to 100.
	**/
	private Vector topRightGameteChromosomeModels;

	/**
	 * Vector of chromosome models for bottom right gamete of this meiosis,
	 * created when the right daughter cell splits in two.  These chromosome
	 * models should be used for steps 71 to 100.
	**/
	private Vector bottomRightGameteChromosomeModels;

	/**
	 * Enclosing view rectangle for this meiosis model.
	**/
	private Rectangle enclosingViewRectangle;

	/**
	 * Have we notified that meiosis has started?
	**/
	private boolean notifiedMeiosisStarted = false;

	/**
	 * Utility object which manages property change events and listeners.
	**/
	protected transient PropertyChangeSupport changes = null;
	
	/**
	 * Gametes;
	 */
	 Gamete [] gametes = new Gamete[4];;

    /**
     * Creates a new instance of MeiosisModel with no organism.
    **/
    public MeiosisModel()
	{
		currentStep = 0;
	
		organism = null;
		selectedGamete = NO_GAMETE;
		realSelectedGamete = null;
		movedGamete = NO_GAMETE;
		realMovedGamete = null;
		
		notifiedMeiosisStarted = false;
		origCellChromosomeModels = new Vector();
		leftDaughterCellChromosomeModels = new Vector();
		rightDaughterCellChromosomeModels = new Vector();
		topLeftGameteChromosomeModels = new Vector();
		bottomLeftGameteChromosomeModels = new Vector();
		topRightGameteChromosomeModels = new Vector();
		bottomRightGameteChromosomeModels = new Vector();
		enclosingViewRectangle = new Rectangle(0,0,500,500);

		changes = new PropertyChangeSupport(this);
    }

	/**
	 * Deletes this model and associated models
	**/
	public void delete()
	{
		// Delete all the chromosome models
		MeiosisChromosomeModel chromosomeModel;
		Enumeration eChromosomeModels;
		Vector chromosomeModelsClone = null;

		int i;
		for (i=0;i<7;i++)
		{
			chromosomeModelsClone = null;

			switch(i)
			{
				case 0: if (origCellChromosomeModels != null)
							chromosomeModelsClone = (Vector) origCellChromosomeModels.clone();
						break;
				case 1: if (leftDaughterCellChromosomeModels != null)
							chromosomeModelsClone = (Vector) leftDaughterCellChromosomeModels.clone();
						break;
				case 2: if (rightDaughterCellChromosomeModels != null)
							chromosomeModelsClone = (Vector) rightDaughterCellChromosomeModels.clone();
						break;
				case 3: if (topLeftGameteChromosomeModels != null)
							chromosomeModelsClone = (Vector) topLeftGameteChromosomeModels.clone();
						break;
				case 4: if (bottomLeftGameteChromosomeModels != null)
							chromosomeModelsClone = (Vector) bottomLeftGameteChromosomeModels.clone();
						break;
				case 5: if (topRightGameteChromosomeModels != null)
							chromosomeModelsClone = (Vector) topRightGameteChromosomeModels.clone();
						break;
				case 6: if (bottomRightGameteChromosomeModels != null)
							chromosomeModelsClone = (Vector) bottomRightGameteChromosomeModels.clone();
						break;
			}
			
			if (chromosomeModelsClone != null)
			{
                eChromosomeModels = chromosomeModelsClone.elements();
				while (eChromosomeModels.hasMoreElements())
				{
					chromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
					chromosomeModel.delete();
				}
			}
		}

		origCellChromosomeModels = null;
		leftDaughterCellChromosomeModels = null;
		rightDaughterCellChromosomeModels = null;
		topLeftGameteChromosomeModels = null;
		bottomLeftGameteChromosomeModels = null;
		topRightGameteChromosomeModels = null;
		bottomRightGameteChromosomeModels = null;

		if (organism != null)
		{
			organism.removePropertyChangeListener(this);
			organism = null;
		}

		selectedGamete = NO_GAMETE;
		movedGamete = NO_GAMETE;
		realSelectedGamete = null;
		realMovedGamete = null;
		currentStep = 0;
		notifiedMeiosisStarted = false;
	}

	/**
	 * Get the organism for this model, may be null.
	 *
	 * @return		Organism - organism for this model, may be null
	**/
	public Organism getOrganism()
	{
		return organism;
	}

	/**
	 * Set the organism of this model, deleting any existing state for another organism.<p>
	 *
	 * @param		Organism anOrganism - organism to use as basis for meiosis model, may be null
	**/
	void setOrganism(Organism anOrganism)
	{
		
		// Return immediately without doing anything if it's the same organism.
		if (anOrganism == organism)
		{
			return;
		}

		// Save reference to old organism
		Organism oldOrganism = organism;

		// Delete state for existing organism, if not null
		if (organism != null)
		{
			delete();
		}

		// Set state for new organism
		currentStep = 0;
		notifiedMeiosisStarted = false;

		organism = anOrganism;
		if (organism != null)
		{
			organism.addPropertyChangeListener(this);
		}

		int oldSelectedGamete = selectedGamete;
		selectedGamete = NO_GAMETE;

		int oldMovedGamete = movedGamete;
		movedGamete = NO_GAMETE;
		
		origCellChromosomeModels = new Vector();
		leftDaughterCellChromosomeModels = new Vector();
		rightDaughterCellChromosomeModels = new Vector();
		topLeftGameteChromosomeModels = new Vector();
		bottomLeftGameteChromosomeModels = new Vector();
		topRightGameteChromosomeModels = new Vector();
		bottomRightGameteChromosomeModels = new Vector();
	
		if (organism != null)
		{
			// Loop through organism chromosomes creating chromosome models for the
			// original cell, the daughter cells and the gametes.  Start the loop
			// with non-sex chromosomes, then switch to sex chromosomes.
			boolean alreadyWentOtherWay = false;
			boolean doingSexChromosomes = false;
			int goWhichWay = GO_EITHER;
			int numberType, visiblePairNumber;
			int maxAutosomePairNumber = 0;
			Color color = Color.blue;
			MeiosisChromosomeModel originalCellChromosomeModel;
			MeiosisChromosomeModel daughterCellChromosomeModel;
			MeiosisChromosomeModel topGameteChromosomeModel;
			MeiosisChromosomeModel bottomGameteChromosomeModel;
			OrganismChromosome anOrganismChromosome;

			Enumeration eOrganismChromosomes = organism.getNonSexChromosomes();
			while (eOrganismChromosomes.hasMoreElements())
			{
				
				anOrganismChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
				
				
				// Determine visible pair number (used to align chromosomes correctly during animation)
				if (anOrganismChromosome.isVisible() == true)
				{
					numberType = anOrganismChromosome.getNumberType();
					if (numberType == IChromosome.X_CHROMOSOME || numberType == IChromosome.Y_CHROMOSOME)
					{
						visiblePairNumber = maxAutosomePairNumber + 1;
					}
					else
					{
						visiblePairNumber = numberType;
						if (visiblePairNumber > maxAutosomePairNumber)
						{
							maxAutosomePairNumber = visiblePairNumber;
						}
					}
				}
				else
				{
					visiblePairNumber = 0;
				}
	
				// Alternate colors
				if (color == Color.blue)
				{
					color = Color.orange;
				}
				else
				{
					color = Color.blue;
				}
				
				
	
				// Original cell has a chromosome model for every chromosome
				originalCellChromosomeModel = new MeiosisChromosomeModel(this, anOrganismChromosome,
																		 MeiosisChromosomeModel.ORIG_CELL_CHROMOSOME,
																		 null, MeiosisChromosomeModel.UNSPECIFIED_STRAND,
																		 enclosingViewRectangle,
																		 visiblePairNumber, color);
				origCellChromosomeModels.addElement(originalCellChromosomeModel);

				// Daughter cells have half the chromosomes, some chromosomes
				// going to the right daughter cell, some to the left.
				if (goWhichWay == GO_EITHER)
				{
					goWhichWay = randomlyChooseDaughterCell();
					alreadyWentOtherWay = false;
				}
	
				if (goWhichWay == GO_LEFT)
				{
					daughterCellChromosomeModel = new MeiosisChromosomeModel(this, anOrganismChromosome,
																			 MeiosisChromosomeModel.LEFT_DAUGHTER_CELL_CHROMOSOME,
																			 originalCellChromosomeModel,
																		     MeiosisChromosomeModel.UNSPECIFIED_STRAND,
																			 enclosingViewRectangle,
																			 visiblePairNumber, color);
	
					leftDaughterCellChromosomeModels.addElement(daughterCellChromosomeModel);
					if (alreadyWentOtherWay)
					{
						goWhichWay = GO_EITHER;
						alreadyWentOtherWay = false;
					}
					else
					{
						goWhichWay = GO_RIGHT;
						alreadyWentOtherWay = true;
					}

					// Left gamete chromosome models get chromosomes of left daughter cell
					topGameteChromosomeModel = new MeiosisChromosomeModel(this, anOrganismChromosome,
																		  MeiosisChromosomeModel.TOP_LEFT_GAMETE_CHROMOSOME,
																		  daughterCellChromosomeModel,
																	      MeiosisChromosomeModel.FIRST_STRAND,
																		  enclosingViewRectangle,
																		  visiblePairNumber, color);
					topLeftGameteChromosomeModels.addElement(topGameteChromosomeModel);
		
					bottomGameteChromosomeModel = new MeiosisChromosomeModel(this, anOrganismChromosome,
																			 MeiosisChromosomeModel.BOTTOM_LEFT_GAMETE_CHROMOSOME,
																			 daughterCellChromosomeModel,
																	         MeiosisChromosomeModel.SECOND_STRAND,
																			 enclosingViewRectangle,
																			 visiblePairNumber, color);
					bottomLeftGameteChromosomeModels.addElement(bottomGameteChromosomeModel);
				}
				else if (goWhichWay == GO_RIGHT)
				{
					daughterCellChromosomeModel = new MeiosisChromosomeModel(this, anOrganismChromosome,
																			 MeiosisChromosomeModel.RIGHT_DAUGHTER_CELL_CHROMOSOME,
																			 originalCellChromosomeModel,
																		     MeiosisChromosomeModel.UNSPECIFIED_STRAND,
																			 enclosingViewRectangle,
																			 visiblePairNumber, color);
	
					rightDaughterCellChromosomeModels.addElement(daughterCellChromosomeModel);
					if (alreadyWentOtherWay)
					{
						goWhichWay = GO_EITHER;
						alreadyWentOtherWay = false;
					}
					else
					{
						goWhichWay = GO_LEFT;
						alreadyWentOtherWay = true;
					}
	
					// Right gamete chromosome models get chromosomes of right daughter cell
					topGameteChromosomeModel = new MeiosisChromosomeModel(this, anOrganismChromosome,
																		  MeiosisChromosomeModel.TOP_RIGHT_GAMETE_CHROMOSOME,
																		  daughterCellChromosomeModel,
																	      MeiosisChromosomeModel.FIRST_STRAND,
																		  enclosingViewRectangle,
																		  visiblePairNumber, color);
					topRightGameteChromosomeModels.addElement(topGameteChromosomeModel);
	
					bottomGameteChromosomeModel = new MeiosisChromosomeModel(this, anOrganismChromosome,
																			 MeiosisChromosomeModel.BOTTOM_RIGHT_GAMETE_CHROMOSOME,
																			 daughterCellChromosomeModel,
																			 MeiosisChromosomeModel.SECOND_STRAND,
																			 enclosingViewRectangle,
																			 visiblePairNumber, color);
					bottomRightGameteChromosomeModels.addElement(bottomGameteChromosomeModel);
				}
	
				// Switch to sex chromosomes if we've run out of non-sex chromosomes
				// and we haven't started sex chromosomes yet.
				if (eOrganismChromosomes.hasMoreElements() == false &&
					doingSexChromosomes == false)
				{
					eOrganismChromosomes = organism.getSexChromosomes();
					doingSexChromosomes = true;
				}
			}
		}
		
		
		

		// Notify listeners that the organism has changed
		changes.firePropertyChange(UIProp.ORGANISM,oldOrganism,organism);

		// DON'T notify listeners that the selected and moved gametes have
		// changed.  Listeners must handle the ORGANISM event and understand
		// that ORGANISM is a superset of SELECTED_GAMETE and MOVED_GAMETE.
	}

	/**
	 * Decrement the current step of the meiosis, returning the new current step value.
	 *
	 * @return		int - new current step value of meiosis
	**/
	public int decrementStep()
	{
		int oldCurrentStep = currentStep;

		if (currentStep == 0)
		{
			// Return immediately if currentStep already 0
			return 0;
		}
		else if (currentStep < 0)
		{
			// Set currentStep to 0
		    currentStep = 0;
		}
		else if (currentStep > 100)
		{
			// Set currentStep to 100
			currentStep = 100;
		}
		else
		{
			// Normal case (1 to 100) - decrement currentStep
			currentStep -= 1;
		}

		if (currentStep != oldCurrentStep)
		{
			// Notify listeners that meiosis started or stepped, but not both
			if (!notifiedMeiosisStarted)
			{
				notifiedMeiosisStarted = true;
				changes.firePropertyChange(UIProp.MEIOSIS_STARTED,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
			else
			{
				changes.firePropertyChange(UIProp.MEIOSIS_STEP,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
		}

		return currentStep;
	}

	/**
	 * Increment the current step of the meiosis
	 *
	 * @return		int - new current step value of meiosis
	**/
	public int incrementStep()
	{
		int oldCurrentStep = currentStep;

		if (currentStep == 100)
		{
			// Return immediately if currentStep already 100
			return 100;
		}
		else if (currentStep < 0)
		{
			// Set currentStep to 0
			currentStep = 0;
		}
		else if (currentStep > 100)
		{
			// Set currentStep to 100
			currentStep = 100;
		}
		else
		{
			// Normal case (0 to 99) - increment currentStep
			currentStep += 1;
		}

		if (currentStep != oldCurrentStep)
		{
			// Notify listeners that meiosis started
			if (!notifiedMeiosisStarted)
			{
				notifiedMeiosisStarted = true;
				changes.firePropertyChange(UIProp.MEIOSIS_STARTED,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
			else
			{
				changes.firePropertyChange(UIProp.MEIOSIS_STEP,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
		}
	
		return currentStep;
	}

	/**
	 * Get the current step of the meiosis.
	 *
	 * @return		int - new current step
	**/
	public int getStep()
	{
		return currentStep;
	}

	/**
	 * Set the current step of the meiosis.
	 *
	 * @param		aStep int - new proposed current step
	 * @return		int - new current step
	**/
	public int setStep(int aStep)
	{
		int oldCurrentStep = currentStep;

		if (currentStep == aStep)
		{
			// Return immediately, no change
			return currentStep;
		}
		else if (aStep < 0)
		{
			// Set currentStep to 0
			currentStep = 0;
		}
		else if (aStep > 100)
		{
			// Set currentStep to 100
			currentStep = 100;
		}
		else
		{
			// Normal case (0 to 99)
			currentStep = aStep;
		}
	
		if (currentStep != oldCurrentStep)
		{
			// Notify listeners that meiosis started
			if (!notifiedMeiosisStarted)
			{
				notifiedMeiosisStarted = true;
				changes.firePropertyChange(UIProp.MEIOSIS_STARTED,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
			else
			{
				changes.firePropertyChange(UIProp.MEIOSIS_STEP,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
		}

		return currentStep;
	}

	/**
	 * Get the phase of the meiosis at the given step.<p>
	 *
	 * @param		aStep int - the step of meiosis
	 * @return		int - current phase
	**/
	public int getPhase(int aStep)
	{
		if (aStep <= 0)
		{
			return PRE_MEIOSIS;
		}
		else if (aStep < 11)
		{
			return INTERPHASE;
		}
		else if (aStep < 21)
		{
			return PROPHASE_1;
		}
		else if (aStep < 31)
		{
			return METAPHASE_1;
		}
		else if (aStep < 41)
		{
			return ANAPHASE_1;
		}
		else if (aStep < 51)
		{
			return TELOPHASE_1;
		}
		else if (aStep < 61)
		{
			return PROPHASE_2;
		}
		else if (aStep < 71)
		{
			return METAPHASE_2;
		}
		else if (aStep < 81)
		{
			return ANAPHASE_2;
		}
		else if (aStep < 91)
		{
			return TELOPHASE_2;
		}
		else if (aStep < 100)
		{
			return CYTOKINESIS;
		}
		else
			return POST_MEIOSIS;
	}

	/**
	 * Return an enumeration of the appropriate gamete chromosome models.<p>
	 *
	 * @param   aGamete int - the gamete (TOP_LEFT_GAMETE, etc.)
	 * @return  Enumeration - an enumeration over the top left gamete chromosome models, null if illegal gamete value
	**/
	public Enumeration getGameteChromosomeModels(int aGamete)
	{
		switch (aGamete)
		{
			case TOP_LEFT_GAMETE:
				return topLeftGameteChromosomeModels.elements();
			case BOTTOM_LEFT_GAMETE:
				return bottomLeftGameteChromosomeModels.elements();
			case TOP_RIGHT_GAMETE:
				return topRightGameteChromosomeModels.elements();
			case BOTTOM_RIGHT_GAMETE:
				return bottomRightGameteChromosomeModels.elements();
		}

		return null;
	}

    /**
	 * Return an enumeration of the top left gamete chromosome models.<p>
	 *
	 * @return  Enumeration - an enumeration over the top left gamete chromosome models.
	**/
	public Enumeration getTopLeftGameteChromosomeModels()
	{
		return topLeftGameteChromosomeModels.elements();
	}

    /**
	 * Return an enumeration of the bottom left gamete chromosome models.<p>
	 *
	 * @return  Enumeration - an enumeration over the bottom left gamete chromosome models.
	**/
	public Enumeration getBottomLeftGameteChromosomeModels()
	{
		return bottomLeftGameteChromosomeModels.elements();
	}

    /**
	 * Return an enumeration of the top right gamete chromosome models.<p>
	 *
	 * @return  Enumeration - an enumeration over the top right gamete chromosome models.
	**/
	public Enumeration getTopRightGameteChromosomeModels()
	{
		return topRightGameteChromosomeModels.elements();
	}

    /**
	 * Return an enumeration of the bottom right gamete chromosome models.<p>
	 *
	 * @return  Enumeration - an enumeration over the bottom right gamete chromosome models.
	**/
	public Enumeration getBottomRightGameteChromosomeModels()
	{
		return bottomRightGameteChromosomeModels.elements();
	}

	/**
	 * Get vector of the appropriate enumerations of chromosome models in this meiosis model.<p>
	 *
	 * In other words, if the current step is 0 to 30, a Vector holding a single Enumeration
	 * over the chromosome models of the original cell will be returned.<p>
	 *
	 * If the current step is 31 to 70, a Vector holding two Enumerations will be returned with
	 * the first Enumeration over the chromosome models of the left daughter cell and the second
	 * Enumeration over the chromosome models of the right daughter cell.v<p>
	 *
	 * If the current step is 71 to 100, a Vector holding four Enumerations will be returned with
	 * the first Enumeration over the chromosome models of the top left gamete, second over the
	 * bottom left gamete, third over the top right gamete and fourth over the bottom right gamete.<p>
	 *
	 * @return		Enumeration - an enumeration over all chromosomes models in this meiosis model
	**/
	public Vector getChromosomeModelEnumerations()
	{
		Vector enumerations = new Vector();

		Enumeration anEnumeration;

		if (currentStep < 31)
		{
			// Pre-Anaphase 1 - Return enumeration of chromosome models for single original cell
			anEnumeration = origCellChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
		}
		else if (currentStep < 71)
		{
			// Pre-Anaphase 2 - Return enumerations for the chromosomes models of 2 daughter cells
			anEnumeration = leftDaughterCellChromosomeModels.elements();
			enumerations.addElement(anEnumeration);

			anEnumeration = rightDaughterCellChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
		}
		else if (currentStep < 100)
		{
			// Anaphase 2 and beyond - Return enumerations for the chromosomes models of 4 gametes
			anEnumeration = topLeftGameteChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
			
			anEnumeration = bottomLeftGameteChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
		
			anEnumeration = topRightGameteChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
			
			anEnumeration = bottomRightGameteChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
		}
		else if (currentStep == 100)
		{
			// Last step
			// Do not return enumerations for the moved gamete if current step is 100.
			if (movedGamete != TOP_LEFT_GAMETE)
			{
				anEnumeration = topLeftGameteChromosomeModels.elements();
				enumerations.addElement(anEnumeration);
			}

			if (movedGamete != BOTTOM_LEFT_GAMETE)
			{
				anEnumeration = bottomLeftGameteChromosomeModels.elements();
				enumerations.addElement(anEnumeration);
			}

			if (movedGamete != TOP_RIGHT_GAMETE)
			{
				anEnumeration = topRightGameteChromosomeModels.elements();
				enumerations.addElement(anEnumeration);
			}

			if (movedGamete != BOTTOM_RIGHT_GAMETE)
			{
				anEnumeration = bottomRightGameteChromosomeModels.elements();
				enumerations.addElement(anEnumeration);
			}
		}

		return enumerations;
	}

	/**
	 * Get CellArc and CellLine enumeration to use when drawing boundaries and
	 * spindles of cell(s).<p>
	 *
	 * @return		Vector of CellArc and CellLine objects.
	**/
	public Vector getCellBoundaries()
	{
		CellArc arc;
		CellLine line;
		SpindleLine spindleLine;

		Vector boundaries = new Vector();

		// Return immediately with an empty vector if organism null
		if (organism == null)
		{
			return boundaries;
		}

		int limitingViewDimension;
		if (enclosingViewRectangle.width > enclosingViewRectangle.height)
		{
			limitingViewDimension = enclosingViewRectangle.height;
		}
		else
		{
			limitingViewDimension = enclosingViewRectangle.width;
		}

		if (currentStep < 31)
		{
			// Just one big cell
			arc = new CellArc(2,2,limitingViewDimension-4,limitingViewDimension-4,0,360);
			boundaries.addElement(arc);
		}
		else if (currentStep < 51)
		{
			// y of center of daughter cells is constant during this portion of animation
			int yCenter = limitingViewDimension / 2;

			// x of center at start of animation (step 50)
			int xCenterStart = yCenter;
			int diameterCellStart = limitingViewDimension-4;

			// dimensions at end of animation (step 70)
			int diameterDaughterCellEnd = (limitingViewDimension / 2) - 4;
			int radiusDaughterCellEnd = diameterDaughterCellEnd / 2;
			int xLeftDaughterCellCenterEnd = 2 + radiusDaughterCellEnd;
			int xRightDaughterCellCenterEnd = 6 + (3 * radiusDaughterCellEnd);

			// Calculate centers of left and right daughter cells at current step
			// where separation is fully complete after 10 steps (at step 40)
			int xDeltaFromCenter;
			int angle;
			if (currentStep > 40)
			{
				xDeltaFromCenter = xCenterStart - xLeftDaughterCellCenterEnd;
				angle = 90 - ((90 * (currentStep-40))/10);
			}
			else
			{
				xDeltaFromCenter = ((xCenterStart - xLeftDaughterCellCenterEnd) * (currentStep-30))/10;
				angle = 90;
			}
			int xLeftCenter = xCenterStart - xDeltaFromCenter;
			int xRightCenter = xCenterStart + xDeltaFromCenter;

			// Calculate radius of daughter cells at current step
			int radius = xLeftCenter - 2;
			int diameter = 2 * radius;

			// Draw 2 arcs
			arc = new CellArc(xLeftCenter - radius,
							  yCenter - radius,
							  diameter, diameter,
							  angle, 360-(2*angle));
			boundaries.addElement(arc);

			arc = new CellArc(xRightCenter - radius,
							  yCenter - radius,
							  diameter, diameter,
							  180+angle, 360-(2*angle));
			boundaries.addElement(arc);

			// Draw 2 lines representing top and bottom of cell walls
			int xDelta = (int) (((float)radius) * Math.cos((((float)angle) * Math.PI) / 180.0));
			int yDelta = (int) (((float)radius) * Math.sin((((float)angle) * Math.PI) / 180.0));

			line = new CellLine(xLeftCenter+xDelta,yCenter-yDelta,xRightCenter-xDelta,yCenter-yDelta);
			boundaries.addElement(line);

			line = new CellLine(xLeftCenter+xDelta,yCenter+yDelta,xRightCenter-xDelta,yCenter+yDelta);
			boundaries.addElement(line);

			// Draw spindles
			if (currentStep < 41)
			{
				Enumeration anEnumeration;
				MeiosisChromosomeModel aChromosomeModel;
				int xSpindle = xLeftCenter - ((2*radius)/3);
	
				anEnumeration = leftDaughterCellChromosomeModels.elements();
				while(anEnumeration.hasMoreElements())
				{
					aChromosomeModel = (MeiosisChromosomeModel) anEnumeration.nextElement();
					if (aChromosomeModel.isVisible())
					{
						spindleLine = new SpindleLine(xSpindle,yCenter,
													  (int) aChromosomeModel.getXCentromereAtStep(currentStep),
													  (int) aChromosomeModel.getYCentromereAtStep(currentStep));
						boundaries.addElement(spindleLine);
					}
				}
	
				xSpindle = xRightCenter + ((2*radius)/3);
				anEnumeration = rightDaughterCellChromosomeModels.elements();
				while(anEnumeration.hasMoreElements())
				{
					aChromosomeModel = (MeiosisChromosomeModel) anEnumeration.nextElement();
					if (aChromosomeModel.isVisible())
					{
						spindleLine = new SpindleLine(xSpindle,yCenter,
													  (int) aChromosomeModel.getXCentromereAtStep(currentStep),
													  (int) aChromosomeModel.getYCentromereAtStep(currentStep));
						boundaries.addElement(spindleLine);
					}
				}
			}
		}
		else if (currentStep < 71)
		{
			// Two daughter cells
			int daughterCellDiameter = (limitingViewDimension / 2) - 4;
			int daughterCellRadius = daughterCellDiameter / 2;
			int xLeftDaughterCellCenter = 2 + daughterCellRadius;
			int xRightDaughterCellCenter = 6 + (3 * daughterCellRadius);
			int yDaughterCellCenter = 4 + daughterCellDiameter;

			arc = new CellArc(xLeftDaughterCellCenter - daughterCellRadius,
							  yDaughterCellCenter - daughterCellRadius,
							  daughterCellDiameter, daughterCellDiameter,
							  0, 360);
			boundaries.addElement(arc);

			arc = new CellArc(xRightDaughterCellCenter - daughterCellRadius,
							  yDaughterCellCenter - daughterCellRadius,
							  daughterCellDiameter, daughterCellDiameter,
							  0, 360);
			boundaries.addElement(arc);
		}
		else if (currentStep < 91)
		{
			// 4 gametes pulling apart from 2 daughter cells

			// x of center of gametes is constant during this portion of animation
			int gameteDiameter = (limitingViewDimension / 2) - 4;
			int gameteRadius = gameteDiameter / 2;
			int xLeftGameteCenter = 2 + gameteRadius;
			int xRightGameteCenter = 6 + (3 * gameteRadius);
			int yGameteCenterStart = 4 + gameteDiameter;
			int yTopGameteCenterEnd = 2 + gameteRadius;
			int yBottomGameteCenterEnd = 6 + (3 * gameteRadius);

			int yTopCenter, yBottomCenter, yDelta, xDelta;
			int topStartAngle, bottomStartAngle, spanAngle;
			int xLeftGameteLeftLine, xLeftGameteRightLine, xRightGameteLeftLine, xRightGameteRightLine;
			int yTopLine, yBottomLine;

			if (currentStep < 81)
			{
				yDelta = ((yBottomGameteCenterEnd - yGameteCenterStart) * (currentStep-70))/10;
				yTopCenter = yGameteCenterStart - yDelta;
				yBottomCenter = yGameteCenterStart + yDelta;
				topStartAngle = 0;
				bottomStartAngle = 180;
				spanAngle = 180;

				xLeftGameteLeftLine = 2;
				xLeftGameteRightLine = 2 + gameteDiameter;
				xRightGameteLeftLine = 6 + gameteDiameter;
				xRightGameteRightLine = 6 + (2*gameteDiameter);
				yTopLine = yTopCenter;
				yBottomLine = yBottomCenter;
			}
			else
			{
				yTopCenter = yTopGameteCenterEnd;
				yBottomCenter = yBottomGameteCenterEnd;

				int deltaAngle = (90 * (currentStep-80))/10;
				topStartAngle = 0 - deltaAngle;
				bottomStartAngle = 180 - deltaAngle;
				spanAngle = 180 + (2*deltaAngle);

				xDelta = (int) (((float)gameteRadius) * Math.cos((((float)deltaAngle) * Math.PI)/180.0));
				yDelta = (int) (((float)gameteRadius) * Math.sin((((float)deltaAngle) * Math.PI)/180.0));

				xLeftGameteLeftLine = xLeftGameteCenter - xDelta;
				xLeftGameteRightLine = xLeftGameteCenter + xDelta;
				xRightGameteLeftLine = xRightGameteCenter - xDelta;
				xRightGameteRightLine = xRightGameteCenter + xDelta;
				yTopLine = yTopCenter + yDelta;
				yBottomLine = yBottomCenter - yDelta;
			}

			// create 4 arcs
			arc = new CellArc(xLeftGameteCenter - gameteRadius,
							  yTopCenter - gameteRadius,
							  gameteDiameter, gameteDiameter,
							  topStartAngle, spanAngle);
			boundaries.addElement(arc);

			arc = new CellArc(xLeftGameteCenter - gameteRadius,
							  yBottomCenter - gameteRadius,
							  gameteDiameter, gameteDiameter,
							  bottomStartAngle, spanAngle);
			boundaries.addElement(arc);

			arc = new CellArc(xRightGameteCenter - gameteRadius,
							  yTopCenter - gameteRadius,
							  gameteDiameter, gameteDiameter,
							  topStartAngle, spanAngle);
			boundaries.addElement(arc);

			arc = new CellArc(xRightGameteCenter - gameteRadius,
							  yBottomCenter - gameteRadius,
							  gameteDiameter, gameteDiameter,
							  bottomStartAngle, spanAngle);
			boundaries.addElement(arc);

			// create 4 lines
			line = new CellLine(xLeftGameteLeftLine, yTopLine,
								xLeftGameteLeftLine, yBottomLine);
			boundaries.addElement(line);

			line = new CellLine(xLeftGameteRightLine, yTopLine,
								xLeftGameteRightLine, yBottomLine);
			boundaries.addElement(line);

			line = new CellLine(xRightGameteLeftLine, yTopLine,
								xRightGameteLeftLine, yBottomLine);
			boundaries.addElement(line);

			line = new CellLine(xRightGameteRightLine, yTopLine,
								xRightGameteRightLine, yBottomLine);
			boundaries.addElement(line);

			// Draw spindles
			if (currentStep < 81)
			{
				Enumeration anEnumeration;
				MeiosisChromosomeModel aChromosomeModel;
				int yTopSpindle = yTopCenter - ((2*gameteRadius)/3);
				int yBottomSpindle = yBottomCenter + ((2*gameteRadius)/3);
	
				anEnumeration = topLeftGameteChromosomeModels.elements();
				while(anEnumeration.hasMoreElements())
				{
					aChromosomeModel = (MeiosisChromosomeModel) anEnumeration.nextElement();
					if (aChromosomeModel.isVisible())
					{
						spindleLine = new SpindleLine(xLeftGameteCenter,yTopSpindle,
													  (int) aChromosomeModel.getXCentromereAtStep(currentStep),
													  (int) aChromosomeModel.getYCentromereAtStep(currentStep));
						boundaries.addElement(spindleLine);
					}
				}
	
				anEnumeration = topRightGameteChromosomeModels.elements();
				while(anEnumeration.hasMoreElements())
				{
					aChromosomeModel = (MeiosisChromosomeModel) anEnumeration.nextElement();
					if (aChromosomeModel.isVisible())
					{
						spindleLine = new SpindleLine(xRightGameteCenter,yTopSpindle,
													  (int) aChromosomeModel.getXCentromereAtStep(currentStep),
													  (int) aChromosomeModel.getYCentromereAtStep(currentStep));
						boundaries.addElement(spindleLine);
					}
				}

				anEnumeration = bottomLeftGameteChromosomeModels.elements();
				while(anEnumeration.hasMoreElements())
				{
					aChromosomeModel = (MeiosisChromosomeModel) anEnumeration.nextElement();
					if (aChromosomeModel.isVisible())
					{
						spindleLine = new SpindleLine(xLeftGameteCenter,yBottomSpindle,
													  (int) aChromosomeModel.getXCentromereAtStep(currentStep),
													  (int) aChromosomeModel.getYCentromereAtStep(currentStep));
						boundaries.addElement(spindleLine);
					}
				}
	
				anEnumeration = bottomRightGameteChromosomeModels.elements();
				while(anEnumeration.hasMoreElements())
				{
					aChromosomeModel = (MeiosisChromosomeModel) anEnumeration.nextElement();
					if (aChromosomeModel.isVisible())
					{
						spindleLine = new SpindleLine(xRightGameteCenter,yBottomSpindle,
													  (int) aChromosomeModel.getXCentromereAtStep(currentStep),
													  (int) aChromosomeModel.getYCentromereAtStep(currentStep));
						boundaries.addElement(spindleLine);
					}
				}
			}
		}
		else if (currentStep < 101)
		{
			// 4 gametes
			int gameteDiameter = (limitingViewDimension / 2) - 4;
			int gameteRadius = gameteDiameter / 2;
			int xLeftGameteCenter = 2 + gameteRadius;
			int xRightGameteCenter = 6 + (3 * gameteRadius);
			int yTopGameteCenter = 2 + gameteRadius;
			int yBottomGameteCenter = 6 + (3 * gameteRadius);

			if (movedGamete != TOP_LEFT_GAMETE || currentStep != 100)
			{
				arc = new CellArc(xLeftGameteCenter - gameteRadius,
								  yTopGameteCenter - gameteRadius,
								  gameteDiameter, gameteDiameter,
								  0, 360);
				boundaries.addElement(arc);

				if (selectedGamete == TOP_LEFT_GAMETE)
				{
					arc = new CellArc(xLeftGameteCenter - gameteRadius - 1,
									  yTopGameteCenter - gameteRadius - 1,
									  gameteDiameter + 2, gameteDiameter + 2,
									  0, 360);
					boundaries.addElement(arc);
				}
			}

			if (movedGamete != BOTTOM_LEFT_GAMETE || currentStep != 100)
			{
				arc = new CellArc(xLeftGameteCenter - gameteRadius,
								  yBottomGameteCenter - gameteRadius,
								  gameteDiameter, gameteDiameter,
								  0, 360);
				boundaries.addElement(arc);

				if (selectedGamete  == BOTTOM_LEFT_GAMETE)
				{
					arc = new CellArc(xLeftGameteCenter - gameteRadius - 1,
									  yBottomGameteCenter - gameteRadius - 1,
									  gameteDiameter + 2, gameteDiameter + 2,
									  0, 360);
					boundaries.addElement(arc);
				}
			}

			if (movedGamete != TOP_RIGHT_GAMETE || currentStep != 100)
			{
				arc = new CellArc(xRightGameteCenter - gameteRadius,
								  yTopGameteCenter - gameteRadius,
								  gameteDiameter, gameteDiameter,
								  0, 360);
				boundaries.addElement(arc);

				if (selectedGamete == TOP_RIGHT_GAMETE)
				{
					arc = new CellArc(xRightGameteCenter - gameteRadius - 1,
									  yTopGameteCenter - gameteRadius - 1,
									  gameteDiameter + 2, gameteDiameter + 2,
									  0, 360);
					boundaries.addElement(arc);
				}
			}

			if (movedGamete != BOTTOM_RIGHT_GAMETE || currentStep != 100)
			{
				arc = new CellArc(xRightGameteCenter - gameteRadius,
								  yBottomGameteCenter - gameteRadius,
								  gameteDiameter, gameteDiameter,
								  0, 360);
				boundaries.addElement(arc);

				if (selectedGamete == BOTTOM_RIGHT_GAMETE)
				{
					arc = new CellArc(xRightGameteCenter - gameteRadius - 1,
									  yBottomGameteCenter - gameteRadius - 1,
									  gameteDiameter + 2, gameteDiameter + 2,
									  0, 360);
					boundaries.addElement(arc);
				}
			}
		}

		return boundaries;
	}

	/**
	 * Set the rectangle within which the meiosis model must confine itself and its
	 * chromosomes.  Buttons should be outside of this area whenever possible.  This
	 * method should be called when the enclosing view is resized, giving the model
	 * data a chance to scale itself appropriately.
	 *
	 * @param	aRectangle Rectangle - the enclosing rectangle
	**/
	public void setEnclosingViewRectangle(Rectangle aRectangle)
	{
		// Verify that input rectangle is okay and different from current one
		if (aRectangle == null)
		{
			return;
		}
		else if (aRectangle.width == enclosingViewRectangle.width &&
				 aRectangle.height == enclosingViewRectangle.height)
		{
			return;
		}

		// Update our enclosing view rectangle
		enclosingViewRectangle = aRectangle;

		// Notify all the chromosome models, giving them a chance to resize their data
		Vector chromosomeModels;
		MeiosisChromosomeModel chromosomeModel;
		Enumeration eChromosomeModels;

		int i;
		for (i=0;i<7;i++)
		{
			chromosomeModels = null;

			switch(i)
			{
				case 0: chromosomeModels = origCellChromosomeModels;
						break;
				case 1: chromosomeModels = leftDaughterCellChromosomeModels;
						break;
				case 2: chromosomeModels = rightDaughterCellChromosomeModels;
						break;
				case 3: chromosomeModels = topLeftGameteChromosomeModels;
						break;
				case 4: chromosomeModels = bottomLeftGameteChromosomeModels;
						break;
				case 5: chromosomeModels = topRightGameteChromosomeModels;
						break;
				case 6: chromosomeModels = bottomRightGameteChromosomeModels;
						break;
			}
			
			if (chromosomeModels != null)
			{
				eChromosomeModels = chromosomeModels.elements();
				while (eChromosomeModels.hasMoreElements())
				{
					chromosomeModel = (MeiosisChromosomeModel) eChromosomeModels.nextElement();
					chromosomeModel.setEnclosingViewRectangle(aRectangle);
				}
			}
		}
	}

	/**
	 * Returns the number of chromosome pairs to be aligned vertically in a cell
	 * during Metaphase 1 and Metaphase 2 during meiosis.  In most cases, this is
	 * the number of chromosome pairs.
	 *
	 * @return		numberOfVisibleChromosomePairs int - number of chromosome pairs
	**/
	public int getNumberOfVisibleChromosomePairs()
	{
		int numVisibleChromosomes = 0;
		OrganismChromosome aChromosome;

		// Count visible non-sex chromosomes
		Enumeration eOrganismChromosomes = organism.getNonSexChromosomes();
		while (eOrganismChromosomes.hasMoreElements())
		{
			aChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
			if (aChromosome.isVisible() == true)
			{
				numVisibleChromosomes++;
			}
		}

		// Count visible sex chromosomes
		eOrganismChromosomes = organism.getSexChromosomes();
		while (eOrganismChromosomes.hasMoreElements())
		{
			aChromosome = (OrganismChromosome) eOrganismChromosomes.nextElement();
			if (aChromosome.isVisible() == true)
			{
				numVisibleChromosomes++;
			}
		}

		// For now, just return half the number of visible chromosomes.
		// Add one and divide by 2 to handle the **rare** case where
		// there are an odd number of chromosomes.
		return (numVisibleChromosomes + 1) / 2;
	}

	/**
	 * Get the chromosome model for the chromosome that can crossover with
	 * the given chromosome model chromosome.  If there is no crossover chromosome,
	 * usually because the given chromosome is an X or Y and the other sex
	 * chromosome is the opposite, then null is returned.<p>
	 *
	 * @param       aMeiosisChromosomeModel MeiosisChromosomeModel - the given meiosis chromosome model
	 * @return      MeiosisChromosomeModel - the crossover chromosome model
	**/
	public MeiosisChromosomeModel getCrossoverChromosomeModel(MeiosisChromosomeModel aMeiosisChromosomeModel)
	{
		if (aMeiosisChromosomeModel == null ||
			(aMeiosisChromosomeModel.getChromosomeModelType() != MeiosisChromosomeModel.LEFT_DAUGHTER_CELL_CHROMOSOME &&
			 aMeiosisChromosomeModel.getChromosomeModelType() != MeiosisChromosomeModel.RIGHT_DAUGHTER_CELL_CHROMOSOME))
		{
			throw new IllegalArgumentException("input aMeiosisChromosomeModel null or not a daughter cell chromosome model");
		}

		// We'll want to traverse the left or right daughter cell chromosome models,
		// depending on whether the given chromosome model is left or right.
		Enumeration eDaughterCellChromosomeModels;
		if (aMeiosisChromosomeModel.getChromosomeModelType() == MeiosisChromosomeModel.LEFT_DAUGHTER_CELL_CHROMOSOME)
		{
			eDaughterCellChromosomeModels = rightDaughterCellChromosomeModels.elements();
		}
		else
		{
			eDaughterCellChromosomeModels = leftDaughterCellChromosomeModels.elements();
		}

		// Traverse chromosome models to find the one of the same number type.
		// Note that this means we won't find one if we have an X or Y chromosome
		// and there are no chromosomes of the same type in the other daughter cell.
		// That's correct behavior.
		int numberType = aMeiosisChromosomeModel.getNumberType();
		MeiosisChromosomeModel mcm;
		while (eDaughterCellChromosomeModels.hasMoreElements())
		{
			mcm = (MeiosisChromosomeModel) eDaughterCellChromosomeModels.nextElement();
			if (mcm.getNumberType() == numberType)
			{
				// Found a match, return it
				return mcm;
			}
		}

		// Never found a match, return null
		return null;
	}

	/**
	 * Returns whether the current step of meiosis represents a step in which
	 * chromosome are replicated.  In other words, should second strands be drawn?
	 *
	 * @return		boolean - are the chromosomes replicated at the current meiosis step?
	**/
	public boolean isReplicated()
	{
		// Replicated from Prophase 1 through Metaphase 2
		if (currentStep > 10 && currentStep < 71)
		{
			return true;
		}

		// Else not replicated
		return false;
	}

	/**
	 * Randomly choose a daughter cell.
	 *
	 * @return		int - GO_LEFT or GO_RIGHT
	**/
	private int randomlyChooseDaughterCell()
	{
		double nextDouble = random.nextDouble();
		if (nextDouble > 0.5)
		{
			return GO_LEFT;
		}

		return GO_RIGHT;
	}

	/**
	 * Switch horizontal alignment of 2 chromosomes at step 33 and beyond in meiosis.
	 *
	 * @param	iChromosome int - number of chromosome (1-based number)
	**/
	public void switchHorizontalAlignment(int iChromosome)
	{
		// Get left and right chromosome models to switch
		MeiosisChromosomeModel oldTopLeftGameteChromosomeModel;
		MeiosisChromosomeModel oldBottomLeftGameteChromosomeModel;
		MeiosisChromosomeModel oldTopRightGameteChromosomeModel;
		MeiosisChromosomeModel oldBottomRightGameteChromosomeModel;

		// Switch daughter cell chromosome models
		MeiosisChromosomeModel oldLeftChromosomeModel = (MeiosisChromosomeModel) leftDaughterCellChromosomeModels.elementAt(iChromosome-1);
		MeiosisChromosomeModel oldRightChromosomeModel = (MeiosisChromosomeModel) rightDaughterCellChromosomeModels.elementAt(iChromosome-1);
		MeiosisChromosomeModel newLeftChromosomeModel = oldRightChromosomeModel;
		MeiosisChromosomeModel newRightChromosomeModel = oldLeftChromosomeModel;

		leftDaughterCellChromosomeModels.setElementAt(newLeftChromosomeModel,iChromosome-1);
		newLeftChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.LEFT_DAUGHTER_CELL_CHROMOSOME);
		rightDaughterCellChromosomeModels.setElementAt(newRightChromosomeModel,iChromosome-1);
		newRightChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.RIGHT_DAUGHTER_CELL_CHROMOSOME);

		// Switch gamete cell chromosome models - right to left, left to right, ...
        oldTopLeftGameteChromosomeModel = (MeiosisChromosomeModel) topLeftGameteChromosomeModels.elementAt(iChromosome-1);
		oldBottomLeftGameteChromosomeModel = (MeiosisChromosomeModel) bottomLeftGameteChromosomeModels.elementAt(iChromosome-1);
		oldTopRightGameteChromosomeModel = (MeiosisChromosomeModel) topRightGameteChromosomeModels.elementAt(iChromosome-1);
		oldBottomRightGameteChromosomeModel = (MeiosisChromosomeModel) bottomRightGameteChromosomeModels.elementAt(iChromosome-1);

		oldTopLeftGameteChromosomeModel.setPreviousMeiosisChromosomeModel(newRightChromosomeModel);
		oldTopLeftGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.TOP_RIGHT_GAMETE_CHROMOSOME);

		oldBottomLeftGameteChromosomeModel.setPreviousMeiosisChromosomeModel(newRightChromosomeModel);
		oldBottomLeftGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.BOTTOM_RIGHT_GAMETE_CHROMOSOME);

		oldTopRightGameteChromosomeModel.setPreviousMeiosisChromosomeModel(newLeftChromosomeModel);
		oldTopRightGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.TOP_LEFT_GAMETE_CHROMOSOME);

		oldBottomRightGameteChromosomeModel.setPreviousMeiosisChromosomeModel(newLeftChromosomeModel);
		oldBottomRightGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.BOTTOM_LEFT_GAMETE_CHROMOSOME);

		topLeftGameteChromosomeModels.setElementAt(oldTopRightGameteChromosomeModel,iChromosome-1);
		bottomLeftGameteChromosomeModels.setElementAt(oldBottomRightGameteChromosomeModel,iChromosome-1);
		topRightGameteChromosomeModels.setElementAt(oldTopLeftGameteChromosomeModel,iChromosome-1);
		bottomRightGameteChromosomeModels.setElementAt(oldBottomLeftGameteChromosomeModel,iChromosome-1);
	}

	/**
	 * Switch vertical alignment of 2 chromosomes at step 73 and beyond in meiosis.
	 *
	 * @param	iChromosome int - number of chromosome (1-based number)
	**/
	public void switchVerticalAlignment(int iChromosome)
	{
		int numberOfVisiblePairs = getNumberOfVisibleChromosomePairs();
		int iChromosomeToSwitch;

		// Determine if we're switching a pair on the right or left
		if (iChromosome <= numberOfVisiblePairs)
		{
			// Switching a pair of chromosomes on the left
			iChromosomeToSwitch = iChromosome;
			MeiosisChromosomeModel oldTopLeftGameteChromosomeModel;
			MeiosisChromosomeModel oldBottomLeftGameteChromosomeModel;

			// Switch left and right gamete cell chromosome models - top to bottom
			oldTopLeftGameteChromosomeModel = (MeiosisChromosomeModel) topLeftGameteChromosomeModels.elementAt(iChromosomeToSwitch-1);
			oldBottomLeftGameteChromosomeModel = (MeiosisChromosomeModel) bottomLeftGameteChromosomeModels.elementAt(iChromosomeToSwitch-1);

			topLeftGameteChromosomeModels.setElementAt(oldBottomLeftGameteChromosomeModel,iChromosomeToSwitch-1);
			bottomLeftGameteChromosomeModels.setElementAt(oldTopLeftGameteChromosomeModel,iChromosomeToSwitch-1);
			
            oldBottomLeftGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.TOP_LEFT_GAMETE_CHROMOSOME);
            oldTopLeftGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.BOTTOM_LEFT_GAMETE_CHROMOSOME);
		}
		else
		{
			// Switching a pair of chromosomes on the right
			iChromosomeToSwitch = iChromosome - numberOfVisiblePairs;
			MeiosisChromosomeModel oldTopRightGameteChromosomeModel;
			MeiosisChromosomeModel oldBottomRightGameteChromosomeModel;

			// Switch left and right gamete cell chromosome models - top to bottom
			oldTopRightGameteChromosomeModel = (MeiosisChromosomeModel) topRightGameteChromosomeModels.elementAt(iChromosomeToSwitch-1);
			oldBottomRightGameteChromosomeModel = (MeiosisChromosomeModel) bottomRightGameteChromosomeModels.elementAt(iChromosomeToSwitch-1);

			topRightGameteChromosomeModels.setElementAt(oldBottomRightGameteChromosomeModel,iChromosomeToSwitch-1);
			bottomRightGameteChromosomeModels.setElementAt(oldTopRightGameteChromosomeModel,iChromosomeToSwitch-1);
			
            oldBottomRightGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.TOP_RIGHT_GAMETE_CHROMOSOME);
			oldTopRightGameteChromosomeModel.setMeiosisChromosomeModelType(MeiosisChromosomeModel.BOTTOM_RIGHT_GAMETE_CHROMOSOME);
		}
	}
	/**
	 * auto select the gamete
	 */
	 public void selectGamete(int num)
	 {
	 	Gamete newGamete = new Gamete();
	 	Gamete oldGamete = new Gamete();
	 	if (movedGamete != NO_GAMETE)
		{
			return;
		}
		int oldSelectedGamete = selectedGamete;
		if (oldSelectedGamete>=1)
			oldGamete = gametes[oldSelectedGamete-1];
			
	 	if (num%4==0)
	 	{
	 		selectedGamete = TOP_LEFT_GAMETE;
	 	
	 		newGamete = gametes[selectedGamete -1];
	 	}
	 	else if (num%4 == 1)
 		{
 			selectedGamete = TOP_RIGHT_GAMETE;
 			newGamete = gametes[selectedGamete -1];
 		}
 		else if (num%4 == 2)
 		{
 			selectedGamete = BOTTOM_RIGHT_GAMETE;
 			newGamete = gametes[selectedGamete -1];
 		}
 		else if (num%4 == 3)
 		{
 			selectedGamete  = BOTTOM_LEFT_GAMETE;
 			newGamete = gametes[selectedGamete -1];
 		}
 	
		
	 	if (selectedGamete!= oldSelectedGamete)
		{
			//changes.firePropertyChange(UIProp.SELECTED_GAMETE,new Integer(oldSelectedGamete),new Integer(selectedGamete));
			changes.firePropertyChange(UIProp.SELECTED_GAMETE,oldGamete,newGamete);
		}
	 }

	/**
	 * Select the gamete closest to the given x, y coordinates.<p>
	 *
	 * Usually this method is called from SmallMeiosisView.mousePressed()<p>
	 *
	 * @param		xMouse int - mouse x coordinate
	 * @param		yMouse int - mouse y coordinate
	 *
	**/
	public void selectGamete(int xMouse, int yMouse)
	{
		// Return immediately if we already have a moved gamete, as you cannot
		// select another gamete when one is already moved.
		Gamete newGamete = new Gamete();
		Gamete oldGamete = new Gamete();
		if (movedGamete != NO_GAMETE)
		{
			return;
		}

		int oldSelectedGamete = selectedGamete;
		if (oldSelectedGamete>=1)
			oldGamete = gametes[oldSelectedGamete - 1];

		int limitingViewDimension;
		if (enclosingViewRectangle.width > enclosingViewRectangle.height)
		{
			limitingViewDimension = enclosingViewRectangle.height;
		}
		else
		{
			limitingViewDimension = enclosingViewRectangle.width;
		}

		if (xMouse < (limitingViewDimension/2))
		{
			if (yMouse < (limitingViewDimension/2))
			{
				selectedGamete = TOP_LEFT_GAMETE;
				newGamete = gametes[selectedGamete - 1];
			}
			else
			{
				selectedGamete = BOTTOM_LEFT_GAMETE;
				newGamete = gametes[selectedGamete - 1];
			}
		}
		else
		{
			if (yMouse < (limitingViewDimension/2))
			{
				selectedGamete = TOP_RIGHT_GAMETE;
				newGamete = gametes[selectedGamete - 1];
			}
			else
			{
				selectedGamete = BOTTOM_RIGHT_GAMETE;
				newGamete = gametes[selectedGamete - 1];
			}
		}
		
		if (selectedGamete != oldSelectedGamete)
		{
			//changes.firePropertyChange(UIProp.SELECTED_GAMETE,new Integer(oldSelectedGamete),new Integer(selectedGamete));
			changes.firePropertyChange(UIProp.SELECTED_GAMETE,oldGamete,newGamete);
		}
	}

	/**
	 * Get the selected gamete
	**/
	public int getSelectedGamete()
	{
		return selectedGamete;
	}

	/**
	 * Get the moved gamete's chromosome models
	 *
	 * @return		Vector of meiosis chromosome models of moved gamete, null if no gamete moved
	**/
	public Vector getMovedGameteChromosomeModels()
	{
		Vector v = null;

		switch (movedGamete)
		{
			case TOP_LEFT_GAMETE:
				v = topLeftGameteChromosomeModels;
				break;
			case BOTTOM_LEFT_GAMETE:
				v = bottomLeftGameteChromosomeModels;
				break;
			case TOP_RIGHT_GAMETE:
				v = topRightGameteChromosomeModels;
				break;
			case BOTTOM_RIGHT_GAMETE:
				v = bottomRightGameteChromosomeModels;
				break;
			case NO_GAMETE:
			default:
				v = null;
				break;
		}

		return v;
	}

	/**
	 * Move the gamete currently selected into fertilization.<p>
	 *
	 * Usually this method is called when the user clicks on the
	 * move into fertilization button in the sex view.<p>
	 *
	 * If there is already a moved gamete, this call is ignored.
	 * The user must move the old moved gamete back into the
	 * meiosis view first.<p>
	 *
	 * If no gamete is selected, this call is ignored.  The
	 * user must select a gamete first.<p>
	**/
	public void moveGamete()
	{
		Gamete theMovedGamete = new Gamete();
		//Gamete newSelectedGamete = null;
		if (movedGamete != NO_GAMETE || selectedGamete == NO_GAMETE)
		{
			return;
		}
		
		// Make change
		movedGamete = selectedGamete;
	
		if (movedGamete>=1)
			theMovedGamete = gametes[movedGamete - 1];
		selectedGamete = NO_GAMETE;
		
		
		// Notify listeners
		//changes.firePropertyChange(UIProp.MOVED_GAMETE,new Integer(NO_GAMETE),new Integer(movedGamete));
		//changes.firePropertyChange(UIProp.SELECTED_GAMETE,new Integer(movedGamete),new Integer(selectedGamete));
		
		changes.firePropertyChange(UIProp.MOVED_GAMETE,new Gamete(),theMovedGamete);
		changes.firePropertyChange(UIProp.SELECTED_GAMETE,theMovedGamete,new Gamete());
	}

	/**
	 * Get the moved gamete
	**/
	public int getMovedGamete()
	{
		return movedGamete;
	}

	/**
	 * Unmove gamete.  In other words, retrieve the moved gamete from the
	 * fertilization view.  Leave the selected gamete alone.
	**/
	public void unmoveGamete()
	{
		Gamete theMovedGamete = new Gamete();
		Gamete theSelectedGamete = new Gamete();
		Gamete theOldMovedGamete = new Gamete();
		
		if (movedGamete == NO_GAMETE || selectedGamete != NO_GAMETE)
		{
			return;
		}

		// Make change
		selectedGamete = movedGamete;
		if (selectedGamete>=1)
			theSelectedGamete = gametes[selectedGamete - 1];
		int oldMovedGamete = movedGamete;
		if (oldMovedGamete>=1)
			theOldMovedGamete = gametes[oldMovedGamete  - 1];
		movedGamete = NO_GAMETE;
		if (movedGamete>=1)
			theMovedGamete = gametes[movedGamete - 1];
			
		
		// Notify listeners
		//changes.firePropertyChange(UIProp.MOVED_GAMETE,new Integer(oldMovedGamete),new Integer(movedGamete));
		//changes.firePropertyChange(UIProp.SELECTED_GAMETE,new Integer(NO_GAMETE),new Integer(selectedGamete));
		
		
		changes.firePropertyChange(UIProp.MOVED_GAMETE,theOldMovedGamete,theMovedGamete);
		
		changes.firePropertyChange(UIProp.SELECTED_GAMETE,new Gamete(),theSelectedGamete);
	}

	/**
	 * Add a property change listener for properties.
	 *
	 * @param	aListener PropertyChangeListener - a new listener
    **/
	public void addPropertyChangeListener(PropertyChangeListener aListener)
	{
		changes.addPropertyChangeListener(aListener);
	}

	/**
	 * Remove a property change listener for properties.
	 *
	 * @param	aListener PropertyChangeListener - a listener to remove
	**/
	public void removePropertyChangeListener(PropertyChangeListener aListener)
	{
		changes.removePropertyChangeListener(aListener);
	}

	/**
	 * Handle property change events
	 *
	 * @param	event PropertyChangeEvent - the property change event
	**/
	public void propertyChange(PropertyChangeEvent event)
	{
		String propertyName = event.getPropertyName();

		if (propertyName.equals(EngineProp.DELETED))
		{
			Object object = event.getSource();
			if (object instanceof Organism)
			{
				Organism deletedOrganism = (Organism) object;
				if (organism == deletedOrganism)
				{
					setOrganism(null);
				}
			}
		}
	}
	
	/**
	 *	After auto crossing over, alleles and colors of chromosomes are changed
	 *  
	 *  @param Branch array
	 */
	 private Vector [] arrBranchs = null;
	 public void setBranchs(Vector [] arr)
	 {
	 	  arrBranchs = null;
	 	  arrBranchs = arr;
	 }
	 
	 public Vector [] getBranchs()
	 {
	 	  return arrBranchs;
	 }
	 
	 /**
	  * Tell the meiosisModel that auto crossing over already happened
	  * @param  Boolean blnAutoCrossOver
	  */
	  private boolean blnAutoCrossOver = false;
	  public void setAutoCrossOverHappened(boolean bln)
	  {
	  	   blnAutoCrossOver = bln;
	  }
	  
	  public boolean isAutoCrossOverHappened()
	  {
	  	return blnAutoCrossOver;
	  }
	  
	  private boolean blnControl1edCrossOver = false;
	  public void setControlledCrossOverHappened(boolean bln)
	  {
	  	   blnControl1edCrossOver = bln;
	  }
	  
	  public boolean isControlledCrossOverHappened()
	  {
	  	return blnControl1edCrossOver;
	  }
	 
	 /**
	  * Tell the meiosisModel about the Gametes
	  * @param Array arrgamete
	  **/
	  
	  public void setGametes(Gamete [] arrgamete)
	  {
	  		gametes = arrgamete;
	  }
}

