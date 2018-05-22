import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.*;
import java.util.Observable;
import javax.vecmath.*;

/**
 * Simple rectangle class
 */
class LandingPad extends Observable {

    private Color colour = Color.RED;
    private Point2d beforeDragPosition;
    private int width = 40;
    private int height = 10;
    private boolean mouseInside = false;

    private Rectangle pad;

    public LandingPad(int x, int y){
        beforeDragPosition = new Point2d(x, y);
        pad = new Rectangle(x,y, width, height);
    }

    public Rectangle getRectangle(){
        return pad;
    }


    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public boolean isMouseInside(){
        return mouseInside;
    }

    public void setMouseInside(boolean b){
        mouseInside = b;
    }

    public Point2d getbeforeDragPosition() {
        return beforeDragPosition;
    }

    public void setBeforeDragPosition(Point2d beforeDragPosition) {
        this.beforeDragPosition = beforeDragPosition;
    }
}