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
    private String id;
    private String name;
    private List<String> image;
    private List<VideoResponse> video;
    private String described;
    private Date created;
    private String like;
    private String comment;
    private String is_liked;
    private String is_blocked;
    private String can_comment;
    private String can_edit;
    private String banned;
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
        private String id;
        private String phone;
        private String avatar;
        private String online;
    }

}


