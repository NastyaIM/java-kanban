package Test;

import model.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    Path path;

    @BeforeEach
    public void beforeEach() {
        path = Paths.get("src/tasks.csv");

        try {
            Files.writeString(path, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = Managers.fromFile(path);
        addCommonTasks();
    }

    @Test
    public void saveFileWithEmptyHistory() {
        assertEquals(List.of(), taskManager.getHistory());
    }

    @Test
    public void saveFileWithEpicWithoutSubtasks() {
        Epic expectedEpic = new Epic("E1", "De1");
        assertEquals(epic, taskManager.getEpicById(epicId));
        taskManager.removeSubtasks();
        assertEquals(expectedEpic, taskManager.getEpicById(epicId));
    }

    @Test
    public void saveEmptyListOfTasks() {
        task = taskManager.getTaskById(taskId);
        assertEquals(List.of(task), taskManager.getHistory());

        taskManager.removeTasks();
        taskManager.removeEpics();
        assertEquals(List.of(), taskManager.getTasks());
        assertEquals(List.of(), taskManager.getEpics());
        assertEquals(List.of(), taskManager.getSubtasks());
        assertEquals(List.of(), taskManager.getHistory());
    }
}