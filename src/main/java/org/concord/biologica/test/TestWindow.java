//
// Class : TestWindow - A test window class for BioLogica that enables testing all the
// 						views without using a script.
//
// Copyright � 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2001/09/30 18:23:54 $
// $Author: ed $
//

package org.concord.biologica.test;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.File;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.ui.*;
import org.concord.biologica.engine.*;

/**
 * This class is a test window for running BioLogica without a script.
 *
 * @version		$Revision: 1.2 $ $Date: 2001/09/30 18:23:54 $
 * @author 		$Author: ed $
**/

public final class TestWindow extends JPanel
{
    /**
     * Frame containing this window
    **/
    static private JFrame frame;

    /**
     * All in One View
    **/
    static private AllInOneView allInOneView;

    /**
     * Main process method.
     *
     * @param	args String[] - command line arguments
     * @exception UnsupportedLookAndFeelException - Windows look and feel not supported
     * @exception IllegalAccessException - Windows look and feel could not be turned on
     * @exception InstantiationException - Windows look and feel could not be turned on
     * @exception ClassNotFoundException - Windows look and feel could not be turned on
    **/
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

