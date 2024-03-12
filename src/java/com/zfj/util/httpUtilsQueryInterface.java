package com.zfj.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class httpUtilsQueryInterface {

    String name;
    Integer age;

    public static void main(String[] args) throws IOException {
        String s="A0338Q532," +
                 "A0338Z100," +
                 "A0338Z103," +
                 "A0338Q503," +
                 "A0338Z015," +
                 "A0338Z102," +
                 "A0338Q530," +
                 "A0338Q502," +
                 "A0338Q519," +
                 "A0338Q529," +
                 "A0338Q531," +
                 "A0338Q576," +
                 "A0338Z012," +
                 "A0338Z013";
        System.out.println(getValue(s));
    }
    public httpUtilsQueryInterface(){
        String name;
        Integer age;
    }
    public httpUtilsQueryInterface(String name,Integer age){
        this.name=name;
        this.age=age;
    }

    public static String getUrlContent(String param) throws IOException {
        String strUrl = "http://dmp.ms.dev.jh:8101/dmp/TServiceInt/json/listAndDel";
        URL url = new URL(strUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Connection", "keep-alive");
        httpConn.setRequestProperty("Cookie", "JSESSIONID=ytDTO9lZBTNW1nVy2X6KiqVQnLmn0WENsbrwr-NLhU-1ePkv5kDy!-1330282337");
        httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        httpConn.connect();

//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpConn.getOutputStream()));
//        writer.write(param);
//        writer.close();

        DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
        out.writeBytes(param);
        out.flush();
        out.close();

//        InputStream in = httpConn.getInputStream();
//        byte[] bytes = readInputStream(in);
//        String s = new String(bytes, "utf-8");

        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"utf-8"));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) buffer.append(line);
        reader.close();

        httpConn.disconnect();
        return buffer.toString();
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
        }
        return bos.toByteArray();
    }

    public static String requestUtil(HashMap<String, String> map) throws UnsupportedEncodingException {
        StringBuffer param = new StringBuffer();
        Set set = map.keySet();
        for (Object obj : set) {
            param.append("&").append(obj.toString()).append("=").append(URLEncoder.encode(map.get(obj.toString()), "utf-8"));
        }
        return param.substring(1);
    }

    public static HashMap getParam(String value) throws UnsupportedEncodingException {
        HashMap<String, String> map = new HashMap();
        map.put("stmEnvIdrNameS", "");
        map.put("impltUnit", "");
        map.put("serviceId", "%" + value + "%");
        map.put("logicName", "");
        map.put("serviceNameCn", "");
        map.put("batchNo", "");
        map.put("batchNo2", "");
        map.put("dataStatus", "Y");
        map.put("serviceType", "");
        map.put("svcCmptId", "");
        map.put("page", "1");
        map.put("size", "15");
        return map;
    }

    public static String getValue(String content) throws IOException {
        StringBuffer value = new StringBuffer();
        String[] split = content.split(",");
        for (String s : split) {
            String urlContent = getUrlContent(requestUtil(getParam(s.trim())));
            HashMap map = JSON.parseObject(urlContent, HashMap.class);
            Object obj = map.get("rows");
            String json = JSONArray.toJSONString(obj);
            List<Map> rows = JSON.parseArray(json, Map.class);
            if(rows.isEmpty()) continue;
            for (Map row : rows) {
                value.append(row.get("serviceId")).append("-").append(row.get("serviceNameCn")).append("\n");
            }
        }
        return value.toString();
    }
}
