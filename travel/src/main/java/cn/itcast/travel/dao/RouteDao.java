package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Route;

import java.util.List;

public interface RouteDao {

    public int finTotalCount(int cid, String rname);
    public List<Route> pageList(int cid, int start, int pageSize, String rname);

    //查询旅游线路详细信息
    Route findOne(int id);

}
