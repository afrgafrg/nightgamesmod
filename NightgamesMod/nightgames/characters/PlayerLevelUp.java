package nightgames.characters;

import nightgames.global.Formatter;
import nightgames.gui.*;
import nightgames.utilities.FixedDeque;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: Write class-level documentation.
 */
class PlayerLevelUp {
    private final Player player;
    private final int initialAttributePoints;
    private final int initialTraitPoints;
    Deque<Attribute> attributeIncreases;
    Deque<Trait> newTraits;

    PlayerLevelUp(Player player, int attributePoints, int traitPoints) {
        this.player = player;
        initialAttributePoints = attributePoints;
        initialTraitPoints = traitPoints;

        attributeIncreases = new FixedDeque<>(initialAttributePoints);
        newTraits = new FixedDeque<>(initialTraitPoints);
    }

    int remainingAttributePoints() {
        return initialAttributePoints - attributeIncreases.size();
    }

    private void spendAttributePoints(GUI gui) throws InterruptedException {
        boolean wantsToSpendAttributePoints = true;
        while (wantsToSpendAttributePoints && remainingAttributePoints() > 0) {
            List<Attribute> attributeChoices = player.att.keySet().stream()
                            .filter(att -> Attribute.isTrainable(player, att) && player.getPure(att) > 0)
                            .collect(Collectors.toList());
            Formatter.writeIfCombatUpdateImmediately(gui.combat, player, remainingAttributePoints() + " Attribute Points remain.\n");

            CompletableFuture<Attribute> chosenAttribute = gui.promptFuture(attributeChoices, Attribute::name);
            CancelButton skipButton = new CancelButton("Skip", chosenAttribute);
            skipButton.setToolTipText("Save attribute points for next level-up.");
            gui.addButton(skipButton);

            try {
                attributeIncreases.push(chosenAttribute.get());
            } catch (CancellationException e) {
                wantsToSpendAttributePoints = false;
            } catch (ExecutionException e) {
                wantsToSpendAttributePoints = false;
                e.printStackTrace();
            }
        }
    }

    private void spendTraitPoints(GUI gui) throws InterruptedException {
        boolean wantsToSpendTraitPoints = true;
        Formatter.writeIfCombatUpdateImmediately(gui.combat, player, "You've earned %d new perk%s. Select below.");
        CompletableFuture<Trait> chosenTrait = new CompletableFuture<>();
        while (wantsToSpendTraitPoints && remainingTraitPoints() > 0) {
            Stream<Trait> traitChoices = Trait.getFeats(player).stream().filter(feat -> !player.has(feat));
            List<ValueButton<Trait>> featButtons =
                            traitChoices.map(feat -> new ValueButton<>(feat, feat.toString(), chosenTrait))
                                            .collect(Collectors.toList());
            featButtons.forEach(button -> button.setToolTipText(button.value.getDesc()));
            CancelButton skipButton = new CancelButton("Skip", chosenTrait);
            skipButton.setToolTipText("Save perk points for next level-up");
            gui.addButton(skipButton);
            try {
                Trait trait = chosenTrait.get();
                newTraits.push(trait);
                gui.clearTextIfNeeded();
                gui.message("Gained feat: " + trait.toString());
            } catch (CancellationException e) {
                wantsToSpendTraitPoints = false;
            } catch (ExecutionException e) {
                wantsToSpendTraitPoints = false;
                e.printStackTrace();
            }
        }
    }

    int remainingTraitPoints() {
        return initialTraitPoints - newTraits.size();
    }

    void getHumanChoices(GUI gui) throws InterruptedException, ExecutionException {
        boolean ready = false;
        while (!ready) {
            spendAttributePoints(gui);
            spendTraitPoints(gui);
            List<LabeledValue<String>> confirmPromptChoices = new ArrayList<>();
            confirmPromptChoices.add(new LabeledValue<>("Continue", "Continue"));
            confirmPromptChoices.add(new LabeledValue<>("Reset", "Reset points"));
            Future<String> chosenOkCancel = gui.promptFuture(confirmPromptChoices);
            switch (chosenOkCancel.get()) {
                case "Continue":
                    ready = true;
                    break;
                case "Reset":
                    attributeIncreases.clear();
                    newTraits.clear();
                    gui.message("Attributes and feats refunded.");
                    break;
            }
            // TODO: consider adding hotkey text to LabeledValue
        }
    }
}
