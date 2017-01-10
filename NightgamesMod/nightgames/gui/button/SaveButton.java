package nightgames.gui.button;

import nightgames.global.Global;

import java.awt.*;

public class SaveButton extends GameButton {

    /**
     *
     */
    private static final long serialVersionUID = 5665392145091151054L;

    public SaveButton() {
        super("Save");  // does not unblock
        addActionListener(event -> Global.global.saveWithDialog());
    }
}
