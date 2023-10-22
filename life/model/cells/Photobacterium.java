package life.model.cells;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import life.utils.Randomizer;
import life.model.Cell;
import life.simulator.Field;
import life.simulator.Location;

/**
 * This class represents a Photobacterium cell in the simulation.
 * This class extends the Cell class and implements the act method called every
 * generation in the simulation.
 * 
 * @author Ahmet Kucuk, Kota Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class Photobacterium extends Cell {
    private boolean neighbouringMyco;

    // Constants
    public static final int MIN_NEIGHBOUR_TO_SURVIVE = 1;
    public static final int MAX_NEIGHBOUR_TO_SURVIVE = 2;
    public static final int NEIGHBOUR_TO_REBIRTH = 2;

    /**
     * Constructor for Photobacterium.
     * 
     * @param field    The field in which this cell exists.
     * @param location The location of this cell in the field.
     */
    public Photobacterium(Field field, Location location) {
        super(field, location, null, 0);
        Random rand = Randomizer.getRandom();
        setColor(new Color(rand.nextInt(MAX_RGB), rand.nextInt(MAX_RGB), rand.nextInt(MAX_RGB)));
    }

    /**
     * Method called every generation that determines cells state in the next gen
     * based on the rule set of its most common type of neighbor
     */
    public void act() {
        // Get a list of this cell's living neighbors.
        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
        int numberOfLivingNeighbours = neighbours.size();
        // If there are no living neighbors, this cell will die.
        if (numberOfLivingNeighbours == 0) {
            setNextState(false);
            setPlagued(false);
            return;
        }
        // Determine the type of neighbor that appears most often.
        String mostCommonNeighbour = getField().getMostCommonNeighbour(neighbours).getClass().getSimpleName();
        // Act differently depending on the type of most common neighbor.
        switch (mostCommonNeighbour) {
            case "Photobacterium":
                rulesBasedOnNeighbours(MIN_NEIGHBOUR_TO_SURVIVE, MAX_NEIGHBOUR_TO_SURVIVE,
                        NEIGHBOUR_TO_REBIRTH, numberOfLivingNeighbours);
                break;
            case "Mycoplasma":
                rulesBasedOnNeighbours(Mycoplasma.MIN_NEIGHBOUR_TO_SURVIVE, Mycoplasma.MAX_NEIGHBOUR_TO_SURVIVE,
                        Mycoplasma.NEIGHBOUR_TO_REBIRTH, numberOfLivingNeighbours);
                break;
            case "Pseudomonas":
                rulesBasedOnNeighbours(Pseudomonas.MIN_NEIGHBOUR_TO_SURVIVE, Pseudomonas.MAX_NEIGHBOUR_TO_SURVIVE,
                        Pseudomonas.NEIGHBOUR_TO_REBIRTH, numberOfLivingNeighbours);
                break;
            case "Escherichia":
                rulesBasedOnNeighbours(Escherichia.MIN_NEIGHBOUR_TO_SURVIVE, Escherichia.MAX_NEIGHBOUR_TO_SURVIVE,
                        Escherichia.NEIGHBOUR_TO_REBIRTH, numberOfLivingNeighbours);
                break;
        }
        // Set the cell's color if it is alive
        setColorIfAlive();

        // Check if this cell is neighboring a Mycoplasma cell.
        avoidPlagueIfNeighbourMyco(neighbours);

        // If this cell is not neighboring a Mycoplasma cell, check if it should catch
        // and apply the plague.
        if (!neighbouringMyco) {
            catchAndApplyPlague(neighbours);
        }
    }

    /**
     * Handles the rules for a cell based on its most common neighbor
     * 
     * @param minNeighbor              the minimum number of neighbors required to
     *                                 stay alive
     * @param maxNeighbor              the maximum number of neighbors required to
     *                                 stay alive
     * @param rebirthNeighbor          the number of neighbours required to become
     *                                 alive
     * @param numberOfLivingNeighbours the number of living neighbors of the photo
     *                                 cell
     */
    private void rulesBasedOnNeighbours(int minNeighbor, int maxNeighbor, int rebirthNeighbor,
            int numberOfLivingNeighbours) {
        if (numberOfLivingNeighbours < minNeighbor
                || numberOfLivingNeighbours > maxNeighbor) {
            setStateEnergyColorAndDisease(false, 0, getColor(), false);
        } else if (numberOfLivingNeighbours == rebirthNeighbor) {
            setNextState(true);
        } else {
            setNextState(isAlive());
        }
    }

    /**
     * Sets a random color to the cell if it is alive.
     */
    private void setColorIfAlive() {
        if (isAlive()) {
            Random random = Randomizer.getRandom();
            setColor(new Color(random.nextInt(MAX_RGB), random.nextInt(MAX_RGB), random.nextInt(MAX_RGB)));
        }
    }

    /**
     * Sets the flag to true if the cell has a neighboring mycoplasma cell,
     * otherwise false.
     * 
     * @param neighbours a list of living neighboring cells
     */
    private void avoidPlagueIfNeighbourMyco(List<Cell> neighbours) {
        neighbouringMyco = isNeighbouringMyco(neighbours);
    }

    /**
     * Determines whether the cell has a neighboring mycoplasma cell.
     * 
     * @param neighbours a list of living neighboring cells
     */
    public boolean isNeighbouringMyco(List<Cell> neighbours) {
        for (Cell neighbour : neighbours) {
            if (neighbour instanceof Mycoplasma) {
                return true;
            }
        }
        return false;
    }

}
