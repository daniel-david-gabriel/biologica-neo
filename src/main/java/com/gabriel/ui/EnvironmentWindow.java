package com.gabriel.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.concord.biologica.engine.Environment;
import org.concord.biologica.engine.PathStrings;
import org.concord.biologica.engine.TerrainEng;
import org.concord.biologica.engine.Trait;
import org.concord.biologica.engine.World;
import org.concord.biologica.ui.EnvironmentView;

public final class EnvironmentWindow extends JFrame {
	private static final int WINDOW_WIDTH = 801;

	private static final int WINDOW_HEIGHT = 664;

	private static final int SIMULATION_WIDTH = 601;

	private static final int SIMULATION_HEIGHT = 600;

	private static final int INFO_WIDTH = 200;

	private static final int INFO_HEIGHT = 600;

	private static final int CONTROLS_WIDTH = 800;

	private static final int CONTROLS_HEIGHT = 30;

	// Default initial values for the simulation
	private static final int INIT_MALES = 100;

	private static final int INIT_FEMALES = 100;

	private static final int MAX_MALES = 10000;

	private static final int MAX_FEMALES = 10000;

	private Boolean canResume = false;

	private static EnvironmentView environmentView;

	private World currentWorld = null;

	private JTextArea worldInfoText = new JTextArea();

	private JTextArea environmentInfoText = new JTextArea();

	// Used to update the Environment Info, once every second
	private Timer infoUpdateTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateEnvironmentInfoText();
		}
	});

	private Vector<String> environmentNames = new Vector<String>();

	private Vector<String> traitNames = new Vector<String>();

	private JComboBox<String> traitComboBox = new JComboBox(traitNames);

	private JButton startButton = new JButton("Start");

	private JButton stopButton = new JButton("Stop");

	private JButton resetButton = new JButton("Reset");

	private NumberFormat numberFormat = NumberFormat.getIntegerInstance();

	private JFormattedTextField initMaleOrganisms = new JFormattedTextField(numberFormat);

	private JFormattedTextField initFemaleOrganisms = new JFormattedTextField(numberFormat);

	public EnvironmentWindow() {
		super("Simulation Mode");

		setBackground(Color.lightGray);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		// setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				infoUpdateTimer.stop();
				environmentView.stop();
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});

		environmentView = new EnvironmentView();
		environmentView.setPreferredSize(new Dimension(SIMULATION_WIDTH, SIMULATION_HEIGHT));

		Box environmentWindowBox = Box.createVerticalBox();
		environmentWindowBox.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

		Box simulationInfoBox = Box.createHorizontalBox();
		simulationInfoBox.setPreferredSize(new Dimension(WINDOW_WIDTH, SIMULATION_HEIGHT));

		simulationInfoBox.add(environmentView);

		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

		worldInfoText.setEditable(false);
		worldInfoText.setLineWrap(true);
		worldInfoText.setWrapStyleWord(true);
		info.add(worldInfoText);

		JPanel environmentSelectorPanel = new JPanel();
		environmentSelectorPanel.setLayout(new GridLayout(1, 2));
		environmentSelectorPanel.add(new JLabel("Environment:"));

		JComboBox<String> environmentsComboBox = new JComboBox(environmentNames);
		environmentsComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent itemEvent) {
				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
					String selectedEnvironment = itemEvent.getItem().toString();

					setEnvironment(selectedEnvironment);
					updateEnvironmentInfoText();
					startButton.setEnabled(true);
					stopButton.setEnabled(true);
					resetButton.setEnabled(true);
				}

			}
		});
		environmentSelectorPanel.add(environmentsComboBox);

		info.add(environmentSelectorPanel);

		JPanel traitSelectorPanel = new JPanel();
		traitSelectorPanel.setLayout(new GridLayout(1, 2));
		traitSelectorPanel.add(new JLabel("Trait:"));

		traitComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent itemEvent) {
				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
					String selectedTrait = itemEvent.getItem().toString();

					setTrait(selectedTrait);
				}

			}
		});
		traitSelectorPanel.add(traitComboBox);

		info.add(traitSelectorPanel);

		JPanel environmentInfoPanel = new JPanel();
		environmentInfoPanel.add(environmentInfoText);
		environmentInfoPanel.setPreferredSize(new Dimension(INFO_WIDTH, INFO_HEIGHT));
		environmentInfoText.setAlignmentX(LEFT_ALIGNMENT);
		environmentInfoText.setAlignmentY(TOP_ALIGNMENT);
		environmentInfoText.setEditable(false);

		info.add(environmentInfoPanel);

		info.add(new Box.Filler(new Dimension(10, 10), new Dimension(10, 500), new Dimension(10, 500)));

		info.setPreferredSize(new Dimension(INFO_WIDTH, INFO_HEIGHT));
		simulationInfoBox.add(info);

		environmentWindowBox.add(simulationInfoBox);

		JToolBar controls = createControlsToolbar();
		controls.setPreferredSize(new Dimension(CONTROLS_WIDTH, CONTROLS_HEIGHT));
		Box controlsBox = Box.createHorizontalBox();
		controlsBox.add(controls);
		controlsBox.add(new Box.Filler(new Dimension(10, 10), new Dimension(700, 10), new Dimension(700, 10)));

		environmentWindowBox.add(controlsBox);

		add(environmentWindowBox);

		setVisible(true);
		repaint();
	}

	private JToolBar createControlsToolbar() {
		JToolBar controls = new JToolBar(JToolBar.HORIZONTAL);
		controls.setFloatable(false);

		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!canResume) {
					setEnvironmentOrganisms();
					canResume = true;
				}
				environmentView.start();
				infoUpdateTimer.start();
			}
		});
		startButton.setEnabled(false);
		controls.add(startButton);

		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				environmentView.stop();
				infoUpdateTimer.stop();
			}
		});
		stopButton.setEnabled(false);
		controls.add(stopButton);

		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				environmentView.reset();
				infoUpdateTimer.stop();
				updateEnvironmentInfoText();
				canResume = false;
			}
		});
		resetButton.setEnabled(false);
		controls.add(resetButton);

		controls.add(new JLabel("Starting Males: "));
		initMaleOrganisms.setValue(INIT_MALES);
		initMaleOrganisms.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				// Can't cast from a Long to an Integer in Java 1.6!!
				int value = ((Long) initMaleOrganisms.getValue()).intValue();
				if (value < 1) {
					initMaleOrganisms.setValue(1);
				}
				if (value > MAX_MALES) {
					initMaleOrganisms.setValue(MAX_MALES);
				}

			}
		});
		controls.add(initMaleOrganisms);

		controls.add(new JLabel("Starting Females: "));
		initFemaleOrganisms.setValue(INIT_FEMALES);
		initFemaleOrganisms.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				// Can't cast from a Long to an Integer in Java 1.6!!
				int value = ((Long) initFemaleOrganisms.getValue()).intValue();
				if (value < 1) {
					initFemaleOrganisms.setValue(1);
				}
				if (value > MAX_FEMALES) {
					initFemaleOrganisms.setValue(MAX_FEMALES);
				}

			}
		});
		controls.add(initFemaleOrganisms);

		return controls;
	}

	public void setWorld(World world) {
		currentWorld = world;

		worldInfoText.setText("World: " + currentWorld.getFile().getPath().toString());

		environmentNames.clear();
		Iterator environmentIterator = currentWorld.getEnvironments().asIterator();
		while (environmentIterator.hasNext()) {
			Environment environment = (Environment) environmentIterator.next();
			environmentNames.add(environment.getName());
		}
		if (environmentNames.size() < 1) {
			return;
		}

		traitNames.clear();
		Iterator traitIterator = currentWorld.getCurrentSpecies().getTraits().asIterator();
		while (traitIterator.hasNext()) {
			Trait trait = (Trait) traitIterator.next();
			traitNames.add(trait.getName());
		}
		if (traitNames.size() < 1) {
			return;
		}
		traitComboBox.setSelectedIndex(0);

		validate();
		repaint();
	}

	public void setEnvironment(String environmentName) {

		Environment environment = null;
		Iterator envIterator = currentWorld.getEnvironments().asIterator();
		while (envIterator.hasNext()) {
			Environment currEnv = (Environment) envIterator.next();
			if (currEnv.getName().equals(environmentName)) {
				environment = currEnv;
			}
		}

		environmentView.setSize(600, 600);
		environmentView.setEnvironment(currentWorld, environment);
	}

	public void setTrait(String traitName) {
		Trait trait = null;

		Iterator traitIterator = currentWorld.getCurrentSpecies().getTraits().asIterator();
		while (traitIterator.hasNext()) {
			Trait currTrait = (Trait) traitIterator.next();
			if (currTrait.getName().equals(traitName)) {
				trait = currTrait;
			}
		}

		environmentView.setTrait(trait);
	}

	public void updateEnvironmentInfoText() {
		TerrainEng[][] currentEnvironment = environmentView.getCurrentEnvironmentTerrain();

		int currentOrganisms = 0; // We tally the organisms from the environment
									// itself instead of using the world's tally
		int totalWater = 0;
		int totalLand = 0;
		int totalSand = 0;
		int totalMountain = 0;
		int totalWaterFood = 0;
		int totalLandFood = 0;
		int totalSandFood = 0;
		int totalMountainFood = 0;

		int width = environmentView.getEnvironment().getWidth();
		int height = environmentView.getEnvironment().getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				TerrainEng currentUnit = currentEnvironment[j][i];

				currentOrganisms += currentUnit.getNumberOfOrganism();
				int currentEnvType = currentUnit.getEnvironment();
				switch (currentEnvType) {
					case TerrainEng.ENVIRONMENT_LAND:
						totalLand++;
						totalLandFood += currentUnit.getFood();
						break;
					case TerrainEng.ENVIRONMENT_WATER:
						totalWater++;
						totalWaterFood += currentUnit.getFood();
						break;
					case TerrainEng.ENVIRONMENT_SAND:
						totalSand++;
						totalSandFood += currentUnit.getFood();
						break;
					case TerrainEng.ENVIRONMENT_MOUNTAIN:
						totalMountain++;
						totalMountainFood += currentUnit.getFood();
						break;
					default:
						System.out.println("Unrecognized TerrainEng type: " + currentEnvType);
						break;
				}
			}
		}

		String environmentText = "Total Organisms: " + currentOrganisms + "\n\n" + "Land: " + totalLand
				+ "\nLand Food: " + totalLandFood + "\n" + "Water: " + totalWater + "\nWater Food: " + totalWaterFood
				+ "\n" + "Sand: " + totalSand + "\nSand Food: " + totalSandFood + "\n" + "Mountain: " + totalMountain
				+ "\nMountain Food: " + totalMountainFood + "\n";

		environmentInfoText.setText(environmentText);
	}

	private void setEnvironmentOrganisms() {
		int maleNumber = 0, femaleNumber = 0;

		Object mNumber = initMaleOrganisms.getValue();
		if (mNumber instanceof Integer) {
			maleNumber = ((Integer) mNumber).intValue();
		} else if (mNumber instanceof Long) {
			maleNumber = ((Long) mNumber).intValue();
		}

		if (maleNumber < 1 || maleNumber > MAX_MALES) {
			maleNumber = INIT_MALES;
		}

		if (maleNumber < 1 || maleNumber > MAX_MALES) {
			maleNumber = INIT_MALES;
		}

		Object fNumber = initMaleOrganisms.getValue();
		if (fNumber instanceof Integer) {
			femaleNumber = ((Integer) fNumber).intValue();
		} else if (fNumber instanceof Long) {
			femaleNumber = ((Long) fNumber).intValue();
		}

		if (femaleNumber < 1 || femaleNumber > MAX_FEMALES) {
			femaleNumber = INIT_FEMALES;
		}

		environmentView.setOrganismNumber(maleNumber, femaleNumber);
	}

	static public void main(String args[]) {
		EnvironmentWindow environmentWindow = new EnvironmentWindow();
		// EXIT_ON_CLOSE standalone mode
		environmentWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		World world = new World(new File(PathStrings.getWorldsDirectory(), "dragon-with-environment.xml"));

		environmentWindow.setWorld(world);
	}

}
