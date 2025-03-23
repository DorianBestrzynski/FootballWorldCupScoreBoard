# World Cup Score Board

## Overview
This project implements a **Live Football World Cup Scoreboard** as an in-memory Java library. It provides functionality to track football matches, update scores, and retrieve match summaries ordered by total score.

## Features
- Start a new match with an initial score of 0-0.
- Update match scores at any time.
- Finish an ongoing match, removing it from the scoreboard.
- Retrieve a **summary of ongoing matches** sorted by total score, with ties resolved by the most recently updated match.

## Simplifications
- The project does **not** use a database. Instead, it relies on an **in-memory list** (`InMemoryMatchRepository`) to store matches.
- **Not thread-safe** – concurrent access might lead to inconsistencies, but this can be solved with `ConcurrentHashMap` and `synchronized` blocks.

## Immutability Approach
- The core models (`Match`, `Team`) are **immutable** to prevent unintended modifications and improve thread safety.
- Any update operation creates a **new instance** rather than modifying existing objects.


## Tests
Project is fully tested, it contains:
- Unit tests with usage of mockito and assertJ.
- In memory storage tests
- Components tests checking whole flow.

## Running Tests
To execute unit and component tests:
```./gradlew test```

## Technologies Used
- **Java** - Core language
- **Gradle** - Build automation
- **JUnit with Mockito and AssertJ** - Unit testing framework
- **Collections Framework** - In-memory storage

## Notes
- Matches are sorted in **descending order by total score**.
- If two matches have the same score, the most **recently updated** match appears first.
- Once a match **finishes**, it is **removed** from the scoreboard.
- However finished matches remain in storage for statistics purposes.
