package nightgames.gui.keybinds;

import nightgames.gui.useraction.UserAction;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stores binding of hotkeys to actions.
 */
public class Keybinds {
    // One key may be mapped to exactly one action. This is not a hard requirement, just easier to implement.
    // May be worth adjusting if, e.g., context-sensitive keys become a requested feature.
    private Map<String, UserAction> keys2actions = new HashMap<>();
    private Map<UserAction, String> actions2keys = new HashMap<>();

    public Keybinds(Path keymapPath) {
        this(KeymapLoader.loadKeybindsOrDefault(keymapPath));
    }

    Keybinds(Map<UserAction, String> keymap) {
        keymap.forEach(this::loadBinding);
    }

    /**
     * Binds a key to an action only if that key-action pair does not yet exist.
     * @param action The action to attempt to bind.
     * @param key The key to attempt to bind.
     */
    private void loadBinding(UserAction action, String key) {
        UserAction existingAction = keys2actions.putIfAbsent(key, action);
        String existingKey = actions2keys.putIfAbsent(action, key);
        if (existingAction != null) {
            System.err.println("Action already bound to key: " + existingAction + ", " + key);
        }
        if (existingKey != null) {
            System.err.println("Key already bound to action: " + existingKey + ", " + action);
        }

    }

    /**
     * Given a hotkey, return the associated action.
     * @param hotkey The bound key.
     * @return An Optional of the action bound with hotkey, or Optional.empty if the key is not bound to an action.
     */
    public Optional<UserAction> actionFromHotkey(String hotkey) {
        return Optional.ofNullable(keys2actions.getOrDefault(hotkey, null));
    }

    /**
     * Given an action, return the hotkey bound to it.
     * @param action The keyed action.
     * @return An Optional of the hotkey bound to action, or Optional.empty if the action does not have a hotkey bound to it.
     */
    public Optional<String> hotkeyFromAction(UserAction action) {
        return Optional.ofNullable(actions2keys.getOrDefault(action, null));
    }

    Map<UserAction, String> mapForSave() {
        return actions2keys;
    }
}
