package nightgames.combat;

import nightgames.characters.Character;
import nightgames.global.Encs;
import nightgames.global.GameState;
import nightgames.global.Match;
import nightgames.gui.GUI;
import nightgames.gui.KeyableButton;
import nightgames.gui.RunnableButton;
import nightgames.items.Item;
import nightgames.trap.Trap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nightgames.requirements.RequirementShortcuts.item;

// C# naming convention, I know, I know
public interface IEncounter {
    boolean battle();

    void engage(Combat c);

    Combat getCombat();

    boolean checkIntrude(Character c);

    void intrude(Character intruder, Character assist);

    void trap(Character opportunist, Character target, Trap trap);

    boolean spotCheck();

    Character getPlayer(int idx);

    void parse(Encs choice, Character primary, Character opponent);

    void parse(Encs choice, Character primary, Character opponent, Trap trap);

    void watch();

    default void promptIntervene(Character p1, Character p2, GUI gui) {
        List<KeyableButton> choices = Arrays.asList(
                        new RunnableButton("Help " + p1.getName(), () -> {
                            intrude(GameState.gameState.characterPool.getPlayer(), p1);
                        }),
                        new RunnableButton("Help " + p2.getName(), () -> {
                            intrude(GameState.gameState.characterPool.getPlayer(), p2);
                        }),
                        new RunnableButton("Watch them fight", () -> {
                            watch();
                        }));
        gui.prompt(choices);
        Match.getMatch().pause();
    }

    default void promptShower(Character target, GUI gui) {
        List<KeyableButton> choices = new ArrayList<>();
        choices.add(EncounterButton.encounterButton("Suprise Her", this, target, Encs.showerattack));
        if (!target.mostlyNude()) {
            choices.add(EncounterButton.encounterButton("Steal Clothes", this, target, Encs.stealclothes));
        }
        if (GameState.gameState.characterPool.human.has(Item.Aphrodisiac)) {
            choices.add(EncounterButton.encounterButton("Use Aphrodisiac", this, target,
                            Encs.aphrodisiactrick));
        }
        choices.add(EncounterButton.encounterButton("Do Nothing", this, target, Encs.wait));
        gui.prompt(choices);
        Match.getMatch().pause();
    }

    default void promptOpportunity(Character target, Trap trap, GUI gui) {
        List<KeyableButton> choices = Arrays.asList(
        new RunnableButton("Attack " + target.getName(), () -> {
            parse(Encs.capitalize, GameState.gameState.characterPool.getPlayer(), target, trap);
            Match.getMatch().resume();
        }),
        EncounterButton.encounterButton("Wait", this, target, Encs.wait));
        gui.prompt(choices);
        Match.getMatch().pause();
    }

    default void promptFF(Character target, GUI gui) {
        List<KeyableButton> choices = new ArrayList<>();
        choices.add(EncounterButton.encounterButton("Fight", this, target, Encs.fight));
        choices.add(EncounterButton.encounterButton("Flee", this, target, Encs.flee));
        if (item(Item.SmokeBomb, 1).meets(null, GameState.gameState.characterPool.human, null)) {
            choices.add(EncounterButton.encounterButton("Smoke Bomb", this, target, Encs.smoke));
        }
        gui.prompt(choices);
        Match.getMatch().pause();
    }

    default void promptAmbush(Character target, GUI gui) {
        List<KeyableButton> choices = new ArrayList<>();
        choices.add(EncounterButton.encounterButton("Attack " + target.getName(), this, target, Encs.ambush));
        choices.add(EncounterButton.encounterButton("Wait", this, target, Encs.wait));
        choices.add(EncounterButton.encounterButton("Flee", this, target, Encs.fleehidden));
        gui.prompt(choices);
        Match.getMatch().pause();
    }
}
