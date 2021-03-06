/*
 * MIT License
 *
 * Copyright (c) 2017 MIchael Obi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.michaelobi.paperplayer.presentation.musiclibrary.fragment.albums;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import xyz.michaelobi.paperplayer.data.model.Album;
import xyz.michaelobi.paperplayer.injection.Injector;
import xyz.michaelobi.paperplayer.mvp.ListViewContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * PaperPlayer
 * Michael Obi
 * 23 10 2016 4:00 PM
 */

public class AlbumsFragment extends Fragment implements ListViewContract.View<Album> {

    ListViewContract.Presenter presenter;
    private AlbumsAdapter albumsAdapter;
    private Context context;
    private RecyclerView recyclerViewAlbums;
    private ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AlbumsPresenter(Injector.provideMusicRepository(getContext()), Schedulers.io(),
                AndroidSchedulers.mainThread());
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(xyz.michaelobi.paperplayer.R.layout.fragment_albums, container, false);
        recyclerViewAlbums = (RecyclerView) v.findViewById(xyz.michaelobi.paperplayer.R.id.rv_albums_grid);
        progressBar = (ProgressBar) v.findViewById(xyz.michaelobi.paperplayer.R.id.progressbar_loading);
        presenter.attachView(this);
        recyclerViewAlbums.setLayoutManager(new GridLayoutManager(context, calculateNoOfColumns()));
        recyclerViewAlbums.setHasFixedSize(true);
        albumsAdapter = new AlbumsAdapter(null, context);
        recyclerViewAlbums.setAdapter(albumsAdapter);
        presenter.getAll();
        return v;
    }

    private int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void showList(List<Album> albums) {
        recyclerViewAlbums.setVisibility(View.VISIBLE);
        albumsAdapter.setAlbums(albums);
    }

    @Override
    public void showLoading() {
        recyclerViewAlbums.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
