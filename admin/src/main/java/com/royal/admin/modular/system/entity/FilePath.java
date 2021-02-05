package com.royal.admin.modular.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
@TableName("b_file_path")
@Data
public class FilePath implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "USER_ID", type = IdType.ID_WORKER)
    private Long userId;
    /**
     * 文件编号
     */
    @TableField("ACCOUNT")
    private String account;
    /**
     * 名字
     */
    @TableField("NAME")
    private String name;
    /**
     * 层级id(多个逗号隔开)
     */
    @TableField("DEPT_ID")
    private Long deptId;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER", fill = FieldFill.INSERT)
    private Long createUser;
    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;
    /**
     * 更新人
     */
    @TableField(value = "UPDATE_USER", fill = FieldFill.UPDATE)
    private Long updateUser;
    /**
     * 乐观锁
     */
    @TableField("VERSION")
    private Integer version;

    /**
     * URL
     */
    @TableField("FILE_URL")
    private String fileUrl;
    /**
     * URL
     */
    @TableField("FILE_TYPE")
    private String fileType;

}
