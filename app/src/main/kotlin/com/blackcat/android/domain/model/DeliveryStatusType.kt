package com.blackcat.android.domain.model

enum class DeliveryStatusType(val displayName: String) {
    RECEIVED("荷物受付"),
    SENT("発送済み"),
    IN_TRANSIT("輸送中"),
    OUT_FOR_DELIVERY("配達中"),
    DELIVERED("配達完了");

    companion object {
        fun fromStatus(status: String): DeliveryStatusType {
            return when {
                status.contains("受付") || status.contains("引受") || status.contains("集荷") -> RECEIVED
                status.contains("発送") || status.contains("出荷") -> SENT
                status.contains("輸送") || status.contains("通過") || status.contains("到着") -> IN_TRANSIT
                status.contains("配達中") || status.contains("持戻") || status.contains("不在") ||
                    status.contains("持ち出し") || status.contains("保管") || status.contains("配送中") -> OUT_FOR_DELIVERY
                status.contains("配達完了") || status.contains("お届け済") || status.contains("完了") ||
                    status.contains("宅配ボックス") -> DELIVERED
                else -> IN_TRANSIT
            }
        }
    }
}
