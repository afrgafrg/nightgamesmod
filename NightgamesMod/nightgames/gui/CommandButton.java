package nightgames.gui;

import javax.swing.*;
import java.awt.*;

/**
 * CommandButtons show up in the Command Pane underneath the main content panel. They can unblock a
 * BlockingPrompt on click, in addition to other effects.
 */
public class CommandButton extends JButton {
    private static final long serialVersionUID = 7083172844328297505L;
    // for use with BlockingPrompt
    final boolean canUnblock;

    public CommandButton() {
        this(false);
    }

    public CommandButton(boolean canUnblock) {
        this("", canUnblock);
    }

    public CommandButton(String text) {
        this(text, false);
    }

    public CommandButton(String text, boolean canUnblock) {
        super(text);
        this.canUnblock = canUnblock;
        this.setFont(new Font("Baskerville Old Face", 0, 18));
    }
}
