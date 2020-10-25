# Music Master
An application to manage playlists between streaming services (tidal/spotify currently).

## setup
- import & build project with maven
- requires setting up a developer account with [spotify](https://developer.spotify.com).
- After creating an account, create a new application and configure a callback URI (localhost). You will receive a `client id` and `client secret`.
- In the `application-example.yml` file, update the following values `spotify.client.id` and `spotify.client.secret`, also update the `spotify.redirect.uri` to match the one you created
- retrieve a tidal token (can be sniffed from the tidal traffic....or google)
- update the `tidal.token` value in the `application-example.yml`.
- rename `application-example.yml` to `application.yml`
- run project

## usage
- authenticate spotify by navigating to http://localhost:8080/auth/login/spotify . This will redirect you to spotify for login.

#### copy playlist from tidal to spotify
- make a GET request to localhost:8080/playlists/tidalToSpotify
- attach the following query params: 
    - `playlistId` : the ID of the tidal playlist
    - `playlistName` : the name of the new playlist being created in spotify