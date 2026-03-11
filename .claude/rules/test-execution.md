---
paths:
  - "src/test/**/*Test.java"
---

# Test Execution

- After ANY change to a test file — immediately run that test
- Command: `mvn test -Dtest=<ClassName>`
- Do NOT mark task complete until the test run finishes
- Always report result explicitly: PASSED / FAILED + summary of failures if any