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
    
## todo
- tidal auth 
- add merge playlist functionality. Given a tidal playlist ID, update an existing spotify playlist with those songs, avoiding duplicates.
- track playlists that get converted, build a local library.
- handle spotify API limits
  - setup task queue or other method to batch adding items to playlist. Spotify limits the number of songs it can add to a playlist per request (100 songs)
  - simple approach is just iteratively make n/100 requests where n = number of songs. Temporarily using this approach
- define interfaces for playlist classes instead of hard implementations