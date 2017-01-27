package nightgames.gui.keybinds;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import nightgames.gui.useraction.UserAction;
import nightgames.json.JsonUtils;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class KeymapLoader {
    /**
     * Attempts to load keymap from file; uses default keybinds otherwise.
     *
     * @param keymapFile The file to load key bindings from.
     * @return A map of user actions to key bindings.
     */
    public static Map<UserAction, String> loadKeybindsOrDefault(Path keymapFile) {
        Map<UserAction, String> keymap;
        keymap = loadKeybinds(keymapFile).orElse(new HashMap<>(DefaultKeybinds.KEYMAP));

        return keymap;
    }

    private static Optional<Map<UserAction, String>> loadKeybinds(Path keymapFile) {
        try {
            JsonObject keymapjson = JsonUtils.rootJson(keymapFile).getAsJsonObject();
            return Optional.of(JsonUtils.mapFromJson(keymapjson, UserAction.class, String.class));
        } catch (IllegalStateException ise) {
            System.err.println("Could not parse keymap: " + ise);
        } catch (IOException ioe) {
            System.err.println("Could not load keybinds: " + ioe);
        }
        return Optional.empty();
    }

    public static void saveKeybinds(Path keymapFile, Keybinds keybinds) {
        saveKeybinds(keymapFile, keybinds.mapForSave());
    }

    public static void saveKeybinds(Path keymapFile, Map<UserAction, String> keymap) {
        try {
            saveKeybinds(Files.newBufferedWriter(keymapFile), keymap);
            System.out.println("Saved keybinds to " + keymapFile);
        } catch (IOException ioe) {
            System.err.println("Could not save keybinds: " + ioe);
        }
    }

    /**
     * Writes the keybinds in an order that is helpful for users wanting to edit the keymap data file.
     * @param writer Where to put the data. Usually a FileWriter.
     * @param keymap The keymap to be saved.
     */
    public static void saveKeybinds(Writer writer, Map<UserAction, String> keymap) {
        // TreeMap sorts a map's keys using the keys' "natural order". In this case, it's the order of the
        // UserAction enum entries.
        keymap = new TreeMap<>(keymap);
        try (JsonWriter saver = new JsonWriter(writer)) {
            saver.setIndent("  ");
            JsonObject keymapjson = JsonUtils.JsonFromMap(keymap);
            JsonUtils.gson.toJson(keymapjson, saver);
        } catch (IOException ioe) {
            System.err.println("Could not save keybinds: " + ioe);
        }
    }
}
