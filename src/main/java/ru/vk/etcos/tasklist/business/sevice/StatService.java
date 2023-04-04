package ru.vk.etcos.tasklist.business.sevice;

import jakarta.transaction.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.repository.*;

@Service
@Transactional
public class StatService {

    private final StatRepo statRepo;

    public StatService(StatRepo statRepo) {
        this.statRepo = statRepo;
    }

    public CStat findStat(String email) {
        return statRepo.findByUserEmail(email);
    }

}
