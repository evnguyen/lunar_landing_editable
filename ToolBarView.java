import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the edit toolbar
public class ToolBarView extends JPanel implements Observer {
    JButton undo = new JButton("Undo");
    JButton redo = new JButton("Redo");
    private GameModel model;

    public ToolBarView(GameModel model) {

        setLayout(new FlowLayout(FlowLayout.LEFT));

        this.model = model;
        this.model.addObserver(this);

        // prevent buttons from stealing focus
        undo.setFocusable(false);
        redo.setFocusable(false);

        add(undo);
        add(redo);

        //Show if able to undo/redo
        undo.setEnabled(model.canUndo());
        redo.setEnabled(model.canRedo());

        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                model.undo();
            }
        });
        // controller for redo menu item
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                model.redo();
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        undo.setEnabled(model.canUndo());
        redo.setEnabled(model.canRedo());
    }
}
