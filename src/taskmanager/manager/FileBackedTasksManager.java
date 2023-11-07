package taskmanager.manager;

import taskmanager.exceptions.ManagerException;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;
    final String HEADER = "id,name,status,description,epic";

    public FileBackedTasksManager(HistoryManager defaultHistory, String fileName) {
        super(historyManager);
        this.file = new File(fileName);
    }

    @Override
    public Task addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task;
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        if (task != null) {
            save();
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        if (epic != null) {
            save();
            return epic;
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        if (subtask != null) {
            save();
            return subtask;
        } else {
            return null;
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        save();
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file.getPath());
        if (Files.exists(Path.of(file.getPath()))) {
            try {
                String[] stringList = Files.readString(Path.of(file.getPath())).split("\n");
                for (int i = 1; i < stringList.length - 1; i++) {
                    if (!stringList[i].isBlank()) {
                        Task task = CSVFormatter.fromString(stringList[i]);
                        if (task.getClass() == Task.class) {
                            fileBackedTasksManager.addNewTask(task);
                        } else if (task.getClass() == Epic.class) {
                            fileBackedTasksManager.addNewEpic((Epic) task);
                        } else if (task.getClass() == Subtask.class) {
                            fileBackedTasksManager.addNewSubtask((Subtask) task);
                        }
                    } else {

                        for (int id : CSVFormatter.historyFromString(stringList[i + 1])) {
                            if (taskHashMap.containsKey(id)) {
                                historyManager.add(taskHashMap.get(id));
                            } else if (subtaskHashMap.containsKey(id)) {
                                historyManager.add(subtaskHashMap.get(id));
                            } else {
                                historyManager.add(epicHashMap.get(id));
                            }
                        }
                    }
                }

            } catch (IOException e) {
                throw new ManagerException("Ошибка загрузки данных!");
            }
        } else {
            return FileBackedTasksManager.loadFromFile(file);
        }

        return fileBackedTasksManager;

    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            HashMap<Integer, String> allTasks = new HashMap<>();

            HashMap<Integer, Task> tasks = super.getTaskList();
            for (Integer id : tasks.keySet()) {
                allTasks.put(id, CSVFormatter.toString(tasks.get(id)));
            }

            HashMap<Integer, Epic> epics = super.getEpicList();
            for (Integer id : epics.keySet()) {
                allTasks.put(id, CSVFormatter.toString(epics.get(id)));
            }

            HashMap<Integer, Subtask> subtasks = super.getSubtaskList();
            for (Integer id : subtasks.keySet()) {
                allTasks.put(id, CSVFormatter.toString(subtasks.get(id)));
            }

            fileWriter.write(HEADER + "\n");
            for (String value : allTasks.values()) {
                fileWriter.write(value);
            }
            fileWriter.write("\n" + CSVFormatter.historyToString(historyManager));


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Все задачи удалены!");
        }
    }
}





