package com.gabriel.ui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.concord.biologica.ui.AllInOneView;

/**
 * 
 * @author dan
 *
 */

public final class AllInOneWindow extends JPanel
{

 static private JFrame frame;
 static private AllInOneView allInOneView;

 static public void main(String args[])
 {
     // Create frame
     frame = new JFrame("BioLogica");
     frame.setBackground(Color.lightGray);
     frame.setSize(780,560);
     frame.addWindowListener(new WindowCloser());

     // Create
     allInOneView = new AllInOneView();

     allInOneView.setFileControlsVisible(true);
     allInOneView.setSpeciesPulldownVisible(true);
     allInOneView.setMemoryMenuVisible(true);

     frame.getContentPane().add(allInOneView,"Center");

     frame.pack();
     frame.setVisible(true);
     frame.repaint();
     allInOneView.validate();
     allInOneView.repaint();
 }
 
 static public class WindowCloser
 extends WindowAdapter
 {
     public void windowClosing(WindowEvent event)
     {
         JFrame frame = (JFrame) event.getSource();
         frame.dispose();
         System.exit(0);
     }
 }
}

