package nightgames.gui;

import nightgames.global.Global;

import javax.swing.*;
import java.awt.*;

public class SceneButton extends CommandButton {
    /**
     * 
     */
    private static final long serialVersionUID = -4333729595458261030L;
    private String choice;

    public SceneButton(String label) {
        super(label, false); // Does not unblock, relies on scene responses
        choice = label;
        addActionListener(() -> Global.global.currentScene.respond(choice));
    }
}
