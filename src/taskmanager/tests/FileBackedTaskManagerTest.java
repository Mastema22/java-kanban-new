package taskmanager.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.FileBackedTasksManager;
import taskmanager.manager.Managers;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Status;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest<FileBackedTasksManager> {
    private final Path path = Path.of("manager.cvs");
    private final File file = new File(String.valueOf(path));

    private final Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW, 10, LocalDateTime.of(2023,10,14,12,0));
    private final Epic epic = new Epic("Test addNewTask", "Test addNewTask description", 50, LocalDateTime.of(2023,10,14,12,30));
    private final Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description", Status.NEW,
            70, LocalDateTime.of(2023,10,14,12,35), 1);
    @BeforeEach
    public void beforeEach(){
         taskManager = new FileBackedTasksManager(Managers.getDefaultHistory(),"manager.cvs");
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
            taskManager.getTaskList().clear();
            taskManager.getEpicList().clear();
            taskManager.getSubtaskList().clear();
            taskManager.getPrioritizedTasks().clear();
            taskManager.getHistory().clear();
        } catch (IOException ex) {
            ex.getStackTrace();
        }
    }

    /*Пустой эпик без подзадач*/
    @Test
    public void save_loadFile_shouldSaveAndLoad() {
        Task task1 = taskManager.addNewTask(task);
        Epic epic1 = taskManager.addNewEpic(epic);
        taskManager.loadFromFile(file);
        HashMap<Integer, Task> taskHashMap = taskManager.getTaskList();
        List<Task> taskList = new ArrayList<>(taskHashMap.values());
        HashMap<Integer,Epic> epicHashMap = taskManager.getEpicList();
        List<Epic> epicList = new ArrayList<>(epicHashMap.values());

        assertEquals(List.of(task1), taskList);
        assertEquals(List.of(epic1), epicList);
    }

/*Пустой список задач*/
    @Test
    public void save_loadFile_shouldSaveAndLoadEmptyFile() {
        taskManager.save();
        taskManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_MAP, taskManager.getTaskList());
        assertEquals(Collections.EMPTY_MAP, taskManager.getEpicList());
        assertEquals(Collections.EMPTY_MAP, taskManager.getSubtaskList());
    }

/*Пустой список истории*/
    @Test
    public void save_loadFile_shouldSaveAndLoadEmptyHistory(){
        taskManager.save();
        taskManager.loadFromFile(file);

        assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
    }


}
