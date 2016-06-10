package ru.lazard.learnwords.ui.fragments.checkWords.checkTranslate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

/**
 * Created by Egor on 02.06.2016.
 */
public class CheckTranslateFragment extends Fragment implements View.OnClickListener {
    public enum State {start, fail}

    private View nextView;
    private CheckTranslatePresenter presenter;
    private ImageView soundView;
    private TextView transcriptionView;
    private View unknownView;
    private AppCompatButton[] variantViews;
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
        for (int i = 0; i < variantViews.length; i++) {
            if (variantViews[i]==v){
                presenter.onVariantViewClick(i);
            }
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

    public AppCompatButton[] getVariantViews() {
        return variantViews;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_check_translate, container, false);
            wordView = (TextView) view.findViewById(R.id.word);
            transcriptionView = (TextView) view.findViewById(R.id.transcription);
            soundView = (ImageView) view.findViewById(R.id.sound);
            nextView = (View) view.findViewById(R.id.next);
            unknownView = (View) view.findViewById(R.id.unknown);

            AppCompatButton variantView1 = (AppCompatButton) view.findViewById(R.id.variant1);
            AppCompatButton variantView2 = (AppCompatButton) view.findViewById(R.id.variant2);
            AppCompatButton variantView3 = (AppCompatButton) view.findViewById(R.id.variant3);
            AppCompatButton variantView4 = (AppCompatButton) view.findViewById(R.id.variant4);
            variantViews = new AppCompatButton[]{variantView1, variantView2, variantView3, variantView4};

            soundView.setOnClickListener(this);
            nextView.setOnClickListener(this);
            unknownView.setOnClickListener(this);
            for (AppCompatButton variantView : variantViews) {
                variantView.setOnClickListener(this);
            }


            if (presenter == null) {
                presenter = new CheckTranslatePresenter(this);
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
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_checkTranslate);
        presenter.onResume();
    }

    ;

    public void setSoundEnable(boolean isReadWords) {
        soundView.setImageResource(isReadWords ? R.drawable.ic_volume_up_grey600_24dp : R.drawable.ic_volume_off_grey600_24dp);
    }

    public void showWord(Word randomWord, State state, List<Word> variants, int rightVariant, int choice) {
        if (randomWord == null) {
            randomWord = new Word("No words selected", "Выбирите слова для изучения");
        }

        String transcription = TextUtils.isEmpty(randomWord.getTranscription()) ? "" : ("[" + randomWord.getTranscription() + "]");
        setNotNullText(wordView, randomWord.getWord());
        setNotNullText(transcriptionView, transcription);
        for (View variantView : variantViews) {
            variantView.setVisibility(View.GONE);
        }
        nextView.setVisibility(View.GONE);
        unknownView.setVisibility(View.GONE);

        for (int i = 0; i < variants.size()&&i < variantViews.length; i++) {
            variantViews[i].setVisibility(View.VISIBLE);
            variantViews[i].setText(variants.get(i).getTranslate());
            variantViews[i].setTextColor(getResources().getColor(R.color.colorSecondaryText));
        }



        if (State.start == state) {
            unknownView.setVisibility(View.VISIBLE);
            for (int i = 0; i < variants.size()&&i < variantViews.length; i++) {
                variantViews[i].clearAnimation();
            }
        }
        if (State.fail == state) {
            if (rightVariant>=0&&rightVariant<=variantViews.length) {
                variantViews[rightVariant].setTextColor(getResources().getColor(R.color.colorGreen));
            }
            if (choice>=0&&choice<=variantViews.length) {
                variantViews[choice].setTextColor(getResources().getColor(R.color.colorRed));

            }

            nextView.setVisibility(View.VISIBLE);
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
