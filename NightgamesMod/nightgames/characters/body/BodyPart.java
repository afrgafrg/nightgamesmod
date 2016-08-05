package nightgames.characters.body;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.DebugFlags;
import nightgames.global.Global;

import java.util.Collection;

public interface BodyPart {
    void describeLong(StringBuilder b, Character c);

    boolean isType(String type);

    String getType();

    public String canonicalDescription();

    public String describe(Character c);

    double getHotness(Character self, Character opponent);

    double getPleasure(Character self, BodyPart target);

    double getSensitivity(BodyPart target);

    boolean isReady(Character self);

    JsonObject save();

    double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c);

    double applySubBonuses(Character self, Character opponent, BodyPart with, BodyPart target, double damage, Combat c);

    String getFluids(Character c);

    default double getFluidAddictiveness(Character c) {
        if (getFluids(c).isEmpty()) {
            return 0;
        } else {
            return c.has(Trait.addictivefluids) ? 1 : 0;
        }
    }

    boolean isVisible(Character c);

    boolean isErogenous();

    boolean isNotable();

    BodyPart upgrade();

    int compare(BodyPart other);

    BodyPart downgrade();

    double applyReceiveBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c);

    String prefix();

    String fullDescribe(Character c);

    double priority(Character c);

    int modifyAttribute(Attribute a, int total);

    BodyPart load(JsonObject obj);

    void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan);

    default boolean present() {
        return !isType("none");
    }

    /**
     * -1 is weak, 0 is none, 1 is strong<br/>
     * ex: <br/>
     * <code>
     	  PussyPart.fiery.counterValue(CockPart.primal) == -1   // fiery pussy builds mojo for primal cock<br/>
         CockPart.primal.counterValue(PussyPart.fiery) == 1    // the reverse case<br/>
         CockPart.primal.counterValue(PussyPart.succubus) == 0 // no effects: both parts function as normal<br/>
         </code> It must always hold that:<br/>
     * <code>
      X.counterValue(Y) == -1 <=> Y.counterValue(X) == 1<br/>
      X.counterValue(Y) == 0  <=> Y.counterValue(X) == 0<br/>
      </code>
     * @param self TODO
     * @param other TODO
     */
    int counterValue(BodyPart otherPart, Character self, Character other);

    default double getFemininity(Character self) {
        return 0;
    }

    // Should be called whenever a combatant is penetrated in any way
    default void onStartPenetration(Combat c, Character self, Character opponent, BodyPart target) {
        // Do nothing, may be overridden in implementing classes.
        if (Global.global.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("Starting Penetration for %s -> (%s, %s, %s)\n", describe(self), self, opponent,
                            target.describe(opponent));
        }
    }

    // Should be called when penetration ends
    default void onEndPenetration(Combat c, Character self, Character opponent, BodyPart target) {
        // Do nothing, may be overridden in implementing classes.
        if (Global.global.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("Ending Penetration for %s -> (%s, %s, %s)\n", describe(self), self, opponent,
                            target.describe(opponent));
        }
    }

    // Should be called when either combatant orgasms
    default void onOrgasm(Combat c, Character self, Character opponent) {}

    // Should be called when either combatant orgasms in/with body parts
    default void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart other, boolean selfCame) {
        if (Global.global.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.printf("Processing OrgasmWith for %s -> (%s, %s, %s, %s)\n", describe(self), self, opponent,
                            other.describe(opponent), Boolean.toString(selfCame));
        }
    }

    // whether the part is modded
    default boolean isGeneric(Character self) {
        return getMod(self).getModType().equals("none");
    }

    BodyPartMod getMod(Character self);

    static boolean hasType(Collection<BodyPart> parts, String type) {
        return parts.stream().anyMatch(part -> part.isType(type));
    }

    static boolean hasOnlyType(Collection<BodyPart> parts, String type) {
        return parts.stream().allMatch(part -> part.isType(type));
    }

    default boolean moddedPartCountsAs(Character self, BodyPartMod mod) {
        return getMod(self).countsAs(self, mod);
    }

    static List<String> genitalTypes = Arrays.asList("pussy", "cock", "ass");

    public default boolean isGenital() {
        return genitalTypes.contains(getType());
    }
}
