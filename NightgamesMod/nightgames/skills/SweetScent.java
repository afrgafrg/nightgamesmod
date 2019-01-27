package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Frenzied;

public class SweetScent extends Skill {
    public SweetScent(Character self) {
        super("Sweet Scent", self, 5);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canRespond() && !target.wary();
    }

    @Override
    public int getMojoCost(Combat c) {
        return 30;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        Result res = target.roll(getSelf(), c, accuracy(c, target)) ? Result.normal : Result.miss;

        writeOutput(c, res, target);
        if (res != Result.miss) {
            target.arouse(25, c);
            target.emote(Emotion.horny, 100);
            target.add(c, new Frenzied(target, 8));
        }
        return res != Result.miss;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Bio) >= 5;
    }

    @Override
    public Skill copy(Character user) {
        return new SweetScent(user);
    }

    @Override
    public int speed() {
        return 9;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier != Result.miss) {
            msg = "You breathe out a dizzying pink gas which spreads through the area. {other:name-do}"
                            + " quickly succumbs to the coying scent as {other:possessive} "
                            + "whole body flushes with arousal.";
        } else {
            msg = "You breathe out a dizzying pink gas, but {other:subject}"
                            + " covers {other:possessive} face and dodges out of the cloud.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier != Result.miss) {
            return String.format("%s breathes out a dizzying pink gas which spreads through the area. "
                            + "%s quickly %s to the coying scent as %s whole"
                            + " body flushes with arousal.", getSelf().subject(),
                            Formatter.capitalizeFirstLetter(target.subject()),
                            target.action("succumb"), target.possessiveAdjective());
        } else {
            return String.format("%s breathes out a dizzying pink gas, but %s to cover"
                            + " %s face and dodge out of the cloud.", getSelf().subject(),
                            target.subjectAction("manage"), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Breathe out a sweet scent to send your opponent into a frenzy.";
    }
}
