package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.file.FileBackedTasksManager;
import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;
import service.http.HttpTasksManager;

import java.io.IOException;
import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager fromFile(Path path) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(path);
        fileManager.load();
        return fileManager;
    }

    public static HttpTasksManager fromServer() throws IOException {
        return new HttpTasksManager("http://localhost:8078/", false);
    }

    public static HistoryManager getHistoryDefault() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder().create();
    }
}
