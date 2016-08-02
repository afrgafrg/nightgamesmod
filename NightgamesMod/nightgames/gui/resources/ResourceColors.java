package nightgames.gui.resources;

import nightgames.characters.resources.Resource;
import nightgames.gui.GUIColors;

import java.awt.*;

/**
 * TODO: Write class-level documentation.
 */
enum ResourceColors {
    stamina(new Color(164, 8, 2)), arousal(new Color(254, 1, 107)), mojo(new Color(51, 153, 255)), willpower(
                    new Color(68, 170, 85)), barBackground(new Color(50, 50, 50)), background(GUIColors.bgDark.color);

    public Color color;

    ResourceColors(Color color) {
        this.color = color;
    }

    static ResourceColors fromResource(Resource resource) {
        switch (resource) {
            case STAMINA:
                return stamina;
            case AROUSAL:
                return arousal;
            case MOJO:
                return mojo;
            case WILLPOWER:
                return willpower;
        }
        throw new EnumConstantNotPresentException(ResourceColors.class, resource.name.toLowerCase());
    }
}
