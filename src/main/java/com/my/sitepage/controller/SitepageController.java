package com.my.sitepage.controller;

import com.my.common.aop.Permission;
import com.my.common.exception.JsonException;
import com.my.sitepage.model.Sitepage;
import com.my.sitepage.service.SitepageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/sitepage")
public class SitepageController {
    @Autowired
    private SitepageService sitepageService;

    @Value("${sitepage.weburl}")
    private String siteWebUrl;

    @Value("${list.pagesize}")
    private Integer pageSize;

    @Permission("1004")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String index(@RequestParam(value = "code", required = false)String code,
                        @RequestParam(value = "title", required = false)String title,
                        @RequestParam(value = "page", required = false)Integer page,
                        HttpServletRequest request,
                        ModelMap model){
        try{
            Map<String, Object> filter = new HashMap<String, Object>();
            filter.put("order", "id desc");
            String currentUrl = request.getRequestURI();
            if(code!=null && !code.isEmpty()){
                filter.put("code", code);
                currentUrl += "?code="+code;
            }
            if(title!=null && !title.isEmpty()){
                filter.put("title", title);
                currentUrl += (currentUrl.contains("?")?"&":"?")+"title"+title;
            }
            if(page == null || page<1){
                page = 1;
            }
            int totalCount = sitepageService.getCount(filter);
            int pageCount = (int)Math.ceil(totalCount/pageSize);
            if(pageCount <1){
                pageCount = 1;
            }

            filter.put("page", (page-1)*pageSize);
            filter.put("pagesize", pageSize);

            List<Sitepage> siteList = sitepageService.getList(filter);

            model.addAttribute("list", siteList);
            model.addAttribute("code",code);
            model.addAttribute("title", title);
            model.addAttribute("SITE_WEB", siteWebUrl);

            model.addAttribute("currentPage", page);
            model.addAttribute("pageCount", pageCount);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("currentUrl", currentUrl);
            model.addAttribute("pageTitle","页面列表 - 网页生产平台 - 后台管理系统");

            model.addAttribute("TopMenuFlag", "sitepage");
            return "sitepage/site_list";
        }catch (JsonException e){
            return "error/500";
        }
    }

    @Permission("1004")
    @RequestMapping("/add")
    public String pageAdd(ModelMap model){

        model.addAttribute("pageTitle","页面列表 - 网页生产平台 - 后台管理系统");
        model.addAttribute("TopMenuFlag", "sitepage");

        return "sitepage/site_add";
    }
}
