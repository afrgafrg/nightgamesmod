package nightgames.encounter;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.gui.NgsController;
import nightgames.gui.button.EncounterButton;
import nightgames.gui.button.FutureButton;
import nightgames.items.Item;
import nightgames.trap.Trap;

import java.util.ArrayList;
import java.util.List;

import static nightgames.requirements.RequirementShortcuts.item;

/**
 * Interface between the GUI and a Match.
 */
public class EncounterController implements NgsController {
    private GUI gui;

    public EncounterController(GUI gui) {
        this.gui = gui;
    }

    public List<FutureButton<Encs>> fightOrFlightButtons() {
        List<FutureButton<Encs>> buttons = new ArrayList<>();
        buttons.add(new EncounterButton(Encs.fight, "Fight"));
        buttons.add(new EncounterButton(Encs.flee, "Flee"));
        if (item(Item.SmokeBomb, 1).meets(null, Global.global.human, null)) {
            buttons.add(new EncounterButton(Encs.smoke, "Smoke Bomb"));
        }
        return buttons;
    }

    public List<FutureButton<Encs>> ambushButtons(IEncounter enc, Character target) {
        List<FutureButton<Encs>> buttons = new ArrayList<>();
        buttons.add(new EncounterButton(Encs.ambush, String.format("Attack %s", target.name())));
        buttons.add(new EncounterButton(Encs.wait, "Wait"));
        return buttons;
    }

    public List<FutureButton<Encs>> opportunityButtons(IEncounter enc, Character target, Trap trap) {
        List<FutureButton<Encs>> buttons = new ArrayList<>();
        buttons.add(new EncounterButton(Encs.capitalize, "Attack " + target.name()));
        buttons.add(new EncounterButton(Encs.wait, "Wait"));
        return buttons;
    }

    public List<FutureButton<Encs>> showerButtons(IEncounter encounter, Character target) {
        List<FutureButton<Encs>> buttons = new ArrayList<>();
        buttons.add(new EncounterButton(Encs.showerattack, "Surprise Her"));
        if (!target.mostlyNude()) {
            buttons.add(new EncounterButton(Encs.stealclothes, "Steal Clothes"));
        }
        if (Global.global.human.hasItem(Item.Aphrodisiac)) {
            buttons.add(new EncounterButton(Encs.aphrodisiactrick, "Use Aphrodisiac"));
        }
        buttons.add(new EncounterButton(Encs.wait, "Do Nothing"));
        return buttons;
    }

    public List<FutureButton<Encs>> interveneButtons(IEncounter enc, Character p1, Character p2) {
        List<FutureButton<Encs>> buttons = new ArrayList<>();
        buttons.add(new EncounterButton(Encs.intervenep1, p1.name()));
        buttons.add(new EncounterButton(Encs.intervenep2, p2.name()));
        return buttons;
    }

}
