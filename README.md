## What is Labyrinth?
Labyrinth is an easy-access Spigot development library that assists you in areas that may
otherwise have been far more time-consuming.


[![Maven Central](https://img.shields.io/maven-central/v/com.github.the-h-team/labyrinth*?style=for-the-badge)](https://s01.oss.sonatype.org/#nexus-search;gav~com.github.the-h-team~labyrinth*~~~)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.the-h-team/labyrinth*?label=sonatype&server=https%3A%2F%2Fs01.oss.sonatype.org&style=for-the-badge)](https://s01.oss.sonatype.org/#nexus-search;gav~com.github.the-h-team~labyrinth*~~~)

[![](https://jitpack.io/v/the-h-team/Labyrinth.svg)](https://jitpack.io/#the-h-team/Labyrinth)
### Importing with Maven
```xml
<project>
    <repositories>
        <!-- For snapshots/versions in development -->
        <repository>
            <id>s01-snapshots</id>
            <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
        </repository>
        <!-- No repository needed for Maven Central versions! :D -->
    </repositories>
    <dependencies>
    <!-- Used for accessing common library functions -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-common</artifactId>
            <version>1.7.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for loading/retrieving custom skull items. -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-skulls</artifactId>
            <version>1.7.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically as a full GUI arsenal (Singular/Paginated/Shared/Live/Slideshow/Anvil). -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-gui</artifactId>
            <version>1.7.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    <!-- Plugin internals, submodules marked to shade (Includes main class + plugin.yml, try not to use this) -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-plugin</artifactId>
            <version>1.7.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```
### Importing with Gradle
```groovy
    allprojects {
        repositories {
            // Normal releases
            mavenCentral()
            maven {
                // For snapshots/development builds
                url "https://s01.oss.sonatype.org/content/repositories/snapshots"
            }
        }
    }

    dependencies {
        compileOnly 'com.github.the-h-team:labyrinth-common:1.7.0-SNAPSHOT'
        compileOnly 'com.github.the-h-team:labyrinth-gui:1.7.0-SNAPSHOT'
        compileOnly 'com.github.the-h-team:labyrinth-skulls:1.7.0-SNAPSHOT'
        // for build use only! (includes full plugin and resources)
        compileOnly 'com.github.the-h-team:labyrinth-plugin:1.7.0-SNAPSHOT'
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
### Spigot
  - [AFK Player Utility]()
  - [Command Builder]()
  - [Command Utility]()
  - [Complete GUI Builder](https://github.com/the-h-team/Labyrinth/wiki/Unity-Library)
  - [Complete Head Database/Locator]()
  - [Cooldown Abstraction](https://github.com/the-h-team/Labyrinth/wiki/Cooldowns)
  - [Custom Gradient Color Interface]()
  - [Custom ID Generation](https://github.com/the-h-team/Labyrinth/wiki/StringUtils-first-dive#2-regex-matching)
  - [Directional Enumeration]()
  - [Economy Interface Wrapper]()
  - [Entity Creation Tools]()
  - [File Management]()
  - [Item Recipe Builder]()
  - [Item Modification Builder]()
  - [Legacy Safe NamespacedKey]()
  - [Listing Collections](https://github.com/the-h-team/Labyrinth/wiki/PaginatedList-Example)
  - [Material Matcher]()
  - [Message Formatter]()
  - [String Utility](https://github.com/the-h-team/Labyrinth/wiki/StringUtils-first-dive)
  - [Tab Completion Builder]()
  - [Task Scheduling](https://github.com/the-h-team/Labyrinth/wiki/Task-Scheduling)
  - [Template Creation]()
  - [TextComponent Builder]()
  - [Vault Permission Interface Wrapper]()
### Library
  - [Applicable Data]()
  - [Annotation Discovery]()
  - [Base64 Serialization]()
  - [Cuboid Generation]()
  - [Custom Event Abstraction & Listening](https://github.com/the-h-team/Labyrinth/wiki/Custom-Events)
  - [List Formatting Utility]()
  - [Math Formatting Utility]()
  - [Persistent Data Container]()
  - [Reflective Class Instantiation]()
  - [Region Services w/ Flag Abstraction]()
  - [Time Utility](https://github.com/the-h-team/Labyrinth/wiki/Get-with-the-times)
  
---
###### Labyrinth
A spigot development tool that makes certain tasks much easier.

*Original components licensed for use under the terms of the [GNU Lesser General Public License, version 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html).*

*Ships with MIT-derived components:*
- *Java port of **RGBApi** by [F1b3r](https://github.com/F1b3rDEV); original Kotlin sources can be found [here](https://github.com/F1b3rDEV/minecraft-spigot-rgb-chat-support).*
