# Test Writing Standards

## Stack
- JUnit 5 + Mockito + AssertJ — only these, no alternatives
- Testcontainers for integration tests — H2 is not used in this project

## Annotations
- @WebMvcTest — controller tests only
- @DataJpaTest — repository tests (not @SpringBootTest)
- @SpringBootTest — only for full end-to-end integration tests
- @ExtendWith(MockitoExtension.class) — service unit tests

## Naming
- Pattern: `methodName_scenario_expectedResult`
- Example: `findById_whenUserNotFound_throwsNotFoundException`

## Rules
- Never mock entity classes — use builders or test fixtures instead
- Structure every test with AAA: // Arrange / Act / Assert comments