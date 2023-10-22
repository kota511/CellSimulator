package life.model.cells;

import java.awt.Color;
import java.util.List;

import life.model.Cell;
import life.simulator.Field;
import life.simulator.Location;

/**
 * This class represents a Mycoplasma cell in the simulation.
 * This class extends the Cell class and implements the act method called every
 * generation in the simulation.
 * 
 * @author David J. Barnes, Michael Kölling, Jeffery Raphael, Ahmet Kucuk, Kota
 *         Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class Mycoplasma extends Cell {
  public static final int MIN_NEIGHBOUR_TO_SURVIVE = 2;
  public static final int MAX_NEIGHBOUR_TO_SURVIVE = 3;
  public static final int NEIGHBOUR_TO_REBIRTH = 3;
  // Constant for the minimum number of neighboring Photobacterium cells to reset
  // energy
  private static final int MIN_NUM_PHOTOS = 2;

  /**
   * Constructor for the Mycoplasma cell.
   * 
   * @param field     The field in which the cell exists.
   * @param location  The location of the cell in the field.
   * @param col       The color of the cell.
   * @param maxEnergy The maximum energy level of the cell.
   */
  public Mycoplasma(Field field, Location location, Color col, int maxEnergy) {
    super(field, location, col, maxEnergy);
  }

  /**
   * Method called every generation that determines cells state in the next gen
   * based on its living neighbors and its energy level
   */
  public void act() {
    List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
    int numberOfLivingNeighbours = neighbours.size();

    // Check if the cell should reset its energy level based on the presence of
    // neighboring Photobacterium cells
    resetEnergyIfNeighboringPhotos(neighbours);

    setEnergyLeft(getEnergyLeft() - 1);

    // If the cell has too many / little neighbors, or its energy level is zero, set
    // its next state to dead
    if (numberOfLivingNeighbours < MIN_NEIGHBOUR_TO_SURVIVE || numberOfLivingNeighbours > MAX_NEIGHBOUR_TO_SURVIVE
        || getEnergyLeft() == 0) {
      setStateEnergyColorAndDisease(false, getMaxEnergy(), getColor(), false);
      return;
    }
    // If the cell has NEIGHBOUR_TO_REBIRTH neighbors, it will become alive
    if (numberOfLivingNeighbours == NEIGHBOUR_TO_REBIRTH) {
      setNextState(true);
    } else {
      setNextState(isAlive());
    }
  }

  /**
   * Resets the cell's energy level if it has enough neighboring Photobacterium.
   * 
   * @param neighbours A list of the cell's neighbors.
   */
  public void resetEnergyIfNeighboringPhotos(List<Cell> neighbours) {
    int photoCount = 0;
    for (Cell neighbour : neighbours) {
      if (neighbour instanceof Photobacterium) {
        photoCount++;
      }
    }
    if (photoCount >= MIN_NUM_PHOTOS) {
      setEnergyLeft(getMaxEnergy());
    }
  }
}
