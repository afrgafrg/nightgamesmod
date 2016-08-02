package nightgames.gui;

import java.awt.*;

public enum GUIColors {
    bgDark(new Color(0, 10, 30)), bgLight(new Color(18, 30, 49)), bgGrey(new Color(35, 35, 35)), textColorLight(
                    new Color(240, 240, 255));

    public Color color;

    GUIColors(Color color) {
        this.color = color;
    }

}
