package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.characters.Trait;
import nightgames.combat.Encounter;
import nightgames.global.Formatter;
import nightgames.global.Match;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingSlot;

public class Spiderweb extends Trap {

    public Spiderweb() {
        this(null);
    }
    
    public Spiderweb(Character owner) {
        super("Spiderweb", owner);
    }

    public void setStrength(Character user) {
        setStrength(user.get(Attribute.Cunning) + user.get(Attribute.Science) + user.getLevel() / 2);
    }

    @Override
    public void trigger(Character target) {
        if (target.human()) {
            if (target.mostlyNude()) {
                GUI.gui.message(
                                "You feel the tripwire underfoot too late to avoid it. A staggering amount of rope flies up to entangle your limbs and pull you off the ground. "
                                                + "Oh hell. You're completely immobilized and suspended in midair. Surprisingly, it's not that uncomfortable, but if someone finds you before you can get free, "
                                                + "you'll be completely defenseless.");
            } else {
                GUI.gui.message(
                                "You feel the tripwire underfoot too late to avoid it. A staggering amount of rope flies up to entangle your limbs and pull you off the ground. "
                                                + "Something snags your clothes and pulls them off of you with unbelievable precision."
                                                + "Oh hell. You're completely immobilized and suspended naked in midair. Surprisingly, it's not that uncomfortable, but if someone finds you before you can get free, "
                                                + "you'll be completely defenseless.");
            }
        } else if (target.location().humanPresent()) {
            GUI.gui.message("You hear a snap as " + target.getName()
                            + " triggers your spiderweb trap and ends up helplessly suspended in midair like a naked present.");
        }
        target.state = State.webbed;
        target.delay(1);
        target.location().opportunity(target, this);
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.Rope, 4) && owner.has(Item.Spring, 2) && owner.has(Item.Tripwire);
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.has(Trait.spider) && !owner.has(Trait.roboweb);
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner;
        owner.consume(Item.Tripwire, 1);
        owner.consume(Item.Rope, 4);
        owner.consume(Item.Spring, 2);
        return "With quite a bit of time and effort, you carefully setup a complex series of spring loaded snares. Anyone who gets caught in this will be rendered as helpless "
                        + "as a fly in a web.";
    }

    @Override
    public void capitalize(Character attacker, Character victim, Encounter enc) {
        if (attacker.human()) {
            GUI.gui.message(Formatter.format(" is naked and helpless in the giant rope web. You approach slowly, taking in the lovely view of {other:possessive} body. You trail your fingers "
                                            + "down {other:possessive} front, settling between {other:possessive} legs to tease {other:possessive} sensitive pussy lips. "
                                            + "{self:PRONOUN} moans and squirms, but is completely unable to do anything in {other:possessive} own defense. "
                                            + "You are going to make {other:possessive} cum, that's just a given. If you weren't such a nice guy,"
                                            + " you would leave {other:direct-object} in that trap afterward to be everyone else's prey "
                                            + "instead of helping {other:direct-object} down. You kiss and lick {other:possessive} neck, turning {other:direct-object} on further. "
                                            + "{other:POSSESSIVE} entrance is wet enough that you can easily work two fingers into {other:direct-object} "
                                            + "and begin pumping. You gradually lick your way down {other:possessive} body, lingering at {other:possessive} "
                                            + "nipples and bellybutton, until you find yourself eye level with {other:possessive} groin. "
                                            + "You can see {other:possessive} clitoris, swollen with arousal, practically begging to be touched. "
                                            + "You trap the sensitive bud between your lips and attack it with your tongue. "
                                            + "The intense stimulation, coupled with your fingers inside {other:direct-object}, quickly brings {other:direct-object} to orgasm. "
                                            + "While {other:pronoun}'s trying to regain {other:possessive} strength, you untie the ropes "
                                            + "binding {other:possessive} hands and feet and ease {other:direct-object} out of the web.", attacker, victim));
        } else if (victim.human()) {
            GUI.gui.message(Formatter.format("You're trying to figure out a way to free yourself, when you see {self:name-do} approach."
                            + " You groan in resignation. There's no way you're going to get free before "
                            + "{self:pronoun} finishes you off. {self:PRONOUN} smiles as {self:pronoun} enjoys"
                            + " your vulnerable state. {self:PRONOUN} grabs your dangling penis and puts it in"
                            + " {self:possessive} mouth, licking and sucking it until it's completely hard. "
                            + "Then the teasing starts. {self:PRONOUN} strokes you, rubs you, and licks the "
                            + "head of your dick. {self:PRONOUN} uses every technique to pleasure you, but "
                            + "stops just short of letting you ejaculate. It's maddening. Finally you have to "
                            + "swallow your pride and beg to cum. {self:PRONOUN} pumps you dick in earnest "
                            + "now and fondles your balls. When you cum, you shoot your load onto {self:possessive}"
                            + " face and chest. You hang in the rope web, literally and figuratively drained. "
                            + "{self:PRONOUN} graciously unties you and helps you down.", attacker, victim));
        }
        if (victim.getOutfit().getBottomOfSlot(ClothingSlot.bottom) != null) {
            attacker.gain(victim.getTrophy());
        }
        victim.nudify();
        victim.defeated(attacker);
        victim.getArousal().empty();
        attacker.tempt(20);
        Match.getMatch().score(attacker, victim.has(Trait.event) ? 5 : 1);
        attacker.state = State.ready;
        victim.state = State.ready;
        victim.location().endEncounter();
        victim.location().remove(this);
    }

}
