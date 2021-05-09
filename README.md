# message-service project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

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
Pre-created user name is `bob` with password `password`.

For simplicity and testability HTTP Basic Authentication is used.
```
AUTH=$(printf "%s" 'bob:password' | base64)
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
curl -i -X PUT --header "Authorization: Basic $AUTH" 'http://localhost:8080/message/1'  \
--header 'Content-Type: application/json' \
--data-raw '{
    "header": "new header",
    "body": "new body"
}'
```

### Deleting a message
Requires authentication
```
curl -i -X DELETE --header "Authorization: Basic $AUTH" 'http://localhost:8080/message/1'
```
