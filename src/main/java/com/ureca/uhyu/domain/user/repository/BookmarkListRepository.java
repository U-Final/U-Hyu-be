package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkListRepository extends JpaRepository<BookmarkList, Long> {
    BookmarkList findByUser(User user);
}
