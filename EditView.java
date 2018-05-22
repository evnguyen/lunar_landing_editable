import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import javax.vecmath.*;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
    private GameModel model;

    public EditView(GameModel model) {
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
        MouseInputListener mil = new MController();
        this.addMouseListener(mil);
        this.addMouseMotionListener(mil);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
                RenderingHints.VALUE_ANTIALIAS_ON);
        //Draw the "world"
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect((int)model.worldBounds.x,(int)model.worldBounds.y, (int)model.worldBounds.width, (int)model.worldBounds.height);

        //Draw the terrain
        g2.setColor(Color.DARK_GRAY);
        g2.fillPolygon(model.terrain.getTerrain());

        //Draw circles
        g2.setColor(Color.GRAY);
        for(int i = 0; i < model.terrain.circles.size(); i++){
            g2.drawOval((int)model.terrain.circles.get(i).getX(), (int)model.terrain.circles.get(i).getY()
                    ,30,30);
        }

        //Draw the landingpad
        g2.setColor(model.landingpad.getColour());
        g2.fill(model.landingpad.getRectangle());
    }

    private class MController extends MouseInputAdapter {
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                model.doubleClickHandler(e.getX(), e.getY());
            }
        }

        public void mousePressed(MouseEvent e) {
            model.mousePressedHandler(e);
        }

        public void mouseDragged(MouseEvent e) {
            model.mouseDraggedHandler(e);
        }

        public void mouseReleased(MouseEvent e) {
            model.mouseReleasedHandler(e);
        }
    }

}
