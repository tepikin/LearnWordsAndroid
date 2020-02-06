package ru.lazard.learnwords.ui.activities.main

import android.content.Intent
import android.net.Uri
import android.util.Log
import ru.lazard.learnwords.ui.fragments.books.readBook.ReadBookFragment

class StartIntentInvoker(val activity: MainActivity?){
    operator fun invoke(){
        val intent = activity?.intent
        if (intent?.action==  Intent.ACTION_SEND ||intent?.action==  Intent.ACTION_OPEN_DOCUMENT||intent?.action==  Intent.ACTION_VIEW){
            var uri = intent?.data
            if (uri !=null){activity?.addFragment(ReadBookFragment.newInstance(uri, null), true);return}
            uri = intent?.extras?.get(Intent.EXTRA_STREAM) as? Uri
            if (uri !=null){activity?.addFragment(ReadBookFragment.newInstance(uri, null), true);return}

            val params = intent.extras?.keySet()?.map { intent?.extras?.get(it)   }

            uri = params?.map { it as? Uri }?.filterNotNull()?.firstOrNull()
            if (uri !=null){activity?.addFragment(ReadBookFragment.newInstance(uri, null), true);return}

            uri = params?.map { it as? String }?.filterNotNull()?.firstOrNull()?.let { Uri.parse(it) }
            if (uri !=null){activity?.addFragment(ReadBookFragment.newInstance(uri, null), true);return}

            Log.e("intent",""+intent.extras?.keySet()?.map { it to intent.extras.get(it) }?.joinToString { "\n" })
        }
    }
}