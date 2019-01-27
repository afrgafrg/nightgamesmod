package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingTable;
import nightgames.nskills.tags.SkillTag;

public class LivingClothingOther extends Skill {
    public LivingClothingOther(Character self) {
        super("Living Clothing: Other", self, 8);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.debuff);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Science) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && !c.getStance().mobile(target) && c.getStance().mobile(getSelf())
                        && !c.getStance().inserted() && target.torsoNude() && getSelf().has(Item.Battery, 3);
    }

    @Override
    public String describe(Combat c) {
        return "Fabricate a living suit of tentacles to wrap around your opponent: 3 Batteries";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        getSelf().consume(Item.Battery, 3);
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, 0, Result.normal, target));
        } else {
            c.write(getSelf(), receive(c, 0, Result.normal, target));
        }
        ClothingTable.getByID("tentacletop").ifPresent(top -> target.getOutfit().equip(top));
        ClothingTable.getByID("tentaclebottom").ifPresent(bottom -> target.getOutfit().equip(bottom));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new LivingClothingOther(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String message;
        message = Formatter.format("You power up your fabricator and dial the knob to the emergency "
                        + "reclothing setting. You hit the button and dark tentacles squirm out "
                        + "of the device. You hold {other:name-do}"
                        + " down and point the tentacles at {other:possessive} body. "
                        + "The undulating tentacles coils around {other:possessive}"
                        + " body and wraps itself into a living suit.", getSelf(), target);
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        String message;
        message = String.format("While holding %s down, %s powers up %s fabricator and dials the knob"
                        + " to the emergency reclothing setting. %s hits the button and dark tentacles squirm"
                        + " out of the device. The created tentacles coils around %s body and"
                        + " wrap themselves into a living suit.", target.nameDirectObject(),
                        getSelf().subject(), getSelf().possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(getSelf().pronoun()),
                        target.nameOrPossessivePronoun());
        return message;
    }

}
