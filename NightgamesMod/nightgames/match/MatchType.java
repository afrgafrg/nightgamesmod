package nightgames.match;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.encounter.Encounter;
import nightgames.encounter.IEncounter;
import nightgames.match.ftc.FTCEncounter;
import nightgames.match.ftc.FTCPrematch;

public enum MatchType {
    NORMAL,
    FTC;

    public IEncounter buildEncounter(Character first, Character second, Area location) {
        switch (this) {
            case FTC:
                return new FTCEncounter(first, second, location);
            case NORMAL:
                return new Encounter(first, second, location);
            default:
                throw new Error();
        }
    }

    public Prematch buildPrematch(Player player) {
        switch (this) {
            case FTC:
                return new FTCPrematch(player);
            case NORMAL:
                return new Prematch(player);
            default:
                throw new Error();
        }
    }
}
