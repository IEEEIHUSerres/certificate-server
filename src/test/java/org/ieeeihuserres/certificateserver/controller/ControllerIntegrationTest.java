package org.ieeeihuserres.certificateserver.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("testing")
class ControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void indexRedirectsToEventUrl() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://ieeeihuserres.org"));
    }

    @Test
    void checkReportsFoundForKnownParticipant() throws Exception {
        mockMvc.perform(get("/api/certificates/check/ikostelidis@i4h.org"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("found"));
    }

    @Test
    void checkReportsNotFoundForUnknownParticipant() throws Exception {
        mockMvc.perform(get("/api/certificates/check/nobody@i4h.org"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("not-found"));
    }

    @Test
    void generateReturnsPdfForKnownParticipant() throws Exception {
        final byte[] body = mockMvc.perform(get("/api/certificates/generate/ikostelidis@i4h.org"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        assertThat(new String(body, 0, 5)).isEqualTo("%PDF-");
    }

    @Test
    void generateReturnsNotFoundForUnknownParticipant() throws Exception {
        mockMvc.perform(get("/api/certificates/generate/nobody@i4h.org"))
                .andExpect(status().isNotFound());
    }
}
