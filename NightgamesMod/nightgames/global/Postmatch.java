package nightgames.global;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.gui.CommandButton;
import nightgames.gui.SceneButton;
import nightgames.status.addiction.Addiction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class Postmatch implements Scene {

    private Player player;
    private Collection<Character> combatants;
    private boolean normal;

    public Postmatch(Player player, Collection<Character> combatants) {
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
        player.getAddictions().forEach(Addiction::endNight);

        events();
        if (normal) {
            normal();
        }
    }

    @Override
    public void respond(String response) {
        if (response.startsWith("Next")) {
            normal();
        }
    }

    private void events() {
        String message = "";
        ArrayList<CommandButton> choice = new ArrayList<>();
        if (Global.global.checkFlag(Flag.metLilly) && !Global.global.checkFlag(Flag.challengeAccepted)
                        && Global.global.random(10) >= 7) {
            message = message
                            + "When you gather after the match to collect your reward money, you notice Jewel is holding a crumpled up piece of paper and ask about it. <i>\"This? I found it lying on the ground during the match. It seems to be a worthless piece of trash, but I didn't want to litter.\"</i> Jewel's face is expressionless, but there's a bitter edge to her words that makes you curious. You uncrumple the note and read it.<br/><br/>'Jewel always acts like the dominant, always-on-top tomboy, but I bet she loves to be held down and fucked hard.'<br/><br/><i>\"I was considering finding whoever wrote the note and tying his penis in a knot,\"</i> Jewel says, still impassive. <i>\"But I decided to just throw it out instead.\"</i> It's nice that she's learning to control her temper, but you're a little more concerned with the note. It mentions Jewel by name and seems to be alluding to the Games. You doubt one of the other girls wrote it. You should probably show it to Lilly.<br/><br/><i>\"Oh for fuck's sake..\"</i> Lilly sighs, exasperated. <i>\"I thought we'd seen the last of these. I don't know who writes them, but they showed up last year too. I'll have to do a second sweep of the grounds each night to make sure they're all picked up by morning. They have competitors' names on them, so we absolutely cannot let a normal student find one.\"</i> She toys with a pigtail idly while looking annoyed. <i>\"For what it's worth, they do seem to pay well if you do what the note says that night. Do with them what you will.\"</i><br/>";

            Global.global.flag(Flag.challengeAccepted);
            choice.add(new SceneButton("Next"));
        }
        if (!message.equals("")) {
            Global.global.gui().prompt(message, choice);
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
            Global.global.gui().message("You walk back to your dorm and get yourself cleaned up.");
        }
    }
}
