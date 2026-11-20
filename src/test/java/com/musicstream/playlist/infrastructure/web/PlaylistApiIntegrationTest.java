package com.musicstream.playlist.infrastructure.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlaylistApiIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void creates_a_playlist_plays_it_exports_it_and_stops_the_player_on_delete() throws Exception {
        String response = mvc.perform(post("/api/playlists").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "test-playlist", "songIds": ["blues-001", "jazz-001", "pop-001"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.songCount").value(3))
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(response, "$.id");

        mvc.perform(post("/api/player/play").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"playlistId": "%s"}
                                """.formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLAYING"));

        mvc.perform(get("/api/player"))
                .andExpect(jsonPath("$.playlistId").value(id))
                .andExpect(jsonPath("$.queueSize").value(3));

        mvc.perform(get("/api/playlists/%s/export?format=m3u".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("test-playlist.m3u8")))
                .andExpect(content().string(startsWith("#EXTM3U")));

        mvc.perform(delete("/api/playlists/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/player")).andExpect(jsonPath("$.status").value("STOPPED"));
        mvc.perform(get("/api/playlists/" + id)).andExpect(status().isNotFound());
    }
}
