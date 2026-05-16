/**
 * Class: GlossaryFrame - main window for BioLogica scripts including a menubar, menu,
 * and menu items (that include menu and items for "glossary" sessions
 *	
 * Copyright © 2000, The Concord Consortium
 *
 * Original Authors: Qing Liao
 * Date:3/26/02
 * 
 * 
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


public class GlossaryFrame
extends ActivityFrame
{
   
    JEditorPane glossaryHTML;
    JEditorPane helpHTML;
    JEditorPane introHTML;
    JFrame helpFrame;
    
    JMenuBar menuBar = new JMenuBar();
    JMenuBar practiceMenuBar = new JMenuBar();
    //JMenu activitiesMenu = new JMenu("Activities");
    JMenu practiceMenu = new JMenu("Glossary");
   
   
    JMenuItem menuItemGlossary = new JMenuItem("Glossary");
  
    JMenuItem helpMenuItemClose = new JMenuItem("Close", KeyEvent.VK_C);
    JPanel helpPane;
    JScrollPane helpScroll;
    JScrollPane introScroll;
    Window window;
    String titleSave;
    
    int mouseX = 0;
    int mouseY = 0;
    
    
    SelectionSet selectSet;
    
    public GlossaryFrame()
    {
  
        this(null, false);
    }
        
    public GlossaryFrame(String speciesListFileName)
    {
        this(speciesListFileName, true);
    }

    public GlossaryFrame(String speciesListFileName, boolean handleClosing)
    {
        super("BioLogica"); // automatically references the class JFrame (from "extends JFrame")
        
        selectSet = new SelectionSet();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        menuBar.add(practiceMenu);
        JMenuItem menuItemClose = new JMenuItem("Close", KeyEvent.VK_C);
        fileMenu.add(menuItemClose);
        
        
       
        
        practiceMenu.add(menuItemGlossary);
       
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
        
        
        // Set the frame size and make visible
        setBackground(Color.lightGray);
        setSize(800,540);
        setResizable(false);

        
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
                GlossaryFrame.this.dispose();
                System.exit(0);
            }
        };
        menuItemClose.addActionListener(closeListener);
        

       
        
        // Anonymous class for ActionListener for closing practice help window
        ActionListener helpCloseListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                helpFrame.dispose();
            }
        };
        helpMenuItemClose.addActionListener(helpCloseListener);
        

      
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
        
       
    }


    private void createHelpWindow()
    {
        helpFrame = new JFrame("HELP");
        JMenuBar helpMenuBar = new JMenuBar();
        helpFrame.setJMenuBar(helpMenuBar);
        JMenu helpFileMenu = new JMenu("File");
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
                    glossaryHTML.setPage(event.getURL());
                    glossaryHTML.setVisible(true);
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
        JFrame frame = new GlossaryFrame(fileName);
        frame.setVisible(true);
        frame.repaint();
    }
  
     public void showGlossary(String term)
    {
    	try{
    			this.getContentPane().add(helpHTML);
    	 		helpHTML.setPage(PathStrings.getHTMLURL("glossary.html"));
    	 		this.show();
    	}catch(Exception e)
    	{
    	}
    	 
    }
}

