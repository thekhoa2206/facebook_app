package com.web.dto.Post;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListPostResponse {
    private List<PostResponse> posts;
    private String newItems;
    // id bài viết cuối
    private String lastId;

}
