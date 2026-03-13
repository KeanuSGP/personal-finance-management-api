package com.keanusantos.personalfinancemanager.domain.category;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.category.dto.mapper.CategoryDTOMapper;
import com.keanusantos.personalfinancemanager.domain.category.dto.request.CreateCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.request.PutCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.response.CategoryResponseDTO;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository repository;

    @Autowired
    TransactionRepository transactionRepository;

    public Category findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public List<CategoryResponseDTO> findAll() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user =  userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));

        if (!isAdmin) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }

        return repository.findAll().stream().map(CategoryDTOMapper::toResponseDTO).toList();
    }

    public List<CategoryResponseDTO> findAllByAuthenticatedUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user =  userDetails.getUser();
        return repository.findAllByUser_id(user.getId()).stream().map(CategoryDTOMapper::toResponseDTO).toList();
    }

    public CategoryResponseDTO findById(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user =  userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }
        return CategoryDTOMapper.toResponseDTO(repository.findByIdAndUser_Id(id, user.getId()).orElseThrow(ResourceNotFoundException::new));
    }

    public Set<Category> findCategoriesByOwner(Set<Long> categoryIds, Long userId) {
        Set<Category> categories = repository.findAllByIdInAndUser_id(categoryIds, userId);
        if (categories.isEmpty() || categories.size() < categoryIds.size()) {
            throw new ResourceNotFoundException();
        }
        return categories;
    };

    public CategoryResponseDTO insert(CreateCategoryDTO obj) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user =  userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }
        Category category = CategoryDTOMapper.toEntity(obj, user);

        if (repository.existsByNameAndUserId(obj.name(), user.getId())) {
            throw new ResourceAlreadyExistsException("Name " + obj.name() + " already exists");
        }

        if (repository.existsByColorAndUserId(obj.color(), user.getId())) {
            throw new ResourceAlreadyExistsException("Color " + obj.color() + " already exists");
        }

        repository.save(category);
        return CategoryDTOMapper.toResponseDTO(category);
    }

    public CategoryResponseDTO update(Long id, PutCategoryDTO obj) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user =  userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }
        Category category = repository.findByIdAndUser_Id(id, user.getId()).orElseThrow(ResourceNotFoundException::new);
        updateData(category, user, obj);
        repository.save(category);
        return CategoryDTOMapper.toResponseDTO(category);
    }

    private void updateData(Category category, User user, PutCategoryDTO obj) {

        if (repository.existsByNameAndUserIdAndIdNot(obj.name(), user.getId(), category.getId())) {
            throw new ResourceAlreadyExistsException("Category already exists");
        }

        if (repository.existsByColorAndUserIdAndIdNot(obj.color(), user.getId(), category.getId())) {
            throw new ResourceAlreadyExistsException("Color " + obj.color() + " already exists");
        }

        category.setName(obj.name());
        category.setColor(obj.color());
    }

    public void delete(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user =  userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }
        if (!repository.existsByIdAndUserId(id, user.getId())) {
            throw new ResourceNotFoundException();
        }

        if (transactionRepository.existsPaidInstallmentByCategoryId(id)) {
            throw new BusinessException("This category has associated transactions", HttpStatus.BAD_REQUEST);
        } else {
            repository.deleteById(id);
        };
    }


}
