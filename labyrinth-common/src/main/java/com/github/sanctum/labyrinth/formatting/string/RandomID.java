package com.github.sanctum.labyrinth.formatting.string;

/**
 * @author Hempfest
 */
public class RandomID {

    private int length;

    private final String assortment;


    public RandomID() {
        this(6);
    }

    /**
     * Generate random IDs.
     *
     * @param length the length of the IDs generated
     */
    public RandomID(int length) {
        this.length = length;
        //noinspection SpellCheckingInspection
        this.assortment = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    }

    /**
     * Generate new random IDs w/ a specified assortment format.
     *
     * @param length the length of the IDs generated
     * @param assortment the format of the IDs
     */
    public RandomID(int length, String assortment) {
        this.length = length;
        this.assortment = assortment;
    }

    // TODO: upgrade to ThreadLocalRandom
    /**
     * Generate a random ID.
     *
     * @return a random ID
     */
    public String generate() {
        StringBuilder builder = new StringBuilder();
        while (length-- != 0) {
            int character = (int) (Math.random() * assortment.length());
            builder.append(assortment.charAt(character));
        }
        return builder.toString();
    }


}
