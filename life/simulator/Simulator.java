package life.simulator;

import java.util.List;
import life.utils.Randomizer;
import java.util.ArrayList;

import life.view.SimulatorView;
import life.model.Cell;
import life.model.CellFactory;

/**
 * This project simulates the behaviour of cells in a 2-dimensional grid, where
 * each cell type has a certain rule set. The rules are based on the neighbors
 * (the living cells directly adjacent to the cell). The cells interact with
 * each other according to these predefined rules and change their state
 * according to these interactions. The field remains populated with the same cells, 
 * however these cells can switch between 2 states: dead and alive. This simulation provides a
 * way to study and observe the behaviour of a large number of cells over time.
 * 
 * @author David J. Barnes, Michael Kölling, Jeffery Raphael, Ahmet Kucuk, Kota
 *         Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class Simulator {
  // Default field dimensions.
  private static final int DEFAULT_NUM_COLUMNS = 100;
  private static final int DEFAULT_NUM_ROWS = 80;

  // List of cells in the field.
  private List<Cell> cells;

  // The current state of the field.
  private Field field;

  // The current generation of the simulation.
  private int generation;

  // A graphical view of the simulation.
  private SimulatorView view;

  // The delay between generations in milliseconds.
  private int delay = 50;

  // Flag indicating whether the simulation is running.
  private boolean isRunning;

  // The thread for running the simulation.
  private Thread simulationThread;

  /**
   * Execute simulation
   */
  public static void main(String[] args) {
    Simulator sim = new Simulator();
    sim.stopSimulation();
  }

  /**
   * Creates a new Simulator object with default dimensions when no arguments are
   * provided.
   */
  public Simulator() {
    this(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLUMNS);
  }

  /**
   * Creates a new Simulator object specific dimensions.
   *
   * @param numRows    The number of rows in the field.
   * @param numColumns The number of columns in the field.
   * @throws IllegalArgumentException If the dimensions are less than or equal to
   *                                  zero.
   */
  public Simulator(int numRows, int numColumns) {
    if (numRows <= 0 || numColumns <= 0) {
      throw new IllegalArgumentException("Dimensions must be greater than zero.");
    }

    cells = new ArrayList<>();
    field = new Field(numRows, numColumns);

    // Create a view of the state of each location in the field.
    view = new SimulatorView(numRows, numColumns, this);

    // Create a new CellFactory and populate the field with cells.
    CellFactory cellFactory = new CellFactory(Randomizer.getRandom());
    populate(cellFactory);

    // Display the initial status.
    view.showStatus(generation, field);
  }

  /**
   * Starts simulation by creating a new thread that runs a while loop until the
   * simulation is stopped or the field becomes unviable.
   */
  public void startSimulation() {
    isRunning = true;
    simulationThread = new Thread(() -> {
      while (isRunning && view.isViable(field)) {
        simOneGeneration();
        delay(delay);
      }
    });
    simulationThread.start();
  }

  /**
   * Stops simulation by interrupting the thread.
   */
  public void stopSimulation() {
    isRunning = false;
    if (simulationThread != null) {
      simulationThread.interrupt();
      simulationThread = null;
    }
  }

  /**
   * Generate random simulation by populating the field with random cells.
   */
  public void generateRandomSimulation() {
    CellFactory cellFactory = new CellFactory(Randomizer.getRandom());
    generation = 0;
    populate(cellFactory);
    view.showStatus(generation, field);
  }

  /**
   * Simulates one generation of cells.
   */
  public void simOneGeneration() {
    generation++;
    cells.forEach(cell -> cell.act());
    cells.forEach(Cell::updateState);
    view.showStatus(generation, field);
  }

  /**
   * Populates field with cells created by a CellFactory.
   * 
   * @param cellFactory
   */
  private void populate(CellFactory cellFactory) {
    field.clear();
    // Loop through each row and column of the field.
    for (int row = 0; row < field.getDepth(); row++) {
      for (int col = 0; col < field.getWidth(); col++) {
        Location location = new Location(row, col);
        // Create new cell using the cell factory and location.
        Cell cell = cellFactory.createCell(field, location);
        if (!cellFactory.isAlive()) {
          cell.setDead();
        }
        cells.add(cell);
      }
    }
  }

  /**
   * Sets the delay time between each generation.
   * 
   * @param delay The time to pause for in milliseconds
   */
  public void setDelay(int delay) {
    this.delay = delay;
  }

  /**
   * Pauses the simulation thread for the given number of milliseconds
   * 
   * @param millisec The time to pause for in milliseconds
   */
  private void delay(int millisec) {
    try {
      Thread.sleep(millisec);
    } catch (InterruptedException ie) {
      // wake up
    }
  }
}
