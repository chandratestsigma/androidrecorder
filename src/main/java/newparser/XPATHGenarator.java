package newparser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.List;

public class XPATHGenarator {

    List<String> attributes = List.of("class", "resource-id", "text",  "checked", "enabled", "package", "password");

    private boolean validateXpath(Document doc, String xpath) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(xpath, doc.getDocumentElement(), XPathConstants.NODESET);
        return nodes.getLength() == 1;
    }

    public String generateXpath(Document doc, Node node) {

        String xpath ="//node";

        for(int i=0; i<attributes.size();i++){
            try {
                xpath = xpath + "[@"+node.getAttributes().getNamedItem(attributes.get(i))+"]";
                if(validateXpath(doc, xpath)){
                    break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        System.out.println(xpath);
        return xpath;
    }
}
