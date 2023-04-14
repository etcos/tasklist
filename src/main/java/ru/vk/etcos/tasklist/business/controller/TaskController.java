package ru.vk.etcos.tasklist.business.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.search.*;
import ru.vk.etcos.tasklist.business.sevice.*;
import ru.vk.etcos.tasklist.util.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    private static final String ID_COLUMN = "id";
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/all")
    public ResponseEntity<List<CTask>> findAll(@RequestBody String email) {
        CLogger.info("TaskController.findAll for email: " + email);

        return ResponseEntity.ok(taskService.findAll(email));
    }

    @PutMapping("/add")
    public ResponseEntity<CTask> add(@RequestBody CTask task) {
        CLogger.info("TaskController.add for task: " + task);

        if (Objects.nonNull(task.getId()) && task.getId() != 0) {
            String msg = "Task redundant param: id MUST be null";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(task.getTitle()) || task.getTitle().trim().length() == 0) {
            String msg = "Task missed param: title";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(taskService.addOrUpdate(task));
    }

    @PatchMapping("/update")
    public ResponseEntity update(@RequestBody CTask task) {
        CLogger.info("TaskController.update for task: " + task);

        if (Objects.isNull(task.getId()) || task.getId() == 0) {
            String msg = "Task missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(task.getTitle()) || task.getTitle().trim().length() == 0) {
            String msg = "Task missed param: title";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        taskService.addOrUpdate(task);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestBody Long id) {
        CLogger.info("TaskController.delete for task id: " + id);

        if (Objects.isNull(id) || id == 0L) {
            String msg = "Task missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            taskService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            String msg = "id = " + id + " not found";
            CLogger.warn(msg);
            CLogger.warn(e.getLocalizedMessage());
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/id")
    public ResponseEntity<CTask> findById(@RequestBody Long id) {
        CLogger.info("TaskController.findById for id: " + id);

        if (Objects.isNull(id) || id == 0) {
            String msg = "Task missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<CTask> optTask = taskService.findById(id);

        if (optTask.isEmpty()) {
            String msg = "Task with id = " + id + " not found";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(optTask.get());
    }

    @PostMapping("/search")
    public ResponseEntity<Page<CTask>> search(@RequestBody TaskSearchValues values) {
        CLogger.info("TaskController.search for task values: " + values);

        if (Objects.nonNull(values.getDateFrom())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(values.getDateFrom());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            values.setDateFrom(calendar.getTime());
        }

        if (Objects.nonNull(values.getDateTo())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(values.getDateTo());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            values.setDateTo(calendar.getTime());
        }

        Sort.Direction direction = Objects.isNull(values.getSortDirection())
                || values.getSortDirection().trim().isEmpty()
                || values.getSortDirection().trim().equals("asc")
            ? Sort.Direction.ASC : Direction.DESC;

        Sort sort = Sort.by(direction, Objects.isNull(values.getSortColumn()) ? ID_COLUMN : values.getSortColumn());
        PageRequest pageRequest = PageRequest.of(
            Objects.isNull(values.getPageNumber()) ? 0 : values.getPageNumber(),
            Objects.isNull(values.getPageSize()) ? 10 : values.getPageSize(),
            sort
        );

        return ResponseEntity.ok(taskService.findByValues(values, pageRequest));
    }

}
