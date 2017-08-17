package nightgames.items.clothing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class OutfitPlan extends ArrayList<Clothing> {
    private static final long serialVersionUID = 2513137104528540625L;

    public OutfitPlan(Collection<Clothing> outfitPlan) {
        super(outfitPlan);
    }

    public OutfitPlan() {
        super();
    }

    public void addByID(Collection<String> clothingKeys) {
        addAll(ClothingTable.getIDs(clothingKeys));
    }

    public void addByID(String... clothingKeys) {
        addByID(Arrays.asList(clothingKeys));
    }
}
