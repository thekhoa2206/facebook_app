package com.web.services.impl;

import com.web.dao.jpa.FileDao;
import com.web.dao.jpa.PostDao;
import com.web.model.Account;
import com.web.model.File;
import com.web.model.Post;
import com.web.repositories.FileRepo;
import com.web.repositories.PostRepo;
import com.web.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Stream;

@Service
public class PostServiceImpl implements PostService {
@Autowired
PostRepo postRepo;
    @Autowired
    PostDao postDao;
    @Autowired
     FileDao fileDao;
    @Autowired
  FileRepo fileRepo;

    @Override
    public Post savePost(Account acount,String status,String described ) throws IOException {

        try{
        Post posts = new Post();
        posts.setCreatedBy(acount.getId());
        posts.setCreatedOn();
        posts.setContent(described);
        posts.setAccountId(acount.getId());
        return postRepo.save(posts);
        }
        catch ( Exception e){
            System.out.printf("lỗi nè",e.getMessage());
        };
        return null;
    };



    @Override
    public Stream<File> getAllFiles() {
        return fileRepo.findAll().stream();
    }

    }

