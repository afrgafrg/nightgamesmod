package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.DebugFlags;
import nightgames.gui.GUI;
import nightgames.status.Bound;
import nightgames.status.Stsflag;

public class Struggle extends Action {
    private static final long serialVersionUID = -644996487174479671L;
    public Struggle() {
        super("Struggle");
    }

    @Override
    public boolean usable(Character user) {
        return user.bound();
    }

    @Override
    public Movement execute(Character user) {
        Bound status = (Bound) user.getStatus(Stsflag.bound);
        int difficulty = 20 - user.getEscape(null, null);
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println(user.getTrueName() + " struggles with difficulty " + difficulty);
        }
        if (user.checkVsDc(Attribute.Power, difficulty)) {
            if (user.human()) {
                if (status != null) {
                    GUI.gui.message("You manage to break free from the " + status.getVariant() + ".");
                } else {
                    GUI.gui.message("You manage to snap the restraints that are binding your hands.");
                }
            }
            user.free();
        } else {
            if (user.human()) {
                if (status != null) {
                    GUI.gui.message("You struggle against the " + status.getVariant() + ", but can't get free.");
                } else {
                    GUI.gui.message("You struggle against your restraints, but can't get free.");
                }
            }
            user.struggle();
        }
        return Movement.struggle;
    }

    @Override
    public Movement consider() {
        return Movement.struggle;
    }

}
