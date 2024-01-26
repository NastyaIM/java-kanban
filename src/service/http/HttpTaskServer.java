package service.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.Const;
import model.Epic;
import model.Subtask;
import model.Task;
import service.ManagerSaveException;
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

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);
        server.start();
    }

    private void handleTasks(HttpExchange httpExchange) {
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
            throw new ManagerSaveException("Ошибка", exception);
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

    private void handleGet(HttpExchange httpExchange, String path) throws IOException {
        String response;
        if (Pattern.matches("^/tasks/task$", path)) {
            response = gson.toJson(taskManager.getTasks());
        } else if (Pattern.matches("^/tasks/subtask$", path)) {
            response = gson.toJson(taskManager.getSubtasks());
        } else if (Pattern.matches("^/tasks/epic$", path)) {
            response = gson.toJson(taskManager.getEpics());
        } else if (Pattern.matches("^/tasks/history$", path)) {
            response = gson.toJson(taskManager.getHistory());
        } else if (Pattern.matches("^/tasks$", path)) {
            response = gson.toJson(taskManager.getPrioritizedTasks());
        } else if (Pattern.matches("^/tasks/subtask/epic/\\?id=\\d+$", path)) {
            response = getEpicSubtasksJson(httpExchange);
        } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", path)) {
            response = getTaskJson(httpExchange);
        } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", path)) {
            response = getSubtaskJson(httpExchange);
        } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", path)) {
            response = getEpicJson(httpExchange);
        } else {
            writeResponse(httpExchange, "Такого GET запроса нет", 404);
            return;
        }
        writeResponse(httpExchange, response, 200);
    }

    private String getEpicSubtasksJson(HttpExchange httpExchange) {
        int epicId = getIdFromPath(httpExchange);
        if (epicId != -1) {
            if (taskManager.getEpicById(epicId) != null) {
                return gson.toJson(taskManager.getEpicSubtasksById(epicId));
            } else {
                return "Эпика с таким идектификатором пока нет";
            }
        }
        return "Некорректный идентификатор";
    }

    private String getTaskJson(HttpExchange httpExchange) {
        int id = getIdFromPath(httpExchange);
        if (id != -1) {
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                return gson.toJson(task);
            } else {
                return "Задачи с таким идектификатором пока нет";
            }
        }
        return "Некорректный идентификатор";
    }

    private String getSubtaskJson(HttpExchange httpExchange) {
        int id = getIdFromPath(httpExchange);
        if (id != -1) {
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                return gson.toJson(subtask);
            } else {
                return "Подзадачи с таким идектификатором пока нет";
            }
        }
        return "Некорректный идентификатор";
    }

    private String getEpicJson(HttpExchange httpExchange) {
        int id = getIdFromPath(httpExchange);
        if (id != -1) {
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                return gson.toJson(epic);
            } else {
                return "Эпика с таким идектификатором пока нет";
            }
        }
        return "Некорректный идентификатор";
    }

    private void handlePost(HttpExchange httpExchange, String path) throws IOException {
        String body = new String(httpExchange.getRequestBody().readAllBytes());
        String response;
        if (Pattern.matches("^/tasks/task$", path)) {
            response = addOrUpdateTask(body);
        } else if (Pattern.matches("^/tasks/subtask$", path)) {
            response = addOrUpdateSubtask(body);
        } else if (Pattern.matches("^/tasks/epic$", path)) {
            response = addOrUpdateEpic(body);
        } else {
            writeResponse(httpExchange, "Такого POST запроса нет", 404);
            return;
        }
        writeResponse(httpExchange, response, 200);
    }

    private String addOrUpdateTask(String taskString) {
        Task task;
        try {
            task = gson.fromJson(taskString, Task.class);
        } catch (Exception e) {
            return "Переданная задача не может быть обновлена/добавлена";
        }
        if (task.getId() != 0) {
            taskManager.updateTask(task);
            return "Задача обновлена";
        }
        int taskId = taskManager.addTask(task);
        if (taskId == -1) {
            return "Произошла ошибка. Задача не добавлена";
        }
        return "Задача добавлена";
    }

    private String addOrUpdateSubtask(String taskString) {
        Subtask subtask;
        try {
            subtask = gson.fromJson(taskString, Subtask.class);
        } catch (Exception e) {
            return "Переданная подзадача не может быть обновлена/добавлена";
        }
        if (subtask.getId() != 0) {
            taskManager.updateSubtask(subtask);
            return "Подзадача обновлена";
        }
        int subtaskId = taskManager.addSubtask(subtask, subtask.getEpicId());
        if (subtaskId == -1) {
            return "Произошла ошибка. Подзадача не добавлена";
        }
        return "Подзадача добавлена";
    }

    private String addOrUpdateEpic(String taskString) {
        Epic epic;
        try {
            epic = gson.fromJson(taskString, Epic.class);
        } catch (Exception e) {
            return "Переданный эпик не может быть обновлен/добавлен";
        }
        if (epic.getId() != 0) {
            taskManager.updateEpic(epic);
            return "Эпик обновлен";
        }
        int subtaskId = taskManager.addEpic(epic);
        if (subtaskId == -1) {
            return "Произошла ошибка. Эпик не добавлена";
        }
        return "Эпик добавлен";
    }

    private void handleDelete(HttpExchange httpExchange, String path) throws IOException {
        String response;
        if (Pattern.matches("^/tasks/task$", path)) {
            taskManager.removeTasks();
            response = "Задачи удалены";
        } else if (Pattern.matches("^/tasks/subtask$", path)) {
            taskManager.removeSubtasks();
            response = "Подзадачи удалены";
        } else if (Pattern.matches("^/tasks/epic$", path)) {
            taskManager.removeEpics();
            response = "Эпики с подзадачами удалены";
        } else if (Pattern.matches("^/tasks/task/\\?id=\\d+$", path)) {
            response = removeTask(httpExchange);
        } else if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", path)) {
            response = removeSubtask(httpExchange);
        } else if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", path)) {
            response = removeEpic(httpExchange);
        } else {
            writeResponse(httpExchange, "Такого DELETE запроса нет", 404);
            return;
        }
        writeResponse(httpExchange, response, 200);
    }

    private String removeTask(HttpExchange httpExchange) {
        int id = getIdFromPath(httpExchange);
        if (id != -1) {
            taskManager.removeTaskById(id);
            return "Задача удалена";
        }
        return "Некорректный идентификатор";
    }

    private String removeSubtask(HttpExchange httpExchange) {
        int id = getIdFromPath(httpExchange);
        if (id != -1) {
            taskManager.removeSubtaskById(id);
            return "Подзадача удалена";
        }
        return "Некорректный идентификатор";
    }

    private String removeEpic(HttpExchange httpExchange) {
        int id = getIdFromPath(httpExchange);
        if (id != -1) {
            taskManager.removeEpicById(id);
            return "Епик удален";
        }
        return "Некорректный идентификатор";
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
}