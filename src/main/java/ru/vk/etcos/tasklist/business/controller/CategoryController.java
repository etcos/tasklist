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
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired // во все параметры будут подставлены конкретные объекты (добавлять аннотацию необязательно)
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/id")
    public ResponseEntity<CCategory> findById(@RequestBody Long id) {
        CLogger.info("CategoryController.findById for id: " + id);

        if (Objects.isNull(id) || id == 0) {
            // id должен быть заполнен, т.к. это обновление объекта
            String msg = "Category missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<CCategory> optCategory = categoryService.findById(id);

        if (optCategory.isEmpty()) {
            String msg = "Category with id = " + id + " not found";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(optCategory.get());
    }

    @PostMapping("/all")
    public ResponseEntity<List<CCategory>> findAll(@RequestBody String email) {
        CLogger.info("CategoryController.findAll for email: " + email);

        return ResponseEntity.ok(categoryService.findAll(email));
    }

    // Передается объект для вставки в БД
    @PutMapping("/add")
    public ResponseEntity<CCategory> add(@RequestBody CCategory category) {
        CLogger.info("CategoryController.add for category: " + category);

        // Проверка на обязательные параметры

        if (Objects.nonNull(category.getId()) && category.getId() != 0) {
            // id создается автоматически в БД (autoincrement), поэтому его не передавать не нужно,
            // иначе может возникнуть конфликт уникальности значения
            String msg = "Category redundant param: id MUST be null";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(category.getTitle()) || category.getTitle().trim().length() == 0) {
            // title должен быть обязательно заполнен
            String msg = "Category missed param: title";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        // Получаем созданный в БД объект уже с id м передаем его клиенту
        return ResponseEntity.ok(categoryService.addOrUpdate(category));
    }

    // Передается объект для обновления в БД
    @PatchMapping("/update")
    public ResponseEntity update(@RequestBody CCategory category) {
        CLogger.info("CategoryController.update for category: " + category);

        // Проверка на обязательные параметры

        if (Objects.isNull(category.getId()) || category.getId() == 0) {
            // id должен быть заполнен, т.к. это обновление объекта
            String msg = "Category missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        if (Objects.isNull(category.getTitle()) || category.getTitle().trim().length() == 0) {
            // title должен быть обязательно заполнен
            String msg = "Category missed param: title";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        // Обновляем объект в БД
        categoryService.addOrUpdate(category);

        // Возвращаем только статус, т.к. объект уже есть у клиента
        return ResponseEntity.ok().build();
    }

    // Передается id для удаления из БД
    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestBody Long id) {
        CLogger.info("CategoryController.delete for category id: " + id);

        if (Objects.isNull(id) || id == 0L) {
            // id должен быть заполнен, т.к. это обновление объекта
            String msg = "Category missed param: id";
            CLogger.warn(msg);
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            categoryService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            String msg = "id = " + id + " not found";
            CLogger.warn(msg);
            CLogger.warn(e.getLocalizedMessage());
            return new ResponseEntity(msg, HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<CCategory>> search(@RequestBody CategorySearchValues values) {
        CLogger.info("CategoryController.search for category values: " + values);

        // поиск категорий пользователя по названию
        List<CCategory> result = categoryService.findByValues(values);

        return ResponseEntity.ok(result);
    }

}
