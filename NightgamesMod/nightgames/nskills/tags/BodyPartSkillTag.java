package nightgames.nskills.tags;

import nightgames.global.Grammar;
import nightgames.nskills.struct.SkillResultStruct;

import java.util.Optional;

public class BodyPartSkillTag extends SkillTag {
    private final String type;
    private final String name;
    private final SkillRequirement requirement = new SkillRequirement() {
        @Override
        public boolean meets(SkillResultStruct results, double value) {
            return results.getSelf().getCharacter().body.has(type);
        }
    };
    public BodyPartSkillTag(String type) {
        this.type = type;
        this.name = "Uses" + Grammar.capitalizeFirstLetter(type);
    }

    @Override
    public SkillRequirement getRequirements() {
        return SkillRequirement.noRequirement();
    }

    @Override
    public SkillRequirement getUsableRequirements() {
        return requirement;
    }

    @Override
    public String getName() {
        return name;
    }

    public Optional<String> getBodyPartType() {
        return Optional.of(type);
    }
}
