package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class Kick extends Skill {

    public Kick(Character self) {
        super("Kick", self);
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Power) >= 17;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().feet(getSelf(), target) && getSelf().canAct() && (!c.getStance().prone(getSelf())
                        || getSelf().has(Trait.dirtyfighter) && !c.getStance().connected(c));
    }

    @Override
    public int getMojoCost(Combat c) {
        return 5;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.getOutfit().slotShreddable(ClothingSlot.bottom) && getSelf().get(Attribute.Ki) >= 14
                        && Random.random(3) == 2) {
            writeOutput(c, Result.special, target);
            target.shred(ClothingSlot.bottom);
        } else if (target.roll(getSelf(), c, accuracy(c, target))) {
            double m = Random.random(16, 21);
            if (target.has(Trait.brassballs)) {
                m *= .8;
            }
            if (getSelf().human()) {
                if (c.getStance().prone(getSelf())) {
                    c.write(getSelf(), deal(c, 0, Result.strong, target));
                } else {
                    c.write(getSelf(), deal(c, 0, Result.normal, target));

                }
            } else if (c.shouldPrintReceive(target, c)) {
                if (c.getStance().prone(getSelf())) {
                    c.write(getSelf(), receive(c, 0, Result.strong, target));
                } else {
                    c.write(getSelf(), receive(c, 0, Result.normal, target));
                }
            }
            if (target.has(Trait.achilles) && !target.has(ClothingTrait.armored)) {
                m += 14 + Random.random(4);
            }
            if (target.has(ClothingTrait.armored)) {
                m = m / 2;
            }
            target.pain(c, getSelf(), (int) getSelf().modifyDamage(DamageType.physical, target, m));
            target.emote(Emotion.angry, 20);
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Kick(user);
    }

    @Override
    public int speed() {
        return 8;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.damage;
    }

    @Override
    public String getLabel(Combat c) {
        if (getSelf().get(Attribute.Ki) >= 14) {
            return "Shatter Kick";
        } else {
            return "Kick";
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return "Your kick hits nothing but air.";
        }
        if (modifier == Result.special) {
            return Formatter.format("You focus your ki into a single kick, targeting not " 
                            + "{other:name-possessive} body, but {other:possessive} "
                            + target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName()
                            + ". The garment is completely destroyed, but "
                            + "{other:pronoun} is safely left completely unharmed. Wait, "
                            + "you are actually fighting right now, aren't you?", 
                            getSelf(), target);
        }
        if (modifier == Result.strong) {
            return Formatter.format("Lying on the floor, you feign exhaustion, hoping "
                            + "{other:subject} will lower {other:possessive} guard. As "
                            + "{other:pronoun} approaches unwarily, you suddenly kick up "
                            + "between {other:possessive} legs, delivering a painful hit "
                            + "to {other:possessive} sensitive vulva.", 
                            getSelf(), target);
        } else {
            return Formatter.format("You deliver a swift kick between {other:name-possessive}"
                            + " legs, hitting {other:direct-object} squarely on the baby maker.", 
                            getSelf(), target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return getSelf().getName() + "'s kick hits nothing but air.";
        }
        if (modifier == Result.special) {
            return Formatter.format("{self:SUBJECT} launches a powerful kick straight at {other:name-possessive} groin, but pulls it back "
                            + "just before impact. {other:pronoun-action:feel|feels} a chill run down {other:possessive} spine and {other:possessive} {other:balls-vulva} "
                            + "are grateful for the last second reprieve. {other:POSSESSIVE} %s crumble off {other:possessive} body,"
                            + " practically disintegrating.... Still somewhat grateful.", getSelf(), target,
                            target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName());
        }
        if (modifier == Result.strong) {
            return Formatter.format("With {other:name-do} flat on {other:possessive} back, {self:subject-action:quickly move|quickly moves} in to press {self:possessive} advantage. "
                            + "Faster than {other:pronoun} can react, {self:possessive} foot shoots up between "
                            + "{other:possessive} legs, dealing a critical hit on {other:possessive} unprotected {other:balls-vulva}.", getSelf(), target);
        } else {
            return Formatter.format("{self:NAME-POSSESSIVE} foot lashes out into {other:name-possessive} delicate {other:balls-vulva} with devastating force.", getSelf(), target);
        }
    }

    @Override
    public String describe(Combat c) {
        return "Kick your opponent in the groin";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
