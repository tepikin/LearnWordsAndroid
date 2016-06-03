package ru.lazard.learnwords.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class DBInit {

    public void writeWordsToDb(SQLiteDatabase db){
        List<Word> words = getWordsList();
        SQLiteStatement statement = db.compileStatement(DBContract.Words.SQL_INSERT);
        db.beginTransaction();

        for (Word word : words) {
            statement.clearBindings();
            statement.bindString(1, word.getWord());
            statement.bindString(2, word.getTranslate());
            statement.bindLong(3, 1);
            statement.bindLong(4, 0);
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    private List<Word> getWordsList() {
            List<Word> wordsList = new ArrayList<>();
            wordsList.add(new Word("abide, abode / abided, abode / abided", "выносить, терпеть"));
            wordsList.add(new Word("arise, arose, arisen", "возникать, появляться, подниматься"));
            wordsList.add(new Word("awake, awakened / awoke, awakened / awoken", "будить, проснуться"));
            wordsList.add(new Word("backslide, backslid, backslidden / backslid", "отказываться от прежних убеждений"));
            wordsList.add(new Word("be, was, were, been", "быть"));
            wordsList.add(new Word("bear, bore, born / borne", "родить"));
            wordsList.add(new Word("beat, beat, beaten / beat", "бить"));
            wordsList.add(new Word("become, became, become", "становиться, делаться"));
            wordsList.add(new Word("begin, began, begun", "начинать"));
            wordsList.add(new Word("bend, bent, bent", "сгибать, гнуть"));
            wordsList.add(new Word("bet, bet / betted, bet / betted", "держать пари"));
            wordsList.add(new Word("bid, bade / bid, bidden / bid", "предлагать цену"));
            wordsList.add(new Word("bind, bound, bound", "связать"));
            wordsList.add(new Word("bite, bit, bitten", "кусать"));
            wordsList.add(new Word("bleed, bled, bled", "кровоточить"));
            wordsList.add(new Word("blow, blew, blown", "дуть"));
            wordsList.add(new Word("break, broke, broken", "ломать"));
            wordsList.add(new Word("breed, bred, bred", "выращивать, воспитывать"));
            wordsList.add(new Word("bring, brought, brought", "приносить"));
            wordsList.add(new Word("broadcast, broadcast / broadcasted, broadcast / broadcasted", "распространять, разбрасывать"));
            wordsList.add(new Word("browbeat, browbeat, browbeaten / browbeat", "запугивать"));
            wordsList.add(new Word("build, built, built", "строить"));
            wordsList.add(new Word("burn, burned / burnt, burned / burnt", "гореть, жечь"));
            wordsList.add(new Word("burst, burst, burst", "взрываться, прорываться"));
            wordsList.add(new Word("bust, busted / bust, busted / bust", "разжаловать"));
            wordsList.add(new Word("buy, bought, bought", "покупать"));
            wordsList.add(new Word("can, could, could", "мочь, уметь"));
            wordsList.add(new Word("cast, cast, cast", "бросить, кинуть, вышвырнуть"));
            wordsList.add(new Word("catch, caught, caught", "ловить, хватать, успеть"));
            wordsList.add(new Word("choose, chose, chosen", "выбирать"));
            wordsList.add(new Word("cling, clung, clung", "цепляться, льнуть"));
            wordsList.add(new Word("clothe, clothed / clad, clothed / clad", "одевать (кого-либо)"));
            wordsList.add(new Word("come, came, come", "приходить"));
            wordsList.add(new Word("cost, cost, cost", "стоить, обходиться (в какую-либо сумму)"));
            wordsList.add(new Word("creep, crept, crept", "ползать"));
            wordsList.add(new Word("cut, cut, cut", "резать, разрезать"));
            wordsList.add(new Word("deal, dealt, dealt", "иметь дело"));
            wordsList.add(new Word("dig, dug, dug", "копать"));
            wordsList.add(new Word("dive, dove / dived, dived", "нырять, погружаться"));
            wordsList.add(new Word("do, did, done", "делать, выполнять"));
            wordsList.add(new Word("draw, drew, drawn", "рисовать, чертить"));
            wordsList.add(new Word("dream, dreamed / dreamt, dreamed / dreamt", "грезить, мечтать"));
            wordsList.add(new Word("drink, drank, drunk", "пить"));
            wordsList.add(new Word("drive, drove, driven", "управлять (авто)"));
            wordsList.add(new Word("dwell, dwelt / dwelled, dwelt / dwelled", "обитать, находиться"));
            wordsList.add(new Word("eat, ate, eaten", "есть, кушать"));
            wordsList.add(new Word("fall, fell, fallen", "падать"));
            wordsList.add(new Word("feed, fed, fed", "кормить"));
            wordsList.add(new Word("feel, felt, felt", "чувствовать"));
            wordsList.add(new Word("fight, fought, fought", "драться, сражаться, бороться"));
            wordsList.add(new Word("find, found, found", "находить"));
            wordsList.add(new Word("fit, fit, fit", "подходить по размеру"));
            wordsList.add(new Word("flee, fled, fled", "убегать, спасаться"));
            wordsList.add(new Word("fling, flung, flung", "бросаться, ринуться"));
            wordsList.add(new Word("fly, flew, flown", "летать"));
            wordsList.add(new Word("forbid, forbade, forbidden", "запрещать"));
            wordsList.add(new Word("forecast, forecast, forecast", "предсказывать, предвосхищать"));
            wordsList.add(new Word("foresee, foresaw, foreseen", "предвидеть"));
            wordsList.add(new Word("foretell, foretold, foretold", "предсказывать, прогнозировать"));
            wordsList.add(new Word("forget, forgot, forgotten", "забывать"));
            wordsList.add(new Word("forgive, forgave, forgiven", "прощать"));
            wordsList.add(new Word("forsake, forsook, forsaken", "покидать"));
            wordsList.add(new Word("freeze, froze, frozen", "замерзать"));
            wordsList.add(new Word("get, got, gotten / got", "получать, достигать"));
            wordsList.add(new Word("give, gave, given", "давать"));
            wordsList.add(new Word("go, went, gone", "идти, ехать"));
            wordsList.add(new Word("grind, ground, ground", "молоть, толочь"));
            wordsList.add(new Word("grow, grew, grown", "расти"));
            wordsList.add(new Word("hang, hung / hanged, hung / hanged", "вешать, развешивать"));
            wordsList.add(new Word("have, has, had, had", "иметь"));
            wordsList.add(new Word("hear, heard, heard", "слышать"));
            wordsList.add(new Word("hew, hewed, hewn / hewed", "рубить"));
            wordsList.add(new Word("hide, hid, hidden", "прятаться, скрываться"));
            wordsList.add(new Word("hit, hit, hit", "ударять, поражать"));
            wordsList.add(new Word("hold, held, held", "держать, удерживать, фиксировать"));
            wordsList.add(new Word("hurt, hurt, hurt", "ранить, причинить боль"));
            wordsList.add(new Word("inlay, inlaid, inlaid", "вкладывать, вставлять, выстилать"));
            wordsList.add(new Word("input, input / inputted, input / inputted", "входить"));
            wordsList.add(new Word("interweave, interwove, interwoven", "воткать"));
            wordsList.add(new Word("keep, kept, kept", "держать, хранить"));
            wordsList.add(new Word("kneel, knelt / kneeled, knelt / kneeled", "становиться на колени"));
            wordsList.add(new Word("knit, knitted / knit, knitted / knit", "вязать"));
            wordsList.add(new Word("know, knew, known", "знать, иметь представление (о чем-либо)"));
            wordsList.add(new Word("lay, laid, laid", "класть, положить"));
            wordsList.add(new Word("lead, led, led", "вести, руководить, управлять"));
            wordsList.add(new Word("lean, leaned / leant, leaned / leant", "опираться, прислоняться"));
            wordsList.add(new Word("leap, leaped / leapt, leaped / leapt", "прыгать, скакать"));
            wordsList.add(new Word("learn, learnt / learned, learnt / learned", "учить"));
            wordsList.add(new Word("leave, left, left", "покидать, оставлять"));
            wordsList.add(new Word("lend, lent, lent", "одалживать, давать взаймы"));
            wordsList.add(new Word("let, let, let", "позволять, предполагать"));
            wordsList.add(new Word("lie, lay, lain", "лежать"));
            wordsList.add(new Word("light, lit / lighted, lit / lighted", "освещать, зажигаться, загораться"));
            wordsList.add(new Word("lose, lost, lost", "терять"));
            wordsList.add(new Word("make, made, made", "делать, производить, создавать"));
            wordsList.add(new Word("may, might, might", "мочь, иметь возможность"));
            wordsList.add(new Word("mean, meant, meant", "значить, иметь ввиду"));
            wordsList.add(new Word("meet, met, met", "встречать"));
            wordsList.add(new Word("miscast, miscast, miscast", "неправильно распределять роли"));
            wordsList.add(new Word("misdeal, misdealt, misdealt", "поступать неправильно"));
            wordsList.add(new Word("misdo, misdid, misdone", "делать что-либо неправильно или небрежно"));
            wordsList.add(new Word("misgive, misgave, misgiven", "внушать недоверия, опасения"));
            wordsList.add(new Word("mishear, misheard, misheard", "ослышаться"));
            wordsList.add(new Word("mishit, mishit, mishit", "промахнуться"));
            wordsList.add(new Word("mislay, mislaid, mislaid", "класть не на место"));
            wordsList.add(new Word("mislead, misled, misled", "ввести в заблуждение"));
            wordsList.add(new Word("misread, misread, misread", "неправильно истолковывать"));
            wordsList.add(new Word("misspell, misspelled / misspelt, misspelled / misspelt", "писать с ошибками"));
            wordsList.add(new Word("misspend, misspent, misspent", "неразумно, зря тратить"));
            wordsList.add(new Word("mistake, mistook, mistaken", "ошибаться"));
            wordsList.add(new Word("misunderstand, misunderstood, misunderstood", "неправильно понимать"));
            wordsList.add(new Word("mow, mowed, mowed / mown", "косить"));
            wordsList.add(new Word("offset, offset, offset", "возмещать, вознаграждать, компенсировать"));
            wordsList.add(new Word("outbid, outbid, outbid", "перебивать цену"));
            wordsList.add(new Word("outdo, outdid, outdone", "превосходить"));
            wordsList.add(new Word("outfight, outfought, outfought", "побеждать в бою"));
            wordsList.add(new Word("outgrow, outgrew, outgrown", "вырастать из"));
            wordsList.add(new Word("output, output / outputted, output / outputted", "выходить"));
            wordsList.add(new Word("outrun, outran, outrun", "перегонять, опережать"));
            wordsList.add(new Word("outsell, outsold, outsold", "продавать лучше или дороже"));
            wordsList.add(new Word("outshine, outshone, outshone", "затмевать"));
            wordsList.add(new Word("overbid, overbid, overbid", "повелевать"));
            wordsList.add(new Word("overcome, overcame, overcome", "компенсировать"));
            wordsList.add(new Word("overdo, overdid, overdone", "пережари(ва)ть"));
            wordsList.add(new Word("overdraw, overdrew, overdrawn", "превышать"));
            wordsList.add(new Word("overeat, overate, overeaten", "объедаться"));
            wordsList.add(new Word("overfly, overflew, overflown", "перелетать"));
            wordsList.add(new Word("overhang, overhung, overhung", "нависать"));
            wordsList.add(new Word("overhear, overheard, overheard", "подслуш(ив)ать"));
            wordsList.add(new Word("overlay, overlaid, overlaid", "покры(ва)ть"));
            wordsList.add(new Word("overpay, overpaid, overpaid", "переплачивать"));
            wordsList.add(new Word("override, overrode, overridden", "отменять, аннулировать"));
            wordsList.add(new Word("overrun, overran, overrun", "переливаться через край"));
            wordsList.add(new Word("oversee, oversaw, overseen", "надзирать за"));
            wordsList.add(new Word("overshoot, overshot, overshot", "расстрелять"));
            wordsList.add(new Word("oversleep, overslept, overslept", "проспать, заспаться"));
            wordsList.add(new Word("overtake, overtook, overtaken", "догонять"));
            wordsList.add(new Word("overthrow, overthrew, overthrown", "свергать"));
            wordsList.add(new Word("partake, partook, partaken", "принимать участие"));
            wordsList.add(new Word("pay, paid, paid", "платить"));
            wordsList.add(new Word("plead, pleaded / pled, pleaded / pled", "обращаться к суду"));
            wordsList.add(new Word("prepay, prepaid, prepaid", "платить вперед"));
            wordsList.add(new Word("prove, proved, proven / proved", "доказывать"));
            wordsList.add(new Word("put, put, put", "класть, ставить, размещать"));
            wordsList.add(new Word("quit, quit / quitted, quit / quitted", "выходить, покидать, оставлять"));
            wordsList.add(new Word("read, read, read", "читать"));
            wordsList.add(new Word("rebind, rebound, rebound", "перевязывать"));
            wordsList.add(new Word("rebuild, rebuilt, rebuilt", "перестроить"));
            wordsList.add(new Word("recast, recast, recast", "изменять, перестраивать"));
            wordsList.add(new Word("redo, redid, redone", "делать вновь, переделывать"));
            wordsList.add(new Word("rehear, reheard, reheard", "слушать вторично"));
            wordsList.add(new Word("remake, remade, remade", "переделывать"));
            wordsList.add(new Word("rend, rent, rent", "раздирать"));
            wordsList.add(new Word("repay, repaid, repaid", "отдавать долг"));
            wordsList.add(new Word("rerun, reran, rerun", "выполнять повторно"));
            wordsList.add(new Word("resell, resold, resold", "перепродавать"));
            wordsList.add(new Word("reset, reset, reset", "возвращать"));
            wordsList.add(new Word("resit, resat, resat", "пересиживать"));
            wordsList.add(new Word("retake, retook, retaken", "забирать"));
            wordsList.add(new Word("retell, retold, retold", "пересказывать"));
            wordsList.add(new Word("rewrite, rewrote, rewritten", "перезаписать"));
            wordsList.add(new Word("rid, rid, rid", "избавлять"));
            wordsList.add(new Word("ride, rode, ridden", "ездить верхом"));
            wordsList.add(new Word("ring, rang, rung", "звонить"));
            wordsList.add(new Word("rise, rose, risen", "подняться"));
            wordsList.add(new Word("run, ran, run", "бегать"));
            wordsList.add(new Word("saw, sawed, sawed / sawn", "пилить"));
            wordsList.add(new Word("say, said, said", "сказать, заявить"));
            wordsList.add(new Word("see, saw, seen", "видеть"));
            wordsList.add(new Word("seek, sought, sought", "искать"));
            wordsList.add(new Word("sell, sold, sold", "продавать"));
            wordsList.add(new Word("send, sent, sent", "посылать"));
            wordsList.add(new Word("set, set, set", "сажать, ставить, устанавливать, садиться (о солнце)"));
            wordsList.add(new Word("sew, sewed, sewn / sewed", "шить"));
            wordsList.add(new Word("shake, shook, shaken", "трясти"));
            wordsList.add(new Word("shave, shaved, shaved / shaven", "бриться"));
            wordsList.add(new Word("shear, sheared, sheared / shorn", "стричь"));
            wordsList.add(new Word("shed, shed, shed", "проливать"));
            wordsList.add(new Word("shine, shined / shone, shined / shone", "светить, сиять, озарять"));
            wordsList.add(new Word("shoot, shot, shot", "стрелять, давать побеги"));
            wordsList.add(new Word("show, showed, shown / showed", "показывать"));
            wordsList.add(new Word("shrink, shrank / shrunk, shrunk", "сокращаться, сжиматься"));
            wordsList.add(new Word("shut, shut, shut", "закрывать, запирать, затворять"));
            wordsList.add(new Word("sing, sang, sung", "петь"));
            wordsList.add(new Word("sink, sank / sunk, sunk", "тонуть, погружаться (под воду)"));
            wordsList.add(new Word("sit, sat, sat", "сидеть"));
            wordsList.add(new Word("slay, slew / slayed, slain / slayed", "убивать"));
            wordsList.add(new Word("sleep, slept, slept", "спать"));
            wordsList.add(new Word("slide, slid, slid", "скользить"));
            wordsList.add(new Word("sling, slung, slung", "бросать, швырять"));
            wordsList.add(new Word("slink, slunk, slunk", "красться, идти крадучись"));
            wordsList.add(new Word("slit, slit, slit", "разрезать, рвать в длину"));
            wordsList.add(new Word("smell, smelled / smelt, smelled / smelt", "пахнуть, нюхать"));
            wordsList.add(new Word("sneak, snuck / sneaked, snuck / sneaked", "красться, увиливать, избегать"));
            wordsList.add(new Word("sow, sowed, sown / sowed", "сеять"));
            wordsList.add(new Word("speak, spoke, spoken", "говорить"));
            wordsList.add(new Word("speed, sped / speeded, sped / speeded", "ускорять, спешить"));
            wordsList.add(new Word("spell, spelled / spelt, spelled / spelt", "писать или читать по буквам"));
            wordsList.add(new Word("spend, spent, spent", "тратить, расходовать"));
            wordsList.add(new Word("spill, spilled / spilt, spilled / spilt", "проливать, разливать"));
            wordsList.add(new Word("spin, spun, spun", "прясть"));
            wordsList.add(new Word("spit, spit / spat, spit / spat", "плевать"));
            wordsList.add(new Word("split, split, split", "расщеплять"));
            wordsList.add(new Word("spoil, spoiled / spoilt, spoiled / spoilt", "портить"));
            wordsList.add(new Word("spread, spread, spread", "распространять(ся), простирать(ся)"));
            wordsList.add(new Word("spring, sprang / sprung, sprung", "отскочить, прыгать, скакать, возникать"));
            wordsList.add(new Word("stand, stood, stood", "стоять"));
            wordsList.add(new Word("steal, stole, stolen", "воровать, красть"));
            wordsList.add(new Word("stick, stuck, stuck", "уколоть, приклеить"));
            wordsList.add(new Word("sting, stung, stung", "жалить"));
            wordsList.add(new Word("stink, stunk / stank, stunk", "вонять"));
            wordsList.add(new Word("strew, strewed, strewn / strewed", "усеять, устлать"));
            wordsList.add(new Word("stride, strode, stridden", "шагать, наносить удар"));
            wordsList.add(new Word("strike, struck, struck", "ударить, бить, бастовать"));
            wordsList.add(new Word("string, strung, strung", "нанизать, натянуть"));
            wordsList.add(new Word("strive, strove / strived, striven / strived", "стараться"));
            wordsList.add(new Word("sublet, sublet, sublet", "передавать в субаренду"));
            wordsList.add(new Word("swear, swore, sworn", "клясться, присягать"));
            wordsList.add(new Word("sweep, swept, swept", "мести, подметать, сметать"));
            wordsList.add(new Word("swell, swelled, swollen / swelled", "разбухать"));
            wordsList.add(new Word("swim, swam, swum", "плавать, плыть"));
            wordsList.add(new Word("swing, swung, swung", "качать, раскачивать, вертеть"));
            wordsList.add(new Word("take, took, taken", "брать, взять"));
            wordsList.add(new Word("teach, taught, taught", "учить, обучать"));
            wordsList.add(new Word("tear, tore, torn", "рвать"));
            wordsList.add(new Word("tell, told, told", "рассказать"));
            wordsList.add(new Word("think, thought, thought", "думать"));
            wordsList.add(new Word("throw, threw, thrown", "кидать, бросать"));
            wordsList.add(new Word("thrust, thrust, thrust", "колоть, пронзать"));
            wordsList.add(new Word("tread, trod, trodden / trod", "ступать"));
            wordsList.add(new Word("unbend, unbent, unbent", "выпрямляться, разгибаться"));
            wordsList.add(new Word("underbid, underbid, underbid", "снижать цену"));
            wordsList.add(new Word("undercut, undercut, undercut", "сбивать цены"));
            wordsList.add(new Word("undergo, underwent, undergone", "испытывать, переносить"));
            wordsList.add(new Word("underlie, underlay, underlain", "лежать в основе"));
            wordsList.add(new Word("underpay, underpaid, underpaid", "оплачивать слишком низко"));
            wordsList.add(new Word("undersell, undersold, undersold", "продавать дешевле"));
            wordsList.add(new Word("understand, understood, understood", "понимать, постигать"));
            wordsList.add(new Word("undertake, undertook, undertaken", "предпринять"));
            wordsList.add(new Word("underwrite, underwrote, underwritten", "подписываться"));
            wordsList.add(new Word("undo, undid, undone", "уничтожать сделанное"));
            wordsList.add(new Word("unfreeze, unfroze, unfrozen", "размораживать"));
            wordsList.add(new Word("unsay, unsaid, unsaid", "брать назад свои слова"));
            wordsList.add(new Word("unwind, unwound, unwound", "развертывать"));
            wordsList.add(new Word("uphold, upheld, upheld", "поддерживать"));
            wordsList.add(new Word("upset, upset, upset", "опрокинуться"));
            wordsList.add(new Word("wake, woke / waked, woken / waked", "просыпаться"));
            wordsList.add(new Word("waylay, waylaid, waylaid", "подстерегать"));
            wordsList.add(new Word("wear, wore, worn", "носить (одежду), снашиваться"));
            wordsList.add(new Word("weave, wove / weaved, woven / weaved", "ткать"));
            wordsList.add(new Word("wed, wed / wedded, wed / wedded", "жениться, выдавать замуж"));
            wordsList.add(new Word("weep, wept, wept", "плакать, рыдать"));
            wordsList.add(new Word("wet, wet / wetted, wet / wetted", "мочить, увлажнять"));
            wordsList.add(new Word("win, won, won", "победить, выиграть"));
            wordsList.add(new Word("wind, wound, wound", "заводить (механизм)"));
            wordsList.add(new Word("withdraw, withdrew, withdrawn", "взять назад, отозвать"));
            wordsList.add(new Word("withhold, withheld, withheld", "воздерживаться, отказывать"));
            wordsList.add(new Word("withstand, withstood, withstood", "противостоять"));
            wordsList.add(new Word("wring, wrung, wrung", "скрутить, сжимать"));
            wordsList.add(new Word("write, wrote, written", "писать"));
            return wordsList;
        }
    }
