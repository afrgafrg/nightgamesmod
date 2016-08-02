package nightgames.gui.button;

import nightgames.characters.Character;

/**
 * TODO: Write class-level documentation.
 */
public class CharacterButton extends ValueButton<String> {
    private static final long serialVersionUID = -1306671912601780678L;

    public CharacterButton(Character character) {
        super(character.getType(), character.getName());
    }
}
