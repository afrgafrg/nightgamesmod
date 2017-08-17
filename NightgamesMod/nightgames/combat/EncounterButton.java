package nightgames.combat;

import nightgames.characters.Character;
import nightgames.global.Encs;
import nightgames.global.GameState;
import nightgames.global.Match;
import nightgames.gui.KeyableButton;
import nightgames.gui.RunnableButton;

import java.io.Serializable;

public class EncounterButton implements Serializable {
    public static KeyableButton encounterButton(String label, IEncounter enc, Character target, Encs choice) {
        return new RunnableButton(label, () -> {
            enc.parse(choice, GameState.gameState.characterPool.getPlayer(), target);
            Match.getMatch().resume();
        });
    }
}
