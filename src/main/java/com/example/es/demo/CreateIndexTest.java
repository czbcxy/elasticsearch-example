package com.example.es.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.es.demo.client.ElasticSearchClient;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateIndexTest extends ElasticSearchClient {

    public static void main(String[] args) throws IOException {
//        doCreateIndex("test004"); //创建索引
//        doGetIndex("test004");   //查询索引集合
//        doSyncBatchInsert("test004");//添加内容
//        doSearch("test004");//查询
//        doDeleteIndex("test004"); //删除索引
    }

    public static void doCreateIndex(String index) throws IOException {
        String toJson = "{\n" +
                "      \"properties\" : {\n" +
                "        \"age\" : {\n" +
                "          \"type\" : \"text\",\n" +
                "          \"fields\" : {\n" +
                "            \"keyword\" : {\n" +
                "              \"type\" : \"keyword\",\n" +
                "              \"ignore_above\" : 256\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"username\" : {\n" +
                "          \"type\" : \"text\",\n" +
                "          \"fields\" : {\n" +
                "            \"keyword\" : {\n" +
                "              \"type\" : \"keyword\",\n" +
                "              \"ignore_above\" : 256\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        Boolean employee = createIndex(index, toJson);
        System.err.println("========创建索引====" + employee);
    }

    public static void doGetIndex(String index) throws IOException {
        GetIndexResponse getIndexResponse = queryIndex(index);
        System.err.println("返回索引：" + Arrays.toString(getIndexResponse.getIndices()));
    }

    public static void doDeleteIndex(String index) throws IOException {
        Boolean aBoolean = deleteIndex(index);
        System.err.println("返回索引：" + index + " 结果:" + aBoolean);
    }

    public static void doSyncBatchInsert(String index) throws IOException {
        ArrayList<String> jsonList   = new ArrayList<>();
        JSONObject        jsonObject = new JSONObject();
        jsonObject.put("username", "zhangsan");
        jsonObject.put("age", 23);
        jsonList.add(jsonObject.toJSONString());
        BulkResponse       bulkItemResponses = syncBatchInsert(index, jsonList);
        BulkItemResponse[] items             = bulkItemResponses.getItems();
        for (BulkItemResponse item : items) {
            System.err.println(item.getResponse().toString());
        }
    }

    public static void doSearch(String index) throws IOException {
        SearchSourceBuilder  builder              = new SearchSourceBuilder();
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        builder.query(matchAllQueryBuilder);
        List<String> search = search(index, builder);
        System.err.println(search);

        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("username", "zhang");
        builder.query(prefixQueryBuilder);
        List<String> search1 = search(index, builder);
        System.err.println(search1);
    }
}
