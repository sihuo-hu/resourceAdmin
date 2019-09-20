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

import cn.hutool.core.bean.BeanUtil;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.reqres.response.ResponseData;
import cn.stylefeng.roses.core.treebuild.DefaultTreeBuildFactory;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.RequestEmptyException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.royal.admin.core.common.annotion.BussinessLog;
import com.royal.admin.core.common.annotion.Permission;
import com.royal.admin.core.common.constant.dictmap.DeptDict;
import com.royal.admin.core.common.constant.dictmap.HierarchyDict;
import com.royal.admin.core.common.constant.factory.ConstantFactory;
import com.royal.admin.core.common.node.TreeviewNode;
import com.royal.admin.core.common.node.ZTreeNode;
import com.royal.admin.core.common.page.LayuiPageFactory;
import com.royal.admin.core.log.LogObjectHolder;
import com.royal.admin.modular.system.entity.Dept;
import com.royal.admin.modular.system.entity.Hierarchy;
import com.royal.admin.modular.system.model.DeptDto;
import com.royal.admin.modular.system.model.HierarchyDto;
import com.royal.admin.modular.system.service.DeptService;
import com.royal.admin.modular.system.service.HierarchyService;
import com.royal.admin.modular.system.warpper.DeptTreeWrapper;
import com.royal.admin.modular.system.warpper.DeptWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 层级控制器
 *
 * @author fengshuonan
 * @Date 2017年2月17日20:27:22
 */
@Controller
@RequestMapping("/hierarchy")
public class HierarchyController extends BaseController {

    private String PREFIX = "/modular/business/hierarchy/";

    @Autowired
    private HierarchyService hierarchyService;

    /**
     * 跳转到部门管理首页
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:56 PM
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "hierarchy.html";
    }

    /**
     * 跳转到添加部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:56 PM
     */
    @RequestMapping("/hierarchy_add")
    public String deptAdd() {
        return PREFIX + "hierarchy_add.html";
    }

    /**
     * 跳转到修改部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:56 PM
     */
    @Permission
    @RequestMapping("/hierarchy_update")
    public String deptUpdate(@RequestParam("deptId") Long deptId) {

        if (ToolUtil.isEmpty(deptId)) {
            throw new RequestEmptyException();
        }

        //缓存部门修改前详细信息
        Hierarchy hierarchy = hierarchyService.getById(deptId);
        LogObjectHolder.me().set(hierarchy);

        return PREFIX + "hierarchy_edit.html";
    }

    /**
     * 获取部门的tree列表，ztree格式
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:56 PM
     */
    @RequestMapping(value = "/tree")
    @ResponseBody
    public List<ZTreeNode> tree() {
        List<ZTreeNode> tree = this.hierarchyService.tree();
        tree.add(ZTreeNode.createParent());
        return tree;
    }

    /**
     * 获取部门的tree列表，treeview格式
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:57 PM
     */
    @RequestMapping(value = "/treeview")
    @ResponseBody
    public List<TreeviewNode> treeview() {
        List<TreeviewNode> treeviewNodes = this.hierarchyService.treeviewNodes();

        //构建树
        DefaultTreeBuildFactory<TreeviewNode> factory = new DefaultTreeBuildFactory<>();
        factory.setRootParentId("0");
        List<TreeviewNode> results = factory.doTreeBuild(treeviewNodes);

        //把子节点为空的设为null
        DeptTreeWrapper.clearNull(results);

        return results;
    }

    /**
     * 新增部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:57 PM
     */
    @BussinessLog(value = "添加层级", key = "simpleName", dict = HierarchyDict.class)
    @RequestMapping(value = "/add")
    @Permission
    @ResponseBody
    public ResponseData add(Hierarchy hierarchy) {
        this.hierarchyService.addDept(hierarchy);
        return SUCCESS_TIP;
    }

    /**
     * 获取所有部门列表
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:57 PM
     */
    @RequestMapping(value = "/list")
    @Permission
    @ResponseBody
    public Object list(@RequestParam(value = "condition", required = false) String condition,
                       @RequestParam(value = "deptId", required = false) String deptId) {
        Page<Map<String, Object>> list = this.hierarchyService.list(condition, deptId);
        Page<Map<String, Object>> wrap = new DeptWrapper(list).wrap();
        return LayuiPageFactory.createPageInfo(wrap);
    }

    /**
     * 部门详情
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:57 PM
     */
    @RequestMapping(value = "/detail/{deptId}")
    @Permission
    @ResponseBody
    public Object detail(@PathVariable("deptId") Long deptId) {
        Hierarchy hierarchy = hierarchyService.getById(deptId);
        HierarchyDto hierarchyDto = new HierarchyDto();
        BeanUtil.copyProperties(hierarchy, hierarchyDto);
        hierarchyDto.setPName(ConstantFactory.me().getDeptName(hierarchyDto.getPid()));
        return hierarchyDto;
    }

    /**
     * 修改部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:57 PM
     */
    @BussinessLog(value = "修改层级", key = "simpleName", dict = HierarchyDict.class)
    @RequestMapping(value = "/update")
    @Permission
    @ResponseBody
    public ResponseData update(Hierarchy hierarchy) {
        hierarchyService.editDept(hierarchy);
        return SUCCESS_TIP;
    }

    /**
     * 删除部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:57 PM
     */
    @BussinessLog(value = "删除层级", key = "deptId", dict = HierarchyDict.class)
    @RequestMapping(value = "/delete")
    @Permission
    @ResponseBody
    public ResponseData delete(@RequestParam Long deptId) {

        //缓存被删除的部门名称
        LogObjectHolder.me().set(ConstantFactory.me().getDeptName(deptId));

        hierarchyService.deleteDept(deptId);

        return SUCCESS_TIP;
    }

}
