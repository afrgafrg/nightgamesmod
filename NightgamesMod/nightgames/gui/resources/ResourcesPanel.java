package nightgames.gui.resources;

import nightgames.characters.Player;
import nightgames.gui.GUIColors;

import javax.swing.*;
import java.awt.*;

/**
 * Container for meters representing Player resources.
 */
public class ResourcesPanel extends JPanel {
    private static final long serialVersionUID = -3243083061913539482L;

    public ResourcesPanel(Player player) {
        setBackground(GUIColors.bgDark.color);
        setLayout(new GridLayout(0, 4, 0, 0));

        ResourceWidget stamina = new ResourceWidget(player.getStamina());
        add(stamina);
        ResourceWidget arousal = new ResourceWidget(player.getArousal());
        add(arousal);
        ResourceWidget mojo = new ResourceWidget(player.getMojo());
        add(mojo);
        ResourceWidget willpower = new ResourceWidget(player.getWillpower());
        add(willpower);
    }
}
