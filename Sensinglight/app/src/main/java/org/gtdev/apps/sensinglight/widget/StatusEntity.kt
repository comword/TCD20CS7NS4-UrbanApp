package org.gtdev.apps.sensinglight.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import org.gtdev.apps.sensinglight.R

class StatusEntity @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private var mDesc: TextView;
    private var mValue: TextView;

    init {
        inflate(context, R.layout.widget_status_entity, this)
        mDesc = findViewById(R.id.se_desc)
        mValue = findViewById(R.id.se_value)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.StatusEntity)
        mDesc.text = attributes.getString(R.styleable.StatusEntity_desc)
        mValue.text = attributes.getString(R.styleable.StatusEntity_value)
        attributes.recycle()
    }

    fun setValue(value: String?) {
        mValue.text = value
    }

    fun setDesc(value: String?) {
        mDesc.text = value
    }

}