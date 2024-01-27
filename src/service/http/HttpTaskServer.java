package service.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Const;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static Gson gson;
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);
        server.start();
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().toString();

            switch (method) {
                case "GET":
                    handleGet(httpExchange, path);
                    break;
                case "POST":
                    handlePost(httpExchange, path);
                    break;
                case "DELETE":
                    handleDelete(httpExchange, path);
                    break;
                default:
                    writeResponse(httpExchange, "Неправильный метод запроса", 404);
                    break;
            }
        } catch (Exception exception) {
            writeResponse(httpExchange, exception.getMessage(), 500);
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        System.out.println("Останавливаем сервер на порту " + PORT);
        server.stop(1);
    }

    private void handleGet(HttpExchange exc, String path) throws IOException {
        if (Pattern.matches("^/tasks/task$", path)) {
            getTasks(exc);
        } else if (Pattern.matches("^/tasks/subtask$", path)) {
            getSubtasks(exc);
        } else if (Pattern.matches("^/tasks/epic$", path)) {
            getEpics(exc);
        } else if (Pattern.matches("^/tasks/history$", path)) {
            getHistory(exc);
        } else if (Pattern.matches("^/tasks$", path)) {
            getPrioritizedTasks(exc);
        } else if (Pattern.matches("^/tasks/subtask/epic/\\?id=\\d+$", path)) {
            getEpicSubtasksJson(exc);
        } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", path)) {
            getTaskJson(exc);
        } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", path)) {
            getSubtaskJson(exc);
        } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", path)) {
            getEpicJson(exc);
        } else {
            writeResponse(exc, "Такого GET запроса нет", 404);
        }
    }

    private void getTasks(HttpExchange exc) throws IOException {
        if (taskManager.getTasks() != null) {
            writeResponse(exc, gson.toJson(taskManager.getTasks()), 200);
            return;
        }
        writeResponse(exc, "Список задач пуст", 500);
    }

    private void getSubtasks(HttpExchange exc) throws IOException {
        if (taskManager.getSubtasks() != null) {
            writeResponse(exc, gson.toJson(taskManager.getSubtasks()), 200);
            return;
        }
        writeResponse(exc, "Список подзадач пуст", 500);
    }

    private void getEpics(HttpExchange exc) throws IOException {
        if (taskManager.getEpics() != null) {
            writeResponse(exc, gson.toJson(taskManager.getEpics()), 200);
            return;
        }
        writeResponse(exc, "Список эпиков пуст", 500);
    }

    private void getHistory(HttpExchange exc) throws IOException {
        if (taskManager.getHistory() != null) {
            writeResponse(exc, gson.toJson(taskManager.getHistory()), 200);
            return;
        }
        writeResponse(exc, "История пуста", 500);
    }

    private void getPrioritizedTasks(HttpExchange exc) throws IOException {
        if (taskManager.getPrioritizedTasks() != null) {
            writeResponse(exc, gson.toJson(taskManager.getPrioritizedTasks()), 200);
            return;
        }
        writeResponse(exc, "Список отсортированных задач пуст", 500);
    }


    private void getEpicSubtasksJson(HttpExchange exc) throws IOException {
        int epicId = getIdFromPath(exc);
        if (epicId != -1) {
            if (taskManager.getEpicById(epicId) != null) {
                writeResponse(exc, gson.toJson(taskManager.getEpicSubtasksById(epicId)), 200);
                return;
            }
            writeResponse(exc, "Несуществующий идентификатор", 500);
            return;
        }
        writeResponse(exc, "Некорректный идентификатор", 500);
    }

    private void getTaskJson(HttpExchange exc) throws IOException {
        int id = getIdFromPath(exc);
        if (id != -1) {
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                writeResponse(exc, gson.toJson(task), 200);
                return;
            }
            writeResponse(exc, "Несуществующий идентификатор", 500);
            return;
        }
        writeResponse(exc, "Некорректный идентификатор", 500);
    }

    private void getSubtaskJson(HttpExchange exc) throws IOException {
        int id = getIdFromPath(exc);
        if (id != -1) {
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                writeResponse(exc, gson.toJson(subtask), 200);
            }
            writeResponse(exc, "Несуществующий идентификатор", 500);
            return;
        }
        writeResponse(exc, "Некорректный идентификатор", 500);
    }

    private void getEpicJson(HttpExchange exc) throws IOException {
        int id = getIdFromPath(exc);
        if (id != -1) {
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                writeResponse(exc, gson.toJson(epic), 200);
                return;
            }
            writeResponse(exc, "Несуществующий идентификатор", 500);
            return;
        }
        writeResponse(exc, "Некорректный идентификатор", 500);
    }

    private void handlePost(HttpExchange exc, String path) throws IOException {
        String body = new String(exc.getRequestBody().readAllBytes());
        if (Pattern.matches("^/tasks/task$", path)) {
            addOrUpdateTask(exc, body);
        } else if (Pattern.matches("^/tasks/subtask$", path)) {
            addOrUpdateSubtask(exc, body);
        } else if (Pattern.matches("^/tasks/epic$", path)) {
            addOrUpdateEpic(exc, body);
        } else {
            writeResponse(exc, "Такого POST запроса нет", 404);
        }
    }

    private void addOrUpdateTask(HttpExchange exc, String taskString) throws IOException {
        Task task;
        try {
            task = gson.fromJson(taskString, Task.class);
        } catch (Exception e) {
            writeResponse(exc, e.getMessage(), 500);
            return;
        }
        if (task.getId() != 0) {
            taskManager.updateTask(task);
            writeResponse(exc, "Задача обновлена", 200);
            return;
        }
        int taskId = taskManager.addTask(task);
        if (taskId == -1) {
            writeResponse(exc, "Произошла ошибка. Задача не добавлена", 500);
            return;
        }
        writeResponse(exc, "Задача добавлена", 200);
    }

    private void addOrUpdateSubtask(HttpExchange exc, String taskString) throws IOException {
        Subtask subtask;
        try {
            subtask = gson.fromJson(taskString, Subtask.class);
        } catch (Exception e) {
            writeResponse(exc, e.getMessage(), 500);
            return;
        }
        if (subtask.getId() != 0) {
            taskManager.updateSubtask(subtask);
            writeResponse(exc, "Подзадача обновлена", 200);
            return;
        }
        int subtaskId = taskManager.addSubtask(subtask, subtask.getEpicId());
        if (subtaskId == -1) {
            writeResponse(exc, "Произошла ошибка. Подзадача не добавлена", 500);
            return;
        }
        writeResponse(exc, "Подзадача добавлена", 200);
    }

    private void addOrUpdateEpic(HttpExchange exc, String taskString) throws IOException {
        Epic epic;
        try {
            epic = gson.fromJson(taskString, Epic.class);
        } catch (Exception e) {
            writeResponse(exc, e.getMessage(), 500);
            return;
        }
        if (epic.getId() != 0) {
            taskManager.updateEpic(epic);
            writeResponse(exc, "Эпик обновлен", 200);
            return;
        }
        int subtaskId = taskManager.addEpic(epic);
        if (subtaskId == -1) {
            writeResponse(exc, "Произошла ошибка. Эпик не добавлена", 500);
            return;
        }
        writeResponse(exc, "Эпик добавлен", 200);
    }

    private void handleDelete(HttpExchange exc, String path) throws IOException {
        if (Pattern.matches("^/tasks/task$", path)) {
            removeTasks(exc);
        } else if (Pattern.matches("^/tasks/subtask$", path)) {
            removeSubtasks(exc);
        } else if (Pattern.matches("^/tasks/epic$", path)) {
            removeEpics(exc);
        } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", path)) {
            removeTaskById(exc);
        } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", path)) {
            removeSubtaskById(exc);
        } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", path)) {
            removeEpicById(exc);
        } else {
            writeResponse(exc, "Такого DELETE запроса нет", 404);
        }
    }

    private void removeTasks(HttpExchange exc) throws IOException {
        taskManager.removeTasks();
        writeResponse(exc, "Задачи удалены", 200);
    }

    private void removeSubtasks(HttpExchange exc) throws IOException {
        taskManager.removeSubtasks();
        writeResponse(exc, "Подзадачи удалены", 200);
    }

    private void removeEpics(HttpExchange exc) throws IOException {
        taskManager.removeEpics();
        writeResponse(exc, "Эпики с подзадачами удалены", 200);
    }

    private void removeTaskById(HttpExchange exc) throws IOException {
        int id = getIdFromPath(exc);
        if (id != -1) {
            taskManager.removeTaskById(id);
            writeResponse(exc, "Задача удалена", 200);
            return;
        }
        writeResponse(exc, "Некорректный идентификатор", 500);
    }

    private void removeSubtaskById(HttpExchange exc) throws IOException {
        int id = getIdFromPath(exc);
        if (id != -1) {
            taskManager.removeSubtaskById(id);
            writeResponse(exc, "Подзадача удалена", 200);
            return;
        }
        writeResponse(exc, "Некорректный идентификатор", 500);
    }

    private void removeEpicById(HttpExchange exc) throws IOException {
        int id = getIdFromPath(exc);
        if (id != -1) {
            taskManager.removeEpicById(id);
            writeResponse(exc, "Епик удален", 200);
            return;
        }
        writeResponse(exc, "Некорректный идентификатор", 500);
    }

    private int getIdFromPath(HttpExchange httpExchange) {
        String param = httpExchange.getRequestURI().getQuery();

        //String[] paramsPart = param.split("&");

        if (param.startsWith("id=")) {
            String id = param.replaceFirst("id=", "");
            return Integer.parseInt(id);
        }
        return -1;
    }

    private void writeResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
        if (response.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = response.getBytes(Const.DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}