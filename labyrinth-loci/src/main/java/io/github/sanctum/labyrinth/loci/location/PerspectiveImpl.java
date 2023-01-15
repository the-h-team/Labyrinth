package io.github.sanctum.labyrinth.loci.location;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class PerspectiveImpl extends PositionImpl implements Perspective {
    protected final float yaw;
    protected final float pitch;

    PerspectiveImpl(double x, double y, double z, boolean relative,
                    float yaw, float pitch) {
        super(x, y, z, relative);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerspectiveImpl)) return false;
        if (!super.equals(o)) return false;
        PerspectiveImpl perspective = (PerspectiveImpl) o;
        return Float.compare(perspective.yaw, yaw) == 0 &&
                Float.compare(perspective.pitch, pitch) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (yaw != 0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (pitch != 0.0f ? Float.floatToIntBits(pitch) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PerspectiveImpl{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", relative=" + relative +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
