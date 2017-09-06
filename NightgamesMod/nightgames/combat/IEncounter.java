package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.global.Encs;
import nightgames.global.GameState;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;
import nightgames.trap.Trap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    // TODO: Refactor these prompts into a single method.
    default void promptIntervene(Character p1, Character p2, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = Arrays.asList(new LabeledValue<>("p1", "Help " + p1.getName()),
                        new LabeledValue<>("p2", "Help " + p2.getName()),
                        new LabeledValue<>("Watch", "Watch them fight"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "p1":
                    intrude(player, p1);
                    break;
                case "p2":
                    intrude(player, p2);
                    break;
                case "Watch":
                    watch();
                    break;
                default:
                    throw new AssertionError("Unknown Intervene choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    default void promptShower(Character target, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Surprise", "Surprise Her"));
        if (!target.mostlyNude()) {
            choices.add(new LabeledValue<>("Steal", "Steal Clothes"));
        }
        if (player.has(Item.Aphrodisiac)) {
            choices.add(new LabeledValue<>("Aphrodisiac", "Use Aphrodisiac"));
        }
        choices.add(new LabeledValue<>("Wait", "Do Nothing"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Surprise":
                    parse(Encs.showerattack, player, target);
                    break;
                case "Steal":
                    parse(Encs.stealclothes, player, target);
                    break;
                case "Aphrodisiac":
                    parse(Encs.aphrodisiactrick, player, target);
                    break;
                case "Wait":
                    parse(Encs.wait, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Shower choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    default void promptOpportunity(Character target, Trap trap, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Attack", "Attack" + target.getName()));
        choices.add(new LabeledValue<>("Wait", "Wait"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Attack":
                    parse(Encs.capitalize, player, target, trap);
                    break;
                case "Wait":
                    parse(Encs.wait, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Opportunity choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    default void promptFF(Character target, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Fight", "Fight"));
        choices.add(new LabeledValue<>("Flee", "Flee"));
        if (item(Item.SmokeBomb, 1).meets(null, player, null)) {
            choices.add(new LabeledValue<>("Smoke", "Smoke Bomb"));
        }
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Fight":
                    parse(Encs.fight, player, target);
                    break;
                case "Flee":
                    parse(Encs.flee, player, target);
                    break;
                case "Smoke":
                    parse(Encs.smoke, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Fight/Flight choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    default void promptAmbush(Character target, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Attack", "Attack " + target.getName()));
        choices.add(new LabeledValue<>("Wait", "Wait"));
        choices.add(new LabeledValue<>("Flee", "Flee"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Attack":
                    parse(Encs.ambush, player, target);
                    break;
                case "Wait":
                    parse(Encs.wait, player, target);
                    break;
                case "Flee":
                    parse(Encs.fleehidden, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Ambush choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
