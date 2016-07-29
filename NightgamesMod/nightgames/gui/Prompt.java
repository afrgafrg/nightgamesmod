package nightgames.gui;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: Write class-level documentation.
 */
interface Prompt {
    default void prompt(CommandButton... choices) {
        prompt(Arrays.asList(choices));
    }

    void prompt(List<CommandButton> choices);
}
