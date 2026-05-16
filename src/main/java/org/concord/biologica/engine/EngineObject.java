//
// Class : EngineObject
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2002/03/11 16:54:46 $
// $Author: ed $
//

package org.concord.biologica.engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.util.Enumeration;
import java.util.Vector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import java.lang.IllegalArgumentException;

import org.xml.sax.HandlerBase;

/**
 * This abstract class is the base for all BioScope engine classes and defines
 * the basic API's that all engine classes must implement.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> EngineProp.ID - object's id has changed
 * <li> EngineProp.DELETED - object has been deleted
 * <li> EngineProp.LOCKED_STATE - the locked state of the object has changed
 * </ul>
 *
 * @see org.concord.biologica.engine.EngineProp#ID
 * @see org.concord.biologica.engine.EngineProp#DELETED
 * @see org.concord.biologica.engine.EngineProp#LOCKED_STATE
 *
 * @version		$Revision: 1.2 $ $Date: 2002/03/11 16:54:46 $
 * @author 		$Author: ed $
**/

public abstract class EngineObject
extends HandlerBase
{
    /**
     * Null ID value.  Illegal value in normal cases.<p>
    **/
    public static final int NULL_ID = 0;

    /**
     * Unlocked locked value.  This means the object has
     * neither a manual nor an automatic lock on it.
    **/
    public static final int UNLOCKED = 0x00000000;
    public static final String UNLOCKED_STRING = "unlocked";

    /**
     * Manual locked value.  This type of lock occurs when
     * the user explicitly, manually locks an object.<p>
    **/
    public static final int MANUAL_LOCKED = 0x00000001;
    public static final String MANUAL_LOCKED_STRING = "manualLocked";

    /**
     * Automatic lock value.  This type of lock occurs when
     * BioLogica automatically locks an object due to some
     * heuristic (e.g. species are automatically locked when
     * an organism of that species is created, organisms
     * are automatically locked when they are in a family
     * with children (as either the parent or child)).
    **/
    public static final int AUTOMATIC_LOCKED = 0x00000002;
    public static final String AUTOMATIC_LOCKED_STRING = "automaticLocked";

    /**
     * Manual and automatic locked value.  A convenience.
    **/
    public static final int MANUAL_AND_AUTOMATIC_LOCKED = 0x00000003;
    public static final String MANUAL_AND_AUTOMATIC_LOCKED_STRING = "manualAndAutomaticLocked";

    /**
     * Next ID value to hand out to a new engine object.<p>
    **/
    private static int nextID = 1;

    /**
     * TRUE Boolean object.<p>
    **/
    protected static final Boolean TRUE = new Boolean(true);

    /**
     * FALSE Boolean object.<p>
    **/
    protected static final Boolean FALSE = new Boolean(false);

    /**
     * Identifier for engine object, used when saving and restoring
     * the object to and from a file.  IDs are not reused and range
     * from 1 to Integer.MAX_VALUE.  Zero and all negative integers
     * are illegal id values.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.ID.
    **/
    protected int id;

    /**
     * Indicates if this object has been deleted and is waiting for
     * garbage collection.<p>
     *
     * When this property is changed, a property change event is
     * generated for the property named EngineProp.DELETED.  It is
     * up to the derived class to fire this event.<p>
    **/
    protected boolean deleted;

    /**
     * Current locked state of this object - see above LOCKED statics.
    **/
    protected int lockedState = EngineObject.UNLOCKED;

    /**
     * XML Element context used when reading an element this object from a file.
    **/
    protected transient ElementContext      xmlElementContext = null;

    /**
     * Utility object which manages property change events and listeners.
    **/
    protected transient LocalPropertyChangeSupport changes = null;

    /**
     * Get nextID for serialization purposes, so we don't
     * increment the id value (unlike getNextID()).<p>
     *
     * @return	int nextID
    **/
    static int getNextIDForEngine()
    {
        return nextID;
    }

    /**
     * Set nextID for serialization purposes.<p>
     *
     * @param	newNextID int - new value for nextID
    **/
    static void setNextIDForEngine(int newNextID)
    {
        // We can only set the nextID if this is a brand new
        // process in which no other engine objects have been
        // created yet.  So make sure nextID still equals one.
        if (nextID == 1)
        {
            nextID = newNextID;
        }
        else
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
    }

    /**
     * Get next ID value, incrementing nextID.  The range of
     * acceptable values is 1 to Integer.MAX_VALUE.<p>
     *
     * @return		int - next ID to be used for a new engine object
    **/
    static int getNextID()
    {
        int returnID = nextID;
        
        // Increment nextID
        if (nextID == Integer.MAX_VALUE)
        {
            throw new InternalEngineException(EngineStrings.EXHAUSTED_AVAILABLE_OBJECT_IDS);
        }
        else
        {
            nextID++;
        }

        return returnID;
    }

    /**
     * Creates engine object, giving the object a proper ID.
    **/
    public EngineObject()
    {
        id = EngineObject.getNextID();
        lockedState = UNLOCKED;
        changes = new LocalPropertyChangeSupport(this);
    }

    /**
     * Returns the ID of this object.
     *
     * @return		int - id of this object
    **/
    public final int getID()
    {
        if (id == NULL_ID)
        {
            throw new InternalEngineException("object id 0");
        }

        return id;
    }

    /**
     * Set the id of the object.  This method should be
     * called with great care, as giving multiple objects
     * the same ID can be disastrous.  In general, this
     * method should only be called during serialization.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.ID.<p>
     *
     * @param	anID int - new ID for object.
    **/
    public void setID(int anID)
    {
        if (id != anID)
        {
            int oldID = id;
            id = anID;
            changes.firePropertyChange(EngineProp.ID,new Integer(oldID),new Integer(id));
        }
    }

    /**
     * Get whether or not this object is deleted.<p>
     *
     * @return 		boolean - is object deleted?
    **/
    public final boolean isDeleted()
    {
        return deleted;
    }

    /**
     * Set or unset the automatic locked state of this object, leaving
     * other components of the locked state untouched.
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * It's expected that most derived classes will override this method
     * and recursively lock their children.<p>
     *
     * @param		automaticLocked boolean - object should be automatic locked (true) or not (false)
    **/
    public void setAutomaticLocked(boolean automaticLocked)
    {
        if (((automaticLocked == true) && ((lockedState & AUTOMATIC_LOCKED) == AUTOMATIC_LOCKED)) ||
            ((automaticLocked == false) && ((lockedState & AUTOMATIC_LOCKED) == UNLOCKED)))
        {
            // Automatic locked state wouldn't change
            return;
        }

        int oldLockedState = lockedState;

        if (automaticLocked == true)
        {
            // Turn automatic locked bit on
            lockedState |= AUTOMATIC_LOCKED;
        }
        else
        {
            // Turn automatic locked bit off
            lockedState ^= AUTOMATIC_LOCKED;
        }

        changes.firePropertyChange(EngineProp.LOCKED_STATE,
                                   new Integer(oldLockedState),
                                   new Integer(lockedState));
    }

    /**
     * Set or unset the manual locked state of this object, leaving
     * other components of the locked state untouched.
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * It's expected that most derived classes will override this method
     * and recursively lock their children.<p>
     *
     * @param		manualLocked boolean - object should be manually locked (true) or not (false)
    **/
    public void setManualLocked(boolean manualLocked)
    {
        if (((manualLocked == true) && ((lockedState & MANUAL_LOCKED) == MANUAL_LOCKED)) ||
            ((manualLocked == false) && ((lockedState & MANUAL_LOCKED) == UNLOCKED)))
        {
            // Manual locked state wouldn't change
            return;
        }

        int oldLockedState = lockedState;

        if (manualLocked == true)
        {
            // Turn manual locked bit on
            lockedState |= MANUAL_LOCKED;
        }
        else
        {
            // Turn manual locked bit off
            lockedState ^= MANUAL_LOCKED;
        }

        changes.firePropertyChange(EngineProp.LOCKED_STATE,
                                   new Integer(oldLockedState),
                                   new Integer(lockedState));
    }

    /**
     * Set the locked state of the object.  This sets the whole
     * state, clearing out any existing locked state entirely.
     * In general, it's better to use one of the other set methods
     * for locked state - setManualLockedState, etc. - as they
     * operate on a specific bit of the locked state, not the
     * whole locked state int.<p>
     *
     * When this property is changed, a property change event
     * is fired for the property named EngineProp.LOCKED_STATE.<p>
     *
     * It's expected that most derived classes will override this method
     * and recursively lock their children.<p>
     *
     * @param		aLockedState int - new locked state of this object
    * @exception	IllegalArgumentException - new locked state invalid
    **/
    public void setLockedState(int aLockedState)
    {
        if (aLockedState < UNLOCKED ||
            aLockedState > AUTOMATIC_LOCKED)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        if (lockedState != aLockedState)
        {
            int oldLockedState = lockedState;
            lockedState = aLockedState;
            changes.firePropertyChange(EngineProp.LOCKED_STATE,
                                       new Integer(oldLockedState),
                                       new Integer(lockedState));
        }
    }

    /**
     * Set locked state of this object using a string.<p>
     *
     * @param  aLockedState String - locked state as a string
     * @exception	IllegalArgumentException - new locked state invalid
    **/
    public final void setLockedStateAsString(String aLockedState)
    {
        if (aLockedState == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        int oldLockedState = lockedState;

        if (aLockedState.equals(UNLOCKED_STRING))
        {
            lockedState = UNLOCKED;
        }
        else if (aLockedState.equals(MANUAL_LOCKED_STRING))
        {
            lockedState = MANUAL_LOCKED;
        }
        else if (aLockedState.equals(AUTOMATIC_LOCKED_STRING))
        {
            lockedState = AUTOMATIC_LOCKED;
        }
        else if (aLockedState.equals(MANUAL_AND_AUTOMATIC_LOCKED_STRING))
        {
            lockedState = MANUAL_AND_AUTOMATIC_LOCKED;
        }

        if (oldLockedState != lockedState)
        {
            changes.firePropertyChange(EngineProp.LOCKED_STATE,
                                       new Integer(oldLockedState),
                                       new Integer(lockedState));
        }
    }

    /**
     * Get locked state as a boolean.  Will return true if the
     * object is locked for any reason, false if it is not locked
     * for any reason.<p>
     *
     * @return boolean - object is locked (true) or not locked (false)
    **/
    public final boolean isLocked()
    {
        if (lockedState == UNLOCKED)
        {
            return false;
        }

        return true;
    }

    /**
     * Is the object automatic locked?  Ignores other components of the locked state.
     *
     * @return boolean - object is automatic locked (true) or not locked (false)
    **/
    public final boolean isAutomaticLocked()
    {
        if ((lockedState & AUTOMATIC_LOCKED) == AUTOMATIC_LOCKED)
        {
            return true;
        }

        return false;
    }
    
    /**
     * Is the object manual locked?  Ignores other components of the locked state.
     *
     * @return boolean - object is manual locked (true) or not locked (false)
    **/
    public final boolean isManualLocked()
    {
        if ((lockedState & MANUAL_LOCKED) == MANUAL_LOCKED)
        {
            return true;
        }

        return false;
    }

    /**
     * Get locked state of this object.<p>
     *
     * @return	int - locked state of this object
    **/
    public final int getLockedState()
    {
        return lockedState;
    }

    /**
     * Get locked state of this object as a string.<p>
     *
     * @return  String - locked state as a string
    **/
    public final String getLockedStateAsString()
    {
        if (lockedState == UNLOCKED)
        {
            return UNLOCKED_STRING;
        }
        else if (lockedState == MANUAL_LOCKED)
        {
            return MANUAL_LOCKED_STRING;
        }
        else if (lockedState == AUTOMATIC_LOCKED)
        {
            return AUTOMATIC_LOCKED_STRING;
        }
        else if (lockedState == MANUAL_AND_AUTOMATIC_LOCKED)
        {
            return MANUAL_AND_AUTOMATIC_LOCKED_STRING;
        }

        // Default to unlocked
        return UNLOCKED_STRING;
    }

    /**
     * Notify listeners that the selected state of this object has changed.
     * The object DOES NOT maintain its selected state!  This method is just
     * provided as a convenience function to notify listeners.  This was
     * initially created for notifying nodes in the TreeView that the selection
     * state of a node has changed, as there was no other reasonable way to do it.<p>
     *
     * @param		oldSelectedState boolean - old selected state
     * @param		newSelectedState boolean - new selected state
    **/
    public final void notifySelected(boolean oldSelectedState, boolean newSelectedState)
    {
        changes.firePropertyChange(EngineProp.SELECTED,
                                   new Boolean(oldSelectedState),
                                   new Boolean(newSelectedState));
    }

    /**
     * Get the world containing this object.<p>
     *
     * @return	World - the world containing this object, never null.
    **/
    abstract public World getWorld();

    /**
     * Deletes the object, notifying any containing objects and
     * deleting any child objects.<p>
     *
     * A default method could be supplied, but every derived class
     * must implement this method, so we chose not to implement one
     * to force the compiler to complain if the derived class doesn't
     * implement this method.<p>
     *
     * A derived class's implementation of this method should at
     * least have:
     * <pre>
     *		id = NULL_ID;
     *		deleted = true;
     * </pre>
     *
     * When this method is called, the implementing class
     * must fire a property change event for the property
     * named EngineProp.DELETED.<p>
    **/
    abstract public void delete();
    
    // Cognate foe delete
    public void release()
    {
        delete();
    }

    /**
     * Return a string representation of this object, usually
     * the object's name.<p>
     *
     * @return	String - string representation of object
    **/
    abstract public String toString();

    /**
     * Writes this object to the given stream in XML format.  Usually
     * this method is used to write the object to a file for saving it,
     * but that is potentially not the only use.<p>
     *
     * We could supply a default implementation that knows how to
     * write the id value.  But that convenience is not chosen
     * because it's never correct to use just this implementation
     * of these methods, so I'd rather have a compilation fail if a
     * derived class fails to implement this method.<p>
     *
     * The inverse of this method is a constructor that takes a
     * org.xml.sax.Parser argument and other arguments.<p>
     *
     * @param       stream PrintWriter - print stream
     * @exception	IllegalArgumentException - input arguments illegal
     * @exception	IOException - an IO error occurred in java libraries
    **/
    abstract public void writeToStream(PrintWriter stream)
    throws java.io.IOException;

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

class LocalPropertyChangeSupport
{
    Object source;
    Vector listeners = new Vector();
    
    public LocalPropertyChangeSupport(Object object)
    {
        source = object;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        listeners.addElement(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        listeners.removeElement(listener);
    }
    
    public void firePropertyChange(String property, Object oldValue, Object newValue)
    {
        PropertyChangeEvent event = new PropertyChangeEvent(source, property, oldValue, newValue);
        Enumeration eListeners = listeners.elements();
        while (eListeners.hasMoreElements())
        {
            PropertyChangeListener target = (PropertyChangeListener) eListeners.nextElement();
            target.propertyChange(event);
        }
    }
}

