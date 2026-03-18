package com.example.mmg.session

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Handler
import android.os.Looper

class InactivityLogoutManager(
    private val activity: Activity,
    private val timeoutMs: Long = 30_000L,
    private val warningSeconds: Int = 10
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isEnabled = true
    private var isForeground = false
    private var warningDialog: AlertDialog? = null
    private var secondsUntilLogout = warningSeconds

    private val showWarningRunnable = Runnable {
        if (isEnabled && isForeground) {
            showWarningDialog()
        }
    }

    private val logoutRunnable = Runnable {
        if (isEnabled && isForeground) {
            performLogout()
        }
    }

    private val warningTickerRunnable = object : Runnable {
        override fun run() {
            if (!isEnabled || !isForeground || warningDialog == null) return

            secondsUntilLogout -= 1
            if (secondsUntilLogout <= 0) return

            warningDialog?.setMessage(buildWarningMessage(secondsUntilLogout))
            mainHandler.postDelayed(this, 1_000L)
        }
    }

    fun onResume() {
        isForeground = true
        if (isEnabled) {
            resetTimer()
        }
    }

    fun onPause() {
        isForeground = false
        cancelAll()
    }

    fun onUserInteraction() {
        if (isEnabled && isForeground) {
            resetTimer()
        }
    }

    private fun resetTimer() {
        cancelScheduledCallbacks()
        dismissWarningDialog()
        secondsUntilLogout = warningSeconds

        val warningDelay = timeoutMs - (warningSeconds * 1_000L)
        mainHandler.postDelayed(showWarningRunnable, warningDelay)
        mainHandler.postDelayed(logoutRunnable, timeoutMs)
    }

    private fun showWarningDialog() {
        if (warningDialog?.isShowing == true) return

        secondsUntilLogout = warningSeconds
        warningDialog = AlertDialog.Builder(activity)
            .setTitle("Automatische Abmeldung")
            .setMessage(buildWarningMessage(secondsUntilLogout))
            .setCancelable(false)
            .setPositiveButton("Ich bin noch hier") { dialog, _ ->
                dialog.dismiss()
                resetTimer()
            }
            .create()
            .also {
                it.setCanceledOnTouchOutside(false)
                it.show()
            }

        mainHandler.postDelayed(warningTickerRunnable, 1_000L)
    }

    private fun performLogout() {
        cancelAll()

        val intent = activity.packageManager.getLaunchIntentForPackage(MENU_PACKAGE)
            ?: Intent().apply {
                setClassName(MENU_PACKAGE, MENU_MAIN_ACTIVITY)
            }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(FORCE_LOGOUT_EXTRA, true)

        activity.startActivity(intent)
        activity.finish()
    }

    private fun buildWarningMessage(seconds: Int): String {
        return "Keine Aktivität erkannt. In $seconds Sekunden werden Sie automatisch abgemeldet."
    }

    private fun cancelScheduledCallbacks() {
        mainHandler.removeCallbacks(showWarningRunnable)
        mainHandler.removeCallbacks(logoutRunnable)
        mainHandler.removeCallbacks(warningTickerRunnable)
    }

    private fun dismissWarningDialog() {
        warningDialog?.dismiss()
        warningDialog = null
    }

    private fun cancelAll() {
        cancelScheduledCallbacks()
        dismissWarningDialog()
        secondsUntilLogout = warningSeconds
    }

    private companion object {
        const val MENU_PACKAGE = "com.example.menu"
        const val MENU_MAIN_ACTIVITY = "com.example.menu.MainActivity"
        const val FORCE_LOGOUT_EXTRA = "force_logout"
    }
}
