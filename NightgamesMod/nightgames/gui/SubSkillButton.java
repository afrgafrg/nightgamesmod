package nightgames.gui;

import nightgames.skills.Skill;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class SubSkillButton extends ValueButton<Skill> {
    private static final long serialVersionUID = -3177604366435328960L;
    protected Skill skill;
    private String choice;

    public SubSkillButton(final Skill skill, final String choice, CompletableFuture<Skill> chosenSkill) {
        super(skill, choice, chosenSkill);
        this.choice = choice;
        this.skill = skill;
        getButton().setOpaque(true);
        getButton().setBorderPainted(false);
        getButton().setFont(new Font("Baskerville Old Face", Font.PLAIN, 18));
        getButton().setBackground(new Color(200, 200, 200));
    }

    @Override protected void run() {
        skill.choice = this.choice;
        super.run();
    }

    @Override
    public String getText() {
        return choice;
    }
}
