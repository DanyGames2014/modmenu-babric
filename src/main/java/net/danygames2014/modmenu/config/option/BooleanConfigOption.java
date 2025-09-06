package net.danygames2014.modmenu.config.option;

import net.danygames2014.modmenu.util.TranslationUtil;
import net.minecraft.client.resource.language.TranslationStorage;

public class BooleanConfigOption implements ConfigOption {
	private final String key, translationKey;
	private final boolean defaultValue;
	private final String enabledText;
	private final String disabledText;

	public BooleanConfigOption(String key, boolean defaultValue, String enabledKey, String disabledKey) {
		ConfigOptionStorage.setBoolean(key, defaultValue);
		this.key = key;
		this.translationKey = TranslationUtil.translationKeyOf("option", key);
		this.defaultValue = defaultValue;
		this.enabledText = TranslationStorage.getInstance().get(translationKey + "." + enabledKey);
		this.disabledText = TranslationStorage.getInstance().get(translationKey + "." + disabledKey);
	}

	public BooleanConfigOption(String key, boolean defaultValue) {
		this(key, defaultValue, "true", "false");
	}

	public String getKey() {
		return key;
	}

	public boolean getValue() {
		return ConfigOptionStorage.getBoolean(key);
	}

	public void setValue(boolean value) {
		ConfigOptionStorage.setBoolean(key, value);
	}

	public void toggleValue() {
		ConfigOptionStorage.toggleBoolean(key);
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getValueLabel() {
		return TranslationUtil.translateOptionLabel(TranslationStorage.getInstance().get(translationKey), getValue() ? enabledText : disabledText);
	}

	@Override
	public void click() {
		toggleValue();
	}
}
