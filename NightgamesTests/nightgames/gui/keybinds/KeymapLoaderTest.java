package nightgames.gui.keybinds;

import com.google.gson.JsonObject;
import nightgames.gui.useraction.UserAction;
import nightgames.json.JsonUtils;
import nightgames.json.ParameterizedMapType;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nightgames.gui.useraction.UserAction.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * TODO: Write class-level documentation.
 *
 * TODO: If unable to load hotkeys, fall back to default bindings
 */
public class KeymapLoaderTest {
    /**
     * Given the parsed Json from a keymap data file, return an ordered map with the actions in the order they
     * appeared in the file.
     *
     * @param object A JsonObject containing the keymap from a keymap data file.
     * @return An map with keys in the order they appeared in the file.
     */
    private LinkedHashMap<UserAction, String> orderedKeymapFromJson(JsonObject object) {
        Type type = new ParameterizedMapType<>(UserAction.class, String.class, LinkedHashMap.class);
        return JsonUtils.gson.fromJson(object, type);
    }

    @Test public void saveSorted() throws Exception {
        Map<UserAction, String> beforeMap = DefaultKeybinds.KEYMAP;
        Writer mockFile = new StringWriter();
        KeymapLoader.saveKeybinds(mockFile, beforeMap);
        String savedJson = mockFile.toString();
        LinkedHashMap<UserAction, String> afterMap =
                        orderedKeymapFromJson(JsonUtils.rootJson(new StringReader(savedJson)).getAsJsonObject());

        // Key bindings should be stored in the order they are defined in the UserAction enum.
        List<UserAction> afterMapActionOrder = afterMap.keySet().stream().collect(Collectors.toList());
        List<UserAction> afterMapSortedOrder = afterMapActionOrder.stream().sorted().collect(Collectors.toList());
        assertThat(afterMapActionOrder, equalTo(afterMapSortedOrder));
    }

    @Test public void savePartialKeymap() throws Exception {
        Map<UserAction, String> partialKeymap = new HashMap<>();
        partialKeymap.put(CONTINUE, " ");
        partialKeymap.put(PAGE_NEXT, "X");
        partialKeymap.put(PAGE_PREV, "V");
        partialKeymap.put(POSITION_05, "R");

        Path tempKeymapFile = Files.createTempFile("","");
        KeymapLoader.saveKeybinds(tempKeymapFile, partialKeymap);
        Map<UserAction, String> loadedKeymap = KeymapLoader.loadKeybindsOrDefault(tempKeymapFile);
        assertThat(loadedKeymap, equalTo(partialKeymap));
    }

    @Test public void fallBackToDefaults() throws Exception {
        Path wontwork = Files.createTempFile("", "");
        Map<UserAction, String> shouldBeDefaultBinds = KeymapLoader.loadKeybindsOrDefault(wontwork);
        assertThat(shouldBeDefaultBinds, equalTo(DefaultKeybinds.KEYMAP));
    }

    @Test public void successfulLoad() throws Exception {
        Map<UserAction, String> expectedKeymap = new HashMap<>();
        expectedKeymap.put(CONTINUE, "A");
        expectedKeymap.put(PAGE_NEXT, "B");
        expectedKeymap.put(PAGE_PREV, "C");
        expectedKeymap.put(TACTICS_ALL, "D");
        expectedKeymap.put(TACTICS_AROUSE, "E");
        expectedKeymap.put(TACTICS_POSITIONING, "F");
        expectedKeymap.put(TACTICS_HURT, "G");
        expectedKeymap.put(TACTICS_MISC, "H");
        expectedKeymap.put(TACTICS_RECOVERY, "I");
        expectedKeymap.put(TACTICS_MANIPULATION, "J");
        expectedKeymap.put(POSITION_01, "K");
        expectedKeymap.put(POSITION_02, "L");
        expectedKeymap.put(POSITION_03, "M");
        expectedKeymap.put(POSITION_04, "N");
        expectedKeymap.put(POSITION_05, "O");
        expectedKeymap.put(POSITION_06, "P");
        expectedKeymap.put(POSITION_07, "Q");
        expectedKeymap.put(POSITION_08, "R");
        expectedKeymap.put(POSITION_09, "S");
        expectedKeymap.put(POSITION_10, "T");
        expectedKeymap.put(POSITION_11, "U");
        expectedKeymap.put(POSITION_12, "V");
        expectedKeymap.put(POSITION_13, "W");
        expectedKeymap.put(POSITION_14, "X");
        expectedKeymap.put(POSITION_15, "Y");
        expectedKeymap.put(POSITION_16, "Z");
        expectedKeymap.put(POSITION_17, "1");
        expectedKeymap.put(POSITION_18, "2");

        Path shouldwork = Paths.get("NightgamesTests/nightgames/gui/keybinds/non_default_keymap.json");
        Map<UserAction, String> loadedKeymap = KeymapLoader.loadKeybindsOrDefault(shouldwork);
        assertThat(loadedKeymap, equalTo(expectedKeymap));
    }
}
