package io.github.prospector.modmenu.gui;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

public abstract class ConfirmChatLinkScreen extends ConfirmScreen {

	private String confirmText;
	private String abortText;
	private String warning;
	private String copy;

	public ConfirmChatLinkScreen(Screen parent, String chatLink, int id) {
		super(parent, TranslationStorage.getInstance().get("chat.link.confirm"), chatLink, "", "", id);
		TranslationStorage languageManager = TranslationStorage.getInstance();
		this.confirmText = languageManager.get("gui.yes");
		this.abortText = languageManager.get("gui.no");
		this.copy = languageManager.get("chat.copy");
		this.warning = languageManager.get("chat.link.warning");
	}

	public void init() {
		this.buttons.add(new ButtonWidget(0, this.width / 3 - 83 + 0, this.height / 6 + 96, 100, 20, this.confirmText));
		this.buttons.add(new ButtonWidget(2, this.width / 3 - 83 + 105, this.height / 6 + 96, 100, 20, this.copy));
		this.buttons.add(new ButtonWidget(1, this.width / 3 - 83 + 210, this.height / 6 + 96, 100, 20, this.abortText));
	}

	protected void buttonClicked(ButtonWidget button) {
		if (button.id == 2) {
			this.copy();
			super.buttonClicked((ButtonWidget) this.buttons.get(1));
		} else {
			super.buttonClicked(button);
		}
	}

	public abstract void copy();

	public void render(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
		this.drawCenteredTextWithShadow(this.textRenderer, this.warning, this.width / 2, 110, 0xFFCCCC);
	}
}
