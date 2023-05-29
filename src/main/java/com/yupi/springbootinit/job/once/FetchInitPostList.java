package com.yupi.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yupi.springbootinit.esdao.PostEsDao;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 全量同步帖子到 es
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    private static final Gson GSON = new Gson();

    @Override
    public void run(String... args) {
        String json = "{\"sortField\": \"createTime\", \"sortOrder\": \"descend\", \"reviewStatus\": 1, \"current\": 1, \"priority\": 999}";
        String result2 = HttpRequest.post("https://www.code-nav.cn/api/post/list/page/vo")
                .body(json)
                .execute().body();
        Map<String,Object> map = GSON.fromJson(result2,new TypeToken<Map<String,Object>>(){}.getType());
        JsonObject jsonObject = GSON.toJsonTree(map.get("data")).getAsJsonObject();
        JsonArray records = (JsonArray) jsonObject.get("records");
        List<Post> list = new ArrayList<>();
        for ( Object re : records) {
            JsonObject temp = ( JsonObject ) re;
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
        if(b){
            log.info("初始化文章列表成功，共{}条！",list.size());
        }else{
            log.error("初始化文章失败！");
        }

    }
}
