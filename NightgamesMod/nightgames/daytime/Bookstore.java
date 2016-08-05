package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;

import java.util.Map;

public class Bookstore extends Store {
    public Bookstore(Character player) {
        super("Bookstore", player);
        add(Item.EnergyDrink);
        add(Item.ZipTie);
        add(Item.Phone);
    }

    @Override
    public boolean available() {
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
                            "In addition to textbooks, the campus bookstore sells assorted items for everyday use.");
            Map<Item, Integer> MyInventory = this.player.getInventory();
            for (Item i : stock.keySet()) {
                if (MyInventory.get(i) == null || MyInventory.get(i) == 0) {
                    Global.global.gui().message(i.getName() + ": $" + i.getPrice());
                } else {
                    Global.global.gui().message(
                                    i.getName() + ": $" + i.getPrice() + " (you have: " + MyInventory.get(i) + ")");
                }
            }
            Global.global.gui().message("You have : $" + player.money + " to spend.");

            displayGoods();
            Global.global.gui().choose(this, "Leave");
        }
    }

    @Override
    public void shop(Character npc, int budget) {
        int remaining = budget;
        int bored = 0;
        while (remaining > 25 && bored < 5) {
            for (Item i : stock.keySet()) {
                if (remaining > i.getPrice() && !npc.has(i, 10)) {
                    npc.gain(i);
                    npc.money -= i.getPrice();
                    remaining -= i.getPrice();
                } else {
                    bored++;
                }
            }
        }
    }
}
