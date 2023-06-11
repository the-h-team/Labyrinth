package io.github.sanctum.labyrinth.resourceloader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

class DependencyImpl implements Dependency {
    final MavenArtifact artifact;
    final String repository;
    private Document pom;

    DependencyImpl(@NotNull MavenArtifact artifact, @NotNull String repository) {
        this.artifact = artifact;
        this.repository = repository;
    }

    @Override
    public @NotNull MavenArtifact getArtifact() {
        return artifact;
    }

    @Override
    public @NotNull String getRepositoryUrl() {
        return repository;
    }

    Document getPom() {
        if (pom == null) {
            try {
                pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(getPomPath(this));
            } catch (SAXException | IOException | ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        return pom;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public @Nullable MavenArtifact resolveParent() {
        final Element project = getPom().getDocumentElement();
        final Element parent = (Element) project.getElementsByTagName("parent").item(0);
        if (parent != null) {
            final Element groupIdElement = (Element) parent.getElementsByTagName("groupId").item(0);
            final Element artifactIdElement = (Element) parent.getElementsByTagName("artifactId").item(0);
            final Element versionElement = (Element) parent.getElementsByTagName("version").item(0);
            if (groupIdElement != null && artifactIdElement != null && versionElement != null) {
                final String groupId = groupIdElement.getTextContent();
                final String artifactId = artifactIdElement.getTextContent();
                final String version = versionElement.getTextContent();
                if (MavenArtifact.GROUP_ID_FORMAT.matches(groupId)
                        && MavenArtifact.ARTIFACT_ID_FORMAT.matches(artifactId)
                        && MavenArtifact.VERSION_FORMAT.matches(version)) {
                    return new MavenArtifactImpl(groupId, artifactId, version, null);
                }
            }
            throw new RuntimeException("Invalid parent element");
        }
        return null;
    }

    @Override
    public @NotNull Set<Dependency> getCompileDependencies() {
        return Collections.emptySet();
    }

    static String getPomPath(DependencyImpl dependency) {
        final StringBuilder sb = new StringBuilder();
        sb.append(dependency.getRepositoryUrl());
        if (sb.charAt(sb.length() - 1) != '/') sb.append('/');
        return sb + dependency.getArtifact().getGroupId().replace('.', '/') + "/" + dependency.getArtifact().getArtifactId() + "/" + dependency.getArtifact().getVersion() + "/" + dependency.getArtifact().getArtifactId() + "-" + dependency.getArtifact().getVersion() + ".pom";
    }
}
