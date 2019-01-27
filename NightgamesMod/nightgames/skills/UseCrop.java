package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class UseCrop extends Skill {

    public UseCrop(Character self) {
        super(Item.Crop.getName(), self);
        addTag(SkillTag.usesToy);
        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return (getSelf().has(Item.Crop) || getSelf().has(Item.Crop2)) && getSelf().canAct()
                        && c.getStance().mobile(getSelf())
                        && (c.getStance().reachTop(getSelf()) || c.getStance().reachBottom(getSelf()));
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 10;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), c, accuracy(c, target))) {
            double m = Random.random(12, 18);
            if (target.crotchAvailable() && c.getStance().reachBottom(getSelf())) {
                if (getSelf().has(Item.Crop2) && Random.random(10) > 7 && !target.has(Trait.brassballs)) {
                    writeOutput(c, Result.critical, target);
                    if (target.has(Trait.achilles)) {
                        m += 6;
                    }
                    target.emote(Emotion.angry, 10);
                    m += 8;
                } else {
                    writeOutput(c, Result.normal, target);
                    target.pain(c, getSelf(), 5 + Random.random(12) + target.get(Attribute.Perception) / 2);
                }
            } else {
                writeOutput(c, Result.weak, target);
                m -= Random.random(2, 6);
                target.pain(c, getSelf(), 5 + Random.random(12));
            }
            target.pain(c, getSelf(), (int) getSelf().modifyDamage(DamageType.gadgets, target, m));
            target.emote(Emotion.angry, 15);
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new UseCrop(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.miss) {
            if (!target.has(Item.Crop)) {
                msg = "You lash out with your riding crop, but it fails to connect.";
            } else {
                msg = "You try to hit {other:name-do} with your riding crop, but "
                                + "{other:pronoun} deflects it with {other:possessive} own.";
            }
        } else if (modifier == Result.critical) {
            if (target.hasBalls()) {
                msg = "You strike {other:name-possessive} bare ass with your crop and the "
                                + "'Treasure Hunter' attachment slips between {other:possessive}"
                                + " legs, hitting one of {other:possessive} hanging testicles "
                                + "squarely. {other:PRONOUN} lets out a shriek and clutches "
                                + "{other:possessive} sore nut";
            } else {
                return "You strike {other:name-possessive} bare ass with your crop and the "
                                + "'Treasure Hunter' attachment slips between {other:possessive}"
                                + " legs, impacting on {other:possessive} sensitive pearl. "
                                + "{other:PRONOUN} lets out a high pitched yelp and "
                                + "clutches {other:possessive} injured anatomy.";
            }
        } else if (modifier == Result.weak) {
            msg = "You hit {other:name-do} with your riding crop.";
        } else {
            msg = "You strike {other:name-possessive} soft, bare skin with your riding "
                            + "crop, leaving a visible red mark.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            if (!target.has(Item.Crop)) {
                return String.format("%s out of the way, as %s swings %s riding crop at %s.",
                                target.subjectAction("duck"), getSelf().subject(),
                                getSelf().possessiveAdjective(), target.directObject());
            } else {
                return String.format("%s swings %s riding crop, but %s %s own crop and %s it.",
                                getSelf().subject(), getSelf().possessiveAdjective(),
                                target.subjectAction("draw"), target.possessiveAdjective(),
                                target.action("parry", "parries"));
            }
        } else if (modifier == Result.critical) {
            return String.format("%s hits %s on the ass with %s riding crop. "
                            + "The attachment on the end delivers a painful sting to "
                            + "%s jewels. %s in pain and %s the urge to "
                            + "curl up in the fetal position.", getSelf().subject(),
                            target.nameDirectObject(), getSelf().possessiveAdjective(),
                            target.possessiveAdjective(), target.subjectAction("groan"),
                            target.action("fight"));
        } else if (modifier == Result.weak) {
            return String.format("%s strikes %s with a riding crop.",
                            getSelf().subject(), target.nameDirectObject());
        } else {
            return String.format("%s hits %s bare ass with a riding crop hard enough to leave a painful welt.",
                            getSelf().subject(), target.nameOrPossessivePronoun());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Strike your opponent with riding crop. More effective if they're naked";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
