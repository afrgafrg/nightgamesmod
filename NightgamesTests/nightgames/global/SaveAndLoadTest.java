package nightgames.global;

import com.google.gson.JsonObject;
import nightgames.characters.*;
import nightgames.characters.Character;
import nightgames.gui.TestGUI;
import org.hamcrest.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests for saving and loading game data.
 */
public class SaveAndLoadTest {
    private Path savePath = new File("NightGamesTests/nightgames/global/test_save.ngs").toPath();
    private GameState gameState;

    @BeforeClass public static void setUpSaveAndLoadTest() throws Exception {
        Main.initialize();
        new TestGUI();
    }

    @Before public void setUp() throws Exception {
        gameState = new GameState();
        gameState.reset();
    }

    @Test public void testLoadAndSave() throws Exception {
        SaveFile.load(savePath.toFile());
        SaveData firstLoadData = gameState.saveData();
        Path tempSave = Files.createTempFile("", "");
        SaveFile.save(tempSave.toFile());
        SaveFile.load(tempSave.toFile());
        SaveData reloadedData = gameState.saveData();
        assertThat(reloadedData.players, equalTo(firstLoadData.players));
        for (Character player : firstLoadData.players) {
            Character reloaded = reloadedData.players.stream().filter(p -> p.equals(player)).findFirst()
                            .orElseThrow(AssertionError::new);
            assertThat(reloaded, CharacterStatMatcher.statsMatch(player));
        }
        assertThat(reloadedData, equalTo(firstLoadData));
    }

    @Test public void testSaveAndLoadAffection() throws Exception {
        BlankPersonality beforeNPC = new BlankPersonality("Affectionate");
        Player human = new Player("testPlayer");
        beforeNPC.character.gainAffection(human, 10);
        JsonObject npcJson = beforeNPC.character.save();
        BlankPersonality afterNPC = new BlankPersonality("AffectionateLoad");
        afterNPC.character.load(npcJson);
        assertThat(afterNPC.character.getAffections(), equalTo(beforeNPC.character.getAffections()));
    }

    private static class CharacterStatMatcher extends TypeSafeMatcher<Character> {
        private Character me;

        CharacterStatMatcher(Character me) {
            this.me = me;
        }

        @Override public boolean matchesSafely(Character other) {
            return me.hasSameStats(other);
        }

        @Override public void describeMismatchSafely(Character other, Description description) {
            description.appendText("was").appendValue(other.printStats());
        }

        @Override public void describeTo(Description description) {
            description.appendText(me.printStats());
        }

        @Factory static CharacterStatMatcher statsMatch(Character me) {
            return new CharacterStatMatcher(me);
        }
    }
}
