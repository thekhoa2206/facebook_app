package com.web.controller;

import com.web.dao.jpa.PostDao;
import com.web.dao.jpa.ReportDao;
import com.web.dao.jpa.TypeReportDao;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReportController extends BaseController{
    private final PostDao postDao;
    private final ReportDao reportDao;
    private final TypeReportDao typeReportDao;

    public ReportController(PostDao postDao, ReportDao reportDao, TypeReportDao typeReportDao) {
        this.postDao = postDao;
        this.reportDao = reportDao;
        this.typeReportDao = typeReportDao;
    }

    //api report
    
}
