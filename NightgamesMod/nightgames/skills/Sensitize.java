package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.status.Hypersensitive;
import nightgames.status.Stsflag;

public class Sensitize extends Skill {

    public Sensitize(Character self) {
        super("Sensitivity Potion", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().mobile(getSelf()) && getSelf().canAct() && getSelf().has(Item.SPotion)
                        && target.mostlyNude() && !c.getStance().prone(getSelf()) && !target.is(Stsflag.hypersensitive);
    }

    @Override
    public String describe(Combat c) {
        return "Makes your opponent hypersensitive";
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return getSelf().has(Item.Aersolizer) ? 200 : 65;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        getSelf().consume(Item.SPotion, 1);
        if (getSelf().has(Item.Aersolizer)) {
            writeOutput(c, Result.special, target);
        } else if (target.roll(getSelf(), c, accuracy(c, target))) {
            writeOutput(c, Result.normal, target);
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        target.add(c, new Hypersensitive(target));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Sensitize(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.special) {
            msg = "You pop a sensitivity potion into your Aerosolizer and spray {other:name-do}"
                            + " with a cloud of mist. {other:PRONOUN} shivers as it takes hold "
                            + "and heightens {other:possessive} sense of touch.";
        } else if (modifier == Result.miss) {
            msg = "You throw a bottle of sensitivity elixir at {other:name-do}"
                            + ", but {other:pronoun} ducks out of the way and it "
                            + "splashes harmlessly on the ground. What a waste.";
        } else {
            msg = "You throw a sensitivity potion at {other:name-do}"
                            + ". You see {other:possessive} skin flush as it takes effect.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.special) {
            return String.format("%s inserts a bottle into the attachment on %s arm. %s "
                            + "suddenly surrounded by a cloud of minty gas. %s skin becomes"
                            + " hot, but goosebumps appear anyway. "
                            + "Even the air touching %s skin makes %s shiver.", getSelf().subject(),
                            getSelf().possessiveAdjective(), 
                            Formatter.capitalizeFirstLetter(target.subjectAction("are", "is")),
                            target.possessiveAdjective(), target.possessiveAdjective(),
                            target.directObject());
        } else if (modifier == Result.miss) {
            return String.format("%s splashes a bottle of liquid in %s direction, but none of it hits %s.",
                            getSelf().subject(), target.nameDirectObject(), target.directObject());
        } else {
            return String.format("%s throws a bottle of strange liquid at %s. The skin it touches grows hot"
                            + " and oversensitive.", getSelf().subject(), target.nameDirectObject());
        }
    }

}
