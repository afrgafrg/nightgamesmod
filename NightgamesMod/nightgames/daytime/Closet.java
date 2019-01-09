package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Closet extends Activity {

    Closet(Player player) {
        super("Change Clothes", player);
    }

    @Override
    public boolean known() {
        return true;
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance) {
        if (choice.equals("Start")) {
            GUI.gui.clearText();
            GUI.gui.clearCommand();
            GUI.gui.changeClothes(player);
            done(false, instance);
        }
    }

    @Override
    public void shop(NPC npc, int budget) {
        // TODO Auto-generated method stub

    }

}
