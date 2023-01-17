package io.github.sanctum.labyrinth.loci.block;

import io.github.sanctum.labyrinth.loci.location.WorldPositionLike;
import io.github.sanctum.labyrinth.loci.world.HasWorld;
import io.github.sanctum.labyrinth.loci.world.WorldReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents a block's position within a world.
 * <p>
 * {@code x}/{@code y}/{@code z} values are absolute.
 *
 * @since 1.9.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface WorldBlockPosition extends BlockPosition, HasWorld, WorldPositionLike {
    @Override
    @Range(from = MIN_XZ, to = MAX_XZ) int getX();

    @Override
    @Range(from = MIN_Y, to = MAX_Y) int getY();

    @Override
    @Range(from = MIN_XZ, to = MAX_XZ) int getZ();

    @Override
    @Contract("-> false")
    boolean isRelative();

    /**
     * Gets a world block position builder from an existing world reference.
     *
     * @param world a world reference
     * @return a new world block position builder
     */
    static Builder builder(@NotNull WorldReference world) {
        return new Builder(world);
    }

    /**
     * Gets a world block position at coordinates in a given world.
     *
     * @param x the block {@code x} coordinate
     * @param y the block {@code y} coordinate
     * @param z the block {@code z} coordinate
     * @param world a world reference
     * @return a world block position
     */
    static WorldBlockPosition of(@Range(from = MIN_XZ, to = MAX_XZ) int x,
                                 @Range(from = MIN_Y, to = MAX_Y) int y,
                                 @Range(from = MIN_XZ, to = MAX_XZ) int z,
                                 @NotNull WorldReference world) {
        return new WorldBlockPositionImpl(x, y, z, world);
    }

    /**
     * Build a world block position incrementally.
     *
     * @since 1.9.0
     */
    @ApiStatus.NonExtendable
    class Builder extends BlockPosition.Builder implements HasWorld {
        @NotNull WorldReference world;

        Builder(@NotNull WorldReference world) {
            this.world = world;
        }

        @Override
        public Builder setX(@Range(from = MIN_XZ, to = MAX_XZ) int x) {
            this.x = x;
            return this;
        }

        @Override
        public Builder setY(@Range(from = MIN_Y, to = MAX_Y) int y) {
            this.y = y;
            return this;
        }

        @Override
        public Builder setZ(@Range(from = MIN_XZ, to = MAX_XZ) int z) {
            this.z = z;
            return this;
        }

        @Override
        @Contract("true -> fail")
        public Builder setRelative(boolean relative) {
            if (relative) throw new IllegalArgumentException("Cannot set relative on a WorldBlockPosition");
            return this; // no-op
        }

        /**
         * Gets the current world reference.
         *
         * @return the current world reference
         */
        @Override
        public @NotNull WorldReference getWorld() {
            return world;
        }

        /**
         * Sets the current world reference.
         *
         * @param world a world reference
         * @return this builder
         */
        public Builder setWorld(@NotNull WorldReference world) {
            this.world = world;
            return this;
        }

        /**
         * Builds a new world block position with the provided coordinate data
         * and world reference.
         *
         * @return a new world block position object
         */
        @Override
        public WorldBlockPosition build() {
            return new WorldBlockPositionImpl(x, y, z, world);
        }
    }
}
