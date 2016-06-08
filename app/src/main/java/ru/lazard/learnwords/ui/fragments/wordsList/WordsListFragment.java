package ru.lazard.learnwords.ui.fragments.wordsList;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.wordsList.edit.WordEditFragment;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsListFragment extends Fragment {

    private WordsListAdapter adapter;
    private WordsListPresenter presenter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private View view;
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
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
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

if (presenter!=null){
    if (!TextUtils.isEmpty(presenter.getSearchQuery())) {
        searchView.setQuery(presenter.getSearchQuery(),true);
        searchView.setIconified(false);
    }
}

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_words_list, container, false);
            waiterView = view.findViewById(R.id.waiter);
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            adapter = new WordsListAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);

            if (presenter ==null)
            presenter = new WordsListPresenter(this);
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
        presenter.init();
    }

    public void setList(List<Word> words) {
        adapter.setList(words);
        adapter.notifyDataSetChanged();
        waiterView.setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_addWord) {
            ((MainActivity)getActivity()).addFragment(WordEditFragment.newInstance(null),true);
            return true;
        }
        if (id == R.id.action_clearList) {
            DAO.setLearnWordsListClear();
            Model.getInstance().setLearnWordsListClear();
            presenter.init();
            return true;
        }if (id == R.id.action_selectAllWords) {
            DAO.setLearnWordsListAll();
            Model.getInstance().setLearnWordsListAll();
            presenter.init();
            return true;
        }
        if (id == R.id.action_baseWords) {
            DAO.setLearnWordsListByDifficult(0);
            Model.getInstance().setLearnWordsListByDifficult(0);
            presenter.init();
            return true;
        }
        if (id == R.id.action_irregularVerbs) {
            int statusLearn = Word.STATUS_LEARN;
            DAO.setLearnWordsListByDifficult(4);
            Model.getInstance().setLearnWordsListByDifficult(4);
            presenter.init();
            return true;
        }
        if (id == R.id.action_irregularVerbsShort) {
            int statusLearn = Word.STATUS_LEARN;
            DAO.setLearnWordsListByDifficult(5);
            Model.getInstance().setLearnWordsListByDifficult(5);
            presenter.init();
            return true;
        }
        if (id == R.id.action_random20) {
            DAO.setLearnWordsListClear();
            Model.getInstance().setLearnWordsListClear();
            List<Word> words = Model.getInstance().getWordsWithStatus(Word.STATUS_NONE);
            List<Word> randomSubItems = Model.getRandomSubItems(20, words);
            for (Word randomSubItem : randomSubItems) {
                randomSubItem.setStatus(Word.STATUS_LEARN);
            }
            DAO.setStatusForWords(Word.STATUS_LEARN,randomSubItems);
            presenter.init();
            return true;
        }
        if (id == R.id.action_random50) {
            DAO.setLearnWordsListClear();
            Model.getInstance().setLearnWordsListClear();
            List<Word> words = Model.getInstance().getWordsWithStatus(Word.STATUS_NONE);
            List<Word> randomSubItems = Model.getRandomSubItems(50, words);
            for (Word randomSubItem : randomSubItems) {
                randomSubItem.setStatus(Word.STATUS_LEARN);
            }
            DAO.setStatusForWords(Word.STATUS_LEARN,randomSubItems);
            presenter.init();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
