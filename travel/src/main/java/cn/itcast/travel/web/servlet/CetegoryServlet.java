package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.service.impl.CategoryServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/cetegory/*")
public class CetegoryServlet extends BaseServlet {

    public void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        CategoryService service = new CategoryServiceImpl();
        List<Category> all = service.findAll();

        //将list转换成json传到前台
//        ObjectMapper mapper = new ObjectMapper();
//        resp.setContentType("application/json;charset=utf-8");
//        String json = mapper.writeValueAsString(all);
//        resp.getWriter().write(json);
        
            writeValue(all,resp);

    }
}
