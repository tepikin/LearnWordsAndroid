package ru.lazard.learnwords.model;

import android.content.Context;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.events.EventDictionaryAdded;
import ru.lazard.learnwords.model.events.EventDictionaryRemoved;
import ru.lazard.learnwords.model.events.EventDictionaryUpdate;
import ru.lazard.learnwords.model.events.EventWordAdded;
import ru.lazard.learnwords.model.events.EventWordRemoved;
import ru.lazard.learnwords.model.events.EventWordStatusChanged;
import ru.lazard.learnwords.model.events.EventWordUpdated;
import ru.lazard.learnwords.model.events.EventWordsListStatusChanged;
import ru.lazard.learnwords.utils.Utils;

/**
 * Created by Egor on 07.06.2016.
 */
public class Model {
    private static Model instance;
    private List<Word> words = new ArrayList<>();
    private List<Dictionary> dictionaries = new ArrayList<>();

    public List<Dictionary> getDictionaries() {
        return dictionaries;
    }

    private Model(Context context) {
        // TODO un implemented yaeat
    }

    public static final Model getInstance() {
        if (instance == null) throw new IllegalStateException("call Model.init(); first;");
        return instance;
    }

    public static <T> List<T> getRandomSubItems(int count, List<T> list) {
        if (list == null) return null;
        if (count <= 0) return new ArrayList<>();
        if (list.size() <= count) return new ArrayList<>(list);

        List<T> resultList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            T randomItem = list.get(Utils.randomInt(list.size()));
            resultList.add(randomItem);
        }
        return resultList;
    }

    public static Throwable init(Context context) {
        if (instance != null) return null;
        instance = new Model(context);
        if (instance .getWords().size()<= 0) {try{instance.initDatabase();}catch (Throwable e){e.printStackTrace();return e;}}
        return null;
    }

    public void addWord(Word word) {
        if (word == null) return;
        int id = 1;
        if (words.size() > 0) {
            id = words.get(words.size() - 1).getId();
            id++;
        }
        word.setId(id);
        DAO.insertWord(word);
        Model.getInstance().getWords().add(word);
        EventBus.getDefault().post(this);
        EventBus.getDefault().post(new EventWordAdded(word));
    }

    public Word getRandomWordByStatus(int status) {
        List<Word> wordsWithStatus = getWordsWithStatus(status);
        if (wordsWithStatus.size() <= 0) return null;
        int randomInt = Utils.randomInt(wordsWithStatus.size());
        return wordsWithStatus.get(randomInt);
    }

    private int currentWordIndex = 0;
    public Word getNextWordByStatus(int status) {
        List<Word> wordsWithStatus = getWordsWithStatus(status);
        if (wordsWithStatus.size() <= 0) return null;
        currentWordIndex--;
        if(currentWordIndex<0)currentWordIndex=wordsWithStatus.size()-1;
        return wordsWithStatus.get(currentWordIndex);
    }

    public Word getRandomWordForLearning() {
        Word randomWordByStatus = getRandomWordByStatus(1);
        if (randomWordByStatus == null) {
            randomWordByStatus = getRandomWordByStatus(0);
        }
        return randomWordByStatus;
    }

    public Word getRandomWordWithStatusLoverOrEqualThen(int status) {
        for (int i = status; i >= 0; i--) {
            Word randomWordByStatus = getRandomWordByStatus(i);
            if (randomWordByStatus != null) return randomWordByStatus;
        }
        return null;
    }

    public Word getNextWordWithStatusLoverOrEqualThen(int status) {
        for (int i = status; i >= 0; i--) {
            Word nextWordByStatus = getNextWordByStatus(i);
            if (nextWordByStatus != null) return nextWordByStatus;
        }
        return null;
    }

    public Word getWordById(int wordId) {
        for (Word word : words) {
            if (wordId == word.getId()) {
                return word;
            }
        }
        return null;
    }

    public List<Word> getWords() {
        return words;
    }

    void setWords(List<Word> words) {
        this.words = words;
    }

    public int getWordsCountByStatus(int status) {
        int count = 0;
        for (Word word : words) {
            if (word.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public List<Word> getWordsWithStatus(int status) {
        List<Word> wordsByStatus = new ArrayList<>();
        for (Word word : words) {
            if (word.getStatus() == status) {
                wordsByStatus.add(word);
            }
        }
        return wordsByStatus;
    }
    public List<Word> getWordsByDictionary(int dictionaryId) {
        if (dictionaryId==Dictionary.NO_DICTIONARY_ID){
            return new ArrayList<>(words);
        }
        List<Word> wordsByStatus = new ArrayList<>();
        for (Word word : words) {
            if (word.getDictionaryId() == dictionaryId) {
                wordsByStatus.add(word);
            }
        }
        return wordsByStatus;
    }


    public void initDatabase() throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {
        long time = System.currentTimeMillis();
        int wordsCount = DAO.getWordsCount();
        if (wordsCount <= 0) {
            DAO.addDbWordsFromXml(R.xml.words);
        }
        if (Model.getInstance().getWords().size() <= 0) {
            Model.getInstance().setWords(DAO.getAllWords());
        }
        if (Model.getInstance().getDictionaries().size() <= 0) {
            Model.getInstance().setDictionaries(DAO.getAllDictionaries());
        }
    }

    public void removeWord(Word word) {
        if (word == null) return;
        Model.getInstance().getWords().remove(word);
        DAO.removeWordsById(word.getId());
        EventBus.getDefault().post(this);
        EventBus.getDefault().post(new EventWordRemoved(word));
    }

    public void setLearnWordsListAll() {
        List<Word> wordsList = new ArrayList<>();
        for (Word word : words) {
            if (word.getStatus() == Word.STATUS_NONE || word.getStatus() == Word.STATUS_LEARN) {
                word.setStatus(Word.STATUS_LEARN);
                wordsList.add(word);
            }
        }
        if (wordsList.size() > 0) {
            DAO.setLearnWordsListAll();
            EventBus.getDefault().post(this);
            EventBus.getDefault().post(new EventWordsListStatusChanged(wordsList));
        }
    }

    public void setLearnWordsListByDifficult(int difficulty) {
        List<Word> wordsList = new ArrayList<>();
        for (Word word : words) {
            if (word.getStatus() == Word.STATUS_NONE || word.getStatus() == Word.STATUS_LEARN) {
                word.setStatus(word.getDictionaryId() == difficulty ? Word.STATUS_LEARN : Word.STATUS_NONE);
                wordsList.add(word);
            }
        }
        if (wordsList.size() > 0) {
            DAO.setLearnWordsListByDifficult(difficulty);
            EventBus.getDefault().post(this);
            EventBus.getDefault().post(new EventWordsListStatusChanged(wordsList));
        }
    }

    public void setLearnWordsListClear() {
        List<Word> wordsList = new ArrayList<>();
        for (Word word : words) {
            if (word.getStatus() == Word.STATUS_NONE || word.getStatus() == Word.STATUS_LEARN) {
                word.setStatus(Word.STATUS_NONE);
                wordsList.add(word);
            }
        }
        if (wordsList.size() > 0) {
            DAO.setLearnWordsListClear();
            EventBus.getDefault().post(this);
            EventBus.getDefault().post(new EventWordsListStatusChanged(wordsList));
        }
    }

    public void setWordStatus(int status, List<Word> wordList) {
        for (Word randomSubItem : wordList) {
            randomSubItem.setStatus(status);
        }
        DAO.setStatusForWords(status, wordList);
        EventBus.getDefault().post(this);
        EventBus.getDefault().post(new EventWordsListStatusChanged(wordList));
    }

    public void setWordStatus(Word word, int status) {
        if (word == null) return;
        int oldStatus = word.getStatus();
        if (oldStatus != status) {
            word.setStatus(status);
            DAO.setStatusForWord(word.getId(), Word.STATUS_CHECK_TRANSLATE);
            EventBus.getDefault().post(this);
            EventBus.getDefault().post(new EventWordStatusChanged(oldStatus, status, word));
        }
    }

    public void updateWord(Word word, int status, String transcription, String translate, String wordText) {
        if (word == null) return;
        if (TextUtils.isEmpty(wordText)) throw new IllegalArgumentException("Word text is null");
        if (word.getStatus() != status ||
                !Utils.equals(word.getTranscription(), transcription) ||
                !Utils.equals(word.getTranslate(), translate) ||
                !Utils.equals(word.getWord(), wordText)
                ) {
            Word fromWord = new Word(word);
            word.setStatus(status);
            word.setTranscription(transcription);
            word.setTranslate(translate);
            word.setWord(wordText);
            DAO.updateWord(word);
            EventBus.getDefault().post(this);
            EventBus.getDefault().post(new EventWordUpdated(fromWord, word));
        }
    }

    public void removeDictionary(Dictionary dictionary) {
        if (dictionary == null) return;
        Model.getInstance().getDictionaries().remove(dictionary);
        DAO.removeDictionaryById(dictionary.getId());
        DAO.removeWordsByDictionary(dictionary.getId());
        EventBus.getDefault().post(this);
        EventBus.getDefault().post(new EventDictionaryRemoved(dictionary));
    }

    public void renameDictionary(Dictionary dictionary, String text) {
        if (dictionary == null) return;
        if (text == null) text="";
        dictionary.setName(text);
        DAO.updateDictionary(dictionary);
        EventBus.getDefault().post(this);
        EventBus.getDefault().post(new EventDictionaryUpdate(dictionary));
    }

    public void setDictionaries(List<Dictionary> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public void switchWordStatusForDictionary(int fromStatus,int toStatus, int dictionaryId) {
        List<Word> wordsList = new ArrayList<>();

        for (Word word : words) {
            if (word.getStatus() ==fromStatus&&word.getDictionaryId()==dictionaryId) {
                word.setStatus(toStatus);
                wordsList.add(word);
            }
        }
        if (wordsList.size() > 0) {
            DAO.switchWordStatusForDictionary(fromStatus,toStatus,dictionaryId);
            EventBus.getDefault().post(this);
            EventBus.getDefault().post(new EventWordsListStatusChanged(wordsList));
        }
    }

    public void addDictionary(Dictionary dictionary) {
        if (dictionary == null) return;
        int id = 1;
        if (dictionaries.size() > 0) {
            id = dictionaries.get(dictionaries.size() - 1).getId();
            id++;
        }
        dictionary.setId(id);
        DAO.insertDictionary(dictionary);
        Model.getInstance().getDictionaries().add(dictionary);
        EventBus.getDefault().post(this);
        EventBus.getDefault().post(new EventDictionaryAdded(dictionary));
    }
}
