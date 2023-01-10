package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a detailed position.
 * <p>
 * {@code Yaw} and {@code pitch} values are absolute degrees.
 * {@code x}/{@code y}/{@code z} values may be absolute or relative.
 *
 * @since 1.8.3
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface Perspective extends Position, PerspectiveLike {
    /**
     * Gets the {@code yaw} of this perspective.
     * <p>
     * This value corresponds to horizontal rotation.
     * Values range from 0 to 360 to express all directions.
     * <p>
     * For reference:
     * <pre>
     *       0 = +Z (south)
     *      90 = -X (west)
     *     180 = -Z (north)
     *     270 = +X (east)
     * </pre>
     *
     * @return the {@code yaw} of this perspective
     */
    float yaw();

    /**
     * Gets the {@code pitch} of this perspective.
     * <p>
     * This value corresponds to vertical rotation.
     * Values range from -90 to 90.
     *
     * @return the {@code pitch} of this perspective
     */
    float pitch();

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
     * @since 1.8.3
     */
    class Builder extends Position.Builder {
        protected float yaw;
        protected float pitch;

        Builder() {}

        /**
         * Sets the {@code yaw} value.
         * <p>
         * Values above 360 and below 0 will be normalized.
         *
         * @param yaw the yaw value
         * @return this builder
         */
        public Builder yaw(float yaw) {
            this.yaw = normalizeYaw(yaw);
            return this;
        }

        /**
         * Sets the {@code pitch} value.
         * <p>
         * Validates that the value is between -90 and 90.
         *
         * @param pitch the pitch value
         * @return this builder
         * @throws IllegalArgumentException if the value is not between
         * {@code -90} (inclusive) and {@code 90} (also inclusive)
         * @see #validatePitch(float)
         */
        public Builder pitch(float pitch) throws IllegalArgumentException {
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
         * The values returned will be between 0 and 360 (inclusive).
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
         * {@code -90} or greater than {@code 90}
         */
        public static float validatePitch(float pitch) throws IllegalArgumentException {
            if (pitch < 90f || pitch > 90f) {
                throw new IllegalArgumentException("Pitch must be between -90 and 90");
            }
            return pitch;
        }
    }
}
