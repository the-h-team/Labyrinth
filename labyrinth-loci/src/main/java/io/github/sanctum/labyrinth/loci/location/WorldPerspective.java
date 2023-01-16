package io.github.sanctum.labyrinth.loci.location;

import io.github.sanctum.labyrinth.loci.world.HasWorld;
import io.github.sanctum.labyrinth.loci.world.WorldReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a detailed position in a world.
 * <p>
 * All values are absolute.
 *
 * @since 1.8.3
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface WorldPerspective extends Perspective, HasWorld, WorldPerspectiveLike {
    @Override
    @Contract("-> false")
    boolean isRelative();

    @Override
    default @NotNull WorldPerspective asWorldPerspective() {
        return this;
    }

    /**
     * Gets a world perspective builder from a world reference.
     *
     * @return a new world perspective builder
     */
    static Builder builder(@NotNull WorldReference worldReference) {
        return new Builder(worldReference);
    }

    /**
     * Builds a world perspective incrementally.
     *
     * @since 1.8.3
     */
    class Builder extends Perspective.Builder implements HasWorld {
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
        public Builder setYaw(float yaw) {
            this.yaw = yaw;
            return this;
        }

        @Override
        public Builder setPitch(float pitch) throws IllegalArgumentException {
            this.pitch = pitch;
            return this;
        }

        @Override
        public Builder setRelative(boolean relative) {
            if (relative) throw new UnsupportedOperationException("Cannot set relative on a WorldPerspective");
            return this; // silently no-op
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
         * Builds a new perspective.
         *
         * @return a new perspective
         */
        @Override
        public WorldPerspective build() {
            return new WorldPerspectiveImpl(
                    x, y, z,
                    yaw, pitch,
                    world
            );
        }
    }
}
