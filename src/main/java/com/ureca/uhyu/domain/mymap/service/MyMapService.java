package com.ureca.uhyu.domain.mymap.service;

import com.ureca.uhyu.domain.mymap.dto.response.MyMapListRes;
import com.ureca.uhyu.domain.mymap.repository.MyMapRepository;
import com.ureca.uhyu.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMapService {

    private final MyMapRepository myMapRepository;

    public MyMapListRes findMyMapList(User user) {
    }
}
