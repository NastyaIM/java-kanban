package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtasksIds;

    public Epic(String name, String description) {
        super(name, description);
        subtasksIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtasksIds = new ArrayList<>();
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void addSubtaskId(int id) {
        subtasksIds.add(id);
    }

    public void removeSubtasksIds() {
        subtasksIds.clear();
    }

    public void update(List<Subtask> subtasks) {
        Duration duration = Const.DEFAULT_DURATION;
        LocalDateTime startTime = Const.DEFAULT_START_TIME;
        State state = null;
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks) {
                duration = duration.plus(subtask.duration);
                if (subtask.startTime.isBefore(startTime)) {
                    startTime = subtask.startTime;
                }
                state = changeState(state, subtask);
            }
        }
        this.state = state == null ? State.NEW : state;
        this.duration = duration;
        this.startTime = startTime;
    }

    private State changeState(State currentState, Subtask subtask) {
        if (currentState == null) {
            return subtask.state;
        }
        if (subtask.state == State.DONE && currentState == State.DONE) {
            return State.DONE;
        }
        if (subtask.state == State.NEW && currentState == State.NEW) {
            return State.NEW;
        }
        return State.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return this.id + ","
                + TaskType.EPIC + ","
                + this.name + ","
                + this.state + ","
                + this.description + ","
                + this.duration + ","
                + this.startTime.format(Const.DATE_TIME_FORMATTER) + ",";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds);
    }
}