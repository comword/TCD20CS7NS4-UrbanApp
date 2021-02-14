package org.gtdev.apps.sensinglight.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.gtdev.apps.sensinglight.MainActivity
import org.gtdev.apps.sensinglight.R
import org.gtdev.apps.sensinglight.databinding.FragmentHomeBinding
import org.gtdev.apps.sensinglight.service.ActivityRecognisedService
import org.gtdev.apps.sensinglight.utils.SharedPreferenceUtil

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

//    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mActivity = activity as MainActivity
        homeViewModel = ViewModelProvider(mActivity).get(HomeViewModel::class.java)
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel
        context ?: return binding.root
        val root = binding.root
        val toolbar: Toolbar = root.findViewById(R.id.toolbar)

        sharedPreferences =
            mActivity.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )

        mActivity.setSupportActionBar(toolbar)
        mActivity.title = getString(R.string.menu_home)

        binding.btnStart.setOnClickListener {
            val ActivityRecognizedIntent = Intent(context, ActivityRecognisedService::class.java)
            if (homeViewModel.btnEnabled.value!!) {
                mActivity.locationService?.unsubscribeToLocationUpdates()
                (activity as MainActivity).stopService(ActivityRecognizedIntent)
                homeViewModel.btnEnabled.value = false
            } else {
                mActivity.locationService?.subscribeToLocationUpdates()
                    ?: Log.d(TAG, "Service Not Bound")
                (activity as MainActivity).startService(ActivityRecognizedIntent)
                homeViewModel.btnEnabled.value = true
            }

        }

//        mExportButton.setOnClickListener {
//            val request = OneTimeWorkRequestBuilder<ExportDataWorker>().build()
//            WorkManager.getInstance(mActivity).enqueue(request)
//        }

//        binding.btnUpload.setOnClickListener {
//            val request = OneTimeWorkRequestBuilder<UploadDataWorker>().build()
//            WorkManager.getInstance(mActivity).enqueue(request)
//            WorkManager.getInstance(mActivity).getWorkInfoByIdLiveData(request.id).observe(viewLifecycleOwner, {
//                if(it.state == WorkInfo.State.SUCCEEDED) {
//                    Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }

        val enabled = sharedPreferences.getBoolean(
            SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
        )
        homeViewModel.btnEnabled.value = enabled
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_discover, R.id.navigation_settings
//            )
//        )
//        setupActionBarWithNavController(appCompatActivity,
//            findNavController(appCompatActivity, R.id.nav_host_fragment), appBarConfiguration)
        return root
    }
}