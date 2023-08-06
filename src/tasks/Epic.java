package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Long> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, "NEW");
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Long> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(long subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void setSubtaskIds(ArrayList<Long> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
