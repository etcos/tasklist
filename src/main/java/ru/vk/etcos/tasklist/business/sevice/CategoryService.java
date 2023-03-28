package ru.vk.etcos.tasklist.business.sevice;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;
import ru.vk.etcos.tasklist.business.repository.*;

// Всегда нужно создавать отдельный класс Service для доступа к данным, даже если кажется, что мало методов
// или все можно реализовать сразу в контроллере
// Такой подход полезен для будущих доработок и правильной архитектуры(особенно, если работаете с транзакциями)

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    @Autowired // во все параметры будут подставлены конкретные объекты (добавлять аннотацию необязательно)
    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Optional<CCategory> findById(Long id) {
        return categoryRepo.findById(id);
    }

    public List<CCategory> findAll(String email) {
        return categoryRepo.findByUserEmailOrderByTitleAsc(email);
    }

    public CCategory addOrUpdate(CCategory category) {
        return categoryRepo.save(category);
    }

    public void deleteById(Long id) {
        categoryRepo.deleteById(id);
    }

    public List<CCategory> findByValues(String title, String email) {
        return categoryRepo.findByValues(title, email);
    }
}
