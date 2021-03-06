package nightgames.gui;

import nightgames.global.Formatter;
import nightgames.skills.TacticGroup;
import nightgames.skills.Tactics;

import javax.swing.border.LineBorder;
import java.awt.*;

public class SwitchTacticsButton extends KeyableButton {
    private static final long serialVersionUID = -3949203523669294068L;
    private String label;
    public SwitchTacticsButton(TacticGroup group) {
        super(Formatter.capitalizeFirstLetter(group.name()));
        label = Formatter.capitalizeFirstLetter(group.name());
        getButton().setBorderPainted(false);
        getButton().setOpaque(true);
        getButton().setFont(new Font("Baskerville Old Face", Font.PLAIN, 14));
        Color bgColor = new Color(80, 220, 120);
        for (Tactics tactic : Tactics.values()) {
            if (tactic.getGroup() == group) {
                bgColor = tactic.getColor();
                break;
            }
        }

        getButton().setBackground(bgColor);
        getButton().setMinimumSize(new Dimension(0, 20));
        getButton().setForeground(foregroundColor(bgColor));
        setBorder(new LineBorder(getButton().getBackground(), 3));
        int nSkills = GUI.gui.nSkillsForGroup(group);
        getButton().setText(Formatter.capitalizeFirstLetter(group.name()) + " [" + nSkills + "]");
        if (nSkills == 0 && group != TacticGroup.all) {
            getButton().setEnabled(false);
            getButton().setForeground(Color.WHITE);
            getButton().setBackground(getButton().getBackground().darker());
        }

        getButton().addActionListener(arg0 -> {
            GUI.gui.switchTactics(group);
        });
        setLayout(new BorderLayout());
        add(getButton());
    }

    private static Color foregroundColor(Color bgColor) {
        float hsb[] = new float[3];
        Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getRed(), hsb);
        if (hsb[2] < .6) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    @Override
    public String getText() {
        return label;
    }
}
