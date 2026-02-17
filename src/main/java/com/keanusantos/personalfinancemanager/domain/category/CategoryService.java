package com.keanusantos.personalfinancemanager.domain.category;

import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        validateData(obj, null);
        return repository.save(obj);
    }

    public Category update(Long id, Category obj) {
        Category category = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        updateData(category, obj);
        return repository.save(category);
    }

    private void updateData(Category category, Category obj) {
        validateData(obj, category.getId());
        category.setName(obj.getName());
        category.setColor(obj.getColor());
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private void validateData(Category obj, Long id) {
        if (repository.existsByNameAndIdNot(obj.getName(), id)) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }

        if (repository.existsByColorAndIdNot(obj.getColor(), id)) {
            throw new BusinessException("Categories must have different colors", HttpStatus.CONFLICT);
        }

        if (repository.existsByName(obj.getName())) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }

        if (repository.existsByColor(obj.getColor())) {
            throw new BusinessException("Categories must have different colors", HttpStatus.CONFLICT);
        }

    }

}
