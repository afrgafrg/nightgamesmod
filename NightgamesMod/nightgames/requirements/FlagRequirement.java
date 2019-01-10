package nightgames.requirements;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Flag;

/**
 * Meets requirement if flag is set.
 */
public class FlagRequirement extends BaseRequirement {
    private final String flagName;

    public FlagRequirement(String flagName) {
        this.flagName = flagName;
    }

    public FlagRequirement(Flag flag) {
        this.flagName = flag.name();
    }

    @Override public boolean meets(Combat c, Character self, Character other) {
        return Flag.checkFlag(flagName);
    }
}
