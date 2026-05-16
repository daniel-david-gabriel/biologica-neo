//
// Class : FertilizationModel
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.5 $
// $Date: 2003/01/16 14:07:15 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.lang.String;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Random;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

/**
 * FertilizationModel contains the data describing the fertilization process
 * state for the fertilization of 2 gametes to form a single offspring organism.
 * It does not contain the code for drawing the animation as that is in the
 * view objects that show fertilization.<p>
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
 * <li> org.concord.biologica.ui.UIProp.FERTILIZATION_GAMETES - the fertilization gametes changed
 * <li> org.concord.biologica.ui.UIProp.FERTILIZATION_OFFSPRING_ORGANISM - fertilization offspring organism changed (became null or non-null)
 * <li> org.concord.biologica.ui.UIProp.FERTILIZATION_STARTED - the fertilization process has started (at step 2+)
 * <li> org.concord.biologica.ui.UIProp.FERTILIZATION_STEP - the fertilization step changed (0 to 35)
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_GAMETES
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_OFFSPRING_ORGANISM
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_STARTED
 * @see org.concord.biologica.ui.UIProp#FERTILIZATION_STEP
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.5 $ $Date: 2003/01/16 14:07:15 $
 * @author 		$Author: qliao $
**/
public final class FertilizationModel implements PropertyChangeListener
{
	/**
	 * Random number generator used in choosing locations, angles, etc.<p>
	**/
	private static Random random = new Random();

	/**
	 * Fertilization step values range from 0 to 100 where the phase
	 * for each step is:
	 *
	 *          step          phase
	 *          --------      -----------
	 *			0 			- start
	 *			1 to 25 	- 2 gametes combining
	 *			26 to 34 	- chromosomes coming together and doing dance
	 *			35			- finished
	**/
	private int currentStep = 0;

	/**
	 * Mother meiosis model, from which a gamete is taken.
	**/
	private MeiosisModel motherMeiosisModel;

	/**
	 * Father meiosis model, from which a gamete is taken.
	**/
	private MeiosisModel fatherMeiosisModel;

	/**
	 * The offspring organism created by fertilization, may be null
	**/
	private Organism offspringOrganism;

	/**
	 * Vector of fertilization chromosome models from mother gamete.
	**/
	private	Vector motherChromosomeModels;

	/**
	 * Vector of fertilization chromosome models from father gamete.
	**/
	private	Vector fatherChromosomeModels;

	/**
	 * Enclosing view rectangle for this fertilization model.
	**/
	private Rectangle enclosingViewRectangle;

	/**
	 * Have we notified that fertilization has started?
	**/
	private boolean notifiedFertilizationStarted = false;

	/**
	 * Utility object which manages property change events and listeners.
	**/
	protected transient PropertyChangeSupport changes = null;

    /**
     * Creates an empty instance of FertilizationModel
    **/
    public FertilizationModel()
	{
		currentStep = 0;
		notifiedFertilizationStarted = false;
		motherMeiosisModel = null;
		fatherMeiosisModel = null;
		offspringOrganism = null;
		motherChromosomeModels = null;
		fatherChromosomeModels = null;
		enclosingViewRectangle = new Rectangle(0,0,500,500);

		changes = new PropertyChangeSupport(this);
    }

	/**
	 * Deletes this model and associated models
	**/
	public void delete()
	{
		// Delete all the chromosome models
		FertilizationChromosomeModel chromosomeModel;
		Enumeration eChromosomeModels;
		Vector chromosomeModelsClone = null;

		int i;
		for (i=0;i<2;i++)
		{
			chromosomeModelsClone = null;

			switch(i)
			{
				case 0: if (motherChromosomeModels != null)
							chromosomeModelsClone = (Vector) motherChromosomeModels.clone();
							//chromosomeModelsClone = (Vector) motherChromosomeModels;
						break;
				case 1: if (fatherChromosomeModels != null)
							chromosomeModelsClone = (Vector) fatherChromosomeModels.clone();
							//chromosomeModelsClone = (Vector) fatherChromosomeModels;
						break;
			}
			
			if (chromosomeModelsClone != null)
			{
                eChromosomeModels = chromosomeModelsClone.elements();
				while (eChromosomeModels.hasMoreElements())
				{
					chromosomeModel = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
					chromosomeModel.delete();
				}
			}
		}
		motherChromosomeModels = null;
		fatherChromosomeModels = null;

		if (motherMeiosisModel != null)
		{
			motherMeiosisModel.removePropertyChangeListener(this);
			motherMeiosisModel = null;
		}

		if (fatherMeiosisModel != null)
		{
			fatherMeiosisModel.removePropertyChangeListener(this);
			fatherMeiosisModel = null;
		}

		if (offspringOrganism != null)
		{
			offspringOrganism.removePropertyChangeListener(this);
			offspringOrganism = null;
		}

		currentStep = 0;
		notifiedFertilizationStarted = false;
	}

	/**
	 * Set the meiosis models of this fertilization model, deleting any existing state for other meiosis models.<p>
	 *
	 * @param		aMotherMeiosisModel MeiosisModel - the mother meiosis model
	 * @param		aFatherMeiosisModel MeiosisModel - the father meiosis model
	**/
	public void setMeiosisModels(MeiosisModel aMotherMeiosisModel, MeiosisModel aFatherMeiosisModel)
	{
		// Don't return immediately if meiosis models haven't changed,
		// as the selected gametes may have changed.

		FertilizationChromosomeModel fertilizationChromosomeModel;
		MeiosisChromosomeModel meiosisChromosomeModel;
		Vector meiosisChromosomeModels;
		Enumeration eMeiosisChromosomeModels;

   		// Delete entire state if changing both models or changing one model and other model null
		if ((motherMeiosisModel != aMotherMeiosisModel && fatherMeiosisModel != aFatherMeiosisModel) ||
			(motherMeiosisModel != aMotherMeiosisModel && fatherMeiosisModel == null) ||
			(motherMeiosisModel == null && fatherMeiosisModel != aFatherMeiosisModel))
		{
			delete();
		}
		
		Organism oldOffspringOrganism = offspringOrganism;
		if (offspringOrganism != null)
		{
			offspringOrganism.removePropertyChangeListener(this);
			offspringOrganism = null;
		}
		currentStep = 0;
		notifiedFertilizationStarted = false;

		// Reset mother state only if not null and changed from previous mother state
		if (aMotherMeiosisModel != null && aMotherMeiosisModel != motherMeiosisModel)
		{
			if (motherMeiosisModel != null)
			{
				motherMeiosisModel.removePropertyChangeListener(this);
			}
			motherMeiosisModel = aMotherMeiosisModel;
			motherMeiosisModel.addPropertyChangeListener(this);

			meiosisChromosomeModels = motherMeiosisModel.getMovedGameteChromosomeModels();
			if (meiosisChromosomeModels != null)
			{
				motherChromosomeModels = new Vector();

				eMeiosisChromosomeModels = meiosisChromosomeModels.elements();
				while (eMeiosisChromosomeModels.hasMoreElements())
				{
					meiosisChromosomeModel = (MeiosisChromosomeModel) eMeiosisChromosomeModels.nextElement();
					fertilizationChromosomeModel = new FertilizationChromosomeModel(FertilizationChromosomeModel.FEMALE_GAMETE_CHROMOSOME,
																					this, meiosisChromosomeModel, enclosingViewRectangle);
					motherChromosomeModels.addElement(fertilizationChromosomeModel);
				}
			}
		}

		// Reset father state only if not null and changed from previous father state
		if (aFatherMeiosisModel != null && aFatherMeiosisModel != fatherMeiosisModel)
		{
			if (fatherMeiosisModel != null)
			{
				fatherMeiosisModel.removePropertyChangeListener(this);
			}
			fatherMeiosisModel = aFatherMeiosisModel;
			fatherMeiosisModel.addPropertyChangeListener(this);

			meiosisChromosomeModels = fatherMeiosisModel.getMovedGameteChromosomeModels();
			if (meiosisChromosomeModels != null)
			{
				fatherChromosomeModels = new Vector();

				eMeiosisChromosomeModels = meiosisChromosomeModels.elements();
				while (eMeiosisChromosomeModels.hasMoreElements())
				{
					meiosisChromosomeModel = (MeiosisChromosomeModel) eMeiosisChromosomeModels.nextElement();
					fertilizationChromosomeModel = new FertilizationChromosomeModel(FertilizationChromosomeModel.MALE_GAMETE_CHROMOSOME,
																					this, meiosisChromosomeModel, enclosingViewRectangle);
					fatherChromosomeModels.addElement(fertilizationChromosomeModel);
				}
			}
		}

		// Notify listeners that fertilization gametes have changed
		changes.firePropertyChange(UIProp.FERTILIZATION_GAMETES,new Gamete(),new Gamete());

		// Notify listeners that the fertilization offspring organism has changed
		if (oldOffspringOrganism != offspringOrganism)
		{
			changes.firePropertyChange(UIProp.FERTILIZATION_OFFSPRING_ORGANISM,oldOffspringOrganism,offspringOrganism);
		}
	}

	/**
	 * React to the mother selected gamete changing
	**/
	public void onMotherGameteChanged()
	{
		// Reset mother state only if not null and changed from previous mother state
		if (motherMeiosisModel != null)
		{
			FertilizationChromosomeModel fertilizationChromosomeModel;
			MeiosisChromosomeModel meiosisChromosomeModel;
			Vector meiosisChromosomeModels;
			Enumeration eMeiosisChromosomeModels;

			meiosisChromosomeModels = motherMeiosisModel.getMovedGameteChromosomeModels();
			if (meiosisChromosomeModels != null)
			{
				motherChromosomeModels = new Vector();

				eMeiosisChromosomeModels = meiosisChromosomeModels.elements();
				while (eMeiosisChromosomeModels.hasMoreElements())
				{
					meiosisChromosomeModel = (MeiosisChromosomeModel) eMeiosisChromosomeModels.nextElement();
					fertilizationChromosomeModel = new FertilizationChromosomeModel(FertilizationChromosomeModel.FEMALE_GAMETE_CHROMOSOME,
																					this, meiosisChromosomeModel, enclosingViewRectangle);
					motherChromosomeModels.addElement(fertilizationChromosomeModel);
				}
			}
			else
			{
				motherChromosomeModels = null;
			}
			
			// New organism must be null'ed out as gamete changed
			Organism oldOffspringOrganism = offspringOrganism;
			if (offspringOrganism != null)
			{
				offspringOrganism.removePropertyChangeListener(this);
				offspringOrganism = null;
			}
			currentStep = 0;

			// Notify listeners that fertilization gametes have changed
			changes.firePropertyChange(UIProp.FERTILIZATION_GAMETES,new Gamete(),new Gamete());

			// Notify listeners that the fertilization new organism has changed, if it did change
			if (offspringOrganism != oldOffspringOrganism)
			{
				changes.firePropertyChange(UIProp.FERTILIZATION_OFFSPRING_ORGANISM,
										   oldOffspringOrganism,offspringOrganism);
			}
		}
	}

	/**
	 * React to the father selected gamete changing
	**/
	public void onFatherGameteChanged()
	{
		// Reset father state only if not null and changed from previous father state
		if (fatherMeiosisModel != null)
		{
			FertilizationChromosomeModel fertilizationChromosomeModel;
			MeiosisChromosomeModel meiosisChromosomeModel;
			Vector meiosisChromosomeModels;
			Enumeration eMeiosisChromosomeModels;

			meiosisChromosomeModels = fatherMeiosisModel.getMovedGameteChromosomeModels();
			if (meiosisChromosomeModels != null)
			{
				fatherChromosomeModels = new Vector();

				eMeiosisChromosomeModels = meiosisChromosomeModels.elements();
				while (eMeiosisChromosomeModels.hasMoreElements())
				{
					meiosisChromosomeModel = (MeiosisChromosomeModel) eMeiosisChromosomeModels.nextElement();
					fertilizationChromosomeModel = new FertilizationChromosomeModel(FertilizationChromosomeModel.MALE_GAMETE_CHROMOSOME,
																					this, meiosisChromosomeModel, enclosingViewRectangle);
					fatherChromosomeModels.addElement(fertilizationChromosomeModel);
				}
			}
			else
			{
				fatherChromosomeModels = null;
			}

			// New organism must be null'ed out as gamete changed
			Organism oldOffspringOrganism = offspringOrganism;
			if (offspringOrganism != null)
			{
				offspringOrganism.removePropertyChangeListener(this);
				offspringOrganism = null;
			}
			currentStep = 0;
			
	
			// Notify listeners that fertilization gametes have changed
			changes.firePropertyChange(UIProp.FERTILIZATION_GAMETES,new Gamete(),new Gamete());
			// Notify listeners that the fertilization new organism has changed
			
			if (oldOffspringOrganism != offspringOrganism)
			{
				changes.firePropertyChange(UIProp.FERTILIZATION_OFFSPRING_ORGANISM,oldOffspringOrganism,offspringOrganism);
				//System.out.println("coddfdfdsfdsf here?");
			}
			
		}
	}

	/**
	 * Decrement the current step of the fertilization, returning the new current step value.
	 *
	 * @return		int - new current step value of fertilization
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
		else if (currentStep > 35)
		{
			// Set currentStep to 35
			currentStep = 35;
		}
		else
		{
			// Normal case (1 to 35) - decrement currentStep
			currentStep -= 1;
		}

		if (currentStep != oldCurrentStep)
		{
			// Notify listeners that meiosis started or stepped, but not both
			if (!notifiedFertilizationStarted)
			{
				notifiedFertilizationStarted = true;
				changes.firePropertyChange(UIProp.FERTILIZATION_STARTED,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
			else
			{
				changes.firePropertyChange(UIProp.FERTILIZATION_STEP,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
		}

		return currentStep;
	}

	/**
	 * Increment the current step of the fertilization
	 *
	 * @return		int - new current step value of fertilization
	**/
	public int incrementStep()
	{
		int oldCurrentStep = currentStep;

		if (currentStep == 35)
		{
			// Return immediately if currentStep already 35
			return 35;
		}
		else if (currentStep < 0)
		{
			// Set currentStep to 0
			currentStep = 0;
		}
		else if (currentStep > 35)
		{
			// Set currentStep to 35
			currentStep = 35;
		}
		else
		{
			// Normal case (0 to 34) - increment currentStep
			currentStep += 1;
		}

		// Create organism if we've finalized fertilization
		if (currentStep == 35 && offspringOrganism == null)
		{
			createOffspringOrganism();
		}
	
		if (currentStep != oldCurrentStep)
		{
			// Notify listeners that meiosis started or stepped, but not both
			if (!notifiedFertilizationStarted)
			{
				notifiedFertilizationStarted = true;
				changes.firePropertyChange(UIProp.FERTILIZATION_STARTED,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
			else
			{
				changes.firePropertyChange(UIProp.FERTILIZATION_STEP,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
		}

		return currentStep;
	}

	/**
	 * Get the current step of the fertilization
	 *
	 * @return		int - new current step
	**/
	public int getStep()
	{
		return currentStep;
	}

	/**
	 * Set the current step of the fertilization
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
		else if (aStep > 35)
		{
			// Set currentStep to 35
			currentStep = 35;
		}
		else
		{
			// Normal case (0 to 35)
			currentStep = aStep;
		}
	
		if (currentStep != oldCurrentStep)
		{
			// Notify listeners that meiosis started or stepped, but not both
			if (!notifiedFertilizationStarted)
			{
				notifiedFertilizationStarted = true;
				changes.firePropertyChange(UIProp.FERTILIZATION_STARTED,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
			else
			{
				changes.firePropertyChange(UIProp.FERTILIZATION_STEP,
										   new Integer(oldCurrentStep),
										   new Integer(currentStep));
			}
		}

		// Create organism if we've finalized fertilization
		if (currentStep == 35 && offspringOrganism == null)
		{
			createOffspringOrganism();
		}

		return currentStep;
	}

	/**
	 * Get vector of the appropriate enumerations of chromosome models in this fertilization model.<p>
	 *
	 * In all cases, a Vector holding 2 Enumerations will be returned.  The first Enumeration will be
	 * over the chromosome models from the mother and the second Enumeration will be over the
	 * chromosome models from the father.<p>
	 *
	 * @return		Enumeration - an enumeration over all chromosomes models in this meiosis model
	**/
	public Vector getChromosomeModelEnumerations()
	{
		Vector enumerations = new Vector();

		Enumeration anEnumeration;

		// Return enumerations for the chromosomes models from the 2 parents
		if (motherChromosomeModels != null)
		{
			anEnumeration = motherChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
		}

		if (fatherChromosomeModels != null)
		{
			anEnumeration = fatherChromosomeModels.elements();
			enumerations.addElement(anEnumeration);
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
		FilledCircle filledCircle;
		SpindleLine spindleLine;
		int animationStep;

		Vector boundaries = new Vector();

		int limitingViewDimension;
		if (enclosingViewRectangle.width > enclosingViewRectangle.height)
		{
			limitingViewDimension = enclosingViewRectangle.height;
		}
		else
		{
			limitingViewDimension = enclosingViewRectangle.width;
		}

		// Only allow animation graphics to occur if we have both
		// the mother and father gametes specified
		if (motherChromosomeModels == null ||
			fatherChromosomeModels == null)
		{
			animationStep = 0;
		}
		else
		{
			animationStep = currentStep;
		}

		if (animationStep < 25)
		{
			// 2 gametes merging - Small cell and oval
			int motherGameteDiameter0 = (limitingViewDimension / 2) - 4;
			int motherGameteRadius0 = motherGameteDiameter0 / 2;
			int xMotherGameteCenter0 = 2 + motherGameteRadius0;
			int xFatherGameteCenter0 = 6 + (3 * motherGameteRadius0);

			int motherGameteDiameter25 = limitingViewDimension - 4;
			int motherGameteRadius25 = motherGameteDiameter25 / 2;
			int xMotherGameteCenter25 = limitingViewDimension / 2;
			int xFatherGameteCenter25 = xMotherGameteCenter25;

			int yGameteCenter = 4 + motherGameteDiameter0;
			int fatherGameteHeight = (2 * motherGameteDiameter0) / 3;
			int yFatherGameteTop = yGameteCenter  - (fatherGameteHeight / 2);

			int motherGameteDiameter = motherGameteDiameter0 + (((animationStep * motherGameteDiameter25) - (animationStep * motherGameteDiameter0))/25);
			int motherGameteRadius = motherGameteDiameter / 2;
			int xMotherGameteCenter = xMotherGameteCenter0 + (((animationStep * xMotherGameteCenter25) - (animationStep * xMotherGameteCenter0))/25);
			int xFatherGameteCenter = xFatherGameteCenter0 + (((animationStep * xFatherGameteCenter25) - (animationStep * xFatherGameteCenter0))/25);

			if (motherChromosomeModels != null)
			{
				arc = new CellArc(xMotherGameteCenter - motherGameteRadius,
								  yGameteCenter - motherGameteRadius,
								  motherGameteDiameter, motherGameteDiameter,
								  0, 360);
				arc.setModel(CellArc.FERTILIZATION_MOTHER_GAMETE_MODEL);
				boundaries.addElement(arc);
			}

			if (fatherChromosomeModels != null)
			{
				arc = new CellArc(xFatherGameteCenter - motherGameteRadius,
								  yFatherGameteTop,
								  motherGameteDiameter, fatherGameteHeight,
								  0, 360);
				arc.setModel(CellArc.FERTILIZATION_FATHER_GAMETE_MODEL);
				boundaries.addElement(arc);
			}

			if (motherChromosomeModels != null)
			{
				filledCircle = new FilledCircle(xMotherGameteCenter - motherGameteRadius + 1,
												yGameteCenter - motherGameteRadius + 1,
												motherGameteDiameter-2,motherGameteDiameter-2);
				boundaries.addElement(filledCircle);
			}
											
			if (fatherChromosomeModels != null)
			{
				filledCircle = new FilledCircle(xFatherGameteCenter - motherGameteRadius + 1,
												yFatherGameteTop + 1,
												motherGameteDiameter-2, fatherGameteHeight-2);
				boundaries.addElement(filledCircle);
			}
		}
		else if (animationStep <36)
		{
			// Just one big cell
			int diameter = limitingViewDimension - 4;
			arc = new CellArc(2,2,diameter,diameter,0,360);
			boundaries.addElement(arc);
		}

		return boundaries;
	}

	/**
	 * Set the rectangle within which the fertilization model must confine itself and its
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
		FertilizationChromosomeModel chromosomeModel;
		Enumeration eChromosomeModels;

		int i;
		for (i=0;i<2;i++)
		{
			chromosomeModels = null;

			switch(i)
			{
				case 0: chromosomeModels = motherChromosomeModels;
						break;
				case 1: chromosomeModels = fatherChromosomeModels;
						break;
			}
			
			if (chromosomeModels != null)
			{
				eChromosomeModels = chromosomeModels.elements();
				while (eChromosomeModels.hasMoreElements())
				{
					chromosomeModel = (FertilizationChromosomeModel) eChromosomeModels.nextElement();
					chromosomeModel.setEnclosingViewRectangle(aRectangle);
				}
			}
		}
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

		if (propertyName.equals(UIProp.MOVED_GAMETE) ||
			propertyName.equals(UIProp.ORGANISM))
		{
			
			MeiosisModel aMotherMeiosisModel = motherMeiosisModel;
			MeiosisModel aFatherMeiosisModel = fatherMeiosisModel;
	
			MeiosisModel meiosisModel;
			Object object = event.getSource();
			if (object instanceof MeiosisModel)
			{
				meiosisModel = (MeiosisModel) object;
				if (meiosisModel == aMotherMeiosisModel)
				{
					onMotherGameteChanged();
				}
				else if (meiosisModel == aFatherMeiosisModel)
				{
					onFatherGameteChanged();
				}
			}
		}
		else if (propertyName.equals(EngineProp.DELETED))
		{
			Object object = event.getSource();
			if (object instanceof Organism)
			{
				Organism organism = (Organism) object;
				if (organism == offspringOrganism)
				{
					Organism oldOffspringOrganism = offspringOrganism;
					offspringOrganism = null;

					// Notify listeners that the fertilization new organism has changed
					changes.firePropertyChange(UIProp.FERTILIZATION_OFFSPRING_ORGANISM,oldOffspringOrganism,offspringOrganism);
				}
			}
		}
	}

	/**
	 * Create an offspring organism using the state of this model, although
	 * return immediately if the mother and father aren't fully specified.
	**/
	private void createOffspringOrganism()
	{
		if (motherChromosomeModels == null ||
			fatherChromosomeModels == null)
		{
			return;
		}

		int nextInt;
		Vector chromosomeSpecifications = new Vector();
		ChromosomeSpecification motherCS,fatherCS;
		FertilizationChromosomeModel aMotherModel,aFatherModel;
		Enumeration eMotherModels = motherChromosomeModels.elements();
		Enumeration eFatherModels = fatherChromosomeModels.elements();
		while (eMotherModels.hasMoreElements() &&
			   eFatherModels.hasMoreElements())
		{
			aMotherModel = (FertilizationChromosomeModel) eMotherModels.nextElement();
			aFatherModel = (FertilizationChromosomeModel) eFatherModels.nextElement();
			motherCS = aMotherModel.getChromosomeSpecification();
			fatherCS = aFatherModel.getChromosomeSpecification();

			// Randomly choose which chromosome specification is first in list (mom's or dad's)
			nextInt = random.nextInt();
			if (nextInt < 0)
			{
				chromosomeSpecifications.addElement(motherCS);
				chromosomeSpecifications.addElement(fatherCS);
			}
			else
			{
				chromosomeSpecifications.addElement(fatherCS);
				chromosomeSpecifications.addElement(motherCS);
			}
		}

		Organism mother = motherMeiosisModel.getOrganism();
		Organism father = fatherMeiosisModel.getOrganism();
		Species species = mother.getSpecies();
		World world = species.getWorld();

		// Remove old offspringOrganism and create new offspringOrganism
		Organism oldOffspringOrganism = offspringOrganism;
		if (offspringOrganism != null)
		{
			offspringOrganism.removePropertyChangeListener(this);
			offspringOrganism = null;
		}

		// Create new organism in a family
		offspringOrganism = new Organism(world,null,species,mother,father,chromosomeSpecifications);

		// Add this model as a property change listener
		offspringOrganism.addPropertyChangeListener(this);
		offspringOrganism.setChromosomePaintInfo(getStrandInfo());
		

		// Notify listeners that the fertilization offspring organism has changed
		if (offspringOrganism != oldOffspringOrganism)
		{
			changes.firePropertyChange(UIProp.FERTILIZATION_OFFSPRING_ORGANISM,oldOffspringOrganism,offspringOrganism);
		}
	}

	public Organism getOffspringOrganism()
	{
		return offspringOrganism;
	}

	public boolean isValidToDoFertilization()
	{
		if (motherMeiosisModel == null || fatherMeiosisModel == null ||
			motherChromosomeModels == null || fatherChromosomeModels == null)
		{
			return false;
		}

		return true;
	}
	
	
    /**
     *  Get the graphics information for chromosome View
     *  
     */
     private Branch [] getStrandInfo()
     {
        
        OrganismAllele [] alleles;
        Color [] colors;
     	Enumeration eChromosomeModels;
     	FertilizationChromosomeModel aChromosomeModel;
        Branch [] branchs;
        // Draw chromosomes and alleles
        Vector chromosomeModelEnumerations = this.getChromosomeModelEnumerations();
    
        Vector [] allelesGroup = new Vector[chromosomeModelEnumerations.size()];
        Vector [] colorsGroup = new Vector[chromosomeModelEnumerations.size()];
        
        for (int i = 0; i<chromosomeModelEnumerations.size();i++)
        {
        	allelesGroup[i] = new Vector();
        	colorsGroup[i] = new Vector();
            eChromosomeModels = (Enumeration) chromosomeModelEnumerations.elementAt(i);
            while (eChromosomeModels.hasMoreElements())
            {
                aChromosomeModel = (FertilizationChromosomeModel)eChromosomeModels.nextElement();
    			OrganismAllele [][]tempAlleles = new OrganismAllele[2][];
           		Color [][] tempColors = new Color[2][];
                for (int iStrand=0;iStrand<2;iStrand++)
                {
               		
                    alleles = null;
                    colors = null;
    
                    // Get strand locations
                    switch (iStrand)
                    {
                        case 0:
                            alleles = aChromosomeModel.getPStrandAlleles();
                            colors = aChromosomeModel.getPStrandColors();
                            break;
        
                        case 1:
                            alleles = aChromosomeModel.getQStrandAlleles();
                            colors = aChromosomeModel.getQStrandColors();
                            break;
                    }
                    tempAlleles[iStrand] = alleles;
                    
                    tempColors [iStrand] = colors;
                   
                 }
                 
                   allelesGroup[i].addElement(tempAlleles);
                   colorsGroup[i].addElement(tempColors);
              }
         }
    
        branchs = new Branch[allelesGroup[0].size()];
        // System.out.println(branchs.length);
         
         for (int i = 0;i<allelesGroup[0].size();i++)
         {
         	
         	OrganismAllele [][] alleleGroup1 = (OrganismAllele[][])allelesGroup[0].elementAt(i);
         	OrganismAllele [][] alleleGroup2 = (OrganismAllele[][])allelesGroup[1].elementAt(i);
         	Color [][] colorGroup1 =(Color [][])colorsGroup[0].elementAt(i);
         	Color [][] colorGroup2 =(Color [][])colorsGroup[1].elementAt(i);
         	
         	branchs[i] = new Branch(alleleGroup1[0],colorGroup1[0],alleleGroup2[0],colorGroup2[0],
         							alleleGroup1[1],colorGroup1[1],alleleGroup2[1],colorGroup2[1]);
         							 
         }
         
         return branchs;
     }
}

