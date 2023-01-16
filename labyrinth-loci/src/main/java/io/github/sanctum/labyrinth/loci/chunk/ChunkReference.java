package io.github.sanctum.labyrinth.loci.chunk;

import org.jetbrains.annotations.ApiStatus;

/**
 * Refers to a chunk.
 * <p>
 * {@code x}/{@code z} coordinates may be relative or absolute.
 *
 * @since 1.9.0
 * @author ms5984
 */
public interface ChunkReference {

    /**
     * Gets the chunk {@code x} coordinate.
     *
     * @return the chunk {@code x} coordinate
     */
    int getChunkX();

    /**
     * Gets the chunk {@code z} coordinate.
     *
     * @return the chunk {@code z} coordinate
     */
    int getChunkZ();

    /**
     * Indicates whether this chunk reference is relative.
     *
     * @return true if relative
     */
    boolean isRelative();

    /**
     * Gets a new chunk reference builder.
     *
     * @return a new chunk reference builder
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Gets a reference with the provided absolute chunk coordinates.
     *
     * @param x a chunk {@code x} coordinate
     * @param z a chunk {@code z} coordinate
     * @return a chunk reference using absolute coordinates
     */
    static ChunkReference absolute(int x, int z) {
        return new ChunkReferenceImpl(x, z, false);
    }

    /**
     * Gets an arbitrary reference with the provided chunk coordinates.
     *
     * @param x a chunk {@code x} coordinate
     * @param z a chunk {@code z} coordinate
     * @param relative whether the coordinates are relative
     * @return an arbitrary chunk reference with the given coordinates
     */
    static ChunkReference arbitrary(int x, int z, boolean relative) {
        return new ChunkReferenceImpl(x, z, relative);
    }

    /**
     * Build a chunk reference incrementally.
     *
     * @since 1.9.0
     */
    @ApiStatus.NonExtendable
    class Builder {
        int x;
        int z;
        boolean relative;

        Builder() {}

        /**
         * Gets the current chunk {@code x} coordinate.
         *
         * @return the current chunk {@code x} coordinate
         */
        public int getChunkX() {
            return x;
        }

        /**
         * Gets the current chunk {@code z} coordinate.
         *
         * @return the current chunk {@code z} coordinate
         */
        public int getChunkZ() {
            return z;
        }

        /**
         * Indicates whether the chunk coordinates are relative.
         *
         * @return true if relative
         */
        boolean isRelative() {
            return relative;
        }

        /**
         * Sets the chunk {@code x} coordinate.
         *
         * @param x the chunk {@code x} coordinate
         * @return this builder
         */
        public Builder setChunkX(int x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the chunk {@code z} coordinate.
         *
         * @param z the chunk {@code z} coordinate
         * @return this builder
         */
        public Builder setChunkZ(int z) {
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
         * Builds a new chunk reference with the provided coordinate data.
         *
         * @return a new chunk reference object
         */
        public ChunkReference build() {
            return new ChunkReferenceImpl(x, z, relative);
        }
    }
}
