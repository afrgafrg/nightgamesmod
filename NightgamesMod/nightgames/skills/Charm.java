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
import nightgames.status.Stsflag;

public class Charm extends Skill {
    public Charm(Character self) {
        super("Charm", self, 4);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canRespond() && c.getStance().facing(getSelf(), target) && !(target.is(Stsflag.wary) || target.is(Stsflag.charmed)) ;
    }

    @Override
    public int getMojoCost(Combat c) {
        if (isPurr(c)) {
            return 0;
        }
        return 30;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (isPurr(c)) {
            return resolvePurr(c, target);
        }
        if (target.human() && target.is(Stsflag.blinded)) {
            printBlinded(c);
            return false;
        }
        if (target.roll(getSelf(), c, accuracy(c, target))) {
            writeOutput(c, Result.normal, target);
            double mag = 2 + Random.random(4) + getSelf().body.getHotness(target);
            if (target.has(Trait.imagination)) {
                mag += 4;
            }
            int m = (int) Math.round(mag);
            target.temptNoSource(c, getSelf(), m, this);
            target.add(c, new Charmed(target));
            target.emote(Emotion.horny, 10);
            getSelf().emote(Emotion.confident, 20);
        } else {
            writeOutput(c, Result.miss, target);
        }
        target.emote(Emotion.horny, 10);
        return true;
    }

    private boolean resolvePurr(Combat c, Character target) {
        if (Random.random(target.getLevel()) <= getSelf().get(Attribute.Animism) * getSelf().getArousal().percent()
                        / 100 && !target.wary()) {
            int damage = getSelf().getArousal().getReal() / 10;
            if (damage < 10) {
                damage = 0;
            }
            writeOutput(c, damage, Result.special, target);
            if (damage > 0) {
                target.temptNoSource(c, getSelf(), damage, this);
            }
            target.add(c, new Charmed(target));
            return true;
        } else {
            writeOutput(c, Result.weak, target);
            return false;
        }        
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return (user.get(Attribute.Cunning) >= 8 && user.get(Attribute.Seduction) > 16) || isPurr(c);
    }

    @Override
    public Skill copy(Character user) {
        return new Charm(user);
    }

    @Override
    public int speed() {
        return 9;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    private boolean isPurr(Combat c) {
        return getSelf().get(Attribute.Animism) >= 9 && getSelf().getArousal().percent() >= 20;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return "You flash a dazzling smile at " + target.directObject() + ", but it wasn't very effective.";
        } else if (modifier == Result.weak){
            return String.format("You let out a soft purr and give %s"
            + " your best puppy dog eyes. %s smiles, but then aims a quick punch"
            + " at your groin, which you barely avoid. "
            + "Maybe you shouldn't have mixed your animal metaphors.",
            target.subject(), Formatter.capitalizeFirstLetter(target.pronoun()));
        } else if (modifier == Result.special) {
            String message = String.format("You give %s"
                            + " an affectionate purr and your most disarming smile. "
                            + "%s battle aura melts away and %s pats your head, completely taken with your "
                            + "endearing behavior.", target.subject(),
                            Formatter.capitalizeFirstLetter(target.possessiveAdjective()),
                            target.pronoun());
            if (damage > 0) {
                message += String.format("\nSome of your apparent arousal seems to have affected %s, "
                                + "%s breath seems shallower than before.",
                                target.nameDirectObject(), target.possessiveAdjective());
            }
            return message;
        } else {
            return getSelf().getName() + " flashes a dazzling smile at "+target.nameDirectObject()+", charming " + target.directObject() + " instantly";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return getSelf().getName() + " flashes a dazzling smile at "+target.directObject() + ", but it wasn't very effective.";
        } else if (modifier == Result.weak){
            return String.format("%s slumps submissively and purrs. It's cute, but %s's not going "
                            + "to get the better of %s.", getSelf().subject(), getSelf().pronoun(),
                            target.nameDirectObject());
        } else if (modifier == Result.special) {
            String message = String.format("%s purrs cutely, and looks up at %s with sad eyes. Oh God,"
                            + " %s's so adorable! It'd be mean to beat %s too quickly. "
                            + "Maybe %s should let %s get some "
                            + "attacks in while %s %s watching %s earnest efforts.",
                            getSelf().subject(), target.nameDirectObject(),
                            getSelf().pronoun(), getSelf().directObject(), target.subject(), getSelf().directObject(),
                            target.pronoun(), target.action("enjoy"), getSelf().possessiveAdjective());
            if (damage > 0) {
                message += String.format("\nYou're not sure if this was intentional, but %s flushed "
                                + "face and ragged breathing makes the act a lot more erotic than "
                                + "you would expect. %s to contain %s need to fuck the little kitty in heat.",
                                getSelf().nameOrPossessivePronoun(), 
                                Formatter.capitalizeFirstLetter(target.subjectAction("try", "tries")),
                                target.possessiveAdjective());
            }
            return message;
        } else {
            return getSelf().getName() + " flashes a dazzling smile at "+target.nameDirectObject()+", charming " + target.directObject() + " instantly.";
        }
    }

    @Override
    public String describe(Combat c) {
        return "Charms your opponent into not hurting you.";
    }

    @Override
    public String getLabel(Combat c) {
        if (isPurr(c)) {
            return "Purr";
        }
        return getName(c);
    }
}
