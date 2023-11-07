package taskmanager.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.FileBackedTasksManager;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final Path path = Path.of("manager.cvs");
    private final File file = new File(String.valueOf(path));

    private FileBackedTasksManager restoreBackedTasksManager;

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
            fileBackTaskManager.getTaskList().clear();
            fileBackTaskManager.getEpicList().clear();
            fileBackTaskManager.getSubtaskList().clear();
            fileBackTaskManager.getPrioritizedTasks().clear();
            fileBackTaskManager.getHistory().clear();
        } catch (IOException ex) {
            ex.getStackTrace();
        }
    }

    /*Пустой эпик без подзадач*/
    @Test
    public void save_loadFile_shouldSaveAndLoad() {
        fileBackTaskManager.addNewTask(task);
        fileBackTaskManager.addNewEpic(epic);
        restoreBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        HashMap<Integer, Task> taskHashMap = fileBackTaskManager.getTaskList();
        List<Task> taskList = new ArrayList<>(taskHashMap.values());
        HashMap<Integer, Task> taskHashMap1 = restoreBackedTasksManager.getTaskList();
        List<Task> taskList1 = new ArrayList<>(taskHashMap1.values());
        HashMap<Integer, Epic> epicHashMap = fileBackTaskManager.getEpicList();
        List<Epic> epicList = new ArrayList<>(epicHashMap.values());
        HashMap<Integer, Epic> epicHashMap1 = restoreBackedTasksManager.getEpicList();
        List<Epic> epicList1 = new ArrayList<>(epicHashMap1.values());

        assertEquals(taskList, taskList1);
        assertEquals(epicList, epicList1);
    }

    /*Пустой список задач*/
    @Test
    public void save_loadFile_shouldSaveAndLoadEmptyFile() {
        fileBackTaskManager.addNewTask(null);
        fileBackTaskManager.addNewEpic(null);
        fileBackTaskManager.addNewSubtask(null);
        restoreBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_MAP, restoreBackedTasksManager.getTaskList());
        assertEquals(Collections.EMPTY_MAP, restoreBackedTasksManager.getEpicList());
        assertEquals(Collections.EMPTY_MAP, restoreBackedTasksManager.getSubtaskList());
    }

    /*Пустой список истории*/
    @Test
    public void save_loadFile_shouldSaveAndLoadEmptyHistory() {
        fileBackTaskManager.addNewTask(null);
        fileBackTaskManager.addNewEpic(null);
        fileBackTaskManager.addNewSubtask(null);
        restoreBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, restoreBackedTasksManager.getHistory());
    }

}
