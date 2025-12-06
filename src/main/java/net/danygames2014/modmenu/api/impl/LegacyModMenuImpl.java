package net.danygames2014.modmenu.api.impl;

import net.danygames2014.modmenu.api.ConfigScreenFactory;
import net.danygames2014.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

/* Made by Farn (farnfarn02) */

public class LegacyModMenuImpl implements ModMenuApi {
    private io.github.prospector.modmenu.api.ModMenuApi ogApi;

    public LegacyModMenuImpl(io.github.prospector.modmenu.api.ModMenuApi ogApi) {
        this.ogApi = ogApi;
    }

    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return new ConfigScreenFactory<Screen>() {
            @Override
            public Screen create(Screen parent) {
                return ogApi.getConfigScreenFactory().apply(parent);
            }
        };
    }
}
