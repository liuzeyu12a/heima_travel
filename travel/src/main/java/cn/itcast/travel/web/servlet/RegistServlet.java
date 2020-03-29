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

@WebServlet("/registServlet")
public class RegistServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //0.校验验证码
        String check = request.getParameter("check");
        //0.1获取系统给出的二维码
        String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        request.getSession().removeAttribute("CHECKCODE_SERVER");
        if(checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)){
            //0.2验证不通过，提示验证码失败
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("注册失败，验证码错误！");
            //0.3.将info数据序列化成json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            //0.4.将json写回客户端
            //5.1设置响应的json格式编码
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);//字符流
            return;
        }


        //1.获取数据
        Map<String, String[]> map = request.getParameterMap();
        //2.封装对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.调用service查询
        UserService service = new UserServiceImpl();
        boolean flag = service.regist(user);
        ResultInfo info = new ResultInfo();
        if(flag == true){  //注册成功
            info.setFlag(true);
        }else{ //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败,用户名重复！");
        }

        //4.将info数据序列化成json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        //5.将json写回客户端
        //5.1设置响应的json格式编码
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);//字符流

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
