package com.gabriel.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.concord.biologica.engine.PathStrings;
import org.concord.biologica.engine.World;
import org.concord.biologica.ui.EnvironmentView;


public final class EnvironmentWindow extends JPanel
{
 static private JFrame frame;

 static public void main(String args[])
 {
     // Create frame
     frame = new JFrame("BioLogica");
     frame.setBackground(Color.lightGray);
     frame.setSize(780,560);
     frame.addWindowListener(new WindowCloser());
     
     World world = new World(new File(PathStrings.getWorldsDirectory(), "dragon-foo.xml"));
     
     EnvironmentView environmentView = new EnvironmentView();
     environmentView.setSize(new Dimension(400,400));
     environmentView.setEnvironmentSize(world, 10, 10, 1);
     System.out.println(environmentView.getWidth());
     environmentView.reset();
     environmentView.start();
     
     frame.getContentPane().add(environmentView,"Center");

     //frame.pack();
     frame.setVisible(true);
     frame.repaint();
     
     environmentView.validate();
     environmentView.repaint();
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

