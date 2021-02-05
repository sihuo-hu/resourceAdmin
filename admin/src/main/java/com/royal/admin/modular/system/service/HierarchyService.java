package com.royal.admin.modular.system.service;

import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.royal.admin.core.common.constant.cache.Cache;
import com.royal.admin.core.common.constant.cache.CacheKey;
import com.royal.admin.core.common.exception.BizExceptionEnum;
import com.royal.admin.core.common.node.TreeviewNode;
import com.royal.admin.core.common.node.ZTreeNode;
import com.royal.admin.core.common.page.LayuiPageFactory;
import com.royal.admin.modular.system.entity.Dept;
import com.royal.admin.modular.system.entity.Hierarchy;
import com.royal.admin.modular.system.mapper.DeptMapper;
import com.royal.admin.modular.system.mapper.HierarchyMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 层级表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@Service
public class HierarchyService extends ServiceImpl<HierarchyMapper, Hierarchy> {

    @Resource
    private HierarchyMapper hierarchyMapper;

    /**
     * 新增部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:00 PM
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDept(Hierarchy dept) {

        if (ToolUtil.isOneEmpty(dept, dept.getSimpleName(), dept.getFullName(), dept.getPid(), dept.getCode())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }
        if (this.isCodeExist(dept.getCode())) {
            throw new ServiceException(BizExceptionEnum.EXISTED_THE_MENU);
        }

        //完善pids,根据pid拿到pid的pids
        this.deptSetPids(dept);

        this.save(dept);
    }

    /**
     * 修改部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:00 PM
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = Cache.CONSTANT, key = "'" + CacheKey.HIERARCHY_NAME + "'+#dept.deptId")
    public void editDept(Hierarchy dept) {

        if (ToolUtil.isOneEmpty(dept, dept.getDeptId(), dept.getSimpleName(), dept.getFullName(), dept.getPid(), dept.getCode())) {
            throw new ServiceException(BizExceptionEnum.REQUEST_NULL);
        }

        if (this.isCodeExist(dept.getCode())) {
            throw new ServiceException(BizExceptionEnum.EXISTED_THE_MENU);
        }

        //完善pids,根据pid拿到pid的pids
        this.deptSetPids(dept);

        this.updateById(dept);
    }

    /**
     * 删除部门
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    @Transactional
    public void deleteDept(Long deptId) {
        Hierarchy dept = hierarchyMapper.selectById(deptId);

        //根据like查询删除所有级联的部门
        QueryWrapper<Hierarchy> wrapper = new QueryWrapper<>();
        wrapper = wrapper.like("PIDS", "%[" + dept.getDeptId() + "]%");
        List<Hierarchy> subDepts = hierarchyMapper.selectList(wrapper);
        for (Hierarchy temp : subDepts) {
            this.removeById(temp.getDeptId());
        }

        this.removeById(dept.getDeptId());
    }

    /**
     * 获取ztree的节点列表
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    public List<ZTreeNode> tree() {
        return this.baseMapper.tree();
    }

    /**
     * 获取ztree的节点列表
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    public List<TreeviewNode> treeviewNodes() {
        return this.baseMapper.treeviewNodes();
    }

    /**
     * 获取所有部门列表
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:16 PM
     */
    public Page<Map<String, Object>> list(String condition, String deptId) {
        Page page = LayuiPageFactory.defaultPage();
        return this.baseMapper.list(page, condition, deptId);
    }

    /**
     * 设置部门的父级ids
     *
     * @author fengshuonan
     * @Date 2018/12/23 4:58 PM
     */
    private void deptSetPids(Hierarchy dept) {
        if (ToolUtil.isEmpty(dept.getPid()) || dept.getPid().equals(0L)) {
            dept.setPid(0L);
            dept.setPids("[0],");
        } else {
            Long pid = dept.getPid();
            Hierarchy temp = this.getById(pid);
            String pids = temp.getPids();
            dept.setPid(pid);
            dept.setPids(pids + "[" + pid + "],");
        }
    }

    public Hierarchy getByCode(String code) {
        QueryWrapper<Hierarchy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CODE", code);
        return this.baseMapper.selectOne(queryWrapper);
    }

    private boolean isCodeExist(String code) {
        Hierarchy hierarchy = getByCode(code);
        if (hierarchy != null && ToolUtil.isNotEmpty(hierarchy.getDeptId())) {
            return true;
        } else {
            return false;
        }
    }

    public List<Hierarchy> getByPId(Long deptId) {
        QueryWrapper<Hierarchy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("PID", deptId);
        return this.baseMapper.selectList(queryWrapper);
    }
}
