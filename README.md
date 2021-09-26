# paperkuts

## Introduction

A collection of libraries for Kotlin to make suite my style of Kotlin function names and usages.

As an short explanation. Consider the `Regex` class, and the use it on the standard library:

|       | Use Case                           | Usage                  |
| :---: | ---------------------------------- | ---------------------- |
|  1.   | Creating a new Regex               | `Regex()`              |
|  2.   | Creating a new Regex from a string | `someString.toRegex()` |

This could be replace by:

```kotlin
val re1 = regex("join\s+")
val re2 = "join\s+".regex() 
```

## Project Structure

The project will follow the package structure of the Kotlin standard library, but instead of using the prefix `kotlin`, or `kotlinx`, it will be `paperkuts`, for example:

| Standard package in Kotlin | Paperkuts package       |
| -------------------------- | ----------------------- |
| `kotlin`                   | `paperkuts`             |
| `kotlin.collections`       | `paperkuts.collections` |
