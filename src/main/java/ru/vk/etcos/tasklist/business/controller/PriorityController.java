package ru.vk.etcos.tasklist.business.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.search.*;
import ru.vk.etcos.tasklist.business.sevice.*;
import ru.vk.etcos.tasklist.util.*;

@RestController
@RequestMapping("/priority")
public class PriorityController {

    private final PriorityService priorityService;

    @Autowired
    public PriorityController(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @PostMapping("/id")
    public ResponseEntity<CPriority> findById(@RequestBody Long id) {
        CLogger.info("PriorityController.findById for id: " + id);

        if (Objects.isNull(id) || id == 0) {
            String msg = "Priority missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<CPriority> optPriority = priorityService.findById(id);

        if (optPriority.isEmpty()) {
            String msg = "Priority with id = " + id + " not found";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(optPriority.get());
    }

    @PostMapping("/all")
    public ResponseEntity<List<CPriority>> findAll(@RequestBody String email) {
        CLogger.info("PriorityController.findAll for email: " + email);

        return ResponseEntity.ok(priorityService.findAll(email));
    }

    @PutMapping("/add")
    public ResponseEntity<CPriority> add(@RequestBody CPriority priority) {
        CLogger.info("PriorityController.add for priority: " + priority);

        if (Objects.nonNull(priority.getId()) && priority.getId() != 0) {
            String msg = "Priority redundant param: id MUST be null";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(priority.getTitle()) || priority.getTitle().trim().length() == 0) {
            String msg = "Priority missed param: title";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(priority.getColor()) || priority.getColor().trim().length() == 0) {
            String msg = "Priority missed param: color";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(priorityService.addOrUpdate(priority));
    }

    @PatchMapping("/update")
    public ResponseEntity update(@RequestBody CPriority priority) {
        CLogger.info("PriorityController.update for priority: " + priority);

        if (Objects.isNull(priority.getId()) || priority.getId() == 0) {
            String msg = "Priority missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(priority.getTitle()) || priority.getTitle().trim().length() == 0) {
            String msg = "Priority missed param: title";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(priority.getColor()) || priority.getColor().trim().length() == 0) {
            String msg = "Priority missed param: color";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        priorityService.addOrUpdate(priority);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestBody Long id) {
        CLogger.info("PriorityController.delete for priority id: " + id);

        if (Objects.isNull(id) || id == 0) {
            String msg = "Priority missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            priorityService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            String msg = "id = " + id + " not found";
            CLogger.warn(msg);
            CLogger.warn(e.getLocalizedMessage());
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<CPriority>> search(@RequestBody PrioritySearchValues values) {
        CLogger.info("PriorityController.search for priority values: " + values);

        List<CPriority> result = priorityService.findByValues(values);

        return ResponseEntity.ok(result);
    }

}
