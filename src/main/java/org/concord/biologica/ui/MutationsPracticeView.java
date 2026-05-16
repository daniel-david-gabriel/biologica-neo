/**
 * Class: MutationsPracticeView - environment to practice and experiment with
 * mutations of DNA of a specific organism trait  
 *	
 * Copyright � 2000, The Concord Consortium
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
import java.io.File;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.concord.biologica.engine.Organism;
import org.concord.biologica.engine.OrganismAllele;
import org.concord.biologica.engine.OrganismAllelePair;
import org.concord.biologica.engine.OrganismChromosome;
import org.concord.biologica.engine.OrganismChromosomePair;
import org.concord.biologica.engine.Species;
import org.concord.biologica.engine.SpeciesImage;
import org.concord.biologica.engine.World;

import org.concord.biologica.ui.ChromosomeView;
import org.concord.biologica.ui.DNAView;
import org.concord.biologica.ui.PracticeView;
import org.concord.biologica.ui.StaticOrganismView;
import org.concord.biologica.ui.UIProp;


public class MutationsPracticeView
extends PracticeView
{
    // Instance variables
    ChromosomeView topChromoView;
    ChromosomeView bottomChromoView;
    StaticOrganismView topStaticView;
    StaticOrganismView bottomStaticView;
    
    DNAView dnaView;
    
    Enumeration enumeration;
    
    JButton backButton;
    
    JScrollPane dnaScroll;
    JScrollPane topChromoScroll;
    JScrollPane bottomChromoScroll;
    JScrollPane topStaticScroll;
    JScrollPane bottomStaticScroll;
    
    // Constructor
    public MutationsPracticeView(JFrame frame) // This "frame" is a parameter
    {
        super(frame); // Calls whatever is in the constructor of PracticeView
        
        topStaticView = new StaticOrganismView();
        topStaticView.setNameTextVisible(false);
        topStaticView.setSpeciesTextVisible(true);
        topStaticView.setCharacteristicsTextVisible(true);
        topStaticView.setSexTextVisible(true);
        topStaticView.setVisible(false);
        
        topStaticScroll = new JScrollPane(topStaticView);
        topStaticScroll.setVisible(false);
        
        topChromoView = new ChromosomeView();
        topChromoView.setSelectionSet(selectSet);
        topChromoView.setVisible(false);
        
        topChromoScroll = new JScrollPane(topChromoView);
        topChromoScroll.setVisible(false);
        
        bottomStaticView = new StaticOrganismView();
        bottomStaticView.setNameTextVisible(false);
        bottomStaticView.setSpeciesTextVisible(true);
        bottomStaticView.setCharacteristicsTextVisible(true);
        bottomStaticView.setSexTextVisible(true);
        bottomStaticView.setVisible(false);
        
        bottomStaticScroll = new JScrollPane(bottomStaticView);
        bottomStaticScroll.setVisible(false);
        
        bottomChromoView = new ChromosomeView();
        bottomChromoView.setSelectionSet(selectSet);
        bottomChromoView.setVisible(false);
        
        bottomChromoScroll = new JScrollPane(bottomChromoView);
        bottomChromoScroll.setVisible(false);
        
        dnaView = new DNAView();
        dnaView.setSize(792, 370);
        dnaView.setVisible(true);
        
        dnaScroll = new JScrollPane(dnaView);
        dnaScroll.setSize(792, 270);
        dnaScroll.setLocation(1, 220);
        dnaScroll.setVisible(false);
        
        backButton = new JButton("BACK");
        backButton.setSize(75, 24);
        backButton.setLocation(600, 190);
        backButton.setVisible(false);
        
        setLayout(null); // Applies to the panel associated with this practice view
        add(topStaticScroll);
        add(bottomStaticScroll);
        add(topChromoScroll);
        add(bottomChromoScroll);
        add(dnaScroll);
        add(backButton);
        
        PropertyChangeListener selectedAlleleListen = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
            	enumeration = selectSet.getSelectedObjects();
                if (enumeration.hasMoreElements())
                {
                    Object selectedAllele = enumeration.nextElement();
                    if (selectedAllele instanceof OrganismAllele)
                    {
                        Organism organism = ((OrganismAllele) selectedAllele).getOrganism();
                        OrganismAllelePair selectedAllelePair = findAllelePair(organism, (OrganismAllele) selectedAllele);
                        dnaView.setOrganismAllelePair(selectedAllelePair);
                        dnaScroll.setVisible(true);
                        backButton.setVisible(true);
                        
                        if (organism == femaleOrganism)
                        {
                            topStaticScroll.setSize(240, 183);
                            topStaticScroll.setLocation(1, 0);
                            topChromoScroll.setSize(552, 183);
                            topChromoScroll.setLocation(241, 0);
                            bottomStaticScroll.setVisible(false);
                            bottomChromoScroll.setVisible(false);
                        }
                        else if (organism == maleOrganism)
                        {
                            bottomStaticScroll.setSize(240, 183);
                            bottomStaticScroll.setLocation(1, 0);
                            bottomChromoScroll.setSize(552, 183);
                            bottomChromoScroll.setLocation(241, 0);
                            topStaticScroll.setVisible(false);
                            topChromoScroll.setVisible(false);
                        }
                    }
                }
            }
                        
        };
        selectSet.addPropertyChangeListener(selectedAlleleListen);
        
        // Anonymous class for ActionListener for return to full geno-pheno view
        ActionListener backButtonListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                clearMutationsViews();
                
                topStaticView.setSize(240, 370);
                topStaticScroll.setVisible(true);
                topChromoScroll.setVisible(true);
                
                if (isSexSpecies(species))
                {
                    bottomStaticView.setSize(240, 370);			
                    bottomStaticScroll.setVisible(true);
                    bottomChromoScroll.setVisible(true);
                }
            }
        };
        backButton.addActionListener(backButtonListener);
    }
    
    public OrganismAllelePair findAllelePair(Organism organism, OrganismAllele selected)
    {
        OrganismChromosome chromo = selected.getOrganismChromosome();
        Enumeration enumChromoPairs = organism.getOrganismChromosomePairs();
        while (enumChromoPairs.hasMoreElements())
        {
            OrganismChromosomePair chromoPair = (OrganismChromosomePair) enumChromoPairs.nextElement();
            OrganismChromosome firstChromo = chromoPair.getFirstOrganismChromosome();
            OrganismChromosome secondChromo = chromoPair.getSecondOrganismChromosome();
            if ((chromo == firstChromo) || (chromo == secondChromo))
            {
                Enumeration enumAllelePairs = chromoPair.getOrganismAllelePairs();
                while (enumAllelePairs.hasMoreElements())
                {
                    OrganismAllelePair allelePair = (OrganismAllelePair) enumAllelePairs.nextElement();
                    OrganismAllele firstAllele = allelePair.getFirstOrganismAllele();
                    OrganismAllele secondAllele = allelePair.getSecondOrganismAllele();
                    if ((selected == firstAllele) || (selected == secondAllele))
                    {
                        return allelePair;
                    }
                }
            }
        }
        return null;
    }
    
    public void initialize(File worldFile)
    {
        super.initialize(worldFile);
System.out.println(femaleOrganism);
        topStaticView.setOrganism(femaleOrganism);
        topChromoView.setOrganism(femaleOrganism);
        topStaticView.setSize(240, 370);
        
        if (isSexSpecies(species))
        {
            topStaticScroll.setSize(240, 246);
            topStaticScroll.setLocation(1, 0);

            topChromoScroll.setSize(552, 246);
            topChromoScroll.setLocation(241, 0);

            bottomStaticView.setOrganism(maleOrganism);
            bottomStaticView.setSize(240, 370);
            bottomStaticView.setLocation(1, 246);
            bottomStaticView.setVisible(true);

            bottomStaticScroll.setSize(241, 246);
            bottomStaticScroll.setLocation(1, 247);
            bottomStaticScroll.setVisible(true);
            
            bottomChromoView.setOrganism(maleOrganism);
            bottomChromoView.setVisible(true);
            
            bottomChromoScroll.setSize(552, 246);
            bottomChromoScroll.setLocation(241, 247);
            bottomChromoScroll.setVisible(true);
        }
        else
        {
            topStaticScroll.setSize(240, 493);
            topStaticScroll.setLocation(1, 0);
            topChromoScroll.setSize(552, 493);
            topChromoScroll.setLocation(241, 0);
        }
        topStaticView.setVisible(true);
        topStaticScroll.setVisible(true);
        topChromoView.setVisible(true);
        topChromoScroll.setVisible(true);
    }
    
    private void clearMutationsViews()
    {
        dnaScroll.setVisible(false);
        backButton.setVisible(false);
        
        topStaticScroll.setLocation(1, 0);
        topStaticScroll.setVisible(false);
        
        if (isSexSpecies(species))
        {
            topStaticScroll.setSize(240, 246);

            topChromoScroll.setSize(552, 246);
            topChromoScroll.setLocation(241, 0);
            
            bottomStaticScroll.setSize(240, 246);
            bottomStaticScroll.setLocation(1, 247);
            bottomStaticScroll.setVisible(false);
            
            bottomChromoScroll.setSize(552, 246);
            bottomChromoScroll.setLocation(241, 247);
            bottomChromoScroll.setVisible(false);
        }
        else 
        {
            topStaticScroll.setSize(240, 493);
            topChromoScroll.setSize(552, 493);	
        }
        topChromoScroll.setVisible(false);
    }
    
    public void reset()
    {
        super.reset(); // Will do everything that reset does in PracticeView
        clearMutationsViews();
    }
    
    public boolean isSexSpecies(Species species)
    {
        return (species.getDiploidType() != Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES);
    }
}
