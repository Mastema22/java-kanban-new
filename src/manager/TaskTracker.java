package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskTracker {

    private HashMap<Long, Task> taskHashMap = new HashMap<>();
    private HashMap<Long, Epic> epicHashMap = new HashMap<>();
    private HashMap<Long, Subtask> subtaskHashMap = new HashMap<>();
    private long generateId = 0;

    public long addNewTask(Task task) {
        task.setId(generateId());
        taskHashMap.put(task.getId(), task);
        return task.getId();
    }

    public long addNewEpic(Epic epic) {
        epic.setId(generateId());
        epicHashMap.put(epic.getId(), epic);
        return epic.getId();
    }

    public Long addNewSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }

        subtask.setId(generateId());
        subtaskHashMap.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(subtask.getEpicId());

        return subtask.getId();
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList(taskHashMap.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList(epicHashMap.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList(subtaskHashMap.values());
    }

    public void removeTaskById(long taskId) {
        Task removeTask = taskHashMap.get(taskId);
        taskHashMap.remove(removeTask.getId(),removeTask);
    }

    public void removeEpicById(long epicId) {
        Epic removeEpic = epicHashMap.get(epicId);
        ArrayList<Long> subtaskIds = removeEpic.getSubtaskIds();

        for (long subtaskId : subtaskIds) {
            Subtask subtask = subtaskHashMap.get(subtaskId);
            subtaskHashMap.remove(subtask.getId(),subtask);
        }
        epicHashMap.remove(removeEpic.getId(),removeEpic);
    }

    public void removeSubtaskById(long subtaskId) {
        Subtask removeSubtask = subtaskHashMap.get(subtaskId);
        Epic epicOfRemoveSubtask = epicHashMap.get(removeSubtask.getEpicId());

        ArrayList<Long> subtaskIds = epicOfRemoveSubtask.getSubtaskIds();
        subtaskIds.remove(removeSubtask.getId());

        epicOfRemoveSubtask.setSubtaskIds(subtaskIds);
        epicHashMap.put(epicOfRemoveSubtask.getId(),epicOfRemoveSubtask);

        subtaskHashMap.remove(removeSubtask.getId(),removeSubtask);
        updateEpicStatus(epicOfRemoveSubtask.getId());
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

    public Task getTaskById(long taskId) {
        Task task = taskHashMap.get(taskId);
        return task;
    }

    public Epic getEpicById(long epicId) {
        Epic epic = epicHashMap.get(epicId);
        return epic;
    }

    public Subtask getSubtaskById(long subtaskId) {
            return subtaskHashMap.get(subtaskId);
    }

    public void updateTask(Task task) {
        Task saveTask = taskHashMap.get(task.getId());
        if (saveTask == null) {
            return;
        }
        taskHashMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic saveEpic = epicHashMap.get(epic.getId());
        if(saveEpic == null) {
            return;
        }
        epicHashMap.put(epic.getId(),epic);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask saveSubtask = subtaskHashMap.get(subtask.getId());
        if (saveSubtask == null) {
            return;
        }
        subtaskHashMap.put(subtask.getId(),subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    private long generateId() {
        return generateId++;
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
}
