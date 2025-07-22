package com.ureca.uhyu.domain.brand.repository;

import com.ureca.uhyu.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long>
        , BrandRepositoryCustom{

    List<Brand> findByBrandNameIn(List<String> brandNames);

    boolean existsByBrandName(String brandName);

    Optional<Brand> findByIdAndDeletedFalse(Long id);
}
