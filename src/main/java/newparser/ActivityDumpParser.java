package newparser;

import newparser.model.Bounds;
import newparser.model.Temp;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ActivityDumpParser {
    public static Temp temp ;
    public Document parseDump(String activityDump) throws AndroidRecorderException {
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse( new ByteArrayInputStream(activityDump.getBytes()));
            doc.getDocumentElement().normalize();
            return doc;
        }catch ( Exception e){
            throw new AndroidRecorderException(e);
        }
    }

    public Node getTopNode(Document doc){
        NodeList nList = doc.getElementsByTagName("hierarchy");
        Node nNode = nList.item(0).getFirstChild().getFirstChild();
        return nNode;
    }

    public Bounds getBounds(Node node){
        String bounds= node.getAttributes().getNamedItem("bounds").getNodeValue();
        String arr[] = bounds.split("\\]\\[");
        int left = Integer.parseInt(arr[0].split(",")[0].substring(1));
        int bottom = Integer.parseInt(arr[0].split(",")[1]);
        int right = Integer.parseInt(arr[1].split(",")[0]);
        int top = Integer.parseInt(arr[1].split(",")[1].substring(0,arr[1].split(",")[1].length()-1));
        return new Bounds(left, bottom, right, top);
    }
    public void initDimentionsTemp() throws IOException, InterruptedException {
        String text = new ActivityDumpGenarator().windowSizeDump();
        int width = Integer.parseInt(text.substring(text.indexOf(":")+1).split("x")[0].trim());
        int height = Integer.parseInt(text.substring(text.indexOf(":")).split("x")[1].trim());
        temp = new Temp(width *height, width, height);
        System.out.println(temp.getWidth()+" "+temp.getHeight());
    }

}
