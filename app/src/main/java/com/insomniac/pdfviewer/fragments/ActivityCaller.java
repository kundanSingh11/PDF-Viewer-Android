package com.insomniac.pdfviewer.fragments;

import com.insomniac.pdfviewer.fragments.models.Recent;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public interface ActivityCaller {
    void inserInto(Recent recent);

    void inserInto(File recent);

    List<Recent> getAllRecent();


}
