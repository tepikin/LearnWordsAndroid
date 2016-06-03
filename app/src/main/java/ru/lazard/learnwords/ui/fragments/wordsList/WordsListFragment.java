package ru.lazard.learnwords.ui.fragments.wordsList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.MainActivity;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsListFragment extends Fragment {

    private WordsListAdapter adapter;
    private WordsListPresenter presenter;
    private RecyclerView recyclerView;
    private View waiterView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_words_list, container, false);
            waiterView = view.findViewById(R.id.waiter);
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            adapter = new WordsListAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);

            presenter = new WordsListPresenter(this);
            presenter.init();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //((MainActivity)getActivity()).getFloatingActionButton().setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).getFloatingActionButton().hide();
    }

    public void setList(List<Word> words) {
        adapter.setList(words);
        adapter.notifyDataSetChanged();
        waiterView.setVisibility(View.GONE);
    }
}
