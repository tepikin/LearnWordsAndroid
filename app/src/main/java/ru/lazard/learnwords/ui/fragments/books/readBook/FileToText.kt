package ru.lazard.learnwords.ui.fragments.books.readBook

import android.content.Context
import android.net.Uri
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class FileToText(val context: Context) {
    fun toText(bookUri: Uri?): String {
        bookUri ?: return "";
        val bytes = context?.contentResolver?.openInputStream(bookUri)?.readBytes()

        var text = bytes?.let { String(it)}

        if (bytes != null) {
            val unzipFiles = unzip(bytes)
            val unzipFilesFb2 = unzipFiles.filter { it.first?.endsWith(".fb2")?:false }
            val unzipFilesTxt = unzipFiles.filter { it.first?.endsWith(".txt")?:false }
            val unzipFilesHtml = unzipFiles.filter { (it.first?.endsWith(".html")?:false) || (it.first?.endsWith(".htm")?:false) }

            if (unzipFiles.size == 1) {
                text = unzipFiles.first().second
            } else if (unzipFilesFb2.size == 1) {
                text = unzipFiles.first().second
            } else if (unzipFilesTxt.size == 1) {
                text = unzipFiles.first().second
            } else if (unzipFilesHtml.size == 1) {
                text = unzipFiles.first().second
            }
        }

        if (text != null) {
            if (text.startsWith("<?xml") ||
                    text.startsWith("<!DOCTYPE") ||
                    text.startsWith("<html")
            ) {
                text = text?.replace("<[?/qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM][^>]*>".toRegex(), "")
            }
        }

        return text ?: "";

    }


    fun unzip(bytes: ByteArray): List<Pair<String?, String?>> {
        val resultFiles = mutableListOf<Pair<String?, String?>>()
        try {
            val zin = ZipInputStream(ByteArrayInputStream(bytes));
            var ze: ZipEntry? = zin.getNextEntry();
            while (ze != null) {
                if (!ze.isDirectory()) {
                    try {
                        val name: String? = ze?.getName()
//                        val out = ByteArrayOutputStream()
//                        zin.copyTo(out)
                        val readText: String? = zin?.reader()?.readText()
                        if (readText != null) {
                            resultFiles.add(name to readText)
                        }
                    }catch (e:Throwable){
                        e.printStackTrace()
                    }
                    zin.closeEntry();
                }
                ze = zin.getNextEntry();
            }
            zin.close();
        } catch (e: Exception) {
            System.out.println(e);
        }
        return resultFiles;
    }
}