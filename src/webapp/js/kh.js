var uploads = document.getElementsByClassName("upload");
var deleteImgBtns = document.getElementsByClassName("deleteImgBtn");
var inputImages = ['#uploadImg'];
// var fileTypes = document.getElementById("imageType").value.split("@");
var fileTypes = ['png', 'jpg', 'jpeg']
for (let i = 0; i < uploads.length; i++) {
    uploads[i].onclick = function () {
        $(inputImages[i]).trigger('click')
        document.querySelector(inputImages[i]).addEventListener("change", function () {
            let file = $(inputImages[i])[0].files[0]
            let fileList = file.name.split('.');
            let fileType = fileList[fileList.length - 1];
            let sign = 0;
            let imageType = '';
            for (let j = 0; j < fileTypes.length; j++) {
                imageType += "、" + fileTypes[j]
                if (fileType.toUpperCase() !== fileTypes[j].toUpperCase()) sign++;
            }

            if (sign === fileTypes.length) {
                $(inputImages[i])[0].outerHTML = $(inputImages[i])[0].outerHTML
                alert("不支持的图片类型" + fileType + "，请选择后缀为" + imageType.substr(1) + "图片");
                return false
            }
            ajaxUpload(file, i)
            // showImg(file,i)
        })
    }
}

for (let i = 0; i < deleteImgBtns.length; i++) {
    deleteImgBtns[i].onclick = function () {
        let showUploadFile = $(".uploadImg")[i];
        showUploadFile.src = '';
        showUploadFile.style.display = 'none'
        $(inputImages[i])[0].value = ''
        deleteImgBtns[i].style.display = 'none';
        uploads[i].style.display = '';
        document.getElementsByName("pic_addr")[0].value = ''
    }
}

function showImg(file, index) {
    if (undefined != file) {
        let reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function (result) {
            let showUploadFile = $(".uploadImg")[index];
            showUploadFile.src = result.target.result;
            showUploadFile.style.display = 'inline-block'
        }
        uploads[index].style.display = 'none';
        deleteImgBtns[index].style.display = '';
    }
}

function ajaxUpload(file, i) {
    let formData = new FormData($('#uploadForm')[0]);
    console.log(formData)
    console.log(file)
    let url = $('#url').val() + 'imageUpload'
    $.ajax({
        type: 'post',
        url: url,
        data: formData,
        async: false,
        contentType: false,
        processData: false,
        success: function (res) {
            if (res === 'fail') {
                alert("图片上传失败")
                return
            }
            alert("图片上传成功")
            showImg(file, i)
            document.getElementsByName("pic_addr")[0].value = res
        }
    })
}