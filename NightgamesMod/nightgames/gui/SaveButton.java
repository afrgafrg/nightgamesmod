package nightgames.gui;

import nightgames.global.SaveFile;

public class SaveButton extends RunnableButton {

    /**
     * 
     */
    private static final long serialVersionUID = 5665392145091151054L;

    public SaveButton() {
        super("Save");
    }

    @Override protected void run() {
        SaveFile.saveWithDialog();
    }
}
