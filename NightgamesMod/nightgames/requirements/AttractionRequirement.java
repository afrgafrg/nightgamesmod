package nightgames.requirements;

import nightgames.characters.Character;
import nightgames.combat.Combat;

/**
 * Requirement is met if Character self's attraction towards Character other is at least attractionThreshold.
 */
public class AttractionRequirement extends BaseRequirement {
    private int attractionThreshold;

    public AttractionRequirement(int attractionThreshold) {
        this.attractionThreshold = attractionThreshold;
    }

    @Override public boolean meets(Combat c, Character self, Character other) {
        return self.getAttraction(other) >= attractionThreshold;
    }
}
