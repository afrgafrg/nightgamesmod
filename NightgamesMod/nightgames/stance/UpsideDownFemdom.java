package nightgames.stance;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class UpsideDownFemdom extends FemdomSexStance {
    public UpsideDownFemdom(Character top, Character bottom) {
        super(top, bottom, Stance.upsidedownfemdom);
    }

    @Override
    public int pinDifficulty(Combat c, Character self) {
        return 8;
    }

    @Override
    public String describe(Combat c) {
        if (top.human()) {
            return "You are holding " + bottom.getName()
                            + " upsidedown by her legs while fucking her cock with your slit.";
        } else {
            return String.format("%s is holding %s upsidedown by %s legs while fucking %s cock with %s slit.",
                            top.subject(), bottom.nameDirectObject(), bottom.possessiveAdjective(),
                            bottom.possessiveAdjective(), top.possessiveAdjective());
        }
    }

    @Override
    public String image() {
        return "upsidedownfemdom.jpg";
    }

    @Override
    public boolean mobile(Character c) {
        return c != bottom;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c != bottom;
    }

    @Override
    public boolean dom(Character c) {
        return c == top;
    }

    @Override
    public boolean sub(Character c) {
        return c == bottom;
    }

    @Override
    public boolean reachTop(Character c) {
        return c != top && c != bottom;
    }

    @Override
    public boolean facing(Character c, Character target) {
        return (c != bottom && c != top) || (target != bottom && target != top);
    }

    @Override
    public boolean reachBottom(Character c) {
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return c == bottom;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public Position insertRandom(Combat c) {
        return new StandingOver(top, bottom);
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            if (bottom.human()) {
                c.write(bottom, Formatter.format(
                                "Summoning your remaining strength, you hold your arms up against the floor and use your hips to tip {other:name-do} off-balance with self dick still held inside of {other:possessive}. "
                                                + "{other:SUBJECT} lands on the floor with you on top of {other:direct-object} in a missionary position.",
                                bottom, top));
            } else {
                c.write(bottom, Formatter.format(
                                "{self:SUBJECT} suddenly pushes against the floor and knocks {other:name-do} to the ground with {self:possessive} hips. "
                                                + "{other:PRONOUN-ACTION:land} on the floor with {self:name-do} on top of"
                                                + " {other:direct-object}, fucking {other:direct-object} in a missionary position.",
                                bottom, top));
            }
        }
        return new UpsideDownMaledom(bottom, top);
    }

    @Override
    public double pheromoneMod(Character self) {
        return 2;
    }
    
    @Override
    public int dominance() {
        return 4;
    }
}
