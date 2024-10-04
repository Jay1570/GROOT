package com.example.groot.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groot.R;
import com.example.groot.model.TreeNode;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TreeViewAdapter extends RecyclerView.Adapter<TreeViewAdapter.TreeViewHolder> {

    private static final String TAG = "TreeViewAdapter";
    private List<TreeNode> nodes;
    private Context context;
    private StorageReference currentRef;

    public TreeViewAdapter(List<TreeNode> nodes, Context context, StorageReference currentRef) {
        this.nodes = nodes;
        this.context = context;
        this.currentRef = currentRef;
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.node_layout, parent, false);
        return new TreeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        TreeNode node = nodes.get(position);
        holder.textView.setText(node.name);
        holder.imageView.setImageResource(node.isFolder ? R.drawable.folder_svgrepo_com : R.drawable.files_interface_svgrepo_com);

        holder.itemView.setOnClickListener(v -> {
            if (node.isFolder) {
                node.isExpanded = !node.isExpanded;
                notifyDataSetChanged();
            } else {
                openFile(currentRef.child(node.name), node.name);
            }
        });

        if (node.isExpanded && node.children.size() > 0) {
            holder.childrenRecyclerView.setVisibility(View.VISIBLE);
            TreeViewAdapter childAdapter = new TreeViewAdapter(node.children, context, currentRef.child(node.name));
            holder.childrenRecyclerView.setAdapter(childAdapter);
            holder.childrenRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            holder.childrenRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }

    public class TreeViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        RecyclerView childrenRecyclerView;

        public TreeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.node_value);
            imageView = itemView.findViewById(R.id.node_icon);
            childrenRecyclerView = itemView.findViewById(R.id.children_recycler_view);
        }
    }

    private void openFile(StorageReference fileRef, String fileName) {
        fileRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(bytes -> {
                    String content = new String(bytes);
                    showFileContent(fileName, content);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to open file", e));
    }

    private void showFileContent(String fileName, String content) {
        //Intent intent = new Intent(context, FileContentActivity.class);
        //intent.putExtra("FILE_NAME", fileName);
        //intent.putExtra("FILE_CONTENT", content);

        //context.startActivity(intent);
    }
}
