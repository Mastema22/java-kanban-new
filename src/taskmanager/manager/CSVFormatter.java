package taskmanager.manager;

import taskmanager.exceptions.ManagerException;
import taskmanager.tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVFormatter {


    public static String toString(Task task) {
        if (task.getClass() == Task.class) {
            return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + "," +
                    task.getDescription() + "," + task.getDuration() + "," + task.getStartTime() + "\n";
        } else if (task.getClass() == Epic.class) {
            return task.getId() + "," + TaskType.EPIC + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getDuration() + "," + task.getStartTime() + "\n";
        } else {
            Subtask subtask = (Subtask) task;
            return subtask.getId() + "," + TaskType.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus() + "," +
                    task.getDescription() + "," + task.getDuration() + "," + task.getStartTime() + "," + subtask.getEpicId() + "\n";
        }
    }

    public static Task fromString(String value) {
        String[] data = value.split(",");
        int id = Integer.parseInt(data[0]);
        TaskType type = TaskType.valueOf(data[1]);
        String name = data[2];
        Status status = Status.valueOf(data[3]);
        String description = data[4];
        long duration = Long.parseLong(data[5]);
        LocalDateTime startTime = LocalDateTime.parse(data[6]);
        int epicId = 0;
        if (data.length == 8) {
            epicId = Integer.parseInt(data[7]);
        }
        switch (type) {
            case TASK:
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, duration, startTime);
                epic.setId(id);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(name, description, status, duration, startTime, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new ManagerException("Произошла ошибка в записи файла");
        }
    }

    public static String historyToString(HistoryManager manager) throws StringIndexOutOfBoundsException {
        StringBuilder s = new StringBuilder();
        for (Task task : manager.getHistory()) {
            s.append(task.getId()).append(",");
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> resultList = new ArrayList<>();
        if (value != null) {
            String[] values = value.split(",");
            for (String idString : values) {
                resultList.add(Integer.parseInt(idString));
            }
        }
        return resultList;
    }
}

