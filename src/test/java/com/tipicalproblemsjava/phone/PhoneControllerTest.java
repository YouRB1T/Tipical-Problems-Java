package com.tipicalproblemsjava.phone;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
class PhoneControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createsReadsUpdatesAndDeletesPhone() throws Exception {
        String createBody = """
                {
                  "brand": "Apple",
                  "model": "iPhone 15",
                  "storageGb": 128,
                  "price": 79900,
                  "inStock": true
                }
                """;

        String location = mockMvc.perform(post("/api/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/phones/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("iPhone 15"));

        String updateBody = """
                {
                  "brand": "Apple",
                  "model": "iPhone 15 Pro",
                  "storageGb": 256,
                  "price": 99900,
                  "inStock": false
                }
                """;

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.inStock").value(false));

        mockMvc.perform(get("/api/phones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound());
    }

    @Test
    void validatesCreateRequest() throws Exception {
        String invalidBody = """
                {
                  "brand": "",
                  "model": "",
                  "storageGb": 0,
                  "price": 0,
                  "inStock": null
                }
                """;

        mockMvc.perform(post("/api/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void validatesLoadRequestAndRunsSmallLoad() throws Exception {
        mockMvc.perform(post("/api/phones/load?iterations=1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iterations").value(1000));

        mockMvc.perform(post("/api/phones/load?iterations=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }
}
