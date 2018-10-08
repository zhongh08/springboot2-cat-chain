package com.dachen.cat;

import com.dianping.cat.Cat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CatMsgContext implements Cat.Context, Serializable {
    private static final long serialVersionUID = 7426007315111778513L;

    private Map<String,String> properties = new HashMap<String,String>();

    @Override
    public void addProperty(String s, String s1) {
        properties.put(s,s1);
    }

    @Override
    public String getProperty(String s) {
        return properties.get(s);
    }

}
