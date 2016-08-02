package nightgames.gui.button;

import java.awt.*;

public class SubSkillButton extends ValueButton<String> {
    /**
     * 
     */
    private static final long serialVersionUID = -3177604366435328960L;

    public SubSkillButton(final String choice) {
        super(choice, choice);
        setOpaque(true);
        setBorderPainted(false);
        setBackground(new Color(200, 200, 200));
    }
}
