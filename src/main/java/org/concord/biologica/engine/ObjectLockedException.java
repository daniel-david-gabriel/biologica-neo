//
// Class : ObjectLockedException
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

import java.lang.RuntimeException;

/**
 * Exception indicating that the object has been "locked" and may
 * not be modified.  In this state, the object cannot be modified
 * and will raise this exception when a function to modify the
 * object is called.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public final class ObjectLockedException
extends RuntimeException
{
	/**
	 * Creates a new exception.
	 *
	 * @param		errorText String - additional error text explaining exception
	**/
	public ObjectLockedException(String errorText)
	{
		super(errorText);
	}
}

