package nightgames.trap;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Encounter;
import nightgames.global.Formatter;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.status.Flatfooted;
import nightgames.status.Hypersensitive;
import nightgames.status.Oiled;

public class TentacleTrap extends Trap {

    public TentacleTrap() {
        this(null);
    }
    
    public TentacleTrap(Character owner) {
        super("Tentacle Trap", owner);
    }

    @Override
    public void trigger(Character target) {
        if (target.mostlyNude()) {
            if (target.human()) {
                GUI.gui.message(
                                "An unearthly glow appears from the floor surrounding you and at least a dozen tentacles burst from the floor. Before you can react, you're lifted helpless "
                                                + "into the air. The tentacles assault you front and back, wriggling around you nipples and cock, while one persistant tentacle forces its way into your ass. The overwhelming "
                                                + "sensations and violation keep you from thinking clearly and you can't even begin to mount a reasonable resistance. Just as suddenly as they attacked you, the tentacles "
                                                + "are gone, dumping you unceremoniously to the floor. You're left coated in a slimy liquid that, based on your rock-hard erection, seems to be a powerful aphrodisiac. Holy "
                                                + "fucking hell....");
            } else if (target.location().humanPresent()) {
                GUI.gui.message(Formatter.format(
                                "{self:subject} gets caught in the tentacle trap and is immediately surrounded by "
                                + "penis-shaped tentacles. Before {self:pronoun} can escape, they bind {self:direct-object} "
                                + "limbs and start probing and caressing {self:possessive} naked body. "
                                + "The tentacles start to ooze out lubricant and two tentacles penetrate "
                                + "{self:direct-object} vaginally and anally. A third tentacle slips into "
                                + "{self:possessive} mouth, while the rest frot against {self:possessive} body. "
                                + "They gang-bang {self:direct-object} briefly, but thoroughly, before squirting "
                                + "liquid over {self:direct-object} and disappearing back into the floor. "
                                + "{self:PRONOUN}'s left shivering, sticky, and unsatisfied. In effect, "
                                + "{self:pronoun}'s already defeated.", target, null));
            }
            target.tempt(target.getArousal().max());
            target.getWillpower().set(target.getWillpower().max() / 3);
            target.calm(null, 1);
            target.addNonCombat(new Oiled(target));
            target.addNonCombat(new Hypersensitive(target));
            target.location().opportunity(target, this);
        } else {
            if (target.human()) {
                GUI.gui.message(
                                "Holy hell! A dozen large tentacles shoot out of the floor in front of you and thrash wildly. You freeze, hoping they won't notice you, but "
                                                + "it seems futile. the tentacles approach you from all sides, poking at you tentatively. As suddenly as they appeared, the tentacles vanish back into the floor. "
                                                + "\n...Is that it? You're safe... you guess?");
            } else if (target.location().humanPresent()) {
                GUI.gui.message(Formatter.format("{self:subject} stumbles into range of the fetish totem. "
                                + "A cage of phallic tentacles appear around {self:direct-object}. They "
                                + "apparently aren't interested in {self:direct-object} and "
                                + "they disappear, leaving {self:direct-object} slightly bewildered.",
                                target, null));
            }
        }
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.Totem);
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.getRank() > 0;
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner;
        owner.consume(Item.Totem, 1);
        return "You need to activate this phallic totem before it can be used as a trap. You stroke the small totem with your hand, which is... weird, but effective. You "
                        + "quickly place the totem someplace out of sight and hurriedly get out of range. You're not sure whether this will actually discriminate before attacking.";
    }

    @Override
    public void capitalize(Character attacker, Character victim, Encounter enc) {
        victim.addNonCombat(new Flatfooted(victim, 1));
        enc.engage(new Combat(attacker, victim, attacker.location()));
        attacker.location().remove(this);
    }
}
