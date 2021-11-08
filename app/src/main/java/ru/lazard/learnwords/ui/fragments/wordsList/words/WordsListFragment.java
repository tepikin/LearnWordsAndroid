package ru.lazard.learnwords.ui.fragments.wordsList.words;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Dictionary;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.wordsList.edit.WordEditFragment;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsListFragment extends Fragment {

    public static final String KEY_DICTIONARY_ID = "KEY_DICTIONARY_ID";
    private WordsListAdapter adapter;
    private WordsListPresenter presenter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private View view;
    private View waiterView;

    public static WordsListFragment newInstance(int dictionaryId){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_DICTIONARY_ID,dictionaryId);
        WordsListFragment fragment = new WordsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    public int getDictionaryId(){
        if (getArguments()==null)return Dictionary.NO_DICTIONARY_ID;
        return getArguments().getInt(KEY_DICTIONARY_ID, Dictionary.NO_DICTIONARY_ID);
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
            adapter.setDictionaryId(getDictionaryId());
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
            ((MainActivity)getActivity()).addFragment(WordEditFragment.newInstance(null,getDictionaryId()),true);
            return true;
        }
        if (id == R.id.action_clearList) {
            Model.getInstance().switchWordStatusForDictionary(Word.STATUS_LEARN,Word.STATUS_NONE,getDictionaryId());
            presenter.init();
            return true;
        }if (id == R.id.action_selectAllWords) {
            Model.getInstance().switchWordStatusForDictionary(Word.STATUS_NONE,Word.STATUS_LEARN,getDictionaryId());
            presenter.init();
            return true;
        }
        if (id == R.id.action_random10) {
            selectRandomWords(10);
            return true;
        }
        if (id == R.id.action_random20) {
            selectRandomWords(20);
            return true;
        }
        if (id == R.id.action_random50) {
            selectRandomWords(50);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectRandomWords(int count) {
        Model.getInstance().setLearnWordsListClear();
        List<Word> words = Model.getInstance().getWordsWithStatus(Word.STATUS_NONE);
        words = presenter.filterWordsByDictionary(words,getDictionaryId());
        List<Word> randomSubItems = Model.getRandomSubItems(count, words);
        Model.getInstance().setWordStatus(Word.STATUS_LEARN, randomSubItems);
        presenter.init();
    }


}
