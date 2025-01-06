# JavaConcurrencyTutorial

This is a set of small tutorials I'm writing for my girlfriend that teach various
concurrency primitives, with Java as the language of choice.

# Requirements

To follow along, you should install the latest [Maven](https://maven.apache.org/) and
[JDK](https://www.oracle.com/java/technologies/downloads/). As of the writing of this README, I'm
using Maven v3.9.9 and OpenJDK 23.

# Building and Running the Lessons

Each lesson is contained within a Maven module (one of the `lesson*` directories) and can be built
and run with the following commands, where `LESSON` should be replaced with the name of the module
(e.g. `lesson1`).

```
$ mvn package -pl LESSON
$ mvn exec:java -pl LESSON
```

To build every lesson, run:

```
$ mvn package
```

# Curriculum

- [Lesson 1: Threads](./lesson1/README.md)
