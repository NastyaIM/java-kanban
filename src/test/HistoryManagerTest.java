package test;

import model.State;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.List;

class HistoryManagerTest {
    HistoryManager historyManager;
    Task task;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getHistoryDefault();
        task = new Task(1, "T1", "Dt1", State.NEW);
        historyManager.add(task);
    }

    @Test
    public void addTaskIfHistoryIsEmpty() {
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void addTaskIfHistoryIsNotEmpty() {
        task = new Task(2, "T2", "Dt2", State.NEW);
        historyManager.add(task);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void addDuplicate() {
        historyManager.add(task);
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void removeTaskFromBeginning() {
        Task task2 = new Task(2, "T2", "Dt2", State.NEW);
        historyManager.add(task2);
        Task task3 = new Task(3, "T3", "Dt3", State.NEW);
        historyManager.add(task3);
        Assertions.assertEquals(task, historyManager.getHistory().get(0));
        historyManager.remove(1);
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(task2, historyManager.getHistory().get(0));
    }

    @Test
    public void removeTaskFromMiddle() {
        Task task2 = new Task(2, "T2", "Dt2", State.NEW);
        historyManager.add(task2);
        Task task3 = new Task(3, "T3", "Dt3", State.NEW);
        historyManager.add(task3);
        historyManager.remove(2);
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(List.of(task, task3), historyManager.getHistory());
    }

    @Test
    public void removeTaskFromEnd() {
        Task task2 = new Task(2, "T2", "Dt2", State.NEW);
        historyManager.add(task2);
        Task task3 = new Task(3, "T3", "Dt3", State.NEW);
        historyManager.add(task3);
        Assertions.assertEquals(task3, historyManager.getHistory().get(2));
        historyManager.remove(3);
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals(task2, historyManager.getHistory().get(1));
    }
}