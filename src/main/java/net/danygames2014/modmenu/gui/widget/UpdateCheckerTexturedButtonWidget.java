package net.danygames2014.modmenu.gui.widget;

import net.minecraft.client.Minecraft;

public class UpdateCheckerTexturedButtonWidget extends TexturedButtonWidget {
	public UpdateCheckerTexturedButtonWidget(int id, int x, int y, int width, int height, int u, int v, int hoveredVOffset, String texture, int textureWidth, int textureHeight) {
		super(id, x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight);
	}

	@Override
	public void render(Minecraft minecraft, int mouseX, int mouseY) {
		super.render(minecraft, mouseX, mouseY);
	}
}
