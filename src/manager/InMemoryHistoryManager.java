package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if(historyList.size() == 10) {
            historyList.add(0,task);
        } else
            historyList.add(task);

    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

}
