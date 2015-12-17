package com.example.helpster;

import java.util.HashMap;

public class RequestItem {

    private String username, request, amount, compensation, place, endtime;

    public enum VIEW_TYPE {
        list, single
    }

    public RequestItem(HashMap<String, String> hm, VIEW_TYPE viewtype) {
        username = hm.get("username");
        place = hm.get("place");
        amount = hm.get("amount");
        if (viewtype == VIEW_TYPE.single) {
            request = hm.get("request");
            compensation = hm.get("compensation");
            endtime = hm.get("endtime");
        }
    }
}
