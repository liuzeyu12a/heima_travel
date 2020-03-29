package cn.itcast.travel.service;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;

public interface RouteService {
    public PageBean pageQuery(int currentPage, int pageSize, int cid, String rname);

    Route findOne(String rid);
}
