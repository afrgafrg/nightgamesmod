package nightgames.gui.button;

import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Skill;
import nightgames.skills.Stage;
import nightgames.skills.Tactics;

import javax.swing.border.LineBorder;
import java.awt.*;

public class SkillButton extends ValueButton<Skill> {

    private static final long serialVersionUID = -1253735466299929203L;
    protected Skill action;
    protected Combat combat;

    public SkillButton(final Skill skill, Combat c) {
        super(skill, skill.getLabel(c));
        boolean hasSubSkills = skill.subChoices(c).size() > 0;
        setBorderPainted(false);
        setOpaque(true);
        setFont(fontForStage(skill.getStage()));
        this.action = skill;
        String text = "<html>" + skill.describe(c);
        if (skill.type(c) == Tactics.damage) {
            setBackground(new Color(150, 0, 0));
        } else if (skill.type(c) == Tactics.pleasure) {
            setBackground(Color.PINK);
        } else if (skill.type(c) == Tactics.fucking) {
            setBackground(new Color(255, 100, 200));
        } else if (skill.type(c) == Tactics.positioning) {
            setBackground(new Color(0, 100, 0));
        } else if (skill.type(c) == Tactics.stripping) {
            setBackground(new Color(0, 100, 0));
        } else if (skill.type(c) == Tactics.debuff) {
            setBackground(Color.CYAN);
        } else if (skill.type(c) == Tactics.recovery || skill.type(c) == Tactics.calming) {
            setBackground(Color.WHITE);
        } else if (skill.type(c) == Tactics.summoning) {
            setBackground(Color.YELLOW);
        } else {
            setBackground(new Color(200, 200, 200));
        }
        setForeground(foregroundColor(skill.type(c)));

        if (skill.getMojoCost(c) > 0) {
            setBorder(new LineBorder(Color.RED, 3));
            text += "<br>Mojo cost: " + skill.getMojoCost(c);
        } else if (skill.getMojoBuilt(c) > 0) {
            setBorder(new LineBorder(new Color(53, 201, 255), 3));
            text += "<br>Mojo generated: " + skill.getMojoBuilt(c) + "%";
        } else {
            setBorder(new LineBorder(getBackground(), 3));
        }
        if (!skill.user().cooldownAvailable(skill)) {
            setEnabled(false);
            text += String.format("<br>Remaining Cooldown: %d turns", skill.user().getCooldown(skill));
            setForeground(Color.WHITE);
            setBackground(getBackground().darker());
        }

        text += "</html>";
        setToolTipText(text);
        combat = c;
        // TODO: Change this to get the subskill choice value from a prompt.
        addActionListener(arg0 -> {
            if (hasSubSkills) {
                Global.global.gui().commandPanel.removeAll();
                for (String choice : skill.subChoices()) {
                    Global.global.gui().commandPanel.add(new SubSkillButton(choice));
                }
                Global.global.gui().commandPanel.repaint();
                Global.global.gui().commandPanel.revalidate();
            } else {
                combat.act(SkillButton.this.action.user(), SkillButton.this.action, "");
            }
        });
        setLayout(new BorderLayout());
    }

    public void addIndex(int idx) {
        setText(getText() + " [" + idx + "]");
    }

    private static Color foregroundColor(Tactics tact) {
        switch (tact) {
            case damage:
            case positioning:
            case stripping:
                return Color.WHITE;
            default:
                return Color.BLACK;
        }
    }
    
    private static Font fontForStage(Stage stage) {
        switch (stage) {
            case FINISHER:
                return new Font("Baskerville Old Face", Font.BOLD, 18);
            case FOREPLAY:
                return new Font("Baskerville Old Face", Font.ITALIC, 18);
            default:
                return new Font("Baskerville Old Face", Font.PLAIN, 18);
            
        }
    }
}
