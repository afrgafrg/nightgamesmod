package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.ReverseMount;
import nightgames.stance.SixNine;
import nightgames.status.Enthralled;

@SuppressWarnings("unused")
public class Cunnilingus extends Skill {

    public Cunnilingus(Character self) {
        super("Lick Pussy", self);
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        boolean canUse = c.getStance().isBeingFaceSatBy(c, getSelf(), target) && getSelf().canRespond()
                        || getSelf().canAct();
        boolean pussyAvailable = target.crotchAvailable() && target.hasPussy();
        boolean stanceAvailable = c.getStance().oral(getSelf(), target) && (!c.getStance().vaginallyPenetrated(c, target) || c.getStance().getPartsFor(c, getSelf(), target).contains(getSelf().body.getRandom("mouth")));
        boolean usable = pussyAvailable && stanceAvailable && canUse;
        return usable;
    }

    @Override
    public float priorityMod(Combat c) {
        return getSelf().has(Trait.silvertongue) ? 1 : 0;
    }

    @Override
    public int getMojoBuilt(Combat c) {
        if (c.getStance().isBeingFaceSatBy(c, getSelf(), c.getOpponent(getSelf()))) {
            return 0;
        } else {
            return 5;
        }
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        Result results = Result.normal;
        boolean facesitting = c.getStance().isBeingFaceSatBy(c, getSelf(), target);
        int m = 10 + Random.random(8);
        if (getSelf().has(Trait.silvertongue)) {
            m += 4;
        }
        int i = 0;
        if (!facesitting && c.getStance().mobile(target) && !target.roll(getSelf(), c, accuracy(c, target))) {
            results = Result.miss;
        } else {
            if (target.has(Trait.enthrallingjuices) && Random.random(4) == 0 && !target.wary()) {
                i = -2;
            } else if (target.has(Trait.lacedjuices)) {
                i = -1;
                getSelf().temptNoSource(c, target, 5, this);
            }
            if (facesitting) {
                results = Result.reverse;
            }
        }
        writeOutput(c, i, results, target);
        if (i == -2) {
            getSelf().add(c, new Enthralled(getSelf(), target, 3));
        }
        if (results != Result.miss) {
            if (results == Result.reverse) {
                target.buildMojo(c, 10);
            }
            if (ReverseMount.class.isInstance(c.getStance())) {
                c.setStance(new SixNine(getSelf(), target), getSelf(), true);
            }
            target.body.pleasure(getSelf(), getSelf().body.getRandom("mouth"), target.body.getRandom("pussy"), m, c, this);
        }
        return results != Result.miss;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Seduction) >= 10;
    }

    @Override
    public Skill copy(Character user) {
        return new Cunnilingus(user);
    }

    @Override
    public int speed() {
        return 2;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return !c.getStance().isBeingFaceSatBy(c, getSelf(), target) && c.getStance().reachTop(target)? 75 : 200;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("You try to eat out {other:name-do}, but {other:pronoun}"
                            + " pushes your head away.", getSelf(), target);
        }
        if (target.getArousal().get() < 10) {
            return Formatter.format("You run your tongue over {other:name-possessive}"
                            + " dry vulva, lubricating it with your saliva.", getSelf(), target);
        }
        if (modifier == Result.special) {
            return Formatter.format("Your skilled tongue explores {other:name-possessive}"
                            + " {other:body-part:pussy}, finding and pleasuring {other:direct-object}"
                            + " more sensitive areas. You frequently tease {other:possessive}"
                            + " clitoris until {other:pronoun} can't suppress {other:possessive} pleasured moans."
                            + (damage == -1 ? " Under your skilled ministrations, {other:possessive}"
                                            + " juices flow freely, and they unmistakably"
                                            + " have their effect on you."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow {other:pronoun} has managed to enthrall "
                                            + "you with {other:possessive} juices." : ""),
                                            getSelf(), target);
        }
        if (modifier == Result.reverse) {
            return Formatter.format("You resign yourself to lapping at {other:name-possessive} "
                            + " {other:body-pary:pussy}, as {other:pronoun} dominates"
                            + " your face with {other:possessive} ass."
                            + (damage == -1 ? " Under your skilled ministrations, {other:possessive} juices"
                                            + " flow freely, and they unmistakably"
                                            + " have their effect on you."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow {other:pronoun} has managed to "
                                            + "enthrall you with {other:possessive} juices." : ""),
                                            getSelf(), target);
        }
        if (target.getArousal().percent() > 80) {
            return Formatter.format("You relentlessly lick and suck the lips of {other:name-possessive} "
                            + "{other:body-part:pussy} as {other:pronoun} squirms in pleasure. "
                            + "You let up just for a second before kissing {other:direct-object}"
                            + " swollen clit, eliciting a cute gasp."
                            + (damage == -1 ? " The highly aroused succubus' vulva is dripping with"
                                            + " {other:possessive} aphrodisiac juices and you consume"
                                            + " generous amounts of them."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow {other:pronoun} has managed to enthrall"
                                            + " you with {other:possessive} juices." : ""),
                            getSelf(), target);
        }
        int r = Random.random(3);
        if (r == 0) {
            return Formatter.format("You gently lick {other:name-possessive} {other:body-part:pussy} and sensitive clit."
                            + (damage == -1 ? " As you drink down {other:possessive} juices, they seem to flow "
                                            + "straight down to your crotch, lighting fires when they arrive."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow {other:pronoun} has managed to enthrall"
                                            + " you with {other:possessive} juices." : ""),
                            getSelf(), target);
        }
        if (r == 1) {
            return Formatter.format("You thrust your tongue into {other:name-possessive} hot vagina and"
                            + " lick the walls of {other:possessive} pussy."
                            + (damage == -1 ? " Your tongue tingles with {other:possessive} juices, "
                                            + "clouding your mind with lust."
                                            : "")
                            + (damage == -2 ? " You feel a strange pull on you mind,"
                                            + " somehow {other:pronoun} has managed to enthrall"
                                            + " you with {other:possessive} juices." : ""),
                            getSelf(), target);
        }
        return Formatter.format("You locate and capture {other:name-possessive} clit between your "
                        + "lips and attack it with your tongue."
                        + (damage == -1 ? " {other:POSSESSIVE} juices taste wonderful and you "
                                        + "cannot help but desire more." : "")
                        + (damage == -2 ? " You feel a strange pull on you mind,"
                                        + " somehow {other:pronoun} has managed to enthrall"
                                        + " you with {other:possessive} juices." : ""),
                        getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        String special;
        switch (damage) {
            case -1:
                special = String.format(" %s aphrodisiac juices manage to arouse %s as much as %s aroused %s.", 
                                target.nameOrPossessivePronoun(), getSelf().nameDirectObject(),
                                getSelf().pronoun(), target.nameDirectObject());
                break;
            case -2:
                special = String.format(" %s tainted juices quickly reduce %s into a willing thrall.",
                                target.nameOrPossessivePronoun(), getSelf().nameDirectObject());
                break;
            default:
                special = "";
        }
        if (modifier == Result.miss) {
            return String.format("%s tries to tease %s cunt with %s mouth, but %s %s %s face away from %s box.",
                            getSelf().subject(), target.nameOrPossessivePronoun(), getSelf().possessiveAdjective(),
                            target.pronoun(), target.action("push", "pushes"), getSelf().nameOrPossessivePronoun(),
                            target.possessiveAdjective());
        } else if (modifier == Result.special) {
            return String.format("%s skilled tongue explores %s pussy, finding and pleasuring %s more sensitive areas. "
                            + "%s repeatedly attacks %s clitoris until %s can't suppress %s pleasured moans.%s",
                            getSelf().nameOrPossessivePronoun(), target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(getSelf().pronoun()), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.possessiveAdjective(), special);
        } else if (modifier == Result.reverse) {
            return String.format("%s obediently laps at %s pussy as %s %s on %s face.%s",
                            getSelf().subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("sit"), getSelf().possessiveAdjective(),
                            special);
        }
        return String.format("%s locates and captures %s clit between %s lips and attacks it with %s tongue.%s", 
                        getSelf().subject(), target.nameOrPossessivePronoun(), getSelf().possessiveAdjective(),
                        getSelf().possessiveAdjective(), special);
    }
    
    @Override
    public String describe(Combat c) {
        return "Perfom cunnilingus on opponent";
    }
}
