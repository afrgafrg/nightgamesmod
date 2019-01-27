package nightgames.skills;

import java.util.Optional;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Kneeling;
import nightgames.status.BodyFetish;

public class FootWorship extends Skill {
    public FootWorship(Character self) {
        super("Foot Worship", self);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.worship);
        addTag(SkillTag.pleasureSelf);
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        Optional<BodyFetish> fetish = getSelf().body.getFetish("feet");
        return user.isPetOf(target) || (fetish.isPresent() && fetish.get().magnitude >= .5);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return target.body.has("feet") && c.getStance().reachBottom(getSelf()) && getSelf().canAct()
                        && !c.getStance().behind(getSelf()) && !c.getStance().behind(target)
                        && target.outfit.hasNoShoes();
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 150;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        Result result = Result.normal;
        int m = 0;
        int n = 0;
        m = 8 + Random.random(6);
        n = 20;
        BodyPart mouth = getSelf().body.getRandom("mouth");
        BodyPart feet = target.body.getRandom("feet");
        if (getSelf().human()) {
            c.write(getSelf(), Formatter.format(deal(c, 0, Result.normal, target), getSelf(), target));
        } else {
            c.write(getSelf(), Formatter.format(receive(c, 0, Result.normal, target), getSelf(), target));
        }
        if (m > 0) {
            target.body.pleasure(getSelf(), mouth, feet, m, c, this);
            if (mouth.isErogenous()) {
                getSelf().body.pleasure(getSelf(), feet, mouth, m, c, this);
            }
        }
        if (n > 0) {
            target.buildMojo(c, n);
        }
        if (!c.getStance().sub(getSelf())) {
            c.setStance(new Kneeling(target, getSelf()), getSelf(), true);
        }
        c.getCombatantData(getSelf()).toggleFlagOn("footworshipped", true);
        return result != Result.miss;
    }

    @Override
    public Skill copy(Character user) {
        return new FootWorship(user);
    }

    @Override
    public int speed() {
        return 2;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (!c.getCombatantData(getSelf()).getBooleanFlag("footworshipped")) {
            return Formatter.format("You throw yourself at {other:name-possessive} dainty "
                            + "feet and start sucking on {other:possessive} toes. {other:PRONOUN} seems "
                            + "surprised at first, but then grins and shoves {other:possessive}"
                            + " toes further in to your mouth, eliciting a moan from you.",
                            getSelf(), target);
        } else {
            return Formatter.format("You can't seem to bring yourself to stop worshipping "
                            + "{other:possessive} feet as your tongue makes its way down to "
                            + "{other:name-possessive} soles. {other:SUBJECT} presses "
                            + "{other:possessive} feet against your face and you feel more "
                            + "addicted to {other:possessive} feet.",
                            getSelf(), target);
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (!c.getCombatantData(getSelf()).getBooleanFlag("footworshipped")) {
            return String.format("%s throws %s at %s feet. %s worshipfully grasps %s feet "
                            + "and starts licking between %s toes, all while %s face displays a mask of ecstasy.",
                            getSelf().subject(), getSelf().reflectivePronoun(), target.nameOrPossessivePronoun(),
                            getSelf().subject(), target.possessiveAdjective(), target.possessiveAdjective(),
                            getSelf().possessiveAdjective());
        }
        return String.format("%s can't seem to get enough of %s feet as %s continues to "
                        + "lick along the bottom of %s soles, %s face further lost in "
                        + "servitude as %s is careful not to miss a spot.", getSelf().subject(),
                        target.nameOrPossessivePronoun(), getSelf().pronoun(),
                        target.possessiveAdjective(), getSelf().possessiveAdjective(),
                        getSelf().pronoun());
    }

    @Override
    public String describe(Combat c) {
        return "Worship opponent's feet: builds mojo for opponent";
    }
}
