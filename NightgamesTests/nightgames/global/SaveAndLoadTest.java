package nightgames.global;

import com.google.gson.JsonObject;
import nightgames.characters.BlankPersonality;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.gui.TestGUI;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
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

    @BeforeClass public static void setUpSaveAndLoadTest() throws Exception {
        Main.initialize();
        new TestGUI();
    }

    @Test public void testLoadAndSave() throws Exception {
        Path savePath = new File("NightGamesTests/nightgames/global/test_save.ngs").toPath();
        GameState gameState = new GameState(SaveFile.load(savePath.toFile()));
        SaveData firstLoadData = new SaveData(gameState);
        Path tempSave = Files.createTempFile("", "");
        SaveFile.save(tempSave.toFile(), firstLoadData);
        gameState = new GameState(SaveFile.load(tempSave.toFile()));
        SaveData reloadedData = new SaveData(gameState);
        assertThat(reloadedData.npcs, equalTo(firstLoadData.npcs));
        for (NPC firstLoadNpc : firstLoadData.npcs) {
            NPC reloadedNpc = reloadedData.npcs.stream().filter(p -> p.equals(firstLoadNpc)).findFirst()
                            .orElseThrow(AssertionError::new);
            assertThat(reloadedNpc, CharacterStatMatcher.statsMatch(firstLoadNpc));
            assertThat(reloadedNpc.status, equalTo(firstLoadNpc.status));
        }
        assertThat(reloadedData.player, equalTo(firstLoadData.player));
        assertThat(reloadedData.player.status, equalTo(firstLoadData.player.status));
        assertThat(reloadedData.player, CharacterStatMatcher.statsMatch(firstLoadData.player));
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

    @Test public void testNPCAvailability() throws Exception {
        File saveFile = new File("NightGamesTests/nightgames/global/test_save.ngs");
        SaveData data = SaveFile.load(saveFile);
        for (NPC npc : data.npcs) {
            if ("Reyka".equals(npc.getType())) {
                assertThat("Reyka should not be available", npc.available, equalTo(false));
            }
        }
    }

    /**
     * Makes sure older save files are properly updated on load.
     */
    @Test public void testLoadLegacySave() throws Exception {
        Path savePath = new File("NightGamesTests/nightgames/global/test_save_legacy.ngs").toPath();
        GameState gameState = new GameState(SaveFile.load(savePath.toFile()));
        SaveData firstLoadData = new SaveData(gameState);
        Path tempSave = Files.createTempFile("", "");
        SaveFile.save(tempSave.toFile(), firstLoadData);
        gameState = new GameState(SaveFile.load(tempSave.toFile()));
        SaveData reloadedData = new SaveData(gameState);
        assertThat(reloadedData.npcs, equalTo(firstLoadData.npcs));
        for (NPC firstLoadNpc : firstLoadData.npcs) {
            NPC reloadedNpc = reloadedData.npcs.stream().filter(p -> p.equals(firstLoadNpc)).findFirst()
                            .orElseThrow(AssertionError::new);
            assertThat(reloadedNpc, CharacterStatMatcher.statsMatch(firstLoadNpc));
            assertThat(reloadedNpc.status, equalTo(firstLoadNpc.status));
        }
        assertThat(reloadedData.player, equalTo(firstLoadData.player));
        assertThat(reloadedData.player.status, equalTo(firstLoadData.player.status));
        assertThat(reloadedData.player, CharacterStatMatcher.statsMatch(firstLoadData.player));
        assertThat(reloadedData, equalTo(firstLoadData));
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
