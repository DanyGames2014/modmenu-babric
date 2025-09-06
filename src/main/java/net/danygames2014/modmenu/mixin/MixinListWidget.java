package net.danygames2014.modmenu.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.danygames2014.modmenu.util.ListWidgetHelper;

@Mixin(EntryListWidget.class)
public class MixinListWidget implements ListWidgetHelper {

	@Inject(method = "clampScrolling", at = @At("HEAD"))
	private void modmenu$capScrolling(CallbackInfo ci) {
		this.doCapScrolling();
	}

	@Override
	public void doCapScrolling() {
	}
}
