package ru.vk.etcos.tasklist.business.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.vk.etcos.tasklist.business.entity.*;
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
}
