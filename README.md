# message-service project

This project uses [Quarkus](https://quarkus.io/), the Supersonic Subatomic Java Framework.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Building and running docker image
```
./mvnw package
```
```
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/message-service-jvm .
```
```
docker run -i --rm -p 8080:8080 quarkus/message-service-jvm
```

## Calling the service

### Quarkus-generated Swagger-UI
After running the app, go to http://localhost:8080/q/swagger-ui/ to see controller, requests and responses specifications.

It's also possible to call API endpoints there.

For endpoints, that require authentication, use `bob`:`qwerty`.

### Getting all messages
Does not require any authentication
```
curl -i -X GET  'http://localhost:8080/message/all'
```

### Getting message by id
Does not require any authentication
```
ID=1
curl -i -X GET  "http://localhost:8080/message/$ID"
```

### Authenticating as existing user
Pre-created username is `bob` with password `qwerty`.

For simplicity and testability HTTP Basic Authentication is used.
```
AUTH=$(printf "%s" 'bob:qwerty' | base64)
```

### Creating new message
Requires authentication
```
curl -i -X POST --header "Authorization: Basic $AUTH" 'http://localhost:8080/message'  \
--header 'Content-Type: application/json' \
--data-raw '{
    "header": "message header",
    "body": "message body"
}'
```

### Editing an existing message
Requires authentication
```
ID=1
curl -i -X PUT --header "Authorization: Basic $AUTH" "http://localhost:8080/message/$ID"  \
--header 'Content-Type: application/json' \
--data-raw '{
    "header": "new header",
    "body": "new body"
}'
```

### Deleting a message
Requires authentication
```
ID=1
curl -i -X DELETE --header "Authorization: Basic $AUTH" "http://localhost:8080/message/$ID"
```
