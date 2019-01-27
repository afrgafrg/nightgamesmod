package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Stance;

public class UseDildo extends Skill {

    public UseDildo(Character self) {
        super(Item.Dildo.getName(), self);
        addTag(SkillTag.usesToy);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return (getSelf().has(Item.Dildo) || getSelf().has(Item.Dildo2)) && getSelf().canAct() && target.hasPussy()
                        && c.getStance().reachBottom(getSelf()) && target.crotchAvailable()
                        && !c.getStance().vaginallyPenetrated(c, target);
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return c.getStance().en == Stance.neutral ? 50 : 100;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), c, accuracy(c, target))) {
            int m;
            if (getSelf().has(Item.Dildo2)) {
                writeOutput(c, Result.upgrade, target);
                m = Random.random(10, 20);
            } else {
                writeOutput(c, Result.normal, target);
                m = Random.random(5, 15);
                
            }

            m = (int)getSelf().modifyDamage(DamageType.gadgets, target, m);
            target.body.pleasure(getSelf(), null, target.body.getRandom("pussy"), m, c, this);
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new UseDildo(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.miss) {
            msg = "You try to slip a dildo into {other:name-do}, but {other:pronoun} blocks it.";
        } else if (modifier == Result.upgrade) {
            msg = "You touch the imperceptibly vibrating dildo to {other:name-possessive}"
                            + " love button and {other:pronoun} jumps as if shocked. Before "
                            + "{other:pronoun} can defend herself, you slip it into {other:possessive} "
                            + "{other:body-part:pussy}. {other:PRONOUN} starts moaning in pleasure immediately.";
        } else {
            msg = "You rub the dildo against {other:name-possessive} lower lips to lubricate "
                            + "it before you thrust it inside {other:direct-object}. {other:PRONOUN}"
                            + " can't help moaning a little as you pump the rubber toy in and"
                            + " out of {other:possessive} {other:body-part:pussy}.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override

    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:try|tries} to slip a dildo into {other:name-do}, but {other:pronoun-action:block|blocks} it.",
                            getSelf(), target);
        } else if (modifier == Result.upgrade) {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:touch|touches} the imperceptibly vibrating dildo to {other:possessive} love button and {other:subject-action:jump|jumps} as if shocked. Before {other:subject} can defend {other:reflective}, {self:subject} "
                                            + "slips it into {other:possessive} {other:body-part:pussy}. {other:SUBJECT-ACTION:start|starts} moaning in pleasure immediately.",
                            getSelf(), target);
        } else {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:rub|rubs} the dildo against {other:name-possessive} lower lips to lubricate it before {self:pronoun-action:thrust|thrusts} it inside {other:name-do}. "
                                            + "{other:SUBJECT} can't help but moan a little as {self:subject-action:pump|pumps} the rubber toy in and out of {other:possessive} {other:body-part:pussy}.",
                            getSelf(), target);
        }
    }

    @Override
    public String describe(Combat c) {
        return "Pleasure opponent with your dildo";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
