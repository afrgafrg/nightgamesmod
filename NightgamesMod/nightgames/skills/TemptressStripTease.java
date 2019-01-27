package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.Stance;
import nightgames.status.Alluring;
import nightgames.status.Charmed;

public class TemptressStripTease extends StripTease {

    public TemptressStripTease(Character self) {
        super("Skillful Strip Tease", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.temptress) && user.get(Attribute.Technique) >= 8;
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return isDance(c) ? 0 : super.getMojoBuilt(c);
    }

    @Override
    public int getMojoCost(Combat c) {
        return isDance(c) ? super.getMojoBuilt(c) : super.getMojoCost(c);
    }

    public boolean canStrip(Combat c, Character target) {
        boolean sexydance = c.getStance().enumerate() == Stance.neutral && getSelf().canAct() && getSelf().mostlyNude();
        boolean normalstrip = !getSelf().mostlyNude();
        return getSelf().stripDifficulty(target) == 0 && (sexydance || normalstrip);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return canStrip(c, target) && getSelf().canAct() && c.getStance().mobile(getSelf())
                        && !c.getStance().prone(getSelf());
    }

    @Override
    public String getLabel(Combat c) {
        return isDance(c) ? "Sexy Dance" : super.getLabel(c);
    }

    @Override
    public String describe(Combat c) {
        return isDance(c) ? "Do a slow, titilating dance to charm your opponent."
                        : "Shed your clothes seductively, charming your opponent.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int technique = getSelf().get(Attribute.Technique);
        //assert technique > 0;

        if (isDance(c)) {
            if (getSelf().human()) {
                c.write(getSelf(), deal(c, 0, Result.weak, target));
            } else {
                c.write(getSelf(), receive(c, 0, Result.weak, target));
            }
            target.temptNoSource(c, getSelf(), 10 + Random.random(Math.max(5, technique)), this);
            if (Random.random(2) == 0) {
                target.add(c, new Charmed(target, Random.random(Math.min(3, technique))));
            }
            getSelf().add(c, new Alluring(getSelf(), 3));
        } else {
            if (getSelf().human()) {
                c.write(getSelf(), deal(c, 0, Result.normal, target));
            } else {
                c.write(getSelf(), receive(c, 0, Result.normal, target));
            }

            target.temptNoSource(c, getSelf(), 15 + Random.random(Math.max(10, technique)), this);
            target.add(c, new Charmed(target, Random.random(Math.min(5, technique))));
            getSelf().add(c, new Alluring(getSelf(), 5));
            getSelf().undress(c);
        }
        target.emote(Emotion.horny, 30);
        getSelf().emote(Emotion.confident, 15);
        getSelf().emote(Emotion.dominant, 15);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new TemptressStripTease(user);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (isDance(c)) {
            return String.format("%s backs up a little and starts swinging"
                            + " %s hips side to side. Curious as to what's going on, %s"
                            + " %s attacks and watch as %s bends and curves, putting"
                            + " on a slow dance that would be very arousing even if %s weren't"
                            + " naked. Now, without a stitch of clothing to obscure %s view,"
                            + " the sight stirs %s imagination. %s shocked out of %s"
                            + " reverie when %s plants a soft kiss on %s lips, and %s dreamily"
                            + " %s into %s eyes as %s gets back into a fighting stance.",
                            getSelf().subject(), getSelf().possessiveAdjective(),
                            target.subjectAction("cease"),
                            target.possessiveAdjective(), getSelf().pronoun(), getSelf().pronoun(),
                            target.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.subjectAction("are", "is"),
                            target.possessiveAdjective(), getSelf().subject(), 
                            target.possessiveAdjective(), target.pronoun(),
                            target.action("gaze"), getSelf().possessiveAdjective(), getSelf().pronoun());
        } else {
            return String.format("%s takes a few steps back and starts "
                            + "moving sinously. %s sensually runs %s hands over %s body, "
                            + "undoing straps and buttons where %s encounters them, and starts"
                            + " peeling %s clothes off slowly, never breaking eye contact."
                            + " %s can only gawk in amazement as %s perfect body is revealed bit"
                            + " by bit, and the thought of doing anything to blemish such"
                            + " perfection seems very unpleasant indeed.", getSelf().subject(),
                            Formatter.capitalizeFirstLetter(getSelf().pronoun()),
                            getSelf().possessiveAdjective(), getSelf().pronoun(),
                            getSelf().possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subject()),
                            getSelf().possessiveAdjective());
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (isDance(c)) {
            return "You slowly dance for " + target.getName() + ", showing off" + " your naked body.";
        } else {
            return "You seductively perform a short dance, shedding clothes as you do so. " + target.getName()
                            + " seems quite taken with it, as " + target.pronoun()
                            + " is practically drooling onto the ground.";
        }
    }

    private boolean isDance(Combat c) {
        return !super.usable(c, c.getOpponent(getSelf())) && usable(c, c.getOpponent(getSelf()));
    }
}
