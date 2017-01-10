package nightgames.skills.strategy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.global.Rng;
import nightgames.skills.Collar;
import nightgames.skills.LaunchHarpoon;
import nightgames.skills.MagLock;
import nightgames.skills.Skill;
import nightgames.skills.ThrowBomb;
import nightgames.skills.Yank;

public class TechStrategy extends AbstractStrategy {

    @Override
    public double weight(Combat c, Character self) {
        double score = 0;
        if (self.hasTrait(Trait.harpoon)) {
            score += .8;
        }
        if (self.hasTrait(Trait.bomber)) {
            score += .5;
        }
        if (self.hasTrait(Trait.maglocks)) {
            score += .5;
        }
        if (self.hasTrait(Trait.trainingcollar)) {
            score += .5;
        }
        if (self.hasTrait(Trait.yank)) {
            score += .2;
        }
        if (self.hasTrait(Trait.conducivetoy)) {
            score += .2;
        }
        return score > 0 ? score : -9999;
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Rng.rng.random(5, 10);
    }

    @Override
    public CombatStrategy instance() {
        return new TechStrategy();
    }

    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        Set<Skill> preferred = new HashSet<>();
        Set<Skill> secondary = new HashSet<>();
        if (self.hasTrait(Trait.harpoon)) {
            preferred.add(new LaunchHarpoon(self));
            preferred.add(new Yank(self));
            secondary.addAll(new UseToyStrategy().getPreferredSkills(c, self, allowedSkills)
                                                 .orElse(Collections.emptySet()));
        }
        if (self.hasTrait(Trait.bomber)) {
            preferred.add(new ThrowBomb(self));
        }
        if (self.hasTrait(Trait.maglocks)) {
            preferred.add(new MagLock(self));
            secondary.addAll(new KnockdownStrategy().filterSkills(c, self, allowedSkills));
        }
        if (self.hasTrait(Trait.trainingcollar)) {
            preferred.add(new Collar(self));
            secondary.addAll(new KnockdownStrategy().filterSkills(c, self, allowedSkills));
        }

        preferred.removeIf(s -> !Skill.skillIsUsable(c, s));
        secondary.removeIf(s -> !Skill.skillIsUsable(c, s));
        
        if (!preferred.isEmpty()) {
            return preferred;
        }
        
        if (!secondary.isEmpty() && Global.randomfloat() < .7) {
            return secondary;
        }
        
        return Collections.emptySet();
    }

}
