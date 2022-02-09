package newparser.model;


public class Bounds {
    private int left;
    private int bottom;
    private int top;
    private int right;

    public Bounds(int left, int bottom, int right, int top) {
        this.left = left;
        this.bottom = bottom;
        this.top = top;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }
}
