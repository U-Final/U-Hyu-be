package com.ureca.uhyu.domain.store.repository;

import com.ureca.uhyu.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
