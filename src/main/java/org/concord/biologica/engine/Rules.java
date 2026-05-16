package org.concord.biologica.engine;

// Class : Rules
//
// Copyright © 2002, The Concord Consortium
//
// Original Author: Qing Liao
//
// 1.16
// 2003/01/16 14:05:08
// qliao
//
import java.awt.event.*;
import java.awt.Panel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.*;
import javax.swing.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.Math;
import org.concord.biologica.ui.*;

public class Rules
{
	private org.concord.biologica.ui.EnvironmentView en;
	private TerrainEng enUnit;
	private Organism org;
	private int currentX, currentY;
	private World world;
	private Species species;

	private Hashtable rulesForLand;
	private Hashtable rulesForWater;
	private Hashtable rulesForSand;
	private Hashtable rulesForMountain;
	private Vector barrierVector;
	
	//make maximum age to be setable by script
	 private int AGE_LIMIT = 100; //maximum age of normal dragon
	 private int AGE_LIMIT_NORMAL_SICKENV = 20; //maximum age of a normal-cell dragon in Malaria Environment
	 private int AGE_LIMIT_SICKEL = 10;  //maximum age of a cickel-cell dragon

	
	public Rules(org.concord.biologica.ui.EnvironmentView e)
	{
		en = e;
		rulesForLand = new Hashtable();
		rulesForWater = new Hashtable();
		rulesForSand = new Hashtable();
		rulesForMountain = new Hashtable();
		barrierVector = new Vector();
	
	}
	
	public void setWorld(World aWorld)
	{
		world = aWorld;
		species = world.getCurrentSpecies();
	}
	
	public void changeDirection(TerrainEng en,Organism o, int newX, int newY)
	{
		enUnit = en;
		org = o;
		currentX = newX;
		currentY = newY;
		
		switch (enUnit.getEnvironment())
		{
			case TerrainEng.ENVIRONMENT_LAND:
				performTheRule(rulesForLand);

				break;
			case TerrainEng.ENVIRONMENT_WATER:
		
				performTheRule(rulesForWater);
				break;
			case TerrainEng.ENVIRONMENT_SAND:
				performTheRule(rulesForSand);
				break;
			case TerrainEng.ENVIRONMENT_MOUNTAIN:
				performTheRule(rulesForMountain);
				break;
			default:
			  break;
		}

		
	}
	private void performTheRule(Hashtable table)
	{
		try{
				EnvironmentView e = enUnit.getMotherEnvi();

				if (table.isEmpty())
				{
				
					moveToNext(currentX,currentY);
				}
				else
				{
					 Enumeration rules = table.keys();
					 while (rules.hasMoreElements())
					 {
					 
					 	String keyWord = (String)(rules.nextElement());
					 	if (org.containsCharacteristic(keyWord))
					 	{
					 		Vector v = (Vector)table.get(keyWord);

					 		if (((String)(v.elementAt(0))).equals("Changed"))
					 		{

					 			boolean oldWantToMate = org.isWantToMate();
						 		org.setHealth(((Double)(v.elementAt(1))).doubleValue());
								org.setRepast(((Integer)(v.elementAt(2))).intValue());
								org.setSpeed(((Double)(v.elementAt(3))).doubleValue());
								org.setAgeOfMate(((Integer)(v.elementAt(4))).intValue());
								if (enUnit.isInsideUnit(currentX,currentY))
									org.setWantToMate(oldWantToMate);
							}
					 		if (!(((Double)v.elementAt(1)).equals(new Double(0))))
					 		{
					 			Double n = (Double)v.elementAt(1);
								double r = (new java.util.Random()).nextDouble();
							 	setAgeLimit((new Double(n.doubleValue()*Organism.AGE_LIMIT)).intValue());
					 			if (!isOrganismSurvived(org,r))
								{
									try{
										enUnit.removeOrganism(org);
									}
									catch(Exception exc)
									{
										exc.printStackTrace();
									}
									
									e.removeOrganism(org);
									org.setCurrentUnit(null);
									org.delete();
									org = null;
									
									return;
								}
					 			
					 		}
					 		else 
					 		{
					 			if(org.getParentFamily() !=null)
					 				org.setParentFamily(null);
					 			enUnit.removeOrganism(org);
			  					
			  					e.removeOrganism(org);
			  					org.setCurrentUnit(null);
			  					org.delete();
			  					org = null;
			  					return;
			  					
					 		}
					 	}

					 	moveToNext(currentX,currentY);
					 
					 }

				}
				
			}catch(Exception exc){}
		
	}
	private boolean isOrganismSurvived(Organism o, double r)
	{
	
	  		double p = o.getAge()/getAgeLimit();
	  		if (r>p)
	  			return true;
	  	

			return false;
	}
	public void setRules(int envi, String traits,double health,int repast, double speed, int ageOfMate)
	{
		
		switch (envi)
		{
			case TerrainEng.ENVIRONMENT_LAND:
				changeRules(rulesForLand,traits,health,repast,speed,ageOfMate);
				break;
			case TerrainEng.ENVIRONMENT_WATER:
				changeRules(rulesForWater,traits,health,repast,speed,ageOfMate);
				break;
			case TerrainEng.ENVIRONMENT_SAND:
				changeRules(rulesForSand,traits,health,repast,speed,ageOfMate);
				break;
			case TerrainEng.ENVIRONMENT_MOUNTAIN:
				changeRules(rulesForMountain,traits,health,repast,speed,ageOfMate);
				break;
			default:
			  break;
		}
	}
	public void setHealthRules(int envi, String traits,double health)
	{
		switch (envi)
		{
			case TerrainEng.ENVIRONMENT_LAND:
				 changeIndivialRules(rulesForLand,traits,(new Double(health)).toString(),1);
				break;
			case TerrainEng.ENVIRONMENT_WATER:
				changeIndivialRules(rulesForWater,traits,(new Double(health)).toString(),1);
				break;
			case TerrainEng.ENVIRONMENT_SAND:
				changeIndivialRules(rulesForSand,traits,(new Double(health)).toString(),1);
				break;
			case TerrainEng.ENVIRONMENT_MOUNTAIN:
				changeIndivialRules(rulesForMountain,traits,(new Double(health)).toString(),1);
				break;
			default:
			  break;
		}
	}
	public void setFoodComsumptionRate(int envi, String traits,int repast)
	{
		switch (envi)
		{
			case TerrainEng.ENVIRONMENT_LAND:
				 changeIndivialRules(rulesForLand,traits,(new Integer(repast)).toString(),2);
				break;
			case TerrainEng.ENVIRONMENT_WATER:
				changeIndivialRules(rulesForWater,traits,(new Integer(repast)).toString(),2);
				break;
			case TerrainEng.ENVIRONMENT_SAND:
				changeIndivialRules(rulesForSand,traits,(new Integer(repast)).toString(),2);
				break;
			case TerrainEng.ENVIRONMENT_MOUNTAIN:
				changeIndivialRules(rulesForMountain,traits,(new Integer(repast)).toString(),2);
				break;
			default:
			  break;
		}	
	}
	public void setSpeedRules(int envi, String traits,double speed)
	{
		switch (envi)
		{
			case TerrainEng.ENVIRONMENT_LAND:
				 changeIndivialRules(rulesForLand,traits,(new Double(speed)).toString(),3);
				break;
			case TerrainEng.ENVIRONMENT_WATER:

				changeIndivialRules(rulesForWater,traits,(new Double(speed)).toString(),3);
				break;
			case TerrainEng.ENVIRONMENT_SAND:

				changeIndivialRules(rulesForSand,traits,(new Double(speed)).toString(),3);
				break;
			case TerrainEng.ENVIRONMENT_MOUNTAIN:

				changeIndivialRules(rulesForMountain,traits,(new Double(speed)).toString(),3);
				break;
			default:
			  break;
		}		
	}
	public void setAgeOfMateRules(int envi, String traits,int ageOfMate)
	{
		switch (envi)
		{
			case TerrainEng.ENVIRONMENT_LAND:
				 changeIndivialRules(rulesForLand,traits,(new Integer(ageOfMate)).toString(),4);
				break;
			case TerrainEng.ENVIRONMENT_WATER:
				changeIndivialRules(rulesForWater,traits,(new Integer(ageOfMate)).toString(),4);
				break;
			case TerrainEng.ENVIRONMENT_SAND:
				changeIndivialRules(rulesForSand,traits,(new Integer(ageOfMate)).toString(),4);
				break;
			case TerrainEng.ENVIRONMENT_MOUNTAIN:
				changeIndivialRules(rulesForMountain,traits,(new Integer(ageOfMate)).toString(),4);
				break;
			default:
			  break;
		}
	}
	public Hashtable getRules(int envi)
	{
		switch (envi)
		{
			case TerrainEng.ENVIRONMENT_LAND:
				return rulesForLand;
				
			case TerrainEng.ENVIRONMENT_WATER:
				return rulesForWater;
				
			case TerrainEng.ENVIRONMENT_SAND:
				return rulesForSand;

			case TerrainEng.ENVIRONMENT_MOUNTAIN:
				return rulesForMountain;
				
			default:
			  break;
		}	
		return null;
	}
	private void changeRules(Hashtable table,String traits,double health,int repast, double speed, int ageOfMate)
	{
	
		
		if (table.isEmpty()) return;
		Enumeration rules = rulesForLand.keys();
		Vector v = new Vector();
		v.addElement(new String("Changed"));
		v.addElement(new Double(health));
        v.addElement(new Integer(repast));
        v.addElement(new Double(speed));
        v.addElement(new Integer(ageOfMate));
        v.addElement(new Integer(1));
		while (rules.hasMoreElements())
		{
			String keyWord = (String)(rules.nextElement());
			if (traits.equals(keyWord))
			{
				traitsRulesChanged(rulesForLand,keyWord);
				traitsRulesChanged(rulesForWater,keyWord);
				traitsRulesChanged(rulesForSand,keyWord);
				traitsRulesChanged(rulesForMountain,keyWord);

				table.put(keyWord,v);
			}
		}
		
		
		
	}
	private void changeIndivialRules(Hashtable table,String traits,String request,int index)
	{


		
		if (table.isEmpty()) return;
		Enumeration rules = table.keys();
	
		while (rules.hasMoreElements())
		{
			String keyWord = (String)(rules.nextElement());
			if (traits.equals(keyWord))
			{

				Vector tmpV = (Vector)table.get(keyWord);
				if (index==1)
					changeRules(table,keyWord,(new Double(request)).doubleValue(),((Integer)(tmpV.elementAt(2))).intValue(),((Double)(tmpV.elementAt(3))).doubleValue(),((Integer)(tmpV.elementAt(4))).intValue());
				else if (index==2)
					changeRules(table,keyWord,((Double)(tmpV.elementAt(1))).doubleValue(),(new Integer(request)).intValue(),((Double)(tmpV.elementAt(3))).doubleValue(),((Integer)(tmpV.elementAt(4))).intValue());
				else if (index==3)
					changeRules(table,keyWord,((Double)(tmpV.elementAt(1))).doubleValue(),((Integer)(tmpV.elementAt(2))).intValue(),(new Double(request)).doubleValue(),((Integer)(tmpV.elementAt(4))).intValue());
				else if (index==4)
					changeRules(table,keyWord,((Double)(tmpV.elementAt(1))).doubleValue(),((Integer)(tmpV.elementAt(2))).intValue(),((Double)(tmpV.elementAt(3))).doubleValue(),(new Integer(request)).intValue());

			}
		}
		
	}
	
	private void traitsRulesChanged(Hashtable table,String key)
	{

		Vector tmpV = (Vector)table.get(key);
		Vector v = new Vector();
		v.addElement(new String("Changed"));
		v.addElement((Double)(tmpV.elementAt(1)));
        v.addElement((Integer)(tmpV.elementAt(2)));
        v.addElement((Double)(tmpV.elementAt(3)));
        v.addElement((Integer)(tmpV.elementAt(4)));
        v.addElement((Integer)(tmpV.elementAt(5)));
		table.put(key,v);

	}
	
	private boolean reachBorder(int newX, int newY)
	{
		int R = org.getR();
		double direction = org.getDirection();
		if (newX<=0 ||newX+R>=en.getWidth()
		  	||newY<=0 || newY+R>=en.getHeight())
		{
		  	return true;
		}
		else 
			return false;
	}
	
	
	private void moveToNext(int newX, int newY)
	{
		
		int R = org.getR();
		double direction = org.getDirection();
		boolean findHeterosexual = false;
		// if the dragon reach the border of Environment , turn back
		// if the dragon reached a corner????
		if (reachBorder(newX,newY))
		{
			org.setDirection(direction+0.98*Math.PI);
		}				
		else if (enUnit.isInsideUnit(newX,newY)) // if the dragon is still in this unit
		{
			//check if the unit which is going to enter is barrier area. added by x.zheng
		   if(enUnit.getBarrier())
		   {
			org.setDirection(direction+Math.PI);
		   }
		   else
		   {
	  		org.setXloc(newX);
  			org.setYloc(newY);
  			if(org.isWantToMate())
  			{
  				
	  			Vector orgGroup = enUnit.getOrganism();
	  			for (int i = 0;i<orgGroup.size();i++)
	  			{
	  				if (org.isDeleted()) return;
	  				if (((Organism)(orgGroup.elementAt(i))).isDeleted()) continue;
	  				if (!((Organism)(orgGroup.elementAt(i))).isWantToMate()) continue;
	  				
	  				
	  				if (org.getSex()!=((Organism)(orgGroup.elementAt(i))).getSex() && ((Organism)(orgGroup.elementAt(i))).getSpeed() != 0)
	  				{
	  			 		int difX = ((Organism)(orgGroup.elementAt(i))).getXloc() - org.getXloc();
	  					int difY = ((Organism)(orgGroup.elementAt(i))).getYloc() - org.getYloc();
	  					
	  					if (difX >0 && difY>0 && Math.abs(difX)>org.getR() && Math.abs(difY)>org.getR())
	  					{
							//changed
	  						org.setDirection(Math.atan((double)(Math.abs((double)difY/(double)difX))));
	  						((Organism)(orgGroup.elementAt(i))).setDirection(Math.PI+Math.atan((double)(Math.abs((double)difY/(double)difX))));
	  						//((Organism)(orgGroup.elementAt(i))).setSpeed(0);
	  					}
	  					else if (difX<0 && difY>0&& Math.abs(difX)>org.getR() && Math.abs(difY)>org.getR())
	  					{
	  						//changed
	  						org.setDirection(-Math.atan((double)(Math.abs((double)difY/(double)difX))));
	  						((Organism)(orgGroup.elementAt(i))).setDirection(Math.PI-Math.atan((double)(Math.abs((double)difY/(double)difX))));
	  						//((Organism)(orgGroup.elementAt(i))).setSpeed(0);
	  					}
	  					else if (difY>=0 && Math.abs(difX)<org.getR())
	  					{
	  						org.setDirection(0);
	  						((Organism)(orgGroup.elementAt(i))).setDirection(Math.PI);
	  						//((Organism)(orgGroup.elementAt(i))).setSpeed(0);
	  					}
	  					else if (difX>=0 &&  Math.abs(difY)<org.getR())
	  					{
	  						
	  						org.setDirection(Math.PI/2);
	  						((Organism)(orgGroup.elementAt(i))).setDirection(3*Math.PI/2);
	  						//((Organism)(orgGroup.elementAt(i))).setSpeed(0);
	  					}
	  					
	  					if (Math.abs(difX)<org.getR() && Math.abs(difY)<org.getR())
	  					{
	  						
	  						if (org.getSpeed() !=0 && ((Organism)(orgGroup.elementAt(i))).getSpeed()!=0)
	  						{
	  							
	  							Organism parent1 = org;
	  							Organism parent2 = ((Organism)(orgGroup.elementAt(i)));
	  						
	  							if (parent2.isWantToMate())
	  							{
		  							double oldSpeed1 = parent1.getSpeed();
		  							double oldSpeed2 = parent2.getSpeed();
		  							double oldDirection1 = parent1.getDirection();
		  							double oldDirection2 = parent2.getDirection();
		  							parent1.setSpeed(0);
		  							parent2.setSpeed(0);
		  				
		  							
		  							Organism child = new Organism(parent1,parent2,"child1");
		  							
		  							int type = Organism.MALE;
		  							if (Math.random()<0.5)
		  								type = Organism.FEMALE;
		  							//Organism child = new Organism(world,type,"",species);
		  							
		  							  
		  				
		  							
		  							if (child.containsFatalCharacteristic())
		  							{
		  								child.delete();
		  								child = null;
		  								//System.runFinalization();
		  							}
		  							else
		  							{
										TerrainEng enUnit1 = org.getCurrentUnit();
										
										// Don't need to do this because setCurrentUnit does it
		  								// enUnit1.addOrganism(child);
		  								child.setCurrentUnit(enUnit1);
		  								EnvironmentView e = enUnit.getMotherEnvi();
		  								e.addOrganism(child);
		  								e.setOrganismProperties(child);
		  								child.setXloc(Math.min(parent1.getXloc(),parent2.getXloc()));
		  								child.setYloc(Math.min(parent1.getYloc(),parent2.getYloc()));
		  								
		  								if (child!= null)
		  								{
		  									parent1.setWantToMate(false);
		  									parent2.setWantToMate(false);
		  									
		  									child.setSpeed(oldSpeed1);
		  									child.setDirection(Math.random()*2*Math.PI);
		  									child.setAge(0);
		  									//World world = parent1.getWorld();
		  									//world.deleteAllFamilies(true,true);
		  								}
		  							}
		  							parent1.setSpeed(oldSpeed1);
		  							parent1.setDirection(Math.random()*2*Math.PI);
		  							parent2.setSpeed(oldSpeed2);
		  							parent2.setDirection(Math.random()*2*Math.PI);
	  
	  							}	
	  						}
	  					}
	  					findHeterosexual = true; 
	  					break;
	  				}
	 			
  				}
  			}	
  		
  			if (!findHeterosexual)
  			{
  				if (Math.random()<0.5)
  					org.setDirection(direction+Math.random()*(Math.PI/12));
  				else
  					org.setDirection(direction-Math.random()*(Math.PI/12));
  			}
		    }
	  	}
	  	else // the draogon will move to next unit
	  	{
	  		goToNeighbours(newX,newY);
	  	}
	}
	
	private void goToNeighbours(int newX,int newY)
	{
		Vector neighbours = enUnit.getNeighbours();
	  	
		if (neighbours.size() == 0) return;
		int R = org.getR();
		double direction = org.getDirection();
		for (int i = 0;i<neighbours.size();i++)
	  	{	  			
	  		TerrainEng subEn = (TerrainEng)(neighbours.elementAt(i));
	  		if (subEn.isInsideUnit(newX,newY))
	  		{
			//check if the unit which is going to enter is barrier area. added by x.zheng
				if(subEn.getBarrier())
				{
					org.setDirection(direction+Math.PI);
				}
				else
				{
					org.setXloc(newX);
					org.setYloc(newY);
				
	  				enUnit.removeOrganism(org);
					int oldFood = enUnit.getFood();
	  				   
					   org.setCurrentUnit(subEn);
					try{ 
						TerrainEng newEnUnit = org.getCurrentUnit();
			
			  				
						if (oldFood>newEnUnit.getFood())
						{
							org.setDirection(direction+Math.PI);	
						}
						else
						{
							if (Math.random()<0.5)
								org.setDirection(direction+Math.random()*(Math.PI/12));
							else
								org.setDirection(direction-Math.random()*(Math.PI/12));
						}
			  					
					}
					catch(Exception e){
						System.out.println("organism is dead.");
				  				//e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * set the default rules for different environment
	 **/
	public void setDefaultRules()
	{
		  if (en.getSpecies() == null) return;
		//1 represents that this organism can stay alive , 0 means that this organism cannot survive 
		  Trait aTrait = null;
	
          Enumeration eTraits = (en.getSpecies()).getTraits();
          while (eTraits.hasMoreElements())
          {
                aTrait = (Trait) eTraits.nextElement();
                Enumeration characteristics = aTrait.getCharacteristics();
               	while(characteristics.hasMoreElements())
               	{
               		Characteristic ch = (Characteristic)(characteristics.nextElement());
					Vector v = new Vector();
					v.addElement(new String("default"));
					v.addElement(new Double(1));
			        v.addElement(new Integer(2));
			        v.addElement(new Double(5.0));
			        v.addElement(new Integer(15));
			        v.addElement(new Integer(0));

               	 	rulesForLand.put(ch.getName(),v);
               		rulesForWater.put(ch.getName(),v);
					rulesForSand.put(ch.getName(),v);
					rulesForMountain.put(ch.getName(),v);
               	}
          }
		
	}
	
	
	public void addBarrier(int env)
	{
		barrierVector.addElement(new Integer(env));
	}
	
	public void removeBarrier(int env)
	{
		barrierVector.removeElement(new Integer(env));
	}
	
	public Vector getBarrier()
	{
		return barrierVector;
	}
	
	public void setAgeLimit(int age)
	{
		AGE_LIMIT = age;
	}
	public int getAgeLimit()
	{
	
		return AGE_LIMIT;
	}
	public void setAgeLimitForSickel(int age)
	{
		AGE_LIMIT_SICKEL = age;
	}
	public int getAgeLimitForSickel()
	{
		return AGE_LIMIT_SICKEL;
	}
	public void setAgeLimitForNormalInSickelEnv(int age)
	{
		AGE_LIMIT_NORMAL_SICKENV = age;
	}
	public int getAgeLimitForSNormalInSickelEnv()
	{
		return AGE_LIMIT_NORMAL_SICKENV;
	}
}
