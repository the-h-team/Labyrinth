package io.github.sanctum.labyrinth.loci.block;

import io.github.sanctum.labyrinth.loci.location.WorldPosition;
import io.github.sanctum.labyrinth.loci.world.WorldReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class WorldBlockPositionImpl extends BlockPositionImpl implements WorldBlockPosition {
    @NotNull final WorldReference world;

    WorldBlockPositionImpl(int x, int y, int z, @NotNull WorldReference world) {
        super(x, y, z, false);
        this.world = world;
    }

    @Override
    public @NotNull WorldReference getWorld() {
        return world;
    }

    @Override
    public @NotNull WorldPosition asWorldPosition() {
        return WorldPosition.builder(world)
                .setX(x)
                .setY(y)
                .setZ(z)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldBlockPositionImpl)) return false;
        if (!super.equals(o)) return false;
        WorldBlockPositionImpl that = (WorldBlockPositionImpl) o;
        return world.equals(that.world);
    }

    @Override
    public int hashCode() {
        // TODO: Profile
        int result = super.hashCode();
        result = 31 * result + world.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WorldBlockPositionImpl{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", world=" + world +
                '}';
    }
}
