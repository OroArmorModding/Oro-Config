/*
 * MIT License
 *
 * Copyright (c) 2021 OroArmor (Eli Orona)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.oroarmor.config.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.oroarmor.config.ArrayConfigItem;
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

    private void setupBooleanArrayConfigItem(ArrayConfigItem<Boolean> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createBooleanArrayConfigItem(ci, entryBuilder));
    }

    private void setupDoubleArrayConfigItem(ArrayConfigItem<Double> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createDoubleArrayConfigItem(ci, entryBuilder));
    }

    private void setupIntegerArrayConfigItem(ArrayConfigItem<Integer> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createIntegerArrayConfigItem(ci, entryBuilder));
    }

    private void setupStringArrayConfigItem(ArrayConfigItem<String> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createStringArrayConfigItem(ci, entryBuilder));
    }

    protected void setupEnumArrayConfigItem(ArrayConfigItem<Enum<?>> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        category.addEntry(createEnumArrayConfigItem(ci, entryBuilder));
    }

    private AbstractConfigListEntry<?> createBooleanArrayConfigItem(ArrayConfigItem<Boolean> ci, ConfigEntryBuilder entryBuilder) {
        List<AbstractConfigListEntry> configs = new ArrayList<>();
        for (int i = 0; i < ci.getValue().length; i++) {
            int finalI = i;
            AbstractConfigListEntry<?> entry = entryBuilder.startBooleanToggle(new TranslatableText(ci.getDetails()).append(": " + i), ci.getValue(i)).setSaveConsumer(val -> ci.setValue(val, finalI)).setDefaultValue(() -> ci.getDefaultValue(finalI)).build();
            configs.add(entry);
        }

        return entryBuilder.startSubCategory(new TranslatableText(ci.getDetails()), configs).build();
    }

    private AbstractConfigListEntry<?> createDoubleArrayConfigItem(ArrayConfigItem<Double> ci, ConfigEntryBuilder entryBuilder) {
        List<AbstractConfigListEntry> configs = new ArrayList<>();
        for (int i = 0; i < ci.getValue().length; i++) {
            int finalI = i;
            AbstractConfigListEntry<?> entry = entryBuilder.startDoubleField(new TranslatableText(ci.getDetails()).append(": " + i), ci.getValue(i)).setSaveConsumer(val -> ci.setValue(val, finalI)).setDefaultValue(() -> ci.getDefaultValue(finalI)).build();
            configs.add(entry);
        }

        return entryBuilder.startSubCategory(new TranslatableText(ci.getDetails()), configs).build();
    }

    private AbstractConfigListEntry<?> createIntegerArrayConfigItem(ArrayConfigItem<Integer> ci, ConfigEntryBuilder entryBuilder) {
        List<AbstractConfigListEntry> configs = new ArrayList<>();
        for (int i = 0; i < ci.getValue().length; i++) {
            int finalI = i;
            AbstractConfigListEntry<?> entry = entryBuilder.startIntField(new TranslatableText(ci.getDetails()).append(": " + i), ci.getValue(i)).setSaveConsumer(val -> ci.setValue(val, finalI)).setDefaultValue(() -> ci.getDefaultValue(finalI)).build();
            configs.add(entry);
        }
        return entryBuilder.startSubCategory(new TranslatableText(ci.getDetails()), configs).build();
    }

    private AbstractConfigListEntry<?> createStringArrayConfigItem(ArrayConfigItem<String> ci, ConfigEntryBuilder entryBuilder) {
        List<AbstractConfigListEntry> configs = new ArrayList<>();
        for (int i = 0; i < ci.getValue().length; i++) {
            int finalI = i;
            AbstractConfigListEntry<?> entry = entryBuilder.startStrField(new TranslatableText(ci.getDetails()).append(": " + i), ci.getValue(i)).setSaveConsumer(val -> ci.setValue(val, finalI)).setDefaultValue(() -> ci.getDefaultValue(finalI)).build();
            configs.add(entry);
        }
        return entryBuilder.startSubCategory(new TranslatableText(ci.getDetails()), configs).build();
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> AbstractConfigListEntry<?> createEnumConfigItem(ConfigItem<Enum<?>> ci, ConfigEntryBuilder entryBuilder) {
        return entryBuilder.startEnumSelector(new TranslatableText(ci.getDetails()), (Class<T>) ((Enum<?>) ci.getValue()).getClass().getEnumConstants()[0].getClass(), (T) ci.getValue())
                .setSaveConsumer(ci::setValue)
                .setDefaultValue(() -> (T) ci.getValue()).build();
    }

    private <T extends Enum<T>> AbstractConfigListEntry<?> createEnumArrayConfigItem(ArrayConfigItem<Enum<?>> ci, ConfigEntryBuilder entryBuilder) {
        List<AbstractConfigListEntry> configs = new ArrayList<>();
        for (int i = 0; i < ci.getValue().length; i++) {
            int finalI = i;
            AbstractConfigListEntry<?> entry = entryBuilder.startEnumSelector(new TranslatableText(ci.getDetails()).append(": " + i), (Class<T>) ((Enum<?>) ci.getValue(i)).getClass().getEnumConstants()[0].getClass(), (T) ci.getValue(i)).setSaveConsumer(val -> ci.setValue(val, finalI)).setDefaultValue(() -> (T) ci.getDefaultValue(finalI)).build();
            configs.add(entry);
        }

        return entryBuilder.startSubCategory(new TranslatableText(ci.getDetails()), configs).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void setupConfigItem(ConfigItem<?> ci, ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        if (!(ci instanceof ArrayConfigItem)) {
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
        } else {
            switch (ci.getType()) {
                case BOOLEAN:
                    setupBooleanArrayConfigItem((ArrayConfigItem<Boolean>) ci, category, entryBuilder);
                    break;
                case DOUBLE:
                    setupDoubleArrayConfigItem((ArrayConfigItem<Double>) ci, category, entryBuilder);
                    break;
                case GROUP:
                    List<AbstractConfigListEntry> subList = new ArrayList<>();
                    for (int i = 0; i < ((ArrayConfigItem<ConfigItemGroup>) ci).getValue().length; i++) {
                        int finalI = i;
                        List<AbstractConfigListEntry> entryList = (((ArrayConfigItem<ConfigItemGroup>) ci).getValue(i)).getConfigs().stream().map(configItem -> createConfigItem(configItem, entryBuilder, category.getCategoryKey().getString() + "." + ((ArrayConfigItem<ConfigItemGroup>) ci).getValue(finalI).getName())).collect(Collectors.toList());
                        SubCategoryBuilder groupCategory = entryBuilder.startSubCategory(new TranslatableText(category.getCategoryKey().getString() + "." + ((ArrayConfigItem<ConfigItemGroup>) ci).getValue(finalI).getName()), entryList);
                        subList.add(groupCategory.build());
                    }

                    category.addEntry(entryBuilder.startSubCategory(new TranslatableText(category.getCategoryKey().getString() + "." + (ci).getName()), subList).build());
                    break;
                case INTEGER:
                    setupIntegerArrayConfigItem((ArrayConfigItem<Integer>) ci, category, entryBuilder);
                    break;
                case STRING:
                    setupStringArrayConfigItem((ArrayConfigItem<String>) ci, category, entryBuilder);
                    break;
                case ENUM:
                    setupEnumArrayConfigItem((ArrayConfigItem<Enum<?>>) ci, category, entryBuilder);
                default:
                    break;
            }
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private AbstractConfigListEntry<?> createConfigItem(ConfigItem<?> ci, ConfigEntryBuilder entryBuilder, String superGroupName) {
        if (!(ci instanceof ArrayConfigItem)) {
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
        } else {
            switch (ci.getType()) {
                case BOOLEAN:
                    return createBooleanArrayConfigItem((ArrayConfigItem<Boolean>) ci, entryBuilder);
                case DOUBLE:
                    return createDoubleArrayConfigItem((ArrayConfigItem<Double>) ci, entryBuilder);
                case INTEGER:
                    return createIntegerArrayConfigItem((ArrayConfigItem<Integer>) ci, entryBuilder);
                case STRING:
                    return createStringArrayConfigItem((ArrayConfigItem<String>) ci, entryBuilder);
                case ENUM:
                    return createEnumArrayConfigItem((ArrayConfigItem<Enum<?>>) ci, entryBuilder);
                case GROUP:
                    List<AbstractConfigListEntry> subList = new ArrayList<>();
                    for (int i = 0; i < ((ArrayConfigItem<ConfigItemGroup>) ci).getValue().length; i++) {
                        int finalI = i;
                        List<AbstractConfigListEntry> entryList = (((ArrayConfigItem<ConfigItemGroup>) ci).getValue(i)).getConfigs().stream().map(configItem -> createConfigItem(configItem, entryBuilder, superGroupName + "." + ((ArrayConfigItem<ConfigItemGroup>) ci).getValue(finalI).getName())).collect(Collectors.toList());
                        SubCategoryBuilder groupCategory = entryBuilder.startSubCategory(new TranslatableText(superGroupName + "." + ((ArrayConfigItem<ConfigItemGroup>) ci).getValue(finalI).getName()), entryList);
                        subList.add(groupCategory.build());
                    }

                    return entryBuilder.startSubCategory(new TranslatableText(superGroupName + "." + (ci).getName()), subList).build();
                default:
                    return null;
            }
        }
    }
}
