package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Mount;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;

public class CommandDown extends PlayerCommand {

    @Override
    public boolean usable(Combat c, Character target) {
        return target.is(Stsflag.enthralled)
                        && ((Enthralled) target.getStatus(Stsflag.enthralled)).master.equals(getSelf())
                        && !c.getStance().havingSex(c) && getSelf().canRespond();
    }

    public CommandDown(Character self) {
        super("Force Down", self);
        addTag(SkillTag.positioning);
    }

    @Override
    public String describe(Combat c) {
        return "Command your opponent to lie down on the ground.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        c.setStance(new Mount(getSelf(), target), target, false);
        writeOutput(c, Result.normal, target);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new CommandDown(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character target) {
        return String.format("Trembling under the weight of your command, %s"
                        + " lies down. You follow %s down and mount %s, facing %s head.",
                        target.subject(), target.directObject(), target.directObject(),
                        target.possessiveAdjective());
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character target) {
        return String.format("%s tells %s to remain still and"
                                        + " gracefully lies down on %s, %s face right above %ss.",
                                        getSelf().getName(), target.subject(), 
                                        target.directObject(), getSelf().possessiveAdjective(),
                                        target.possessiveAdjective());
    }
}
