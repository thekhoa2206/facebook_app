package com.web.controller;

import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dao.jpa.*;
import com.web.dto.BaseResponse;
import com.web.dto.Post.DataPostResponse;
import com.web.dto.Post.ListPostResponse;
import com.web.dto.Post.NewItems;
import com.web.dto.Post.PostResponse;
import com.web.dto.exception.Exception;
import com.web.dto.exception.FormValidateException;
import com.web.dto.exception.NotFoundException;
import com.web.dto.like.LikeReponse;
import com.web.model.*;
import com.web.repositories.LikesRepo;
import com.web.repositories.PostRepo;
import com.web.repositories.ReportRepo;
import com.web.repositories.TypeReportRepo;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReportController extends BaseController {
    private final PostDao postDao;
    private final ReportDao reportDao;
    private final TypeReportDao typeReportDao;
    private final JwtProvider tokenProvider;
    private final AccountDao accountDao;
    private final ReportRepo reportRepo;
    private final TypeReportRepo typeReportRepo;
    private final LikesRepo likesRepo;
    private final LikeDao likeDao;
    private final ModelMapper mapper;
    private final FileDao fileDao;
    private final PostRepo postRepo;

    public ReportController(PostDao postDao, ReportDao reportDao, TypeReportDao typeReportDao, JwtProvider tokenProvider, AccountDao accountDao, ReportRepo reportRepo, TypeReportRepo typeReportRepo, LikesRepo likesRepo, LikeDao likeDao, ModelMapper mapper, FileDao fileDao, PostRepo postRepo) {
        super(tokenProvider, accountDao);
        this.postDao = postDao;
        this.reportDao = reportDao;
        this.typeReportDao = typeReportDao;
        this.tokenProvider = tokenProvider;
        this.accountDao = accountDao;
        this.reportRepo = reportRepo;
        this.typeReportRepo = typeReportRepo;
        this.likesRepo = likesRepo;
        this.likeDao = likeDao;
        this.mapper = mapper;
        this.fileDao = fileDao;
        this.postRepo = postRepo;
    }

    //api report
    @PostMapping("/report")
    public BaseResponse report(@Valid @RequestParam(required = false) String token, @Valid @RequestParam(required = false) String id,
                               @Valid @RequestParam(required = false) String subject, @Valid @RequestParam(required = false) String details) {
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (subject == null) {
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (details == null) {
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        Integer idI = null;
        try {
            idI = Integer.parseInt(id);
        }catch (Exception e){
            throw new Exception("1003","Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }
        if(idI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        TypeReport typeReport = null;
        if (subject != null) {
            typeReport = typeReportDao.findTypeReportBySubject(subject);
            if (typeReport == null) {
                val typeReportReq = new TypeReport();
                typeReportReq.setName(subject);
                typeReportReq.setCreatedOn();
                typeReportReq.setCreatedBy(account.getId());
                typeReport = typeReportRepo.save(typeReportReq);
            }
        }
        val post = postDao.findPostById(idI);
        if (post == null)
            throw new Exception("9992","Post is not existed", "Bài viết không tồn tại");
        if (post != null) {
            val report = new Report();
            report.setAccountId(account.getId());
            report.setDetails(details);
            report.setPostId(post.getId());
            report.setCreatedOn();
            report.setCreatedBy(account.getId());
            report.setTypeReportId(typeReport.getId());
            reportRepo.save(report);
        }
        val response = new BaseResponse();
        response.setCode("1000");
        response.setMessage("OK");
        return response;
    }

    //api like
    @PostMapping("/like")
    public BaseResponse like(@Valid @RequestParam(required = false) String token, @Valid @RequestParam(required = false) String id) {
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        Integer idI = null;
        try {
            idI = Integer.parseInt(id);
        }catch (Exception e){
            throw new Exception("1003","Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }
        if(idI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        val response = new BaseResponse();
        val post = postDao.findPostById(idI);
        if (post == null)
            throw new Exception("9992","Post is not existed", "Bài viết không tồn tại");
        if (post != null) {
            val like = likeDao.findLikeByPostIdAndAccountId(post.getId(), account.getId());
            if (like != null) {
                likesRepo.delete(like);
            } else {
                val likes = new Likes();
                likes.setAccountId(account.getId());
                likes.setPostId(post.getId());
                likes.setCreatedOn();
                likes.setCreatedBy(account.getId());
                likesRepo.save(likes);
            }
            val likeRes = new LikeReponse();
            val countLike = likeDao.countLikeByPostId(post.getId());
            likeRes.setLike(String.valueOf(countLike));
            val data = new ArrayList<>();
            data.add(likeRes);
            response.setData(data);
        }
        response.setCode("1000");
        response.setMessage("OK");
        return response;
    }

    // api get_list_post
    @PostMapping("/get_list_post")
    public BaseResponse getListPost(@Valid @RequestParam(required = false) String token, @Valid @RequestParam(required = false) String user_id,
                                    @Valid @RequestParam(required = false) String index, @Valid @RequestParam(required = false) String count,
                                    @Valid @RequestParam(required = false) String last_id, @Valid @RequestParam(required = false) String in_campaign,
                                    @Valid @RequestParam(required = false) String campaign_id) {
        val account = checkJwt(token);
        if(count == null)
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");
        if(index == null)
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");

        Integer countI = null;
        Integer indexI = null;
        Integer user_idI = null;
        Integer last_idI = null;
        Integer in_campaignI = null;
        Integer campaign_idI = null;
        try {
            countI = Integer.parseInt(count);
            indexI = Integer.parseInt(index);
            if(user_id != null) user_idI = Integer.parseInt(user_id);
            if(last_id != null) last_idI = Integer.parseInt(last_id);
            if(in_campaign != null){
                in_campaignI = Integer.parseInt(in_campaign);
            }
            if(campaign_id != null)
                campaign_idI = Integer.parseInt(campaign_id);
        }catch (Exception e){
            throw new Exception("1003","Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }
        if(countI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(indexI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(user_id != null && user_idI < 0) throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        if(last_id != null && last_idI < 0) throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        if(in_campaign != null && in_campaignI < 0) throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        if(campaign_id != null && campaign_idI < 0) throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");

        List<DataPostResponse> listData = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        val dataRes = new DataPostResponse();
        val listPostResponse = new ListPostResponse();
        val accountCheck = accountDao.findAccountById(user_idI);
        List<Post> posts = null;
        if(accountCheck == null){
             posts = postDao.findPostByAll(countI, indexI, campaign_idI, in_campaignI, null);
        }else{
             posts = postDao.findPostByAll(countI, indexI, campaign_idI, in_campaignI, user_idI);
        }

        List<Post> postsLastId = null;
        if(last_idI != null && posts != null){
            Integer finalLast_idI = last_idI;
            postsLastId = posts.stream().filter(i -> i.getId() > finalLast_idI).collect(Collectors.toList());
        }
        List<PostResponse> postResponses = new ArrayList<>();
        int lastId = 0;
        if(posts != null){
            for (val post : postsLastId) {
                val postReponse = mapper.map(post, PostResponse.class);
                postReponse.setDescribed(post.getContent());
                postReponse.setCreated(post.getCreatedOn());
                val countLike = likeDao.countLikeByPostId(post.getId());
                postReponse.setLike(String.valueOf(countLike));
                val countComment = likeDao.countCommentByPostId(post.getId());
                postReponse.setComment(String.valueOf(countComment));
                if(post.getAccountId() != 0){
                    val accountPost = accountDao.findAccountById(post.getAccountId());
                    if(accountPost != null){
                        PostResponse.AuthorReponse authorReponse = new PostResponse.AuthorReponse();
                        authorReponse.setAvatar(accountPost.getAvatar());
                        authorReponse.setId(String.valueOf(accountPost.getId()));
                        authorReponse.setPhone(accountPost.getPhoneNumber());
                        authorReponse.setOnline("1");
                        postReponse.setName(accountPost.getName());
                        postReponse.setAuthor(authorReponse);
                    }
                    val files = fileDao.findPostByAll(post.getId());
                    if(files != null){
                        val imagesUrl = files.stream().filter(i -> i.getUrl() != null).map(File::getUrl).collect(Collectors.toList());
                        postReponse.setImage(imagesUrl);
                    }
                }
                postResponses.add(postReponse);
            }
        }
        listPostResponse.setPosts(postResponses);
        if(postResponses != null && postResponses.size() > 0){
            lastId = Integer.parseInt(postResponses.get(postResponses.size() - 1).getId());
        }
        listPostResponse.setLastId(String.valueOf(lastId));
        listPostResponse.setNewItems(String.valueOf(postsLastId.size()));
        dataRes.setData(listPostResponse);
        if(campaign_id != null && in_campaign != null){
            dataRes.setCampaignId(String.valueOf(campaign_idI));
            dataRes.setInCampaign(String.valueOf(in_campaignI));
        }
        data.add(dataRes);
        val response = new BaseResponse();
        response.setData(data);
        response.setCode("1000");
        response.setMessage("OK");
        return response;
    }

    // api check_new_item
    @PostMapping("/check_new_item")
    public BaseResponse checkNewItem(@Valid @RequestParam(required = false) String last_id, @Valid @RequestParam(required = false) String category_id){
        if(last_id == null)
            throw new Exception("1002","Parameter is not enought", "Số lượng parameter không đầy đủ");


        Integer category_idI = null;
        Integer last_idI = null;
        try {
            last_idI = Integer.parseInt(last_id);
            if(category_id != null) category_idI  = Integer.parseInt(category_id);
        }catch (Exception e){
            throw new Exception("1003","Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }

        if(last_idI < 0) throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        val response = new BaseResponse();
        val posts  = postDao.findPostByAllByLastId(category_idI);
        if(posts != null){
            Integer finalLast_idI = last_idI;
            val postsResponse = posts.stream().filter(i -> i.getId() > finalLast_idI).collect(Collectors.toList());
            List<Object> data = new ArrayList<>();
            val newItems = new NewItems();
            newItems.setNewItem(String.valueOf(postsResponse.size()));
            data.add(newItems);
            response.setData(data);
        }

        response.setCode("1000");
        response.setMessage("OK");
        return response;
    }
}
