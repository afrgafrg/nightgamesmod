package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.characters.Trait;
import nightgames.match.ftc.FTCMatch;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;

public class Resupply extends Action {

    /**
     *
     */
    private static final long serialVersionUID = -3349606637987124335L;

    public Resupply() {
        super("Resupply");
    }

    @Override
    public boolean usable(Character user) {
        return  !user.bound() && user.location().resupply() || user.has(Trait.immobile) || (Global.global.checkFlag(Flag.FTC)
                        && ((FTCMatch) Global.global.getMatch()).isBase(user, user.location()));
    }

    @Override
    public Movement execute(Character user) {
        if (Global.global.checkFlag(Flag.FTC)) {
            FTCMatch match = (FTCMatch) Global.global.getMatch();
            if (user.human()) {
                Global.global.gui().message("You get a change of clothes from the chest placed here.");
            }
            if (user.has(Item.Flag) && !match.isPrey(user)) {
                match.turnInFlag(user);
            } else if (match.canCollectFlag(user)) {
                match.grabFlag();
            }
        } else {
            if (user.human()) {
                if (Global.global.getMatch().condition.name().equals("nudist")) {
                    Global.global.gui().message(
                                    "You check in so that you're eligible to fight again, but you still don't get any clothes.");
                } else {
                    Global.global.gui().message("You pick up a change of clothes and prepare to get back in the fray.");
                }
            }
        }
        user.state = State.resupplying;
        return Movement.resupply;
    }

    @Override
    public Movement consider() {
        return Movement.resupply;
    }

}
