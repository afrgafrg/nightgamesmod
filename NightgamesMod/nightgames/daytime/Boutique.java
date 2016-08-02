package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.clothing.Clothing;

public class Boutique extends Store {
    public Boutique(Character player) {
        super("Boutique", player);
        Clothing.getAllBuyableFrom("Boutique").forEach(article -> add(article));
    }

    @Override
    public boolean known() {
        if (player.hasPussy()) {
            return Global.global.checkFlag(Flag.basicStores);
        }
        return false;
    }

    @Override
    public void visit(String choice) {
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
                            "This is a higher end store for women's clothing. Things may get a bit expensive here.");
            for (Clothing i : clothing().keySet()) {
                Global.global.gui().message(i.getName() + ": " + i.getPrice() + (player.has(i) ? " (Owned)" : ""));
            }
            Global.global.gui().message("You have: $" + player.money + " available to spend.");
            displayGoods();
            Global.global.gui().choose(this, "Leave");
        }
    }

    @Override
    public void shop(Character npc, int budget) {}

}
