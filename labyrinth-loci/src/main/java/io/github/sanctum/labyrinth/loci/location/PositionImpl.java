package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class PositionImpl implements Position {
    protected final double x, y, z;
    protected final boolean relative;

    PositionImpl(double x, double y, double z, boolean relative) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.relative = relative;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public boolean isRelative() {
        return relative;
    }

    @Override
    public Builder toBuilder() {
        return new Builder()
                .setX(x)
                .setY(y)
                .setZ(z)
                .setRelative(relative);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionImpl)) return false;
        PositionImpl position = (PositionImpl) o;
        return Double.compare(position.x, x) == 0 &&
                Double.compare(position.y, y) == 0 &&
                Double.compare(position.z, z) == 0 &&
                position.relative == relative;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        if (relative) {
            result = 31 * result + 1;
        }
        return result;
    }

    @Override
    public String toString() {
        return "PositionImpl{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", relative=" + relative +
                '}';
    }
}
