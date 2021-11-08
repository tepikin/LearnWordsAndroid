package ru.lazard.learnwords.ui.fragments.wordsList.dictionaries;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Dictionary;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

/**
 * Created by Egor on 21.12.2016.
 */

public class DictionariesListFragment  extends Fragment {

    private DictionariesListAdapter adapter;
    private DictionariesListPresenter presenter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private View view;
    private View waiterView;

    public DictionariesListFragment newInstance(){
        Bundle bundle = new Bundle();
        DictionariesListFragment fragment = new DictionariesListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dictionaries_list, menu);


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
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dictionaries_list, container, false);
            waiterView = view.findViewById(R.id.waiter);
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            adapter = new DictionariesListAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);

            if (presenter ==null)
                presenter = new DictionariesListPresenter(this);
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

    public void setList(List<Dictionary> words) {
        adapter.setList(words);
        adapter.notifyDataSetChanged();
        waiterView.setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_addDictionary) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dictionary_edit,null);
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.dictionariesList_dialog_rename_title)
                    .setMessage(R.string.dictionariesList_dialog_rename_message)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = ((TextView)view.findViewById(R.id.name)).getText().toString();
                            Model.getInstance().addDictionary(new Dictionary(text,-1));
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
