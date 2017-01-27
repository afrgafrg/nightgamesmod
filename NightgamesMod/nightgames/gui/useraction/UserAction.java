package nightgames.gui.useraction;

import java.awt.*;
import java.util.Map;

/**
 * All actions that can be performed by the user. Can activated by mouse click or hotkey.
 */
public enum UserAction {
    // Default button. Can be labeled as "continue", "next", etc.
    CONTINUE,

    // Buttons for changing button pages when there is an excess of options.
    PAGE_NEXT,
    PAGE_PREV,

    // Buttons for activating tactics filters during combat.
    TACTICS_ALL,
    TACTICS_AROUSE,
    TACTICS_POSITIONING,
    TACTICS_HURT,
    TACTICS_MISC,
    TACTICS_RECOVERY,
    TACTICS_MANIPULATION,

    // Generic buttons based on position in the Command Panel.
    POSITION_01,
    POSITION_02,
    POSITION_03,
    POSITION_04,
    POSITION_05,
    POSITION_06,
    POSITION_07,
    POSITION_08,
    POSITION_09,
    POSITION_10,
    POSITION_11,
    POSITION_12,
    POSITION_13,
    POSITION_14,
    POSITION_15,
    POSITION_16,
    POSITION_17,
    POSITION_18;
}
