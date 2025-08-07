package com.ureca.uhyu.domain.brand.repository;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.brand.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long>
        , BrandRepositoryCustom{

    boolean existsByBrandName(String brandName);

    boolean existsByBrandNameAndIdNot(String brandName, Long id);

    List<Brand> findByCategory(Category category);

    List<Brand> findByIdIn(List<Long> ids);
}
