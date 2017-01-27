package nightgames.gui.keybinds;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static nightgames.gui.useraction.UserAction.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Tests for key bindings.
 *
 * TODO: Make sure keybinds are accessible after initial creation
 * TODO: Make sure keybinds work after bind(), both adding and replacing
 * TODO: Make sure keybinds don't work after unbind()
 */
public class KeybindsTest {
    @Test public void defaultKeymapFallback() throws Exception {
        // Should fall back to defaults
        Keybinds keybinds = new Keybinds(Paths.get("notarealfile.json"));
        assertThat(keybinds.actionFromHotkey("`"), equalTo(Optional.of(PAGE_NEXT)));
        assertThat(keybinds.hotkeyFromAction(PAGE_NEXT), equalTo(Optional.of("`")));
        assertThat(keybinds.actionFromHotkey("~"), equalTo(Optional.of(PAGE_PREV)));
        assertThat(keybinds.hotkeyFromAction(PAGE_PREV), equalTo(Optional.of("~")));
    }

    @Test public void nonDefaultKeymapSuccess() throws Exception {
        // Should not fall back to defaults
        Keybinds keybinds = new Keybinds(Paths.get("NightgamesTests/nightgames/gui/keybinds/non_default_keymap.json"));
        assertThat(keybinds.actionFromHotkey("A"), equalTo(Optional.of(CONTINUE)));
        assertThat(keybinds.hotkeyFromAction(CONTINUE), equalTo(Optional.of("A")));
        assertThat(keybinds.actionFromHotkey("2"), equalTo(Optional.of(POSITION_18)));
        assertThat(keybinds.hotkeyFromAction(POSITION_18), equalTo(Optional.of("2")));
    }
}

