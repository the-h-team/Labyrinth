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
 * @since 1.8.3
 * @author ms5984
 */
@ApiStatus.NonExtendable
public interface WorldBlockPosition extends BlockPosition, HasWorld, WorldPositionLike {
    /**
     * The minimum block {@code x}/{@code z} coordinate.
     */
    int MIN_XZ = -30_000_000;
    /**
     * The maximum block {@code x}/{@code z} coordinate.
     */
    int MAX_XZ = 30_000_000;
    /**
     * The minimum block {@code y} coordinate.
     */
    int MIN_Y = -2_032;
    /**
     * The maximum block {@code y} coordinate.
     */
    int MAX_Y = 2_032;

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

    // TODO builder?
}
