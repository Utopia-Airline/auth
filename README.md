# Authentication Microservice 

This Microservice is responsible for managing the users and authenticating consumers of in the Utopia Airline API endpoints.

## API EXAMPLE

`GET /api/users/?queryParams`

## AUTHENTICATION

Most of the end-points in this microservice requires users to be logged in. Authentication is implemented using JWT Token authentication. In order to login(Get a valid token) you need to consume the `auth` microservice first. 

push II
