package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.skills.damage.Staleness;
import nightgames.status.Hypersensitive;
import nightgames.status.Winded;

public class Tickle extends Skill {
    public Tickle(Character self) {
        // tickle has higher decay but pretty fast recovery
        super("Tickle", self, 0, Staleness.build().withDefault(1.0).withFloor(.5).withDecay(.15).withRecovery(.20));
        addTag(SkillTag.weaken);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && (c.getStance().mobile(getSelf()) || c.getStance().dom(getSelf()))
                        && (c.getStance().reachTop(getSelf()) || c.getStance().reachBottom(getSelf()));
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 7;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        DamageType type = DamageType.technique;
        if (getSelf().has(Trait.ticklemonster) || target.roll(getSelf(), c, accuracy(c, target))) {
            if (target.crotchAvailable() && c.getStance().reachBottom(getSelf()) && !c.getStance().havingSex(c)) {
                int bonus = 0;
                int weak = 0;
                Result result = Result.normal;

                if (getSelf().has(Item.Tickler2) && Random.random(2) == 1 && getSelf().canSpend(10)) {
                    getSelf().spendMojo(c, 10);
                    result = Result.special;
                    bonus += 2;
                    weak += 2;
                }
                if (hastickler()) {
                    result = Result.strong;
                    bonus += 5 + Random.random(4);
                    weak += 3 + Random.random(4);
                    type = DamageType.gadgets;
                }
                writeOutput(c, result, target);
                if (getSelf().has(Trait.ticklemonster) && target.mostlyNude()) {
                    writeOutput(c, Result.special, target);
                    bonus += 5 + Random.random(4);
                    weak += 3 + Random.random(4);
                    if (Random.random(4) == 0) {
                        target.add(c, new Winded(target, 1));
                    }
                }
                if (result == Result.special) {
                    target.add(c, new Hypersensitive(target, 5));
                }
                if (target.has(Trait.ticklish)) {
                    bonus = 4 + Random.random(3);
                    c.write(target, Formatter.format(
                                    "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup, definitely ticklish.",
                                    getSelf(), target));
                }
                target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"),
                                (int) getSelf().modifyDamage(type, target, 2 + Random.random(4)), bonus, c, false, this);
                target.weaken(c, (int) getSelf().modifyDamage(type, target, weak + Random.random(10, 15)));
            } else if (hastickler() && Random.random(2) == 1) {
                type = DamageType.gadgets;
                int bonus = 0;
                if (target.breastsAvailable() && c.getStance().reachTop(getSelf())) {
                    writeOutput(c, Result.item, target);
                    if (target.has(Trait.ticklish)) {
                        bonus = 4 + Random.random(3);
                        c.write(target, Formatter.format(
                                        "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                        getSelf(), target));
                    }
                    target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"),
                                    4 + Random.random(4), bonus, c, false, this);
                } else {
                    writeOutput(c, Result.weak, target);
                    if (target.has(Trait.ticklish)) {
                        bonus = 4 + Random.random(3);
                        c.write(target, Formatter.format(
                                        "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                        getSelf(), target));
                    }
                    target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"),
                                    4 + Random.random(2), bonus, c, false, this);
                }
                target.weaken(c, (int) getSelf().modifyDamage(type, target, bonus + Random.random(5, 10)));
            } else {
                writeOutput(c, Result.normal, target);
                int bonus = 0;
                if (target.has(Trait.ticklish)) {
                    bonus = 2 + Random.random(3);
                    c.write(target, Formatter.format(
                                    "{other:SUBJECT-ACTION:squirm|squirms} uncontrollably from {self:name-possessive} actions. Yup definitely ticklish.",
                                    getSelf(), target));
                }
                int m = (int) Math.round((2 + Random.random(3)) * (.25 + target.getExposure()));
                int weak = (int) Math.round(bonus / 2 * (.25 + target.getExposure()));
                target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("skin"), (int) getSelf().modifyDamage(type, target, m),
                                bonus, c, false, this);
                target.weaken(c, (int) getSelf().modifyDamage(type, target, weak + Random.random(4, 7)));
            }
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Cunning) >= 5;
    }

    @Override
    public Skill copy(Character user) {
        return new Tickle(user);
    }

    @Override
    public int speed() {
        return 7;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.miss) {
            msg = "You try to tickle {other:name-do}, but {other:pronoun} squirms away.";
        } else if (modifier == Result.special) {
            msg = "You work your fingers across {other:name-possessive} most ticklish and most"
                            + " erogenous zones until {other:pronoun} is writhing in pleasure "
                            + "and can't even make coherent words.";
        } else if (modifier == Result.critical) {
            msg = "You brush your tickler over {other:name-possessive} body, causing "
                            + "{other:direct-object} to shiver and retreat. When you tickle "
                            + "{other:direct-object} again, {other:pronoun} yelps and almost "
                            + "falls down. It seems like your special feathers made "
                            + "{other:direct-object} more sensitive than usual.";
        } else if (modifier == Result.strong) {
            msg = "You run your tickler across {other:name-possessive} sensitive thighs and "
                            + "pussy. {other:PRONOUN} can't help but let out a quiet whimper "
                            + "of pleasure.";
        } else if (modifier == Result.item) {
            msg = "You tease {other:name-possessive} naked upper body with your feather "
                            + "tickler, paying close attention to {other:possessive} nipples.";
        } else if (modifier == Result.weak) {
            msg = "You catch {other:name-do} off guard by tickling {other:possessive} neck and ears.";
        } else {
            msg = "You tickle {other:name-possessive} sides as {other:pronoun} giggles and "
                            + "squirms.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to tickle %s, but fails to find a sensitive spot.",
                            getSelf().subject(), target.nameDirectObject());
        } else if (modifier == Result.special) {
            return String.format("%s tickles %s nude body mercilessly, gradually working %s way to %s dick and balls. "
                            + "As %s fingers start tormenting %s privates, %s %s to "
                            + "clear %s head enough to keep from cumming immediately.", getSelf().subject(),
                            target.nameOrPossessivePronoun(), getSelf().possessiveAdjective(),
                            target.possessiveAdjective(), getSelf().possessiveAdjective(), target.possessiveAdjective(),
                            target.pronoun(), target.action("struggle"), target.possessiveAdjective());
        } else if (modifier == Result.critical) {
            return String.format("%s teases %s privates with %s feather tickler. After %s stops,"
                            + " %s an unnatural sensitivity where the feathers touched %s.", getSelf().subject(),
                            target.nameDirectObject(), getSelf().possessiveAdjective(), getSelf().pronoun(),
                            target.subjectAction("feel"), target.directObject());
        } else if (modifier == Result.strong) {
            return String.format("%s brushes %s tickler over %s balls and teases the sensitive head of %s penis.",
                            getSelf().subject(), getSelf().possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective());
        } else if (modifier == Result.item) {
            return String.format("%s runs %s feather tickler across %s nipples and abs.",
                            getSelf().subject(), getSelf().possessiveAdjective(), 
                            target.nameOrPossessivePronoun());
        } else if (modifier == Result.weak) {
            return String.format("%s pulls out a feather tickler and teases any exposed skin %s can reach.",
                            getSelf().subject(), getSelf().pronoun());
        } else {
            return String.format("%s suddenly springs toward %s and tickles %s"
                            + " relentlessly until %s can barely breathe.", getSelf().subject(),
                            target.nameDirectObject(), target.directObject(), target.pronoun());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Tickles opponent, weakening and arousing them. More effective if they're nude";
    }

    private boolean hastickler() {
        return getSelf().has(Item.Tickler) || getSelf().has(Item.Tickler2);
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}
