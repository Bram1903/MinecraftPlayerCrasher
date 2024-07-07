<div align="center">
  <h1>PlayerCrasher</h1>
  <img alt="Build" src="https://github.com/Bram1903/MinecraftPlayerCrasher/actions/workflows/gradle.yml/badge.svg">
  <img alt="CodeQL" src="https://github.com/Bram1903/MinecraftPlayerCrasher/actions/workflows/codeql.yml/badge.svg">
  <img alt="GitHub Release" src="https://img.shields.io/github/release/Bram1903/MinecraftPlayerCrasher.svg">
  <br>
  <a href="https://modrinth.com/plugin/playercrasher"><img alt="Modrinth" src="https://img.shields.io/badge/-Modrinth-green?style=for-the-badge&logo=Modrinth"></a>
  <a href="https://ahi.deathmotion.com/"><img alt="Discord" src="https://img.shields.io/badge/-Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"></a>
</div>

## Overview

An easy-to-use Minecraft plugin that enables the crashing of a player's game through the use of a command.

### Requires PacketEvents

Ensure the [PacketEvents](https://modrinth.com/plugin/packetevents) library is installed on your server.

## Table of Contents

- [Overview](#overview)
    - [Requires PacketEvents](#requires-packetevents)
- [Showcase](#showcase)
- [Supported Platforms & Versions](#supported-platforms--versions)
- [Spoofers](#spoofers)
- [Commands](#commands)
- [Permission Nodes](#permission-nodes)
- [Installation](#installation)
- [Compiling From Source](#compiling-from-source)
    - [Prerequisites](#prerequisites)
    - [Steps](#steps)
- [Credits](#credits)
- [License](#license)

## Showcase

![Demo](docs/showcase/img.png)

## Supported Platforms & Versions

| Platform                           | Supported Versions |
|------------------------------------|--------------------|
| Bukkit (Spigot, Paper, Folia etc.) | 1.8.8 - 1.20.6     |
| Velocity                           | Latest Major       |
| BungeeCord (or any forks)          | Latest Major       |

## Features

- **Completely Asynchronous** - The plugin is designed to be as lightweight as possible.
  All packet modifications are done asynchronously, so the main thread is never blocked.
- **Folia Support** - The plugin integrates with [Folia](https://papermc.io/software/folia), which is a Paper fork that
  adds regionised multithreading to the server.
- **Crash Detector** - By sending both a keep alive and transaction packet, the plugin can detect if a player has
  crashed, even if the player is still connected.
- **Configurable** - The plugin is highly configurable, allowing you to adjust the settings to your liking.
- **Update Checker** - The plugin automatically checks for updates on startup.
  If a new version is available, a message will be sent to the console.

## Usage/Examples

- `/crash (player) [Method]`

> **Note:** The method parameter is optional. If no method is provided, the plugin will use the most appropriate method
> based on the player's version.

**Methods:**

- `Explosion`
- `Particle`
- `Position`

## Commands

- `/pc` - Displays the plugin's version and author.
- `/crash (player) [Method]` - Crashes the specified player's game.
- `/crashinfo [player]` - Displays the client brand and version of the specified player.

## Permission Nodes

Players that are OP (Operators) have these permissions by default.

- `PlayerCrasher.Crash` Allows the player to use the /crash command.
- `PlayerCrasher.Bypass` Exempts the player from being crashed when the /crash command is used on them.
- `PlayerCrasher.Alerts` Makes the player receive alerts when a player is being crashed by another player.
- `PlayerCrasher.CrashInfo` - Allows the player to use the /crashinfo command.
- `PlayerCrasher.UpdateNotify` Makes the player receive an update notification when a new version is available.

## Installation

1. **Prerequisites**: Install [PacketEvents](https://modrinth.com/plugin/packetevents).
2. **Download**: Get the latest release from
   the [GitHub release page](https://github.com/Bram1903/MinecraftPlayerCrasher/releases/latest).
3. **Installation**: Move the downloaded plugin to your server's plugins directory.
4. **Configuration**: Customize settings in `config.yml`.
5. **Restart**: Restart the server for changes to take effect.

## Compiling From Source

### Prerequisites

- Java Development Kit (JDK) version 21 or higher
- [Git](https://git-scm.com/downloads)

### Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Bram1903/MinecraftPlayerCrasher.git
   ```

2. **Navigate to Project Directory**:
   ```bash
   cd MinecraftPlayerCrasher
   ```

3. **Compile the Source Code**:
   Use the Gradle wrapper to compile and generate the plugin JAR file:

   <details>
   <summary><strong>Linux / macOS</strong></summary>

   ```bash
   ./gradlew build
   ```
   </details>
   <details>
   <summary><strong>Windows</strong></summary>

   ```cmd
   .\gradlew build
   ```
   </details>

## Credits

Special thanks to:

- **[@Retrooper](https://github.com/retrooper)**: Author of [PacketEvents](https://github.com/retrooper/packetevents).

## License

This project is licensed under the [GPL3 License](LICENSE).