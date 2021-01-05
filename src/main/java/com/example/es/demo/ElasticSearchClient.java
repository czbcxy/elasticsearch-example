//package com.example.es.demo;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import java.io.IOException;
//
//public class ElasticSearchClient {
//
//    protected static RestHighLevelClient client = null;
//
//    static {
//        createClient();
//    }
//
//    public static void createClient() {
//        // 集群连接 http
//        RestClientBuilder build = RestClient.builder(new HttpHost("localhost", 9200, "http"));
//        // 集群连接 tcp
////        RestClientBuilder build = RestClient.builder(new TransportAddress(InetAddress.getByName("localhost"), 9300));
//        client = new RestHighLevelClient(build);
//        System.err.println("===========" + client + "===========");
//    }
//
//}
