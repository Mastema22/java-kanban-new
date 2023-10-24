package taskmanager.manager;

import taskmanager.exceptions.ManagerException;
import taskmanager.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path pathFileName;
    final String HEADER = "id,name,status,description,epic";

    public FileBackedTasksManager(HistoryManager defaultHistory, String fileName) {
        this.pathFileName = Paths.get(fileName);
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

    private void save() {
        try (FileWriter fileWriter = new FileWriter(pathFileName.toFile())) {
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

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), "manager.csv");

        Task task1 = new Task("Задача1", "Описание", Status.NEW);
        Task task2 = new Task("Задача2", "Описание1", Status.IN_PROGRESS);
        Task task3 = new Task("Задача3", "Описание2", Status.IN_PROGRESS);

        fileBackedTasksManager.addNewTask(task1);
        fileBackedTasksManager.addNewTask(task2);
        fileBackedTasksManager.addNewTask(task3);

        Epic epic1 = new Epic("Эпик1", "Описание");
        Epic epic2 = new Epic("Эпик2", "Описание");
        Epic epic3 = new Epic("Эпик3", "Описание");

        fileBackedTasksManager.addNewEpic(epic1);
        fileBackedTasksManager.addNewEpic(epic2);
        fileBackedTasksManager.addNewEpic(epic3);

        Subtask subtask1 = new Subtask("subTaskName1", "subTaskDescription1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("subTaskName2", "subTaskDescription1", Status.IN_PROGRESS, epic2.getId());
        Subtask subtask3 = new Subtask("subTaskName3", "subTaskDescription1", Status.NEW, epic3.getId());

        fileBackedTasksManager.addNewSubtask(subtask1);
        fileBackedTasksManager.addNewSubtask(subtask2);
        fileBackedTasksManager.addNewSubtask(subtask3);

        System.out.println("Task list:");
        fileBackedTasksManager.getTaskList().forEach((key, value) -> System.out.println(key + " " + value));
        fileBackedTasksManager.getEpicList().forEach((key, value) -> System.out.println(key + " " + value));
        fileBackedTasksManager.getSubtaskList().forEach((key, value) -> System.out.println(key + " " + value));
        System.out.println();

        System.out.println("History:");
        fileBackedTasksManager.getHistory().forEach(System.out::println);
        System.out.println();

        //fileBackedTasksManager.removeTaskById(task1.getId());
        //fileBackedTasksManager.removeEpicById(epic2.getId());
        //fileBackedTasksManager.removeAllTask();
        //fileBackedTasksManager.removeAllEpics();
        fileBackedTasksManager.removeAllSubtasks();
        //fileBackedTasksManager.removeAll();


        System.out.println("Task list:");
        fileBackedTasksManager.getTaskList().forEach((key, value) -> System.out.println(key + " " + value));
        fileBackedTasksManager.getEpicList().forEach((key, value) -> System.out.println(key + " " + value));
        fileBackedTasksManager.getSubtaskList().forEach((key, value) -> System.out.println(key + " " + value));
        System.out.println();

        System.out.println("History:");
        fileBackedTasksManager.getHistory().forEach(System.out::println);
        System.out.println();

        System.out.println("Restore file:");
        File file = new File("manager.csv");
        FileBackedTasksManager restoreBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        restoreBackedTasksManager.getTaskList().forEach((key, value) -> System.out.println(key + " " + value));
        restoreBackedTasksManager.getEpicList().forEach((key, value) -> System.out.println(key + " " + value));
        restoreBackedTasksManager.getSubtaskList().forEach((key, value) -> System.out.println(key + " " + value));
        System.out.println();

        System.out.println(compareTo(fileBackedTasksManager, restoreBackedTasksManager));

    }

    private static String compareTo(FileBackedTasksManager fileBackedTasksManager, FileBackedTasksManager restoreBackedTasksManager) {
        boolean compareTask = false;
        boolean compareEpic = false;
        boolean compareSubtask = false;
        boolean compareHistory = false;

        HashMap<Integer, Task> taskList1 = fileBackedTasksManager.getTaskList();
        HashMap<Integer, Task> taskList2 = restoreBackedTasksManager.getTaskList();

        HashMap<Integer, Epic> epicList1 = fileBackedTasksManager.getEpicList();
        HashMap<Integer, Epic> epicList2 = restoreBackedTasksManager.getEpicList();

        HashMap<Integer, Subtask> subtaskList1 = fileBackedTasksManager.getSubtaskList();
        HashMap<Integer, Subtask> subtaskList2 = restoreBackedTasksManager.getSubtaskList();

        if (!taskList1.isEmpty() && !taskList2.isEmpty()) {
            for (Task task : taskList1.values()) {
                compareTask = taskList2.containsValue(task);
            }
        } else compareTask = true;

        if (!epicList1.isEmpty() && !epicList2.isEmpty()) {
            for (Epic epic : epicList1.values()) {
                compareEpic = epicList2.containsValue(epic);
            }
        } else compareEpic = true;

        if (!subtaskList1.isEmpty() && !subtaskList2.isEmpty()) {
            for (Subtask subtask : subtaskList1.values()) {
                compareSubtask = subtaskList2.containsValue(subtask);
            }
        } else compareSubtask = true;

        if (!fileBackedTasksManager.getHistory().isEmpty() && !restoreBackedTasksManager.getHistory().isEmpty()) {
            for (Task task : fileBackedTasksManager.getHistory()) {
                compareHistory = restoreBackedTasksManager.getHistory().contains(task);
            }
        } else compareHistory = true;

        if (compareTask && compareEpic && compareSubtask && compareHistory) {
            return "Восстановление прошло успешно!";
        } else {
            return "Ошибка в восстановлении файла!";
        }
    }

}



