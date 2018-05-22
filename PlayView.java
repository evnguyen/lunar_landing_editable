import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import javax.vecmath.*;

// the actual game view
public class PlayView extends JPanel implements Observer {
    private GameModel model;

    public PlayView(GameModel model) {

        // needs to be focusable for keylistener
        setFocusable(true);

        this.model = model;
        this.model.addObserver(this);
        this.registerControllers();
        setBackground(Color.BLACK);

    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    private void registerControllers() {
        KeyListener kl = new KController();
        this.addKeyListener(kl);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);

        //Transform view such that it always centers around the ship
        AffineTransform m = g2.getTransform();
        Point2d point = model.ship.getPosition();
        g2.translate((model.worldBounds.width - 5) /2, (model.worldBounds.height - 5) /2);
        g2.scale(3,3);
        //We essentially make the ship the "origin" instead of 0,0
        g2.translate(-point.getX(), -point.getY());

        //Draw the "world"
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect((int)model.worldBounds.x,(int)model.worldBounds.y, (int)model.worldBounds.width, (int)model.worldBounds.height);

        //Draw the terrain
        g2.setColor(Color.DARK_GRAY);
        g2.fillPolygon(model.terrain.getTerrain());

        //Draw the landing pad
        g2.setColor(model.landingpad.getColour());
        g2.fill(model.landingpad.getRectangle());

        //Draw the ship
        g2.setColor(Color.BLUE);
        g2.fill(model.ship.getShape());

        g2.setTransform(m);

    }

    private class KController extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            model.keyPressHandler(e);
        }
    }

}
