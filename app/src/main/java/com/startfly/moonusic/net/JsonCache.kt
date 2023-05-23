import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class JsonCache(context: Context) {
    private val gson: Gson = Gson()
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun saveJson(key: String?, data: Any?) {
        val jsonStr: String = gson.toJson(data)
        sharedPreferences.edit().putString(key, jsonStr).apply()
    }

    fun <T> getJson(key: String?, type: Class<T>?): T? {
        val jsonStr = sharedPreferences.getString(key, null)
        return if (jsonStr != null) {
            gson.fromJson(jsonStr, type)
        } else null
    }

    fun removeJson(key: String?) {
        sharedPreferences.edit().remove(key).apply()
    }

    companion object {
        private const val CACHE_FILE_NAME = "json_cache"
    }
}