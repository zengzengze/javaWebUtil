package com.zfj.util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebServlet("/httpUtils")
public class httpUtils extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String res = getUrlContent(requestUtil(request));
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(res);
    }

    public static String requestUtil(HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuffer param = new StringBuffer();
        Map<String,String[]> map = request.getParameterMap();
        Set set = map.keySet();
        for (Object obj : set) {
            param.append("&").append(obj.toString()).append("=").append(URLEncoder.encode(map.get(obj.toString())[0], "utf-8"));
        }
        return param.substring(1);
    }

    public static String getUrlContent(String param) throws IOException {
        String strUrl = "http://www.ccsht-pl1.msbank.deev:9001/svcManager/fileUploadAll";
        URL url = new URL(strUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Connection", "keep-alive");
        httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        httpConn.connect();

//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpConn.getOutputStream()));
//        writer.write(param);
//        writer.close();

        DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
        out.writeBytes(param);
        out.flush();
        out.close();

        InputStream in = httpConn.getInputStream();
        byte[] bytes = readInputStream(in);
        String s = new String(bytes, "utf-8");

//        BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"utf-8"));
//        String line;
//        StringBuffer buffer = new StringBuffer();
//        while ((line = reader.readLine()) != null) buffer.append(line);
//        reader.close();

        httpConn.disconnect();
        return s;
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
}

