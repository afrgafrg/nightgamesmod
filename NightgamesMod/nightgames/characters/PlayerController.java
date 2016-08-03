package nightgames.characters;

import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.gui.NgsController;
import nightgames.gui.Prompt;
import nightgames.gui.button.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * TODO: Write class-level documentation.
 */
public class PlayerController implements NgsController{
    private final GUI gui;

    public PlayerController(GUI gui) {
        this.gui = gui;
    }


    public void ding(Player player) throws InterruptedException {
        spendAttributePoints(player);
        spendTraitPoints(player);
    }

    private void spendAttributePoints(Player player) throws InterruptedException {
        while (player.availableAttributePoints > 0) {
            Optional<Attribute> response = spendAttributePoint(player);
            if (response.isPresent()) {
                Attribute attribute = response.get();
                player.att.merge(attribute, 1, (a, b) -> a + b);
            } else {
                break;
            }
        }
    }

    private Optional<Attribute> spendAttributePoint(Player player) throws InterruptedException {
        gui.message(player.availableAttributePoints + " Attribute Points remain.\n");
        List<FutureButton<Attribute>> buttons = new ArrayList<>();
        for (Attribute att : player.att.keySet()) {
            if (Attribute.isTrainable(att, player) && player.getPure(att) > 0) {
                buttons.add(new AttributeButton(att));
            }
        }
        buttons.add(new AttributeButton(Attribute.Willpower));
        Prompt<Attribute> prompt = new Prompt<>(buttons);
        gui.setChoices(new ArrayList<>(buttons));
        return prompt.response();
    }

    private void spendTraitPoints(Player player) throws InterruptedException {
        while (player.traitPoints > 0) {
            Optional<Trait> response = spendTraitPoint(player);
            if (response.isPresent()) {
                Trait trait = response.get();
                player.add(trait);
            } else {
                break;
            }
        }
    }

    private Optional<Trait> spendTraitPoint(Player player) throws InterruptedException {
        gui.message("You've earned a new perk. Select one below.");
        List<FutureButton<Trait>> buttons = new ArrayList<>();
        for (Trait feat : Global.global.getFeats(player)) {
            if (!player.has(feat)) {
                buttons.add(new FeatButton(feat));
            }
        }
        buttons.add(new SkipFeatButton());
        Prompt<Trait> prompt = new Prompt<>(buttons);
        gui.setChoices(new ArrayList<>(buttons));
        return prompt.response();
    }
}
