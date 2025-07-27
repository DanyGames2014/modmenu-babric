package io.github.prospector.modmenu.gui.widget.entries;

import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import io.github.prospector.modmenu.util.GlUtil;
import io.github.prospector.modmenu.util.ListWidgetHelper;
import io.github.prospector.modmenu.util.MathUtil;

import net.minecraft.client.Minecraft;

public abstract class ModMenuEntryListWidget extends EntryListWidget implements ListWidgetHelper {

	protected int mouseX;
	protected int mouseY;
	protected double scrollAmount;
	protected boolean scrolling;

	public ModMenuEntryListWidget(Minecraft minecraft, int i, int j, int k, int l, int m) {
		super(minecraft, i, j, k, l, m);
	}

	@Override
	protected void entryClicked(int index, boolean doubleClick) {
	}

	@Override
	protected boolean isSelectedEntry(int index) {
		return false;
	}

	@Override
	protected void renderBackground() {
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		int n;
		int n2;
		int n3;
		int n4;
		int n5;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.renderBackground();
		int n6 = this.getEntryCount();
		int n7 = this.getScrollbarPosition();
		int n8 = n7 + 6;
		if (mouseX > this.left && mouseX < this.right && mouseY > this.top && mouseY < this.bottom) {
			int n9;
			if (Mouse.isButtonDown(0) && this.isScrolling()) {
				if (this.mostYStart == -1.0f) {
					n9 = 1;
					if (mouseY >= this.top && mouseY <= this.bottom) {
						int n10 = this.width / 2 - this.getRowWidth() / 2;
						n5 = this.width / 2 + this.getRowWidth() / 2;
						n4 = mouseY - this.top - this.headerHeight + (int) this.scrollAmount - 4;
						n3 = n4 / this.itemHeight;
						if (mouseX >= n10 && mouseX <= n5 && n3 >= 0 && n4 >= 0 && n3 < n6) {
							n2 = n3 == this.pos && MathUtil.getTime() - this.time < 250L ? 1 : 0;
							this.entryClicked(n3, n2 != 0);
							this.pos = n3;
							this.time = MathUtil.getTime();
						} else if (mouseX >= n10 && mouseX <= n5 && n4 < 0) {
							this.headerClicked(mouseX - n10, mouseY - this.top + (int) this.scrollAmount - 4);
							n9 = 0;
						}
						if (mouseX >= n7 && mouseX <= n8) {
							this.scrollSpeedMultiplier = -1.0f;
							n2 = this.getMaxScroll();
							if (n2 < 1) {
								n2 = 1;
							}
							if ((n = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top))
									/ (float) this.getEntriesHeight())) < 32) {
								n = 32;
							}
							if (n > this.bottom - this.top - 8) {
								n = this.bottom - this.top - 8;
							}
							this.scrollSpeedMultiplier /= (float) (this.bottom - this.top - n) / (float) n2;
						} else {
							this.scrollSpeedMultiplier = 1.0f;
						}
						this.mostYStart = n9 != 0 ? (float) mouseY : -2.0f;
					} else {
						this.mostYStart = -2.0f;
					}
				} else if (this.mostYStart >= 0.0f) {
					super.scrollAmount = (float) (this.scrollAmount -= ((float) mouseY - this.mostYStart) * this.scrollSpeedMultiplier);
					this.mostYStart = mouseY;
				}
			} else {
				while (/*!this.minecraft.options.touchScreen &&*/ Mouse.next()) {
					n9 = Mouse.getEventDWheel();
					if (n9 != 0) {
						if (n9 > 0) {
							n9 = -1;
						} else if (n9 < 0) {
							n9 = 1;
						}
						super.scrollAmount = (float) (this.scrollAmount += (float) (n9 * this.itemHeight / 2));
					}
					this.minecraft.currentScreen.onMouseEvent();
				}
				this.mostYStart = -1.0f;
			}
		}
		this.clampScrolling();
		GL11.glDisable(2896);
		GL11.glDisable(2912);
		Tessellator bufferBuilder = Tessellator.INSTANCE;
		this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/gui/background.png"));
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		float f = 32.0f;
		bufferBuilder.startQuads();
		bufferBuilder.color(0x202020);
		bufferBuilder.vertex(this.left, this.bottom, 0.0, (float) this.left / f,
				(float) (this.bottom + (int) this.scrollAmount) / f);
		bufferBuilder.vertex(this.right, this.bottom, 0.0, (float) this.right / f,
				(float) (this.bottom + (int) this.scrollAmount) / f);
		bufferBuilder.vertex(this.right, this.top, 0.0, (float) this.right / f,
				(float) (this.top + (int) this.scrollAmount) / f);
		bufferBuilder.vertex(this.left, this.top, 0.0, (float) this.left / f,
				(float) (this.top + (int) this.scrollAmount) / f);
		bufferBuilder.draw();
		n5 = this.left + (this.width / 2 - this.getRowWidth() / 2 + 2);
		n4 = this.top + 4 - (int) this.scrollAmount;
		if (this.renderHeader) {
			this.renderHeader(n5, n4, bufferBuilder);
		}
		this.renderList(n5, n4, mouseX, mouseY);
		GL11.glDisable(2929);
		n3 = 4;
		this.renderHoleBackground(0, this.top, 255, 255);
		this.renderHoleBackground(this.bottom, this.height, 255, 255);
		GL11.glEnable(3042);
		GlUtil.blendFuncSeparate(770, 771, 0, 1);
		GL11.glDisable(3008);
		GL11.glShadeModel(7425);
		GL11.glDisable(3553);
		bufferBuilder.startQuads();
		bufferBuilder.color(0, 0);
		bufferBuilder.vertex(this.left, this.top + n3, 0.0, 0.0, 1.0);
		bufferBuilder.vertex(this.right, this.top + n3, 0.0, 1.0, 1.0);
		bufferBuilder.color(0, 255);
		bufferBuilder.vertex(this.right, this.top, 0.0, 1.0, 0.0);
		bufferBuilder.vertex(this.left, this.top, 0.0, 0.0, 0.0);
		bufferBuilder.draw();
		bufferBuilder.startQuads();
		bufferBuilder.color(0, 255);
		bufferBuilder.vertex(this.left, this.bottom, 0.0, 0.0, 1.0);
		bufferBuilder.vertex(this.right, this.bottom, 0.0, 1.0, 1.0);
		bufferBuilder.color(0, 0);
		bufferBuilder.vertex(this.right, this.bottom - n3, 0.0, 1.0, 0.0);
		bufferBuilder.vertex(this.left, this.bottom - n3, 0.0, 0.0, 0.0);
		bufferBuilder.draw();
		n2 = this.getMaxScroll();
		if (n2 > 0 && this.getEntriesHeight() > 0) {
			int n11;
			n = (this.bottom - this.top) * (this.bottom - this.top) / this.getEntriesHeight();
			if (n < 32) {
				n = 32;
			}
			if (n > this.bottom - this.top - 8) {
				n = this.bottom - this.top - 8;
			}
			if ((n11 = (int) this.scrollAmount * (this.bottom - this.top - n) / n2 + this.top) < this.top) {
				n11 = this.top;
			}
			bufferBuilder.startQuads();
			bufferBuilder.color(0, 255);
			bufferBuilder.vertex(n7, this.bottom, 0.0, 0.0, 1.0);
			bufferBuilder.vertex(n8, this.bottom, 0.0, 1.0, 1.0);
			bufferBuilder.vertex(n8, this.top, 0.0, 1.0, 0.0);
			bufferBuilder.vertex(n7, this.top, 0.0, 0.0, 0.0);
			bufferBuilder.draw();
			bufferBuilder.startQuads();
			bufferBuilder.color(0x808080, 255);
			bufferBuilder.vertex(n7, n11 + n, 0.0, 0.0, 1.0);
			bufferBuilder.vertex(n8, n11 + n, 0.0, 1.0, 1.0);
			bufferBuilder.vertex(n8, n11, 0.0, 1.0, 0.0);
			bufferBuilder.vertex(n7, n11, 0.0, 0.0, 0.0);
			bufferBuilder.draw();
			bufferBuilder.startQuads();
			bufferBuilder.color(0xC0C0C0, 255);
			bufferBuilder.vertex(n7, n11 + n - 1, 0.0, 0.0, 1.0);
			bufferBuilder.vertex(n8 - 1, n11 + n - 1, 0.0, 1.0, 1.0);
			bufferBuilder.vertex(n8 - 1, n11, 0.0, 1.0, 0.0);
			bufferBuilder.vertex(n7, n11, 0.0, 0.0, 0.0);
			bufferBuilder.draw();
		}
		this.renderDecorations(mouseX, mouseY);
		GL11.glEnable(3553);
		GL11.glShadeModel(7424);
		GL11.glEnable(3008);
		GL11.glDisable(3042);
	}

	protected void renderList(int x, int y, int mouseX, int mouseY) {
		int size = this.getEntryCount();
		Tessellator bufferBuilder = Tessellator.INSTANCE;
		for (int i = 0; i < size; ++i) {
			int entryY = y + i * this.itemHeight + this.headerHeight;
			int itemHeight = this.itemHeight - 4;
			if (entryY > this.bottom || entryY + itemHeight < this.top)
				continue;
			if (this.renderSelectionHighlight && this.isSelectedEntry(i)) {
				int n4 = this.left + (this.width / 2 - this.getRowWidth() / 2);
				int n5 = this.left + (this.width / 2 + this.getRowWidth() / 2);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glDisable(3553);
				bufferBuilder.startQuads();
				bufferBuilder.color(0x808080);
				bufferBuilder.vertex(n4, entryY + itemHeight + 2, 0.0, 0.0, 1.0);
				bufferBuilder.vertex(n5, entryY + itemHeight + 2, 0.0, 1.0, 1.0);
				bufferBuilder.vertex(n5, entryY - 2, 0.0, 1.0, 0.0);
				bufferBuilder.vertex(n4, entryY - 2, 0.0, 0.0, 0.0);
				bufferBuilder.color(0);
				bufferBuilder.vertex(n4 + 1, entryY + itemHeight + 1, 0.0, 0.0, 1.0);
				bufferBuilder.vertex(n5 - 1, entryY + itemHeight + 1, 0.0, 1.0, 1.0);
				bufferBuilder.vertex(n5 - 1, entryY - 1, 0.0, 1.0, 0.0);
				bufferBuilder.vertex(n4 + 1, entryY - 1, 0.0, 0.0, 0.0);
				bufferBuilder.draw();
				GL11.glEnable(3553);
			}
			this.renderEntry(i, x, entryY, itemHeight, bufferBuilder);
		}
	}

	private void renderHoleBackground(int top, int bottom, int topAlpha, int bottomAlpha) {
		Tessellator bufferBuilder = Tessellator.INSTANCE;
		Minecraft.INSTANCE.textureManager.bindTexture(Minecraft.INSTANCE.textureManager.getTextureId("/gui/background.png"));
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		float f = 32.0f;
		bufferBuilder.startQuads();
		bufferBuilder.color(0x404040, bottomAlpha);
		bufferBuilder.vertex(this.left, bottom, 0.0, 0.0, (float) bottom / f);
		bufferBuilder.vertex(this.left + this.width, bottom, 0.0, (float) this.width / f, (float) bottom / f);
		bufferBuilder.color(0x404040, topAlpha);
		bufferBuilder.vertex(this.left + this.width, top, 0.0, (float) this.width / f, (float) top / f);
		bufferBuilder.vertex(this.left, top, 0.0, 0.0, (float) top / f);
		bufferBuilder.draw();
	}

	@Override
	protected void renderEntry(int index, int x, int y, int itemHeight, Tessellator bufferBuilder) {
		this.getEntry(index).render(index, x, y, this.getRowWidth(), itemHeight, bufferBuilder, mouseX, mouseY, this.getEntryAt(mouseX, mouseY) == index);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		int n;
		if (this.isMouseInList(mouseY) && (n = this.getEntryAt(mouseX, mouseY)) >= 0) {
			int n2 = this.left + (this.width / 2 - this.getRowWidth() / 2 + 2);
			int n3 = this.top + 4 - this.getScrollAmount() + (n * this.itemHeight + this.headerHeight);
			int n4 = mouseX - n2;
			int n5 = mouseY - n3;
			if (this.getEntry(n).mouseClicked(n, mouseX, mouseY, button, n4, n5)) {
				this.setScrolling(false);
				return true;
			}
		}
		return false;
	}

	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		for (int i = 0; i < this.getEntryCount(); ++i) {
			int n = this.left + (this.width / 2 - this.getRowWidth() / 2 + 2);
			int n2 = this.top + 4 - this.getScrollAmount() + (i * this.itemHeight + this.headerHeight);
			int n3 = mouseX - n;
			int n4 = mouseY - n2;
			this.getEntry(i).mouseReleased(i, mouseX, mouseY, button, n3, n4);
		}
		this.setScrolling(true);
		return false;
	}

	public abstract Entry getEntry(int var1);

	public int getScrollAmount() {
		return (int) this.scrollAmount;
	}

	protected void scroll(int i) {
		this.setScrollAmount(this.scrollAmount + (double)i);
		this.mostYStart = -2.0F;
	}

	public void setScrollAmount(double amount) {
		if (amount < 0) {
			amount = 0;
		}
		if (amount > getMaxScroll()) {
			amount = getMaxScroll();
		}
		super.scrollAmount = (float) (this.scrollAmount = amount);
	}

	@Override
	public void doCapScrolling() {
		int max = this.getEntriesHeight() - (this.bottom - this.top - 4);
		if (max < 0) {
			max /= 2;
		}
		if (this.scrollAmount < 0.0F) {
			super.scrollAmount = (float) (this.scrollAmount = 0.0F);
		}
		if (this.scrollAmount > max) {
			super.scrollAmount = (float) (this.scrollAmount = max);
		}
	}

	protected int getMaxScroll() {
		return Math.max(0, this.getEntriesHeight() - (this.bottom - this.top - 4));
	}

	public boolean isMouseInList(int mouseY) {
		return mouseY >= this.top && mouseY <= this.bottom;
	}

	public void setScrolling(boolean scrolling) {
		this.scrolling = scrolling;
	}

	public boolean isScrolling() {
		return this.scrolling;
	}

	protected int getScrollbarPosition() {
		return this.width / 2 + 124;
	}

	public void setX(int x) {
		this.left = x;
		this.right = x + this.width;
	}

	public int getRowWidth() {
		return 220;
	}

	public static interface Entry {

		void render(int var1, int var2, int var3, int var4, int var5, Tessellator var6, int var7, int var8, boolean var9);

		boolean mouseClicked(int var1, int var2, int var3, int var4, int var5, int var6);

		void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6);

	}
}
