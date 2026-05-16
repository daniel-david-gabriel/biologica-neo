/**
 * Class: ChromosomePracticeView - for practicing and experimenting with Geno-Pheno relationships 
 * 
 * Copyright © 2000, The Concord Consortium
 *
 * Original Authors: Rose Len and Ed Burke
 * Date: 4/12/00
 * 
 * $Revision: 1.1.1.1 $Date::
 * $Author:
 *
**/

package org.concord.biologica.ui;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.concord.biologica.engine.Organism;
import org.concord.biologica.engine.Species;
import org.concord.biologica.engine.SpeciesImage;
import org.concord.biologica.engine.World;
import org.concord.biologica.ui.ChromosomeView;
import org.concord.biologica.ui.PracticeView;
import org.concord.biologica.ui.StaticOrganismView;


public class ChromosomePracticeView
extends PracticeView
{
    // Instance variables
    StaticOrganismView topStaticView;
    StaticOrganismView bottomStaticView;
    ChromosomeView topHalfChromoView;
    ChromosomeView bottomHalfChromoView;
    
    JScrollPane topStaticScroll;
    JScrollPane bottomStaticScroll;
    JScrollPane topHalfChromoScroll;
    JScrollPane bottomHalfChromoScroll;
    
    
    // Constructor
    public ChromosomePracticeView(JFrame frame) // This "frame" is a parameter
    {
        super(frame); // Calls whatever is in the constructor of PracticeView
        
        topStaticView = new StaticOrganismView();
        topStaticView.setOrganismImageSize(SpeciesImage.LARGE_IMAGE_SIZE);
        topStaticView.setNameTextVisible(false);
        topStaticView.setSpeciesTextVisible(true);
        topStaticView.setCharacteristicsTextVisible(true);
        topStaticView.setSexTextVisible(true);
        
        bottomStaticView = new StaticOrganismView();
        bottomStaticView.setOrganismImageSize(SpeciesImage.LARGE_IMAGE_SIZE);
        bottomStaticView.setNameTextVisible(false);
        bottomStaticView.setSpeciesTextVisible(true);
        bottomStaticView.setCharacteristicsTextVisible(true);
        bottomStaticView.setSexTextVisible(true);
        
        topHalfChromoView = new ChromosomeView();
        topHalfChromoView.setSelectionSet(selectSet);
        bottomHalfChromoView = new ChromosomeView();
        bottomHalfChromoView.setSelectionSet(selectSet);

        topStaticScroll = new JScrollPane(topStaticView);
        bottomStaticScroll = new JScrollPane(bottomStaticView);
        topHalfChromoScroll = new JScrollPane(topHalfChromoView);
        bottomHalfChromoScroll = new JScrollPane(bottomHalfChromoView);

        topStaticScroll.setSize(240, 246);
        topStaticScroll.setLocation(1, 0);
        topStaticScroll.setVisible(false);
        
        bottomStaticScroll.setSize(240, 246);
        bottomStaticScroll.setLocation(1, 247);
        bottomStaticScroll.setVisible(false);
        
        topHalfChromoScroll.setSize(552, 246);
        topHalfChromoScroll.setLocation(241, 0);
        topHalfChromoScroll.setVisible(true);
        
        bottomHalfChromoScroll.setSize(552, 246);
        bottomHalfChromoScroll.setLocation(241, 247);
        bottomHalfChromoScroll.setVisible(true);
        
        setLayout(null); // All of the following are associated with the ChromosomePracticeView panel
        add(topStaticScroll);
        add(bottomStaticScroll);
        add(topHalfChromoScroll);
        add(bottomHalfChromoScroll);
    }
    
    public void initialize(File worldFile)
    {
        super.initialize(worldFile);
        
        topStaticView.setOrganism(femaleOrganism);
        bottomStaticView.setOrganism(maleOrganism);
        topHalfChromoView.setOrganism(femaleOrganism);
        bottomHalfChromoView.setOrganism(maleOrganism);
        
        add(topStaticScroll);
        add(bottomStaticScroll);
        
        topStaticView.setSize(240, 370);
        bottomStaticView.setSize(240, 370);
        
        topStaticScroll.setVisible(true);
        bottomStaticScroll.setVisible(true);
    }
    
    public void reset()
    {
        super.reset(); // Will do everything that reset does in PracticeView
        remove(topStaticScroll);
        remove(bottomStaticScroll);
    }
}

