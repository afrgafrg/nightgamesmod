package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.mods.FeralMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.FaceSitting;
import nightgames.status.BodyFetish;
import nightgames.status.Enthralled;
import nightgames.status.Shamed;

public class FaceSit extends Skill {

    public FaceSit(Character self) {
        super("Facesit", self);
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.dominant);
        addTag(SkillTag.facesit);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getLevel() >= 10 || user.get(Attribute.Seduction) >= 30;
    }

    @Override
    public float priorityMod(Combat c) {
        return getSelf().has(Trait.lacedjuices) || getSelf().has(Trait.addictivefluids)
                        || (getSelf().body.has("pussy") && getSelf().body.
                                        getRandomPussy().moddedPartCountsAs(getSelf(), FeralMod.INSTANCE)) ? 2.5f : 0;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().crotchAvailable() && getSelf().canAct() && c.getStance().dom(getSelf())
                        && c.getStance().prone(target) && !c.getStance().penetrated(c, getSelf())
                        && !c.getStance().inserted(getSelf()) && c.getStance().prone(target)
                        && !getSelf().has(Trait.shy);
    }

    @Override
    public String describe(Combat c) {
        return "Shove your crotch into your opponent's face to demonstrate your superiority";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (getSelf().has(Trait.enthrallingjuices) && Random.random(4) == 0 && !target.wary()) {
            writeOutput(c, Result.special, target);
            target.add(c, new Enthralled(target, getSelf(), 5));
        } else {
            writeOutput(c, getSelf().has(Trait.lacedjuices) ? Result.strong : Result.normal, target);
        }
        
        int m = 10;
        if (target.has(Trait.silvertongue)) {
            m = m * 3 / 2;
        }
        if (getSelf().hasBalls()) {
            getSelf().body.pleasure(target, target.body.getRandom("mouth"), getSelf().body.getRandom("balls"), m, c, this);
        } else {
            getSelf().body.pleasure(target, target.body.getRandom("mouth"), getSelf().body.getRandom("pussy"), m, c, this);
            
            if (Random.random(100) < 1 + getSelf().get(Attribute.Fetish) / 2) {
                target.add(c, new BodyFetish(target, getSelf(), "pussy", .05));
            }
        }
        double n = 4 + Random.random(4) + getSelf().body.getHotness(target);
        if (target.has(Trait.imagination)) {
            n *= 1.5;
        }

        target.temptWithSkill(c, getSelf(), getSelf().body.getRandom("ass"), (int) Math.round(n / 2), this);
        target.temptWithSkill(c, getSelf(), getSelf().body.getRandom("pussy"), (int) Math.round(n / 2), this);

        target.loseWillpower(c, 5);
        target.add(c, new Shamed(target));
        if (!c.getStance().isFaceSitting(getSelf())) {
            c.setStance(new FaceSitting(getSelf(), target), getSelf(), true);
        }
        int fetishChance = 5 + 2 * getSelf().get(Attribute.Fetish);
        if (getSelf().has(Trait.bewitchingbottom)) {
            fetishChance *= 2;
        }
        if (Random.random(100) < fetishChance) {
            target.add(c, new BodyFetish(target, getSelf(), "ass", .25));
        }
      
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 25;
    }

    @Override
    public Skill copy(Character user) {
        return new FaceSit(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String getLabel(Combat c) {
        if (getSelf().hasBalls() && !getSelf().hasPussy()) {
            return "Teabag";
        } else if (!c.getStance().isFaceSitting(getSelf())) {
            return "Facesit";
        } else {
            return "Ride Face";
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (getSelf().hasBalls()) {
            if (modifier == Result.special) {
                return Formatter.format("You crouch over {other:name-possessive} face and dunk your balls into "
                                + "{other:possessive} mouth. {other:PRONOUN} can do little "
                                + "except lick them submissively, which does feel "
                                + "pretty good. {other:PRONOUN} is so affected by your manliness "
                                + "that {other:possessive} eyes glaze over and {other:pronoun}"
                                + " falls under your control. Oh yeah. You're awesome.", getSelf(), target);
            } else if (modifier == Result.strong) {
                return Formatter.format("You crouch over {other:name-possessive} face and dunk your balls "
                                + "into {other:possessive} mouth. {other:PRONOUN} can do little"
                                + " except lick them submissively, which does feel "
                                + "pretty good. Your powerful musk is clearly starting to "
                                + "turn {other:direct-object} on. Oh yeah. You're awesome.", getSelf(), target);
            } else {
                return Formatter.format("You crouch over {other:name-possessive} face and dunk your balls into "
                                + "{other:possessive} mouth. {other:PRONOUN} can do little "
                                + "except lick them submissively, which does feel "
                                + "pretty good. Oh yeah. You're awesome.", getSelf(), target);
            }
        } else {
            if (modifier == Result.special) {
                return Formatter.format("You straddle {other:name-possessive} face and grind your pussy "
                                + "against {other:possessive} mouth, forcing {other:direct-object} to "
                                + "eat you out. Your juices take control of {other:possessive} lust and "
                                + "turn {other:direct-object} into a pussy licking slave. Ooh, that "
                                + "feels good. You better be careful not to get carried away with this."
                                , getSelf(), target);
            } else if (modifier == Result.strong) {
                return Formatter.format("You straddle {other:name-possessive} face and grind your pussy "
                                + "against {other:possessive} mouth, forcing {other:direct-object} to eat "
                                + "you out. {other:PRONOUN} flushes and seeks more of your tainted juices. "
                                + "Ooh, that feels good. You better be careful not to get carried away "
                                + "with this.", getSelf(), target);
            } else {
                return Formatter.format("You straddle {other:name-possessive} face and grind your pussy "
                                + "against {other:possessive} mouth, forcing {other:direct-object} to eat "
                                + "you out. Ooh, that feels good. You better be careful "
                                + "not to get carried away with this.", getSelf(), target);
            }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (getSelf().hasBalls()) {
            if (modifier == Result.special) {
                return String.format("%s straddles %s head and dominates %s by putting %s balls in %s mouth. "
                                + "For some reason, %s mind seems to cloud over and %s %s "
                                + "desperate to please %s. %s gives a superior smile as %s obediently %s on %s nuts.",
                                getSelf().subject(), target.nameOrPossessivePronoun(), target.directObject(),
                                getSelf().possessiveAdjective(), target.possessiveAdjective(),
                                target.nameOrPossessivePronoun(), target.pronoun(),
                                target.action("are", "is"), getSelf().directObject(),
                                Formatter.capitalizeFirstLetter(getSelf().subject()),
                                target.subject(), target.action("suck"), getSelf().possessiveAdjective());
            } else if (modifier == Result.strong) {
                return String.format("%s straddles %s head and dominates %s by putting %s balls in %s mouth. "
                                + "Despite the humiliation, %s scent is turning %s on incredibly. "
                                + "%s gives a superior smile as %s obediently %s on %s nuts.",
                                getSelf().subject(), target.nameOrPossessivePronoun(), target.directObject(),
                                getSelf().possessiveAdjective(), target.possessiveAdjective(),
                                getSelf().nameOrPossessivePronoun(), target.subject(),
                                getSelf().subject(), target.subject(), target.action("suck"),
                                getSelf().possessiveAdjective());
            } else {
                return String.format("%s straddles %s head and dominates %s by putting %s balls in %s mouth. "
                                + "%s gives a superior smile as %s obediently %s on %s nuts.",
                                getSelf().subject(), target.nameOrPossessivePronoun(), target.directObject(),
                                getSelf().possessiveAdjective(),
                                target.possessiveAdjective(),
                                getSelf().subject(), target.subject(), target.action("suck"),
                                getSelf().possessiveAdjective());
            }
        } else {
            if (modifier == Result.special) {
                return String.format("%s straddles %s face and presses %s pussy against %s mouth. %s "
                                + "%s mouth and %s to lick %s freely offered muff, but %s just smiles "
                                + "while continuing to queen %s. As %s %s %s juices, %s %s"
                                + " eyes start to bore into %s mind. %s can't resist %s. %s %s even want to.",
                                getSelf().subject(), target.nameOrPossessivePronoun(), getSelf().possessiveAdjective(),
                                target.possessiveAdjective(), target.subjectAction("open"), target.possessiveAdjective(),
                                target.action("start"), getSelf().possessiveAdjective(), getSelf().pronoun(),
                                target.directObject(), target.pronoun(),
                                target.action("drink"), getSelf().possessiveAdjective(),
                                target.subjectAction("feel"), getSelf().nameOrPossessivePronoun(), 
                                target.possessiveAdjective(),
                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                Formatter.capitalizeFirstLetter(target.pronoun()),
                                getSelf().nameDirectObject(), target.action("don't", "doesn't"));
            } else if (modifier == Result.strong) {
                return String.format("%s straddles %s face and presses %s pussy against %s mouth. %s "
                                + "%s mouth and start to lick %s freely offered muff, but %s just smiles "
                                + "while continuing to queen %s. %s %s body start to heat up as %s "
                                + "juices flow into %s mouth, %s %s giving %s a mouthful of aphrodisiac straight from "
                                + "the source!", getSelf().subject(), target.nameOrPossessivePronoun(),
                                getSelf().possessiveAdjective(), target.possessiveAdjective(), target.subjectAction("open"),
                                target.possessiveAdjective(), getSelf().nameDirectObject(), getSelf().pronoun(),
                                 target.directObject(), Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                                 target.possessiveAdjective(), getSelf().nameOrPossessivePronoun(), target.possessiveAdjective(),
                                 getSelf().pronoun(), getSelf().action("are", "is"), target.directObject());
            } else {
                return String.format("%s straddles %s face and presses %s pussy against %s mouth. %s "
                                + "%s mouth and start to lick %s freely offered muff, but %s just smiles "
                                + "while continuing to queen %s. %s clearly doesn't mind accepting some pleasure"
                                + " to demonstrate %s superiority.",getSelf().subject(), target.nameOrPossessivePronoun(),
                                getSelf().possessiveAdjective(), target.possessiveAdjective(), target.subjectAction("open"),
                                target.possessiveAdjective(), getSelf().nameDirectObject(), getSelf().pronoun(),
                                 target.directObject(), Formatter.capitalizeFirstLetter(getSelf().pronoun()), getSelf().possessiveAdjective());
            }
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
