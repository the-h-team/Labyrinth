package io.github.sanctum.labyrinth.resourceloader;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

class TestLoadXML {
    @Test
    void testLoadXMLForMavenArtifact() throws ParserConfigurationException, IOException, SAXException {
        // https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
//        final Document document = builder.parse("https://jitpack.io/com/github/the-h-team/Panther/panther-common/1.0.2/panther-common-1.0.2.pom");
        final Document document = builder.parse("https://repo.maven.apache.org/maven2/net/kyori/adventure-api/4.13.1/adventure-api-4.13.1.pom");
        final Element root = document.getDocumentElement();
        System.out.println("Root element: " + root.getNodeName());
        final NodeList dependencies = root.getElementsByTagName("dependency");
        for (int i = 0; i < dependencies.getLength(); ++i) {
            if (dependencies.item(i).getNodeType() != Element.ELEMENT_NODE) continue;
            final Element dependency = (Element) dependencies.item(i);
            final Node scope = dependency.getElementsByTagName("scope").item(0);
            if (scope != null && "compile".equals(scope.getTextContent())) {
                System.out.println("Found compile dependency");
                final String groupId = dependency.getElementsByTagName("groupId").item(0).getTextContent();
                final String artifactId = dependency.getElementsByTagName("artifactId").item(0).getTextContent();
                final String version = dependency.getElementsByTagName("version").item(0).getTextContent();
                System.out.println("Dependency: " + groupId + ":" + artifactId + ":" + version);
            }
        }
    }
}
