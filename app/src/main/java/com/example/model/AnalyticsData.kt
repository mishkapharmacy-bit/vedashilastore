package com.example.model

data class SalesTrendPoint(
  val date: String,
  val revenue: Int,
  val organicRevenue: Int,
  val campaignRevenue: Int,
  val orders: Int
)

data class CampaignMetric(
  val id: String,
  val name: String,
  val platform: String,
  val format: String,
  val impressions: Int,
  val clicks: Int,
  val conversions: Int,
  val revenue: Int,
  val roas: Float,
  val status: String = "ACTIVE"
)

data class TrafficMetric(
  val source: String,
  val visitors: Int,
  val percentage: Float,
  val bounceRate: Float,
  val colorHex: String
)

data class DashboardSummary(
  val totalRevenue: String = "₹4,82,650",
  val campaignRevenue: String = "₹3,15,400",
  val totalVisitors: String = "68,420",
  val averageRoas: String = "4.6x",
  val conversionRate: String = "3.8%",
  val totalOrders: String = "1,894",
  val activeCampaigns: Int = 12
)
