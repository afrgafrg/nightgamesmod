package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.PussyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.global.Grammar;

public class SlimeMimicry extends DurationStatus {
    private final String mimickedName;
    private final PussyPart pussyMimicked;
    private final CockPart.Mod cockModMimicked;

    public SlimeMimicry(String name, PussyPart pussyMimicked, CockPart.Mod cockModMimicked, Character affected, int duration) {
        super("Mimicry: " + Grammar.capitalizeFirstLetter(name), affected, duration);
        this.mimickedName = name;
        this.pussyMimicked = pussyMimicked;
        this.cockModMimicked = cockModMimicked;
        this.flag(Stsflag.mimicry);
        this.flag(Stsflag.form);
    }

    public PussyPart getPussyMimicked() {
        return pussyMimicked;
    }

    public CockPart.Mod getCockModMimicked() {
        return cockModMimicked;
    }

    @Override
    public String initialMessage(Combat c, boolean replaced) {
        return Global.global.format("{self:SUBJECT} started mimicking a %s.", affected, c.getOpponent(affected), mimickedName);
    }

    @Override
    public String describe(Combat c) {
    	return Global.global.format("{self:SUBJECT-ACTION:are|is} mimicking a %s.", affected, c.getOpponent(affected), mimickedName);
    }

    @Override
    public int mod(Attribute a) {
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
    public int weakened(int x) {
        return 0;
    }

    @Override
    public int tempted(int x) {
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
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new SlimeMimicry(getMimickedName(), pussyMimicked, cockModMimicked, newAffected, getDuration());
    }

     @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("mimickedName", getMimickedName());
        obj.addProperty("pussyMimicked", pussyMimicked.name());
        obj.addProperty("cockMimicked", cockModMimicked.name());
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new SlimeMimicry(obj.get("mimickedName").getAsString(),
                        PussyPart.valueOf(obj.get("pussyMimicked").getAsString()),
                        CockPart.Mod.valueOf(obj.get("cockMimicked").getAsString()), null, obj.get("duration").getAsInt());
    }

    public String getMimickedName() {
        return mimickedName;
    }

}
