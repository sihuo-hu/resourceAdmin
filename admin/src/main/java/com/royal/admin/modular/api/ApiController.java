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
package com.royal.admin.modular.api;

import cn.stylefeng.roses.core.reqres.response.ResponseData;
import com.royal.admin.core.shiro.ShiroKit;
import com.royal.admin.core.shiro.ShiroUser;
import com.royal.admin.core.util.JwtTokenUtil;
import com.royal.admin.modular.system.entity.FilePath;
import com.royal.admin.modular.system.entity.Hierarchy;
import com.royal.admin.modular.system.entity.User;
import com.royal.admin.modular.system.mapper.UserMapper;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ErrorResponseData;
import com.royal.admin.modular.system.model.FileJson;
import com.royal.admin.modular.system.model.HierarchyJson;
import com.royal.admin.modular.system.service.FileService;
import com.royal.admin.modular.system.service.HierarchyService;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 接口控制器提供
 *
 * @author stylefeng
 * @Date 2018/7/20 23:39
 */
@RestController
@RequestMapping("/gunsApi")
public class ApiController extends BaseController {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private HierarchyService hierarchyService;

    /**
     * api登录接口，通过账号密码获取token
     */
    @RequestMapping("/auth")
    public Object auth(@RequestParam("username") String username,
                       @RequestParam("password") String password) {

        //封装请求账号密码为shiro可验证的token
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password.toCharArray());

        //获取数据库中的账号密码，准备比对
        User user = userMapper.getByAccount(username);

        String credentials = user.getPassword();
        String salt = user.getSalt();
        ByteSource credentialsSalt = new Md5Hash(salt);
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                new ShiroUser(), credentials, credentialsSalt, "");

        //校验用户账号密码
        HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher();
        md5CredentialsMatcher.setHashAlgorithmName(ShiroKit.hashAlgorithmName);
        md5CredentialsMatcher.setHashIterations(ShiroKit.hashIterations);
        boolean passwordTrueFlag = md5CredentialsMatcher.doCredentialsMatch(
                usernamePasswordToken, simpleAuthenticationInfo);

        if (passwordTrueFlag) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("token", JwtTokenUtil.generateToken(String.valueOf(user.getUserId())));
            return result;
        } else {
            return new ErrorResponseData(500, "账号密码错误！");
        }
    }

    /**
     * 获取文件地址
     */
    @RequestMapping(value = "/not/getUrl")
    @ResponseBody
    public Object getUrl(String id) {
        FilePath filePath = fileService.getByAccount(id);
        return ResponseData.success(filePath.getFileUrl());
    }

    /**
     * 获取文件地址
     */
    @RequestMapping(value = "/not/getJson")
    @ResponseBody
    public Object getJson(String code) {
        HierarchyJson hierarchyJson = new HierarchyJson();
        Hierarchy hierarchy = hierarchyService.getByCode(code);
        if(hierarchy==null){
            return ResponseData.error("未找到对应编码");
        }
        hierarchyJson.setId(hierarchy.getCode());
        hierarchyJson.setName(hierarchy.getSimpleName());
        List<FilePath> filePathList = fileService.getListByDeptId(hierarchy.getDeptId());
        if(filePathList!=null&&filePathList.size()>0){
            List<FileJson> fileJsonList = new ArrayList<>();
            for (FilePath filePath : filePathList) {
                FileJson fileJson = new FileJson();
                fileJson.setId(filePath.getAccount());
                fileJson.setName(filePath.getName());
                fileJson.setType(filePath.getFileType());
                fileJsonList.add(fileJson);
            }
            hierarchyJson.setFileList(fileJsonList);
        }
        fileService.recursionListByDeptId(hierarchyJson,hierarchy.getDeptId());
        return ResponseData.success(hierarchyJson);
    }

}

