package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.mymap.dto.request.MyMapReq;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.repository.MyMapListRepository;
import com.ureca.uhyu.domain.user.entity.User;
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

    public List<MyMapRes> findMyMapList(User user) {
        List<MyMapList> myMapLists =  myMapListRepository.findByUser(user);
        return myMapLists.stream()
                .map(MyMapRes::from)
                .toList();
    }

    @Transactional
    public Long createMyMapList(User user,  MyMapReq myMapReq) {
        MyMapList myMapList = MyMapList.builder()
                .title(myMapReq.title())
                .markerColor(myMapReq.markerColor())
                .uuid(myMapReq.uuid())
                .user(user)
                .build();
        MyMapList savedMyMapList = myMapListRepository.save(myMapList);

        return savedMyMapList.getId();
    }
}
