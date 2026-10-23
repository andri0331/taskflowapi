package sg.edu.ntu.taskflowapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests the HTTP layer of the TaskFlow API end to end.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TaskApiTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a task and returns the id the API generated for it.
     */
    private long createTaskAndReturnId(String title) throws Exception {
        String json = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\",\"description\":\"a test task\",\"completed\":false}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode created = objectMapper.readTree(json);
        return created.get("id").asLong();
    }

    @Test
    void createTaskReturns201AndTheSavedTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Create endpoint test\",\"description\":\"testing\",\"completed\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Create endpoint test"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getAllTasksReturns200() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTaskByIdReturnsTheTask() throws Exception {
        long id = createTaskAndReturnId("Get by id test");

        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Get by id test"));
    }

    @Test
    void getTaskByIdReturns404WhenTheTaskIsMissing() throws Exception {
        mockMvc.perform(get("/api/tasks/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskChangesTheStoredTask() throws Exception {
        long id = createTaskAndReturnId("Update endpoint test");

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated title\",\"description\":\"updated\",\"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void markTaskAsCompleteSetsCompletedToTrue() throws Exception {
        long id = createTaskAndReturnId("Complete endpoint test");

        mockMvc.perform(put("/api/tasks/" + id + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Complete endpoint test"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTaskRemovesIt() throws Exception {
        long id = createTaskAndReturnId("Delete endpoint test");

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTaskWithoutCompletedFieldDefaultsToFalse() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"No completed field\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("No completed field"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void createTaskWithoutTitleReturns400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"no title\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title is mandatory."));
    }

    @Test
    void missingTaskErrorBodyHasMessageAndTimestamp() throws Exception {
        mockMvc.perform(get("/api/tasks/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Could not find task with id: 999999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void nonNumericIdReturns400() throws Exception {
        mockMvc.perform(get("/api/tasks/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value 'not-a-number' for parameter 'id'"));
    }

    @Test
    void unknownPathReturns404() throws Exception {
        mockMvc.perform(get("/api/there-is-no-such-endpoint"))
                .andExpect(status().isNotFound());
    }
}
