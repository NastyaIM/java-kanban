package service;

import model.Task;

public class Node {
    public Node prev;
    public Node next;
    public Task task;

    public Node(Node prev, Node next, Task task) {
        this.prev = prev;
        this.next = next;
        this.task = task;
    }
}