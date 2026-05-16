/**
 * Class: PracticeView - includes all of the common functions, etc., for each
 * of the BioLogica practice sessions
 *	
 * Copyright © 2000, The Concord Consortium
 *
 * Original Authors: Rose Len and Ed Burke
 * Date: 4/13/00
 * 
 * $Revision: 1.1.1.1 $Date::
 * $Author:
 *
**/

package org.concord.biologica.ui;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.concord.biologica.engine.World;
import org.concord.biologica.engine.Organism;
import org.concord.biologica.engine.Species;

import org.concord.biologica.ui.BioLogicaFrame;
import org.concord.biologica.ui.SelectionSet;


public class PracticeView
extends JPanel
{
    // Defining an instance variable called "frame"
    JFrame frame;
    JPanel contentPane;
    World world;
    SelectionSet selectSet;
    Species species;
    Dimension saveSize;
    
    Organism femaleOrganism;
    Organism maleOrganism;
    
    // Constructor
    public PracticeView(JFrame frame)
    {
        super(); // Calls JPanel "no arg" constructor
        
        this.frame = frame;
        contentPane = (JPanel) frame.getContentPane();
        setVisible(true); // "this" particular practice view is not visible
        selectSet = ((BioLogicaFrame) frame).selectSet;
        
        repaint();
    }
    
    public Organism createOrganism(String name, int sex)
    {
        Organism organism = new Organism(world, sex, name, world.getCurrentSpecies());
        for (int i = 0; i < 100; i++)
        {
            if (organism.containsFatalCharacteristic())
            {
                organism = new Organism(world, sex, name, world.getCurrentSpecies());
                continue;
            }
            else
            {
                return organism;
            }
        }
        return organism;
    }
    
    public void initialize(File worldFile)
    {
        saveSize = frame.getSize();
        frame.setResizable(true);
        frame.setSize(800, 540);
        frame.setResizable(false);
        frame.setContentPane(this);
        // Initializing world
        world = new World(worldFile);
        species = world.getCurrentSpecies();
        
        if (species.getDiploidType() == Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
        {
            femaleOrganism = createOrganism("Plant One", Organism.NO_SEX);
            maleOrganism = createOrganism("Plant Two", Organism.NO_SEX);
        }
        else
        {
            femaleOrganism = createOrganism("Female", Organism.FEMALE);
            maleOrganism = createOrganism("Male", Organism.MALE);
        }
    }

    
    // Resets each type of practice session in prep for next one called
    public void reset()
    {
        world.delete();
        
        frame.setContentPane(contentPane);
        frame.setResizable(true);
        frame.setSize(saveSize);
        frame.setResizable(false);
        
        repaint();
    }
}

