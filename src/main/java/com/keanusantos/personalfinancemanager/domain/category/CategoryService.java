package com.keanusantos.personalfinancemanager.domain.category;

import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository repository;


    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Category insert(Category obj) {
        return repository.save(obj);
    }

    public Category update(Long id, Category obj) {
        Category category = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        updateData(category, obj);
        return repository.save(category);
    }

    private void updateData(Category category, Category obj) {
        category.setName(obj.getName());
        category.setColor(obj.getColor());
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }

}
