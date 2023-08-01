package tasks;

public class Subtask extends Task {
    private Long epicId;
    public Subtask(String name, String description, String status, Long epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
