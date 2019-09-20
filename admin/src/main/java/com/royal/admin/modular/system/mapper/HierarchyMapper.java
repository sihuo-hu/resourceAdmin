package com.royal.admin.modular.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.royal.admin.core.common.node.TreeviewNode;
import com.royal.admin.core.common.node.ZTreeNode;
import com.royal.admin.modular.system.entity.Dept;
import com.royal.admin.modular.system.entity.Hierarchy;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 层级表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
public interface HierarchyMapper extends BaseMapper<Hierarchy> {

    /**
     * 获取ztree的节点列表
     */
    List<ZTreeNode> tree();

    /**
     * 获取所有部门列表
     */
    Page<Map<String, Object>> list(@Param("page") Page page, @Param("condition") String condition, @Param("deptId") String deptId);

    /**
     * 获取所有部门树列表
     */
    List<TreeviewNode> treeviewNodes();
}
