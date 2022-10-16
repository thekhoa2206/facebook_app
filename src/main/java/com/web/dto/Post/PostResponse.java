package com.web.dto.Post;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonRootName(value = "post")
public class PostResponse {
    private int id;
    private String name;
    private List<String> image;
    private List<VideoResponse> video;
    private String described;
    private Date created;
    private int like;
    private int comment;
    private boolean is_liked;
    private boolean is_blocked;
    private boolean can_comment;
    private boolean can_edit;
    private boolean banned;
    private String state;
    private AuthorReponse author;

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
        private int id;
        private String phone;
        private String avatar;
        private boolean online;
    }

}


