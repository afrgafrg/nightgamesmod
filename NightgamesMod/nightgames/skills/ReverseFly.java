package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.FlyingCowgirl;
import nightgames.status.Falling;

public class ReverseFly extends Fly {
    public ReverseFly(Character self) {
        super("ReverseFly", self);
    }

    @Override
    public String describe(Combat c) {
        return "Take off and fuck your opponent's cock in the air.";
    }

    @Override
    public Skill copy(Character target) {
        return new ReverseFly(target);
    }

    @Override
    public BodyPart getSelfOrgan() {
        return getSelf().body.getRandomPussy();
    }

    @Override
    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomCock();
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        String premessage = premessage(c, target);

        Result result = target.roll(getSelf(), c, accuracy(c, target)) ? Result.normal : Result.miss;
        if (getSelf().human()) {
            c.write(getSelf(), premessage + deal(c, 0, result, target));
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(getSelf(), premessage + receive(c, 0, result, target));
        }
        if (result == Result.normal) {
            getSelf().emote(Emotion.dominant, 50);
            getSelf().emote(Emotion.horny, 30);
            target.emote(Emotion.desperate, 50);
            target.emote(Emotion.nervous, 75);

            int m = 5 + Random.random(5);
            int otherm = m;
            if (getSelf().has(Trait.insertion)) {
                otherm += Math.min(getSelf().get(Attribute.Seduction) / 4, 40);
            }
            c.setStance(new FlyingCowgirl(getSelf(), target), getSelf(), getSelf().canMakeOwnDecision());
            target.body.pleasure(getSelf(), getSelfOrgan(), getTargetOrgan(target), otherm, c, this);
            getSelf().body.pleasure(target, getTargetOrgan(target), getSelfOrgan(), m, c, this);
        } else {
            getSelf().add(c, new Falling(getSelf()));
            return false;
        }
        return true;
    }

    @Override
    public String deal(Combat c, int amount, Result modifier, Character target) {
        String msg;
        if (modifier == Result.miss) {
            msg = "You grab {other:name-do} tightly and try to take off. However "
                            + "{other:pronoun} has other ideas. {other:PRONOUN} "
                            + "knees your crotch as you approach and sends "
                            + "you sprawling to the ground.";
        } else {
            msg = "You grab {other:name-do} tightly and take off, inserting {other:possessive}"
                            + " dick into your hungry {self:body-part:cock}.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int amount, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s lunges for %s with a hungry look in %s eyes. However, %s other ideas."
                            + " %s %s %s as %s approaches and send %s sprawling to the floor.",
                            getSelf().subject(), target.nameDirectObject(), getSelf().possessiveAdjective(),
                            target.subjectAction("have", "has"), Formatter.capitalizeFirstLetter(target.pronoun()),
                            target.action("trip"), getSelf().directObject(), getSelf().pronoun(),
                            getSelf().directObject());
        } else {
            return String.format("Suddenly, %s leaps at %s, embracing %s tightly. %s then flaps %s %s"
                            + " hard and before %s %s it,"
                            + " %s twenty feet in the sky held up by %s arms and legs."
                            + " Somehow, %s dick ended up inside of %s in the process and"
                            + " the rhythmic movements of %s flying arouse %s to no end.",
                            getSelf().subject(), target.nameDirectObject(), target.directObject(),
                            Formatter.capitalizeFirstLetter(getSelf().pronoun()),
                            getSelf().possessiveAdjective(), getSelf().body.getRandomWings().describe(getSelf()),
                            target.pronoun(), target.action("know"), target.subjectAction("are", "is"),
                            getSelf().possessiveAdjective(), target.nameOrPossessivePronoun(),
                            getSelf().nameDirectObject(), getSelf().possessiveAdjective(),
                            target.directObject());
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
