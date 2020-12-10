package com.oroarmor.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Config is a holder class for a list of {@link ConfigItemGroup}. It's main
 * feature is to save and read from a file.
 * 
 * @author Eli Orona
 *
 */
public class Config {
	/**
	 * The GSON formatter for the Config
	 */
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * The list of ConfigItemGroups for the config
	 */
	private final List<ConfigItemGroup> configs;

	/**
	 * The file to save and read the config from
	 */
	private final File configFile;

	/**
	 * The ID (often the mod id) for the config
	 */
	private final String id;

	/**
	 * Creates a new config
	 * 
	 * @param configs    The list of {@link ConfigItemGroup} for the config
	 * @param configFile The file to read and save from
	 * @param id         The id of the config, should be the mod id
	 */
	public Config(List<ConfigItemGroup> configs, File configFile, String id) {
		this.configs = configs;
		this.configFile = configFile;
		this.id = id;
	}

	/**
	 * 
	 * @return The list of {@link ConfigItemGroup} for this config
	 */
	public List<ConfigItemGroup> getConfigs() {
		return configs;
	}

	/**
	 * Reads the config from the file. If any changes are made and not saved this
	 * will overwrite them.
	 */
	public void readConfigFromFile() {
		try (FileInputStream stream = new FileInputStream(configFile)) {
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			String file = new String(bytes);
			JsonObject parsed = new JsonParser().parse(file).getAsJsonObject();
			configs.stream().forEachOrdered(cig -> cig.fromJson(parsed.get(cig.getName())));
		} catch (FileNotFoundException e) {
			saveConfigToFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String path, Class<T> clazz) {
		String[] splitPath = path.split("\\.");

		ConfigItem<?> selectedItem = getConfigs().stream().filter(cig -> cig.name.equals(splitPath[0])).findFirst().get();
		try {
			LinkedList<String> paths = new LinkedList<>(Arrays.asList(splitPath));
			while (selectedItem instanceof ConfigItemGroup) {
				paths.removeFirst();
				selectedItem = ((ConfigItemGroup) selectedItem).getConfigs().stream().filter(ci -> ci.name.equals(paths.getFirst())).findFirst().get();
			}
		} catch (NoSuchElementException e) {
			System.err.printf("Path: %s does not exist\n", path);
			return null;
		}

		boolean validType = false;

		switch (selectedItem.getType()) {
		case BOOLEAN:
			validType = clazz == Boolean.class;
			break;
		case DOUBLE:
			validType = clazz == Double.class;
			break;
		case GROUP:
			validType = clazz == ConfigItemGroup.class;
			break;
		case INTEGER:
			validType = clazz == Integer.class;
			break;
		case STRING:
			validType = clazz == String.class;
			break;
		default:
			break;
		}

		if (!validType) {
			throw new IllegalArgumentException("Incorrect type " + clazz.getName() + " for " + path + ". Correct type is " + selectedItem.getType());
		}

		return ((ConfigItem<T>) selectedItem).getValue();
	}

	/**
	 * Saves the current config to the file.
	 */
	public void saveConfigToFile() {
		JsonObject object = new JsonObject();
		configs.stream().forEachOrdered(c -> object.add(c.getName(), c.toJson()));

		try (FileOutputStream stream = new FileOutputStream(configFile)) {
			stream.write(GSON.toJson(object).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a string representation of the config and all the sub configs
	 */
	@Override
	public String toString() {
		return configFile.getName() + ": [" + configs.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}

	/**
	 * 
	 * @return The id of the config
	 */
	public String getID() {
		return id;
	}
}
