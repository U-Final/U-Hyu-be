package com.ureca.uhyu.domain.store.repository;

import com.ureca.uhyu.domain.brand.entity.Brand;
import com.ureca.uhyu.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findAllByBrand(Brand brand);
}
