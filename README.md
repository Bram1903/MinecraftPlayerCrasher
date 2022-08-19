# Minecraft Player Crasher Plugin

A simple Minecraft plugin to crash a player using a command.

## Features

- AntiCrash Bypass (Multiple Methods intented to bypass)
- Reflections (This plugin makes use of Reflections to make the plugin work on multiple server versions)
- Plugin Hider (The plugin will hide itself from /crash, /plugin and /pl for those without permissions to use the crash command.)
- Tab Completion (While using /crash you can use tab to auto complete certain options)
- Options to use all the methods at once (Except the entity method)
- Configuration file (Enable / Disable certain features within the plugin)

## Usage/Examples

/crash (Main command that shows which methods are available)

/crash (player) (method / all)

Methods:
- `Explosion`
- `Position`
- `Entity` (Only use this method if the other methods didn't work. It can be quite heavy for the server.)

## Tested Minecraft Server Versions
These are the server versions the plugin will work on. You are still able to crash players that joined with other versions through ViaVersion.

1.8.9, 1.12.2, 1.16, 1.16.4 and 1.16.5 

Any server version from 1.17 or above will not work, since they reworked the way their classes/packages work.

## Installation

Using this plugin is as simple as downloading the latest release, and placing the jar into your server plugins folder.

## Permission Nodes
Players that are OP (Operators) have these permissions by default.

- `crasher.use` Allows the player to use the /crash command.
- `crasher.bypass` Makes the player invincible to the crasher.

## Compiling Jar From Source

Requirements
- Java 1.8
- Maven

Compile the jar from source by running the following command in the project root's directory (where the pom.xml file is located).

```bash
mvn clean package
```
 You can find the compiled jar in the project root's /target/ directory.
    
## Acknowledgements

 - This is a fork of [Marcelektro's](https://github.com/Marcelektro/Minecraft-PlayerCrasher) player crasher.

## License

[MIT](https://choosealicense.com/licenses/mit/)