# Introduction

**Reflecto** is an open‑source Java library that makes reflection simple and expressive. It is built around three **key features** that save you boilerplate and let you work with objects and types in a natural way:

1. **`perform(...)`** – a unified method to call any method or read/write any field, even deep inside nested structures (collections, maps, arrays).
2. **`Types` class** – a fluent API to create and manipulate `Type` objects, especially useful for generics, arrays, wildcards, Optional, and Stream types.
3. **Reflecting types and objects** – inspect and manipulate both type metadata (`ReflectoType`) and real instances (`Reflecto.reflect(obj)`).

These three ideas form the heart of Reflecto. Below you’ll find examples to get started quickly.

# Getting Started

## Maven Dependency

```xml
<dependency>
    <groupId>com.cariochi.reflecto</groupId>
    <artifactId>reflecto</artifactId>
    <version>2.0.2</version>
</dependency>
```

## Quick Example

```java
Bug bug = Bug.builder()
    .id(1)
    .reporter(new User(100, "qa"))
    .build();

// Access nested field
String username = Reflecto.reflect(bug).perform("reporter.username");

// Modify nested field
Reflecto.reflect(bug).perform("reporter.username=?", "new_name");

// Call method
Reflecto.reflect(bug).perform("getReporter().setUsername(?)", "test_user");
```

# Core Features

## Unified `perform`

`perform` is the fastest way to access fields and methods through a single expressive syntax.

* **Fields**: `object.field`, `nested.field`
* **Lists/arrays**: `list[0]`, `list[?]`
* **Maps**: `map[key]`, `map[?]`
* **Methods**: `method()`, `method(?)`
* **Mixed usage**: combine everything seamlessly.

### Examples

```java
// Read value
String username = Reflecto.reflect(bug).perform("reporter.username");

// Write value
Reflecto.reflect(bug).perform("reporter.username=?", "new_name");

// Collections
Reflecto.reflect(bug).perform("watchers[0].username=?", "dev");

// Maps
Reflecto.reflect(bug).perform("details[Sprint].text=?", "SPR-002");
Reflecto.reflect(bug).perform("details[?].text=?", "Sprint", "SPR-002");

// Mixed usage
String name = Reflecto.reflect(bug).perform("getWatchers().get(?).username", 0);
```

## The `Types` Class

Work with generics and complex type signatures easily.

### Examples

```java
// Simple generics
Type listStr = Types.listOf(String.class);                          // List<String>
Type map = Types.mapOf(String.class, Types.listOf(Integer.class));  // Map<String, List<Integer>>

// Wildcards
Type anyList = Types.listOf(Types.any());                           // List<?>
Type upperBound = Types.listOf(Types.anyExtends(Number.class));     // List<? extends Number>
Type lowerBound = Types.listOf(Types.anySuper(Integer.class));      // List<? super Integer>

// Optionals and streams
Type optUser = Types.optionalOf(User.class);                        // Optional<User>
Type ids = Types.streamOf(Long.class);                              // Stream<Long>

// From string
String typeName = "java.util.List<java.util.Map<java.lang.String, java.lang.Integer>>";
Type parsed = Types.type(typeName);
```

## Reflecting Types and Objects

### Reflecting Types (`ReflectoType`)

Inspect constructors, methods, fields, modifiers, generics.

```java
Type type = Types.type(ArrayList.class, String.class);
ReflectoType reflectoType = Reflecto.reflect(type);

boolean isIterable = reflectoType.is(Iterable.class);
Class<?> arg = reflectoType.as(Iterable.class).arguments().get(0).actualType(); // String.class
```

### Reflecting Objects

Work directly with real instances.

```java
// Read field value
String username = Reflecto.reflect(bug)
    .reflect("reporter.username")
    .getValue();

// Update field value
Reflecto.reflect(bug)
    .reflect("reporter.username")
    .setValue("new_name");

// Call method
Reflecto.reflect(bug).perform("getDetails().put(?, ?)", "Sprint", new Details("SPR-003"));
```

# Advanced Usage

## Filtering Methods

```java
List<ReflectoMethod> setters = Reflecto.reflect(MyService.class).methods().declared().stream()
        .filter(m -> m.name().startsWith("set"))
        .filter(m -> m.parameters().size() == 1)
        .filter(m -> m.modifiers().isPublic())
        .toList();
```

## Arrays

```java
ReflectoType arrayType = Reflecto.reflect(String[][].class);
if (arrayType.isArray()) {
ReflectoType comp = arrayType.asArray().componentType();
}
```

## Enums

```java
ReflectoType enumType = Reflecto.reflect(MyEnum.class);
List<MyEnum> constants = enumType.asEnum().constants().list();
```

## Constructors

```java
ReflectoConstructor ctor = Reflecto.reflect(ArrayList.class).constructors().get(Collection.class);
Object instance = ctor.newInstance(Set.of("a", "b"));
```

## Safe method lookup

```java
Reflecto.reflect(bug).methods().find("setReporter(?)", User.class)
    .ifPresent(m -> m.perform(new User(200, "lead")));
```

# Quick Recipes

## Static method

```java
ReflectoMethod m = Reflecto.reflect(Util.class).methods().get("sayHello(?)", String.class);
String result = m.asStatic().perform("World");
```

## Static field

```java
ReflectoField f = Reflecto.reflect(Config.class).fields().get("NAME", String.class);
String name = f.asStatic().getValue();
```

## Assignability

```java
ReflectoType listStr = Reflecto.reflect(Types.listOf(String.class));
boolean ok = listStr.isAssignableFrom(Types.type(ArrayList.class, String.class)); // true
```

# Collections & Maps Advanced

## Access deep structures

```java
String sprint = Reflecto.reflect(bug).perform("details[Sprint].text");
String reporterOfComponent = Reflecto.reflect(bug)
        .perform("details[Component].owner.getWatchers().get(?).getUsername()", 0);
```

## Update elements

```java
Reflecto.reflect(bug).perform("watchers[1].username=?", "tech_lead");
Reflecto.reflect(bug).perform("details[Sprint]=?", new Details("SPR-010"));
```

## Insert/remove entries

```java
Reflecto.reflect(bug).perform("getDetails().put(?, ?)", "Epic", new Details("SEC-001"));
        Reflecto.reflect(bug).perform("getDetails().remove(?)", "Sprint");
```

# License

Reflecto is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
