package com.example.es.demo.client;


import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;

/**
 * 基本 CRUD
 */
@Slf4j
public class ElasticSearchClient extends ElasticSearchBase {

    /**
     * 添加索引
     */
    public static Boolean createIndex(String index, String toJson) throws IOException {
        if (indexExists(index)) {
            return true;
        }
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index.toLowerCase());
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1)
                .build());
        createIndexRequest.mapping(toJson, XContentType.JSON);
        return esClient.indices().create(createIndexRequest, RequestOptions.DEFAULT).isAcknowledged();
    }

    public static Boolean createIndex(String index, XContentBuilder value) throws IOException {
        if (indexExists(index)) {
            return true;
        }
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index.toLowerCase());
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1)
                .build());
        createIndexRequest.mapping(value);
        return esClient.indices().create(createIndexRequest, RequestOptions.DEFAULT).isAcknowledged();
    }

    /**
     * 索引是否存在
     */
    public static Boolean indexExists(String... index) throws IOException {
        if (Objects.isNull(index)) {
            return false;
        }
        return esClient.indices().exists(new GetIndexRequest(index).humanReadable(true), RequestOptions.DEFAULT);
    }

    /**
     * 查询索引
     */
    public static GetIndexResponse queryIndex(String index) throws IOException {
        if (!indexExists(index)) {
            return null;
        }
        GetIndexRequest request = new GetIndexRequest(index);
        return esClient.indices().get(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引
     */
    public static Boolean deleteIndex(String... index) throws IOException {
        if (!indexExists(index)) {
            return false;
        }
        return esClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT).isAcknowledged();
    }


    /**
     * 重建索引
     */
    public static BulkByScrollResponse reindexIndex(String sourceIndices, String destIndex,
                                                    QueryBuilder queryBuilder) throws IOException {
        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices(sourceIndices).setDestIndex(destIndex).setDestVersionType(VersionType.EXTERNAL)
                .setDestOpType(DocWriteRequest.OpType.CREATE.name())
                .setSourceQuery(queryBuilder).setConflicts("proceed");
        return esClient.reindex(reindexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 同步批量插入数据
     */
    public static BulkResponse syncBatchInsert(String index, List<String> jsonList) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        jsonList.forEach(item -> {
            bulkRequest.add(new IndexRequest(index).source(item, XContentType.JSON));
        });
        return esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * 异步批量插入数据
     */
    public static Cancellable asyncBatchInsert(String index, List<String> jsonList) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        jsonList.forEach(item -> {
            bulkRequest.add(new IndexRequest(index).source(item, XContentType.JSON));
        });
        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {
                if (bulkItemResponses.hasFailures()) {
                    log.error("asyncBatchInsert error => index={}, jsonList={} ,bulkReponse={}", index, jsonList,
                            bulkItemResponses);
                }
            }

            @Override
            public void onFailure(Exception e) {
                log.error("asyncBatchInsert error ={},{}", index, e);
            }
        };
        return esClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, listener);
    }

    /**
     * 删除数据
     */
    public static BulkByScrollResponse deleteByQuery(String index, QueryBuilder builder) throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(builder);
        request.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        return esClient.deleteByQuery(request, RequestOptions.DEFAULT);
    }

    /**
     * 更新数据
     */
    public static BulkByScrollResponse updateByQuery(String index, QueryBuilder builder) throws IOException {
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index);
        updateByQueryRequest.setQuery(builder);
        updateByQueryRequest.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        updateByQueryRequest.setConflicts("proceed");
        return esClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
    }

    /**
     * 查询数据
     */
    public static List<String> search(String index, SearchSourceBuilder builder) throws IOException {
        SearchRequest request = new SearchRequest(index);
        request.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        request.source(builder);
        SearchResponse search = esClient.search(request, RequestOptions.DEFAULT);
        SearchHit[]    hits   = search.getHits().getHits();
        List<String>   result = new ArrayList<>();
        for (SearchHit hit : hits) {
            result.add(hit.getSourceAsString());
        }
        return result;
    }
}
