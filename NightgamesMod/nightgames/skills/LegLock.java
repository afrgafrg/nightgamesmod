package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.status.AttributeBuff;

public class LegLock extends Skill {

    public LegLock(Character self) {
        super("Leg Lock", self);
        // addTag(SkillTag.positioning); it's not, right?
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().dom(getSelf()) && c.getStance().reachBottom(getSelf()) && c.getStance().prone(target)
                        && getSelf().canAct() && !c.getStance().connected(c);
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), c, accuracy(c, target))) {
            writeOutput(c, Result.normal, target);
            target.add(c, new AttributeBuff(target, Attribute.Speed, -2, 5));
            target.pain(c, getSelf(), (int) getSelf().modifyDamage(DamageType.physical, target, Random.random(10, 16)));
            target.emote(Emotion.angry, 15);
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Power) >= 24;
    }

    @Override
    public Skill copy(Character user) {
        return new LegLock(user);
    }

    @Override
    public int speed() {
        return 2;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return Formatter.format("You grab {other:name-possessive} leg, but "
                            + "{other:pronoun} kicks free.", getSelf(), target);
        } else {
            return Formatter.format("You take hold of {other:name-possessive} ankle and "
                            + "force {other:possessive} leg to extend painfully.",
                            getSelf(), target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to put %s in a leglock, but %s %s away.",
                            getSelf().subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("slip"));
        } else {
            return String.format("%s pulls %s leg across %s body in a painful submission hold.",
                            getSelf().subject(), target.nameOrPossessivePronoun(), getSelf().possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c) {
        return "A submission hold on your opponent's leg";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
