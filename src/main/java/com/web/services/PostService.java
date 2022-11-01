package com.web.services;

import com.web.model.Account;
import com.web.model.File;
import com.web.model.Post;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.stream.Stream;

@Service
public interface PostService {
    // Hàm lưu user
    @Transactional(rollbackOn = Exception.class)
    Post savePost (Account acount , String status,String described ) throws IOException;
//    File store(List<MultipartFile> file) throws IOException;

    Stream<File> getAllFiles();
}
