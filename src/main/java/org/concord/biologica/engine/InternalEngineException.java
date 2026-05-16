//
// Class : InternalEngineException
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
 * Exception indicating that an internal exception occurred within
 * BioLogica's engine.  In most cases this is an exception which should never
 * have happened, is usually due to a severe bug in the code, is not
 * handled because the problem is so severe, and which will cause
 * BioScope to crash since the exception cannot be handled.
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public final class InternalEngineException extends RuntimeException
{

	/**
	 * Creates a new exception.
	 *
	 * @param		errorText String - additional error text explaining exception
	**/
	public InternalEngineException(String errorText)
	{
		super(errorText);
	}
}

