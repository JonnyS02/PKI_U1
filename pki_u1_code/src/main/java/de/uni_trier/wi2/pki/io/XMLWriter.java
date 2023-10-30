package de.uni_trier.wi2.pki.io;

import de.uni_trier.wi2.pki.Main;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Serializes the decision tree in form of an XML structure.
 */
public class XMLWriter {

    /**
     * Serialize decision tree to specified path.
     *
     * @param path         the path to write to.
     * @param decisionTree the tree to serialize.
     * @throws IOException if something goes wrong.
     */

    static String classTitle = "Node";

    static Element rootElement;
    static Element firstSplitt;
    static Document doc;

    public static void writeXML(String path, DecisionTreeNode decisionTree) throws IOException {
        setupWriter(decisionTree);
        buildXmlTree(decisionTree, firstSplitt, doc);
        try (FileOutputStream output = new FileOutputStream(path)) {
            writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


    private static void setupWriter(DecisionTreeNode decisionTree) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        doc = docBuilder.newDocument();
        rootElement = doc.createElement("DecisionTree");
        doc.appendChild(rootElement);
        firstSplitt = doc.createElement(classTitle);
        firstSplitt.setAttribute("attribute", Main.getIndexName(decisionTree.getAttributeIndex()));
        rootElement.appendChild(firstSplitt);
    }

    /**
     * Builds decisionTree trough depth-first-search.
     * @param decisionTree
     * @param parent
     * @param doc
     */
    private static void buildXmlTree(DecisionTreeNode decisionTree, Element parent, Document doc) {
        HashMap<String, DecisionTreeNode> map = decisionTree.getSplits();
        for (HashMap.Entry<String, DecisionTreeNode> branch : map.entrySet()) {
            DecisionTreeNode child = branch.getValue();
            if (child == null)
                return;
            Element childElement = doc.createElement(classTitle);
            childElement = testIfLeafNode(child, childElement, doc);

            String value = branch.getKey();
            Attr attributeName = doc.createAttribute("value");
            attributeName.setValue(value);

            Element compareElement = doc.createElement("IF");
            parent.appendChild(compareElement);
            compareElement.setAttributeNode(attributeName);
            compareElement.appendChild(childElement);

            buildXmlTree(child, childElement, doc);
        }
    }

    /**
     * Detects if node is a leaf and marks it with leaf node.
     * Otherwise it gets labeled with its value for the represented attribute.
     * @param child
     * @param childElement
     * @param doc
     * @return
     */
    private static Element testIfLeafNode(DecisionTreeNode child, Element childElement, Document doc) {
        if (child.getSplits().
                size() == 1) {
            childElement = doc.createElement("LeafNode");
            childElement.setAttribute("class", "" + child.getSplits().keySet().toArray()[0]);
        } else
            childElement.setAttribute("attribute", Main.getIndexName(child.getAttributeIndex()));
        return childElement;
    }


    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }
}
