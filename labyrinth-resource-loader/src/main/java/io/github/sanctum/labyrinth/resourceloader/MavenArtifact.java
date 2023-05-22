package io.github.sanctum.labyrinth.resourceloader;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Documented;

/**
 * Represents a Maven artifact.
 *
 * @author ms5984
 */
public interface MavenArtifact {
    //<editor-fold defaultstate="collapsed" desc="Regexes">
    /**
     * A pattern for matching Maven group IDs.
     */
    @RegExp String GROUP_ID_FORMAT = "(\\w[\\w-]*\\w(?:\\.\\w[\\w-]*\\w)*)";
    /**
     * A pattern for matching Maven artifact IDs.
     */
    @RegExp String ARTIFACT_ID_FORMAT = "(\\w[\\w-]*\\w)";
    /**
     * A pattern for matching Maven version strings.
     */
    @RegExp String VERSION_FORMAT = "(\\w[\\w.-]*\\w)";
    /**
     * A pattern for matching Maven artifact classifiers.
     */
    @RegExp String CLASSIFIER_FORMAT = "(\\w[\\w.-]*\\w)";
    /**
     * A pattern for matching GAV strings with optional classifier.
     */
    @RegExp String GAV_FORMAT = GROUP_ID_FORMAT + ":" + ARTIFACT_ID_FORMAT + ":" + VERSION_FORMAT +
            "(?::" + CLASSIFIER_FORMAT + ")?";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Extra Patterns">
    /**
     * Marks a string as a valid Maven group ID.
     */
    @Documented
    @Pattern(GROUP_ID_FORMAT)
    @interface GroupId {}

    /**
     * Marks a string as a valid Maven artifact ID.
     */
    @Documented
    @Pattern(ARTIFACT_ID_FORMAT)
    @interface ArtifactId {}

    /**
     * Marks a string as a valid Maven version string.
     */
    @Documented
    @Pattern(VERSION_FORMAT)
    @interface Version {}

    /**
     * Marks a string as a valid Maven artifact classifier.
     */
    @Documented
    @Pattern(CLASSIFIER_FORMAT)
    @interface Classifier {}
    //</editor-fold>
    /**
     * Marks a string as a valid GAV string.
     */
    @Documented
    @Pattern(GAV_FORMAT)
    @interface GAV {}

    /**
     * Gets the Maven group ID of this artifact.
     *
     * @return a Maven group ID
     */
    @GroupId @NotNull String getGroupId();

    /**
     * Gets the Maven artifact ID of this artifact.
     *
     * @return a Maven artifact ID
     */
    @ArtifactId @NotNull String getArtifactId();

    /**
     * Gets the Maven version string of this artifact.
     *
     * @return a Maven version string
     */
    @Version @NotNull String getVersion();

    /**
     * Gets the Maven artifact classifier of this artifact, if any.
     *
     * @return a Maven artifact classifier or {@code null}
     */
    @Classifier @Nullable String getClassifier();

    /**
     * Gets a Maven artifact from a GAV string.
     *
     * @param gavString a GAV string
     * @return a Maven artifact
     * @implNote Supports GAV strings with or without a classifier.
     * @see #GAV_FORMAT
     */
    static MavenArtifact fromGAV(@GAV String gavString) {
        return new MavenArtifactImpl(gavString);
    }
}
