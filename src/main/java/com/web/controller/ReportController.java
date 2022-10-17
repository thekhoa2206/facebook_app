package com.web.controller;

import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dao.jpa.*;
import com.web.dto.BaseResponse;
import com.web.dto.Post.DataPostResponse;
import com.web.dto.Post.ListPostResponse;
import com.web.dto.Post.PostResponse;
import com.web.dto.exception.FormValidateException;
import com.web.dto.exception.NotFoundException;
import com.web.dto.like.LikeReponse;
import com.web.model.File;
import com.web.model.Likes;
import com.web.model.Report;
import com.web.model.TypeReport;
import com.web.repositories.LikesRepo;
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

    public ReportController(PostDao postDao, ReportDao reportDao, TypeReportDao typeReportDao, JwtProvider tokenProvider, AccountDao accountDao, ReportRepo reportRepo, TypeReportRepo typeReportRepo, LikesRepo likesRepo, LikeDao likeDao, ModelMapper mapper, FileDao fileDao) {
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
    }

    //api report
    @PostMapping("/report")
    public BaseResponse report(@Valid @RequestParam(required = false) String token, @Valid @RequestParam(required = false) Integer id,
                               @Valid @RequestParam(required = false) String subject, @Valid @RequestParam(required = false) String details) {
        val account = checkJwt(token);
        if (id == null) {
            throw new FormValidateException("report.id", "Mã Id không được để trống!");
        }
        if (subject == null) {
            throw new FormValidateException("report.id", "Subject không được để trống!");
        }
        if (details == null) {
            throw new FormValidateException("report.id", "Details không được để trống!");
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
        val post = postDao.findPostById(id);
        if (post == null)
            throw new NotFoundException("Không tìm thấy bài viết!");
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
        response.setCode(HttpStatus.OK);
        response.setMessage("Bài viết đang được xem xét!");
        return response;
    }

    //api like
    @PostMapping("/like")
    public BaseResponse like(@Valid @RequestParam(required = false) String token, @Valid @RequestParam(required = false) Integer id) {
        val account = checkJwt(token);
        if (id == null) {
            throw new FormValidateException("like.id", "Mã Id không được để trống!");
        }
        val response = new BaseResponse();
        val post = postDao.findPostById(id);
        if (post == null)
            throw new NotFoundException("Không tìm thấy bài viết!");
        if (post != null) {
            val like = likeDao.findLikeByPostIdAndAccountId(post.getId(), account.getId());
            if (like != null) {
                likesRepo.delete(like);
                response.setMessage("Bài viết được giảm thêm một like bởi " + (account.getName() != null ? account.getName() : "người dùng"));
            } else {
                val likes = new Likes();
                likes.setAccountId(account.getId());
                likes.setPostId(post.getId());
                likes.setCreatedOn();
                likes.setCreatedBy(account.getId());
                likesRepo.save(likes);
                response.setMessage("Bài viết được tăng thêm một like bởi " + (account.getName() != null ? account.getName() : "người dùng"));
            }
            val likeRes = new LikeReponse();
            val countLike = likeDao.countLikeByPostId(post.getId());
            likeRes.setLike(countLike);
            val data = new ArrayList<>();
            data.add(likeRes);
            response.setData(data);
        }
        response.setCode(HttpStatus.OK);
        return response;
    }

    // api get_list_post
    @PostMapping("/get_list_post")
    public BaseResponse getListPost(@Valid @RequestParam(required = false) String token, @Valid @RequestParam(required = false) Integer user_id,
                                    @Valid @RequestParam(required = false) Integer index, @Valid @RequestParam(required = false) Integer count,
                                    @Valid @RequestParam(required = false) Integer last_id, @Valid @RequestParam(required = false) Integer in_campaign,
                                    @Valid @RequestParam(required = false) Integer campaign_id) {
        val account = checkJwt(token);
        if(count == null)
            throw new FormValidateException("get_list_post.count", "Trường Count không được để trống!");
        if(index == null)
            throw new FormValidateException("get_list_post.index", "Trường Index không được để trống!");
        List<DataPostResponse> listData = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        val dataRes = new DataPostResponse();
        val listPostResponse = new ListPostResponse();

        val posts = postDao.findPostByAll(count, index, campaign_id, in_campaign);
        List<PostResponse> postResponses = new ArrayList<>();
        int lastId = 0;
        if(posts != null){
            for (val post : posts) {
                val postReponse = mapper.map(post, PostResponse.class);
                postReponse.setDescribed(post.getContent());
                postReponse.setCreated(post.getCreatedOn());
                val countLike = likeDao.countLikeByPostId(post.getId());
                postReponse.setLike(countLike);
                val countComment = likeDao.countCommentByPostId(post.getId());
                postReponse.setComment(countComment);
                if(post.getAccountId() != 0){
                    val accountPost = accountDao.findAccountById(post.getAccountId());
                    if(accountPost != null){
                        PostResponse.AuthorReponse authorReponse = new PostResponse.AuthorReponse();
                        authorReponse.setAvatar(accountPost.getAvatar());
                        authorReponse.setId(accountPost.getId());
                        authorReponse.setPhone(accountPost.getPhoneNumber());
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
            lastId = postResponses.get(postResponses.size() - 1).getId();
        }
        listPostResponse.setLastId(lastId);
        //listPostResponse.setNewItems();
        dataRes.setData(listPostResponse);
        if(campaign_id != null && in_campaign != null){
            dataRes.setCampaignId(campaign_id);
            dataRes.setInCampaign(in_campaign);
        }
        data.add(dataRes);
        val response = new BaseResponse();
        response.setData(data);
        response.setCode(HttpStatus.OK);
        response.setMessage("Lấy danh sách bài viết thành công");
        return response;
    }

    // api check_new_item
}
