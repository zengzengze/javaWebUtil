package com.zfj.util;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


@WebServlet("/imageCompareUtil")
public class imageCompareUtil extends HttpServlet {

    private static final String path = "H:/zfj/Code/ms/qywy-static/ccvec-p1web-conf/WebResources_B2BV6/WebResources/V6/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String type = request.getParameter("type");
            System.out.println("type----------------" + type);
            String res = "";
            if (type == null) {
                ImportExcl(request);
                response.sendRedirect("/javaWebUtil/imageCompareUtil.jsp");
            } else if (type.equals("delImg")) {
                res = delFIle(request);
            }
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
//        ImportExcl();
        HashMap[] maps = new HashMap[2];
        HashMap map = new HashMap();
        map.put("test", "test");
        maps[0] = map;
        System.out.println(maps.length);
        for (int i = 0; i < maps.length; i++) {
            System.out.println(maps[i] == null);
        }
    }

    public static void ImportExcl(HttpServletRequest request) throws Exception {
        String xlsxPath = "H:/zfj/蒙商/IMG.xlsx";
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(xlsxPath));
        XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);
        int maxRow = sheetAt.getLastRowNum();
        HashMap[] maps = new HashMap[maxRow];
        int count = 0;
        a:for (int row = 0; row < maxRow; row++) {
            int maxRol = sheetAt.getRow(row).getLastCellNum();
            HashMap map = new HashMap();
            for (int col = 0; col < maxRol; col++) {
                String s = sheetAt.getRow(row).getCell(col).toString();
                if (col == 0) {
                    File file = new File(path + s);
                    if (!file.exists()) continue a;
                }
                map.put(col, s);
            }
            maps[count] = map;
            count++;
        }
        request.getSession().setAttribute("data", maps);
    }

    public static String delFIle(HttpServletRequest request) {
        String imgName = request.getParameter("imgName");
        File file = new File(path + imgName);
        boolean delete = file.delete();
        if (delete) {
            return "success";
        }
        return "fail";
    }
}
