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
        if (subtasks.isEmpty()) {
            this.state = State.NEW;
            this.startTime = Const.defaultStartTime;
            this.duration = Const.defaultDuration;
        } else {
            Duration duration = subtasks.get(0).duration;
            LocalDateTime startTime = subtasks.get(0).startTime;
            State state = subtasks.get(0).state;
            for (int i = 1; i < subtasks.size(); i++) {
                Subtask subtask = subtasks.get(i);
                if (subtask.duration != Const.defaultDuration)
                    duration = duration.plus(subtask.duration);
                if (subtask.startTime != Const.defaultStartTime
                        && (subtask.startTime.isBefore(startTime) || startTime == Const.defaultStartTime)) {
                    startTime = subtask.startTime;
                }
                state = changeState(state, subtask);
            }
            this.state = state;
            this.duration = duration;
            this.startTime = startTime;
        }
    }

    private State changeState(State currentState, Subtask subtask) {
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
                + this.startTime.format(Const.dateTimeFormatter) + ",";
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