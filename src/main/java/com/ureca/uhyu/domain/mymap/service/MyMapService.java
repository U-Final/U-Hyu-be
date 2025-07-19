package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.mymap.dto.request.CreateMyMapListReq;
import com.ureca.uhyu.domain.mymap.dto.response.MyMapListRes;
import com.ureca.uhyu.domain.mymap.dto.response.UpdateMyMapListRes;
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

    public List<MyMapListRes> findMyMapList(User user) {
        List<MyMapList> myMapLists =  myMapListRepository.findByUser(user);
        return myMapLists.stream()
                .map(MyMapListRes::from)
                .toList();
    }

    @Transactional
    public Long createMyMapList(User user,  CreateMyMapListReq createMyMapListReq) {
        MyMapList myMapList = MyMapList.builder()
                .title(createMyMapListReq.title())
                .markerColor(createMyMapListReq.markerColor())
                .uuid(createMyMapListReq.uuid())
                .user(user)
                .build();
        MyMapList savedMyMapList = myMapListRepository.save(myMapList);

        return savedMyMapList.getId();
    }

    public UpdateMyMapListRes updateMyMapList(User user, UpdateMyMapListRes updateMyMapListRes) {
        return null;
    }
}
