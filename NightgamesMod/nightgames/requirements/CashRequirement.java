package nightgames.requirements;

import nightgames.characters.Character;
import nightgames.combat.Combat;

/**
 * Met if character self has at least the specified amount of money.
 */
public class CashRequirement extends BaseRequirement{
    private final int cost;

    public CashRequirement(int cost) {
        this.cost = cost;
    }

    @Override public boolean meets(Combat c, Character self, Character other) {
        return self.money >= cost;
    }
}
