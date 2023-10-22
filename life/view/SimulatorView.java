package life.view;

import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import life.simulator.Field;
import life.simulator.FieldStats;
import life.model.Cell;
import life.simulator.Simulator;

/**
 * This class is responsible for the simulator GUI and controls.
 *
 * @author David J. Barnes, Michael Kölling, Jeffery Raphael, Ahmet Kucuk, Kota
 *         Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class SimulatorView extends JFrame {

    // Constants
    private static final Color EMPTY_COLOR = Color.white;
    private final String GENERATION_PREFIX = "Generation: ";
    private final String POPULATION_PREFIX = "Population: ";

    private boolean isSimulationRunning = false;
    private JLabel genLabel, population, infoLabel;
    private FieldView fieldView;
    private FieldStats stats;
    private JButton startButton;
    private JButton randomButton;
    private JButton stepButton;
    private JSlider delaySlider;

    /**
     * Constructs a SimulatorView object.
     *
     * @param height    The height of the field.
     * @param width     The width of the field.
     * @param simulator The simulator object to control.
     */
    public SimulatorView(int height, int width, Simulator simulator) {
        stats = new FieldStats();
        fieldView = new FieldView(height, width);

        // Set up window.
        setTitle("Life Simulation");
        setLocation(100, 50);

        // Set up information panel.
        genLabel = new JLabel(GENERATION_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        delaySlider = new JSlider(0, 1000, 50);
        delaySlider.setBorder(BorderFactory.createTitledBorder("Delay"));

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(genLabel, BorderLayout.WEST);
        infoPane.add(infoLabel, BorderLayout.CENTER);
        infoPane.add(population, BorderLayout.EAST);
        infoPane.add(delaySlider, BorderLayout.SOUTH);

        // Set up the delay slider.
        delaySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int delay = delaySlider.getValue();
                simulator.setDelay(delay);
            }
        });

        // Create the buttons.
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isSimulationRunning) {
                    simulator.startSimulation();
                    startButton.setText("Stop");
                    isSimulationRunning = true;
                } else {
                    simulator.stopSimulation();
                    startButton.setText("Start");
                    isSimulationRunning = false;
                }
            }
        });

        randomButton = new JButton("Randomize");
        randomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isSimulationRunning) {
                    simulator.stopSimulation();
                    startButton.setText("Start");
                    isSimulationRunning = false;
                }
                simulator.generateRandomSimulation();
            }
        });

        stepButton = new JButton("Step");
        stepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isSimulationRunning) {
                    simulator.stopSimulation();
                    startButton.setText("Start");
                    isSimulationRunning = false;
                }
                simulator.simOneGeneration();
            }
        });

        JPanel buttonPane = new JPanel();
        buttonPane.add(startButton);
        buttonPane.add(randomButton);
        buttonPane.add(stepButton);

        Container contents = getContentPane();
        contents.setLayout(new FlowLayout());
        contents.add(infoPane);
        contents.add(fieldView);
        contents.add(buttonPane);
        contents.add(infoPane);
        contents.add(fieldView);
        contents.add(buttonPane);

        pack();
        setVisible(true);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text) {
        infoLabel.setText(text);
    }

    /**
     * Show the current status of the field.
     * 
     * @param generation The current generation.
     * @param field      The field whose status is to be displayed.
     */
    public void showStatus(int generation, Field field) {
        if (!isVisible()) {
            setVisible(true);
        }

        genLabel.setText(GENERATION_PREFIX + generation);
        stats.reset();
        fieldView.preparePaint();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Cell cell = field.getObjectAt(row, col);

                if (cell != null && cell.isAlive()) {
                    stats.incrementCount(cell.getClass());
                    fieldView.drawMark(col, row, cell.getColor());
                } else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }

        stats.countFinished();
        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * 
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field) {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this
     * for your project if you like.
     */
    private class FieldView extends JPanel {
        private final int GRID_VIEW_SCALING_FACTOR = 6;
        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width) {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize() {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                    gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint() {
            if (!size.equals(getSize())) { // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if (xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if (yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g) {
            if (fieldImage != null) {
                Dimension currentSize = getSize();
                if (size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                } else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
