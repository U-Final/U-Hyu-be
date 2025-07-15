package com.ureca.uhyu.domain.brand.repository;

import com.ureca.uhyu.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>
        , BrandRepositoryCustom{

    List<Brand> findByBrandNameIn(List<String> brandNames);

}
