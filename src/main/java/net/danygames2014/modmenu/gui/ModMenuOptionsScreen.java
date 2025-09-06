package net.danygames2014.modmenu.gui;

import net.danygames2014.modmenu.config.ModMenuConfig;
import net.danygames2014.modmenu.config.ModMenuConfigManager;
import net.danygames2014.modmenu.gui.widget.ConfigOptionListWidgetModMenu;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

public class ModMenuOptionsScreen extends Screen {

	private static final int DONE = 0;

	private Screen previous;
	private String title;
	private ConfigOptionListWidgetModMenu list;
	private int mouseX;
	private int mouseY;

	public ModMenuOptionsScreen(Screen previous) {
		this.previous = previous;
		this.title = TranslationStorage.getInstance().get("modmenu.options");
	}

	@Override
	public void init() {
		this.list = new ConfigOptionListWidgetModMenu(this.minecraft, this.width, this.height, 32, this.height - 32, 25, ModMenuConfig.asOptions());
		this.buttons.add(new ButtonWidget(DONE, this.width / 2 - 100, this.height - 27, 200, 20, TranslationStorage.getInstance().get("gui.done")));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.renderBackground();
		this.list.render(mouseX, mouseY, delta);
		this.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xffffff);
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public void onMouseEvent() {
		super.onMouseEvent();
		if (this.list.isMouseInList(mouseX, mouseY)) {
	//		this.list.handleMouse();
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		this.list.mouseClicked(mouseX, mouseY, button);
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void buttonClicked(ButtonWidget button) {
		switch (button.id) {
		case DONE:
			ModMenuConfigManager.save();
			ModMenuOptionsScreen.this.minecraft.setScreen(ModMenuOptionsScreen.this.previous);
			break;
		}
	}

	@Override
	public void removed() {
		ModMenuConfigManager.save();
	}
}
