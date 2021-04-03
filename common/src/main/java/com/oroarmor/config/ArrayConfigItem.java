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

package com.oroarmor.config;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;


/**
 * {@link ArrayConfigItem} stores an array of the supported types <br>
 * The current supported types are booleans, integers, doubles, strings, and
 * {@link ConfigItemGroup}
 *
 * @param <T>
 * @author Eli Orona
 */
public class ArrayConfigItem<T> extends ConfigItem<T[]> {

    /**
     * Creates a new config with the name, defaultValue, and details
     *
     * @param name         The name for the config item
     * @param defaultValue The default value in case of a corrupted/missing config
     * @param details      A translatable string for readability in multiple
     *                     languages
     */
    public ArrayConfigItem(String name, T[] defaultValue, String details) {
        this(name, defaultValue, details, null);
    }

    /**
     * Creates a new config with the name, defaultValue, details, and an onChange
     * consumer
     *
     * @param name         The name for the config item
     * @param defaultValue The default value in case of a corrupted/missing config
     * @param details      A translatable string for readability in multiple
     *                     languages
     * @param onChange     A {@link Consumer} that is run every time the config item
     *                     is modified
     */
    public ArrayConfigItem(String name, T[] defaultValue, String details, @Nullable Consumer<ConfigItem<T[]>> onChange) {
        super(name, defaultValue, details, onChange);
        if (defaultValue[0] instanceof ArrayConfigItem) {
            throw new UnsupportedOperationException("ArrayConfigItems cannot be nested");
        }
        this.value = Arrays.copyOf(defaultValue, defaultValue.length);
    }

    /**
     * Reads and sets the {@link ConfigItem} from a JSON Element. Will throw an
     * error if the type does not match the type of the {@link ConfigItem}
     *
     * @param element The JSON Element
     */
    @SuppressWarnings("unchecked")
    public void fromJson(JsonElement element) {
        for (int i = 0; i < element.getAsJsonArray().size(); i++) {
            T newValue;
            JsonElement arrayElement = element.getAsJsonArray().get(i);
            switch (defaultValue[0].getClass().isEnum() ? "ENUM" : defaultValue[0].getClass().getSimpleName().toUpperCase()) {
                case "BOOLEAN":
                    newValue = (T) (Object) arrayElement.getAsBoolean();
                    break;

                case "INTEGER":
                    newValue = (T) (Object) arrayElement.getAsInt();
                    break;

                case "DOUBLE":
                    newValue = (T) (Object) arrayElement.getAsDouble();
                    break;

                case "STRING":
                    newValue = (T) arrayElement.getAsString();
                    break;

                case "ENUM":
                    newValue = (T) Arrays.stream(((T) defaultValue[i]).getClass().getEnumConstants()).filter(val -> val.toString().equals(arrayElement.getAsString())).findFirst().get();
                    break;

                default:
                    return;
            }
            value[i] = newValue;
        }
        if (value != null) {
            setValue(value);
        }
    }

    @Override
    public void toJson(JsonObject object) {
        JsonArray array = new JsonArray();
        for (T t : value) {
            JsonElement element;
            switch (defaultValue[0].getClass().isEnum() ? "ENUM" : defaultValue[0].getClass().getSimpleName().toUpperCase()) {
                case "BOOLEAN":
                    element = new JsonPrimitive((Boolean) t);
                    break;

                case "INTEGER":
                case "DOUBLE":
                    element = new JsonPrimitive((Number) t);
                    break;

                case "STRING":
                    element = new JsonPrimitive((String) t);
                    break;

                case "ENUM":
                    element = new JsonPrimitive(t.toString());
                    break;

                default:
                    return;
            }

            array.add(element);
        }

        object.add(this.name, array);
    }

    @Override
    public boolean atDefaultValue() {
        return Arrays.equals(defaultValue, value);
    }

    @Override
    public <S> boolean isValidType(Class<S> clazz) {
        return clazz == defaultValue[0].getClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S extends CommandSource> ArgumentBuilder<?, ?> getSetCommand(ConfigItemGroup group, Config config) {
        ArgumentBuilder<S, ?> setCommand;
            switch (defaultValue[0].getClass().isEnum() ? "ENUM" : defaultValue[0].getClass().getSimpleName().toUpperCase()) {
                case "BOOLEAN":
                    setCommand = RequiredArgumentBuilder.<S, Boolean>argument("boolean", BoolArgumentType.bool()).executes(c -> {
                        boolean result = BoolArgumentType.getBool(c, "boolean");
                        int index = IntegerArgumentType.getInteger(c, "index");
                        this.setValue((T) (Object) result, index);
                        config.saveConfigToFile();
                        return 1;
                    });
                    break;

                case "INTEGER":
                    setCommand = RequiredArgumentBuilder.<S, Integer>argument("int", IntegerArgumentType.integer()).executes(c -> {
                        int result = IntegerArgumentType.getInteger(c, "int");
                        int index = IntegerArgumentType.getInteger(c, "index");
                        this.setValue((T) (Object) result, index);
                        config.saveConfigToFile();
                        return 1;
                    });
                    break;
                case "DOUBLE":
                    setCommand = RequiredArgumentBuilder.<S, Double>argument("double", DoubleArgumentType.doubleArg()).executes(c -> {
                        double result = DoubleArgumentType.getDouble(c, "double");
                        int index = IntegerArgumentType.getInteger(c, "index");
                        this.setValue((T) (Object) result, index);
                        config.saveConfigToFile();
                        return 1;
                    });
                    break;

                case "STRING":
                    setCommand = RequiredArgumentBuilder.<S, String>argument("string", StringArgumentType.greedyString()).executes(c -> {
                        String result = StringArgumentType.getString(c, "string");
                        int index = IntegerArgumentType.getInteger(c, "index");
                        this.setValue((T) result, index);
                        config.saveConfigToFile();
                        return 1;
                    });
                    break;

                case "ENUM":
                    setCommand = literal("set");
                    Enum<?>[] enums = ((Enum<?>) this.getValue()[0]).getClass().getEnumConstants();
                    for (Enum<?> _enum : enums) {
                        setCommand.then(LiteralArgumentBuilder.<S>literal(_enum.toString()).executes(c -> {
                            int index = IntegerArgumentType.getInteger(c, "index");
                            this.setValue((T) _enum, index);
                            config.saveConfigToFile();
                            return 1;
                        }));
                    }
                    break;

                default:
                    throw new IllegalStateException("Class " + defaultValue[0].getClass().getSimpleName() + " is an unsupported type");
            }
        return RequiredArgumentBuilder.<S, Integer>argument("index", IntegerArgumentType.integer(0, this.value.length)).then(setCommand);
    }

    @Override
    public String getCommandValue() {
        return Arrays.stream(this.value).map(Object::toString).collect(Collectors.joining(","));
    }

    @Override
    public String getCommandDefaultValue() {
        return Arrays.stream(this.defaultValue).map(Object::toString).collect(Collectors.joining(","));
    }

    /**
     * @param position The position in the array for the value
     * @return The default value of the {@link ConfigItem}
     */
    public T getDefaultValue(int position) {
        return this.defaultValue[position];
    }

    /**
     * @param position The position in the array for the value
     * @return The current value of the {@link ConfigItem}
     */
    public T getValue(int position) {
        return this.value[position];
    }

    /**
     * Sets the value in {@code position} the {@link ArrayConfigItem}
     *
     * @param value    The value to set
     * @param position The position in the array for the value
     */
    public void setValue(T value, int position) {
        this.value[position] = value;
    }
}
