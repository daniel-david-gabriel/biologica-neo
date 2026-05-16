//
// Class : Tool - the class for BioLogica tools
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.3 $
// $Date: 2001/08/16 21:01:30 $
// $Author: bdias $
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
 * The BioLogica Tool class.<p>
 *
 * @version		$Revision: 1.3 $ $Date: 2001/08/16 21:01:30 $
 * @author 		$Author: bdias $
**/
public final class Tool
{
    // Tool names - must be unique
    static public final String SELECTION_NAME  = "Selection";
    static public final String CROSS_NAME	   = "Cross";
    static public final String SNIP_NAME	   = "Snip";
    static public final String CHROMOSOME_NAME = "Chromosome";
    static public final String PEDIGREE_NAME   = "Pedigree";

    // Tool ids - must be unique
    static public final int NO_TOOL    = -1;
    static public final int SELECTION  = 0;
    static public final int CROSS      = 1;
    static public final int SNIP       = 2;
    static public final int CHROMOSOME = 3;
    static public final int PEDIGREE   = 4;
    static public final int NUMBER_OF_TOOLS = 5;

    // Tool commands - must be unique
    static public final String COMMAND_SELECTION  = "cmdSelectionTool";
    static public final String COMMAND_CROSS	  = "cmdCrossTool";
    static public final String COMMAND_SNIP		  = "cmdSnipTool";
    static public final String COMMAND_CHROMOSOME = "cmdChromosomeTool";
    static public final String COMMAND_PEDIGREE   = "cmdPedigreeTool";

    // Tool tips - do not need to be unique
    static public final String TIP_SELECTION  = "Selection Tool ";
    static public final String TIP_CROSS	  = "Cross Tool ";
    static public final String TIP_SNIP		  = "Snip Tool ";
    static public final String TIP_CHROMOSOME = "Chromosome Tool ";
    static public final String TIP_PEDIGREE   = "Pedigree Tool ";

    /**
     * Tool cursors
    **/
    static public final Cursor selectionToolCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    static public final Cursor crossToolCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
    static public final Cursor snipToolCursor = new Cursor(Cursor.HAND_CURSOR);
    static public final Cursor chromosomeToolCursor = new Cursor(Cursor.HAND_CURSOR);
    static public final Cursor pedigreeToolCursor = new Cursor(Cursor.HAND_CURSOR);

    /**
     * Image icons
    **/
    static public final ImageIcon selectionToolImageIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/seltool.gif");
    static public final ImageIcon crossToolImageIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/crosstool.gif");
    static public final ImageIcon snipToolImageIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/sniptool.gif");
    static public final ImageIcon chromosomeToolImageIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/chromotool.gif");
    static public final ImageIcon pedigreeToolImageIcon = UIView.getLocalImage("org/concord/biologica/locked/gifs/pedtool.gif");

    /**
     * Get the cursor for the given tool.  Returns null
     * if the tool id is not recognized.
     *
     * @param     toolID int - tool id (one of the above)
     * @return    Cursor - tool cursor
    **/
    static public Cursor getCursor(int toolID)
    {
        switch (toolID)
        {
            case SELECTION:
                return selectionToolCursor;
            case CROSS:
                return crossToolCursor;
            case SNIP:
                return snipToolCursor;
            case CHROMOSOME:
                return chromosomeToolCursor;
            case PEDIGREE:
                return pedigreeToolCursor;
            case NO_TOOL:
                return selectionToolCursor;
        }

        return null;
    }

    /**
     * Get the image icon for the given tool.  Returns null
     * if the tool id is not recognized.
     *
     * @param     toolID int - tool id (one of the above)
     * @return    ImageIcon - tool ImageIcon
    **/
    static public ImageIcon getImageIcon(int toolID)
    {
        switch (toolID)
        {
            case SELECTION:
                return selectionToolImageIcon;
            case CROSS:
                return crossToolImageIcon;
            case SNIP:
                return snipToolImageIcon;
            case CHROMOSOME:
                return chromosomeToolImageIcon;
            case PEDIGREE:
                return pedigreeToolImageIcon;
        }

        return null;
    }

    /**
     * Get the command string for the given tool.  Returns null
     * if the tool id is not recognized.
     *
     * @param     toolID int - tool id (one of the above)
     * @return    String - command string, null if not recognized
    **/
    static public String getCommandString(int toolID)
    {
        switch (toolID)
        {
            case SELECTION:
                return COMMAND_SELECTION;
            case CROSS:
                return COMMAND_CROSS;
            case SNIP:
                return COMMAND_SNIP;
            case CHROMOSOME:
                return COMMAND_CHROMOSOME;
            case PEDIGREE:
                return COMMAND_PEDIGREE;
        }

        return null;
    }

    /**
     * Get the tool tip string for the given tool.  Returns null
     * if the tool id is not recognized.
     *
     * @param     toolID int - tool id (one of the above)
     * @return    String - tool tip string, null if not recognized
    **/
    static public String getToolTipString(int toolID)
    {
        switch (toolID)
        {
            case SELECTION:
                return TIP_SELECTION;
            case CROSS:
                return TIP_CROSS;
            case SNIP:
                return TIP_SNIP;
            case CHROMOSOME:
                return TIP_CHROMOSOME;
            case PEDIGREE:
                return TIP_PEDIGREE;
        }

        return null;
    }

    /**
     * Get the enabled property change event for the given tool.
     *
     * @param     toolID int - tool id (one of the above)
     * @return    String - enabled property change event string
    **/
    static public String getEnabledPropertyChangeEvent(int toolID)
    {
        switch (toolID)
        {
            case SELECTION:
                return UIProp.SELECTION_TOOL_ENABLED;
            case CROSS:
                return UIProp.CROSS_TOOL_ENABLED;
            case SNIP:
                return UIProp.SNIP_TOOL_ENABLED;
            case CHROMOSOME:
                return UIProp.CHROMOSOME_TOOL_ENABLED;
            case PEDIGREE:
                return UIProp.PEDIGREE_TOOL_ENABLED;
        }

        return null;
    }
     
    /**
     * Get the visible property change event for the given tool.
     *
     * @param     toolID int - tool id (one of the above)
     * @return    String - visible property change event string
    **/
    static public String getVisiblePropertyChangeEvent(int toolID)
    {
        switch (toolID)
        {
            case SELECTION:
                return UIProp.SELECTION_TOOL_VISIBLE;
            case CROSS:
                return UIProp.CROSS_TOOL_VISIBLE;
            case SNIP:
                return UIProp.SNIP_TOOL_VISIBLE;
            case CHROMOSOME:
                return UIProp.CHROMOSOME_TOOL_VISIBLE;
            case PEDIGREE:
                return UIProp.PEDIGREE_TOOL_VISIBLE;
        }

        return null;
    }

    /**
     * Get whether or not this is a valid tool.
     *
     * @param     toolID int - tool id (one of the above)
     * @return    boolean - valid (true) or invalid (false) tool?
    **/
    static public boolean validTool(int toolID)
    {
        switch (toolID)
        {
            case SELECTION:
            case CROSS:
            case SNIP:
            case CHROMOSOME:
            case PEDIGREE:
                return true;
        }

        return false;
    }
}

