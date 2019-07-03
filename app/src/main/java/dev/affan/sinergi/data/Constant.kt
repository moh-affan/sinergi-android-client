package dev.affan.sinergi.data

object Constant {
    private const val BASE_URL = "http://sinergi.sumenepkab.go.id"
    private const val WEB_URL = BASE_URL
    const val DOWNLOAD_URL = WEB_URL
    const val API_URL = "$WEB_URL/api/"
    const val PICK_PDF_REQ = 11001

    fun getUrlImg(nama: String): String {
        return "$WEB_URL/assets/uploads/$nama"
    }
}
