package ru.vk.etcos.tasklist.business.sevice;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.repository.*;
import ru.vk.etcos.tasklist.business.search.*;

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

    public List<CPriority> findAll(String email) {
        return priorityRepo.findByUserEmailOrderByIdAsc(email);
    }

    public CPriority addOrUpdate(CPriority priority) {
        return priorityRepo.save(priority);
    }

    public void deleteById(Long id) {
        priorityRepo.deleteById(id);
    }

    public List<CPriority> findByValues(PrioritySearchValues values) {
        return priorityRepo.findByValues(values.getTitle(), values.getEmail());
    }
}
