package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImple;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao dao = new CategoryDaoImple();
    @Override
    public List<Category> findAll() {
        List<Category> all = null;  //要返回的list
        //使用redis作缓存优化
        //1.获取redis客户端连接
        Jedis jedis = JedisUtil.getJedis();
        //2.获取redis中键值为category的集合
        //Set<String> category = jedis.zrange("category", 0, -1); //获取redis缓存中的数据
        //携带cide的查询方式
        Set<Tuple> category = jedis.zrangeWithScores("category", 0, -1);
        if( category == null || category.size() == 0){
            System.out.println("从数据库中获取...");
            //3.第一次访问，需要查询数据库，并添加至redis中
            all = dao.findAll();
            for (Category name : all) {
                jedis.zadd("category",name.getCid(),name.getCname());
            }
        }else{ //从数据库中获取
            System.out.println("从缓存中获取...");
            all = new ArrayList<Category>();
            //4.因为从redis中获取的是set集合，所以要将其转为list集合才可以返回
            for (Tuple tuple : category) {
                Category c = new Category();
                c.setCid((int)tuple.getScore());
                c.setCname(tuple.getElement());
                all.add(c);
            }
        }
        return all;
    }
}
