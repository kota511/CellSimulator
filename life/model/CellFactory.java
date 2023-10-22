package life.model;

import java.util.Random;
import java.awt.Color;

import life.model.cells.Escherichia;
import life.model.cells.Mycoplasma;
import life.model.cells.Photobacterium;
import life.model.cells.Pseudomonas;
import life.model.cells.Wolbachia;
import life.simulator.Field;
import life.simulator.Location;

/**
 * This class is responsible for creating new cells for the simulation.
 * It randomly selects a type of cell to create based on probability, and
 * creates a new instance of that cell type with the given field and location.
 * 
 * @author Ahmet Kucuk, Kota Amemiya & Mohammed Fohpa
 * @version 2023.02.16 (2)
 */

public class CellFactory {
    private static final double ALIVE_PROB = 0.15;

    private Random rand;

    /**
     * Constructor that takes a random number generator as input.
     * 
     * @param rand
     */
    public CellFactory(Random rand) {
        this.rand = rand;
    }

    /**
     * Create a random type of cell at the specified location within the given field.
     * 
     * @param field
     * @param location
     * @return cell type with given field and location
     */
    public Cell createCell(Field field, Location location) {
        double randNum = rand.nextDouble();
        CellType cellType = null;

        if (randNum < 0.3) {
            cellType = CellType.MYCOPLASMA;
        } else if (randNum < 0.50) {
            cellType = CellType.ESCHERICHIA;
        } else if (randNum < 0.6) {
            cellType = CellType.PHOTOBACTERIUM;
        } else if (randNum < 0.9) {
            cellType = CellType.PSEUDOMONAS;
        } else {
            cellType = CellType.WOLBACHIA;
        }

        return createCellOfType(cellType, field, location);
    }

    /**
     * Create a new cell of the specified type with the given field and location.
     * @param cellType
     * @param location
     * @param location
     * @return cell with given field and location
     */
    private Cell createCellOfType(CellType cellType, Field field, Location location) {
        switch (cellType) {
            case MYCOPLASMA:
                return new Mycoplasma(field, location, Color.ORANGE, 55);
            case ESCHERICHIA:
                return new Escherichia(field, location, new Color(21, 76, 121), 65);
            case PHOTOBACTERIUM:
                return new Photobacterium(field, location);
            case PSEUDOMONAS:
                return new Pseudomonas(field, location, Color.RED, 75);
            case WOLBACHIA:
                return new Wolbachia(field, location, Color.BLACK, 30);
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }

    /**
     * Returns boolean indicating whether cell is alive.
     */
    public boolean isAlive() {
        return rand.nextDouble() < ALIVE_PROB;
    }
}