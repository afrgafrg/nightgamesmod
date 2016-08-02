package nightgames.gui.button;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: Write class-level documentation.
 */
public class GameButton extends JButton {
    private static final long serialVersionUID = -4653184610189682770L;
    private static Font defaultFont = new Font("Baskerville Old Face", 0, 18);

    public GameButton() {
        this.setFont(defaultFont);
    }

    public GameButton(String text) {
        super(text);
        this.setFont(defaultFont);
    }
}
