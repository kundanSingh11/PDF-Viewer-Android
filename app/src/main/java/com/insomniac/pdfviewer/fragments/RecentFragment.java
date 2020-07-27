package com.insomniac.pdfviewer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.insomniac.pdfviewer.R;
import com.insomniac.pdfviewer.fragments.models.Recent;
import com.insomniac.pdfviewer.view.TextViewPlus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends Fragment {

    RecyclerView mainRecycler;
    List<Recent> recents = new ArrayList<>();
    ActivityCaller activityCaller;
    private TextView empyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recent_fragment, container, false);
        mainRecycler = view.findViewById(R.id.mainRecycler);
        mainRecycler.setAdapter(new FileListAdapter());
        mainRecycler.setHasFixedSize(true);
        mainRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        empyView = view.findViewById(R.id.emptyView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateRecents();
    }

    public void updateRecents() {
        recents.clear();
        recents.addAll(activityCaller.getAllRecent());
        if (recents.size() == 0) {
            empyView.setVisibility(View.VISIBLE);
            mainRecycler.setVisibility(View.GONE);
        } else {
            empyView.setVisibility(View.GONE);
            mainRecycler.setVisibility(View.VISIBLE);
            mainRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    class FolderListViewHolder extends RecyclerView.ViewHolder {

        TextViewPlus folderIcon;
        CheckBox nextButton;
        TextView foldername;
        ImageView imageIcon;
        RelativeLayout parent;

        public FolderListViewHolder(View itemView) {
            super(itemView);
            folderIcon = (TextViewPlus) itemView.findViewById(R.id.folderImage);
            nextButton = (CheckBox) itemView.findViewById(R.id.nextButton);
            foldername = (TextView) itemView.findViewById(R.id.folderName);
            imageIcon = (ImageView) itemView.findViewById(R.id.imageIcon);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    class FileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FolderListViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.file_selector_view_holder, null, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final FolderListViewHolder folderViewFileSelector = (FolderListViewHolder) holder;
            folderViewFileSelector.imageIcon.setVisibility(View.GONE);
            folderViewFileSelector.nextButton.setVisibility(View.GONE);
            folderViewFileSelector.folderIcon.setText(getString(R.string.pdf));
            folderViewFileSelector.folderIcon.setTextColor(Color.RED);

            folderViewFileSelector.foldername.setText(recents.get(position).getName());


            folderViewFileSelector.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityCaller.inserInto(recents.get(position));
                    Intent intent = new Intent(getActivity(), MuPDFActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.fromFile(new File(recents.get(position).getPath())));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return recents.size();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityCaller = (ActivityCaller) context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        activityCaller = (ActivityCaller) activity;
    }
}
