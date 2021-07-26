## What is Labyrinth?
Labyrinth is an easy-access Spigot development library that assists you in areas that may
otherwise have been far more time-consuming.


[![](https://jitpack.io/v/the-h-team/Labyrinth.svg)](https://jitpack.io/#the-h-team/Labyrinth)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.the-h-team/labyrinth)](https://s01.oss.sonatype.org/#nexus-search;gav~com.github.the-h-team~labyrinth~~~)
### Importing with Maven
```xml
    <dependencies>
    <!-- Used for accessing common library functions -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-common</artifactId>
            <version>1.7.0</version>
        </dependency>
    <!-- Used specifically for loading/retrieving custom skull items. -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-skulls</artifactId>
            <version>1.7.0</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically as a full GUI arsenal (Singular/Paginated/Shared/Live/Slideshow/Anvil). -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-gui</artifactId>
            <version>1.7.0</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for AFK implementations. -->
        <dependency>
            <groupId>com.github.the-h-team.Labyrinth</groupId>
            <artifactId>labyrinth-afk</artifactId>
            <version>1.7.0</version>
            <scope>provided</scope>
        </dependency>
    <!-- For build use only! (Includes main class + plugin.yml, try not to use this) -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-plugin</artifactId>
            <version>1.7.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```
### Importing with Gradle
```groovy
	allprojects {
		repositories {
			mavenCentral()
		}
	}

	dependencies {
	        implementation 'com.github.the-h-team:labyrinth-common:1.7.0'
	        implementation 'com.github.the-h-team:labyrinth-afk:1.7.0'
	        implementation 'com.github.the-h-team:labyrinth-gui:1.7.0'
	        implementation 'com.github.the-h-team:labyrinth-skulls:1.7.0'
	}
```

## What are its key points?
+ Easy Collection management with pagination
+ Tools for simple, automatic class registration
+ Load External Jars
+ Time, Command, String, Math and List Utilities
+ _Custom object!_ Persistent Data Storage using Base64 serialization
+ Command Building
+ Safe, common library access to amazing plugins like Vault, Enterprise, PlaceholderAPI
& Much More!


_API Table of Contents_:
--

  - [String Utility](https://github.com/the-h-team/Labyrinth/wiki/StringUtils-first-dive)
  - [Time Utility](https://github.com/the-h-team/Labyrinth/wiki/Get-with-the-times)
  - [Listing Collections](https://github.com/the-h-team/Labyrinth/wiki/PaginatedList-Example)
  - [Automatic Command Pickup]()
  - [Base64 Serialization]()
  - [Persistent Data Container]()
  - [Singular GUI](https://github.com/the-h-team/Labyrinth/wiki/Singular-GUI)
  - [Applicable Data]()
  - [Economy Interface Wrapper]()
  - [Centered Cardinal Directions]()
  - [EntityType Matcher]()
  - [Material Matcher]()
  - [Item Recipe Builder]()
  - [Player/Console Message formatter]()
  - [Player Skull Finder]()
  - [Tab Completion Builder]()
  - [Paginated GUI](https://github.com/the-h-team/Labyrinth/wiki/Paginated-GUI)
  - [Colored Component Builder]()
  - [Custom Color Generation/Interfacing]()
  - [Cuboid Generation/Interfacing]()
  - [File Manager]()
  - [Automatic Listener Pickup]()
  - [Shareable/Interactable GUI]()
  - [Cooldown Interface](https://github.com/the-h-team/Labyrinth/wiki/Cooldowns)
  - [Custom ID Generation](https://github.com/the-h-team/Labyrinth/wiki/StringUtils-first-dive#2-regex-matching)
  - [Task Scheduling](https://github.com/the-h-team/Labyrinth/wiki/Task-Scheduling)
---
###### Labyrinth
A spigot development tool that makes certain tasks much easier.

*Original components licensed for use under the terms of the [GNU Lesser General Public License, version 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html).*

*Ships with MIT-derived components:*
- *Java port of **RGBApi** by [F1b3r](https://github.com/F1b3rDEV); original Kotlin sources can be found [here](https://github.com/F1b3rDEV/minecraft-spigot-rgb-chat-support).*
