package me.host43.locationnotifier.trackpoints

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import me.host43.locationnotifier.R
import me.host43.locationnotifier.database.PointDatabase
import me.host43.locationnotifier.databinding.FragmentInputPointBinding
import me.host43.locationnotifier.databinding.FragmentInputPointBindingImpl
import me.host43.locationnotifier.databinding.FragmentTrackPointsBinding

class InputPointFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val b = DataBindingUtil.inflate<FragmentInputPointBindingImpl>(
            inflater,
            R.layout.fragment_input_point,
            container,
            false
        )

        val app = requireNotNull(this.activity).application
        val ds = PointDatabase.getInstance(app).dao
        val vmFactory = InputPointViewModelFactory(ds, app)
        val vm = ViewModelProvider(this, vmFactory).get(InputPointViewModel::class.java)

        b.lifecycleOwner = this
        b.vm = vm

        vm.eventAddPointDone.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.i("##$#","#####################")
                this.findNavController().navigate(
                    InputPointFragmentDirections.actionInputPointFragmentToTrackPointsFragment()
                )
            }
            vm.addPointComplete()
        })

        return b.root
    }
}