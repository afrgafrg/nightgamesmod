package nightgames.daytime;

import java.util.ArrayList;
import java.util.List;

import nightgames.characters.Character;
import nightgames.characters.custom.effect.CustomEffect;
import nightgames.daytime.Purchaseable;
import nightgames.requirements.ItemRequirement;
import nightgames.requirements.Requirement;

public class Transformation implements Purchaseable {
    public final String name;
    public List<ItemRequirement> ingredients;
    public List<Requirement> requirements;
    public String scene;
    public String additionalRequirements;
    public CustomEffect effect;

    public Transformation(String name) {
        this.name = name;
        ingredients = new ArrayList<>();
        additionalRequirements = "";
        requirements = new ArrayList<>();
        effect = (c, self, other) -> true;
    }

    @Override public boolean available(Character purchaser) {
        return requirements.stream().allMatch(requirement -> requirement.meets(null, purchaser, null));
    }

    @Override public boolean affordable(Character purchaser) {
        return ingredients.stream().allMatch(ingredient -> ingredient.meets(null, purchaser, null));
    }

    @Override public void purchase(Character purchaser) {
        effect.execute(null, purchaser, null);
    }
}
