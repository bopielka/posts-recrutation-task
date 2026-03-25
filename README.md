# JSONPlaceholder Post Exporter

Spring Boot CLI application that fetches all posts from the [JSONPlaceholder](https://jsonplaceholder.typicode.com/) API
and saves each post as a separate JSON file.

## Requirements

- Java 21
- Maven (or use the included `./mvnw` wrapper)

## Running

```bash
./mvnw spring-boot:run
```

After startup the application fetches all posts, writes them to a timestamped subdirectory inside the configured output
directory, and exits. Output files are named after the post ID: `1.json`, `2.json`, …, `100.json`.

Example output directory after two runs:

```
posts/
├── 2026-03-25_10-00-00-000-posts/
│   ├── 1.json
│   └── 2.json
└── 2026-03-25_10-05-00-000-posts/
    ├── 1.json
    └── 2.json
```

## Configuration

All settings are in `src/main/resources/application.properties`:

| Property                | Default                                | Description                                           |
|-------------------------|----------------------------------------|-------------------------------------------------------|
| `post.api.base-url`     | `https://jsonplaceholder.typicode.com` | Base URL of the JSONPlaceholder API                   |
| `post.export.directory` | `posts`                                | Base directory; each run creates a timestamped subfolder inside |

You can override any property at runtime without recompiling:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--post.export.directory=/tmp/posts"
```

## Project structure

```
src/main/java/com/bopielka/recrutationtask/
├── RecrutationtaskApplication.java       entry point (CommandLineRunner)
├── client/post/
│   ├── PostClient.java                   interface
│   └── JsonPostClient.java               HTTP implementation (RestClient → /posts)
├── config/
│   ├── AppConfig.java                    RestClient and Clock beans
│   └── PostProperties.java               typed configuration (@ConfigurationProperties)
├── exception/post/
│   └── PostExportException.java          domain exception
├── model/post/
│   └── Post.java                         record (id, userId, title, body)
└── service/post/
    ├── api/PostExportService.java         interface
    └── impl/
        └── JsonPostExportService.java     orchestrates fetch → create dir → save
```

## Running tests

```bash
./mvnw test
```
