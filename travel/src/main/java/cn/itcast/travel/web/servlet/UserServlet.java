package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private UserService service = new UserServiceImpl();

    public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
//            ObjectMapper mapper = new ObjectMapper();
//            String json = null;
//            try {
//                json = mapper.writeValueAsString(info);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            //0.4.将json写回客户端
//            //5.1设置响应的json格式编码
//            resp.setContentType("application/json;charset=utf-8");
//            resp.getWriter().write(json);//字符流
            writeValue(info,resp);
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
        //UserService service = new UserServiceImpl();
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

    public void regist(HttpServletRequest request, HttpServletResponse response) throws IOException {

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
//            //0.3.将info数据序列化成json
//            ObjectMapper mapper = new ObjectMapper();
//            String json = null;
//            try {
//                json = mapper.writeValueAsString(info);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            //0.4.将json写回客户端
//            //5.1设置响应的json格式编码
//            response.setContentType("application/json;charset=utf-8");
//            response.getWriter().write(json);//字符流
            writeValue(info,response);
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
       // UserService service = new UserServiceImpl();
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

    public void activeUser(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //1.获取激活码
        String code = request.getParameter("code");
        if( code != null){
            //开始调用service激活
            UserService service = new UserServiceImpl();
            boolean flag = service.active(code);
            String msg = null;
            if(flag == true){
                //激活成功
                msg = "激活成功，请<a href='login.html'>登录</a>";
            }else{//激活失败
                msg = "激活失败，请联系管理员！";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }

    public void findOne(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //判断session中是否有user对象，如果有则发送
        Object user = request.getSession().getAttribute("user");
        if(user != null){
            writeValue(user,response);

        }
    }

    public void exit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //销毁session数据
        request.getSession().invalidate();
        //重定向到login.html
        response.sendRedirect(request.getContextPath()+"/login.html");
    }

    public void checkCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //服务器通知浏览器不要缓存
        response.setHeader("pragma","no-cache");
        response.setHeader("cache-control","no-cache");
        response.setHeader("expires","0");

        //在内存中创建一个长80，宽30的图片，默认黑色背景
        //参数一：长
        //参数二：宽
        //参数三：颜色
        int width = 80;
        int height = 30;
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        //获取画笔
        Graphics g = image.getGraphics();
        //设置画笔颜色为灰色
        g.setColor(Color.GRAY);
        //填充图片
        g.fillRect(0,0, width,height);

        //产生4个随机验证码，12Ey
        String checkCode = getCheckCode();
        //将验证码放入HttpSession中
        request.getSession().setAttribute("CHECKCODE_SERVER",checkCode);

        //设置画笔颜色为黄色
        g.setColor(Color.YELLOW);
        //设置字体的小大
        g.setFont(new Font("黑体",Font.BOLD,24));
        //向图片上写入验证码
        g.drawString(checkCode,15,25);

        //将内存中的图片输出到浏览器
        //参数一：图片对象
        //参数二：图片的格式，如PNG,JPG,GIF
        //参数三：图片输出到哪里去
        ImageIO.write(image,"PNG",response.getOutputStream());
    }
    /**
     * 产生4位随机字符串
     */
    private String getCheckCode() {
        String base = "0123456789ABCDEFGabcdefg";
        int size = base.length();
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        for(int i=1;i<=4;i++){
            //产生0到size-1的随机值
            int index = r.nextInt(size);
            //在base字符串中获取下标为index的字符
            char c = base.charAt(index);
            //将c放入到StringBuffer中去
            sb.append(c);
        }
        return sb.toString();
    }
}
