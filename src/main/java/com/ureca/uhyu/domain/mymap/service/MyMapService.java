package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.map.dto.response.MapRes;
import com.ureca.uhyu.domain.mymap.dto.request.CreateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.*;
import com.ureca.uhyu.domain.mymap.dto.request.UpdateMyMapListReq;
import com.ureca.uhyu.domain.mymap.entity.MyMap;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.enums.MarkerColor;
import com.ureca.uhyu.domain.mymap.repository.MyMapListRepository;
import com.ureca.uhyu.domain.mymap.repository.MyMapRepository;
import com.ureca.uhyu.domain.store.entity.Store;
import com.ureca.uhyu.domain.store.repository.StoreRepository;
import com.ureca.uhyu.domain.user.entity.Bookmark;
import com.ureca.uhyu.domain.user.entity.BookmarkList;
import com.ureca.uhyu.domain.user.entity.User;
import com.ureca.uhyu.domain.user.repository.BookmarkListRepository;
import com.ureca.uhyu.domain.user.repository.BookmarkRepository;
import com.ureca.uhyu.global.exception.GlobalException;
import com.ureca.uhyu.global.response.ResultCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMapService {

    private final MyMapListRepository myMapListRepository;
    private final MyMapRepository myMapRepository;
    private final StoreRepository storeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkListRepository bookmarkListRepository;

    public List<MyMapListRes> findMyMapList(User user) {
        List<MyMapList> myMapLists =  myMapListRepository.findByUser(user);
        return myMapLists.stream()
                .map(MyMapListRes::from)
                .toList();
    }

    @Transactional
    public CreateMyMapListRes createMyMapList(User user, CreateMyMapListReq createMyMapListReq) {
        MyMapList myMapList = MyMapList.builder()
                .title(createMyMapListReq.title())
                .markerColor(createMyMapListReq.markerColor())
                .uuid(createMyMapListReq.uuid())
                .user(user)
                .build();
        MyMapList savedMyMapList = myMapListRepository.save(myMapList);

        return new CreateMyMapListRes(savedMyMapList.getId());
    }

    @Transactional
    public UpdateMyMapListRes updateMyMapList(User user, UpdateMyMapListReq request) {
        MyMapList myMapList = myMapListRepository.findById(request.myMapListId())
                .orElseThrow(() -> new GlobalException(ResultCode.MY_MAP_LIST_NOT_FOUND));

        if (!myMapList.getUser().getId().equals(user.getId())) {
            throw new GlobalException(ResultCode.FORBIDDEN);
        }

        String title = (request.title() != null)?
                request.title():myMapList.getTitle();

        MarkerColor markerColor = (request.markerColor() != null)?
                request.markerColor():myMapList.getMarkerColor();

        myMapList.updateMyMapList(title, markerColor);
        MyMapList savedMyMapList = myMapListRepository.save(myMapList);
        return UpdateMyMapListRes.from(savedMyMapList);
    }

    @Transactional
    public void deleteMyMapList(User user, Long myMapListId) {
        MyMapList myMapList = myMapListRepository.findById(myMapListId)
                .orElseThrow(() -> new GlobalException(ResultCode.MY_MAP_LIST_NOT_FOUND));

        if (!myMapList.getUser().getId().equals(user.getId())) {
            throw new GlobalException(ResultCode.FORBIDDEN);
        }

        myMapListRepository.delete(myMapList);
    }

    public MyMapRes findMyMap(User user, String uuid) {
        MyMapList myMapList = myMapListRepository.findByUuid(uuid).orElseThrow(() -> new GlobalException(ResultCode.MY_MAP_LIST_NOT_FOUND));
        List<MyMap> myMaps = myMapRepository.findByMyMapList(myMapList);

        List<MapRes> storeList = myMaps.stream()
                .map(myMap -> MapRes.from(myMap.getStore()))
                .toList();

        boolean isMine = myMapList.getUser().getId().equals(user.getId());

        return MyMapRes.from(myMapList, storeList, isMine);
    }

    public BookmarkedMyMapRes findMyMapListWithIsBookmarked(User user, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new GlobalException(ResultCode.NOT_FOUND_STORE));

        //MyMap 안에 store가 있는지 검증
        List<MyMapList> myMapLists = myMapListRepository.findByUser(user);

        List<BookmarkedMyMapListRes> myMapListResponses = myMapLists.stream()
                .map(myMapList -> {
                    List<MyMap> myMaps = myMapRepository.findByMyMapList(myMapList); // ← 핵심 수정 부분
                    boolean isMapped = myMaps.stream()
                            .anyMatch(myMap -> myMap.getStore().getId().equals(storeId));
                    return BookmarkedMyMapListRes.from(myMapList, isMapped);
                })
                .toList();

        //Bookmark 안에 store가 있는지 검증
        BookmarkList bookmarkList = bookmarkListRepository.findByUser(user)
                .orElseThrow(() -> new GlobalException(ResultCode.BOOKMARK_LIST_NOT_FOUND));

        List<Bookmark> bookmarks = bookmarkRepository.findByBookmarkList(bookmarkList);

        boolean isBookmarked = bookmarks.stream()
                .anyMatch(bookmark -> bookmark.getStore().getId().equals(storeId));

        return BookmarkedMyMapRes.from(store, myMapListResponses, isBookmarked);
    }
}
