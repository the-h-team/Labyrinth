package io.github.sanctum.labyrinth.loci.location;

import io.github.sanctum.labyrinth.loci.world.HasWorld;
import io.github.sanctum.labyrinth.loci.world.WorldReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a set of position coordinates in a world.
 * <p>
 * {@code x}/{@code y}/{@code z} values are absolute.
 *
 * @since 1.8.3
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface WorldPosition extends Position, HasWorld, WorldPositionLike {
    /**
     * Gets a new builder initialized with data from this world position.
     *
     * @return a new world position builder
     */
    @Override
    Builder toBuilder();

    @Override
    @Contract("-> false")
    boolean isRelative();

    @Override
    default @NotNull WorldPosition asWorldPosition() {
        return this;
    }

    /**
     * Gets a world position builder from a world reference.
     *
     * @param worldReference a world reference
     * @return a new world position builder
     */
    static Builder world(@NotNull WorldReference worldReference) {
        return new Builder(worldReference);
    }

    /**
     * Builds a world position incrementally.
     *
     * @since 1.8.3
     */
    @ApiStatus.NonExtendable
    class Builder extends Position.Builder implements HasWorld {
        protected @NotNull WorldReference world;

        Builder(@NotNull WorldReference world) {
            this.world = world;
        }

        @Override
        public Builder setX(double x) {
            this.x = x;
            return this;
        }

        @Override
        public Builder setY(double y) {
            this.y = y;
            return this;
        }

        @Override
        public Builder setZ(double z) {
            this.z = z;
            return this;
        }

        @Override
        public Builder setRelative(boolean relative) {
            if (relative) throw new UnsupportedOperationException("Cannot set relative on a WorldPosition");
            return this; // silently no-op
        }

        /**
         * Gets the current world reference.
         *
         * @return the current world reference
         */
        @Override
        public final @NotNull WorldReference getWorld() {
            return world;
        }

        /**
         * Sets the world reference.
         *
         * @param world a world reference
         * @return this builder
         */
        public Builder setWorld(@NotNull WorldReference world) {
            this.world = world;
            return this;
        }

        /**
         * Builds a world position with the given coordinates and world.
         *
         * @return a new world position
         */
        @Override
        public WorldPosition build() {
            return new WorldPositionImpl(x, y, z, world);
        }
    }
}
