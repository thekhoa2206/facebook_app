package com.web.dto.Post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataPostResponse {
    private ListPostResponse data;
    private String inCampaign;
    private String campaignId;

}
