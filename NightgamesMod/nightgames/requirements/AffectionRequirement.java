package nightgames.requirements;

import nightgames.characters.Character;
import nightgames.combat.Combat;

/**
 * Requirement is met if Character self's affection towards Character other is at least affectionThreshold.
 */
public class AffectionRequirement extends BaseRequirement {
    private int affectionThreshold;

    public AffectionRequirement(int affectionThreshold) {
        this.affectionThreshold = affectionThreshold;
    }

    @Override public boolean meets(Combat c, Character self, Character other) {
        return self.getAffection(other) >= affectionThreshold;
    }
}
