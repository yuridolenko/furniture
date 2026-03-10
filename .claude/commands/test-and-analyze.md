---
description: Run Java unit tests and analyze failures as a senior engineer
allowed-tools: Bash
---

Run Java unit tests and analyze any failures as a senior Java engineer.

## Arguments
$ARGUMENTS

## Instructions

1. **Determine what to run:**
    - If `$ARGUMENTS` is `*` or empty — run ALL tests
    - Otherwise — treat `$ARGUMENTS` as test name(s), e.g. `UserServiceTest` or `UserServiceTest PaymentServiceTest`

2. **Run the tests using the appropriate build tool:**
    - If `pom.xml` exists → use Maven:
        - All tests: `mvn test -q 2>&1`
        - Specific test(s): `mvn test -Dtest=$ARGUMENTS -q 2>&1`
    - If `build.gradle` or `build.gradle.kts` exists → use Gradle:
        - All tests: `./gradlew test 2>&1`
        - Specific test(s): `./gradlew test --tests "$ARGUMENTS" 2>&1`

3. **Parse the output:**
    - If ALL tests pass → print a brief success summary only (e.g. "✅ All 42 tests passed in 3.2s") and STOP. No further analysis needed.
    - If there are failures → proceed to analysis.

4. **For each failed test, analyze as a senior Java engineer:**
    - State the test name and the exact assertion or exception that failed
    - Read the full stack trace and identify the ROOT CAUSE (not just the symptom)
    - Look at the relevant source files to understand the context
    - Explain WHY it failed in plain language
    - Suggest a concrete fix with a code snippet if applicable
    - Categorize the failure type: `NullPointerException`, `AssertionError`, `timeout`, `wrong mock setup`, `data mismatch`, etc.

5. **End with a summary table:**
   | Test | Failure Type | Root Cause (short) | Fix Complexity |
   |------|-------------|-------------------|----------------|
   | ... | ... | ... | Low/Medium/High |
```

---

### Шаг 3 — Используй команду
```
/test-analyze *
```
```
/test-analyze UserServiceTest
```
```
/test-analyze UserServiceTest PaymentServiceTest