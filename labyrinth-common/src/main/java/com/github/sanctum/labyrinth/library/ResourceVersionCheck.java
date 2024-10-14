package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.interfacing.WebResponse;
import com.github.sanctum.panther.annotation.Experimental;
import com.github.sanctum.panther.util.Applicable;
import org.jetbrains.annotations.NotNull;

public interface ResourceVersionCheck extends Applicable {

    @NotNull String getAuthor();

    @NotNull String getResource();

    int getId();

    @NotNull String getCurrent();

    @NotNull String getLatest();

    @Experimental(dueTo = "Not fully tested.")
    @NotNull WebResponse getFromGitHub(String output, String file, String type);

}
