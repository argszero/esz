package com.github.esz.api.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.esz.sdk.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by shaoaq on 7/15/15.
 */
@Controller
public class ApiController {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    @Autowired
    private ApiService apiService;

    @RequestMapping("/api/{user}/**")
    @ResponseBody
    public JsonNode api(@PathVariable String user, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        try {
            String servletPath = request.getServletPath();
            String path = servletPath.substring(("/api/" + user + "/").length());
            logger.info("user:" + user + ",path:" + path);
            Map<String, String[]> parameterMap = request.getParameterMap();
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                ArrayNode value = new ArrayNode(JsonNodeFactory.instance);
                String[] valueArray = entry.getValue();
                for (String v : valueArray) {
                    value.add(v);
                }
                node.set(entry.getKey()+"[]", value);
                node.put(entry.getKey(), valueArray == null ? null : valueArray[0]);
            }
            return apiService.invoke(servletPath,node);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/api/esz/time")
    @ResponseBody
    public String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
