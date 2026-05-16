//
// Class : FertilizationChromosomeModel
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.3 $
// $Date: 2002/12/19 21:57:56 $
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

import org.concord.biologica.engine.*;

/**
 * FertilizationChromosomeModel contains the data describing the fertilization
 * state for a given chromosome.  It does not contain the code for drawing the
 * animation as that is in the view objects that show meiosis.<p>
 *
 * This class and the associated fertilization model class assume a display
 * space of 0 to 1000 by 0 to 1000 and all locations are within that range.
 * This means a user of these model classes must scale locations
 * appropriately from this coordinate system to their own in order
 * to draw the chromosomes.<p>
 *
 * @version		$Revision: 1.3 $ $Date: 2002/12/19 21:57:56 $
 * @author 		$Author: qliao $
**/
public final class FertilizationChromosomeModel
{
	/**
	 * Types of fertilization chromosome models
	**/
	static final public int FEMALE_GAMETE_CHROMOSOME	= 1;
	static final public int MALE_GAMETE_CHROMOSOME		= 2;

	/**
	 * For convenience - 2 PI
	**/
	static final private double TWO_PI = 2.0 * Math.PI;

	/**
	 * Random number generator used in choosing locations, angles, etc.<p>
	**/
	static private Random random = new Random();

	/**
	 * Array of X coordinates of centromere locations, indexed by fertilization step.  We use a static array
	 * here of the maximum size ever needed to avoid reallocating the array for each chromosome.
	**/
	static private float[] xCentromereDouble = null;

	/**
	 * Array of Y coordinates of centromere locations, indexed by fertilization step.  We use a static array
	 * here of the maximum size ever needed to avoid reallocating the array for each chromosome.
	**/
	static private float[] yCentromereDouble = null;

	/**
	 * Array of angles of P strand at centromere, indexed by meiosis step.
	**/
	static private float[] anglePStrandAtCentromereDouble = null;

	/**
	 * Array of angles of Q strand at centromere, indexed by meiosis step.
	**/
	static private float[] angleQStrandAtCentromereDouble = null;

	/**
	 * Static array of float P angles for strand 1.  Used as scratch space when
	 * calculating strand locations, indexed by fertilization step.
	**/
	static private float[] prevStepPAnglesDouble = null;

	/**
	 * Static array of float Q angles for strand 1.  Used as scratch space when
	 * calculating strand locations, indexed by fertilization step.
	**/
	static private float[] prevStepQAnglesDouble = null;

	/**
	 * Array of array of float X coordinates of P strand, indexed by fertilization step.
	**/
	static private float[][] xPStrandPointsDouble;

	/**
	 * Array of array of float Y coordinates of P strand, indexed by fertilization step.
	**/
	static private float[][] yPStrandPointsDouble;

	/**
	 * Array of array of float X coordinates of Q strand, indexed by fertilization step.
	**/
	static private float[][] xQStrandPointsDouble;

	/**
	 * Array of array of float Y coordinates of Q strand, indexed by fertilization step.
	**/
	static private float[][] yQStrandPointsDouble;

	/**
	 * Type of this chromosome model - from male or female gamete, etc.
	**/
	private int chromosomeModelType;

	/**
	 * Limiting view dimension (width or height)
	**/
	private int limitingViewDimension;

	/**
	 * The parent's chromosome
	**/
	private OrganismChromosome parentChromosome;

	/**
	 * The fertilization model
	**/
	private FertilizationModel fertilizationModel;

	/**
	 * The meiosis chromosome model in the gamete which produced this fertilization chromosome model
	**/
	private MeiosisChromosomeModel meiosisChromosomeModel;

	/**
	 * Number of P strand segments
	**/
	private int numberOfPStrandSegments;

	/**
	 * Number of Q strand segments
	**/
	private int numberOfQStrandSegments;

	/**
	 * Array of X coordinate of centromere locations, indexed by meiosis step.
	**/
	private int[] xCentromere;

	/**
	 * Array of Y coordinate of centromere locations, indexed by meiosis step.
	**/
	private int[] yCentromere;

	/**
	 * Array of array of X coordinates of P strand, indexed by fertilization step.
	**/
	private int[][] xPStrandPoints;

	/**
	 * Array of array of Y coordinates of P strand, indexed by fertilization step.
	**/
	private int[][] yPStrandPoints;

	/**
	 * Array of array of X coordinates of Q strand, indexed by meiosis step.
	**/
	private int[][] xQStrandPoints;

	/**
	 * Array of array of Y coordinates of Q strand, indexed by meiosis step.
	**/
	private int[][] yQStrandPoints;

	/**
	 * Array of alleles along P strand, indexed by the number of points
	 * along the P and Q strand of the chromosome.  An element is null if there
	 * there is no allele at that point in the chromosome, non-null if there is.
	**/
	private OrganismAllele[] allelesPStrand;

	/**
	 * Array of alleles along Q strand, indexed by the number of points
	 * along the P and Q strands of the chromosome.  An element is null if there
	 * there is no allele at that point in the chromosome, non-null if there is.
	**/
	private OrganismAllele[] allelesQStrand;

	/**
	 * Array of colors along P strand, indexed by the number of points
	 * along the P and Q strand of the chromosome.
	**/
	private Color[] colorsPStrand;

	/**
	 * Array of colors along Q strand, indexed by the number of points
	 * along the P and Q strands of the chromosome.
	**/
	private Color[] colorsQStrand;

	/**
	 * Current temporary X offset - used when chromosome is dragged by user.
	 * Zero normally and zeroed immediately when fertilization step changes.
	**/
	private int xTemporaryOffset;

	/**
	 * Current temporary Y offset - used when chromosome is dragged by user.
	 * Zero normally and zeroed immediately when fertilization step changes.
	**/
	private int yTemporaryOffset;

	/**
	 * Segment length in pixels
	**/
	private float lengthSegment;

	/**
	 * Creates a new instance of FertilizationChromosomeModel with the given type,
	 * fertilization model and meiosis chromosome model.<p>
	 *
	 * @param		aFertilizationChromosomeModelType int - the type of this chromosome model (male or female)
	 * @param		aFertilizationModel FertilizationModel - the parent fertilization model, may not be null
	 * @param		aMeiosisChromosomeModel MeiosisChromosomeModel - the meiosis chromosome model from a gamete
	 * @param		anEnclosingViewRectangle Rectangle - an enclosing rectangle for the chromosome's view
	 * @exception	IllegalArgumentException - illegal argument
	**/
	public FertilizationChromosomeModel(int aFertilizationChromosomeModelType,
										FertilizationModel aFertilizationModel,
										MeiosisChromosomeModel aMeiosisChromosomeModel,
										Rectangle anEnclosingViewRectangle)
	{
		if (aFertilizationModel == null ||
			(aFertilizationChromosomeModelType != FEMALE_GAMETE_CHROMOSOME &&
			 aFertilizationChromosomeModelType != MALE_GAMETE_CHROMOSOME) ||
			aMeiosisChromosomeModel == null ||
			anEnclosingViewRectangle == null)
		{
			throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
		}

		// Allocate static double arrays if not already allocated
		if (xCentromereDouble == null)
		{
			// Maximum number of fertilization steps is 36
			// Maximum number of P strand segments is 20
			// Maximum number of Q strand segments is 42
			xCentromereDouble = new float[36];
			yCentromereDouble = new float[36];

			anglePStrandAtCentromereDouble = new float[36];
			angleQStrandAtCentromereDouble = new float[36];

			prevStepPAnglesDouble = new float[20];
			prevStepQAnglesDouble = new float[42];

			xPStrandPointsDouble = new float[36][20];
			yPStrandPointsDouble = new float[36][20];
	
			xQStrandPointsDouble = new float[36][42];
			yQStrandPointsDouble = new float[36][42];
		}

		fertilizationModel = aFertilizationModel;
		chromosomeModelType = aFertilizationChromosomeModelType;
		meiosisChromosomeModel = aMeiosisChromosomeModel;
		parentChromosome = aMeiosisChromosomeModel.getChromosome();
		xTemporaryOffset = 0;
		yTemporaryOffset = 0;

		// Calculate limiting view dimension, using only the width and height (assume top left is 0,0)
		if (anEnclosingViewRectangle.width < anEnclosingViewRectangle.height)
		{
			limitingViewDimension = anEnclosingViewRectangle.width;
		}
		else
		{
			limitingViewDimension = anEnclosingViewRectangle.height;
		}

		// Chromosome segment length
		lengthSegment = (float) (((double) limitingViewDimension) / 160.0);

		// Determine number of strand segments
		switch (parentChromosome.getImageNumber())
		{
			case SpeciesChromosome.LONGEST_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_LONGEST_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_LONGEST_CHROMOSOME;
				break;

			case SpeciesChromosome.LONGER_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_LONGER_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_LONGER_CHROMOSOME;
				break;

			case SpeciesChromosome.LONG_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_LONG_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_LONG_CHROMOSOME;
				break;

			case SpeciesChromosome.MEDIUM_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_MEDIUM_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_MEDIUM_CHROMOSOME;
				break;

			case SpeciesChromosome.SHORT_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_SHORT_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_SHORT_CHROMOSOME;
				break;

			case SpeciesChromosome.SHORTER_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_SHORTER_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_SHORTER_CHROMOSOME;
				break;

			case SpeciesChromosome.SHORTEST_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_SHORTEST_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_SHORTEST_CHROMOSOME;
				break;

			case SpeciesChromosome.TINY_CHROMOSOME_IMAGE:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_TINY_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_TINY_CHROMOSOME;
				break;

			default:
				numberOfPStrandSegments = SpeciesChromosome.NUMBER_P_STRAND_SEGMENTS_LONGEST_CHROMOSOME;
				numberOfQStrandSegments = SpeciesChromosome.NUMBER_Q_STRAND_SEGMENTS_LONGEST_CHROMOSOME;
				break;
		}

		// Create centromere locations and angles
		xCentromere = new int[36];
		yCentromere = new int[36];

		randomlySetCentromereLocationAndAngle();

		// Create strand points
		xPStrandPoints = new int[36][numberOfPStrandSegments];
		yPStrandPoints = new int[36][numberOfPStrandSegments];

		xQStrandPoints = new int[36][numberOfQStrandSegments];
		yQStrandPoints = new int[36][numberOfQStrandSegments];

		randomlySetStrandPoints();

		// Create allele vectors
 		// allelesPStrand = new OrganismAllele[numberOfPStrandSegments];
		// allelesQStrand = new OrganismAllele[numberOfQStrandSegments];
		// setAlleleVectors();
		allelesPStrand = meiosisChromosomeModel.getPStrandOneAlleles();
		allelesQStrand = meiosisChromosomeModel.getQStrandOneAlleles();

		// Get color vectors from meiosis chromosome model
 		colorsPStrand = meiosisChromosomeModel.getPStrandOneColors();
		colorsQStrand = meiosisChromosomeModel.getQStrandOneColors();
	}

	/**
	 * Delete this chromosome model.  Assume the meiosis model is calling this method
	 * and therefore we don't need to notify it.
	**/
	public void delete()
	{
		fertilizationModel = null;
		parentChromosome = null;

		// TBD - more?  probably...
	}

	/**
	 * Set the enclosing rectangle for the view containing a display of this chromosome model.
	 * Note that this is not always the rectangle enclosing the cell displaying this chromosome.
	 *
	 * This method will adjust the locations of the chromosome model coordinates to scale
	 * properly when the view is resized by the user.<p>
	 *
	 * @param			newEnlosingViewRectangle Rectangle - an enclosing rectangle for this model's view
	**/
	public void setEnclosingViewRectangle(Rectangle newEnclosingViewRectangle)
	{
		int oldLimitingViewDimension = limitingViewDimension;

		// Calculate limiting view dimension, using only the width and height (assume top left is 0,0)
		if (newEnclosingViewRectangle.width < newEnclosingViewRectangle.height)
		{
			limitingViewDimension = newEnclosingViewRectangle.width;
		}
		else
		{
			limitingViewDimension = newEnclosingViewRectangle.height;
		}

		if (limitingViewDimension == oldLimitingViewDimension)
		{
			// No change
			return;
		}

		// Recalculate everything

		// Change chromosome segment length and width
		lengthSegment = (float) (((double) limitingViewDimension) / 160.0);

		randomlySetCentromereLocationAndAngle();
		randomlySetStrandPoints();
		// setAlleleVectors();

		xTemporaryOffset = 0;
		yTemporaryOffset = 0;
	}

	/**
	 * Randomly set the location and angle for a centromere of this chromosome
	 * over the course of this chromosome model's .<p>
	**/
	private void randomlySetCentromereLocationAndAngle()
	{
		int i;
		double nextDouble;
		double angle0, angle25, angle35;
		double centromereRange0, centromereRange25, centromereRange35;
		double scaleFactor;
		float xDelta, yDelta;
		float anglePStrandAtCentromereDelta, angleQStrandAtCentromereDelta;

		float xLeft = (float) (((double) limitingViewDimension) / 4.0);
		float xRight = (float) ((3.0 * ((double) limitingViewDimension)) / 4.0);
		float xMiddle = (float) (((double) limitingViewDimension) / 2.0);
		float yMiddle = (float) (((double) limitingViewDimension) / 2.0);

		// Randomly determine initial location and angle of centromere at
		// 3 steps during animation - 0, 25 and 35.  Interpolate between
		// those locations and angles for all other locations and angles.
		nextDouble = random.nextDouble();
		centromereRange0 = nextDouble * (((double) limitingViewDimension) / 12.0);
		nextDouble = random.nextDouble();
		angle0 = nextDouble * TWO_PI;

		nextDouble = random.nextDouble();
		centromereRange25 = nextDouble * (((double) limitingViewDimension) / 6.0);
		nextDouble = random.nextDouble();
		angle25 = nextDouble * TWO_PI;

		nextDouble = random.nextDouble();
		centromereRange35 = nextDouble * (((double) limitingViewDimension) / 6.0);
		nextDouble = random.nextDouble();
		angle35 = nextDouble * TWO_PI;
		
		if (chromosomeModelType == FEMALE_GAMETE_CHROMOSOME)
		{
			xCentromereDouble[0] = xLeft + (float)(centromereRange0 * Math.cos(angle0));
		}
		else
		{
			xCentromereDouble[0] = xRight + (float)(centromereRange0 * Math.cos(angle0));
		}
		xCentromere[0] = (int) xCentromereDouble[0];
		yCentromereDouble[0] = yMiddle + (float)(centromereRange0 * Math.sin(angle0));
		yCentromere[0] = (int) yCentromereDouble[0];
		anglePStrandAtCentromereDouble[0] = (float)angle0;
		angleQStrandAtCentromereDouble[0] = (float)(angle0 - Math.PI);

		xCentromereDouble[25] = xMiddle + (float)(centromereRange25 * Math.cos(angle25));
		xCentromere[25] = (int) xCentromereDouble[25];
		yCentromereDouble[25] = yMiddle + (float)(centromereRange25 * Math.sin(angle25));
		yCentromere[25] = (int) yCentromereDouble[25];
		anglePStrandAtCentromereDouble[25] = (float)angle25;
		angleQStrandAtCentromereDouble[25] = (float)(angle25 - Math.PI);

		xCentromereDouble[35] = xMiddle + (float)(centromereRange35 * Math.cos(angle35));
		xCentromere[35] = (int) xCentromereDouble[35];
		yCentromereDouble[35] = yMiddle + (float)(centromereRange35 * Math.sin(angle35));
		yCentromere[35] = (int) yCentromereDouble[35];
		anglePStrandAtCentromereDouble[35] = (float)angle35;
		angleQStrandAtCentromereDouble[35] = (float)(angle35 - Math.PI);

		// Determine first 25 centromere locations and angles
		// by interpolating between the 0 and 25 locations and angles.
		xDelta = xCentromereDouble[25] - xCentromereDouble[0];
		yDelta = yCentromereDouble[25] - yCentromereDouble[0];
		anglePStrandAtCentromereDelta = anglePStrandAtCentromereDouble[25] - anglePStrandAtCentromereDouble[0];
		angleQStrandAtCentromereDelta = angleQStrandAtCentromereDouble[25] - angleQStrandAtCentromereDouble[0];

		for (i=1;i<25;i++)
		{
			scaleFactor = ((double) i) / 24.0;
			xCentromereDouble[i] = xCentromereDouble[0] + (float)(scaleFactor * xDelta);
			xCentromere[i] = (int) xCentromereDouble[i];
			yCentromereDouble[i] = yCentromereDouble[0] + (float)(scaleFactor * yDelta);
			yCentromere[i] = (int) yCentromereDouble[i];
			anglePStrandAtCentromereDouble[i] = anglePStrandAtCentromereDouble[0] + (float)(scaleFactor * anglePStrandAtCentromereDelta);
			angleQStrandAtCentromereDouble[i] = angleQStrandAtCentromereDouble[0] + (float)(scaleFactor * angleQStrandAtCentromereDelta);
		}

		// Determine last 10 centromere locations and angles
		// by interpolating between the 25 and 35 locations and angles.
		xDelta = xCentromereDouble[35] - xCentromereDouble[25];
		yDelta = yCentromereDouble[35] - yCentromereDouble[25];
		anglePStrandAtCentromereDelta = anglePStrandAtCentromereDouble[35] - anglePStrandAtCentromereDouble[25];
		angleQStrandAtCentromereDelta = angleQStrandAtCentromereDouble[35] - angleQStrandAtCentromereDouble[25];

		int j;
		for (i=1;i<10;i++)
		{
			j = i+25;
			scaleFactor = ((double) i) / 9.0;
			xCentromereDouble[j] = xCentromereDouble[25] + (float)(scaleFactor * xDelta);
			xCentromere[j] = (int) xCentromereDouble[j];
			yCentromereDouble[j] = yCentromereDouble[25] + (float)(scaleFactor * yDelta);
			yCentromere[j] = (int) yCentromereDouble[j];
			anglePStrandAtCentromereDouble[j] = anglePStrandAtCentromereDouble[25] + (float)(scaleFactor * anglePStrandAtCentromereDelta);
			angleQStrandAtCentromereDouble[j] = angleQStrandAtCentromereDouble[25] + (float)(scaleFactor * angleQStrandAtCentromereDelta);
		}

		xTemporaryOffset = 0;
		yTemporaryOffset = 0;
	}

	/**
	 * Get the X temporary offset of this chromosome.<p>
	 *
	 * @return		int - x temporary offset
	**/
	public int getXTemporaryOffset()
	{
		return xTemporaryOffset;
	}

	/**
	 * Get the Y temporary offset of this chromosome.<p>
	 *
	 * @return		int - y temporary offset
	**/
	public int getYTemporaryOffset()
	{
		return yTemporaryOffset;
	}

	/**
	 * Set the temporary offsets of this chromosome.<p>
	 *
	 * @param		anXTemporaryOffset int - an X temporary offset
	 * @param		aYTemporaryOffset int - a Y temporary offset
	**/
	public void setTemporaryOffsets(int anXTemporaryOffset, int aYTemporaryOffset)
	{
		xTemporaryOffset = anXTemporaryOffset;
		yTemporaryOffset = aYTemporaryOffset;
	}

	/**
	 * Clear temporary offsets.  A shortcut routine identical to calling
	 * setTemporaryOffsets(0,0).<p>
	**/
	public void clearTemporaryOffsets()
	{
		xTemporaryOffset = 0;
		yTemporaryOffset = 0;
	}

	/**
	 * Get the X centromere location as an int at the given fertilization step
	 * (NOT the array index!!).
	 *
	 * @param		aStep int - step of fertilization (not array index!!)
	 * @return		float - x location of centromere
	 * @exception	IllegalArgumentException - illegal step input
	**/
	public float getXCentromereAtStep(int aStep)
	{
		int arrayIndex = aStep;

		if (arrayIndex < 0 || arrayIndex > 35)
		{
			throw new IllegalArgumentException("invalid aStep");
		}

		return xCentromere[arrayIndex];
	}

	/**
	 * Get the Y centromere location as an int at the given fertilization step
	 * (NOT the array index!!).
	 *
	 * @param		aStep int - step of fertilization (not array index!!)
	 * @return		float - y location of centromere
	 * @exception	IllegalArgumentException - illegal step input
	**/
	public float getYCentromereAtStep(int aStep)
	{
		int arrayIndex = aStep;

		if (arrayIndex < 0 || arrayIndex > 36)
		{
			throw new IllegalArgumentException("invalid aStep");
		}

		return yCentromere[arrayIndex];
	}

	/**
	 * Get a random angle given the starting angle, the previous angle and a maxAngleIndex.<p>
	 *
	 * The starting angle is the "target" angle around which we want to randomly
	 * choose an actual angle.<p>
	 *
	 * The previous angle is the angle that the segment had in the previous fertilization step.<p>
	 *
	 * The maxAngleIndex is the index into the maxAngle array for the appropriate
	 * step of meiosis.  The index is the fertilization step.
	 *
	 * @param		startingAngle float - starting angle in radians
	 * @param		previousAngle float - previous angle in radians
	 * @param		maxAngleIndex int - index into maxAngle array
	 * @return		float - new angle in radians
	**/
	private float getRandomAngle(float startingAngle, float previousAngle, int maxAngleIndex)
	{
		// Get random number between 0.0 and 1.0 and then subtract 0.5 to normalize
		// it between -0.5 and +0.5.  Then multiply that by the maximum angle allowed
		// to get the delta angle.
		double maxAngle = 2.0;
		double deltaAngle = (random.nextDouble() - 0.5) * maxAngle;

		// Return new angle by applying delta to the starting angle and then averaging with previous angle
		return (float) (((3.0 * (startingAngle + deltaAngle)) + previousAngle)/4.0);
	}

	/**
	 * Randomly set the strand points for all appropriate fertilization steps, using
	 * the location and angle of the centromere as a starting point for each step.
	**/
	private void randomlySetStrandPoints()
	{
		int i, iStep;
		float lastAnglePStrand, currentAnglePStrand;
		float lastAngleQStrand, currentAngleQStrand;

		for (iStep=0;iStep<36;iStep++)
		{
			// Calculate positions and angles of P strand.
			lastAnglePStrand = anglePStrandAtCentromereDouble[iStep];
			currentAnglePStrand = anglePStrandAtCentromereDouble[iStep];
			prevStepPAnglesDouble[0] = lastAnglePStrand;

			// Calculate first points of strands, which are identical
			xPStrandPointsDouble[iStep][0] = xCentromereDouble[iStep] + (float)(lengthSegment * Math.cos((double)lastAnglePStrand));
			yPStrandPointsDouble[iStep][0] = yCentromereDouble[iStep] + (float)(lengthSegment * Math.sin((double)lastAnglePStrand));
			xPStrandPoints[iStep][0] = (int) xPStrandPointsDouble[iStep][0];
			yPStrandPoints[iStep][0] = (int) yPStrandPointsDouble[iStep][0];

			// Loop through points of P strand calculating centerpoints
			for (i=1;i<numberOfPStrandSegments;i++)
			{
				// P Strand

				// Randomly choose an angle for the next segment of P strand one
				lastAnglePStrand = currentAnglePStrand;
				if (iStep > 0)
				{
					currentAnglePStrand = getRandomAngle(lastAnglePStrand,prevStepPAnglesDouble[i],iStep);
				}
				else
				{
					currentAnglePStrand = getRandomAngle(lastAnglePStrand,lastAnglePStrand,iStep);
				}
				prevStepPAnglesDouble[i] = currentAnglePStrand;
					
				// Calculate position of next center point using currentAngleStrand
				xPStrandPointsDouble[iStep][i] = xPStrandPointsDouble[iStep][i-1] +
													(float)(lengthSegment * Math.cos((double)currentAnglePStrand));
				yPStrandPointsDouble[iStep][i] = yPStrandPointsDouble[iStep][i-1] +
													(float)(lengthSegment * Math.sin((double)currentAnglePStrand));

				// Convert values to int and store in array for use in drawing
				xPStrandPoints[iStep][i] = (int) xPStrandPointsDouble[iStep][i];
				yPStrandPoints[iStep][i] = (int) yPStrandPointsDouble[iStep][i];
			}

			// Calculate positions and angles of Q strand.
			// They start off near the centromere with identical positions and angles
			// and then randomly diverge as we go out the strands.
			lastAngleQStrand = angleQStrandAtCentromereDouble[iStep];
			currentAngleQStrand = angleQStrandAtCentromereDouble[iStep];
			prevStepQAnglesDouble[0] = lastAngleQStrand;

			// Calculate first points of strands, which are identical
			xQStrandPointsDouble[iStep][0] = xCentromereDouble[iStep] + (float)(lengthSegment * Math.cos((double)lastAngleQStrand));
			yQStrandPointsDouble[iStep][0] = yCentromereDouble[iStep] + (float)(lengthSegment * Math.sin((double)lastAngleQStrand));
			xQStrandPoints[iStep][0] = (int) xQStrandPointsDouble[iStep][0];
			yQStrandPoints[iStep][0] = (int) yQStrandPointsDouble[iStep][0];

			// Loop through points of Q strand calculating centerpoints
			for (i=1;i<numberOfQStrandSegments;i++)
			{
				// Q Strand

				// Randomly choose an angle for the next segment of Q strand
				lastAngleQStrand = currentAngleQStrand;
				if (iStep > 0)
				{
					currentAngleQStrand = getRandomAngle(lastAngleQStrand,prevStepQAnglesDouble[i],iStep);
				}
				else
				{
					currentAngleQStrand = getRandomAngle(lastAngleQStrand,lastAngleQStrand,iStep);
				}
				prevStepQAnglesDouble[i] = currentAngleQStrand;

				// Calculate position of next center point using currentAngleStrand
				xQStrandPointsDouble[iStep][i] = xQStrandPointsDouble[iStep][i-1] +
												 (float)(lengthSegment * Math.cos((double)currentAngleQStrand));
				yQStrandPointsDouble[iStep][i] = yQStrandPointsDouble[iStep][i-1] +
												 (float)(lengthSegment * Math.sin((double)currentAngleQStrand));

				// Convert values to int and store in array for use in drawing
				xQStrandPoints[iStep][i] = (int) xQStrandPointsDouble[iStep][i];
				yQStrandPoints[iStep][i] = (int) yQStrandPointsDouble[iStep][i];
			}
		}
	}

	/**
	 * Get the array of int's for x coordinates of P strand at the given fertilization step.
	 *
	 * @param		aFertilizationStep int - the fertilization step value (NOT the array index)
	 * @return		int[] - array of int values for x coordinates of P strand
	**/
	public int[] getXPStrandPoints(int aFertilizationStep)
	{
		return xPStrandPoints[aFertilizationStep];
	}

	/**
	 * Get the array of int's for y coordinates of P strand at the given meiosis step.
	 *
	 * @param		aFertilizationStep int - the fertilization step value (NOT the array index)
	 * @return		int[] - array of int values for y coordinates of P strand
	**/
	public int[] getYPStrandPoints(int aFertilizationStep)
	{
		return yPStrandPoints[aFertilizationStep];
	}
	/**
	 * Get the array of int's for x coordinates of Q strand at the given fertilization step.
	 *
	 * @param		aFertilizationStep int - the fertilization step value (NOT the array index)
	 * @return		int[] - array of int values for x coordinates of Q strand
	**/
	public int[] getXQStrandPoints(int aFertilizationStep)
	{
		return xQStrandPoints[aFertilizationStep];
	}

	/**
	 * Get the array of int's for y coordinates of Q strand at the given fertilization step.
	 *
	 * @param		aFertilizationStep int - the fertilization step value (NOT the array index)
	 * @return		int[] - array of int values for y coordinates of Q strand
	**/
	public int[] getYQStrandPoints(int aFertilizationStep)
	{
		return yQStrandPoints[aFertilizationStep];
	}

	/**
	 * Set allele vectors to show which alleles are at which array positions on the chromosomes.
	**/
	public void setAlleleVectors()
	{
		double geneLocation;
		int lengthOfChromosomeInBases = parentChromosome.getLengthInBases();
		double numberOfTotalSegments = (double) (numberOfPStrandSegments + numberOfQStrandSegments);

		Gene aGene;
		int startIndexInHolderInBases, segmentLocation;

		OrganismAllele anOrganismAllele;
		Enumeration eOrganismAlleles = parentChromosome.getOrganismAlleles();
		while (eOrganismAlleles.hasMoreElements())
		{
			anOrganismAllele = (OrganismAllele) eOrganismAlleles.nextElement();
			aGene = anOrganismAllele.getGene();
			startIndexInHolderInBases = aGene.getStartIndexInHolder();

			geneLocation = ((double)startIndexInHolderInBases) / ((double) lengthOfChromosomeInBases);
			
			segmentLocation = (int) (geneLocation * ((double)numberOfTotalSegments))-2;

			if (segmentLocation < 0)
			{
				// Error
				System.err.println("segmentLocation = " + segmentLocation);
			}
			else if (segmentLocation < numberOfPStrandSegments)
			{
				allelesPStrand[segmentLocation] = anOrganismAllele;
			}
			else if (segmentLocation-numberOfPStrandSegments < numberOfQStrandSegments)
			{
				allelesQStrand[segmentLocation-numberOfPStrandSegments] = anOrganismAllele;
			}
			else
			{
				// Error
				System.err.println("segmentLocation = " + segmentLocation);
			}
		}
	}

	/**
	 * Get the array of organism alleles for P strand.  Elements are null if there is
	 * no organism allele at that position, non-null if there is an allele there.
	 *
	 * @return		OrganismAllele[] - array of OrganismAllele values along P strand of chromosome
	**/
	public OrganismAllele[] getPStrandAlleles()
	{
		return allelesPStrand;
 	}

	/**
	 * Get the array of organism alleles for Q strand.  Elements are null if there is
	 * no organism allele at that position, non-null if there is an allele there.
	 *
	 * @return		OrganismAllele[] - array of OrganismAllele values along Q strand of chromosome
	**/
	public OrganismAllele[] getQStrandAlleles()
	{
		return allelesQStrand;
 	}

	/**
	 * Get the array of Colors for P strand.
	 *
	 * @return		Color[] - array of Colors along P strand of chromosome
	**/
	public Color[] getPStrandColors()
	{
		return colorsPStrand;
 	}

	/**
	 * Get the array of Colors for Q strand.
	 *
	 * @return		Color[] - array of Colors along Q strand of chromosome
	**/
	public Color[] getQStrandColors()
	{
		return colorsQStrand;
 	}

	/**
	 * Get a ChromosomeSpecification for this model.  Typically this is used
	 * to create the child organism.<p>
	 *
	 * @return		ChromosomeSpecification for this chromosome model
	**/
	public ChromosomeSpecification getChromosomeSpecification()
	{
		Species aSpecies = parentChromosome.getSpecies();
		SpeciesChromosome aSpeciesChromosome = parentChromosome.getSpeciesChromosome();

		Vector aSpeciesAllelesVector = new Vector();

		int i;
		int length = allelesPStrand.length;
		SpeciesAllele aSpeciesAllele;

		for (i=0;i<length;i++)
		{
			if (allelesPStrand[i] != null)
			{
				aSpeciesAllele = allelesPStrand[i].getSpeciesAllele();
				aSpeciesAllelesVector.addElement(aSpeciesAllele);
			}
		}

		length = allelesQStrand.length;
		for (i=0;i<length;i++)
		{
			if (allelesQStrand[i] != null)
			{
				aSpeciesAllele = allelesQStrand[i].getSpeciesAllele();
				aSpeciesAllelesVector.addElement(aSpeciesAllele);
			}
		}

		return new ChromosomeSpecification(aSpecies,aSpeciesChromosome,aSpeciesAllelesVector);
	}
	public int getChromosomeModelType()
	{
		return chromosomeModelType;
	}
}
