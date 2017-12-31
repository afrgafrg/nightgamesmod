package nightgames.combat;

import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.characters.*;
import nightgames.characters.Character;
import nightgames.global.*;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
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
import java.util.concurrent.ExecutionException;

import static nightgames.requirements.RequirementShortcuts.item;

/**
 * An Encounter is a meeting between two or more characters in one area during a match.
 */
public class Encounter implements Serializable {

    private static final long serialVersionUID = 3122246133619156539L;

    private List<Character> participants;
    private boolean p1ff;
    private boolean p2ff;
    private transient Optional<String> p1Guaranteed;
    private transient Optional<String> p2Guaranteed;
    protected Area location;
    protected transient Combat fight;
    private int checkin;
    private CountDownLatch waitForFinish;

    // TODO: Figure out what to do with encounters involving more than three characters.
    public Encounter(Area location) {
        this.location = location;
        participants = new ArrayList<>(location.present);
        assert participants.size() >= 2;
        checkin = 0;
        fight = null;
        p1Guaranteed = Optional.empty();
        p2Guaranteed = Optional.empty();
        checkEnthrall(getP1(), getP2());
        checkEnthrall(getP2(), getP1());
        waitForFinish = new CountDownLatch(1);
    }

    public Character getP1() {
        return participants.get(0);
    }

    public Character getP2() {
        return participants.get(1);
    }

    public Optional<Character> getIntervener() {
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
                    GUI.gui
                          .message("At " + p2.getName() + "'s interruption, you break free from the"
                                          + " succubus' hold on your mind. However, the shock all but"
                                          + " short-circuits your brain; you "
                                          + " collapse to the floor, feeling helpless and"
                                          + " strangely oversensitive");
                } else if (p2.human()) {
                    GUI.gui
                          .message(p1.getName() + " doesn't appear to notice you at first, but when you "
                                          + "wave your hand close to her face her eyes open wide and"
                                          + " she immediately drops to the floor. Although the display"
                                          + " leaves you somewhat worried about her health, she is"
                                          + " still in a very vulnerable position and you never were"
                                          + " one to let an opportunity pass you by.");
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
            if (p1.state == State.shower) {
                p2.showerScene(p1, this);
            } else if (p2.state == State.shower) {
                p1.showerScene(p2, this);
            } else if (p1.state == State.webbed) {
                spider(p2, p1);
            } else if (p2.state == State.webbed) {
                spider(p1, p2);
            } else if (p1.state == State.crafting || p1.state == State.searching) {
                p2.spy(p1, this);
            } else if (p2.state == State.crafting || p2.state == State.searching) {
                p1.spy(p2, this);
            } else if (p1.state == State.masturbating) {
                caught(p2, p1);
            } else if (p2.state == State.masturbating) {
                caught(p1, p2);
            } else if (p2.spotCheck(p1)) {
                if (p1.spotCheck(p2)) {
                    p1.faceOff(p2, this);
                    p2.faceOff(p1, this);
                } else {
                    p2.spy(p1, this);
                }
            } else {
                if (p1.spotCheck(p2)) {
                    p1.spy(p2, this);
                } else {
                    location.endEncounter();
                }
            }
        } else {
            if (p1.state == State.masturbating) {
                if (p1.human()) {
                    GUI.gui
                          .message(p2.getName()
                                          + " catches you masturbating, but fortunately she's still not allowed to attack you, so she just watches you jerk off with "
                                          + "an amused grin.");
                } else if (p2.human()) {
                    GUI.gui
                          .message("You stumble onto " + p1.getName()
                                          + " with her hand between her legs, masturbating. Since you just fought, you still can't touch her, so "
                                          + "you just watch the show until she orgasms.");
                }
            } else if (p2.state == State.masturbating) {
                if (p2.human()) {
                    GUI.gui
                          .message(p1.getName()
                                          + " catches you masturbating, but fortunately she's still not allowed to attack you, so she just watches you jerk off with "
                                          + "an amused grin.");
                } else if (p1.human()) {
                    GUI.gui
                          .message("You stumble onto " + p2.getName()
                                          + " with her hand between her legs, masturbating. Since you just fought, you still can't touch her, so "
                                          + "you just watch the show until she orgasms.");
                }
            } else if (!p1.eligible(p2) && p1.human()) {
                GUI.gui
                      .message("You encounter " + p2.getName()
                                      + ", but you still haven't recovered from your last fight.");
            } else if (p1.human()) {
                GUI.gui
                      .message("You find " + p2.getName()
                                      + " still naked from your last encounter, but she's not fair game again until she replaces her clothes.");
            }
            location.endEncounter();
        }
    }

    protected void fightOrFlight(Character p, boolean fight, Optional<String> guaranteed) {
        if (p == getP1()) {
            p1ff = fight;
            p1Guaranteed = guaranteed;
            checkin++;
        } else {
            p2ff = fight;
            p2Guaranteed = guaranteed;
            checkin++;
        }
        if (checkin >= 2) {
            if (p1ff && p2ff) {
                if (getP1().human() || getP2().human()) {
                    Character player = getP1();
                    this.fight = new Combat(player, getP2(), player.location());
                } else {
                    this.fight = new Combat(getP1(), getP2(), location);
                }
            } else if (p1ff) {
                if (p1Guaranteed.isPresent() && !p2Guaranteed.isPresent()) {
                    if (getP1().human() || getP2().human())
                        GUI.gui.message(p1Guaranteed.get());
                    Character player = getP1();
                    this.fight = new Combat(player, getP2(), player.location());
                } else if (p2Guaranteed.isPresent()) {
                    if (getP1().human() || getP2().human())
                        GUI.gui.message(p2Guaranteed.get());
                    getP2().flee(location);
                } else if (getP2().check(Attribute.Speed, 10 + getP1().get(Attribute.Speed) + (getP1().has(Trait.sprinter) ? 5 : 0)
                                + (getP2().has(Trait.sprinter) ? -5 : 0))) {
                    if (getP1().human()) {
                        GUI.gui
                              .message(getP2().getName() + " dashes away before you can move.");
                    }
                    getP2().flee(location);
                } else {
                    if (getP1().human() || getP2().human()) {
                        if (getP1().human()) {
                            GUI.gui
                                  .message(getP2().getName() + " tries to run, but you stay right on her heels and catch her.");
                        } else {
                            GUI.gui
                                  .message("You quickly try to escape, but " + getP1().getName()
                                                  + " is quicker. She corners you and attacks.");
                        }
                        Character player = getP1();
                        this.fight = new Combat(player, getP2(), player.location());
                    } else {

                        // this.fight=new NullGUI().beginCombat(p1,p2);
                        this.fight = new Combat(getP1(), getP2(), location);
                    }
                }
            } else if (p2ff) {
                if (p2Guaranteed.isPresent() && !p1Guaranteed.isPresent()) {
                    if (getP1().human() || getP2().human())
                        GUI.gui.message(p2Guaranteed.get());
                    Character player = getP1();
                    this.fight = new Combat(player, getP2(), player.location());
                } else if (p1Guaranteed.isPresent()) {
                    if (getP1().human() || getP2().human())
                        GUI.gui.message(p1Guaranteed.get());
                    getP1().flee(location);
                } else if (getP1().check(Attribute.Speed, 10 + getP2().get(Attribute.Speed) + (getP1().has(Trait.sprinter) ? -5 : 0)
                                + (getP2().has(Trait.sprinter) ? 5 : 0))) {
                    if (getP2().human()) {
                        GUI.gui
                              .message(getP1().getName() + " dashes away before you can move.");
                    }
                    getP1().flee(location);
                } else {
                    if (getP1().human() || getP2().human()) {
                        if (getP2().human()) {
                            GUI.gui
                                  .message(getP1().getName() + " tries to run, but you stay right on her heels and catch her.");
                        } else {
                            GUI.gui
                                  .message("You quickly try to escape, but " + getP2().getName()
                                                  + " is quicker. She corners you and attacks.");
                        }
                        Character player = getP1();
                        this.fight = new Combat(player, getP2(), player.location());
                    } else {
                        // this.fight=new NullGUI().beginCombat(p1,p2);
                        this.fight = new Combat(getP1(), getP2(), location);
                    }
                }
            } else {
                boolean humanPresent = getP1().human() || getP2().human();
                if (p1Guaranteed.isPresent()) {
                    if (humanPresent) {
                        GUI.gui.message(p1Guaranteed.get());
                    }
                    getP1().flee(location);
                } else if (p2Guaranteed.isPresent()) {
                    if (humanPresent) {
                        GUI.gui.message(p2Guaranteed.get());
                    }
                    getP2().flee(location);
                } else if (getP1().get(Attribute.Speed) + Random.random(10) >= getP2().get(Attribute.Speed) + Random.random(10)) {
                    if (getP2().human()) {
                        GUI.gui
                              .message(getP1().getName() + " dashes away before you can move.");
                    }
                    getP1().flee(location);
                } else {
                    if (getP1().human()) {
                        GUI.gui
                              .message(getP2().getName() + " dashes away before you can move.");
                    }
                    getP2().flee(location);
                }
            }
        }
    }

    protected void ambush(Character attacker, Character target) {
        target.addNonCombat(new Flatfooted(target, 3));
        if (getP1().human() || getP2().human()) {
            fight = new Combat(attacker, target, attacker.location(), Initiation.ambushRegular);
            GUI.gui.message(Formatter.format("{self:SUBJECT-ACTION:catch|catches} {other:name-do} by surprise and {self:action:attack|attacks}!", attacker, target));
        } else {
            fight = new Combat(attacker, target, location, Initiation.ambushRegular);
        }
    }

    protected void showerambush(Character attacker, Character target) {
        if (target.human()) {
            if (location.id() == Movement.shower) {
                GUI.gui
                      .message("You aren't in the shower long before you realize you're not alone. Before you can turn around, a soft hand grabs your exposed penis. "
                                      + attacker.getName() + " has the drop on you.");
            } else if (location.id() == Movement.pool) {
                GUI.gui
                      .message("The relaxing water causes you to lower your guard a bit, so you don't notice "
                                      + attacker.getName()
                                      + " until she's standing over you. There's no chance to escape, you'll have to face her nude.");
            }
        } else if (attacker.human()) {
            if (location.id() == Movement.shower) {
                GUI.gui
                      .message("You stealthily walk up behind " + target.getName()
                                      + ", enjoying the view of her wet naked body. When you stroke her smooth butt, "
                                      + "she jumps and lets out a surprised yelp. Before she can recover from her surprise, you pounce!");
            } else if (location.id() == Movement.pool) {
                GUI.gui
                      .message("You creep up to the jacuzzi where " + target.getName()
                                      + " is soaking comfortably. As you get close, you notice that her eyes are "
                                      + "closed and she may well be sleeping. You crouch by the edge of the jacuzzi for a few seconds and just admire her nude body with her breasts "
                                      + "just above the surface. You lean down and give her a light kiss on the forehead to wake her up. She opens her eyes and swears under her breath "
                                      + "when she sees you. She scrambles out of the tub, but you easily catch her before she can get away.");
            }
        }
        if (getP1().human() || getP2().human()) {
            Character player = getP1();
            fight = new Combat(player, getP2(), player.location(), Initiation.ambushStrip);
        } else {
            // this.fight=new NullGUI().beginCombat(p1,p2);
            fight = new Combat(getP1(), getP2(), location, Initiation.ambushStrip);
        }
    }

    protected void aphrodisiactrick(Character attacker, Character target) {
        attacker.consume(Item.Aphrodisiac, 1);
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (target.human()) {
            if (location.id() == Movement.shower) {
                GUI.gui
                      .message("The hot shower takes your fatigue away, but you can't seem to calm down. Your cock is almost painfully hard. You need to deal with this while "
                                      + "you have the chance. You jerk off quickly, hoping to finish before someone stumbles onto you. Right before you cum, you are suddenly grabbed from behind and "
                                      + "spun around. " + attacker.getName()
                                      + " has caught you at your most vulnerable and, based on her expression, may have been waiting for this moment. She kisses you and "
                                      + "firmly grasps your twitching dick. In just a few strokes, you cum so hard it's almost painful.\n");
            } else if (location.id() == Movement.pool) {
                GUI.gui
                      .message("As you relax in the jacuzzi, you start to feel extremely horny. Your cock is in your hand before you're even aware of it. You stroke yourself "
                                      + "off underwater and you're just about ready to cum when you hear nearby footsteps. Oh shit, you'd almost completely forgotten you were in the middle of a "
                                      + "match. The footsteps are from " + attacker.getName()
                                      + ", who sits down at the edge of the jacuzzi while smiling confidently. You look for a way to escape, but it's "
                                      + "hopeless. You were so close to finishing you just need to cum now. "
                                      + attacker.getName()
                                      + " seems to be thinking the same thing, as she dips her bare feet into the "
                                      + "water and grasps your penis between them. She pumps you with her feet and you shoot your load into the water in seconds.\n");
            }
        } else if (attacker.human()) {
            if (location.id() == Movement.shower) {
                GUI.gui
                      .message("You empty the bottle of aphrodisiac onto the shower floor, letting the heat from the shower turn it to steam. You watch "
                                      + target.getName() + " and wait "
                                      + "for a reaction. Just when you start to worry that it was all washed down the drain, you see her hand slip between her legs. Her fingers go to work pleasuring herself "
                                      + "and soon she's completely engrossed in her masturbation, allowing you to safely get closer without being noticed. She's completely unreserved, assuming she's alone "
                                      + "and you feel a voyeuristic thrill at the show. You can't just remain an observer though. For this to count as a victory, you need to be in physical contact with her "
                                      + "when she orgasms. When you judge that she's in the home stretch, you embrace her from behind and kiss her neck. She freezes in surprise and you move your hand between "
                                      + "her legs to replace her own. Her pussy is hot, wet, and trembling with need. You stick two fingers into her and rub her clit with your thumb. She climaxes almost "
                                      + "immediately. You give her a kiss on the cheek and leave while she's still too dazed to realize what happened. You're feeling pretty horny, but after a show like that "
                                      + "it's hardly surprising.\n");
            } else if (location.id() == Movement.pool) {
                GUI.gui
                      .message("You sneak up to the jacuzzi, and empty the aphrodisiac into the water without "
                                      + target.getName() + " noticing. You slip away and find a hiding spot. In a "
                                      + "couple minutes, you notice her stir. She glances around, but fails to see you and then closes her eyes and relaxes again. There's something different now though and "
                                      + "her soft moan confirms it. You grin and quietly approach again. You can see her hand moving under the surface of the water as she enjoys herself tremendously. Her moans "
                                      + "rise in volume and frequency. Now's the right moment. You lean down and kiss her on the lips. Her masturbation stops immediately, but you reach underwater and finger "
                                      + "her to orgasm. When she recovers, she glares at you for your unsportsmanlike trick, but she can't manage to get really mad in the afterglow of her climax. You're "
                                      + "pretty turned on by the encounter, but you can chalk this up as a win.\n");
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

    protected void caught(Character attacker, Character target) {
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (target.human()) {
            GUI.gui
                  .message("You jerk off frantically, trying to finish as fast as possible. Just as you feel the familiar sensation of imminent orgasm, you're grabbed from behind. "
                                  + "You freeze, cock still in hand. As you turn your head to look at your attacker, "
                                  + attacker.getName()
                                  + " kisses you on the lips and rubs the head of your penis with her "
                                  + "palm. You were so close to the edge that just you cum instantly.");
            if (!target.mostlyNude()) {
                GUI.gui
                      .message("You groan in resignation and reluctantly strip off your clothes and hand them over.");
            }
        } else if (attacker.human()) {
            GUI.gui
                  .message("You spot " + target.getName()
                                  + " leaning against the wall with her hand working excitedly between her legs. She is mostly, but not completely successful at "
                                  + "stifling her moans. She hasn't noticed you yet, and as best as you can judge, she's pretty close to the end. It'll be an easy victory for you as long as you work fast. "
                                  + "You sneak up and hug her from behind while kissing the nape of her neck. She moans and shudders in your arms, but doesn't stop fingering herself. She probably realizes "
                                  + "she has no chance of winning even if she fights back. You help her along by licking her neck and fondling her breasts as she hits her climax.");
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
            GUI.gui
                  .message(target.getName()
                                  + " is naked and helpless in the giant rope web. You approach slowly, taking in the lovely view of her body. You trail your fingers "
                                  + "down her front, settling between her legs to tease her sensitive pussy lips. She moans and squirms, but is completely unable to do anything in her own defense. "
                                  + "You are going to make her cum, that's just a given. If you weren't such a nice guy, you would leave her in that trap afterward to be everyone else's prey "
                                  + "instead of helping her down. You kiss and lick her neck, turning her on further. Her entrance is wet enough that you can easily work two fingers into her "
                                  + "and begin pumping. You gradually lick your way down her body, lingering at her nipples and bellybutton, until you find yourself eye level with her groin. "
                                  + "You can see her clitoris, swollen with arousal, practically begging to be touched. You trap the sensitive bud between your lips and attack it with your tongue. "
                                  + "The intense stimulation, coupled with your fingers inside her, quickly brings her to orgasm. While she's trying to regain her strength, you untie the ropes "
                                  + "binding her hands and feet and ease her out of the web.");
        } else if (target.human()) {
            GUI.gui
                  .message("You're trying to figure out a way to free yourself, when you see " + attacker.getName()
                                  + " approach. You groan in resignation. There's no way you're "
                                  + "going to get free before she finishes you off. She smiles as she enjoys your vulnerable state. She grabs your dangling penis and puts it in her mouth, licking "
                                  + "and sucking it until it's completely hard. Then the teasing starts. She strokes you, rubs you, and licks the head of your dick. She uses every technique to "
                                  + "pleasure you, but stops just short of letting you ejaculate. It's maddening. Finally you have to swallow your pride and beg to cum. She pumps you dick in earnest "
                                  + "now and fondles your balls. When you cum, you shoot your load onto her face and chest. You hang in the rope web, literally and figuratively drained. "
                                  + attacker.getName() + " " + "graciously unties you and helps you down.");
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
            GUI.gui
                  .message("You quietly swipe " + target.getName()
                                  + "'s clothes while she's occupied. It's a little underhanded, but you can still turn them in for cash just as if you defeated her.");
        }
        thief.gain(target.getTrophy());
        target.nudify();
        target.state = State.lostclothes;
        location.endEncounter();
    }

    public void trap(Character opportunist, Character target, Trap trap) {
        if (opportunist.human()) {
            GUI.gui
                  .message("You leap out of cover and catch " + target.getName() + " by surprise.");
        } else if (target.human()) {
            GUI.gui
                  .message("Before you have a chance to recover, " + opportunist.getName() + " pounces on you.");
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

    public void parse(Encs choice, Character self, Character target, Trap trap) {
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println(
                            Formatter.format("{self:true-name} uses %s (%s) on {other:true-name}", self, target, choice, trap));
        }
        switch (choice) {
            case ambush:
                ambush(self, target);
                break;
            case capitalize:
                trap(self, target, trap);
                break;
            case showerattack:
                showerambush(self, target);
                break;
            case aphrodisiactrick:
                aphrodisiactrick(self, target);
                break;
            case stealclothes:
                steal(GameState.gameState.characterPool.getPlayer(), target);
                break;
            case fight:
                fightOrFlight(self, true, Optional.empty());
                break;
            case flee:
                fightOrFlight(self, false, Optional.empty());
                break;
            case fleehidden:
                checkin += 2;
                fightOrFlight(self, false, Optional.of(fleeHiddenMessage(self, target)));
                break;
            case smoke:
                fightOrFlight(self, false, Optional.of(smokeMessage(self)));
                self.consume(Item.SmokeBomb, 1);
                break;
            default:
                return;
        }
    }

    private String smokeMessage(Character c) {
        return String.format("%s a smoke bomb and %s.", 
                        Formatter.capitalizeFirstLetter(c.subjectAction("drop", "drops"))
                        , c.action("disappear", "disappears"));
    }

    private String fleeHiddenMessage(Character c, Character other) {
        return Formatter.format("{self:SUBJECT-ACTION:flee} before {other:subject-action:can} notice {self:direct-object}.", c, other);
    }

    public boolean checkIntrude(Character c) {
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

    // TODO: Refactor these prompts into a single method.
    // FIXME: Intervene prompts do not show up!
    public void promptIntervene(Character p1, Character p2, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = Arrays.asList(new LabeledValue<>("p1", "Help " + p1.getName()),
                        new LabeledValue<>("p2", "Help " + p2.getName()),
                        new LabeledValue<>("Watch", "Watch them fight"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "p1":
                    intrude(player, p1);
                    break;
                case "p2":
                    intrude(player, p2);
                    break;
                case "Watch":
                    watch(gui);
                    break;
                default:
                    throw new AssertionError("Unknown Intervene choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void promptShower(Character target, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Surprise", "Surprise Her"));
        if (!target.mostlyNude()) {
            choices.add(new LabeledValue<>("Steal", "Steal Clothes"));
        }
        if (player.has(Item.Aphrodisiac)) {
            choices.add(new LabeledValue<>("Aphrodisiac", "Use Aphrodisiac"));
        }
        choices.add(new LabeledValue<>("Wait", "Do Nothing"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Surprise":
                    parse(Encs.showerattack, player, target);
                    break;
                case "Steal":
                    parse(Encs.stealclothes, player, target);
                    break;
                case "Aphrodisiac":
                    parse(Encs.aphrodisiactrick, player, target);
                    break;
                case "Wait":
                    parse(Encs.wait, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Shower choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void promptOpportunity(Character target, Trap trap, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Attack", "Attack" + target.getName()));
        choices.add(new LabeledValue<>("Wait", "Wait"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Attack":
                    parse(Encs.capitalize, player, target, trap);
                    break;
                case "Wait":
                    parse(Encs.wait, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Opportunity choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void promptFF(Character target, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Fight", "Fight"));
        choices.add(new LabeledValue<>("Flee", "Flee"));
        if (item(Item.SmokeBomb, 1).meets(null, player, null)) {
            choices.add(new LabeledValue<>("Smoke", "Smoke Bomb"));
        }
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Fight":
                    parse(Encs.fight, player, target);
                    break;
                case "Flee":
                    parse(Encs.flee, player, target);
                    break;
                case "Smoke":
                    parse(Encs.smoke, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Fight/Flight choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void promptAmbush(Character target, GUI gui) {
        Player player = GameState.gameState.characterPool.getPlayer();
        List<LabeledValue<String>> choices = new ArrayList<>();
        choices.add(new LabeledValue<>("Attack", "Attack " + target.getName()));
        choices.add(new LabeledValue<>("Wait", "Wait"));
        choices.add(new LabeledValue<>("Flee", "Flee"));
        try {
            String choice = gui.promptFuture(choices).get();
            switch (choice) {
                case "Attack":
                    parse(Encs.ambush, player, target);
                    break;
                case "Wait":
                    parse(Encs.wait, player, target);
                    break;
                case "Flee":
                    parse(Encs.fleehidden, player, target);
                    break;
                default:
                    throw new AssertionError("Unknown Ambush choice: " + choice);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
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
            Optional<Character> intervener = getIntervener();
            if (intervener.isPresent() && checkIntrude(intervener.get())) {
                intervener.get().intervene(this, getP1(), getP2());
            }
            if (!fight.isEnded()) {
                return Optional.of(fight);
            }
        }
        return Optional.empty();
    }
}
