package com.ureca.uhyu.domain.mymap.repository;

import com.ureca.uhyu.domain.mymap.entity.MyMap;
import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MyMapRepository extends JpaRepository<MyMap, Long> {
    List<MyMap> findByMyMapList(MyMapList myMapList);

    List<MyMap> findByMyMapListIn(Collection<MyMapList> myMapLists);
}
