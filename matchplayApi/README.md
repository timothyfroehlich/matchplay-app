# Matchplay API Client

A Kotlin client library for the [Matchplay Events API](https://matchplay.events/api).

## Setup

1. Copy `template.properties` to `local.properties`
2. Add your Matchplay API key to `local.properties`

## Testing

### Unit Tests

Run the unit tests with:
```bash
./gradlew :matchplayApi:test
```

### Integration Tests

Integration tests make real API calls and require a valid API key.

1. Ensure your API key is set in `local.properties`
2. Run integration tests with:
```bash
./gradlew :matchplayApi:test -PrunIntegration
```

### Security Notes

- Never commit `local.properties` to source control
- API keys are loaded at runtime from `local.properties`
- Integration tests are tagged and only run when explicitly configured
- Integration tests require a valid Matchplay Premium API key
