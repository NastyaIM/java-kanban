package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId = 0;

    public Subtask(String name, String description, State state) {
        super(name, description, state);
    }

    public Subtask(int id, String name, String description, State state) {
        super(id, name, description, state);
    }

    public Subtask(String name, String description, State state, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, state, duration, startTime);
    }

    public Subtask(int id, String name, String description, State state, Duration duration,
                   LocalDateTime startTime) {
        super(id, name, description, state, duration, startTime);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return this.id + ","
                + TaskType.SUBTASK + ","
                + this.name + ","
                + this.state + ","
                + this.description + ","
                + this.duration + ","
                + this.startTime.format(Const.DATE_TIME_FORMATTER) + ","
                + this.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}