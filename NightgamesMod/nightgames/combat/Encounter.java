package nightgames.combat;

import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.characters.*;
import nightgames.characters.Character;
import nightgames.global.*;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.status.*;
import nightgames.trap.Spiderweb;
import nightgames.trap.Trap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static nightgames.requirements.RequirementShortcuts.item;

/**
 * An Encounter is a meeting between two or more characters in one area during a match.
 */
public class Encounter implements Serializable {

    private static final long serialVersionUID = 3122246133619156539L;

    private List<Character> participants;
    protected Area location;
    protected transient Combat fight;
    private CountDownLatch waitForFinish;
    private List<Character> faster;

    // TODO: Figure out what to do with encounters involving more than three characters.
    public Encounter(Area location) {
        this.location = location;
        participants = new ArrayList<>(location.present);
        assert participants.size() >= 2;
        fight = null;
        checkEnthrall(getP1(), getP2());
        checkEnthrall(getP2(), getP1());
        waitForFinish = new CountDownLatch(1);
        faster = faster(participants);
    }

    public Character getP1() {
        return participants.get(0);
    }

    public Character getP2() {
        return participants.get(1);
    }

    private boolean observed() {
        return participants.stream().anyMatch(Character::human);
    }

    private void messageIfObserved(String message) {
        messageIfObserved(message, GUI.gui);
    }

    private void messageIfObserved(String message, GUI gui) {
        if (observed()) {
            gui.message(message);
        }
    }

    private Optional<Character> getIntervener() {
        if (participants.size() > 2) {
            return Optional.of(participants.get(2));
        }
        return Optional.empty();
    }

    public List<Character> getExtras() {
        List<Character> extras = new ArrayList<>();
        if (participants.size() > 3) {
            extras.addAll(participants.subList(3, participants.size()));
        }
        return extras;
    }

    public void intervene(Character intervener) {
        assert participants.size() == 2;
        participants.add(intervener);
    }

    public enum Initiation {
        ambushStrip,
        ambushRegular
    }

    protected void checkEnthrall(Character p1, Character p2) {
        Status enthrall = p1.getStatus(Stsflag.enthralled);
        if (enthrall != null) {
            if (((Enthralled) enthrall).master != p2) {
                p1.removelist.add(enthrall);
                p1.addNonCombat(new Flatfooted(p1, 2));
                p1.addNonCombat(new Hypersensitive(p1));
                if (p1.human()) {
                    messageIfObserved("At " + p2.getName() + "'s interruption, you break free from the"
                                    + " succubus' hold on your mind. However, the shock all but"
                                    + " short-circuits your brain; you "
                                    + " collapse to the floor, feeling helpless and"
                                    + " strangely oversensitive");
                } else if (p2.human()) {
                    messageIfObserved(Formatter.format("{self:subject} doesn't appear to notice you at first, "
                                    + "but when you wave your hand close to {self:possessive} face {self:possessive}"
                                    + " eyes open wide and {self:pronoun} immediately drops to the floor. Although "
                                    + "the display leaves you somewhat worried about {self:possessive} health, "
                                    + "{self:pronoun} is still in a very vulnerable position and you never were"
                                    + " one to let an opportunity pass you by.", p1, p2));
                }
            }
        }
    }

    /**
     * Prompts characters for responses to the encounter, depending on the state of other present characters. Creates an active Combat if applicable.
     */
    protected void spotCheck() {
        final Character p1 = getP1();
        final Character p2 = getP2();
        // If both players are eligible, first check for various one-sided encounters. Second, see who's observant enough
        // to spot the other. If both spot each other, both face off (decide fight or flight). If only one spots the other,
        // the other starts flat-footed. If neither spot each other, they move on, no one the wiser.
        if (p1.eligible(p2) && p2.eligible(p1)) {
            Encs encounterType;
            Character attacker;
            Character target;
            if (p2.isVulnerable()) {
                attacker = p1;
                target = p2;
                encounterType = vulnerable(attacker, target);
            } else if (p1.isVulnerable()) {
                attacker = p2;
                target = p1;
                encounterType = vulnerable(attacker, target);
            } else {
                boolean p1SpotCheck = p1.spotCheck(p2);
                boolean p2SpotCheck = p2.spotCheck(p1);
                if (p1SpotCheck && p2SpotCheck) {
                    Character.FightIntent p1Intent = p1.faceOff(p2, this);
                    Character.FightIntent p2Intent = p2.faceOff(p1, this);

                    if (p1Intent == Character.FightIntent.smoke) {
                        attacker = p2;
                        target = p1;
                        encounterType = Encs.smoke;
                    } else if (p2Intent == Character.FightIntent.smoke) {
                        attacker = p1;
                        target = p2;
                        encounterType = Encs.smoke;
                    } else if (p1Intent == Character.FightIntent.flee
                                    && p2Intent == Character.FightIntent.flee) {
                        encounterType = Encs.bothflee;
                        attacker = faster.get(0);
                        target = faster.get(1);
                    } else if (p1Intent == Character.FightIntent.fight && p2Intent == Character.FightIntent.flee) {
                        encounterType = Encs.flee;
                        attacker = p1;
                        target = p2;
                    } else if (p1Intent == Character.FightIntent.flee
                                    && p2Intent == Character.FightIntent.fight) {
                        encounterType = Encs.flee;
                        attacker = p2;
                        target = p1;
                    } else {
                        encounterType = Encs.fight;
                        attacker = faster.get(0);
                        target = faster.get(1);
                    }
                } else if (p1SpotCheck) {
                    attacker = p1;
                    target = p2;
                    encounterType = attacker.spy(target, this);
                } else if (p2SpotCheck) {
                    attacker = p2;
                    target = p1;
                    encounterType = attacker.spy(target, this);
                } else {
                    attacker = faster.get(0);
                    target = faster.get(1);
                    encounterType = Encs.missed;
                }
            }
            parse(encounterType, attacker, target);
        } else {
            if (p1.state == State.masturbating) {
                if (p1.human()) {
                    messageIfObserved(Formatter.format(
                                    "{other:subject} catches you masturbating, but fortunately {other:pronoun}'s "
                                    + "still not allowed to attack you, so {other:pronoun}"
                                    + " just watches you jerk off with "
                                    + "an amused grin.", p1, p2));
                } else if (p2.human()) {
                    messageIfObserved(Formatter.format("You stumble onto {self:name-do} with "
                                    + "{self:possessive} hand between {self:possessive} legs, "
                                    + "masturbating. Since you just fought, you still can't "
                                    + "touch {self:direct-object}, so you just watch the"
                                    + " show until {self:pronoun} orgasms.", p1, p2));
                }
            } else if (p2.state == State.masturbating) {
                if (p2.human()) {
                    messageIfObserved(Formatter.format(p1.getName()
                                    + "{self:subject} catches you masturbating, but fortunately "
                                    + "{self:pronoun}'s still not allowed to attack you, so "
                                    + "{self:pronoun} just watches you jerk off with "
                                    + "an amused grin.", p1, p2));
                } else if (p1.human()) {
                    messageIfObserved(Formatter.format("You stumble onto {other:subject} with "
                                    + "{other:possessive} hand between {other:possessive} "
                                    + "legs, masturbating. Since you just fought, you still "
                                    + "can't touch {other:direct-object}, so you just watch "
                                    + "the show until {other:pronoun} orgasms.", p1, p2));
                }
            } else if (!p1.eligible(p2) && p1.human()) {
                messageIfObserved(Formatter.format("You encounter {other:name-do}, but you "
                                + "still haven't recovered from your last fight.", p1, p2));
            } else if (p1.human()) {
                messageIfObserved(Formatter.format("You find {other:name-do} still naked "
                                + "from your last encounter, but {other:pronoun}'s not fair "
                                + "game again until {other:pronoun} replaces"
                                + " {other:pronoun} clothes.", p1, p2));
            }
            location.endEncounter();
        }
    }

    /**
     * Determines which of two characters is faster. Ties broken by base speed, then by whoever ended up being p1.
     *
     * @return a list of the two characters sorted by speed.
     */
    private List<Character> faster(Character p1, Character p2) {
        Random.DieRoll p1Check = p1.check(Attribute.Speed, p1.getTraitMod(Trait.sprinter, 5));
        Random.DieRoll p2Check = p2.check(Attribute.Speed, p2.getTraitMod(Trait.sprinter, 5));

        if (p1Check.result() == p2Check.result()) {
            if (p1.get(Attribute.Speed) >= p2.get(Attribute.Speed)) {
                return Arrays.asList(p1, p2);
            } else {
                return Arrays.asList(p2, p1);
            }
        } else if (p1Check.result() > p2Check.result()) {
            return Arrays.asList(p1, p2);
        } else {
            return Arrays.asList(p2, p1);
        }
    }

    private List<Character> faster(List<Character> characters) {
        return faster(characters.get(0), characters.get(1));
    }

    private Encs vulnerable(Character attacker, Character target) {
        if (target.state == State.shower) {
            return attacker.showerSceneResponse(target, this);
        } else if (target.state == State.webbed) {
            return Encs.spidertrap;
        } else if (target.state == State.crafting || target.state == State.searching) {
            return attacker.spy(target, this);
        } else if (target.state == State.masturbating) {
            return Encs.caughtmasturbating;
        }
        throw new RuntimeException("Invalid vulnerable encounter type");
    }

    protected void smokeFlee(Character runner) {
        GUI.gui.message(String.format("%s a smoke bomb and %s.",
                        Formatter.capitalizeFirstLetter(runner.subjectAction("drop", "drops")),
                        runner.action("disappear", "disappears")));
        runner.consume(Item.SmokeBomb, 1);
        runner.flee(this.location);
    }

    protected void fleeHidden(Character attacker, Character runner) {
        if (attacker.human() || runner.human())
        GUI.gui.message(Formatter
                        .format("{self:SUBJECT-ACTION:flee} before {other:subject-action:can} notice {self:direct-object}.",
                                        runner, attacker));
        runner.flee(this.location);
    }

    protected void fleeAttempt(Character attacker, Character runner) {
        if (this.faster.get(0).equals(attacker)) {
            if (attacker.human()) {
                GUI.gui.message(Formatter.format("{other:subject} tries to run, but you stay right"
                                + " on {other:possessive} heels and catch {other:direct-object}.",
                                attacker, runner));
            } else if (runner.human()) {
                GUI.gui.message(Formatter.format("You quickly try to escape, but {self:subject} is "
                                + "quicker. {self:PRONOUN} corners you and attacks.",
                                attacker, runner));
            }
            this.fight = new Combat(attacker, runner, this.location);
        } else {
            if (attacker.human()) {
                GUI.gui.message(runner.getName() + " dashes away before you can move.");
            } else if (runner.human()) {
                GUI.gui.message("You dash away before " + attacker.getName() + " can move.");
            }
            runner.flee(this.location);
        }
    }

    protected void ambush(Character attacker, Character target) {
        target.addNonCombat(new Flatfooted(target, 3));
        if (attacker.human() || target.human()) {
            GUI.gui.message(Formatter.format("{self:SUBJECT-ACTION:catch|catches} {other:name-do} by surprise and {self:action:attack|attacks}!", attacker, target));
        }
        fight = new Combat(attacker, target, location, Initiation.ambushRegular);
    }

    protected void showerambush(Character attacker, Character target) {
        if (target.human()) {
            if (location.id() == Movement.shower) {
                                messageIfObserved("You aren't in the shower long before you realize you're not alone. Before you can turn around, a soft hand grabs your exposed penis. "
                                                + attacker.getName() + " has the drop on you.");
            } else if (location.id() == Movement.pool) {
                                messageIfObserved(Formatter.format("The relaxing water causes you to lower your guard "
                                                + "a bit, so you don't notice "
                                                + "{self:name-do} until {self:pronoun}'s standing over you. There's "
                                                + "no chance to escape, you'll have to face {self:direct-object} nude.",
                                                attacker, target));
            }
        } else if (attacker.human()) {
            if (location.id() == Movement.shower) {
                                messageIfObserved(Formatter.format("You stealthily walk up behind {other:name-do}, "
                                                + "enjoying the view of {other:possessive} wet naked body. When you "
                                                + "stroke {other:possessive} smooth butt, {other:pronoun} jumps "
                                                + "and lets out a surprised yelp. Before {other:pronoun} can "
                                                + "recover from {other:possessive} surprise, you pounce!",
                                                attacker, target));
            } else if (location.id() == Movement.pool) {
                                messageIfObserved(Formatter.format("You creep up to the jacuzzi where {other:name-do} is"
                                                + " soaking comfortably. As you get close, you notice that {other:possessive} "
                                                + "eyes are closed and {other:pronoun} may well be sleeping. You crouch by "
                                                + "the edge of the jacuzzi for a few seconds and just admire {other:possessive}"
                                                + " nude body with {other:possessive} breasts just above the surface. You lean "
                                                + "down and give {other:direct-object} a light kiss on the forehead to wake "
                                                + "{other:direct-object} up. {other:PRONOUN} opens {other:possessive} eyes and "
                                                + "swears under {other:possessive} breath when {other:pronoun} sees you. "
                                                + "{other:PRONOUN} scrambles out of the tub, but you easily catch "
                                                + "{other:direct-object} before {other:pronoun} can get away.",
                                                attacker, target));
            }
        }
        fight = new Combat(attacker, target, location, Initiation.ambushStrip);
    }

    protected void aphrodisiactrick(Character attacker, Character target) {
        attacker.consume(Item.Aphrodisiac, 1);
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (target.human()) {
            if (location.id() == Movement.shower) {
                                messageIfObserved(Formatter.format("The hot shower takes your fatigue away, "
                                                + "but you can't seem to calm down. Your cock is almost "
                                                + "painfully hard. You need to deal with this while "
                                                + "you have the chance. You jerk off quickly, hoping to "
                                                + "finish before someone stumbles onto you. Right before you"
                                                + " cum, you are suddenly grabbed from behind and "
                                                + "spun around. {self:subject} has caught you at your most "
                                                + "vulnerable and, based on {self:possessive} expression, "
                                                + "may have been waiting for this moment. {self:PRONOUN} "
                                                + "kisses you and firmly grasps your twitching dick. In "
                                                + "just a few strokes, you cum so hard it's"
                                                + " almost painful.\n", attacker, target));
            } else if (location.id() == Movement.pool) {
                                messageIfObserved(Formatter.format("As you relax in the jacuzzi, you start"
                                                + " to feel extremely horny. Your cock is in your hand "
                                                + "before you're even aware of it. You stroke yourself "
                                                + "off underwater and you're just about ready to cum "
                                                + "when you hear nearby footsteps. Oh shit, you'd "
                                                + "almost completely forgotten you were in the middle of a "
                                                + "match. The footsteps are from {self:name-do}, who sits "
                                                + "down at the edge of the jacuzzi while smiling confidently."
                                                + " You look for a way to escape, but it's hopeless. You "
                                                + "were so close to finishing you just need to cum now. "
                                                + "{self:subject} seems to be thinking the same thing, as "
                                                + "{self:pronoun} dips {self:possessive} bare feet into the "
                                                + "water and grasps your penis between them. {self:PRONOUN}"
                                                + " pumps you with {self:possessive} feet and you shoot "
                                                + "your load into the water in seconds.\n", attacker, target));
            }
        } else if (attacker.human()) {
            if (location.id() == Movement.shower) {
                                messageIfObserved(Formatter.format("You empty the bottle of aphrodisiac onto"
                                                + " the shower floor, letting the heat from the shower turn"
                                                + " it to steam. You watch {other:name-do} and wait "
                                                + "for a reaction. Just when you start to worry that it was"
                                                + " all washed down the drain, you see {other:possessive} "
                                                + "hand slip between {other:possessive} legs. {other:POSSESSIVE}"
                                                + " fingers go to work pleasuring herself and soon "
                                                + "{other:pronoun}'s completely engrossed in {other:possessive}"
                                                + " masturbation, allowing you to safely get closer without"
                                                + " being noticed. {other:PRONOUN}'s completely unreserved,"
                                                + " assuming {other:pronoun}'s alone and you feel a voyeuristic "
                                                + "thrill at the show. You can't just remain an observer though. "
                                                + "For this to count as a victory, you need to be in physical "
                                                + "contact with {other:possessive} when {other:pronoun} orgasms."
                                                + " When you judge that {other:pronoun}'s in the home stretch, "
                                                + "you embrace {other:direct-object} from behind and kiss "
                                                + "{other:possessive} neck. {other:PRONOUN} freezes in "
                                                + "surprise and you move your hand between {other:possessive}"
                                                + " legs to replace {other:direct-object} own. {other:POSSESSIVE}"
                                                + " pussy is hot, wet, and trembling with need. "
                                                + "You stick two fingers into {other:direct-object} and rub "
                                                + "{other:possessive} clit with your thumb. {other:PRONOUN} "
                                                + "climaxes almost immediately. You give {other:direct-object}"
                                                + " a kiss on the cheek and leave while {other:pronoun}'s still"
                                                + " too dazed to realize what happened. You're feeling "
                                                + "pretty horny, but after a show like that "
                                                + "it's hardly surprising.\n", attacker, target));
            } else if (location.id() == Movement.pool) {
                                messageIfObserved(Formatter.format("You sneak up to the jacuzzi, and empty the aphrodisiac"
                                                + " into the water without {other:name-do} noticing. You slip away and "
                                                + "find a hiding spot. In a couple minutes, you notice {other:direct-object}"
                                                + " stir. {other:PRONOUN} glances around, but fails to see you and then "
                                                + "closes {other:possessive} eyes and relaxes again. There's something "
                                                + "different now though and {other:possessive} soft moan confirms it. You "
                                                + "grin and quietly approach again. You can see {other:possessive} hand "
                                                + "moving under the surface of the water as {other:pronoun} enjoys herself"
                                                + " tremendously. {other:POSSESSIVE} moans rise in volume and frequency. "
                                                + "Now's the right moment. You lean down and kiss {other:direct-object} on"
                                                + " the lips. {other:POSSESSIVE} masturbation stops immediately, but you "
                                                + "reach underwater and finger {other:direct-object} to orgasm. When "
                                                + "{other:pronoun} recovers, {other:pronoun} glares at you for your "
                                                + "unsportsmanlike trick, but {other:pronoun} can't manage to get "
                                                + "really mad in the afterglow of {other:possessive} climax. You're "
                                                + "pretty turned on by the encounter, but you can chalk"
                                                + " this up as a win.\n", attacker, target));
            }
        }
        if (!target.mostlyNude()) {
            attacker.gain(target.getTrophy());
        }
        target.nudify();
        target.defeated(attacker);
        target.getArousal()
                        .empty();
        attacker.tempt(20);
        Match.getMatch()
                        .score(attacker, target.has(Trait.event) ? 5 : 1);
        attacker.state = State.ready;
        target.state = State.ready;
        location.endEncounter();
    }

    // TODO: Update messages to use formatter and provide alternate-gender options.
    protected void caught(Character attacker, Character target) {
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (target.human()) {
            messageIfObserved(Formatter.format("You jerk off frantically, trying to finish as fast "
                            + "as possible. Just as you feel the familiar sensation of imminent orgasm,"
                            + " you're grabbed from behind. "
                            + "You freeze, cock still in hand. As you turn your head to look at"
                            + " your attacker, {self:subject} kisses you on the lips and rubs the head"
                            + " of your penis with {self:possessive} palm. You were so close to the"
                            + " edge that just you cum instantly.",
                            attacker, target));
            if (!target.mostlyNude()) {
                messageIfObserved(Formatter.format("You groan in resignation and reluctantly strip off "
                                + "your clothes and hand them over.", attacker, target));
            }
        } else if (attacker.human()) {
            messageIfObserved(Formatter.format("You spot {other:name-do}"
                            + " leaning against the wall with {other:possessive} hand working excitedly between "
                            + "{other:possessive} legs. {other:PRONOUN} is mostly, but not completely successful at "
                            + "stifling {other:possessive} moans. {other:PRONOUN} hasn't noticed you yet, and"
                            + " as best as you can judge, {other:pronoun}'s pretty close to the "
                            + "end. It'll be an easy victory for you as long as you work fast. "
                            + "You sneak up and hug {other:direct-object} from behind while kissing the nape of "
                            + "{other:possessive} neck. {other:PRONOUN} moans and shudders in your arms, "
                            + "but doesn't stop fingering herself. {other:PRONOUN} probably realizes "
                            + "{other:pronoun} has no chance of winning even if {other:pronoun} fights back. You help "
                            + "{other:direct-object} along by licking {other:possessive} neck and "
                            + "fondling {other:possessive} breasts as {other:pronoun}"
                            + " hits {other:possessive} climax.", attacker, target));
        }
        if (!target.mostlyNude()) {
            attacker.gain(target.getTrophy());
        }
        target.nudify();
        target.defeated(attacker);
        target.getArousal()
                        .empty();
        attacker.tempt(20);
        Match.getMatch()
                        .score(attacker, target.has(Trait.event) ? 5 : 1);
        attacker.state = State.ready;
        target.state = State.ready;
        location.endEncounter();
    }

    protected void spider(Character attacker, Character target) {
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (attacker.human()) {
                            messageIfObserved(Formatter.format(
                                            "{other:subject} is naked and helpless in the giant rope web. You approach slowly, "
                                            + "taking in the lovely view of {other:possessive} body. You trail your fingers "
                                            + "down {other:possessive} front, settling between {other:possessive} legs to "
                                            + "tease {other:possessive} sensitive pussy lips. {other:PRONOUN} moans and"
                                            + " squirms, but is completely unable to do anything in {other:possessive}"
                                            + " own defense. You are going to make {other:possessive} cum, that's just"
                                            + " a given. If you weren't such a nice guy, you would leave {other:direct-object}"
                                            + " in that trap afterward to be everyone else's prey instead of helping "
                                            + "{other:direct-object} down. You kiss and lick {other:possessive} neck, turning "
                                            + "{other:direct-object} on further. {other:POSSESSIVE} entrance is wet enough "
                                            + "that you can easily work two fingers into {other:direct-object} and begin pumping."
                                            + " You gradually lick your way down {other:possessive} body, lingering at "
                                            + "{other:possessive} nipples and bellybutton, until you find yourself eye "
                                            + "level with {other:possessive} groin. You can see {other:possessive} clitoris, "
                                            + "swollen with arousal, practically begging to be touched. You trap the "
                                            + "sensitive bud between your lips and attack it with your tongue. The intense "
                                            + "stimulation, coupled with your fingers inside {other:direct-object}, quickly "
                                            + "brings {other:direct-object} to orgasm. While {other:pronoun}'s trying to "
                                            + "regain {other:possessive} strength, you untie the ropes binding "
                                            + "{other:possessive} hands and feet and ease {other:direct-object} out of the web.",
                                            attacker, target));
        } else if (target.human()) {
                            messageIfObserved(Formatter.format("You're trying to figure out a way to free yourself, when you see " 
                                            + "{self:name-do} approach. You groan in resignation. There's no way you're "
                                            + "going to get free before {self:pronoun} finishes you off. {self:PRONOUN} smiles "
                                            + "as {self:pronoun} enjoys your vulnerable state. {self:PRONOUN} grabs your "
                                            + "dangling penis and puts it in {self:possessive} mouth, licking and sucking it "
                                            + "until it's completely hard. Then the teasing starts. {self:PRONOUN} strokes you, "
                                            + "rubs you, and licks the head of your dick. {self:PRONOUN} uses every technique to "
                                            + "pleasure you, but stops just short of letting you ejaculate. It's maddening. "
                                            + "Finally you have to swallow your pride and beg to cum. {self:PRONOUN} pumps you "
                                            + "dick in earnest now and fondles your balls. When you cum, you shoot your load onto "
                                            + "{self:possessive} face and chest. You hang in the rope web, literally and "
                                            + "figuratively drained. {self:subject} graciously unties you and helps you down.",
                                            attacker, target));
        }
        if (!target.mostlyNude()) {
            attacker.gain(target.getTrophy());
        }
        target.nudify();
        target.defeated(attacker);
        target.getArousal()
                        .empty();
        attacker.tempt(20);
        Match.getMatch()
                        .score(attacker, target.has(Trait.event) ? 5 : 1);
        attacker.state = State.ready;
        target.state = State.ready;
        location.endEncounter();
        location.remove(location.get(Spiderweb.class));
    }

    public void intrude(Character intruder, Character assist) {
        participants.add(intruder);
        fight.intervene(intruder, assist);
    }

    /**
     * NPC combat lasts for a few turns before resolving.
     */
    public void battle() {
        // Handled by combat's delayCounter during match loop combat phase.
    }

    public Optional<Combat> getCombat() {
        return Optional.ofNullable(fight);
    }

    public Character getPlayer(int i) {
        if (i == 1) {
            return getP1();
        } else {
            return getP2();
        }
    }

    protected void steal(Character thief, Character target) {
        if (thief.human()) {
            messageIfObserved(Formatter.format("You quietly swipe {other:name-possessive} clothes"
                            + " while {other:pronoun}'s occupied. It's a little underhanded, but"
                            + " you can still turn them in for cash just as if you "
                            + "defeated {other:direct-object}.", thief, target));
        }
        thief.gain(target.getTrophy());
        target.nudify();
        target.state = State.lostclothes;
        location.endEncounter();
    }

    public void trap(Character opportunist, Character target, Trap trap) {
        if (opportunist.human()) {
            messageIfObserved(Formatter.format("You leap out of cover and catch {other:name-do} "
                            + "by surprise.", opportunist, target));
        } else if (target.human()) {
            messageIfObserved(Formatter.format("Before you have a chance to recover,"
                            + " {self:subject} pounces on you.", opportunist, target));
        }
        trap.capitalize(opportunist, target, this);
    }

    public void engage(Combat fight) {
        this.fight = fight;
        if (fight.p1.human() || fight.p2.human()) {
            fight.loadCombatGUI(GUI.gui);
        }
    }

    public void parse(Encs choice, Character self, Character target) {
        parse(choice, self, target, null);
    }

    public void parse(Encs choice, Character attacker, Character target, Trap trap) {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println(
                            Formatter.format("{self:true-name} uses %s (%s) on {other:true-name}", attacker, target, choice, trap));
        }
        switch (choice) {
            case ambush:
                ambush(attacker, target);
                break;
            case capitalizeontrap:
                trap(attacker, target, trap);
                break;
            case showerattack:
                showerambush(attacker, target);
                break;
            case aphrodisiactrick:
                aphrodisiactrick(attacker, target);
                break;
            case stealclothes:
                steal(attacker, target);
                break;
            case caughtmasturbating:
                caught(attacker, target);
                break;
            case fight:
                this.fight = new Combat(attacker, target, this.location);
                break;
            case bothflee:
                bothFlee(attacker, target);
                break;
            case flee:
                fleeAttempt(attacker, target);
                break;
            case fleehidden:
                fleeHidden(attacker, target);
                break;
            case smoke:
                smokeFlee(target);
                break;
            default:
                location.endEncounter();
        }
    }

    private void bothFlee(Character faster, Character slower) {
        messageIfObserved(Formatter.format("{self:subject} and {other:subject} dash away from each other at top speed.",
                        faster, slower));
        faster.flee(this.location);
        slower.flee(this.location);
        location.endEncounter();
    }

    public boolean checkIntrudePossible(Character c) {
        return fight != null && !c.equals(getP1()) && !c.equals(getP2());
    }

    public void watch(GUI gui) {
        fight.loadCombatGUI(gui);
    }

    public void await() throws InterruptedException {
        waitForFinish.await();
    }

    public void finish() {
        if (fight != null) {
            fight = null;
        }
        waitForFinish.countDown();
    }

    /**
     * Based on participant responses, determine whether this encounter results in combat.
     */
    public Optional<Combat> resolve() {
        // TODO: spotCheck() has a lot of side effects. Refactor them into something less innocuous.
        if (fight == null) {
            spotCheck();
        }
        if (fight != null) {
            if (!fight.isEnded()) {
                return Optional.of(fight);
            }
        }
        return Optional.empty();
    }
}
