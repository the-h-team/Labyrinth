package io.github.sanctum.labyrinth.loci.chunk;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class ChunkReferenceImpl implements ChunkReference {
    final int x;
    final int z;
    final boolean relative;

    ChunkReferenceImpl(int x, int z, boolean relative) {
        this.x = x;
        this.z = z;
        this.relative = relative;
    }

    @Override
    public int getChunkX() {
        return x;
    }

    @Override
    public int getChunkZ() {
        return z;
    }

    @Override
    public boolean isRelative() {
        return relative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkReferenceImpl)) return false;
        ChunkReferenceImpl that = (ChunkReferenceImpl) o;
        return x == that.x && z == that.z && relative == that.relative;
    }

    @Override
    public int hashCode() {
        // TODO: Profile
        int result = x;
        result = 31 * result + z;
        result = 31 * result + (relative ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChunkReferenceImpl{" +
                "x=" + x +
                ", z=" + z +
                ", relative=" + relative +
                '}';
    }
}
