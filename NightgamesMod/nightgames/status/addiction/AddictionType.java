package nightgames.status.addiction;

import nightgames.characters.Character;

public enum AddictionType {
    MAGIC_MILK(MagicMilkAddiction::new),
    ZEAL(ZealAddiction::new),
    CORRUPTION(Corruption::new),
    BREEDER(Breeder::new),
    MIND_CONTROL(MindControl::new),
    DOMINANCE(Dominance::new)
    ;
    
    interface AddictionConstructor {
        Addiction construct(Character affected, String supplier, Float magnitude);
    }
    
    private final AddictionConstructor constructor;

    AddictionType(AddictionConstructor constructor) {
        this.constructor = constructor;
    }

    public Addiction build(Character affected, String cause) {
        return build(affected, cause, .01f);
    }
    
    public Addiction build(Character affected, String cause, float mag) {
        return constructor.construct(affected, cause, mag);
    }
}
