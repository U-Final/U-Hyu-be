package com.ureca.uhyu.domain.mymap.repository;

import com.ureca.uhyu.domain.mymap.entity.MyMapList;
import com.ureca.uhyu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyMapListRepository extends JpaRepository<MyMapList, Long> {
   List<MyMapList> findByUser(User user);

   MyMapList findByUuid(String uuid);
}
