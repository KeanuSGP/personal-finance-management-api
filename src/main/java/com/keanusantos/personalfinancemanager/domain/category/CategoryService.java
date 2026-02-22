package com.keanusantos.personalfinancemanager.domain.category;

import com.keanusantos.personalfinancemanager.domain.category.dto.mapper.CategoryDTOMapper;
import com.keanusantos.personalfinancemanager.domain.category.dto.request.CreateCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.request.PutCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.response.CategoryResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.domain.user.dto.mapper.UserDTOMapper;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository repository;

    @Autowired
    private UserService userService;


    public Category findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public List<CategoryResponseDTO> findAll() {
        return repository.findAll().stream().map(CategoryDTOMapper::toResponseDTO).collect(Collectors.toList());
    }

    public CategoryResponseDTO findById(Long id) {
        return CategoryDTOMapper.toResponseDTO(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    public CategoryResponseDTO insert(CreateCategoryDTO obj) {
        User user = userService.findByIdEntity(obj.user());
        Category category = CategoryDTOMapper.toEntity(obj, user);

        if (repository.existsByName(obj.name())) {
            throw new ResourceAlreadyExistsException("Name " + obj.name() + " already exists");
        }

        if (repository.existsByColor(obj.color())) {
            throw new ResourceAlreadyExistsException("Color " + obj.color() + " already exists");
        }

        repository.save(category);
        return CategoryDTOMapper.toResponseDTO(category);
    }

    public CategoryResponseDTO update(Long id, PutCategoryDTO obj) {
        Category category = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        updateData(category, obj);
        repository.save(category);
        return CategoryDTOMapper.toResponseDTO(category);
    }

    private void updateData(Category category, PutCategoryDTO obj) {

        if (repository.existsByNameAndIdNot(obj.name(), category.getId())) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }

        if (repository.existsByColorAndIdNot(obj.color(), category.getId())) {
            throw new ResourceAlreadyExistsException("Color " + obj.color() + " already exists");
        }

        User user =  userService.findByIdEntity(obj.user());

        category.setName(obj.name());
        category.setColor(obj.color());
        category.setUser(user);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }


}
