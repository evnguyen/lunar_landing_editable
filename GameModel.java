import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.undo.*;
import javax.vecmath.*;

public class GameModel extends Observable {
    public Ship ship;
    public LandingPad landingpad;
    public Terrain terrain;
    private UndoManager undoManager;

    public GameModel(int fps, int width, int height, int peaks) {

        ship = new Ship(60, width/2, 50);
        landingpad = new LandingPad(330, 100);
        terrain = new Terrain();
        undoManager = new UndoManager();

        worldBounds = new Rectangle2D.Double(0, 0, width, height);

        // anonymous class to monitor ship updates
        ship.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                //Ship has crashed onto terrain or outside of world bounds OR
                //Ship has landed on landingpad but not at safe speed
                if(terrain.getTerrain().intersects(ship.getShape()) || !worldBounds.contains(ship.getShape())
                        || landingpad.getRectangle().intersects(ship.getShape()) && ship.getSpeed() >= ship.getSafeLandingSpeed()){
                    ship.stop();
                    ship.setCrashed(true);
                }
                //Ship has landed on landingpad using a safe speed
                else if(landingpad.getRectangle().intersects(ship.getShape()) && ship.getSpeed() < ship.getSafeLandingSpeed()){
                    ship.stop();
                    ship.setCrashed(false);
                    ship.setLanded(true);
                }
                setChangedAndNotify();
            }
        });
    }

    public void doubleClickHandler(int x, int y) {
        Point2d point = new Point2d(x,y);
        UndoableEdit undoableEdit = new AbstractUndoableEdit() {
            // capture variables for closure
            final int oldX = (int)landingpad.getRectangle().getX();
            final int oldY = (int)landingpad.getRectangle().getY();
            final int newX = (int) (point.getX() - landingpad.getRectangle().getWidth()/2);
            final int newY = (int) (point.getY() - landingpad.getRectangle().getHeight()/2);

            public void undo() throws CannotUndoException {
                super.undo();
                landingpad.getRectangle().setLocation(oldX,oldY);
                setChangedAndNotify();
            }

            public void redo() throws CannotRedoException {
                super.redo();
                landingpad.getRectangle().setLocation(newX,newY);
                setChangedAndNotify();
            }
        };
        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        x -= landingpad.getRectangle().getWidth()/2;
        y -= landingpad.getRectangle().getHeight()/2;
        landingpad.getRectangle().setLocation(x,y);
        setChangedAndNotify();
    }


    public void mousePressedHandler(MouseEvent e) {
        int index;
        if(landingpad.getRectangle().contains(e.getPoint())){
            terrain.inCircle = -1;

            landingpad.setMouseInside(true);
            Point2d point = new Point2d(landingpad.getRectangle().getX(), landingpad.getRectangle().getY());
            landingpad.setBeforeDragPosition(point);
        }
        else if((index = terrain.insideCircle(e)) != -1){
            landingpad.setMouseInside(false);

            terrain.inCircle = index;
            terrain.setBeforeDraggedY((int)terrain.circles.get(index).getCenterY());
        }
        else{
            landingpad.setMouseInside(false);
            terrain.inCircle = -1;
        }
        setChangedAndNotify();
    }

    public void mouseDraggedHandler(MouseEvent e) {
        if(landingpad.isMouseInside()){
            int width = (int)landingpad.getRectangle().getWidth();
            int height = (int)landingpad.getRectangle().getHeight();

            int x = e.getX()-width/2;
            int y = e.getY()-height/2;

            if(x+width > worldBounds.width){
                x = (int)worldBounds.width-width;
            }
            else if(x < 0){
                x = 0;
            }

            if(y+height > worldBounds.height){
                y = (int)worldBounds.height-height;
            }
            else if(y < 0){
                y = 0;
            }

            landingpad.getRectangle().setLocation(x, y);
        }
        else if(terrain.inCircle != -1){
            int index = terrain.inCircle;

            int y = e.getY();

            if(y > worldBounds.height){
                y = (int)worldBounds.height;
            }
            else if(y < 0){
                y = 0;
            }

            terrain.circles.get(index).setFrame(terrain.circles.get(index).getX(), y-15, 30,30);
            terrain.remakeTerrain();
        }
        setChangedAndNotify();
    }

    public void mouseReleasedHandler(MouseEvent e) {
        if(landingpad.isMouseInside()){
            UndoableEdit undoableEdit = new AbstractUndoableEdit() {
                int width = (int)landingpad.getRectangle().getWidth();
                int height = (int)landingpad.getRectangle().getHeight();
                final int oldX = (int)landingpad.getbeforeDragPosition().getX();
                final int oldY = (int)landingpad.getbeforeDragPosition().getY();
                final int newX = e.getX()-width/2;
                final int newY = e.getY()-height/2;

                public void undo() throws CannotUndoException {
                    super.undo();
                    landingpad.getRectangle().setLocation(oldX,oldY);
                    setChangedAndNotify();
                }

                public void redo() throws CannotRedoException {
                    super.redo();
                    landingpad.getRectangle().setLocation(newX,newY);
                    setChangedAndNotify();
                }

            };
            undoManager.addEdit(undoableEdit);
        }
        else if(terrain.inCircle != -1){
            int index = terrain.inCircle;
            UndoableEdit undoableEdit = new AbstractUndoableEdit() {
                final int oldY = terrain.getBeforeDraggedY();

                public void undo() throws CannotUndoException {
                    super.undo();
                    terrain.circles.get(index).setFrame(terrain.circles.get(index).getX(), oldY, 30,30);
                    terrain.remakeTerrain();
                    setChangedAndNotify();
                }

                public void redo() throws CannotRedoException {
                    super.redo();
                    terrain.circles.get(index).setFrame(terrain.circles.get(index).getX(), e.getY()-15, 30,30);
                    terrain.remakeTerrain();
                    setChangedAndNotify();
                }
            };
            undoManager.addEdit(undoableEdit);
        }
        // Add this undoable edit to the undo manager

        setChangedAndNotify();
    }

    public void keyPressHandler(KeyEvent e){
        char key = e.getKeyChar();
        if(key == ' '){
            if(ship.hasCrashed() || ship.hasLanded()){
                ship.reset(new Point2d(worldBounds.width/2, 50));
            }
            else{
                ship.setPaused(!ship.isPaused());
            }
        }
        else if(key == 'w'){
            ship.thrustUp();
        }
        else if(key == 'a'){
            ship.thrustLeft();
        }
        else if(key == 's'){
            ship.thrustDown();
        }
        else if(key == 'd'){
            ship.thrustRight();
        }
    }

    // World
    // - - - - - - - - - - -
    public final Rectangle2D getWorldBounds() {
        return worldBounds;
    }

    Rectangle2D.Double worldBounds;


    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

    public void undo() {
        if (canUndo())
            undoManager.undo();
    }
    public void redo() {
        if (canRedo())
            undoManager.redo();
    }
    public boolean canUndo() {
        return undoManager.canUndo();
    }
    public boolean canRedo() {
        return undoManager.canRedo();
    }

}



