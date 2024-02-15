# Reflecto

Java Deep Reflection Library

# Documentation

Please, see the recently published documentation [here](https://www.cariochi.com/reflecto). 

# Maven dependency

```markup
<dependency>
    <groupId>com.cariochi.reflecto</groupId>
    <artifactId>reflecto</artifactId>
    <version>1.2.0</version>
</dependency>
```

# Examples

##Type Creation

```java
// MyType<String, Long>
Type myType = Types.type(MyType.class, String.class, Long.class); 

// List<Double>
Type listType = Types.listOf(Double.class); 

// Set<String>
Type setType = Types.setOf(String.class); 

// Map<String, Integer>
Type mapType = Types.mapOf(String.class, Integer.class);

// List<String>[]
Type arrayOfListType = Types.arrayOf(Types.listOf(String.class));

// List<Map<String, Integer>>
Type nestedGenericType = Types.type(List.class, Types.mapOf(String.class, Integer.class));

// Map<String, Supplier<User>>
Type complexMapType = Types.mapOf(String.class, Types.type(Supplier.class, User.class));

// List<?>
Type unboundedWildcardType = Types.listOf(Types.any());

// List<? extends Number>
Type upperBoundedWildcardType = Types.listOf(Types.anyExtends(Number.class));

// List<? super Integer>
Type lowerBoundedWildcardType = Types.listOf(Types.anySuper(Integer.class));

// Map<String, List<? extends Serializable>>
Type complexNestedType = Types.mapOf(String.class, Types.listOf(Types.anyExtends(Serializable.class)));

// List<String>
Type typeByName = Types.type("java.util.List<java.lang.String>");

// List<Supplier<? extends MyType>>
Type complexTypeByName = Types.type("java.util.List<java.util.function.Supplier<? extends com.cariochi.reflecto.examples.MyType>>");

```

## Reflecting Types

### Inspecting Types
```java
// Example class
public static class Dto<T> {
    private T value;
    private Dto<T> child;
    private Set<Dto<T>> set;
    private Map<String, Set<Dto<T>>> map;
}

Type type = Types.type(Dto.class, Integer.class);
final ReflectoType reflectoType = Reflecto.reflect(type);

// type of first argument
assertThat(reflectoType.arguments().get(0).actualType()).isEqualTo(Integer.class);
assertThat(reflectoType.reflect("[0]").actualType()).isEqualTo(Integer.class)

// type of the 'value' field
assertThat(reflectoType.reflect("value").actualType()).isEqualTo(Integer.class);

// type of the 'child.value' nested field
assertThat(reflectoType.reflect("child.value").actualType()).isEqualTo(Integer.class);

// type of the first argument (T) of the first argument (Dto<T>) of the 'set' field type (Set<Dto<T>>)
assertThat(reflectoType.reflect("set[0][0]").actualType()).isEqualTo(Integer.class);

// type of the 'value' field (T) of the first argument (Dto<T>) of the 'set' field type (Set<Dto<T>>)
assertThat(reflectoType.reflect("set[0].value").actualType()).isEqualTo(Integer.class);

// type of the first argument (String) of the nested 'child.map' field type (Map<String, Set<Dto<T>>>)
assertThat(reflectoType.reflect("child.map[0]").actualType()).isEqualTo(String.class);

// type of the 'value' field (T) of the first argument (Dto<T>) of the second argument (Set<Dto<T>>) of the 'child.map' nested field type (Map<String, Set<Dto<T>>>)
assertThat(reflectoType.reflect("child.map[1][0].value").actualType()).isEqualTo(Integer.class);

```

### Constructors
```java
ReflectoType type = reflect(Types.type(ArrayList.class, String.class));

List<ReflectoConstructor> constructors = type.constructors().list();
List<ReflectoConstructor> declaredConstructors = type.declared().constructors().list();

// find constructor by parameter types
ReflectoConstructor constructor = type.constructors()
    .find(Collection.class)
    .orElseThrow();

Object instance = constructor.newInstance(Set.of(1));
```

### Methods
```java
// lists methods
List<ReflectoMethod> methods = type.methods().list();
List<ReflectoMethod> declaredMethods = type.declared().methods().list();
List<ReflectoMethod> includeEnclosingMethods = type.includeEnclosing().methods().list();

// find and invoke method
ReflectoMethod method = type.methods().find("setUsername(?)", String.class).orElseThrow();
TargetMethod targetMethod = method.withTarget(user);
targetMethod.invoke("test_user");

// filter methods
List<ReflectoMethods> postProcessors = type.declared().methods().stream()
    .filter(method -> method.modifiers().isPublic())
    .filter(method -> method.annotations().contains(PostProcessor.class))
    .filter(method -> method.returnType().is(void.class))
    .filter(method -> method.parameters().size() == 1)
    .collect(Collectors.toList());

// find and invoke a static method
ReflectoMethod method = type.methods().find("sayHello(?)", String.class).orElseThrow();
TargetMethod staticMethod = method.asStatic();
String result = staticMethod.invoke("World");
```

### Fields
```java
// list fields
List<ReflectoField> fields = type.fields().list();
List<ReflectoField> declaredFields = type.declared().fields().list();
List<ReflectoField> includeEnclosingFields = type.includeEnclosing().fields().list();

// find field
ReflectoField field = type.fields().find("username").orElseThrow();
TargetField targetField = field.withTarget(user);
String username = targetField.getValue();
targetField.

setValue("test_user");

// filter fields
List<ReflectoField> fields = type.declared().fields().stream()
    .filter(field -> field.modifiers().isPrivate())
    .filter(field -> field.annotations().contains(NotNull.class))
    .filter(field -> field.type().is(String.class))
    .collect(toList());

// find static field
ReflectoField field = type.fields().find("NAME", String.class).orElseThrow();
TargetField staticField = field.asStatic();
String name = staticField.getValue();
staticField.

setValue("New Name");
```

### Working with Arrays and Enums
```java
ReflectoType arrayType = Reflecto.reflect(String[].class);
boolean isArray = arrayType.isArray();
ReflectoType componentType= asArray().componentType();

ReflectoType enumType = Reflecto.reflect(MyEnum.class);
boolean isEnum = enumTypee.isEnum();
List<Object> enumType = enumType.asEnum().constants();
```

### Methods for Type Checking
```java
ReflectoType type = Reflecto.reflect(Types.listOf(String.class));

assertThat(type.is(Iterable.class)).isTrue();
assertThat(type.is(Types.type(Iterable.class, String.class))).isTrue();
assertThat(type.is(Types.type(Iterable.class, Long.class))).isFalse();

assertThat(type.as(Iterable.class).arguments().get(0).actualType()).isEqualTo(String.class);
                
assertThat(type.isAssignableFrom(ArrayList.class)).isTrue();
assertThat(type.isAssignableFrom(Types.type(ArrayList.class, String.class))).isTrue();
assertThat(type.isAssignableFrom(Types.type(ArrayList.class, Long.class))).isFalse();

assertThat(type.isInstance(new ArrayList<>())).isTrue();
```

## Reflecting Objects

### Initial Object for Examples
```java
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
        .build();
```

### Methods
```java
List<TargetMethod> methods = Reflecto.reflect(bug).methods().list();

User reporter = Reflecto.reflect(bug).methods().find("getReporter()")
    .map(TargetMethod::invoke)
    .map(User.class::cast)
    .orElseThrow();
    
Reflecto.reflect(bug).methods().find("setReporter(?)", User.class)
    .ifPresent(method -> method.invoke(reporter));    
```

### Fields
```java
List<TargetField> fields = Reflecto.reflect(bug).fields().list();

User reporter = Reflecto.reflect(bug).fields().find("reporter")
    .map(TargetField::getValue)
    .map(User.class::cast)
    .orElseThrow();
    
Reflecto.reflect(bug).fields().find("reporter")
    .ifPresent(field -> field.setValue(reporter));
```

### Simple Field Access and Modification
```java
// Getting Value

String username = Reflecto.reflect(bug)
        .reflect("reporter.username")
        .getValue();

String username = Reflecto.reflect(bug)
        .invoke("reporter.username");
        
// Setting Value

Reflecto.reflect(bug)
        .reflect("reporter.username")
        .setValue("new_name");

Reflecto.reflect(bug)
        .invoke("reporter.username=?", "new_name");
```
### Working with Lists/Arrays
```java
// Getting Value

String username = Reflecto.reflect(bug)
        .reflect("watchers[0].username")
        .getValue();

String username = Reflecto.reflect(bug)
        .reflect("watchers[?].username", 0)
        .getValue();
        
String username = Reflecto.reflect(bug)
        .invoke("watchers[0].username");
        
String username = Reflecto.reflect(bug)
        .invoke("watchers[?].username", 0);
        
// Setting Value

Reflecto.reflect(bug)
        .reflect("watchers[0]")
        .setValue(new User("user4"));

Reflecto.reflect(bug)
        .reflect("watchers[?]", 0)
        .setValue(new User("user4"));
        
Reflecto.reflect(bug)
        .invoke("watchers[0]=?", new User("user4"));
        
Reflecto.reflect(bug)
        .invoke("watchers[?]=?", 0, new User("user4"));
```

### Working with Maps
```java
// Getting Value

String sprint = Reflecto.reflect(bug)
        .reflect("details[Sprint].text")
        .getValue();
        
String sprint = Reflecto.reflect(bug)
        .reflect("details[?].text", "Sprint")
        .getValue();
        
String sprint = Reflecto.reflect(bug)
        .invoke("details[Sprint].text");
        
String sprint = Reflecto.reflect(bug)
        .invoke("details[?].text", "Sprint");
        
// Setting Value

Reflecto.reflect(bug)
        .reflect("details[Sprint]")
        .setValue(new Details("SPR-002"));

Reflecto.reflect(bug)
        .reflect("details[?]", "Sprint")
        .setValue(new Details("SPR-002"));
        
Reflecto.reflect(bug)
        .invoke("details[Sprint]=?", new Details("SPR-002"));
        
Reflecto.reflect(bug)
        .invoke("details[?]=?", "Sprint", new Details("SPR-002"));
```

### Invoking Methods
```java
String username = Reflecto.reflect(bug)
        .reflect("getWatchers().get(?).getUsername()", 0)
        .getValue();

Reflecto.reflect(bug)
        .invoke("getWatchers().get(?).setUsername(?)", 0, "new_name");
        
Reflecto.reflect(bug)
        .invoke("getDetails().remove(?)", "Sprint");
        
Reflecto.reflect(bug)
        .invoke("getDetails().put(?, ?)", "Sprint", new Details("SPR-002"));
```

### Mixed Usage
```java
String username = Reflecto.reflect(bug)
        .reflect("watchers[0].getUsername()")
        .getValue();

String username = Reflecto.reflect(bug)
        .reflect("watchers[?].getUsername()", 0)
        .getValue();
        
String username = Reflecto.reflect(bug)
        .reflect("getWatchers()[0].getUsername()")
        .getValue();
        
String username = Reflecto.reflect(bug)
        .reflect("getWatchers()[?].getUsername()", 0)
        .getValue();

Reflecto.reflect(bug)
        .invoke("getWatchers().get(?).username=?", 0, "new_name");
        
Reflecto.reflect(bug)
        .invoke("details[Sprint].setText(?)", "SPR-002");
        
Reflecto.reflect(bug)
        .invoke("details[?].setText(?)", "Sprint", "SPR-002");
```

# License

**Reflecto** library is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). 
