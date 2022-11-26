package com.web.controller;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.web.config.sercurity.jwt.JwtProvider;
import com.web.dao.jpa.*;
import com.web.dto.BaseResponse;
import com.web.dto.Post.CommentReponse;
import com.web.dto.Post.PostDataRepone;
import com.web.dto.Post.PostResponse;
import com.web.dto.Post.PostResponseId;
import com.web.dto.exception.Exception;
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
import java.util.stream.Collectors;

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
                               @Valid @RequestParam(required = false, defaultValue = "") List<MultipartFile> video,
                               @Valid @RequestParam(required = false, defaultValue = "") List<MultipartFile> images,
                               @Valid @RequestParam(required = false) String described,
                               @Valid @RequestParam(required = false) String status) throws IOException {
        // xử lý upload file khi người dùng nhấn nút thực hiện
        int MAX_FILE_SIZE = 1024 * 1024 * 15; // 40MB
        val checkHaveImages = images.size() > 1 || (images.size() == 1 && !images.get(0).getOriginalFilename().isEmpty());
        val checkHaveVideo = video.size() > 1 || (video.size() == 1 && !video.get(0).getOriginalFilename().isEmpty());
        if (checkHaveImages && checkHaveVideo) {
            throw new Exception("1017", "Cannot add the image and video", "không thể cùng thêm ảnh và video");
        }
        val account = checkJwt(token);
        Post postdata = null;
        postdata = postService.savePost(account, described, status);
        String message = "";

        if (checkHaveVideo) {
            try {
                for (val file : video) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        throw new Exception("1006", "File size is too big", "Cỡ file quá kích cỡ quy định");
                    }
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File videos = new File();
                    videos.setUrl(file.getOriginalFilename());
                    videos.setCreatedOn();
                    videos.setPost_id(postdata.getId());
                    fileRepo.save(videos);
                }
                message = "OK";

            } catch (Exception e) {
                throw new Exception("1007", "Upload File Failed", "upload thất bại");
            }
        }
        if (checkHaveImages) {
            try {
                for (val file : images) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        throw new Exception("1006", "File size is too big", "Cỡ file quá kích cỡ quy định");
                    }
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File image = new File();
                    image.setUrl(file.getOriginalFilename());
                    image.setCreatedOn();
                    image.setPost_id(postdata.getId());
                    fileRepo.save(image);
                }
                message = "OK";

            } catch (Exception e) {
                throw new Exception("1007", "Upload File Failed", "upload thất bại");
            }
        }
        message = "OK";
        val data = new ArrayList<>();
        val dataResponse = new PostDataRepone();
        dataResponse.setId(String.valueOf(postdata.getId()));
        dataResponse.setUrl(account.getName() + "/post/" + postdata.getId());
        data.add(dataResponse);
        val response = new BaseResponse();
        response.setCode("1000");
        response.setMessage(message);
        response.setData(data);
        return response;

    }

    //edit post
    @PostMapping("/edit-post")
    public BaseResponse edit(@Valid @RequestParam(required = false) String token,
                             @Valid @RequestParam(required = false) String id,
                             @Valid @RequestParam(required = false) List<MultipartFile> video,
                             @Valid @RequestParam(required = false) List<MultipartFile> images,
                             @Valid @RequestParam(required = false) List<Integer> image_del,
                             @Valid @RequestParam(required = false) List<Integer> image_sort,
                             @Valid @RequestParam(required = false) String described,
                             @Valid @RequestParam(required = false) String status,
                             @Valid @RequestParam(required = false) List<MultipartFile> thumb,
                             @Valid @RequestParam(required = false) String auto_accept,
                             @Valid @RequestParam(required = false) String auto_block
    ) throws IOException {
        int MAX_FILE_SIZE = 1024 * 1024 * 15; // 40MB
        val checkHaveImages = images.size() > 1 || (images.size() == 1 && !images.get(0).getOriginalFilename().isEmpty());
        val checkHaveVideo = video.size() > 1 || (video.size() == 1 && !video.get(0).getOriginalFilename().isEmpty());
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        }

        Integer idPost = null;
        try {
            idPost = Integer.parseInt(id);
        } catch (Exception e) {
            throw new Exception("1003", "Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }

        if(idPost < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        val post = postDao.findPostById(idPost);
        if (post == null) {
            throw new Exception("9992", "Post is not existed", "Bài viết không tồn tại");
        }
        if (account.getId() != post.getAccountId()) {
            throw new Exception("1018", "Do not edit other people's posts", "Không thể sửa bài viết của người khác");
        }

        if (checkHaveImages && checkHaveVideo) {
            throw new Exception("1017", "Cannot add the image and video", "không thể cùng thêm ảnh và video");
        }
        String message = "";
        if (checkHaveVideo) {
            try {
                for (val file : video) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        throw new Exception("1006", "File size is too big", "Cỡ file quá kích cỡ quy định");
                    }

                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File videos = new File();
                    videos.setUrl(file.getOriginalFilename());
                    videos.setModifiedOn();
                    videos.setPost_id(idPost);
                    fileRepo.save(videos);
                }
                message = "OK";

            } catch (Exception e) {
                throw new Exception("1007", "Upload File Failed", "upload thất bại");
            }
        }
        if (checkHaveImages) {
            try {
                for (val file : images) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        throw new Exception("1006", "File size is too big", "Cỡ file quá kích cỡ quy định");
                    }
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    File image = new File();
                    image.setUrl(file.getOriginalFilename());
                    image.setCreatedOn();
                    image.setPost_id(idPost);
                    fileRepo.save(image);
                }
                message = "OK";
            } catch (Exception e) {
                throw new Exception("1007", "Upload File Failed", "upload thất bại");
            }
        }
        ;
        // Xóa ảnh trong post
        if (image_del != null && !image_del.isEmpty()) {
            val File = fileDao.findPostById(idPost, image_del);
            if (File == null) {
                throw new Exception("1019", "Image is not in post", "File ảnh không có trong bài viết");
            }
            if (File != null) {

                fileRepo.deleteById(File.getId());
            }
        }
        post.setContent(described);
        post.setAuto_accept(true);
        postRepo.save(post);
        val data = new ArrayList<>();
        val dataResponse = new PostDataRepone();
        dataResponse.setUrl(account.getName() + "/post/" + id);
        data.add(dataResponse);
        val response = new BaseResponse();
        response.setCode("1000");
        response.setMessage(message);
        response.setData(data);
        return response;

    }

    //delete post
    @PostMapping("/delete-post")
    public BaseResponse delete(@Valid @RequestParam(required = false) String token,
                               @Valid @RequestParam(required = false) String id
    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
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
        val post = postDao.findPostById(idI);
        if (post == null) {
            throw new Exception("9992", "Post is not existed", "Bài viết không tồn tại");
        }
        if (account.getId() != post.getAccountId()) {
            throw new Exception("1020", "Do not edit other people's posts", "Không thể xóa bài viết của người khác");
        }
        postRepo.deleteById(idI);

        val response = new BaseResponse();
        response.setCode("1000");
        response.setMessage("OK");
        return response;
    }

    //get comment
    @PostMapping("/get-comment")
    public BaseResponse comment(@Valid @RequestParam(required = false) String token,
                                @Valid @RequestParam(required = false) String id,
                                @Valid @RequestParam(required = false) String index,
                                @Valid @RequestParam(required = false) String count

    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (index == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (count == null) throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");

        Integer idI = null;
        Integer indexI = null;
        Integer countI = null;
        try {
            idI = Integer.parseInt(id);
            indexI = Integer.parseInt(index);
            countI = Integer.parseInt(count);
        }catch (Exception e){
            throw new Exception("1003","Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }
        if(idI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(indexI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(countI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        val comments = commentDao.findCommentAll(idI, indexI, countI);
        if (comments == null) {
            throw new Exception("9992", "Post is not existed", "Bài viết không tồn tại");
        }
        val data = new ArrayList<>();
        for (val comment : comments) {
            val dataResponse = new CommentReponse();
            dataResponse.setId(String.valueOf(comment.getId()));
            dataResponse.setComment(comment.getContent());
            dataResponse.setCreated(account.getName());

            val post = postDao.findPostById(idI);
            if (post == null) {
                throw new Exception("9992", "Post is not existed", "Bài viết không tồn tại");
            }
            if (post.getAccountId() != account.getId()) {
                throw new Exception("1009", "Not access", "Không có quyền truy cập tài nguyên");
            }
            val acounts = accountDao.findAccountById(post.getAccountId());
            List<CommentReponse.PosterReponse> posters = new ArrayList<>();
            CommentReponse.PosterReponse poster = new CommentReponse.PosterReponse();
            poster.setId(String.valueOf(acounts.getId()));
            poster.setPhone(acounts.getPhoneNumber());
            poster.setAvatar(acounts.getAvatar());
            posters.add(poster);
            dataResponse.setPosters(posters);
            data.add(dataResponse);
        }
        val response = new BaseResponse();
        response.setCode("1000");
        response.setMessage("OK");
        response.setData(data);
        return response;
    }

    //set comment
    @PostMapping("/add-comment")
    public BaseResponse addComment(@Valid @RequestParam(required = false) String token,
                                   @Valid @RequestParam(required = false) String id,
                                   @Valid @RequestParam(required = false) String comment,
                                   @Valid @RequestParam(required = false) String index,
                                   @Valid @RequestParam(required = false) String count

    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (index == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (count == null) throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");

        if (comment == null) throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        Integer idI = null;
        Integer indexI = null;
        Integer countI = null;
        try {
            idI = Integer.parseInt(id);
            indexI = Integer.parseInt(index);
            countI = Integer.parseInt(count);
        }catch (Exception e){
            throw new Exception("1003","Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }
        if(idI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(indexI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(countI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if (id != null) {
            val post = postDao.findPostById(idI);
            if (post == null) {
                throw new Exception("9992", "Post is not existed", "Bài viết không tồn tại");
            }
            if (post.getAccountId() != account.getId()) {
                throw new Exception("1009", "Not access", "Không có quyền truy cập tài nguyên");
            }
        }
        try {
            Comment comments = new Comment();
            comments.setPostId(idI);
            comments.setContent(comment);
            comments.setAccountId(account.getId());
            comments.setCreatedOn();
            commentRepo.save(comments);
        } catch (Exception e) {
            throw new Exception("9999", "Exception error", "Lỗi exception");
        }
        ;
        val commentDetails = commentDao.findCommentByPostId(idI, indexI, countI);
        val data = new ArrayList<>();

        val post = postDao.findPostById(idI);


        List<CommentReponse> dataRes = new ArrayList<>();
        for (val commentDetail : commentDetails) {
            List<CommentReponse.PosterReponse> posters = new ArrayList<>();
            val dataResponse = new CommentReponse();
            dataResponse.setId(String.valueOf(commentDetail.getId()));
            dataResponse.setComment(commentDetail.getContent());
            Account accountComment = null;
            if (commentDetail.getAccountId() != 0) {
                accountComment = accountDao.findAccountById(commentDetail.getAccountId());
            }
            CommentReponse.PosterReponse poster = new CommentReponse.PosterReponse();
            poster.setId(String.valueOf(commentDetail.getAccountId()));
            if (accountComment != null) {
                dataResponse.setCreated(accountComment.getName());
                poster.setPhone(accountComment.getPhoneNumber());
                poster.setAvatar(accountComment.getAvatar());
                poster.setId(String.valueOf(accountComment.getId()));
            }
            posters.add(poster);
            dataResponse.setPosters(posters);
            dataRes.add(dataResponse);
        }
        data.add(dataRes);
        val response = new BaseResponse();
        response.setCode("1000");
        response.setMessage("OK");
        response.setData(data);
        return response;
    }


    //set comment
    @PostMapping("/edit-comment")
    public BaseResponse editComment(@Valid @RequestParam(required = false) String token,
                                    @Valid @RequestParam(required = false) String id,
                                    @Valid @RequestParam(required = false) String comment,
                                    @Valid @RequestParam(required = false) String index,
                                    @Valid @RequestParam(required = false) String count,
                                    @Valid @RequestParam(required = false) String id_comment

    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (index == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        }
        if (count == null) throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        if (id_comment == null) throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
        if (comment == null) throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");

        Integer idI = null;
        Integer indexI = null;
        Integer countI = null;
        Integer id_commentI = null;

        try {
            idI = Integer.parseInt(id);
            indexI = Integer.parseInt(index);
            countI = Integer.parseInt(count);
            id_commentI = Integer.parseInt(id_comment);
        }catch (Exception e){
            throw new Exception("1003","Parameter type is invalid", "Kiểu tham số không đúng đắn");
        }
        if(idI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(indexI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(countI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if(id_commentI < 0){
            throw new Exception("1004", "Parameter type is invalid", "Giá trị của tham số không hợp lệ");
        }
        if (id != null) {
            val post = postDao.findPostById(idI);
            if (post == null) {
                throw new Exception("9992", "Post is not existed", "Bài viết không tồn tại");
            }
        }
        try {
            Comment comments = commentRepo.findById(id_commentI).get();
            if (comments.getAccountId() != account.getId()) {
                throw new Exception("1009", "Not access", "Không có quyền truy cập tài nguyên");
            }
            if (comments != null) {
                comments.setContent(comment);
                comments.setAccountId(account.getId());
                comments.setCreatedOn();
                commentRepo.save(comments);
            }
        } catch (Exception e) {
            throw new Exception("1001", "Can not connect to DB", "Lỗi mất kết nối DB/hoặc thực thi câu SQL");
        }
        ;
        val commentDetails = commentDao.findCommentByPostId(idI, indexI, countI);
        val data = new ArrayList<>();

        val post = postDao.findPostById(idI);
        if (post.getAccountId() != account.getId()) {
            throw new Exception("1009", "Not access", "Không có quyền truy cập tài nguyên");
        }

        List<CommentReponse> dataRes = new ArrayList<>();
        for (val commentDetail : commentDetails) {
            List<CommentReponse.PosterReponse> posters = new ArrayList<>();
            val dataResponse = new CommentReponse();
            dataResponse.setId(String.valueOf(commentDetail.getId()));
            dataResponse.setComment(commentDetail.getContent());
            Account accountComment = null;
            if (commentDetail.getAccountId() != 0) {
                accountComment = accountDao.findAccountById(commentDetail.getAccountId());
            }
            CommentReponse.PosterReponse poster = new CommentReponse.PosterReponse();
            poster.setId(String.valueOf(commentDetail.getAccountId()));
            if (accountComment != null) {
                dataResponse.setCreated(accountComment.getName());
                poster.setPhone(accountComment.getPhoneNumber());
                poster.setAvatar(accountComment.getAvatar());
                poster.setId(String.valueOf(accountComment.getId()));
            }
            posters.add(poster);
            dataResponse.setPosters(posters);
            dataRes.add(dataResponse);
        }
        data.add(dataRes);
        val response = new BaseResponse();
        response.setCode("1000");
        response.setMessage("OK");
        response.setData(data);
        return response;
    }

    //api get post
    @PostMapping("/get_post")
    public BaseResponse get_post(@Valid @RequestParam(required = false) String token,
                                 @Valid @RequestParam(required = false) String id

    ) throws IOException {
        val account = checkJwt(token);
        if (id == null) {
            throw new Exception("1002", "Parameter is not enought", "Số lượng parameter không đầy đủ");
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
        val post = postDao.findPostById(idI);
        if (post == null) {
            throw new Exception("9992", "Post is not existed", "Bài viết không tồn tại");
        }
        PostResponseId postResponseById = new PostResponseId();
        postResponseById.setId(String.valueOf(post.getId()));
        postResponseById.setDescribed(post.getContent());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS MM/dd/yyyy");
        postResponseById.setCreated(dateFormat.format(post.getCreatedOn()));
        postResponseById.setModified(dateFormat.format(post.getModifiedOn()));
        val countLike = likeDao.countLikeByPostId(post.getId());
        postResponseById.setLike(String.valueOf(countLike));
        val countComment = likeDao.countCommentByPostId(post.getId());
        postResponseById.setComment(String.valueOf(countComment));
        if (post.getAccountId() != 0) {
            val accountPost = accountDao.findAccountById(post.getAccountId());
            if (accountPost != null) {
                PostResponseId.AuthorReponse authorReponse = new PostResponseId.AuthorReponse();
                authorReponse.setAvatar(accountPost.getAvatar());
                authorReponse.setId(String.valueOf(accountPost.getId()));
                authorReponse.setPhone(accountPost.getPhoneNumber());
                authorReponse.setOnline("0");
                List<PostResponseId.AuthorReponse> authorReponses = new ArrayList<>();
                authorReponses.add(authorReponse);
                postResponseById.setAuthor(authorReponses);
            }
            val files = fileDao.findPostByAll(post.getId());
            if (files != null) {
                List<PostResponseId.ImageResponse> imageResponses = new ArrayList<>();
                for (val image : files) {
                    PostResponseId.ImageResponse imageResponse = new PostResponseId.ImageResponse();
                    imageResponse.setId(String.valueOf(image.getId()));
                    imageResponse.setUrl(image.getUrl());
                    imageResponses.add(imageResponse);
                }
                postResponseById.setImage(imageResponses);
            }
        }
        postResponseById.setState(String.valueOf(post.isState()));
        postResponseById.setCan_comment(String.valueOf(post.isCan_comment()));
        postResponseById.setBanned(String.valueOf(post.isBanned()));
        postResponseById.setCan_edit(String.valueOf(post.isCan_edit()));
        postResponseById.setIs_liked(String.valueOf(checkLikePostById(account, post)));
        postResponseById.setUrl(account.getName() + "/post/" + post.getId());
        val response = new BaseResponse();
        List<Object> data = new ArrayList<>();
        data.add(postResponseById);
        response.setData(data);
        response.setCode("1000");
        response.setMessage("OK");
        return response;
    }

    private boolean checkLikePostById(Account account, Post post) {
        if (account.getId() != null && post.getId() != null) {
            val like = likeDao.findLikeByPostIdAndAccountId(post.getId(), account.getId());
            if (like != null && like.getId() > 0) {
                return true;
            }
        }
        return false;
    }

}
