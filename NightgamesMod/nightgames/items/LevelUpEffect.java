package nightgames.items;

import nightgames.characters.Character;
import nightgames.combat.Combat;

public class LevelUpEffect extends ItemEffect {
    private int levels;

    public LevelUpEffect(int levels) {
        super("drink", "throw", true, true);
        this.levels = levels;
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        user.addLevelsImmediate(c, levels);
        return true;
    }
}
