package io.github.sanctum.labyrinth.loci.location;

import io.github.sanctum.labyrinth.loci.world.WorldReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
class WorldPerspectiveImpl extends PerspectiveImpl implements WorldPerspective {
    protected final @NotNull WorldReference world;

    WorldPerspectiveImpl(double x, double y, double z,
                         float yaw, float pitch,
                         @NotNull WorldReference world) {
        super(x, y, z, false, yaw, pitch);
        this.world = world;
    }

    @Override
    public @NotNull WorldReference getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldPerspectiveImpl)) return false;
        if (!super.equals(o)) return false;
        WorldPerspectiveImpl worldPerspective = (WorldPerspectiveImpl) o;
        return world.equals(worldPerspective.world);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + world.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WorldPerspectiveImpl{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", relative=" + relative +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", world=" + world +
                '}';
    }
}
