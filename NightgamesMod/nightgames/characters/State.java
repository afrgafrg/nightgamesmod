package nightgames.characters;

import java.util.*;

public enum State {
    ready,
    shower,
    combat,
    searching,
    crafting,
    hidden,
    resupplying,
    lostclothes,
    webbed,
    masturbating,
    quit,

    // FTC-specific
    inTree,
    inBushes,
    inPass;

    private static final List<State> vulnerable = Arrays.asList(shower, searching, crafting, webbed, masturbating);

    boolean isVulnerable() {
        return vulnerable.contains(this);
    }
}
