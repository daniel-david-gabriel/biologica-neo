//
// Class : BioComboBox - A small override of JComboBox to avoid a crash in Swing
//
// Copyright © 1998, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.1.1.1 $
// $Date: 2001/04/28 00:39:17 $
// $Author: ed $
// Modified by Rose on 10/11/00

package org.concord.biologica.ui;


import javax.swing.JComboBox;

/**
 * This class represents a combobox in BioLogica.
 *
 * @version		$Revision: 1.1.1.1 $ $Date: 2001/04/28 00:39:17 $
 * @author 		$Author: ed $
**/

public class BioComboBox extends JComboBox
{
    /**
     * Override the constructor so we can set the model
     * to one that knows how to remove all its items
     * without crashing :-(.
    **/
    public BioComboBox()
    {
        super();

        setModel(new BioComboBoxModel());
    }

    /**
     * Selects the item at index <code>anIndex</code>.
     *
     * @param anIndex an int specifying the list item to select, where 0 specifies
     *                the first item in the list
     * @beaninfo
     *   preferred: true
     *  description: The item at index is selected.
     */
    public void setSelectedIndex(int anIndex) {
        int size = dataModel.getSize();

        if ( anIndex < -1 || anIndex >= size )
            throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");

        if (anIndex == -1)
        {
            setSelectedItem(null);
        }
        else
        {
            setSelectedItem(dataModel.getElementAt(anIndex));
        }
    }
}

