package nightgames.global;

import nightgames.characters.Character;
import nightgames.characters.CharacterSex;
import nightgames.modifier.standard.NoModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Test version of GameState that exposes more handles for easier testing.
 */
public class TestGameState extends GameState {
    public TestGameState() {
        super("TestPlayer", Optional.empty(), new ArrayList<>(), CharacterSex.male, new HashMap<>());
        GameState.gameState = this;
    }

    @Override public void closeGame() {
        run = false;
    }

    public Match makeMatch(List<Character> participants) {
        return new Match(participants, new NoModifier());
    }
}
