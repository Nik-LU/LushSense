package com.example.iiushka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.iiushka.ui.theme.LushSenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LushSenseTheme {
                Surface {
                    AppNavHost()
                }
            }
        }
    }
}
