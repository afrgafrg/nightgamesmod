package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;

public class Finger extends Skill {

    public Finger(Character self) {
        super("Finger", self);
        addTag(SkillTag.usesHands);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().reachBottom(getSelf())
                        && (target.crotchAvailable() || getSelf().has(Trait.dexterous)
                                        && target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getLayer() <= 1)
                        && target.hasPussy() && getSelf().canAct() && !c.getStance().vaginallyPenetrated(c, target);
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), c, accuracy(c, target))) {
            int m = Random.random(8, 13);
            if (getSelf().get(Attribute.Seduction) >= 8) {
                m += 6;
                if (getSelf().human()) {
                    c.write(getSelf(), deal(c, m, Result.normal, target));
                } else {
                    c.write(getSelf(), receive(c, 0, Result.normal, target));
                }
                target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("pussy"), m,
                                c, this);
            } else {
                if (getSelf().human()) {
                    c.write(getSelf(), deal(c, m, Result.weak, target));
                } else {
                    c.write(getSelf(), receive(c, 0, Result.weak, target));
                }
                target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("pussy"), m,
                                c, this);
            }
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 7;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return c.getStance().en == Stance.neutral ? 50 : 100;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Seduction) >= 5;
    }

    @Override
    public Skill copy(Character user) {
        return new Finger(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("You grope at {other:name-possessive} {other:body-part:pussy}"
                            + ", but miss. (Maybe you should get closer?)", getSelf(), target);
        }
        if (modifier == Result.weak) {
            return Formatter.format("You grope between {other:name-possessive} legs, not really "
                            + "knowing what you're doing. You don't know where {other:pronoun} is"
                            + " the most sensitive, so you rub and stroke every bit of moist flesh "
                            + "under your fingers.", getSelf(), target);
        } else {
            if (target.getArousal().get() <= 15) {
                return Formatter.format("You softly rub the petals of {other:name-possessive}"
                                + " closed flower.", getSelf(), target);
            } else if (target.getArousal().percent() < 50) {
                return Formatter.format("{other:NAME-POSSESSIVE sensitive lower lips start to "
                                + "open up under your skilled touch and you can feel "
                                + "{other:direct-object} becoming wet.", getSelf(), target);
            } else if (target.getArousal().percent() < 80) {
                return Formatter.format("You locate {other:name-possessive} clitoris and "
                                + "caress it directly, causing {other:direct-object} to "
                                + "tremble from the powerful stimulation.", getSelf(), target);
            } else {
                return Formatter.format("You stir {other:name-possessive} increasingly soaked "
                                + "pussy with your fingers and rub {other:possessive} clit "
                                + "with your thumb.", getSelf(), target);
            }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s gropes at %s pussy, but misses the mark.",
                            getSelf().subject(), target.nameOrPossessivePronoun());
        }
        if (modifier == Result.weak) {
            return String.format("%s gropes between %s legs, not really knowing what %s is doing. "
                            + "%s doesn't know where %s the most sensitive, so %s rubs and "
                            + "strokes every bit of %s moist flesh under %s fingers.",
                            getSelf().subject(), target.nameOrPossessivePronoun(), getSelf().pronoun(),
                            getSelf().subject(), target.subjectAction("are", "is"), getSelf().pronoun(),
                            target.possessiveAdjective(), getSelf().possessiveAdjective());
        } else {
            if (target.getArousal().get() <= 15) {
                return String.format("%s softly rubs the petals of %s closed flower.",
                                getSelf().subject(), target.nameOrPossessivePronoun());
            } else if (target.getArousal().percent() < 50) {
                return String.format("%s sensitive lower lips start to open up under"
                                + " %s skilled touch and %s can feel %s becoming wet.",
                                target.nameOrPossessivePronoun(), getSelf().nameOrPossessivePronoun(),
                                target.pronoun(), target.reflectivePronoun());
            } else if (target.getArousal().percent() < 80) {
                return String.format("%s locates %s clitoris and caress it directly, causing"
                                + " %s to tremble from the powerful stimulation.",
                                getSelf().subject(), target.nameOrPossessivePronoun(), target.directObject());
            } else {
                return String.format("%s stirs %s increasingly soaked pussy with %s fingers and "
                                + "rubs %s clit directly with %s thumb.",
                                getSelf().subject(), target.nameOrPossessivePronoun(),
                                getSelf().possessiveAdjective(), target.possessiveAdjective(),
                                getSelf().possessiveAdjective());
            }
        }
    }

    @Override
    public String describe(Combat c) {
        return "Digitally stimulate opponent's pussy, difficult to land without "
                        + "pinning her down.";
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
