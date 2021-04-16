## What is Labyrinth?
It is an easy access spigot development library that assists you in areas that may otherwise have been far more time consuming.



[![](https://jitpack.io/v/the-h-team/Labyrinth.svg)](https://jitpack.io/#the-h-team/Labyrinth)
### Importing with maven
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
  	<dependency>
	    <groupId>com.github.the-h-team</groupId>
	    <artifactId>Labyrinth</artifactId>
	    <version>1.4.5</version>
	</dependency>
```
### Importing with gradle
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.the-h-team:Labyrinth:1.4.5'
	}
```

## What are its key points?
+ Easy collection pagination
+ Automatic command and listener registration
+ Time and string utilities for color translation & more (Random unique/custom ID's, Sorted maps)
+ Custom object Persistent Data Container using Base64 serialization
+ Easy access to said serialization above using object constructors for string/object conversion.
+ Easy common library access to amazing plugins like (Vault & PlaceholderAPI)
& More!


_API Table of Contents_:
--
  - [String Utility](https://github.com/the-h-team/Labyrinth/blob/2c8f1ac9bd9d764badc28d758a2cd4b995972e25/src/main/java/com/github/sanctum/labyrinth/library/StringUtils.java#L28)
  - [Time Utility](https://github.com/the-h-team/Labyrinth/wiki/Get-with-the-times)
  - [Listing Collections](https://github.com/the-h-team/Labyrinth/wiki/String-work.-Feel-the-magic.#3-list-pagination)
  - [Automatic Command Pickup]()
  - [Base64 Serialization]()
  - [Persistent Data Container]()
  - [Singular GUI](https://github.com/the-h-team/Labyrinth/wiki/MenuMan-GUI-Tutorial)
  - [Applicable Data]()
  - [Economy Interface Wrapper]()
  - [Centered Cardinal Directions]()
  - [EntityType Matcher]()
  - [Material Matcher]()
  - [Item Recipe Builder]()
  - [Player/Console Message formatter]()
  - [Player Skull Finder]()
  - [Tab Completion Builder]()
  - [Paginated GUI]()
  - [Colored Component Builder]()
  - [File Manager]()
  - [Automatic Listener Pickup]()
  - [Shareable/Interactable GUI]()
  - [Cooldown Interface](https://github.com/the-h-team/Labyrinth/wiki/Cooldowns)
  - [Custom ID Generation]()
  - [Unique ID Object]()
  - [Task Scheduling]()

---
###### Labyrinth
A spigot development tool that makes certain tasks much easier.

*Original components licensed for use under the terms of the [GNU Lesser General Public License, version 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html).*

*Ships with shaded, unedited LGPLv3 components:*
- *from **VaultAPI** by [Morgan Humes](https://github.com/MilkBowl/) (aka MilkBowl); sources for these components can be found [here](https://github.com/MilkBowl/VaultAPI/).*
- *from **Enterprise** by [Sanctum](https://github.com/the-h-team/); sources for included components can be found [here](https://github.com/the-h-team/Enterprise).*

*MIT-derived components:*
- *Java port of **RGBApi** by [F1b3r](https://github.com/F1b3rDEV); original Kotlin sources can be found [here](https://github.com/F1b3rDEV/minecraft-spigot-rgb-chat-support).*

*You may inspect the [pom.xml](./pom.xml) for further detail on the shading process.*
