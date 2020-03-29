package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.Favorite;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImple;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {

    /**
     * 分页查询
     * @param request
     * @param response
     * @return PageBean
     */
    public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.获取前端请求
        String currentPageStr = request.getParameter("currentPage");
        String pageSizeStr = request.getParameter("pageSize");
        String cidStr = request.getParameter("cid");

        //获取搜索条件
        String rname = request.getParameter("rname");
        if(rname != null){
            rname = new String(rname.getBytes("iso-8859-1"), "UTF-8");
        }
        //System.out.println("cid ："+cidStr +" rname："+rname);
        //2.处理参数
        int  currentPage = 0;
        int pageSize = 0;
        int cid = 0;

        //cid
        if( cidStr != null && cidStr.length() > 0 && !"null".equals(cidStr)){
            cid = Integer.parseInt(cidStr);
        }

        //currentPage
        if( currentPageStr != null && currentPageStr.length() > 0){
            currentPage = Integer.parseInt(currentPageStr);
        }else{
            currentPage = 1;
        }
        //pageSize
        if( pageSizeStr != null && pageSizeStr.length() > 0){
            pageSize = Integer.parseInt(pageSizeStr);
        }else{
            pageSize = 5;
        }
        //3.调用service查询
        RouteService service = new RouteServiceImpl();
        PageBean pageBean = service.pageQuery(currentPage, pageSize, cid,rname);
        writeValue(pageBean,response);
    }

    /**
     * 根据rid（旅游线路id）请去查询一个旅游线路的详细信息
     * @param request
     * @param response
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.获取rid参数信息
        String rid = request.getParameter("rid");
        if(rid != null && rid.length() > 0){
            System.out.println(rid);
            //2.调用service层查询
            RouteService service = new RouteServiceImpl();
            Route route = service.findOne(rid);
            //收藏次数查询
            FavoriteService favoriteService = new FavoriteServiceImple();
            int fcount = favoriteService.findByRid(route.getRid());
            route.setCount(fcount);
            writeValue(route,response);
        }

    }

    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.获取rid
        String rid = request.getParameter("rid");
        int uid = 0;
        //2.获取rid,从session中的user中获取
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            uid = 0;
        }else{
            uid = user.getUid();
        }
        FavoriteService service = new FavoriteServiceImple();
        boolean favorite = service.isFavorite(rid, uid);

        //3.写回到前端
        writeValue(favorite,response);
    }

    /**
     * 添加收藏
     * @param request
     * @param response
     */
    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
//1.获取rid
        String rid = request.getParameter("rid");
//2.获取rid,从session中的user中获取
        User user = (User) request.getSession().getAttribute("user");
        int uid = 0;
        if(user == null){
            return; //您尚未登录
        }else{
            uid = user.getUid();
        }
        FavoriteService service = new FavoriteServiceImple();
        service.addFavorite(rid, uid);
    }
}
