package nightgames.skills.strategy;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.Rng;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;

public class BreastStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        double weight = .55;
       
        if (self.hasTrait(Trait.lactating)) {
            weight += .25;
        }
        if (self.hasTrait(Trait.temptingtits)) {
            weight += .25;
        }
        if (self.hasTrait(Trait.beguilingbreasts)) {
            weight += .25;
        }
        if (self.getMood().equals(Emotion.confident)) {
            weight += .25;
        }
        return weight;
    }

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return emptyIfSetEmpty(allowedSkills.stream()
                        .filter(skill -> ((skill.getTags(c).contains(SkillTag.usesBreasts)) 
                                        || skill.getTags(c).contains(SkillTag.stripping))
                                        && !skill.getTags(c).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet()));
    }
    
    @Override
    public CombatStrategy instance() {
        return new BreastStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Rng.rng.random(2, 6);
    }
}
