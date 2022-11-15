package com.web.dto.Post;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@JsonRootName(value = "post_reponse")
public class PostResponseId {
    private String id;
    private String described;
    private String created;
    private String modified;
    private String like;
    private String comment;
    private String is_liked;
    private List<ImageResponse> image;
    private List<VideoResponse> video;
    private List<AuthorReponse> author;
    private String state;
    private String is_blocked;
    private String can_edit;
    private String banned;
    private String can_comment;
    private String url;
    private String messages;

    @Getter
    @Setter
    @JsonRootName(value = "image")
    public static class  ImageResponse{
        private String id;
        private String url;
    }
    @Getter
    @Setter
    @JsonRootName(value = "video")
    public static class  VideoResponse{
        private String url;
        private String thumb;
    }

    @Getter
    @Setter
    @JsonRootName(value = "author")
    public static class  AuthorReponse{
        private String id;
        private String phone;
        private String avatar;
        private String online;
    }
}
