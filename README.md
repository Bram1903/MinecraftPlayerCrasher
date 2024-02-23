# Minecraft Player Crasher Plugin

A simple Minecraft plugin to crash a player using a command.

## Features

- AntiCrash Bypass (Multiple Methods intended to bypass)
- Tab Completion (While using /crash you can use tab to auto-complete certain options)
- Options to use all the methods at once
- Update Checker (Upon the server start the plugin will check if there is a newer release on GitHub.)

## Usage/Examples

/crash (The main command that shows which methods are available)

/crash (player) (method / all)

Methods:

- `Explosion`
- `Position`

## Tested Minecraft Server Versions

These are the server versions the plugin will work on. You are still able to crash players that joined with other
versions through ViaVersion.

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

- Java 17
- Gradle

Compile the jar from source by running the following command in the project root's directory (where the pom.xml file is
located).

```bash
gradle build
```

You can find the compiled jar in the project root's /build/libs/ directory.

## License

[MIT](https://choosealicense.com/licenses/mit/)
