package io.github.sanctum.labyrinth.loci.world;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class ByNameImpl implements WorldReference.ByName {
    private final String name;

    ByNameImpl(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByNameImpl byName = (ByNameImpl) o;
        return name.equals(byName.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "ByNameImpl{" +
                "name='" + name + '\'' +
                '}';
    }
}
