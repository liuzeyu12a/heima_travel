package cn.itcast.travel.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 任务：
 *      完成方法的分发
 */
public class BaseServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       // System.out.println("baseServlet的service方法被执行了...");
        //1.获取uri
        String uri = req.getRequestURI();  //travel/user/xxx
        //截取运行的方法名
        String methodName = uri.substring(uri.lastIndexOf("/") + 1);
        //System.out.println(methodName);
        System.out.println(this);  //cn.itcast.travel.web.servlet.UserServlet@6fe6330d
        try {
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this,req,resp);  //执行方法
            //如果执行的方法不是public 可以使用暴力反射
            //method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接将对象序列化成json,并写回前端
     * @param obj
     * @param response
     */
    public void writeValue(Object obj,HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),obj);
    }

    /**
     * 直接将对象转成json字符串
     * @param obj
     * @return
     * @throws IOException
     */
    public String writeValueAsStrinig(Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.writeValueAsString(obj);
    }
}
