package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService {
    private UserDao dao = new UserDaoImpl();
    @Override
    public boolean regist(User user) {
        //根据用户名查询对象，如果不为空
        User u = dao.findByUsername(user.getUsername());
        if(u != null){  //查询到重复。不进行注册
            return false;
        }
        //保存用户信息
        //设置激活码:唯一字符串
        user.setCode(UuidUtil.getUuid());
        user.setStatus("N");
        dao.sava(user);

        //激活邮件，发送正文
        String content = "<a href='http://192.168.0.101/travel/user/activeUser?code="+user.getCode()+"'>激活【黑马旅游网】</a>";
        MailUtils.sendMail(user.getEmail(),content,"激活邮件");
        return true;
    }

    @Override
    public boolean active(String code) {
        //1.根据状态码寻找用户
        User user = dao.findByCode(code);
        if( user != null){ //找到
            dao.updataStatus(user);
            return true;
        }
        return false;
    }

    @Override
    public User login(User user) {
        return dao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }
}
