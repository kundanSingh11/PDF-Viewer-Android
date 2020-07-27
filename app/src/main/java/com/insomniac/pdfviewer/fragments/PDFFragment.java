package com.insomniac.pdfviewer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.insomniac.pdfviewer.R;
import com.insomniac.pdfviewer.view.TextViewPlus;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PDFFragment extends Fragment {
    RecyclerView recyclerView;
    boolean isMenuActive = false;
    ActivityCaller activityCaller;
    ArrayList<PDFFile> fileListPri = new ArrayList<>();
    ArrayList<PDFFile> fileList = new ArrayList<>();

    FileListAdapter ad = new FileListAdapter();
    private boolean inSearch = false;
    private String searchQuery;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_selector_folder_file_seletor_layout, null, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.folderFile);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fileList.clear();
                fileList.addAll(getAllSdCardTrack_Beans(getActivity()));
                fileListPri.addAll(fileList);
                recyclerView.getAdapter().notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView.setNestedScrollingEnabled(true);
        fileList.clear();
        fileList.addAll(getAllSdCardTrack_Beans(getActivity()));
        fileListPri.addAll(fileList);
        recyclerView.setAdapter(ad);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.context_manu_file_select, menu);


        final MenuItem item = menu.findItem(R.id.home_search);
//        final SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        final SearchView searchView = (SearchView) item.getActionView();
//        MenuItemCompat.setActionView(item, searchView);
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);

        EditText txtSearch = ((EditText) searchView.findViewById(R.id.search_src_text));
        txtSearch.setHint("Search ..");
        txtSearch.setHintTextColor(Color.WHITE);
        txtSearch.setTextColor(getResources().getColor(R.color.white));

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findAndShow(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                findAndShow(newText);
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MenuItemCompat.collapseActionView(item);
                    searchView.onActionViewCollapsed();
                    resetSearch();
                } else {
                    inSearch = true;
                }
            }
        });
    }

    private void findAndShow(String query) {
        searchQuery = query;
        List<PDFFile> databaseModels = new ArrayList<>();
        for (PDFFile databaseModel : fileListPri) {
            if (databaseModel.getName().toLowerCase().contains(query.toLowerCase())) {
                databaseModels.add(databaseModel);
            }
        }

        fileList.clear();
        fileList.addAll(databaseModels);
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    private void resetSearch() {
        searchQuery = null;
        inSearch = false;
        fileList.clear();
        fileList.addAll(fileListPri);
        recyclerView.getAdapter().notifyDataSetChanged();
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

            if (inSearch && searchQuery != null) {
                folderViewFileSelector.foldername.setText(fileList.get(position).getName());
                highlightString(searchQuery.toUpperCase(), folderViewFileSelector.foldername);
            } else {
                folderViewFileSelector.foldername.setText(fileList.get(position).getName());
            }


            folderViewFileSelector.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityCaller.inserInto(new File(fileList.get(position).location));
                    Intent intent = new Intent(getActivity(), MuPDFActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.fromFile(new File(fileList.get(position).location)));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }
    }


    private void highlightString(String input, TextView mTextView) {
        SpannableString spannableString = new SpannableString(mTextView.getText());
        BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);

        for (BackgroundColorSpan span : backgroundSpans) {
            spannableString.removeSpan(span);
        }
        int indexOfKeyword = spannableString.toString().toUpperCase().indexOf(input);

        while (indexOfKeyword > -1 && input != null && !input.isEmpty()) {
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), indexOfKeyword, indexOfKeyword + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            indexOfKeyword = spannableString.toString().indexOf(input, indexOfKeyword + input.length());
        }
        mTextView.setText(spannableString);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityCaller = (ActivityCaller) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityCaller = (ActivityCaller) activity;
    }


    public ArrayList<PDFFile> getAllSdCardTrack_Beans(Context context) {
        ArrayList<PDFFile> pdfFiles = new ArrayList<PDFFile>();

        Cursor c = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                null, MediaStore.Files.FileColumns.DATA + " LIKE '%.pdf'", null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        if (c.moveToFirst()) {
            do {
                PDFFile pdfFile = new PDFFile();
                String location = c.getString(c.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                if (location != null) {
                    File file = new File(location);
                    pdfFile.setName(file.getName());
                    pdfFile.setLocation(file.getAbsolutePath());
                    pdfFiles.add(pdfFile);
                } else {
                    continue;
                }
            } while (c.moveToNext());
            if (c != null)
                c.close();
        }
        return pdfFiles;

    }

    class PDFFile {
        String name;
        String location;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}
