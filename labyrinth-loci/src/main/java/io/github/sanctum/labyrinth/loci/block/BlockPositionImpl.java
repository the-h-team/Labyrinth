package io.github.sanctum.labyrinth.loci.block;

import io.github.sanctum.labyrinth.loci.location.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
class BlockPositionImpl implements BlockPosition {
    final int x;
    final int y;
    final int z;
    final boolean relative;

    BlockPositionImpl(int x, int y, int z, boolean relative) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.relative = relative;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public boolean isRelative() {
        return relative;
    }

    @Override
    public @NotNull Position asPosition() {
        return Position.arbitrary(x, y, z, relative);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldBlockPositionImpl)) return false;
        WorldBlockPositionImpl that = (WorldBlockPositionImpl) o;
        return x == that.x && y == that.y && z == that.z && relative == that.relative;
    }

    @Override
    public int hashCode() {
        // TODO: Profile
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + (relative ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BlockPositionImpl{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", relative=" + relative +
                '}';
    }
}
