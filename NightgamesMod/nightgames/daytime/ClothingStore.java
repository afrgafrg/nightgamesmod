package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.clothing.Clothing;

public class ClothingStore extends Store {

    public ClothingStore(Character player) {
        super("Clothing Store", player);
        Clothing.getAllBuyableFrom("ClothingStore").forEach(article -> add(article));
    }

    @Override
    public boolean known() {
        return Global.global.checkFlag(Flag.basicStores);
    }

    @Override
    public void start() {
        Global.global.gui().clearText();
        Global.global.gui().commandPanel.clearCommand(Global.global.gui());
        if (choice.equals("Start")) {
            acted = false;
        }
        if (choice.equals("Leave")) {
            done(acted);
            return;
        }
        checkSale(choice);
        if (player.human()) {
            Global.global.gui().message(
                            "This is a normal retail clothing outlet. For obvious reasons, you'll need to buy anything you want to wear at night in bulk.");
            for (Clothing i : clothing().keySet()) {
                Global.global.gui().message(i.getName() + ": " + i.getPrice() + (player.has(i) ? " (Owned)" : ""));
            }
            Global.global.gui().message("You have: $" + player.money + " available to spend.");
            displayGoods();
            Global.global.gui().choose(this, "Leave");
        }
    }

    @Override
    public void shop(Character npc, int budget) {
        // TODO Auto-generated method stub

    }

}
