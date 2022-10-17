package com.web.dto.Post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
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
    private int is_liked;
    private int is_blocked;
    private int can_comment;
    private int can_edit;
    private int banned;
    private int state;
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


