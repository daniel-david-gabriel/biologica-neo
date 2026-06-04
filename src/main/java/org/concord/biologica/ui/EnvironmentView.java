package org.concord.biologica.ui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
// Class : EnvironmentEng
//
// Copyright � 2002, The Concord Consortium
//
// Original Author: Qing Liao
//
// $Revision: 1.15 $
// $Date: 2004/08/08 18:14:47 $
// $Author: qliao $
//
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.concord.biologica.engine.Environment;
import org.concord.biologica.engine.Organism;
import org.concord.biologica.engine.Rules;
import org.concord.biologica.engine.Species;
import org.concord.biologica.engine.Terrain;
import org.concord.biologica.engine.TerrainEng;
import org.concord.biologica.engine.Trait;
import org.concord.biologica.engine.World;
import org.concord.shared.simulation.CCSimulator;

public class EnvironmentView extends JPanel implements ActionListener, CCSimulator {

	private int maleOrganismNumber;
	private int initialMaleOrganismNumber;
	private int femaleOrganismNumber;
	private int initialFemaleOrganismNumber;
	private int initialOrganismNumber;
	private double initialSpeed = 5;
	private World world;
	private Environment environment;
	private Species species;
	private Trait trait;
	private Vector organisms = new Vector();
	private org.concord.biologica.engine.TerrainEng enUnit[][];
	private int theWidth;
	private int theHeight;
	private Timer timer;
	private int growing = 1;
	private int startX, startY,endX, endY;
	private int currentMode = DRAG_MODE_NONE;
	private int x1,y1,xs,ys;
	private boolean showRedLine = true;
	public static final int ENVIRONMENT_CHANGE_LAND = 4;
	public static final int ENVIRONMENT_CHANGE_SAND = 5;
	public static final int ENVIRONMENT_CHANGE_WATER = 6;
	public static final int ENVIRONMENT_CHANGE_MOUNTAIN = 7;
	public static final int DRAG_MODE_NONE = 0;

	public static final int DRAG_ENVIRONMENT_CHANGE = 1;
	public static final int DRAG_ORGANISM_MODE = 2;
	public static final int DRAG_SELECTED_ORGANISM_MODE = 3;
	private Organism currentOrg;
	private org.concord.biologica.engine.TerrainEng oldUnit;
	private JPopupMenu 			popupMenu;
	private Vector selectedUnits;
	private Rules rules;
	int is = 0;
	int js = 0;
        
    private ButtonGroup buttonGroup;
    private JToggleButton selectionToolToggleButton;
    private JToggleButton chromosomeToolToggleButton;
    private JToggleButton enviromentToolToggleButton;
	static private boolean imagesPreloaded = false;
	static public Image mountainImage = null;
	
	private java.util.Hashtable propertyTable = new java.util.Hashtable();
	private int initialRepast = 2,initialAgeOfMate = 15;
	private double intialHealth = 1;
       
	
   	public EnvironmentView()
   	{
   		selectedUnits = new Vector();
                            
   		timer = new Timer(200,this);
		
		if (imagesPreloaded == false)
		{
			try
			{
				org.concord.biologica.ui.LocalImageIcon mountainImageIcon = 
				new org.concord.biologica.ui.LocalImageIcon("org/concord/biologica/locked/gifs/mountain.gif");
				mountainImageIcon.waitForLoadImage();
				mountainImage = mountainImageIcon.getImage();
			}
			catch (Exception e)
			{
				mountainImage = null;
				e.printStackTrace();
			}
			imagesPreloaded = true;
		}
   		addMouseListener(new MouseAdapter(){
   			public void mousePressed(MouseEvent e){
   				if(((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) ||
   				   ((e.getModifiers() & InputEvent.CTRL_MASK) != 0))
   				{
   					if (selectedUnits.size()>0)
   					{
   						createPopupMenu();
   						if(popupMenu != null) popupMenu.show((JComponent)e.getSource(),e.getX(),e.getY());
   					}
   				}
   				else
   				{
					xs = e.getX();
					ys = e.getY();
					
   					for (int i = 0;i<theHeight;i++)
			  		{
			  		 	for(int j = 0; j<theWidth;j++)
				  		 {
   							if (enUnit[j][i].isInsideUnit(xs,ys))
				  			{
				  				
				  		 		is = i;
				  		 		js = j;
				  		 	}
				  		 }
				  	 }
				
	   				currentOrg = findDraggableOrganism (e.getX(),e.getY());
	   				if (currentOrg == null) return;
	   				oldUnit =findNewUnit(e.getX(),e.getY());
	   				oldUnit.removeOrganism(currentOrg);
					currentOrg.startDrag(e.getX(),e.getY());
				}
				
   			}
   			public void mouseClicked(MouseEvent e){
			    if (e.getClickCount() == 1) {
				if (currentMode == ENVIRONMENT_CHANGE_LAND)
				{
					Vector barrier = rules.getBarrier();
					for (int i = 0;i<theHeight;i++)
					{
						for(int j = 0; j<theWidth;j++)
						{
							if (enUnit[j][i].isInsideUnit(e.getX(),e.getY()))
							{		
								boolean isBarrier = enUnit[j][i].getBarrier();
								boolean toBeBarrier = false;
								if(barrier.size() > 0)
								{
									for(int k = 0; k < barrier.size(); k++)
									{
										if(new Integer(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_LAND).equals(barrier.elementAt(k)))
										{
											enUnit[j][i].setBarrier(true);
											toBeBarrier = true;
											k = barrier.size();
										}
									}
								}
								
								if(isBarrier && !toBeBarrier)
									enUnit[j][i].setBarrier(false);
								
								enUnit[j][i].setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_LAND);
								repaint();	
							}
						}
					}
				}
				else if (currentMode == ENVIRONMENT_CHANGE_WATER)
				{
					Vector barrier = rules.getBarrier();
					for (int i = 0;i<theHeight;i++)
					{
						for(int j = 0; j<theWidth;j++)
						{
							if (enUnit[j][i].isInsideUnit(e.getX(),e.getY()))
							{							
								boolean isBarrier = enUnit[j][i].getBarrier();
								boolean toBeBarrier = false;
								if(barrier.size() > 0)
								{
									for(int k = 0; k < barrier.size(); k++)
									{
										if(new Integer(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_WATER).equals(barrier.elementAt(k)))
										{
											enUnit[j][i].setBarrier(true);
											toBeBarrier = true;
											k = barrier.size();
										}
									}
								}
								if(isBarrier && !toBeBarrier)
									enUnit[j][i].setBarrier(false);
								enUnit[j][i].setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_WATER);
								repaint();		
							}
						}
					}
				}
			        else if (currentMode == ENVIRONMENT_CHANGE_SAND)
				{
					Vector barrier = rules.getBarrier();
					for (int i = 0;i<theHeight;i++)
					{
						for(int j = 0; j<theWidth;j++)
						{
							if (enUnit[j][i].isInsideUnit(e.getX(),e.getY()))
							{							
								boolean isBarrier = enUnit[j][i].getBarrier();
								boolean toBeBarrier = false;
								if(barrier.size() > 0)
								{
									for(int k = 0; k < barrier.size(); k++)
									{
										if(new Integer(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_SAND).equals(barrier.elementAt(k)))
										{
											enUnit[j][i].setBarrier(true);
											toBeBarrier = true;
											k = barrier.size();
										}
									}
								}
								
								if(isBarrier && !toBeBarrier)
									enUnit[j][i].setBarrier(false);
								enUnit[j][i].setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_SAND);
								repaint();		
							}
						}
					}
				}
			        else if (currentMode == ENVIRONMENT_CHANGE_MOUNTAIN)
				{
					Vector barrier = rules.getBarrier();
					
					for (int i = 0;i<theHeight;i++)
					{
						for(int j = 0; j<theWidth;j++)
						{
							if (enUnit[j][i].isInsideUnit(e.getX(),e.getY()))
							{	
								boolean isBarrier = enUnit[j][i].getBarrier();
								boolean toBeBarrier = false;
								if(barrier.size() > 0)
								{
									for(int k = 0; k < barrier.size(); k++)
									{
										if(new Integer(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_MOUNTAIN).equals(barrier.elementAt(k)))
										{
											enUnit[j][i].setBarrier(true);
											toBeBarrier = true;
											k = barrier.size();
										}
									}
								}
								
								if(isBarrier && !toBeBarrier)
									enUnit[j][i].setBarrier(false);
								
								enUnit[j][i].setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_MOUNTAIN);
								repaint();		
							}
						}
					}
				}
			  }
   			 else if (e.getClickCount() == 2) {
					if (currentMode == DRAG_SELECTED_ORGANISM_MODE)
	   				{
		   				if(currentOrg == null) return;
		   				Frame temp = JOptionPane.getFrameForComponent(getParent());
		   				BioDialogBox box = new BioDialogBox(temp,"ChromosomeView");
		   				String showingStr = "Health: "+currentOrg.getHealth()+"\n"+
		   									"Speed: "+currentOrg.getSpeed()+"\n"+
		   									"Age: "+currentOrg.getAge()+"\n"+
		   									"Food Consumption Rate: "+currentOrg.getRepast();
		   				box.setShowingText(showingStr);
		   				box.setChromosomeView(currentOrg,false);
		   				
		   			}
                }
        
   			}
   			public void mouseReleased(MouseEvent e){
   				if (currentMode == DRAG_ORGANISM_MODE)
   				{
	   				if(currentOrg == null) return;
	   				currentOrg.endDrag(e.getX(),e.getY());
	   				currentOrg.setCurrentUnit(findNewUnit(currentOrg.getXloc(),currentOrg.getYloc()));
	   				repaint();
	   			}
   				else if (currentMode == DRAG_ENVIRONMENT_CHANGE)
   				{
   				  if((e.getModifiers() & InputEvent.BUTTON3_MASK) != InputEvent.BUTTON3_MASK)
   				  {
	   				for (int i = 0;i<theHeight;i++)
			  			{
			  		 		for(int j = 0; j<theWidth;j++)
				  		 	{
						  		 		
				  		 		if (enUnit[j][i].isInsideUnit(e.getX(),e.getY()))
				  		 		{
				  		 			
				  		 			for (int i1 = is; i1<i+1;i1++){
				  		 				for(int j1 = js;j1<j+1;j1++){
				  		 					enUnit[j1][i1].setEnvironment(org.concord.biologica.engine.TerrainEng.CHANGE_ENVIRONMENT);
				  		 					selectedUnits.addElement(enUnit[j1][i1]);
				  		 				}
				  		 			}	
				  		 		}
				  		 	}
			  			}
			  		showRedLine = false;
			  		repaint();
			  	   }
		  		}
   			}
   		});
   		addMouseMotionListener(new MouseMotionAdapter(){
   			public void mouseDragged(MouseEvent e){
  				if (currentMode ==  DRAG_ENVIRONMENT_CHANGE){
  					stop();
  					x1 = e.getX();
					y1 = e.getY();
					
					showRedLine = true;
					repaint();
				}
				else if (currentMode == DRAG_ORGANISM_MODE){
					if (currentOrg == null) return;
					
					currentOrg.doDrag(e.getX(),e.getY());
					repaint();
					
				}
   			}
   			public void mouseMoved(MouseEvent e){
   			}
   		});
   		rules = new Rules(this);
   		
   	}
   	
   	private Organism findDraggableOrganism(float x,float y){
   		if(world == null) return null;
   		if(organisms.size()==0) return null;
		for(int i = 0; i < organisms.size(); i++){
			Organism org1 = (Organism)organisms.elementAt(i);
			if(org1 == null) continue;
			if (x>org1.getXloc() && x<(org1.getXloc()+org1.getR()) && y>org1.getYloc() && y<(org1.getYloc()+org1.getR()))
			   	return org1;	
		}
		return null;
   	}
   	
   	private org.concord.biologica.engine.TerrainEng findNewUnit(int x, int y){
   		for (int i = 0;i<theHeight;i++)
  		{
  		 	for(int j = 0; j<theWidth;j++)
  		 	{  
  		 		if (enUnit[j][i].isInsideUnit(x,y))
  		 			return enUnit[j][i];
  		 	}
  		}
  		return null;	
   		
   	}

  	public void setEnvironment(World world, Environment environment)
  	{
  		if (environment.getWidth() < 1 || environment.getHeight() < 1) return;
  		if (world == null) return;
  		
  		this.world = world;
  		this.environment = environment;
  		
  		species = world.getCurrentSpecies();
  		System.out.println("currentSpecies : "+species);
  		
  		rules.setDefaultRules();
  		rules.setAgeLimit(99);
  		rules.setWorld(world);
  		
  		// Set to the first trait like in UI
  		setTrait((Trait)species.getTraits().nextElement());
  		
  		
  		theWidth = environment.getWidth();
  		theHeight = environment.getHeight();
  		
  		int w = environment.getWidth();
  		int h = environment.getHeight();
  		
  		removeAll();
  		
  		setLayout(new GridLayout(w,h));
  		enUnit = new TerrainEng[w][h];
  		
  		int subW =(int)(this.getWidth()/w);
	 	int subH = (int)(this.getHeight()/h);
  		 
  		 for(int i = 0; i<h; i++)
  		 {
  		 	for(int j = 0; j<w; j++)
  		 	{
  		 		TerrainEng eu = new TerrainEng(this,rules,subW*j,subH*i,subW,subH);
  		 		eu.setEnvironment(mapTerrainToEng(environment.getTerrain(j,i)));
  		 		enUnit[j][i]=eu;            
  		 	}
  		 }
  		 setNeighbours();
  	}
  	
  	private int mapTerrainToEng(Terrain terrain) {
  		
  		if (terrain.getName().toUpperCase().contains("LAND")) return TerrainEng.ENVIRONMENT_LAND;
  		if (terrain.getName().toUpperCase().contains("WATER")) return TerrainEng.ENVIRONMENT_WATER;
  		if (terrain.getName().toUpperCase().contains("SAND")) return TerrainEng.ENVIRONMENT_SAND;
  		if (terrain.getName().toUpperCase().contains("MOUNTAIN")) return TerrainEng.ENVIRONMENT_MOUNTAIN;
  		
  		return TerrainEng.CHANGE_ENVIRONMENT;
  	}
  	
  	public TerrainEng[][] getCurrentEnvironmentTerrain() {
  		return enUnit;
  	}
  	
  	public Environment getEnvironment() {
  		return environment;
  	}
  	
  	public World getWorld()
  	{
  		return world;
  	}
  	
  	public void start()
  	{
  		timer.start();
  	}
  	public void stop()
  	{
  		timer.stop();
  	}
  	public boolean isRunning()
  	{
  		return timer.isRunning();
  	}
  	public void reset()
  	{
  		stop();
 
  		while(organisms.size()>0)
  		{
  			removeOrganism((Organism)(organisms.elementAt(0)));
  		}
  		if (species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
  			setOrganismNumber(initialMaleOrganismNumber,initialFemaleOrganismNumber);
  		else
  			setOrganismNumber(initialOrganismNumber);
  		for (int i = 0;i<theHeight;i++)
  		 {
  		 	for(int j = 0; j<theWidth;j++)
  		 	{
  		 		enUnit[j][i].setFood(org.concord.biologica.engine.TerrainEng.FOOD_FULL);            
  		 	}
  		 }
  		repaint();
  	}
  	public void continueSteps()
	{
	}	
	public void doOneStep(){
	
		if (isRunning())
			stop();
		nextStep();
		for (int i = 0;i<theHeight;i++)
		 {
	 		for(int j = 0; j<theWidth;j++)
	 		{  
	 			//int food = enUnit[j][i].getFood() +org.concord.biologica.engine.TerrainEng.GROWING;
				//food growth rate varies from the environment. added by x.zheng
				int foodGrowing = 0;
				
				switch(enUnit[j][i].getEnvironment())
				{
					case TerrainEng.ENVIRONMENT_LAND:
						foodGrowing = enUnit[j][i].getLandGrowing();
						break;
					case TerrainEng.ENVIRONMENT_WATER:
						foodGrowing = enUnit[j][i].getWaterGrowing();
						break;
					case TerrainEng.ENVIRONMENT_SAND:
						foodGrowing = enUnit[j][i].getSandGrowing();
						break;
					case TerrainEng.ENVIRONMENT_MOUNTAIN:
						foodGrowing = enUnit[j][i].getMountainGrowing();
						break;
					default:
						break;
				}
				int food = enUnit[j][i].getFood() + foodGrowing;
	 			
	 			if (food<org.concord.biologica.engine.TerrainEng.FOOD_FULL)
	 				enUnit[j][i].setFood(food);
          
	 		}
	 	}	
	}
	
	
	public int getSimulationState(){
		return 1;
	}
  	public void setCurrentDragMode(int i)
  	{
  			currentMode = i;
  	}
  	public int getCurrentDragMode()
  	{
  		return currentMode;
  	}
  	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == timer)
		{
			Runnable updateAComponent = new Runnable() {
   					 public void run() { /*System.gc();*/ }
					};
			SwingUtilities.invokeLater(updateAComponent);


			nextStep();
			 for (int i = 0;i<theHeight;i++)
  			 {
  		 		for(int j = 0; j<theWidth;j++)
  		 		{  
  		 			//int food = enUnit[j][i].getFood() +org.concord.biologica.engine.TerrainEng.GROWING;
					//food growth rate varies from the environment. added by x.zheng
					int foodGrowing = 0;
				
					switch(enUnit[j][i].getEnvironment())
					{
						case TerrainEng.ENVIRONMENT_LAND:
							foodGrowing = enUnit[j][i].getLandGrowing();
							break;
						case TerrainEng.ENVIRONMENT_WATER:
							foodGrowing = enUnit[j][i].getWaterGrowing();
							break;
						case TerrainEng.ENVIRONMENT_SAND:
							foodGrowing = enUnit[j][i].getSandGrowing();
							break;
						case TerrainEng.ENVIRONMENT_MOUNTAIN:
							foodGrowing = enUnit[j][i].getMountainGrowing();
							break;
						default:
							break;
					}
					int food = enUnit[j][i].getFood() + foodGrowing; 		 			
  		 				if (food<org.concord.biologica.engine.TerrainEng.FOOD_FULL)
							enUnit[j][i].setFood(food);
              
  		 		}
  		 	}	
		}
		else if(evt.getActionCommand().equals("Land"))
		{
			for(int i = 0;i<selectedUnits.size();i++)
			{
				((org.concord.biologica.engine.TerrainEng)(selectedUnits.elementAt(i))).setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_LAND);
				repaint();
			}
			selectedUnits.removeAllElements();
		}
		else if(evt.getActionCommand().equals("Water"))
		{
			for(int i = 0;i<selectedUnits.size();i++)
			{
				((org.concord.biologica.engine.TerrainEng)(selectedUnits.elementAt(i))).setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_WATER);
				repaint();
			}
			selectedUnits.removeAllElements();
		}
		else if(evt.getActionCommand().equals("Sand"))
		{
			for(int i = 0;i<selectedUnits.size();i++)
			{
				((org.concord.biologica.engine.TerrainEng)(selectedUnits.elementAt(i))).setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_SAND);
				repaint();
			}
			selectedUnits.removeAllElements();
		}
		else if(evt.getActionCommand().equals("Mountain"))
		{
			for(int i = 0;i<selectedUnits.size();i++)
			{
				((org.concord.biologica.engine.TerrainEng)(selectedUnits.elementAt(i))).setEnvironment(org.concord.biologica.engine.TerrainEng.ENVIRONMENT_MOUNTAIN);
				repaint();
			}
			selectedUnits.removeAllElements();
		}
	}
	
  	private void setNeighbours()
  	{
       if (enUnit == null) return;
       if (theHeight == 1 && theWidth == 1)
       {
       		return;
       }
  		 for (int i = 0;i<theHeight;i++)
  		 {
  		 	for(int j = 0; j<theWidth;j++)
  		 	{
  		 		if (i==0 &&j==0)
  		 		{
  		 			enUnit[j][i].addNeighbours(enUnit[j][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i+1]);
  		 		}
  		 		else if ((i==theHeight-1) && (j==0))
  		 		{
 		 			enUnit[j][i].addNeighbours(enUnit[j][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i]);
  		 		}
  		 		else if (i==0 && (j==theWidth-1))
  		 		{
 
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i+1]);
  		 		}
  		 		else if ((i==theHeight-1) && (j==theWidth-1))
  		 		{
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i-1]);
  		 		}
  		 		else if (i==0 &&(j>0 && j<theWidth-1))
  		 		{
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i+1]);
  		 		}
  		 		else if ((i>0 && i<theHeight-1) &&j==0)
  		 		{
  		 			enUnit[j][i].addNeighbours(enUnit[j][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i+1]);
  		 		}
  		 		
  		 		else if ((i>0 && i<theHeight-1) && (j==theWidth-1))
  		 		{
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i+1]);
  		 		}
  		 		else if ((i==theHeight-1) && (j>0 && j<theWidth-1))
  		 		{
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i]);
  		 		}
  		 		else
  		 		{
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i-1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i]);
  		 			enUnit[j][i].addNeighbours(enUnit[j-1][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j][i+1]);
  		 			enUnit[j][i].addNeighbours(enUnit[j+1][i+1]);	
  		 		}
  		 	}
  		 }
  		
  	}
  	public void setOrganismNumber(int num)
  	{
  		if (world==null) return;
  		while(organisms.size()>0)
  		{
  			removeOrganism((Organism)(organisms.elementAt(0)));
  		}
  		world.deleteAllOrganisms(true);
  		world.deleteAllFamilies(true,true);
  		initialOrganismNumber = num;
  		if (species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
  		{
  			maleOrganismNumber =(int)(num * Math.random());
  			femaleOrganismNumber = num - maleOrganismNumber;
  			setOrganismNumber(maleOrganismNumber,femaleOrganismNumber);
  		}
  		else
  		{
  			
  			for(int i = 0;i<num;i++)
  			{
  				createOrganisms(Organism.NO_SEX);
  				maleOrganismNumber =0;
  				femaleOrganismNumber = 0;
  			}
  		}

  	}
  	
  	public TerrainEng findOrganism(Organism org)
	{
		for (int i = 0;i<theHeight;i++)
  		{
  		 	for(int j = 0; j<theWidth;j++)
  		 	{  
				if(enUnit[j][i].getOrganism().indexOf(org) != -1)
				{
					return enUnit[j][i];
				}
  		 	}
  		}

		return null;
	}

  	public Vector getOrganisms()
  	{
  		return organisms;
  	}
  	public void addOrganism(Organism org)
  	{
  		organisms.addElement(org);
  		if (org.getSex() == Organism.MALE)
  			setMaleOrganismNumber(maleOrganismNumber+1);
  		else if (org.getSex() == Organism.FEMALE)
  			setFemaleOrganismNumber(femaleOrganismNumber+1);
  		else if (org.getSex() == Organism.NO_SEX)
  		{
  			setMaleOrganismNumber(0);
  			setFemaleOrganismNumber(0);
  		}
  		
  		if (org.containsFatalCharacteristic()) {
			org.delete();
			world.deleteOrganism(org);
			
			return;
		}
  	}
	public void removeOrganism(Organism org)
	{
		organisms.removeElement(org);
		
		if (org.getSex() == Organism.MALE)
  			setMaleOrganismNumber(maleOrganismNumber-1);
  		else if (org.getSex() == Organism.FEMALE)
  			setFemaleOrganismNumber(femaleOrganismNumber-1);
  			
		
		org.delete();
		world.deleteOrganism(org);
		

		org = null;
	}
	
	
  	public void setOrganismNumber(int maleNumber, int femaleNumber)
  	{
            setOrganismNumber(maleNumber,"",femaleNumber,"");  	
    }
      
    public void setOrganismNumber(int maleNumber,String maleGenotype,int femaleNumber,String femaleGenotype)
  	{
  		initialMaleOrganismNumber = maleNumber;
  		initialFemaleOrganismNumber = femaleNumber;
  		while(organisms.size()>0)
  		{
  			removeOrganism((Organism)organisms.elementAt(0));
  		}
  		world.deleteAllOrganisms(true);
  		
  		world.deleteAllFamilies(true,true);
  
  		
  		maleOrganismNumber = maleNumber;
  		femaleOrganismNumber = femaleNumber;
  		if (species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
  		{
	  		for (int i = 0;i<maleOrganismNumber;i++)
	  		{
	  			createOrganisms(Organism.MALE,maleGenotype);

	  		}
	  		for (int i = 0;i<femaleOrganismNumber;i++)
	  		{
	  			createOrganisms(Organism.FEMALE,femaleGenotype);	
	  		}
		}
		
  		repaint();
 
  	}
        
    /**
     * add certain amount to Male Organism with special Genotype
    */
    public void addOrganismsWithDefinedGenotype(int type,int num,String genotype)
    {

		Organism tmpOrg = null;
  		if (species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
  		{
	  		for (int i = 0;i<num;i++)
	  		{
	  			tmpOrg = createOrganisms(type,genotype);
			}
        }
        if (type == Organism.MALE) maleOrganismNumber +=num;
        else if (type == Organism.FEMALE) femaleOrganismNumber +=num;
        

  		
  		repaint();
    }
             

  	private Organism createOrganisms(int type)
  	{
        return    createOrganisms(type,"");
    }
    
    private Organism createOrganisms(int type,String genotype)
    {
		
        Organism org= new Organism(world, "", species,type,genotype);
		while (org.containsFatalCharacteristic()){
			org.delete();
			world.deleteOrganism(org);
			//org = new Organism(world,type,"",species);
            org= new Organism(world, "", species,type,genotype);
		}
		
		//org.setSpeed(speed);
		org.setDirection(2*Math.PI*Math.random());
		organisms.addElement(org);
		int w = Math.round((float)((theWidth-1)*Math.random()));
		int h = Math.round((float)((theHeight-1)*Math.random()));

		TerrainEng currentUnit = enUnit[w][h];
		org.setCurrentUnit(currentUnit);
		org.setXloc((int)(enUnit[w][h].getX()+Math.round(Math.random()*(enUnit[w][h].getWidth()-org.getR()))));
		org.setYloc((int)(enUnit[w][h].getY()+Math.round(Math.random()*(enUnit[w][h].getHeight()-org.getR()))));
		int age =(int)Math.round(Math.random()*Organism.AGE_LIMIT);
		
		org.setAge(age);
		setOrganismProperties(org);

		return org;
  	}
  	public Species getSpecies()
  	{
  		return species;
  	}
  	public void setMaleOrganismNumber(int num)
  	{
  		maleOrganismNumber = num;
  	}
  	public void setFemaleOrganismNumber(int num)
  	{
  		femaleOrganismNumber = num;
  	}
  	public int getMaleOrganismNumber()
  	{
  		return maleOrganismNumber;
  	}
  	
  	public int getFemaleOrganismNumber()
  	{
  		return femaleOrganismNumber;
  	}
 
	public void createPopupMenu(){
		if(popupMenu != null){
			return;
		}
		popupMenu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Land");
		item.addActionListener(this);
		popupMenu.add(item);
		item = new JMenuItem("Water");
		item.addActionListener(this);
		popupMenu.add(item);
		item = new JMenuItem("Sand");
		item.addActionListener(this);
		popupMenu.add(item);
		item = new JMenuItem("Mountain");
		item.addActionListener(this);
		popupMenu.add(item);		
		/*item = new JMenuItem("Set Barrier");
		item.addActionListener(this);
		popupMenu.add(item);		
		item = new JMenuItem("Unset Barrier");
		item.addActionListener(this);
		popupMenu.add(item);*/		
	}
	
	public int getEnvironmentWidth()
	{
		return theWidth;
	}
	
	public int getEnvironmentHeight()
	{
		return theHeight;
	}
	
	public void nextStep()
	{
		if (organisms.size() == 0) return;
		for (int i = 0; i<organisms.size(); i++)
		{
			
			((Organism)(organisms.elementAt(i))).nextStep();	
		}
		this.repaint();
	}

	public void gotoPreviousStep(){
	}

	public int getAvailableSteps(){
	    throw new RuntimeException("getAvailableSteps is not implemented for the "+getClass().getName()+" class ");
	}

	public int getCurrentStepNumber(){
	    throw new RuntimeException("getCurrentStepNumber is not implemented for the "+getClass().getName()+" class ");
	}

	public void goToStep(int n){
	    throw new RuntimeException("goToStep is not implemented for the "+getClass().getName()+" class ");
	}


	public void paintComponent(Graphics g)
	{
		
		try
			{
				g.clearRect(0,0,this.getWidth(),this.getHeight());
				
				for (int i = 0;i<theHeight;i++)
		  		{
		  		 	for(int j = 0; j<theWidth;j++)
		  		 	{
		  		 		enUnit[j][i].paint(g,enUnit[j][i].getX(),enUnit[j][i].getY(),enUnit[j][i].getWidth(),enUnit[j][i].getHeight());
		  		 	}
		  		}
		  		for (int i = 0;i<organisms.size();i++)
		 			{
		 				Organism org =(Organism)(organisms.elementAt(i));
		 				//int r = (int)(Math.round(Math.min(this.getWidth(),this.getHeight())));
		 				int r = 10;
		 				if (org !=null)
		 					org.paintOrganism(g);
		 			}
		 		if(currentMode == DRAG_ENVIRONMENT_CHANGE)
		 		{
		 			
					if (showRedLine)
					{
						g.setColor(Color.red);
						g.drawRect(xs,ys,(x1-xs),(y1-ys));
						
					}
					else
					{
						g.setColor(Color.black);
						g.drawRect(0,0,0,0);
					}
					
				}
				
				
			}
			catch(Exception e)
			{
			}
	}
	
	public void setRules(int envi, String traits,double health,int repast, double speed, int ageOfMate)
	{
		rules.setRules(envi,traits,health,repast,speed,ageOfMate);
	}
	public void setHealthRules(int envi, String traits,double health)
	{
		rules.setHealthRules(envi,traits,health);
	}
	public void setFoodComsumptionRate(int envi, String traits,int repast)
	{
		rules.setFoodComsumptionRate(envi,traits,repast);
	}
	public void setSpeedRules(int envi, String traits,double speed)
	{
		rules.setSpeedRules(envi,traits,speed);
	}
	public void setAgeOfMateRules(int envi, String traits,int ageOfMate)
	{
		rules.setAgeOfMateRules(envi,traits,ageOfMate);
	}
	
	public void setOrganismProperties(Organism org)
	{
		if (org == null) return;
				
	   	Hashtable ruleTable = rules.getRules((org.getCurrentUnit()).getEnvironment());
	   	Enumeration theRules = ruleTable.keys();

		if ( ruleTable != null)
		{
			while (theRules.hasMoreElements())
			{
	
			 	String keyWord = (String)(theRules.nextElement());
			 	if (org.containsCharacteristic(keyWord))
			 	{
			 		Vector v = (Vector)ruleTable.get(keyWord);
		 		    
		 			org.setHealth(((Double)(v.elementAt(1))).doubleValue());
					org.setRepast(((Integer)(v.elementAt(2))).intValue());
					org.setSpeed(((Double)(v.elementAt(3))).doubleValue());
					org.setAgeOfMate(((Integer)(v.elementAt(4))).intValue());
				}
				
			}
		}
		
	}
	private void changeOrganismPropertyies()
	{
		if (organisms == null) return;
		
		//set Default properties for organisms
 		for(int i = 0; i<organisms.size();i++)
		{

			Organism tmpOrg =(Organism)(organisms.elementAt(i));
			tmpOrg.setHealth(intialHealth);
			tmpOrg.setRepast(initialRepast);
			tmpOrg.setSpeed(initialSpeed);
			tmpOrg.setAgeOfMate(initialAgeOfMate);	
		}
	
 			
		Enumeration prop = propertyTable.keys();

		while (prop.hasMoreElements())
		{
				 
			String keyWord = (String)(prop.nextElement());
			for(int i = 0; i<organisms.size();i++)
			{

				Organism tmpOrg =(Organism)(organisms.elementAt(i));
				
				if (tmpOrg.containsCharacteristic(keyWord))
				{
					Vector v = (Vector)propertyTable.get(keyWord);
					tmpOrg.setHealth(((Double)(v.elementAt(0))).doubleValue());
					tmpOrg.setRepast(((Integer)(v.elementAt(1))).intValue());
					tmpOrg.setSpeed(((Double)(v.elementAt(2))).doubleValue());
					tmpOrg.setAgeOfMate(((Integer)(v.elementAt(3))).intValue());
				}
			}
			
		}
	}
	
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
        if (species == null)
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
            Enumeration eTraits = species.getTraits();
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
		
	public void setTrait(Trait aTrait)
	{
		trait = aTrait;
	}
	public Trait getTrait()
	{
		return trait;
	}
	public void paintUnit(int i, int j, int env)
	{
		if(i < 0 || j < 0 || i >= theWidth || j >= theHeight)
		{
			System.out.println("The subunit doesn't exist");
			return;
		}
		
		enUnit[i][j].setEnvironment(env);
		repaint();		
	}
	public void setBarierUnit(int env, boolean option)
	{
		if(option)
		{	
			rules.addBarrier(env);
		
			for (int i = 0;i<theHeight;i++)
			{
				for(int j = 0; j<theWidth;j++)
				{	
					if(enUnit[i][j].getEnvironment() == env)
						enUnit[i][j].setBarrier(true);
				}
			}
		}
		else
		{
			rules.removeBarrier(env);
			for (int i = 0;i<theHeight;i++)
			{
				for(int j = 0; j<theWidth;j++)
				{	
					if(enUnit[i][j].getEnvironment() == env)
						enUnit[i][j].setBarrier(false);
				}
			}
		}
	}
	/*public void setBarrierUnit(int i, int j, boolean env)
	{
		if(i < 0 || j < 0 || i >= theWidth || j >= theHeight)
		{
			System.out.println("The subunit doesn't exist");
			return;
		}
		
		enUnit[i][j].setBarrier(env);	
	}*/
	/*public void setBarrierUnitByCoor(int x, int y, boolean env)
	{	
		org.concord.biologica.engine.TerrainEng tempUnit = findNewUnit(x,y);
		
		if(tempUnit == null)
		{
			System.out.println("The subunit doesn't exist");
			return;
		}
		tempUnit.setBarrier(env);	
	}*/
	public int getEnvironmentProperty(int i, int j)
	{
		return enUnit[i][j].getEnvironment();
	}
	
	public void setAgeLimit(int age)
	{
		rules.setAgeLimit(age);
	}
	public void setAgeLimitForSickel(int age)
	{
		rules.setAgeLimitForSickel(age);
	}
	public void setAgeLimitForNormalInSickelEnv(int age)
	{
		rules.setAgeLimitForNormalInSickelEnv(age);
	}
}