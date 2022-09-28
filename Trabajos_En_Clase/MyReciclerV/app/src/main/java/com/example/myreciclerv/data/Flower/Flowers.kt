package com.example.myreciclerv.data.Flower;

import android.content.res.Resources
import com.example.myreciclerv.R

fun flowerList(recursos: Resources): List<Flower>{
    return listOf(
        Flower( 1, recursos.getString(R.string.flower1_name), R.drawable.rose, recursos.getString(R.string.flower1_description)),
        Flower( 2, recursos.getString(R.string.flower2_name), R.drawable.freesia, recursos.getString(R.string.flower2_description)),
        Flower( 3, recursos.getString(R.string.flower3_name), R.drawable.lily, recursos.getString(R.string.flower3_description)),
        Flower( 4, recursos.getString(R.string.flower4_name), R.drawable.sunflower, recursos.getString(R.string.flower4_description)),
        Flower( 5, recursos.getString(R.string.flower5_name), R.drawable.freesia, recursos.getString(R.string.flower2_description)),
        Flower( 2, recursos.getString(R.string.flower2_name), R.drawable.freesia, recursos.getString(R.string.flower2_description)),
        Flower( 2, recursos.getString(R.string.flower2_name), R.drawable.freesia, recursos.getString(R.string.flower2_description))
    )
}
