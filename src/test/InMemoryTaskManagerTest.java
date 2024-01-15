package test;

import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;
import service.Managers;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        super.beforeEach();
    }
}
