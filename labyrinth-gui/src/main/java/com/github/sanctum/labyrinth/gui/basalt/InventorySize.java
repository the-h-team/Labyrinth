package com.github.sanctum.labyrinth.gui.basalt;

public enum InventorySize {

    /**
     * Slots: 9
     */
    ONE(9),

    /**
     * Slots: 18
     */
    TWO(18),

    /**
     * Slots: 27
     */
    THREE(27),

    /**
     * Slots: 36
     */
    FOUR(36),

    /**
     * Slots: 45
     */
    FIVE(45),

    /**
     * Slots: 54
     */
    SIX(54);

    private final int slots;

    InventorySize(int slots) {
        this.slots = slots;
    }

    /**
     * @return The size of the inventory.
     */
    public int getSize() {
        return slots;
    }

    public int[] getSlots(InventoryPane pane) {
        return pane.get(getSize());
    }

    public static InventorySize valueOf(int size) {
        switch (size) {
            case 9:
                return ONE;
            case 18:
                return TWO;
            case 27:
                return THREE;
            case 36:
                return FOUR;
            case 45:
                return FIVE;
            case 54:
                return SIX;
        }
        return InventorySize.ONE;
    }

}
