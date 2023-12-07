package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Node head;
    Node tail;

    void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, null, task);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;
            head.prev = null;
        } else {
            prev.next = next;
            if (next != null) {
                next.prev = prev;
            }
        }
    }

    private final Map<Integer, Node> viewsMap = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
        viewsMap.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        Node node = viewsMap.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }
}