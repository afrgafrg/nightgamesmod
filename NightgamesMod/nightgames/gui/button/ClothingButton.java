package nightgames.gui.button;

import nightgames.items.clothing.Clothing;

/**
 * TODO: Write class-level documentation.
 */
public class ClothingButton extends ValueButton<Clothing> {

    private static final long serialVersionUID = 3200753975433797292L;

    public ClothingButton(Clothing clothing) {
        super(clothing, clothing.getName());
        setToolTipText(clothing.getToolTip());
    }
}
