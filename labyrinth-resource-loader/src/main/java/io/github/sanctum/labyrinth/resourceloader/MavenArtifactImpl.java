package io.github.sanctum.labyrinth.resourceloader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ms5984
 */
class MavenArtifactImpl implements MavenArtifact {
    final @GroupId String groupId;
    final @ArtifactId String artifactId;
    final @Version String version;
    final @Classifier String classifier;


    @SuppressWarnings("PatternValidation")
    MavenArtifactImpl(@GAV String gav) {
        final String[] parts = gav.split(":", 4);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid GAV string: '" + gav + "'");
        }
        groupId = parts[0];
        artifactId = parts[1];
        version = parts[2];
        classifier = parts.length > 3 ? parts[3] : null;
    }

    MavenArtifactImpl(@GroupId String groupId, @ArtifactId String artifactId, @Version String version, @Classifier String classifier) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
    }

    @GroupId
    @Override
    public @NotNull String getGroupId() {
        return groupId;
    }

    @ArtifactId
    @Override
    public @NotNull String getArtifactId() {
        return artifactId;
    }

    @Version
    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Classifier
    @Override
    public @Nullable String getClassifier() {
        return classifier;
    }
}
