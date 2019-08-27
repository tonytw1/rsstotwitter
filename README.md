# RSS to Twitter

Pushes RSS feed items to linked Twitter accounts.

- Supports multiple input feeds and Twitter accounts.
- Supports rate limiting and duplicate post protection.

Uses MongoDB for storage.
Java / Spring Boot / Maven application intended to be run in a Docker container.

Example output: [@wellynews](https://twitter.com/wellynews)

## Build

Run Maven build then Docker build.
```
mvn clean test install
docker build -t rsstotwitter:latest .
```

