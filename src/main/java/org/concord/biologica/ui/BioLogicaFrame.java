/**
 * Class: BioLogicaFrame - main window for BioLogica scripts including a menubar, menu,
 * and menu items (that include menu and items for "practice" sessions
 *	
 * Copyright � 2000, The Concord Consortium
 *
 * Original Authors: Rose Len and Ed Burke
 * Date: 4/12/00
 * 
 * $Revision: 1.5 $Date::
 * $Author:
 *
**/

package org.concord.biologica.ui;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;

import java.util.ResourceBundle;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;

import org.concord.biologica.engine.BioLogicaSpecies;
import org.concord.biologica.engine.PathStrings;

import org.concord.biologica.ui.ChromosomeView;
import org.concord.biologica.ui.DNAView;
import org.concord.biologica.ui.PedigreeView;
import org.concord.biologica.ui.SelectionSet;
import org.concord.biologica.ui.SexView;
import org.concord.biologica.ui.StaticOrganismView;
import org.concord.pedagogica.ui.ActivityFrame;

public class BioLogicaFrame
extends ActivityFrame
{
    static ResourceBundle resbundle = null;
    static{
        try{
            resbundle = ResourceBundle.getBundle (BioLogicaFrame.class.getName(), new Locale(System.getProperty("org.concord.pedagogica.localization")));
        }catch(Throwable t){
            resbundle = null;
        }
        //System.out.println("resbundle "+resbundle.getLocale());
        if(resbundle == null) resbundle = ResourceBundle.getBundle (BioLogicaFrame.class.getName(), Locale.getDefault());
    }
    private final static String HELP_GENO_PHENO = resbundle.getString("HELP_GENO_PHENO");
    private final static String HELP_MEIOSIS = resbundle.getString("HELP_MEIOSIS");
    private final static String HELP_PEDIGREE = resbundle.getString("HELP_PEDIGREE");
    private final static String HELP_MUTATIONS = resbundle.getString("HELP_MUTATIONS");
    
    
    JEditorPane glossaryHTML;
    JEditorPane helpHTML;
    JEditorPane introHTML;
    JFrame helpFrame;
    
    JMenuBar menuBar = new JMenuBar();
    JMenuBar practiceMenuBar = new JMenuBar();
    JMenu practiceMenu = new JMenu(resbundle.getString("MENU_PRACTICE_SESSION"));
    JMenu subMenuSpecies;
    JMenuItem [] speciesSubMenuItems;
    File [] speciesFiles;
    int speciesNumber;
    BioLogicaSpecies speciesList;    
    JMenuItem menuItemGlossary = new JMenuItem(resbundle.getString("MENU_ITEM_GLOSSARY"));
    JMenuItem practiceMenuItemHelp = new JMenuItem(resbundle.getString("MENU_ITEM_PRACTICE_HELP"));
    JMenuItem helpMenuItemClose = new JMenuItem(resbundle.getString("HELP_MENU_ITEM_CLOSE"), KeyEvent.VK_C);
    JPanel helpPane;
    JScrollPane helpScroll;
    JScrollPane introScroll;
    Window window;
    String titleSave;
    
    int mouseX = 0;
    int mouseY = 0;
    
    // Protected means both package and inheritance access
    protected PracticeView practiceView;
    protected File defaultWorldFile;
    protected ChromosomePracticeView genoPhenoPracticeView;
    protected MeiosisPracticeView meiosisPracticeView;
    protected PedigreePracticeView pedigreePracticeView;
    protected MutationsPracticeView mutationsPracticeView;
    
    SelectionSet selectSet;
    
    public BioLogicaFrame()
    {
        this(null, false);
    }
        
    public BioLogicaFrame(String speciesListFileName)
    {
        this(speciesListFileName, true);
    }

    public BioLogicaFrame(String speciesListFileName, boolean handleClosing)
    {
        super(resbundle.getString("TITLE_BIOLOGICA_FRAME")); // automatically references the class JFrame (from "extends JFrame")
        
        selectSet = new SelectionSet();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu(resbundle.getString("FILE_MENU_ITEM"));
        menuBar.add(fileMenu);
        menuBar.add(practiceMenu);
        JMenuItem menuItemClose = new JMenuItem(resbundle.getString("FILE_MENU_ITEM_CLOSE"), KeyEvent.VK_C);
        fileMenu.add(menuItemClose);
        
        
        // Set up species selection submenu
        if (speciesListFileName == null)
            speciesListFileName = PathStrings.getSpeciesDirectory() + "species.xml";
        speciesList = new BioLogicaSpecies(speciesListFileName);
        speciesNumber = speciesList.getSpeciesNumber();
        speciesFiles = new File[speciesNumber];
        ButtonGroup buttonGroup = new ButtonGroup();
        if (speciesNumber > 1)
        {
            speciesSubMenuItems = new JMenuItem[speciesNumber];
            subMenuSpecies = new JMenu(resbundle.getString("FILE_MENU_ITEM_SET_SPECIES"));
            for (int i = 0; i < speciesNumber; i++)
            {
                speciesSubMenuItems[i] = new JRadioButtonMenuItem(speciesList.getSpeciesName(i));
                subMenuSpecies.add(speciesSubMenuItems[i]);
                buttonGroup.add(speciesSubMenuItems[i]);
                speciesFiles[i] = new File(PathStrings.getWorldsDirectory() + speciesList.getSpeciesFile(i));
            }
            speciesSubMenuItems[0].setSelected(true);
            practiceMenu.add(subMenuSpecies);
            practiceMenu.addSeparator();
        }
        
        JMenuItem menuItemGenPhen = new JMenuItem(resbundle.getString("GENO_PHENO_SESSION"));
        practiceMenu.add(menuItemGenPhen);
        
        JMenuItem menuItemMeio = new JMenuItem(resbundle.getString("MENU_ITEM_MEIOSIS_PRACTICE_SESSION"));
        practiceMenu.add(menuItemMeio);
        JMenuItem menuItemPedigree = new JMenuItem(resbundle.getString("MENU_ITEM_PEDIGREE_PRACTICE_SESSION"));
        practiceMenu.add(menuItemPedigree);
        JMenuItem menuItemMutate = new JMenuItem(resbundle.getString("MENU_ITEM_MUTATE_PRACTICE_SESSION"));
        practiceMenu.add(menuItemMutate);
        practiceMenu.addSeparator();
        practiceMenu.add(menuItemGlossary);
        
        JMenu practiceFileMenu = new JMenu(resbundle.getString("MENU_PRACTICE_FILE"));
        JMenuItem practiceMenuItemQuit = new JMenuItem(resbundle.getString("MENU_ITEM_QUIT_PRACTICE_SESSION"));
        JMenu practiceHelpMenu = new JMenu(resbundle.getString("MENU_PRACTICE_HELP"));
        
        createHelpWindow();
        
        introHTML = new JEditorPane();
        introHTML.setEditable(false);
        try
        {
            //introHTML.setPage("file:///" + PathStrings.getHTMLDirectory() + "introPractice.html");
            introHTML.setPage(PathStrings.getHTMLURL("introPractice.html"));
            introScroll = new JScrollPane(introHTML);
            getContentPane().add(introScroll, "Center");
        }
        catch (Exception e)
        {
        }
        
        practiceMenuBar.add(practiceFileMenu);
        practiceMenuBar.add(practiceHelpMenu);
        practiceFileMenu.add(practiceMenuItemQuit);
        practiceHelpMenu.add(practiceMenuItemHelp);
        
        // Set the frame size and make visible
        setBackground(Color.lightGray);
        //setSize(800,540);
        //setResizable(false);
        setSize(1200,800);
        setResizable(true);

        defaultWorldFile = new File(PathStrings.getWorldsDirectory() + speciesList.getSpeciesFile(0));;
        
        genoPhenoPracticeView = new ChromosomePracticeView(this);  // "this" is the current instance frame (whatever it is)
        genoPhenoPracticeView.setSize(792, 493);
        genoPhenoPracticeView.setLocation(0, 0);
        genoPhenoPracticeView.setVisible(false);

        meiosisPracticeView = new MeiosisPracticeView(this);
        meiosisPracticeView.setSize(792, 493);
        meiosisPracticeView.setLocation(0, 0);
        meiosisPracticeView.setVisible(false);
        
        pedigreePracticeView = new PedigreePracticeView(this);
        pedigreePracticeView.setSize(792, 493);
        pedigreePracticeView.setLocation(0, 0);
        pedigreePracticeView.setVisible(false);
        
        mutationsPracticeView = new MutationsPracticeView(this);
        mutationsPracticeView.setSize(792, 493);
        mutationsPracticeView.setLocation(0, 0);
        mutationsPracticeView.setVisible(false);

        // Enable the window to be closed
        if (handleClosing)
        {
            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    System.exit(0);
                }
            });
        }

        // Anonymous class for ActionListener for closing window
        ActionListener closeListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                BioLogicaFrame.this.dispose();
                System.exit(0);
            }
        };
        menuItemClose.addActionListener(closeListener);
        

        // Anonymous class for ActionListener for quitting practice session
        ActionListener quitPracticeListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setTitle(titleSave);
                practiceView.setVisible(false);
                practiceView.reset();
                setJMenuBar(menuBar);

                repaint();
            }
        };
        practiceMenuItemQuit.addActionListener(quitPracticeListener);	
        
        // Anonymous class for ActionListener for closing practice help window
        ActionListener helpCloseListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                helpFrame.dispose();
            }
        };
        helpMenuItemClose.addActionListener(helpCloseListener);
        

        // Anonymous class for ActionListener for selecting practice species
        ActionListener speciesListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JMenuItem item = (JMenuItem) e.getSource();
                for (int i = 0; i < speciesSubMenuItems.length; i++)
                {
                    if (item == speciesSubMenuItems[i])
                    {
                        //subMenuSpecies.setText("Species: " + speciesSubMenuItems[i].getText());
                        defaultWorldFile = speciesFiles[i];
                        break;
                    }
                }
            }
        };
        if (speciesSubMenuItems != null)
        {
            for (int i = 0; i < speciesSubMenuItems.length; i++)
            {
                speciesSubMenuItems[i].addActionListener(speciesListener);
            }
        }
        
        // Anonymous class for ActionListener for Geno-Pheno Praction Session
        ActionListener genPhenPracticeListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Make the geno-pheno practive view visible
                // Set practice session menu bar
                titleSave = getTitle();
                setTitle(resbundle.getString("TITLE_GENO_PHENO_PRACTICE_SESSION"));
                genoPhenoPracticeView.initialize(defaultWorldFile);
                genoPhenoPracticeView.setVisible(true);
                practiceView = genoPhenoPracticeView;
                setJMenuBar(practiceMenuBar);
                practiceMenuItemHelp.setText(HELP_GENO_PHENO);
            }
        };
        menuItemGenPhen.addActionListener(genPhenPracticeListener);
        
        
        // Anonymous class for ActionListener for Practice Help
        ActionListener practiceHelpListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                String prefix = "file:///";
                String htmlDir = PathStrings.getHTMLDirectory();
                JMenuItem item = (JMenuItem) event.getSource();
                
                // If we already have a URL string, don't use the "file:///" prefix
                if (htmlDir.indexOf("://") > -1)
                    prefix = "";
                
                try
                {
                    if (HELP_GENO_PHENO.equals(item.getText()))
                    {
                        //helpHTML.setPage(prefix + htmlDir + "gpPracticeHelp.html");	
                        helpHTML.setPage(PathStrings.getHTMLURL("gpPracticeHelp.html"));
                    }
                    else if (HELP_MEIOSIS.equals(item.getText()))
                    {
                        //helpHTML.setPage(prefix + htmlDir + "meioPracticeHelp.html");
                        helpHTML.setPage(PathStrings.getHTMLURL("meioPracticeHelp.html"));
                    }
                    else if (HELP_PEDIGREE.equals(item.getText()))
                    {
                        //helpHTML.setPage(prefix + htmlDir + "pedigreePracticeHelp.html");
                        helpHTML.setPage(PathStrings.getHTMLURL("pedigreePracticeHelp.html"));
                    }
                    else if (HELP_MUTATIONS.equals(item.getText()))
                    {
                        //helpHTML.setPage(prefix + htmlDir + "mutaPracticeHelp.html");
                        helpHTML.setPage(PathStrings.getHTMLURL("mutaPracticeHelp.html"));
                    }
                    
                    helpFrame.setVisible(true);
                    helpScroll.setVisible(true);
                    helpHTML.setVisible(true);
                    helpPane.validate();
                    helpFrame.repaint();
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        };
        practiceMenuItemHelp.addActionListener(practiceHelpListener);
        
        // ActionListener for glossary
        ActionListener glossaryListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                String prefix = "file:///";
                String htmlDir = PathStrings.getHTMLDirectory();
                JMenuItem item = (JMenuItem) event.getSource();
                
                if (htmlDir.indexOf("://") > -1)
                prefix = "";
                
                try
                {
                    helpHTML.setPage(PathStrings.getHTMLURL("glossary.html"));
                    //helpHTML.setPage(prefix + htmlDir + "glossary.html");
                    
                    helpFrame.setVisible(true);
                    helpScroll.setVisible(true);
                    helpHTML.setVisible(true);
                    helpPane.validate();
                    helpFrame.repaint();
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        };
        menuItemGlossary.addActionListener(glossaryListener);
        
        // Anonymous class for ActionListener for Meiosis Praction Session
        ActionListener meioPracticeListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Make the meiosis practive view visible
                // Set practice session menu bar
                titleSave = getTitle();
                setTitle(resbundle.getString("TITLE_MEIOSIS_PRACTICE_SESSION"));
                meiosisPracticeView.initialize(defaultWorldFile);
                meiosisPracticeView.setVisible(true);
                practiceView = meiosisPracticeView;
                setJMenuBar(practiceMenuBar);
                practiceMenuItemHelp.setText(HELP_MEIOSIS);
            }
        };
        menuItemMeio.addActionListener(meioPracticeListener);
        
        
        // Anonymous class for ActionListener for Pedigree Praction Session
        ActionListener pedigreePracticeListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Make the pedigree practive view visible
                // Set practice session menu bar
                titleSave = getTitle();
                setTitle(resbundle.getString("TITLE_PEDIGREE_PRACTICE_SESSION"));
                pedigreePracticeView.initialize(defaultWorldFile);
                pedigreePracticeView.setVisible(true);
                practiceView = pedigreePracticeView;
                setJMenuBar(practiceMenuBar);
                practiceMenuItemHelp.setText(HELP_PEDIGREE);
            }
        };
        menuItemPedigree.addActionListener(pedigreePracticeListener);
        
        
        // Anonymous class for ActionListener for Mutatations Praction Session
        ActionListener mutatePracticeListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // Make the mutations practive view visible
                // Set practice session menu bar
                titleSave = getTitle();
                setTitle(resbundle.getString("TITLE_MUTATIONS_PRACTICE_SESSION"));
                mutationsPracticeView.initialize(defaultWorldFile);
                mutationsPracticeView.setVisible(true);
                practiceView = mutationsPracticeView;
                setJMenuBar(practiceMenuBar);
                practiceMenuItemHelp.setText(HELP_MUTATIONS);
            }
        };
        menuItemMutate.addActionListener(mutatePracticeListener);
    }


    private void createHelpWindow()
    {
        helpFrame = new JFrame(resbundle.getString("TITLE_HELP_FRAME"));
        JMenuBar helpMenuBar = new JMenuBar();
        helpFrame.setJMenuBar(helpMenuBar);
        JMenu helpFileMenu = new JMenu(resbundle.getString("MENU_HELP_FILE"));
        helpFileMenu.setMnemonic(KeyEvent.VK_F);
        helpMenuBar.add(helpFileMenu);
        helpFileMenu.add(helpMenuItemClose);
        
        helpHTML = new JEditorPane();
        helpScroll = new JScrollPane(helpHTML);
        helpPane = (JPanel) helpFrame.getContentPane();
        helpPane.setLayout(new BorderLayout());
        
        helpFrame.setBackground(Color.lightGray);
        helpFrame.setSize(600, 425);
        helpFrame.setResizable(false);
        
        helpHTML.setContentType("text/html");
        helpHTML.setEditable(false);
        helpHTML.setBackground(Color.white);

        helpPane.add(helpScroll, "Center");
        //helpScroll.setSize(592,303);
        helpScroll.setLocation(1, 0);
        
        glossaryHTML = new JEditorPane();
        glossaryHTML.setContentType("text/html");
        glossaryHTML.setEditable(false);
        
        helpPane.add(glossaryHTML, "South");
        //glossaryHTML.setSize(592, 75);

        glossaryHTML.setBackground(new Color(200,200,200));
        
        HyperlinkListener listener = new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent event)
            {
                try
                {
                    glossaryHTML.setVisible(false);
					if(event.getEventType().equals(HyperlinkEvent.EventType.ENTERED) || 
					   event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)){
                    	glossaryHTML.setPage(event.getURL());
						glossaryHTML.setVisible(true);
                	}                    
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        };
        helpHTML.addHyperlinkListener(listener);
    }
    
    public static void main(String [] args)
    {
        String fileName = null;
        if (args.length > 0)
            fileName = args[0];
        JFrame frame = new BioLogicaFrame(fileName);
        frame.setVisible(true);
        frame.repaint();
    }
}

