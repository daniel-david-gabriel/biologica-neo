//
// Class : ObjectDeletedException
//
// Copyright © 1997, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:13 $
// $Author: ed $
//

package org.concord.biologica.engine;

import java.lang.RuntimeException;

/**
 * Exception indicating that the object has been "deleted" and is
 * awaiting garbage collection.  In this state, the object should
 * have no outstanding references to it, as that would block garbage
 * collection.  Similarly, no calls into the object should be made,
 * except perhaps delete().<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public final class ObjectDeletedException
extends RuntimeException
{
	/**
	 * Creates a new exception.
	 *
	 * @param		errorText String - additional error text explaining exception
	**/
	public ObjectDeletedException(String errorText)
	{
		super(errorText);
	}
}

