package com.what_to_watch.seed

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.studio.domain.repository.StudioRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Profile("dev")
class CartoonSeeder(
    private val cartoonRepository: CartoonRepository,
    private val studioRepository: StudioRepository,
) : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    // Несколько мультфильмов делят одну и ту же студию (например, Pixar) — кэш
    // не даёт создать по новой записи на каждый такой мультфильм
    private val studioIds = mutableMapOf<String, UUID>()

    override fun run(vararg args: String) {
        val added = FAKE_CARTOONS.count { fake ->
            if (cartoonRepository.existsByTitle(fake.title)) {
                false
            } else {
                val studioId = resolveStudioId(fake.studio)
                cartoonRepository.addCartoon(
                    fake.title, fake.releaseYear, fake.genre, fake.duration, fake.type, studioId,
                )
                true
            }
        }
        if (added > 0) {
            logger.info { "Добавлено фейковых мультфильмов: $added" }
        }
    }

    private fun resolveStudioId(name: String): UUID =
        studioIds.getOrPut(name) {
            (studioRepository.findByName(name) ?: studioRepository.addStudio(name)).id
        }

    private data class FakeCartoon(
        val title: String,
        val releaseYear: Int,
        val genre: String,
        val duration: Int,
        val type: String,
        val studio: String,
    )

    private companion object {
        val FAKE_CARTOONS = listOf(
            // Disney
            FakeCartoon("Белоснежка и семь гномов", 1937, "Фэнтези", 83, "Полнометражный", "Disney"),
            FakeCartoon("Пиноккио", 1940, "Фэнтези", 88, "Полнометражный", "Disney"),
            FakeCartoon("Фантазия", 1940, "Мюзикл", 126, "Полнометражный", "Disney"),
            FakeCartoon("Фантазия 2000", 1999, "Мюзикл", 75, "Полнометражный", "Disney"),
            FakeCartoon("Дамбо", 1941, "Драма", 64, "Полнометражный", "Disney"),
            FakeCartoon("Бэмби", 1942, "Драма", 70, "Полнометражный", "Disney"),
            FakeCartoon("Салют, друзья!", 1942, "Приключения", 42, "Полнометражный", "Disney"),
            FakeCartoon("Три кабальеро", 1944, "Приключения", 72, "Полнометражный", "Disney"),
            FakeCartoon("Сыграй мою музыку", 1946, "Мюзикл", 75, "Полнометражный", "Disney"),
            FakeCartoon("Весёлые и беззаботные", 1947, "Мюзикл", 73, "Полнометражный", "Disney"),
            FakeCartoon("Время мелодий", 1948, "Мюзикл", 76, "Полнометражный", "Disney"),
            FakeCartoon("Приключения Икабода и мистера Тоада", 1949, "Приключения", 68, "Полнометражный", "Disney"),
            FakeCartoon("Золушка", 1950, "Фэнтези", 74, "Полнометражный", "Disney"),
            FakeCartoon("Алиса в Стране чудес", 1951, "Фэнтези", 75, "Полнометражный", "Disney"),
            FakeCartoon("Питер Пэн", 1953, "Фэнтези", 77, "Полнометражный", "Disney"),
            FakeCartoon("Леди и Бродяга", 1955, "Мелодрама", 76, "Полнометражный", "Disney"),
            FakeCartoon("Спящая красавица", 1959, "Фэнтези", 75, "Полнометражный", "Disney"),
            FakeCartoon("101 далматинец", 1961, "Приключения", 79, "Полнометражный", "Disney"),
            FakeCartoon("Меч в камне", 1963, "Фэнтези", 79, "Полнометражный", "Disney"),
            FakeCartoon("Книга джунглей", 1967, "Приключения", 78, "Полнометражный", "Disney"),
            FakeCartoon("Коты-аристократы", 1970, "Комедия", 79, "Полнометражный", "Disney"),
            FakeCartoon("Робин Гуд", 1973, "Приключения", 83, "Полнометражный", "Disney"),
            FakeCartoon("Приключения Винни-Пуха", 1977, "Семейный", 74, "Полнометражный", "Disney"),
            FakeCartoon("Спасатели", 1977, "Приключения", 77, "Полнометражный", "Disney"),
            FakeCartoon("Спасатели в Австралии", 1990, "Приключения", 77, "Полнометражный", "Disney"),
            FakeCartoon("Лис и пёс", 1981, "Драма", 83, "Полнометражный", "Disney"),
            FakeCartoon("Чёрный котёл", 1985, "Фэнтези", 80, "Полнометражный", "Disney"),
            FakeCartoon("Великий мышиный сыщик", 1986, "Приключения", 74, "Полнометражный", "Disney"),
            FakeCartoon("Оливер и компания", 1988, "Драма", 74, "Полнометражный", "Disney"),
            FakeCartoon("Русалочка", 1989, "Фэнтези", 83, "Полнометражный", "Disney"),
            FakeCartoon("Красавица и Чудовище", 1991, "Мюзикл", 84, "Полнометражный", "Disney"),
            FakeCartoon("Аладдин", 1992, "Мюзикл", 90, "Полнометражный", "Disney"),
            FakeCartoon("Возвращение Джафара (Алладин 2)", 1994, "Приключения", 69, "Полнометражный", "Disney"),
            FakeCartoon("Аладдин и король разбойников", 1996, "Приключения", 81, "Полнометражный", "Disney"),
            FakeCartoon("Король Лев", 1994, "Драма", 88, "Полнометражный", "Disney"),
            FakeCartoon("Горбун из Нотр-Дама", 1996, "Драма", 91, "Полнометражный", "Disney"),
            FakeCartoon("Геркулес", 1997, "Комедия", 93, "Полнометражный", "Disney"),
            FakeCartoon("Тарзан", 1999, "Приключения", 88, "Полнометражный", "Disney"),
            FakeCartoon("Динозавр", 2000, "Приключения", 82, "Полнометражный", "Disney"),
            FakeCartoon("Похождения императора", 2000, "Комедия", 78, "Полнометражный", "Disney"),
            FakeCartoon("Атлантида: Затерянный мир", 2001, "Фантастика", 96, "Полнометражный", "Disney"),
            FakeCartoon("Лило и Стич", 2002, "Фантастика", 85, "Полнометражный", "Disney"),
            FakeCartoon("Планета сокровищ", 2002, "Фантастика", 95, "Полнометражный", "Disney"),
            FakeCartoon("Братец медвежонок", 2003, "Драма", 85, "Полнометражный", "Disney"),
            FakeCartoon("Не бей копытом", 2004, "Комедия", 76, "Полнометражный", "Disney"),
            FakeCartoon("Цыплёнок Цыпа", 2005, "Комедия", 81, "Полнометражный", "Disney"),
            FakeCartoon("В гости к Робинсонам", 2007, "Фантастика", 95, "Полнометражный", "Disney"),
            FakeCartoon("Вольт", 2008, "Приключения", 96, "Полнометражный", "Disney"),
            FakeCartoon("Принцесса и лягушка", 2009, "Фэнтези", 97, "Полнометражный", "Disney"),
            FakeCartoon("Рапунцель: Запутанная история", 2010, "Мюзикл", 100, "Полнометражный", "Disney"),
            FakeCartoon("Медвежонок Винни и его друзья", 2011, "Семейный", 63, "Полнометражный", "Disney"),
            FakeCartoon("Ральф", 2012, "Комедия", 101, "Полнометражный", "Disney"),
            FakeCartoon("Ральф против интернета", 2018, "Комедия", 112, "Полнометражный", "Disney"),
            FakeCartoon("Холодное сердце", 2013, "Мюзикл", 102, "Полнометражный", "Disney"),
            FakeCartoon("Холодное сердце 2", 2019, "Мюзикл", 103, "Полнометражный", "Disney"),
            FakeCartoon("Город героев", 2014, "Фантастика", 102, "Полнометражный", "Disney"),
            FakeCartoon("Зверополис", 2016, "Комедия", 108, "Полнометражный", "Disney"),
            FakeCartoon("Зверополис 2", 2025, "Комедия", 108, "Полнометражный", "Disney"),
            FakeCartoon("Энканто", 2021, "Мюзикл", 102, "Полнометражный", "Disney"),
            FakeCartoon("Странный мир", 2022, "Фантастика", 102, "Полнометражный", "Disney"),
            FakeCartoon("Заветное желание", 2023, "Мюзикл", 95, "Полнометражный", "Disney"),
            // Pixar
            FakeCartoon("История игрушек", 1995, "Приключения", 81, "Полнометражный", "Pixar"),
            FakeCartoon("История игрушек 2", 1999, "Приключения", 92, "Полнометражный", "Pixar"),
            FakeCartoon("История игрушек 3", 2010, "Приключения", 103, "Полнометражный", "Pixar"),
            FakeCartoon("История игрушек 4", 2019, "Приключения", 100, "Полнометражный", "Pixar"),
            FakeCartoon("ВАЛЛ·И", 2008, "Фантастика", 98, "Полнометражный", "Pixar"),
            FakeCartoon("Головоломка", 2015, "Драма", 95, "Полнометражный", "Pixar"),
            FakeCartoon("Головоломка 2", 2024, "Драма", 96, "Полнометражный", "Pixar"),
            FakeCartoon("Тайна Коко", 2017, "Драма", 105, "Полнометражный", "Pixar"),
            // DreamWorks
            FakeCartoon("Шрек", 2001, "Комедия", 90, "Полнометражный", "DreamWorks"),
            FakeCartoon("Шрек 2", 2004, "Комедия", 92, "Полнометражный", "DreamWorks"),
            FakeCartoon("Шрек Третий", 2007, "Комедия", 93, "Полнометражный", "DreamWorks"),
            FakeCartoon("Шрек мороз зленый нос", 2007, "Комедия", 21, "Короткометражный", "DreamWorks"),
            FakeCartoon("Шрек Навсегда", 2010, "Комедия", 93, "Полнометражный", "DreamWorks"),
            FakeCartoon("Кот в сапогах", 2011, "Приключения", 90, "Полнометражный", "DreamWorks"),
            FakeCartoon("Кот в сапогах: Последнее желание", 2022, "Приключения", 102, "Полнометражный", "DreamWorks"),
            FakeCartoon("Кунг-фу Панда", 2008, "Боевик", 91, "Полнометражный", "DreamWorks"),
            FakeCartoon("Кунг-фу Панда 2", 2011, "Боевик", 90, "Полнометражный", "DreamWorks"),
            FakeCartoon("Кунг-фу Панда 3", 2016, "Боевик", 95, "Полнометражный", "DreamWorks"),
            FakeCartoon("Кунг-фу Панда 4", 2024, "Боевик", 94, "Полнометражный", "DreamWorks"),
            FakeCartoon("Как приручить дракона", 2010, "Приключения", 98, "Полнометражный", "DreamWorks"),
            FakeCartoon("Как приручить дракона 2", 2014, "Приключения", 102, "Полнометражный", "DreamWorks"),
            FakeCartoon("Как приручить дракона 3", 2019, "Приключения", 104, "Полнометражный", "DreamWorks"),
            // Blue Sky
            FakeCartoon("Ледниковый период", 2002, "Приключения", 81, "Полнометражный", "Blue Sky"),
            FakeCartoon("Ледниковый период 2", 2006, "Приключения", 90, "Полнометражный", "Blue Sky"),
            FakeCartoon("Ледниковый период 3", 2009, "Приключения", 94, "Полнометражный", "Blue Sky"),
            FakeCartoon("Ледниковый период 4", 2012, "Приключения", 88, "Полнометражный", "Blue Sky"),
            FakeCartoon("Ледниковый период: Столкновение неизбежно", 2016, "Фантастика", 94, "Полнометражный", "Blue Sky"),
            FakeCartoon("Рио", 2011, "Приключения", 94, "Полнометражный", "Blue Sky"),
            FakeCartoon("Рио 2", 2014, "Приключения", 101, "Полнометражный", "Blue Sky"),
            // Illumination
            FakeCartoon("Гадкий я", 2010, "Комедия", 95, "Полнометражный", "Illumination"),
            FakeCartoon("Гадкий я 2", 2013, "Комедия", 98, "Полнометражный", "Illumination"),
            FakeCartoon("Гадкий я 3", 2017, "Комедия", 90, "Полнометражный", "Illumination"),
            FakeCartoon("Гадкий я 4", 2024, "Комедия", 94, "Полнометражный", "Illumination"),
            FakeCartoon("Миньоны", 2015, "Комедия", 91, "Полнометражный", "Illumination"),
            FakeCartoon("Миньоны: Грювитация", 2022, "Комедия", 87, "Полнометражный", "Illumination"),
            FakeCartoon("Тайная жизнь домашних животных 2", 2019, "Комедия", 86, "Полнометражный", "Illumination"),
            FakeCartoon("Братья Супер Марио в кино", 2023, "Приключения", 92, "Полнометражный", "Illumination"),
            // Sony
            FakeCartoon("Монстры на каникулах", 2012, "Комедия", 91, "Полнометражный", "Sony"),
            FakeCartoon("Монстры на каникулах 2", 2015, "Комедия", 89, "Полнометражный", "Sony"),
            FakeCartoon("Монстры на каникулах 3", 2018, "Комедия", 97, "Полнометражный", "Sony"),
            FakeCartoon("Монстры на каникулах: Трансформания", 2022, "Комедия", 87, "Полнометражный", "Sony"),
            FakeCartoon("Человек-паук: Через вселенные", 2018, "Фантастика", 117, "Полнометражный", "Sony"),
            FakeCartoon("Человек-паук: Паутина вселенных", 2023, "Фантастика", 140, "Полнометражный", "Sony"),
            FakeCartoon("Митчеллы против машин", 2021, "Фантастика", 109, "Полнометражный", "Sony"),
        )
    }

}
