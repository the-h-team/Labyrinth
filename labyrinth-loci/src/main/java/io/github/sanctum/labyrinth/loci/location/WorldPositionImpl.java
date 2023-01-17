package io.github.sanctum.labyrinth.loci.location;

import io.github.sanctum.labyrinth.loci.world.WorldReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
class WorldPositionImpl extends PositionImpl implements WorldPosition {
    protected final @NotNull WorldReference world;

    WorldPositionImpl(double x, double y, double z, @NotNull WorldReference world) {
        super(x, y, z, false);
        this.world = world;
    }

    @Override
    public @NotNull WorldReference getWorld() {
        return world;
    }

    @Override
    public WorldPosition.Builder toBuilder() {
        return new WorldPosition.Builder(world);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldPositionImpl)) return false;
        WorldPositionImpl that = (WorldPositionImpl) o;
        return super.equals(o) && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 ^ world.hashCode();
    }

    @Override
    public String toString() {
        return "WorldPositionImpl{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", world=" + world +
                '}';
    }
}
