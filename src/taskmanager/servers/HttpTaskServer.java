package taskmanager.servers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import taskmanager.manager.TaskManager;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Gson gson = new Gson();
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new Handler(taskManager));
    }

    public void start() {
        System.out.println("Запустился сервер на " + PORT + " порт.");
        httpServer.start();
    }

    public void stop() {
        System.out.println("Сервер был остановлен.");
        httpServer.stop(1);
    }

    private static class Handler implements HttpHandler {
        private final TaskManager taskManager;

        public Handler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String url = exchange.getRequestURI().toString();
            String[] partUrl = url.split("/");

            Endpoint endpoint = getEndpoint(requestMethod, url);

            switch (endpoint) {
                case GET_TASKS:
                    handleGetTasks(exchange, partUrl[2]);
                    break;
                case GET_PRIORITIZED:
                    handleGetPrioritized(exchange);
                    break;
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    break;
                case GET_BY_ID:
                    handleGetById(exchange, partUrl);
                    break;
                case DELETE_TASKS:
                    handleDeleteTasks(exchange, partUrl[2]);
                    break;
                case DELETE_BY_ID:
                    handleDeleteById(exchange, partUrl);
                    break;
                case POST_TASK:
                    handlePostTask(exchange);
                    break;
                default:
                    writeResponse(exchange, "Некорректный запрос.", 404);
            }
        }

        private Endpoint getEndpoint(String requestMethod, String path) {
            String[] partUrl = path.split("/");
            if (requestMethod.equals("GET")) {
                switch (path) {
                    case "/tasks/":
                        return Endpoint.GET_PRIORITIZED;
                    case "/tasks/history/":
                        return Endpoint.GET_HISTORY;
                    case "/tasks/task/":
                    case "/tasks/subtask/":
                    case "/tasks/epic/":
                        return Endpoint.GET_TASKS;
                }
                if (partUrl[partUrl.length - 1].startsWith("?id")) {
                    return Endpoint.GET_BY_ID;
                }
            }
            if (requestMethod.equals("DELETE")) {
                switch (path) {
                    case "/tasks/task":
                    case "/tasks/subtask":
                    case "/tasks/epic":
                        return Endpoint.DELETE_TASKS;
                }
                if (partUrl[partUrl.length - 1].startsWith("?id")) {
                    return Endpoint.DELETE_BY_ID;
                }
            }
            if (requestMethod.equals("POST") && partUrl[1].equals("tasks") && partUrl.length == 3) {
                return Endpoint.POST_TASK;
            }
            return Endpoint.UNKNOWN;
        }

        private Optional<Integer> getTaskId(String[] urlParts) {
            try {
                return Optional.of(Integer.parseInt(urlParts[urlParts.length - 1].split("=")[1]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private void handleGetTasks(HttpExchange exchange, String type) throws IOException {
            switch (type) {
                case "task":
                    writeResponse(exchange, gson.toJson(taskManager.getTaskList()), 200);
                    return;
                case "subtask":
                    writeResponse(exchange, gson.toJson(taskManager.getSubtaskList()), 200);
                    return;
                case "epic":
                    writeResponse(exchange, gson.toJson(taskManager.getEpicList()), 200);
                    return;
            }
            writeResponse(exchange, "Некорректный запрос.", 400);
        }

        private void handleGetPrioritized(HttpExchange exchange) throws IOException {
            Collection<Task> prioritized = taskManager.getPrioritizedTasks();
            if (prioritized.isEmpty()) {
                writeResponse(exchange, "Лист приоритизации задач пустой.", 404);
                return;
            }
            writeResponse(exchange, gson.toJson(prioritized), 200);
        }

        private void handleGetById(HttpExchange exchange, String[] partUrl) throws IOException {
            Optional<Integer> optionalId = getTaskId(partUrl);
            if (optionalId.isEmpty()) {
                writeResponse(exchange, "Некорректный индификатор задачи.", 400);
                return;
            }
            int id = optionalId.get();
            if (partUrl.length == 4 && partUrl[2].equals("task")) {
                try {
                    writeResponse(exchange, gson.toJson(taskManager.getTaskById(id)), 200);
                } catch (NoSuchElementException e) {
                    writeResponse(exchange, e.getMessage(), 404);
                }
            } else if (partUrl.length == 5 && partUrl[2].equals("subtask") && partUrl[3].equals("epic")) {
                try {
                    for (Epic epic : taskManager.getEpicList().values())
                        if (epic.getId() == id) {
                            writeResponse(exchange, gson.toJson(taskManager.getSubtasksOfEpic(epic)), 200);
                        }

                } catch (NoSuchElementException e) {
                    writeResponse(exchange, e.getMessage(), 404);
                }
            } else {
                writeResponse(exchange, "Некорректный запрос.", 400);
            }
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task;
                Subtask subtask;
                Epic epic;
                switch (exchange.getRequestURI().getPath()) {
                    case "/tasks/task/":
                        task = gson.fromJson(body, Task.class);
                        taskManager.addNewTask(task);
                        writeResponse(exchange, "Задача была создана.", 201);
                        break;
                    case "/tasks/epic/":
                        epic = gson.fromJson(body, Epic.class);
                        taskManager.addNewEpic(epic);
                        writeResponse(exchange, "Эпик был создан", 201);
                        break;
                    case "/tasks/subtask/":
                        subtask = gson.fromJson(body, Subtask.class);
                        taskManager.addNewSubtask(subtask);
                        writeResponse(exchange, "Подзадача была создана.", 201);
                        break;
                    default:
                        writeResponse(exchange, "Некорректный запрос.", 400);
                        break;
                }
            } catch (JsonSyntaxException e) {
                writeResponse(exchange, "Некорректный JSON", 400);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange, String type) throws IOException {
            switch (type) {
                case "task":
                    taskManager.removeAllTask();
                    writeResponse(exchange, "Все задачи были удалены.", 200);
                    return;
                case "epic":
                    taskManager.removeAllEpics();
                    writeResponse(exchange, "Все эпики были удалены.", 200);
                    return;
                case "subtask":
                    taskManager.removeAllSubtasks();
                    writeResponse(exchange, "Все подзадачи были удалены.", 200);
                    return;
            }
            writeResponse(exchange, "Некорректный запрос.", 400);
        }

        private void handleDeleteById(HttpExchange exchange, String[] partUrl) throws IOException {
            Optional<Integer> optionalId = getTaskId(partUrl);
            if (optionalId.isEmpty()) {
                writeResponse(exchange, "некорректный индификатор.", 400);
                return;
            }
            int id = optionalId.get();
            if (partUrl[2].equals("task")) {
                try {
                    taskManager.removeTaskById(id);
                    writeResponse(exchange, "Задача была удалена", 200);
                } catch (NoSuchElementException e) {
                    writeResponse(exchange, e.getMessage(), 404);
                }
            } else {
                writeResponse(exchange, "Некорректный запрос.", 400);
            }
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            Collection<Task> history = taskManager.getHistory();
            if (history.isEmpty()) {
                writeResponse(exchange, "Лист истории пустой", 404);
                return;
            }
            List<Integer> historyIds = history
                    .stream()
                    .map(Task::getId)
                    .collect(Collectors.toList());
            writeResponse(exchange, gson.toJson(historyIds), 200);
        }

        private void writeResponse(HttpExchange exchange, String response, int code) throws IOException {
            if (response.isBlank()) {
                exchange.sendResponseHeaders(code, 0);
            } else {
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(code, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        public enum Endpoint {
            GET_TASKS,
            GET_PRIORITIZED,
            GET_HISTORY,
            GET_BY_ID,
            DELETE_TASKS,
            DELETE_BY_ID,
            POST_TASK,
            UNKNOWN
        }
    }

}
