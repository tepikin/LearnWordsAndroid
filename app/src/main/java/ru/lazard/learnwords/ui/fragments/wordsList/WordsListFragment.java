package ru.lazard.learnwords.ui.fragments.wordsList;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

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
        inflater.inflate(R.menu.menu_words_list, menu);


        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                presenter.onSearch(query);
                return true;
            }
        });



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
        if (presenter!=null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //((MainActivity)getActivity()).getFloatingActionButton().setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).getFloatingActionButton().hide();
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_wordsList);
    }

    public void setList(List<Word> words) {
        adapter.setList(words);
        adapter.notifyDataSetChanged();
        waiterView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_removeSelection) {
            DAO.setVisibleForAllWords(false);
            presenter.init();
            return true;
        }if (id == R.id.action_selectAll) {
            DAO.setVisibleForAllWords(true);
            presenter.init();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
