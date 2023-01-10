package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an arbitrary set of position coordinates.
 * <p>
 * May be absolute or relative unless otherwise specified.
 *
 * @since 1.8.3
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface Position extends PositionLike {
    /**
     * Gets the {@code x} coordinate.
     *
     * @return the {@code x} coordinate
     */
    double getX();

    /**
     * Gets the {@code y} coordinate.
     *
     * @return the {@code y} coordinate
     */
    double getY();

    /**
     * Gets the {@code z} coordinate.
     *
     * @return the {@code z} coordinate
     */
    double getZ();

    /**
     * Indicates whether this position is relative.
     *
     * @return true if relative
     */
    boolean isRelative();

    /**
     * Copies this position's data to a new position builder.
     *
     * @return a new position builder with this position's data
     */
    Builder toBuilder();

    @Override
    default @NotNull Position asPosition() {
        return this;
    }

    /**
     * Gets an absolute position object with the given coordinates.
     *
     * @param x the {@code x} coordinate
     * @param y the {@code y} coordinate
     * @param z the {@code z} coordinate
     * @return an absolute position
     */
    static Position absolute(double x, double y, double z) {
        return new PositionImpl(x, y, z, false);
    }

    /**
     * Gets an arbitrary position object with the given coordinates.
     *
     * @param x the {@code x} coordinate
     * @param y the {@code y} coordinate
     * @param z the {@code z} coordinate
     * @param relative whether the coordinates are relative
     * @return an arbitrary position
     */
    static Position arbitrary(double x, double y, double z, boolean relative) {
        return new PositionImpl(x, y, z, relative);
    }

    /**
     * Gets a new position builder.
     *
     * @return a new position builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Builds a position incrementally.
     *
     * @since 1.8.3
     */
    @ApiStatus.NonExtendable
    class Builder {
        protected double x;
        protected double y;
        protected double z;
        protected boolean relative;

        Builder() {}

        /**
         * Gets the current {@code x} coordinate.
         *
         * @return the current {@code x} coordinate
         */
        public double getX() {
            return x;
        }

        /**
         * Gets the current {@code y} coordinate.
         *
         * @return the current {@code y} coordinate
         */
        public double getY() {
            return y;
        }

        /**
         * Gets the current {@code z} coordinate.
         *
         * @return the current {@code z} coordinate
         */
        public double getZ() {
            return z;
        }

        /**
         * Sets the {@code x} coordinate.
         *
         * @param x a new {@code x} coordinate
         * @return this builder
         */
        public Builder setX(double x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the {@code y} coordinate.
         *
         * @param y a new {@code y} coordinate
         * @return this builder
         */
        public Builder setY(double y) {
            this.y = y;
            return this;
        }

        /**
         * Sets the {@code z} coordinate.
         *
         * @param z a new {@code z} coordinate
         * @return this builder
         */
        public Builder setZ(double z) {
            this.z = z;
            return this;
        }

        /**
         * Sets whether the coordinates are relative.
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
         * Builds a new position with the given coordinate data.
         *
         * @return a new position object
         */
        public Position build() {
            return new PositionImpl(x, y, z, relative);
        }
    }
}
