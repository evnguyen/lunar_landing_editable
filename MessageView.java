import javax.swing.*;
import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class MessageView extends JPanel implements Observer {

    // status messages for game
    JLabel fuel = new JLabel("fuel");
    JLabel speed = new JLabel("speed");
    JLabel message = new JLabel("message");

    GameModel model;

    public MessageView(GameModel model) {

        this.model = model;
        model.addObserver(this);

        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(fuel);
        add(speed);
        add(message);

        fuel.setText("Fuel: " + model.ship.getFuel());
        speed.setText("Speed: " + model.ship.getSpeed());
        message.setText("(Paused)");

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(model.ship.getFuel() < 10){
            fuel.setForeground(Color.RED);
        }
        else{
            fuel.setForeground(Color.WHITE);
        }
        fuel.setText("Fuel: " + model.ship.getFuel());


        //Truncate speed to 2 decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        String s = df.format(model.ship.getSpeed());
        if(model.ship.getSpeed() < model.ship.getSafeLandingSpeed()){
            speed.setForeground(Color.GREEN);
        }
        else{
            speed.setForeground(Color.WHITE);
        }
        speed.setText("Speed: " + s);


        String m;
        if(model.ship.hasCrashed()){
            m = "CRASH";
        }
        else if(model.ship.hasLanded()){
            m = "LANDED!";
        }
        else if(model.ship.isPaused()){
            m = "(Paused)";
        }
        else{
            m = "";
        }
        message.setText(m);
    }
}