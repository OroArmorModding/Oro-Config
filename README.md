# Oro-Config
A simple mod config with ModMenu and command integration


## Goal
My goal in creating this library was to make an light-weight config library that feels like the json that you save your code to. Along the way I addded support for commands and ModMenu/ClothConfig screens.


## How to include
Include my library in your mod by adding it to your `build.gradle` file.

Make sure maven central is included in your repositiories section:
```
repositories {
  mavenCentral()
}
```

The current version is: ![](https://img.shields.io/github/v/tag/oroarmor/oro-config.svg)

### Fabric

Then in your dependencies section add my library to the classpath and jar-in-jar it so that users don't have to download an extra file:

```
dependencies {	
  modImplementation 'com.oroarmor:oro-config-fabric:${version}'
  include 'com.oroarmor:oro-config-fabric:${version}'
}
```

### Forge

Then in your dependencies section add my library to the classpath and shadow it so that users don't have to download an extra file:

```
dependencies {	
  implementation fg.deobf('com.oroarmor:oro-config-forge:${version}')
  shadow 'com.oroarmor:oro-config-forge:${version}'
}
```

### Outside of minecraft

Then in your dependencies section add my library to the classpath and shadow it so that users don't have to download an extra file:

```
dependencies {	
  implemenation 'com.oroarmor:oro-config-common:${version}'
  shadow 'com.oroarmor:oro-config-common:${version}'
}
```

## How to use
The best way to use my config is to extend `com.oroarmor.config.Config` with your own class. Inside this class, you should include other classes that extend `com.oroarmor.config.ConfigItemGroup` for your config groups. See the [example](#example) for a way to use the library.

### Config Item
`ConfigItem`s are the main storage of the different values that make up your config. There are two constructors for `ConfigItem`s:
```java
ConfigItem(String name, T defaultValue, String details)
```
and 
```java
ConfigItem(String name, T defaultValue, String details, Consumer<ConfigItem<T>> onChange)
```
T is the type of data that you are storing in this `ConfigItem`, being one of the supported types.

The name is for the name of this `ConfigItem`, which is used in the json to identify what the values are used for.
The default value is the value that is normal for the config to use, but is overriden when the config file is read.
The details are a string representing a language key that can be used in your lang file as it is used in both commands and in the Cloth Config support.

The second constructor has an `onChange` parameter, which is a Consumer that is run every time this config is changed, which can be used to send data to clients on servers or trigger other events.

You can get the value from a `ConfigItem` with the `getValue` method.

The library provides `ConfigItem`s for the types `String`, `Double`, `Integer`, `Boolean`, and any `Enum`. `ConfigItem` can be extended for custom value types. Registering an `EntryBuilder` and a `CommandBuilder` in `ConfigScreenBuilders` and `ConfigItemCommands` respectively add them to the config screen and command.

#### Array Config Items

`ArrayConfigItem`s are exactly the same as normal config items, but have a couple useful methods to get values based on their index. You cannot nest `ArrayConfigItem`s inside other `ArrayConfigItem`s. The only supported types are `String`, `Double`, `Integer`, `Boolean`, and any `Enum`

### Config Item Groups
`ConfigItemGroup`s are a way to store multiple `ConfigItem`s into one group. `ConfigItemGroup`s can be nested in each other for sub groups. There is one constructor:
```java
ConfigItemGroup(List<ConfigItem<?>> configs, String name)
```
Configs is for a list of `ConfigItem`s, not needing any type.
Name is for the name of the `ConfigItemGroup`, and is used in the same way as name for `ConfigItem`.

### Config
`Config` is the root for your mod config and can read and save to files with just one method. Currently `Config`s only store a list of `ConfigItemGroup`s and a single `ConfigItem` cannot be be stored. Because the file for a config can be defined, there can be configs for the entire mod, per world, and even per dimension (Commands and ClothConfig have only been tested with entire mod examples). `Config`s will auto-initialize themselves and should always be read and then written to (If the config file does not exist when trying to read, it will not crash and will create the file in the write method). This ensures that any user data is read and not overwritten. There is one constructor:
```java
Config(List<ConfigItemGroup> configs, File configFile, String id)
```
Configs is for the list of `ConfigItemGroup`s
File is for the file to save the config into.
Id is for the ID of the config which is used in commands and modmenu.

### Command
`ConfigCommand` is a simple class that just requires a `Config` in its constructor. It does not handle its own registering, and must be registered through Fabric API

The command is currently broken for forge

### Cloth Config
`ModMenuConfigScreen` is an abstract class that requires you to extend it, passing in your config into a super constructor. Because this is used as an entry point, your constructor ***must*** have no parameters.

### Forge Config Screen
Currently broken

### Example:
These are pulled from the testmod, and are part of this repository. [Test Mod](https://github.com/OroArmor/oro-config/tree/master/fabric-testmod/src/main)

Config Class:
```java
public class TestConfig extends Config {
    public static final ConfigItemGroup mainGroup = new ConfigGroupLevel1();

    public static final List<ConfigItemGroup> configs = of(mainGroup);

    public TestConfig() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "oroarmor_config_testmod.json"), "oroarmor_config_testmod");
    }

    public static class ConfigGroupLevel1 extends ConfigItemGroup {
        public static final EnumConfigItem<EnumTest> testEnum = new EnumConfigItem<>("test_enum", EnumTest.A, "test_enum");
        public static final BooleanConfigItem testItem = new BooleanConfigItem("test_boolean", true, "test_boolean");

        public static final ArrayConfigItem<Integer> testArray = new ArrayConfigItem<>("test_array", new Integer[]{1, 2, 3}, "test_array");

        public ConfigGroupLevel1() {
            super(of(new NestedGroup(), testItem, testEnum, testArray), "group");
        }

        public static class NestedGroup extends ConfigItemGroup {
            public static final IntegerConfigItem nestedItem = new IntegerConfigItem("test_int", 0, "test_integer");

            public NestedGroup() {
                super(of(nestedItem, new TripleNested()), "nested");
            }

            public static class TripleNested extends ConfigItemGroup {
                public static final StringConfigItem testString = new StringConfigItem("test_string", "Default", "test_string");

                public TripleNested() {
                    super(of(testString), "triple");
                }
            }
        }
    }
}
```
This then creates a config file called `oroarmor_config_testmod.json` in the `/config/` directory wherever Minecraft is run:
```json
{
  "group": {
    "nested": {
      "test_int": 0,
      "triple": {
        "test_string": "Default"
      }
    },
    "test_boolean": true,
    "test_enum": "A",
    "test_array": [
      1,
      2,
      3
    ]
  }
}
```

Fabric Command Registration:
```java
CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> new ConfigCommand(YOUR_CONFIG).register(dispatcher, p -> p.hasPermissionLevel(2)));
```

Mod Menu Integration:
```java
public class ModMenuIntegration extends ModMenuConfigScreen {
	public ModMenuIntegration() {
		super(OroConfigTestMod.CONFIG);
	}
}
```

Entry point:
```json
"entrypoints": {
  "main": ["com.oroarmor.config.testmod.OroConfigTestMod"],
  "modmenu": ["com.oroarmor.config.testmod.ModMenuIntegration"]
}
```



