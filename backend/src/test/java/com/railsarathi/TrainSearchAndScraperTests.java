package com.railsarathi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.railsarathi.seeder.DatabaseSeeder;

@SpringBootTest
public class TrainSearchAndScraperTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DatabaseSeeder databaseSeeder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        databaseSeeder.run();
    }

    @Test
    void shouldAutocompleteStations() throws Exception {
        mockMvc.perform(get("/api/v1/stations/search")
                        .param("query", "Howrah")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].stationCode").value("HWH"))
                .andExpect(jsonPath("$.data[0].city").value("Kolkata"));
    }

    @Test
    void shouldSearchDirectTrainsBetweenHowrahAndNJP() throws Exception {
        mockMvc.perform(get("/api/v1/trains/search")
                        .param("source", "HWH")
                        .param("destination", "NJP")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].trainNumber").value("22301"))
                .andExpect(jsonPath("$.data[0].trainName").value("Howrah - NJP Vande Bharat Express"))
                .andExpect(jsonPath("$.data[0].availableClasses").isNotEmpty());
    }

    @Test
    void shouldSearchIntermediateRouteBetweenKanpurAndNewDelhi() throws Exception {
        // Kanpur Central (CNB) to New Delhi (NDLS) should match 12301 Rajdhani Express
        mockMvc.perform(get("/api/v1/trains/search")
                        .param("source", "CNB")
                        .param("destination", "NDLS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void shouldGetTrainScheduleTimetable() throws Exception {
        mockMvc.perform(get("/api/v1/trains/22301/schedule")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].stationCode").value("HWH"));
    }

    @Test
    void shouldQueryLiveTrainStatus() throws Exception {
        mockMvc.perform(get("/api/v1/trains/22301/live-status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.trainNumber").value("22301"))
                .andExpect(jsonPath("$.data.statusMessage").isNotEmpty());
    }
}
