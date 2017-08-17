package nightgames.items.clothing;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import nightgames.Resources.ResourceLoader;
import nightgames.global.DebugFlags;
import nightgames.json.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class ClothingTable {
    private static Map<String, Clothing> clothingTable;
    static {
        ClothingTable.buildClothingTable();
    }


    public static void buildClothingTable() {
        clothingTable = new HashMap<>();
        try (InputStreamReader inputstreamreader = new InputStreamReader(
                        ResourceLoader.getFileResourceAsStream("data/clothing/defaults.json"))) {
            JsonArray defaultClothesJson = JsonUtils.rootJson(inputstreamreader).getAsJsonArray();
            JsonClothingLoader.loadClothingListFromJson(defaultClothesJson).forEach(article -> {
                clothingTable.put(article.id, article);
                if (DebugFlags.isDebugOn(DebugFlags.DEBUG_LOADING)) {
                    System.out.println("Loaded " + article.id);
                }
            });
        } catch (ClassCastException | JsonParseException | IOException e) {
            e.printStackTrace();
        }
        ResourceLoader.getFileResourcesFromDirectory("data/clothing").forEach(inputstream -> {
            try (InputStreamReader inputstreamreader = new InputStreamReader(inputstream)) {
                JsonArray clothesJson = new JsonParser().parse(inputstreamreader).getAsJsonArray();
                JsonClothingLoader.loadClothingListFromJson(clothesJson).forEach(article -> {
                    clothingTable.put(article.id, article);
                    if (DebugFlags.isDebugOn(DebugFlags.DEBUG_LOADING)) {
                        System.out.println("Loaded " + article.id);
                    }
                });
            } catch (ClassCastException | JsonParseException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets an item of clothing by its ID, logging an exception if the ID was not found.
     * @param key The clothing ID to find.
     * @return The item of Clothing, or null if no clothing with that ID was found.
     */
    private static Clothing getAndWarn(String key) {
        Clothing clothing = clothingTable.get(key);
        if (clothing == null) {
            try {
                throw new IllegalArgumentException("No clothing with ID " + key + " was found in the clothing table");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return clothing;
    }

    public static Optional<Clothing> getByID(String key) {
        return Optional.ofNullable(getAndWarn(key));
    }

    /**
     * Given a list of clothing IDs, returns a list of Clothing, excluding the IDs that were not found.
     * @param ids A list of clothing IDs to find.
     * @return A list of found Clothing items.
     */
    public static List<Clothing> getIDs(Collection<String> ids) {
        return ids.stream().map(ClothingTable::getByID).filter(Optional::isPresent).map(Optional::get)
                        .collect(Collectors.toList());
    }

    public static List<Clothing> getAllBuyableFrom(String shopName) {
        return clothingTable.values()
                            .stream()
                            .filter(article -> article.stores.contains(shopName))
                            .collect(Collectors.toList());
    }
}
