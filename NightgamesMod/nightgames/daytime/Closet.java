package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Global;

public class Closet extends Activity {

    public Closet(Character player) {
        super("Change Clothes", player);
    }

    @Override
    public boolean available() {
        return true;
    }

    @Override
    public void start() {
        Global.global.gui().clearText();
        Global.global.gui().commandPanel.clearCommand(Global.global.gui());
        if (choice.equals("Start")) {
            Global.global.gui().changeClothes(player, this, "Back");
        } else {
            done(false);
        }
    }

    public void shop(Character npc, int budget) {
        // TODO Auto-generated method stub

    }

}
