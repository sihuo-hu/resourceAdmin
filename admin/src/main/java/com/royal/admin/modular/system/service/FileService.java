package com.royal.admin.modular.system.service;

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.roses.core.datascope.DataScope;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.royal.admin.core.common.constant.Const;
import com.royal.admin.core.common.constant.cache.Cache;
import com.royal.admin.core.common.constant.cache.CacheKey;
import com.royal.admin.core.common.constant.state.ManagerStatus;
import com.royal.admin.core.common.exception.BizExceptionEnum;
import com.royal.admin.core.common.node.MenuNode;
import com.royal.admin.core.common.page.LayuiPageFactory;
import com.royal.admin.core.shiro.ShiroKit;
import com.royal.admin.core.shiro.ShiroUser;
import com.royal.admin.core.shiro.service.UserAuthService;
import com.royal.admin.core.util.ApiMenuFilter;
import com.royal.admin.modular.system.entity.FilePath;
import com.royal.admin.modular.system.entity.Hierarchy;
import com.royal.admin.modular.system.entity.User;
import com.royal.admin.modular.system.factory.UserFactory;
import com.royal.admin.modular.system.mapper.FileMapper;
import com.royal.admin.modular.system.mapper.UserMapper;
import com.royal.admin.modular.system.model.FileJson;
import com.royal.admin.modular.system.model.HierarchyJson;
import com.royal.admin.modular.system.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@Service
public class FileService extends ServiceImpl<FileMapper, FilePath> {
    @Autowired
    private HierarchyService hierarchyService;

    /**
     * 删除文件
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:54
     */
    public void deleteUser(Long userId) {
        this.baseMapper.deleteById(userId);
    }


    /**
     * 添加用戶
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:51
     */
    public void addUser(FilePath filePath) {

        // 判断编号是否重复
        FilePath theUser = this.getByAccount(filePath.getAccount());
        if (theUser != null) {
            filePath.setUserId(theUser.getUserId());
            this.updateById(filePath);
        } else {
            this.save(filePath);
        }
    }

    /**
     * 通过账号获取用户
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:46
     */
    public FilePath getByAccount(String account) {
        return this.baseMapper.getByAccount(account);
    }

//
//    /**
//     * 修改用户
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:53
//     */
//    public void editUser(UserDto user) {
//        User oldUser = this.getById(user.getUserId());
//
//        if (ShiroKit.hasRole(Const.ADMIN_NAME)) {
//            this.updateById(UserFactory.editUser(user, oldUser));
//        } else {
//            this.assertAuth(user.getUserId());
//            ShiroUser shiroUser = ShiroKit.getUserNotNull();
//            if (shiroUser.getId().equals(user.getUserId())) {
//                this.updateById(UserFactory.editUser(user, oldUser));
//            } else {
//                throw new ServiceException(BizExceptionEnum.NO_PERMITION);
//            }
//        }
//    }
//

//
//
//
//    /**
//     * 修改密码
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:45
//     */
//    public void changePwd(String oldPassword, String newPassword) {
//        Long userId = ShiroKit.getUserNotNull().getId();
//        User user = this.getById(userId);
//
//        String oldMd5 = ShiroKit.md5(oldPassword, user.getSalt());
//
//        if (user.getPassword().equals(oldMd5)) {
//            String newMd5 = ShiroKit.md5(newPassword, user.getSalt());
//            user.setPassword(newMd5);
//            this.updateById(user);
//        } else {
//            throw new ServiceException(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
//        }
//    }

    /**
     * 根据条件查询用户列表
     *
     * @author fengshuonan
     * @Date 2018/12/24 22:45
     */
    public Page<Map<String, Object>> selectUsers(DataScope dataScope, String name, String beginTime, String endTime, Long deptId) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.selectUsers(page, dataScope, name, beginTime, endTime, deptId);
    }

    public List<FilePath> getListByDeptId(Long deptId) {
        QueryWrapper<FilePath> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("DEPT_ID", deptId);
        return this.baseMapper.selectList(queryWrapper);
    }

    public HierarchyJson recursionListByDeptId(HierarchyJson hierarchyJson, Long deptId) {
        List<Hierarchy> hierarchyList = hierarchyService.getByPId(deptId);
        if (hierarchyList != null && hierarchyList.size() > 0) {
                List<HierarchyJson> hierarchyJsonList = new ArrayList<>();
            for (Hierarchy hierarchy : hierarchyList) {
                HierarchyJson hierarchyJson1 = new HierarchyJson();
                hierarchyJson1.setId(hierarchy.getCode());
                hierarchyJson1.setName(hierarchy.getSimpleName());
                List<FilePath> filePathList = this.getListByDeptId(hierarchy.getDeptId());
                if (filePathList != null && filePathList.size() > 0) {
                    List<FileJson> fileJsonList = new ArrayList<>();
                    for (FilePath filePath : filePathList) {
                        FileJson fileJson = new FileJson();
                        fileJson.setId(filePath.getAccount());
                        fileJson.setName(filePath.getName());
                        fileJson.setType(filePath.getFileType());
                        fileJsonList.add(fileJson);
                    }
                    hierarchyJson1.setFileList(fileJsonList);
                }
                hierarchyJsonList.add(hierarchyJson1);
                recursionListByDeptId(hierarchyJson1,hierarchy.getDeptId());
            }
            hierarchyJson.setHierarchyList(hierarchyJsonList);
        } else {
            return hierarchyJson;
        }
        return hierarchyJson;
    }

//    /**
//     * 设置用户的角色
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:45
//     */
//    public int setRoles(Long userId, String roleIds) {
//        return this.baseMapper.setRoles(userId, roleIds);
//    }
//

//
//    /**
//     * 获取用户菜单列表
//     *
//     * @author fengshuonan
//     * @Date 2018/12/24 22:46
//     */
//    public List<MenuNode> getUserMenuNodes(List<Long> roleList) {
//        if (roleList == null || roleList.size() == 0) {
//            return new ArrayList<>();
//        } else {
//            List<MenuNode> menus = menuService.getMenusByRoleIds(roleList);
//            List<MenuNode> titles = MenuNode.buildTitle(menus);
//            return ApiMenuFilter.build(titles);
//        }
//
//    }
//
//
//
//    /**
//     * 刷新当前登录用户的信息
//     *
//     * @author fengshuonan
//     * @Date 2019/1/19 5:59 PM
//     */
//    public void refreshCurrentUser() {
//        ShiroUser user = ShiroKit.getUserNotNull();
//        Long id = user.getId();
//        User currentUser = this.getById(id);
//        ShiroUser shiroUser = userAuthService.shiroUser(currentUser);
//        ShiroUser lastUser = ShiroKit.getUser();
//        BeanUtil.copyProperties(shiroUser, lastUser);
//    }

}
