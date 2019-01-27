package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.status.Falling;

public class HipThrow extends Skill {

    public HipThrow(Character self) {
        super("Hip Throw", self);
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.judonovice);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return !target.wary() && c.getStance().mobile(getSelf()) && c.getStance().mobile(target)
                        && !c.getStance().prone(getSelf()) && !c.getStance().prone(target) && getSelf().canAct()
                        && !c.getStance().connected(c);
    }

    @Override
    public int getMojoCost(Combat c) {
        return 10;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (getSelf().checkVsDc(Attribute.Power, target.knockdownDC() - target.get(Attribute.Cunning) / 2)) {
            writeOutput(c, Result.normal, target);
            target.pain(c, getSelf(), (int) getSelf().modifyDamage(DamageType.physical, target, Random.random(10, 16)));
            target.add(c, new Falling(target));
            target.emote(Emotion.angry, 5);
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new HipThrow(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.normal) {
            return Formatter.format("{other:subject} rushes toward you, but you step "
                            + "in close and pull {other:direct-object} towards you, "
                            + "using {other:possessive} momentum to throw {other:direct-object}"
                            + " across your hip and onto the floor.",
                            getSelf(), target);
        } else {
            return Formatter.format("As {other:subject} advances, you pull {other:direct-object}"
                            + " towards you and attempt to throw {other:direct-object} over "
                            + "your hip, but {other:pronoun} steps away from the throw and "
                            + "manages to keep {other:possessive} footing.",
                            getSelf(), target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.normal) {
            return String.format("%s a momentary weakness in %s guard and %s toward %s to "
                            + "take advantage of it. The next thing %s, %s %s "
                            + "hitting the floor behind %s.",
                            getSelf().subjectAction("see"), target.nameOrPossessivePronoun(),
                            getSelf().action("lunge"), target.directObject(),
                            getSelf().subjectAction("know"), getSelf().pronoun(),
                            getSelf().action("are", "is"), target.directObject());
        } else {
            return String.format("%s grabs %s arm and pulls %s off balance, but %s %s"
                            + " to plant %s foot behind %s leg sweep. This gives %s a more"
                            + " stable stance than %s and %s has "
                            + "to break away to stay on %s feet.", getSelf().subject(),
                            target.nameOrPossessivePronoun(), target.directObject(),
                            target.pronoun(), target.action("manage"), target.possessiveAdjective(),
                            getSelf().possessiveAdjective(), target.nameDirectObject(),
                            getSelf().nameDirectObject(), getSelf().pronoun(),
                            getSelf().possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Throw your opponent to the ground, dealing some damage: 10 Mojo";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
