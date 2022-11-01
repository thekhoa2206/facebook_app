package com.web.dto.Post;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReponse {
    private int id;
    private String comment;
    private String created;
private  PosterReponse poster;
    @Getter
    @Setter
    @JsonRootName(value = "poster")
    public static class  PosterReponse{
        private int id;
        private String phone;
        private String avatar;
    }
}
