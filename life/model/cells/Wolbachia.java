package life.model.cells;

import java.awt.Color;
import java.util.Random;

import life.model.Cell;
import life.simulator.Field;
import life.simulator.Location;
import life.utils.Randomizer;

import java.util.List;

/**
 * This class represents a Wolbachia cell in the simulation.
 * This class extends the Cell class and implements the act method called every
 * generation in the simulation.
 * 
 * @author Ahmet Kucuk, Kota Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class Wolbachia extends Cell {
    // The probabilities for different events
    private static final double PROB_TO_DIE = 0.8;
    private static final double PROB_TO_COME_ALIVE = 0.1;
    private static final double PROB_TO_HAVE_PLAGUE = 0.2;

    /**
     * Constructor for creating a Wolbachia cell.
     * 
     * @param field     The field in which the cell is located.
     * @param location  The location of the cell in the field.
     * @param col       The color of the cell.
     * @param maxEnergy The maximum energy that the cell can have.
     */
    public Wolbachia(Field field, Location location, Color col, int maxEnergy) {
        super(field, location, col, maxEnergy);
    }

    /**
     * The act method defines the behavior of the Wolbachia cell.
     */
    public void act() {

        setEnergyLeft(getEnergyLeft() - 1);

        Random rand = Randomizer.getRandom();
        double randomVal = rand.nextDouble();

        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
        int numberOfLivingNeighbours = neighbours.size();

        if (isAlive()) {
            // Check if the cell dies due to lack of energy or probability or no neighbors
            if (numberOfLivingNeighbours == 0 || randomVal < PROB_TO_DIE || getEnergyLeft() == 0) {
                setStateEnergyColorAndDisease(false, getMaxEnergy(), getColor(), false);
                return;
            }
        } else {
            // Check if the cell comes alive due to probability and living neighbours
            boolean becomeAlive = numberOfLivingNeighbours != 0 && randomVal < PROB_TO_COME_ALIVE;
            setStateEnergyColorAndDisease(becomeAlive, getEnergyLeft(), getColor(),
                    becomeAlive && rand.nextDouble() < PROB_TO_HAVE_PLAGUE);
        }
    }


    
}
