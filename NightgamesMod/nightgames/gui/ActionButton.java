package nightgames.gui;

import nightgames.actions.Action;
import nightgames.characters.Character;
import nightgames.global.Global;

import javax.swing.*;
import java.awt.*;

// TODO: Consider making this a RunnableButton.
public class ActionButton extends CommandButton {
    /**
     * 
     */
    private static final long serialVersionUID = 2822534455509003521L;
    protected Action action;
    protected Character user;

    public ActionButton(Action action, Character user) {
        super(action.toString(), true); // can unblock
        this.action = action;
        this.user = user;
        addActionListener(() -> {
            Global.global.gui().clearText();
            this.action.execute(this.user);
            if (!this.action.freeAction()) {
                Global.global.getMatch().resume();
            }
        });
    }
}
