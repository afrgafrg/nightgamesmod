package nightgames.skills;

import java.util.Set;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.pet.CharacterPet;
import nightgames.pet.Pet;
import nightgames.skills.petskills.SlimeCloneParasite;

public class Divide extends Skill {
    public Divide(Character self) {
        super("Divide", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return getSelf().has(Trait.BinaryFission) && getSelf().has(Trait.slime);
    }

    @Override
    public float priorityMod(Combat c) {
        return 8.0f;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        // ignore pet limit
        return getSelf().canAct() && c.getStance().mobile(getSelf()) && !c.getStance().prone(getSelf())
                        && !target.isPet();
    }

    @Override
    public int getMojoCost(Combat c) {
        return 30;
    }

    @Override
    public String describe(Combat c) {
        return "Divides your body into two.";
    }

    public static Pet makeClone(Combat c, Character self) {
        int power;
        if (self.has(Trait.NoblesseOblige)) {
            power = Math.max(1, self.getLevel() * 3 / 4);
        } else {
            power = Math.max(1, self.getLevel() / 2);
        }
        int ac = 4 + power / 3;
        CharacterPet pet = null;
        String clonePrefix = String.format("%s clone", self.nameOrPossessivePronoun());
        Set<String> existingNames = c.getOtherCombatants()
                                      .stream().map(Character::getTrueName)
                                      .filter(name -> name.contains(clonePrefix))
                                      .collect(Collectors.toSet());
        String cloneName = clonePrefix + " ?";
        for (char letter : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
            String testName = clonePrefix + " " + String.valueOf(letter).toUpperCase();
            if (!existingNames.contains(testName)) {
                cloneName = testName;
                break;
            }
        }
        if (self instanceof Player) {
            pet = new CharacterPet(cloneName, self, (Player)self, power, ac);
        } else if (self instanceof NPC) {
            pet = new CharacterPet(cloneName, self, (NPC)self, power, ac);
        } else {
            c.write(self, "Something fucked up happened in Divide.");
            return pet;
        }
        pet.getSelf().add(Trait.MindlessClone);
        pet.getSelf().getSkills().add(new SlimeCloneParasite(pet.getSelf()));
        return pet;

    }
    
    @Override
    public boolean resolve(Combat c, Character target) {
        Pet pet = makeClone(c, getSelf());
        if (pet == null) {
            return false;
        }
        c.write(getSelf(), formatMessage(Result.normal, target));
        c.addPet(getSelf(), pet.getSelf());

        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Divide(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.summoning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "unused";
    }
    
    private String formatMessage(Result modifier, Character target) {
        if (getSelf().human()) {
            return Formatter.format("You focus your attention on your slimey consitution and force yourself apart. "
                            + "The force of effort almost makes you black out, but when you finally raise your head, you are face to face with your own clone!", getSelf(), target);
        } else {
            return Formatter.format("Airi's slimy body bubbles as if boiling over. Worried, you step "
                            + "closer to make sure {self:pronoun-action:are} not in any kind of trouble. "
                            + "Suddenly, {self:possessive} viscous body splits apart, "
                            + "making you jump in surprise. Somehow, {self:pronoun} managed to "
                            + "divide {self:possessive} body in half, and now you're fighting "
                            + "another copy of {self:direct-object}!",
                            getSelf(), target);
        }
    }
    
    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return "unused";
    }
}
