package com.zfj.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tools.ant.util.DateUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@WebServlet("/fileUploadAll")
public class fileUploadDev extends HttpServlet {
    private static final String path = "/usr/local/tomcat/";
    private static final String fileType = "file.txt";
    private static final String dataSource = "source.txt";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");
        String res = "";
        try {
            switch (type) {
                case "QueryInterface":
                    res = httpUtilsQueryInterface.getValue(request.getParameter("content")); break;
                case "upload": res = upload(request); break;
                case "dmlSqlByFile": res = dmlSqlByFile(request); break;
                case "fileRead": res = fileRead(); break;
                case "sql": String sign = request.getParameter("sign");
                    switch (sign) {
                        case "selSql": res = selSql(request); break;
                        case "exportInsert": res = createDmlSql(request); break;
                        case "dmlSql": res = dmlSql(request); break;
                    }
                    break;
                case "fileWriter": res = fileWriter(request); break;
                case "clearFile": res = clearFile(); break;
                case "selDataSource": res = selDataSource(); break;
                case "selFileList": res = selFileList(request); break;
                case "selPathList": res = selPathList(request); break;
                case "fileDownLoad":
                    String filePath = request.getParameter("filePath");
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        File inFile = new File(filePath);
                        if (!inFile.exists()) {
                            response.setContentType("text/html");
                            response.getWriter().print("下载错误:文件不存在");
                            return;
                        }
                        in = new BufferedInputStream(new FileInputStream(inFile));
                        response.setContentType("multipart/form-data");
                        response.setHeader("Content-disposition", "attachment;filename=" + inFile.getName());
                        out = response.getOutputStream();
                        int len = 0;
                        byte[] bytes = new byte[1024 * 10];
                        while ((len = in.read(bytes)) != -1) {
                            out.write(bytes, 0, len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) in.close();
                        if (out != null) out.close();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(res);
    }

    private String upload(HttpServletRequest request) {
        String savePath = request.getParameter("uploadUrl");
        File f = new File(savePath);
        if (!f.exists()) f.mkdirs();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);// 设置缓冲,这个值决定了是fileinputstream还是bytearrayinputstream
//        factory.setRepository(new File(savePath+"/tmp"));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("utf-8");
        List<FileItem> fileList = null;
        try {
            fileList = upload.parseRequest(request);
            if (fileList == null || fileList.size() == 0) {
                return "upload fail";
            }
        } catch (FileUploadException ex) {
            return ex.getMessage();
        }
        String name = "";
        String extName = "";
        StringBuffer str = new StringBuffer();
        Iterator<FileItem> it = fileList.iterator();
        while (it.hasNext()) {
            FileItem item = it.next();
            if (!item.isFormField()) {
                name = item.getName();
                if (name.lastIndexOf(".") >= 0) extName = name;
                if (name == null || name.trim().equals("")) continue;
                File file = null;
                do {
                    name = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    file = new File(savePath + "/" + extName + "." + name);
                } while (file.exists());
                File saveFile = new File(savePath + "/" + extName + "." + name);
                try {
                    item.write(saveFile);
                    str.append("file upload success,").append(extName).append(".").append(name).append("</br>");
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }
        return str.toString();
    }

    private String fileRead() throws IOException {
        File file = new File(path + fileType);
        StringBuilder content = new StringBuilder();
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) content.append(tempStr).append("\r\n");
            reader.close();
        }
        return content.toString();
    }

    private String fileWriter(HttpServletRequest request) throws IOException {
        String sign = request.getParameter("sign");
        String content = request.getParameter("content");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = "";
        if (sign.equals("dataSource")) {
            filePath = path + dataSource;
            String[] split = content.trim().split(";");
            content = "";
            for (String s : split) {
                if (!s.trim().equals("")) content += s.trim() + "\r\n";
            }
            content = content.trim();
        } else {
            content = content + "\r\n";
            filePath = path + fileType;
        }
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(filePath, true);
        try {
            fileWriter.write(content);
        } catch (Exception e) {
            return "file write fail";
        } finally {
            fileWriter.close();
        }
        return "file write success";
    }

    private String clearFile() throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileWriter fileWriter = new FileWriter(path + fileType);
        try {
            fileWriter.write("");
            fileWriter.flush();
        } catch (Exception e) {
            return "clear file fail";
        } finally {
            fileWriter.close();
        }
        return "clear file success";
    }

    private String selSql(HttpServletRequest request) throws SQLException, UnsupportedEncodingException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String[] dataSources = request.getParameter("dataSource").split("\\|@\\|");
        StringBuilder sbf = new StringBuilder();
        boolean sign = true;
        String[] split = request.getParameter("content").split(";");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dataSources[1], dataSources[2], dataSources[3]);
            for (String sql : split) {
                sql = sql.trim();
                if (sql.equals("")) continue;
                if (!sql.contains("limit")) sql += " limit 200";
                List<Map<String, Object>> list = new ArrayList<>();
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                fileUploadDev[] headers = new fileUploadDev[columnCount + 1];
                for (int i = 1; i <= columnCount; i++) {
                    fileUploadDev header = new fileUploadDev();
                    if (sign) {
                        header.setHeaderName(rsmd.getColumnLabel(i).toLowerCase());
                        header.setHeaderLen(rsmd.getColumnLabel(i).getBytes("GBK").length);
                        headers[i] = header;
                    }
                }
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String RR1 = rs.getString(i);
                        RR1 = RR1 == null ? "" : RR1;
                        fileUploadDev header = new fileUploadDev();
                        int len = "".equals(RR1) ? 0 : RR1.getBytes("GBK").length;
                        if (headers[i].getHeaderLen() < len) {
                            header.setHeaderName(headers[i].getHeaderName());
                            header.setHeaderLen(len);
                            headers[i] = header;
                        }
                        map.put(rsmd.getColumnLabel(i).toLowerCase(), RR1);
                    }
                    list.add(map);
                    sign = false;
                }
                if (!list.isEmpty()) {
                    int count = 1;
                    for (Map<String, Object> dataMap : list) {
                        if (!sign) {
                            sbf.append("|---");
                            for (int i = 1; i < headers.length; i++) {
                                sbf.append("|").append(headers[i].getHeaderName());
                                int len = headers[i].getHeaderLen() - headers[i].getHeaderName().length();
                                for (int j = 0; j < len; j++) sbf.append("-");
                            }
                            sbf.append("|\n");
                            sign = true;
                        }
                        sbf.append("|").append(count);
                        for (int j = 0; j < 3 - (String.valueOf(count).length()); j++) sbf.append("-");
                        for (int i = 1; i < headers.length; i++) {
                            String data = (String) dataMap.get(headers[i].getHeaderName());
                            data = data == null ? "" : data;
                            sbf.append("|").append(data);
                            int len = headers[i].getHeaderLen() - data.getBytes("GBK").length;
                            for (int j = 0; j < len; j++) sbf.append("-");
                        }
                        sbf.append("|\n");
                        count++;
                    }
                } else {
                    for (int i = 1; i < headers.length; i++) {
                        sbf.append("|").append(headers[i].getHeaderName());
                    }
                    sbf.append("|\n");
                }
                if (!sbf.toString().equals("")) sbf.append("\n\n");
            }
        } catch (ClassNotFoundException | SQLException e) {
            sbf.append(e.getMessage());
        } finally {
            if (conn != null) conn.close();
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }
        return sbf.toString().equals("") ? "查询数据为空" : sbf.toString();
    }

    private String selDataSource() throws IOException {
        File file = new File(path + dataSource);
        StringBuilder content = new StringBuilder();
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String[] split = tempStr.split("\\|@\\|");
                content.append("<option value=\"").append(tempStr).append("\">").append(split[0]).append("</option>");
            }
            reader.close();
        }
        return content.toString();
    }

    private String selFileList(HttpServletRequest request) {
        String filePath = request.getParameter("filePath");
        String context = request.getContextPath();
        File file = new File(filePath);
        filePath = file.getPath().replaceAll("\\\\", "/");
        if (!file.exists()) {
            return "directory is not exist";
        }
        String[] list = file.list();
        assert list != null;
        Arrays.sort(list);
        StringBuilder fileListStr = new StringBuilder();
        StringBuilder directoryListStr = new StringBuilder();
        String parent = file.getParent();
        if (!"".equals(parent) && parent != null) {
            filePath+="/";
            parent = parent.replaceAll("\\\\", "/");
            if (!(parent).endsWith("/"))
                parent += "/";
            directoryListStr.append("<a onclick=\"submitBtn2('selFileList','','").append(parent).append("')\">").append("/..").append("</a></br>");
        }
        for (String fileName : list) {
            File file1 = new File(filePath + fileName);
            if (!file1.isDirectory()) {
//                    String[] split = fileName.split("\\.");
//                    String suffix = split[split.length - 1];
//                    String[] suffixs = {"jsp", "html", "htm", "swp", "db", "hbf", "inc"};
//                    int k = 0;
//                    for (String s : suffixs) {
//                        if (suffix.equalsIgnoreCase(s))
//                            k++;
//                    }
//                    if (k == 0)
                fileListStr.append("<a href=\"").append(context).append("/fileUploadAll?filePath=").append(filePath).append(fileName).append("&type=fileDownLoad").append("\">").append(fileName).append("</a></br>");
            } else {
                directoryListStr.append("<a onclick=\"submitBtn2('selFileList','','").append(filePath).append(fileName).append("/')\">/").append(fileName).append("</a></br>");
            }
        }
        return directoryListStr + fileListStr.toString();
    }

    private String createDmlSql(HttpServletRequest request) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String[] dataSources = request.getParameter("dataSource").split("\\|@\\|");
        String[] split = request.getParameter("content").split(";");
        String tableName = "";
        int row = 0;
        StringBuilder msg = new StringBuilder();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dataSources[1], dataSources[2], dataSources[3]);
            for (String sql : split) {
                sql = sql.trim();
                if (sql.equals("")) continue;
                StringBuilder inserts = new StringBuilder();
                StringBuilder updates = new StringBuilder();
                boolean sign = true;
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                tableName = rsmd.getTableName(1);
                ResultSet keys = conn.getMetaData().getPrimaryKeys(null, "%", tableName);
                HashSet<String> set = new HashSet<>();
                while (keys.next()) {
                    String primaryKey = keys.getString("column_name");
                    set.add(primaryKey);
                }
                StringBuilder values = new StringBuilder();
                while (rs.next()) {
                    StringBuilder updateMain = new StringBuilder();
                    StringBuilder insertMain = new StringBuilder();
                    StringBuilder where = new StringBuilder();
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        String value = getColumnValue(rsmd.getColumnName(i), rsmd.getColumnType(i), i, rs);
                        updateMain.append(",").append(rsmd.getColumnName(i).toLowerCase()).append("=").append(value);
                        insertMain.append(",").append(value);
                        if (sign) values.append(",").append(rsmd.getColumnName(i).toLowerCase());
                        for (Object o : set) {
                            String primaryKey = o.toString();
                            if (primaryKey.equalsIgnoreCase(rsmd.getColumnName(i))) {
                                where.append("and ").append(rsmd.getColumnName(i).toLowerCase()).append("=").append(value);
                            }
                        }
                    }
                    sign = false;
                    inserts.append("insert into ").append(tableName).append("(").append(values.substring(1)).append(") ").append(" values ").append("(").append(insertMain.substring(1)).append(");").append("\n");
                    updates.append("update ").append(tableName).append(" set ").append(updateMain.substring(1)).append(" where ").append(where.toString().equals("") ? "" : where.substring(3)).append(";").append("\n");
                    row++;
                }
                if (row < 50) {
                    msg.append("----------").append(tableName).append("-----------insert-----------\n");
                    msg.append(inserts).append("\n");
                    msg.append("----------").append(tableName).append("-----------update-----------\n");
                    msg.append(updates).append("\n\n");
                }
                createFile(path + "insert/", inserts.toString(), tableName, ".sql");
            }
        } catch (ClassNotFoundException | IOException e) {
            return e.getMessage();
        } finally {
            if (conn != null) conn.close();
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }
        return "导出成功,文件路径" + path + "insert/" + "\n" + msg;
    }

    private String dmlSql(HttpServletRequest request) throws SQLException {
        String[] dataSources = request.getParameter("dataSource").split("\\|@\\|");
        String[] sqls = request.getParameter("content").split(";");
        Connection conn = null;
        PreparedStatement stmt = null;
        StringBuilder msg = new StringBuilder();
        int count = 0, success = 0, fail = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dataSources[1], dataSources[2], dataSources[3]);
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i].trim();
                if (sql.equals("")) continue;
                try {
                    count++;
                    stmt = conn.prepareStatement(sql);
                    int row = stmt.executeUpdate();
                    if (row > 0) success++;
                } catch (SQLException e) {
                    fail++;
                    msg.append("第").append(i + 1).append("条sql执行失败!--").append(e.getMessage()).append("\n");
                }
            }
        } catch (ClassNotFoundException e) {
            return e.getMessage();
        } finally {
            if (conn != null) conn.close();
            if (stmt != null) stmt.close();
        }
        String message = "总共执行" + count + "条sql,成功:" + success + ",失败:" + fail + "\r\n";
        return message + msg.toString();
    }

    private String dmlSqlByFile(HttpServletRequest request) throws SQLException {
        String[] dataSources = request.getParameter("dataSource").split("/@/");
        Connection conn = null;
        PreparedStatement stmt = null;
        File f = new File(path + "temp");
        if (!f.exists()) f.mkdirs();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(4096);
        factory.setRepository(new File(path + "temp"));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        StringBuilder message = new StringBuilder();
        try {
            List<FileItem> fileItems = upload.parseRequest(request);
            List<fileUploadDev> mapList = new ArrayList<>();
            List<String> sqlList = null;
            String tableName = "";
            for (FileItem fileItem : fileItems) {
                tableName = fileItem.getName();
                byte[] bytes = readInputStream(fileItem.getInputStream());
                String[] sqls = new String(bytes,"utf-8").split(";");
                sqlList = new ArrayList<>(Arrays.asList(sqls));
            }
            mapList.add(SqlObject(tableName, sqlList));
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(dataSources[1], dataSources[2], dataSources[3]);
                for (fileUploadDev map : mapList) {
                    int success = 0, fail = 0 , count = 0;
                    StringBuilder msg = new StringBuilder();
                    for (String sql : map.getSqlList()) {
                        sql = sql.trim();
                        if (sql.equals("")) continue;
                        try {
                            count ++;
                            stmt = conn.prepareStatement(sql);
                            int row = stmt.executeUpdate();
                            if (row > 0) success++;
                        } catch (SQLException e) {
                            fail++;
                            msg.append("第").append(count + 1).append("条sql执行失败!失败sql:").append(e.getMessage()).append("\n");
                        }
                    }
                    message.append("文件").append(map.getTableName()).append("执行完毕;总共执行"+count+"条sql,成功:").append(success).append("条;失败:").append(fail).append("条\n");
                    if (count != 0) {
                        createFile(path + "error/", msg.toString(), map.getTableName(), ".error");
                        message.append("错误日志路径," + path + "error/").append(map.getTableName()).append(".error");
                    }
                }
            } catch (ClassNotFoundException e) {
                return e.getMessage();
            } finally {
                if (conn != null) conn.close();
                if (stmt != null) stmt.close();
            }
        } catch (FileUploadException | IOException e) {
            return e.getMessage();
        }
        return message.toString();
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

    public static fileUploadDev SqlObject(String tableName, List<String> sqlList) {
        fileUploadDev sqlMap = new fileUploadDev();
        sqlMap.setTableName(tableName);
        sqlMap.setSqlList(sqlList);
        return sqlMap;
    }

    public static void main(String[] args) throws SQLException, IOException {
        String sql = "select * from pt_txattr limit 10";
        selSqlTest(sql);
//        createDmlSqlTest(sql);
//        String sql1 = "update pt_txattr set txname='交易痕迹查询' where txcode = 'M00101';";
//        dmlTest(sql);
//        selSqlTest(sql);
//        fileUploadDev dev = new fileUploadDev();
//        System.out.println(dev.selDataSource());
    }

    public static void selSqlTest(String sqls) throws SQLException, UnsupportedEncodingException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String[] dataSources = "ccst1|@|jdbc:mysql://128.195.71.127:4000/CCST1|@|CCSVC_TRAN|@|CCSVC_TRAN".split("\\|@\\|");
        StringBuffer sbf = new StringBuffer();
        boolean sign = true;
        String[] split = sqls.split(";");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dataSources[1], dataSources[2], dataSources[3]);
            for (String sql : split) {
                if (sql.equals("")) continue;
                List<Map<String, Object>> list = new ArrayList();
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                fileUploadDev[] headers = new fileUploadDev[columnCount + 1];
                fileUploadDev header = null;
                for (int i = 1; i <= columnCount; i++) {
                    if (sign) {
                        header = new fileUploadDev();
                        header.setHeaderName(rsmd.getColumnLabel(i).toLowerCase());
                        header.setHeaderLen(rsmd.getColumnLabel(i).getBytes("GBK").length);
                        headers[i] = header;
                    }
                }
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String RR1 = rs.getString(i);
                        header = new fileUploadDev();
                        if (RR1 != null) RR1 = RR1.trim();
                        int len = RR1 == null ? 0 : RR1.getBytes("GBK").length;
                        if (headers[i].getHeaderLen() < len) {
                            header.setHeaderName(headers[i].getHeaderName());
                            header.setHeaderLen(len);
                            headers[i] = header;
                        }
                        map.put(rsmd.getColumnLabel(i).toLowerCase(), RR1);
                    }
                    list.add(map);
                }
                sign = false;
                if (!list.isEmpty()) {
                    int count = 1;
                    for (Map<String, Object> dataMap : list) {
                        if (!sign) {
                            sbf.append("|").append("---");
                            for (int i = 1; i < headers.length; i++) {
                                sbf.append("|").append(headers[i].getHeaderName());
                                int len = headers[i].getHeaderLen() - headers[i].getHeaderName().length();
                                for (int j = 0; j < len; j++) sbf.append("-");
                            }
                            sbf.append("|\n");
                            sign = true;
                        }
                        sbf.append("|").append(count);
                        for (int j = 0; j < 3 - (String.valueOf(count).length()); j++) sbf.append("-");
                        for (int i = 1; i < headers.length; i++) {
                            String data = (String) dataMap.get(headers[i].getHeaderName());
                            data = data == null ? "" : data;
                            sbf.append("|").append(data);
                            int len = headers[i].getHeaderLen() - data.getBytes("GBK").length;
                            for (int j = 0; j < len; j++) sbf.append("-");
                        }
                        sbf.append("|\n");
                        count++;
                    }
                } else {
                    for (int i = 1; i < headers.length; i++) {
                        sbf.append("|").append(headers[i].getHeaderName());
                    }
                    sbf.append("|\n");
                }
                if (!sbf.toString().equals("")) sbf.append("\n\n");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            if (conn != null) conn.close();
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }
        System.out.println(sbf.toString().equals("") ? "查询数据为空" : sbf.toString());
    }

    private static String getColumnValue(String columnValue, int columnType, int i, ResultSet rs) throws SQLException {
        switch (columnType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.BIGINT:
                return rs.getLong(i) + "";
            case Types.DATE:
                return rs.getDate(i) == null ? null : "'" + DateUtils.format(rs.getDate(i), "yyyyMMdd") + "'";
            case Types.TIMESTAMP:
                return rs.getTimestamp(i) == null ? null : "'" + DateUtils.format(rs.getTimestamp(i), "yyyyMMddHHmmss") + "'";
            case Types.BOOLEAN:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.INTEGER:
                return rs.getInt(i) + "";
            case Types.DOUBLE:
                return rs.getDouble(i) + "";
            case Types.FLOAT:
                return rs.getFloat(i) + "";
            default:
                return rs.getString(i) == null ? null : "'" + rs.getString(i).trim() + "'";
        }
    }

    private static void createFile(String filePath, String str, String fileName, String stffx) throws IOException {
        if (!str.equals("")) {
            FileWriter fileWriter = null;
            try {
                File f = new File(filePath);
                if (!f.exists()) f.mkdirs();
                fileName = filePath + fileName + stffx;
                File file = new File(fileName);
                if (file.exists()) file.delete();
                file.createNewFile();
                fileWriter = new FileWriter(fileName, true);
                fileWriter.write(str);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                if (fileWriter != null) fileWriter.close();
            }
        }
    }

    private static String selPathList(HttpServletRequest request) {
        String filePath = request.getParameter("filePath");
        File file = new File(filePath);
        filePath = file.getPath().replaceAll("\\\\", "/")+"/";
        String[] list = file.list();
        assert list != null;
        StringBuffer pathList = new StringBuffer();
        for (String fileName : list) {
            File file1 = new File(filePath + fileName);
            if (file1.isDirectory()) {
                pathList.append("<option value = \"" + fileName + "/\">" + fileName + "</option>");
            }
        }
        return pathList.toString();
    }

    private String tableName;
    private List<String> sqlList;

    private String headerName;
    private Integer headerLen;

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public Integer getHeaderLen() {
        return headerLen;
    }

    public void setHeaderLen(Integer headerLen) {
        this.headerLen = headerLen;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

}
