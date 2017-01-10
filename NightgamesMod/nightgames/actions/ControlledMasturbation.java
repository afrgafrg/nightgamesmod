package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.global.Global;
import nightgames.global.Rng;
import nightgames.status.Stsflag;

public class ControlledMasturbation extends Action {
    private static final long serialVersionUID = 3348315784779117715L;

    public ControlledMasturbation() {
        super("Be Controlled");
    }

    @Override
    public boolean usable(Character user) {
        return user.is(Stsflag.trance);
    }

    @Override
    public Movement execute(Character user) {
        
        String mast;
        if (user.hasDick()) {
            mast = "stroking {self:possessive} {self:body-part:cock}";
        } else if (user.hasPussy()) {
            mast = "rubbing {self:possessive} {self:body-part:pussy}";
        } else {
            mast = "fingering {self:possessive} ass";
        }
         
        if (user.human()) {
            Global.global.gui().message(Global.global.format("Your limbs are still not your own, and your hand"
                            + " continues %s against your will.", user, NPC.NONE_CHARACTER, mast));
        } else {
            Global.global.gui().message(Global.global.format("You see {self:name} furiously %s. This would normally be"
                            + " very exciting to watch, but the glazed look in {self:possessive} eyes give"
                            + " you pause. Just what is going on here?", user, NPC.NONE_CHARACTER, mast));
        }
        
        user.tempt(40 + Rng.rng.random(20));
        
        return Movement.wait;
    }

    @Override
    public Movement consider() {
        return Movement.wait;
    }

}
