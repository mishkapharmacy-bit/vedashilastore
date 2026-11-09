package com.example.data

import com.example.model.CampaignMetric
import com.example.model.DashboardSummary
import com.example.model.SalesTrendPoint
import com.example.model.TrafficMetric

object AnalyticsRepository {

  val summary = DashboardSummary()

  val salesTrends7Days = listOf(
    SalesTrendPoint("Mon", 45200, 18000, 27200, 142),
    SalesTrendPoint("Tue", 52400, 19500, 32900, 168),
    SalesTrendPoint("Wed", 68900, 22000, 46900, 215),
    SalesTrendPoint("Thu", 61000, 21000, 40000, 194),
    SalesTrendPoint("Fri", 79800, 26000, 53800, 256),
    SalesTrendPoint("Sat", 94500, 31000, 63500, 308),
    SalesTrendPoint("Sun", 80850, 28000, 52850, 261)
  )

  val salesTrends30Days = listOf(
    SalesTrendPoint("Week 1", 280000, 95000, 185000, 890),
    SalesTrendPoint("Week 2", 345000, 115000, 230000, 1120),
    SalesTrendPoint("Week 3", 410000, 130000, 280000, 1340),
    SalesTrendPoint("Week 4", 482650, 167250, 315400, 1560)
  )

  val topCampaigns = listOf(
    CampaignMetric(
      id = "cmp_1",
      name = "Himalayan Shilajit - YouTube 30s Ad",
      platform = "YouTube",
      format = "16:9 In-Stream",
      impressions = 142500,
      clicks = 8420,
      conversions = 412,
      revenue = 135960,
      roas = 5.2f,
      status = "TOP PERFORMER"
    ),
    CampaignMetric(
      id = "cmp_2",
      name = "Festive Shilajit Gold - Instagram Reel",
      platform = "Instagram",
      format = "9:16 Reel",
      impressions = 98200,
      clicks = 6310,
      conversions = 295,
      revenue = 97350,
      roas = 4.8f,
      status = "VIRAL"
    ),
    CampaignMetric(
      id = "cmp_3",
      name = "Kashmiri Mongra Saffron - WhatsApp Status",
      platform = "WhatsApp",
      format = "9:16 Status",
      impressions = 45800,
      clicks = 4920,
      conversions = 248,
      revenue = 62000,
      roas = 6.4f,
      status = "HIGH ROAS"
    ),
    CampaignMetric(
      id = "cmp_4",
      name = "Pure Herbal Glow - Facebook Banner",
      platform = "Facebook",
      format = "16:9 Feed",
      impressions = 64100,
      clicks = 3120,
      conversions = 139,
      revenue = 45800,
      roas = 3.4f,
      status = "STEADY"
    )
  )

  val trafficSources = listOf(
    TrafficMetric("Instagram (Reels & Bio)", 26400, 38.6f, 32.4f, "#E1306C"),
    TrafficMetric("YouTube (Ad Video Links)", 19800, 28.9f, 28.1f, "#FF0000"),
    TrafficMetric("WhatsApp (Broadcast & Status)", 12500, 18.3f, 21.5f, "#25D366"),
    TrafficMetric("Facebook Feed Ads", 6200, 9.1f, 41.2f, "#1877F2"),
    TrafficMetric("Direct & Organic Search", 3520, 5.1f, 29.8f, "#C59B27")
  )

  /**
   * Generates interactive HTML bundle embedding Recharts React charting library
   * with AreaChart for Sales Trends, BarChart for Campaign ROAS/Revenue, and Traffic PieChart.
   */
  fun generateRechartsHtml(timeRange: String = "7d"): String {
    val salesDataJson = if (timeRange == "30d") {
      """
      [
        {"name": "Week 1", "Total": 280, "Campaigns": 185, "Organic": 95, "Orders": 890},
        {"name": "Week 2", "Total": 345, "Campaigns": 230, "Organic": 115, "Orders": 1120},
        {"name": "Week 3", "Total": 410, "Campaigns": 280, "Organic": 130, "Orders": 1340},
        {"name": "Week 4", "Total": 482, "Campaigns": 315, "Organic": 167, "Orders": 1560}
      ]
      """.trimIndent()
    } else {
      """
      [
        {"name": "Mon", "Total": 45.2, "Campaigns": 27.2, "Organic": 18.0, "Orders": 142},
        {"name": "Tue", "Total": 52.4, "Campaigns": 32.9, "Organic": 19.5, "Orders": 168},
        {"name": "Wed", "Total": 68.9, "Campaigns": 46.9, "Organic": 22.0, "Orders": 215},
        {"name": "Thu", "Total": 61.0, "Campaigns": 40.0, "Organic": 21.0, "Orders": 194},
        {"name": "Fri", "Total": 79.8, "Campaigns": 53.8, "Organic": 26.0, "Orders": 256},
        {"name": "Sat", "Total": 94.5, "Campaigns": 63.5, "Organic": 31.0, "Orders": 308},
        {"name": "Sun", "Total": 80.8, "Campaigns": 52.8, "Organic": 28.0, "Orders": 261}
      ]
      """.trimIndent()
    }

    val campaignsJson = """
    [
      {"name": "YouTube Ad", "Revenue": 135.9, "ROAS": 5.2, "Clicks": 8.4},
      {"name": "Insta Reel", "Revenue": 97.3, "ROAS": 4.8, "Clicks": 6.3},
      {"name": "WhatsApp", "Revenue": 62.0, "ROAS": 6.4, "Clicks": 4.9},
      {"name": "Facebook", "Revenue": 45.8, "ROAS": 3.4, "Clicks": 3.1}
    ]
    """.trimIndent()

    val trafficJson = """
    [
      {"name": "Instagram", "value": 38.6, "color": "#E1306C"},
      {"name": "YouTube", "value": 28.9, "color": "#FF0000"},
      {"name": "WhatsApp", "value": 18.3, "color": "#25D366"},
      {"name": "Facebook", "value": 9.1, "color": "#1877F2"},
      {"name": "Organic", "value": 5.1, "color": "#C59B27"}
    ]
    """.trimIndent()

    return """
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
      <title>Vedashila Recharts Dashboard</title>
      <!-- Load React, ReactDOM, and Recharts via CDN -->
      <script src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
      <script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
      <script src="https://unpkg.com/prop-types@15.8.1/prop-types.min.js"></script>
      <script src="https://unpkg.com/recharts@2.12.7/umd/Recharts.min.js"></script>
      <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
      <style>
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        body { background: #F9F7F2; color: #14201A; padding: 12px; }
        .chart-card {
          background: #FFFFFF;
          border-radius: 14px;
          padding: 14px;
          margin-bottom: 16px;
          border: 1px solid #D6E2DB;
          box-shadow: 0 2px 6px rgba(0,0,0,0.04);
        }
        .chart-title {
          font-size: 14px;
          font-weight: 700;
          color: #0D3B2E;
          margin-bottom: 4px;
        }
        .chart-sub {
          font-size: 11px;
          color: #5D6B63;
          margin-bottom: 12px;
        }
        .recharts-default-tooltip {
          border-radius: 8px !important;
          background: #0D3B2E !important;
          color: #FFFFFF !important;
          border: 1px solid #C59B27 !important;
          padding: 8px 12px !important;
          font-size: 11px !important;
        }
        .recharts-tooltip-item {
          color: #FFFFFF !important;
        }
      </style>
    </head>
    <body>
      <div id="root"></div>

      <script type="text/babel">
        const {
          AreaChart, Area, BarChart, Bar, PieChart, Pie, Cell,
          XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
        } = Recharts;

        const salesData = $salesDataJson;
        const campaignsData = $campaignsJson;
        const trafficData = $trafficJson;

        function Dashboard() {
          return (
            <div>
              {/* Chart 1: Sales Trends using Recharts AreaChart */}
              <div className="chart-card">
                <div className="chart-title">📈 Sales Trends: Marketing vs Organic</div>
                <div className="chart-sub">Daily revenue in thousands (₹k) driven by posters & video ads</div>
                <div style={{ width: '100%', height: 210 }}>
                  <ResponsiveContainer>
                    <AreaChart data={salesData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                      <defs>
                        <linearGradient id="campaignGrad" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="5%" stopColor="#0D3B2E" stopOpacity={0.8}/>
                          <stop offset="95%" stopColor="#0D3B2E" stopOpacity={0.05}/>
                        </linearGradient>
                        <linearGradient id="organicGrad" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="5%" stopColor="#C59B27" stopOpacity={0.8}/>
                          <stop offset="95%" stopColor="#C59B27" stopOpacity={0.05}/>
                        </linearGradient>
                      </defs>
                      <CartesianGrid strokeDasharray="3 3" stroke="#E2E8F0" />
                      <XAxis dataKey="name" tick={{ fontSize: 10, fill: '#64748B' }} />
                      <YAxis tick={{ fontSize: 10, fill: '#64748B' }} />
                      <Tooltip formatter={(value) => '₹' + value + 'k'} />
                      <Legend wrapperStyle={{ fontSize: 11, paddingTop: 6 }} />
                      <Area type="monotone" dataKey="Campaigns" stroke="#0D3B2E" strokeWidth={2.5} fillOpacity={1} fill="url(#campaignGrad)" />
                      <Area type="monotone" dataKey="Organic" stroke="#C59B27" strokeWidth={2} fillOpacity={1} fill="url(#organicGrad)" />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>
              </div>

              {/* Chart 2: Campaign Performance using Recharts BarChart */}
              <div className="chart-card">
                <div className="chart-title">🎯 Campaign ROI & Revenue by Platform</div>
                <div className="chart-sub">Revenue generated (₹k) and ROAS multiplier</div>
                <div style={{ width: '100%', height: 200 }}>
                  <ResponsiveContainer>
                    <BarChart data={campaignsData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                      <CartesianGrid strokeDasharray="3 3" stroke="#E2E8F0" />
                      <XAxis dataKey="name" tick={{ fontSize: 10, fill: '#64748B' }} />
                      <YAxis tick={{ fontSize: 10, fill: '#64748B' }} />
                      <Tooltip formatter={(val, name) => name === 'Revenue' ? '₹' + val + 'k' : val + 'x'} />
                      <Legend wrapperStyle={{ fontSize: 11, paddingTop: 6 }} />
                      <Bar dataKey="Revenue" fill="#0D3B2E" radius={[6, 6, 0, 0]} />
                      <Bar dataKey="ROAS" fill="#C59B27" radius={[6, 6, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>

              {/* Chart 3: Website Traffic Share using Recharts PieChart */}
              <div className="chart-card">
                <div className="chart-title">🌐 Website Traffic Attribution (vedashila.com)</div>
                <div className="chart-sub">Share of incoming visitors by social marketing channel</div>
                <div style={{ width: '100%', height: 180, display: 'flex', alignItems: 'center' }}>
                  <ResponsiveContainer>
                    <PieChart>
                      <Pie
                        data={trafficData}
                        cx="50%"
                        cy="50%"
                        innerRadius={45}
                        outerRadius={70}
                        paddingAngle={4}
                        dataKey="value"
                      >
                        {trafficData.map((entry, index) => (
                          <Cell key={'cell-' + index} fill={entry.color} />
                        ))}
                      </Pie>
                      <Tooltip formatter={(val) => val + '%'} />
                      <Legend wrapperStyle={{ fontSize: 10 }} />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </div>
          );
        }

        ReactDOM.render(<Dashboard />, document.getElementById('root'));
      </script>
    </body>
    </html>
    """.trimIndent()
  }
}
