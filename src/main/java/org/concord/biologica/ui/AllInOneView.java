//
// Class : AllInOneView - A view that acts as a parent view for all the other BioLogica views,
//						  essentially creating an open-ended Genscope-like interface to all of
//						  the functionality of BioLogica in one view.
//
// Copyright � 1999, The Concord Consortium
//
// Original Author: Bob Miner
//
// $Revision: 1.2 $
// $Date: 2001/06/05 18:48:24 $
// $Author: ed $
//

package org.concord.biologica.ui;

import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.File;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import org.concord.biologica.engine.*;

/**
 * This class represents a view which contains and acts as a parent or manager of all
 * of the other views of BioLogica.  A script should use this view when they want to
 * give a user an open-ended interface to all of the functionality of BioLogica.<p>
 *
 * An object of this class will generate the following property change events:<p>
 *
 * <ul>
 * <li> UIProp.BACKGROUND - the background color of the view changed
 * <li> UIProp.FONT - the font of the view changed
 * <li> UIProp.FOREGROUND - the foreground color of the view changed
 * </ul>
 *
 * @see org.concord.biologica.ui.UIProp#BACKGROUND
 * @see org.concord.biologica.ui.UIProp#FONT
 * @see org.concord.biologica.ui.UIProp#FOREGROUND
 * @see java.beans.PropertyChangeListener
 *
 * @version		$Revision: 1.2 $ $Date: 2001/06/05 18:48:24 $
 * @author 		$Author: ed $
**/

public final class AllInOneView
extends UIView
implements ActionListener, MenuListener, ItemListener, PropertyChangeListener, ImageObserver, SelectionPresenter
{
    // Commands - must be unique, 2 characters, starting with 'C'
    static private final String cmdNewWorld             = "CA";
    static private final String cmdOpenWorld            = "CB";
    static private final String cmdCloseWorld           = "CC";
    static private final String cmdSaveWorld            = "CD";
    static private final String cmdSaveWorldAs          = "CE";
    static private final String cmdImportSpecies        = "CF";
    static private final String cmdExportSpecies        = "CG";

    static private final String cmdCut                  = "CI";

    static private final String cmdCreateFemale         = "CJ";
    static private final String cmdCreateMale           = "CK";
    static private final String cmdCreateOrganism       = "CL";
    static private final String cmdShowParentFamily     = "CM";
    static private final String cmdShowChildFamilies    = "CN";

    static private final String cmdTreePane             = "CP";
    static private final String cmdObjectPropertiesPane = "CQ";
    static private final String cmdPedigreePane         = "CR";
    static private final String cmdMultiOrganismPane    = "CS";
    static private final String cmdSexPane              = "CT";
    static private final String cmdChromosomePane       = "CU";
    static private final String cmdDNAPane              = "CV";

    static private final String cmdReportCollectMemory  = "CW";
    static private final String cmdReportFreeMemory     = "CX";
    static private final String cmdReportTotalMemory    = "CY";
    static private final String cmdCollectMemory        = "CZ";

    /**
     * Startup window mode, meaning we want ALL views visible
     * so they all get the proper look and feel.  This mode
     * is only briefly used during startup.
    **/
    static public final int WINDOW_MODE_STARTUP             = 0;

    /**
     * Welcome window mode, meaning no world is currently
     * open and we want some text on the screen telling the
     * user to choose New or Open... to create or open a world.
    **/
    static public final int WINDOW_MODE_WELCOME             = 1;

    /**
     * Open window mode, meaning the file chooser is shown
     * for choosing a world file to open.
    **/
    static public final int WINDOW_MODE_OPEN_WORLD_FILE     = 2;

    /**
     * Save As window mode, meaning the file chooser is shown
     * for choosing a world file to save to.
    **/
    static public final int WINDOW_MODE_SAVE_WORLD_FILE_AS  = 3;

    /**
     * Import Species window mode, meaning the file chooser
     * is shown for choosing a species file to import.
    **/
    static public final int WINDOW_MODE_IMPORT_SPECIES_FILE = 4;

    /**
     * Export Species window mode, meaning the file chooser
     * is shown for choosing a species file to export.
    **/
    static public final int WINDOW_MODE_EXPORT_SPECIES_FILE = 5;

    /**
     * Normal window mode, meaning we have a world open and
     * the normal views can be seen.
    **/
    static public final int WINDOW_MODE_NORMAL              = 6;

    /**
     * File filter - used in file chooser to choose an XML file
    **/
    static private javax.swing.filechooser.FileFilter fileFilter = new XMLFileFilter();

    /**
     * Starting directory for world browsing
    **/
    private File worldStartPath;

    /**
     * Current world - either loaded from a file or created new
    **/
    private World currentWorld = null;

    /**
     * Something primary selected?
    **/
    private boolean somethingSelected = false;

    /**
     * A selected species, null if one isn't selected.
    **/
    private Species selectedSpecies = null;

    /**
     * A selected species image, null if one isn't selected.
    **/
    private SpeciesImage selectedSpeciesImage = null;

    /**
     * Current window mode
    **/
    private int currentWindowMode = WINDOW_MODE_STARTUP;

    /**
     * Previous window mode
    **/
    private int previousWindowMode = WINDOW_MODE_WELCOME;

    /**
     * Updating state flag
    **/
    private boolean updatingState = false;

    /**
     * X pedigree view location
    **/
    private int xPedigreeView = 50;

    /**
     * Y pedigree view location
    **/
    private int yPedigreeView = 50;

    // Menubar components
    private JMenuBar mainMenuBar;
    private JMenu fileMenu;
    private JMenuItem miNewWorld;
    private JMenuItem miOpenWorld;
    private JMenuItem miCloseWorld;
    private JMenuItem miSaveWorld;
    private JMenuItem miSaveWorldAs;
    private JMenuItem miImportSpecies;
    private JMenuItem miExportSpecies;
    private JMenu editMenu;
    private JMenuItem miCut;
    private JMenu organismMenu;
    private JMenuItem miCreateFemale;
    private JMenuItem miCreateMale;
    private JMenuItem miCreateOrganism;
    private JMenuItem miShowParentFamily;
    private JMenuItem miShowChildFamilies;
    private JMenu memoryMenu;
    private JMenuItem miReportCollectMemory;
    private JMenuItem miReportFreeMemory;
    private JMenuItem miReportTotalMemory;
    private JMenuItem miCollectMemory;

    // Toolbar items
    private BioToolBar toolBar;
    private JButton newWorldButton;
    private JButton openWorldButton;
    private JButton saveWorldButton;
    private JButton cutButton;
    private JButton speciesTextButton;
    private BioComboBox speciesComboBox;
    private JButton femaleButton;
    private JButton maleButton;
    private JButton noSexButton;
    private JButton viewsButton;
    private JToggleButton treeViewToggleButton;
    private JToggleButton pedigreeViewToggleButton;
    private JToggleButton multipleOrganismViewToggleButton;
    private JToggleButton sexViewToggleButton;
    private JToggleButton objectPropertiesViewToggleButton;
    private JToggleButton chromosomeViewToggleButton;
    private JToggleButton dnaViewToggleButton;

    // Main panel in center of window
    private JPanel mainPanel;

    // Scroll panes in the main panel
    private JScrollPane welcomeViewScrollPane;
    private JSplitPane  treeSplitPane;
    private JScrollPane objectPropertiesViewScrollPane;
    private JScrollPane multipleOrganismScrollPane;
    private JScrollPane chromosomeScrollPane;
    private JScrollPane dnaScrollPane;

    // Views in the main panel
    private WelcomeView welcomeView;
    private TreeView treeView;
    private ObjectPropertiesView objectPropertiesView;
    private PedigreeView pedigreeView;
    private MultipleOrganismView multipleOrganismView;
    private SexView sexView;
    private ChromosomeView chromosomeView;
    private DNAView dnaView;
    private JFileChooser fileChooser;
    private ToolView toolView;
    
    // New?
    private EnvironmentView environmentView;

    // Booleans indicating which views are visible in normal window mode
    private boolean treePaneVisible;
    private boolean pedigreePaneVisible;
    private boolean multipleOrganismPaneVisible;
    private boolean objectPropertiesPaneVisible;
    private boolean sexPaneVisible;
    private boolean chromosomePaneVisible;
    private boolean dnaPaneVisible;

    private boolean fileControlsVisible;
    private boolean speciesPulldownVisible;
    private boolean memoryMenuVisible = false;

    // Layout manager
    private GridLayout gridLayout;

    // Selection set
    private SelectionSet selectionSet = null;

    /**
     * Creates BioLogica AllInOne view
    **/
    public AllInOneView()
    {
        // Number of views
        int numViews;

        // Use default selection set initially
        selectionSet = SelectionSet.getDefaultSelectionSet();

        // Create menubar
        createMenuBar();

        // Set layout to border
        setLayout(new BorderLayout());

        // Turn on double buffering
        setDoubleBuffered(true);

        // Create action toolbar along top of window
        createActionToolBar();

        // Create tree view on left and main panel on right of window.
        // Main panel will have up to 5 subviews which vertically divide it.
        mainPanel = new JPanel();
        mainPanel.setOpaque(true);
        gridLayout = new GridLayout(5,1,1,2);
        mainPanel.setLayout(gridLayout);

        treeView = new TreeView();
        treeView.setMinimumSize(new Dimension(100,100));
        treeView.setPreferredSize(new Dimension(200,100));
        treeView.addPropertyChangeListener(this);
        treeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                       treeView,
                                       mainPanel);

        // Create main panel views
        BevelBorder bevelBorder = new BevelBorder(BevelBorder.LOWERED);

        welcomeView = new WelcomeView();
        welcomeViewScrollPane = new JScrollPane(welcomeView,
                                                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        welcomeViewScrollPane.setBorder(bevelBorder);

        objectPropertiesView = new ObjectPropertiesView();
        objectPropertiesViewScrollPane = new JScrollPane(objectPropertiesView,
                                                         ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        objectPropertiesViewScrollPane.setBorder(bevelBorder);
        objectPropertiesView.setScrollPane(objectPropertiesViewScrollPane);

        pedigreeView = new PedigreeView();
        pedigreeView.setBorder(bevelBorder);
        pedigreeView.setSelectionToolVisible(false);
        pedigreeView.setCrossToolVisible(false);
        pedigreeView.setSnipToolVisible(false);
        pedigreeView.setChromosomeToolVisible(false);
        pedigreeView.addPropertyChangeListener(this);
        // pedigreeView.setOnlyLiveChildren(true);

        multipleOrganismView = new MultipleOrganismView();
        multipleOrganismScrollPane = new JScrollPane(multipleOrganismView,
                                                     ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        multipleOrganismScrollPane.setBorder(bevelBorder);
        multipleOrganismView.setScrollPane(multipleOrganismScrollPane);
        multipleOrganismView.setNameTextVisible(true);
        multipleOrganismView.setSpeciesTextVisible(true);
        multipleOrganismView.setCharacteristicsTextVisible(true);
        multipleOrganismView.addPropertyChangeListener(this);
        multipleOrganismView.setOrganismImageSize(SpeciesImage.LARGE_IMAGE_SIZE);
        multipleOrganismView.setTextIndent(20);

        Font aFont = new Font("SansSerif", Font.BOLD, 12);
        multipleOrganismView.setFont(aFont);
        multipleOrganismView.setTextLineSpacing(3);
        multipleOrganismView.setBackground(Color.white);
        multipleOrganismView.setForeground(Color.black);

        sexView = new SexView();
        sexView.addPropertyChangeListener(this);
        sexView.setBackground(Color.white);
        sexView.setBackgroundCellSubViews(Color.white);
        sexView.setForeground(Color.black);
        sexView.setForegroundCellSubViews(Color.black);
        sexView.setOrganismImageSize(SpeciesImage.LARGE_IMAGE_SIZE);
        sexView.setCharacteristicsTextVisible(false);
        sexView.setNameTextVisible(false);
        sexView.setSexTextVisible(false);
        sexView.setSpeciesTextVisible(false);

        chromosomeView = new ChromosomeView();
        chromosomeScrollPane = new JScrollPane(chromosomeView,
                                               ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chromosomeScrollPane.setBorder(bevelBorder);
        chromosomeView.setScrollPane(chromosomeScrollPane);

        dnaView = new DNAView();
        dnaScrollPane = new JScrollPane(dnaView,
                                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dnaScrollPane.setBorder(bevelBorder);

        toolView = new ToolView(ToolView.TOOL_VIEW_VERTICAL);
        toolView.addTool(Tool.SELECTION);
        toolView.addTool(Tool.CROSS);
        toolView.addTool(Tool.SNIP);
        toolView.addTool(Tool.CHROMOSOME);
        toolView.addTool(Tool.PEDIGREE);
        toolView.setBorder(bevelBorder);
        toolView.addView(pedigreeView);
        toolView.addView(multipleOrganismView);
        toolView.addView(sexView);
        
        environmentView = new EnvironmentView();
        environmentView.setAgeLimit(99);
        environmentView.setBounds(0, 0, 600, 800);

        worldStartPath = new File(PathStrings.getWorldsDirectory());
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new WorldFileFilter());
        fileChooser.setBorder(bevelBorder);
        fileChooser.setCurrentDirectory(worldStartPath);
        fileChooser.addActionListener(this);

        // Set the window views to startup window mode
        setWindowMode(WINDOW_MODE_STARTUP);

        // Set initial state
        setInitialState();

        // Add menubar, main panel and toolbar, initially hiding tree view
        add(mainMenuBar,"North");
        add(toolView,"West");
        treeSplitPane.remove(mainPanel);
        add(mainPanel,"Center");
        add(toolBar,"South");

        // Add this view as a SelectionPresenter
        selectionSet.addSelectionPresenter(this);
    }

    /**
     * Create the main menu bar for BioLogica.
    **/
    private void createMenuBar()
    {
        mainMenuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        fileMenu.addMenuListener(this);

        miNewWorld = new JMenuItem("New World");
        miNewWorld.addActionListener(this);
        miNewWorld.setActionCommand(cmdNewWorld);
        miNewWorld.setEnabled(true);
        fileMenu.add(miNewWorld);

        miOpenWorld = new JMenuItem("Open World...");
        miOpenWorld.addActionListener(this);
        miOpenWorld.setActionCommand(cmdOpenWorld);
        miOpenWorld.setEnabled(true);
        fileMenu.add(miOpenWorld);

        miCloseWorld = new JMenuItem("Close World");
        miCloseWorld.addActionListener(this);
        miCloseWorld.setActionCommand(cmdCloseWorld);
        miCloseWorld.setEnabled(false);
        fileMenu.add(miCloseWorld);

        miSaveWorld = new JMenuItem("Save World");
        miSaveWorld.addActionListener(this);
        miSaveWorld.setActionCommand(cmdSaveWorld);
        miSaveWorld.setEnabled(false);
        fileMenu.add(miSaveWorld);

        miSaveWorldAs = new JMenuItem("Save World As...");
        miSaveWorldAs.addActionListener(this);
        miSaveWorldAs.setActionCommand(cmdSaveWorldAs);
        miSaveWorldAs.setEnabled(false);
        fileMenu.add(miSaveWorldAs);

        fileMenu.addSeparator();

        miImportSpecies = new JMenuItem("Import Species...");
        miImportSpecies.addActionListener(this);
        miImportSpecies.setActionCommand(cmdImportSpecies);
        miImportSpecies.setEnabled(true);
        fileMenu.add(miImportSpecies);

        miExportSpecies = new JMenuItem("Export Species...");
        miExportSpecies.addActionListener(this);
        miExportSpecies.setActionCommand(cmdExportSpecies);
        miExportSpecies.setEnabled(true);
        fileMenu.add(miExportSpecies);

        mainMenuBar.add(fileMenu);

        editMenu = new JMenu("Edit");
        editMenu.addMenuListener(this);

        miCut = new JMenuItem("Cut");
        miCut.addActionListener(this);
        miCut.setActionCommand(cmdCut);
        miCut.setEnabled(false);
        editMenu.add(miCut);

        mainMenuBar.add(editMenu);

        organismMenu = new JMenu("Organism");
        organismMenu.addMenuListener(this);

        miCreateFemale = new JMenuItem("Create Female");
        miCreateFemale.addActionListener(this);
        miCreateFemale.setActionCommand(cmdCreateFemale);
        miCreateFemale.setEnabled(false);
        organismMenu.add(miCreateFemale);

        miCreateMale = new JMenuItem("Create Male");
        miCreateMale.addActionListener(this);
        miCreateMale.setActionCommand(cmdCreateMale);
        miCreateMale.setEnabled(false);
        organismMenu.add(miCreateMale);

        miCreateOrganism = new JMenuItem("Create Organism");
        miCreateOrganism.addActionListener(this);
        miCreateOrganism.setActionCommand(cmdCreateOrganism);
        miCreateOrganism.setEnabled(false);
        organismMenu.add(miCreateOrganism);

        miShowParentFamily = new JMenuItem("Show Parent Family");
        miShowParentFamily.addActionListener(this);
        miShowParentFamily.setActionCommand(cmdShowParentFamily);
        miShowParentFamily.setEnabled(false);
        organismMenu.add(miShowParentFamily);

        miShowChildFamilies = new JMenuItem("Show Child Families");
        miShowChildFamilies.addActionListener(this);
        miShowChildFamilies.setActionCommand(cmdShowChildFamilies);
        miShowChildFamilies.setEnabled(false);
        organismMenu.add(miShowChildFamilies);

        mainMenuBar.add(organismMenu);

        memoryMenu = new JMenu("Memory");
        memoryMenu.addMenuListener(this);

        miReportCollectMemory = new JMenuItem("Report + Collect");
        miReportCollectMemory.addActionListener(this);
        miReportCollectMemory.setActionCommand(cmdReportCollectMemory);
        miReportCollectMemory.setEnabled(true);
        memoryMenu.add(miReportCollectMemory);

        memoryMenu.addSeparator();

        miReportFreeMemory = new JMenuItem("Report Free");
        miReportFreeMemory.addActionListener(this);
        miReportFreeMemory.setActionCommand(cmdReportFreeMemory);
        miReportFreeMemory.setEnabled(true);
        memoryMenu.add(miReportFreeMemory);

        miReportTotalMemory = new JMenuItem("Report Total");
        miReportTotalMemory.addActionListener(this);
        miReportTotalMemory.setActionCommand(cmdReportTotalMemory);
        miReportTotalMemory.setEnabled(true);
        memoryMenu.add(miReportTotalMemory);

        miCollectMemory = new JMenuItem("Garbage Collect");
        miCollectMemory.addActionListener(this);
        miCollectMemory.setActionCommand(cmdCollectMemory);
        miCollectMemory.setEnabled(true);
        memoryMenu.add(miCollectMemory);

        mainMenuBar.add(memoryMenu);
        memoryMenu.setVisible(memoryMenuVisible);
    }

    /**
     * Create action toolbar across the top of the window.
     * When this method returns, the instance variable
     * toolBar will be non-null and ready to be added
     * to the panel.
    **/
    void createActionToolBar()
    {
        Insets insets = new Insets(2,2,2,2);

        toolBar = new BioToolBar();
        toolBar.setBorderPainted(false);
        toolBar.setFloatable(false);
        toolBar.setMargin(insets);

        newWorldButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/new.gif"));
        newWorldButton.setMargin(insets);
        newWorldButton.addActionListener(this);
        newWorldButton.setActionCommand(cmdNewWorld);
        newWorldButton.setFocusPainted(false);
        newWorldButton.setToolTipText("Create New World");
        toolBar.add(newWorldButton);

        openWorldButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/open.gif"));
        openWorldButton.setMargin(insets);
        openWorldButton.addActionListener(this);
        openWorldButton.setActionCommand(cmdOpenWorld);
        openWorldButton.setFocusPainted(false);
        openWorldButton.setToolTipText("Open World File");
        toolBar.add(openWorldButton);

        saveWorldButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/save.gif"));
        saveWorldButton.setMargin(insets);
        saveWorldButton.addActionListener(this);
        saveWorldButton.setActionCommand(cmdSaveWorld);
        saveWorldButton.setFocusPainted(false);
        saveWorldButton.setToolTipText("Save World");
        toolBar.add(saveWorldButton);

        toolBar.addSeparator();

        cutButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/cut.gif"));
        cutButton.setMargin(insets);
        cutButton.addActionListener(this);
        cutButton.setActionCommand(cmdCut);
        cutButton.setFocusPainted(false);
        cutButton.setToolTipText("Cut");
        toolBar.add(cutButton);

        toolBar.addSeparator();

        speciesTextButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/species.gif"));
        speciesTextButton.setMargin(insets);
        speciesTextButton.setFocusPainted(false);
        speciesTextButton.setBorderPainted(false);
        speciesTextButton.setDisabledIcon(getLocalImage("org/concord/biologica/locked/gifs/species.gif"));
        speciesTextButton.setEnabled(false);
        toolBar.add(speciesTextButton);

        speciesComboBox = new BioComboBox();
        speciesComboBox.addItemListener(this);
        speciesComboBox.setPreferredSize(new Dimension(40,20));
        speciesComboBox.setMaximumSize(new Dimension(150,20));
        toolBar.add(speciesComboBox);

        femaleButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/female.gif"));
        femaleButton.setMargin(insets);
        femaleButton.addActionListener(this);
        femaleButton.setActionCommand(cmdCreateFemale);
        femaleButton.setFocusPainted(false);
        femaleButton.setToolTipText("Create Female");
        femaleButton.setEnabled(false);
        toolBar.add(femaleButton);

        maleButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/male.gif"));
        maleButton.setMargin(insets);
        maleButton.addActionListener(this);
        maleButton.setActionCommand(cmdCreateMale);
        maleButton.setFocusPainted(false);
        maleButton.setToolTipText("Create Male");
        maleButton.setEnabled(false);
        toolBar.add(maleButton);

        noSexButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/nosex.gif"));
        noSexButton.setMargin(insets);
        noSexButton.addActionListener(this);
        noSexButton.setActionCommand(cmdCreateOrganism);
        noSexButton.setFocusPainted(false);
        noSexButton.setToolTipText("Create Organism");
        noSexButton.setEnabled(false);
        toolBar.add(noSexButton);

        toolBar.addSeparator();

        viewsButton = new JButton(getLocalImage("org/concord/biologica/locked/gifs/views.gif"));
        viewsButton.setMargin(insets);
        viewsButton.setFocusPainted(false);
        viewsButton.setBorderPainted(false);
        viewsButton.setDisabledIcon(getLocalImage("org/concord/biologica/locked/gifs/views.gif"));
        viewsButton.setEnabled(false);
        toolBar.add(viewsButton);

        treeViewToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/speciesvw.gif"));
        treeViewToggleButton.setMargin(insets);
        treeViewToggleButton.addActionListener(this);
        treeViewToggleButton.setActionCommand(cmdTreePane);
        treeViewToggleButton.setFocusPainted(false);
        treeViewToggleButton.setToolTipText("Show/Hide World Tree Pane");
        treeViewToggleButton.setSelected(false);
        toolBar.add(treeViewToggleButton);

        toolBar.addSeparator();

        pedigreeViewToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/pedvw.gif"));
        pedigreeViewToggleButton.setMargin(insets);
        pedigreeViewToggleButton.addActionListener(this);
        pedigreeViewToggleButton.setActionCommand(cmdPedigreePane);
        pedigreeViewToggleButton.setFocusPainted(false);
        pedigreeViewToggleButton.setToolTipText("Show/Hide Pedigree Pane");
        pedigreeViewToggleButton.setSelected(false);
        toolBar.add(pedigreeViewToggleButton);

        multipleOrganismViewToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/multiorgvw.gif"));
        multipleOrganismViewToggleButton.setMargin(insets);
        multipleOrganismViewToggleButton.addActionListener(this);
        multipleOrganismViewToggleButton.setActionCommand(cmdMultiOrganismPane);
        multipleOrganismViewToggleButton.setFocusPainted(false);
        multipleOrganismViewToggleButton.setToolTipText("Show/Hide Multiple Organism Pane");
        multipleOrganismViewToggleButton.setSelected(true);
        toolBar.add(multipleOrganismViewToggleButton);

        sexViewToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/cellvw.gif"));
        sexViewToggleButton.setMargin(insets);
        sexViewToggleButton.addActionListener(this);
        sexViewToggleButton.setActionCommand(cmdSexPane);
        sexViewToggleButton.setFocusPainted(false);
        sexViewToggleButton.setToolTipText("Show/Hide Sex Pane");
        sexViewToggleButton.setSelected(false);
        toolBar.add(sexViewToggleButton);

        objectPropertiesViewToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/singleorgvw.gif"));
        objectPropertiesViewToggleButton.setMargin(insets);
        objectPropertiesViewToggleButton.addActionListener(this);
        objectPropertiesViewToggleButton.setActionCommand(cmdObjectPropertiesPane);
        objectPropertiesViewToggleButton.setFocusPainted(false);
        objectPropertiesViewToggleButton.setToolTipText("Show/Hide Object Properties Pane");
        objectPropertiesViewToggleButton.setSelected(false);
        toolBar.add(objectPropertiesViewToggleButton);

        chromosomeViewToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/chromovw.gif"));
        chromosomeViewToggleButton.setMargin(insets);
        chromosomeViewToggleButton.addActionListener(this);
        chromosomeViewToggleButton.setActionCommand(cmdChromosomePane);
        chromosomeViewToggleButton.setFocusPainted(false);
        chromosomeViewToggleButton.setToolTipText("Show/Hide Chromosome Pane");
        chromosomeViewToggleButton.setSelected(false);
        toolBar.add(chromosomeViewToggleButton);

        dnaViewToggleButton = new JToggleButton(getLocalImage("org/concord/biologica/locked/gifs/dnavw.gif"));
        dnaViewToggleButton.setMargin(insets);
        dnaViewToggleButton.addActionListener(this);
        dnaViewToggleButton.setActionCommand(cmdDNAPane);
        dnaViewToggleButton.setFocusPainted(false);
        dnaViewToggleButton.setToolTipText("Show/Hide DNA Pane");
        dnaViewToggleButton.setSelected(false);
        toolBar.add(dnaViewToggleButton);
    }

    /**
     * Get file controls visible
     *
     * @return		boolean - visible of file controls
    **/
    public boolean getFileControlsVisible()
    {
        return fileControlsVisible;
    }

    /**
     * Set file controls visible
     *
     * @param		aVisible boolean - new visible of file controls
    **/
    public void setFileControlsVisible(boolean aVisible)
    {
        // Return immediately if no change
        if (aVisible == fileControlsVisible)
        {
            return;
        }

        // Make change
        fileControlsVisible = aVisible;

        if (fileControlsVisible)
        {
            add(mainMenuBar,"North");

            newWorldButton.setVisible(true);
            openWorldButton.setVisible(true);
            saveWorldButton.setVisible(true);
        }
        else
        {
            remove(mainMenuBar);

            newWorldButton.setVisible(false);
            openWorldButton.setVisible(false);
            saveWorldButton.setVisible(false);
        }

        validate();
    }

    /**
     * Get species pulldown visible
     *
     * @return		boolean - visibility of species pulldown
    **/
    public boolean getSpeciesPulldownVisible()
    {
        return speciesPulldownVisible;
    }

    /**
     * Set species pulldown visible
     *
     * @param		aVisible boolean - new visibility of species pulldown
    **/
    public void setSpeciesPulldownVisible(boolean aVisible)
    {
        // Return immediately if no change
        if (aVisible == speciesPulldownVisible)
        {
            return;
        }

        // Make change
        speciesPulldownVisible = aVisible;

        if (speciesPulldownVisible)
        {
            speciesTextButton.setVisible(true);
            speciesComboBox.setVisible(true);
        }
        else
        {
            speciesTextButton.setVisible(false);
            speciesComboBox.setVisible(false);
        }

        validate();
    }

    /**
     * Get memory menu visible
     *
     * @return		boolean - visible of memory menu
    **/
    public boolean getMemoryMenuVisible()
    {
        return memoryMenuVisible;
    }

    /**
     * Set memory menu visible
     *
     * @param		aVisible boolean - new visible of memory menu
    **/
    public void setMemoryMenuVisible(boolean aVisible)
    {
        // Return immediately if no change
        if (aVisible == memoryMenuVisible)
        {
            return;
        }

        // Make change
        memoryMenuVisible = aVisible;
        memoryMenu.setVisible(memoryMenuVisible);
    }

    /**
     * Return the preferred size of this application
     *
     * @return		Dimension - preferred size of application
    **/
    public Dimension getPreferredSize()
    {
        return new Dimension(800,600);
    }

    /**
     * Set the initial state of the display, although the initial state
     * of the level toggle buttons is set when they're created.
    **/
    void setInitialState()
    {
        treePaneVisible = false;
        pedigreePaneVisible = false;
        multipleOrganismPaneVisible = true;
        objectPropertiesPaneVisible = false;
        sexPaneVisible = false;
        chromosomePaneVisible = false;
        dnaPaneVisible = false;
        memoryMenuVisible = false;

        fileControlsVisible = true;
        speciesPulldownVisible = true;

        sexView.setSexViewMode(SexView.SEX_VIEW_MODE_SIX_VIEWS);

        setWindowMode(WINDOW_MODE_WELCOME);

        updateState();
    }

    /**
     * Get the current selection set
     *
     * @return		SelectionSet - the current selection set
    **/
    public SelectionSet getSelectionSet()
    {
        return selectionSet;
    }

    /**
     * Set the current selection set.
     *
     * @param		aSelectionSet SelectionSet - a new selection set
    **/
    public void setSelectionSet(SelectionSet aSelectionSet)
    {
        // If selection set hasn't changed, return immediately
        if (aSelectionSet == selectionSet)
        {
            return;
        }

        // Validate input arguments
        if (aSelectionSet == null)
        {
            throw new IllegalArgumentException(EngineStrings.INPUT_ARGUMENT_ILLEGAL);
        }

        // Make change
        selectionSet = aSelectionSet;

        // Update view
        repaint();

        // Don't notify listeners, as this isn't an event that anyone cares about
    }

    /**
     * Get the tree view
     *
     * @return		TreeView - the tree view
    **/
    public TreeView getTreeView()
    {
        return treeView;
    }

    /**
     * Get the object properties view
     *
     * @return		ObjectPropertiesView - the object properties view
    **/
    public ObjectPropertiesView getObjectPropertiesView()
    {
        return objectPropertiesView;
    }

    /**
     * Get the pedigree view
     *
     * @return		PedigreeView - the pedigree view
    **/
    public PedigreeView getPedigreeView()
    {
        return pedigreeView;
    }

    /**
     * Get the multiple organism view
     *
     * @return		MultipleOrganismView - the multiple organism view
    **/
    public MultipleOrganismView getMultipleOrganismView()
    {
        return multipleOrganismView;
    }

    /**
     * Get the sex view
     *
     * @return		SexView - the sex view
    **/
    public SexView getSexView()
    {
        return sexView;
    }

    /**
     * Get the chromosome view
     *
     * @return		ChromosomeView - the chromosome view
    **/
    public ChromosomeView getChromosomeView()
    {
        return chromosomeView;
    }

    /**
     * Selection changed notification
    **/
    public void selectionChanged()
    {
        // Update special views not subscribed to selection set
        if (selectionSet.getNumberOfSelectedObjects() == 1)
        {
            Enumeration eSelectedObjects = selectionSet.getSelectedObjects();
            EngineObject aSelectedObject = (EngineObject) eSelectedObjects.nextElement();

            objectPropertiesView.setObject(aSelectedObject);

            if (aSelectedObject instanceof Organism)
            {
                Organism selectedOrganism = (Organism) aSelectedObject;

                chromosomeView.setOrganism(selectedOrganism);
                chromosomeView.setChromosomesToShow(0);
                dnaView.setOrganismAllele(null);

                doSelectionOrganismStuff(selectedOrganism);
            }
            else if (aSelectedObject instanceof OrganismChromosomePair)
            {
                OrganismChromosomePair selectedOrganismChromosomePair = (OrganismChromosomePair) aSelectedObject;

                chromosomeView.setOrganism(selectedOrganismChromosomePair.getOrganism());
                chromosomeView.setChromosomesToShow(selectedOrganismChromosomePair.getChromosomePairNumberType());
                dnaView.setOrganismAllele(null);
            }
            else if (aSelectedObject instanceof OrganismAllele)
            {
                OrganismAllele selectedOrganismAllele = (OrganismAllele) aSelectedObject;
                dnaView.setOrganismAllele(selectedOrganismAllele);
                chromosomeView.setOrganism(selectedOrganismAllele.getOrganism());
                int numberType = selectedOrganismAllele.getOrganismChromosome().getNumberType();
                if (numberType == IChromosome.X_CHROMOSOME ||
                    numberType == IChromosome.Y_CHROMOSOME)
                {
                    chromosomeView.setChromosomesToShow(IChromosome.SEX_CHROMOSOME);
                }
                else
                {
                    chromosomeView.setChromosomesToShow(numberType);
                }
            }
            else if (aSelectedObject instanceof OrganismAllelePair)
            {
                OrganismAllelePair selectedOrganismAllelePair = (OrganismAllelePair) aSelectedObject;
                dnaView.setOrganismAllelePair(selectedOrganismAllelePair);
                
                chromosomeView.setOrganism(selectedOrganismAllelePair.getOrganism());
                int numberType = selectedOrganismAllelePair.getOrganismChromosomePair().getChromosomePairNumberType();
                chromosomeView.setChromosomesToShow(numberType);
            }
            else
            {
                // Not one organism selected, so clear out chromosome view
                chromosomeView.setOrganism(null);
                chromosomeView.setChromosomesToShow(0);
                dnaView.setOrganismAllele(null);
            }
        }
        else if (selectionSet.getNumberOfSelectedObjects() == 2)
        {
            // Look for case where organism and organism allele in that organism are selected
            Organism selectedOrganism = null;
            OrganismAllele selectedOrganismAllele = null;

            Enumeration eSelectedObjects = selectionSet.getSelectedObjects();
            EngineObject firstSelectedObject = (EngineObject) eSelectedObjects.nextElement();
            EngineObject secondSelectedObject = (EngineObject) eSelectedObjects.nextElement();

            if (firstSelectedObject instanceof Organism)
            {
                if (secondSelectedObject instanceof OrganismAllele)
                {
                    selectedOrganism = (Organism) firstSelectedObject;
                    selectedOrganismAllele = (OrganismAllele) secondSelectedObject;
                }
            }
            else if (secondSelectedObject instanceof Organism)
            {
                if (firstSelectedObject instanceof OrganismAllele)
                {
                    selectedOrganism = (Organism) secondSelectedObject;
                    selectedOrganismAllele = (OrganismAllele) firstSelectedObject;
                }
            }

            if (selectedOrganism != null && selectedOrganismAllele != null)
            {
                objectPropertiesView.setObject(selectedOrganism);
                chromosomeView.setOrganism(selectedOrganism);
                chromosomeView.setChromosomesToShow(0);
                dnaView.setOrganismAllele(selectedOrganismAllele);
                doSelectionOrganismStuff(selectedOrganism);
            }
            else
            {
                // Not one object selected, so clear out various views
                objectPropertiesView.setObject(null);
                chromosomeView.setOrganism(null);
                chromosomeView.setChromosomesToShow(0);
                objectPropertiesView.setObject(null);
                dnaView.setOrganismAllele(null);
            }
        }
        else
        {
            // Not one object selected, so clear out various views
            objectPropertiesView.setObject(null);
            chromosomeView.setOrganism(null);
            chromosomeView.setChromosomesToShow(0);
            objectPropertiesView.setObject(null);
            dnaView.setOrganismAllele(null);
        }

        // Update menu items
        updateState();
    }

    /**
     * Do selection organism stuff
    **/
    public void doSelectionOrganismStuff(Organism selectedOrganism)
    {
        if (selectedOrganism != null &&
            !selectedOrganism.containsFatalCharacteristic())
        {
            int selectedOrganismSex = selectedOrganism.getSex();
            if (selectedOrganismSex == Organism.NO_SEX)
            {
                Organism fatherOrganism = sexView.getFatherOrganism();
                Organism motherOrganism = sexView.getMotherOrganism();
                if (motherOrganism == null &&
                    (fatherOrganism == null ||
                     (fatherOrganism.getSpecies() == selectedOrganism.getSpecies())))
                {
                    sexView.setMotherOrganism(selectedOrganism);
                }
                else if (fatherOrganism == null &&
                         (motherOrganism == null ||
                          (motherOrganism.getSpecies() == selectedOrganism.getSpecies())))
                {
                    sexView.setFatherOrganism(selectedOrganism);
                }
                else if (fatherOrganism != null && motherOrganism != null &&
                         (motherOrganism.getSpecies() == selectedOrganism.getSpecies()))
                {
                    sexView.setMotherOrganism(selectedOrganism);
                }
            }
            else if (selectedOrganismSex == Organism.FEMALE)
            {
                Organism fatherOrganism = sexView.getFatherOrganism();
                if (fatherOrganism == null ||
                    fatherOrganism.getSpecies() == selectedOrganism.getSpecies())
                {
                    sexView.setMotherOrganism(selectedOrganism);
                }
            }
            else
            {
                Organism motherOrganism = sexView.getMotherOrganism();
                if (motherOrganism == null ||
                    motherOrganism.getSpecies() == selectedOrganism.getSpecies())
                {
                    sexView.setFatherOrganism(selectedOrganism);
                }
            }
        }
    }

    /**
     * React to actions
    **/
    public void actionPerformed(ActionEvent e)
    {
        File aFile, aSaveFile;
        String cmd = e.getActionCommand();

        // Determine if action is a command or a tool
        if (cmd.equals(JFileChooser.CANCEL_SELECTION))
        {
            // Switch back to previous window mode
            setWindowMode(previousWindowMode);
        }
        else if (cmd.equals(JFileChooser.APPROVE_SELECTION))
        {
            // Open or save a world file
            if (currentWorld == null && currentWindowMode == WINDOW_MODE_OPEN_WORLD_FILE)
            {
                aFile = fileChooser.getSelectedFile();
                if (aFile != null)
                {
                    aSaveFile = enforceFileExtension(aFile,"xml");
                    if (aSaveFile != null)
                    {
                        currentWorld = new World(aSaveFile);
                        currentWorld.addPropertyChangeListener(this);

                        // Populate tree view if it's visible
                        if (treeView != null && treePaneVisible == true)
                        {
                            treeView.setRootEngineObject(currentWorld);
                        }

                        // Add all organisms to multiple organism view
                        addWorldOrganismsToMultipleOrganismView();

                        updateState();
                    }
                }

                // Switch to normal window mode
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (currentWorld != null)
            {
                if (currentWindowMode == WINDOW_MODE_SAVE_WORLD_FILE_AS)
                {
                    aFile = fileChooser.getSelectedFile();
                    if (aFile != null)
                    {
                        aSaveFile = enforceFileExtension(aFile,"xml");
                        if (aSaveFile != null)
                        {
                            currentWorld.saveAs(aSaveFile);
                        }
                    }
                }
                else if (currentWindowMode == WINDOW_MODE_IMPORT_SPECIES_FILE)
                {
                    aFile = fileChooser.getSelectedFile();
                    if (aFile != null)
                    {
                        aSaveFile = enforceFileExtension(aFile,"xml");
                        if (aSaveFile != null)
                        {
                            new Species(currentWorld,aSaveFile);
                        }
                    }
                }
                else if (currentWindowMode == WINDOW_MODE_EXPORT_SPECIES_FILE)
                {
                    aFile = fileChooser.getSelectedFile();
                    if (aFile != null)
                    {
                        aSaveFile = enforceFileExtension(aFile,"xml");
                        if (aSaveFile != null)
                        {
                            Species aSpecies = currentWorld.getCurrentSpecies();
                            if (aSpecies != null)
                            {
                                aSpecies.exportTo(aSaveFile);
                            }
                        }
                    }
                }

                // Switch to normal window mode
                setWindowMode(WINDOW_MODE_NORMAL);
            }
        }
        else if (cmd.charAt(0) == 'C')
        {
            // Command
            if (cmd.equals(cmdNewWorld))
            {
                if (currentWorld == null)
                {
                    currentWorld = new World();
                    currentWorld.addPropertyChangeListener(this);

                    // Populate tree view if it's visible
                    if (treeView != null && treePaneVisible == true)
                    {
                        treeView.setRootEngineObject(currentWorld);
                    }

                    // Switch to normal window mode
                    setWindowMode(WINDOW_MODE_NORMAL);

                    updateState();
                }
            }
            else if (cmd.equals(cmdOpenWorld))
            {
                if (currentWorld == null)
                {
                    // Switch to normal window mode
                    setWindowMode(WINDOW_MODE_OPEN_WORLD_FILE);
                }
            }
            else if (cmd.equals(cmdCloseWorld))
            {
                // Close
                if (currentWorld != null)
                {
                    // Should ask if they want to save if world has been changed
                    if (currentWorld.isDirty() == true)
                    {
                        // Should ask if they want to save...
                    }

                    // Clear selections
                    selectionSet.deselectAllObjects();

                    // Remove all organisms from views
                    chromosomeView.setOrganism(null);
                    multipleOrganismView.removeAllOrganisms();
                    pedigreeView.removeAllOrganisms();
                    treeView.setRootEngineObject(null);
                    objectPropertiesView.setObject(null);
                    sexView.setMotherOrganism(null);
                    sexView.setFatherOrganism(null);

                    // Forget the current world
                    currentWorld.removePropertyChangeListener(this);
                    currentWorld.delete();
                    currentWorld = null;

                    // Switch to normal window mode
                    setWindowMode(WINDOW_MODE_WELCOME);

                    // Reset pedigree locations
                    xPedigreeView = 50;
                    yPedigreeView = 50;

                    updateState();
                }
            }
            else if (cmd.equals(cmdSaveWorld))
            {
                // Save current world if there is one and it has a filename and it is XML
                // Else do a SaveAs, forcing XML
                if (currentWorld != null)
                {
                    // Force a save as
                    File currentWorldFile = currentWorld.getFile();
                    if (currentWorldFile != null)
                    {
                        String s = currentWorldFile.getName();
                        int length = s.length();
                        if (length > 4 &&
                            s.charAt(length-1) == 'l' &&
                            s.charAt(length-2) == 'm' &&
                            s.charAt(length-3) == 'x' &&
                            s.charAt(length-4) == '.')
                        {
                            currentWorld.saveAs(currentWorldFile);
                        }
                        else
                        {
                            // Switch to save as window mode
                            setWindowMode(WINDOW_MODE_SAVE_WORLD_FILE_AS);
                        }
                    }
                    else
                    {
                        // Switch to save as window mode
                        setWindowMode(WINDOW_MODE_SAVE_WORLD_FILE_AS);
                    }
                }
            }
            else if (cmd.equals(cmdSaveWorldAs))
            {
                // Save As
                if (currentWorld != null)
                {
                    // Switch to save as window mode
                    setWindowMode(WINDOW_MODE_SAVE_WORLD_FILE_AS);
                }
            }
            else if (cmd.equals(cmdImportSpecies))
            {
                // Import species file
                if (currentWorld != null)
                {
                    setWindowMode(WINDOW_MODE_IMPORT_SPECIES_FILE);
                }
            }
            else if (cmd.equals(cmdExportSpecies))
            {
                // Import species file
                if (currentWorld != null &&
                    currentWorld.getCurrentSpecies() != null)
                {
                    setWindowMode(WINDOW_MODE_EXPORT_SPECIES_FILE);
                }
            }
            else if (cmd.equals(cmdCut))
            {
                if (selectionSet.getNumberOfSelectedObjects() > 0)
                {
                    // Cut whatever is selected
                    EngineObject engineObject;
                    Enumeration eObjects = selectionSet.getSelectedObjects();
                    while (eObjects.hasMoreElements())
                    {
                        engineObject = (EngineObject) eObjects.nextElement();
                        engineObject.delete();
                    }
                }
            }
            else if (cmd.equals(cmdCreateFemale) || cmd.equals(cmdCreateMale) || cmd.equals(cmdCreateOrganism))
            {
                // Create male or female organism
                if (currentWorld != null && currentWorld.getCurrentSpecies() != null)
                {
                    int gender;
                    if (cmd.equals(cmdCreateFemale))
                    {
                        gender = Organism.FEMALE;
                    }
                    else if (cmd.equals(cmdCreateMale))
                    {
                        gender = Organism.MALE;
                    }
                    else
                    {
                        gender = Organism.NO_SEX;
                    }


                    // Create an organism.  Don't allow it to be dead.
                    Organism organism = new Organism(currentWorld, gender,
                                                     null, currentWorld.getCurrentSpecies());
                }
            }
            else if (cmd.equals(cmdTreePane))
            {
                if (treePaneVisible)
                {
                    setTreePaneVisible(false);
                }
                else
                {
                    setTreePaneVisible(true);
                }
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (cmd.equals(cmdPedigreePane))
            {
                if (pedigreePaneVisible)
                {
                    pedigreePaneVisible = false;
                }
                else
                {
                    pedigreePaneVisible = true;
                }
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (cmd.equals(cmdMultiOrganismPane))
            {
                if (multipleOrganismPaneVisible)
                {
                    multipleOrganismPaneVisible = false;
                }
                else
                {
                    multipleOrganismPaneVisible = true;
                }
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (cmd.equals(cmdSexPane))
            {
                if (sexPaneVisible)
                {
                    sexPaneVisible = false;
                }
                else
                {
                    sexPaneVisible = true;
                }
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (cmd.equals(cmdObjectPropertiesPane))
            {
                if (objectPropertiesPaneVisible)
                {
                    objectPropertiesPaneVisible = false;
                }
                else
                {
                    objectPropertiesPaneVisible = true;
                }
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (cmd.equals(cmdChromosomePane))
            {
                if (chromosomePaneVisible)
                {
                    chromosomePaneVisible = false;
                }
                else
                {
                    chromosomePaneVisible = true;
                }
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (cmd.equals(cmdDNAPane))
            {
                if (dnaPaneVisible)
                {
                    dnaPaneVisible = false;
                }
                else
                {
                    dnaPaneVisible = true;
                }
                setWindowMode(WINDOW_MODE_NORMAL);
            }
            else if (cmd.equals(cmdShowParentFamily))
            {
                if (selectionSet.getNumberOfSelectedObjects() == 1)
                {
                    Enumeration selectedObjects = selectionSet.getSelectedObjects();
                    EngineObject selectedEngineObject = (EngineObject) selectedObjects.nextElement();
                    if (selectedEngineObject instanceof Organism)
                    {
                        Organism selectedOrganism = (Organism) selectedEngineObject;
                        Family parentFamily = selectedOrganism.getParentFamily();
                        if (parentFamily != null)
                        {
                            pedigreeView.addFamily(parentFamily,100,100);
                        }
                    }
                }
            }
            else if (cmd.equals(cmdShowChildFamilies))
            {
                if (selectionSet.getNumberOfSelectedObjects() == 1)
                {
                    Enumeration selectedObjects = selectionSet.getSelectedObjects();
                    EngineObject selectedEngineObject = (EngineObject) selectedObjects.nextElement();
                    if (selectedEngineObject instanceof Organism)
                    {
                        Family childFamily;
                        Organism selectedOrganism = (Organism) selectedEngineObject;
                        Enumeration childFamilies = selectedOrganism.getChildFamilies();
                        while (childFamilies.hasMoreElements())
                        {
                            childFamily = (Family) childFamilies.nextElement();
                            pedigreeView.addFamily(childFamily,100,100);
                        }
                    }
                }
            }
            else if (cmd.equals(cmdReportCollectMemory))
            {
                Runtime.getRuntime().gc();
                System.out.println("Garbage collected");
                System.out.println("Free memory = " + Runtime.getRuntime().freeMemory());
                System.out.println("Total memory = " + Runtime.getRuntime().totalMemory());
            }
            else if (cmd.equals(cmdReportFreeMemory))
            {
                System.out.println("Free memory = " + Runtime.getRuntime().freeMemory());
            }
            else if (cmd.equals(cmdReportTotalMemory))
            {
                System.out.println("Total memory = " + Runtime.getRuntime().totalMemory());
            }
            else if (cmd.equals(cmdCollectMemory))
            {
                Runtime.getRuntime().gc();
                System.out.println("Garbage collected");
            }
        }
    }

    /**
     * Get the tree pane visibility
     *
     * @return   boolean - visibility of tree pane
    **/
    public boolean getTreePaneVisible()
    {
        return treePaneVisible;
    }

    /**
     * Set the tree pane visibility.  Do nothing if
     * visibility is not changing.
     *
     * @param   aVisible boolean - new visibility
    **/
    public void setTreePaneVisible(boolean aVisible)
    {
        if (aVisible == treePaneVisible)
        {
            return;
        }

        treePaneVisible = aVisible;
        if (treePaneVisible == false)
        {
            if (treeView != null)
            {
                treeView.setRootEngineObject(null);
            }

            remove(treeSplitPane);
            treeSplitPane.remove(mainPanel);
            add(mainPanel,"Center");
        }
        else
        {
            if (treeView != null && currentWorld != null)
            {
                treeView.setRootEngineObject(currentWorld);
            }

            remove(mainPanel);
            treeSplitPane.add(mainPanel);
            add(treeSplitPane);
        }
    }

    /**
     * Enforce the proper file extension, if possible
     *
     * @param	aFile File - a possible file
     * @param	anExtension String - filename extension to enforce (e.g. "xml", "blw" or "bls")
     * @return	File - proper file with enforced extension, null if it can't be done,
     *				   may be input aFile if that's okay, else new File with correct extension
    **/
    public File enforceFileExtension(File aFile, String anExtension)
    {
        // If aFile null or a directory, return null immediately
        if (aFile == null || aFile.isDirectory())
        {
            return null;
        }

        String oldPath = aFile.getAbsolutePath();
        if (oldPath == null)
        {
            return null;
        }

        int i = oldPath.lastIndexOf('.');
        if (i > 0 &&  i < oldPath.length() - 1)
        {
            String currentExtension = oldPath.substring(i+1).toLowerCase();
            if (anExtension.equals(currentExtension))
            {
                // Input aFile okay, so just return it
                return aFile;
            }
        }

        // Just add extension to old path
        String newPath = oldPath + "." + anExtension;

        // Create and return File with new path
        return new File(newPath);
    }

    /**
     * Handle property change events
     *
     * @param	event PropertyChangeEvent - the property change event
    **/
    public void propertyChange(PropertyChangeEvent event)
    {
        String propertyName = event.getPropertyName();

        if (propertyName.equals(EngineProp.CURRENT_SPECIES))
        {
            Object object = event.getNewValue();
            if (object instanceof Species)
            {
                onSetCurrentSpecies((Species)object);
            }
            else if (object == null)
            {
                onSetCurrentSpecies(null);
            }
        }
        else if (propertyName.equals(EngineProp.SPECIES_ADDED))
        {
            // Update state of combo boxes, buttons, etc.
            // and add this object as a property change listener
            Object object = event.getNewValue();
            if (object instanceof Species)
            {
                updateState();
                Species species = (Species) object;
                species.addPropertyChangeListener(this);
            }
        }
        else if (propertyName.equals(EngineProp.SPECIES_REMOVED))
        {
            // Update state of combo boxes, buttons, etc.
            // and remove this object as a property change listener
            Object object = event.getNewValue();
            if (object instanceof Species)
            {
                updateState();
                Species species = (Species) object;
                species.removePropertyChangeListener(this);
            }
        }
        else if (propertyName.equals(EngineProp.NAME))
        {
            Object object = event.getSource();
            if (object instanceof Species)
            {
                // Species name changed, so need to update combo box
                updateState();
            }
        }
        else if (propertyName.equals(UIProp.OFFSPRING_ORGANISM))
        {
            Object object = event.getSource();
            if (object == sexView)
            {
                Object objectNewValue = event.getNewValue();
                if (objectNewValue instanceof Organism)
                {
                    Organism offspring = (Organism) objectNewValue;

                    // Add organism to pedigree organism view
                    Species pedigreeViewSpecies = pedigreeView.getSpecies();
                    if (pedigreeViewSpecies == null ||
                        pedigreeViewSpecies == offspring.getSpecies())
                    {
                        if (pedigreeView.containsOrganism(offspring) == false)
                        {
                            pedigreeView.addOrganism(offspring,xPedigreeView,yPedigreeView);
                            xPedigreeView += 50;
                            if (xPedigreeView > 400)
                            {
                                xPedigreeView = 50;
                                yPedigreeView += 50;
                            }
                        }
                    }
                }
            }
        }
        else if (propertyName.equals(EngineProp.ORGANISM_ADDED))
        {
            Object object = event.getNewValue();
            if (object instanceof Organism)
            {
                Organism newOrganism = (Organism) object;
                multipleOrganismView.addOrganism(newOrganism);
            }
        }
        else if (propertyName.equals(UIProp.CHROMOSOME_TOOL_PICK_ON_ORGANISM))
        {
            Object object = event.getNewValue();
            if (object instanceof Organism)
            {
                Organism organism = (Organism) object;
                // Show the chromosome view, ignoring shift key and select organism
                if (!chromosomePaneVisible)
                {
                    chromosomePaneVisible = true;
                    chromosomeViewToggleButton.setSelected(true);
                    setWindowMode(WINDOW_MODE_NORMAL);
                }
                selectionSet.selectObject(organism,false,false);
            }
        }
        else if (propertyName.equals(UIProp.PEDIGREE_TOOL_PICK_ON_ORGANISM))
        {
            Object object = event.getNewValue();
            if (object instanceof Organism)
            {
                Organism organism = (Organism) object;
                // Show the pedigree view, ignoring shift key and select organism
                if (!pedigreePaneVisible)
                {
                    pedigreePaneVisible = true;
                    pedigreeViewToggleButton.setSelected(true);
                    setWindowMode(WINDOW_MODE_NORMAL);
                }
                selectionSet.selectObject(organism,false,false);
                if (pedigreeView.containsOrganism(organism) == false)
                {
                    pedigreeView.addOrganism(organism,xPedigreeView,yPedigreeView);
                    xPedigreeView += 50;
                    if (xPedigreeView > 400)
                    {
                        xPedigreeView = 50;
                        yPedigreeView += 50;
                    }
                    updateState();
                }
            }
        }
    }

    /**
     * Handle combo box item changed events.
     *
     * @param		event ItemEvent - item changed event
    **/
    public void itemStateChanged(ItemEvent event)
    {
        // Avoid loops by ignoring state changes while updating species combo box
        if (updatingState)
        {
            return;
        }

        if (currentWorld != null)
        {
            Object object = event.getSource();
            if (object instanceof BioComboBox)
            {
                BioComboBox comboBox = (BioComboBox) object;
                object = comboBox.getSelectedItem();
                if (object instanceof String && comboBox == speciesComboBox)
                {
                    String newSpeciesName = (String) object;

                    // Determine which species was selected
                    Species aSpecies;
                    Enumeration eSpecies = currentWorld.getSpecies();
                    while (eSpecies.hasMoreElements())
                    {
                        aSpecies = (Species) eSpecies.nextElement();
                        if (aSpecies.getName().equals(newSpeciesName))
                        {
                            // Set current species
                            // Let resulting notification cause ui updates,
                            // so don't set any state directly here.
                            currentWorld.setCurrentSpecies(aSpecies);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * React to a new current species
     *
     * @param		aSpecies Species - a new species, may be null
    **/
    private void onSetCurrentSpecies(Species aSpecies)
    {
        // Update state of interface (buttons, etc.)
        updateState();
    }

    /**
     * Set the font for this view.  If a null font is specified,
     * the view will revert back to its default font.<p>
     *
     * @param		aFont Font - a new font, if null, then will revert to default font
    **/
    public void setFont(Font aFont)
    {
        if (aFont != null)
        {
            // Let superclass do all the interesting stuff
            super.setFont(aFont);

            // Now tell subviews that they have a new font
            welcomeView.setFont(aFont);
            treeView.setFont(aFont);
            objectPropertiesView.setFont(aFont);
            pedigreeView.setFont(aFont);
            multipleOrganismView.setFont(aFont);
            sexView.setFont(aFont);
            chromosomeView.setFont(aFont);
            dnaView.setFont(aFont);
            fileChooser.setFont(aFont);
            toolView.setFont(aFont);
        }
    }

    /**
     * Set the current world.
     *
     * @param		aWorld World - a world, may be null
    **/
    public void setCurrentWorld(World aWorld)
    {
        onSetCurrentWorld(aWorld);
    }

    /**
     * React to a new current world.  Note that this method is NOT the
     * way to set the current world.  Instead, use Engine.setCurrentWorld()
     * to do that.<p>
     *
     * @param		aWorld World - a new world, may be null
    **/
    private void onSetCurrentWorld(World aWorld)
    {
        // Don't return immediately if same world.  Just handle it.

        // Remove this window as a listener on the old current world
        if (currentWorld != null)
        {
            currentWorld.removePropertyChangeListener(this);
        }

        // Set new world
        currentWorld = aWorld;

        // Listen for events on new world, so we'll find out
        // about selected objects, species, organisms, etc.
        if (currentWorld != null)
        {
            treeView.setRootEngineObject(currentWorld);
            currentWorld.addPropertyChangeListener(this);
        }

        // Add all organisms to multiple organism view
        addWorldOrganismsToMultipleOrganismView();

        // Update state of interface (buttons, etc.)
        updateState();
    }

    /**
     * Set window mode, updating window appropriately.
     *
     * @param	aWindowMode int - new window mode
    **/
    public void setWindowMode(int aWindowMode)
    {
        int numPanes = 0;

        // Remove all views from the main panel
        mainPanel.removeAll();

        // Add appropriate views back into main panel in correct order
        if (aWindowMode == WINDOW_MODE_STARTUP)
        {
            previousWindowMode = WINDOW_MODE_WELCOME;
            currentWindowMode = aWindowMode;

            // Show all the panes in this mode
            mainPanel.add(fileChooser); numPanes++;
            mainPanel.add(welcomeViewScrollPane); numPanes++;
            mainPanel.add(pedigreeView); numPanes++;
            mainPanel.add(multipleOrganismScrollPane); numPanes++;
            mainPanel.add(sexView); numPanes++;
            mainPanel.add(objectPropertiesViewScrollPane); numPanes++;
            mainPanel.add(chromosomeScrollPane); numPanes++;
            mainPanel.add(dnaScrollPane); numPanes++;

            gridLayout.setRows(numPanes);
            gridLayout.layoutContainer(mainPanel);
            mainPanel.validate();
        }
        else if (aWindowMode == WINDOW_MODE_WELCOME)
        {
            previousWindowMode = WINDOW_MODE_WELCOME;
            currentWindowMode = aWindowMode;

            // Show just the welcome view
            mainPanel.add(welcomeViewScrollPane);

            numPanes = 1;
            gridLayout.setRows(numPanes);
            gridLayout.layoutContainer(mainPanel);
            mainPanel.validate();

            welcomeView.repaint();
        }
        else if (aWindowMode == WINDOW_MODE_OPEN_WORLD_FILE)
        {
            previousWindowMode = currentWindowMode;
            currentWindowMode = aWindowMode;

            // Show just the file chooser view
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setCurrentDirectory(worldStartPath);
            fileChooser.setApproveButtonText("Open");
            mainPanel.add(fileChooser);

            numPanes = 1;
            gridLayout.setRows(numPanes);
            gridLayout.layoutContainer(mainPanel);
            mainPanel.validate();

            // Force a repaint of file chooser
            fileChooser.repaint();
        }
        else if (aWindowMode == WINDOW_MODE_SAVE_WORLD_FILE_AS)
        {
            previousWindowMode = currentWindowMode;
            currentWindowMode = aWindowMode;

            // Show just the file chooser view
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setCurrentDirectory(worldStartPath);
            fileChooser.setApproveButtonText("Save");
            mainPanel.add(fileChooser);

            numPanes = 1;
            gridLayout.setRows(numPanes);
            gridLayout.layoutContainer(mainPanel);
            mainPanel.validate();

            // Force a repaint of file chooser
            fileChooser.repaint();
        }
        else if (aWindowMode == WINDOW_MODE_IMPORT_SPECIES_FILE ||
                 aWindowMode == WINDOW_MODE_EXPORT_SPECIES_FILE)
        {
            previousWindowMode = currentWindowMode;
            currentWindowMode = aWindowMode;

            // Show just the file chooser view
            fileChooser.setFileFilter(fileFilter);
            File startPath;
            if (selectedSpecies != null)
            {
                startPath = new File(PathStrings.getSpeciesDirectory() + selectedSpecies.getName());
            }
            else
            {
                startPath = new File(PathStrings.getSpeciesDirectory());
            }

            fileChooser.setCurrentDirectory(startPath);
            if (aWindowMode == WINDOW_MODE_IMPORT_SPECIES_FILE)
            {
                fileChooser.setApproveButtonText("Import");
            }
            else
            {
                fileChooser.setApproveButtonText("Export");
            }
            mainPanel.add(fileChooser);

            numPanes = 1;
            gridLayout.setRows(numPanes);
            gridLayout.layoutContainer(mainPanel);
            mainPanel.validate();

            // Force a repaint of file chooser
            fileChooser.repaint();
        }
        else if (aWindowMode == WINDOW_MODE_NORMAL)
        {
            previousWindowMode = currentWindowMode;
            currentWindowMode = aWindowMode;

            // Normal mode - show whichever views are visible
            if (pedigreePaneVisible)
            {
                mainPanel.add(pedigreeView);
                numPanes++;
            }
            if (multipleOrganismPaneVisible)
            {
                mainPanel.add(multipleOrganismScrollPane);
                numPanes++;
            }
            if (sexPaneVisible)
            {
                mainPanel.add(sexView);
                numPanes++;
            }
            if (objectPropertiesPaneVisible)
            {
                mainPanel.add(objectPropertiesViewScrollPane);
                numPanes++;
            }
            if (chromosomePaneVisible)
            {
                mainPanel.add(chromosomeScrollPane);
                numPanes++;
            }
            if (dnaPaneVisible)
            {
                mainPanel.add(dnaScrollPane);
                numPanes++;
            }

            gridLayout.setRows(numPanes);
            gridLayout.layoutContainer(mainPanel);
            mainPanel.validate();

            // Force a repaint of the visible views
            if (treePaneVisible)
            {
                treeView.repaint();
            }
            if (pedigreePaneVisible)
            {
                pedigreeView.repaint();
            }
            if (multipleOrganismPaneVisible)
            {
                multipleOrganismView.repaint();
            }
            if (sexPaneVisible)
            {
                sexView.repaint();
            }
            if (objectPropertiesPaneVisible)
            {
                objectPropertiesView.repaint();
            }
            if (chromosomePaneVisible)
            {
                chromosomeView.repaint();
            }
            if (dnaPaneVisible)
            {
                dnaView.repaint();
            }
        }

        repaint();

        updateState();
    }

    /**
     * Add all organisms to multiple organism view
    **/
    private void addWorldOrganismsToMultipleOrganismView()
    {
        if (currentWorld != null &&
            multipleOrganismView != null)
        {
            Organism anOrganism;
            Enumeration eOrganisms = currentWorld.getOrganisms();
            while (eOrganisms.hasMoreElements())
            {
                anOrganism = (Organism) eOrganisms.nextElement();
                multipleOrganismView.addOrganism(anOrganism);
            }
        }
    }

    /**
     * Update state of interface (buttons, etc.)
    **/
    private void updateState()
    {
        // Avoid recursive calls to this method
        if (updatingState == true)
        {
            return;
        }
        updatingState = true;

        if (currentWorld != null &&
            currentWindowMode == WINDOW_MODE_NORMAL)
        {
            // Current world non-null
            miNewWorld.setEnabled(false);
            newWorldButton.setEnabled(false);

            miOpenWorld.setEnabled(false);
            openWorldButton.setEnabled(false);

            miCloseWorld.setEnabled(true);

            miSaveWorld.setEnabled(true);
            saveWorldButton.setEnabled(true);

            miSaveWorldAs.setEnabled(true);

            miImportSpecies.setEnabled(true);

            // Create male and female enabled if there's a current species
            Species currentSpecies = currentWorld.getCurrentSpecies();
            if (currentSpecies != null)
            {
                miExportSpecies.setEnabled(true);

                if (currentSpecies.getDiploidType() == Species.DIPLOID_TYPE_NO_SEX_CHROMOSOMES)
                {
                    miCreateFemale.setEnabled(false);
                    femaleButton.setEnabled(false);

                    miCreateMale.setEnabled(false);
                    maleButton.setEnabled(false);

                    miCreateOrganism.setEnabled(true);
                    noSexButton.setEnabled(true);
                }
                else
                {
                    miCreateFemale.setEnabled(true);
                    femaleButton.setEnabled(true);

                    miCreateMale.setEnabled(true);
                    maleButton.setEnabled(true);

                    miCreateOrganism.setEnabled(false);
                    noSexButton.setEnabled(false);
                }

                // Some items are enabled only if a certain number or types of objects are selected
                int numberSelectedObjects = selectionSet.getNumberOfSelectedObjects();
                if (numberSelectedObjects == 1)
                {
                    miCut.setEnabled(true);
                    cutButton.setEnabled(true);

                    Enumeration selectedObjects = selectionSet.getSelectedObjects();
                    EngineObject selectedEngineObject = (EngineObject) selectedObjects.nextElement();
                    if (selectedEngineObject instanceof Organism)
                    {
                        Organism selectedOrganism = (Organism) selectedEngineObject;
                        if (pedigreeView.containsOrganism(selectedOrganism))
                        {
                            miShowParentFamily.setEnabled(true);
                            miShowChildFamilies.setEnabled(true);
                        }
                        else
                        {
                            miShowParentFamily.setEnabled(false);
                            miShowChildFamilies.setEnabled(false);
                        }
                    }
                    else
                    {
                        miShowParentFamily.setEnabled(false);
                        miShowChildFamilies.setEnabled(false);
                    }
                }
                else if (numberSelectedObjects > 1)
                {
                    miCut.setEnabled(true);
                    cutButton.setEnabled(true);
                    miShowParentFamily.setEnabled(false);
                    miShowChildFamilies.setEnabled(false);
                }
                else
                {
                    miCut.setEnabled(false);
                    cutButton.setEnabled(false);
                    miShowParentFamily.setEnabled(false);
                    miShowChildFamilies.setEnabled(false);
                }
            }
            else
            {
                miExportSpecies.setEnabled(false);

                miCreateFemale.setEnabled(false);
                femaleButton.setEnabled(false);

                miCreateMale.setEnabled(false);
                maleButton.setEnabled(false);

                miCreateOrganism.setEnabled(false);
                noSexButton.setEnabled(false);

                miCut.setEnabled(false);
                cutButton.setEnabled(false);

                miShowParentFamily.setEnabled(false);
                miShowChildFamilies.setEnabled(false);
            }

            // Clear species combo box
            if (speciesComboBox.getItemCount() > 0)
            {
                speciesComboBox.removeAllItems();
            }

            // Repopulate species combo box
            if (currentWorld.getNumberOfSpecies() > 0)
            {
                Species species;
                Enumeration eSpecies = currentWorld.getSpecies();
                while (eSpecies.hasMoreElements())
                {
                    species = (Species) eSpecies.nextElement();
                    speciesComboBox.addItem(species.getName());
                }

                // Select current species if not null
                if (currentSpecies != null)
                {
                    speciesComboBox.setSelectedItem(currentSpecies.getName());
                }
            }

            // Enable level toggle buttons
            treeViewToggleButton.setEnabled(true);
            pedigreeViewToggleButton.setEnabled(true);
            multipleOrganismViewToggleButton.setEnabled(true);
            sexViewToggleButton.setEnabled(true);
            objectPropertiesViewToggleButton.setEnabled(true);
            chromosomeViewToggleButton.setEnabled(true);
            dnaViewToggleButton.setEnabled(true);
        }
        else
        {
            // Current world null or window mode not normal
            miNewWorld.setEnabled(true);
            newWorldButton.setEnabled(true);

            miOpenWorld.setEnabled(true);
            openWorldButton.setEnabled(true);

            miCloseWorld.setEnabled(false);

            miSaveWorld.setEnabled(false);
            saveWorldButton.setEnabled(false);

            miSaveWorldAs.setEnabled(false);

            miImportSpecies.setEnabled(false);
            miExportSpecies.setEnabled(false);

            miCreateFemale.setEnabled(false);
            femaleButton.setEnabled(false);

            miCreateMale.setEnabled(false);
            maleButton.setEnabled(false);

            miCreateOrganism.setEnabled(false);
            noSexButton.setEnabled(false);

            miCut.setEnabled(false);
            cutButton.setEnabled(false);

            miShowParentFamily.setEnabled(false);
            miShowChildFamilies.setEnabled(false);

            // Clear species combo box
            if (speciesComboBox.getItemCount() > 0)
            {
                speciesComboBox.removeAllItems();
            }

            // Disable level toggle buttons
            treeViewToggleButton.setEnabled(false);
            pedigreeViewToggleButton.setEnabled(false);
            multipleOrganismViewToggleButton.setEnabled(false);
            sexViewToggleButton.setEnabled(false);
            objectPropertiesViewToggleButton.setEnabled(false);
            chromosomeViewToggleButton.setEnabled(false);
            dnaViewToggleButton.setEnabled(false);
        }

        updatingState = false;
    }

    /**
     * React to menu events
    **/
    public void menuSelected(MenuEvent e)
    {
    }

    public void menuDeselected(MenuEvent e)
    {
    }

    public void menuCanceled(MenuEvent e)
    {
    }
}

