package io.github.sanctum.labyrinth.loci.block;

import io.github.sanctum.labyrinth.loci.location.PositionLike;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a block's position.
 * <p>
 * Values may be absolute or relative. See {@link #isRelative()}.
 *
 * @since 1.9.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface BlockPosition extends PositionLike {
    /**
     * Gets the block {@code x} coordinate.
     *
     * @return the block {@code x} coordinate
     */
    int getX();

    /**
     * Gets the block {@code y} coordinate.
     *
     * @return the block {@code y} coordinate
     */
    int getY();

    /**
     * Gets the block {@code z} coordinate.
     *
     * @return the block {@code z} coordinate
     */
    int getZ();

    /**
     * Indicates whether this block position is relative.
     *
     * @return true if relative
     */
    boolean isRelative();

    /**
     * Gets a block position with the given absolute coordinates.
     *
     * @param x the block {@code x} coordinate
     * @param y the block {@code y} coordinate
     * @param z the block {@code z} coordinate
     * @return a block position with the given absolute coordinates
     */
    static BlockPosition absolute(int x, int y, int z) {
        return new BlockPositionImpl(x, y, z, false);
    }

    /**
     * Gets an arbitrary block position with the given coordinates.
     *
     * @param x the block {@code x} coordinate
     * @param y the block {@code y} coordinate
     * @param z the block {@code z} coordinate
     * @param relative whether the coordinates are relative
     * @return an arbitrary block position with the given coordinates
     */
    static BlockPosition arbitrary(int x, int y, int z, boolean relative) {
        return new BlockPositionImpl(x, y, z, relative);
    }

    // TODO builder?
}
