package dev.affan.sinergi.activity

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.tapadoo.alerter.Alerter
import dev.affan.sinergi.R
import dev.affan.sinergi.utils.NetworkCheck

class SplashActivity : BaseActivity() {
    init {
        isFullScreen = true
    }

    override fun initContentView() {
        setContentView(R.layout.activity_splash)
    }

    override fun initComponents() {
        if (NetworkCheck.isConnect(this)) {
            goToNext()
        } else {
            Alerter.create(this@SplashActivity)
                .setTitle("Tidak ada koneksi")
                .setText("Tidak dapat menyambungkan ke server karena tidak ada koneksi")
                .setBackgroundColorRes(R.color.danger)
                .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                .setIconColorFilter(Color.WHITE)
                .addButton(
                    getString(R.string.label_coba_lagi),
                    R.style.AlerterButtonDefault,
                    View.OnClickListener {
                        if (NetworkCheck.isConnect(this)) {
                            Alerter.clearCurrent(this)
                            goToNext()
                        }
                    })
                .addButton("Tutup", R.style.AlerterButtonWarning, View.OnClickListener { finish() })
                .enableSwipeToDismiss()
                .setDuration(3000)
                .show()
        }
    }

    private fun goToNext() {
        startActivityDelayed(Intent(this, MainActivity::class.java), 2500)
    }
}
