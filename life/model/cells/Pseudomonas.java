package life.model.cells;

import java.awt.Color;
import java.util.List;

import life.model.Cell;
import life.simulator.Field;
import life.simulator.Location;

/**
 * This class represents a Pseudomonas cell in the simulation.
 * This class extends the Cell class and implements the act method called every
 * generation in the simulation.
 * 
 * @author Ahmet Kucuk, Kota Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class Pseudomonas extends Cell {
    public static final int MIN_NEIGHBOUR_TO_SURVIVE = 2;
    public static final int MAX_NEIGHBOUR_TO_SURVIVE = 3;
    public static final int NEIGHBOUR_TO_REBIRTH = 3;

    /**
     * Constructor for the Pseudomonas cell.
     * 
     * @param field     the field that the cell belongs to
     * @param location  the location of the cell on the field
     * @param col       the color of the cell
     * @param maxEnergy the maximum energy that the cell can have
     */
    public Pseudomonas(Field field, Location location, Color col, int maxEnergy) {
        super(field, location, col, maxEnergy);
    }

    /**
     * The act method defines the behavior of the Pseudomonas cell.
     */
    public void act() {

        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
        int numberOfLivingNeighbours = neighbours.size();

        setEnergyLeft(getEnergyLeft() - 1);

        if (getEnergyLeft() == 0 || numberOfLivingNeighbours == 0) {
            setStateEnergyColorAndDisease(false, getMaxEnergy(), null, false);
            return;
        }

        Color mostCommonColor = getField().getMostCommonNeighbourColour(getField().getMostCommonNeighbour(neighbours));
        // If the cell has too many / little neighbors, or its energy level is zero, set its next state to dead
        if (numberOfLivingNeighbours < MIN_NEIGHBOUR_TO_SURVIVE
                || numberOfLivingNeighbours > MAX_NEIGHBOUR_TO_SURVIVE) {
            setStateEnergyColorAndDisease(false, getMaxEnergy(), null, false);
            return;
        }

        if (numberOfLivingNeighbours == NEIGHBOUR_TO_REBIRTH) {
            setStateEnergyColorAndDisease(true, getEnergyLeft(), mostCommonColor, hasPlague());
        } else {
            setStateEnergyColorAndDisease(isAlive(), getEnergyLeft(), mostCommonColor, hasPlague());
        }
        catchAndApplyPlague(neighbours);
    }
}