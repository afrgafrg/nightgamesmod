package nightgames.gui;

import nightgames.global.Global;

import javax.swing.*;
import java.awt.*;

public class SaveButton extends CommandButton {

    /**
     * 
     */
    private static final long serialVersionUID = 5665392145091151054L;

    public SaveButton() {
        super("Save");  // does not unblock
        addActionListener(() -> Global.global.saveWithDialog());
    }
}
