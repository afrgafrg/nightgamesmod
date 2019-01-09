package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.global.Flag;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingTable;

import java.util.List;

public class ClothingStore extends Store {

    ClothingStore(Player player) {
        super("Clothing Store", player);
        ClothingTable.getAllBuyableFrom("ClothingStore").forEach(this::add);
    }

    @Override
    public boolean known() {
        return Flag.checkFlag(Flag.basicStores);
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance) {
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        if (choice.equals("Start")) {
            acted = false;
        }
        if (choice.equals("Leave")) {
            done(acted, instance);
        }
        attemptBuy(choice);
        if (player.human()) {
            GUI.gui.message(
                            "This is a normal retail clothing outlet. For obvious reasons, you'll need to buy anything you want to wear at night in bulk.");
            for (Clothing i : clothing().keySet()) {
                GUI.gui.message(i.getName() + ": " + i.getPrice() + (player.has(i) ? " (Owned)" : ""));
            }
            GUI.gui.message("You have: $" + player.money + " available to spend.");
            displayGoods(nextChoices);
            choose("Leave", nextChoices);
        }
    }

    @Override
    public void shop(NPC npc, int budget) {
        // TODO Auto-generated method stub

    }

}
