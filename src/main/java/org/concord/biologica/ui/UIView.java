//
// Class : UIView - a base user interface view for BioLogica
//
// Copyright � 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.4 $
// $Date: 2002/10/16 18:18:48 $
// $Author: qliao $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

/**
 * The basic user interface view of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.4 $ $Date: 2002/10/16 18:18:48 $
 * @author 		$Author: qliao $
**/
public class UIView
extends JComponent
{
    /**
     * Font metrics
    **/
    protected FontMetrics fontMetrics;

    /**
     * Font ascent
    **/
    protected int fontAscent;

    /**
     * Font descent
    **/
    protected int fontDescent;

    /**
     * Font height
    **/
    protected int fontHeight;

    /**
     * Optional scroll pane parent of this view.
    **/
    protected JScrollPane scrollPane = null;

    /**
     * Preferred width of this view.
    **/
    protected int preferredWidth = 100;

    /**
     * Preferred height of this view.
    **/
    protected int preferredHeight = 100;

    /**
     * Utility object which manages property change events and listeners.
    **/
    protected transient PropertyChangeSupport changes = null;

    /**
     * Creates a user interface view.
    **/
    protected UIView()
    {
        changes = new PropertyChangeSupport(this);
        scrollPane = null;
        setDoubleBuffered(true);

        // Cannot call plain setFont because subclasses haven't been initialized yet
        super.setFont(UIGraphics.getFontBold12());
        
        // Update font information
        Graphics g = this.getGraphics();
        updateFont(g);
        //g.dispose();
    }
    
    public static ImageIcon getLocalImage(String path)
    {
        return new ImageIcon(PathStrings.getResource(path));
    }

    /**
     * Tell this view its scroll pane.  Most views that care will override
     * this method and update scrollbars when this method is called.<p>
     *
     * @param		aScrollPane JScrollPane - the scroll pane containing this view, may be null
    **/
    public void setScrollPane(JScrollPane aScrollPane)
    {
        scrollPane = aScrollPane;
    }

    /**
     * Set the font for this view.  If a null font is specified,
     * the view will revert back to its default font.<p>
     *
     * @param		aFont Font - a new font, if null, then will revert to default font
    **/
    public void setFont(Font aFont)
    {
        // Return immediately if no change
        if (getFont() == aFont)
        {
            return;
        }

        // Ok - make change
        Font oldFont = getFont();
        if (aFont == null)
        {
            super.setFont(UIGraphics.getFontBold12());
        }
        else
        {
            super.setFont(aFont);
        }

        // Update font information
        Graphics g = this.getGraphics();
        updateFont(g);
       // g.dispose();

        // Force repaint
        repaint();

        // Notify listeners
        changes.firePropertyChange(UIProp.FONT,oldFont,aFont);
    }

    /**
     * Method called by ToolView when the current tool changes.<p>
     *
     * @param   aTool int - the active tool
    **/
    public void toolChanged(int aTool)
    {
        // Default implementation is to do nothing, as some views
        // don't care about the tool and/or only support one tool.
    }

    /**
     * Update the font settings
     *
     * @param		g Graphics
    **/
    protected void updateFont(Graphics g)
    {
        if (g != null)
        {
            fontMetrics = g.getFontMetrics();
            fontAscent = fontMetrics.getAscent();
            fontDescent = fontMetrics.getDescent();
        }
        else
        {
            // Guess at values
            fontMetrics = null;
            fontAscent = 8;
            fontDescent = 4;
        }
        fontHeight = fontAscent + fontDescent;
    }

    /**
     * Set the background color for this view.  If a null background color
     * is specified, the view will revert back to its default background color.<p>
     *
     * @param		aBackground Color - a new background color. If null, then will revert to default background color.
    **/
    public void setBackground(Color aBackground)
    {
        // Return immediately if no change
        if (getBackground() == aBackground)
        {
            return;
        }

        // Ok - make change
        Color oldBackground = getBackground();
        if (aBackground == null)
        {
            super.setBackground(Color.white);
        }
        else
        {
            super.setBackground(aBackground);
        }

        // Notify listeners
        changes.firePropertyChange(UIProp.BACKGROUND,oldBackground,aBackground);
    }

    /**
     * Set the foreground color for this view.  If a null foreground color
     * is specified, the view will revert back to its default foreground color.<p>
     *
     * @param		aForeground Color - a new color, if null, then will revert to default foreground color
    **/
    public void setForeground(Color aForeground)
    {
        // Return immediately if no change
        if (getForeground() == aForeground)
        {
            return;
        }

        // Ok - make change
        Color oldForeground = getForeground();
        if (aForeground == null)
        {
            super.setForeground(Color.black);
        }
        else
        {
            super.setForeground(aForeground);
        }

        // Notify listeners
        changes.firePropertyChange(UIProp.FOREGROUND,oldForeground,aForeground);
    }

    /**
     * Return the preferred size of this canvas
     *
     * @return		Dimension - preferred size of canvas
    **/
    public Dimension getPreferredSize()
    {
        return new Dimension(preferredWidth, preferredHeight);
    }

    /**
     * Paint the background of this view using the given Graphics
     * and bounds rectangle.  This method is unfortunately necessary
     * until I figure out how to get the views to draw their own
     * backgrounds using standard Swing methods.<p>
     *
     * @param		g Graphics - graphics to use, may not be null
     * @param		bounds Rectangle - rectangle to use, may not be null
    **/
    protected void paintBackground(Graphics g, Rectangle bounds)
    {
        g.setColor(getBackground());
        g.fillRect(0,0,bounds.width,bounds.height);
    }

    /**
     * Add a property change listener for properties.
     *
     * @param	aListener PropertyChangeListener - a new listener
    **/
    public void addPropertyChangeListener(PropertyChangeListener aListener)
    {
        changes.addPropertyChangeListener(aListener);
    }

    /**
     * Remove a property change listener for properties.
     *
     * @param	aListener PropertyChangeListener - a listener to remove
    **/
    public void removePropertyChangeListener(PropertyChangeListener aListener)
    {
        changes.removePropertyChangeListener(aListener);
    }
    
}

