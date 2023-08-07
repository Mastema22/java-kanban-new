package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if(historyList.size() >= 10) {
            for (int i = historyList.size()-1; i > 0; i--) {
                historyList.set(i,historyList.get(i-1));
            }
                historyList.set(0,task);
        } else if (historyList.size() < 10) {
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

}
