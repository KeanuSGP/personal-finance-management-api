package com.keanusantos.personalfinancemanager.domain.category;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    CategoryService service;

    @GetMapping
    public List<Category> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public Category findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Category insert(@Valid @RequestBody Category obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public Category update(@PathVariable Long id, @Valid @RequestBody Category obj) {
        return service.update(id, obj);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

}
