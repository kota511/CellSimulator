package life.model.cells;

import java.awt.Color;
import java.util.List;

import life.model.Cell;
import life.simulator.Field;
import life.simulator.Location;

/**
 * This class represents a Escherichia cell in the simulation.
 * This class extends the Cell class and implements the act method called every
 * generation in the simulation.
 * 
 * @author Ahmet Kucuk, Kota Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class Escherichia extends Cell {
    public static final int MIN_NEIGHBOUR_TO_SURVIVE = 2;
    public static final int MAX_NEIGHBOUR_TO_SURVIVE = 4;
    public static final int NEIGHBOUR_TO_REBIRTH = 3;

    private static final Color DEFAULT_COLOR = new Color(21, 76, 121);
    private static final int GEN_TO_CHANGE_SHADE = 5;
    private static final int CHANGE_IN_RGB = 25;

    /**
    * 
    */
    public Escherichia(Field field, Location location, Color color, int maxEnergy) {
        super(field, location, color, maxEnergy);
    }

    /**
     * The act method defines the behavior of the Escherichia cell.
     */
    public void act() {

        setEnergyLeft(getEnergyLeft() - 1);

        List<Cell> neighbours = getField().getLivingNeighbours(getLocation());
        int numberOfLivingNeighbours = neighbours.size();

        if (numberOfLivingNeighbours < MIN_NEIGHBOUR_TO_SURVIVE || numberOfLivingNeighbours > MAX_NEIGHBOUR_TO_SURVIVE
                || getEnergyLeft() == 0) {
            setStateEnergyColorAndDisease(false, getMaxEnergy(), DEFAULT_COLOR, false);
            return;
        }
        // If the cell has NEIGHBOUR_TO_REBIRTH neighbors, it will become alive
        if (numberOfLivingNeighbours == NEIGHBOUR_TO_REBIRTH) {
            setNextState(true);
        } else {
            setNextState(isAlive());
            setNextColor();
        }
        catchAndApplyPlague(neighbours);
    }

    /**
     * Change colour to brighter shade if it survives GEN_TO_CHANGE_SHADE
     * generations
     */
    private void setNextColor() {
        if (getEnergyLeft() % GEN_TO_CHANGE_SHADE == 0) {
            Color colorToUse = (getColor() != null) ? getColor() : DEFAULT_COLOR;
            setColor(calcNextColor(colorToUse));
        }
    }

    /**
     * Calculate the next brighter shade of its current color.
     * 
     * @param currentColor current color of the cell
     * @return the brighter shade of the current color
     */
    private Color calcNextColor(Color currentColor) {
        int red = currentColor.getRed();
        int green = currentColor.getGreen();
        int blue = currentColor.getBlue();
        if (red < MAX_RGB - CHANGE_IN_RGB && green < MAX_RGB - CHANGE_IN_RGB && blue < MAX_RGB - CHANGE_IN_RGB) {
            return new Color(red + CHANGE_IN_RGB, green + CHANGE_IN_RGB, blue + CHANGE_IN_RGB);
        }
        return currentColor;
    }

}