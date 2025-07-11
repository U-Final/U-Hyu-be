package com.ureca.uhyu.domain.user.repository;

import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarksList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarksListRepository extends JpaRepository<BookmarksList, Long> {
    List<BookmarksList> findByBookmark(Bookmark bookmark);
}
