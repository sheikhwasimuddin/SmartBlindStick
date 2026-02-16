package com.example.smartblindstick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.google.firebase.database.*

class DashboardFragment : Fragment() {

    private lateinit var database: DatabaseReference

    private lateinit var lidarText: TextView
    private lateinit var waterText: TextView
    private lateinit var emergencyText: TextView

    private lateinit var lidarChart: LineChart
    private lateinit var waterChart: LineChart

    private val lidarEntries = ArrayList<Entry>()
    private val waterEntries = ArrayList<Entry>()

    private var index = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lidarText = view.findViewById(R.id.lidarText)
        waterText = view.findViewById(R.id.waterText)
        emergencyText = view.findViewById(R.id.emergencyText)

        lidarChart = view.findViewById(R.id.lidarChart)
        waterChart = view.findViewById(R.id.waterChart)

        database = FirebaseDatabase.getInstance()
            .getReference("Sensor")

        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.exists()) return

                val lidar =
                    snapshot.child("lidar")
                        .getValue(Int::class.java) ?: 0

                val water =
                    snapshot.child("water")
                        .getValue(Int::class.java) ?: 0

                val emergency =
                    snapshot.child("emergency_pressed")
                        .getValue(Boolean::class.java) ?: false

                // ✅ Update Text
                lidarText.text = "LiDAR: $lidar mm"
                waterText.text = "Water Level: $water"
                emergencyText.text =
                    if (emergency) "🚨 EMERGENCY!"
                    else "Status: Safe"

                // ✅ Update Charts
                updateCharts(lidar, water)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateCharts(lidar: Int, water: Int) {

        lidarEntries.add(Entry(index, lidar.toFloat()))
        waterEntries.add(Entry(index, water.toFloat()))

        if (lidarEntries.size > 30) {
            lidarEntries.removeAt(0)
            waterEntries.removeAt(0)
        }

        index++

        val lidarDataSet = LineDataSet(lidarEntries, "LiDAR Distance")
        val waterDataSet = LineDataSet(waterEntries, "Water Level")

        lidarChart.data = LineData(lidarDataSet)
        waterChart.data = LineData(waterDataSet)

        lidarChart.invalidate()
        waterChart.invalidate()
    }
}