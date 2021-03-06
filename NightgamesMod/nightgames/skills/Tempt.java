package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Charmed;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;
import nightgames.status.Trance;

public class Tempt extends Skill {

    public Tempt(Character self) {
        super("Tempt", self);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canRespond();
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        writeOutput(c, Result.normal, target);
        double m = 4 + Random.random(4);

        if (c.getStance().front(getSelf())) {
            // opponent can see self
            m += getSelf().body.getHotness(target);
        }

        if (target.has(Trait.imagination)) {
            m *= 1.5;
        }

        int n = (int) Math.round(m);

        boolean tempted = Random.random(5) == 0;
        if (getSelf().has(Trait.darkpromises) && tempted && !target.wary() && getSelf().canSpend(15)) {
            getSelf().spendMojo(c, 15);
            c.write(getSelf(),
                            Formatter.format("{self:NAME-POSSESSIVE} words fall on fertile grounds. {other:NAME-POSSESSIVE} will to resist crumbles in light of {self:possessive} temptation.",
                                            getSelf(), target));
            target.add(c, new Enthralled(target, getSelf(), 3));
        } else if (getSelf().has(Trait.commandingvoice) && Random.random(3) == 0) {
            c.write(getSelf(), Formatter.format("{self:SUBJECT-ACTION:speak|speaks} with such unquestionable"
                            + " authority that {other:subject-action:don't|doesn't} even consider disobeying."
                            , getSelf(), target));
            target.add(c, new Trance(target, 1, false));
        } else if (getSelf().has(Trait.MelodiousInflection) && !target.is(Stsflag.charmed) && Random.random(3) == 0) {
            c.write(getSelf(), Formatter.format("Something about {self:name-possessive} words, the"
                            + " way {self:possessive} voice rises and falls, {self:possessive}"
                            + " pauses and pitch... {other:SUBJECT} soon {other:action:find|finds}"
                            + " {other:reflective} utterly hooked.", getSelf(), target));
            target.add(c, new Charmed(target, 2).withFlagRemoved(Stsflag.mindgames));
        }

        target.temptNoSource(c, getSelf(), n, this);
        target.emote(Emotion.horny, 10);
        getSelf().emote(Emotion.confident, 10);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Seduction) >= 15;
    }

    @Override
    public Skill copy(Character user) {
        return new Tempt(user);
    }

    @Override
    public int speed() {
        return 9;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return getSelf().temptLiner(c, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return getSelf().temptLiner(c, target);
    }

    @Override
    public String describe(Combat c) {
        return "Tempts your opponent. More effective if they can see you.";
    }
}
