<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String contextPath = request.getContextPath();
    String server_name = request.getServerName();
    String server_port = String.valueOf(request.getServerPort());
    String url = "http://" + server_name + ":" + server_port + contextPath + "/";
%>
<html>
<head>
    <title>P1文件上传工具</title>
    <link rel="stylesheet" href="<%=contextPath%>/Css/jh.css" type="text/css">
    <link rel="stylesheet" href="<%=contextPath%>/Css/ccb.css" type="text/css">
    <script language="JavaScript" src="<%=contextPath%>/common/jquery-1.7.2.min.js"></script>
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<div style="display: flex">
    <div>
        <table width="650px" border="0" cellspacing="0" cellpadding="4">
            <tr>
                <td>
                    <table class="main">
                        <tr>
                            <td class="Table_H1"> 图片:</td>
                            <td class="Table_H2">
                            <td class="Table_H2">
                                <div style="display: flex">
                                    <input id="pic_addr" name="pic_addr" value=""/>
                                    <img class="uploadImg" src="" alt=""/>
                                    <div class="upload">新增</div>
                                    <p class="deleteImgBtn" style="margin-right: 10px; display: none">删除</p>
                                </div>
                                <span style="color: red">*只可上传一张图</span>
                            </td>
                        </tr>
                    </table>
                    <form name="jhform" hidden method="post" id="uploadForm" enctype="multipart/form-data">
                        <input id="uploadImg" type="file" name="file_en"/>
                    </form>
                </td>
            </tr>
        </table>
        <input id="url" type="hidden" value="<%=url%>">
    </div>
    <iframe>
        <html>
        <head></head>
        <body>
        <span><a style="TEXT-DECORATION: none; color:#900" href="#" onclick="uassLogin()">测试</a></span>

        </body>
        </html>
    </iframe>
</div>
</body>
<script language="JavaScript" src="<%=contextPath%>/js/kh.js"></script>
<style>
    .btnMain button {
        margin: 5px 0px;
        width: 50px;
    }

    .main {
        star: expression(this.align='center');
        border-collapse: collapse;
        margin: 5px;
        width: 95%;
        font-family: "宋体", "Arial";
        font-size: 12px;
    }

    .table_h1 {
        font-family: "宋体", "Arial";
        font-size: 12px;
        background-color: #D5DFF4;
        border: 1px #000000 solid;
        width: 20%;
        white-space: nowrap;
        padding: 6px;
        text-align: right;
    }

    .table_h2 {
        font-family: "宋体", "Arial";
        font-size: 12px;
        border: 1px #000000 solid;
        width: 30%;
    }

    input {
        BORDER-BOTTOM-COLOR: #cccccc;
        BORDER-BOTTOM-WIDTH: 1px;
        BORDER-LEFT-COLOR: #cccccc;
        BORDER-LEFT-WIDTH: 1px;
        BORDER-RIGHT-COLOR: #cccccc;
        BORDER-RIGHT-WIDTH: 1px;
        BORDER-TOP-COLOR: #cccccc;
        BORDER-TOP-WIDTH: 1px;
        FONT-SIZE: 9pt;
        HEIGHT: 23px;
        PADDING-BOTTOM: 1px;
        PADDING-LEFT: 1px;
        PADDING-RIGHT: 1px;
        PADDING-TOP: 1px;
    }

    select {
        BACKGROUND-COLOR: #efefef;
        BORDER-BOTTOM-COLOR: #000000;
        BORDER-BOTTOM-WIDTH: 1px;
        BORDER-LEFT-COLOR: #000000;
        BORDER-LEFT-WIDTH: 1px;
        BORDER-RIGHT-COLOR: #000000;
        BORDER-RIGHT-WIDTH: 1px;
        BORDER-TOP-COLOR: #000000;
        BORDER-TOP-WIDTH: 1px;
        FONT-SIZE: 9pt;
    }

    .none {
        display: none;
    }

    .upload {
        width: 50px;
        height: 50px;
        margin-right: 10px;
    <%--background-image: url(<%=Parameter.WEB_URL%>/images/plus.png);--%>
    }

    img {
        margin-right: 10px;
    }
</style>
</html>
