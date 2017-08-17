package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.gui.GUI;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingTable;

public class ClothingStore extends Store {

    public ClothingStore(Character player) {
        super("Clothing Store", player);
        ClothingTable.getAllBuyableFrom("ClothingStore").forEach(article -> add(article));
    }

    @Override
    public boolean known() {
        return Flag.checkFlag(Flag.basicStores);
    }

    @Override
    public void visit(String choice) {
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        if (choice.equals("Start")) {
            acted = false;
        }
        if (choice.equals("Leave")) {
            done(acted);
            return;
        }
        checkSale(choice);
        if (player.human()) {
            GUI.gui.message(
                            "This is a normal retail clothing outlet. For obvious reasons, you'll need to buy anything you want to wear at night in bulk.");
            for (Clothing i : clothing().keySet()) {
                GUI.gui.message(i.getName() + ": " + i.getPrice() + (player.has(i) ? " (Owned)" : ""));
            }
            GUI.gui.message("You have: $" + player.money + " available to spend.");
            displayGoods();
            choose("Leave", GUI.gui);
        }
    }

    @Override
    public void shop(Character npc, int budget) {
        // TODO Auto-generated method stub

    }

}
