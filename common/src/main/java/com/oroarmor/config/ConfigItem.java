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

import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.ArgumentBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.command.CommandSource;

/**
 * {@link ConfigItem} often stores a name and a value for saving data into a
 * config. <br>
 * The current supported types are booleans, integers, doubles, strings, and
 * {@link ConfigItemGroup}
 *
 * @param <T>
 * @author Eli Orona
 */
public abstract class ConfigItem<T> {
    protected final String name;
    protected final String details;
    protected final T defaultValue;
    @Nullable
    protected final Consumer<ConfigItem<T>> onChange;
    protected T value;

    /**
     * Creates a new config with the name, defaultValue, and details
     *
     * @param name         The name for the config item
     * @param defaultValue The default value in case of a corrupted/missing config
     * @param details      A translatable string for readability in multiple
     *                     languages
     */
    public ConfigItem(String name, T defaultValue, String details) {
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
    public ConfigItem(String name, T defaultValue, String details, @Nullable Consumer<ConfigItem<T>> onChange) {
        this.name = name;
        this.details = details;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.onChange = onChange;
    }

    /**
     * Reads and sets the {@link ConfigItem} from a JSON Element. Will throw an
     * error if the type does not match the type of the {@link ConfigItem}
     *
     * @param element The JSON Element
     */
    public abstract void fromJson(JsonElement element);

    public abstract void toJson(JsonObject object);

    /**
     * @return The default value of the {@link ConfigItem}
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return The detail string of the {@link ConfigItem}
     */
    public String getDetails() {
        return details;
    }

    /**
     * @return the name of the {@link ConfigItem}
     */
    public String getName() {
        return name;
    }

    /**
     * @return The current value of the {@link ConfigItem}
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the value of the {@link ConfigItem}
     *
     * @param value The value to set
     */
    public void setValue(T value) {
        this.value = value;
        if (this.onChange != null) {
            this.onChange.accept(this);
        }
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }

    public abstract <T1> boolean isValidType(Class<T1> clazz);

    public abstract <S extends CommandSource> ArgumentBuilder<?, ?> getSetCommand(ConfigItemGroup group, Config config);

    public abstract String getCommandValue();

    public boolean atDefaultValue() {
        return this.value.equals(this.defaultValue);
    }

    public String getCommandDefaultValue() {
        return this.value.toString();
    }

//    /**
//     * The current types for the config items
//     *
//     * @author Eli Orona
//     */
//    public enum Type {
//        BOOLEAN, INTEGER, DOUBLE, STRING, GROUP, ENUM;
//
//        /**
//         * Gets the corresponding type for an object. If the object's type is not
//         * supported, this returns null.
//         *
//         * @param value The object to find the type for
//         * @return The type for that object
//         */
//        public static Type getTypeFrom(Object value) {
//            if (value instanceof Boolean) {
//                return BOOLEAN;
//            }
//            if (value instanceof Integer) {
//                return INTEGER;
//            }
//            if (value instanceof Double) {
//                return DOUBLE;
//            }
//            if (value instanceof String) {
//                return STRING;
//            }
//            if (value instanceof ConfigItemGroup) {
//                return GROUP;
//            }
//            if (value instanceof Enum) {
//                return ENUM;
//            }
//
//            if (value != null && value.getClass().isArray()) {
//                return getTypeFrom(((Object[]) value)[0]);
//            }
//
//            return null;
//        }
//    }
}
