
// Modified by Rose on 10/11/00


package org.concord.biologica.ui;


import javax.swing.DefaultComboBoxModel;


public class BioComboBoxModel extends DefaultComboBoxModel
{
    /**
     * Empties the list if the list has some elements.
     */
    public void removeAllElements()
    {
        if (getSize() > 0)
        {
            super.removeAllElements();
        }
    }

}
