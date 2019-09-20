/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.royal.admin.modular.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.datascope.DataScope;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.RequestEmptyException;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.royal.admin.config.properties.GunsProperties;
import com.royal.admin.core.common.annotion.BussinessLog;
import com.royal.admin.core.common.annotion.Permission;
import com.royal.admin.core.common.constant.Const;
import com.royal.admin.core.common.constant.dictmap.UserDict;
import com.royal.admin.core.common.constant.factory.ConstantFactory;
import com.royal.admin.core.common.constant.state.ManagerStatus;
import com.royal.admin.core.common.exception.BizExceptionEnum;
import com.royal.admin.core.common.page.LayuiPageFactory;
import com.royal.admin.core.log.LogObjectHolder;
import com.royal.admin.core.shiro.ShiroKit;
import com.royal.admin.modular.system.entity.FilePath;
import com.royal.admin.modular.system.entity.User;
import com.royal.admin.modular.system.factory.UserFactory;
import com.royal.admin.modular.system.model.UserDto;
import com.royal.admin.modular.system.service.FileService;
import com.royal.admin.modular.system.service.UserService;
import com.royal.admin.modular.system.warpper.FileWrapper;
import com.royal.admin.modular.system.warpper.UserWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 系统管理员控制器
 *
 * @author fengshuonan
 * @Date 2017年1月11日 下午1:08:17
 */
@Controller
@RequestMapping("/file")
public class FileController extends BaseController {

    private static String PREFIX = "/modular/business/file/";

    @Autowired
    private FileService fileService;

    /**
     * 跳转到查看文件列表的页面
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:43
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "file.html";
    }

    /**
     * 查询文件列表
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:43
     */
    @RequestMapping("/list")
    @Permission
    @ResponseBody
    public Object list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String timeLimit,
                       @RequestParam(required = false) Long deptId) {

        //拼接查询条件
        String beginTime = "";
        String endTime = "";

        if (ToolUtil.isNotEmpty(timeLimit)) {
            String[] split = timeLimit.split(" - ");
            beginTime = split[0];
            endTime = split[1];
        }

        Page<Map<String, Object>> users = fileService.selectUsers(null, name, beginTime, endTime, deptId);
        Page wrapped = new FileWrapper(users).wrap();
        return LayuiPageFactory.createPageInfo(wrapped);
    }

    /**
     * 删除文件
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:44
     */
    @RequestMapping("/delete")
    @Permission
    @ResponseBody
    public ResponseData delete(@RequestParam Long userId) {
        if (ToolUtil.isEmpty(userId)) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        this.fileService.deleteUser(userId);
        return SUCCESS_TIP;
    }

    /**
     * 跳转到添加文件的页面
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:43
     */
    @RequestMapping("/file_add")
    public String addView() {
        return PREFIX + "file_add.html";
    }

    /**
     * 添加文件
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:44
     */
    @RequestMapping("/add")
    @Permission()
    @ResponseBody
    public ResponseData add(FilePath filePath, HttpServletRequest request) {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        StringBuffer sb = new StringBuffer();
        while (iter.hasNext()) {
            // 取得上传文件
            MultipartFile file = multiRequest.getFile(iter.next());
            if (!file.isEmpty()) {
                String pictureName = UUID.randomUUID().toString() + "." + ToolUtil.getFileSuffix(file.getOriginalFilename());
                try {
                    file.transferTo(new File(Const.FILE_UPLOAD_PATH + pictureName));
                } catch (Exception e) {
                    throw new ServiceException(BizExceptionEnum.UPLOAD_ERROR);
                }
                String fileUrl = Const.FILE_URL + pictureName;
                filePath.setFileUrl(fileUrl);
                this.fileService.addUser(filePath);
            }
        }
        return SUCCESS_TIP;
    }
//
//    /**
//     * 跳转到角色分配页面
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:43
//     */
//    @Permission
//    @RequestMapping("/role_assign")
//    public String roleAssign(@RequestParam Long userId, Model model) {
//        if (ToolUtil.isEmpty(userId)) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        model.addAttribute("userId", userId);
//        return PREFIX + "user_roleassign.html";
//    }
//
//    /**
//     * 跳转到编辑管理员页面
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:43
//     */
//    @Permission
//    @RequestMapping("/user_edit")
//    public String userEdit(@RequestParam Long userId) {
//        if (ToolUtil.isEmpty(userId)) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        User user = this.userService.getById(userId);
//        LogObjectHolder.me().set(user);
//        return PREFIX + "user_edit.html";
//    }
//
//    /**
//     * 获取用户详情
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:43
//     */
//    @RequestMapping("/getUserInfo")
//    @ResponseBody
//    public Object getUserInfo(@RequestParam Long userId) {
//        if (ToolUtil.isEmpty(userId)) {
//            throw new RequestEmptyException();
//        }
//
//        this.userService.assertAuth(userId);
//        User user = this.userService.getById(userId);
//        Map<String, Object> map = UserFactory.removeUnSafeFields(user);
//
//        HashMap<Object, Object> hashMap = CollectionUtil.newHashMap();
//        hashMap.putAll(map);
//        hashMap.put("roleName", ConstantFactory.me().getRoleName(user.getRoleId()));
//        hashMap.put("deptName", ConstantFactory.me().getDeptName(user.getDeptId()));
//
//        return ResponseData.success(hashMap);
//    }
//
//    /**
//     * 修改当前用户的密码
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:43
//     */
//    @RequestMapping("/changePwd")
//    @ResponseBody
//    public Object changePwd(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
//        if (ToolUtil.isOneEmpty(oldPassword, newPassword)) {
//            throw new RequestEmptyException();
//        }
//        this.userService.changePwd(oldPassword, newPassword);
//        return SUCCESS_TIP;
//    }


//
//    /**
//     * 修改管理员
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:44
//     */
//    @RequestMapping("/edit")
//    @BussinessLog(value = "修改管理员", key = "account", dict = UserDict.class)
//    @ResponseBody
//    public ResponseData edit(@Valid UserDto user, BindingResult result) {
//        if (result.hasErrors()) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        this.userService.editUser(user);
//        return SUCCESS_TIP;
//    }
//

//
//    /**
//     * 查看管理员详情
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:44
//     */
//    @RequestMapping("/view/{userId}")
//    @ResponseBody
//    public User view(@PathVariable Long userId) {
//        if (ToolUtil.isEmpty(userId)) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        this.userService.assertAuth(userId);
//        return this.userService.getById(userId);
//    }
//
//    /**
//     * 重置管理员的密码
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:44
//     */
//    @RequestMapping("/reset")
//    @BussinessLog(value = "重置管理员密码", key = "userId", dict = UserDict.class)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public ResponseData reset(@RequestParam Long userId) {
//        if (ToolUtil.isEmpty(userId)) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        this.userService.assertAuth(userId);
//        User user = this.userService.getById(userId);
//        user.setSalt(ShiroKit.getRandomSalt(5));
//        user.setPassword(ShiroKit.md5(Const.DEFAULT_PWD, user.getSalt()));
//        this.userService.updateById(user);
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 冻结用户
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:44
//     */
//    @RequestMapping("/freeze")
//    @BussinessLog(value = "冻结用户", key = "userId", dict = UserDict.class)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public ResponseData freeze(@RequestParam Long userId) {
//        if (ToolUtil.isEmpty(userId)) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        //不能冻结超级管理员
//        if (userId.equals(Const.ADMIN_ID)) {
//            throw new ServiceException(BizExceptionEnum.CANT_FREEZE_ADMIN);
//        }
//        this.userService.assertAuth(userId);
//        this.userService.setStatus(userId, ManagerStatus.FREEZED.getCode());
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 解除冻结用户
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:44
//     */
//    @RequestMapping("/unfreeze")
//    @BussinessLog(value = "解除冻结用户", key = "userId", dict = UserDict.class)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public ResponseData unfreeze(@RequestParam Long userId) {
//        if (ToolUtil.isEmpty(userId)) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        this.userService.assertAuth(userId);
//        this.userService.setStatus(userId, ManagerStatus.OK.getCode());
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 分配角色
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:44
//     */
//    @RequestMapping("/setRole")
//    @BussinessLog(value = "分配角色", key = "userId,roleIds", dict = UserDict.class)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public ResponseData setRole(@RequestParam("userId") Long userId, @RequestParam("roleIds") String roleIds) {
//        if (ToolUtil.isOneEmpty(userId, roleIds)) {
//            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
//        }
//        //不能修改超级管理员
//        if (userId.equals(Const.ADMIN_ID)) {
//            throw new ServiceException(BizExceptionEnum.CANT_CHANGE_ADMIN);
//        }
//        this.userService.assertAuth(userId);
//        this.userService.setRoles(userId, roleIds);
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 上传图片
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:44
//     */
//    @RequestMapping(method = RequestMethod.POST, path = "/upload")
//    @ResponseBody
//    public String upload(@RequestPart("file") MultipartFile picture) {
//
//        String pictureName = UUID.randomUUID().toString() + "." + ToolUtil.getFileSuffix(picture.getOriginalFilename());
//        try {
//            picture.transferTo(new File(Const.FILE_UPLOAD_PATH + pictureName));
//        } catch (Exception e) {
//            throw new ServiceException(BizExceptionEnum.UPLOAD_ERROR);
//        }
//        return pictureName;
//    }
}
