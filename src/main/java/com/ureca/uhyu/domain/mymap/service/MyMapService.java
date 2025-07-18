package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.mymap.dto.response.MyMapRes;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.mymap.repository.MyMapListRepository;
import com.ureca.uhyu.domain.user.entity.User;
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
}
