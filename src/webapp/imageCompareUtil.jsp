<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String contextPath = request.getContextPath();
    String server_name = request.getServerName();
    String server_port = String.valueOf(request.getServerPort());
    String url = "http://" + server_name + ":" + server_port + contextPath + "/";
    HashMap[] maps = (HashMap[]) request.getSession().getAttribute("data");
%>
<html>
<head>
    <title>imageCompareUtil</title>
    <link rel="stylesheet" href="<%=contextPath%>/Css/jh.css" type="text/css">
    <link rel="stylesheet" href="<%=contextPath%>/Css/ccb.css" type="text/css">
    <script language="JavaScript" src="<%=contextPath%>/common/jquery-1.7.2.min.js"></script>
    <script>
        let url = '<%=url%>' + 'imageCompareUtil';

        function openImgBig(e) {
            let value = e.parentNode.parentNode.parentNode.children[0].children[1].value;
            $('#imgBigDiv').empty()
            $('#imgBigDiv').append("<img style=\"height: 200px\" src=\"/imgPath/" + value + "\"><div>" + value + "</div>")
        }

        function delImg(e) {
            let imgName = e.parentNode.children[1].value;
            if (confirm("是否删除所选图片!")) {
                $.ajax({
                    type: 'post',
                    url: url,
                    data: {type: 'delImg', imgName: imgName},
                    success: function (res) {
                        if (res === 'success') {
                            alert("delete image success")
                            e.parentNode.parentNode.remove()
                        }
                    }
                })
            }
        }
    </script>
</head>
<body>
<main style="display: flex">
    <div style="width: 600px;overflow: hidden">
        <ul>
            <%
                int length = maps.length;
                for (int i = 0; i < length; i++) {
                    if (maps[i] == null) break;
                    HashMap map = maps[i];
                    Set set = map.keySet();
                    out.print("<li><div><span onclick=\"delImg(this)\">删除</span> " +
                            "<input type=\"hidden\" name=\"path\" value=\"" + map.get(0) + "\">" +
                            "</div>");
                    for (int j = 0; j < set.size(); j++) {
                        if (j == 0) {
                            out.print("<div><div><img src=\"/imgPath/" + map.get(j) + "\" onclick=\"openImgBig(this)\"></div><span>");
                        } else {
                            out.print(map.get(j) + " ");
                        }
                    }
                    out.print("</span></div></li>");
                }
            %>
        </ul>
    </div>
    <div id="imgBigDiv">

    </div>
</main>
</body>
<style>
    ul li {
        list-style-type: none;
        padding: 5px 0px;
        display: flex;
    }

    img {
        height: 40px;
    }

    #imgBigDiv {
        width: 800px;
        overflow: hidden;
        position: fixed;
        right: 0px;
    }
</style>
</html>
