package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.BD;
import nightgames.status.Stsflag;

public class Bondage extends Skill {

    public Bondage(Character self) {
        super("Bondage", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Fetish) >= 6;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canRespond() && c.getStance().mobile(getSelf()) && getSelf().getArousal().get() >= 5
                        && !getSelf().is(Stsflag.bondage);
    }

    @Override
    public String describe(Combat c) {
        return "You and your opponent become aroused by being tied up for five turns: Arousal at least 5";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        writeOutput(c, Result.normal, target);
        getSelf().add(c, new BD(getSelf()));
        target.add(c, new BD(target));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Bondage(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "You imagine the exhilarating feeling of ropes digging into your skin and binding you. You push this feeling into "
                        + target.getName() + "'s libido.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return String.format("%s and wraps %s arms around herself tightly. "
                        + "Suddenly the thought of being tied up and dominated slips into %s head.",
                        getSelf().subjectAction("flush"), getSelf().possessiveAdjective(),
                        target.nameOrPossessivePronoun());
    }

}
