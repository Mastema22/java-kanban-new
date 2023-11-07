package taskmanager.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import taskmanager.servers.KVTaskClient;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.Task;

import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private static final Gson gson = new Gson();

    public HttpTaskManager(HistoryManager historyManager, String url, KVTaskClient taskClient) {
        super(historyManager, url);
        this.taskClient = taskClient;
    }

    @Override
    public void save() {
        taskClient.put("task", gson.toJson(taskHashMap.values()));
        taskClient.put("subtask", gson.toJson(subtaskHashMap.values()));
        taskClient.put("epic", gson.toJson(epicHashMap.values()));
        taskClient.put("tasks", gson.toJson(getPrioritizedTasks()));
        List<Integer> historyIds = getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        taskClient.put("history", gson.toJson(historyIds));
    }

    public void loadFromServer() {
        loadTasks("task");
        loadTasks("subtask");
        loadTasks("epic");
        loadHistory();
    }

    private void loadTasks(String key) {
        JsonElement jsonElement = JsonParser.parseString(taskClient.load(key));
        JsonArray jsonTasksArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonTasksArray) {
            Task task;
            Epic epic;
            Subtask subtask;
            switch (key) {
                case "task":
                    task = gson.fromJson(element.getAsJsonObject(), Task.class);
                    taskHashMap.put(task.getId(), task);
                    addTaskToPrioritizedList(task);
                    break;
                case "subtask":
                    subtask = gson.fromJson(element.getAsJsonObject(), Subtask.class);
                    subtaskHashMap.put(subtask.getId(), subtask);
                    addTaskToPrioritizedList(subtask);
                    break;
                case "epic":
                    epic = gson.fromJson(element.getAsJsonObject(), Epic.class);
                    epicHashMap.put(epic.getId(), epic);
                    addTaskToPrioritizedList(epic);
                    break;
                default:
                    System.out.println("Не удается загрузить задачи");
                    return;
            }
        }
    }

    private void loadHistory() {
        JsonElement jsonElement = JsonParser.parseString(taskClient.load("history"));
        JsonArray jsonHistoryArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonHistoryArray) {
            int id = element.getAsInt();
            if (taskHashMap.containsKey(id)) {
                historyManager.add(taskHashMap.get(id));
            } else if (epicHashMap.containsKey(id)) {
                historyManager.add(epicHashMap.get(id));
            } else if (subtaskHashMap.containsKey(id)) {
                historyManager.add(subtaskHashMap.get(id));
            }
        }
    }
}