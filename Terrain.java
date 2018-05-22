
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.*;
import java.util.Observable;
import javax.crypto.Cipher;
import javax.vecmath.*;

public class Terrain {
    private Color colour = Color.RED;
    private int width = 40;
    private int height = 10;
    private Polygon terrain;
    ArrayList<Point2d> peaks = new ArrayList<Point2d>();
    ArrayList<Ellipse2D.Double> circles = new ArrayList<Ellipse2D.Double>();
    int inCircle = -1;
    private int beforeDraggedY = -1;

    public Terrain(){
        terrain = new Polygon();
        terrain.addPoint(0,110);
        //Add points for each peak
        int random = (int)(Math.random() * (200 - 100) + 100);
        for(int i = 0; i < 20; i++){
            terrain.addPoint(36*i, random);
            //Keep track of each peak so we can redraw later
            peaks.add(new Point2d(36*i, random));
            circles.add(new Ellipse2D.Double((36*i)-15, random-15, 30,30));
            random = (int)(Math.random() * (200 - 100) + 100);
        }
        terrain.addPoint(700, 110);
        terrain.addPoint(700, 200);
        terrain.addPoint(0, 200);

    }

    public void remakeTerrain(){
        terrain = new Polygon();
        terrain.addPoint(0,110);

        for(int i = 0; i < 20; i++){
            terrain.addPoint((36*i), (int)circles.get(i).getY()+15);
        }

        terrain.addPoint(700, 110);
        terrain.addPoint(700, 200);
        terrain.addPoint(0, 200);
    }


    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Polygon getTerrain() {
        return terrain;
    }

    //Hit test to check if mouse is inside circle
    //Returns -1 if fails, else returns the index of the circle
    public int insideCircle(MouseEvent e){
        int size = circles.size();
        for(int i = 0; i < size; i++){
            if(circles.get(i).contains(e.getX(), e.getY())){
                return i;
            }
        }
        return -1;
    }

    public void setBeforeDraggedY(int y) {
        beforeDraggedY = y;
    }

    public int getBeforeDraggedY() {
        return beforeDraggedY;
    }
}