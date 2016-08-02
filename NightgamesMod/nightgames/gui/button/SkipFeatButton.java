package nightgames.gui.button;

import nightgames.characters.Trait;

/**
 * TODO: Write class-level documentation.
 */
public class SkipFeatButton extends CancelButton<Trait> {
    /**
     *
     */
    private static final long serialVersionUID = -4949332486895844480L;

    public SkipFeatButton() {
        super("Skip");
        setToolTipText("Save the trait point for later.");
    }
}
