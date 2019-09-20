package com.royal.admin.modular.system.mapper;

import cn.stylefeng.roses.core.datascope.DataScope;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.royal.admin.modular.system.entity.FilePath;
import com.royal.admin.modular.system.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-07
 */
public interface FileMapper extends BaseMapper<FilePath> {

    /**
     * 根据条件查询用户列表
     */
    Page<Map<String, Object>> selectUsers(@Param("page") Page page, @Param("dataScope") DataScope dataScope, @Param("name") String name, @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("deptId") Long deptId);

    /**
     * 通过账号获取用户
     */
    FilePath getByAccount(@Param("account") String account);

}
