package com.musicmaster.main.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/playlists")
public class PlaylistController {

    @GetMapping(path = "", produces = APPLICATION_JSON_VALUE)
    public List<Object> getPlaylists() {
        return Arrays.asList(1,23,54);
    }
}
