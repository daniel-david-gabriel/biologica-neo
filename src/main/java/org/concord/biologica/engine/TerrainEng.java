package org.concord.biologica.engine;


// Class : Terrain
//
// Copyright © 2002, The Concord Consortium
//
// Original Author: Qing Liao
//
// $Revision: 1.8 $
// $Date: 2004/08/08 18:15:55 $
// $Author: qliao $
//

import java.awt.Color;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.*;
import java.util.Vector;
import org.concord.biologica.ui.*;
public class TerrainEng
{
	//food on this Unit
	private int currentFood;
	
	private int currentWater;
	
	private int currentTemperature;
	
	private int numberOfOrganism;
	
	private Dimension location;
	
	private boolean blnHasDragon;
	
	private Color color = new Color(0,0,0);
	
	private Vector neighbours;
	
	
	//this Unit is land of water;
	private int environmentProperties;
	private boolean barrierProperties;
	
	static final public int ENVIRONMENT_LAND = 1;
	static final public int ENVIRONMENT_WATER = 2;
	static final public int ENVIRONMENT_SAND = 4;
	static final public int ENVIRONMENT_MOUNTAIN = 5;
	static final public int CHANGE_ENVIRONMENT = 3;
	static final public int FOOD_FULL = 200;
	static final public int FOOD_LOW = 10;
	static final public int WATER_LOW = 30;
	static final public int GROWING = 2;
	
	//Organisms on this Unit;
	private Vector organisms;
	private Vector maleOrg;
	private Vector femaleOrg;
	private EnvironmentView en;
	
	private int x1,y1, x2,y2, x3,y3, x4,y4;
	private int theWidth, theHeight;
	private Rules rules;
	
	public int landGrowing = 2;
	public int waterGrowing = 2;
	public int sandGrowing = 2;
	public int mountainGrowing = 2;
	
	public int landConsuming = 2;
	public int waterConsuming = 2;
	public int sandConsuming = 2;
	public int mountainConsuming = 2;
		
	public TerrainEng()
	{	
	}
	
	public TerrainEng(EnvironmentView e,Rules rule,int xs, int ys, int width, int height)
	{
		en = e;
		environmentProperties = ENVIRONMENT_LAND;
		barrierProperties = false;
		currentFood = FOOD_FULL;
		currentWater = 220;
		numberOfOrganism = 0;
		organisms = new Vector();
		maleOrg = new Vector();
		femaleOrg = new Vector();
		neighbours = new Vector();
		
		x1 = xs;
		y1 = ys;
		
		x2 = xs+width;
		y2 = ys;
		
		x3 = xs;
		y3 = ys+height;
		
		x4 = xs+width;
		y4 = ys+height;
		
		theWidth = width;
		theHeight = height;
		rules = rule;
		
	}
	public int getX()
	{
		return x1;
	}
	public int getY()
	{
		return y1;
	}
	
	public int getWidth()
	{
		return theWidth;
	}
	public int getHeight()
	{
		return theHeight;
	}
	public Rules getRules()
	{
		return rules;
	}
  
	//get number of Orgnaisms on this unit
	public int getNumberOfOrganism()
	{
		numberOfOrganism = organisms.size();
		return numberOfOrganism;
	}
	
	public EnvironmentView getMotherEnvi()
	{
		return en;
	}
	public World getWorld()
	{
		return en.getWorld();
	}
	//change enivornment
	public void setEnvironment(int env)
	{
		environmentProperties = env;
	}
	public int getEnvironment()
	{
		return environmentProperties;
	}
	public void setBarrier(boolean env)
	{
		barrierProperties = env;
	}
	public boolean getBarrier()
	{
		return barrierProperties;
	}
	
	//add new organisms in this Unit
	public void addOrganism(Organism org)
	{
		if(org.isDeleted())
		{
            throw new ObjectDeletedException("organism deleted");
		}

		int r = (int)(Math.round(Math.min(this.getWidth(),this.getHeight())));
		org.setR((int)(r/5));
		if(organisms.indexOf(org) == -1)
		{
			organisms.addElement(org);
		}
	}
	
	public void removeOrganism(Organism org)
	{
		organisms.removeElement(org);
		//org.delete();
		org = null;
	}
	
	public Vector getOrganism()
	{
		return organisms;
	}
	
	public boolean hasDragon()
	{
		if (organisms.size()>0)
			blnHasDragon = true;
		else
			blnHasDragon = false;
		return blnHasDragon;
	}
 	public void paint(Graphics g,int xs, int ys, int width, int height)
 	{
 		paintBackground(g,xs,ys,width,height);
 		/*if (hasDragon())
 		{
 			for (int i = 0;i<organisms.size();i++)
 			{
 				Organism org =(Organism)(organisms.elementAt(i));
 				//int r = (int)(Math.round(Math.min(this.getWidth(),this.getHeight())));
 				int r = 10;
 				org.paintOrganism(g);
 			}
 		}*/
 	}
 	
 	public void addNeighbours(TerrainEng e1)
 	{
 		neighbours.addElement(e1);
 	}
 	
 	public Vector getNeighbours()
 	{
 		return neighbours;
 	}
 	
 	public void setFood(int f)
 	{
 		currentFood = f;
 	}
 	
 	public int getFood()
 	{
 		return currentFood;
 	}
 	public void setWater(int f)
 	{
 		currentWater = f;
 	}
 	public int getWater()
 	{
 		return currentWater;
 	}
  	public void setLandGrowing(int f)
 	{
 		landGrowing = f;
 	}
 	public int getLandGrowing()
 	{
 		return landGrowing;
 	}
  	public void setWaterGrowing(int f)
 	{
 		waterGrowing = f;
 	}
 	public int getWaterGrowing()
 	{
 		return waterGrowing;
 	}
  	public void setSandGrowing(int f)
 	{
 		sandGrowing = f;
 	}
 	public int getSandGrowing()
 	{
 		return sandGrowing;
 	}
  	public void setMountainGrowing(int f)
 	{
 		mountainGrowing = f;
 	}
 	public int getMountainGrowing()
 	{
 		return mountainGrowing;
 	}
  	/*public void setLandConsuming(int f)
 	{
 		landConsuming = f;
 	}
 	public int getLandConsuming()
 	{
 		return landConsuming;
 	}
  	public void setSandConsuming(int f)
 	{
 		sandConsuming = f;
 	}
 	public int getSandConsuming()
 	{
 		return sandConsuming;
 	}
   	public void setWaterConsuming(int f)
 	{
 		waterConsuming = f;
 	}
 	public int getWaterConsuming()
 	{
 		return waterConsuming;
 	}
  	public void setMountainConsuming(int f)
 	{
 		mountainConsuming = f;
 	}
 	public int getMountainConsuming()
 	{
 		return mountainConsuming;
 	}*/
	public boolean isInsideUnit(int sx,int sy)
 	{
		boolean bln = false;
 		if (sx>=x1 && sx<=x2)
 			if(sy>=y1 && sy<=y4)
 				bln = true;
		
 		return bln;
 	
 	}
 	protected void paintBackground(Graphics g,int xs, int ys, int width, int height)
    {
    	
    	if (environmentProperties == TerrainEng.ENVIRONMENT_LAND)
    	{
       		g.setColor(new Color((225-currentFood),255,(225-currentFood)));
       		g.fillRect(xs,ys,width,height);
       	}
      	else if (environmentProperties == TerrainEng.ENVIRONMENT_WATER)
       	{
       		//g.setColor(new Color(0,255,255-currentFood));
       		g.setColor(new Color((220-currentFood),255,255));
       		g.fillRect(xs,ys,width,height);
       	}
		else if (environmentProperties == TerrainEng.ENVIRONMENT_SAND)
       	{
       		g.setColor(new Color(221,(int)(210-Math.round(currentFood/30)),25));
       		g.fillRect(xs,ys,width,height);
       	}
		else if (environmentProperties == TerrainEng.ENVIRONMENT_MOUNTAIN)
       	{
		    Color bg = new Color(10, 114,(242-currentFood));
		    Color oldColor = g.getColor();
		    g.setColor(bg);
		    g.fillRect(xs, ys, width, height);
		    g.drawImage(org.concord.biologica.ui.EnvironmentView.mountainImage, xs, ys,width, height,null);
		    //g.drawImage(org.concord.biologica.ui.EnvironmentView.mountainImage, xs, ys, width, height,bg,null);
       	    g.setColor(oldColor);
       	}
       	else if (environmentProperties == TerrainEng.CHANGE_ENVIRONMENT)
       	{
       		g.setColor(Color.blue);
       		//g.fillRect(xs,ys,width,height);
		
		int tempX = xs + 1;
		int tempY = ys + 1;
		
		/*g.fillOval(tempX, tempY, 5, 5);
		g.fillOval(tempX+30, tempY+30, 5, 5);*/
		int i = 0;
		
		while(tempX+i < xs + width - 1)
		{
			int j = 0;
			while(tempY+j < ys + height -1 )
			{	
				g.fillOval(tempX+i, tempY+j, 5, 5);
				j += 5;
			}
			i += 5;
		}
       	}
       	g.setColor(Color.black);
    	g.drawRect(xs,ys,width,height);
              
    }
 	
    
}
