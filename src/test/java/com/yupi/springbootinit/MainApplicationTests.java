package com.yupi.springbootinit;

import cn.hutool.http.HttpRequest;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yupi.springbootinit.config.WxOpenConfig;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Resource
    private PostService postService;

    private static final Gson GSON = new Gson();

    @Test
    void contextLoads() {
        System.out.println(wxOpenConfig);
    }

    @Test
    void test20230527(){
        String json = "{\"sortField\": \"createTime\", \"sortOrder\": \"descend\", \"reviewStatus\": 1, \"current\": 1, \"priority\": 999}";
        String result2 = HttpRequest.post("https://www.code-nav.cn/api/post/list/page/vo")
                .body(json)
                .execute().body();
        System.out.println(result2);
        Map<String,Object> map = GSON.fromJson(result2,new TypeToken<Map<String,Object>>(){}.getType());
        JsonObject jsonObject = GSON.toJsonTree(map.get("data")).getAsJsonObject();
        JsonArray records = (JsonArray) jsonObject.get("records");
        List<Post> list = new ArrayList<>();
        for ( Object re : records) {
            JsonObject temp = ( JsonObject ) re;
            System.out.println(GSON.toJson(temp));
            Post post = new Post();
            post.setContent(temp.get("content").getAsString());
            post.setTitle(temp.get("title").getAsString());
            post.setThumbNum(temp.get("thumbNum").getAsInt());
            post.setFavourNum(temp.get("favourNum").getAsInt());
            post.setCreateTime(new Date());
            post.setUpdateTime(new Date());
            post.setIsDelete(0);
            post.setUserId(1L);
            list.add(post);
        }
        boolean b = postService.saveBatch(list);
        Assertions.assertTrue(b);
//        System.out.println(result2);
//        JsonObject jsonObject = GSON.fromJson(result2, JsonObject.class);
//        System.out.println(jsonObject);
//        JsonObject data = GSON.fromJson(jsonObject.get("data"), JsonObject.class);
//        System.out.println(data);
//        JsonElement records = data.get("records");
//        System.out.println(records);
//        Post[] array = GSON.fromJson(records, new TypeToken<Post[]>(){}.getType());
//        System.out.println(GSON.toJson(array));

    }

}
