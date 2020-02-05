package ru.lazard.learnwords.ui.fragments.books.readBook

import org.json.JSONObject
import ru.lazard.learnwords.utils.Utils
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object Translate {


    public fun translateRUtoEN(sourceText: String?) = translate(sourceText, "ru-en")

    public fun translateENtoRU(sourceText: String?) = translate(sourceText, "en-ru")

    public fun translate(sourceText: String?) = if (Utils.isTextEn(sourceText)) {
        translateENtoRU(sourceText)
    } else {
        translateRUtoEN(sourceText)
    }


    /**
     * Api key get from here :
     * https://developers.google.com/apis-explorer/?hl=ru#p/translate/v2/language.translations.translate
     *
     */
    public fun translate(sourceText: String?, language: String): Pair<String?, String?>? {
        val sourceText = sourceText ?: ""
        val url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20150624T074328Z.31cc65b3a28e5003.7d48498834749b7b40d956f8d8f6217b96b37572&lang=" + language + "&format=plain&text=" + URLEncoder.encode(sourceText)
        val httpURLConnection = URL(url).openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "GET"


        httpURLConnection.setRequestProperty("Host", "translate.yandex.net")
        httpURLConnection.setRequestProperty("Connection", "keep-alive")
        httpURLConnection.setRequestProperty("Cache-Control", "max-age=0")
        httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1")
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
        httpURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
        //httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br")
        httpURLConnection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
        //httpURLConnection.setRequestProperty("text", sourceText)


        //httpURLConnection.addRequestProperty("text", sourceText)
        httpURLConnection.doOutput = true
        httpURLConnection.doInput = true
        httpURLConnection.outputStream.use { it.bufferedWriter().write("text=$sourceText") }
        var response:String? = ""
        try {
            response = httpURLConnection.inputStream.reader().readText()
        } catch (e: Throwable) {
            print("Error: responseCode = ${httpURLConnection.responseCode}  responseMessage = ${httpURLConnection.responseMessage}")
            throw  e
        }

        if (httpURLConnection.responseCode != 200) throw IllegalArgumentException("Error: responseCode = ${httpURLConnection.responseCode}  responseMessage = ${httpURLConnection.responseMessage}")

        println(response)


//        response = response.replace("{\"code\":200,\"lang\":\"ru-en\",\"text\":[\"", "")
//        response = response.replace("\"]}", "")

        val jsonObject = JSONObject(response)
        val code = jsonObject.getInt("code")
        if (code != 200 ) throw  java.lang.IllegalArgumentException("Wrong server response code=$code ");
        response = jsonObject.getJSONArray("text").get(0)?.toString()


        return sourceText to response
    }
}