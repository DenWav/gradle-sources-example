package com.example;

import libs.com.google.gson.Gson;
import libs.com.google.gson.JsonObject;

public class Main {

    public static void main(final String[] args) {
        final JsonObject o = new JsonObject();
        o.addProperty("hello", "world");
        System.out.println(new Gson().toJson(o));
    }
}
