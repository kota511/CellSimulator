package life.model;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import life.simulator.Field;
import life.simulator.Location;
import life.utils.Randomizer;

/**
 * A class representing the shared characteristics of all forms of life
 *
 * @author David J. Barnes, Michael Kölling, Jeffery Raphael, Ahmet Kucuk, Kota
 *         Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public abstract class Cell {
  // Cell's properties
  private boolean alive;
  private boolean nextAlive;
  private Field field;
  private Location location;
  private Color color;
  private boolean hasPlague;
  private int energyLeft;
  private final int maxEnergy;

  // Constants
  public static final double CATCH_PLAGUE_PROBABILITY = 0.2;
  public static final int MAX_ENERGY_AFTER_PLAGUE = 3;
  public static final int MAX_RGB = 255;

  /**
   * Constructs a Cell object
   * 
   * @param field     the Field the Cell belongs to
   * @param location  the Location of the Cell
   * @param color     the color of the Cell
   * @param maxEnergy the maximum energy of the Cell
   */
  public Cell(Field field, Location location, Color color, int maxEnergy) {
    this.alive = true;
    this.nextAlive = false;
    this.energyLeft = maxEnergy;
    this.field = field;
    this.maxEnergy = maxEnergy;
    setLocation(location);
    setColor(color);
  }

  /**
   * Make this cell act - that is: the cell decides it's status in the
   * next generation.
   */
  public abstract void act();

  /**
   * Check whether the cell is alive or not.
   * 
   * @return true if the cell is still alive.
   */
  public boolean isAlive() {
    return alive;
  }

  /**
   * Indicate that the cell is no longer alive.
   */
  public void setDead() {
    alive = false;
  }

  /**
   * Sets the next state of the Cell
   * 
   * @param value the boolean value of the next state
   */
  public void setNextState(boolean value) {
    nextAlive = value;
  }

  /**
   * Returns the next state of the Cell
   * 
   * @return true if the next state is alive
   */
  public boolean getNextState() {
    return nextAlive;
  }

  /**
   * Changes the state of the cell
   */
  public void updateState() {
    alive = nextAlive;
  }

  /**
   * Sets the color of the Cell
   * 
   * @param col the Color
   */
  public void setColor(Color col) {
    color = col;
  }

  /**
   * Returns the color of the Cell
   * 
   * @return the Color
   */
  public Color getColor() {
    return color;
  }

  /**
   * Return the cell's location.
   * 
   * @return The cell's location.
   */
  protected Location getLocation() {
    return location;
  }

  /**
   * Place the cell at the new location in the given field.
   * 
   * @param location The cell's location.
   */
  protected void setLocation(Location location) {
    this.location = location;
    field.place(this, location);
  }

  /**
   * Return the cell's field.
   * 
   * @return The cell's field.
   */
  protected Field getField() {
    return field;
  }

  /**
   * Returns the maximum energy of the cell
   * 
   * @return the maximum energy
   */
  public int getMaxEnergy() {
    return maxEnergy;
  }

  /**
   * Returns the energy left of the cell
   * 
   * @return the energy left
   */
  public int getEnergyLeft() {
    return energyLeft;
  }

  /**
   * Sets the energy left for this cell.
   * 
   * @param energyLeft the new energy left for this cell
   */
  public void setEnergyLeft(int energyLeft) {
    this.energyLeft = energyLeft;
  }

  /**
   * Checks whether this cell has the plague.
   * 
   * @return true if this cell has the plague
   */
  public boolean hasPlague() {
    return hasPlague;
  }

  /**
   * Sets whether this cell has the plague.
   * 
   * @param hasPlague true if this cell has the plague
   */
  public void setPlagued(boolean hasPlague) {
    this.hasPlague = hasPlague;
  }

  /**
   * Checks whether any neighbouring cells are infected with the plague.
   * 
   * @param neighbours a list of neighbouring cells to check
   * @return true if any neighbouring cell is infected with the plague
   */
  public boolean isNeighbourInfected(List<Cell> neighbours) {
    for (Cell neighbour : neighbours) {
      if (neighbour.hasPlague()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Apply the plague to the cell if the random number is below a certain
   * threshold.
   * 
   * @param neighbours a list of neighbouring cells to check for the plague
   */
  public void catchAndApplyPlague(List<Cell> neighbours) {
    Random random = Randomizer.getRandom();
    if (canCatchPlague() && random.nextDouble() < CATCH_PLAGUE_PROBABILITY) {
      setPlagued(isNeighbourInfected(neighbours));
    }
    applyEffectsOfPlague();
  }

  /**
   * Applies the effects of the plague to this cell.
   */
  private void applyEffectsOfPlague() {
    if (hasPlague()) {
      setColor(Color.BLACK);
      if (getEnergyLeft() > MAX_ENERGY_AFTER_PLAGUE) {
        setEnergyLeft(MAX_ENERGY_AFTER_PLAGUE);
      }
    }
  }

  /**
   * Checks whether this cell can catch the plague.
   * 
   * @return true if the cell is alive, not already plagued, and is alive in the
   *         next generation
   */
  private boolean canCatchPlague() {
    return isAlive() && getNextState() && !hasPlague();
  }

  /**
   * Sets the state, energy, color and infection status of the cell
   * 
   * @param nextState
   * @param energyLeft
   * @param color
   * @param infected
   */
  public void setStateEnergyColorAndDisease(boolean nextState, int energyLeft, Color color, boolean infected) {
    setNextState(nextState);
    setEnergyLeft(energyLeft);
    setColor(color);
    setPlagued(infected);
  }

}
