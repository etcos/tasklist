package ru.vk.etcos.tasklist.business.sevice;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.repository.*;

@Service
public class PriorityService {

    private final PriorityRepo priorityRepo;

    @Autowired
    public PriorityService(PriorityRepo priorityRepo) {
        this.priorityRepo = priorityRepo;
    }

    public Optional<CPriority> findById(Long id) {
        return priorityRepo.findById(id);
    }
}
