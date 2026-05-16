//
// Class : MeiosisChromosomeModel
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.4 $
// $Date: 2002/12/19 21:58:23 $
// $Author: qliao $
//
// $Revision: 1.4 $Date:: 11/1/00
// $Author:

package org.concord.biologica.ui;

import java.util.Enumeration;
import java.util.Random;

import java.awt.Color;
import java.awt.Rectangle;

import org.concord.biologica.engine.Base;
import org.concord.biologica.engine.Elements;
import org.concord.biologica.engine.Gene;
import org.concord.biologica.engine.OrganismAllele;
import org.concord.biologica.engine.OrganismChromosome;
import org.concord.biologica.engine.SpeciesChromosome;

/**
 * MeiosisChromosomeModel contains the data describing the meiosis state
 * for a given chromosome.  It does not contain the code for drawing the
 * animation as that is in the view objects that show meiosis.<p>
 *
 * This class and the associated meiosis model class assume a display
 * space of 0 to 1000 by 0 to 1000 and all locations are within that range.
 * This means a user of these model classes must scale locations
 * appropriately from this coordinate system to their own in order
 * to draw the chromosomes.<p>
 *
 * @version		$Revision: 1.4 $ $Date: 2002/12/19 21:58:23 $
 * @author 		$Author: qliao $
**/
public final class MeiosisChromosomeModel
{
    /**
     * Types of chromosome models
    **/
    static final public int ORIG_CELL_CHROMOSOME            = 1;
    static final public int LEFT_DAUGHTER_CELL_CHROMOSOME   = 2;
    static final public int RIGHT_DAUGHTER_CELL_CHROMOSOME  = 3;
    static final public int TOP_LEFT_GAMETE_CHROMOSOME      = 4;
    static final public int BOTTOM_LEFT_GAMETE_CHROMOSOME   = 5;
    static final public int TOP_RIGHT_GAMETE_CHROMOSOME     = 6;
    static final public int BOTTOM_RIGHT_GAMETE_CHROMOSOME  = 7;

    /**
     * Types of chromosome model strands
    **/
    static final public int P_STRAND_ONE   = 1;
    static final public int Q_STRAND_ONE   = 2;
    static final public int P_STRAND_TWO   = 3;
    static final public int Q_STRAND_TWO   = 4;

    /**
     * Number of chromosome model strands, used in creating
     * gamete chromosome models only.  This tells the gamete
     * chromosome model which strand of the daughter cell
     * chromosome model to copy.<p>
    **/
    static final public int UNSPECIFIED_STRAND = 0;
    static final public int FIRST_STRAND       = 1;
    static final public int SECOND_STRAND      = 2;

    /**
     * For convenience - 2 PI
    **/
    static final private float TWO_PI = (float) (2.0 * Math.PI);

    /**
     * For convenience - PI / 2
    **/
    static final private float HALF_PI = (float) (Math.PI / 2.0);

    /**
     * For convenience - 3 * (PI / 2)
    **/
    static final private float ONE_AND_HALF_PI = (float) (3.0 * HALF_PI);

    /**
     * Random number generator used in choosing locations, angles, etc.<p>
    **/
    static private Random random = new Random();

    /**
     * Max angle array, uses the meiosis step as an index.  Represents the
     * maximum angle (in radians) difference between 2 segments of a chromosome.<p>
    **/
    static private float MAX_ANGLE[] = {(float)2.0, (float)2.0, (float)2.0, (float)1.9, (float)1.8, (float)1.7,
        (float)1.6, (float)1.5, (float)1.4, (float)1.3, (float)1.3,
        (float)1.2, (float)1.2, (float)1.2, (float)1.2, (float)1.2,
        (float)1.0, (float)1.0, (float)1.0, (float)1.0, (float)1.0,
        (float)0.8, (float)0.8, (float)0.8, (float)0.8, (float)0.8,
        (float)0.6, (float)0.6, (float)0.6, (float)0.6, (float)0.6,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.4, (float)0.4, (float)0.4, (float)0.4, (float)0.4,
        (float)0.6, (float)0.6, (float)0.6, (float)0.6, (float)0.8,
        (float)0.8, (float)0.8, (float)0.8, (float)1.0, (float)1.0,
        (float)1.0, (float)1.0, (float)1.2, (float)1.2, (float)1.4,
        (float)1.5, (float)1.6, (float)1.7, (float)1.8, (float)2.0};

    /**
     * Angles of P strand at centromere for right daughter cell during the middle third of meiosis.
     * Done this way because the angles are hard-coded and there's really no formula for it.
     * This array has 40 elements as there are 40 steps during this portion of meiosis.
    **/
    static private float RIGHT_P_ANGLE[] = {(float)4.7124, (float)4.53, (float)4.23, (float)3.93, (float)3.63,
        (float)3.63, (float)3.63, (float)3.63, (float)3.63, (float)3.63,
        (float)3.63, (float)3.63, (float)3.63, (float)3.63, (float)3.63,
        (float)3.63, (float)3.63, (float)3.63, (float)3.63, (float)3.63,
        (float)3.60, (float)3.60, (float)3.55, (float)3.50, (float)3.45,
        (float)3.40, (float)3.35, (float)3.30, (float)3.25, (float)3.20,
        (float)3.15, (float)3.1416, (float)3.1416, (float)3.1416, (float)3.1416,
        (float)3.1416, (float)3.1416, (float)3.1416, (float)3.1416, (float)3.1416};

    /**
     * Angles of Q strand at centromere for right daughter cell during the middle third of meiosis.
     * Done this way because the angles are hard-coded and there's really no formula for it.
     * This array has 40 elements as there are 40 steps during this portion of meiosis.
    **/
    static private float RIGHT_Q_ANGLE[] = {(float)1.5708, (float)1.75, (float)2.05, (float)2.35, (float)2.65,
        (float)2.65, (float)2.65, (float)2.65, (float)2.65, (float)2.65,
        (float)2.60, (float)2.55, (float)2.50, (float)2.45, (float)2.40,
        (float)2.35, (float)2.25, (float)2.15, (float)2.05, (float)1.95,
        (float)1.85, (float)1.75, (float)1.65, (float)1.55, (float)1.40,
        (float)1.30, (float)1.20, (float)1.10, (float)1.00, (float)0.90,
        (float)0.80, (float)0.70, (float)0.60, (float)0.50, (float)0.40,
        (float)0.30, (float)0.20, (float)0.10, (float)0.00, (float)0.00};

    /**
     * Angles of P strand at centromere for left daughter cells during the middle third of meiosis.
     * Done this way because the angles are hard-coded and there's really no formula for it.
     * This array has 40 elements as there are 40 steps during this portion of meiosis.
    **/
    static private float LEFT_P_ANGLE[] = { (float)4.7124, (float)4.90, (float)5.20, (float)5.50, (float)5.80,
        (float)5.80, (float)5.80, (float)5.80, (float)5.80, (float)5.80,
        (float)5.80, (float)5.70, (float)5.65, (float)5.60, (float)5.55,
        (float)5.50, (float)5.40, (float)5.30, (float)5.20, (float)5.10,
        (float)5.00, (float)4.90, (float)4.80, (float)4.70, (float)4.60,
        (float)4.50, (float)4.40, (float)4.30, (float)4.20, (float)4.10,
        (float)4.00, (float)3.90, (float)3.80, (float)3.70, (float)3.60,
        (float)3.50, (float)3.40, (float)3.30, (float)3.20, (float)3.1416};

    /**
     * Angles of Q strand at centromere for left daughter cells during the middle third of meiosis.
     * Done this way because the angles are hard-coded and there's really no formula for it.
     * This array has 40 elements as there are 40 steps during this portion of meiosis.
    **/
    static private float LEFT_Q_ANGLE[] =  {(float)1.5708, (float)1.35, (float)1.05, (float)0.85, (float)0.55,
        (float)0.55, (float)0.55, (float)0.55, (float)0.55, (float)0.55,
        (float)0.55, (float)0.52, (float)0.49, (float)0.46, (float)0.43,
        (float)0.40, (float)0.37, (float)0.34, (float)0.31, (float)0.28,
        (float)0.25, (float)0.22, (float)0.19, (float)0.17, (float)0.15,
        (float)0.13, (float)0.11, (float)0.09, (float)0.07, (float)0.06,
        (float)0.05, (float)0.04, (float)0.03, (float)0.02, (float)0.02,
        (float)0.01, (float)0.01, (float)0.00, (float)0.00, (float)0.00};

    /**
     * Angles of P strand at centromere for top gamete cells during steps 71 to 80 of meiosis.  Done this
     * way because the angles are hard-coded and there's really no formula for it.
     * This array has 10 elements as there are 10 steps during this portion of meiosis.
    **/
    static private float TOP_P_ANGLE[] =  {(float)3.14156, (float)2.90, (float)2.60, (float)2.30, (float)2.00,
        (float)2.00, (float)2.00, (float)2.00, (float)2.00, (float)2.00};

    /**
     * Angles of Q strand at centromere for top gamete cells during steps 71 to 80 of meiosis.  Done this
     * way because the angles are hard-coded and there's really no formula for it.
     * This array has 10 elements as there are 10 steps during this portion of meiosis.
    **/
    static private float TOP_Q_ANGLE[] =  {(float)0.00, (float)0.30, (float)0.60, (float)0.90, (float)1.20,
        (float)1.20, (float)1.20, (float)1.20, (float)1.20, (float)1.20};

    /**
     * Angles of P strand at centromere for bottom gamete cells during steps 71 to 80 of meiosis.  Done this
     * way because the angles are hard-coded and there's really no formula for it.
     * This array has 10 elements as there are 10 steps during this portion of meiosis.
    **/
    static private float BOTTOM_P_ANGLE[] =  {(float)3.14156, (float)3.40, (float)3.70, (float)4.00, (float)4.30,
        (float)4.30, (float)4.30, (float)4.30, (float)4.30, (float)4.30};

    /**
     * Angles of Q strand at centromere for bottom gamete cells during steps 71 to 80 of meiosis.  Done this
     * way because the angles are hard-coded and there's really no formula for it.
     * This array has 10 elements as there are 10 steps during this portion of meiosis.
    **/
    static private float BOTTOM_Q_ANGLE[] =  {(float)0.00, (float)-0.30, (float)-0.60, (float)-0.90, (float)-1.20,
        (float)-1.20, (float)-1.20, (float)-1.20, (float)-1.20, (float)-1.20};

    /**
     * Array of X coordinates of centromere locations, indexed by meiosis step.  We use a static array
     * here of the maximum size ever needed to avoid reallocating the array for each chromosome.
    **/
    static private float[] xCentromereFloat = null;

    /**
     * Array of X coordinates of centromere locations, indexed by meiosis step.  We use a static array
     * here of the maximum size ever needed to avoid reallocating the array for each chromosome.
    **/
    static private float[] yCentromereFloat = null;

    /**
     * Array of angles of centromere towards P strand of this chromosome model
    **/
    static private float[] anglePStrandAtCentromereFloat;

    /**
     * Array of angles of centromere towards Q strand of this chromosome model
    **/
    static private float[] angleQStrandAtCentromereFloat;

    /**
     * Array of array of float X coordinates of P strand one, indexed by meiosis step.
    **/
    static private float[][] xPStrandOnePointsFloat = null;

    /**
     * Array of array of float Y coordinates of P strand one, indexed by meiosis step.
    **/
    static private float[][] yPStrandOnePointsFloat = null;

    /**
     * Array of array of float X coordinates of P strand two, indexed by meiosis step.
    **/
    static private float[][] xPStrandTwoPointsFloat = null;

    /**
     * Array of array of float Y coordinates of P strand two, indexed by meiosis step.
    **/
    static private float[][] yPStrandTwoPointsFloat = null;

    /**
     * Array of array of float X coordinates of Q strand one, indexed by meiosis step.
    **/
    static private float[][] xQStrandOnePointsFloat = null;

    /**
     * Array of array of float Y coordinates of Q strand one, indexed by meiosis step.
    **/
    static private float[][] yQStrandOnePointsFloat = null;

    /**
     * Array of array of float X coordinates of Q strand two, indexed by meiosis step.
    **/
    static private float[][] xQStrandTwoPointsFloat = null;

    /**
     * Array of array of float Y coordinates of Q strand two, indexed by meiosis step.
    **/
    static private float[][] yQStrandTwoPointsFloat = null;

    /**
     * Static array of float P angles for strand 1.  Used as scratch space when
     * calculating strand locations, indexed by meiosis steps.
    **/
    static private float[] prevStepPAnglesOneFloat = null;

    /**
     * Static array of float P angles for strand 2.  Used as scratch space when
     * calculating strand locations, indexed by meiosis steps.
    **/
    static private float[] prevStepPAnglesTwoFloat = null;

    /**
     * Static array of float Q angles for strand 1.  Used as scratch space when
     * calculating strand locations, indexed by meiosis steps.
    **/
    static private float[] prevStepQAnglesOneFloat = null;

    /**
     * Static array of float Q angles for strand 2.  Used as scratch space when
     * calculating strand locations, indexed by meiosis steps.
    **/
    static private float[] prevStepQAnglesTwoFloat = null;

    /**
     * Type of this chromosome model - original, daughter, gamete, etc.  See above static final types.
    **/
    private int chromosomeModelType;

    /**
     * Does this chromosome ever have a second strand?  If not, then we'll avoid the memory
     * and CPU cost of allocating and calculating the positions for the second strands.
    **/
    private boolean doubleStrand;

    /**
     * The number of the visible chromosome pair for which this object is a model (1 based).
     * Usually this is the same as the number of the chromosome.  However, there are 3 exceptions:
     * - When it's a sex chromosome, in which case the number is the next number after the last
     *   autosome.  (dragon sex chromosomes = 3).
     * - When this chromosome is visible but there are invisible chromosomes before it, in which
     *   case the number of this chromosome model pair will be lower than the real pair number.
     * - When this chromosome model should be invisible, in which case the pair number is 0.
    **/
    private int visibleChromosomePairNumber;

    /**
     * Number of meiosis steps, strictly determined by the chromosome model type above.  Kept as an
     * instance variable because it's simpler than recalculating it everytime we need it from the type.
     * This is the length of all the arrays storing meiosis step values in this object.
    **/
    private int numberOfMeiosisSteps;

    /**
     * Offset from external step index to the arrays in this object.
    **/
    private int offsetMeiosisStep;

    /**
     * Limiting view dimension (width or height)
    **/
    private int limitingViewDimension;

    /**
     * The chromosome
    **/
    private OrganismChromosome chromosome;

    /**
     * The meiosis model
    **/
    private MeiosisModel meiosisModel;

    /**
     * Previous meiosis chromosome model, representing the chromosome model used for the
     * previous portion of the meiosis animation.  This reference varies depending on
     * the type of chromosome model:<p>
     *
     * An original cell chromosome model has a null previous chromosome model.<p>
     * A daughter cell chromosome model has an original cell previous chromosome model.<p>
     * A gamete chromosome model has a daughter cell previous chromosome model.<p>
    **/
    private MeiosisChromosomeModel previousMeiosisChromosomeModel;

    /**
     * Previous meiosis chromosome model strand.  This is the strand of the previous
     * chromosome model copied to create this chromosome model.<p>
     *
     * An original cell chromosome model has a null previous chromosome model and
     * this value is ignored.<p>
     *
     * A daughter cell chromosome model has an original cell previous chromosome model
     * and this value is also ignored, as both strands of the original cell previous
     * chromosome model are identical.<p>
     *
     * A gamete chromosome model has a daughter cell previous chromosome model and
     * this value is used to determine which strand of the daughter cell chromosome
     * model should be copied.  This is significant when crossing over happens, as
     * the strands of the daughter cell chromosome models are not identical.<p>
     *
     * This value should be UNSPECIFIED_STRAND for original and daughter cell
     * chromosome models and FIRST_STRAND or SECOND_STRAND for gamete chromosome models.
    **/
    private int previousMeiosisChromosomeModelStrand;

    /**
     * Number of P strand segments
    **/
    private int numberOfPStrandSegments;

    /**
     * Number of Q strand segments
    **/
    private int numberOfQStrandSegments;

    /**
     * Array of X coordinates of centromere of this chromosome model
    **/
    private int[] xCentromere;

    /**
     * Array of Y coordinates of centromere of this chromosome model
    **/
    private int[] yCentromere;

    /**
     * Angle at centromere of P strand for the last step of this chromosome model.
     * In other words, for steps 30, 70 or 100 depending on whether this chromosome
     * model is an original cell, daughter cell or gamete type of chromosome model.
    **/
    private float angleLastStepPStrandAtCentromere;

    /**
     * Angle at centromere of Q strand for the last step of this chromosome model.
     * In other words, for steps 30, 70 or 100 depending on whether this chromosome
     * model is an original cell, daughter cell or gamete type of chromosome model.
    **/
    private float angleLastStepQStrandAtCentromere;

    /**
     * Array of array of X coordinates of P strand one, indexed by meiosis step.
    **/
    private int[][] xPStrandOnePoints;

    /**
     * Array of array of Y coordinates of P strand one, indexed by meiosis step.
    **/
    private int[][] yPStrandOnePoints;

    /**
     * Array of array of X coordinates of P strand two, indexed by meiosis step.
    **/
    private int[][] xPStrandTwoPoints;

    /**
     * Array of array of Y coordinates of P strand two, indexed by meiosis step.
    **/
    private int[][] yPStrandTwoPoints;

    /**
     * Array of array of X coordinates of Q strand one, indexed by meiosis step.
    **/
    private int[][] xQStrandOnePoints;

    /**
     * Array of array of Y coordinates of Q strand one, indexed by meiosis step.
    **/
    private int[][] yQStrandOnePoints;

    /**
     * Array of array of X coordinates of Q strand two, indexed by meiosis step.
    **/
    private int[][] xQStrandTwoPoints;

    /**
     * Array of array of Y coordinates of Q strand two, indexed by meiosis step.
    **/
    private int[][] yQStrandTwoPoints;

    /**
     * Array of alleles along P strand one, indexed by the number of points
     * along the P and Q strands of the chromosome.  An element is null if there
     * there is no allele at that point in the chromosome, non-null if there is.
    **/
    private OrganismAllele[] allelesPStrandOne;

    /**
     * Array of alleles along P strand two, indexed by the number of points
     * along the P and Q strands of the chromosome.  An element is null if there
     * there is no allele at that point in the chromosome, non-null if there is.
    **/
    private OrganismAllele[] allelesPStrandTwo;

    /**
     * Array of alleles along Q strand one, indexed by the number of points
     * along the P and Q strands of the chromosome.  An element is null if there
     * there is no allele at that point in the chromosome, non-null if there is.
    **/
    private OrganismAllele[] allelesQStrandOne;

    /**
     * Array of alleles along Q strand two, indexed by the number of points
     * along the P and Q strands of the chromosome.  An element is null if there
     * there is no allele at that point in the chromosome, non-null if there is.
    **/
    private OrganismAllele[] allelesQStrandTwo;

    /**
     * Array of colors for the P strand one, index by the number of points
     * along the P and Q strands of the chromosome.
    **/
    private Color[] colorsPStrandOne;

    /**
     * Array of colors for the P strand two, index by the number of points
     * along the P and Q strands of the chromosome.
    **/
    private Color[] colorsPStrandTwo;

    /**
     * Array of colors for the Q strand one, index by the number of points
     * along the P and Q strands of the chromosome.
    **/
    private Color[] colorsQStrandOne;

    /**
     * Array of colors for the Q strand two, index by the number of points
     * along the P and Q strands of the chromosome.
    **/
    private Color[] colorsQStrandTwo;

    /**
     * Current temporary X offset - used when chromosome is dragged by user.
     * Zero normally and zeroed immediately when meiosis step changes.
    **/
    private int xTemporaryOffset;

    /**
     * Current temporary Y offset - used when chromosome is dragged by user.
     * Zero normally and zeroed immediately when meiosis step changes.
    **/
    private int yTemporaryOffset;

    /**
     * Segment length in pixels
    **/
    private float lengthSegment;

    /**
     * Creates a new instance of MeiosisChromosomeModel with the given chromosome, meiosis model, type and previous
     * chromosome model.<p>
     *
     * The previous chromosome model varies with the type.  For an "original cell" chromosome
     * model, the previous chromosome model is null as the original cell is first.  For a "daughter cell"
     * chromosome model, the previous chromosome model is the "original cell" chromosome model.  For a
     * "gamete" chromosome model, the previous chromosome model is the daughter cell.<p>
     *
     * The previous chromosome model is used to make sure that the transition from one state to the next
     * across significant boundaries is smooth during meiosis animation.  For example, when transitioning
     * from the original cell to 2 daughter cells, the chromosome model(s) used changes from the single
     * original cell chromosome model to the 2 daughter cell chromosome models.  Ditto when going from
     * daughter cell chromosome models to gamete chromosome models.<p>
     *
     * @param		aMeiosisModel MeiosisModel - the parent meiosis model, may not be null
     * @param		anOrganismChromosome OrganismChromosome - the organism chromosome upon which to base model, may not be null
     * @param		aMeiosisChromosomeModelType int - the type of this chromosome model (original, daughter, gamete, etc.)
     * @param		aPreviousMeiosisChromosomeModel MeiosisChromosomeModel - the previous chromosome model chronologically.
     * @param		aPreviousMeiosisChromosomeModelStrand int - the strand of previous chromosome model chronologically from which this model is formed (FIRST_STRAND, SECOND_STRAND, etc.)
     * @param		anEnclosingViewRectangle Rectangle - an enclosing rectangle for the chromosome's view
     * @param		aChromosomeVisiblePairNumber int - the number of the chromosome pair that this model is part of (e.g. 1) - zero if not visible
     * @param		aColor Color - the color of the chromosome
     * @exception	IllegalArgumentException - illegal argument
    **/
    public MeiosisChromosomeModel(MeiosisModel aMeiosisModel,
                                  OrganismChromosome anOrganismChromosome,
                                  int aMeiosisChromosomeModelType,
                                  MeiosisChromosomeModel aPreviousMeiosisChromosomeModel,
                                  int aPreviousMeiosisChromosomeModelStrand,
                                  Rectangle anEnclosingViewRectangle,
                                  int aVisibleChromosomePairNumber,
                                  Color aColor)
    {
        if (aMeiosisModel == null)
        {
            throw new IllegalArgumentException("input aMeiosisModel null");
        }
        if (anOrganismChromosome == null)
        {
            throw new IllegalArgumentException("input anOrganismChromosome null");
        }
        if (aMeiosisChromosomeModelType < ORIG_CELL_CHROMOSOME || aMeiosisChromosomeModelType > BOTTOM_RIGHT_GAMETE_CHROMOSOME)
        {
            throw new IllegalArgumentException("input aMeiosisChromosomeModelType illegal");
        }
        if (aMeiosisChromosomeModelType != ORIG_CELL_CHROMOSOME && aPreviousMeiosisChromosomeModel == null)
        {
            throw new IllegalArgumentException("input aPreviousMeiosisChromosomeModel null illegally");
        }
        if (anEnclosingViewRectangle == null)
        {
            throw new IllegalArgumentException("input anEnclosingViewRectangle null");
        }
        if (aVisibleChromosomePairNumber < 0)
        {
            throw new IllegalArgumentException("input aVisibleChromosomePairNumber illegal");
        }
        if (aColor == null)
        {
            throw new IllegalArgumentException("input aColor null");
        }

        // Allocate static float arrays if not already allocated
        if (xCentromereFloat == null)
        {
            // Maximum number of meiosis steps is 40
            // Maximum number of P strand segments is 20
            // Maximum number of Q strand segments is 42
            xCentromereFloat = new float[40];
            yCentromereFloat = new float[40];
            anglePStrandAtCentromereFloat = new float[40];
            angleQStrandAtCentromereFloat = new float[40];

            xPStrandOnePointsFloat = new float[40][20];
            yPStrandOnePointsFloat = new float[40][20];
            xPStrandTwoPointsFloat = new float[40][20];
            yPStrandTwoPointsFloat = new float[40][20];

            xQStrandOnePointsFloat = new float[40][42];
            yQStrandOnePointsFloat = new float[40][42];
            xQStrandTwoPointsFloat = new float[40][42];
            yQStrandTwoPointsFloat = new float[40][42];

            prevStepPAnglesOneFloat = new float[20];
            prevStepPAnglesTwoFloat = new float[20];
            prevStepQAnglesOneFloat = new float[42];
            prevStepQAnglesTwoFloat = new float[42];
        }

        meiosisModel = aMeiosisModel;
        chromosome = anOrganismChromosome;
        chromosomeModelType = aMeiosisChromosomeModelType;
        previousMeiosisChromosomeModel = aPreviousMeiosisChromosomeModel;
        visibleChromosomePairNumber = aVisibleChromosomePairNumber;
        xTemporaryOffset = 0;
        yTemporaryOffset = 0;
        
    

        // Determine the number of meiosis steps for this chromosome model.  The number varies with
        // the type of chromosome model.  The offset is used to speed up lookup and reduce memory usage.
        switch (chromosomeModelType)
        {
            case ORIG_CELL_CHROMOSOME:
                numberOfMeiosisSteps = 31;	// steps 0 to 30
                offsetMeiosisStep = 0;		// array index = meiosis step
                doubleStrand = true;		// two strands for steps 10 to 30
                previousMeiosisChromosomeModelStrand = UNSPECIFIED_STRAND;	 // force to unspecified
                break;

            case LEFT_DAUGHTER_CELL_CHROMOSOME:
            case RIGHT_DAUGHTER_CELL_CHROMOSOME:
                numberOfMeiosisSteps = 40;	// steps 31 to 70
                offsetMeiosisStep = 31;		// array index = meiosis step - 31
                doubleStrand = true;		// two strands for steps 31 to 70
                previousMeiosisChromosomeModelStrand = UNSPECIFIED_STRAND;	 // force to unspecified
                break;

            case TOP_LEFT_GAMETE_CHROMOSOME:
            case BOTTOM_LEFT_GAMETE_CHROMOSOME:
            case TOP_RIGHT_GAMETE_CHROMOSOME:
            case BOTTOM_RIGHT_GAMETE_CHROMOSOME:
                numberOfMeiosisSteps = 30;	// steps 71 to 100
                offsetMeiosisStep = 71;		// array index = meiosis step - 71
                doubleStrand = false;		// single strand from steps 71 to 100
                previousMeiosisChromosomeModelStrand = aPreviousMeiosisChromosomeModelStrand;
                break;

            default:
                throw new IllegalArgumentException("input aMeiosisChromosomeModelType illegal");
        }

        // Determine number of strand segments
        switch (chromosome.getImageNumber())
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

        // Calculate limiting view dimension, using only the width and height (assume top left is 0,0)
        if (anEnclosingViewRectangle.width < anEnclosingViewRectangle.height)
        {
            limitingViewDimension = anEnclosingViewRectangle.width;
        }
        else
        {
            limitingViewDimension = anEnclosingViewRectangle.height;
        }

        // Chromosome segment length and width
        lengthSegment = (float) (((double) limitingViewDimension) / 160.0);

        // Allocate and randomly determine centromere locations and angles
        xCentromere = new int[numberOfMeiosisSteps];
        yCentromere = new int[numberOfMeiosisSteps];
        angleLastStepPStrandAtCentromere = (float) 0.0;
        angleLastStepQStrandAtCentromere = (float) 0.0;

        randomlySetCentromereLocationAndAngle();

        // Allocate and randomly set strand locations
        xPStrandOnePoints = new int[numberOfMeiosisSteps][numberOfPStrandSegments];
        yPStrandOnePoints = new int[numberOfMeiosisSteps][numberOfPStrandSegments];
        if (doubleStrand)
        {
            xPStrandTwoPoints = new int[numberOfMeiosisSteps][numberOfPStrandSegments];
            yPStrandTwoPoints = new int[numberOfMeiosisSteps][numberOfPStrandSegments];
        }

        xQStrandOnePoints = new int[numberOfMeiosisSteps][numberOfQStrandSegments];
        yQStrandOnePoints = new int[numberOfMeiosisSteps][numberOfQStrandSegments];
        if (doubleStrand)
        {
            xQStrandTwoPoints = new int[numberOfMeiosisSteps][numberOfQStrandSegments];
            yQStrandTwoPoints = new int[numberOfMeiosisSteps][numberOfQStrandSegments];
        }

        randomlySetStrandPoints();

        // Set alleles and colors along strands
        int i;
        allelesPStrandOne = new OrganismAllele[numberOfPStrandSegments];
  
        allelesQStrandOne = new OrganismAllele[numberOfQStrandSegments];
        colorsPStrandOne = new Color[numberOfPStrandSegments];
        colorsQStrandOne = new Color[numberOfQStrandSegments];

        if (doubleStrand)
        {
            allelesPStrandTwo = new OrganismAllele[numberOfPStrandSegments];
            allelesQStrandTwo = new OrganismAllele[numberOfQStrandSegments];
            colorsPStrandTwo = new Color[numberOfPStrandSegments];
            colorsQStrandTwo = new Color[numberOfQStrandSegments];

        }

        // Determine allele and color vectors
        setAlleleAndColorVectors(aColor);
    }

    /**
     * Delete this chromosome model.  Assume the meiosis model is calling this method
     * and therefore we don't need to notify it.
    **/
    public void delete()
    {
        meiosisModel = null;
        chromosome = null;

        // TBD - more?  probably...
    }

    /**
     * Get the visible chromosome pair number
     *
     * @return     int - visible chromosome pair number
    **/
    int getVisibleChromosomePairNumber()
    {
        return visibleChromosomePairNumber;
    }

    /**
     * Set chromosome model type.  For now, it only works for
     * daughter cells.
     *
     * @param		newType int - new type of chromosome model
    **/
    public void setMeiosisChromosomeModelType(int newType)
    {
        if (newType == chromosomeModelType)
        {
            return;
        }
        if (chromosomeModelType == LEFT_DAUGHTER_CELL_CHROMOSOME ||
            chromosomeModelType == RIGHT_DAUGHTER_CELL_CHROMOSOME)
        {
            if (newType != LEFT_DAUGHTER_CELL_CHROMOSOME &&
                newType != RIGHT_DAUGHTER_CELL_CHROMOSOME)
            {
                throw new IllegalArgumentException("input newType illegal");
            }
        }
        if (chromosomeModelType == TOP_LEFT_GAMETE_CHROMOSOME ||
            chromosomeModelType == BOTTOM_LEFT_GAMETE_CHROMOSOME ||
            chromosomeModelType == TOP_RIGHT_GAMETE_CHROMOSOME ||
            chromosomeModelType == BOTTOM_RIGHT_GAMETE_CHROMOSOME)
        {
            if (newType != TOP_LEFT_GAMETE_CHROMOSOME &&
                newType != BOTTOM_LEFT_GAMETE_CHROMOSOME &&
                newType != TOP_RIGHT_GAMETE_CHROMOSOME &&
                newType != BOTTOM_RIGHT_GAMETE_CHROMOSOME)
            {
                throw new IllegalArgumentException("input newType illegal");
            }
        }

        // Okay to change chromosome model type
        chromosomeModelType = newType;
        randomlySetCentromereLocationAndAngle();
        randomlySetStrandPoints();
    }

    /**
     * Set the previous chromosome model of this chromosome model.  Currently
     * this only works for gamete chromosome models.
     *
    **/
    public void setPreviousMeiosisChromosomeModel(MeiosisChromosomeModel aPreviousMeiosisChromosomeModel)
    {
        if (previousMeiosisChromosomeModel == aPreviousMeiosisChromosomeModel)
        {
            return;
        }
        if (chromosomeModelType != TOP_LEFT_GAMETE_CHROMOSOME &&
            chromosomeModelType != BOTTOM_LEFT_GAMETE_CHROMOSOME &&
            chromosomeModelType != TOP_RIGHT_GAMETE_CHROMOSOME &&
            chromosomeModelType != BOTTOM_RIGHT_GAMETE_CHROMOSOME)
        {
            throw new IllegalArgumentException("Cannot set the previous chromosome of this chromosome model");
        }

        // Okay to set previous chromosome model
        previousMeiosisChromosomeModel = aPreviousMeiosisChromosomeModel;
        randomlySetCentromereLocationAndAngle();
        randomlySetStrandPoints();
        setAlleleAndColorVectors(null);
    }

    /**
     * Set the enclosing rectangle for the view containing a display of this chromosome model.
     * Note that this is not always the rectangle enclosing the cell displaying this chromosome.
     * For example, during the latter parts of meiosis there are 2 or 4 "cells" being shown
     * within the single enclosing view rectangle.<p>
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

        xTemporaryOffset = 0;
        yTemporaryOffset = 0;
    }

    /**
     * Get whether this model is visible (true) or not visible (false).
    **/
    public boolean isVisible()
    {
        if (visibleChromosomePairNumber == 0)
        {
            return false;
        }

        return true;
    }

    /**
     * Randomly set the location and angle for a centromere of this chromosome
     * over the course of this chromosome model's .<p>
    **/
    void randomlySetCentromereLocationAndAngle()
    {
        // Return immediately if this chromosome model is not visible
        if (visibleChromosomePairNumber == 0)
        {
            return;
        }

        int i, j;
        float nextFloat;
        float angle, centromereRange;
        float scaleFactor;
        float xDelta, yDelta;
        float anglePStrandAtCentromereDelta, angleQStrandAtCentromereDelta;
        float xMin, yMin, xRange, yRange;

        float xMiddle = (float) (((double) limitingViewDimension) / 2.0);
        float yMiddle = (float) (((double) limitingViewDimension) / 2.0);

        int numberOfVisibleChromosomePairs = meiosisModel.getNumberOfVisibleChromosomePairs();

        if (chromosomeModelType == ORIG_CELL_CHROMOSOME)
        {
            // Randomly determine initial location and angle of centromere.
            // Force centromere location to be less than half
            // the cell radius from the center of the cell.
            nextFloat = random.nextFloat();
            centromereRange = nextFloat * (float)(((double) limitingViewDimension) / 6.0);

            nextFloat = random.nextFloat();
            angle = nextFloat * TWO_PI;

            xCentromereFloat[0] = xMiddle + (float)(((double)centromereRange) * Math.cos((double)angle));
            yCentromereFloat[0] = yMiddle + (float)(((double)centromereRange) * Math.sin((double)angle));
            anglePStrandAtCentromereFloat[0] = angle;
            angleQStrandAtCentromereFloat[0] = (float) (angle - Math.PI);

            // Calculate location and angle of centromere at step 30 of meiosis
            xCentromereFloat[numberOfMeiosisSteps-1] = xMiddle;
            yCentromereFloat[numberOfMeiosisSteps-1] =
            (float) (((double)((visibleChromosomePairNumber * 2) - 1) * ((double)limitingViewDimension)) /
                     ((double)(2 * numberOfVisibleChromosomePairs)));

            anglePStrandAtCentromereFloat[numberOfMeiosisSteps-1] = ONE_AND_HALF_PI;
            angleQStrandAtCentromereFloat[numberOfMeiosisSteps-1] = HALF_PI;

            // Determine remaining centromere locations and angles
            // by interpolating between the 0 and 30 locations and angles.
            xDelta = xCentromereFloat[numberOfMeiosisSteps-1] - xCentromereFloat[0];
            yDelta = yCentromereFloat[numberOfMeiosisSteps-1] - yCentromereFloat[0];
            anglePStrandAtCentromereDelta = anglePStrandAtCentromereFloat[numberOfMeiosisSteps-1]
                                            - anglePStrandAtCentromereFloat[0];
            angleQStrandAtCentromereDelta = angleQStrandAtCentromereFloat[numberOfMeiosisSteps-1]
                                            - angleQStrandAtCentromereFloat[0];

            // Calculate locations and angles using static float arrays
            for (i=1;i<numberOfMeiosisSteps-1;i++)
            {
                scaleFactor = (float) (((double) i) / (double)(numberOfMeiosisSteps-2));
                xCentromereFloat[i] = xCentromereFloat[0] + (float)(scaleFactor * xDelta);
                yCentromereFloat[i] = yCentromereFloat[0] + (float)(scaleFactor * yDelta);
                anglePStrandAtCentromereFloat[i] = anglePStrandAtCentromereFloat[0]
                                                   + (float)(scaleFactor * anglePStrandAtCentromereDelta);
                angleQStrandAtCentromereFloat[i] = angleQStrandAtCentromereFloat[0]
                                                   + (float)(scaleFactor * angleQStrandAtCentromereDelta);
            }

            // Copy locations into instance int arrays
            for (i=0;i<numberOfMeiosisSteps;i++)
            {
                xCentromere[i] = (int)xCentromereFloat[i];
                yCentromere[i] = (int)yCentromereFloat[i];
            }

            // Save angles at last step so next chromosome can refer to them
            angleLastStepPStrandAtCentromere = anglePStrandAtCentromereFloat[numberOfMeiosisSteps-1];
            angleLastStepQStrandAtCentromere = angleQStrandAtCentromereFloat[numberOfMeiosisSteps-1];
        }
        else if (chromosomeModelType == LEFT_DAUGHTER_CELL_CHROMOSOME ||
                 chromosomeModelType == RIGHT_DAUGHTER_CELL_CHROMOSOME)
        {
            // Get position and angle of chromosome at last step of previous stage of meiosis (step 30)
            // and make that posiiton and angle the location and angle of the first step of this
            // stage of the meiosis (step 31).  In other words, the locations and angles at steps 30
            // and 31 of the animation are identical.
            xCentromereFloat[0] = (float)previousMeiosisChromosomeModel.getXCentromereAtStep(30);
            yCentromereFloat[0] = (float)previousMeiosisChromosomeModel.getYCentromereAtStep(30);
            anglePStrandAtCentromereFloat[0] = (float)previousMeiosisChromosomeModel.getAnglePStrandAtCentromereAtLastStep();
            angleQStrandAtCentromereFloat[0] = (float)previousMeiosisChromosomeModel.getAngleQStrandAtCentromereAtLastStep();

            // Calculate position of chromosome at step 40 of the meiosis
            if (chromosomeModelType == LEFT_DAUGHTER_CELL_CHROMOSOME)
            {
                xCentromereFloat[9] = (float) (((double) limitingViewDimension) / 4.0);
            }
            else
            {
                xCentromereFloat[9] = (float) (3.0 * (((double) limitingViewDimension) / 4.0));
            }
            yCentromereFloat[9] = (float) ((yCentromereFloat[0] + (3.0 * ((double) limitingViewDimension) / 2.0))/4.0);
            if (chromosomeModelType == LEFT_DAUGHTER_CELL_CHROMOSOME)
            {
                anglePStrandAtCentromereFloat[9] = LEFT_P_ANGLE[9];
                angleQStrandAtCentromereFloat[9] = LEFT_Q_ANGLE[9];
            }
            else
            {
                anglePStrandAtCentromereFloat[19] = RIGHT_P_ANGLE[9];
                angleQStrandAtCentromereFloat[19] = RIGHT_Q_ANGLE[9];
            }

            // Determine centromere locations and angles between steps 0 and 19
            // by interpolating between the 0 and 19 locations and angles.
            xDelta = xCentromereFloat[9] - xCentromereFloat[0];
            yDelta = yCentromereFloat[9] - yCentromereFloat[0];

            for (i=1;i<9;i++)
            {
                scaleFactor = (float) (((double) i) / 8.0);
                xCentromereFloat[i] = xCentromereFloat[0] + (float)(scaleFactor * xDelta);
                yCentromereFloat[i] = yCentromereFloat[0] + (float)(scaleFactor * yDelta);
                if (chromosomeModelType == LEFT_DAUGHTER_CELL_CHROMOSOME)
                {
                    anglePStrandAtCentromereFloat[i] = LEFT_P_ANGLE[i];
                    angleQStrandAtCentromereFloat[i] = LEFT_Q_ANGLE[i];
                }
                else
                {
                    anglePStrandAtCentromereFloat[i] = RIGHT_P_ANGLE[i];
                    angleQStrandAtCentromereFloat[i] = RIGHT_Q_ANGLE[i];
                }
            }

            // Calculate position of chromosome at last step of this state of meiosis (step 70)
            xMin = (float) (((double)limitingViewDimension)/8.0);
            xRange = (float) (2.0 * xMin);
            xDelta = (float) (((double)xRange) / (((double)numberOfVisibleChromosomePairs) + 1.0));	 // Distance between centromeres
            if (chromosomeModelType == LEFT_DAUGHTER_CELL_CHROMOSOME)
            {
                xCentromereFloat[39] = (float) ((((double) limitingViewDimension) / 2.0) - xMin -
                                                ((double) ((numberOfVisibleChromosomePairs - visibleChromosomePairNumber + 1) * xDelta)));
                anglePStrandAtCentromereFloat[39] = LEFT_P_ANGLE[39];
                angleQStrandAtCentromereFloat[39] = LEFT_Q_ANGLE[39];
            }
            else
            {
                xCentromereFloat[39] = (float) ((((double) limitingViewDimension) / 2.0) + xMin +
                                                ((double) (visibleChromosomePairNumber * xDelta)));
                anglePStrandAtCentromereFloat[39] = RIGHT_P_ANGLE[39];
                angleQStrandAtCentromereFloat[39] = RIGHT_Q_ANGLE[39];
            }
            yCentromereFloat[39] = (float) (((double) limitingViewDimension) / 2.0);

            // Determine remaining centromere locations and angles
            // by interpolating between the 9 and 39 locations and angles.
            xDelta = xCentromereFloat[39] - xCentromereFloat[9];
            yDelta = yCentromereFloat[39] - yCentromereFloat[9];
            for (i=10;i<39;i++)
            {
                scaleFactor = (float) (((double) (i-9)) / 29.0);
                xCentromereFloat[i] = xCentromereFloat[9] + (float)(scaleFactor * xDelta);
                yCentromereFloat[i] = yCentromereFloat[9] + (float)(scaleFactor * yDelta);
                if (chromosomeModelType == LEFT_DAUGHTER_CELL_CHROMOSOME)
                {
                    anglePStrandAtCentromereFloat[i] = LEFT_P_ANGLE[i];
                    angleQStrandAtCentromereFloat[i] = LEFT_Q_ANGLE[i];
                }
                else
                {
                    anglePStrandAtCentromereFloat[i] = RIGHT_P_ANGLE[i];
                    angleQStrandAtCentromereFloat[i] = RIGHT_Q_ANGLE[i];
                }
            }

            // Copy locations into instance int arrays
            for (i=0;i<numberOfMeiosisSteps;i++)
            {
                xCentromere[i] = (int)xCentromereFloat[i];
                yCentromere[i] = (int)yCentromereFloat[i];
            }

            // Save away the last angles
            angleLastStepPStrandAtCentromere = anglePStrandAtCentromereFloat[numberOfMeiosisSteps-1];
            angleLastStepQStrandAtCentromere = angleQStrandAtCentromereFloat[numberOfMeiosisSteps-1];
        }
        else if (chromosomeModelType == TOP_LEFT_GAMETE_CHROMOSOME ||
                 chromosomeModelType == BOTTOM_LEFT_GAMETE_CHROMOSOME ||
                 chromosomeModelType == TOP_RIGHT_GAMETE_CHROMOSOME ||
                 chromosomeModelType == BOTTOM_RIGHT_GAMETE_CHROMOSOME)
        {
            // Get position and angle of chromosome at last step of previous stage of meiosis (step 70)
            // and make that posiiton and angle the location and angle of the first step of this
            // stage of the meiosis (step 71).  In other words, the locations and angles at steps 70
            // and 71 of the animation are identical.
            xCentromereFloat[0] = (float)previousMeiosisChromosomeModel.getXCentromereAtStep(70);
            yCentromereFloat[0] = (float)previousMeiosisChromosomeModel.getYCentromereAtStep(70);
            anglePStrandAtCentromereFloat[0] = previousMeiosisChromosomeModel.getAnglePStrandAtCentromereAtLastStep();
            angleQStrandAtCentromereFloat[0] = previousMeiosisChromosomeModel.getAngleQStrandAtCentromereAtLastStep();

            // Calculate position and angle of chromosome at end of vertical movement (step 80)
            xCentromereFloat[9] = xCentromereFloat[0];

            if (chromosomeModelType == TOP_LEFT_GAMETE_CHROMOSOME ||
                chromosomeModelType == TOP_RIGHT_GAMETE_CHROMOSOME)
            {
                yCentromereFloat[9] = (float) (((double) limitingViewDimension) / 4.0);
                anglePStrandAtCentromereFloat[9] = TOP_P_ANGLE[9];
                angleQStrandAtCentromereFloat[9] = TOP_Q_ANGLE[9];
            }
            else
            {
                yCentromereFloat[9] = (float) (((double) (3 * limitingViewDimension)) / 4.0);
                anglePStrandAtCentromereFloat[9] = BOTTOM_P_ANGLE[9];
                angleQStrandAtCentromereFloat[9] = BOTTOM_Q_ANGLE[9];
            }

            // Determine first 10 centromere locations and angles
            // by interpolating between the 0 and 9 locations and angles.
            xDelta = xCentromereFloat[9] - xCentromereFloat[0];
            yDelta = yCentromereFloat[9] - yCentromereFloat[0];

            for (i=1;i<9;i++)
            {
                scaleFactor = (float) (((double) i) / 9.0);
                xCentromereFloat[i] = xCentromereFloat[0] + (float)(scaleFactor * xDelta);
                yCentromereFloat[i] = yCentromereFloat[0] + (float)(scaleFactor * yDelta);

                if (chromosomeModelType == TOP_LEFT_GAMETE_CHROMOSOME ||
                    chromosomeModelType == TOP_RIGHT_GAMETE_CHROMOSOME)
                {
                    anglePStrandAtCentromereFloat[i] = TOP_P_ANGLE[i];
                    angleQStrandAtCentromereFloat[i] = TOP_Q_ANGLE[i];
                }
                else
                {
                    anglePStrandAtCentromereFloat[i] = BOTTOM_P_ANGLE[i];
                    angleQStrandAtCentromereFloat[i] = BOTTOM_Q_ANGLE[i];
                }
            }

            // Determine random locations and angles for last step of meiosis
            nextFloat = random.nextFloat();
            xDelta = (float) ((((double) limitingViewDimension) / 8.0) * (((double)nextFloat) - 0.5));

            nextFloat = random.nextFloat();
            yDelta = (float) ((((double) limitingViewDimension) / 8.0) * (((double)nextFloat) - 0.5));

            if (chromosomeModelType == TOP_LEFT_GAMETE_CHROMOSOME ||
                chromosomeModelType == BOTTOM_LEFT_GAMETE_CHROMOSOME)
            {
                xCentromereFloat[29] = (float) ((((double) limitingViewDimension) / 4.0) + xDelta);
            }
            else
            {
                xCentromereFloat[29] = (float) ((3.0 * ((double) limitingViewDimension) / 4.0) + xDelta);
            }

            if (chromosomeModelType == TOP_LEFT_GAMETE_CHROMOSOME ||
                chromosomeModelType == TOP_RIGHT_GAMETE_CHROMOSOME)
            {
                yCentromereFloat[29] = (float) ((((double) limitingViewDimension) / 4.0) + yDelta);
            }
            else
            {
                yCentromereFloat[29] = (float) ((3.0 * ((double) limitingViewDimension) / 4.0) + yDelta);
            }

            // Recalculate xDelta and yDelta
            xDelta = xCentromereFloat[29] - xCentromereFloat[9];
            yDelta = yCentromereFloat[29] - yCentromereFloat[9];

            nextFloat = random.nextFloat() - (float)0.5;   // range is pi to -pi
            float lastPAngle = (float) (nextFloat * TWO_PI);
            float pAngleDelta = lastPAngle - anglePStrandAtCentromereFloat[9];

            float lastQAngle = lastPAngle - (float)Math.PI;
            float qAngleDelta = lastQAngle - angleQStrandAtCentromereFloat[9];

            for (i=10;i<30;i++)
            {
                scaleFactor = (float) (((double) (i-9)) / 20.0);
                xCentromereFloat[i] = xCentromereFloat[9] + (float)(scaleFactor * xDelta);
                yCentromereFloat[i] = yCentromereFloat[9] + (float)(scaleFactor * yDelta);

                anglePStrandAtCentromereFloat[i] = anglePStrandAtCentromereFloat[9] + (float)(scaleFactor * pAngleDelta);
                angleQStrandAtCentromereFloat[i] = angleQStrandAtCentromereFloat[9] + (float)(scaleFactor * qAngleDelta);
            }

            // Copy locations into instance int arrays
            for (i=0;i<numberOfMeiosisSteps;i++)
            {
                xCentromere[i] = (int)xCentromereFloat[i];
                yCentromere[i] = (int)yCentromereFloat[i];
            }

            // Save away the last angles
            angleLastStepPStrandAtCentromere = anglePStrandAtCentromereFloat[numberOfMeiosisSteps-1];
            angleLastStepQStrandAtCentromere = angleQStrandAtCentromereFloat[numberOfMeiosisSteps-1];
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
     * Get the X centromere location as an int at the given meiosis step
     * (NOT the array index!!).
     *
     * @param		aStep int - step of meiosis (not array index!!)
     * @return		int - x location of centromere
     * @exception	IllegalArgumentException - illegal step input
    **/
    public int getXCentromereAtStep(int aStep)
    {
        int arrayIndex = aStep - offsetMeiosisStep;

        if (arrayIndex < 0 || arrayIndex > numberOfMeiosisSteps-1)
        {
            throw new IllegalArgumentException("invalid aStep");
        }

        return xCentromere[arrayIndex];
    }

    /**
     * Get the Y centromere location as an int at the given meiosis step
     * (NOT the array index!!).
     *
     * @param		aStep int - step of meiosis (not array index!!)
     * @return		int - y location of centromere
     * @exception	IllegalArgumentException - illegal step input
    **/
    public int getYCentromereAtStep(int aStep)
    {
        int arrayIndex = aStep - offsetMeiosisStep;

        if (arrayIndex < 0 || arrayIndex > numberOfMeiosisSteps-1)
        {
            throw new IllegalArgumentException("invalid aStep");
        }

        return yCentromere[arrayIndex];
    }

    /**
     * Get the angle of the P strand of the chromosome at the
     * centromere at the last step of this chromosome's portion
     * of meiosis.
     *
     * @return		float - angle of P strand of chromosome at centromere in radians at the last step of meiosis
    **/
    public float getAnglePStrandAtCentromereAtLastStep()
    {
        return angleLastStepPStrandAtCentromere;
    }

    /**
     * Get the angle of the Q strand of the chromosome at the
     * centromere at the last step of this chromosome's portion
     * of meiosis.
     *
     * @return		float - angle of Q strand of chromosome at centromere in radians at the last step of meiosis
    **/
    public float getAngleQStrandAtCentromereAtLastStep()
    {
        return angleLastStepQStrandAtCentromere;
    }

    /**
     * Get a random angle given the starting angle, the previous angle and a maxAngleIndex.<p>
     *
     * The starting angle is the "target" angle around which we want to randomly
     * choose an actual angle.<p>
     *
     * The previous angle is the angle that the segment had in the previous meiosis step.<p>
     *
     * The maxAngleIndex is the index into the maxAngle array for the appropriate
     * step of meiosis.  The index is the meiosis step.
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
        float deltaAngle = (float) ((random.nextFloat() - (float)0.5) * MAX_ANGLE[maxAngleIndex]);

        // Return new angle by applying delta to the starting angle and then averaging with previous angle
        return(float)(((3.0 * (startingAngle + deltaAngle)) + previousAngle)/4.0);
    }

    /**
     * Randomly set the strand points for all appropriate meiosis steps, using
     * the location and angle of the centromere as a starting point for each step.
    **/
    void randomlySetStrandPoints()
    {
        // Return immediately if this chromosome model is not visible
        if (visibleChromosomePairNumber == 0)
        {
            return;
        }

        int i, iStep;
        float lastAnglePStrandOne, currentAnglePStrandOne;
        float lastAngleQStrandOne, currentAngleQStrandOne;
        float lastAnglePStrandTwo = (float)0.0;
        float currentAnglePStrandTwo = (float)0.0;
        float lastAngleQStrandTwo = (float)0.0;
        float currentAngleQStrandTwo = (float)0.0;
        float nudgeStrandsApartAngle = (float)0.3;

        for (iStep=0;iStep<numberOfMeiosisSteps;iStep++)
        {
            // Calculate positions and angles of 2 P strands.
            // They start off near the centromere with identical positions and angles
            // and then randomly diverge as we go out the strands.
            currentAnglePStrandOne = anglePStrandAtCentromereFloat[iStep];
            if (doubleStrand)
            {
                currentAnglePStrandOne += nudgeStrandsApartAngle;
            }
            lastAnglePStrandOne = currentAnglePStrandOne;
            prevStepPAnglesOneFloat[0] = currentAnglePStrandOne;

            xPStrandOnePointsFloat[iStep][0] = xCentromereFloat[iStep] + (float)(lengthSegment * Math.cos(lastAnglePStrandOne));
            yPStrandOnePointsFloat[iStep][0] = yCentromereFloat[iStep] + (float)(lengthSegment * Math.sin(lastAnglePStrandOne));
            xPStrandOnePoints[iStep][0] = (int) xPStrandOnePointsFloat[iStep][0];
            yPStrandOnePoints[iStep][0] = (int) yPStrandOnePointsFloat[iStep][0];

            if (doubleStrand)
            {
                currentAnglePStrandTwo = anglePStrandAtCentromereFloat[iStep] - nudgeStrandsApartAngle;
                lastAnglePStrandTwo = currentAnglePStrandTwo;
                prevStepPAnglesTwoFloat[0] = currentAnglePStrandTwo;

                xPStrandTwoPointsFloat[iStep][0] = xPStrandOnePointsFloat[iStep][0];
                yPStrandTwoPointsFloat[iStep][0] = yPStrandOnePointsFloat[iStep][0];
                xPStrandTwoPoints[iStep][0] = xPStrandOnePoints[iStep][0];
                yPStrandTwoPoints[iStep][0] = yPStrandOnePoints[iStep][0];
            }

            // Loop through points of each strand calculating centerpoints
            for (i=1;i<numberOfPStrandSegments;i++)
            {
                // P Strand One

                // Randomly choose an angle for the next segment of P strand one
                lastAnglePStrandOne = currentAnglePStrandOne;
                if (iStep > 0)
                {
                    currentAnglePStrandOne = getRandomAngle(lastAnglePStrandOne,
                                                            prevStepPAnglesOneFloat[i],
                                                            iStep+offsetMeiosisStep);
                }
                else
                {
                    currentAnglePStrandOne = getRandomAngle(lastAnglePStrandOne,
                                                            lastAnglePStrandOne,
                                                            iStep+offsetMeiosisStep);
                }
                prevStepPAnglesOneFloat[i] = currentAnglePStrandOne;

                // Calculate position of next center point using currentAngleStrandOne
                xPStrandOnePointsFloat[iStep][i] = xPStrandOnePointsFloat[iStep][i-1] +
                                                   (float)(lengthSegment * Math.cos(currentAnglePStrandOne));
                yPStrandOnePointsFloat[iStep][i] = yPStrandOnePointsFloat[iStep][i-1] +
                                                   (float)(lengthSegment * Math.sin(currentAnglePStrandOne));

                // Convert values to int and store in array for use in drawing
                xPStrandOnePoints[iStep][i] = (int) xPStrandOnePointsFloat[iStep][i];
                yPStrandOnePoints[iStep][i] = (int) yPStrandOnePointsFloat[iStep][i];

                // P Strand Two
                if (doubleStrand)
                {
                    // Randomly choose an angle for the next segment of P strand two
                    lastAnglePStrandTwo = currentAnglePStrandTwo;
                    if (iStep > 0)
                    {
                        currentAnglePStrandTwo = getRandomAngle(lastAnglePStrandTwo,
                                                                prevStepPAnglesTwoFloat[i],
                                                                iStep+offsetMeiosisStep);
                    }
                    else
                    {
                        currentAnglePStrandTwo = getRandomAngle(lastAnglePStrandTwo,
                                                                lastAnglePStrandTwo,
                                                                iStep+offsetMeiosisStep);
                    }
                    prevStepPAnglesTwoFloat[i] = currentAnglePStrandTwo;

                    // Calculate position of next center point using currentAngleStrandTwo
                    xPStrandTwoPointsFloat[iStep][i] = xPStrandTwoPointsFloat[iStep][i-1] +
                                                       (float)(lengthSegment * Math.cos(currentAnglePStrandTwo));
                    yPStrandTwoPointsFloat[iStep][i] = yPStrandTwoPointsFloat[iStep][i-1] +
                                                       (float)(lengthSegment * Math.sin(currentAnglePStrandTwo));

                    // Convert values to int and store in array for use in drawing
                    xPStrandTwoPoints[iStep][i] = (int) xPStrandTwoPointsFloat[iStep][i];
                    yPStrandTwoPoints[iStep][i] = (int) yPStrandTwoPointsFloat[iStep][i];
                }
            }

            // Calculate positions and angles of 2 Q strands.
            // They start off near the centromere with identical positions and angles
            // and then randomly diverge as we go out the strands.
            currentAngleQStrandOne = angleQStrandAtCentromereFloat[iStep];
            if (doubleStrand)
            {
                currentAngleQStrandOne += nudgeStrandsApartAngle;
            }
            lastAngleQStrandOne = currentAngleQStrandOne;
            prevStepQAnglesOneFloat[0] = currentAngleQStrandOne;

            // Calculate first points of strands, which are identical
            xQStrandOnePointsFloat[iStep][0] = xCentromereFloat[iStep] +
                                               (float)(lengthSegment * Math.cos(lastAngleQStrandOne));
            yQStrandOnePointsFloat[iStep][0] = yCentromereFloat[iStep] +
                                               (float)(lengthSegment * Math.sin(lastAngleQStrandOne));
            xQStrandOnePoints[iStep][0] = (int) xQStrandOnePointsFloat[iStep][0];
            yQStrandOnePoints[iStep][0] = (int) yQStrandOnePointsFloat[iStep][0];

            if (doubleStrand)
            {
                currentAngleQStrandTwo = angleQStrandAtCentromereFloat[iStep] - nudgeStrandsApartAngle;
                lastAngleQStrandTwo = currentAngleQStrandTwo;;
                prevStepQAnglesTwoFloat[0] = currentAngleQStrandTwo;

                xQStrandTwoPointsFloat[iStep][0] = xQStrandOnePointsFloat[iStep][0];
                yQStrandTwoPointsFloat[iStep][0] = yQStrandOnePointsFloat[iStep][0];
                xQStrandTwoPoints[iStep][0] = xQStrandOnePoints[iStep][0];
                yQStrandTwoPoints[iStep][0] = yQStrandOnePoints[iStep][0];
            }

            // Loop through points of each strand calculating centerpoints
            for (i=1;i<numberOfQStrandSegments;i++)
            {
                // Q Strand One

                // Randomly choose an angle for the next segment of Q strand one
                lastAngleQStrandOne = currentAngleQStrandOne;
                if (iStep > 0)
                {
                    currentAngleQStrandOne = getRandomAngle(lastAngleQStrandOne,
                                                            prevStepQAnglesOneFloat[i],
                                                            iStep+offsetMeiosisStep);
                }
                else
                {
                    currentAngleQStrandOne = getRandomAngle(lastAngleQStrandOne,
                                                            lastAngleQStrandOne,
                                                            iStep+offsetMeiosisStep);
                }
                prevStepQAnglesOneFloat[i] = currentAngleQStrandOne;

                // Calculate position of next center point using currentAngleStrandOne
                xQStrandOnePointsFloat[iStep][i] = xQStrandOnePointsFloat[iStep][i-1] +
                                                   (float)(lengthSegment * Math.cos(currentAngleQStrandOne));
                yQStrandOnePointsFloat[iStep][i] = yQStrandOnePointsFloat[iStep][i-1] +
                                                   (float)(lengthSegment * Math.sin(currentAngleQStrandOne));

                // Convert values to int and store in array for use in drawing
                xQStrandOnePoints[iStep][i] = (int) xQStrandOnePointsFloat[iStep][i];
                yQStrandOnePoints[iStep][i] = (int) yQStrandOnePointsFloat[iStep][i];

                // Q Strand Two
                if (doubleStrand)
                {
                    // Randomly choose an angle for the next segment of Q strand two
                    lastAngleQStrandTwo = currentAngleQStrandTwo;
                    if (iStep > 0)
                    {
                        currentAngleQStrandTwo = getRandomAngle(lastAngleQStrandTwo,
                                                                prevStepQAnglesTwoFloat[i],
                                                                iStep+offsetMeiosisStep);
                    }
                    else
                    {
                        currentAngleQStrandTwo = getRandomAngle(lastAngleQStrandTwo,lastAngleQStrandTwo,
                                                                iStep+offsetMeiosisStep);
                    }
                    prevStepQAnglesTwoFloat[i] = currentAngleQStrandTwo;

                    // Calculate position of next center point using currentAngleStrandTwo
                    xQStrandTwoPointsFloat[iStep][i] = xQStrandTwoPointsFloat[iStep][i-1] +
                                                       (float)(lengthSegment * Math.cos(currentAngleQStrandTwo));
                    yQStrandTwoPointsFloat[iStep][i] = yQStrandTwoPointsFloat[iStep][i-1] +
                                                       (float)(lengthSegment * Math.sin(currentAngleQStrandTwo));

                    // Convert values to int and store in array for use in drawing
                    xQStrandTwoPoints[iStep][i] = (int) xQStrandTwoPointsFloat[iStep][i];
                    yQStrandTwoPoints[iStep][i] = (int) yQStrandTwoPointsFloat[iStep][i];
                }
            }
        }
    }

    /**
     * Get the array of int's for x coordinates of P strand one at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for x coordinates of P strand one
    **/
    public int[] getXPStrandOnePoints(int aMeiosisStep)
    {
        return xPStrandOnePoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Get the array of int's for y coordinates of P strand one at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for y coordinates of P strand one
    **/
    public int[] getYPStrandOnePoints(int aMeiosisStep)
    {
        return yPStrandOnePoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Get the array of int's for x coordinates of P strand two at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for x coordinates of P strand two
    **/
    public int[] getXPStrandTwoPoints(int aMeiosisStep)
    {
        return xPStrandTwoPoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Get the array of int's for y coordinates of P strand two at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for y coordinates of P strand two
    **/
    public int[] getYPStrandTwoPoints(int aMeiosisStep)
    {
        return yPStrandTwoPoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Get the array of int's for x coordinates of Q strand one at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for x coordinates of Q strand one
    **/
    public int[] getXQStrandOnePoints(int aMeiosisStep)
    {
        return xQStrandOnePoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Get the array of int's for y coordinates of Q strand one at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for y coordinates of Q strand one
    **/
    public int[] getYQStrandOnePoints(int aMeiosisStep)
    {
        return yQStrandOnePoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Get the array of int's for x coordinates of Q strand two at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for x coordinates of Q strand two
    **/
    public int[] getXQStrandTwoPoints(int aMeiosisStep)
    {
        return xQStrandTwoPoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Get the array of int's for y coordinates of Q strand two at the given meiosis step.
     *
     * @param		aMeiosisStep int - the meiosis step value (NOT the array index)
     * @return		int[] - array of int values for y coordinates of Q strand two
    **/
    public int[] getYQStrandTwoPoints(int aMeiosisStep)
    {
        return yQStrandTwoPoints[aMeiosisStep-offsetMeiosisStep];
    }

    /**
     * Set the vectors for the alleles and colors of this meiosis chromosome model.
     * Use the default color only if you don't have another way of determining the
     * color (e.g. using the previous chromosome model).<p>
     *
     * Note that calling this method after doing a crossing over on a daughter cell
     * meiosis chromosome model will undo the crossing over changes.  That may or may
     * not be what you intended, so be careful.<p>
     *
     * @param     aDefaultColor Color - use this color if you have no other choice, must be specified
     *									for ORIG_CELL_CHROMOSOME chromosome models
    **/
    public void setAlleleAndColorVectors(Color aDefaultColor)
    {
        int i;

        switch (chromosomeModelType)
        {
            case ORIG_CELL_CHROMOSOME:
            case LEFT_DAUGHTER_CELL_CHROMOSOME:
            case RIGHT_DAUGHTER_CELL_CHROMOSOME:
                {
                    // aDefaultColor may not be null
                    if (aDefaultColor == null && chromosomeModelType == ORIG_CELL_CHROMOSOME)
                    {
                        throw new IllegalArgumentException("aDefaultColor null for original cell chromosome");
                    }

                    // Determine allele vectors
                    double geneLocation;
                    int lengthOfChromosomeInBases = chromosome.getLengthInBases();
                    double numberOfTotalSegments = (double) (numberOfPStrandSegments + numberOfQStrandSegments);

                    Gene aGene;
                    int startIndexInHolderInBases, segmentLocation;

                    OrganismAllele anOrganismAllele;
                    Enumeration eOrganismAlleles = chromosome.getOrganismAlleles();
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
                           
                            allelesPStrandOne[segmentLocation] = anOrganismAllele;
                            if (doubleStrand)
                            {
                                allelesPStrandTwo[segmentLocation] = anOrganismAllele;
                            }
                        }
                        else if (segmentLocation-numberOfPStrandSegments < numberOfQStrandSegments)
                        {
                            allelesQStrandOne[segmentLocation-numberOfPStrandSegments] = anOrganismAllele;
                            if (doubleStrand)
                            {
                                allelesQStrandTwo[segmentLocation-numberOfPStrandSegments] = anOrganismAllele;
                            }
                        }
                        else
                        {
                            // Error
                            System.err.println("segmentLocation = " + segmentLocation);
                        }
                    }

                    // Determine color vectors
                    if (doubleStrand)
                    {
                        if (previousMeiosisChromosomeModel != null)
                        {
                            Color[] colorsPStrandPrevOne = previousMeiosisChromosomeModel.getStrandColors(P_STRAND_ONE);
                            Color[] colorsQStrandPrevOne = previousMeiosisChromosomeModel.getStrandColors(Q_STRAND_ONE);
                            Color[] colorsPStrandPrevTwo = previousMeiosisChromosomeModel.getStrandColors(P_STRAND_ONE);
                            Color[] colorsQStrandPrevTwo = previousMeiosisChromosomeModel.getStrandColors(Q_STRAND_ONE);

                            for (i=0;i<numberOfPStrandSegments;i++)
                            {
                                colorsPStrandOne[i] = colorsPStrandPrevOne[i];
                                colorsPStrandTwo[i] = colorsPStrandPrevTwo[i];
                            }
                            for (i=0;i<numberOfQStrandSegments;i++)
                            {
                                colorsQStrandOne[i] = colorsQStrandPrevOne[i];
                                colorsQStrandTwo[i] = colorsQStrandPrevTwo[i];
                            }
                        }
                        else if (aDefaultColor != null)
                        {
                        	//Alternate the color back;
                        	Color thisColor = Color.blue;
                        	if (aDefaultColor == Color.blue)
                        		thisColor = Color.orange;
                        	else
                        		thisColor = Color.blue;
                        		
                            for (i=0;i<numberOfPStrandSegments;i++)
                            {
                                colorsPStrandOne[i] = thisColor;
                                colorsPStrandTwo[i] = thisColor;
                            }

                            for (i=0;i<numberOfQStrandSegments;i++)
                            {
                                colorsQStrandOne[i] = thisColor;
                                colorsQStrandTwo[i] = thisColor;
                            }
                           
                        }
                    }
                    else
                    {
                        if (previousMeiosisChromosomeModel != null)
                        {
                            Color[] colorsPStrand = null, colorsQStrand = null;
                            if (previousMeiosisChromosomeModelStrand == FIRST_STRAND ||
                                previousMeiosisChromosomeModelStrand == UNSPECIFIED_STRAND)
                            {
                                colorsPStrand = previousMeiosisChromosomeModel.getStrandColors(P_STRAND_ONE);
                                colorsQStrand = previousMeiosisChromosomeModel.getStrandColors(Q_STRAND_ONE);
                            }
                            else
                            {
                                colorsPStrand = previousMeiosisChromosomeModel.getStrandColors(P_STRAND_TWO);
                                colorsQStrand = previousMeiosisChromosomeModel.getStrandColors(Q_STRAND_TWO);
                            }

                            for (i=0;i<numberOfPStrandSegments;i++)
                            {
                                colorsPStrandOne[i] = colorsPStrand[i];
                            }
                            for (i=0;i<numberOfQStrandSegments;i++)
                            {
                                colorsQStrandOne[i] = colorsQStrand[i];
                            }
                        }
                        else if (aDefaultColor != null)
                        {
                            for (i=0;i<numberOfPStrandSegments;i++)
                            {
                                colorsPStrandOne[i] = aDefaultColor;
                            }

                            for (i=0;i<numberOfQStrandSegments;i++)
                            {
                                colorsQStrandOne[i] = aDefaultColor;
                            }
                        }
                    }
                }
                break;

            case TOP_LEFT_GAMETE_CHROMOSOME:
            case BOTTOM_LEFT_GAMETE_CHROMOSOME:
            case TOP_RIGHT_GAMETE_CHROMOSOME:
            case BOTTOM_RIGHT_GAMETE_CHROMOSOME:
                {
                    // Always ignore aDefaultColor and use previous chromosome model.
                    // Copy alleles and colors from strand of previous chromosome model, as
                    // those alleles and colors may be mixed due to crossing over
                    OrganismAllele[] allelesPStrand = null, allelesQStrand = null;
                    Color[] colorsPStrand = null, colorsQStrand = null;
                    if (previousMeiosisChromosomeModelStrand == FIRST_STRAND ||
                        previousMeiosisChromosomeModelStrand == UNSPECIFIED_STRAND)
                    {
                        allelesPStrand = previousMeiosisChromosomeModel.getStrandAlleles(P_STRAND_ONE);
                        allelesQStrand = previousMeiosisChromosomeModel.getStrandAlleles(Q_STRAND_ONE);
                        colorsPStrand = previousMeiosisChromosomeModel.getStrandColors(P_STRAND_ONE);
                        colorsQStrand = previousMeiosisChromosomeModel.getStrandColors(Q_STRAND_ONE);
                    }
                    else
                    {
                        allelesPStrand = previousMeiosisChromosomeModel.getStrandAlleles(P_STRAND_TWO);
                        allelesQStrand = previousMeiosisChromosomeModel.getStrandAlleles(Q_STRAND_TWO);
                        colorsPStrand = previousMeiosisChromosomeModel.getStrandColors(P_STRAND_TWO);
                        colorsQStrand = previousMeiosisChromosomeModel.getStrandColors(Q_STRAND_TWO);
                    }

                    for (i=0;i<numberOfPStrandSegments;i++)
                    {
                        allelesPStrandOne[i] = allelesPStrand[i];
                        colorsPStrandOne[i] = colorsPStrand[i];
                    }
                    for (i=0;i<numberOfQStrandSegments;i++)
                    {
                        allelesQStrandOne[i] = allelesQStrand[i];
                        colorsQStrandOne[i] = colorsQStrand[i];
                    }
                }
                break;
        }
    }

    /**
     * Set the array of Alleles for the given strand.<p>
     *
     * @param       aStrand int - the strand
     * @param		OrganismAllele[] - array of Organism Alleles along given strand of chromosome
    **/
    public void setStrandAlleles(int aStrand, OrganismAllele[] alleles)
    {
        switch (aStrand)
        {
            case P_STRAND_ONE:
                allelesPStrandOne = alleles;
                break;
            case Q_STRAND_ONE:
                allelesQStrandOne = alleles;
                break;
            case P_STRAND_TWO:
                allelesPStrandTwo = alleles;
                break;
            case Q_STRAND_TWO:
                allelesQStrandTwo = alleles;
                break;
        }
    }

    /**
     * Get the array of Alleles for the given strand.<p>
     *
     * @param       aStrand int - the strand
     * @return		OrganismAllele[] - array of Organism Alleles along given strand of chromosome
    **/
    public OrganismAllele[] getStrandAlleles(int aStrand)
    {
        switch (aStrand)
        {
            case P_STRAND_ONE:
                return getPStrandOneAlleles();
            case Q_STRAND_ONE:
                return getQStrandOneAlleles();
            case P_STRAND_TWO:
                return getPStrandTwoAlleles();
            case Q_STRAND_TWO:
                return getQStrandTwoAlleles();
        }

        return null;
    }

    /**
     * Get the array of organism alleles for P strand one.  Elements are null if there is
     * no organism allele at that position, non-null if there is an allele there.
     *
     * @return		OrganismAllele[] - array of OrganismAllele values along P strand one of chromosome
    **/
    public OrganismAllele[] getPStrandOneAlleles()
    {
        return allelesPStrandOne;
    }
    
    public void setPStrandOneAlleles(OrganismAllele [] arr)
    {
    	allelesPStrandOne = arr;
    }

    /**
     * Get the array of organism alleles for P strand two.  Elements are null if there is
     * no organism allele at that position, non-null if there is an allele there.
     *
     * @return		OrganismAllele[] - array of OrganismAllele values along P strand two of chromosome
    **/
    public OrganismAllele[] getPStrandTwoAlleles()
    {
        return allelesPStrandTwo;
    }
    
    public void setPStrandTwoAlleles(OrganismAllele [] arr)
    {
    	allelesPStrandTwo = arr;
    }


    /**
     * Get the array of organism alleles for Q strand one.  Elements are null if there is
     * no organism allele at that position, non-null if there is an allele there.
     *
     * @return		OrganismAllele[] - array of OrganismAllele values along Q strand one of chromosome
    **/
    public OrganismAllele[] getQStrandOneAlleles()
    {
        return allelesQStrandOne;
    }
    
    public void setQStrandOneAlleles(OrganismAllele [] arr)
    {
    	allelesQStrandOne = arr;
    }

    /**
     * Get the array of organism alleles for Q strand two.  Elements are null if there is
     * no organism allele at that position, non-null if there is an allele there.
     *
     * @return		OrganismAllele[] - array of OrganismAllele values along Q strand two of chromosome
    **/
    public OrganismAllele[] getQStrandTwoAlleles()
    {
        return allelesQStrandTwo;
    }
    
    public void setQStrandTwoAlleles(OrganismAllele [] arr)
    {
    	allelesQStrandTwo = arr;
    }


    /**
     * Set the array of Colors for the given strand.<p>
     *
     * @param       aStrand int - the strand
     * @param		Color[] - array of Colors along given strand of chromosome
    **/
    public void setStrandColors(int aStrand, Color[] colors)
    {
        switch (aStrand)
        {
            case P_STRAND_ONE:
                colorsPStrandOne = colors;
                break;
            case Q_STRAND_ONE:
                colorsQStrandOne = colors;
                break;
            case P_STRAND_TWO:
                colorsPStrandTwo = colors;
                break;
            case Q_STRAND_TWO:
                colorsQStrandTwo = colors;
                break;
        }
    }

    /**
     * Get the array of Colors for the given strand
     *
     * @param       aStrand int - the strand
     * @return		Color[] - array of Colors along given strand of chromosome
    **/
    public Color[] getStrandColors(int aStrand)
    {
        switch (aStrand)
        {
            case P_STRAND_ONE:
                return getPStrandOneColors();
            case Q_STRAND_ONE:
                return getQStrandOneColors();
            case P_STRAND_TWO:
                return getPStrandTwoColors();
            case Q_STRAND_TWO:
                return getQStrandTwoColors();
        }

        return null;
    }

    /**
     * Get the array of Colors for P strand one.
     *
     * @return		Color[] - array of Colors along P strand one of chromosome
    **/
    public Color[] getPStrandOneColors()
    {
        return colorsPStrandOne;
    }
	public void setPStrandOneColors(Color [] arr)
	{
		colorsPStrandOne = arr;
	}

    /**
     * Get the array of Colors for P strand two.
     *
     * @return		Color[] - array of Colors along P strand two of chromosome
    **/
    public Color[] getPStrandTwoColors()
    {
        return colorsPStrandTwo;
    }
    
    public void setPStrandTwoColors(Color [] arr)
	{
		colorsPStrandTwo = arr;
	}

    /**
     * Get the array of Colors for Q strand one.
     *
     * @return		Color[] - array of Colors along Q strand one of chromosome
    **/
    public Color[] getQStrandOneColors()
    {
        return colorsQStrandOne;
    }
    public void setQStrandOneColors(Color [] arr)
	{
		colorsQStrandOne = arr;
	}
    /**
     * Get the array of Colors for Q strand two.
     *
     * @return		Color[] - array of Colors along Q strand two of chromosome
    **/
    public Color[] getQStrandTwoColors()
    {
        return colorsQStrandTwo;
    }
    
    public void setQStrandTwoColors(Color [] arr)
	{
		colorsQStrandTwo = arr;
	}

    /**
     * Return the previous meiosis chromosome model.
     *
     * @return  MeiosisChromosomeModel - the previous chromosome model for this model.
    **/
    public MeiosisChromosomeModel getPreviousMeiosisChromosomeModel()
    {
        return previousMeiosisChromosomeModel;
    }

    /**
     * Get the chromosome model type of this chromosome model
     *
     * @return    int - chromosome model type
    **/
    public int getChromosomeModelType()
    {
        return chromosomeModelType;
    }

    /**
     * Get the number/type of this chromosome model, zero if no chromosome.
     *
     * @return    int - number/type
    **/
    public int getNumberType()
    {
        if (chromosome != null)
        {
            return chromosome.getNumberType();
        }

        return 0;
    }

    /**
     * Get the chromosome for this model.
     *
     * @return		OrganismChromosome - the chromosome from the parent for this model
    **/
    public OrganismChromosome getChromosome()
    {
        return chromosome;
    }
}
