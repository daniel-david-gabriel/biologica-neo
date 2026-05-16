package org.concord.biologica.engine;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.concord.biologica.engine.*;

public class Branch
{
   
	OrganismChromosome theChromosome;
	OrganismAllele [] PL;
	OrganismAllele [] PR;
	OrganismAllele [] QL;
	OrganismAllele [] QR;
	int type;
	int numberOfPStrandSegments;
	int numberOfQStrandSegments;
	int lengthOfChromosomeInBases;
	
	Color [] PLStrandColor;
	Color [] PRStrandColor;
	Color [] QLStrandColor;
	Color [] QRStrandColor;
	
	Color [] defaultColor1;
	Color [] defaultColor2;
	
	private double p = 0.5;
	/**
	 * This class is used to store the chromosome's alleles when the cross over happened,
	 * 
	 *
	 */
	 
	public Branch(OrganismChromosome chr1)
	{
		this(chr1,Color.blue);
	}
	public Branch (OrganismChromosome chr1, Color strandColor)
	{
		theChromosome= chr1;
		lengthOfChromosomeInBases = chr1.getLengthInBases();
		getStrandSegments(chr1);
		PL = new OrganismAllele[numberOfPStrandSegments];
		PR = new OrganismAllele[numberOfPStrandSegments];
		QL = new OrganismAllele[numberOfQStrandSegments];
		QR = new OrganismAllele[numberOfQStrandSegments];
		PLStrandColor = new Color[numberOfPStrandSegments];
		PRStrandColor = new Color[numberOfPStrandSegments];
		QLStrandColor = new Color[numberOfQStrandSegments];
		QRStrandColor = new Color[numberOfQStrandSegments];
		
		setColors(numberOfPStrandSegments,numberOfQStrandSegments,strandColor);
		
		splitChromosome();
	}
	
	public Branch(OrganismAllele[] PL_arr, Color [] PL_Color, OrganismAllele[] PR_arr, Color [] PR_Color,
				  OrganismAllele[] QL_arr,Color [] QL_Color,OrganismAllele[] QR_arr,Color [] QR_Color)
	{
		theChromosome= null;
		this.setPLStrand(PL_arr);
	    this.setPRStrand(PR_arr);
		this.setQLStrand(QL_arr);
	    this.setQRStrand(QR_arr);
	    this.setPLStrandColor(PL_Color);
	    this.setPRStrandColor(PR_Color);
	    this.setQLStrandColor(QL_Color);
	    this.setQRStrandColor(QR_Color);
	   
	    this.getAllelesAsString();
	}
	
	public Branch(OrganismAllele[] PL_arr,OrganismAllele[] PR_arr,OrganismAllele[] QL_arr,OrganismAllele[] QR_arr)
	{
		this(PL_arr,PR_arr,QL_arr,QR_arr,Color.blue);
	    
	}
	
	public Branch(OrganismAllele[] PL_arr,OrganismAllele[] PR_arr,
				  OrganismAllele[] QL_arr,OrganismAllele[] QR_arr,Color color)
	{
		theChromosome= null;
		this.setPLStrand(PL_arr);
	    this.setPRStrand(PR_arr);
		this.setQLStrand(QL_arr);
	    this.setQRStrand(QR_arr);
	    setColors(PL_arr.length,QL_arr.length,color);
	   
	    this.getAllelesAsString();
	}
	
	public void setColors(int PStrandLength,int QStrandLength,Color color)
	{
		for (int i = 0;i<PStrandLength;i++)
		{
			PLStrandColor[i] = color;
			PRStrandColor[i] = color;
		}
		for (int i = 0;i<QStrandLength;i++)
		{
			QLStrandColor[i] = color;
			QRStrandColor[i] = color;
		}
	}
	/**
	 * get the numbers
	 */
	 private void getStrandSegments(OrganismChromosome chromosome)
	 {
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
        
	 }
	 
	
	/**
	 * Now the chromosome has 4 parts.Two upper arms and two lower arms (replicated)
	 */
	private void splitChromosome()
	{
		
		OrganismAllele anOrganismAllele;
        Enumeration eOrganismAlleles = theChromosome.getOrganismAlleles();
        double geneLocation;
    
        
        double numberOfTotalSegments = (double) (numberOfPStrandSegments + numberOfQStrandSegments);
       
        Gene aGene;
        int startIndexInHolderInBases, segmentLocation;
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
                
                PL[segmentLocation] = anOrganismAllele;
                PR[segmentLocation] = anOrganismAllele;
           }
            else if (segmentLocation-numberOfPStrandSegments < numberOfQStrandSegments)
            {             
                     QL[segmentLocation-numberOfPStrandSegments] = anOrganismAllele;
                	 QR[segmentLocation-numberOfPStrandSegments] = anOrganismAllele;
            }
            else
            {
                // Error
                System.err.println("segmentLocation = " + segmentLocation);
            }
        }
	}
	
	public Branch [] crossOverWith(Branch b)
	{
		OrganismAllele [] bPL = b.getPLStrand();
		OrganismAllele [] bPR = b.getPRStrand();
		OrganismAllele [] bQL = b.getQLStrand();
		OrganismAllele [] bQR = b.getQRStrand();
		Color [] bPL_Color = b.getPLStrandColor();
		Color [] bPR_Color = b.getPRStrandColor();
		Color [] bQL_Color = b.getQLStrandColor();
		Color [] bQR_Color = b.getQRStrandColor();
		
		Color [] new_PL_Color1 = new Color[numberOfPStrandSegments];
		Color [] new_PR_Color1 = new Color[numberOfPStrandSegments];
		Color [] new_QL_Color1 = new Color[numberOfQStrandSegments];
		Color [] new_QR_Color1 = new Color[numberOfQStrandSegments];
		
		Color [] new_PL_Color2 = new Color[numberOfPStrandSegments];
		Color [] new_PR_Color2 = new Color[numberOfPStrandSegments];
		Color [] new_QL_Color2 = new Color[numberOfQStrandSegments];
		Color [] new_QR_Color2 = new Color[numberOfQStrandSegments];
		
		OrganismAllele [] newPL1 = new OrganismAllele[numberOfPStrandSegments];
		OrganismAllele [] newPR1 = new OrganismAllele[numberOfPStrandSegments];
		OrganismAllele [] newQL1 = new OrganismAllele[numberOfQStrandSegments];
		OrganismAllele [] newQR1 = new OrganismAllele[numberOfQStrandSegments];
		
		OrganismAllele [] newPL2 = new OrganismAllele[numberOfPStrandSegments];
		OrganismAllele [] newPR2 = new OrganismAllele[numberOfPStrandSegments];
		OrganismAllele [] newQL2 = new OrganismAllele[numberOfQStrandSegments];
		OrganismAllele [] newQR2 = new OrganismAllele[numberOfQStrandSegments];
		
		for (int i = numberOfPStrandSegments-1; i>=0;i--)
		{ 
			double P = Math.random();
			newPL1[i] = PL[i];
			newPR1[i] = PR[i];
			
			new_PL_Color1[i] = PLStrandColor[i];
			new_PR_Color1[i] = PRStrandColor[i];
						
			newPL2[i] = bPL[i];
			newPR2[i] = bPR[i];
			
			new_PL_Color2[i] = bPL_Color[i];
		    new_PR_Color2[i] = bPR_Color[i];
		
			double R = Math.random();
		
			
			if (R>=0 && R<0.25)
			{
				
				//cross from chra1 with chra2
				if (P<p)
				{
					OrganismAllele [] temp = newPL1;
					Color [] tempColor = new_PL_Color1;
					newPL1 = newPL2;
					new_PL_Color1 = new_PL_Color2;
					newPL2 = temp;
					new_PL_Color2 = tempColor;
				}
			}
			else if (R>=0.25 && R<0.5)
			{
				//cross from chra1 with chrb2
				if (P<p)
				{
					OrganismAllele [] temp = newPL1;
					Color [] tempColor = new_PL_Color1;
					newPL1 = newPR2;
					new_PL_Color1 = new_PR_Color2;
					newPR2= temp;
					new_PR_Color2 = tempColor;
				}
				
			}
			else if (R>=0.5 && R<0.75)
			{
				//cross from chrb1 with chra2
				if (P<p)
				{
					OrganismAllele [] temp = newPR1;
					Color [] tempColor = new_PR_Color1;
					newPR1 = newPL2;
					new_PR_Color1 = new_PL_Color2;
					newPL2 = temp;
					new_PL_Color2 = tempColor;
				}
			}
			else if (R>=0.75 && R<1.0)
			{
				//cross from chrb1 with chrb2
				if (P<p)
				{
					OrganismAllele [] temp = newPR1;
					Color [] tempColor = new_PR_Color1;
					newPR1 = newPR2;
					new_PR_Color1 = new_PR_Color2;
					newPR2 = temp;
					new_PR_Color2 = tempColor;
				}
			}
		}
		for (int i = numberOfQStrandSegments-1; i>=0;i--)
		{
			newQL1[i] = QL[i];
			newQR1[i] = QR[i];
			
			new_QL_Color1[i] = QLStrandColor[i];
			new_QR_Color1[i] = QRStrandColor[i];
			
			newQL2[i] = bQL[i];
			newQR2[i] = bQR[i];
			
			new_QL_Color2[i] = bQL_Color[i];
			new_QR_Color2[i] = bQR_Color[i];
			
			double P = Math.random();
			double R = Math.random();
			if (R>=0 && R<0.25)
			{
				//cross from chra1 with chra2
				if (P<p)
				{
					OrganismAllele [] temp = newQL1;
					Color [] tempColor = new_QL_Color1;
					newQL1 = newQL2;
					new_QL_Color1 = new_QL_Color2;
					newQL2= temp;
					new_QL_Color2 = tempColor;
				}
			}
			else if (R>=0.25 && R<0.5)
			{
				//cross from chra1 with chrb2
				if (P<p)
				{
					OrganismAllele [] temp = newQL1;
					Color [] tempColor = new_QL_Color1;
					newQL1 = newQR2;
					new_QL_Color1 = new_QR_Color2;
					newQR2= temp;
					new_QR_Color2 = tempColor;
				}
			}
			else if (R>=0.5 && R<0.75)
			{
				//cross from chrb1 with chra2
				if (P<p)
				{
					OrganismAllele [] temp = newQR1;
					Color [] tempColor = new_QR_Color1;
					newQR1 = newQL2;
					new_QR_Color1 = new_QL_Color2;
					newQL2= temp;
					new_QL_Color2 = tempColor;
				}
			}
			else if (R>=0.75 && R<1.0)
			{
				//cross from chrb1 with chrb2
				if (P<p)
				{
					OrganismAllele [] temp = newQR1;
					Color [] tempColor = new_QR_Color1;
					newQR1 = newQR2;
					new_QR_Color1 = new_QR_Color2;
					newQR2= temp;
					new_QR_Color2 = tempColor;
				}

			}
		}
	  Branch [] newBranchs = new Branch[2];
	  //
	  newBranchs[0] = new Branch(newPL1,new_PL_Color1,newPR1,new_PR_Color1,newQL1,new_QL_Color1,newQR1,new_QR_Color1);
	  newBranchs[1] = new Branch(newPL2,new_PL_Color2,newPR2,new_PR_Color2,newQL2,new_QL_Color2,newQR2,new_QR_Color2);
	  
	 
	  return newBranchs;
	}
	
	public String getAllelesAsString()
	{
		String alleleStr ="";
        if ((PL.length == PR.length) && (QL.length == QR.length))
        {
			for (int i = 0;i<PL.length;i++)
			{
					if (PL[i] != null && PR[i] !=null)
					{
						alleleStr ="a:"+((OrganismAllele)PL[i]).getTextSymbol()+",b:"+((OrganismAllele)PR[i]).getTextSymbol();
					}
			} 
			for (int i = 0; i<QL.length;i++)
			{

				if (alleleStr.equals(""))
				{
					if (QL[i] != null && QR[i] != null)
					{
						alleleStr = "a:"+((OrganismAllele)QL[i]).getTextSymbol()+",b:"+((OrganismAllele)QR[i]).getTextSymbol();
					}
				}
				else
				{
					
					if (QL[i] != null && QR[i] != null)
					{
						alleleStr =alleleStr+ ",a:"+((OrganismAllele)QL[i]).getTextSymbol()+",b:"+((OrganismAllele)QR[i]).getTextSymbol();
					}
				}
			}
		}
		else // X and Y chromosome
		{
			int len1 = Math.max(PL.length,PR.length);
			OrganismAllele [] temp1 = new OrganismAllele[len1];
			if (PL.length>PR.length)
			{
				temp1 = PL;
			}
			else
			{
				temp1 = PR;
			}
			
			
			for (int i = 0;i<len1;i++)
			{
					if (temp1[i] != null)
					{
						alleleStr ="a:"+((OrganismAllele)temp1[i]).getTextSymbol();
					}
			} 
			
			int len2 = Math.max(QL.length,QR.length);
			OrganismAllele [] temp2 = new OrganismAllele[len2];
			if (QL.length>QR.length)
			{
				temp2 = QL;
			}
			else
			{
				temp2 = QR;
			}
			for (int i = 0; i<len2;i++)
			{

				if (alleleStr.equals(""))
				{
					if (temp2[i] != null)
					{
						alleleStr = "a:"+((OrganismAllele)temp2[i]).getTextSymbol();
					}
				}
				else
				{
					
					if (temp2[i] != null)
					{
						alleleStr =alleleStr+ ",a:"+((OrganismAllele)temp2[i]).getTextSymbol();
					}
				}
			}
		}
		
		
		return alleleStr;
	}
	
	protected void setPossibility(double po)
	{
		p = po;
	}
	
	private double getPossibility()
	{
		return p;
	}
	
	public void setPLStrand(OrganismAllele [] array)
	{
		PL = array;
	}
	
	public OrganismAllele [] getPLStrand()
	{
		return PL;
	}
	
	public void setQLStrand(OrganismAllele [] array)
	{
		QL = array;
	}
	
	public OrganismAllele [] getQLStrand()
	{
		return QL;
	}
	
	public void setPRStrand(OrganismAllele [] array)
	{
		PR = array;
	}
	public OrganismAllele [] getPRStrand()
	{
		return PR;
	}
	public void setQRStrand(OrganismAllele [] array)
	{
		QR = array;
	}
	public OrganismAllele [] getQRStrand()
	{
		return QR;
	}
	
	public int getnumberOfPStrandSegments()
	{
		return numberOfPStrandSegments;
	}
	
	public int getnumberOfQStrandSegments()
	{
		return numberOfQStrandSegments;
	}
	
	public String getNumberTypeAsString()
	{
		return theChromosome.getNumberTypeAsString();
	}
	
	public OrganismChromosome getChromosome()
	{
		return theChromosome;
	}
	
	public void setChromosome(OrganismChromosome chr)
	{
		theChromosome = chr;
	}
	
    public void setPLStrandColor(Color [] colorArr)
    {
    	PLStrandColor = colorArr;
    }
	public Color[] getPLStrandColor()
	{
		return  PLStrandColor;
	}
	
	public void setPRStrandColor(Color [] colorArr)
	{
		 PRStrandColor = colorArr;
	}
	
	public Color[] getPRStrandColor()
	{
		return PRStrandColor;
	}
	
	public void setQLStrandColor(Color [] colorArr)
	{
		 QLStrandColor = colorArr;
	}
	
	public Color[] getQLStrandColor()
	{
		return QLStrandColor;
	}
	
	public void setQRStrandColor(Color [] colorArr)
	{
		 QRStrandColor = colorArr;
	}
	
	public Color[] getQRStrandColor()
	{
		return QRStrandColor;
	}
}
