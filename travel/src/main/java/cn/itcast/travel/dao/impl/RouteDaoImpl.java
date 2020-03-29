package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.util.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class RouteDaoImpl implements RouteDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JDBCUtils.getDataSource());
    /**
     * 通过旅游分类id查询
     * @param cid
     * @return
     */
    @Override
    public int finTotalCount(int cid, String rname) {
        //String sql = "select count(*) from tab_route where cid=?";
        //Integer integer = jdbcTemplate.queryForObject(sql, Integer.class, cid);
        //自定义sql模板
        String sql = "select count(*) from tab_route where 1=1";
        StringBuffer sb = new StringBuffer(sql);
        List params  = new ArrayList();   //存储条件
        if(cid > 0){
            sb.append(" and cid = ?");
            params .add(cid);
        }
        if(rname != null && rname.length()>0){
            sb.append(" and rname like ? ");
            params .add("%"+rname+"%");
        }
         sql = sb.toString();
        return jdbcTemplate.queryForObject(sql, Integer.class, params .toArray());
    }
    /**
     * 查询每页显示的集合
     * @param cid
     * @param start
     * @param pageSize
     * @return
     */
    @Override
    public List<Route> pageList(int cid, int start, int pageSize, String rname) {
        String sql = "select * from tab_route where 1=1";
        StringBuffer sb = new StringBuffer(sql);
        List params = new ArrayList();
        if(cid != 0 ){
            sb.append(" and cid=? ");
            params.add(cid);
        }
        if(rname != null && rname.length() > 0){
            sb.append(" and rname like ? ");
            params.add("%"+rname+"%");
        }
        sb.append(" limit ?,? ");
        sql = sb.toString();
        params.add(start);
        params.add(pageSize);

        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<Route>(Route.class), params.toArray());
    }

    @Override
    public Route findOne(int rid) {
        String sql = "select * from tab_route where rid=?";
        return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<Route>(Route.class),rid);
    }
}