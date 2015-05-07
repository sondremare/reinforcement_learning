/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flatland;

/**
 *
 * @author Linnk
 */
public class Cell {
    private Type type;
    private int value;

    public Cell(Type type, int value) {
        this.type = type;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public Cell clone() {
        return new Cell(this.getType(), this.getValue());
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public enum Type {
        Food, Poison, Nothing
    }
}
