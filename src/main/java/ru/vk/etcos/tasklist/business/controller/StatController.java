package ru.vk.etcos.tasklist.business.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.sevice.*;
import ru.vk.etcos.tasklist.util.*;

@RestController
public class StatController {

    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/stat")
    public ResponseEntity<CStat> findByEmail(@RequestBody String email) {
        CLogger.info("StatController.findByEmail for email: " + email);

        return ResponseEntity.ok(statService.findStat(email));
    }
}
