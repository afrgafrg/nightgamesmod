package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.TentaclePart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Bound;
import nightgames.status.Oiled;
import nightgames.status.Stsflag;

public class TentaclePorn extends Skill {

    public TentaclePorn(Character self) {
        super("Tentacle Porn", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Fetish) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return !target.wary() && !c.getStance().sub(getSelf()) && !c.getStance().prone(getSelf())
                        && !c.getStance().prone(target) && getSelf().canAct() && getSelf().getArousal().get() >= 20;
    }

    @Override
    public int getMojoCost(Combat c) {
        return 10;
    }

    @Override
    public String describe(Combat c) {
        return "Create a bunch of hentai tentacles.";
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), c, accuracy(c, target))) {
            if (target.mostlyNude()) {
                int m = Random.random(getSelf().get(Attribute.Fetish)) / 2 + 1;
                if (target.bound()) {
                    writeOutput(c, Result.special, target);
                    if (target.hasDick())
                        TentaclePart.pleasureWithTentacles(c, target, m, target.body.getRandomCock());
                    if (target.hasPussy())
                        TentaclePart.pleasureWithTentacles(c, target, m, target.body.getRandomPussy());
                    TentaclePart.pleasureWithTentacles(c, target, m, target.body.getRandomBreasts());
                    TentaclePart.pleasureWithTentacles(c, target, m, target.body.getRandomAss());
                } else if (getSelf().human()) {
                    c.write(getSelf(), deal(c, 0, Result.normal, target));
                    TentaclePart.pleasureWithTentacles(c, target, m, target.body.getRandom("skin"));
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(getSelf(), receive(c, 0, Result.normal, target));
                    TentaclePart.pleasureWithTentacles(c, target, m, target.body.getRandom("skin"));
                }
                if (!target.is(Stsflag.oiled)) {
                    target.add(c, new Oiled(target));
                }
                target.emote(Emotion.horny, 20);
            } else {
                writeOutput(c, Result.weak, target);
            }
            target.add(c, new Bound(target, 30 + 2 * Math.sqrt(getSelf().get(Attribute.Fetish) + getSelf().get(Attribute.Slime)), "tentacles"));
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new TentaclePorn(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.miss) {
            msg = "You summon a mass of tentacles that try to snare {other:name-do}"
                            + ", but {other:pronoun} nimbly dodges them.";
        } else if (modifier == Result.weak) {
            msg = "You summon a mass of phallic tentacles that wrap around {other:name-possessive}"
                            + " arms, holding {other:direct-object} in place.";
        } else if (modifier == Result.normal) {
            msg = "You summon a mass of phallic tentacles that wrap around {other:name-possessive}"
                            + " naked body. They squirm against {other:direct-object} and squirt slimy"
                            + " fluids on {other:possessive} body.";
        } else {
            msg = "You summon tentacles to toy with {other:name-possessive}"
                            + " helpless form. The tentacles toy with {other:possessive}"
                            + " breasts and penetrate {other:possessive} pussy and ass.";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s stomps on the ground and a bundle of tentacles erupt from the "
                            + "ground. %s barely able to avoid them.", getSelf().subject(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("are", "is")));
        } else if (modifier == Result.weak) {
            return String.format("%s stomps on the ground and a bundle of tentacles erupt from the "
                            + "ground around %s, entangling %s arms and legs.", getSelf().subject(),
                            target.nameDirectObject(), target.possessiveAdjective());
        } else if (modifier == Result.normal) {
            return String.format("%s stomps on the ground and a bundle of tentacles erupt from the "
                            + "ground around %s, entangling %s arms and legs. The slimy appendages "
                            + "wriggle over %s body and coat %s in the slippery liquid.",
                            getSelf().subject(), target.nameDirectObject(), target.possessiveAdjective(),
                            target.possessiveAdjective(), target.directObject());
        } else {
            String actions = "";
            if (target.hasDick())
                actions += String.format("tease %s %s", target.possessiveAdjective(), 
                                target.body.getRandomCock().describe(target));
            
            if (target.hasPussy())
                actions += String.format("%scaress %s clit", actions.length() > 0 ? ", " : "", 
                                target.possessiveAdjective());
            
            if (target.body.getRandomBreasts() != BreastsPart.flat)
                actions += String.format("%sknead %s %s" ,actions.length() > 0 ? ", " : "", 
                                target.possessiveAdjective(),
                                target.body.getRandomBreasts().describe(target));
            
            if (actions.length() > 0)
                actions += ", and";
            return String.format("%s summons slimy tentacles that cover %s helpless body,"
                            + " %s probe %s ass.", getSelf().subject(),
                            target.nameOrPossessivePronoun(), actions,
                            target.possessiveAdjective());
        }
    }
}
