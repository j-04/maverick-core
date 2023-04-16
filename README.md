# Self implementation of IoC and DI concepts  

## Author
* Dragosh Sergey - [j-04](https://github.com/j-04)
dragoshs.j@yahoo.com

---
## Additional modules
#### [Maverick core API module](https://github.com/j-04/maverick-core-api)

---
## Built with
* Java 14
* Maven 3.6.1
* org.reflections 0.9.4
* cglib 3.3.0
* Lombok 1.18.12
* Apache Commons Lang 3
* JSR 250 Annotations

---
## Provided features
* ***Entity injection***: 
Injection of an entity by the specified type or automatic injection by the field type
* ***Property injection***: 
Injection of a property specified in application.properties file
* ***Singleton objects***:
Singleton objects without any implementation of the singleton pattern
* ***Eager and lazy initialization of singleton objects***
* ***Proxy pattern***

---
## How to install
**Terminal:**
```
$ git clone https://github.com/j-04/maverick-core.git
$ cd maverick-core
$ mvn clean install
```

**Maven dependency:**
```
<dependency>
    <groupId>com.maverick.core</groupId>
    <artifactId>maverick-core</artifactId>
    <version>1.0</version>
</dependency>
```
