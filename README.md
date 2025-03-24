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
- In a real-world scenario, all changes are typically made in separate branches and merged into the `master` branch following a thorough review process. However, since I was the sole contributor to this repository, I opted to push changes directly to master for simplicity
- Additionally, I introduced validation to ensure that a team cannot participate in multiple ongoing matches simultaneously. To optimize this check, rather than iterating over the entire collection of matches, an index should be implemented to enhance query efficiency.


## Mutability Approach
- The core model (`Match`) is **mutable** due to nature of library. Since match updates - such as score changes or status updates - occur frequently, an immutable approach would require creating a new object for every change. This would introduce unnecessary overhead in terms of memory allocation and garbage collection, leading to performance inefficiencies.
- To maintain data integrity and prevent unintended modifications, access to mutable fields is controlled through encapsulation and validation logic within setter methods. This approach balances performance efficiency with data consistency while keeping the implementation straightforward.


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
- If two matches have the same score, the most **recently started** match appears first.
- Once a match **finishes**, it is **removed** from the scoreboard.
- Finished matches remain in storage for statistics purposes.
