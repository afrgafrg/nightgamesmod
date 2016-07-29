package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Global;

public class Closet extends Activity {

    public Closet(Character player) {
        super("Change Clothes", player);
    }

    @Override
    public boolean known() {
        return true;
    }

    @Override
    public void visit(String choice) {
        Global.global.gui().clearText();
        Global.global.gui().clearCommand();
        if (choice.equals("Start")) {
            Global.global.gui().changeClothes(player, this, "Back");
        } else {
            done(false);
        }
    }

    @Override
    public void shop(Character npc, int budget) {
        // TODO Auto-generated method stub

    }

}
