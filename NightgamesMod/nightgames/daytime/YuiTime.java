package nightgames.daytime;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;

import java.util.Collections;

public class YuiTime extends BaseNPCTime {
    private boolean acted;

    public YuiTime(Character player) {
        super(player, Global.global.getNPC("Yui"));
        knownFlag = "YuiLoyalty";
        giftedString = "\"Thanks! You're a pretty nice you know?\"";
        giftString = "\"A present? I'm not going to go easy on you even if you bribe me you know?\"";
        transformationOptionString = "Error";
        loveIntro = "[Placeholder] Yui jumps into your arms as you enter, kissing you forcefully.";
        advTrait = null;
        transformationFlag = "";
    }

    @Override
    public void buildTransformationPool() {
        options = Collections.emptyList();
    }

    @Override
    public void visit(String choice) {
        Global.global.gui()
              .clearText();
        Global.global.gui()
              .clearCommand();
        if (choice.equals("Start")) {
            acted = false;
            if (Global.global.checkFlag(Flag.YuiAvailable)) {
                Global.global.gui()
                      .message("Yui greets you happily when you arrive at her hideout/shed. <i>\"Master! It's good to see you. I've collected some items "
                                      + "you may find useful.\"</i> She grins and hands you the items proudly. She's acting so much like an excited puppy that you end up patting "
                                      + "her on the head. She blushes, but seems to like it. <i>\"Would you like to spend some time training? Or is there something else I can do "
                                      + "to help you?\"</i><br/>");
                if (Global.global.checkFlag(Flag.Yui)) {
                    Global.global.gui()
                          .message("");
                } else {
                    Global.global.gui()
                          .message("Hearing the offer from such an attractive blushing girl stirs up a natural temptation. She'd almost certainly agree to "
                                          + "any sexual request you could think of. However, knowing Yui's sincerity and "
                                          + "innocence, your conscience won't let you take advantage of her loyalty.");
                }
                Global.global.gui().choose(this, "Train with Yui");
                npc.gainAffection(player, Math.min(Math.max(0, 20 - npc.getAffection(player)), 5));
                player.gainAffection(npc, Math.min(Math.max(0, 20 - player.getAffection(npc)), 5));
                Global.global.unflag(Flag.YuiAvailable);
                acted = true;
                player.gain(Item.Tripwire);
                player.gain(Item.Rope);
                if (player.getPure(Attribute.Ninjutsu) >= 1) {
                    player.gain(Item.Needle, 3);
                }
                if (player.getPure(Attribute.Ninjutsu) >= 3) {
                    player.gain(Item.SmokeBomb, 2);
                }
            } else {
                Global.global.gui()
                      .message("You head to Yui's hideout, but find it empty. She must be out doing something. It would be a lot easier to track her down if she "
                                      + "had a phone.");
            }
            Global.global.gui()
                  .choose(this, "Leave");
        } else if (choice.equals("Leave")) {
            done(acted);
        } else if (choice.startsWith("Train")) {
            Global.global.gui()
                  .message("Yui's skills at subterfuge turn out to be as strong as she claimed. She's also quite a good teacher. Apparently she helped train her "
                                  + "younger sister, so she's used to it. Nothing she teaches you is overtly sexual, but you can see some useful applications for the Games.");
            player.mod(Attribute.Ninjutsu, 1);
            Global.global.gui()
                  .choose(this, "Leave");
        }
    }

    @Override
    public void subVisit(String choice) {

    }

    @Override
    public void subVisitIntro(String choice) {

    }

}
