package nightgames.gui.keybinds;

import nightgames.gui.useraction.UserAction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static nightgames.gui.useraction.UserAction.*;

/**
 * TODO: Write class-level documentation.
 */
final class DefaultKeybinds {
    static final Map<UserAction, String> KEYMAP;
    static {
        Map<UserAction, String> keymapBuilder = new HashMap<>();
        keymapBuilder.put(CONTINUE, " ");
        keymapBuilder.put(PAGE_NEXT, "`");
        keymapBuilder.put(PAGE_PREV, "~");
        keymapBuilder.put(TACTICS_ALL, "1");
        keymapBuilder.put(TACTICS_AROUSE, "2");
        keymapBuilder.put(TACTICS_POSITIONING, "3");
        keymapBuilder.put(TACTICS_HURT, "4");
        keymapBuilder.put(TACTICS_MISC, "5");
        keymapBuilder.put(TACTICS_RECOVERY, "6");
        keymapBuilder.put(TACTICS_MANIPULATION, "7");
        keymapBuilder.put(POSITION_01, "Q");
        keymapBuilder.put(POSITION_02, "W");
        keymapBuilder.put(POSITION_03, "E");
        keymapBuilder.put(POSITION_04, "R");
        keymapBuilder.put(POSITION_05, "T");
        keymapBuilder.put(POSITION_06, "Y");
        keymapBuilder.put(POSITION_07, "A");
        keymapBuilder.put(POSITION_08, "S");
        keymapBuilder.put(POSITION_09, "D");
        keymapBuilder.put(POSITION_10, "F");
        keymapBuilder.put(POSITION_11, "G");
        keymapBuilder.put(POSITION_12, "H");
        keymapBuilder.put(POSITION_13, "Z");
        keymapBuilder.put(POSITION_14, "X");
        keymapBuilder.put(POSITION_15, "C");
        keymapBuilder.put(POSITION_16, "V");
        keymapBuilder.put(POSITION_17, "B");
        keymapBuilder.put(POSITION_18, "N");
        KEYMAP = Collections.unmodifiableMap(keymapBuilder);
    }
}
