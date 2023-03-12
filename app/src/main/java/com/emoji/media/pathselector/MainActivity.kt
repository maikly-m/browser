package com.emoji.media.pathselector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import com.emoji.media.pathselector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val firstFragment = FirstFragment()
        transaction.add(
            binding.fl.id, firstFragment,
            FirstFragment::class.java.simpleName
        )
        transaction.addToBackStack(FirstFragment::class.java.getSimpleName())
        transaction.commitAllowingStateLoss()

    }

}