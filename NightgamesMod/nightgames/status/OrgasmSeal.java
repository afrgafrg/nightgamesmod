package nightgames.status;

import java.util.Optional;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

public class OrgasmSeal extends DurationStatus {
    public OrgasmSeal(Character affected, int duration) {
        super("Orgasm Sealed", affected, duration);
        flag(Stsflag.orgasmseal);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        return String.format("%s ability to cum is now sealed!\n", affected.nameOrPossessivePronoun());
    }

    @Override
    public String describe(Combat c) {
        if (affected.hasBalls()) {
            return Formatter.format("A pentragram on {self:name-possessive} ballsack glows with a sinister light.",
                            affected, affected);
        } else {
            return Formatter.format("A pentragram on {self:name-possessive} lower belly glows with a sinister light.",
                            affected, affected);
        }
    }

    @Override
    public float fitnessModifier() {
        if (affected.getArousal().percent() > 80) {
            return -10;
        }
        return 0;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
        super.regen(c);
        if (affected.getArousal().isFull()) {
            tick(4);
        }
        if (affected.getArousal().percent() > 80) {
            affected.emote(Emotion.desperate, 10);
            affected.emote(Emotion.horny, 10);
        }
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return 0;
    }

    @Override
    public int escape() {
        return 0;
    }

    @Override
    public int gainmojo(int x) {
        return 0;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return 0;
    }

    @Override
    public String toString() {
        return "Orgasm Sealed";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new OrgasmSeal(newAffected, getDuration());
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new OrgasmSeal(null, obj.get("duration").getAsInt());
    }
}
