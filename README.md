# Realistic Firearms - NeoForge

A survival-friendly firearms mod for Minecraft 26.1.2 / NeoForge.

## Target
- Minecraft 26.1.2
- NeoForge 26.1.2.109
- Java 25
- NeoForge ModDevGradle

## Included weapons
Pistol, Revolver, SMG, Assault Rifle, Carbine, Battle Rifle, DMR, Bolt Rifle, Shotgun, LMG, Bullpup Rifle, PDW, Machine Pistol, and Heavy Rifle.

The project is structured for per-weapon ammunition, attachments, reload state, armor-aware damage, and ballistic projectile work. Weapon behavior is intentionally gameplay-focused rather than real-world weapon construction.

## Build
Use Java 25 and run:

```text
./gradlew build
```

On Windows:

```text
gradlew.bat build
```

The built JAR is written to `build/libs/`.

## Install
Put the generated JAR into the Minecraft 26.1.2 `mods` folder alongside NeoForge 26.1.2.
