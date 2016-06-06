package ru.lazard.learnwords.ui.fragments.learn;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.MainActivity;

/**
 * Created by Egor on 02.06.2016.
 */
public class LearnFragment extends Fragment implements   View.OnClickListener{
    private View baseLayout;
    private FloatingActionButton floatingActionButton;


    private LearnPresenter presenter;
    private TextView translateView;
    private TextView wordView;


    @Override
    public void onClick(View v) {
        if (floatingActionButton==v){
            presenter.onFloatingActionButtonClick();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setStatePause() {
        floatingActionButton.setImageResource(android.R.drawable.ic_media_play);
    }

    public void setStatePlay() {
        floatingActionButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    public void showWord(Word randomWord) {
        if (randomWord==null){randomWord=new Word("No words selected","Выбирите слова для изучения");}
        wordView.setText(randomWord.getWord());
        translateView.setText(randomWord.getTranslate());
    }


    public void blink() {
        baseLayout.setSelected(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                baseLayout.setSelected(false);
            }
        },100l);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view == null ) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_learn,container,false);
            baseLayout = view.findViewById(R.id.base_layout);
            wordView = (TextView) view.findViewById(R.id.word);
            translateView = (TextView) view.findViewById(R.id.translate);

            floatingActionButton  = ((MainActivity)getActivity()).getFloatingActionButton();


            floatingActionButton.setOnClickListener(this);
            floatingActionButton.setVisibility(View.VISIBLE);
            setStatePause();
            presenter = new LearnPresenter(this);


        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).getFloatingActionButton().setVisibility(View.INVISIBLE);
        ((MainActivity)getActivity()).getFloatingActionButton().show();
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_learnWords);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetach();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();

    }
}
