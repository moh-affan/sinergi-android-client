@file:Suppress("unused")

package dev.affan.sinergi.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Color
import android.graphics.PorterDuff.Mode
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.affan.sinergi.App
import dev.affan.sinergi.BuildConfig
import dev.affan.sinergi.R
import dev.affan.sinergi.data.Constant
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Tools {

    @JvmStatic
    val isLolipopOrHigher
        get() = VERSION.SDK_INT >= 21

    val deviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) model else "$manufacturer $model"
        }

    val androidVersion: String
        get() = VERSION.RELEASE + BuildConfig.FLAVOR

    //Tools
    val windowWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    fun needRequestPermission(): Boolean {
        return VERSION.SDK_INT > 22
    }

    @JvmStatic
    fun setSystemBarColor(act: Activity, color: Int) {
        if (isLolipopOrHigher) {
            val window = act.window
            window.addFlags(Integer.MIN_VALUE)
            window.clearFlags(67108864)
            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = color
            }
        }
    }

    fun setSystemBarColor(act: Activity, color: String) {
        setSystemBarColor(act, Color.parseColor(color))
    }

    fun setSystemBarColorDarker(act: Activity, color: String) {
        setSystemBarColor(act, colorDarker(Color.parseColor(color)))
    }

    fun setSystemBarColorDarker(act: Activity, color: Int) {
        setSystemBarColor(act, colorDarker(color))
    }

    fun systemBarLolipop(act: Activity) {
        if (VERSION.SDK_INT >= 21) {
            val window = act.window
            window.addFlags(Integer.MIN_VALUE)
            window.clearFlags(67108864)
            window.statusBarColor = ContextCompat.getColor(act, R.color.colorPrimaryDark)
        }
    }

    fun rateAction(activity: Activity) {
        try {
            activity.startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("market://details?id=" + activity.packageName)
                )
            )
        } catch (e: ActivityNotFoundException) {
            activity.startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.packageName)
                )
            )
        }
    }

//    fun showDialogAbout(activity: Activity) {
//        DialogUtils(activity).buildDialogInfo(R.string.title_about, R.string.content_about, R.string.OK, R.drawable.img_about, object : CallbackDialog {
//            override fun onPositiveClick(dialog: Dialog) {
//                dialog.dismiss()
//            }
//
//            override fun onNegativeClick(dialog: Dialog) {}
//        }).show()
//    }

    @TargetApi(Build.VERSION_CODES.P)
    fun getVersionCode(ctx: Context): Long {
        return try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }

    }

    fun getVersionNamePlain(ctx: Context): String {
        return try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown version"
        }

    }

    fun getFormattedDate(dateTime: Long): String {
        return SimpleDateFormat("MMMM dd, yyyy hh:mm", Locale.getDefault()).format(Date(dateTime))
    }

    fun getFormattedDateSimple(dateTime: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dateTime))
    }

    @JvmStatic
    fun colorDarker(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] * 0.9f
        return Color.HSVToColor(hsv)
    }

    fun colorBrighter(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] / 0.8f
        return Color.HSVToColor(hsv)
    }

    fun getGridSpanCount(activity: Activity): Int {
        val display = activity.windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        return Math.round(displayMetrics.widthPixels.toFloat() / 180)
    }

    fun tintMenuIcon(context: Context, item: MenuItem, @ColorRes color: Int) {
        val wrapDrawable = DrawableCompat.wrap(item.icon)
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color))
        item.icon = wrapDrawable
    }

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getBitmap(file: File): Bitmap {
        val options = Options()
        options.inPreferredConfig = Config.ARGB_8888
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    fun copyToClipboard(context: Context, data: String) {
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip =
                ClipData.newPlainText("clipboard", data)
        Toast.makeText(context, R.string.msg_copied_clipboard, Toast.LENGTH_SHORT).show()
    }

    fun getDeviceID(): String? {
        var deviceID: String? = Build.ID
        if (deviceID == null || deviceID.trim { it <= ' ' }.isEmpty() || deviceID == "unknown") {
            try {
                deviceID = Settings.Secure.ANDROID_ID/*getString(context.contentResolver, "android_id")*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return deviceID
    }

    fun convertToDip(context: Context, i: Int): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                i.toFloat(),
                context.resources.displayMetrics
            )
        )
    }

    fun setSystemBarColor(activity: Activity) {
        if (VERSION.SDK_INT >= 21) {
            val window = activity.window
            window.addFlags(Integer.MIN_VALUE)
            window.clearFlags(67108864)
            window.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        }
    }

    fun scrollToBootom(nestedScrollView: NestedScrollView, view: View) {
        nestedScrollView.post { nestedScrollView.scrollTo(500, view.bottom) }
    }

    fun tintNavigationIcon(toolbar: Toolbar, resColorId: Int) {
        val navigationIcon = toolbar.navigationIcon
        navigationIcon!!.mutate()
        navigationIcon.setColorFilter(resColorId, Mode.SRC_ATOP)
    }

    fun tintMenuIcon(menu: Menu, resColorId: Int) {
        for (i2 in 0 until menu.size()) {
            val icon = menu.getItem(i2).icon
            if (icon != null) {
                icon.mutate()
                icon.setColorFilter(resColorId, Mode.SRC_ATOP)
            }
        }
    }

    fun rotate(view: View): Boolean {
        if (view.rotation == 0.0f) {
            view.animate().setDuration(200).rotation(180.0f)
            return true
        }
        view.animate().setDuration(200).rotation(0.0f)
        return false
    }

    @JvmOverloads
    fun animateRotation(z: Boolean, view: View, z2: Boolean = true): Boolean {
        var j: Long = 200
        val animate: ViewPropertyAnimator
        if (z) {
            animate = view.animate()
            if (!z2) {
                j = 0
            }
            animate.setDuration(j).rotation(180.0f)
            return true
        }
        animate = view.animate()
        if (!z2) {
            j = 0
        }
        animate.setDuration(j).rotation(0.0f)
        return false
    }

    fun getFullFormattedDate(l: Long): String {
        return SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault()).format(Date(l))
    }

    fun toTitleCase(str: String): String {
        val toLowerCase = str.toLowerCase()
        val stringBuilder = StringBuilder()
        var obj: Any? = 1
        for (c in toLowerCase.toCharArray()) {
            var ch: Char = c
            if (Character.isSpaceChar(c)) {
                obj = 1
            } else if (obj != null) {
                ch = Character.toTitleCase(c)
                obj = null
            }
            stringBuilder.append(ch)
        }
        return stringBuilder.toString()
    }

    fun tintOverflowIcon(toolbar: Toolbar, i: Int) {
        try {
            val overflowIcon = toolbar.overflowIcon
            overflowIcon!!.mutate()
            overflowIcon.setColorFilter(i, Mode.SRC_ATOP)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFormattedTime(l: Long): String {
        return SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date(l))
    }

    fun getFormattedTime(activity: Activity) {
        if (VERSION.SDK_INT >= 21) {
            val window = activity.window
            window.addFlags(Integer.MIN_VALUE)
            window.statusBarColor = 0
        }
    }

    fun hideKeyboard(ctx: Context, view: View) {
        val imm = ctx.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getGridSpanCount(activity: FragmentActivity): Int {
        val display = activity.windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        return Math.round(displayMetrics.widthPixels.toFloat() / 200)
    }

    fun getDateOnly(time: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time)
    }

    fun getDateAndTime(time: Long): String {
        val sample = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sample.format(Date(time))
    }

    fun getTimeOnly(time: Long): String {
        val sample = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sample.format(time)
    }

//    fun pickImage(activity: Activity? = null, fragment: Fragment? = null) {
//        val a = if (fragment != null) ImagePicker.create(fragment) else ImagePicker.create(activity)
//        a.language("id")
//            .folderMode(true)
//            .toolbarFolderTitle("Folder")
//            .toolbarImageTitle("Pilih gambar")
//            .toolbarArrowColor(Color.WHITE)
//            .includeVideo(false)
//            .toolbarDoneButtonText("SELESAI")
//            .single()
//            .showCamera(true)
//            .imageDirectory("Camera")
//            .theme(R.style.ImagePicker)
//            .enableLog(true)
//            .start()
//    }

    fun pickPdf(activity: Activity? = null, fragment: Fragment? = null) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val mimeTypes = arrayOf(/*"application/pdf", */"image/jpg", "image/jpeg"/*, "image/png"*/)
        if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        val i = Intent.createChooser(intent, "Pilih file")
        activity?.startActivityForResult(i, Constant.PICK_PDF_REQ)
        fragment?.startActivityForResult(i, Constant.PICK_PDF_REQ)
    }

//    fun checkApi(activity: Activity, response: Response<out Any>) {
//        val sharedPref = SharedPref(activity)
//        Log.d("response code", response.code().toString())
//        if (response.code() == 403) {
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
//            Toasty.error(activity, activity.getString(R.string.session_expired)).show()
//            sharedPref.token = Constant.GUEST_API_KEY
//            sharedPref.isLogIn = false
//            sharedPref.member = null
//            activity.startActivity(Intent(activity, LoginActivity::class.java))
//            activity.finish()
//        }
//    }

//    fun pickLocation(activity: Activity) {
//        val builder = PlacePicker.IntentBuilder()
//        val i = builder.build(activity)
//        activity.startActivityForResult(i, Constant.PICK_LOCATION_REQ)
//    }

    fun ymdToDmy(ymd: String): String {
        val fr = ymd.split("-")
        val ret: String
        ret = try {
            "${fr[2]}-${fr[1]}-${fr[0]}"
        } catch (ex: Exception) {
            ""
        }
        return ret
    }

    fun ymdToDmyWithTime(ymd: String): String {
        val r = ymd.split(" ")
        val ret: String
        ret = try {
            val fr = r[0].split("-")
            try {
                "${fr[2]}-${fr[1]}-${fr[0]} ${r[1]}"
            } catch (ex: Exception) {
                ""
            }
        } catch (ex: Exception) {
            ""
        }
        return ret
    }

    private fun getCaptureImageOutputUri(activity: Activity): Uri? {
        var outputFileUri: Uri? = null
        val getImage = activity.externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }

    fun getPickImageChooserIntent(activity: Activity): Intent {
        val outputFileUri = getCaptureImageOutputUri(activity)
        val allIntents: ArrayList<Intent> = arrayListOf()
        val camIntents: ArrayList<Intent> = arrayListOf()
        val packageManager = activity.packageManager
        val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
            camIntents.add(intent)
        }
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }
        var mainIntent = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component?.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)
        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, activity.getString(R.string.select_source))
        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents)//to parcellable array
        return chooserIntent
    }

    fun getPickImageResultUri(activity: Activity, data: Intent?): Uri? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri(activity) else data?.data
    }

    fun download(url: String, fileName: String = "") {
        val uri = Uri.parse(url)
        val req = DownloadManager.Request(uri)
        val downloadMgr = App.instance?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI.or(DownloadManager.Request.NETWORK_MOBILE))
        req.setAllowedOverRoaming(false)
        req.setDescription("mengunduh berkas $fileName")
        req.setTitle("Mengunduh")
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        req.allowScanningByMediaScanner()
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val a = downloadMgr.enqueue(req)

        Log.d("downlod", a.toString())
    }
}
