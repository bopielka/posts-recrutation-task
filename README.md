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

After startup the application fetches all posts, writes them to the configured output directory and exits.
Output files are named after the post ID: `1.json`, `2.json`, …, `100.json`.

## Configuration

All settings are in `src/main/resources/application.properties`:

| Property                    | Default                                  | Description                                              |
|-----------------------------|------------------------------------------|----------------------------------------------------------|
| `post.api.base-url`         | `https://jsonplaceholder.typicode.com`   | Base URL of the JSONPlaceholder API                      |
| `post.export.directory`     | `posts`                                  | Directory where JSON files are written                   |
| `post.export.max-copies`    | `5`                                      | Max number of suffixed copies allowed per file           |

You can override any property at runtime without recompiling:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--post.export.directory=/tmp/posts"
```

## File conflict handling

If a file with a given name already exists, the application logs a warning and creates a suffixed copy:

```
1.json        ← already exists
1_1.json      ← already exists
1_2.json      ← written here
```

If the number of existing copies reaches `post.export.max-copies`, a `PostExportException` is thrown and the export stops.

## Project structure

```
src/main/java/com/bopielka/recrutationtask/
├── RecrutationtaskApplication.java       entry point (CommandLineRunner)
├── client/post/
│   ├── PostClient.java                   interface
│   └── JsonPostClient.java               HTTP implementation (RestClient → /posts)
├── config/
│   ├── AppConfig.java                    RestClient bean
│   └── PostProperties.java               typed configuration (@ConfigurationProperties)
├── exception/post/
│   └── PostExportException.java          domain exception
├── model/post/
│   └── Post.java                         record (id, userId, title, body)
└── service/post/
    ├── api/PostExportService.java         interface
    └── impl/
        ├── JsonPostExportService.java     orchestrates fetch → create dir → save
        └── OutputFileResolver.java        resolves output filename, handles duplicates
```

## Running tests

```bash
./mvnw test
```
