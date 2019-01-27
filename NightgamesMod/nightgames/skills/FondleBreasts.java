package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.mods.SizeMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.stance.Stance;

public class FondleBreasts extends Skill {

    public FondleBreasts(Character self) {
        super("Fondle Breasts", self);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return c.getStance().reachTop(getSelf()) && target.hasBreasts() && getSelf().canAct();
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 7;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int m = 6 + Random.random(4);
        Result result = Result.normal;
        if (target.roll(getSelf(), c, accuracy(c, target))) {
            if (target.breastsAvailable()) {
                m += 4;
                result = Result.strong;
            } else if (target.outfit.getTopOfSlot(ClothingSlot.top).getLayer() <= 1 && getSelf().has(Trait.dexterous)) {
                m += 4;
                result = Result.special;
            }
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }

        writeOutput(c, result, target);
        target.body.pleasure(getSelf(), getSelf().body.getRandom("hands"), target.body.getRandom("breasts"), m,
                        c, this);

        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new FondleBreasts(user);
    }

    @Override
    public int speed() {
        return 6;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return c.getStance().en == Stance.neutral ? 70 : 100;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String msg;
        if (modifier == Result.miss) {
            msg = "You grope at {other:name-possessive} breasts, but miss. "
                            + "(Maybe you should get closer?)";
        } else if (modifier == Result.strong) {
            msg = "You massage {other:name-possessive} soft breasts and pinch "
                            + "{other:possessive} nipples, causing {other:direct-object}"
                            + " to moan with desire.";
        } else if (modifier == Result.special) {
            msg = "You slip your hands into {other:name-possessive} " 
                            + target.outfit.getTopOfSlot(ClothingSlot.top).getName() + 
                            ", massaging {other:possessive} soft breasts"
                            + " and pinching {other:possessive} nipples.";
        } else {
            msg = "You massage {other:name-possessive} breasts over {other:possessive} "
                            + target.getOutfit().getTopOfSlot(ClothingSlot.top).getName() + ".";
        }
        return Formatter.format(msg, getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s gropes at %s %s, but misses the mark.",
                            getSelf().subject(), target.nameOrPossessivePronoun(),
                            target.body.getRandomBreasts().describe(target));
        } else if (modifier == Result.strong) {
            return String.format("%s massages %s %s, and pinches %s nipples, causing %s to moan with desire.",
                            getSelf().subject(), target.nameOrPossessivePronoun(),
                            target.body.getRandomBreasts().describe(target),
                            target.possessiveAdjective(), target.directObject());
        } else if (modifier == Result.special) {
            return Formatter.format("{self:SUBJECT-ACTION:slip|slips} {self:possessive} agile fingers into {other:name-possessive} bra, massaging and pinching at {other:possessive} nipples.",
                            getSelf(), target);
        } else {
            return String.format("%s massages %s %s over %s %s.",
                            getSelf().subject(), target.nameOrPossessivePronoun(),
                            target.body.getRandomBreasts().describe(target), target.possessiveAdjective(),
                            target.getOutfit().getTopOfSlot(ClothingSlot.top).getName());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Grope your opponents breasts. More effective if she's topless";
    }

    @Override
    public String getLabel(Combat c) {
        return c.getOpponent(getSelf()).body.getBreastsAbove(SizeMod.getMinimumSize("breasts")) != null ? "Fondle Breasts"
                        : "Tease Chest";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}
