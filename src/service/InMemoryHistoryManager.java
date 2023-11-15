package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final List<Task> views = new ArrayList<>();

    private static final int VIEWS_COUNT = 10;

    @Override
    public List<Task> getHistory() {
        return views;
    }

    @Override
    public void add(Task task) {
        if (views.size() == VIEWS_COUNT) {
            views.remove(0);
        }
        views.add(task);
    }
}