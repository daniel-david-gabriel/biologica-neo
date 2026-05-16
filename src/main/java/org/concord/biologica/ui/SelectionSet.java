//
// Class : SelectionSet - A selection set, an object which manages a set of selected engine objects, notifies
//						  listeners that the selection set has changed, etc.  A script may create a selection
//						  set and then assign it to one or more views.  Those views will then reflect the
//						  state of the selection set in that the correct object(s) will be selected, etc.
//
// Copyright © 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:20 $
// $Author: ed $
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
 * A selection set, an object which manages a set of selected engine objects, notifies
 * listeners that the selection set has changed, etc.  A script may create a selection
 * set and then assign it to one or more views.  Those views will then reflect the
 * state of the selection set in that the correct object(s) will be selected, etc.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> org.concord.biologica.ui.UIProp.SELECTED_OBJECTS - the selected engine objects changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#SELECTED_OBJECTS
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:20 $
 * @author 		$Author: ed $
**/
public final class SelectionSet
implements PropertyChangeListener
{
    /**
     * Default selection set
    **/
    static private SelectionSet defaultSelectionSet = new SelectionSet();

    /**
     * Zero objects selection mode.  When this is the current selection mode,
     * no objects may be selected.<p>
    **/
    static public final int SELECTION_MODE_ZERO_OBJECTS = 0;

    /**
     * Single objects selection mode.  When this is the current selection mode,
     * a maximum of one object may be selected at any time.  So extend selection
     * and multiple selections do not work.<p>
    **/
    static public final int SELECTION_MODE_ONE_OBJECT = 1;

    /**
     * Two object selection mode.  When this is the current selection mode,
     * a maximum of two objects may be selected at any time.  Selecting a new
     * object deselects the oldest-selected object.  Extend selection and
     * multiple selection works to a maximum of 2 objects selected.<p>
    **/
    static public final int SELECTION_MODE_TWO_OBJECTS = 2;

    /**
     * Two organism, mixed gender selection mode.  When this is the current selection mode,
     * two organisms may be selected and they must be of opposite sexes.  So selecting
     * a male organism deselects the previously selected male organism.  Ditto for
     * female organisms.  Extend selection and multiple selection works to a maximum
     * of 2 opposite gender organisms selected.<p>
    **/
    static public final int SELECTION_MODE_TWO_ORGANISMS_OPPOSITE_SEXES = 3;

    /**
     * Normal, multiple object selection mode.  When this is the current selection mode,
     * single and multiple objects may be selected, extend selection works, etc.
     * This is the default selection mode.<p>
    **/
    static public final int SELECTION_MODE_MULTIPLE_OBJECTS = 4;

    /**
     * Selection mode
    **/
    private int selectionMode;

    /**
     * Vector of selected objects.  The objects on this
     * vector are derived from type EngineObject.<p>
    **/
    private Vector selectedObjects;

    /**
     * Vector of objects that present the selection set (e.g. views).
    **/
    private Vector selectionPresenters;

    /**
     * Utility object which manages property change events and listeners.
     * Needed here since we don't inherit from UIView.
    **/
    protected transient PropertyChangeSupport changes = null;

    /**
     * Get default selection set
     *
     * @return		SelectionSet - default selection set
    **/
    public static SelectionSet getDefaultSelectionSet()
    {
        return defaultSelectionSet;
    }

    /**
     * Set default selection set
     *
     * @param      selectionSet SelectionSet - new default selection set
    **/
    public static void setDefaultSelectionSet(SelectionSet selectionSet)
    {
        defaultSelectionSet = selectionSet;
    }

    /**
     * Creates a new, empty selection set.
    **/
    public SelectionSet()
    {
        changes = new PropertyChangeSupport(this);

        selectionMode = SELECTION_MODE_MULTIPLE_OBJECTS;
        selectedObjects = new Vector();
        selectionPresenters = new Vector();
    }
    
    /**
     * Select an object.<p>
     *
     * If extendSelect is false, then all other organisms will be deselected first.<p>
     *
     * If anOrganism is null and extendSelect is true, then do nothing.<p>
     *
     * If anOrganism is null and extendSelect is false, deselect everything.<p>
     *
     * If anOrganism is not null but the organism is not in this view, then
     * this method will return without doing anything.<p>
     *
     * @param		anObject EngineObject - an object to select, may be null
     * @param		aShiftDown boolean - shift key down
     * @param		aControlDown boolean - control key down
     * @exception	IllegalArgumentException - input organism not already in view
    **/
    public void selectObject(EngineObject anObject,
                             boolean aShiftDown, boolean aControlDown)
    {
        boolean selectionChanged = false;

        // For now, treat shiftDown and controlDown identically
        boolean extendSelect = false;
        if (aShiftDown || aControlDown)
        {
            extendSelect = true;
        }

        // If organism already selected and extendSelect false, return without doing anything
        if (!extendSelect && anObject != null && selectedObjects.contains(anObject) == true)
        {
            return;
        }

        // Do the selection / deselection
        if (extendSelect)
        {
            if (anObject != null)
            {
                // Extend selecting - unselect if already selected, else select
                if (selectedObjects.contains(anObject))
                {
                    anObject.removePropertyChangeListener(this);
                    selectedObjects.removeElement(anObject);
                }
                else
                {
                    anObject.addPropertyChangeListener(this);
                    selectedObjects.addElement(anObject);
                }
                selectionChanged = true;
            }
        }
        else
        {
            // Remove all objects from selected objects list
            EngineObject aSelectedObject;
            Enumeration eSelectedObjects = selectedObjects.elements();
            while(eSelectedObjects.hasMoreElements())
            {
                aSelectedObject = (EngineObject) eSelectedObjects.nextElement();
                aSelectedObject.removePropertyChangeListener(this);
            }
            selectedObjects.removeAllElements();

            // Add the newly selected object
            if (anObject != null)
            {
                anObject.addPropertyChangeListener(this);
                selectedObjects.addElement(anObject);
            }

            selectionChanged = true;
        }

        // Update presenters and notify listeners if selection changed
        if (selectionChanged)
        {
            updatePresenters();
            changes.firePropertyChange(UIProp.SELECTED_OBJECTS,null,null);
        }
    }

    /**
     * Deselect the given object.
     *
     * @param		anObject EngineObject - an object to deselect, may be null
    **/
    public void deselectObject(EngineObject anObject)
    {
        // Return immediately if anObject null or no objects selected
        if (anObject == null || selectedObjects.size() == 0)
        {
            return;
        }

        // Deselect object
        boolean selectionChanged = selectedObjects.removeElement(anObject);

        // Update presenters and notify listeners if selection changed
        if (selectionChanged)
        {
            anObject.removePropertyChangeListener(this);
            updatePresenters();
            changes.firePropertyChange(UIProp.SELECTED_OBJECTS,null,null);
        }
    }

    /**
     * Deselect all objects
    **/
    public void deselectAllObjects()
    {
        EngineObject aSelectedObject;
        Enumeration eSelectedObjects = selectedObjects.elements();
        while(eSelectedObjects.hasMoreElements())
        {
            aSelectedObject = (EngineObject) eSelectedObjects.nextElement();
            aSelectedObject.removePropertyChangeListener(this);
        }
        selectedObjects.removeAllElements();
            
        updatePresenters();
        changes.firePropertyChange(UIProp.SELECTED_OBJECTS,null,null);
    }

    /**
     * Update the selection presenters because the selection changed.  If
     * aSelectionPresenter is non-null, then that selection presenter will
     * not be updated.  Usually this is because that presenter called the
     * selection set originally and already knows it must update itself.<p>
    **/
    public void updatePresenters()
    {
        SelectionPresenter selectionPresenter;
        Enumeration eSelectionPresenters = selectionPresenters.elements();
        while (eSelectionPresenters.hasMoreElements())
        {
            selectionPresenter = (SelectionPresenter) eSelectionPresenters.nextElement();
            selectionPresenter.selectionChanged();
        }
    }

    /**
     * Get the number of selected objects in this view.
     *
     * @return		int - number of selected objects in this view (0 or greater)
    **/
    public int getNumberOfSelectedObjects()
    {
        return selectedObjects.size();
    }

    /**
     * Get the set of selected objects in this view.
     *
     * @return		Enumeration - enumeration over the set of selected objects in this view
    **/
    public Enumeration getSelectedObjects()
    {
        // If selected organisms is null, return empty enumeration
        if (selectedObjects == null)
        {
            Vector v = new Vector();
            return v.elements();
        }

        return selectedObjects.elements();
    }

    /**
     * Get the selected object at the given index in the selection set.
     * Return null if the index is bad.
     *
     * @param   index int - selected object index (zero based)
     * @return  EngineObject - selected engine object
    **/
    public EngineObject getSelectedObjectAtIndex(int index)
    {
        if (selectedObjects == null ||
            index < 0 ||
            index >= selectedObjects.size())
        {
            return null;
        }

        return (EngineObject) selectedObjects.elementAt(index);
    }
    
    /**
     * Returns whether or not this selection set contains the given object.<p>
     *
     * @param		boolean - returns true of object in selected set, else false
    **/
    public boolean contains(EngineObject anObject)
    {
        return selectedObjects.contains(anObject);
    }

    /**
     * Get the current selection mode.
     *
     * @return		int - current selection mode
    **/
    public int getSelectionMode()
    {
        return selectionMode;
    }

    /**
     * Set the current selection mode.
     *
     * @param		aSelectionMode int - a new selection mode
    **/
    public void setSelectionMode(int aSelectionMode)
    {
        // If selection mode hasn't changed, return immediately
        if (aSelectionMode == selectionMode)
        {
            return;
        }

        // Validate input arguments
        if (aSelectionMode != SELECTION_MODE_MULTIPLE_OBJECTS)
        {
            // Temporary restriction - only support multiple object selection
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Ok to change, make change
        selectionMode = aSelectionMode;
    }

    /**
     * Get the selection presenters
     *
     * @param		Enumeration of SelectionPresenter objects
    **/
    public Enumeration getSelectionPresenters()
    {
        return selectionPresenters.elements();
    }
    
    /**
     * Add a selection presenter
     *
     * @param		aSelectionPresenter SelectionPresenter - a selection presenter
    **/
    public void addSelectionPresenter(SelectionPresenter aSelectionPresenter)
    {
        if (aSelectionPresenter != null)
        {
            selectionPresenters.addElement(aSelectionPresenter);
        }
    }

    /**
     * Remove a selection presenter
     *
     * @param		aSelectionPresenter SelectionPresenter - a selection presenter
    **/
    public boolean removeSelectionPresenter(SelectionPresenter aSelectionPresenter)
    {
        if (aSelectionPresenter != null)
        {
            return selectionPresenters.removeElement(aSelectionPresenter);
        }

        return false;
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.DELETED))
        {
            EngineObject anObject = (EngineObject) event.getSource();
            deselectObject(anObject);
        }
    }

    /**
     * Add a property change listener for properties.
     *
     * @param	aListener PropertyChangeListener - a new listener
    **/
    public void addPropertyChangeListener(PropertyChangeListener aListener)
    {
        if (changes == null)
        {
            changes = new PropertyChangeSupport(this);
        }
        changes.addPropertyChangeListener(aListener);
    }

    /**
     * Remove a property change listener for properties.
     *
     * @param	aListener PropertyChangeListener - a listener to remove
    **/
    public void removePropertyChangeListener(PropertyChangeListener aListener)
    {
        if (changes == null)
        {
            changes = new PropertyChangeSupport(this);
        }
        changes.removePropertyChangeListener(aListener);
    }
}

