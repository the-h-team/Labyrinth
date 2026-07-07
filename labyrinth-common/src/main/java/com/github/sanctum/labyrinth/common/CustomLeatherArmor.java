package com.github.sanctum.labyrinth.common;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to easily color and customize a leather armor piece of choice.
 */
public class CustomLeatherArmor {

    private final ItemStack BASE;
    private String TITLE;
    private Color COLOR;
    private List<String> LORE;

    protected CustomLeatherArmor(@NotNull Piece piece) {
        switch (piece) {
            case HEAD:
                this.BASE = new ItemStack(Material.LEATHER_HELMET);
                break;
            case TORSO:
                this.BASE = new ItemStack(Material.LEATHER_CHESTPLATE);
                break;
            case LEGS:
                this.BASE = new ItemStack(Material.LEATHER_LEGGINGS);
                break;
            case FEET:
                this.BASE = new ItemStack(Material.LEATHER_BOOTS);
                break;
            default:
                throw new IllegalArgumentException(piece + " is not a valid armor piece.");
        }
    }

    /**
     * Select a piece of armor to start customizing!
     *
     * @param piece the piece of armor to customize
     * @return a new ColoredArmor instance
     */
    public static CustomLeatherArmor select(@NotNull Piece piece) {
        return new CustomLeatherArmor(piece);
    }

    /**
     * Customize an entire set of leather armor.
     *
     * @return a new ColoredArmor instance
     */
    public static CustomLeatherArmor prepare() {
        return new CustomLeatherArmor(Piece.HEAD);
    }

    /**
     * Change the title of this armor piece.
     * <p>
     * <strong>Automatically color translated.</strong>
     *
     * @param text the title of the item
     * @return this builder
     */
    public CustomLeatherArmor setTitle(@NotNull String text) {
        this.TITLE = StringUtils.use(text).translate();
        return this;
    }

    /**
     * Select a color for this armor piece.
     *
     * @param color a {@link Color} of choice
     * @return this builder
     */
    public CustomLeatherArmor setColor(@NotNull Color color) {
        this.COLOR = color;
        return this;
    }

    /**
     * Select a color for this armor piece via hexadecimal.
     * <em>(Ex. 0x0000f)</em>
     *
     * @param rgb the hex RGB value to use
     * @return this builder
     */
    public CustomLeatherArmor setColor(int rgb) {
        this.COLOR = Color.fromRGB(rgb);
        return this;
    }

    /**
     * Select a color for this armor piece via RGB format.
     *
     * @param red   the red value to use
     * @param green the green value to use
     * @param blue  the blue value to use
     * @return this builder
     */
    public CustomLeatherArmor setColor(int red, int green, int blue) {
        this.COLOR = Color.fromRGB(red, green, blue);
        return this;
    }

    /**
     * Add some lore to this armor piece.
     * <p>
     * <strong>Automatically color translated.</strong>
     *
     * @param text the line(s) of lore to add to this armor piece
     * @return this builder
     */
    public CustomLeatherArmor setLore(@NotNull String... text) {
        List<String> list = new LinkedList<>();
        for (String t : text) {
            list.add(StringUtils.use(t).translate());
        }
        this.LORE = list;
        return this;
    }

    /**
     * Build an entire set of armor based off your options.
     *
     * @return the finished armor set, fully customized
     */
    public ItemStack[] buildAll() {
        List<ItemStack> i = new LinkedList<>(Arrays.asList(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS)));
        for (ItemStack it : i) {
            if (it.getItemMeta() instanceof LeatherArmorMeta) {
                LeatherArmorMeta meta = (LeatherArmorMeta) it.getItemMeta();
                if (this.COLOR != null) {
                    meta.setColor(this.COLOR);
                }
                if (this.LORE != null && !this.LORE.isEmpty()) {
                    meta.setLore(this.LORE);
                }
                if (this.TITLE != null) {
                    meta.setDisplayName(this.TITLE);
                }
                it.setItemMeta(meta);
            } else
                throw new IllegalStateException("An invalid item type was found present, yell at the devs!!");
        }
        return i.toArray(new ItemStack[0]);
    }

    /**
     * Build your piece of armor! Stare at yourself in awe in the mirror!
     *
     * @return the finished armor piece, fully customized
     */
    public ItemStack build() {
        if (this.BASE.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) this.BASE.getItemMeta();
            if (this.COLOR != null) {
                meta.setColor(this.COLOR);
            }
            if (this.LORE != null && !this.LORE.isEmpty()) {
                meta.setLore(this.LORE);
            }
            if (this.TITLE != null) {
                meta.setDisplayName(this.TITLE);
            }
            this.BASE.setItemMeta(meta);
            return this.BASE;
        } else
            throw new IllegalStateException("An invalid item type was found present, yell at the devs!!");
    }

    /**
     * An enum encapsulating bodily positioning.
     */
    public enum Piece {
        HEAD, TORSO, LEGS, FEET
    }

}
