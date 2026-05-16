/**
 * Class: PedigreePracticeView - for practicing and experimenting with
 * cross-breeding of random or user-defined organisms
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
import org.concord.biologica.ui.PedigreeView;
import org.concord.biologica.ui.PracticeView;
import org.concord.biologica.ui.StaticOrganismView;
import org.concord.biologica.ui.Tool;
import org.concord.biologica.ui.UIProp;


public class PedigreePracticeView
extends PracticeView
{
    // Instance variables
    ChromosomeView organismChromoView;
    PedigreeView pedigreeView;
    StaticOrganismView organismStaticView;

    Organism selectedOrganism;
    
    JButton button;
    JScrollPane organismChromoScroll;
    JScrollPane organismStaticScroll;
    
    Object selected;
    PropertyChangeListener pedigreeSelectedOrganism;
    
    int selectedTool = Tool.NO_TOOL;
    
    // Constructor
    public PedigreePracticeView(JFrame frame) // This "frame" is a parameter
    {
        super(frame); // Calls whatever is in the constructor of PracticeView (this "frame" is passed from PracticeView)
        
        organismChromoView = new ChromosomeView();
        organismChromoView.setSelectionSet(selectSet);
        
        organismChromoScroll = new JScrollPane(organismChromoView);
        organismChromoScroll.setSize(552, 153);
        organismChromoScroll.setLocation(241, 0);
        organismChromoScroll.setVisible(false);
        
        organismStaticView = new StaticOrganismView();
        organismStaticView.setSize(240, 370);
        organismStaticView.setNameTextVisible(false);
        organismStaticView.setSpeciesTextVisible(true);
        organismStaticView.setCharacteristicsTextVisible(true);
        organismStaticView.setSexTextVisible(true);
        
        organismStaticScroll = new JScrollPane(organismStaticView);
        organismStaticScroll.setSize(240, 153);
        organismStaticScroll.setLocation(1, 0);
        organismStaticScroll.setVisible(false);
        
        pedigreeView = new PedigreeView();
        pedigreeView.setSelectionSet(selectSet);
        pedigreeView.setSize(792, 493);
        pedigreeView.setLocation(1, 0);
        pedigreeView.setTraitPulldownEnabled(true);
        pedigreeView.setSpeciesTextVisible(false);
        pedigreeView.setCharacteristicsTextVisible(false);
        pedigreeView.setSexTextVisible(false);
        
        pedigreeView.setSelectionToolVisible(true);
        pedigreeView.setCrossToolVisible(true);
        pedigreeView.setSnipToolVisible(true);
        pedigreeView.setChromosomeToolVisible(true);
        pedigreeView.setVisible(true);
        
        button = pedigreeView.getUtilityButton();
        button.setText("Full Pedigree View");
        button.setSize(120, 24);
        button.setVisible(false);
        
        setLayout(null); // Of the panel associated with this practice view
        add(organismChromoScroll);
        add(organismStaticScroll);
        add(pedigreeView);
        
        PropertyChangeListener pedigreeSelectedOrganism = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                String eventString;
    
                eventString = event.getPropertyName().toString();
                
                if (eventString.equals(UIProp.SELECTED_OBJECTS))
                {
                    if (selectSet.getNumberOfSelectedObjects() == 1)
                    {
                        // Beep
                        Toolkit.getDefaultToolkit().beep();
                        
                        selected = selectSet.getSelectedObjectAtIndex(0);
                        if (selected instanceof Organism)
                        {
                            selectedOrganism = (Organism) selected;
                            
                            organismStaticView.setOrganism(selectedOrganism);
                            organismChromoView.setOrganism(selectedOrganism);
                            
                            pedigreeView.setSize(792, 340);
                            pedigreeView.setLocation(1, 154);

                            organismStaticScroll.setVisible(true);
                            organismChromoScroll.setVisible(true);
                            button.setVisible(true);
                            
                            repaint();
                        }
                    }
                }
                else if (eventString.equals(UIProp.ACTIVE_TOOL))
                {
                    selectedTool = ((Integer) event.getNewValue()).intValue();
                    if (selectedTool == Tool.SNIP)
                    {
                        System.out.println("Locking parents");

                        // Lock parent organisms
                        femaleOrganism.setManualLocked(true);
                        maleOrganism.setManualLocked(true);
                    }
                    else
                    {
                        // Unlock parent organisms
                        femaleOrganism.setManualLocked(false);
                        maleOrganism.setManualLocked(false);
                    }
                }
            }
        };
        selectSet.addPropertyChangeListener(pedigreeSelectedOrganism);
        pedigreeView.addPropertyChangeListener(pedigreeSelectedOrganism);
        
        // Anonymous class for ActionListener for return to full size pedigree view
        ActionListener buttonListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                organismStaticScroll.setVisible(false);
                organismChromoScroll.setVisible(false);
                pedigreeView.setSize(792, 493);
                pedigreeView.setLocation(1, 0);
                button.setVisible(false);
                
                repaint();
            }
        };
        button.addActionListener(buttonListener);
    }
    
    public void initialize(File worldFile)
    {
        super.initialize(worldFile);
        
        pedigreeView.addOrganism(femaleOrganism, 100, 25);
        pedigreeView.addOrganism(maleOrganism, 180, 25);

        validate();
        repaint();	
    }
        
    public void reset()
    {
        super.reset(); // Will do everything that reset does in PracticeView
        organismStaticScroll.setVisible(false);
        organismChromoScroll.setVisible(false);
        pedigreeView.setSize(792, 493);
        pedigreeView.setLocation(1, 0);
        selectSet.deselectAllObjects();

        repaint();
    }
}
