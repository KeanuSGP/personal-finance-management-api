package com.keanusantos.personalfinancemanager.domain.category;

import com.keanusantos.personalfinancemanager.domain.category.dto.request.CreateCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.request.PutCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.response.CategoryResponseDTO;
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
    public List<CategoryResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public CategoryResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public CategoryResponseDTO insert(@Valid @RequestBody CreateCategoryDTO obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public CategoryResponseDTO update(@PathVariable Long id, @Valid @RequestBody PutCategoryDTO obj) {
        return service.update(id, obj);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

}
