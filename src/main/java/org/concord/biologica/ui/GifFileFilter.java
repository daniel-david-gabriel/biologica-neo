//
// Class : GifFileFilter - used in JFileChooser to filter for GIF files
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:18 $
// $Author: ed $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import org.concord.biologica.engine.*;
import org.concord.biologica.ui.*;

public final class GifFileFilter extends javax.swing.filechooser.FileFilter
{
	final static String gif = "gif";

	// Accept all directories and (gif) files.
	public boolean accept(File f)
	{
		if(f.isDirectory())
		{
			return true;
		}

		String s = f.getName();
		int i = s.lastIndexOf('.');
		if(i > 0 &&  i < s.length() - 1)
		{
			String extension = s.substring(i+1).toLowerCase();
			if (gif.equals(extension))
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		return false;
	}

	// The description of this filter
	public String getDescription()
	{
		return "Species Image GIF Files (*.gif)";
	}
}

