package com.oroarmor.config.screen;

import java.util.List;
import java.util.stream.Collectors;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;

import net.minecraft.text.TranslatableText;

/**
 * This class allows for the easy addition of a Mod Menu config screen to your
 * mod. The abstract modifier is so that your {@link ConfigScreen} can be
 * used as a entry point for modmenu, as you need to set the config in the
 * constructor for this to work. <br>
 * <br>
 * Add this to your entrypoint list in {@code fabric.mod.json}: <br>
 * <code>
 * "modmenu" : [ <br>
 * &emsp;"your.package.structure.YourModMenuConfigScreen" <br>
 * ]
 * </code>
 *
 * @author Eli Orona
 */
public abstract class ConfigScreen {

    /**
     * The config for the screen
     */
    protected final Config config;

    /**
     * Creates a new {@link ConfigScreen}
     *
     * @param config The config
     */
    public ConfigScreen(Config config) {
        this.config = config;
    }

    protected ConfigCategory createCategory(ConfigBuilder builder, String categoryName) {
        return builder.getOrCreateCategory(new TranslatableText(categoryName));
    }


    private void setupBooleanConfigItem(ConfigItem<Boolean> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createBooleanConfigItem(ci, entryBuilder));
    }

    private void setupDoubleConfigItem(ConfigItem<Double> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createDoubleConfigItem(ci, entryBuilder));
    }

    private void setupIntegerConfigItem(ConfigItem<Integer> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createIntegerConfigItem(ci, entryBuilder));
    }

    private void setupStringConfigItem(ConfigItem<String> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createStringConfigItem(ci, entryBuilder));
    }

    protected void setupEnumConfigItem(ConfigItem<Enum<?>> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createEnumConfigItem(ci, entryBuilder));
    }

    private AbstractConfigListEntry<?> createBooleanConfigItem(ConfigItem<Boolean> ci, ConfigEntryBuilder entryBuilder) {
        return entryBuilder.startBooleanToggle(new TranslatableText(ci.getDetails()), ci.getValue()).setSaveConsumer(ci::setValue).setDefaultValue(ci::getDefaultValue).build();
    }

    private AbstractConfigListEntry<?> createDoubleConfigItem(ConfigItem<Double> ci, ConfigEntryBuilder entryBuilder) {
        return entryBuilder.startDoubleField(new TranslatableText(ci.getDetails()), ci.getValue()).setSaveConsumer(ci::setValue).setDefaultValue(ci::getDefaultValue).build();
    }

    private AbstractConfigListEntry<?> createIntegerConfigItem(ConfigItem<Integer> ci, ConfigEntryBuilder entryBuilder) {
        return entryBuilder.startIntField(new TranslatableText(ci.getDetails()), ci.getValue()).setSaveConsumer(ci::setValue).setDefaultValue(ci::getDefaultValue).build();
    }

    private AbstractConfigListEntry<?> createStringConfigItem(ConfigItem<String> ci, ConfigEntryBuilder entryBuilder) {
        return entryBuilder.startStrField(new TranslatableText(ci.getDetails()), ci.getValue()).setSaveConsumer(ci::setValue).setDefaultValue(ci::getDefaultValue).build();
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> AbstractConfigListEntry<?> createEnumConfigItem(ConfigItem<Enum<?>> ci, ConfigEntryBuilder entryBuilder) {
        return entryBuilder.startEnumSelector(new TranslatableText(ci.getDetails()), (Class<T>) ((Enum<?>) ci.getValue()).getClass().getEnumConstants()[0].getClass(), (T) ci.getValue())
                .setSaveConsumer(ci::setValue)
                .setDefaultValue(() -> (T) ci.getValue()).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void setupConfigItem(ConfigItem<?> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        switch (ci.getType()) {
            case BOOLEAN:
                setupBooleanConfigItem((ConfigItem<Boolean>) ci, category, entryBuilder);
                break;
            case DOUBLE:
                setupDoubleConfigItem((ConfigItem<Double>) ci, category, entryBuilder);
                break;
            case GROUP:
                List<AbstractConfigListEntry> entryList = ((ConfigItemGroup) ci).getConfigs().stream().map(configItem -> createConfigItem(configItem, entryBuilder, category.getCategoryKey().getString() + "." + ci.getName())).collect(Collectors.toList());
                SubCategoryBuilder groupCategory = entryBuilder.startSubCategory(new TranslatableText(category.getCategoryKey().getString() + "." + ci.getName()), entryList);
                category.addEntry(groupCategory.build());
                break;
            case INTEGER:
                setupIntegerConfigItem((ConfigItem<Integer>) ci, category, entryBuilder);
                break;
            case STRING:
                setupStringConfigItem((ConfigItem<String>) ci, category, entryBuilder);
                break;
            case ENUM:
                setupEnumConfigItem((ConfigItem<Enum<?>>) ci, category, entryBuilder);
            default:
                break;
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private AbstractConfigListEntry<?> createConfigItem(ConfigItem<?> ci, ConfigEntryBuilder entryBuilder, String superGroupName) {
        switch (ci.getType()) {
            case BOOLEAN:
                return createBooleanConfigItem((ConfigItem<Boolean>) ci, entryBuilder);
            case DOUBLE:
                return createDoubleConfigItem((ConfigItem<Double>) ci, entryBuilder);
            case INTEGER:
                return createIntegerConfigItem((ConfigItem<Integer>) ci, entryBuilder);
            case STRING:
                return createStringConfigItem((ConfigItem<String>) ci, entryBuilder);

            case ENUM:
                return createEnumConfigItem((ConfigItem<Enum<?>>) ci, entryBuilder);
            case GROUP:
                List<AbstractConfigListEntry> subItems = ((ConfigItemGroup) ci).getConfigs().stream().map(configItem -> createConfigItem(configItem, entryBuilder, superGroupName + "." + ci.getName())).collect(Collectors.toList());
                SubCategoryBuilder groupCategory = entryBuilder.startSubCategory(new TranslatableText(superGroupName + "." + ci.getName()), subItems);
                return groupCategory.build();
            default:
                return null;
        }
    }

}
