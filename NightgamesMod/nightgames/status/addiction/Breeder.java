package nightgames.status.addiction;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.NPC;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.status.Horny;
import nightgames.status.Status;
import nightgames.status.Stsflag;

import java.util.Optional;

public class Breeder extends Addiction {
    public Breeder(Character affected, String cause, float magnitude) {
        super(affected, "Breeder", cause, magnitude);
    }

    public Breeder(Character affected, String cause) {
        this(affected, cause, .01f);
    }

    @Override
    protected Optional<Status> withdrawalEffects() {
        return Optional.of(new Horny(affected, 5.f * getSeverity().ordinal(), 999, affected.possessiveAdjective() + " animal instincts"));
    }

    @Override
    protected Optional<Status> addictionEffects() {
        if (atLeast(Severity.HIGH))
            flag(Stsflag.feral);
        else
            unflag(Stsflag.feral);
        return Optional.of(this);
    }

    @Override
    protected String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                return "All you can think about while fucking " + getCause().getName() + " is filling up "
                                + getCause().directObject() + " pussy with your cum. That desire is seriously undermining"
                                + " your staying power.";
            case LOW:
                return "Whatever " + getCause().getName() + " did to you, you don't seem to be able to last as long as before"
                                + " while fucking " + getCause().directObject() + ".";
            case MED:
                return "Whenever you fuck " + getCause().getName() + " you feel an instinctive need to pound "
                                + getCause().directObject() + " as hard and fast as you can. Of course, that does mean you"
                                + " won't last as long.";
            case NONE:
            default:
                return ""; // hide
        }
    }

    @Override
    protected String describeDecrease() {
        switch (getSeverity()) {
            case LOW:
                return "You still feel a desire to fuck " + getCause().getName() + " silly, but it's no longer a driving force"
                                + " in your mind.";
            case MED:
                return "The need to fill " + getCause().getName() + " with your seed shifts to simply fucking "
                                + getCause().directObject() + " as hard as you can. That won't be enough to last very long,"
                                + " but it's better than before.";
            case NONE:
                return "The animalistic instincts " + getCause().getName() + " imbued in you have fully faded away.";
            case HIGH:
            default:
                return ""; // hide
        }
    }

    @Override
    protected String describeWithdrawal() {
        switch (getSeverity()) {
            case HIGH:
                return "<b>Finding and seeding a willing pussy is foremost in your mind after not"
                                + " fucking " + getCause().getName() + " all day, and you are already hard at the prospect of"
                                + " mending that unfortunate situation.</b>";
            case LOW:
                return "<b>Having not fucked " + getCause().getName() + " all day, you feel a tingle in your balls telling you it's"
                                + " time to do something about that.</b>";
            case MED:
                return "<b>Your instincts are telling you that you haven't fucked enough today, and"
                                + " they are driving up your arousal.</b>";
            case NONE:
                throw new IllegalStateException("Tried to describe withdrawal for an inactive breeder addiction.");
            default:
                return ""; // hide
        }
    }

    @Override
    protected String describeCombatIncrease() {
        return "Your unnatural breeding instincts are slowly but steadily bubbling up in your mind.";
    }

    @Override
    protected String describeCombatDecrease() {
        return "You relish at letting loose inside of " + getCause().getName() + ", subduing the instinctive needs for now.";
    }

    @Override
    public String informantsOverview() {
        return "<i>\"Well... I don't know " + getCause().getName() + " that well, but don't you </i>always<i> want to fuck " + getCause()
                        .directObject() + "?"
                        + " What's so special?\"</i> You explain how the desire is so much stronger than usual,"
                        + " that you just can't get it out of your mind. <i>\"Mmmm... You must be overly sensitive"
                        + " to " + getCause().directObject() + " pheromones. It happens every once in a while, just some genetic bad luck. Or"
                        + " some </i>particularly<i> naughty parents, but let's not go there. Either way, it causes"
                        + " this feedback effect, basically driving you into a rut. The good news is that you'll"
                        + " be able to cum more often, the bad news is that you WILL cum more often when fucking " + getCause()
                        .getName() + "."
                        + " As in all the time. Other contenstants won't be as prepared for your ferocity, though,"
                        + " so you may have an advantage there. If you </i>don't<i> go and fuck someone, well, you"
                        + " might just go crazy. Best not take the risk.\"</i>";
    }

    @Override
    public String describeMorning() {
        return "Of course morning wood is pretty normal, but this morning it just won't go down! No"
                        + " matter what you do, it just gets worse. There must be <i>something</i> you"
                        + " can do. " + getCause().getName() + " is clearly the cause, so maybe " + getCause().pronoun() + " can help?";
    }

    @Override
    public AddictionType getType() {
        return AddictionType.BREEDER;
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        if (inWithdrawal) {
            return "Arousal rages through your body at the sight of " + c.getOpponent(affected).getName() 
                            + ", expecting a well-earned fuck.";
        }
        return "Your instincts howl at the sight of " + getCause().getName() + ", urging you to fuck " + getCause().directObject() + " as soon as possible.";
    }

    @Override
    public String describe(Combat c) {
        switch (getCombatSeverity()) {
            case HIGH:
                return "The animal part of your brain cultivated by " + getCause().getName() + " is screaming for"
                                + " you to sink your cock into " + getCause().directObject() + " and fill "
                                + getCause().directObject() + " with your seed.";
            case LOW:
                return "You feel as if you've had plenty of foreplay. Time to move on.";
            case MED:
                return getCause().getName() + "'s little trick has you all geared up to fuck " + getCause().directObject() + ". You can "
                                + "think of few things you'd like to do more.";
            case NONE:
            default:
                return "";

        }
    }

    @Override
    public int mod(Attribute a) {
        return a == Attribute.Animism ? getSeverity().ordinal() * 2 : 0;
    }

    @Override
    public void tick(Combat c) {
        super.tick(c);
        if (inWithdrawal) {
            affected.arouse(Math.round(magnitude * 5), c, " (Breeding Instincts)");
            affected.emote(Emotion.horny, 20);
        }
    }
    
    @Override
    public int regen(Combat c) {
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
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Breeder(newAffected, newOther.getType(), magnitude);
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new Breeder(NPC.noneCharacter(), obj.get("cause").getAsString(),
                        (float) obj.get("magnitude").getAsInt());
    }
}
