package nightgames.pet.arms.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.pet.PetCharacter;
import nightgames.pet.arms.Arm;

public class DefabRay extends ArmSkill {
    public DefabRay() {
        super("Defabrication Ray", 30);
    }

    @Override
    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return super.usable(c, arm, owner, target) && !target.outfit.isNude();
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        boolean sub = c.getStance().dom(owner);
        boolean success = sub || Random.random(100) < 10 + owner.get(Attribute.Science);
        
        if (success) {
            ClothingSlot slot = target.outfit.getRandomShreddableSlot();
            Clothing item = target.outfit.getTopOfSlot(slot);
            if (item == null || item.is(ClothingTrait.harpoonDildo) 
                            || item.is(ClothingTrait.harpoonOnahole)) {
                return false;
            }
            target.shred(slot);
            c.write(PetCharacter.DUMMY, Formatter.format("{self:NAME-POSSESSIVE} %s points at you, its"
                            + " head faintly glowing with a blue light. Suddenly, an eerily similar light"
                            + " surrounds {other:name-possessive} %s, and it soon disappears entirely!"
                            , owner, target, arm.getName(), item.toString()));
            return true;
        }
        return false;
    }
}
