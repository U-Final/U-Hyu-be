package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkListRepository extends JpaRepository<BookmarkList, Long> {
    Optional<BookmarkList> findByUser(User user);
}
