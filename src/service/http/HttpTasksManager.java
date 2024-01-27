package service.http;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.file.FileBackedTasksManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpTasksManager extends FileBackedTasksManager {
    private final Gson gson = Managers.getGson();
    private final KVTaskClient client;

    public HttpTasksManager(String url, boolean load) {
        client = new KVTaskClient(url);
        if (load) {
            load();
        }
    }

    @Override
    public void save() {
        client.put("tasks", gson.toJson(tasks));
        client.put("epics", gson.toJson(epics));
        client.put("subtasks", gson.toJson(subtasks));
        client.put("history", gson.toJson(historyManager.getHistory()));
    }

    @Override
    public void load() {
        String tasksJson = client.load("tasks");
        tasks = gson.fromJson(tasksJson, new TypeToken<HashMap<Integer, Task>>() {
        }.getType());

        String epicsJson = client.load("epics");
        epics = gson.fromJson(epicsJson, new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());

        String subtasksJson = client.load("subtasks");
        subtasks = gson.fromJson(subtasksJson, new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType());

        String historyJ = client.load("history");
        List<Integer> history = gson.fromJson(historyJ, new TypeToken<ArrayList<Integer>>() {
        }.getType());
        for (Integer id : history) {
            addTaskIdToHistory(id);
        }
    }
}
