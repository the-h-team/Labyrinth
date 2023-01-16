package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a detailed position.
 * <p>
 * {@code Yaw} and {@code pitch} values are absolute degrees.
 * {@code x}/{@code y}/{@code z} values may be absolute or relative.
 *
 * @since 1.9.0
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface Perspective extends Position, PerspectiveLike {
    /**
     * Gets the {@code yaw} of this perspective.
     * <p>
     * This value corresponds to horizontal rotation.
     * Values range from 0.0 to 360.0 to express all directions.
     * <p>
     * For reference:
     * <pre>
     *   {@code 0.0f} = +Z (south)
     *  {@code 90.0f} = -X (west)
     * {@code 180.0f} = -Z (north)
     * {@code 270.0f} = +X (east)</pre>
     *
     * @return the {@code yaw} of this perspective
     */
    float getYaw();

    /**
     * Gets the {@code pitch} of this perspective.
     * <p>
     * This value corresponds to vertical rotation.
     * Values range from -90.0 to 90.0.
     * <p>
     * For reference:
     * <pre>
     * {@code -90.0f} = looking straight up
     *   {@code 0.0f} = looking forward
     *  {@code 90.0f} = looking straight down</pre>
     *
     * @return the {@code pitch} of this perspective
     */
    float getPitch();

    @Override
    default @NotNull Perspective asPerspective() {
        return this;
    }

    /**
     * Gets a new perspective builder.
     *
     * @return a new perspective builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Build a perspective incrementally.
     *
     * @since 1.9.0
     */
    class Builder extends Position.Builder {
        protected float yaw;
        protected float pitch;

        Builder() {}

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

        /**
         * Gets the current {@code yaw} value.
         *
         * @return the current {@code yaw} value
         */
        public float getYaw() {
            return yaw;
        }

        /**
         * Sets the {@code yaw} value.
         * <p>
         * Values above {@code 360.0} and below {@code 0.0} will be normalized.
         *
         * @param yaw the yaw value
         * @return this builder
         * @see #normalizeYaw(float)
         */
        public Builder setYaw(float yaw) {
            this.yaw = normalizeYaw(yaw);
            return this;
        }

        /**
         * Gets the current {@code pitch} value.
         *
         * @return the current {@code pitch} value
         */
        public float getPitch() {
            return pitch;
        }

        /**
         * Sets the {@code pitch} value.
         * <p>
         * This method checks that the provided pitch is a valid pitch.
         *
         * @param pitch the pitch value
         * @return this builder
         * @throws IllegalArgumentException if {@code pitch} is not a valid
         * pitch
         * @see #validatePitch(float)
         */
        public Builder setPitch(float pitch) throws IllegalArgumentException {
            this.pitch = validatePitch(pitch);
            return this;
        }

        /**
         * Builds a new perspective.
         *
         * @return a new perspective
         */
        @Override
        public Perspective build() {
            return new PerspectiveImpl(
                    x, y, z, relative,
                    yaw, pitch
            );
        }

        /**
         * Normalizes a yaw value.
         * <p>
         * The values returned will be between {@code 0.0f} and {@code 360.0f}
         * (inclusive).
         *
         * @param yaw a yaw value
         * @return a normalized yaw value
         */
        public static float normalizeYaw(float yaw) {
            if (yaw > 360f) {
                return yaw % 360f;
            } else if (yaw < 0f) {
                return yaw % 360f + 360f;
            }
            return yaw;
        }

        /**
         * Validates a pitch value.
         *
         * @param pitch a pitch value
         * @return {@code pitch} if it is a valid pitch value
         * @throws IllegalArgumentException if {@code pitch} is less than
         * {@code -90.0f} or greater than {@code 90.0f}
         */
        public static float validatePitch(float pitch) throws IllegalArgumentException {
            if (pitch < 90f || pitch > 90f) {
                throw new IllegalArgumentException("Pitch must be between -90 and 90");
            }
            return pitch;
        }
    }
}
