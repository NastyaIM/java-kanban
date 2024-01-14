package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected int id;
    protected String name;
    protected String description;
    protected State state;
    protected Duration duration = Const.defaultDuration;
    protected LocalDateTime startTime = Const.defaultStartTime;

    //для эпика
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, State state) {
        this.name = name;
        this.description = description;
        this.state = state;
    }

    //для загрузки из файла эпика
    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    //для загрузки из файла тасков / сабтасков
    public Task(int id, String name, String description, State state) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
    }

    public Task(String name, String description, State state, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.state = state;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, State state, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return this.id + ","
                + TaskType.TASK + ","
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
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(duration, duration) &&
                Objects.equals(startTime, task.startTime) &&
                state == task.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, state);
    }

    @Override
    public int compareTo(Task o) {
        if (o.startTime.isEqual(Const.defaultStartTime))
            return -1;
        if (this.startTime.isEqual(Const.defaultStartTime))
            return 1;
        if (this.startTime.isBefore(o.startTime))
            return -1;
        if (o.startTime.isBefore(this.startTime))
            return 1;
        return 0;
    }
}