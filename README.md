# Reflecto

Reflecto is a Java reflection library focused on three tasks that are usually verbose in plain Java reflection:

- reading, updating, and invoking through a single expression language
- creating and reasoning about generic `Type` instances
- inspecting classes, methods, fields, constructors, parameters, and proxy types through a fluent API

The library is designed for runtime tooling, framework code, metadata-heavy libraries, and internal infrastructure where native reflection APIs are correct but too low-level.

## Features

- Unified `perform(...)` and `reflect(...)` API for fields, methods, arrays, lists, sets, maps, and nested object graphs
- Generic-aware type utilities for `List<T>`, `Map<K, V>`, arrays, wildcards, `Optional<T>`, and `Stream<T>`
- Fluent type inspection with `ReflectoType`
- Fluent member inspection for methods, fields, constructors, parameters, annotations, exceptions, and modifiers
- Generic-aware proxy generation for abstract classes and interfaces
- Detailed lookup failures via `NotFoundException`

## Installation

Reflecto requires Java 17 or newer.

```xml
<dependency>
    <groupId>com.cariochi.reflecto</groupId>
    <artifactId>reflecto</artifactId>
    <version>2.1.0</version>
</dependency>
```

## Quick Start

```java
import com.cariochi.reflecto.Reflecto;
import com.cariochi.reflecto.invocations.model.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

Bug bug = Bug.builder()
        .id(1)
        .summary("Invalid value")
        .reporter(new User(100, "qa"))
        .watchers(new ArrayList<>(List.of(
                new User(101, "developer"),
                new User(102, "manager")
        )))
        .details(new HashMap<>(Map.of(
                "Sprint", "SPR-001",
                "Component", "Authorization"
        )))
        .tags(new String[]{"role", "user", "auth"})
        .build();

Reflection reflection = Reflecto.reflect(bug);

String username = reflection.perform("reporter.username");
String sprint = reflection.perform("details[Sprint]");

reflection.perform("summary=?", "Modified bug");
reflection.perform("watchers[0].username=?", "java-dev");
reflection.perform("getWatchers().get(?).setId(?)", 1, 1002);
reflection.perform("details[?]=?", "Sprint", "SPR-002");
```

## Core Entry Points

`Reflecto` exposes a small set of static entry points:

```java
Reflecto.reflect(Type type)                // -> ReflectoType
Reflecto.reflect(Object instance)          // -> Reflection
Reflecto.reflect(Field field)              // -> ReflectoField
Reflecto.reflect(Method method)            // -> ReflectoMethod
Reflecto.reflect(Parameter parameter)      // -> ReflectoParameter
Reflecto.reflect(Constructor<?> ctor)      // -> ReflectoConstructor
Reflecto.proxy(Type... types)              // -> ProxyType
```

In practice:

- use `Reflecto.reflect(object)` when you want to work with a live instance
- use `Reflecto.reflect(type)` when you want metadata and generic type analysis
- use `Reflecto.proxy(...)` when you want a runtime proxy class with preserved generic information

## Working with Objects

### `perform(...)` vs `reflect(...)`

`Reflection.perform(expression, args...)` executes an expression and returns the resulting value.

`Reflection.reflect(expression, args...)` executes the same expression but returns another `Reflection`, which lets you continue navigation or call `getValue()` / `setValue(...)`.

```java
Reflection bug = Reflecto.reflect(bugInstance);

String summary = bug.perform("summary");
bug.perform("summary=?", "Updated summary");

Reflection reporter = bug.reflect("reporter");
String username = reporter.perform("username");
reporter.reflect("username").setValue("lead");
```

### Expression Syntax

Reflecto expressions are composed left to right and can mix fields, methods, collection access, and assignment.

#### Field access

```java
bug.perform("summary");
bug.perform("reporter.username");
```

#### Method invocation

```java
bug.perform("getReporter()");
bug.perform("getReporter().getUsername()");
bug.perform("setSummary(?)", "Updated summary");
```

#### List and array indexing

```java
bug.perform("watchers[0].username");
bug.perform("tags[1]");

bug.perform("watchers.get(?).username", 1);
bug.perform("getTags()[?]=?", 0, "roles");
```

#### Map access

```java
bug.perform("details[Sprint]");
bug.perform("details[?]", "Sprint");
bug.perform("getDetails().put(?,?)", "Sprint", "SPR-002");
```

#### Assignment

Assignment is expressed by appending `=?` to the final segment.

```java
bug.perform("summary=?", "Modified bug");
bug.perform("reporter.username=?", "qa-lead");
bug.perform("watchers[0].id=?", 1001);
bug.perform("details[?]=?", "Component", "Auth");
```

#### Wildcard iteration with `[*]`

`[*]` applies an operation to every element in an array, `List`, `Set`, `Map`, or generic `Iterable`.

```java
Reflecto.reflect(dto).perform("list[*].name=?", "TEST");
Reflecto.reflect(dto).perform("map[*].name=?", "TEST");
Reflecto.reflect(dto).perform("stringArray[*]=?", "TEST");
Reflecto.reflect(dtos).perform("[*].list[*].name=?", "TEST");
```

Wildcard reads return a flattened list of values:

```java
List<String> names = Reflecto.reflect(dto).perform("set[*].set[*].name");
```

### Placeholder Arguments

`?` placeholders are filled from `perform(...)` / `reflect(...)` arguments in encounter order.

```java
bug.perform("watchers.get(?).username=?", 0, "java-dev");
bug.perform("details[?]=?", "Sprint", "SPR-002");
```

### Direct Member API on Objects

If you want explicit field or method handles instead of the expression language:

```java
TargetField field = Reflecto.reflect(bugInstance)
        .fields()
        .get("summary");

field.setValue("Updated summary");

TargetMethod method = Reflecto.reflect(bugInstance)
        .methods()
        .get("setSummary(?)", String.class);

method.invoke("Updated again");
```

## Working with Types

### Creating Generic Types with `Types`

`Types` is the builder API for `Type` objects.

```java
import com.cariochi.reflecto.types.Types;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

Type listOfString = Types.listOf(String.class);
Type setOfLong = Types.setOf(Long.class);
Type mapType = Types.mapOf(String.class, Integer.class);
Type optionalUser = Types.optionalOf(User.class);
Type streamOfIds = Types.streamOf(Long.class);

Type wildcardList = Types.listOf(Types.any());
Type upperBound = Types.listOf(Types.anyExtends(Number.class));
Type lowerBound = Types.listOf(Types.anySuper(Integer.class));

Type nested = Types.type(List.class, Types.type(Map.class, String.class, Integer.class));
Type array = Types.arrayOf(Types.listOf(String.class));
```

### Parsing Types from Strings

Reflecto can parse complex type declarations directly from strings and turn them into runtime `Type` instances.

```java
Type parsed = Types.type("java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>");
Type wildcard = Types.type("java.util.List<? extends java.lang.Number>");
Type array = Types.type("java.util.List<java.lang.String>[][]");
```

### Inspecting Types with `ReflectoType`

```java
Type type = Types.type(List.class, String.class);
ReflectoType reflectoType = Reflecto.reflect(type);

reflectoType.name();                                      // java.util.List<java.lang.String>
reflectoType.actualType();                                // Type
reflectoType.actualClass();                               // List.class
reflectoType.is(List.class);                              // true
reflectoType.is(Types.type(List.class, String.class));    // true
reflectoType.is(Types.type(List.class, Integer.class));   // false
reflectoType.isArray();                                   // false
reflectoType.isEnum();                                    // false
```

#### Generic-aware type checks

`ReflectoType.is(...)` and `ReflectoType.isAssignableFrom(...)` work with full generic type information, not only raw classes.

```java
ReflectoType type = Reflecto.reflect(Types.listOf(String.class));

type.is(java.util.Collection.class);                                      // true
type.is(Types.type(java.util.Collection.class, String.class));            // true
type.is(Types.type(java.util.Collection.class, Long.class));              // false

type.isAssignableFrom(java.util.ArrayList.class);                         // true
type.isAssignableFrom(Types.type(java.util.ArrayList.class, String.class)); // true
type.isAssignableFrom(Types.type(java.util.ArrayList.class, Long.class));   // false
```

#### Generic arguments

```java
ReflectoType listType = Reflecto.reflect(Types.listOf(String.class));
ReflectoType itemType = listType.arguments().get(0);

assert itemType.actualType().equals(String.class);
```

#### Resolving nested generic fields

`ReflectoType.reflect(expression)` resolves the type behind a nested path and keeps generic substitutions from the root type.

Assume this DTO:

```java
class Dto<T> {
    T value;
    Dto<T> child;
    Map<String, Set<Dto<T>>> map;
}
```

If the root type is `Dto<Integer>`, Reflecto resolves every `T` as `Integer` while navigating through fields.

```java
Type dtoType = Types.type(Dto.class, Integer.class);
ReflectoType dto = Reflecto.reflect(dtoType);

dto.reflect("value").actualType();         // Integer
dto.reflect("child.value").actualType();   // Integer
dto.reflect("child.map").actualType();     // Map<String, Set<Dto<Integer>>>
dto.reflect("child").actualType();         // Dto<Integer>
```

After any segment that resolves to a parameterized type, `[0]`, `[1]`, and so on navigate through its generic arguments.

```java
dto.reflect("map[0]").actualType();           // String
dto.reflect("map[1]").actualType();           // Set<Dto<Integer>>
dto.reflect("map[1][0]").actualType();        // Dto<Integer>
dto.reflect("map[1][0].value").actualType();  // Integer
```

This is type navigation only. It does not read a map entry, list element, or array item from a runtime object.

#### Arrays and enums

Use `isArray()` / `asArray()` and `isEnum()` / `asEnum()` when you need array- or enum-specific behavior.

```java
ReflectoType arrayType = Reflecto.reflect(String[][].class);
boolean array = arrayType.isArray();
ReflectoType component = arrayType.asArray().componentType();

ReflectoType enumType = Reflecto.reflect(MyEnum.class);
boolean isEnum = enumType.isEnum();
List<MyEnum> constants = enumType.asEnum().constants().list();
MyEnum value = enumType.asEnum().constants().get("OPEN");
```

#### Super types and interfaces

```java
ReflectoType type = Reflecto.reflect(ArrayList.class);

ReflectoType superType = type.superType();
List<ReflectoType> allSupers = type.allSuperTypes();
List<ReflectoType> directInterfaces = type.interfaces();
List<ReflectoType> allInterfaces = type.allInterfaces();
```

## Inspecting Members

### Fields

```java
ReflectoType type = Reflecto.reflect(MyClass.class);

ReflectoField field = type.fields().get("name");
String fieldName = field.name();
ReflectoType fieldType = field.type();
boolean isStatic = field.modifiers().isStatic();
boolean hasIdAnnotation = field.annotations().stream().anyMatch(a -> a.annotationType() == Id.class);
```

On instances:

```java
TargetField field = Reflecto.reflect(myObject).fields().get("name");
String value = field.getValue();
field.setValue("new value");
```

### Methods

Method lookup is overload-aware and uses the supplied argument types.

```java
ReflectoMethod method = Reflecto.reflect(User.class)
        .methods()
        .get("sayHello(?)", String.class);

String name = method.name();
ReflectoType returnType = method.returnType();
List<ReflectoParameter> parameters = method.parameters().list();
List<ReflectoType> exceptions = method.exceptions().list();
boolean isPublic = method.modifiers().isPublic();
```

Invocation styles:

```java
User user = User.builder().username("qa").build();
ReflectoMethod method = Reflecto.reflect(User.class).methods().get("sayHello(?)", String.class);

String result1 = method.withTarget(user).invoke("Vadym");
String result2 = method.withArguments("Vadym").withTarget(user).invoke();
String result3 = method.withTarget(user).withArguments("Vadym").invoke();
```

Static methods use `asStatic()`:

```java
ReflectoMethod method = Reflecto.reflect(Util.class).methods().get("parse(?)", String.class);
Object value = method.asStatic().invoke("42");
```

### Constructors

```java
ReflectoType type = Reflecto.reflect(ArrayList.class);

ReflectoConstructor ctor = type.constructors().get(java.util.Collection.class);
ArrayList<?> list = ctor.newInstance(List.of("a", "b"));
```

Constructor metadata:

```java
ctor.name();
ctor.parameters().types();
ctor.exceptions().list();
ctor.modifiers().isPublic();
ctor.annotations().declared().list();
```

### Declared vs inherited members

For fields, methods, constructors, annotations, and nested types, Reflecto distinguishes between:

- resolved view: `fields()`, `methods()`, `constructors()`, `types()`
- declared-only view: `fields().declared()`, `methods().declared()`, `constructors().declared()`, `types().declared()`

Example:

```java
ReflectoType type = Reflecto.reflect(MyList.class);

long allMethods = type.methods().size();
long declaredMethods = type.methods().declared().size();
```

## Proxies

Reflecto can generate a runtime proxy class for:

- one abstract or concrete superclass
- zero or more interfaces
- parameterized types, while preserving generic metadata on the generated class

### Creating a proxy type

```java
ProxyType proxyType = Reflecto.proxy(
        Types.type(AbstractDto.class, Double.class),
        Types.type(HasValue.class, String.class),
        HasName.class
);

ReflectoType generatedType = proxyType.type();
```

### Creating proxy instances

There are two ways to provide the invocation handler:

```java
// Handler class with matching constructor arguments
AbstractDto<Double> instance1 = Reflecto.proxy(
        Types.type(AbstractDto.class, Double.class),
        HasName.class
).with(DtoProxyHandler.class)
 .getConstructor(String.class, Double.class)
 .newInstance("Name", 50.0);

// Handler supplier
MyAbstractClass instance2 = Reflecto.proxy(MyAbstractClass.class)
        .with(MyProxyHandler::new)
        .getConstructor(int.class)
        .newInstance(1);
```

### Invocation handler contract

```java
public interface InvocationHandler {
    Object invoke(Object proxy, ReflectoMethod thisMethod, Object[] args, TargetMethod proceed) throws Throwable;
}
```

`proceed` is non-null when there is a concrete implementation to delegate to.

```java
public Object invoke(Object proxy, ReflectoMethod thisMethod, Object[] args, TargetMethod proceed) {
    if (proceed != null) {
        return proceed.invoke(args);
    }
    return thisMethod.name() + " was invoked";
}
```

### Reading the handler from an existing proxy

```java
Optional<InvocationHandler> handler = ProxyFactory.getHandler(proxyInstance);
```

## Modifiers, Annotations, Parameters, Exceptions

These helper APIs are available across fields, methods, constructors, and parameters:

- `annotations()` and `annotations().declared()`
- `modifiers().isPublic()`, `isPrivate()`, `isStatic()`, `isAbstract()`, `isFinal()`, and others
- `parameters().list()` and `parameters().types()`
- `exceptions().list()`

Example:

```java
ReflectoMethod method = Reflecto.reflect(MyClass.class).methods().get("handle(?)", Request.class);

boolean publicMethod = method.modifiers().isPublic();
List<ReflectoParameter> params = method.parameters().list();
List<ReflectoType> thrown = method.exceptions().list();
```

## Error Handling

Lookup APIs throw `NotFoundException` with contextual messages when a member cannot be resolved.

Typical cases:

- `methods().get(...)` when no overload matches
- `constructors().get(...)` when no constructor matches
- `fields().get(...)` when a field is absent
- `ReflectoType.reflect(...)` when a type path cannot be resolved

Examples of guarded alternatives:

```java
Optional<ReflectoMethod> method = Reflecto.reflect(MyClass.class)
        .methods()
        .find("setName(?)", String.class);

Optional<ReflectoConstructor> ctor = Reflecto.reflect(MyClass.class)
        .constructors()
        .find(String.class);
```

Invalid collection access produces `IllegalArgumentException`, for example when using a non-integer array/list index.

## Notes and Practical Limits

- `perform(...)` is most useful for runtime tooling and infrastructure code. It is not a replacement for normal method calls in regular business code.
- Method lookup is based on runtime argument types. When overload selection matters, prefer explicit APIs such as `methods().get(name, argTypes...)`.
- Generic-aware type resolution is available through `ReflectoType` and `Types`.
- `[*]` reads return flattened lists. `[*]` writes update every element in the traversed collection.
- Direct writes through `set[*]=?` replace every value in the set with the provided value because positional assignment is not meaningful for sets.

## License

Reflecto is licensed under the [Apache License 2.0](LICENSE).
