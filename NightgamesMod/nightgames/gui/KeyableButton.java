package nightgames.gui;

import org.apache.commons.lang3.text.WordUtils;

import javax.swing.*;
import java.awt.*;

// TODO: make a button class that completes a future
public abstract class KeyableButton extends JPanel {
    private static final long serialVersionUID = -2379908542190189603L;
    private final JButton button;

    public KeyableButton(String text) {
        this.button = new JButton(text);
        this.setLayout(new BorderLayout());
        this.add(button);
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    public void call() {
        button.doClick();
    }

    protected static String formatHTMLMultiline(String original, String hotkeyExtra) {
        String out = WordUtils
                        .wrap(original.replace("<", "&lt").replace(">", "&gt"), Math.max(30, original.length() * 2 / 3), "<br/>", false);
        // do not word wrap the hotkey extras, since it looks pretty bad.
        return String.format("<html><center>%s%s</center></html>", out, hotkeyExtra);
    }

    protected void resetFontSize() {
        if (getButton().getText().contains("<br/>")) {
            getButton().setFont(new Font("Baskerville Old Face", 0, 14));
        } else {
            getButton().setFont(new Font("Baskerville Old Face", 0, 18));
        }
    }

    public abstract String getText();

    public void setHotkeyTextTo(String string) {
        button.setText(String.format("%s [%s]", getText(), string));
    }

    public void clearHotkeyText() {
        button.setText(getText());
    }

    public JButton getButton() {
        return button;
    }
}
