package com.web.dto.Post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataPostResponse {
    private List<ListPostResponse> data;
    private int inCampaign;
    private int campaignId;

}
