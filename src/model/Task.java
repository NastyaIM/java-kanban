package model;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected State state;

    public Task(String name, String description, State state) {
        this.name = name;
        this.description = description;
        this.state = state;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, String description, State state) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name +
                //'\'' +
                //", description='" + description + '\'' +
                //", state=" + state +
                '}';
    }
}