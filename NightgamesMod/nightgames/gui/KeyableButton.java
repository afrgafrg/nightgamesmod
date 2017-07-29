package nightgames.gui;

import nightgames.gui.useraction.Actionable;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public abstract class KeyableButton extends JPanel implements Actionable {
    private static final long serialVersionUID = -2379908542190189603L;
    private final JButton button;

    public KeyableButton(String text) {
        this.button = new JButton(text);
        this.setLayout(new BorderLayout());
        this.add(button);
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    @Override public void keyActivated() {
        button.doClick();
    }

    @Override public void setUserAction(String string) {
        button.setText(String.format("%s [%s]", getText(), string));
    }

    @Override public void clearUserAction() {
        button.setText(getText());
    }

    public JButton getButton() {
        return button;
    }
}
