package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Stsflag;

public class OrgasmSeal extends Skill {

    public OrgasmSeal(Character self) {
        super("Orgasm Seal", self, 4);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Arcane) >= 15 || user.get(Attribute.Dark) >= 5;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && !target.is(Stsflag.orgasmseal);
    }

    @Override
    public int getMojoCost(Combat c) {
        return 20;
    }

    @Override
    public String describe(Combat c) {
        return "Prevents your opponent from cumming with a mystical seal";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        writeOutput(c, Result.normal, target);
        target.add(c, new nightgames.status.OrgasmSeal(target, 15));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new OrgasmSeal(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return Formatter.format("You focus your energy onto {other:name-possessive}"
                        + " abdomen, coalescing it into a blood red mark that"
                        + " prevents {other:direct-object} from cumming.", getSelf(), target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return String.format("%s makes a complicated gesture and envelopes %s finger tips in a blood red glow. "
                        + "With a nasty grin, %s jams %s finger into %s %s. Strangely it doesn't hurt at all, but"
                        + " when %s withdraws %s finger, %s leaves a glowing pentagram on %s.",
                        getSelf().subject(), getSelf().possessiveAdjective(), getSelf().pronoun(),
                        getSelf().possessiveAdjective(), target.nameOrPossessivePronoun(),
                        (target.hasBalls() ? "balls" : "lower abdomen"),
                        getSelf().pronoun(), target.possessiveAdjective(), getSelf().subject(),
                        target.nameDirectObject());
    }
}
