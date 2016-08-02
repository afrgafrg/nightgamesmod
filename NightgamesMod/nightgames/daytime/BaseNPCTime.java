package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.global.Grammar;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.Clothing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public BaseNPCTime(Character player, NPC npc) {
        super(npc.getName(), player);
        this.npc = npc;
        buildTransformationPool();
    }

    @Override
    public boolean known() {
        return knownFlag.isEmpty() || Global.global.checkFlag(knownFlag);
    }

    List<TransformationOption> options;

    public abstract void buildTransformationPool();

    public List<Loot> getGiftables() {
        List<Loot> giftables = new ArrayList<>();
        player.closet.stream().filter(article -> !npc.has(article)).forEach(article -> giftables.add(article));
        return giftables;
    }

    public abstract void subVisit(String choice);

    public abstract void subVisitIntro(String choice);

    public Optional<String> getAddictionOption() {
        return Optional.empty();
    }
    
    @Override
    public void visit(String choice) {
        Global.global.gui().clearText();
        Global.global.gui().commandPanel.clearCommand(Global.global.gui());
        List<Loot> giftables = getGiftables();
        Map<Item, Integer> MyInventory = this.player.getInventory();

        Optional<TransformationOption> optionalOption =
                        options.stream().filter(opt -> choice.equals(opt.option)).findFirst();
        Optional<Loot> optionalGiftOption = giftables.stream()
                        .filter(gift -> choice.equals(Grammar.capitalizeFirstLetter(gift.getName()))).findFirst();

        if (optionalOption.isPresent()) {
            TransformationOption option = optionalOption.get();
            boolean hasAll = option.ingredients.entrySet().stream()
                            .allMatch(entry -> player.has(entry.getKey(), entry.getValue()));
            if (hasAll) {
                Global.global.gui().message(Global.global.format(option.scene, npc, player));
                option.ingredients.entrySet().stream().forEach(entry -> player.consume(entry.getKey(), entry.getValue(), false));
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
        } else if (choice.equals("Gift")) {
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
            options.stream()
                   .filter(option -> option.requirements.stream().allMatch(req -> req.meets(null, player, npc)))
                   .forEach(opt -> {
                Global.global.gui().message(opt.option + ":");
                opt.ingredients.entrySet().forEach((entry) -> {
                    if (MyInventory.get(entry.getKey()) == null || MyInventory.get(entry.getKey()) == 0) {
                        Global.global.gui().message(
                                        entry.getValue() + " " + entry.getKey().getName() + " (you don't have any)");

                    } else {
                        Global.global.gui().message(entry.getValue() + " " + entry.getKey().getName() + " (you have: "
                                        + MyInventory.get(entry.getKey()) + ")");
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
            if (npc.getAffection(player) > 25 && (advTrait == null || npc.has(advTrait))) {
                Global.global.gui().message(Global.global.format(loveIntro, npc, player));
                Global.global.gui().choose(this, "Games");
                Global.global.gui().choose(this, "Sparring");
                Global.global.gui().choose(this, "Sex");
                if (!options.isEmpty()) {
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

    @Override
    public void shop(Character paramCharacter, int paramInt) {
        paramCharacter.gainAffection(npc, 1);
        npc.gainAffection(paramCharacter, 1);

    }

}
