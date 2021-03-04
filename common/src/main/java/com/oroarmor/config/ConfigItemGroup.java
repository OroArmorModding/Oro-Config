package com.oroarmor.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringJoiner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Extending {@link ConfigItem}, {@link ConfigItemGroup} can store multiple
 * {@link ConfigItem}s
 *
 * @author Eli Orona
 */
public class ConfigItemGroup extends ConfigItem<ConfigItemGroup> {

    /**
     * The list of {@link ConfigItem}
     */
    private final List<ConfigItem<?>> configs;

    /**
     * BAD CONSTRUCTOR ONLY FOR DEFAULT VALUE. DO NOT USE
     */
    private ConfigItemGroup() {
        super(null, null, null);
        configs = new ArrayList<>();
    }

    /**
     * Creates a new {@link ConfigItemGroup} with the list of configs and the name
     *
     * @param configs The list of configs
     * @param name    The name of this group
     */
    public ConfigItemGroup(List<ConfigItem<?>> configs, String name) {
        super(name, new ConfigItemGroup(), "");
        this.configs = configs;
    }

    @Override
    public void fromJson(JsonElement jsonConfigs) {
        JsonObject object = jsonConfigs.getAsJsonObject();
        for (Entry<String, JsonElement> entry : object.entrySet()) {
            for (ConfigItem<?> c : configs) {
                if (c.getName().equals(entry.getKey())) {
                    c.fromJson(entry.getValue());
                }
            }
        }
    }

    /**
     * @return The configs for this group
     */
    public List<ConfigItem<?>> getConfigs() {
        return configs;
    }

    /**
     * Turns a config into a json property
     *
     * @param c      The config item
     * @param object the json object
     */
    private void parseConfig(ConfigItem<?> c, JsonObject object) {
        switch (c.getType()) {
            case BOOLEAN:
                object.addProperty(c.getName(), (Boolean) c.getValue());
                break;
            case DOUBLE:
            case INTEGER:
                object.addProperty(c.getName(), (Number) c.getValue());
                break;
            case STRING:
                object.addProperty(c.getName(), (String) c.getValue());
                break;
            case ENUM:
                object.addProperty(c.getName(), c.getValue().toString());
                break;
            case GROUP:
                object.add(c.getName(), ((ConfigItemGroup) c).toJson());
            default:
                break;
        }
    }

    /**
     * Converts the config items into json
     *
     * @return A {@link JsonObject} of this group
     */
    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        for (ConfigItem<?> c : configs) {
            parseConfig(c, object);
        }

        return object;
    }

    @Override
    public String toString() {
        String string = getName() + ": [";
        StringJoiner joiner = new StringJoiner(", ");
        for (ConfigItem<?> config : configs) {
            String toString = config.toString();
            joiner.add(toString);
        }
        string += joiner.toString();
        return string + "]";
    }
}
