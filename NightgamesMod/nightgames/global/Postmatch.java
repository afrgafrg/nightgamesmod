package nightgames.global;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.gui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Postmatch {
    public final CountDownLatch readyForBed;

    private Character player;
    private ArrayList<Character> combatants;
    private boolean normal;

    public Postmatch(Character player, ArrayList<Character> combatants) {
        readyForBed = new CountDownLatch(1);
        this.player = player;
        this.combatants = combatants;
        normal = true;
        for (Character self : combatants) {
            for (Character other : combatants) {
                if (self != other && self.getAffection(other) >= 1 && self.getAttraction(other) >= 20) {
                    self.gainAttraction(other, -20);
                    self.gainAffection(other, 2);
                }
            }
        }

        events();
        if (normal) {
            normal();
        }
    }

    public void EndMatchGui() {
        GUI.gui.combat = null;
        GUI.gui.clearCommand();
        GUI.gui.showNone();
        GUI.gui.mntmQuitMatch.setEnabled(false);
        RunnableButton button = new RunnableButton("Go to sleep", readyForBed::countDown);  // unblock main loop
        GUI.gui.commandPanel.add(button);
        GUI.gui.commandPanel.add(new SaveButton());
        GUI.gui.commandPanel.refresh();
    }

    public void endMatch() {
        double level = 0;
        int maxLevelTracker = 0;

        for (Character player : GameState.gameState.characterPool.players) {
            player.getStamina().fill();
            player.getArousal().empty();
            player.getMojo().empty();
            player.change();
            level += player.getLevel();
            if (!player.has(Trait.unnaturalgrowth) && !player.has(Trait.naturalgrowth)) {
                maxLevelTracker = Math.max(player.getLevel(), maxLevelTracker);
            }
        }
        final int maxLevel = maxLevelTracker / GameState.gameState.characterPool.players.size();
        GameState.gameState.characterPool.players.stream().filter(c -> c.has(Trait.naturalgrowth)).filter(c -> c.getLevel() < maxLevel + 2).forEach(c -> {
            while (c.getLevel() < maxLevel + 2) {
                c.ding(null);
            }
        });
        GameState.gameState.characterPool.players.stream().filter(c -> c.has(Trait.unnaturalgrowth)).filter(c -> c.getLevel() < maxLevel + 5)
                        .forEach(c -> {
                            while (c.getLevel() < maxLevel + 5) {
                                c.ding(null);
                            }
                        });

        level /= GameState.gameState.characterPool.players.size();

        for (Character rested : Match.resting) {
            rested.gainXP(100 + Math.max(0, (int) Math.round(10 * (level - rested.getLevel()))));
        }
        if (DebugFlags.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Match end");
        }
    }

    private void events() {
        String message = "";
        List<KeyableButton> choice = new ArrayList<KeyableButton>();
        if (Flag.checkFlag(Flag.metLilly) && !Flag.checkFlag(Flag.challengeAccepted) && Random.random(10) >= 7) {
            message = message
                            + "When you gather after the match to collect your reward money, you notice Jewel is holding a crumpled up piece of paper and ask about it. <i>\"This? I found it lying on the ground during the match. It seems to be a worthless piece of trash, but I didn't want to litter.\"</i> Jewel's face is expressionless, but there's a bitter edge to her words that makes you curious. You uncrumple the note and read it.<br/><br/>'Jewel always acts like the dominant, always-on-top tomboy, but I bet she loves to be held down and fucked hard.'<br/><br/><i>\"I was considering finding whoever wrote the note and tying his penis in a knot,\"</i> Jewel says, still impassive. <i>\"But I decided to just throw it out instead.\"</i> It's nice that she's learning to control her temper, but you're a little more concerned with the note. It mentions Jewel by name and seems to be alluding to the Games. You doubt one of the other girls wrote it. You should probably show it to Lilly.<br/><br/><i>\"Oh for fuck's sake..\"</i> Lilly sighs, exasperated. <i>\"I thought we'd seen the last of these. I don't know who writes them, but they showed up last year too. I'll have to do a second sweep of the grounds each night to make sure they're all picked up by morning. They have competitors' names on them, so we absolutely cannot let a normal student find one.\"</i> She toys with a pigtail idly while looking annoyed. <i>\"For what it's worth, they do seem to pay well if you do what the note says that night. Do with them what you will.\"</i><br/>";

            Flag.flag(Flag.challengeAccepted);
            choice.add(new ContinueButton("Next"));
        }
        if (!message.equals("")) {
            GUI.gui.clearText();
            GUI.gui.message(message);
            GUI.gui.prompt(choice);
        }
    }

    private void normal() {
        Character closest = null;
        int maxaffection = 0;
        for (Character rival : combatants) {
            if (rival.getAffection(player) > maxaffection) {
                closest = rival;
                maxaffection = rival.getAffection(player);
            }
        }

        if (maxaffection >= 15 && closest != null) {
            closest.afterParty();
        } else {
            GUI.gui.message("You walk back to your dorm and get yourself cleaned up.");
        }
    }
}
