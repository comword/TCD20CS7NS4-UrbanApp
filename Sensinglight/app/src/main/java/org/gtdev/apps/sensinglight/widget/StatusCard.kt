package org.gtdev.apps.sensinglight.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.card.MaterialCardView
import org.gtdev.apps.sensinglight.R

class StatusCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyle) {

    private var mSpeed: StatusEntity
    private var mAltitude: StatusEntity
    private var mLatitude: StatusEntity
    private var mLongitude: StatusEntity

    private var mContainer: LinearLayout
    private var mSuspense: LinearLayout

    init {
        inflate(context, R.layout.widget_status_card, this)

        mSpeed = findViewById(R.id.status_speed)
        mAltitude = findViewById(R.id.status_altitude)
        mLatitude = findViewById(R.id.status_latitude)
        mLongitude = findViewById(R.id.status_lontitude)

        mContainer = findViewById(R.id.container)
        mSuspense = findViewById(R.id.suspense)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.StatusCard)
        mSpeed.setValue(attributes.getString(R.styleable.StatusCard_speed))
        mAltitude.setValue(attributes.getString(R.styleable.StatusCard_altitude))
        mLatitude.setValue(attributes.getString(R.styleable.StatusCard_latitude))
        mLongitude.setValue(attributes.getString(R.styleable.StatusCard_longitude))
        attributes.recycle()
    }

    fun setSpeed(value: String?) { mSpeed.setValue(value) }
    fun setAltitude(value: String?) { mAltitude.setValue(value) }
    fun setLatitude(value: String?) { mLatitude.setValue(value) }
    fun setLongitude(value: String?) { mLongitude.setValue(value) }

    fun setVisibility(visible: Boolean) {
        if (visible){
            mContainer.visibility = View.VISIBLE
            mSuspense.visibility = View.GONE
        } else {
            mContainer.visibility = View.INVISIBLE
            mSuspense.visibility = View.VISIBLE
        }
    }
}