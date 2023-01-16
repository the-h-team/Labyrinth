package io.github.sanctum.labyrinth.loci.block;

import io.github.sanctum.labyrinth.loci.location.PositionLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;

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
     * The minimum absolute {@code x}/{@code z} block coordinate.
     */
    int MIN_XZ = -30_000_000;
    /**
     * The maximum absolute {@code x}/{@code z} block coordinate.
     */
    int MAX_XZ = 30_000_000;
    /**
     * The minimum absolute {@code y} block coordinate.
     */
    int MIN_Y = -2_032;
    /**
     * The maximum absolute {@code y} block coordinate.
     */
    int MAX_Y = 2_032;

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
    static BlockPosition absolute(@Range(from = MIN_XZ, to = MAX_XZ) int x,
                                  @Range(from = MIN_Y, to = MAX_Y) int y,
                                  @Range(from = MIN_XZ, to = MAX_XZ) int z) {
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
    /**
     * Build a block position incrementally.
     *
     * @since 1.9.0
     */
    @ApiStatus.NonExtendable
    class Builder {
        int x, y, z;
        boolean relative;

        Builder() {}

        /**
         * Gets the current block {@code x} coordinate.
         *
         * @return the current block {@code x} coordinate
         */
        public int getX() {
            return x;
        }

        /**
         * Sets the block {@code x} coordinate.
         *
         * @param x the block {@code x} coordinate
         * @return this builder
         */
        public Builder setX(int x) {
            this.x = x;
            return this;
        }

        /**
         * Gets the current block {@code y} coordinate.
         *
         * @return the current block {@code y} coordinate
         */
        public int getY() {
            return y;
        }

        /**
         * Sets the block {@code y} coordinate.
         *
         * @param y the block {@code y} coordinate
         * @return this builder
         */
        public Builder setY(int y) {
            this.y = y;
            return this;
        }

        /**
         * Gets the current block {@code z} coordinate.
         *
         * @return the current block {@code z} coordinate
         */
        public int getZ() {
            return z;
        }

        /**
         * Sets the block {@code z} coordinate.
         *
         * @param z the block {@code z} coordinate
         * @return this builder
         */
        public Builder setZ(int z) {
            this.z = z;
            return this;
        }

        /**
         * Indicates whether the block coordinates are relative.
         *
         * @return true if relative
         */
        public boolean isRelative() {
            return relative;
        }

        /**
         * Sets whether the block coordinates are relative.
         *
         * @param relative true if relative
         * @return this builder
         * @throws UnsupportedOperationException if this builder subtype
         * does not support relative coordinates
         */
        public Builder setRelative(boolean relative) {
            this.relative = relative;
            return this;
        }

        /**
         * Builds a new block position with the provided coordinate data.
         *
         * @return a new block position object
         */
        public BlockPosition build() {
            return new BlockPositionImpl(x, y, z, relative);
        }
    }
}
