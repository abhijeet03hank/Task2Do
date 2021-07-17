package com.hank.task2do.util

import android.app.Activity
import android.app.AlertDialog
import com.hank.task2do.R

class LoadingDialog(val activity: Activity) {
    private lateinit var isDialog: AlertDialog
    fun startLoadingDialog(){
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_bar,null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun dismissDialog(){
        isDialog.dismiss()
    }


}
