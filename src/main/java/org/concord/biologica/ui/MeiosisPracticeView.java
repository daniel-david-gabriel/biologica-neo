/**
 * Class: MeiosisPracticeView - meiosis session for practicing and
 * experimenting creating offspring with random or user-defined parents
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

import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.lang.Object;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.concord.biologica.engine.Organism;
import org.concord.biologica.engine.Species;
import org.concord.biologica.engine.SpeciesImage;
import org.concord.biologica.engine.World;
import org.concord.biologica.ui.ChromosomeView;
import org.concord.biologica.ui.PracticeView;
import org.concord.biologica.ui.SexView;
import org.concord.biologica.ui.StaticOrganismView;


public class MeiosisPracticeView
extends PracticeView
{
    // Instance variables for this class
    SexView meiosisView;
    
    // Constructor
    public MeiosisPracticeView(JFrame frame)
    {
        super(frame); // Calls whatever is in the constructor of PracticeView
    
        meiosisView = new SexView();
        meiosisView.setSize(792, 493);
        meiosisView.setLocation(0, 0);
        meiosisView.setSexViewMode(meiosisView.SEX_VIEW_MODE_SIX_VIEWS);
        meiosisView.setSpeciesTextVisible(false);
        meiosisView.setCharacteristicsTextVisible(false);
        meiosisView.setSexTextVisible(false);
        meiosisView.setVisible(true);
        
        setLayout(null); // For this panel that is associated with this practice view
        add(meiosisView);
        
        repaint();
    }
                
    public void initialize(File worldFile)
    {
        super.initialize(worldFile);
    
        meiosisView.setMotherOrganism(femaleOrganism);
        meiosisView.setFatherOrganism(maleOrganism);
        
        validate();
        repaint();
    }
    
    public void reset()
    {
        super.reset(); // Will do everything that reset does in PracticeView
    }
}


