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
<script>
    let url = '<%=url%>' + 'fileUploadAll';
    function selProjectUrl() {
        let i = $('#projectUrl option:selected').val();
        ajaxSelPathList('selPathList', 1, projectUrls[i].projectUrl)
    }
    function deleteFile(e) {
        e.parentNode.parentNode.parentNode.remove()
    }
    function patchAddFile() {
        let upload = $('.upload')
        let len = upload.length
        let str = '<tr class=\"upload\" >' +
            '<td class=\"table_h1\"> 选择文件:</td>' +
            '<td class=\"table_h2\">' +
            '<input type=\"file\" class=\"file\" name=\"file' + len + '\"/>' +
            '</td>' +
            '<td class=\"table_h2\" >' +
            '<div align=\"center\" style=\"width: 80px\">' +
            '<img src=\"/images/big_minus.gif\" onclick=\"deleteFile(this)\"/>' +
            '</div>' +
            '</td>'
        $(str).insertAfter(upload[len - 1])
    }
    function submitBtn() {
        document.getElementById('msg').innerHTML =''
        let formData = new FormData($('#uploadForm')[0]);
        let url = '<%=url%>' + 'fileUploadAll' + '?uploadUrl=' + $("#uploadUrlAll").val() + '&type=upload'
        let files = document.getElementsByClassName("file")
        for (let i = 0; i < files.length; i++) {
            if (files[i].value == null || files[i].value == "") {
                alert("请选择需要上传的文件!");return;
            }
        }
        $.ajax({
            type: 'post',
            url: url,
            data: formData,
            async: false,
            contentType: false,
            processData: false,
            success: function (res) {
                $('#msg').append(res)
                for (let i = files.length - 1; i >= 0; i--) {
                    files[i].value = '';
                    if (i === 0) return;
                    document.getElementsByClassName("upload")[i].remove()
                }
            }
        })
    }
    function submitBtn2(type, sign, fileName) {
        let param = {type: type, sign: sign, fileName: fileName};
        if (type === 'fileWriter') {
            if (sign === 'addText') param.content = $('#fileContent').val();
            if (sign === 'addSql') param.content = $('#sqlContent').val()+'\n';
            if (sign === 'dataSource') param.content = $('#source').val();
        } else if (type === 'sql' || type === 'QueryInterface') {
            document.getElementById("fileContent").value = ''
            param.content = $('#sqlContent').val()
            param.dataSource = $('#dataSourceList option:selected').val()
        } else if (type === 'clearFile') {
            if (sign === 'canalSql') {
                document.getElementById("sqlContent").value = '';
                return;
            } else if (sign === 'canalText') {
                document.getElementById("fileContent").value = '';
                return;
            } else if (sign === 'canalFile') document.getElementById("fileContent").value = ''
        } else if (type === 'selFileList') {
            param.filePath = $('#uploadUrlAll').val();
            if(fileName!==''){
                param.filePath=fileName;
                document.getElementById("uploadUrlAll").value=fileName;
            }
        }
        $.ajax({
            type: 'post',
            url: url,
            data: param,
            success: function (res) {
                if (type === 'fileRead' || type === 'sql' || type === 'QueryInterface') {
                    document.getElementById("fileContent").value = res.toString()
                    return;
                } else if (type === 'selFileList') {
                    $('#fileList').empty().append(res)
                    return;
                }
                if (sign === 'dataSource') {
                    selDataSource('selDataSource')
                    return;
                }
                document.getElementById("msg").innerText = res
                submitBtn2('fileRead', 'selFile', fileName)
            }
        })
    }
    function submitBtnDmlSqlByFile() {
        document.getElementById("fileContent").value = '';
        let formData = new FormData($('#dmlSqlByFile')[0]);
        let url = '<%=url%>' + 'fileUploadAll' + '?type=dmlSqlByFile&dataSource='+($('#dataSourceList option:selected').val().replaceAll('|@|','/@/'))
        let files = document.getElementsByClassName("dmlSqlFile")
        for (let i = 0; i < files.length; i++) {
            if (files[i].value == null || files[i].value == "") {
                alert("请选择需要执行的文件!")
                return
            }
        }
        $.ajax({
            type: 'post',
            url: url,
            data: formData,
            async: false,
            contentType: false,
            processData: false,
            success: function (res) {
                document.getElementById("fileContent").value = res
                for (let i = 0; i < files.length; i++) {
                    files[i].value = ''
                }
            }
        })
    }
    function selDataSource(type) {
        $.ajax({
            type: 'post',
            url: url,
            data: {type: type},
            success: function (res) {
                if (res != null && res !== "") {
                    $('#dataSourceList').empty().append(res)
                }
            }
        })
    }
    function selPathList(type, index) {
        let i = $('#projectUrl option:selected').val();
        let filePath = projectUrls[i].projectUrl;
        index = index === '' ? $('#pathList')[0].children.length : index;
        for (let i = 0; i < index; i++) {
            let key = 'selPath' + (i + 1)
            let path = $('#' + key + ' option:selected').val();
            filePath += path;
        }
        ajaxSelPathList(type, index + 1, filePath)
    }
    function ajaxSelPathList(type, index, filePath) {
        let pathList = $('#pathList')[0];
        $.ajax({
            type: 'post',
            url: url,
            data: {type: type, filePath: filePath},
            success: function (res) {
                let len = pathList.children.length;
                for (let i = index; i < (len + 1); i++) {
                    pathList.removeChild($('#selPath'+i)[0])
                }
                if (res != null && res !== "") {
                    res = "<select id=\"selPath" + index + "\" onchange=\"selPathList('selPathList'," + index + ")\">" + res + "</select>";
                    $('#pathList').append(res)
                }
                let lastPath = $('#selPath' + index + ' option:selected').val();
                if (lastPath !== undefined) filePath += lastPath;
                document.jhform[0].uploadUrlAll.value = filePath
            }
        })
    }
</script>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<div style="display: flex">
    <div>
        <table width="650px" border="0" cellspacing="0" cellpadding="4">
            <tr>
                <td>
                    <form name="jhform" method="post" id="uploadForm" enctype="multipart/form-data">
                        <table class="main">
                            <tr class="upload">
                                <td class="table_h1"> 选择文件:</td>
                                <td class="table_h2">
                                    <input type="file" class="file" name="file"/>
                                </td>
                                <td class="table_h2">
                                    <div align="center" style="width: 80px">
                                        <img src="/images/big_plus.gif" onclick="patchAddFile(this)"/>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td class="table_h1">选择项目路径:</td>
                                <td class="table_h2"><select id="projectUrl" onchange="selProjectUrl()"></select></td>
                                <td class="table_h2"></td>
                            </tr>
                            <tr>
                                <td class="table_h1">选择上传路径:</td>
                                <td class="table_h2" id="pathList"></td>
                                <td class="table_h2">
                                    <div align="center" class="btnMain" style="width: 80px">
                                        <button type="button" onclick="selPathList('selPathList','')" class="button">查找</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td class="table_h1">文件路径:</td>
                                <td class="table_h2"><input value="" style="width: 750px;" size="80" id="uploadUrlAll">
                                </td>
                                <td class="table_h2"></td>
                            </tr>
                            <tr>
                                <td class="table_h1">文件列表:</td>
                                <td class="table_h2">
                                    <div style="margin: 0 10px;max-height: 300px;overflow-y: auto" id="fileList"></div>
                                </td>
                                <td class="table_h2"></td>
                            </tr>
                        </table>
                    </form>
                    <table class="main">
                        <tr>
                            <td colspan="2" align="center">
                                <button onclick="submitBtn()" class="button">提交</button>
                                <button onclick="submitBtn2('selFileList','','')" class="button">查找</button>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <table width="650px" border="0" cellspacing="0" cellpadding="4">
            <tr>
                <td>
                    <form style="margin-bottom: -1px;" name="jhform" method="post" id="dmlSqlByFile" enctype="multipart/form-data">
                        <table class="main">
                            <tr>
                                <td class="table_h1">数据源:</td>
                                <td class="table_h2">
                                    <select id="dataSourceList"><option value="">请添加数据源</option></select>
                                    <input style="margin-left: 10px" type="file" class="dmlSqlFile" name="dmlSqlFile"/>
                                </td>
                                <td class="table_h2">
                                    <div align="center" class="btnMain" style="width: 80px">
                                        <button type="button" onclick="submitBtnDmlSqlByFile()" class="button">执行</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td class="table_h1">添加新数据源:</td>
                                <td class="table_h2">
                                    <input style="height:25px;width: 750px;" size="80" value="" id="source" placeholder="数据源命名|@|数据源|@|用户名|@|密码"/>
                                </td>
                                <td class="table_h2">
                                    <div align="center" class="btnMain" style="width: 80px">
                                        <button type="button" onclick="submitBtn2('fileWriter','dataSource')" class="button">添加</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td class="table_h1">sql:</td>
                                <td class="table_h2">
                                    <textarea id="sqlContent" style="min-width:750px;min-height:240px;background: #ffffff;width: 100%;height: 240px;"></textarea>
                                </td>
                                <td class="table_h2">
                                    <div align="center" class="btnMain" style="width: 80px">
                                        <button type="button" onclick="submitBtn2('sql','selSql')" class="button">查询</button>
                                        <button type="button" onclick="submitBtn2('sql','exportInsert')" class="button">导出</button>
                                        <button type="button" onclick="submitBtn2('sql','dmlSql')" class="button">dml</button>
                                        <button type="button" onclick="submitBtn2('QueryInterface','QueryInterface')" class="button">查找</button>
                                        <button type="button" onclick="submitBtn2('fileWriter','addSql','file')" class="button">添加</button>
                                        <button type="button" onclick="submitBtn2('clearFile','canalSql')" class="button">清空</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td class="table_h1">内容显示:</td>
                                <td class="table_h2"><textarea id="fileContent" style="min-width:750px;min-height: 400px;width: 100%;height: 400px;background: white"></textarea>
                                </td>
                                <td class="table_h2">
                                    <div align="center" class="btnMain" style="width: 80px">
                                        <button type="button" onclick="submitBtn2('fileRead','selFile','file')" class="button">查找</button>
                                        <button type="button" onclick="submitBtn2('fileWriter','addText','file')" class="button">添加</button>
                                        <button type="button" onclick="submitBtn2('clearFile','canalFile','file')" class="button">清除</button>
                                        <button type="button" onclick="submitBtn2('clearFile','canalText')" class="button">清空</button>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </form>
                </td>
            </tr>
        </table>
    </div>
    <div style="padding: 25px 5px;">
        <span id="msg" style="color: red;font-weight: bold"></span>
    </div>
</div>
</body>
<script>
    let projectUrls = [
        {projectUrl: '/usr/local/tomcat/webapps/svcManager/'},
        {projectUrl: '/usr/local/tomcat/webapps/elbManager/'}]

    for (let i = 0; i < projectUrls.length; i++) {
        $('<option value=\"' + i + '\">' + projectUrls[i].projectUrl + '</option>').appendTo($('#projectUrl'))
    }
    let i = $('#projectUrl option:selected').val();
    document.getElementById("uploadUrlAll").value = projectUrls[i].projectUrl
    selDataSource('selDataSource')
    ajaxSelPathList('selPathList', 1 , projectUrls[0].projectUrl)
</script>
<style>
    .btnMain button {
        margin: 5px 0px;
        width: 50px;
    }
    #pathList select{
        margin-right: 5px;
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
    input{
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
    a{
        cursor: pointer;
        line-height: 15px;
    }
</style>
</html>
