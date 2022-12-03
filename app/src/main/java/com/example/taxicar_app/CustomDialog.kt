package com.example.taxicar_app

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.EditText
import kotlinx.android.synthetic.main.notification_edit.*

data class Notice(var notification: String) //constructor():this("")

class CustomDialog(context: Context)
{
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog()
    {
        dialog.setContentView(R.layout.notification_edit)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val notice = dialog.findViewById<EditText>(R.id.notification_edit)

        dialog.cancel_button.setOnClickListener {
            dialog.dismiss()
        }

        dialog.finish_button.setOnClickListener {
            onClickListener.onClicked(notice.text.toString())
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener
    {
        fun onClicked(notice: String)
    }

}