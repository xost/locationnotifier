package me.host43.locationnotifier

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import me.host43.locationnotifier.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val requestpermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it["android.permission.ACCESS_COARSE_LOCATION"] != true ||
                it["android.permission.ACCESS_FINE_LOCATION"] != true ||
                it["android.permission.ACCESS_BACKGROUND_LOCATION"] != true
            ) {
                //TODO ("Show window that explains why app is needed with permissions and give a choice to \"Give permissions\" or \"Exit\" ")
                finish()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestpermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        )

        val b = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }
}