package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //删除session 中的user
        req.getSession().removeAttribute("user");

        //0.校验验证码
        String check = req.getParameter("check");
        //0.1获取系统给出的二维码
        String checkcode_server = (String) req.getSession().getAttribute("CHECKCODE_SERVER");
        req.getSession().removeAttribute("CHECKCODE_SERVER");
        if(checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)){
            //0.2验证不通过，提示验证码失败
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("登录失败，验证码错误！");
            //0.3.将info数据序列化成json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            //0.4.将json写回客户端
            //5.1设置响应的json格式编码
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write(json);//字符流
            return;
        }

        //1.获取登录请求参数
        Map<String, String[]> map = req.getParameterMap();
        //2.封装对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service层登录
        UserService service = new UserServiceImpl();
        User u = service.login(user);
        //4.设置响应消息
        ResultInfo info = new ResultInfo();
        //4.1登录名或密码错误
        if(u == null ){
            info.setFlag(false);
            info.setErrorMsg("登录名或密码错误");
        }
        //4.2尚未激活
        if( u!= null && "N".equals(u.getStatus())){
            info.setFlag(false);
            info.setErrorMsg("请先去邮箱激活");
        }
        //4.3已激活
        if( u!= null && "Y".equals(u.getStatus())){
            info.setFlag(true);
        }

        //5.为前台响应数据
        req.getSession().setAttribute("user",u);
        //5.1将info转换成json数据
        ObjectMapper mapper = new ObjectMapper();
        resp.setContentType("application/json;charset=utf-8");
        mapper.writeValue(resp.getOutputStream(),info);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
