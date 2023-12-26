package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
