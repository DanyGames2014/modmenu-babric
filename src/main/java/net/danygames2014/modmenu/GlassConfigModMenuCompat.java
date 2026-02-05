package net.danygames2014.modmenu;

import net.danygames2014.modmenu.api.ConfigScreenFactory;
import net.danygames2014.modmenu.api.ModMenuApi;
import net.glasslauncher.mods.gcapi3.impl.GCCore;

import java.util.HashMap;
import java.util.Map;

public class GlassConfigModMenuCompat implements ModMenuApi {
    private final HashMap<String, Integer> lowestIndexes = new HashMap<>();
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return null;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> map = new HashMap<>();
        //noinspection deprecation,UnstableApiUsage
        GCCore.MOD_CONFIGS.forEach((key, value) -> {
            String namespace = key.split(":")[0];

            if (!map.containsKey(namespace)) {
                lowestIndexes.put(namespace, value.configRoot().index());
                map.put(namespace, (parent) -> value.configCategoryHandler().getConfigScreen(parent, value.modContainer()));
            } else if (value.configRoot().index() < lowestIndexes.getOrDefault(namespace, Integer.MAX_VALUE)) {
                lowestIndexes.put(namespace, value.configRoot().index());
                map.put(namespace, (parent) -> value.configCategoryHandler().getConfigScreen(parent, value.modContainer()));
            }
        });
        return map;
    }
}
