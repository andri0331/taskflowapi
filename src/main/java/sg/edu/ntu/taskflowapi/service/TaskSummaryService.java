package sg.edu.ntu.taskflowapi.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import sg.edu.ntu.taskflowapi.exceptions.TaskSummaryException;
import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.model.TaskAnalysis;

/**
 * Uses Spring AI to turn the task list into a short plain-English summary.
 *
 * The {@link ChatClient.Builder} is injected by Spring, and the actual API key
 * is read from the OPENAI_API_KEY environment variable - it is never written
 * into the source code.
 */
@Service
public class TaskSummaryService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public TaskSummaryService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Asks the model to summarise what is pending and what is done.
     *
     * @param tasks the tasks to summarise
     * @return a short plain-English summary
     */
    public String summarise(List<Task> tasks) {
        String taskData;
        try {
            taskData = objectMapper.writeValueAsString(tasks);
        } catch (JsonProcessingException ex) {
            throw new TaskSummaryException("Could not prepare task data for the AI summary.", ex);
        }

        TaskAnalysis analysis = chatClient.prompt()
                .system("""
                        You are a task assistant for the TaskFlow API. Return a TaskAnalysis with all
                        three fields filled in as polished, user-facing text:

                        - summary: a short overall status, including the total task count.
                        - completed: a heading with the exact completed count, followed by completed
                          task titles as a Markdown bullet list. If there are none, say "No tasks
                          completed yet."
                        - pending: a heading with the exact pending count, followed by pending task
                          titles as a Markdown bullet list. If there are none, say "No pending tasks.
                          All tasks are completed." If there are no tasks at all, say "No tasks have
                          been added yet."

                        Use only the supplied JSON task data and its exact task states. Do not invent
                        or omit tasks. Derive the counts from the list.
                        """)
                .user("Summarize these tasks:\n" + taskData)
                .call()
                .entity(TaskAnalysis.class, spec -> spec.useProviderStructuredOutput());

        if (analysis == null
                || analysis.summary() == null || analysis.summary().isBlank()
                || analysis.completed() == null || analysis.completed().isBlank()
                || analysis.pending() == null || analysis.pending().isBlank()) {
            throw new TaskSummaryException(
                    "The AI returned an incomplete task summary.",
                    new IllegalStateException("TaskAnalysis fields must all contain formatted text."));
        }

        return """
                ## Task Summary

                %s

                ## Completed

                %s

                ## Pending

                %s
                """.formatted(analysis.summary(), analysis.completed(), analysis.pending()).strip();
    }
}
