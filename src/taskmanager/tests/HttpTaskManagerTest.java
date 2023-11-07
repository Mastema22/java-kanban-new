package taskmanager.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.CSVFormatter;
import taskmanager.manager.HttpTaskManager;
import taskmanager.manager.Managers;
import taskmanager.servers.KVServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static KVServer server;

    @BeforeEach
    void setManager() {
        taskManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
    }

    @BeforeAll
    static void startServer() throws IOException {
        server = new KVServer();
        server.start();
    }

    @Test
    void shouldLoadFromServer() {
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask2);
        taskManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        taskManager.loadFromServer();

        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(2, taskManager.getHistory().size());
        assertEquals(CSVFormatter.toString(task), CSVFormatter.toString(taskManager.getTaskById(task.getId())));
        assertEquals(CSVFormatter.toString(epic), CSVFormatter.toString(taskManager.getEpicById(epic.getId())));

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}