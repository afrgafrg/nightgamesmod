package nightgames.daytime.NPCTime;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.characters.Trait;
import nightgames.daytime.Activity;
import nightgames.daytime.Transformation;
import nightgames.global.Global;
import nightgames.global.Grammar;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.Clothing;
import nightgames.requirements.Requirement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nightgames.requirements.RequirementShortcuts.*;

public abstract class BaseNPCTime extends Activity {
    protected NPC npc;
    String knownFlag = "";
    String noRequestedItems = "{self:SUBJECT} frowns when {self:pronoun} sees that you don't have the requested items.";
    String giftedString = "\"Awww thanks!\"";
    String giftString = "\"A present? You shouldn't have!\"";
    String transformationOptionString = "Transformations";
    String loveIntro = "[Placeholder]<br/>LoveIntro";
    String transformationIntro = "[Placeholder]<br/>TransformationIntro";
    String transformationFlag = "";
    Trait advTrait = null;
    List<Transformation> transformations;

    public BaseNPCTime(Player player, NPC npc) {
        super(npc.getName(), player);
        this.npc = npc;
        buildTransformationPool();
    }

    @Override
    public boolean available() {
        return knownFlag.isEmpty() || Global.global.checkFlag(knownFlag);
    }


    public abstract void buildTransformationPool();

    public List<Loot> getGiftables() {
        List<Loot> giftables = new ArrayList<>();
        player.closet.stream().filter(article -> !npc.has(article)).forEach(giftables::add);
        return giftables;
    }

    public abstract void subVisit(String choice);

    public abstract void subVisitIntro(String choice);

    public Optional<String> getAddictionOption() {
        return Optional.empty();
    }

    protected enum VisitChoice {
        GAMES,
        SPARRING,
        SEX,
        SHOP,
        GIFT,
        OUTFIT,
        ADDICTION,
        LEAVE;

        String getLabel() {
            return Grammar.capitalizeFirstLetter(name());
        }
    }


    @Override
    public void start() throws InterruptedException {
        List<Loot> giftables = getGiftables();
        Map<Item, Integer> playerInventory = this.player.getInventory();

        // TODO: implement choiceAvailable() or availableChoices()
        Map<String, String> choiceLabelMap = Stream.of(VisitChoice.values()).filter(choice -> choiceAvailable(choice))
                        .collect(Collectors.toMap(VisitChoice::name, VisitChoice::getLabel));

//        Optional<Loot> optionalGiftOption = giftables.stream()
//                        .filter(gift -> choice.equals(Grammar.capitalizeFirstLetter(gift.getName()))).findFirst();

        if (optionalOption.isPresent()) {
            Transformation option = optionalOption.get();
            boolean hasAll = option.ingredients.entrySet().stream()
                            .allMatch(entry -> player.has(entry.getKey(), entry.getValue()));
            if (hasAll) {
                Global.global.gui().message(Global.global.format(option.scene, npc, player));
                option.ingredients.entrySet().forEach(entry -> player.consume(entry.getKey(), entry.getValue(), false));
                option.effect.execute(null, player, npc);
                Global.global.gui().choose(this, "Leave");
            } else {
                Global.global.gui().message(Global.global.format(noRequestedItems, npc, player));
                Global.global.gui().choose(this, "Back");
            }
        } else if (optionalGiftOption.isPresent()) {
            Global.global.gui().message(Global.global.format(giftedString, npc, player));
            if (optionalGiftOption.get() instanceof Clothing) {
                if (player.closet.contains(optionalGiftOption.get())) {
                    player.closet.remove(optionalGiftOption.get());
                }
                npc.closet.add((Clothing) optionalGiftOption.get());
            }
            player.gainAffection(npc, 2);
            npc.gainAffection(player, 2);
            Global.global.gui().choose(this, "Back");
        }

        VisitChoice choice = controller.getChoice(choiceLabelMap).map(VisitChoice::valueOf).orElse(VisitChoice.LEAVE);
        if (choice.equals("Gift")) {
            Global.global.gui().message(Global.global.format(giftString, npc, player));
            giftables.stream().forEach(loot -> Global.global.gui()
                            .choose(this, Grammar.capitalizeFirstLetter(loot.getName())));
            Global.global.gui().choose(this, "Back");
        } else if (choice.equals("Change Outfit")) {
            Global.global.gui().changeClothes(npc, this, "Back");
        } else if (choice.equals(transformationOptionString)) {
            Global.global.gui().message(Global.global.format(transformationIntro, npc, player));
            if (!transformationFlag.equals("")) {
                Global.global.flag(transformationFlag);
            }
            transformations.stream()
                   .filter(option -> option.requirements.stream().allMatch(req -> req.meets(null, player, npc)))
                   .forEach(opt -> {
                Global.global.gui().message(opt.name + ":");
                opt.ingredients.entrySet().forEach((entry) -> {
                    if (playerInventory.get(entry.getKey()) == null || playerInventory.get(entry.getKey()) == 0) {
                        Global.global.gui().message(
                                        entry.getValue() + " " + entry.getKey().getName() + " (you don't have any)");

                    } else {
                        Global.global.gui().message(entry.getValue() + " " + entry.getKey().getName() + " (you have: "
                                        + playerInventory.get(entry.getKey()) + ")");
                    }
                });
                if (!opt.additionalRequirements.isEmpty()) {
                    Global.global.gui().message(opt.additionalRequirements);
                }
                Global.global.gui().message("<br/>");
                Global.global.gui().choose(this, opt.option);
            });
            Global.global.gui().choose(this, "Back");
        } else if (choice.equals("Start") || choice.equals("Back")) {
            if (npc.getAffection(player) > 25 && (advancedTrait == null || npc.hasTrait(advancedTrait))) {
                Global.global.gui().message(Global.global.format(loveIntro, npc, player));
                Global.global.gui().choose(this, "Games");
                Global.global.gui().choose(this, "Sparring");
                Global.global.gui().choose(this, "Sex");
                if (!transformations.isEmpty()) {
                    Global.global.gui().choose(this, transformationOptionString);
                }
                if (npc.getAffection(player) > 30) {
                    Global.global.gui().choose(this, "Gift");
                }
                if (npc.getAffection(player) > 35) {
                    Global.global.gui().choose(this, "Change Outfit");
                }
                Optional<String> addictionOpt = getAddictionOption();
                if (addictionOpt.isPresent()) {
                    Global.global.gui().choose(this, addictionOpt.get());
                }
                Global.global.gui().choose(this, "Leave");
            } else {
                subVisitIntro(choice);
            }
        } else if (choice.equals("Leave")) {
            done(true);
        } else {
            subVisit(choice);
        }
    }

    public void shop(Character paramCharacter, int paramInt) {
        paramCharacter.gainAffection(npc, 1);
        npc.gainAffection(paramCharacter, 1);

    }

}
