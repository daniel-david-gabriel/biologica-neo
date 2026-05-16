//
// Class : NotBioLogicaFileException
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
 * Exception indicating that an attempt was made to use a file which is
 * not a BioLogica file as a BioLogica file.  For example, a file was opened
 * and expected to be a BioLogica file but was not.<p>
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:13 $
 * @author 		$Author: ed $
**/

public final class NotBioLogicaFileException extends RuntimeException
{

	/**
	 * Creates a new exception.
	 *
	 * @param		errorText String - additional error text explaining exception
	**/
	public NotBioLogicaFileException(String errorText)
	{
		super(errorText);
	}
}

