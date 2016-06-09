package ru.lazard.learnwords.ui.fragments.spellcheck;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

/**
 * Created by Egor on 02.06.2016.
 */
public class SpellCheckFragment extends Fragment implements View.OnClickListener {
    public enum State {start, fail}

    private View nextView;
    private SpellCheckPresenter presenter;
    private ImageView soundView;
    private TextView transcriptionView;
    private TextView translationView;
    private EditText translationEditView;
    private View unknownView;
    private View wordLayout;
    private TextView wordView;

    @Override
    public void onClick(View v) {
        if (soundView == v) {
            presenter.onSoundViewClick();
        }
        if (nextView == v) {
            presenter.onNextViewClick();;
        }
        if (unknownView== v) {
            presenter.onUnknownViewClick();
        }
    }

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
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_check_spell, container, false);
            wordView = (TextView) view.findViewById(R.id.word);
            transcriptionView = (TextView) view.findViewById(R.id.transcription);
            translationView = (TextView) view.findViewById(R.id.translate);
            translationEditView = (EditText) view.findViewById(R.id.translateEdit);
            soundView = (ImageView) view.findViewById(R.id.sound);
            nextView = (View) view.findViewById(R.id.next);
            unknownView = (View) view.findViewById(R.id.unknown);
            wordLayout = (View) view.findViewById(R.id.wordLayout);


            soundView.setOnClickListener(this);
            nextView.setOnClickListener(this);
            unknownView.setOnClickListener(this);

            translationEditView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        presenter.onApply(translationView.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

            if (presenter == null) {
                presenter = new SpellCheckPresenter(this);
            }
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getFloatingActionButton().hide();
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_spellcheck);
        presenter.onResume();
    }

    ;

    public void setSoundEnable(boolean isReadWords) {
        soundView.setImageResource(isReadWords ? R.drawable.ic_volume_up_grey600_24dp : R.drawable.ic_volume_off_grey600_24dp);
    }

    public void showWord(Word randomWord, State state) {
        if (randomWord == null) {
            randomWord = new Word("No words selected", "Выбирите слова для изучения");
        }

        String transcription = TextUtils.isEmpty(randomWord.getTranscription()) ? "" : ("[" + randomWord.getTranscription() + "]");
        setNotNullText(wordView, randomWord.getWord());
        setNotNullText(transcriptionView, transcription);
        setNotNullText(translationView, randomWord.getTranslate());


        nextView.setVisibility(View.GONE);
        unknownView.setVisibility(View.GONE);
        wordLayout.setVisibility(View.GONE);
        translationEditView.setText("");



        if (State.start == state) {
            unknownView.setVisibility(View.VISIBLE);
        }
        if (State.fail == state) {
            nextView.setVisibility(View.VISIBLE);
            wordLayout.setVisibility(View.VISIBLE);
        }

    }

    private void setNotNullText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
    }
}
