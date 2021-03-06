package com.spacelynx.controllerhub

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.spacelynx.controllerhub.viewmodels.ContextBarViewModel
import com.spacelynx.controllerhub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var mainContent: ConstraintLayout
  private val contextBarModel: ContextBarViewModel by viewModels()

  private var shouldAllowBack = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (!isTaskRoot) {
      // Android launched another instance of the root activity into an existing task
      // so just quietly finish and go away, dropping the user back into the activity
      // at the top of the stack (ie: the last state of this task)
      finish()
      return
    }

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    mainContent = binding.mainContent

    contextBarModel.contextIcon.observe(this, {
      binding.contextBar.contextIcon.text = it
      if (it.isNullOrBlank()) {
        binding.contextBar.contextIcon.text = getString(R.string.cc_generic_outline)
        binding.contextBar.contextIconError.visibility = View.VISIBLE
      } else
        binding.contextBar.contextIconError.visibility = View.GONE
    })

    contextBarModel.action0text.observe(this, {
      binding.contextBar.action0.text = it
    })
    contextBarModel.action0drawable.observe(this, {
      binding.contextBar.action0.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
    })

    contextBarModel.action1text.observe(this, {
      binding.contextBar.action1.text = it
    })
    contextBarModel.action1drawable.observe(this, {
      binding.contextBar.action1.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
    })

    contextBarModel.registerGamepadService(this)
    contextBarModel.updateContextIcon()
  }

  override fun onResume() {
    super.onResume()
    hideSystemUI()
  }

  override fun onDestroy() {
    super.onDestroy()
    contextBarModel.unregisterGamepadService(this)
  }

  /**
   * Disable back button unless we explicitly allow it.
   * This prevents the app from closing when back button is pressed.
   */
  override fun onBackPressed() {
    if (shouldAllowBack)
      super.onBackPressed()
  }

  private fun hideSystemUI() {
    mainContent.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LOW_PROFILE or
          View.SYSTEM_UI_FLAG_FULLSCREEN or
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
          View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
  }
}
