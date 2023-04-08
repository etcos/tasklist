package ru.vk.etcos.tasklist.business.sevice;

import java.util.*;

import jakarta.transaction.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.repository.*;

@Service
@Transactional
public class TaskService {

    private final TaskRepo taskRepo;

    @Autowired
    public TaskService(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public List<CTask> findAll(String email) {
        return taskRepo.findByUserEmailOrderByTitleAsc(email);
    }

    public CTask addOrUpdate(CTask task) {
        return taskRepo.save(task);
    }

    public void deleteById(Long id) {
        taskRepo.deleteById(id);
    }

    public Optional<CTask> findById(Long id) {
        return taskRepo.findById(id);
    }
}
