package io.github.nircek.thegame2

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set up controls
        up.setOnClickListener { radar.takeAction(RadarView.ACTION.ACCELERATE) }
        down.setOnClickListener { radar.takeAction(RadarView.ACTION.STOP) }
        left.setOnClickListener { radar.takeAction(RadarView.ACTION.TURN_LEFT) }
        right.setOnClickListener { radar.takeAction(RadarView.ACTION.TURN_RIGHT) }
    }
}
