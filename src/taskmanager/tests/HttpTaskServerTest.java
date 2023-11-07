package taskmanager.tests;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.manager.CSVFormatter;
import taskmanager.manager.HttpTaskManager;
import taskmanager.manager.Managers;
import taskmanager.manager.TaskManager;
import taskmanager.servers.HttpTaskServer;
import taskmanager.servers.KVServer;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest extends TaskManagerTest<HttpTaskManager> {
    private TaskManager manager;
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private static final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    @BeforeEach
    public void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault("http://localhost:8078");
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    @Test
    void shouldPOSTTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        manager.addNewTask(task);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(CSVFormatter.toString(task), CSVFormatter.toString(manager.getTaskById(task.getId())));
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETTask() throws IOException, InterruptedException {
        manager.addNewTask(task);
        manager.addNewTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        HashMap<Integer, Task> tasksFromJson =
                gson.fromJson(jsonElement, new TypeToken<HashMap<Integer, Task>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(2, tasksFromJson.size());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        task.setId(1);
        int id = task.getId();
        manager.addNewTask(task);
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int jsonId = jsonObject.get("id").getAsInt();
        String jsonDescription = jsonObject.get("description").getAsString();

        assertEquals(id, jsonId);
        assertTrue(jsonElement.isJsonObject(), "Некорректный JSON");
        assertEquals(task.getDescription(), jsonDescription);
    }

    @Test
    void shouldDELETETask() throws IOException, InterruptedException {
        manager.addNewTask(task);
        manager.addNewTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все задачи были удалены.", response.body());
    }

    @Test
    void shouldPOSTEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        manager.addNewEpic(epic);
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(CSVFormatter.toString(epic), CSVFormatter.toString(manager.getEpicById(epic.getId())));
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETEpic() throws IOException, InterruptedException {
        manager.addNewEpic(epic);
        manager.addNewEpic(epic2);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        HashMap<Integer, Task> tasksFromJson =
                gson.fromJson(jsonElement, new TypeToken<HashMap<Integer, Epic>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(2, tasksFromJson.size());
    }

    @Test
    void shouldDELETEEpic() throws IOException, InterruptedException {
        manager.addNewEpic(epic);
        manager.addNewEpic(epic2);
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все эпики были удалены.", response.body());
    }

    @Test
    void shouldPOSTSubtask() throws IOException, InterruptedException {
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);
        epic.setId(subtask.getEpicId());
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(subtask.toString(), manager.getSubtaskById(subtask.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETSubtask() throws IOException, InterruptedException {
        manager.addNewEpic(epic);
        manager.addNewSubtask(subtask);
        epic.setId(subtask.getEpicId());
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        HashMap<Integer, Subtask> tasksFromJson =
                gson.fromJson(jsonElement, new TypeToken<HashMap<Integer, Subtask>>(){}.getType());

        assertEquals(200, response.statusCode());
        assertEquals(1, tasksFromJson.size());
    }

    @Test
    void shouldDELETESubtask() throws IOException, InterruptedException {
        manager.addNewSubtask(subtask);
        manager.addNewSubtask(subtask2);
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все подзадачи были удалены.", response.body());
    }

    @Test
    void shouldGETHistory() throws IOException, InterruptedException {
        manager.addNewTask(task);
        manager.addNewTask(task2);
        manager.getTaskById(task.getId());
        manager.getTaskById(task2.getId());
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray(), "Некорректный JSON");
        assertEquals(2, jsonArray.size());
    }

    @Test
    void shouldGETPrioritized() throws IOException, InterruptedException {
        manager.addNewTask(task);
        manager.addNewTask(task2);
        task.setStartTime(LocalDateTime.of(2023, 11, 14, 12, 0));
        task2.setStartTime(LocalDateTime.of(2023, 1, 14, 12, 0));
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray(), "Incorrect JSON");
        assertEquals(2, jsonElement.getAsJsonArray().size());
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }
}