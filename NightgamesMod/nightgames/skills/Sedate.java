package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.skills.damage.DamageType;

public class Sedate extends Skill {

    public Sedate(Character self) {
        super("Sedate", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().mobile(getSelf()) && getSelf().canAct() && getSelf().has(Item.Sedative)
                        && !c.getStance().prone(getSelf());
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return getSelf().has(Item.Aersolizer) ? 200 : 65;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        getSelf().consume(Item.Sedative, 1);
        if (getSelf().has(Item.Aersolizer)) {
            writeOutput(c, Result.special, target);
            target.weaken(c, (int) getSelf().modifyDamage(DamageType.biological, target, 50));
            target.loseMojo(c, (int) getSelf().modifyDamage(DamageType.biological, target, 35));
        } else if (target.roll(getSelf(), c, accuracy(c, target))) {
            writeOutput(c, Result.normal, target);
            target.weaken(c, (int) getSelf().modifyDamage(DamageType.biological, target, 50));
            target.loseMojo(c, (int) getSelf().modifyDamage(DamageType.biological, target, 35));
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Sedate(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.special) {
            msg = "You pop a sedative into your Aerosolizer and spray {other:name-do}"
                            + " with a cloud of mist. {other:PRONOUN} stumbles out "
                            + "of the cloud looking drowsy and unfocused.";
        } else if (modifier == Result.miss) {
            msg = "You throw a bottle of sedative at {other:name-do}"
                            + ", but {other:pronoun} ducks out of the way and it "
                            + "splashes harmlessly on the ground. What a waste.";
        } else {
            msg = "You through a bottle of sedative at {other:name-do}. {other:PRONOUN}"
                            + " stumbles for a moment, trying to clear "
                            + "the drowsiness from {other:possessive} head.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.special) {
            return String.format("%s inserts a bottle into the attachment on %s arm. "
                            + "%s suddenly surrounded by a cloud of dense fog. The "
                            + "fog seems to fill %s head and %s body feels heavy.",
                            getSelf().subject(), getSelf().possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.action("are", "is")),
                            target.possessiveAdjective(), target.possessiveAdjective());
        } else if (modifier == Result.miss) {
            return String.format("%s splashes a bottle of liquid in %s direction, but none of it hits %s.",
                            getSelf().subject(), target.nameOrPossessivePronoun(), target.directObject());
        } else {
            return String.format("%s hits %s with a flask of liquid. Even the fumes make %s feel"
                            + " sluggish and %s limbs become heavy.",
                            getSelf().subject(), target.nameDirectObject(),
                            target.directObject(), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Throw a sedative at your opponent, weakening " + c.getOpponent(getSelf()).directObject();
    }
}
