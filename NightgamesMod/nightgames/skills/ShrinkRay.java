package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.mods.SizeMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.Shamed;

@SuppressWarnings("unused")
public class ShrinkRay extends Skill {

    public ShrinkRay(Character self) {
        super("Shrink Ray", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Science) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().mobile(getSelf()) && !c.getStance().prone(getSelf())
                        && target.mostlyNude() && getSelf().has(Item.Battery, 2);
    }

    @Override
    public float priorityMod(Combat c) {
        return 2.f;
    }

    @Override
    public String describe(Combat c) {
        return "Shrink your opponent's 'assets' to damage their ego: 2 Batteries";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        getSelf().consume(Item.Battery, 2);
        boolean permanent = Random.random(20) == 0 && (getSelf().human() || target.human())
                        && !target.has(Trait.stableform);
        if (getSelf().human()) {
            if (target.hasDick()) {
                c.write(getSelf(), deal(c, permanent ? 1 : 0, Result.special, target));
            } else {
                c.write(getSelf(), deal(c, permanent ? 1 : 0, Result.normal, target));
            }
        } else if (c.shouldPrintReceive(target, c)) {
            if (target.hasDick()) {
                c.write(getSelf(), receive(c, permanent ? 1 : 0, Result.special, target));
            } else {
                c.write(getSelf(), receive(c, permanent ? 1 : 0, Result.normal, target));
            }
        }
        target.add(c, new Shamed(target));
        if (permanent) {
            if (target.hasDick()) {
                CockPart part = target.body.getCockAbove(SizeMod.getMinimumSize("cock"));
                if (part != null) {
                    target.body.addReplace(part.downgrade(), 1);
                } else {
                    target.body.remove(target.body.getRandomCock());
                }
            } else {
                BreastsPart part = target.body.getBreastsAbove(BreastsPart.flat.getSize());
                if (part != null) {
                    target.body.addReplace(part.downgrade(), 1);
                }
            }
        } else {
            if (target.hasDick()) {
                CockPart part = target.body.getCockAbove(SizeMod.getMinimumSize("cock"));
                if (part != null) {
                    target.body.temporaryAddOrReplacePartWithType(part.downgrade(), part, 10);
                } else {
                    target.body.temporaryRemovePart(target.body.getRandom("cock"), 10);
                }
            } else {
                BreastsPart part = target.body.getBreastsAbove(BreastsPart.flat.getSize());
                if (part != null) {
                    target.body.temporaryAddOrReplacePartWithType(part.downgrade(), part, 10);
                }
            }
        }
        target.loseMojo(c, 50);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ShrinkRay(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String message;
        if (modifier == Result.special) {
            message = "You aim your shrink ray at {other:name-possessive} cock, "
                            + "shrinking {other:possessive} male anatomy. {other:PRONOUN}"
                            + " turns red and glares at you in humiliation.";
        } else {
            message = "You point your shrink ray to turn {other:name-possessive}"
                            + " breasts. {other:PRONOUN} whimpers and covers "
                            + "{other:possessive} chest in shame.";
        }
        if (damage > 0) {
            message += " {other:PRONOUN} glares at you angrily when "
                            + "{other:pronoun} realizes the effects are permanent!";
        }
        return Formatter.format(message, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        String message;
        if (modifier == Result.special) {
            message = String.format("%s points a device at %s groin and giggles as %s genitals "
                            + "shrink. %s in shame and %s %s.", getSelf().subject(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("flush", "flushes")),
                            target.action("cover"), target.reflectivePronoun());
        } else {
            message = String.format("%s points a device at %s chest and giggles as %s %s"
                            + " shrink. %s in shame and %s %s.", getSelf().subject(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            getSelf().body.getRandomBreasts().describe(getSelf()),
                            Formatter.capitalizeFirstLetter(target.subjectAction("flush", "flushes")),
                            target.action("cover"), target.reflectivePronoun());
        }
        if (damage == 0) {
            message += String.format(" The effect wears off quickly, but the"
                            + " damage to %s dignity lasts much longer.", target.nameOrPossessivePronoun());
        } else {
            message += " You realize the effects are permanent!";
        }
        return message;
    }

}
