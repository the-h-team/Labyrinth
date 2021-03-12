package com.github.sanctum.labyrinth.gui.builder;

/**
 * Helpful enum to define inventory size with.
 */
public enum InventoryRows {
        ONE(9),
        TWO(18),
        THREE(27),
        FOUR(36),
        FIVE(45),
        SIX(54);

        /**
         * Number of slots in an Inventory of these rows.
         */
        private final int slotCount;

        InventoryRows(int slots) {
            this.slotCount = slots;
        }

        public int getSlotCount() {
                return slotCount;
        }
}