package cn.itcast.travel.dao;

import cn.itcast.travel.domain.User;

public interface UserDao {

    //根据名称查询用户
    public User findByUsername(String username);
    public  void sava(User user);

    //根据状态码寻找用户
    public User findByCode(String code);
    public void updataStatus(User user);

    public User findByUsernameAndPassword(String username,String password);
}
