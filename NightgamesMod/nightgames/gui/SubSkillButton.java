package nightgames.gui;

import nightgames.combat.Combat;
import nightgames.skills.Skill;

import java.awt.*;

public class SubSkillButton extends CommandButton {
    /**
     * 
     */
    private static final long serialVersionUID = -3177604366435328960L;
    protected Skill action;
    private String choice;

    public SubSkillButton(final Skill action, final String choice, Combat c) {
        super(choice, true);    // can unblock
        this.choice = choice;
        getButton().setOpaque(true);
        getButton().setBorderPainted(false);
        getButton().setFont(new Font("Baskerville Old Face", Font.PLAIN, 18));
        this.action = action;
        getButton().setBackground(new Color(200, 200, 200));
        getButton().addActionListener(arg0 -> c.act(action.user(), action, choice));
    }

    @Override
    public String getText() {
        return choice;
    }
}
