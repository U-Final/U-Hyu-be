package com.ureca.uhyu.domain.user.service;

import com.ureca.uhyu.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface BarcodeService {
    String upload(User user, MultipartFile image);
    String update(User user, MultipartFile image);
    String get(User user);
}
