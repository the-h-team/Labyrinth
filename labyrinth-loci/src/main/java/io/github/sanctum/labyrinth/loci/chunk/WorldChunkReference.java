package io.github.sanctum.labyrinth.loci.chunk;

import io.github.sanctum.labyrinth.loci.world.WorldReference;
import io.github.sanctum.labyrinth.loci.world.HasWorld;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Refers to a chunk in a world.
 * <p>
 * {@code x}/{@code z} coordinates are absolute.
 *
 * @since 1.8.3
 * @author ms5984
 */
public interface WorldChunkReference extends ChunkReference, HasWorld {
    /**
     * The maximum negative {@code x} coordinate of a chunk.
     */
    int MIN_CHUNK_X = -1_875_000;
    /**
     * The maximum positive {@code x} coordinate of a chunk.
     */
    int MAX_CHUNK_X = 1_875_000;
    /**
     * The maximum negative {@code z} coordinate of a chunk.
     */
    int MIN_CHUNK_Z = -1_875_000;
    /**
     * The maximum positive {@code z} coordinate of a chunk.
     */
    int MAX_CHUNK_Z = 1_875_000;

    /**
     * Gets the chunk {@code x} coordinate.
     *
     * @return the chunk {@code x} coordinate
     */
    @Override
    @Range(from = MIN_CHUNK_X, to = MAX_CHUNK_X) int getChunkX();

    /**
     * Gets the chunk {@code z} coordinate.
     *
     * @return the chunk {@code z} coordinate
     */
    @Override
    @Range(from = MIN_CHUNK_Z, to = MAX_CHUNK_Z) int getChunkZ();

    /**
     * Gets the world reference for this chunk.
     *
     * @return a world reference
     */
    @Override
    @NotNull WorldReference getWorld();

    /**
     * Gets a chunk reference builder from an existing world reference.
     *
     * @param world a world reference
     * @return a new chunk reference builder
     */
    static Builder world(@NotNull WorldReference world) {
        return new Builder(world);
    }

    /**
     * Gets a chunk reference at chunk coordinates in a given world.
     *
     * @param x the chunk {@code x} coordinate
     * @param z the chunk {@code z} coordinate
     * @param world a world reference
     * @return a chunk reference
     */
    static WorldChunkReference of(@Range(from = MIN_CHUNK_X, to = MAX_CHUNK_X) int x,
                                  @Range(from = MIN_CHUNK_Z, to = MAX_CHUNK_Z) int z,
                                  @NotNull WorldReference world) {
        return new WorldChunkReferenceImpl(x, z, world);
    }

    /**
     * Builds a chunk reference incrementally.
     *
     * @since 1.8.3
     */
    @ApiStatus.NonExtendable
    final class Builder extends ChunkReference.Builder implements HasWorld {
        @NotNull WorldReference world;

        Builder(@NotNull WorldReference world) {
            this.world = world;
        }

        @Override
        public ChunkReference.Builder setRelative(boolean relative) {
            if (relative) throw new UnsupportedOperationException("Cannot set relative on a WorldChunkReference");
            return this; // silently no-op
        }

        /**
         * Gets the current chunk {@code x} coordinate.
         *
         * @return the current chunk {@code x} coordinate
         */
        @Override
        public @Range(from = MIN_CHUNK_X, to = MAX_CHUNK_X) int getChunkX() {
            return x;
        }

        /**
         * Gets the current chunk {@code z} coordinate.
         *
         * @return the current chunk {@code z} coordinate
         */
        @Override
        public @Range(from = MIN_CHUNK_Z, to = MAX_CHUNK_Z) int getChunkZ() {
            return z;
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
         * Sets the chunk {@code x} coordinate.
         *
         * @param x the chunk {@code x} coordinate
         * @return this builder
         */
        @Override
        public Builder setChunkX(@Range(from = MIN_CHUNK_X, to = MAX_CHUNK_X) int x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the chunk {@code z} coordinate.
         *
         * @param z the chunk {@code z} coordinate
         * @return this builder
         */
        @Override
        public Builder setChunkZ(@Range(from = MIN_CHUNK_Z, to = MAX_CHUNK_Z) int z) {
            this.z = z;
            return this;
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
         * Builds a new world chunk reference with the provided coordinate data
         * and world reference.
         *
         * @return a new world chunk reference
         */
        public WorldChunkReference build() {
            return new WorldChunkReferenceImpl(x, z, world);
        }
    }
}
