package io.github.sanctum.labyrinth.resourceloader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Represents a Maven artifact as a dependency.
 * <p>
 * Dependencies may have transitive dependencies.
 *
 * @author ms5984
 */
public interface Dependency {
    /**
     * Gets the Maven artifact represented by this dependency.
     *
     * @return the artifact
     */
    @NotNull MavenArtifact getArtifact();

    /**
     * Gets the repository from which this dependency is loaded.
     *
     * @return the repository
     */
    @NotNull String getRepositoryUrl();

    /**
     * Resolves the parent of this dependency.
     * <p>
     * This is required in some cases to resolve {@code dependencyManagement}.
     *
     * @return the parent dependency or {@code null} if none
     */
    @Nullable MavenArtifact resolveParent();

    /**
     * Gets the {@code compile} dependencies of this dependency.
     * <p>
     * This will traverse the dependency tree to find all dependencies.
     *
     * @return the {@code compile} dependencies
     */
    @NotNull Set<Dependency> getCompileDependencies();
}
