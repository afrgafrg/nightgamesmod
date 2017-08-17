package nightgames.items.clothing;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import nightgames.Resources.ResourceLoader;
import nightgames.global.DebugFlags;
import nightgames.json.JsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static Clothing getByID(String key) {
        Clothing results = clothingTable.get(key);
        if (results == null) {
            throw new IllegalArgumentException(key + " is not a valid clothing key");
        }
        return results;
    }

    public static List<Clothing> getAllBuyableFrom(String shopName) {
        return clothingTable.values()
                            .stream()
                            .filter(article -> {
                                return article.stores.contains(shopName);
                            })
                            .collect(Collectors.toList());
    }
}
