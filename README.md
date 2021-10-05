# Reflecto

Java Deep Reflection Library

## Example

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

## Fields Reflection

### Simple

#### Get Value

```java
String username = reflect(bug)
                    .get("reporter.username")
                    .getValue();
```

```java
String username = reflect(bug)
                    .invoke("reporter.username");
```

#### Set Value

```java
reflect(bug)
    .get("reporter.username")
    .setValue("new_name");
```

```java
reflect(bug)
    .invoke("reporter.username=?","new_name");
```

### List / Array

#### Get Value

```java
String username = reflect(bug)
                    .get("watchers[0].username")
                    .getValue();
```

```java
String username = reflect(bug)
                    .invoke("watchers[0].username");
```

#### Set Value

```java
reflect(bug)
    .get("watchers[0]")
    .setValue(new User("user4"));
```

```java
reflect(bug)
    .invoke("watchers[0]=?", new User("user4"));
```

```java
reflect(bug)
    .invoke("watchers[?]=?", 0, new User("user4"));
```

### Map

#### Get Value

```java
String sprint = reflect(bug)
                    .get("details[Sprint].text")
                    .getValue();
```

```java
String sprint = reflect(bug)
                    .invoke("details[Sprint].text");
```

#### Set Value

```java
reflect(bug)
    .get("details[Sprint]")
    .setValue(new Details("SPR-002"));
```

```java
reflect(bug)
    .invoke("details[Sprint]=?", new Details("SPR-002"));
```

```java
reflect(bug)
    .invoke("details[?]=?", "Sprint", new Details("SPR-002"));
```

## Methods Reflection

```java
String username = reflect(bug)
                    .get("getWatchers().get(?).getUsername()", 0)
                    .getValue();
```

```java
String username = reflect(bug)
                    .invoke("getWatchers().get(?).getUsername()", 0);
```

```java
reflect(bug)
    .invoke("getWatchers().get(?).setUsername(?)", 0, "new_name");
```

```java
reflect(bug)
    .invoke("getDetails().put(?, ?)", "Sprint", new Details("SPR-002"));
```

## Mixed Reflection

```java
String username = reflect(bug)
                    .get("watchers[0].getUsername()")
                    .getValue();
```
```java
String username = reflect(bug)
                    .get("getWatchers()[0].getUsername()")
                    .getValue();
```
```java
reflect(bug)
    .invoke("getWatchers().get(?).username=?", 0, "new_name");
```
```java
reflect(bug)
    .invoke("details[Sprint].setText(?)", "SPR-002");
```
