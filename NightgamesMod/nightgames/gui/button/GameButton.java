package nightgames.gui.button;

import nightgames.gui.useraction.Actionable;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: Write class-level documentation.
 */
public class GameButton extends JButton implements Actionable {
    private static final long serialVersionUID = -4653184610189682770L;
    private static Font defaultFont = new Font("Baskerville Old Face", 0, 18);

    public GameButton() {
        this.setFont(defaultFont);
    }

    public GameButton(String text) {
        super(text);
        this.setFont(defaultFont);
    }

    @Override public void setUserAction(String string) {
        // Add a little label in the top right with the hotkey, or something like that
    }

    @Override public void clearUserAction() {
        // Remove the hotkey label
    }


}
