package com.royal.admin.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 层级表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@TableName("b_hierarchy")
@Data
public class Hierarchy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "DEPT_ID", type = IdType.ID_WORKER)
    private Long deptId;
    /**
     * 父部门id
     */
    @TableField("PID")
    private Long pid;
    /**
     * 编号
     */
    @TableField("CODE")
    private String code;
    /**
     * 父级ids
     */
    @TableField("PIDS")
    private String pids;
    /**
     * 简称
     */
    @TableField("SIMPLE_NAME")
    private String simpleName;
    /**
     * 全称
     */
    @TableField("FULL_NAME")
    private String fullName;
    /**
     * 描述
     */
    @TableField("DESCRIPTION")
    private String description;
    /**
     * 版本（乐观锁保留字段）
     */
    @TableField("VERSION")
    private Integer version;
    /**
     * 排序
     */
    @TableField("SORT")
    private Integer sort;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private Long createUser;
    /**
     * 修改人
     */
    @TableField(value = "UPDATE_USER", fill = FieldFill.UPDATE)
    private Long updateUser;

}
