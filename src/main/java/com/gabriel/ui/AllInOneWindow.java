package com.gabriel.ui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.concord.biologica.ui.AllInOneView;

import com.gabriel.util.BioLogicaProperties;

/**
 * 
 * The primary view. Sets Neo Mode in a static initializer, loads an
 * AllInOneView and sets it in the window. Where possible, the AllInOneView has
 * been left alone.
 * 
 * The default Main class for the jar.
 * 
 * @author dan
 *
 */

public final class AllInOneWindow extends JPanel {

	static {
		BioLogicaProperties.setNeoMode(true);
	}

	static private JFrame frame;

	static private AllInOneView allInOneView;

	static public void main(String args[]) {
		// Create frame
		frame = new JFrame("BioLogica");
		frame.setBackground(Color.lightGray);
		frame.setSize(780, 560);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Create
		allInOneView = new AllInOneView();

		allInOneView.setFileControlsVisible(true);
		allInOneView.setSpeciesPulldownVisible(true);
		allInOneView.setMemoryMenuVisible(true);

		frame.getContentPane().add(allInOneView, "Center");

		frame.pack();
		frame.setVisible(true);
		frame.repaint();
		allInOneView.validate();
		allInOneView.repaint();
	}
}
