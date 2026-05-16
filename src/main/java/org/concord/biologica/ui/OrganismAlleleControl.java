//
// Class : OrganismAlleleControl - A small override of BioComboBox to add knowledge of
//									organism alleles, drawing alleles on the chromosome, etc.
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.4 $
// $Date: 2003/08/15 17:53:58 $
// $Author: dima $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.concord.biologica.engine.*;

/**
 * This class represents an organism allele combobox in BioLogica.
 *
 * @version		$Revision: 1.4 $ $Date: 2003/08/15 17:53:58 $
 * @author 		$Author: dima $
**/

public final class OrganismAlleleControl extends BioComboBox
implements ItemListener, PropertyChangeListener
{
	/**
	 * Organism
	**/
	Organism organism;

	/**
	 * Gene
	**/
	Gene gene;

	/**
	 * Organism allele
	**/
	OrganismAllele organismAllele;

	/**
	 * Organism allele's gene's name, drawn next to combo box
	**/
	String geneName;

	/**
	 * Gene visible?  If not, then don't draw gene or this control at all.
	**/
	boolean geneVisible;

	/**
	 * Alleles of this organism visible?  If not, then draw a "?" instead of the allele value.
	**/
	boolean allelesVisible;

	/**
	 * Alleles of this organism alterable?  If not, then draw as text, not a combobox
	**/
	boolean allelesAlterable;

	/**
	 * Selected?
	**/
	boolean selected;

	/**
	 * The x coordinate of the left end of the line drawn
	 * on the chromosome representing the allele.
	**/
	int x1Allele;

	/**
	 * The x coordinate of the right end of the line drawn
	 * on the chromosome representing the allele.
	**/
	int x2Allele;

	/**
	 * The y coordinate of line drawn on the
	 * chromosome representing the allele.
	**/
	int yAllele;

	/**
	 * The x coordinate of the left end of the leader
	 * line drawn from the allele to the combo box.
	**/
	int x1Leader;

	/**
	 * The x coordinate of the right end of the leader
	 * line drawn from the allele to the combo box.
	**/
	int x2Leader;

	/**
	 * The y coordinate of the left end of the leader
	 * line drawn from the allele to the combo box.
	**/
	int y1Leader;

	/**
	 * The y coordinate of the left end of the leader
	 * line drawn from the allele to the combo box.
	**/
	int y2Leader;

	/**
	 * Chromosome view containing this control
	**/
	ChromosomeView chromosomeView;

    /**
     * Creates an AlleleComboBox given a ChromosomeView
	 *
	 * @param		aChromosomeView ChromosomeView - the view creating this control
	 * @param		aFont Font - the font to use
     */
    public OrganismAlleleControl(ChromosomeView aChromosomeView)
	{
        super();
		setVisible(false);
		setFont(UIGraphics.getFontBold12());
		setBounds(2000,2000,120,20);

		organism = null;
		gene = null;
		organismAllele = null;
		chromosomeView = aChromosomeView;
		geneName = null;
		geneVisible = true;
		allelesVisible = true;
		allelesAlterable = true;
		selected = false;

		addItemListener(this);
    }

	/**
	 * Get the Organism associated with this control
	 *
	 * @return		Organism - Organism associated with this control, may be null
	**/
	public Organism getOrganism()
	{
		return organism;
	}
	
	/**
	 * Get the Gene associated with this control
	 *
	 * @return		Gene - Gene associated with this control, may be null
	**/
	public Gene getGene()
	{
		return gene;
	}

	/**
	 * Get the OrganismAllele associated with this control
	 *
	 * @return		OrganismAllele - OrganismAllele associated with this control, may be null
	**/
	public OrganismAllele getOrganismAllele()
	{
		return organismAllele;
	}

	/**
	 * Set the state needed to draw and use this control.
	 *
	 * @param		anOrganismAllele OrganismAllele - allele to be associated with this control
	 * @param		chromosomeImageNumber int - chromosome image number of this allele's chromosome
	 * @param		xTopLeftChromosomeImage int - x position of top left of image
	 * @param		yTopLeftChromosomeImage int - y position of top left of image
	 * @param		yTopLeftComboBox int - y position of allele's combo box
	 * @param		aSelected boolean - selected?
	**/
	public void setState(OrganismAllele anOrganismAllele,
						 int chromosomeImageNumber,
						 int xTopLeftChromosomeImage,
						 int yTopLeftChromosomeImage,
						 int yTopLeftComboBox,
						 boolean aSelected)
	{
		// If anOrganismAllele null, then make this control invisible
		if (anOrganismAllele == null)
		{
			setVisible(false);
			setBounds(2000,2000,120,20);

			if (organism != null)
			{
				organism.removePropertyChangeListener(this);
			}
			organism = null;

			if (gene != null)
			{
				gene.removePropertyChangeListener(this);
			}
			gene = null;

			if (organismAllele != null)
			{
				organismAllele.removePropertyChangeListener(this);
			}
			organismAllele = null;

			geneName = null;
			geneVisible = true;
			allelesVisible = true;
			allelesAlterable = true;
			selected = false;

			setEnabled(false);

			return;
		}

		// anOrganismAllele not null
		organismAllele = anOrganismAllele;
		organismAllele.addPropertyChangeListener(this);

		OrganismChromosome organismChromosome = organismAllele.getOrganismChromosome();
		organism = organismChromosome.getOrganism();
		organism.addPropertyChangeListener(this);
		allelesVisible = organism.isAllelesVisible();
		allelesAlterable = organism.isAllelesAlterable();

		gene = organismAllele.getGene();
		gene.addPropertyChangeListener(this);
		geneName = gene.getName();
		geneVisible = gene.isVisible();

		// Update the items in the combo box
		boolean blnTemp = false;
		removeAllItems();
		SpeciesAllele aSpeciesAllele;
		Enumeration eSpeciesAlleles = gene.getSpeciesAlleles();
		while (eSpeciesAlleles.hasMoreElements())
		{
			aSpeciesAllele = (SpeciesAllele) eSpeciesAlleles.nextElement();
			if (aSpeciesAllele.isVisible())
			{
				addItem(aSpeciesAllele.getTextSymbol());
			}
		}
		setSelectedItem(organismAllele.getTextSymbol());

		// Calculate locations used when painting lines for this control
		SpeciesChromosome aSpeciesChromosome = gene.getSpeciesChromosome();
		int lengthOfChromosome = aSpeciesChromosome.getLengthInBases();
		int positionOnChromosome = gene.getStartIndexInHolder();
		float chromosomeMidPoint = ChromosomeView.CHROMOSOME_MID_POINT[chromosomeImageNumber];
		float geneLocation = ((float)positionOnChromosome) / ((float)lengthOfChromosome);
		if (geneLocation < chromosomeMidPoint)
		{
			float topLength = (float) ChromosomeView.CHROMOSOME_TOP_LENGTH[chromosomeImageNumber];
			yAllele = yTopLeftChromosomeImage;// +
					 // ChromosomeView.CHROMOSOME_TOP_SECTION_TOP[chromosomeImageNumber] +
					 // (int) (geneLocation * topLength);
		}
		else
		{
			float bottomLength = (float) ChromosomeView.CHROMOSOME_BOTTOM_LENGTH[chromosomeImageNumber];
			yAllele = yTopLeftChromosomeImage ;//+
					  //ChromosomeView.CHROMOSOME_BOTTOM_SECTION_TOP[chromosomeImageNumber] +
					 // (int) (geneLocation * bottomLength);
		}

	    x1Allele = xTopLeftChromosomeImage + 1;
		x2Allele = x1Allele + ChromosomeView.CHROMOSOME_IMAGE_WIDTH - 3;
		
		x1Leader = x2Allele + 3;
		y1Leader = yAllele;

		x2Leader = x1Leader + ChromosomeView.X_COMBO_BOX_OFFSET;
		y2Leader = yTopLeftComboBox + 10;

		setBounds(x2Leader + 5, yTopLeftComboBox, 80, 20);

		if (geneVisible == false ||
			allelesVisible == false ||
			allelesAlterable == false ||
			organismAllele.isLocked() == true)
		{
			setEnabled(false);
			setVisible(false);
		}
		else
		{
			setEnabled(true);
			setVisible(true);
		}
		selected = aSelected;

		repaint();
	}

	/**
	 * Tell organism allele control to paint its lines.
	 * Assume the font has been correctly set already.<p>
	 *
	 * If this allele is locked, then the combo box will not
	 * be visible and we should also draw the allele text symbol.<p>
	 *
	 * If the gene is not visible, draw nothing.<p>
	 *
	 * If the organism's alleles are locked,
	 *
	 * @param	g Graphics - Graphics object to use in drawing
	 * @param	selectionColor Color - selection color, may not be null
	**/
	public int paintLines(Graphics g, Color selectionColor)
	{
	    int retValue = 0;
		if (geneVisible == true)
		{
			if (selected)
			{
				g.setColor(selectionColor);
				g.fillRect(x1Allele-2,yAllele-2,(x2Allele-x1Allele+5),5);
			}

			g.setColor(Color.red);
			g.drawLine(x1Allele,yAllele,x2Allele,yAllele);
			g.setColor(Color.black);
			g.drawLine(x1Leader,y1Leader,x2Leader,y2Leader);
			if (isVisible() == true)
			{
				if (geneName != null)
				{
					g.drawString(geneName,x2Leader+90,y2Leader+5);
				}
			}
			else if (organismAllele != null)
			{
				// If alleles are not visible, draw a "?" instead of the allele text symbol
				if (allelesVisible == false)
				{
					if (geneName != null)
					{
						g.drawString("?  " + geneName,
									 x2Leader+5,y2Leader+5);
					}
					else
					{
						g.drawString("?  ",
									 x2Leader+5,y2Leader+5);
					}
				}
				else
				{
					if (geneName != null)
					{
						g.drawString(organismAllele.getTextSymbol() + "  " + geneName,
									 x2Leader+5,y2Leader+5);
					}
					else
					{
						g.drawString(organismAllele.getTextSymbol(),
									 x2Leader+5,y2Leader+5);
					}
					
				}
			}
		}
	    return (y2Leader+5);
	}
	
	//new paintLines function,
	public void paintLines(Graphics g, Color selectionColor,int x1,int x2, int y1)
	{
	
		x1Allele = x1;
		x2Allele = x2;
		yAllele = y1;
		if (geneVisible == true)
		{
			if (selected)
			{
				g.setColor(selectionColor);
				g.fillRect(x1Allele-2,yAllele-2,(x2Allele-x1Allele+5),5);
			}

			g.setColor(Color.red);
			g.drawLine(x1Allele,yAllele,x2Allele,yAllele);
			g.setColor(Color.black);
			g.drawLine(x1Leader,y1Leader,x2Leader,y2Leader);
			if (isVisible() == true)
			{
				if (geneName != null)
				{
					g.drawString(geneName,x2Leader+90,y2Leader+5);
				}
			}
			else if (organismAllele != null)
			{
				// If alleles are not visible, draw a "?" instead of the allele text symbol
				if (allelesVisible == false)
				{
					if (geneName != null)
					{
						g.drawString("?  " + geneName,
									 x2Leader+5,y2Leader+5);
					}
					else
					{
						g.drawString("?  ",
									 x2Leader+5,y2Leader+5);
					}
				}
				else
				{
					if (geneName != null)
					{
						g.drawString(organismAllele.getTextSymbol() + "  " + geneName,
									 x2Leader+5,y2Leader+5);
					}
					else
					{
						g.drawString(organismAllele.getTextSymbol(),
									 x2Leader+5,y2Leader+5);
					}
				}
			}
		}
	}

	/**
	 * Is this control at the x,y coordinates specified?
	 *
	 * @param	x int - x coordinate
	 * @param	y int - y coordinate
	 * @return	boolean - at specified coordinates?
	**/
	public boolean pick(int x, int y)
	{
		// Return immediately if this combo box does not have an allele
		if (organismAllele == null)
		{
			return false;
		}

		// Check coordinates
		if ((x > x1Allele-5 && x < x2Allele+5) &&
			(y > yAllele-5 && y < yAllele+5))
		{
			return true;
		}

		return false;
	}

	/**
	 * Handle combo box item changed events.
	 *
	 * @param	event ItemEvent - change event to handle
	**/
	public void itemStateChanged(ItemEvent event)
	{
		// Return immediately if this combo box does not have an allele
		if (organismAllele == null)
		{
			return;
		}

		// Only combo boxes can generate this event now
		JComboBox comboBox = (JComboBox) event.getSource();
		String newValue = (String) comboBox.getSelectedItem();

		// Notify chromosome view if the event comboBox is this comboBox (should always be)
		if (comboBox == this)
		{
			chromosomeView.organismAlleleControlChanged(this,organismAllele,newValue);
		}
	}

	/**
	 * Handle property change events
	 *
	 * @param	event PropertyChangeEvent - the property change event
	**/
	public void propertyChange(PropertyChangeEvent event)
	{
		if (organismAllele == null)
		{
			return;
		}

		boolean updateState = false;

		String propertyName = event.getPropertyName();

		if (propertyName.equals(EngineProp.LOCKED_STATE))
		{
			if (organismAllele.isLocked() == true)
			{
				setVisible(false);
				setEnabled(false);
			}
			else
			{
				setVisible(true);
				setEnabled(true);
			}
			updateState = true;
		}
		else if (propertyName.equals(EngineProp.ALLELES_VISIBLE))
		{
			if (organism != null)
			{
				allelesVisible = organism.isAllelesVisible();
				updateState = true;
			}
		}
		else if (propertyName.equals(EngineProp.ALLELES_ALTERABLE))
		{
			if (organism != null)
			{
				allelesAlterable = organism.isAllelesAlterable();
				updateState = true;
			}
		}
		else if (propertyName.equals(EngineProp.VISIBLE))
		{
			if (gene != null)
			{
				geneVisible = gene.isVisible();
				updateState = true;
			}
			// Ignore VISIBLE property notifications on other objects
		}

		// Update state
		if (updateState == true)
		{
			if (geneVisible == false ||
				allelesVisible == false ||
				allelesAlterable == false ||
				organismAllele.isLocked() == true)
			{
				setEnabled(false);
				setVisible(false);
			}
			else
			{
				setEnabled(true);
				setVisible(true);
			}
			repaint();
		}
	}
}
