//package com.example.es.demo;
//
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.support.IndicesOptions;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//
//public class EmployeeAggrTest extends SearchClient {
//
//    public static void main(String[] args) {
//        aggr();
//    }
//
//    /**
//     * <pre>
//     * 需求：
//     * （1）首先按照 country 国家来进行分组
//     * （2）然后在每个 country 分组内，再按照入职年限进行分组
//     * （3）最后计算每个分组内的平均薪资
//     * </pre>
//     */
//    public static void aggr() throws ExecutionException, InterruptedException, IOException {
//        SearchRequest searchRequest = new SearchRequest();
//        IndicesOptions employee     = searchRequest.indices("employee").indicesOptions();
//
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
////                .addAggregation(
////                        AggregationBuilders
////                                // 前面的是对该操作取名，后面的是真实的字段
////                                .terms("group_by_country")
////                                .field("country")
////                                .subAggregation(
////                                        AggregationBuilders
////                                                .dateHistogram("group_by_join_date")
////                                                .field("join_date")
////                                                .dateHistogramInterval(DateHistogramInterval.YEAR) // 按照年来分
////                                                .subAggregation(
////                                                        AggregationBuilders
////                                                                .avg("ave_salary")
////                                                                .field("salary")
////                                                               )
////                                               )
////                               )
////                .execute()
////                .get();
//        System.out.println(searchResponse);
//    }
//}
