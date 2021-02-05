/**
 * 用户详情对话框
 */
var UserInfoDlg = {
    data: {
        deptId: "",
        deptName: ""
    }
};
var MgrUser = {
    tableId: "fileTable",    //表格id
    condition: {
        name: "",
        deptId: "",
        timeLimit: ""
    }
};

layui.use(['layer', 'form', 'admin', 'laydate', 'ax', 'upload'], function () {
    var $ = layui.jquery;
    var $ax = layui.ax;
    var form = layui.form;
    var admin = layui.admin;
    var laydate = layui.laydate;
    var layer = layui.layer;
    var upload = layui.upload;

    // 让当前iframe弹层高度适应
    admin.iframeAuto();

    // 点击部门时
    $('#deptName').click(function () {
        var formName = encodeURIComponent("parent.UserInfoDlg.data.deptName");
        var formId = encodeURIComponent("parent.UserInfoDlg.data.deptId");
        var treeUrl = encodeURIComponent("/hierarchy/tree");

        layer.open({
            type: 2,
            title: '层级选择',
            area: ['300px', '400px'],
            content: Feng.ctxPath + '/system/commonTree?formName=' + formName + "&formId=" + formId + "&treeUrl=" + treeUrl,
            end: function () {
                console.log(UserInfoDlg.data);
                $("#deptId").val(UserInfoDlg.data.deptId);
                $("#deptName").val(UserInfoDlg.data.deptName);
            }
        });
    });

    // 渲染时间选择框
    laydate.render({
        elem: '#birthday'
    });

    /**
     * 导入
     */
    //指定允许上传的文件类型
    upload.render({
        elem: '#importFile'
        , url: Feng.ctxPath + '/file/add'
        , auto: false
        , accept: 'file'
        // , exts: 'AVI|mov|rmvb|rm|FLV|mp4|3GP|json|csv|WAV|FLAC|APE|ALAC|cda|MP3|AAC|Opus|WMA|OGG' //普通文件
        , bindAction: '#saveFile'
        , data: {
            account: function () {
                return $('#account').val();
            },
            name: function () {
                return $('#name').val();
            },
            deptId: function () {
                return $('#deptId').val();
            }
            ,
            fileType: function () {
                return $('#fileType').val();
            }
        }
        , before: function (obj) { //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
            layer.load(); //上传loading
        }
        , done: function (res) {
            layer.closeAll('loading'); //关闭loading
            if (res.success) {
                //传给上个页面，刷新table用
                admin.putTempData('formOk', true);
                //关掉对话框
                admin.closeThisDialog();
            } else {
                layer.open({
                    title: '失败'
                    , content: res.message
                });
            }
            console.log(res);
        }
        , error: function (index, upload) {
            layer.closeAll('loading'); //关闭loading
            layer.open({
                title: '异常'
                , content: '请检查上传的文件是否符合规定'
            });
        }
    });
});