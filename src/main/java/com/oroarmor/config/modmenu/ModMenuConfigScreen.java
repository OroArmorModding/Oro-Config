package com.oroarmor.config.modmenu;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;

public class ModMenuConfigScreen implements ModMenuApi {

	private final Config config;

	public ModMenuConfigScreen(Config config) {
		this.config = config;
	}

	private ConfigCategory createCategory(ConfigBuilder builder, String categoryName) {
		return builder.getOrCreateCategory(new TranslatableText(categoryName));
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> {
			ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen).setTitle(new TranslatableText("config.netherite_plus"));
			builder.setSavingRunnable(config::saveConfigToFile);

			ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

			config.getConfigs().forEach(group -> {
				ConfigCategory groupCategory = createCategory(builder, "config." + config.getID() + "." + group.getName());
				group.getConfigs().forEach(configItem -> {
					setupConfigItem(configItem, groupCategory, entryBuilder);
				});
			});

			return builder.build();
		};
	}

	private void setupBooleanConfigItem(ConfigItem<Boolean> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText(ci.getDetails()), ci.getValue()).setSaveConsumer(ci::setValue).setDefaultValue(ci::getDefaultValue).build());
	}

	private void setupDoubleConfigItem(ConfigItem<Double> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		category.addEntry(entryBuilder.startDoubleField(new TranslatableText(ci.getDetails()), ci.getValue()).setSaveConsumer(ci::setValue).setDefaultValue(ci::getDefaultValue).build());
	}

	private void setupIntegerConfigItem(ConfigItem<Integer> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		category.addEntry(entryBuilder.startIntField(new TranslatableText(ci.getDetails()), ci.getValue()).setSaveConsumer(ci::setValue).setDefaultValue(ci::getDefaultValue).build());
	}

	@SuppressWarnings("unchecked")
	private void setupConfigItem(ConfigItem<?> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
		switch (ci.getType()) {
		case BOOLEAN:
			setupBooleanConfigItem((ConfigItem<Boolean>) ci, category, entryBuilder);
			break;
		case DOUBLE:
			setupDoubleConfigItem((ConfigItem<Double>) ci, category, entryBuilder);
			break;
		case GROUP:
			break;
		case INTEGER:
			setupIntegerConfigItem((ConfigItem<Integer>) ci, category, entryBuilder);
			break;
		case STRING:
			break;
		default:
			break;
		}
	}

}
