## What is Labyrinth?
Labyrinth is an easy-access Spigot development library that assists you in areas that may
otherwise have been far more time-consuming. It uses a global java library called Panther created by us to handle the base of the apis such as the string utility, task scheduling and many other things.

[![GitHub license](https://img.shields.io/github/license/the-h-team/Labyrinth.svg)](https://github.com/the-h-team/Labyrinth/blob/master/LICENSE)
[![](https://jitpack.io/v/the-h-team/Labyrinth.svg)](https://jitpack.io/#the-h-team/Labyrinth)
[![Github all releases](https://img.shields.io/github/downloads/the-h-team/Labyrinth/total.svg)](https://gitHub.com/the-h-team/Labyrinth/releases/)
![Spiget tested server versions](https://img.shields.io/spiget/tested-versions/97679)

### Importing with Maven
```xml
<project>
    <properties>
        <labyrinth.version>1.9.3-SNAPSHOT</labyrinth.version>
    </properties>
    <repositories>
        <repository>
            <id>jitpack</id>
            <url>https://jitpack.io/</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    <dependencies>
    <!-- Used for accessing common library functions -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-common</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for loading/retrieving custom skull items. -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-skulls</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically as a full GUI arsenal (Singular/Paginated/Shared/Live/Slideshow/Anvil). -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-gui</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for region related services. -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-regions</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for location related services. -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-loci</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for permission related services (Vault replacement). -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-perms</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for pastebin/hastebin related services. -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-paste</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Used specifically for placeholder provision services. -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-placeholders</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    <!-- Plugin internals, submodules marked to shade (Includes main class + plugin.yml, try not to use this) -->
        <dependency>
            <groupId>com.github.the-h-team</groupId>
            <artifactId>labyrinth-plugin</artifactId>
            <version>${labyrinth.version}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```
### Importing with Gradle
```groovy
    allprojects {
        repositories {
            mavenCentral()
            maven {
                url "https://jitpack.io/"
            }
        }
    }

    dependencies {
        compileOnly 'com.github.the-h-team:labyrinth-common:1.9.3-SNAPSHOT'
        compileOnly 'com.github.the-h-team:labyrinth-gui:1.9.3-SNAPSHOT'
        compileOnly 'com.github.the-h-team:labyrinth-skulls:1.9.3-SNAPSHOT'
        compileOnly 'com.github.the-h-team:labyrinth-regions:1.9.3-SNAPSHOT'
        compileOnly 'com.github.the-h-team:labyrinth-loci:1.9.3-SNAPSHOT'
        // for build use only! (includes full plugin and resources)
        compileOnly 'com.github.the-h-team:labyrinth-plugin:1.9.3-SNAPSHOT'
    }
```

## What are its key points?
+ Easy Collection management with pagination
+ Tools for simple, automatic class registration
+ Load External Jars
+ Time, Command, String, Math and List Utilities
+ _Custom object!_ Persistent Data Storage using Base64 serialization
+ In-daemon task scheduling.
+ Custom o(1) o(n) complexity collection/map types.
* Cuboid/Region API
* Tablist display API
* Tab completion utility
+ Safe, common library access to amazing plugins like Vault, Enterprise, PlaceholderAPI
& Much More!


_API Table of Contents_:
-- 
* ### Common Utilities
  - [Command Builder](https://github.com/the-h-team/Labyrinth/wiki/Commands)
  - [Command Utility](https://github.com/the-h-team/Labyrinth/wiki/Commands)
  - [Complete GUI Builder](https://github.com/the-h-team/Labyrinth/wiki/Unity-Library)
  - [Complete Head Database/Locator](https://github.com/the-h-team/Labyrinth/wiki/Custom-Heads)
  - [Cooldown Abstraction](https://github.com/the-h-team/Labyrinth/wiki/Cooldowns)
  - [Custom Gradient Color Interface](https://github.com/the-h-team/Labyrinth/wiki/Custom-Gradients)
  - [Custom ID Generation](https://github.com/the-h-team/Labyrinth/wiki/StringUtils-first-dive#2-regex-matching)
  - [Directional Enumeration](https://github.com/the-h-team/Labyrinth/wiki/Directional-Enumeration)
  - [Economy Interface Wrapper](https://github.com/the-h-team/Labyrinth/wiki/Economy-Bridge)
  - [Entity Creation Tools](https://github.com/the-h-team/Labyrinth/wiki/Entity-Creation)
  - [File Management](https://github.com/the-h-team/Labyrinth/wiki/File-Management)
  - [Item Recipe Builder](https://github.com/the-h-team/Labyrinth/wiki/Item-Recipe-Builder)
  - [Item Modification Builder](https://github.com/the-h-team/Labyrinth/wiki/Item-modification)
  - [Legacy Safe NamespacedKey](https://github.com/the-h-team/Labyrinth/wiki/StringUtils-first-dive#namespaces)
  - [Listing Collections](https://github.com/the-h-team/Labyrinth/wiki/PaginatedList-Example)
  - [Material Matcher](https://github.com/the-h-team/Labyrinth/wiki/Item-modification)
  - [Message Formatter]()
  - [String Utility](https://github.com/the-h-team/Labyrinth/wiki/StringUtils-first-dive)
  - [Tab Completion Builder]()
  - [Task Scheduling](https://github.com/the-h-team/Labyrinth/wiki/Task-Scheduling)
  - [Template Creation](https://github.com/the-h-team/Templates/wiki)
  - [TextComponent Builder](https://github.com/the-h-team/Labyrinth/wiki/Messages)
  - [Vault Permission Interface Wrapper]()
* ### General-Purpose Utilities
  - [Applicable Data]()
  - [Annotation Discovery](https://github.com/the-h-team/Labyrinth/wiki/Annotation-Discovery)
  - [Base64 Serialization](https://github.com/the-h-team/Labyrinth/wiki/Base64-Serialization)
  - [Color Creation](https://github.com/the-h-team/Labyrinth/wiki/Color-stuff)
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
