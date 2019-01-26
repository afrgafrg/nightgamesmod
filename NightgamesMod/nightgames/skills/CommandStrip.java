package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;

public class CommandStrip extends PlayerCommand {

    public CommandStrip(Character self) {
        super("Force Strip Self", self);
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return super.usable(c, target) && !target.mostlyNude();
    }

    @Override
    public String describe(Combat c) {
        return "Force your opponent to strip naked.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        target.undress(c);
        if (target.human()) {
            c.write(getSelf(), receive(c, 0, Result.normal, target));
        } else {
            c.write(getSelf(), deal(c, 0, Result.normal, target));
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new CommandStrip(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character target) {
        return String.format("You look %s in the eye, sending a psychic command for"
                        + " %s to strip. %s complies without question, standing before"
                        + " you nude only seconds later.", target.nameDirectObject(),
                        target.directObject(),
                        Formatter.capitalizeFirstLetter(target.pronoun()));
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character target) {
        return "<<This should not be displayed, please inform The Silver Bard: CommandStrip-receive>>";
    }

}
