package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.RouteImgDao;
import cn.itcast.travel.dao.SellerDao;
import cn.itcast.travel.dao.impl.RouteDaoImpl;
import cn.itcast.travel.dao.impl.RouteImgDaoImpl;
import cn.itcast.travel.dao.impl.SellerDaoImpl;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.RouteImg;
import cn.itcast.travel.domain.Seller;
import cn.itcast.travel.service.RouteService;

import java.util.List;

public class RouteServiceImpl implements RouteService {
    private RouteDao routeDao = new RouteDaoImpl();
    private SellerDao sellerDao = new SellerDaoImpl();
    private RouteImgDao routeImgDao = new RouteImgDaoImpl();
    /**
     * 目的：封装pageBean对象并返回
     * @param currentPage
     * @param pageSize
     * @param cid
     * @param rname
     * @return
     *
     *  1. int totalCount：总记录数
        2. int totalPage：总页数
        3. int currentPage：当前页码
        4. int pageSize：当前页显示的条数
        5. List list：当前页显示的数据集合
     */
    @Override
    public PageBean pageQuery(int currentPage, int pageSize, int cid, String rname) {
        //1.创建PageBean对象
        PageBean<Route> pageBean = new PageBean<>();
        //2.设置前台传入的参数
        pageBean.setCurrentPage(currentPage);
        pageBean.setPageSize(pageSize);

        //3.查询dao层数据，将返回数据再次设置进pageBean
        int totalCount = routeDao.finTotalCount(cid,rname);
        pageBean.setTotalCount(totalCount);
        int start = (currentPage -1 ) * pageSize; //开始每页查询的索引
        List<Route> list = routeDao.pageList(cid, start, pageSize,rname);
        pageBean.setList(list);

        //4.总页数
        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : (totalCount / pageSize)+1;
        pageBean.setTotalPage(totalPage);
        return pageBean;
    }

    @Override
    public Route findOne(String rid) {

        //1.根据id去route表中查询route对象
        Route route = routeDao.findOne(Integer.parseInt(rid));
        //2.根据sid去serller表中查询seller对象
        Seller seller = sellerDao.findById(route.getSid());
        //3.根据route中的rid去查询图片的旅游图片的集合
        List<RouteImg> routeImgs = routeImgDao.findByRid(route.getRid());

        //4.将查询出来的信息,存入route集合中
        route.setRouteImgList(routeImgs);
        route.setSeller(seller);
        return route;
    }
}
