package com.web.dto.Post;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommentReponse {
    private String id;
    private String comment;
    private String created;
    private List<PosterReponse> posters;
    @Getter
    @Setter
    @JsonRootName(value = "poster")
    public static class  PosterReponse{
        private String id;
        private String phone;
        private String avatar;
    }
}
