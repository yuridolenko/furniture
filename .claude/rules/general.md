# General Java Rules

- Use Java 21 features: records, sealed classes, pattern matching where appropriate
- NEVER use raw types or unchecked casts
- Use `@Slf4j` (Lombok) for logging, NEVER `System.out.println`
- Prefer constructor injection over field injection (`@Autowired` on field forbidden)
- use var instead of explicit type