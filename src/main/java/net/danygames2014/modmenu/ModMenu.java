package net.danygames2014.modmenu;

import com.google.common.collect.LinkedListMultimap;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.danygames2014.modmenu.api.impl.LegacyModMenuImpl;
import net.danygames2014.modmenu.api.ConfigScreenFactory;
import net.danygames2014.modmenu.api.ModMenuApi;
import net.danygames2014.modmenu.config.ModMenuConfig;
import net.danygames2014.modmenu.config.ModMenuConfig.GameMenuButtonStyle;
import net.danygames2014.modmenu.config.ModMenuConfig.TitleMenuButtonStyle;
import net.danygames2014.modmenu.config.ModMenuConfigManager;
import net.danygames2014.modmenu.event.ModMenuEventHandler;
import net.danygames2014.modmenu.util.TranslationUtil;
import net.danygames2014.modmenu.util.mod.Mod;
import net.danygames2014.modmenu.util.mod.fabric.FabricDummyParentMod;
import net.danygames2014.modmenu.util.mod.fabric.FabricMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.TranslationStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.*;

public class ModMenu implements ClientModInitializer {
	public static final String MOD_ID = "modmenu";
	public static final Logger LOGGER = LogManager.getLogger("Mod Menu");
	public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
	public static final Gson GSON_MINIFIED = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	public static final Map<String, Mod> MODS = new HashMap<>();
	public static final Map<String, Mod> ROOT_MODS = new HashMap<>();
	public static final LinkedListMultimap<Mod, Mod> PARENT_MAP = LinkedListMultimap.create();

	private static final Map<String, ConfigScreenFactory<?>> configScreenFactories = new HashMap<>();
	private static final List<ModMenuApi> apiImplementations = new ArrayList<>();

	private static int cachedDisplayedModCount = -1;
	public static boolean devEnvironment = FabricLoader.getInstance().isDevelopmentEnvironment();

	public static Screen getConfigScreen(String modid, Screen menuScreen) {
		for (ModMenuApi api : apiImplementations) {
			Map<String, ConfigScreenFactory<?>> factoryProviders = api.getProvidedConfigScreenFactories();
			if (!factoryProviders.isEmpty()) {
				factoryProviders.forEach(configScreenFactories::putIfAbsent);
			}
		}
		if (ModMenuConfig.HIDDEN_CONFIGS.getValue().contains(modid)) {
			return null;
		}
		ConfigScreenFactory<?> factory = configScreenFactories.get(modid);
		if (factory != null) {
			return factory.create(menuScreen);
		}
		return null;
	}

	@Override
	public void onInitializeClient() {
		ModMenuConfigManager.initializeConfig();
		Set<String> modpackMods = new HashSet<>();

		FabricLoader.getInstance().getEntrypointContainers("modmenu", Object.class).forEach(entrypoint -> {
			ModMetadata metadata = entrypoint.getProvider().getMetadata();
			String modId = metadata.getId();
			try {
				if(entrypoint.getEntrypoint() instanceof ModMenuApi api) {
					configScreenFactories.put(modId, api.getModConfigScreenFactory());
					apiImplementations.add(api);
					api.attachModpackBadges(modpackMods::add);
				} else if(entrypoint.getEntrypoint() instanceof io.github.prospector.modmenu.api.ModMenuApi api2) {
					ModMenuApi newApi = new LegacyModMenuImpl(api2);
					configScreenFactories.put(modId, newApi.getModConfigScreenFactory());
					apiImplementations.add(newApi);
					newApi.attachModpackBadges(modpackMods::add);
				}
			} catch (Throwable e) {
				LOGGER.error("Mod {} provides a broken implementation of ModMenuApi", modId, e);
			}
		});

		// Fill mods map
		for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
			Mod mod;

			mod = new FabricMod(modContainer, modpackMods);
			
			MODS.put(mod.getId(), mod);
		}

		Map<String, Mod> dummyParents = new HashMap<>();

		// Initialize parent map
		for (Mod mod : MODS.values()) {
			String parentId = mod.getParent();
			if (parentId != null) {
				Mod parent = MODS.getOrDefault(parentId, dummyParents.get(parentId));
				if (parent == null) {
					if (mod instanceof FabricMod) {
						parent = new FabricDummyParentMod((FabricMod) mod, parentId);
						dummyParents.put(parentId, parent);
					}
				}
				PARENT_MAP.put(parent, mod);
			} else {
				ROOT_MODS.put(mod.getId(), mod);
			}
		}
		MODS.putAll(dummyParents);
		ModMenuEventHandler.register();
	}

	public static void clearModCountCache() {
		cachedDisplayedModCount = -1;
	}

	public static String getDisplayedModCount() {
		if (cachedDisplayedModCount == -1) {
			// listen, if you have >= 2^32 mods then that's on you
			cachedDisplayedModCount = Math.toIntExact(MODS.values().stream().filter(mod ->
				(ModMenuConfig.COUNT_CHILDREN.getValue() || mod.getParent() == null) &&
					(ModMenuConfig.COUNT_LIBRARIES.getValue() || !mod.getBadges().contains(Mod.Badge.LIBRARY)) &&
					(ModMenuConfig.COUNT_HIDDEN_MODS.getValue() || !mod.isHidden())
			).count());
		}
		return NumberFormat.getInstance().format(cachedDisplayedModCount);
	}

	public static String createModsButtonText(boolean title) {
		TitleMenuButtonStyle titleStyle = ModMenuConfig.MODS_BUTTON_STYLE.getValue();
		GameMenuButtonStyle gameMenuStyle = ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue();
		boolean isIcon = title ? titleStyle == ModMenuConfig.TitleMenuButtonStyle.ICON : gameMenuStyle == ModMenuConfig.GameMenuButtonStyle.ICON;
		boolean isShort = title ? titleStyle == ModMenuConfig.TitleMenuButtonStyle.SHRINK : false;
		String modsText = TranslationStorage.getInstance().get("modmenu.title");
		if (ModMenuConfig.MOD_COUNT_LOCATION.getValue().isOnModsButton() && !isIcon) {
			String count = ModMenu.getDisplayedModCount();
			if (isShort) {
				modsText += " " + TranslationStorage.getInstance().get("modmenu.loaded.short", count);
			} else {
				String specificKey = "modmenu.loaded." + count;
				String key = TranslationUtil.hasTranslation(specificKey) ? specificKey : "modmenu.loaded";
				if (ModMenuConfig.EASTER_EGGS.getValue() && TranslationUtil.hasTranslation(specificKey + ".secret")) {
					key = specificKey + ".secret";
				}
				modsText += " " + TranslationStorage.getInstance().get(key, count);
			}
		}
		return modsText;
	}
}
