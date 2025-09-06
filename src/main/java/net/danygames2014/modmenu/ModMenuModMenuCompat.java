package net.danygames2014.modmenu;

import com.google.common.collect.ImmutableMap;
import net.danygames2014.modmenu.api.ConfigScreenFactory;
import net.danygames2014.modmenu.api.ModMenuApi;
import net.danygames2014.modmenu.gui.ModMenuOptionsScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.option.OptionsScreen;

import java.util.Map;

public class ModMenuModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ModMenuOptionsScreen::new;
	}

	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ImmutableMap.of("minecraft", parent -> new OptionsScreen(parent, Minecraft.INSTANCE.options));
	}
}
