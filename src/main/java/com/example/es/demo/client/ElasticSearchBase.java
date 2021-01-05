package com.example.es.demo.client;

import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import java.util.Objects;
import java.util.function.BiConsumer;

public class ElasticSearchBase {

    private static final String              host     = "localhost";
    private static final int                 port     = 9200; // tcp 9300
    private static final String              username = null;
    private static final String              password = null;
    private static final String              scheme   = "http";
    public static       RestHighLevelClient esClient;
    public static       BulkProcessor       bulkProcessor;


    private static BulkProcessor.Listener listener = new BulkProcessor.Listener() {
        @Override
        public void beforeBulk(long executionId, BulkRequest request) {
            request.numberOfActions();
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            response.hasFailures();
        }

        @Override
        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            failure.getMessage();
        }
    };

    static {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        if (Objects.nonNull(username)) {
            basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }
        esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, scheme))
                        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider)) //用户名和密码
                        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(50000).setSocketTimeout(300000))); //超时时间
//     @params:  BiConsumer<BulkRequest, ActionListener<BulkResponse>> consumer, Listener listener
        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer =
                (request, bulkListener) -> esClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener);
        bulkProcessor = BulkProcessor.builder(bulkConsumer, listener)
                // 每10000个request flush一次
                .setBulkActions(10000)
                // bulk数据每达到5MB flush一次
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                // 每5秒flush一次
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                // 0代表同步提交即只能提交一个request；
                // 1代表当有一个新的bulk正在累积时，1个并发请求可被允许执行
                .setConcurrentRequests(1)
                // 设置当出现代表ES集群拥有很少的可用资源来处理request时抛出
                // EsRejectedExecutionException造成N个bulk内request失败时
                // 进行重试的策略,初始等待100ms，后面指数级增加，总共重试3次.
                // 不重试设为BackoffPolicy.noBackoff()
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

    }
}
