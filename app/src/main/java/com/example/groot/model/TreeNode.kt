package com.example.groot.model

data class TreeNode(
    var name: String,
    var isFolder: Boolean,
    var path: String,
    var isExpanded: Boolean = false,
    var children: MutableList<TreeNode> = mutableListOf()
)
