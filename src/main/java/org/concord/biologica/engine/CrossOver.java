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


public class CrossOver
{
	
    private double p = 0.05;
	public CrossOver()
	{
	}
	
	  
   /**
     * create a new family when doing crossover
     *
     *
     * @param		parentOne Organism - the first parent of the family
     * @param		parentTwo Organism - the second parent of the family
     * @param 		numberOfChildren int - the number of Children in this family
     * @param		blnCrossOver boolean - is Crossover turn on;
     * @return		Family - the new or existing family
    **/
	public Family getCrossOverFamily(Organism one, Organism two, int numberOfChildren, boolean blnOnlyLiveChildren)
	{
		Family family;
		
		Vector newChildren = new Vector();
		
		for (int i = 0;i<numberOfChildren;i++)
		{
			
			Organism org1 = crossOverOrganism(one);
			Organism org2 = crossOverOrganism(two);
			Organism newOrg = new Organism(org1,org2,"",true);
			if (!newOrg.containsFatalCharacteristic())
			{
				newChildren.addElement(newOrg);
			}
			else
			{
				i = i-1;
			}			
		}
		
		family =new Family(one,two,newChildren);
			
		return family;
   
	}
	
	  
   /**
     * Create a new family when doing crossover
     *
     * @param		one Organism - the first parent of the family
     * @param		two Organism - the second parent of the family
     * @param 		numberFemaleChildren int - the number of female Children
     * @param 		numberMaleChildren int - the number of male Children 
     * @param		blnCrossOver boolean - is Crossover turn on;
     * @return		Family - the new or existing family
     *
    **/
	
	public Family getCrossOverFamily(Organism one,Organism two,int numberFemaleChildren,int numberMaleChildren,boolean blnOnlyLiveChildren)
	{
		Family family;
		int numberOfFemaleOrg = 0 ;
		int numberOfMaleOrg = 0;
		
		Vector newChildren = new Vector();
		
		for (int i = 0;i<numberFemaleChildren+numberMaleChildren;i++)
		{
			Organism org1 = crossOverOrganism(one);
			Organism org2 = crossOverOrganism(two);
			Organism newOrg = new Organism(org1,org2,"",true);
			if (!newOrg.containsFatalCharacteristic())
			{
				if (newOrg.getSex() == Organism.MALE)
				{
					numberOfMaleOrg = numberOfMaleOrg +1;
					if (numberOfMaleOrg<= numberMaleChildren)
					{
						newChildren.addElement(newOrg);
					}
					else
					{
						i = i - 1;
					}
				}
				else if (newOrg.getSex() == Organism.FEMALE)
				{
					numberOfFemaleOrg = numberOfFemaleOrg +1;
					if (numberOfFemaleOrg<= numberFemaleChildren)
					{
						newChildren.addElement(newOrg);
					}
					else
					{
						i = i - 1;
					}
				}
				
			}
			else
			{
				i = i-1;
			}			
		}
		
		family =new Family(one,two,newChildren);
			
		return family;
	}
	
	/**
	 * change  the possibility of crossover
	 *
	 * @param	po int - the new possibility value
	 */
	 
	public void setPossibility(double po)
	{
		p = po;
	}
	
	public double getPossibility()
	{
		return p;
	}
	
	
	/**
	 * create a fake 
	 *
	 * @param	po int - the new possibility value
	 */
          
	public Organism crossOverOrganism(Organism org)
	{
		
		Enumeration chromosomes = org.getChromosomes();
		Organism newOrg = org;
		OrganismChromosome chr1=null;
		OrganismChromosome chr2=null;
		Branch [] temp;
		String strAllele1 = "";
		String strAllele2 = "";
		int start = 0;
		Vector orgBranchs1 = new Vector();
		Vector orgBranchs2 = new Vector();
		Branch [] branchs1 = null;
		Branch [] branchs2 = null;
		
		while (chromosomes.hasMoreElements())
		{
			try
			{
				chr1 = (OrganismChromosome)(chromosomes.nextElement());
				chr2 = (OrganismChromosome)(chromosomes.nextElement());
			}
			catch (Exception e)
            {
               
               System.out.println(e);
            }
            
  			Branch branchForChrom1 = new Branch(chr1,Color.blue);
			Branch branchForChrom2 = new Branch(chr2,Color.orange);
			temp = switchAlleles(branchForChrom1,branchForChrom2);
			orgBranchs1.addElement(temp[0]);
			orgBranchs2.addElement(temp[1]);
			
			if (start==0)
			{
				strAllele1 = temp[0].getAllelesAsString();
				strAllele2 = temp[1].getAllelesAsString();
			}
			else 
			{
				if (strAllele1.equals(""))
					strAllele1 = temp[0].getAllelesAsString();
				else
					strAllele1 = strAllele1+","+ temp[0].getAllelesAsString();
				
				if (strAllele2.equals(""))
					strAllele2 = temp[1].getAllelesAsString();
				else
					strAllele2 = strAllele2+","+ temp[1].getAllelesAsString();
			}
		
			start = 1;              
		}
		
		//create two Mom or two dad to hold the new Chromosomes;
		
		World world = org.getWorld();
		Species species = org.getSpecies();
		
		Organism org1 = new Organism(world, "dragon1", species,org.getSex(),strAllele1);
		
		Organism org2 = new Organism(world, "dragon2", species,org.getSex(),strAllele2);
		
		branchs1 = new Branch[orgBranchs1.size()];
		branchs2 = new Branch[orgBranchs2.size()];
		
		for (int i = 0; i<orgBranchs1.size();i++)
		{
			branchs1[i] = (Branch)orgBranchs1.elementAt(i);
		}
		for (int i = 0; i<orgBranchs2.size();i++)
		{
			branchs2[i] =(Branch)orgBranchs2.elementAt(i);
		}
		
		org1.setChromosomePaintInfo(branchs1);
		org2.setChromosomePaintInfo(branchs2);
		double possibility = Math.random();
		if (possibility<=0.5)
			newOrg = org1;
		else
			newOrg = org2;
			
		
		return newOrg;
	}
	
	
	public Branch [] switchAlleles(Branch b1,Branch b2)
	{
	
		Branch [] newBranchs = new Branch[2];
		if ((b1.getNumberTypeAsString().equals("X")) && (b2.getNumberTypeAsString().equals("Y"))||
		    (b2.getNumberTypeAsString().equals("X")) && (b1.getNumberTypeAsString().equals("Y")))
		{
			
			//don't cross over
			newBranchs[0] = new Branch(b1.getPLStrand(),b1.getPLStrandColor(),b2.getPLStrand(),b2.getPLStrandColor(),
									   b1.getQLStrand(),b1.getQLStrandColor(),b2.getQLStrand(),b2.getQLStrandColor());
			newBranchs[1] = new Branch(b1.getPRStrand(),b1.getPRStrandColor(),b2.getPRStrand(),b2.getPRStrandColor(),
									   b1.getQRStrand(),b1.getQRStrandColor(),b2.getQRStrand(),b2.getQRStrandColor());
		}
		else
		{
			b1.setPossibility(p);
			
			newBranchs = b1.crossOverWith(b2);
			
		}
			return 	newBranchs;

	}
	

}