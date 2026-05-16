
//
// Class : BioDialogBox
//
// Copyright © 1999, The Concord Consortium
//
// Original Author:Qing Liao
//
// $Revision: 1.11 $
// $Date: 2003/09/02 16:58:25 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.border.*;
import javax.swing.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

public final class BioDialogBox extends JDialog
implements ActionListener
{
	Frame parentComponent;
	StaticOrganismView staticOrganismView;
	ChromosomeView chrView;
	JButton backButton;
	JTextArea textBox;
	JScrollPane chrScrollPane; 
	int width,height;
	public BioDialogBox(Frame owner, String title){
  			super(owner,title, true);
  			parentComponent  = owner;
  			
  			staticOrganismView = new StaticOrganismView();
  			chrView = new ChromosomeView();
  			backButton = new JButton("Back");
  				
  			chrScrollPane = new JScrollPane(chrView,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                                           
  			textBox = new JTextArea();
  			Font font = new Font("SanSerif", Font.PLAIN, 14);
			textBox.setLineWrap(true);
			textBox.setWrapStyleWord(true);
			textBox.setEditable(false);
			textBox.setFont(font);
			textBox.setVisible(false);
  			
  			
  			this.getContentPane().setLayout(null);		
    }
    public void setTextFont(Font font)
    {
    	textBox.setFont(font);
    	repaint();
    }

	public void setShowingText(String str)
	{
		/*if (textBox.isVisible())
			return;*/
		if (str != null && !str.equals(""))
		{
			textBox.setVisible(true);
			textBox.setText(str);
			repaint();
		}
	}
	
	public void setChromosomeView(Organism org)
	{
		setChromosomeView(org,false);
	}
	
	/**
	* only can view Chromosomes
	**/
	public void setChromosomeView(Organism org,boolean bln)
	{
		org.setAllelesAlterable(bln);
		int parentWidth = ((Dimension)(parentComponent.getSize())).width;
		int parentHeight = ((Dimension)(parentComponent.getSize())).height;
		this.setSize(parentWidth,parentHeight);
		int parentLocX = ((Point)(parentComponent.getLocation())).x;
		int parentLocY = ((Point)(parentComponent.getLocation())).y;
		
		this.setLocation(parentLocX,parentLocY);
		width = ((Dimension)(this.getSize())).width;
		height = ((Dimension)(this.getSize())).height;
		
		staticOrganismView.setBackground(this.getContentPane().getBackground());
		staticOrganismView.setBounds(5*width/160,height/30,width/4,height/2);
		
		
		chrScrollPane.setBounds(5*width/16,height/60,5*width/8,5*height/6);
		
		
		backButton.addActionListener(this);
		backButton.setBounds(7*width/16,21*height/24,width/8,height/20);
		
		
		textBox.setBackground(this.getContentPane().getBackground());
		textBox.setBounds(5*width/160,5*height/12,width/4,height/2);
		
		this.getContentPane().add(textBox);
		
		this.getContentPane().add(staticOrganismView);
		//this.getContentPane().add(chrView);
		this.getContentPane().add(chrScrollPane);
		this.getContentPane().add(backButton);	
	
	
		staticOrganismView.setOrganism(org);
		
		if (org.isChromosomesVisible())
		{
			chrView.setOrganism(org);
		}
		else{
			chrView.setOrganism(null);
			textBox.setVisible(true);
			textBox.setFont(new Font("SanSerif", Font.BOLD, 14));
			textBox.setText("You can't look at the chromosomes of this dragon!");
		}
		
		this.show();
		
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == backButton){
			textBox.setVisible(false);
			this.dispose();
		}
	}
	
}

