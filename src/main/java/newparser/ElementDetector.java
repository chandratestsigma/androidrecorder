package newparser;

import newparser.model.Bounds;
import newparser.model.Point;
import newparser.model.Temp;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementDetector {

    public Boolean verifyPointsInside(Node node, Point point, Temp temp){

        Bounds bounds = new ActivityDumpParser().getBounds(node);
       if((point.getX() >= bounds.getLeft()) && (point.getX() < bounds.getRight())
               && (point.getY() <= bounds.getTop()) && (point.getY() > bounds.getBottom())){
           int area = (temp.getWidth()-(bounds.getRight()-bounds.getLeft())) * (temp.getHeight()-(bounds.getTop()- bounds.getBottom()));
           boolean hasMinorChilds=false;
           for( int i=0 ; i< node.getChildNodes().getLength();i++){
               boolean hasMinor = verifyPointsInside(node.getChildNodes().item(i), point, temp);
               if(hasMinor){
                   hasMinorChilds = true;
                   break;
               }
           }
           if((area<temp.getMinArea()) && (area>0) && !node.hasChildNodes() && !hasMinorChilds){
               temp.setSelectedNode(node);
               temp.setMinArea(area);
               return true;
           }
       }
       return false;
   }

    public void getMinNodeInsidePoint( Node node, Point point, Temp temp){
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i <childNodes.getLength(); i++) {
            Node nNode = childNodes.item(i);
            verifyPointsInside(node, point, temp);
            verifySiblings(nNode, point, temp);
            getMinNodeInsidePoint( nNode, point, temp);
        }
    }

    private void verifySiblings(Node node, Point point, Temp temp){
        Node sibling = node.getNextSibling();
        while (sibling !=null) {
            verifyPointsInside(node, point, temp);
            getMinNodeInsidePoint( sibling, point, temp);
            sibling = sibling.getNextSibling();

        }
    }
}
