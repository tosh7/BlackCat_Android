package com.blackcat.android.data.remote.parser

import com.blackcat.android.domain.model.DeliveryStatus
import org.jsoup.Jsoup

object SagawaHtmlParser {

    private val STATUS_KEYWORDS = listOf(
        "集荷", "輸送中", "配達中", "配達完了", "持戻り", "不在",
        "保管中", "配送中", "到着", "出荷"
    )

    private val DATE_PATTERN = Regex("(\\d{1,2})[/月](\\d{1,2})[日]?")
    private val TIME_PATTERN = Regex("(\\d{1,2}):(\\d{2})")

    fun parse(html: String): List<DeliveryStatus> {
        val document = Jsoup.parse(html)
        val statusList = mutableListOf<DeliveryStatus>()

        val tables = document.select("table.table_basic")
        if (tables.isEmpty()) {
            return parseFallback(document)
        }

        for (table in tables) {
            val rows = table.select("tr")
            for (row in rows) {
                val cells = row.select("td")
                if (cells.size < 3) continue

                val statusText = cells[0].text().trim()
                    .replace("↓", "").replace("⇒", "").trim()
                val dateTimeText = cells[1].text().trim()
                val locationText = cells[2].text().trim()
                    .replace(Regex("TEL:.*"), "")
                    .replace(Regex("FAX:.*"), "")
                    .trim()

                if (statusText.isEmpty()) continue
                if (!STATUS_KEYWORDS.any { statusText.contains(it) }) continue

                val dateMatch = DATE_PATTERN.find(dateTimeText)
                val date = dateMatch?.let { "${it.groupValues[1]}/${it.groupValues[2]}" } ?: ""
                val timeMatch = TIME_PATTERN.find(dateTimeText)
                val time = timeMatch?.value

                if (date.isNotEmpty()) {
                    statusList.add(
                        DeliveryStatus(
                            status = statusText,
                            date = date,
                            time = time,
                            location = locationText
                        )
                    )
                }
            }
        }

        return statusList
    }

    private fun parseFallback(document: org.jsoup.nodes.Document): List<DeliveryStatus> {
        val statusList = mutableListOf<DeliveryStatus>()
        val allText = document.body().text()
        val lines = allText.split(Regex("\\s+"))

        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (STATUS_KEYWORDS.any { line.contains(it) }) {
                val statusText = line.replace("↓", "").replace("⇒", "").trim()
                var date = ""
                var time: String? = null
                var location = ""

                if (i + 1 < lines.size) {
                    val dateMatch = DATE_PATTERN.find(lines[i + 1])
                    date = dateMatch?.let { "${it.groupValues[1]}/${it.groupValues[2]}" } ?: ""
                    val timeMatch = TIME_PATTERN.find(lines[i + 1])
                    time = timeMatch?.value
                }

                if (i + 2 < lines.size) {
                    location = lines[i + 2]
                        .replace(Regex("TEL:.*"), "")
                        .replace(Regex("FAX:.*"), "")
                        .trim()
                }

                if (date.isNotEmpty() && statusText.isNotEmpty()) {
                    statusList.add(
                        DeliveryStatus(
                            status = statusText,
                            date = date,
                            time = time,
                            location = location
                        )
                    )
                }
                i += 3
            } else {
                i++
            }
        }

        return statusList
    }
}
