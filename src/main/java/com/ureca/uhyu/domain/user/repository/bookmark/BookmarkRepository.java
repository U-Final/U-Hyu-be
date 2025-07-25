package com.ureca.uhyu.domain.user.repository.bookmark;

import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.repository.BookmarkRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository
        extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    List<Bookmark> findByBookmarkList(BookmarkList bookmarkList);
    Optional<Bookmark> findByBookmarkListAndStore(BookmarkList bookmarkList, Store store);
    boolean existsByBookmarkListUserAndStore(User user, Store store);
    int countByStore(Store store);
}
