package nightgames.daytime;

import nightgames.characters.Character;

/**
 * Something that can be purchased in a shop, like an item, piece of clothing, or transformation.
 */
public interface Purchaseable {
    boolean available(Character purchaser);

    boolean affordable(Character purchaser);

    void purchase(Character purchaser);
}
