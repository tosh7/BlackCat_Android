package com.blackcat.android.data.remote.parser

import com.blackcat.android.domain.model.DeliveryStatus
import org.jsoup.Jsoup

object YamatoHtmlParser {

    fun parse(html: String): List<DeliveryStatus> {
        val document = Jsoup.parse(html)
        val statusList = mutableListOf<DeliveryStatus>()

        val detailBlocks = document.select(".tracking-invoice-block-detail")
        if (detailBlocks.isEmpty()) return emptyList()

        val detailBlock = detailBlocks.first() ?: return emptyList()
        val rows = detailBlock.select("tr")

        for (row in rows) {
            val cells = row.select("td")
            if (cells.size < 4) continue

            val statusText = cells[0].text().trim()
            if (statusText.isEmpty() || statusText.contains("：")) continue

            val dateRaw = cells[1].text().trim()
            val timeText = cells[2].text().trim().ifEmpty { null }
            val locationText = cells[3].text().trim()

            val date = convertDate(dateRaw)
            if (date.isNotEmpty() && statusText.isNotEmpty()) {
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

        return statusList
    }

    private fun convertDate(raw: String): String {
        val match = Regex("(\\d{1,2})月(\\d{1,2})日").find(raw)
        return if (match != null) {
            "${match.groupValues[1]}/${match.groupValues[2]}"
        } else {
            raw
        }
    }
}
