package com.emoji.media.pathselector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.emoji.media.pathselector.databinding.ActivityViewTestBinding

class ViewTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewTestBinding
    private var i = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnScale.setOnClickListener {
            if (i>11){
                i=0
            }
            binding.bae.changeScale(i)
            i++
        }
    }
}