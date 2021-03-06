# Colisweb Test

## Basic architecture

![Diagram](diagram.png)

Services communicate through HTTP APIs. A gateway pattern is used to expose every endpoint on a common host. This also
enables centralized error formatting regarding badly formatted requests.

## Rationale

- Each service gets an _API_ and an _Implementation_ project. Each service should be able to call other services through
a unified interface
- This project aims at a clear distinction between the models and interfaces that form the _domain_ and the technical
implementations that form the _infrastructure_. Any consumer of any service should be completely agnostic of the implementation
of its dependencies.
- I've used the Typelevel stack (cats, cats Effect, http4s and doobie) because I wanted a fully typesafe, purely
functional application. Side effects are restricted to the main method of each service.

## Running the services

### Start a postgres database using docker

```
docker run --name colisweb-postgres -p 5436:5432 postgres:alpine
```

Then apply `schema.sql` to the newly created database.

### Start both services

```
sbt transportersImpl/run
```

Transporters service should start on port 8092. Port is customizable through the `TRANSPORTER_SERVICE_PORT` env. variable.

```
sbt carriersImpl/run
```

Carriers service should start on port 8091. Port is customizable through the `CARRIERS_SERVICE_PORT` env. variable.

### Start the gateway

```
sbt gateway/run
```

Gateway should start on port 8090. Port is customizable through the `GATEWAY_PORT` env. variable

## Deploying

// TODO

## Running the tests

```
sbt test
```

## Possible improvements

- Logging : there is currently no log whatsoever
- Gzip Compression
- Unified configuration : replace `sys.env.getOrElse` calls by a true configuration system that should be :
  - type safe
  - shared accross the stack
- Test coverage
- Code duplication : there is a bit of code duplication, especially in the Filters part. There is room for improvement regarding
the segregation of responsibilities and the code duplication. Genrally speaking, the Filter system could be improved.
- Service discovery :
  - Right now services need to be started on a known port at all time. This is not very scalable nor fault-resilient. A
  service discovery system should allow multiple instances of the same service to run on the same machine without explcicitly
  assigning ports beforehand.
- Carriers creation retry :
  - Right now you can create transporters even when carriers creation fails. The carriers creation will be retried in the background
  four times, with an increasing delay between each attempt. You can test this behaviour by trying to insert a transporter while then
  carriers service is down, then start the carriers service and wait for a 40 seconds or so.
  - This retry mechanism could be improved in the following ways :
    - There is no job persistence, meaning carriers creation will be retried as long as the transporters service is up, but there
    is no way to recover from a JVM crash and have the application retry the creation on startup
    - There is no way to be informed of the error when the 4 retries have failed
    
    
