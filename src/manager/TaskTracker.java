package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskTracker {

    private HashMap<Long, Task> taskHashMap = new HashMap<>();
    private HashMap<Long, Epic> epicHashMap = new HashMap<>();
    private HashMap<Long, Subtask> subtaskHashMap = new HashMap<>();
    private long generationId = 0;

    private long generationId() {
        return generationId++;
    }

    public long addNewTask(Task task) {
        task.setId(generationId());
        taskHashMap.put(task.getId(), task);
        return task.getId();
    }
    public long addNewEpic(Epic epic) {
        epic.setId(generationId());
        epicHashMap.put(epic.getId(), epic);
        return epic.getId();
    }

    public Long addNewSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }

        subtask.setId(generationId());
        subtaskHashMap.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(subtask.getEpicId());

        return subtask.getId();
    }

    public HashMap<Long, Task> getTaskHashMap() {
        return taskHashMap;
    }

    public HashMap<Long, Epic> getEpicHashMap() {
        return epicHashMap;
    }

    public HashMap<Long, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    private void updateTask(Task task) {
        Task saveTask = taskHashMap.get(task.getId());
        if (saveTask == null) {
            return;
        }
        taskHashMap.put(task.getId(), task);
    }

    private void updateEpic(Epic epic) {
        Epic saveEpic = epicHashMap.get(epic.getId());
        if(saveEpic == null) {
            return;
        }
        epicHashMap.put(epic.getId(),epic);
    }

    private void updateSubtask(Subtask subtask) {
        Subtask saveSubtask = subtaskHashMap.get(subtask.getId());
        if (saveSubtask == null) {
            return;
        }
        subtaskHashMap.put(subtask.getId(),subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public void removeTaskUseId(long taskId) {
        Task removeTask = taskHashMap.get(taskId);
        taskHashMap.remove(removeTask.getId(),removeTask);
    }

    public void removeEpicUseId(long epicId) {
        Epic removeEpic = epicHashMap.get(epicId);
        ArrayList<Long> subtaskIds = removeEpic.getSubtaskIds();


        for (long subtaskId : subtaskIds) {
            Subtask subtask = subtaskHashMap.get(subtaskId);

            if (subtask.getEpicId() == epicId) {
                subtaskHashMap.remove(subtask.getId(),subtask);
            }
        }
        epicHashMap.remove(removeEpic.getId(),removeEpic);
    }

    public void removeSubtaskUseID(long subtaskId) {
        Subtask removeSubtask = subtaskHashMap.get(subtaskId);
        subtaskHashMap.remove(removeSubtask.getId(),removeSubtask);
    }

    private void updateEpicStatus(long epicId) {
        Epic epic = epicHashMap.get(epicId);
        ArrayList<Long> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }

        String status = null;
        for (long subtaskId : subtaskIds) {
            Subtask subtask = subtaskHashMap.get(subtaskId);

            if (status == null) {
                status = subtask.getStatus();
                continue;
            }

            if (status.equals(subtask.getStatus())
            && !status.equals("IN_PROGRESS")) {
                continue;
            }

            epic.setStatus("IN_PROGRESS");
            return;
        }

        epic.setStatus(status);
    }

    public void getSubtasksOfEpic(Epic epic) {
        if (epic != null) {
            ArrayList<Long> subtaskIDs = epic.getSubtaskIds();

            for (long subtaskId : subtaskIDs) {
                Subtask subtask = subtaskHashMap.get(subtaskId);
                System.out.println(subtask.toString());
            }
        }

    }

}
