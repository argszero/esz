package com.github.esz.api.example.weibo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.esz.sdk.Api;
import com.github.esz.sdk.ApiService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * Created by shaoaq on 7/15/15.
 */
@Api
public class GetHomeTimeline {
    @Autowired
    private ApiService apiService;
    @Autowired
    private ObjectMapper objectMapper;

    @Api("home/timeline")
    public JsonNode getTimeline(ObjectNode parameter) throws IOException {
        System.out.println(parameter.get("token"));
        System.out.println(new URL("https://api.weibo.com/2/statuses/home_timeline.json?access_token=" + parameter.get("token").asText()));
        String result = IOUtils.toString(new URL("https://api.weibo.com/2/statuses/home_timeline.json?access_token=" + parameter.get("token").asText()));
        return objectMapper.readTree(result);
    }

    @Api("home/textline")
    public JsonNode getUserLine(ObjectNode parameter) throws IOException, InvocationTargetException, IllegalAccessException {
        ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
        JsonNode timeline = apiService.invoke("/api/weibo/home/timeline", parameter);
        ArrayNode statuses = (ArrayNode) timeline.get("statuses");
        ArrayNode texts = new ArrayNode(JsonNodeFactory.instance);
        for (JsonNode node : statuses) {
            texts.add(node.get("text").toString());
        }
        result.set("texts", texts);
        return result;
    }

    @Override
    public String toString() {
        return "GetHomeTimeline{" +
                "apiService=" + apiService +
                ", objectMapper=" + objectMapper +
                '}';
    }
}
