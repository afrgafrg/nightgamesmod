package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.global.DebugFlags;
import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.gui.NgsController;

import java.util.Observable;
import java.util.Observer;

/**
 * TODO: Write class-level documentation.
 */
public class CombatController implements NgsController, Observer {
    private GUI gui;

    public CombatController(GUI gui) {
        this.gui = gui;
    }

    @Override public void update(Observable o, Object arg) {
        if (!(o instanceof Combat)) {
            return;
        }
        Combat combat = (Combat) o;
        if (combat.combatMessageChanged) {
            gui.combatMessage(combat.getMessage());
            combat.combatMessageChanged = false;
        }
    }

    public Combat beginCombat(Combat combat, Player player, Character other) {
        gui.showPortrait();
        combat.addObserver(this);
        gui.message(combat.getMessage());
        gui.loadPortrait(combat, player, other);
        gui.showPortrait();
        return gui.combat;
    }

    public void endCombat(Combat combat) {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("End Combat");
        }
        gui.clearText();
        gui.clearImage();
        gui.showMap();
    }

    // NYI: Spectator mode
    public void watchCombat(Combat combat) {
        gui.showPortrait();
        combat.addObserver(this);
        gui.loadPortrait(combat, combat.p1, combat.p2);
        gui.showPortrait();
    }
}
