//
// Class : ImportContext
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:13 $
// $Author: ed $
//

package org.concord.biologica.engine;

import java.lang.IllegalArgumentException;
import java.lang.String;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class represents a context for a set of objects being imported
 * into an active BioLogica process.<p>
 *
 * Specifically, this class maintains a mapping of old object ids to
 * object references.  This diagram shows what the objects look like
 * in the File on the left side and what the objects look like after
 * they've been read into an existing BioLogica process.<p>
 * <pre>
 *			File							BioLogica Process
 *
 *		object 1						object 435
 *			no object references			no object references
 *		object 2						object 567
 *			reference to object 1			reference to object 435
 *		object 3						object 798
 *			reference to object 1			reference to object 435
 *			reference to object 2			reference to object 567
 *		object 4						object 876
 *			reference to object 3			reference to object 798
 * </pre>
 * So the mapping of old IDs to new objects would be:
 * <pre>
 *			Old ID				New Object Reference
 *
 *				1				reference to object 435
 *				2				reference to object 567
 *				3				reference to object 798
 *				4				reference to object 876
 * </pre>
 * As additional objects are read in, this mapping is added to and used.<p>
 *
 * Of course, this implies that objects are written to the file originally
 * in a parent to child order, as that's the only way to avoid cycles.<p>
 *
 * This object fires no property change events, as this object is created
 * and used and then deleted all within the process of opening and reading
 * in one file.  So no other object has a chance to add itself to this
 * object as a listener.<p>
 *
 * This class works by maintaining an array of objects that have been
 * read into the world where the index of an object into that array is
 * the id of that object in the world file.  But the id of the object
 * after it has been created in this process will normally not be the
 * same as it was in the file.  In other words, the id of the object is
 * changed as it is read into this process from the file.  This class
 * maintains the mapping of the old ids to the new objects.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public final class ImportContext
{
	/**
	 * The set of all engine objects which have been read in from a single file.<p>
	 *
	 * An object's index into this array is its id in the original world file.<p>
	 *
	 * Transient and hidden from others.<p>
	**/
	private transient EngineObject		objects[];

	/**
	 * Creates a new ImportContext.
	 *
	 * @exception	InternalEngineException - multiple Engine objects detected in process
	**/
	public ImportContext()
	{
		objects = new EngineObject[10000];
	}

	/**
	 * Get an object given its old id in the world file.<p>
	 *
	 * @param		id int - id of the object to get, must be 0 to Integer.MAX_VALUE
	 * @return		EngineObject - object with the given id, may be null
	 * @exception	IllegalArgumentException - object with id not found
	**/
	public EngineObject getObject(int id)
	{
		if (id < 0 || id > (objects.length-1) || objects[id] == null)
		{
			throw new IllegalArgumentException("input id bad");
		}

		return objects[id];
	}

	/**
	 * Add an object to the engine's array of objects, reallocating the
	 * array to a larger size if necessary.  Grow the array by 1024 for now,
	 * but perhaps it should be a larger increment in the future?<p>
	 *
	 * @param		engineObject EngineObject - object to add, may not be null
	 * @param		oldID int - the old id the object had (NOT the id it has now)
	 * @exception	IllegalArgumentException - input arguments illegal
	 * @exception	InternalEngineException - oldID was more than 1024 greater than array size
	**/
	public void addObject(EngineObject engineObject, int oldID)
	{
		if (engineObject == null)
		{
			throw new IllegalArgumentException("input engineObject null");
		}

		if (oldID <= 0)
		{
			throw new IllegalArgumentException("input engineObject's oldID bad " + oldID);
		}
		else if (oldID > (objects.length-1))
		{
			// We've hit the upperbound of the current array, so reallocate
			// the array to a larger one, copying elements of old one.
			EngineObject newObjects[] = new EngineObject[objects.length + 1024];
			int i;
			int len = objects.length;
			for (i=0;i<len;i++)
			{
				newObjects[i] = objects[i];
				objects[i] = null;
			}
			objects = newObjects;
			newObjects = null;

			if (oldID > (objects.length-1))
			{
				throw new InternalEngineException("oldID " + oldID + " more than 1024 beyond previous id range");
			}
		}

		// Finally assign element of array to new object
		objects[oldID] = engineObject;
	}
}
