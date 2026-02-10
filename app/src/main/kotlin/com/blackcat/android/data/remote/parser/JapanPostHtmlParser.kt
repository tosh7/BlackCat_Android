package com.blackcat.android.data.remote.parser

import com.blackcat.android.domain.model.DeliveryStatus
import org.jsoup.Jsoup

object JapanPostHtmlParser {

    private val STATUS_KEYWORDS = listOf(
        "引受", "到着", "発送", "通過", "配達中", "お届け済み",
        "持ち出し中", "ご不在のため持ち戻り", "保管"
    )

    private val DATE_PATTERN_JP = Regex("(\\d{1,2})月(\\d{1,2})日")
    private val DATE_PATTERN_SLASH = Regex("(\\d{4})/(\\d{1,2})/(\\d{1,2})")
    private val DATE_PATTERN_SHORT = Regex("(\\d{1,2})/(\\d{1,2})")
    private val TIME_PATTERN = Regex("(\\d{1,2}):(\\d{2})")
    private val LOCATION_PATTERN = Regex("(.+?(?:郵便局|支店|センター|局))")

    fun parse(html: String): List<DeliveryStatus> {
        val document = Jsoup.parse(html)
        val statusList = mutableListOf<DeliveryStatus>()

        val tables = document.select("table.tableType01")
        if (tables.isEmpty()) {
            return parseFallback(document)
        }

        for (table in tables) {
            val rows = table.select("tr")
            for (row in rows) {
                val cells = row.select("td")
                if (cells.size < 4) continue

                val statusText = cells[0].text().trim()
                val dateText = cells[1].text().trim()
                val timeText = cells[2].text().trim().ifEmpty { null }
                val locationText = cells[3].text().trim()

                if (statusText.isEmpty()) continue

                val date = extractDate(dateText)
                if (date.isNotEmpty()) {
                    statusList.add(
                        DeliveryStatus(
                            status = statusText,
                            date = date,
                            time = timeText,
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
        val rows = document.select("table tr")

        for (row in rows) {
            val cells = row.select("td")
            if (cells.isEmpty()) continue

            val text = row.text()
            if (STATUS_KEYWORDS.any { text.contains(it) }) {
                val statusText = STATUS_KEYWORDS.firstOrNull { text.contains(it) } ?: continue
                val date = extractDate(text)
                val timeMatch = TIME_PATTERN.find(text)
                val locationMatch = LOCATION_PATTERN.find(text)

                if (date.isNotEmpty()) {
                    statusList.add(
                        DeliveryStatus(
                            status = statusText,
                            date = date,
                            time = timeMatch?.value,
                            location = locationMatch?.groupValues?.get(1) ?: ""
                        )
                    )
                }
            }
        }

        return statusList
    }

    private fun extractDate(text: String): String {
        DATE_PATTERN_JP.find(text)?.let {
            return "${it.groupValues[1]}/${it.groupValues[2]}"
        }
        DATE_PATTERN_SLASH.find(text)?.let {
            return "${it.groupValues[2]}/${it.groupValues[3]}"
        }
        DATE_PATTERN_SHORT.find(text)?.let {
            return "${it.groupValues[1]}/${it.groupValues[2]}"
        }
        return ""
    }
}
