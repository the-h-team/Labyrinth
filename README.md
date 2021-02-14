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
	    <version>1.2.4R2</version>
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
	        implementation 'com.github.the-h-team:Labyrinth:1.2.4R2'
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
  - [Strings](https://github.com/the-h-team/Labyrinth/wiki/String-work.-Feel-the-magic.#1-string-formatting)
  - [Time Util](https://github.com/the-h-team/Labyrinth/blob/master/src/main/java/com/github/sanctum/labyrinth/library/TimeUtils.java)
  - [Listing Collections](https://github.com/the-h-team/Labyrinth/wiki/String-work.-Feel-the-magic.#3-list-pagination)
  - [Automatic registration]()
  - [Base64 Serialization]()
  - [PDC Uses]()
  - [Custom GUI]()

*More api additions are underway including a new runnable like interface*

---
Labyrinth
A spigot development tool that makes certain tasks much easier.
*License registered under CC LGPL 2.1*
