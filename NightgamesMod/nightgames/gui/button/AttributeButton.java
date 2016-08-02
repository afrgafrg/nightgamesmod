package nightgames.gui.button;

import nightgames.characters.Attribute;

/**
 * TODO: Write class-level documentation.
 */
public class AttributeButton extends ValueButton<Attribute> {
    private static final long serialVersionUID = -8860455413688200054L;

    public AttributeButton(Attribute att) {
        super(att, att.name());
    }
}
