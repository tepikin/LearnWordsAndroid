package ru.lazard.learnwords;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.preferences.SettingsActivity;

public class MainActivity extends AppCompatActivity implements  CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private View baseLayout;

    private ToggleButton playView;
    private MainPresenter presenter;
    private View settingsView;
    private TextView translateView;
    private TextView wordView;

    public void blink() {
        baseLayout.setSelected(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                baseLayout.setSelected(false);
            }
        },100l);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (playView==buttonView){
                presenter.setPlay(isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        if (settingsView==v){
            SettingsActivity.show(this);
        }

    }

    public void showWord(Word randomWord) {
        wordView.setText(randomWord.getWord());
        translateView.setText(randomWord.getTranslate());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseLayout = findViewById(R.id.base_layout);
        wordView = (TextView)findViewById(R.id.word);
        translateView = (TextView)findViewById(R.id.translate);
        playView = (ToggleButton)findViewById(R.id.play);
        settingsView = findViewById(R.id.settings);


        playView.setOnCheckedChangeListener(this);
        settingsView.setOnClickListener(this);


        presenter = new MainPresenter(this);
        presenter.setPlay(playView.isChecked());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
