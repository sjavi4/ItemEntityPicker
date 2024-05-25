package me.autobot.itementitypicker.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.autobot.itementitypicker.Itementitypicker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public static final Path CONFIG_PATH;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static boolean MOD_ENABLE = false;
    public static pickMode PICK_MODE = pickMode.BLACKLIST;
    public static final Set<Item> BLACKLIST_ITEMS = new LinkedHashSet<>();
    public static final Set<Item> WHITELIST_ITEMS = new LinkedHashSet<>();

    public enum pickMode {
        WHITELIST {
            @Override
            public Set<Item> getList() {
                return WHITELIST_ITEMS;
            }
        }, BLACKLIST {
            @Override
            public Set<Item> getList() {
                return BLACKLIST_ITEMS;
            }
        };

        public abstract Set<Item> getList();
    }

    static {
        CONFIG_PATH = Itementitypicker.LOADER.getConfigDir().resolve(Itementitypicker.MOD_ID+".json");
        File file = CONFIG_PATH.toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loadConfig() {
        try {
            String reader = new String(Files.readAllBytes(CONFIG_PATH));
            if (reader.isEmpty()) {
                saveConfig();
            }
            JsonObject config = GSON.fromJson(new String(Files.readAllBytes(CONFIG_PATH)), JsonObject.class);
            MOD_ENABLE = config.get("MOD_ENABLE").getAsBoolean();
            final String pickString = config.get("PICK_MODE").getAsString();
            if (pickString.equalsIgnoreCase("blacklist") || pickString.equalsIgnoreCase("whitelist")) {
                PICK_MODE = pickMode.valueOf(config.get("PICK_MODE").getAsString().toUpperCase());
            } else {
                PICK_MODE = pickMode.BLACKLIST;
            }

            JsonElement blacklistItems = config.get("BLACKLIST_ITEMS");
            List<String> array_blacklist = GSON.fromJson(blacklistItems, new TypeToken<List<String>>(){}.getType());
            for (String s : array_blacklist) {
                Item input = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(s));
                BLACKLIST_ITEMS.add(input);
            }
            JsonElement whitelistItems = config.get("WHITELIST_ITEMS");
            List<String> array_whitelist = GSON.fromJson(whitelistItems, new TypeToken<List<String>>(){}.getType());
            for (String s : array_whitelist) {
                Item input = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(s));
                WHITELIST_ITEMS.add(input);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig() {
        JsonObject config = new JsonObject();
        config.addProperty("MOD_ENABLE", MOD_ENABLE);
        config.addProperty("PICK_MODE", PICK_MODE.toString());

        //config.add("SEEDS", MAP_SERIALIZER.serialize(SEEDS, Map.class, null));
        //SEEDS.putAll(seedMap);


        //JsonArray array = new JsonArray();
        config.add("BLACKLIST_ITEMS", GSON.toJsonTree(BLACKLIST_ITEMS.stream().map(Item::toString).toList()));
        config.add("WHITELIST_ITEMS", GSON.toJsonTree(WHITELIST_ITEMS.stream().map(Item::toString).toList()));
        try {
            Files.write(CONFIG_PATH, GSON.toJson(config).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
