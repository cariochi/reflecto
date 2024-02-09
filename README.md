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

## Types

### TypeReference

```java
TypeReference<Map<String, Integer>> typeReference = new TypeReference<>() {};
Type type = typeReference.getType();

assertThat(type.getTypeName())
        .isEqualTo("java.util.Map<java.lang.String, java.lang.Integer>");
```

### Types

```java
Type type = Types.type(Map.class, String.class, Integer.class);

assertThat(type.getTypeName())
        .isEqualTo("java.util.Map<java.lang.String, java.lang.Integer>");
```


## Fields and methods

Example object:

```java
Bug bug = Bug.builder()
        .reporter(new User("user1"))
        .watchers(List.of(
                new User("user2"),
                new User("user3")
        ))
        .details(Map.of(
                "Sprint",new Details("SPR-001"),
                "Component",new Details("Authorization")
        ))
        .build();
```

### Fields

#### Simple

Get Value

```java
String username = Reflecto.reflect(bug)
        .reflect("reporter.username")
        .getValue();
```

```java
String username = Reflecto.reflect(bug)
        .invoke("reporter.username");
```

Set Value

```java
Reflecto.reflect(bug)
        .reflect("reporter.username")
        .setValue("new_name");
```

```java
Reflecto.reflect(bug)
        .invoke("reporter.username=?","new_name");
```

#### List / Array

Get Value

```java
String username = Reflecto.reflect(bug)
        .reflect("watchers[0].username")
        .getValue();
```

```java
String username = Reflecto.reflect(bug)
        .invoke("watchers[0].username");
```

Set Value

```java
Reflecto.reflect(bug)
        .reflect("watchers[0]")
        .setValue(new User("user4"));
```

```java
Reflecto.reflect(bug)
        .invoke("watchers[0]=?", new User("user4"));
```

```java
Reflecto.reflect(bug)
        .invoke("watchers[?]=?", 0, new User("user4"));
```

#### Map

Get Value

```java
String sprint = Reflecto.reflect(bug)
        .reflect("details[Sprint].text")
        .getValue();
```

```java
String sprint = Reflecto.reflect(bug)
        .invoke("details[Sprint].text");
```

Set Value

```java
Reflecto.reflect(bug)
        .reflect("details[Sprint]")
        .setValue(new Details("SPR-002"));
```

```java
Reflecto.reflect(bug)
        .invoke("details[Sprint]=?", new Details("SPR-002"));
```

```java
Reflecto.reflect(bug)
        .invoke("details[?]=?", "Sprint", new Details("SPR-002"));
```

### Methods

```java
String username = Reflecto.reflect(bug)
        .reflect("getWatchers().get(?).getUsername()", 0)
        .getValue();
```

```java
String username = Reflecto.reflect(bug)
        .invoke("getWatchers().get(?).getUsername()", 0);
```

```java
Reflecto.reflect(bug)
        .invoke("getWatchers().get(?).setUsername(?)", 0, "new_name");
```
```java
Reflecto.reflect(bug)
        .invoke("getDetails().remove(?)", "Sprint");
```

```java
Reflecto.reflect(bug)
        .invoke("getDetails().put(?, ?)", "Sprint", new Details("SPR-002"));
```

### Mixed

```java
String username = Reflecto.reflect(bug)
        .reflect("watchers[0].getUsername()")
        .getValue();
```
```java
String username = Reflecto.reflect(bug)
        .reflect("getWatchers()[0].getUsername()")
        .getValue();
```
```java
Reflecto.reflect(bug)
        .invoke("getWatchers().get(?).username=?", 0, "new_name");
```
```java
Reflecto.reflect(bug)
        .invoke("details[Sprint].setText(?)", "SPR-002");
```
# License

**Reflecto** library is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). 
