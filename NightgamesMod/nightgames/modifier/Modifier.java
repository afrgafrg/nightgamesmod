package nightgames.modifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nightgames.actions.Action;
import nightgames.characters.Character;
import nightgames.global.DebugFlags;
import nightgames.global.Match;
import nightgames.json.JsonUtils;
import nightgames.modifier.skill.SkillModifier;
import nightgames.modifier.standard.*;

@SuppressWarnings("unused")
public interface Modifier {

    /**
     * Ensure that the character has an appropriate outfit
     */
    void handleOutfit(Character c);

    /**
     * Ensure that the character has a legal inventory
     */
    void handleItems(Character c);

    /**
     * Apply any required statuses
     */
    void handleStatus(Character c);

    /**
     * Get a SkillModifier specific to the current Match
     */
    SkillModifier getSkillModifier();

    /**
     * Process non-combat turn
     */
    void handleTurn(Character c, Match m);

    /**
     * Undo all changes to the character's inventory made by handleItems
     */
    void undoItems(Character c);

    boolean allowAction(Action act, Character c, Match m);

    int bonus();

    boolean isApplicable();

    String name();

    String intro();

    String acceptance();
    
    default void extraWinnings(Character player, int score) {}
}
