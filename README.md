## What is Labyrinth?
It is an easy access spigot development library that assists you in areas that may otherwise have been far more time consuming.



[![](https://jitpack.io/v/the-h-team/Labyrinth.svg)](https://jitpack.io/#the-h-team/Labyrinth)
### Importing with maven
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
  	<dependency>
	    <groupId>com.github.the-h-team</groupId>
	    <artifactId>Labyrinth</artifactId>
	    <version>1.3.3</version>
	</dependency>
```
### Importing with gradle
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.the-h-team:Labyrinth:1.3.3'
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
  - [String Utility](https://github.com/the-h-team/Labyrinth/wiki/String-work.-Feel-the-magic.#1-string-formatting)
  - [Time Utility](https://github.com/the-h-team/Labyrinth/wiki/Get-with-the-times)
  - [Listing Collections](https://github.com/the-h-team/Labyrinth/wiki/String-work.-Feel-the-magic.#3-list-pagination)
  - [Automatic registration]()
  - [Base64 Serialization]()
  - [Labyrinth PDC Uses]()
  - [Custom GUI w/ MenuMan](https://github.com/the-h-team/Labyrinth/wiki/MenuMan-GUI-Tutorial)
  - [Applicable Data]()
  - [Task Scheduling]()

---
###### Labyrinth
A spigot development tool that makes certain tasks much easier.

*Original components licensed for use under the terms of the [GNU Lesser General Public License, version 2.1](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html).*

*Ships with shaded, unedited LGPLv3 components:*
- *from **VaultAPI** by [Morgan Humes](https://github.com/MilkBowl/) (aka MilkBowl); sources for these components can be found [here](https://github.com/MilkBowl/VaultAPI/).*
- *from **Enterprise** by [Sanctum](https://github.com/the-h-team/); sources for included components can be found [here](https://github.com/the-h-team/Enterprise).*

*Compiled + not-shaded & used MIT components:*
- *from **RGBApi** by [F1b3r](https://github.com/F1b3rDEV); sources for the used components can be found [here](https://github.com/F1b3rDEV/minecraft-spigot-rgb-chat-support).*

*You may inspect the [pom.xml](./pom.xml) for further detail on the shading process.*
