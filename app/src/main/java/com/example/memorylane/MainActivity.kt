package com.example.memorylane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.memorylane.ui.theme.MemorylaneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorylaneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Base()
                }
            }
        }
    }
}

@Composable
fun Base(modifier: Modifier = Modifier) {
    Text(
        text = "Base page",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PagePreview() {
    MemorylaneTheme {
        Base()
    }
}