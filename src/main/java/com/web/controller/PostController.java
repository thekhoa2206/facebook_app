package com.web.controller;

import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dao.jpa.*;
import com.web.dto.BaseResponse;
import com.web.dto.Post.CommentReponse;
import com.web.dto.Post.PostDataRepone;
import com.web.dto.exception.FormValidateException;
import com.web.dto.exception.NotFoundException;
import com.web.dto.exception.UnauthorizedException;
import com.web.model.Account;
import com.web.model.Comment;
import com.web.model.File;
import com.web.model.Post;
import com.web.repositories.*;
import com.web.services.PostService;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController extends BaseController {

    private final PostDao postDao;
    private final CommentRepo commentRepo;
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
    private final FileRepo fileRepo;
    private final PostService postService;
    private final CommentDao commentDao;


    public PostController(CommentRepo commentRepo, CommentDao commentDao, FileRepo fileRepo, PostService postService, PostDao postDao, ReportDao reportDao, TypeReportDao typeReportDao, JwtProvider tokenProvider, AccountDao accountDao, ReportRepo reportRepo, TypeReportRepo typeReportRepo, LikesRepo likesRepo, LikeDao likeDao, ModelMapper mapper, FileDao fileDao, PostRepo postRepo) {
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
        this.postService = postService;
        this.fileRepo = fileRepo;
        this.commentDao = commentDao;
        this.commentRepo = commentRepo;
    }

    //add post
    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse create(@Valid @RequestParam(required = false) String token,
                               @Valid @RequestParam(required = false) List<MultipartFile> video,
                               @Valid @RequestParam(required = false) List<MultipartFile> images,
                               @Valid @RequestParam(required = false) String described,
                               @Valid @RequestParam(required = false) String status) throws IOException {
        // xử lý upload file khi người dùng nhấn nút thực hiện
        int MAX_FILE_SIZE = 1024 * 1024 * 15; // 40MB
        val checkHaveImages = images.size() > 1 ||(images.size() ==1 && !images.get(0).getOriginalFilename().isEmpty());
        val checkHaveVideo = video.size() > 1 ||(video.size() ==1 && !video.get(0).getOriginalFilename().isEmpty());
        if (checkHaveImages && checkHaveVideo) {
            throw new FormValidateException("upload", "không thể cùng thêm ảnh và video");
        }
        val account = checkJwt(token);
        Post postdata = null;
        postdata = postService.savePost(account, described, status);
        String message = "";
        if (!video.isEmpty()) {
            try {
                for (val file : video) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        throw new FormValidateException("video", "File video quá kích cỡ quy định");
                    }
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File videos = new File();
                    videos.setUrl(file.getOriginalFilename());
                    videos.setCreatedOn();
                    videos.setPost_id(postdata.getId());
                    fileRepo.save(videos);
                }
                message = "Tạo bài viết thành công ";

            } catch (Exception e) {
                message = "có lỗi phần upvideo";
            }
        } else {

            try {
                for (val file : images) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        throw new FormValidateException("ảnh", "File ảnh quá kích cỡ quy định");
                    }
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File image = new File();
                    image.setUrl(file.getOriginalFilename());
                    image.setCreatedOn();
                    image.setPost_id(postdata.getId());
                    fileRepo.save(image);
                }
                message = "Tạo bài viết thành công ";

            } catch (Exception e) {
                message = "có lỗi phần up ảnh!";
            }
        }
        val data = new ArrayList<>();
        val dataResponse = new PostDataRepone();
        dataResponse.setId(postdata.getId());
        dataResponse.setUrl(account.getName() + "/post/" + postdata.getId());
        data.add(dataResponse);
        val response = new BaseResponse();
        response.setCode(HttpStatus.OK);
        response.setMessage(message);
        response.setData(data);
        return response;

    }

    //edit post
    @PostMapping("/edit-post")
    public BaseResponse edit(@Valid @RequestParam(required = false) String token,
                             @Valid @RequestParam(required = false) Integer id,
                             @Valid @RequestParam(required = false) List<MultipartFile> video,
                             @Valid @RequestParam(required = false) List<MultipartFile> images,
                             @Valid @RequestParam(required = false) List<Integer> image_del,
                             @Valid @RequestParam(required = false) List<Integer> image_sort,
                             @Valid @RequestParam(required = false) String described,
                             @Valid @RequestParam(required = false) String status,
                             @Valid @RequestParam(required = false) List<MultipartFile> thumb,
                             @Valid @RequestParam(required = false) Boolean auto_accept,
                             @Valid @RequestParam(required = false) Boolean auto_block
    ) throws IOException {
        int MAX_FILE_SIZE = 1024 * 1024 * 15; // 40MB
        val account = checkJwt(token);
        if (id == null) {
            throw new FormValidateException("erros", "id bài viết khng được để trống");
        }
        val post = postDao.findPostById(id);
        if (post == null) {
            throw new FormValidateException("erros", "không thể tìm thấy bài viết");
        }
        if (account.getId() != post.getAccountId()) {
            throw new FormValidateException("erros", "Không thể sửa bài viết của người khác!");
        }
        if (!video.isEmpty() && !images.isEmpty()) {
            throw new FormValidateException("erros", "không thể cùng thêm ảnh và video");
        }
        String message = "";
        if (!video.isEmpty()) {
            try {
                for (val file : video) {
                    if (file.getSize() > MAX_FILE_SIZE) {

                        throw new FormValidateException("video", "File video quá kích cỡ quy định");
                    }

                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File videos = new File();
                    videos.setUrl(file.getOriginalFilename());
                    videos.setModifiedOn();
                    videos.setPost_id(id);
                    fileRepo.save(videos);
                }
                message = "Tạo bài viết thành công ";

            } catch (Exception e) {
                message = "có lỗi phần upvideo";
            }
        }
        if (images != null && !images.isEmpty()) {
            try {
                for (val file : images) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        throw new FormValidateException("ảnh", "File ảnh quá kích cỡ quy định");
                    }
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File image = new File();
                    image.setUrl(file.getOriginalFilename());
                    image.setCreatedOn();
                    image.setPost_id(id);
                    fileRepo.save(image);
                }
                message = "Sửa bài viết thành công ";
            } catch (Exception e) {
                message = "có lỗi phần up ảnh!";

            }
        }
        ;
        // Xóa ảnh trong post
        if (image_del != null && !image_del.isEmpty()) {
            val File = fileDao.findPostById(id, image_del);
            if (File == null) {
                throw new FormValidateException("erros", "file ảnh không có trong bài viết");
            }
            if (File != null) {

                fileRepo.deleteById(File.getId());
            }
        }
        post.setContent(described);
        post.setAuto_accept(true);
        postRepo.save(post);
        val response = new BaseResponse();
        response.setCode(HttpStatus.OK);
        response.setMessage(message);
        return response;

    }

    //delete post
    @PostMapping("/delete-post")
    public BaseResponse delete(@Valid @RequestParam(required = false) String token,
                               @Valid @RequestParam(required = false) Integer id
    ) throws IOException {
        val account = checkJwt(token);
        long Id = id.longValue();
        if (id == null) {
            throw new FormValidateException("erros", "id bài viết khng được để trống");
        }
        val post = postDao.findPostById(id);
        if (post == null) {
            throw new FormValidateException("erros", "không thể tìm thấy bài viết");
        }
        if (account.getId() != post.getAccountId()) {
            throw new FormValidateException("erros", "Không thể xóa bài viết của người khác!");
        }
        postRepo.deleteById(id);

        val response = new BaseResponse();
        response.setCode(HttpStatus.OK);
        response.setMessage("xóa bài viết thành công");
        return response;
    }

    //get comment
    @PostMapping("/get-comment")
    public BaseResponse comment(@Valid @RequestParam(required = false) String token,
                                @Valid @RequestParam(required = false) Integer id,
                                @Valid @RequestParam(required = false) Integer index,
                                @Valid @RequestParam(required = false) Integer count

    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new FormValidateException("erros", "id bài viết khng được để trống");
        }
        if (index == null) {
            throw new FormValidateException("erros", "index comment không được để trống");
        }
        if (count == null) throw new FormValidateException("erros", "index comment không được để trống");
        val comments = commentDao.findCommentAll(id, index, count);
        if (comments == null) {
            throw new FormValidateException("erros", "không thể tìm thấy bài viết");
        }
        val data = new ArrayList<>();
        for (val comment : comments) {
            val dataResponse = new CommentReponse();
            dataResponse.setId(comment.getId());
            dataResponse.setComment(comment.getContent());
            dataResponse.setCreated(account.getName());

            val post = postDao.findPostById(id);
            val acounts = accountDao.findAccountById(post.getAccountId());
            List<CommentReponse.PosterReponse> posters = new ArrayList<>();
            CommentReponse.PosterReponse poster = new CommentReponse.PosterReponse();
            poster.setId(acounts.getId());
            poster.setPhone(acounts.getPhoneNumber());
            poster.setAvatar(acounts.getAvatar());
            posters.add(poster);
            dataResponse.setPosters(posters);
            data.add(dataResponse);
        }
        val response = new BaseResponse();
        response.setCode(HttpStatus.OK);
        response.setMessage("Lấy comment thành công");
        response.setData(data);
        return response;
    }

    //set comment
    @PostMapping("/add-comment")
    public BaseResponse addComment(@Valid @RequestParam(required = false) String token,
                                   @Valid @RequestParam(required = false) Integer id,
                                   @Valid @RequestParam(required = false) String comment,
                                   @Valid @RequestParam(required = false) Integer index,
                                   @Valid @RequestParam(required = false) Integer count

    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new FormValidateException("erros", "id bài viết không được để trống");
        }
        if (index == null) {
            throw new FormValidateException("erros", "index comment không được để trống");
        }
        if (count == null) throw new FormValidateException("erros", "index comment không được để trống");

        if (comment == null) throw new FormValidateException("erros", "comment bài viết không được để trống");
        if (id != null) {
            val post = postDao.findPostById(id);
            if (post == null) {
                throw new NotFoundException("Không tìm thấy bài viết!");
            }
        }
        try {
            Comment comments = new Comment();
            comments.setPostId(id);
            comments.setContent(comment);
            comments.setAccountId(account.getId());
            comments.setCreatedOn();
            commentRepo.save(comments);
        } catch (Exception e) {
            System.out.printf("lỗi nè", e.getMessage());
        }
        ;
        val commentDetails = commentDao.findCommentByPostId(id, index, count);
        val data = new ArrayList<>();

        val post = postDao.findPostById(id);


        List<CommentReponse> dataRes = new ArrayList<>();
        for (val commentDetail : commentDetails) {
            List<CommentReponse.PosterReponse> posters = new ArrayList<>();
            val dataResponse = new CommentReponse();
            dataResponse.setId(commentDetail.getId());
            dataResponse.setComment(commentDetail.getContent());
            Account accountComment = null;
            if(commentDetail.getAccountId() != 0){
                accountComment = accountDao.findAccountById(commentDetail.getAccountId());
            }
            CommentReponse.PosterReponse poster = new CommentReponse.PosterReponse();
            poster.setId(commentDetail.getAccountId());
            if(accountComment != null){
                dataResponse.setCreated(accountComment.getName());
                poster.setPhone(accountComment.getPhoneNumber());
                poster.setAvatar(accountComment.getAvatar());
                poster.setId(accountComment.getId());
            }
            posters.add(poster);
            dataResponse.setPosters(posters);
            dataRes.add(dataResponse);
        }
        data.add(dataRes);
        val response = new BaseResponse();
        response.setCode(HttpStatus.OK);
        response.setMessage("add coment thành công");
        response.setData(data);
        return response;
    }


    //set comment
    @PostMapping("/edit-comment")
    public BaseResponse editComment(@Valid @RequestParam(required = false) String token,
                                   @Valid @RequestParam(required = false) Integer id,
                                   @Valid @RequestParam(required = false) String comment,
                                   @Valid @RequestParam(required = false) Integer index,
                                   @Valid @RequestParam(required = false) Integer count,
                                    @Valid @RequestParam(required = false) Integer id_comment

    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new FormValidateException("errors", "id bài viết không được để trống");
        }
        if (index == null) {
            throw new FormValidateException("errors", "index comment không được để trống");
        }
        if (count == null) throw new FormValidateException("errors", "index comment không được để trống");

        if (comment == null) throw new FormValidateException("errors", "comment bài viết không được để trống");
        if (id != null) {
            val post = postDao.findPostById(id);
            if (post == null) {
                throw new NotFoundException("Không tìm thấy bài viết!");
            }
        }
        try {
            Comment comments = commentRepo.findById(id_comment).get();
            if(comments.getAccountId() != account.getId()){
                throw new FormValidateException("errors", "Không được sửa comment của người khác");
            }
            if(comments != null){
                comments.setContent(comment);
                comments.setAccountId(account.getId());
                comments.setCreatedOn();
                commentRepo.save(comments);
            }
        } catch (Exception e) {
            System.out.printf("lỗi nè", e.getMessage());
        }
        ;
        val commentDetails = commentDao.findCommentByPostId(id, index, count);
        val data = new ArrayList<>();

        val post = postDao.findPostById(id);


        List<CommentReponse> dataRes = new ArrayList<>();
        for (val commentDetail : commentDetails) {
            List<CommentReponse.PosterReponse> posters = new ArrayList<>();
            val dataResponse = new CommentReponse();
            dataResponse.setId(commentDetail.getId());
            dataResponse.setComment(commentDetail.getContent());
            Account accountComment = null;
            if(commentDetail.getAccountId() != 0){
                accountComment = accountDao.findAccountById(commentDetail.getAccountId());
            }
            CommentReponse.PosterReponse poster = new CommentReponse.PosterReponse();
            poster.setId(commentDetail.getAccountId());
            if(accountComment != null){
                dataResponse.setCreated(accountComment.getName());
                poster.setPhone(accountComment.getPhoneNumber());
                poster.setAvatar(accountComment.getAvatar());
                poster.setId(accountComment.getId());
            }
            posters.add(poster);
            dataResponse.setPosters(posters);
            dataRes.add(dataResponse);
        }
        data.add(dataRes);
        val response = new BaseResponse();
        response.setCode(HttpStatus.OK);
        response.setMessage("sửa comment thành công");
        response.setData(data);
        return response;
    }

}
