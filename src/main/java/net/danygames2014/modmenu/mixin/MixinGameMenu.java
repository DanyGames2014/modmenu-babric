package net.danygames2014.modmenu.mixin;

import net.danygames2014.modmenu.ModMenu;
import net.danygames2014.modmenu.api.ModMenuApi;
import net.danygames2014.modmenu.config.ModMenuConfig;
import net.danygames2014.modmenu.gui.ModsScreen;
import net.danygames2014.modmenu.gui.widget.ModMenuButtonWidget;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenu extends Screen {
	/** button id for Options... button */
	private static final int OPTIONS = 0;
	/** button id for gui.achievements button */
	private static final int ACHIEVEMENTS = 5;
	/** button id for gui.stats button */
	private static final int STATS = 6;
	/** button id for modmenu.title button */
	private static final int MODS = 69;
	private static final String FABRIC_ICON_BUTTON_LOCATION = "/assets/" + ModMenu.MOD_ID + "/textures/gui/mods_button.png";
	@Inject(method = "init", at = @At(value = "TAIL"))
	private void onInit(CallbackInfo ci) {
		if (ModMenuConfig.MODIFY_GAME_MENU.getValue()) {
			final ModMenuConfig.GameMenuButtonStyle style = ModMenuConfig.GAME_MENU_BUTTON_STYLE.getValue();
			final int spacing = 4;
			int modsButtonIndex = -1;
			int modsButtonX = -1;
			int modsButtonY = -1;
			int modsButtonWidth = -1;
			int modsButtonHeight = 20;
			for (int i = 0; i < this.buttons.size(); i++) {
				final ButtonWidget button = (ButtonWidget) this.buttons.get(i);
				if (style == ModMenuConfig.GameMenuButtonStyle.BELOW_ACHIEVEMENTS && button.id == ACHIEVEMENTS) {
					modsButtonX = button.x;
					modsButtonWidth = button.width;
				}
				if (style == ModMenuConfig.GameMenuButtonStyle.BELOW_STATISTICS && button.id == STATS) {
					modsButtonX = button.x;
					modsButtonWidth = button.width;
				}
				if (style == ModMenuConfig.GameMenuButtonStyle.BELOW_ACHIEVEMENTS_AND_STATISTICS && button.id == ACHIEVEMENTS) {
					modsButtonX = button.x;
					modsButtonWidth = 2 * button.width + spacing;
				}
				if (style == ModMenuConfig.GameMenuButtonStyle.ICON && button.id == STATS) {
					modsButtonX = button.x + (button.width + spacing);
					modsButtonWidth = modsButtonHeight;
				}
				if (button.id == OPTIONS) {
					modsButtonIndex = i + 1;
					if (style == ModMenuConfig.GameMenuButtonStyle.ICON) {
						modsButtonY = button.y;
					} else {
						modsButtonY = button.y - spacing - modsButtonHeight;
					}
				}
			}
			if (modsButtonIndex != -1) {
				if (style == ModMenuConfig.GameMenuButtonStyle.ICON) {
				} else {
					this.buttons.add(new ModMenuButtonWidget(MODS, modsButtonX, modsButtonY, modsButtonWidth, modsButtonHeight, ModMenuApi.createModsButtonText()));
				}
			}
		}
	}

	@Inject(method = "buttonClicked", at = @At(value = "HEAD"))
	private void onButtonClicked(ButtonWidget button, CallbackInfo ci) {
		if (button.id == MODS) {
			this.minecraft.setScreen(new ModsScreen(this));
		}
	}
}
