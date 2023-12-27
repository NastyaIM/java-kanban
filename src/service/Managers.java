package service;

import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager fromFile(Path path) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(path);
        fileManager.loadFromFile(path);
        return fileManager;
    }

    public static HistoryManager getHistoryDefault() {
        return new InMemoryHistoryManager();
    }
}
