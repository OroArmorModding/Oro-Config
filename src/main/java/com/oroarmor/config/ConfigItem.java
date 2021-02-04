package com.oroarmor.config;

import java.util.Arrays;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

/**
 * {@link ConfigItem} often stores a name and a value for saving data into a
 * config. <br>
 * The current supported types are booleans, integers, doubles, strings, and
 * {@link ConfigItemGroup}
 *
 * @param <T>
 * @author Eli Orona
 */
public class ConfigItem<T> {
    protected final String name;
    protected final String details;
    protected final T defaultValue;
    protected final Type type;
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
        this.type = Type.getTypeFrom(defaultValue);
        this.onChange = onChange;
    }

    /**
     * Reads and sets the {@link ConfigItem} from a JSON Element. Will throw an
     * error if the type does not match the type of the {@link ConfigItem}
     *
     * @param element The JSON Element
     */
    @SuppressWarnings("unchecked")
    public void fromJson(JsonElement element) {
        T newValue;

        switch (this.type) {
            case BOOLEAN:
                newValue = (T) (Object) element.getAsBoolean();
                break;

            case INTEGER:
                newValue = (T) (Object) element.getAsInt();
                break;

            case DOUBLE:
                newValue = (T) (Object) element.getAsDouble();
                break;

            case STRING:
                newValue = (T) element.getAsString();
                break;

            case ENUM:
                newValue = (T) Arrays.stream(((T) defaultValue).getClass().getEnumConstants()).filter(val -> val.toString().equals(element.getAsString())).findFirst().get();
                break;

            case GROUP:
                ((ConfigItemGroup) defaultValue).fromJson(element.getAsJsonObject());

            default:
                return;
        }

        if (newValue != null) {
            setValue(newValue);
        }
    }

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
     * @return The type of the {@link ConfigItem}
     */
    public Type getType() {
        return type;
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

    /**
     * The current types for the config items
     *
     * @author Eli Orona
     */
    public enum Type {
        BOOLEAN, INTEGER, DOUBLE, STRING, GROUP, ENUM;

        /**
         * Gets the corresponding type for an object. If the object's type is not
         * supported, this returns null.
         *
         * @param value The object to find the type for
         * @return The type for that object
         */
        public static Type getTypeFrom(Object value) {
            if (value instanceof Boolean) {
                return BOOLEAN;
            }
            if (value instanceof Integer) {
                return INTEGER;
            }
            if (value instanceof Double) {
                return DOUBLE;
            }
            if (value instanceof String) {
                return STRING;
            }
            if (value instanceof ConfigItemGroup) {
                return GROUP;
            }
            if (value instanceof Enum) {
                return ENUM;
            }

            return null;
        }

    }
}
