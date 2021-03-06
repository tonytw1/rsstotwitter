# RSS to Twitter

Pushes RSS feed items to linked Twitter accounts.

- Supports multiple input feeds and Twitter accounts.
- Supports rate limiting and duplicate post protection.

Uses MongoDB for storage.
Java / Spring Boot / Maven application intended to be run in a Docker container.

Example output: [@wellynews](https://twitter.com/wellynews)


### Adding a new feed
![Adding a new feed](newfeed.png)

### Listing feeds
![Listing feeds](newfeed.png)

### Feed details
![Feed detail](newfeed.png)


## Run locally

Use Docker to provide a local MongoDB instance:

```
docker-compose -f docker/docker-compose.yml up
```

Inspect the application.properties file.
Set the Mongo connection details and your Twitter client credentials.

In the Twitter developer tools, set http://localhost:8080/oauth/callback as an allowed oauth callback url.

Use Spring Boot to start the application locally:
```
mvn spring-boot:run
```

The sign in screen will be visible at http://localhost:8080


## Build

Run Maven build then Docker build.
```
mvn clean test install
docker build -t rsstotwitter:latest .
```

