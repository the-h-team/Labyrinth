package io.github.sanctum.labyrinth.loci.chunk;

import io.github.sanctum.labyrinth.loci.world.WorldReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class WorldChunkReferenceImpl extends ChunkReferenceImpl implements WorldChunkReference {
    final @NotNull WorldReference world;

    WorldChunkReferenceImpl(int x, int z, @NotNull WorldReference world) {
        super(x, z, false);
        this.world = world;
    }

    @Override
    public @NotNull WorldReference getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldChunkReferenceImpl)) return false;
        if (!super.equals(o)) return false;
        WorldChunkReferenceImpl that = (WorldChunkReferenceImpl) o;
        return world.equals(that.world);
    }

    @Override
    public int hashCode() {
        // TODO: Profile
        return 31 * super.hashCode() + world.hashCode();
    }

    @Override
    public String toString() {
        return "WorldChunkReferenceImpl{" +
                "x=" + x +
                ", z=" + z +
                ", world=" + world +
                '}';
    }
}
