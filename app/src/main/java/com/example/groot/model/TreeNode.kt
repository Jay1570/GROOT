package com.example.groot.model;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    public String name;
    public boolean isFolder;
    public boolean isExpanded;  // New property
    public List<TreeNode> children;

    public TreeNode(String name, boolean isFolder) {
        this.name = name;
        this.isFolder = isFolder;
        this.isExpanded = false;  // Initialize as collapsed
        this.children = new ArrayList<>();
    }
}
