package io.github.sanctum.labyrinth.loci.world;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

final class ByUIDImpl implements WorldReference.ByUID {
    private final UUID uuid;

    ByUIDImpl(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public @NotNull UUID getUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByUIDImpl byUUID = (ByUIDImpl) o;
        return uuid.equals(byUUID.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "ByUIDImpl{" +
                "uid=" + uuid +
                '}';
    }
}

