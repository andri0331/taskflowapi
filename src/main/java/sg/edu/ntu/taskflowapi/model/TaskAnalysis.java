package sg.edu.ntu.taskflowapi.model;

public record TaskAnalysis(
        String summary,
        String completed,
        String pending) {
}