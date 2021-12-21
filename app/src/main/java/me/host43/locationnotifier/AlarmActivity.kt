package me.host43.locationnotifier

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import me.host43.locationnotifier.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {

    private lateinit var b: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = DataBindingUtil.setContentView<ActivityAlarmBinding>(this, R.layout.activity_alarm)
    }
}