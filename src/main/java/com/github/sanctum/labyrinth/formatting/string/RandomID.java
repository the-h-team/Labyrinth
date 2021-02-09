package com.github.sanctum.labyrinth.formatting.string;

public class RandomID {

    private int length;

    private final String assortment;

    /**
     * Generate a new random ID
     *
     * @param length              The length of the ID
     */
    public RandomID(int length) {
        this.length = length;
        this.assortment = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    }

    /**
     * Generate a new random ID w/ a specified assortment format
     *
     * @param length              The length of the ID
     * @param assortment                The format of the ID
     */
    public RandomID(int length, String assortment) {
        this.length = length;
        this.assortment = assortment;
    }

    /**
     * Generate the ID
     */
    public String generate() {
        String ALPHA_NUMERIC_STRING = assortment;
        StringBuilder builder = new StringBuilder();
        while (length-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


}
