package ru.lazard.learnwords.learn;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.preferences.SettingsActivity;

/**
 * Created by Egor on 02.06.2016.
 */
public class LearnFragment extends Fragment implements  CompoundButton.OnCheckedChangeListener, View.OnClickListener{
    private View baseLayout;

    private ToggleButton playView;
    private LearnPresenter presenter;
    private View settingsView;
    private TextView translateView;
    private TextView wordView;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (playView==buttonView){
            presenter.setPlay(isChecked);
        }
    }
    @Override
    public void onClick(View v) {
        if (settingsView==v){
            SettingsActivity.show(getContext());
        }

    }

    public void showWord(Word randomWord) {
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
            playView = (ToggleButton) view.findViewById(R.id.play);
            settingsView = view.findViewById(R.id.settings);

            playView.setOnCheckedChangeListener(this);
            settingsView.setOnClickListener(this);

            presenter = new LearnPresenter(this);
            presenter.setPlay(playView.isChecked());
        }
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
