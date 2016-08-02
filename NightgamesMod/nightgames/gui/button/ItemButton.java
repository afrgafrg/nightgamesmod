package nightgames.gui.button;

import nightgames.items.Item;

/**
 * TODO: Write class-level documentation.
 */
public class ItemButton extends ValueButton<Item> {

    private static final long serialVersionUID = 3200753975433797292L;

    public ItemButton(Item item) {
        super(item, item.getName());
        setToolTipText(item.getDesc());
    }
}
