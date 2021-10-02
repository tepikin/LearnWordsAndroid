package ru.lazard.learnwords.ui.fragments.books.readBook

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import ru.lazard.learnwords.model.Word
import ru.lazard.learnwords.utils.Utils

object Translate {


    public fun translateRUtoEN(sourceText: String?) = translate(sourceText, translationRuToEn)

    public fun translateENtoRU(sourceText: String?) = translate(sourceText, translationEnToRu)

    public fun translate(sourceText: String?) = if (Utils.isTextEn(sourceText)) {
        translateENtoRU(sourceText)
    } else {
        translateRUtoEN(sourceText)
    }


    private lateinit var translationEnToRu: Translator
    private lateinit var translationRuToEn: Translator

    init {

        translationEnToRu = Translation.getClient(TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.RUSSIAN)
            .setExecutor { it.run() }
            .build())

        translationRuToEn = Translation.getClient(TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.RUSSIAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .setExecutor { it.run() }
            .build())
    }


    public fun translateWordTwice(
        sourceText: String?,
        language: Translator = if (Utils.isTextEn(sourceText)) translationEnToRu else translationRuToEn
    ): Word {
        var translate: String? = translate(sourceText, language)?.second
        val isWordEn = Utils.isTextEn(sourceText)
        return Word(
            6,
            0,
            1,
            null,
            if (isWordEn) translate else sourceText,
            0,
            if (isWordEn) sourceText else translate
        )
    }


    private fun translate(sourceText: String?, translator: Translator): Pair<String?, String?>? {
        var result = ""
        var resultError: Throwable? = null
        translator.translate(sourceText)
            .addOnSuccessListener { translatedText ->
                result = translatedText
            }
            .addOnFailureListener { exception ->
                resultError = exception
            }
        resultError?.let { throw  it }
        return sourceText to result
    }

}


//    /**
//     * Api key get from here :
//     * https://developers.google.com/apis-explorer/?hl=ru#p/translate/v2/language.translations.translate
//     *
//     */
//    public fun translate(sourceText: String?, language: String): Pair<String?, String?>? {
//        val sourceText = sourceText ?: ""
//        val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=$language&dt=t&q="+ URLEncoder.encode(sourceText)
//
//
//       // val url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20150624T074328Z.31cc65b3a28e5003.7d48498834749b7b40d956f8d8f6217b96b37572&lang=" + language + "&format=plain&text=" + URLEncoder.encode(sourceText)
//        val httpURLConnection = URL(url).openConnection() as HttpURLConnection
//        httpURLConnection.requestMethod = "GET"
//
//
//        httpURLConnection.setRequestProperty("Host", "translate.yandex.net")
//        httpURLConnection.setRequestProperty("Connection", "keep-alive")
//        httpURLConnection.setRequestProperty("Cache-Control", "max-age=0")
//        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1")
//        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
//        httpURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//        //httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
//        httpURLConnection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
//        //httpURLConnection.setRequestProperty("text", sourceText)
//
//
//        //httpURLConnection.addRequestProperty("text", sourceText)
//        httpURLConnection.doOutput = true
//        httpURLConnection.doInput = true
//        httpURLConnection.outputStream.use { it.bufferedWriter().write("text=$sourceText") }
//        var response:String? = ""
//        try {
//            response = httpURLConnection.inputStream.reader().readText()
//        } catch (e: Throwable) {
//            print("Error: responseCode = ${httpURLConnection.responseCode}  responseMessage = ${httpURLConnection.responseMessage}")
//            throw  e
//        }
//
//        if (httpURLConnection.responseCode != 200) throw IllegalArgumentException("Error: responseCode = ${httpURLConnection.responseCode}  responseMessage = ${httpURLConnection.responseMessage}")
//
//        println(response)
//
//
////        response = response.replace("{\"code\":200,\"lang\":\"ru-en\",\"text\":[\"", "")
////        response = response.replace("\"]}", "")
//
//        val jsonObject = JSONObject(response)
//        val code = jsonObject.getInt("code")
//        if (code != 200 ) throw  java.lang.IllegalArgumentException("Wrong server response code=$code ");
//        response = jsonObject.getJSONArray("text").get(0)?.toString()
//
//
//        return sourceText to response
//    }


//    /**
//     * Api key get from here :
//     * https://developers.google.com/apis-explorer/?hl=ru#p/translate/v2/language.translations.translate
//     *
//     */
//    fun translateWord(sourceText: String?, language: String =  if (Utils.isTextEn(sourceText))"en-ru" else "ru-en"): Word {
//        val sourceText = sourceText ?: ""
//        val url = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20200207T080046Z.2d6eb7e1b9eba162.583a8615b72a4a965bbbff83ad4595feab6bd339&lang=" + language + "&flags=4&text=" + URLEncoder.encode(sourceText)
//        val httpURLConnection = URL(url).openConnection() as HttpURLConnection
//        httpURLConnection.requestMethod = "GET"
//
//
//        httpURLConnection.setRequestProperty("Host", "translate.yandex.net")
//        httpURLConnection.setRequestProperty("Connection", "keep-alive")
//        httpURLConnection.setRequestProperty("Cache-Control", "max-age=0")
//        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1")
//        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
//        httpURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//        //httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
//        httpURLConnection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
//        //httpURLConnection.setRequestProperty("text", sourceText)
//
//
//        //httpURLConnection.addRequestProperty("text", sourceText)
//        httpURLConnection.doOutput = true
//        httpURLConnection.doInput = true
//        httpURLConnection.outputStream.use { it.bufferedWriter().write("text=$sourceText") }
//        var response:String? = ""
//        try {
//            response = httpURLConnection.inputStream.reader().readText()
//        } catch (e: Throwable) {
//            print("Error: responseCode = ${httpURLConnection.responseCode}  responseMessage = ${httpURLConnection.responseMessage}")
//            throw  e
//        }
//
//        if (httpURLConnection.responseCode != 200) throw IllegalArgumentException("Error: responseCode = ${httpURLConnection.responseCode}  responseMessage = ${httpURLConnection.responseMessage}")
//
//        println(response)
//
//
////        response = response.replace("{\"code\":200,\"lang\":\"ru-en\",\"text\":[\"", "")
////        response = response.replace("\"]}", "")
//
//        val jsonObject = JSONObject(response)
//        val trObject = jsonObject?.getJSONArraySafe("def")?.toJsonObjectsList()?.firstOrNull()
//        val transcription =trObject?.getStringSafe("ts")
//        var translate =trObject?.getJSONArraySafe("tr")?.toJsonObjectsList()?.firstOrNull()?.getStringSafe("text")
//
//        val isTextEn = Utils.isTextEn(sourceText)
//
//        if (isTextEn){
//            translate = jsonObject?.getJSONArraySafe("def")?.toJsonObjectsList()?.flatMap { it?.getJSONArraySafe("tr")?.toJsonObjectsList()?: emptyList() }?.flatMap {
//                linkedSetOf(it.getStringSafe("text"),*(it.getJSONArraySafe("syn")?.toJsonObjectsList()?.map{it.getStringSafe("text")}?.toTypedArray()?: emptyList<String>().toTypedArray()))
//            }?.joinToString("; ")
//
//        }
//
//        return Word(6, 0, 1, if (isTextEn) transcription else null, if (isTextEn)translate else sourceText , 0, if (isTextEn)sourceText else translate);
//    }

//dict.1.1.20200207T080046Z.2d6eb7e1b9eba162.583a8615b72a4a965bbbff83ad4595feab6bd339
//}
//
//fun JSONObject.getStringSafe(name:String) = if (this.has(name))    this.getString(name) else null
//fun JSONObject.getJSONArraySafe(name:String) = if (this.has(name))    this.getJSONArray(name) else null
//
//fun JSONArray.toJsonObjectsList():List<JSONObject>{
//    val result = mutableListOf<JSONObject>()
//    for(i in 0 until length()){
//        result.add(this.getJSONObject(i))
//    }
//    return result
//}