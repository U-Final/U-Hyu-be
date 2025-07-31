package com.ureca.uhyu.domain.user.repository.barcode;

import com.ureca.uhyu.domain.user.entity.Barcode;
import com.ureca.uhyu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {
    Optional<Barcode> findByUser(User user);
}
