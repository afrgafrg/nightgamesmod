package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.skills.damage.Staleness;
import nightgames.stance.Stance;
import nightgames.status.DurationStatus;
import nightgames.status.Lovestruck;
import nightgames.status.Stsflag;

public class Kiss extends Skill {
    private static final String divineString = "Kiss of Baptism";
    private static final int divineCost = 30;

    public Kiss(Character self) {
        // kiss starts off strong, but becomes stale fast. It recovers pretty quickly too, but makes spamming it less effective
        super("Kiss", self, 0, Staleness.build().withDefault(1.0).withFloor(.20).withDecay(.50).withRecovery(.10));
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().kiss(getSelf(), target) && getSelf().canAct();
    }

    @Override
    public int getMojoBuilt(Combat c) {
        if (getLabel(c).equals(divineString)) {
            return 0;
        }
        return 10 + (getSelf().has(Trait.romantic) ? 5 : 0);
    }

    @Override
    public int getMojoCost(Combat c) {
        if (getLabel(c).equals(divineString)) {
            return divineCost;
        }
        return 0;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        int accuracy = c.getStance().en == Stance.neutral ? 70 : 100;
        if (getSelf().has(Trait.romantic)) {
            accuracy += 20;
        }
        return accuracy;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int m = Random.random(6, 10);
        if (!target.roll(getSelf(), c, accuracy(c, target))) {
            writeOutput(c, Result.miss, target);
            return false;
        }
        boolean deep = getLabel(c).equals("Deep Kiss");
        if (getSelf().has(Trait.romantic)) {
            m += 2;
            // if it's an advanced kiss.
            if (!getLabel(c).equals("Kiss")) {
                m += 2;
            }
        }
        Result res = Result.normal;
        if (getSelf().get(Attribute.Seduction) >= 9) {
            m += Random.random(4, 6);
            res = Result.normal;
        } else {
            res = Result.weak;
        }
        if (deep) {
            m += 2;
            res = Result.special;
        }
        if (getSelf().has(Trait.experttongue)) {
            m += 2;
            res = Result.special;
        }
        if (getSelf().has(Trait.soulsucker)) {
            res = Result.upgrade;
        }
        if (getLabel(c).equals(divineString)) {
            res = Result.divine;
            m += 12;
        }
        writeOutput(c, res, target);
        if (res == Result.upgrade) {
            target.drain(c, getSelf(), (int) getSelf().modifyDamage(DamageType.drain, target, target.getStamina().max() / 8));
            target.drainWillpowerAsMojo(c, getSelf(), (int) getSelf().modifyDamage(DamageType.drain, target, 2), 2);
        }
        if (res == Result.divine) {
            target.buildMojo(c, 50);
            target.heal(c, 100);
            target.loseWillpower(c, Random.random(3) + 2, false);
            target.add(c, new Lovestruck(target, getSelf(), 2));
            getSelf().usedAttribute(Attribute.Divinity, c, .5);
        }
        if (getSelf().has(Trait.TenderKisses) && target.is(Stsflag.charmed) && Random.random(3) == 0) {
            DurationStatus charmed = (DurationStatus) target.getStatus(Stsflag.charmed);
            charmed.setDuration(charmed.getDuration() + Random.random(1, 2));
            c.write(getSelf(), Formatter.format("<b>The exquisite tenderness of {self:name-possessive} kisses"
                            + " reinforces the haze clouding {other:name-possessive} mind.</b>", getSelf(), target));
        }
        BodyPart selfMouth = getSelf().body.getRandom("mouth");
        target.body.pleasure(getSelf(), selfMouth, target.body.getRandom("mouth"), m, c, this);
        int selfDamage = Math.max(1, m / 4);
        if (selfMouth.isErogenous()) {
            selfDamage = m / 2;
        }
        getSelf().body.pleasure(target, target.body.getRandom("mouth"), selfMouth, selfDamage, c, this);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Seduction) >= 3;
    }

    @Override
    public Skill copy(Character user) {
        return new Kiss(user);
    }

    @Override
    public int speed() {
        return 6;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.miss) {
            msg = "You pull {other:name-do} in for a kiss, but {other:pronoun} pushes your "
                            + "face away. Rude. (Maybe you should "
                            + "try pinning {other:direct-object} down?)";
        }
        if (modifier == Result.divine) {
            msg = "You pull {other:name-do} to you and kiss {other:direct-object} passionately, "
                            + "sending your divine aura into {other:possessive} body though "
                            + "{other:possessive} mouth. You tangle your tongue around "
                            + "{other:poss-pronoun} and probe the sensitive insides of "
                            + "{other:possessive} mouth while mirroring the action in the "
                            + "space of {other:possessive} soul, sending quakes of pleasure "
                            + "through {other:possessive} physical and astral body. "
                            + "As you finally break the kiss, {other:pronoun} looks energized "
                            + "but desperate for more.";
        }
        if (modifier == Result.special) {
            msg = "You pull {other:name-do} to you and kiss {other:direct-object} passionately. "
                            + "You run your tongue over {other:possessive} lips until "
                            + "{other:pronoun} opens them and immediately invade "
                            + "{other:possessive} mouth. You tangle your tongue around "
                            + "{other:poss-pronoun} and probe the sensitive insides "
                            + "{other:possessive} mouth. As you finally break the kiss, "
                            + "{other:pronoun} leans against you, looking kiss-drunk and needy.";
        }
        if (modifier == Result.upgrade) {
            msg = "You pull {other:name-do} to you and kiss {other:direct-object} passionately. "
                            + "You run your tongue over {other:possessive} lips until "
                            + "{other:pronoun} opens them and immediately invade {other:possessive}"
                            + " mouth. You focus on {other:possessive} lifeforce inside "
                            + "{other:direct-object} and draw it out through the kiss while "
                            + "overwhelming {other:possessive} defenses with heady pleasure. "
                            + "As you finally break the kiss, {other:pronoun} leans against you, "
                            + "looking kiss-drunk and needy.";
        } else if (modifier == Result.weak) {
            msg = "You aggressively kiss {other:name-do} on the lips. It catches "
                            + "{other:direct-object} off guard for a moment, but "
                            + "{other:pronoun} soon responds approvingly.";
        } else {
            switch (Random.random(4)) {
                case 0:
                    msg = "You pull {other:name-do} close and capture {other:possessive} lips. "
                                    + "{other:PRONOUN} returns the kiss enthusiastically and "
                                    + "lets out a soft noise of approval when you "
                                    + "push your tongue into {other:possessive} mouth.";
                    break;
                case 1:
                    msg = "You press your lips to {other:poss-pronoun} in a romantic kiss. "
                                    + "You tease out {other:possessive} tongue and meet it "
                                    + "with your own.";
                    break;
                case 2:
                    msg = "You kiss {other:name-do} deeply, overwhelming {other:possessive}"
                                    + " senses and swapping quite a bit of saliva.";
                    break;
                default:
                    msg = "You steal a quick kiss from {other:name-do}, pulling back "
                                    + "before {other:pronoun} can respond. As {other:pronoun}"
                                    + " hesitates in confusion, you kiss {other:direct-object}"
                                    + " twice more, lingering on the last to run "
                                    + "your tongue over {other:possessive} lips.";
                    break;
            }

        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s pulls you in for a kiss, but you manage to "
                            + "push %s face away.", getSelf().subject(), 
                            getSelf().possessiveAdjective());
        }
        if (modifier == Result.divine) {
            return String.format("%s seductively pulls %s into a deep kiss. As first %s %s to match %s enthusiastic"
                            + " tongue with %s own, but %s starts using %s divine energy to directly attack %s soul. "
                            + "Golden waves of ecstacy flow through %s body, completely shattering every single thought %s and replacing them with %s.",
                            getSelf().subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("try", "tries"), getSelf().possessiveAdjective(),
                            target.possessiveAdjective(), getSelf().subject(), getSelf().possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(), 
                            target.subjectAction("hold"), getSelf().reflectivePronoun());
        }
        if (modifier == Result.upgrade) {
            return String.format("%s seductively pulls %s into a deep kiss. As first %s %s to match %s "
                            + "enthusiastic tongue with %s own, but %s %s quickly overwhelmed. "
                            + "%s to feel weak as the kiss continues, and %s %s %s is "
                            + "draining %s; %s kiss is sapping %s will to fight through %s connection! "
                            + "%s to resist, but %s splendid tonguework prevents "
                            + "%s from mounting much of a defense.",
                            getSelf().subject(), target.nameDirectObject(), target.subject(),
                            target.action("try", "tries"), getSelf().possessiveAdjective(),
                            target.possessiveAdjective(), target.pronoun(), target.action("are", "is"),
                            Formatter.capitalizeFirstLetter(target.subjectAction("start")),
                            target.pronoun(), target.action("realize"), getSelf().subject(),
                            target.directObject(), getSelf().possessiveAdjective(), 
                            target.nameOrPossessivePronoun(), c.bothPossessive(target), 
                            Formatter.capitalizeFirstLetter(target.subjectAction("try", "tries")),
                            getSelf().nameOrPossessivePronoun(), target.directObject());
        }
        if (modifier == Result.special) {
            return String.format("%s seductively pulls %s into a deep kiss. As first %s %s to match %s "
                            + "enthusiastic tongue with %s own, but %s %s quickly overwhelmed. %s draws "
                            + "%s tongue into %s mouth and sucks on it in a way that seems to fill %s "
                            + "mind with a pleasant, but intoxicating fog.",
                            getSelf().subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("try", "tries"), getSelf().possessiveAdjective(),
                            target.possessiveAdjective(), target.pronoun(), target.action("are", "is"),
                            getSelf().subject(), target.nameOrPossessivePronoun(),
                            getSelf().possessiveAdjective(), target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format("%s presses %s lips against %s in a passionate, if not particularly skillful, kiss.",
                            getSelf().subject(), getSelf().possessiveAdjective(),
                            target.human() ? "yours" : target.nameOrPossessivePronoun());
        } else {
            switch (Random.random(3)) {
                case 0:
                    return String.format("%s grabs %s and kisses %s passionately on the mouth. "
                                    + "As %s for air, %s gently nibbles on %s bottom lip.",
                                    getSelf().subject(), target.nameDirectObject(), target.directObject(),
                                    target.subjectAction("break"), getSelf().subject(), target.possessiveAdjective());
                case 1:
                    return String.format("%s peppers quick little kisses around %s mouth before suddenly"
                                    + " taking %s lips forcefully and invading %s mouth with %s tongue.",
                                    getSelf().subject(), target.nameOrPossessivePronoun(),
                                    target.possessiveAdjective(), target.possessiveAdjective(),
                                    getSelf().possessiveAdjective());
                default:
                    return String.format("%s kisses %s softly and romantically, slowly drawing %s into %s "
                                    + "embrace. As %s part, %s teasingly brushes %s lips against %s.",
                                    getSelf().subject(), target.nameDirectObject(), target.directObject(),
                                    getSelf().possessiveAdjective(), c.bothSubject(target),
                                    getSelf().subject(), target.possessiveAdjective(),
                                    target.human() ? "yours" : target.possessiveAdjective());
            }
        }
    }

    @Override
    public String describe(Combat c) {
        return "Kiss your opponent";
    }

    @Override
    public boolean makesContact() {
        return true;
    }

    @Override
    public String getLabel(Combat c) {
        if (getSelf().get(Attribute.Divinity) >= 1 && getSelf().canSpend(divineCost)) {
            return divineString;
        } else if (getSelf().has(Trait.soulsucker)) {
            return "Drain Kiss";
        } else if (getSelf().get(Attribute.Seduction) >= 20) {
            return "Deep Kiss";
        } else {
            return "Kiss";
        }
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}
