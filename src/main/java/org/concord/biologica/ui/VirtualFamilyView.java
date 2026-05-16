//
//Class : VirtualFamilyView - a simple view for a virtual family.
//
//Copyright � 2004, The Concord Consortium
//
//Original Author: Shengyao Wang
//
//$Revision: 1.5 $
//$Date: 2004/05/26 18:43:49 $
//$Author: swang $
//
package org.concord.biologica.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.File;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar; 
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import javax.swing.Timer;

import org.concord.biologica.engine.*;

public class VirtualFamilyView
extends JPanel
implements ItemListener, ActionListener
{
	/**
	 * virtual family in this view
	**/
	private VirtualFamily family;
	
	/**
	 * trait to be displayed for the view
	**/
    private Trait displayedTrait;
    
    /**
     * table row height
    **/
    private int rowHeight = 20;
    
    /**
     * table column names vector
    **/
    private Vector columnNames = new Vector(0,1);
    
    /**
     * Array of result vector. Each vector stores percentage of characteristics for specific trait.
    **/
    private Vector result[];
    
    /**
     * ScrollPane for the result table.
    **/
    private JScrollPane tablePane;

    /**
     * trait pulldown label
    **/
    private JLabel traitPulldownLabel;
    
    /**
     * Trait pulldown
    **/
    private BioComboBox traitPulldown;
    
    /**
     * Trait pulldown enabled?
    **/
    private boolean traitPulldownEnabled;

    /**
     * Trait pulldown visible
    **/
    private boolean traitPulldownVisible;
    
    /**
     * Total offspring label
    **/
    private JLabel totalOffspringLabel = null;

    /**
     * Total offspring number
    **/
    private JLabel totalOffspringNumber;
    
    /**
     * Dead offspring label
    **/
    private JLabel deadOffspringLabel = null;
    
    /**
     * Dead offspring number
    **/
    private JLabel deadOffspringNumber;

    /**
     * Actual width of this view.
    **/
    private int actualWidth = -10;

    /**
     * Current species
    **/
    private Species currentSpecies;
    
    /**
     * progress bar shows the progress of creating virtual offspring
    **/
    private JProgressBar progressBar;
    
    /**
     * Timer for repainting progress bar.
    **/
    private Timer timer;

    /**
     * Interval for timer.
    **/
    private final int ONE_SECOND = 1000;

    /**
     * For traitPulldown. 
    **/
    private final String initialSelectedItem = "Select ...";
    
    /**
     * value for progressBar. This is got from VirtualFamily.
    **/
    private int progress;
    
    /**
     * Button used to cancel the time-consuming task.
    **/
    private JButton cancelButton;
    
    /**
     * A seperate thread for the time-consuming task.
    **/
    private Thread thread;
    
    /**
     * Results table
    **/
    private JTable table;
    
    /**
     * Previous selected item. When user select "Select ...", use this item instead.
    **/
    private String previousItem = "";
    
    /**
     * Current selected item.
    **/
    private String currentItem = "";

    /**
     * Create an virtual family view object. Add trait pulldown and labels.
     *
    **/
    public VirtualFamilyView()
    {
        super();
        setLayout(null);

        traitPulldownEnabled = true;
        traitPulldownVisible = true;
        
        traitPulldownLabel = new JLabel();
        traitPulldownLabel.setText("Trait:");
        traitPulldownLabel.setBounds(5, 5, 100, 24);
        traitPulldownLabel.setVisible(true);
        add(traitPulldownLabel);
        
        traitPulldown = new BioComboBox();
        traitPulldown.addItemListener(this);
        traitPulldown.setVisible(true);
        traitPulldown.setEnabled(true);
        traitPulldown.setBounds(5,30,120,24);
        add(traitPulldown);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(150, 150, 100, 30);
        cancelButton.addActionListener(this);
        cancelButton.setVisible(false);
        add(cancelButton);
        
        totalOffspringLabel = new JLabel();
        totalOffspringLabel.setBounds(150,2,120,24);
        totalOffspringLabel.setText("Total offspring:");
        totalOffspringLabel.setVisible(true);
        totalOffspringLabel.setFont(getFont());
        add(totalOffspringLabel);

        totalOffspringNumber = new JLabel();
        totalOffspringNumber.setBounds(275,2,60,24);
        totalOffspringNumber.setHorizontalAlignment(JLabel.RIGHT);
        totalOffspringNumber.setVisible(true);
        totalOffspringNumber.setFont(getFont());
        add(totalOffspringNumber);

        deadOffspringLabel = new JLabel();
        deadOffspringLabel.setBounds(150,26,120,24);
        deadOffspringLabel.setText("Dead offspring:");
        deadOffspringLabel.setVisible(true);
        deadOffspringLabel.setFont(getFont());
        add(deadOffspringLabel);
        
        deadOffspringNumber = new JLabel();
        deadOffspringNumber.setBounds(275,26,60,24);
        deadOffspringNumber.setHorizontalAlignment(JLabel.RIGHT);
        deadOffspringNumber.setVisible(true);
        deadOffspringNumber.setFont(getFont());
        add(deadOffspringNumber);
    }

    /**
     * Create a virtual family view object, based on an virtual family.<p>
     * 
     * @param aFamily VirtualFamily - VirtualFamily in this view
    **/
    public VirtualFamilyView(VirtualFamily aFamily)
    {
    		this();
    		
        setFamily(aFamily);
    }
    
    /**
     * Get family in this view.
     * 
     * @return family VirtualFamily - virtual family in this view
    **/
    public VirtualFamily getFamily()
    {
        return family;
    }
    
    /**
     * Set family for this view.
     * 
     * @param aFamily VirtualFamily
    **/
    public void setFamily(VirtualFamily aFamily)
    {
        if(aFamily == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);            
        }
        family = aFamily;

        totalOffspringNumber.setText(""+family.getNumberOfChildren());              

        if(progressBar != null) remove(progressBar);
        progressBar = new JProgressBar(0, family.getNumberOfChildren());
        progressBar.setValue(0);
        progressBar.setBounds(50,100,300, 30);
        add(progressBar);

        initTimer();
        if(!timer.isRunning()) timer.start();
        
        setTraitPulldown(traitPulldown);
        if(!traitPulldown.isEnabled()) traitPulldown.setEnabled(true);
        repaint();
    }
    
    /**
     * Get trait.
     * 
     * @return displayedTrait Trait - trait for displaying the results.
    **/
    public Trait getTrait()
    {
        return displayedTrait;
    }
    
    /**
     * Set trait for displaying results.
     * 
     * @param aTrait Trait - trait for displaying results.
    **/
    public void setTrait(Trait aTrait)
    {
        if(aTrait == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
        
        displayedTrait = aTrait;
        if(traitPulldown != null) traitPulldown.setSelectedItem(displayedTrait.getName());
    }
    
    /**
     * Set trait for displaying results.
     * 
     * @param traitStr String - trait name for the trait.
    **/
    public void setTrait(String traitStr)
    {
        Vector vector = family.getTraits();
        
        boolean exists = false;
        
        for(int i = 0; i<vector.size(); i++)
        {
            Trait theTrait =(Trait) (vector.elementAt(i));
            if(theTrait.getName().equalsIgnoreCase(traitStr))
            {
                displayedTrait = theTrait;
                if(traitPulldown != null) traitPulldown.setSelectedItem(displayedTrait.getName());
                exists = true;
                return;
            }
        }
        
        if(!exists)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }
    }
    
    /**
     * Initiate the progressbar timer.
     **/
    public void initTimer()
    {
        timer = new Timer(ONE_SECOND, new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        int progress = family.getTaskProgress();
                        if(progress >= family.getNumberOfChildren())
                        {
                            remove(progressBar);
                            cancelButton.setVisible(false);
                            remove(cancelButton);
                            repaint();
                            timer.stop();
                        }
                        progressBar.setValue(progress);
                        if(!cancelButton.isVisible()) cancelButton.setVisible(true);
                        repaint();
                    }
                });
    }

    /**
     * actionPerformed for cancelButton
    **/
    public void actionPerformed(ActionEvent evt)
    {
        if(evt.getSource().equals(cancelButton))
        {
            stopCalculation();
        }
    }
    
    /**
     * Stop calculation after "Cancel" is clicked
     *
    **/
    public void stopCalculation()
    {
        family.setCancelled(true);
        try {
            if(thread != null) thread.join();            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        traitPulldown.setEnabled(true);
        
        cancelButton.setEnabled(true);
        
        progressBar.setValue(0);
        
        family.clearAll();
    }
    
    /**
     * Get species.
     * 
     * @return species Species - species of the family
    **/
    public Species getSpecies()
    {
        if(family == null) getFamily();
        return family.getSpecies();
    }
    
    /**
     * Get fixed number of offspring.
     * 
     * @return int - number of offspring in the family.
    **/
    public int getFixedNumberChildren()
    {
        if(family == null) getFamily();
        return family.getNumberOfChildren();
    }
    
    /**
     * Set fixed number of offspring for family.
     * 
     * @param aFixedNumberChildren int - fixed number of offspring in the family.
    **/
    public void setFixedNumberChildren(int aFixedNumberChildren)
    {
        family.setNumberOfChildren(aFixedNumberChildren);
    }
    
    /**
     * Get number of female offspring in the family.
     * 
     * @return int - number of female offspring 
    **/
    public int getNumberOfFemaleOffspring()
    {
        return family.getFemaleOffspringNumber();
    }
    
    /**
     * Get number of male offspring in the family
     * 
     * @return int - number of male offspring
    **/
    public int getNumberOfMaleOffspring()
    {
        return family.getMaleOffspringNumber();
    }
    
    /**
     * get result table row height
     * 
     * @return rowHeight int - result table row height
    **/
    public int getRowHeight()
    {
        return rowHeight;
    }
    
    /**
     * Set result table row height. Default value is 20.
     * 
     * @param aRowHeight int - result table row height
    **/
    public void setRowHeight(int aRowHeight)
    {
        rowHeight = aRowHeight;
    }

    /**
     * Get result to show
    **/
    private void getResult()
    {
        // if species has sex and user wants to display result for different gender, use this part.
        if(family.getSeperateSex())
        {
            columnNames.removeAllElements();
            columnNames.addElement("Characteristic");
            columnNames.addElement("Female");
            columnNames.addElement("Male");
            columnNames.addElement("Total");
            
            if(displayedTrait == null)
            	{
            		if(traitPulldown == null) setTraitPulldown(null);
            		else setTrait((String)(traitPulldown.getSelectedItem()));
            }

            Vector femalePercent = family.getFemalePercentPerTrait(displayedTrait);
            Vector malePercent = family.getMalePercentPerTrait(displayedTrait);
            int minSize = Math.min(femalePercent.size(), malePercent.size());

            result = new Vector[4];
            result[0] = new Vector(0,1);
            result[1] = new Vector(0,1);
            result[2] = new Vector(0,1);
            result[3] = new Vector(0,1);
            
            Enumeration enu = displayedTrait.getCharacteristics();
            
            for(int i = 0; i<minSize; i++)
            {
                if(enu.hasMoreElements())
                {
                    double aDouble1 = Math.round(((Double)(femalePercent.elementAt(i))).doubleValue()*10.)/10.;
                    double aDouble2 = Math.round(((Double)(malePercent.elementAt(i))).doubleValue()*10.)/10.;
                    double aDouble3 = Math.round((aDouble1 + aDouble2)*10.)/10.;
                    result[0].addElement(((Characteristic)(enu.nextElement())).getName());
                    result[1].addElement(new String(aDouble1 + "%"));
                    result[2].addElement(new String(aDouble2 + "%"));
                    result[3].addElement(new String(aDouble3 + "%"));
                } else break;
            }

            result[0].addElement("Total");

            double femaleTotal = 0;
            double maleTotal = 0;
            for(int i = 0; i<result[1].size(); i++)
            {
                femaleTotal += ((Double)(femalePercent.elementAt(i))).doubleValue();
                maleTotal += ((Double)(malePercent.elementAt(i))).doubleValue();
            }
            femaleTotal = Math.round(femaleTotal*10)/10.;
            maleTotal = Math.round(maleTotal*10)/10.;
            double total = Math.round((femaleTotal + maleTotal)*10)/10.;
            
            result[1].addElement(new String(femaleTotal + "%"));
            result[2].addElement(new String(maleTotal + "%"));
            result[3].addElement(new String(total + "%"));
        } else {   // if no sex, use this part
            columnNames.removeAllElements();
            columnNames.addElement("Characteristic");
            columnNames.addElement("Percent");
            
            if(displayedTrait == null)
            {
        			if(traitPulldown == null)
        				setTraitPulldown(null);
        			else
        				setTrait((String)(traitPulldown.getSelectedItem()));
            }
            Vector totalPercent = family.getPercentPerTrait(displayedTrait);
            int maxSize = totalPercent.size();
            
            result = new Vector[4];
            result[0] = new Vector(0,1);
            result[1] = new Vector(0,1);
            
            Enumeration enu = displayedTrait.getCharacteristics();

            for(int i = 0; i<maxSize; i++)
            {
                if(enu.hasMoreElements())
                {
                    double aDouble1 = Math.round(((Double)(totalPercent.elementAt(i))).doubleValue()*10.)/10.;
                    result[0].addElement(((Characteristic)(enu.nextElement())).getName());
                    result[1].addElement(new String(aDouble1 + "%"));
                }
                else
                    break;                    
            }
            result[0].addElement("Total");
            double total = 0;
            for(int i = 0; i<result[1].size(); i++)
            {
                total += ((Double)(totalPercent.elementAt(i))).doubleValue();
            }
            total = Math.round(total*10)/10.;
            
            result[1].addElement(new String(total + "%"));
        }
    }
    
    /**
     * Show result
    **/
    public void showResult()
    {
        // disable traitpulldown when showing result
        traitPulldown.setEnabled(false);
        
        // start the getresult thread.
        initThread();
        thread.start();
    }
    
    /**
     * Initiate thread to get result and show result 
    **/
    public void initThread()
    {
        thread = new Thread(new Runnable()
                {
                    public void run() 
                    {
                        getResult();
                        if(!family.getCancelled())
                        {
                            totalOffspringNumber.setText("" + family.getNumberOfChildren());               
                            deadOffspringNumber.setText("" +  family.getNumberOfDeadChildren());
                            createResultTable();                 
                        }
                    }
                });        
    }

    /**
     * Create result table.
    **/
    public void createResultTable()
    {
    		SwingUtilities.invokeLater(new Runnable(){
    			public void run()
            {
                table = new JTable(new VFTableModel());
                table.getTableHeader().setReorderingAllowed(false);
                table.setRowHeight(rowHeight);
                setColumnSize(table);
                table.getTableHeader().setResizingAllowed(false);
                /*
                if(tablePane == null){
                    
                    tablePane = new JScrollPane(table);                               
                    int aHeight = 7*rowHeight;
                    //Add the scroll pane to this panel.
                    tablePane.setBounds(50, 80, 400, aHeight);
                    add(tablePane);
               }
               //table.revalidate();
                revalidate();
               traitPulldown.setEnabled(true);
               */
                if(tablePane != null){
                    remove(tablePane);
                    try{
                        Thread.sleep(100);
                    }catch(Throwable t){}
                }
                tablePane = new JScrollPane(table);
                        
                int aHeight = 5*rowHeight;
    
                tablePane.setBounds(20, 80, 360, aHeight);
                add(tablePane);

                traitPulldown.setEnabled(true);
                revalidate();
            }
    		});
    }
    
    /**
     * Set first column size based on the length of characteristic name
     * 
     * @param table JTable - result table
    **/
    private void setColumnSize(JTable table) {
        VFTableModel model = (VFTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();
        
        column = table.getColumnModel().getColumn(0);
        comp = headerRenderer.getTableCellRendererComponent(
                             null, column.getHeaderValue(),
                             false, false, 0, 0);
        headerWidth = comp.getPreferredSize().width;

        cellWidth = 0;
        Enumeration enu = displayedTrait.getCharacteristics();

        while(enu.hasMoreElements())
        {
            comp = table.getDefaultRenderer(model.getColumnClass(0)).
            getTableCellRendererComponent(
                table, ((Characteristic)enu.nextElement()).getName(),
                false, false, 0, 0);
            cellWidth = Math.max(comp.getPreferredSize().width, cellWidth);
        }
        column.setPreferredWidth(Math.max(headerWidth, cellWidth));
    }

    /**
     * Reset the view
    **/
    public void reset()
    {
        if(tablePane != null) remove(tablePane);
        totalOffspringNumber.setText("");
        deadOffspringNumber.setText("");
        add(cancelButton);
        cancelButton.setVisible(false);
        traitPulldown.setEnabled(false);
        repaint();
    }
    
    /**
     * inner class VFTableModel - table model for result table
     * 
     * @author swang
     *
    **/
    class VFTableModel extends AbstractTableModel 
    {
        private Vector theColumnNames = columnNames; 
        public int getColumnCount() {
            return columnNames.size();
        }

        public int getRowCount() {
            return result[0].size();
        }

        public String getColumnName(int col) {
            return (String)(columnNames.elementAt(col));
        }

        public Object getValueAt(int row, int col) {
            return (Object)(result[col].elementAt(row));
        }
    }

    /**
     * Handle combo box item changed events.
     *
     * @param   event ItemEvent - change event to handle
    **/
    public void itemStateChanged(ItemEvent event)
    {
        BioComboBox comboBox = (BioComboBox) event.getSource();

        // Return immediately if combo box null
        if (comboBox == null) return;

        try
        {
            // Only combo boxes can generate this event now
            if (comboBox == traitPulldown && (event.getStateChange() == ItemEvent.SELECTED) && traitPulldown.isEnabled())
            {
                if(previousItem.equalsIgnoreCase(""))
                    previousItem = (String) traitPulldown.getSelectedItem();
                else
                    previousItem = currentItem;
                
                currentItem = (String) traitPulldown.getSelectedItem();
                
                if(!currentItem.equals(initialSelectedItem))
                {
                    setTrait((String)traitPulldown.getSelectedItem());
                    if(timer.isRunning()) cancelButton.setVisible(true);
                    family.setCancelled(false);
                    showResult();
                } else {
                    traitPulldown.setSelectedItem(previousItem);
                }
           }
        }
        catch (Exception e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    
    /**
     * Set the trait pulldown.  This method exists so the containing pedigree view
     * can tell this view the pulldown and this view can keep the items in thee
     * pulldown current as the species of this view changes.
     *
     * @param       aTraitPulldown BioComboBox - a trait pulldown, may be null
    **/
    public void setTraitPulldown(BioComboBox aTraitPulldown)
    {
        traitPulldown = aTraitPulldown;

        if(currentSpecies == null) currentSpecies = family.getSpecies();
        setTraitPulldownItem();
    }
    
    /**
     * Set traitPulldown items.<p>
    **/
    public void setTraitPulldownItem()
    {
        if(currentSpecies == null) currentSpecies = family.getSpecies();
        
        String selectedItemName = null;

        // Set the items in the trait pulldown
        if (traitPulldown != null)
        {
            // Clear pulldown of all items
            traitPulldown.removeAllItems();
            
            traitPulldown.addItem(initialSelectedItem);

            // Add new items
            if (currentSpecies != null)
            {
                Trait aTrait;
                String traitName;
                Enumeration eTraits = currentSpecies.getTraits();
                while (eTraits.hasMoreElements())
                {
                    aTrait = (Trait) eTraits.nextElement();
                    traitName = aTrait.getName();

                    // Don't put the Liveliness trait in the pulldown - Paul H. wants to hide it (10/7/99)
                    if (!traitName.equals("Liveliness"))
                    {
                        if (selectedItemName == null)
                        {
                                if(displayedTrait == null)
                                    selectedItemName = traitName;
                                else
                                    selectedItemName = displayedTrait.getName();
                        }
                        
                        if (!traitName.equals("Scales") && !traitName.equals("Plates"))
                        {
                            traitPulldown.addItem(aTrait.getName());
                        }
                        else 
                        {
                            Gene currentGene = currentSpecies.getGene(traitName);
                            if (currentGene.isVisible())
                            {
                                traitPulldown.addItem(aTrait.getName());
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Remove cancelButton from the view.
     *
    **/
    public void removeCancelButton()
    {
        remove(cancelButton);
    }

    public static void main(String[] args) 
    {
        long startTime = System.currentTimeMillis();
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Virtual Family View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        File file = new File(PathStrings.getWorldsDirectory(), "dragon.xml");
        World world = new World(file);
        Species species = world.getCurrentSpecies();
        Organism father = new Organism(world, "Duncan", species, Organism.MALE,
                        "b:h,a:T,b:t,a:W,b:w,a:L,b:l,a:f,a:B");
        Organism mother = new Organism(world, "Darlene", species, Organism.FEMALE,
                        "a:H,b:h,a:t,b:T,a:w,b:w,a:L,b:L,a:f,b:fb,a:B,b:b,a:A,b:aw");

        boolean seperateSex = true;
                   
        VirtualFamily vf1 = new VirtualFamily(mother, father, 100000, seperateSex);

        //Create and set up the content pane.
        VirtualFamilyView newContentPane = new VirtualFamilyView();
        newContentPane.setFamily(vf1);
        newContentPane.setPreferredSize(new Dimension(400,300));
        //newContentPane.setTrait("Color");
        newContentPane.setRowHeight(20);
        //newContentPane.showResult();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.getContentPane().add(newContentPane);
        //      Display the window.
        frame.pack();
        frame.setVisible(true);
        long endTime = System.currentTimeMillis();
        System.out.println("Time elapsed: "+(endTime-startTime)/1000.);
    }
}