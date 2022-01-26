public class Keyboard {

    public static final int EDGE_LEFT = 14;
    public static final int EDGE_RIGHT = 14;
    public static final int EDGE_TOP = 14;
    public static final int EDGE_BOTTOM = 14;
    /**
     * All the key codes (unicode or custom code) that this key could generate, zero'th
     * being the most important.
     */
    public int[] codes;

    /**
     * Label to display
     */
    public CharSequence label;
    /**
     * Width of the key, not including the gap
     */
    public int width;
    /**
     * Height of the key, not including the gap
     */
    public int height;
    /**
     * The horizontal gap before this key
     */
    public int gap;
    /**
     * Whether this key is sticky, i.e., a toggle key
     */
    public boolean sticky;
    /**
     * X coordinate of the key in the keyboard layout
     */
    public int x;
    /**
     * Y coordinate of the key in the keyboard layout
     */
    public int y;
    /**
     * The current pressed state of this key
     */
    public boolean pressed;
    /**
     * If this is a sticky key, is it on?
     */
    public boolean on;
    /**
     * Text to output when pressed. This can be multiple characters, like ".com"
     */
    public CharSequence text;
    /**
     * Popup characters
     */

    public int edgeFlags;
    /**
     * Whether this is a modifier key, such as Shift or Alt
     */
    public boolean modifier;
    /**
     * If this key pops up a mini keyboard, this is the resource id for the XML layout for that
     * keyboard.
     */
    public int popupResId;
    /**
     * Whether this key repeats itself when held down
     */
    public boolean repeatable;

    Keyboard(int minx, int miny, int width, int height){
        this.x =minx;
        this.y=miny;
        this.width =width;
        this.height=height;
    }
    /** Create a key with the given top-left coordinate and extract its attributes from
     * the XML parser.
     * @param res resources associated with the caller's context
     * @param parent the row that this key belongs to. The row must already be attached to
     * a {@link Keyboard}.
     * @param x the x coordinate of the top-left
     * @param y the y coordinate of the top-left
     * @param parser the XML parser containing the attributes for this key
     */

    /**
     * Detects if a point falls inside this key.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return whether or not the point falls inside the key. If the key is attached to an edge,
     * it will assume that all points between the key and the edge are considered to be inside
     * the key.
     */
    public boolean isInside(int x, int y) {
        boolean leftEdge = (EDGE_LEFT) > 0;
        boolean rightEdge = ( EDGE_RIGHT) > 0;
        boolean topEdge = ( EDGE_TOP) > 0;
        boolean bottomEdge = (EDGE_BOTTOM) > 0;
        if ((x >= this.x && (x <= this.x + this.width))
                && (y >= this.y && (y <= this.y + this.height))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the square of the distance between the center of the key and the given point.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the square of the distance of the point from the center of the key
     */
    public int squaredDistanceFrom(int x, int y) {
        int xDist = this.x + width / 2 - x;
        int yDist = this.y + height / 2 - y;
        return xDist * xDist + yDist * yDist;
    }


}