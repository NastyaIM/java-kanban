package test;

import model.Epic;
import model.State;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected int taskId;
    protected Epic epic;
    protected int epicId;
    protected Subtask subtask1;
    protected int subtask1Id;
    protected Subtask subtask2;
    protected int subtask2Id;

    @BeforeEach
    public void beforeEach() throws IOException {
        addCommonTasks();
    }

    @Test
    public void addTask() {
        System.out.println(taskManager.getTasks());
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        int duplicateId = taskManager.addTask(task);

        assertEquals(-1, duplicateId, "Добавили дубликат");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addEpic() {
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        int duplicateId = taskManager.addEpic(epic);

        assertEquals(-1, duplicateId, "Добавили дубликат");

        final List<Subtask> epicSubtasks = taskManager.getEpicSubtasksById(epicId);

        assertEquals(List.of(subtask1, subtask2), epicSubtasks);

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addSubtask() {
        final Subtask savedSubtask = taskManager.getSubtaskById(subtask1Id);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");

        assertEquals(epicId, subtask1.getEpicId());

        int duplicateId = taskManager.addSubtask(subtask1, epicId);

        assertEquals(-1, duplicateId, "Добавили дубликат");

        assertEquals(epicId, savedSubtask.getEpicId());

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void getPrioritizedTasks() {
        task = taskManager.updateTask(new Task(taskId, "T1", "Dt1", State.NEW,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JANUARY, 8, 10, 11)));
        subtask2 = taskManager.updateSubtask(new Subtask(subtask2Id, "Sb2", "Dsb2",
                State.NEW, Duration.ofMinutes(8),
                LocalDateTime.of(2024, Month.JANUARY, 10, 11, 13)));

        assertEquals(List.of(task, subtask2, subtask1), new ArrayList<>(taskManager.getPrioritizedTasks()));

        taskManager.removeSubtaskById(subtask2Id);
        assertEquals(List.of(task, subtask1), new ArrayList<>(taskManager.getPrioritizedTasks()));

        taskManager.removeTasks();
        assertEquals(List.of(subtask1), new ArrayList<>(taskManager.getPrioritizedTasks()));

        System.out.println(taskManager.getTasks());
        taskManager.addTask(task);
        epic = taskManager.updateEpic(new Epic(epicId, "NewEpic", "NewDe1"));
        assertEquals(List.of(task), new ArrayList<>(taskManager.getPrioritizedTasks()));
    }

    @Test
    public void getTaskByIncorrectId() {
        assertNotNull(taskManager.getTaskById(taskId));
        assertNull(taskManager.getTaskById(taskId + 16));
    }

    @Test
    public void getEpicByIncorrectId() {
        assertNotNull(taskManager.getEpicById(epicId));
        assertNull(taskManager.getEpicById(epicId + 16));
    }

    @Test
    public void getSubtaskByIncorrectId() {
        assertNotNull(taskManager.getSubtaskById(subtask1Id));
        assertNull(taskManager.getSubtaskById(subtask1Id + 16));
    }

    @Test
    public void removeTasks() {
        Task task2 = new Task("T2", "Dt2", State.NEW);
        taskManager.addTask(task2);
        assertEquals(2, taskManager.getTasks().size());
        taskManager.removeTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void removeEpicsAndSubtasks() {
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(2, taskManager.getSubtasks().size());

        taskManager.removeSubtasks();
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());

        taskManager.addSubtask(subtask2, epicId);
        assertEquals(1, taskManager.getSubtasks().size());

        taskManager.removeEpics();
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void removeTaskById() {
        assertEquals(1, taskManager.getTasks().size());

        taskManager.removeTaskById(taskId + 16);
        assertEquals(1, taskManager.getTasks().size());

        taskManager.removeTaskById(taskId);
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void removeEpicAndSubtaskById() {
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(2, taskManager.getSubtasks().size());

        taskManager.removeEpicById(epicId + 16);
        assertEquals(1, taskManager.getEpics().size());
        taskManager.removeSubtaskById(subtask1Id + 16);
        assertEquals(2, taskManager.getSubtasks().size());

        taskManager.removeSubtaskById(subtask1Id);
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());

        taskManager.addSubtask(subtask1, epicId);

        taskManager.removeEpicById(epicId);
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void getHistory() {
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtask2Id);
        taskManager.getSubtaskById(subtask1Id);

        assertEquals(List.of(task, epic, subtask2, subtask1), taskManager.getHistory());

        taskManager.getTaskById(taskId);
        assertEquals(List.of(epic, subtask2, subtask1, task), taskManager.getHistory());

        taskManager.removeSubtaskById(subtask2Id);
        assertEquals(List.of(epic, subtask1, task), taskManager.getHistory());

        taskManager.removeEpics();
        assertEquals(List.of(task), taskManager.getHistory());
    }

    @Test
    public void updateTask() {
        Task savedTask = taskManager.getTaskById(taskId);
        task = taskManager.updateTask(new Task(taskId, "NewTask", "NewDt1", State.IN_PROGRESS));
        Task updatedTask = taskManager.getTaskById(taskId);

        assertNotEquals(savedTask, updatedTask);
        assertEquals(task, updatedTask);
    }

    @Test
    public void updateEpic() {
        Epic savedEpic = taskManager.getEpicById(epicId);
        epic = taskManager.updateEpic(new Epic(epicId, "NewEpic", "NewDe1"));
        Epic updatedEpic = taskManager.getEpicById(epicId);

        assertNotEquals(savedEpic, updatedEpic);
        assertEquals(epic, updatedEpic);
        assertEquals(List.of(), epic.getSubtasksIds());
    }

    @Test
    public void updateSubtask() {
        Subtask savedSubtask = taskManager.getSubtaskById(subtask1Id);
        subtask1 = taskManager.updateSubtask(new Subtask(subtask1Id, "NewSubtask1",
                "NewDs1", State.IN_PROGRESS));
        Subtask updatedSubtask = taskManager.getSubtaskById(subtask1Id);

        assertNotEquals(savedSubtask, updatedSubtask);
        assertEquals(subtask1, updatedSubtask);
        assertEquals(taskManager.getEpicSubtasksById(epicId), List.of(updatedSubtask, subtask2));
    }

    @Test
    public void unableToAddTaskIfTimeWasTaken() {
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        Task task1 = new Task("T1", "Dt1", State.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2024, Month.JANUARY, 11, 13, 12));
        taskManager.addTask(task1);
        assertEquals(1, taskManager.getTasks().size());

        Task startTimeBeforeEndTimeAfter = new Task("T1", "Dt1", State.NEW, Duration.ofMinutes(20),
                LocalDateTime.of(2024, Month.JANUARY, 11, 13, 9));
        taskManager.addTask(startTimeBeforeEndTimeAfter);
        assertEquals(1, taskManager.getTasks().size());

        Task startTimeBeforeEndTimeBefore = new Task("T1", "Dt1", State.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2024, Month.JANUARY, 11, 13, 9));
        taskManager.addTask(startTimeBeforeEndTimeBefore);
        assertEquals(1, taskManager.getTasks().size());

        Task startTimeAfterEndTimeBefore = new Task("T1", "Dt1", State.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 11, 13, 15));
        taskManager.addTask(startTimeAfterEndTimeBefore);
        assertEquals(1, taskManager.getTasks().size());

        Task startTimeAfterEndTimeAfter = new Task("T1", "Dt1", State.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2024, Month.JANUARY, 11, 13, 15));
        taskManager.addTask(startTimeAfterEndTimeAfter);
        assertEquals(1, taskManager.getTasks().size());

        Task theSameTime = new Task("T2", "Dt2", State.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2024, Month.JANUARY, 11, 13, 12));
        taskManager.addTask(theSameTime);
        assertEquals(1, taskManager.getTasks().size());
    }

    public void addCommonTasks() {
        task = new Task("T1", "Dt1", State.NEW);
        taskId = taskManager.addTask(task);
        epic = new Epic("E1", "De1");
        epicId = taskManager.addEpic(epic);
        subtask1 = new Subtask("Sb1", "Dsb1", State.NEW);
        subtask2 = new Subtask("Sb2", "Dsb2", State.NEW);
        subtask1Id = taskManager.addSubtask(subtask1, epicId);
        subtask2Id = taskManager.addSubtask(subtask2, epicId);
    }
}
