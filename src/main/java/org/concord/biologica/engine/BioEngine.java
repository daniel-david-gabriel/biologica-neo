//
// Class : BioEngine
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:11 $
// $Author: ed $
//
package org.concord.biologica.engine;

import java.io.Serializable;
import java.lang.Object;
import java.lang.String;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * This class represents the Java Bean representing all of the
 * BioScope engine.  A user of this bean would place it in a
 * beanbox and then use the properties, events and methods of
 * this bean to manipulate the BioScope engine.<p>
 *
 * In truth, it seems difficult to really do much with the
 * BioScope engine via this Bean, but perhaps it'll have some
 * value in the future that I don't see now.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:11 $
 * @author 		$Author: ed $
**/
public class BioEngine
extends Object
implements Serializable
{
	/**
	 * The name of this object.  May be null.
	**/
	protected String name;

	/**
	 * Utility object which manages property change events and listeners.
	**/
	private transient PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Creates a bioengine, the main object for the BioScope engine.
	**/
	public BioEngine()
	{
		name = null;
	}
	
	/**
	 * Sets the name of the bioengine.
	 *
	 * @param	aName String - new name of bioengine, may be null
	**/
	public void setName(String aName)
	{
		if (aName != name)
		{
			String oldName = name;
			name = aName;
			changes.firePropertyChange(EngineProp.NAME,oldName,name);
		}
	}

	/**
	 * Returns the name of the bioengine.
	 *
	 * @return	String - name of engine, may be null
	**/
	public String getName()
	{
		return name;
	}

	/**
	 * Returns a string describing this object.
	 *
	 * @return	String - String representation of this object
	**/
	public String toString()
	{
		return name;
	}

	/**
	 * Add a property change listener for all non-vector properties.
	 *
	 * @param	aListener PropertyChangeListener - a new listener
    **/
	public void addPropertyChangeListener(PropertyChangeListener aListener)
	{
		changes.addPropertyChangeListener(aListener);
	}

	/**
	 * Remove a property change listener for all non-vector properties.
	 *
	 * @param	aListener PropertyChangeListener - a listener to remove
	**/
	public void removePropertyChangeListener(PropertyChangeListener aListener)
	{
		changes.removePropertyChangeListener(aListener);
	}
}