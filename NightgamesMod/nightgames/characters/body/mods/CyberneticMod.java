package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;
import nightgames.global.Random;
import nightgames.status.Shamed;

public class CyberneticMod extends PartMod {
    public static final CyberneticMod INSTANCE = new CyberneticMod();

    public CyberneticMod() {
        super("cybernetic", -.1, .8, -.5, 0);
    }


    public String adjective(GenericBodyPart part) {
        if (part.getType().equals("pussy")) {
            return "cybernetic";
        }
        if (part.getType().equals("ass")) {
            return "biomech";
        }
        return "prosthetic";
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        int bonus = 0;

        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part)) {
            if (target.moddedPartCountsAs(opponent, CockMod.enlightened)) {
                c.write(self, String.format(
                                "Despite %s %s's best efforts, %s focus does not waver, and %s barely %s a thing.",
                                self.nameOrPossessivePronoun(), part.describe(self), opponent.nameOrPossessivePronoun(),
                                opponent.pronoun(), opponent.human() ? "feel" : "feels"));
                bonus -= 5;
            } else {
                if (Random.random(3) == 0 || target.moddedPartCountsAs(opponent, CockMod.incubus)) {
                    String prefix = target.moddedPartCountsAs(opponent, CockMod.incubus) ? "Eager to gain a sample of "
                                    + opponent.nameOrPossessivePronoun() + " exotic, demonic sperm, " : "";
                    c.write(self, String.format(
                                    prefix + "%s %s whirls to life and starts attempting to extract all the semen packed inside %s %s. "
                                                    + "At the same time, %s feel a thin filament sliding into %s urethra, filling %s with both pleasure and shame.",
                                    self.possessiveAdjective(), part.describe(self), opponent.possessiveAdjective(),
                                    target.describe(opponent), opponent.pronoun(), opponent.possessiveAdjective(),
                                    opponent.directObject()));
                    bonus += 15;
                    if (target.moddedPartCountsAs(opponent, CockMod.incubus) || Random.random(4) == 0) {
                        opponent.add(c, new Shamed(opponent));
                    }
                }
            }
        }
        return bonus;
    }

    public int counterValue(BodyPart part, BodyPart otherPart, Character self, Character other) { 
        return otherPart.moddedPartCountsAs(other, CockMod.incubus) ? 1 : otherPart.moddedPartCountsAs(other, CockMod.enlightened) ? -1 : 0;
    }

    @Override
    public String describeAdjective(String partType) {
        return "cybernetics";
    }
}
