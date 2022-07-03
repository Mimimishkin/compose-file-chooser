import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import java.util.*

interface Strings {
    val open get() = "Open"
    val cancel get() = "Cancel"
    val confirm get() = "Confirm"
    val new_folder get() = "New folder"
    val table get() = "Table"
    val tiny_icons get() = "Tiny icons"
    val small_icons get() = "Small icons"
    val normal_icons get() = "Normal icons"
    val increased_icons get() = "Increased icons"
    val big_icons get() = "Big icons"
    val large_icons get() = "Large icons"
    val all_files get() = "All files"
    val folder_path get() = "Folder path"
    val type get() = "Type"
    val creation_time get() = "Creation time"
    val edit_time get() = "Edit time"
    val size get() = "Size"
    val bit get() = "bit"
    val byte get() = "byte"
    val k_byte get() = "KB"
    val m_byte get() = "MB"
    val g_bite get() = "GB"
    val t_bite get() = "TB"
    val file_name get() = "File name"
    val name get() = "Name"
    val rename_files get() = "Rename files"
    val new_name get() = "New name"
    val deleting_failed get() = "Failed to delete some files:"
}

private val Default: Pair<Locale, Strings> = Locale("en") to object : Strings {}

private val Russian: Pair<Locale, Strings> = Locale("ru") to object : Strings {
    override val open = "Открыть"
    override val cancel = "Отмена"
    override val confirm = "Подтвердить"
    override val new_folder = "Новая папка"
    override val table = "Таблица"
    override val tiny_icons = "Крошечные значки"
    override val small_icons = "Маленькие значки"
    override val normal_icons = "Обычние значки"
    override val increased_icons = "Увеличенные значки"
    override val big_icons = "Большие значки"
    override val large_icons = "Огромные значки"
    override val all_files = "Все файлы"
    override val folder_path = "Путь до папки"
    override val type = "Тип"
    override val creation_time = "Дата создания"
    override val edit_time = "Дата изменения"
    override val size = "Размер"
    override val bit = "бит"
    override val byte = "байт"
    override val k_byte = "Кб"
    override val m_byte = "Мб"
    override val g_bite = "Гб"
    override val t_bite = "Тб"
    override val file_name = "Имя файла"
    override val name = "Имя"
    override val rename_files = "Переименовать файлы"
    override val new_name = "Новое имя"
    override val deleting_failed = "Не удалось удалить следущие файлы:"
}

private val languageMap = mapOf(Default, Russian)

private val systemLocale = Locale(Locale.getDefault().language)

val LocalLocalization = compositionLocalOf { languageMap[systemLocale] ?: Default.second }

val Vocabulary
    @Composable
    @ReadOnlyComposable
    get() = LocalLocalization.current

@Composable
fun Localized(locale: Locale, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLocalization provides (languageMap[locale] ?: Default.second)
    ) {
        content()
    }
}