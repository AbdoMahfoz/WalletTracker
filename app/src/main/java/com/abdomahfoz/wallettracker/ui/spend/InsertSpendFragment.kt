package com.abdomahfoz.wallettracker.ui.spend

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abdomahfoz.wallettracker.R
import com.abdomahfoz.wallettracker.databinding.FragmentInsertSpendBinding
import com.abdomahfoz.wallettracker.entities.SpendEntity
import com.abdomahfoz.wallettracker.entities.SpendEntity.Important
import com.abdomahfoz.wallettracker.ui.MainActivity
import com.abdomahfoz.wallettracker.viewModels.SpendViewModel
import com.abdomahfoz.wallettracker.viewModels.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class InsertSpendFragment : Fragment() {
    private val spendDate = Calendar.getInstance()
    private lateinit var binding : FragmentInsertSpendBinding
    private lateinit var viewModel: SpendViewModel
    private var mapFrag: SupportMapFragment? = null
    private var markerPosition: LatLng = LatLng(0.0, 0.0)
    private var locationRecorded: Boolean = false
    private var mapsInitialized: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_insert_spend, container, false
        )
        viewModel = ViewModelFactory.of(this).get(SpendViewModel::class.java)
        val mainActivity = activity as MainActivity
        mainActivity.setSupportActionBar(binding.toolBar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.date = spendDate.time
        handleDate()
        handleSubmit()
        mapFrag = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        binding.recordLocationSwitch.setOnCheckedChangeListener{ _, value ->
            if(value) {
                locationRecorded = true
                binding.mapFragment.visibility = View.VISIBLE
                if(!mapsInitialized) {
                    initMaps()
                }
            } else {
                locationRecorded = false
                binding.mapFragment.visibility = View.GONE
            }
        }
        return binding.root
    }
    private fun initMaps() {
        val fineAccess = ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseAccess = ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!fineAccess && !coarseAccess) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                5000
            )
        } else {
            mapsInitialized = true
            mapFrag?.getMapAsync{
                it.isMyLocationEnabled = true
                it.isBuildingsEnabled = false
                it.isIndoorEnabled = false
                it.isTrafficEnabled = false
                it.uiSettings.isMyLocationButtonEnabled = true
                val lm : LocationManager? = getSystemService(requireContext(), LocationManager::class.java)
                var currentMarker: Marker? = null
                val lastLocation = lm?.getLastKnownLocation(lm.getBestProvider(Criteria(), false)!!)
                it.apply {
                    if(lastLocation != null) {
                        val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                        markerPosition = latLng
                        currentMarker?.remove()
                        currentMarker = addMarker(MarkerOptions().position(latLng))
                        moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                    setOnCameraMoveListener {
                        markerPosition = it.cameraPosition.target
                        if(currentMarker == null) {
                            currentMarker = addMarker(
                                MarkerOptions().position(it.cameraPosition.target)
                            )
                        } else {
                            currentMarker?.position = it.cameraPosition.target
                        }
                    }
                    setOnMapClickListener { clickLocation ->
                        markerPosition = clickLocation
                        currentMarker?.remove()
                        currentMarker = addMarker(
                            MarkerOptions().position(clickLocation)
                        )
                        moveCamera(CameraUpdateFactory.newLatLng(clickLocation))
                    }
                }
                lm?.requestSingleUpdate(Criteria(), object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if(location != null) {
                            val marker = LatLng(location.latitude, location.longitude)
                            markerPosition = marker
                            it.apply {
                                currentMarker = addMarker(MarkerOptions().position(marker))
                                moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
                            }
                        }
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String?) {}
                    override fun onProviderDisabled(provider: String?) {}
                }, Looper.myLooper())
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 5000) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED ||
               grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initMaps()
            }
        }
    }
    private fun handleDate() {
        binding.changeDateButton.setOnClickListener{
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{_, year, month, dayOfMonth ->
                spendDate.set(Calendar.YEAR, year)
                spendDate.set(Calendar.MONTH, month)
                spendDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.date = spendDate.time
                TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener{ _, hour, minute ->
                    spendDate.set(Calendar.HOUR_OF_DAY, hour)
                    spendDate.set(Calendar.MINUTE, minute)
                    binding.date = spendDate.time
                }, spendDate.get(Calendar.HOUR_OF_DAY), spendDate.get(Calendar.MINUTE), false).show()
            }, spendDate.get(Calendar.YEAR), spendDate.get(Calendar.MONTH), spendDate.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
    private fun handleSubmit() {
        binding.submitButton.setOnClickListener{
            viewModel.insertNewSpend(
                SpendEntity(
                    comment = binding.commentText.text.toString(),
                    amount = binding.amountEditText.text.toString().toDouble(),
                    date = spendDate.time,
                    importance = when {
                        binding.veryImportantCheckBox.isChecked -> Important.VeryImportant
                        binding.avgImportanceCheckBox.isChecked -> Important.AverageImportance
                        else -> Important.NotImportant
                    },
                    latitude = if (locationRecorded) markerPosition.latitude else 0.0,
                    longitude = if (locationRecorded) markerPosition.longitude else 0.0,
                    locationRecorded = this.locationRecorded
                )
            )
            findNavController().popBackStack()
        }
    }
}
