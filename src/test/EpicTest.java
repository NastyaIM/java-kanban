package test;

import model.Const;
import model.Epic;
import model.State;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private Epic epic;

    @BeforeEach
    public void beforeEach() {
        epic = new Epic("e1", "d1");
    }

    @Test
    public void epicStateNewWhenSubtasksEmpty() {
        epic.update(List.of());
        assertEquals(0, epic.getSubtasksIds().size());
        assertEquals(epic.getState(), State.NEW);
    }

    @Test
    public void epicStateNewWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.NEW);
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.NEW);
        epic.update(List.of(subtask1, subtask2));
        assertEquals(epic.getState(), State.NEW);
    }

    @Test
    public void epicStateDoneWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.DONE);
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.DONE);
        epic.update(List.of(subtask1, subtask2));
        assertEquals(epic.getState(), State.DONE);
    }

    @Test
    public void epicStateInProgressWhenSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.NEW);
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.DONE);
        epic.update(List.of(subtask1, subtask2));
        assertEquals(epic.getState(), State.IN_PROGRESS);
    }

    @Test
    public void epicStateInProgressWhenSubtasksInProgress() {
        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.IN_PROGRESS);
        epic.update(List.of(subtask1, subtask2));
        assertEquals(epic.getState(), State.IN_PROGRESS);
    }

    @Test
    public void startTimeAndDurationDefaultIfEpicWithoutSubtasks() {
        assertEquals(Const.DEFAULT_START_TIME, epic.getStartTime());
        assertEquals(Const.DEFAULT_DURATION, epic.getDuration());
    }

    @Test
    public void startTimeAndDurationEqualSubtasksStartTimeAndDurarion() {
        assertEquals(Const.DEFAULT_START_TIME, epic.getStartTime());
        assertEquals(Const.DEFAULT_DURATION, epic.getDuration());
        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.NEW, Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 10, 10, 13));
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.NEW, Duration.ofMinutes(8),
                LocalDateTime.of(2024, Month.JANUARY, 10, 11, 13));
        epic.update(List.of(subtask1, subtask2));
        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(13, epic.getDuration().toMinutes());
    }

    @Test
    public void startTimeNotDefaultWhenSubtaskStartTimeNotDefault() {
        Subtask subtask1 = new Subtask("Sb1", "Dsb1", State.NEW);
        Subtask subtask2 = new Subtask("Sb2", "Dsb2", State.NEW, Duration.ofMinutes(8),
                LocalDateTime.of(2024, Month.JANUARY, 10, 11, 13));
        epic.update(List.of(subtask1, subtask2));
        assertEquals(Const.DEFAULT_START_TIME, subtask1.getStartTime());
        assertEquals(subtask2.getStartTime(), epic.getStartTime());
    }
}