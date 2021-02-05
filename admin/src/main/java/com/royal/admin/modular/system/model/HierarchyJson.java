package com.royal.admin.modular.system.model;

import lombok.Data;

import java.util.List;

@Data
public class HierarchyJson {

    private String id;

    private String name;

    private List<FileJson> fileList;

    private List<HierarchyJson> hierarchyList;
}
