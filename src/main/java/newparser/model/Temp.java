package newparser.model;

import org.w3c.dom.Node;

public class Temp {
   private int minArea;
   private int width;
   private int height;
   private Node selectedNode;
   public Temp(int minArea, int width, int height) {
        this.minArea = minArea;
        this.width = width;
        this.height = height;
   }
    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }
    public int getMinArea() {
        return minArea;
    }

    public void setMinArea(int minArea) {
        this.minArea = minArea;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
