package nightgames.stance;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

public class FFMCowgirlThreesome extends FemdomSexStance {
    protected Character domSexCharacter;

    public FFMCowgirlThreesome(Character domSexCharacter, Character top, Character bottom) {
        super(top, bottom, Stance.reversecowgirl);
        this.domSexCharacter = domSexCharacter;
    }

    @Override
    public Character domSexCharacter(Combat c) {
        return domSexCharacter;
    }

    @Override
    public void setOtherCombatants(List<? extends Character> others) {
        for (Character other : others) {
            if (other.equals(domSexCharacter)) {
                domSexCharacter = other;
            }
        }
    }

    @Override
    public List<BodyPart> partsForStanceOnly(Combat combat, Character self, Character other) {
        if (self == domSexCharacter(combat) && other == bottom) {
            return topParts(combat);
        }
        return self.equals(bottom) ? bottomParts() : Collections.emptyList();
    }

    public Character getPartner(Combat c, Character self) {
        Character domSex = domSexCharacter(c);
        if (self == top) {
            return bottom;
        } else if (domSex == self) {
            return bottom;
        } else {
            return domSex;
        }
    }

    @Override
    public String describe(Combat c) {
        if (top.human()) {
            return "";
        } else {
            return String.format("%s is holding %s down while %s fucking %s in the Cowgirl position.",
                            top.subject(), bottom.nameDirectObject(), domSexCharacter(c).subjectAction("are", "is"), bottom.directObject());
        }
    }

    @Override
    public boolean mobile(Character c) {
        return c != bottom;
    }

    @Override
    public String image() {
        if (bottom.useFemalePronouns()) {
            return "ThreesomeFFMCowgirl_futa.jpg";
        } else {
            return "ThreesomeFFMCowgirl.jpg";
        }
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return false;
    }

    @Override
    public boolean dom(Character c) {
        return c == top || c == domSexCharacter;
    }

    @Override
    public boolean sub(Character c) {
        return c == bottom;
    }

    @Override
    public boolean reachTop(Character c) {
        return c != bottom;
    }

    @Override
    public boolean reachBottom(Character c) {
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return c == bottom;
    }

    @Override
    public boolean behind(Character c) {
        return c == bottom;
    }

    @Override
    public Position insertRandom(Combat c) {
        return new ReverseMount(top, bottom);
    }

    @Override
    public Position reverse(Combat c, boolean writeMessage) {
        if (writeMessage) {
            c.write(bottom, Formatter.format("{self:SUBJECT-ACTION:manage|manages} to unbalance {other:name-do} and push {other:direct-object} off {self:reflective}.", bottom, domSexCharacter));
        }
        return new Neutral(bottom, top);
    }

    @Override
    public void checkOngoing(Combat c) {
        if (!c.getOtherCombatants().contains(domSexCharacter)) {
            c.write(bottom, Formatter.format("With the disappearance of {self:name-do}, {other:subject-action:manage|manages} to escape.", domSexCharacter, bottom));
            c.setStance(new Neutral(top, bottom));
        }
    }

    @Override
    public float priorityMod(Character self) {
        return super.priorityMod(self) + 3;
    }

    @Override
    public int dominance() {
        return 3;
    }

    @Override
    public Collection<Skill> availSkills(Combat c, Character self) {
        if (self != domSexCharacter) {
            return Collections.emptySet();
        } else {
            Collection<Skill> avail = self.getSkills().stream()
                            .filter(skill -> skill.requirements(c, self, bottom))
                            .filter(skill -> Skill.skillIsUsable(c, skill, bottom))
                            .filter(skill -> skill.type(c) == Tactics.fucking).collect(Collectors.toSet());
            return avail;
        }
    }
}
