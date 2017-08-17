package nightgames.characters;

import nightgames.global.GameState;
import nightgames.gui.GUI;
import nightgames.gui.KeyableButton;
import nightgames.gui.RunnableButton;

import java.io.Serializable;

public class AttributeButton implements Serializable {
    public static KeyableButton attributeButton(GUI gui, Attribute att) {
        return new RunnableButton(att.name(), () -> {
            gui.clearTextIfNeeded();
            GameState.gameState.characterPool.getPlayer().mod(att, 1);
            GameState.gameState.characterPool.getPlayer().availableAttributePoints -= 1;
            gui.refresh();
            Player.ding(gui);
        });
    }
}
