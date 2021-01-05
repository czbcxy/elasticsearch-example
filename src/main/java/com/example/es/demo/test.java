package com.example.es.demo;

public class test {

    public static void main(String[] args) {
        int   n   = 10;
        int[] ret = new int[2];
        ret[1] = 1;
        for (int i = 2; i <= n; i++) {
            ret[i % 2] += ret[(i % 2) ^ 1];
        }
        System.out.println(ret[n % 2]);
    }
}
