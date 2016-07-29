package nightgames.gui;

import nightgames.global.Global;

import javax.swing.*;
import java.awt.*;

public class SceneButton extends RunnableButton {
    private static final long serialVersionUID = -4333729595458261030L;
    private String choice;

    public SceneButton(String label) {
        this(label, label);
    }

    public SceneButton(String label, String choice) {
        this.choice = choice;
        super(label, () -> Global.global.current.respond(choice));
    }
}
