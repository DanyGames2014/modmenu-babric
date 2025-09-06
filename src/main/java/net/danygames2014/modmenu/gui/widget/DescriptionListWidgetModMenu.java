package net.danygames2014.modmenu.gui.widget;

import net.danygames2014.modmenu.config.ModMenuConfig;
import net.danygames2014.modmenu.gui.ConfirmChatLinkScreen;
import net.danygames2014.modmenu.gui.ModsScreen;
import net.danygames2014.modmenu.gui.widget.entries.ModMenuEntryListWidget;
import net.danygames2014.modmenu.gui.widget.entries.ModListEntry;
import net.danygames2014.modmenu.util.GlUtil;
import net.danygames2014.modmenu.util.MathUtil;
import net.danygames2014.modmenu.util.ScreenUtil;
import net.danygames2014.modmenu.util.mod.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import net.minecraft.client.resource.language.TranslationStorage;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class DescriptionListWidgetModMenu extends ModMenuEntryListWidget {

	private static final String HAS_UPDATE_TEXT = TranslationStorage.getInstance().get("modmenu.hasUpdate");
	private static final String EXPERIMENTAL_TEXT = /*Formatting.GOLD +*/ TranslationStorage.getInstance().get("modmenu.experimental");
	private static final String DOWNLOAD_TEXT = "" + /*Formatting.BLUE + Formatting.UNDERLINE +*/ TranslationStorage.getInstance().get("modmenu.downloadLink");
	private static final String CHILD_HAS_UPDATE_TEXT = TranslationStorage.getInstance().get("modmenu.childHasUpdate");
	private static final String LINKS_TEXT = TranslationStorage.getInstance().get("modmenu.links");
	private static final String SOURCE_TEXT = "" + /*Formatting.BLUE + Formatting.UNDERLINE +*/ TranslationStorage.getInstance().get("modmenu.source");
	private static final String LICENSE_TEXT = TranslationStorage.getInstance().get("modmenu.license");
	private static final String VIEW_CREDITS_TEXT = "" + /*Formatting.BLUE + Formatting.UNDERLINE +*/ TranslationStorage.getInstance().get("modmenu.viewCredits");
	private static final String CREDITS_TEXT = TranslationStorage.getInstance().get("modmenu.credits");

	private final Minecraft minecraft;
	private final ModsScreen parent;
	private final TextRenderer textRenderer;
	private final List<DescriptionEntry> entries = new ArrayList<>();
	private ModListEntry lastSelected = null;

	public DescriptionListWidgetModMenu(Minecraft client, int width, int height, int top, int bottom, int entryHeight, ModsScreen parent) {
		super(client, width, height, top, bottom, entryHeight);
		this.minecraft = client;
		this.parent = parent;
		this.textRenderer = client.textRenderer;
	}

	@Override
	public int getRowWidth() {
		return this.width - 10;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.width - 6 + this.left;
	}

	public boolean isMouseInList(int mouseX, int mouseY) {
		return mouseY >= this.top && mouseY <= this.bottom && mouseX >= this.left && mouseX <= this.right;
	}

	@Override
	public int getEntryCount() {
		return this.entries.size();
	}

	public void clear() {
		this.entries.clear();
	}

	@Override
	public DescriptionEntry getEntry(int index) {
		return this.entries.get(index);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		clampScrolling();
		ModListEntry selectedEntry = parent.getSelectedEntry();
		if (selectedEntry != lastSelected) {
			lastSelected = selectedEntry;
			clear();
			// Prevent text jumping around
			// scroll(-Integer.MAX_VALUE);
			if (lastSelected != null) {
				DescriptionEntry emptyEntry = new DescriptionEntry("");
				int wrapWidth = getRowWidth() - 5;

				Mod mod = lastSelected.getMod();
				String description = mod.getTranslatedDescription();
				if (!description.isEmpty()) {
					for (Object line : textRenderer.split(description.replaceAll("\n", "\n\n"), wrapWidth)) {
						this.entries.add(new DescriptionEntry((String) line));
					}
				}

				Map<String, String> links = mod.getLinks();
				String sourceLink = mod.getSource();
				if ((!links.isEmpty() || sourceLink != null) && !ModMenuConfig.HIDE_MOD_LINKS.getValue()) {
					this.entries.add(emptyEntry);

					for (Object line : textRenderer.split(LINKS_TEXT, wrapWidth)) {
						this.entries.add(new DescriptionEntry((String) line));
					}

					if (sourceLink != null) {
						int indent = 8;
						for (Object line : textRenderer.split(SOURCE_TEXT, wrapWidth - 16)) {
							this.entries.add(new LinkEntry((String) line, sourceLink, indent));
							indent = 16;
						}
					}

					links.forEach((key, value) -> {
						int indent = 8;
						for (Object line : textRenderer.split("" + /*Formatting.BLUE + Formatting.UNDERLINE +*/ TranslationStorage.getInstance().get(key), wrapWidth - 16)) {
							this.entries.add(new LinkEntry((String) line, value, indent));
							indent = 16;
						}
					});
				}

				Set<String> licenses = mod.getLicense();
				if (!ModMenuConfig.HIDE_MOD_LICENSE.getValue() && !licenses.isEmpty()) {
					this.entries.add(emptyEntry);

					for (Object line : textRenderer.split(LICENSE_TEXT, wrapWidth)) {
						this.entries.add(new DescriptionEntry((String) line));
					}

					for (String license : licenses) {
						int indent = 8;
						for (Object line : textRenderer.split(license, wrapWidth - 16)) {
							this.entries.add(new DescriptionEntry((String) line, indent));
							indent = 16;
						}
					}
				}

				if (!ModMenuConfig.HIDE_MOD_CREDITS.getValue()) {
					if ("minecraft".equals(mod.getId())) {
						this.entries.add(emptyEntry);
					} else if (!"java".equals(mod.getId())) {
						SortedMap<String, Set<String>> credits = mod.getCredits();

						if (!credits.isEmpty()) {
							this.entries.add(emptyEntry);

							for (Object line : textRenderer.split(CREDITS_TEXT, wrapWidth)) {
								this.entries.add(new DescriptionEntry((String) line));
							}

							Iterator<Map.Entry<String, Set<String>>> iterator = credits.entrySet().iterator();

							while (iterator.hasNext()) {
								int indent = 8;

								Map.Entry<String, Set<String>> role = iterator.next();
								String roleName = role.getKey();

								for (Object line : textRenderer.split(this.creditsRoleText(roleName), wrapWidth - 16)) {
									this.entries.add(new DescriptionEntry((String) line, indent));
									indent = 16;
								}

								for (String contributor : role.getValue()) {
									indent = 16;

									for (Object line : textRenderer.split(contributor, wrapWidth - 24)) {
										this.entries.add(new DescriptionEntry((String) line, indent));
										indent = 24;
									}
								}

								if (iterator.hasNext()) {
									this.entries.add(emptyEntry);
								}
							}
						}
					}
				}
			}
		}

		Tessellator bufferBuilder = Tessellator.INSTANCE;

		{
			this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/gui/background.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			bufferBuilder.start(GL11.GL_QUADS);
			bufferBuilder.color(0x20, 0x20, 0x20);
            bufferBuilder.vertex(this.left, this.bottom, 0.0, (this.left / 32.0F), ((this.bottom + this.scrollAmount) / 32.0F));
            bufferBuilder.vertex(this.right, this.bottom, 0.0, (this.right / 32.0F), ((this.bottom + this.scrollAmount) / 32.0F));
            bufferBuilder.vertex(this.right, this.top, 0.0, (this.right / 32.0F), ((this.top + this.scrollAmount) / 32.0F));
            bufferBuilder.vertex(this.left, this.top, 0.0, (this.left / 32.0F), ((this.top + this.scrollAmount) / 32.0F));
			bufferBuilder.draw();
		}

		int listX = this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
		int listY = this.top + 4 - (int)this.scrollAmount;
		this.renderList(listX, listY, mouseX, mouseY);

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GlUtil.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		bufferBuilder.start(GL11.GL_QUADS);
		bufferBuilder.color(0, 0, 0, 0);
		bufferBuilder.vertex(this.left, this.top + 4, 0.0, 0.0, 1.0);
		bufferBuilder.vertex(this.right, this.top + 4, 0.0, 1.0, 1.0);
		bufferBuilder.color(0, 0, 0, 255);
		bufferBuilder.vertex(this.right, this.top, 0.0, 1.0, 0.0);
		bufferBuilder.vertex(this.left, this.top, 0.0, 0.0, 0.0);
		bufferBuilder.draw();
		bufferBuilder.start(GL11.GL_QUADS);
		bufferBuilder.color(0, 0, 0, 255);
		bufferBuilder.vertex(this.left, this.bottom, 0.0, 0.0, 1.0);
		bufferBuilder.vertex(this.right, this.bottom, 0.0, 1.0, 1.0);
		bufferBuilder.color(0, 0, 0, 0);
		bufferBuilder.vertex(this.right, this.bottom - 4, 0.0, 1.0, 0.0);
		bufferBuilder.vertex(this.left, this.bottom - 4, 0.0, 0.0, 0.0);
		bufferBuilder.draw();

		this.renderScrollBar(bufferBuilder);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void handleMouse() {
		int size = this.getEntryCount();
		int scrollbarMinX = this.getScrollbarPosition();
		int scrollbarMaxX = scrollbarMinX + 6;
		if (mouseX > this.left && mouseX < this.right && mouseY > this.top && mouseY < this.bottom) {
			if (Mouse.isButtonDown(0)) {
				if (this.mostYStart == -1.0f) {
					int mouseClickMode = 1;
					if (mouseY >= this.top && mouseY <= this.bottom) {
						int rowMinX = this.width / 2 - this.getRowWidth() / 2;
						int rowMaxX = this.width / 2 + this.getRowWidth() / 2;
						int selectedY = mouseY - this.top - this.headerHeight + (int) this.scrollAmount - 4;
						int selectedPos = selectedY / this.itemHeight;
						if (mouseX >= rowMinX && mouseX <= rowMaxX && selectedPos >= 0 && selectedY >= 0 && selectedPos < size) {
							int selectedIndex = selectedPos == this.pos && MathUtil.getTime() - this.time < 250L ? 1 : 0;
							this.entryClicked(selectedPos, selectedIndex != 0);
							this.pos = selectedPos;
							this.time = MathUtil.getTime();
						} else if (mouseX >= rowMinX && mouseX <= rowMaxX && selectedY < 0) {
							this.headerClicked(mouseX - rowMinX, mouseY - this.top + (int) this.scrollAmount - 4);
							mouseClickMode = 0;
						}
						if (mouseX >= scrollbarMinX && mouseX <= scrollbarMaxX) {
							this.scrollSpeedMultiplier = -1.0f;
							int maxScroll = this.getMaxScroll();
							if (maxScroll < 1) {
								maxScroll = 1;
							}
							int heightForScrolling;
							if ((heightForScrolling = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getEntriesHeight())) < 32) {
								heightForScrolling = 32;
							}
							if (heightForScrolling > this.bottom - this.top - 8) {
								heightForScrolling = this.bottom - this.top - 8;
							}
							this.scrollSpeedMultiplier /= (float) (this.bottom - this.top - heightForScrolling) / (float) maxScroll;
						} else {
							this.scrollSpeedMultiplier = 1.0f;
						}
						this.mostYStart = mouseClickMode != 0 ? (float) mouseY : -2.0f;
					} else {
						this.mostYStart = -2.0f;
					}
				} else if (this.mostYStart >= 0.0f) {
					this.scrollAmount -= ((float) mouseY - this.mostYStart) * this.scrollSpeedMultiplier;
					this.mostYStart = mouseY;
				}
			} else {
				int dwheel = Mouse.getEventDWheel();
				if (dwheel != 0) {
					if (dwheel > 0) {
						dwheel = -1;
					} else {
						dwheel = 1;
					}
					this.scrollAmount += dwheel * this.itemHeight;
				}

				this.mostYStart = -1.0f;
			}
		}
		this.clampScrolling();
	}

	@Override
	protected void renderEntry(int index, int x, int y, int height, Tessellator bufferBuilder) {
		if (y >= this.top && y + height <= this.bottom) {
			super.renderEntry(index, x, y, height, bufferBuilder);
		}
	}

	public void renderScrollBar(Tessellator bufferBuilder) {
		int scrollbarStartX = this.getScrollbarPosition();
		int scrollbarEndX = scrollbarStartX + 6;
		int maxScroll = this.getMaxScroll();
		if (maxScroll > 0) {
			int p = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxScroll());
			p = MathUtil.clamp(p, 32, this.bottom - this.top - 8);
			int q = (int) this.getScrollAmount() * (this.bottom - this.top - p) / maxScroll + this.top;
			if (q < this.top) {
				q = this.top;
			}

			bufferBuilder.start(GL11.GL_QUADS);
			bufferBuilder.color(0, 0, 0, 0xFF);
			bufferBuilder.vertex(scrollbarStartX, this.bottom, 0.0, 0.0, 1.0);
			bufferBuilder.vertex(scrollbarEndX, this.bottom, 0.0, 1.0, 1.0);
			bufferBuilder.vertex(scrollbarEndX, this.top, 0.0, 1.0, 0.0);
			bufferBuilder.vertex(scrollbarStartX, this.top, 0.0, 0.0, 0.0);
			bufferBuilder.draw();
			bufferBuilder.start(GL11.GL_QUADS);
			bufferBuilder.color(0x80, 0x80, 0x80, 0xFF);
			bufferBuilder.vertex(scrollbarStartX, q + p, 0.0, 0.0, 1.0);
			bufferBuilder.vertex(scrollbarEndX, q + p, 0.0, 1.0, 1.0);
			bufferBuilder.vertex(scrollbarEndX, q, 0.0, 1.0, 0.0);
			bufferBuilder.vertex(scrollbarStartX, q, 0.0, 0.0, 0.0);
			bufferBuilder.draw();
			bufferBuilder.start(GL11.GL_QUADS);
			bufferBuilder.color(0xC0, 0xC0, 0xC0, 0xFF);
			bufferBuilder.vertex(scrollbarStartX, q + p - 1, 0.0, 0.0, 1.0);
			bufferBuilder.vertex(scrollbarEndX - 1, q + p - 1, 0.0, 1.0, 1.0);
			bufferBuilder.vertex(scrollbarEndX - 1, q, 0.0, 1.0, 0.0);
			bufferBuilder.vertex(scrollbarStartX, q, 0.0, 0.0, 0.0);
			bufferBuilder.draw();
		}
	}

	public void confirmResult(boolean result, int id) {
		if (result) {
			int index = id - ModsScreen.MODS_LIST_CONFIRM_ID_OFFSET;
			List<DescriptionEntry> entries = this.entries;

			if (index >= 0 && index < entries.size()) {
				DescriptionEntry entry = entries.get(index);

				if (entry instanceof LinkEntry) {
					String link = ((LinkEntry) entry).link;
					ScreenUtil.openLink(parent, link, parent.getSelectedEntry().mod.getId() + "/link");
				}
			}
		}

		minecraft.setScreen(this.parent);
	}

	private String creditsRoleText(String roleName) {
		// Replace spaces and dashes in role names with underscores if they exist
		// Notably Quilted Fabric API does this with FabricMC as "Upstream Owner"
		String translationKey = roleName.replaceAll("[\\s-]", "_").toLowerCase();

		return TranslationStorage.getInstance().get("modmenu.credits.role." + translationKey) + ":";
	}

	protected class DescriptionEntry implements ModMenuEntryListWidget.Entry {
		protected String text;
		protected int indent;
		public boolean updateTextEntry = false;

		public DescriptionEntry(String text, int indent) {
			this.text = text;
			this.indent = indent;
		}

		public DescriptionEntry(String text) {
			this(text, 0);
		}

		public DescriptionEntry setUpdateTextEntry() {
			this.updateTextEntry = true;
			return this;
		}

		@Override
		public void render(int index, int x, int y, int width, int height, Tessellator bufferBuilder, int mouseX, int mouseY, boolean hovered) {
			textRenderer.drawWithShadow(text, x + indent, y, 0xAAAAAA);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
			return false;
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
		}
	}

	protected class LinkEntry extends DescriptionEntry {
		private final String link;

		public LinkEntry(String text, String link, int indent) {
			super(text, indent);
			this.link = link;
		}

		public LinkEntry(String text, String link) {
			this(text, link, 0);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
			if (isMouseInList(mouseX, mouseY)) {
				minecraft.setScreen(new ConfirmChatLinkScreen(DescriptionListWidgetModMenu.this.parent, link, ModsScreen.MODS_LIST_CONFIRM_ID_OFFSET + index) {

					@Override
					public void copy() {
					}
				});
			}
			return super.mouseClicked(index, mouseX, mouseY, button, entryMouseX, entryMouseY);
		}
	}

}
